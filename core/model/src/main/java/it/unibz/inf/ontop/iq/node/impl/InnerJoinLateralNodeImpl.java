package it.unibz.inf.ontop.iq.node.impl;

import com.google.common.collect.*;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import it.unibz.inf.ontop.evaluator.TermNullabilityEvaluator;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.iq.DownPropagation;
import it.unibz.inf.ontop.iq.IQTree;
import it.unibz.inf.ontop.iq.IQTreeCache;
import it.unibz.inf.ontop.iq.NaryIQTree;
import it.unibz.inf.ontop.iq.exception.InvalidIntermediateQueryException;
import it.unibz.inf.ontop.iq.impl.IQTreeTools;
import it.unibz.inf.ontop.iq.impl.NaryIQTreeTools;
import it.unibz.inf.ontop.iq.node.LateralJoinNode;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.iq.node.normalization.ConditionSimplifier;
import it.unibz.inf.ontop.iq.node.normalization.ConditionSimplifier.ExpressionAndSubstitution;
import it.unibz.inf.ontop.iq.node.normalization.InnerJoinLateralNormalizer;
import it.unibz.inf.ontop.iq.request.FunctionalDependencies;
import it.unibz.inf.ontop.iq.request.VariableNonRequirement;
import it.unibz.inf.ontop.model.term.ImmutableExpression;
import it.unibz.inf.ontop.model.term.NonVariableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.model.type.TypeFactory;
import it.unibz.inf.ontop.substitution.InjectiveSubstitution;
import it.unibz.inf.ontop.substitution.Substitution;
import it.unibz.inf.ontop.substitution.SubstitutionFactory;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import it.unibz.inf.ontop.utils.VariableGenerator;

import java.util.*;

import static it.unibz.inf.ontop.iq.impl.NaryIQTreeTools.replaceChild;

public class InnerJoinLateralNodeImpl extends JoinLikeNodeImpl implements LateralJoinNode {

    private static final String LATERAL_JOIN_NODE_STR = "LATERAL_JOIN";
    private final InnerJoinLateralNormalizer normalizer;
//    private final ImmutableSet<Variable> boundVariables;

    @AssistedInject
    protected InnerJoinLateralNodeImpl(@Assisted Optional<ImmutableExpression> optionalFilterCondition,
//                                       @Assisted Optional<ImmutableExpression> boundVariables,
                                       TermNullabilityEvaluator nullabilityEvaluator,
                                       TermFactory termFactory, TypeFactory typeFactory,
                                       IntermediateQueryFactory iqFactory, SubstitutionFactory substitutionFactory,
                                       IQTreeTools iqTreeTools,
                                       JoinOrFilterVariableNullabilityTools variableNullabilityTools, ConditionSimplifier conditionSimplifier,
                                       InnerJoinLateralNormalizer normalizer) {
        super(optionalFilterCondition, nullabilityEvaluator, termFactory, iqFactory, typeFactory,
                substitutionFactory, variableNullabilityTools, conditionSimplifier, iqTreeTools);
        this.normalizer = normalizer;
//        this.boundVariables = boundVariables;
    }

    @AssistedInject
    private InnerJoinLateralNodeImpl(@Assisted ImmutableExpression joiningCondition,
                                     TermNullabilityEvaluator nullabilityEvaluator,
                                     TermFactory termFactory, TypeFactory typeFactory,
                                     IntermediateQueryFactory iqFactory, SubstitutionFactory substitutionFactory,
                                     IQTreeTools iqTreeTools,
                                     JoinOrFilterVariableNullabilityTools variableNullabilityTools, ConditionSimplifier conditionSimplifier,
                                     InnerJoinLateralNormalizer normalizer) {
        this(Optional.of(joiningCondition), nullabilityEvaluator, termFactory, typeFactory, iqFactory,
                substitutionFactory, iqTreeTools, variableNullabilityTools, conditionSimplifier, normalizer);
    }

    @AssistedInject
    private InnerJoinLateralNodeImpl(TermNullabilityEvaluator nullabilityEvaluator, TermFactory termFactory,
                                     TypeFactory typeFactory, IntermediateQueryFactory iqFactory,
                                     SubstitutionFactory substitutionFactory, IQTreeTools iqTreeTools,
                                     JoinOrFilterVariableNullabilityTools variableNullabilityTools, ConditionSimplifier conditionSimplifier,
                                     InnerJoinLateralNormalizer normalizer) {
        this(Optional.empty(), nullabilityEvaluator, termFactory, typeFactory, iqFactory,
                substitutionFactory, iqTreeTools, variableNullabilityTools, conditionSimplifier, normalizer);
    }

