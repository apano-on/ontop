package it.unibz.inf.ontop.iq.node.normalization.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.iq.*;
import it.unibz.inf.ontop.iq.impl.IQTreeTools;
import it.unibz.inf.ontop.iq.impl.NaryIQTreeTools;
import it.unibz.inf.ontop.iq.node.*;
import it.unibz.inf.ontop.iq.node.impl.JoinOrFilterVariableNullabilityTools;
import it.unibz.inf.ontop.iq.node.normalization.ConstructionSubstitutionNormalizer;
import it.unibz.inf.ontop.iq.node.normalization.ConstructionSubstitutionNormalizer.ConstructionSubstitutionNormalization;
import it.unibz.inf.ontop.iq.node.normalization.ConditionSimplifier;
import it.unibz.inf.ontop.iq.node.normalization.InnerJoinLateralNormalizer;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.substitution.SubstitutionFactory;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import it.unibz.inf.ontop.utils.VariableGenerator;

import java.util.Optional;
import java.util.stream.Stream;

import static it.unibz.inf.ontop.iq.impl.UnaryIQTreeTools.UnaryIQTreeDecomposition;


public class InnerJoinLateralNormalizerImpl implements InnerJoinLateralNormalizer {

    private static final int MAX_ITERATIONS = 10000;

    private final JoinLikeChildBindingLifter bindingLifter;
    private final IntermediateQueryFactory iqFactory;
    private final ConstructionSubstitutionNormalizer substitutionNormalizer;
    private final ConditionSimplifier conditionSimplifier;
    private final TermFactory termFactory;
    private final JoinOrFilterVariableNullabilityTools variableNullabilityTools;
    private final IQTreeTools iqTreeTools;
    private final SubstitutionFactory substitutionFactory;

    @Inject
    private InnerJoinLateralNormalizerImpl(JoinLikeChildBindingLifter bindingLifter,
                                           IntermediateQueryFactory iqFactory,
                                           ConstructionSubstitutionNormalizer substitutionNormalizer,
                                           ConditionSimplifier conditionSimplifier,
                                           TermFactory termFactory,
                                           JoinOrFilterVariableNullabilityTools variableNullabilityTools,
                                           IQTreeTools iqTreeTools,
                                           SubstitutionFactory substitutionFactory) {
        this.bindingLifter = bindingLifter;
        this.iqFactory = iqFactory;
        this.substitutionNormalizer = substitutionNormalizer;
        this.conditionSimplifier = conditionSimplifier;
        this.termFactory = termFactory;
        this.variableNullabilityTools = variableNullabilityTools;
        this.iqTreeTools = iqTreeTools;
        this.substitutionFactory = substitutionFactory;
    }

    @Override
    public IQTree normalizeForOptimizationLateral(LateralJoinNode lateralJoinNode,
                                                  ImmutableList<IQTree> children,
                                                  VariableGenerator variableGenerator,
                                                  IQTreeCache treeCache) {
        var initialLateralJoin = new LateralJoinSubTree(lateralJoinNode.getOptionalFilterCondition(), children);
        Context context = new Context(initialLateralJoin.projectedVariables(), variableGenerator, treeCache);
        return context.normalize(initialLateralJoin);
    }

    /**
     * LATERAL join subtree maintains ordering constraints
     * Unlike regular inner join, children cannot be freely reordered
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static class LateralJoinSubTree {
        private final Optional<ImmutableExpression> joiningCondition;
        private final ImmutableList<IQTree> children; // Order matters!

        private LateralJoinSubTree(Optional<ImmutableExpression> joiningCondition, ImmutableList<IQTree> children) {
            this.joiningCondition = joiningCondition;
            this.children = children;
        }

        Optional<ImmutableExpression> joiningCondition() { return joiningCondition; }

        ImmutableList<IQTree> children() { return children; }

        ImmutableSet<Variable> projectedVariables() { return NaryIQTreeTools.projectedVariables(children); }

        IQTree leftChild() {
            return children.isEmpty() ? null : children.get(0);
        }

        ImmutableList<IQTree> rightChildren() {
            return children.size() <= 1 ? ImmutableList.of() : children.subList(1, children.size());
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof LateralJoinSubTree) {
                LateralJoinSubTree other = (LateralJoinSubTree)o;
                return joiningCondition.equals(other.joiningCondition)
                        && children.equals(other.children);
            }
            return false;
        }
    }

    private class Context extends NormalizationContext {

        private Context(ImmutableSet<Variable> projectedVariables,
                        VariableGenerator variableGenerator,
                        IQTreeCache treeCache) {
            super(projectedVariables, variableGenerator, treeCache, InnerJoinLateralNormalizerImpl.this.iqTreeTools);
        }

        IQTree normalize(LateralJoinSubTree initialLateralJoin) {
            var initial = State.initial(initialLateralJoin);
            // For LATERAL, we do fewer optimizations due to ordering constraints
            var state = initial.reachFixedPoint(MAX_ITERATIONS,
                    this::liftBindingsSequentially,
                    this::liftDistinctIfPossible);
            return asIQTree(state);
        }

        /**
         * Propagate condition down through the LATERAL join while respecting ordering
         */
        State<UnaryOperatorNode, LateralJoinSubTree> propagateDownCondition(
                State<UnaryOperatorNode, LateralJoinSubTree> state) {

            LateralJoinSubTree subTree = state.getSubTree();
            if (subTree.joiningCondition().isEmpty()) {
                return state;
            }

            try {
                var simplification = conditionSimplifier.simplifyCondition(
                        subTree.joiningCondition(),
                        ImmutableSet.of(),
                        subTree.children(),
                        variableNullabilityTools.getChildrenVariableNullability(subTree.children()));

                var extendedDownConstraint = iqTreeTools.getDownPropagation(
                        simplification,
                        subTree.projectedVariables(),
                        variableGenerator);

                // For LATERAL: propagate sequentially
                ImmutableList<IQTree> transformedChildren = propagateSequentially(
                        subTree.children(),
                        extendedDownConstraint);

                return state.lift(
                        iqTreeTools.createOptionalConstructionNode(
                                subTree::projectedVariables,
                                simplification.getSubstitution()),
                        new LateralJoinSubTree(
                                simplification.getOptionalExpression(),
                                transformedChildren));
            }
            catch (DownPropagation.InconsistentDownPropagationException e) {
                return declareAsEmpty();
            }
        }