    @Override
    public ImmutableSet<Substitution<NonVariableTerm>> getPossibleVariableDefinitions(ImmutableList<IQTree> children) {
        if (children.isEmpty()) {
            return ImmutableSet.of();
        }

        // For LATERAL: left side definitions are independent, right side may depend on left
        // Start with left child definitions
        ImmutableSet<Substitution<NonVariableTerm>> result = children.get(0).getPossibleVariableDefinitions();

        // Accumulate right-side definitions, which can reference left variables
        for (int i = 1; i < children.size(); i++) {
            ImmutableSet<Substitution<NonVariableTerm>> rightDefs = children.get(i).getPossibleVariableDefinitions();
            result = combineVarDefsLateral(result, rightDefs);
        }

        return result;
    }

    private ImmutableSet<Substitution<NonVariableTerm>> combineVarDefsLateral(
            ImmutableSet<Substitution<NonVariableTerm>> leftDefs,
            ImmutableSet<Substitution<NonVariableTerm>> rightDefs) {

        // Right side is evaluated per left row, so compose with left context
        return leftDefs.stream()
                .flatMap(leftDef -> rightDefs.stream()
                        .map(rightDef -> substitutionFactory.onNonVariableTerms().compose(rightDef, leftDef)))
                .collect(ImmutableCollectors.toSet());
    }