        /**
         * Lift bindings while respecting LATERAL dependency order
         * Only lift from left child, as right children depend on left context
         */
        State<UnaryOperatorNode, LateralJoinSubTree> liftBindingsSequentially(
                State<UnaryOperatorNode, LateralJoinSubTree> state) {
            return propagateDownCondition(state).reachFinal(
                    this::normalizeChildren,
                    this::liftLeftBinding);
        }

        /**
         * Only lift bindings from the LEFT child
         * Right children cannot have bindings lifted as they depend on left variables
         */
        Optional<State<UnaryOperatorNode, LateralJoinSubTree>> liftLeftBinding(
                State<UnaryOperatorNode, LateralJoinSubTree> state) {

            LateralJoinSubTree subTree = state.getSubTree();
            if (subTree.children().isEmpty()) {
                return Optional.empty();
            }

            IQTree leftChild = subTree.leftChild();

            // Try to lift construction from left child
            var decomposition = UnaryIQTreeDecomposition.of(leftChild, ConstructionNode.class);
            if (!decomposition.isPresent()) {
                return Optional.empty();
            }

            if (decomposition.getNode().getSubstitution().isEmpty()) {
                return Optional.empty();
            }

            return liftLeftChildBinding(state, decomposition);
        }

        Optional<State<UnaryOperatorNode, LateralJoinSubTree>> liftLeftChildBinding(
                State<UnaryOperatorNode, LateralJoinSubTree> state,
                UnaryIQTreeDecomposition<ConstructionNode> construction) {

            try {
                LateralJoinSubTree subTree = state.getSubTree();

                // Create left child with limited projection
                IQTree leftGrandChild = iqTreeTools.unaryIQTreeBuilder(construction.getNode().getChildVariables())
                        .build(construction.getChild());

                // Temporarily replace left child
                ImmutableList<IQTree> provisionalChildren = ImmutableList.<IQTree>builder()
                        .add(leftGrandChild)
                        .addAll(subTree.rightChildren())
                        .build();

                // Lift binding from left child (position 0)
                var bindingLift = bindingLifter.liftRegularChildBinding(
                        construction.getNode(),
                        0, // left child position
                        subTree.children(),
                        ImmutableSet.of(),
                        subTree.joiningCondition(),
                        variableGenerator,
                        variableNullabilityTools.getChildrenVariableNullability(provisionalChildren));

                ConstructionSubstitutionNormalization normalization = substitutionNormalizer
                        .normalizeSubstitution(bindingLift.getAscendingSubstitution(), subTree.projectedVariables());

                // Create down propagation with combined substitution
                DownPropagation dp = iqTreeTools.createDownPropagation(
                        substitutionFactory.onVariableOrGroundTerms().compose(
                                normalization.getDownRenamingSubstitution(),
                                bindingLift.getDescendingSubstitution()),
                        bindingLift.getCondition()
                                .map(normalization.getDownRenamingSubstitution()::apply),
                        NaryIQTreeTools.projectedVariables(provisionalChildren),
                        variableGenerator);

                // For LATERAL: propagate sequentially left-to-right
                ImmutableList<IQTree> newChildren = propagateSequentially(provisionalChildren, dp);

                Optional<ConstructionNode> newParent = normalization.createOptionalConstructionNode();

                return Optional.of(state.lift(newParent, new LateralJoinSubTree(dp.getConstraint(), newChildren)));
            }
            catch (DownPropagation.InconsistentDownPropagationException e) {
                return Optional.of(declareAsEmpty());
            }
        }

        /**
         * Propagate substitution sequentially: left child first, then right children with left context
         */
        private ImmutableList<IQTree> propagateSequentially(ImmutableList<IQTree> children, DownPropagation dp) {
            if (children.isEmpty()) {
                return children;
            }

            ImmutableList.Builder<IQTree> result = ImmutableList.builder();

            // Propagate to left child
            IQTree leftChild = dp.propagateWithRestrictedScope(children.get(0));
            result.add(leftChild);

            // Propagate to right children with extended context from left
            for (int i = 1; i < children.size(); i++) {
                // Right children can see left variables, so propagate with full scope
                result.add(dp.propagateWithRestrictedScope(children.get(i)));
            }

            return result.build();
        }

        /**
         * Normalize children while preserving order
         */
        State<UnaryOperatorNode, LateralJoinSubTree> normalizeChildren(
                State<UnaryOperatorNode, LateralJoinSubTree> state) {

            LateralJoinSubTree subTree = state.getSubTree();

            // Normalize left child
            if (subTree.children().isEmpty()) {
                return state;
            }

            IQTree normalizedLeft = normalizeSubTreeRecursively(subTree.leftChild());
            if (normalizedLeft.isDeclaredAsEmpty()) {
                return declareAsEmpty();
            }
            if (normalizedLeft.getRootNode() instanceof TrueNode) {
                return declareAsEmpty(); // LATERAL join needs left side
            }

            // Normalize right children (they may reference left variables)
            ImmutableList<IQTree> normalizedRightChildren = subTree.rightChildren().stream()
                    .map(this::normalizeSubTreeRecursively)
                    .filter(c -> !(c.getRootNode() instanceof TrueNode))
                    .collect(ImmutableCollectors.toList());

            if (normalizedRightChildren.stream().anyMatch(IQTree::isDeclaredAsEmpty)) {
                return declareAsEmpty();
            }

            ImmutableList<IQTree> allNormalizedChildren = ImmutableList.<IQTree>builder()
                    .add(normalizedLeft)
                    .addAll(normalizedRightChildren)
                    .build();

            return state.replace(new LateralJoinSubTree(subTree.joiningCondition(), allNormalizedChildren));
        }

        /**
         * Can only lift DISTINCT if both sides are distinct
         * Unlike regular join, we cannot reorder or reorganize children
         */
        State<UnaryOperatorNode, LateralJoinSubTree> liftDistinctIfPossible(
                State<UnaryOperatorNode, LateralJoinSubTree> state) {

            LateralJoinSubTree subTree = state.getSubTree();

            // Check if all children are distinct
            if (subTree.children().stream().allMatch(IQTree::isDistinct)
                    || iqTreeTools.createInnerJoinLateralTree(subTree.joiningCondition(), subTree.children()).isDistinct()) {

                return state.lift(iqFactory.createDistinctNode(),
                        new LateralJoinSubTree(
                                subTree.joiningCondition(),
                                NaryIQTreeTools.transformChildren(subTree.children(), IQTree::removeDistincts)));
            }
            return state;
        }

        /**
         * No child is interpreted as EMPTY
         */
        State<UnaryOperatorNode, LateralJoinSubTree> declareAsEmpty() {
            EmptyNode emptyChild = createEmptyNode();
            return State.initial(new LateralJoinSubTree(Optional.empty(), ImmutableList.of(emptyChild)));
        }

        protected IQTree asIQTree(State<UnaryOperatorNode, LateralJoinSubTree> state) {
            IQTree joinLevelTree = createLateralJoinOrFilterOrEmpty(state.getSubTree());
            if (joinLevelTree.isDeclaredAsEmpty()) {
                return joinLevelTree;
            }

            // Normalize ancestors recursively
            return normalizeSubTreeRecursively(
                    iqTreeTools.unaryIQTreeBuilder(projectedVariables)
                            .append(state.getAncestors())
                            .build(joinLevelTree));
        }

        private IQTree createLateralJoinOrFilterOrEmpty(LateralJoinSubTree subTree) {
            switch (subTree.children().size()) {
                case 0:
                    return iqFactory.createTrueNode();
                case 1:
                    // Single child with filter condition
                    return iqTreeTools.unaryIQTreeBuilder()
                            .append(iqTreeTools.createOptionalFilterNode(subTree.joiningCondition()))
                            .build(subTree.children().get(0));
                default:
                    // LATERAL join with multiple children - ORDER MATTERS, NO LIFTING
                    // We cannot lift left joins or reorder children due to LATERAL dependency
                    return iqFactory.createNaryIQTree(
                            iqFactory.createInnerJoinLateralNode(subTree.joiningCondition()),
                            subTree.children(),
                            getNormalizedTreeCache(true));
            }
        }
    }
}