    @Override
    public int hashCode() {
        return getOptionalFilterCondition().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof InnerJoinLateralNodeImpl) {
            InnerJoinLateralNodeImpl that = (InnerJoinLateralNodeImpl) o;
            return getOptionalFilterCondition().equals(that.getOptionalFilterCondition());
        }
        return false;
    }

    @Override
    public String toString() {
        return LATERAL_JOIN_NODE_STR + getOptionalFilterString();
    }

    @Override
    public IQTree normalizeForOptimization(ImmutableList<IQTree> children,
                                           VariableGenerator variableGenerator,
                                           IQTreeCache treeCache) {
        // LATERAL joins preserve child order - cannot apply commutative/associative optimizations
        return normalizer.normalizeForOptimizationLateral(this, children, variableGenerator, treeCache);
    }

    @Override
    public LateralJoinNode applyFreshRenaming(InjectiveSubstitution<Variable> renamingSubstitution) {
        return iqFactory.createInnerJoinLateralNode(getOptionalFilterCondition().map(renamingSubstitution::apply));
    }

    @Override
    public IQTree applyDescendingSubstitution(DownPropagation dp, ImmutableList<IQTree> children) {
        VariableNullability simplifiedChildFutureVariableNullability = variableNullabilityTools.getSimplifiedVariableNullability(
                dp.getResultingProjectedVariables());
        return propagateDownLateral(dp, children, simplifiedChildFutureVariableNullability);
    }

    @Override
    public IQTree propagateDownConstraint(DownPropagation dp, ImmutableList<IQTree> children) {
        VariableNullability extendedChildrenVariableNullability = dp.extendVariableNullability(
                variableNullabilityTools.getChildrenVariableNullability(children));
        return propagateDownLateral(dp, children, extendedChildrenVariableNullability);
    }

    private IQTree propagateDownLateral(DownPropagation dp, ImmutableList<IQTree> children,
                                        VariableNullability variableNullability) {
        try {
            var simplification = conditionSimplifier.simplifyCondition(
                    dp.applyDescendingSubstitution(getOptionalFilterCondition()),
                    ImmutableSet.of(),
                    children,
                    variableNullability);

            // For LATERAL: propagate to left child first, then use its output for right child
            ImmutableList<IQTree> transformedChildren = propagateToChildrenSequentially(
                    children, dp, simplification, variableNullability);

            return iqTreeTools.unaryIQTreeBuilder()
                    .append(iqTreeTools.createOptionalConstructionNode(
                            dp::getResultingProjectedVariables,
                            simplification.getSubstitution()))
                    .build(iqTreeTools.createInnerJoinLateralTree(
                            simplification.getOptionalExpression(),
                            transformedChildren));
        }
        catch (DownPropagation.InconsistentDownPropagationException e) {
            return iqFactory.createEmptyNode(dp.getResultingProjectedVariables());
        }
    }

    private ImmutableList<IQTree> propagateToChildrenSequentially(
            ImmutableList<IQTree> children,
            DownPropagation dp,
            ExpressionAndSubstitution simplification,
            VariableNullability variableNullability) throws DownPropagation.InconsistentDownPropagationException {

        ImmutableList.Builder<IQTree> result = ImmutableList.builder();

        try {
            // Propagate to left child (index 0) with current context
            var extendedDownConstraint = conditionSimplifier.getCombinedDownPropagation(
                    dp, simplification, variableNullability);
            IQTree leftChild = extendedDownConstraint.propagateWithRestrictedScope(children.get(0));
            result.add(leftChild);

            // For right children (index 1+), extend context with left child's variables
            for (int i = 1; i < children.size(); i++) {
                // Right child can see left variables through the propagation context
                result.add(extendedDownConstraint.propagateWithRestrictedScope(children.get(i)));
            }
        } catch (DownPropagation.InconsistentDownPropagationException e) {
            throw e;
        }

        return result.build();
    }

    @Override
    public VariableNullability getVariableNullability(ImmutableList<IQTree> children) {
        return variableNullabilityTools.getVariableNullability(children, getOptionalFilterCondition());
    }

    @Override
    public boolean isConstructed(Variable variable, ImmutableList<IQTree> children) {
        return children.stream()
                .anyMatch(c -> c.isConstructed(variable));
    }

    @Override
    public boolean isDistinct(IQTree tree, ImmutableList<IQTree> children) {
        return super.isDistinct(tree, children);
    }

    @Override
    public IQTree liftIncompatibleDefinitions(Variable variable, ImmutableList<IQTree> children,
                                              VariableGenerator variableGenerator) {
        // For LATERAL joins, we cannot lift definitions from right to left due to dependency
        // Only lift from the left child if needed
        if (children.isEmpty()) {
            return iqFactory.createNaryIQTree(this, children);
        }

        IQTree leftChild = children.get(0);
        if (!leftChild.isConstructed(variable)) {
            return iqFactory.createNaryIQTree(this, children);
        }

        IQTree liftedLeft = leftChild.liftIncompatibleDefinitions(variable, variableGenerator);
        var union = NaryIQTreeTools.UnionDecomposition.of(liftedLeft)
                .filter(d -> d.getNode().hasAChildWithLiftableDefinition(variable, d.getChildren()));

        if (!union.isPresent()) {
            return iqFactory.createNaryIQTree(this, children);
        }

        // Lift union above LATERAL join, preserving all children
        return iqTreeTools.createUnionTree(NaryIQTreeTools.projectedVariables(children),
                union.transformChildren(c ->
                        iqFactory.createNaryIQTree(this,
                                replaceChild(children, 0, c))));
    }

    @Override
    public void validateNode(ImmutableList<IQTree> children) throws InvalidIntermediateQueryException {
        if (children.size() < 2) {
            throw new InvalidIntermediateQueryException("LATERAL JOIN node " + this
                    + " does not have at least 2 children.\n" + children);
        }

        getOptionalFilterCondition()
                .ifPresent(e -> checkExpression(e, children));

        checkNonProjectedVariables(children);

        // LATERAL-specific validation: ensure right children only reference left variables
        validateLateralScoping(children);
    }

    private void validateLateralScoping(ImmutableList<IQTree> children) throws InvalidIntermediateQueryException {
        // Accumulate variables available from left side
        ImmutableSet<Variable> availableVariables = children.get(0).getVariables();

        for (int i = 1; i < children.size(); i++) {
            IQTree rightChild = children.get(i);

            // Check if right child references variables not available from left
            ImmutableSet<Variable> requiredVars = rightChild.getKnownVariables();
            ImmutableSet<Variable> locallyDefined = rightChild.getVariables();
            ImmutableSet<Variable> externalRefs = Sets.difference(requiredVars,
                    Sets.union(availableVariables, locallyDefined)).immutableCopy();

            if (!externalRefs.isEmpty()) {
                throw new InvalidIntermediateQueryException(
                        "LATERAL JOIN child at position " + i +
                                " references variables not available from left side: " + externalRefs);
            }

            // Make current child's variables available for next iteration
            availableVariables = Sets.union(availableVariables, locallyDefined).immutableCopy();
        }
    }

    @Override
    public IQTree removeDistincts(ImmutableList<IQTree> children, IQTreeCache treeCache) {
        ImmutableList<IQTree> newChildren = NaryIQTreeTools.transformChildren(children,
                IQTree::removeDistincts);

        IQTreeCache newTreeCache = treeCache.declareDistinctRemoval(newChildren.equals(children));
        return iqFactory.createNaryIQTree(this, newChildren, newTreeCache);
    }

    @Override
    public ImmutableSet<ImmutableSet<Variable>> inferUniqueConstraints(ImmutableList<IQTree> children) {
        // LATERAL joins only preserve uniqueness from the left side
        // Right side is re-evaluated per left row, so provides no global uniqueness
        if (children.isEmpty()) {
            return ImmutableSet.of();
        }

        return children.get(0).inferUniqueConstraints();
    }

    @Override
    public FunctionalDependencies inferFunctionalDependencies(ImmutableList<IQTree> children,
                                                              ImmutableSet<ImmutableSet<Variable>> uniqueConstraints,
                                                              ImmutableSet<Variable> variables) {
        // Only collect FDs from left child, as right side FDs are context-dependent
        if (children.isEmpty()) {
            return FunctionalDependencies.empty();
        }

        return children.get(0).inferFunctionalDependencies();
    }


    @Override
    public VariableNonRequirement computeVariableNonRequirement(ImmutableList<IQTree> children) {
        var nonRequirementBeforeFilter = computeVariableNonRequirementForChildren(children);
        return nonRequirementBeforeFilter.withRequiredVariables(getLocallyRequiredVariables());
    }

    @Override
    public ImmutableSet<Variable> inferStrictDependents(NaryIQTree tree, ImmutableList<IQTree> children) {
        // Default implementation
        return IQTreeTools.computeStrictDependentsFromFunctionalDependencies(tree);
    }
}