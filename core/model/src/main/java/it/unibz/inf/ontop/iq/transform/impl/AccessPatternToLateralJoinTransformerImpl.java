package it.unibz.inf.ontop.iq.transform.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.dbschema.AccessPatternConstraint;
import it.unibz.inf.ontop.dbschema.Attribute;
import it.unibz.inf.ontop.dbschema.NamedRelationDefinition;
import it.unibz.inf.ontop.dbschema.RelationDefinition;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.iq.BinaryNonCommutativeIQTree;
import it.unibz.inf.ontop.iq.IQTree;
import it.unibz.inf.ontop.iq.NaryIQTree;
import it.unibz.inf.ontop.iq.UnaryIQTree;
import it.unibz.inf.ontop.iq.node.*;
import it.unibz.inf.ontop.iq.visit.IQTreeVisitor;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.model.term.VariableOrGroundTerm;
import it.unibz.inf.ontop.utils.ImmutableCollectors;

import java.util.*;

/**
 * Transforms InnerJoinNodes into LateralJoinNodes when necessary to satisfy
 * access pattern constraints.
 *
 * This transformation:
 * 1. Analyzes each join to see if any child has access patterns
 * 2. Checks if required input variables are bound from previous children
 * 3. Converts n-ary inner joins into a left-deep tree of lateral joins when needed
 *
 * Example:
 *   JOIN(table1, table2_with_access_pattern(needs: table1.id))
 *
 * Becomes:
 *   LATERAL_JOIN(table1, table2_with_access_pattern)
 */
public class AccessPatternToLateralJoinTransformerImpl implements IQTreeVisitor<IQTree> {

    private final IntermediateQueryFactory iqFactory;

    public AccessPatternToLateralJoinTransformerImpl(IntermediateQueryFactory iqFactory) {
        this.iqFactory = iqFactory;
    }

    /**
     * Main entry point: transform an entire IQ tree
     */
    public IQTree transform(IQTree tree) {
        return tree.acceptVisitor(this);
    }

    @Override
    public IQTree transformInnerJoin(NaryIQTree tree, InnerJoinNode node, ImmutableList<IQTree> children) {
        // First, recursively transform children
        ImmutableList<IQTree> transformedChildren = children.stream()
                .map(c -> c.acceptVisitor(this))
                .collect(ImmutableCollectors.toList());

        // Check if any child has access patterns that require lateral semantics
        if (!hasAccessPatterns(transformedChildren)) {
            // No access patterns, keep as regular inner join
            return iqFactory.createNaryIQTree(node, transformedChildren);
        }

        // Reorder children to satisfy access patterns and convert to lateral joins
        return convertToLateralJoinTree(node, transformedChildren);
    }

    @Override
    public IQTree transformLateralJoin(NaryIQTree tree, LateralJoinNode node, ImmutableList<IQTree> children) {
        // Recursively transform children
        ImmutableList<IQTree> transformedChildren = children.stream()
                .map(c -> c.acceptVisitor(this))
                .collect(ImmutableCollectors.toList());

        return iqFactory.createNaryIQTree(node, transformedChildren);
    }

    /**
     * Checks if any child has access patterns
     */
    private boolean hasAccessPatterns(ImmutableList<IQTree> children) {
        return children.stream()
                .anyMatch(this::hasAccessPattern);
    }

    private boolean hasAccessPattern(IQTree tree) {
        if (!(tree.getRootNode() instanceof ExtensionalDataNode)) {
            return false;
        }
        ExtensionalDataNode node = (ExtensionalDataNode) tree.getRootNode();
        RelationDefinition relation = node.getRelationDefinition();

        // Check if it's a NamedRelationDefinition and has access patterns
        if (!(relation instanceof NamedRelationDefinition)) {
            return false;
        }

        NamedRelationDefinition namedRelation = (NamedRelationDefinition) relation;
        return !namedRelation.getAccessPatterns().isEmpty();
    }

    /**
     * Converts an n-ary inner join into a left-deep tree of lateral joins
     * when access patterns require it.
     */
    private IQTree convertToLateralJoinTree(InnerJoinNode originalNode,
                                            ImmutableList<IQTree> children) {

        // Reorder children to satisfy access patterns
        ImmutableList<IQTree> orderedChildren = reorderForAccessPatterns(children);

        // Build left-deep tree
        IQTree result = orderedChildren.get(0);
        ImmutableSet<Variable> boundVariables = result.getVariables();

        for (int i = 1; i < orderedChildren.size(); i++) {
            IQTree rightChild = orderedChildren.get(i);

            boolean needsLateral = needsLateralJoin(rightChild, boundVariables);

            if (needsLateral) {
                // Create LATERAL JOIN (inner lateral join only)
                LateralJoinNode lateralNode = iqFactory.createInnerJoinLateralNode(
                        originalNode.getOptionalFilterCondition());
                result = iqFactory.createNaryIQTree(
                        lateralNode,
                        ImmutableList.of(result, rightChild));
            } else {
                // Regular inner join is sufficient
                result = iqFactory.createNaryIQTree(
                        originalNode,
                        ImmutableList.of(result, rightChild));
            }

            boundVariables = ImmutableSet.<Variable>builder()
                    .addAll(boundVariables)
                    .addAll(rightChild.getVariables())
                    .build();
        }

        return result;
    }

    /**
     * Reorders children so that:
     * 1. Children without access patterns come first
     * 2. Children with access patterns come after their required inputs
     */
    private ImmutableList<IQTree> reorderForAccessPatterns(ImmutableList<IQTree> children) {
        List<IQTree> result = new ArrayList<>();
        Set<IQTree> remaining = new LinkedHashSet<>(children);
        ImmutableSet<Variable> boundVariables = ImmutableSet.of();

        while (!remaining.isEmpty()) {
            IQTree next = null;

            // First, try to find a child with no access patterns
            for (IQTree child : remaining) {
                if (!hasAccessPattern(child)) {
                    next = child;
                    break;
                }
            }

            // If all remaining have access patterns, find one we can satisfy
            if (next == null) {
                for (IQTree child : remaining) {
                    if (canAccessPatternBeSatisfied(child, boundVariables)) {
                        next = child;
                        break;
                    }
                }
            }

            if (next == null) {
                throw new IllegalArgumentException(
                        "Cannot reorder joins to satisfy access patterns. " +
                                "Required input variables are not available.");
            }

            result.add(next);
            remaining.remove(next);
            boundVariables = ImmutableSet.<Variable>builder()
                    .addAll(boundVariables)
                    .addAll(next.getVariables())
                    .build();
        }

        return ImmutableList.copyOf(result);
    }

    /**
     * Checks if this child needs lateral join semantics given the bound variables
     */
    private boolean needsLateralJoin(IQTree child, ImmutableSet<Variable> boundVariables) {
        if (!(child.getRootNode() instanceof ExtensionalDataNode)) {
            return false;
        }

        ExtensionalDataNode node = (ExtensionalDataNode) child.getRootNode();
        RelationDefinition relation = node.getRelationDefinition();

        if (!(relation instanceof NamedRelationDefinition)) {
            return false;
        }

        NamedRelationDefinition namedRelation = (NamedRelationDefinition) relation;
        ImmutableSet<AccessPatternConstraint> patterns = namedRelation.getAccessPatterns();

        if (patterns.isEmpty()) {
            return false;
        }

        // Check if at least one pattern can be satisfied
        for (AccessPatternConstraint pattern : patterns) {
            if (isPatternSatisfied(node, pattern, boundVariables)) {
                // Pattern can be satisfied, so we need lateral join
                return true;
            }
        }

        // No pattern can be satisfied - this should have been caught earlier
        return false;
    }

    private boolean canAccessPatternBeSatisfied(IQTree child, ImmutableSet<Variable> boundVariables) {
        if (!(child.getRootNode() instanceof ExtensionalDataNode)) {
            return true; // Not a data node, no access pattern to check
        }

        ExtensionalDataNode node = (ExtensionalDataNode) child.getRootNode();
        RelationDefinition relation = node.getRelationDefinition();

        if (!(relation instanceof NamedRelationDefinition)) {
            return true;
        }

        NamedRelationDefinition namedRelation = (NamedRelationDefinition) relation;
        ImmutableSet<AccessPatternConstraint> patterns = namedRelation.getAccessPatterns();

        if (patterns.isEmpty()) {
            return true; // No access patterns to satisfy
        }

        // Check if at least one pattern can be satisfied
        return patterns.stream()
                .anyMatch(pattern -> isPatternSatisfied(node, pattern, boundVariables));
    }

    private boolean isPatternSatisfied(ExtensionalDataNode node,
                                       AccessPatternConstraint pattern,
                                       ImmutableSet<Variable> boundVariables) {
        ImmutableSet<Attribute> requiredInputs = pattern.getInputs();

        for (Attribute inputAttr : requiredInputs) {
            VariableOrGroundTerm term = node.getArgumentMap().get(inputAttr.getIndex() - 1);

            if (term instanceof Variable) {
                Variable var = (Variable) term;
                if (!boundVariables.contains(var)) {
                    return false; // Required input not bound
                }
            }
            // Ground terms are always "bound"
        }

        return true; // All required inputs are bound
    }

    // Default implementations for other node types - just recurse

    @Override
    public IQTree transformIntensionalData(IntensionalDataNode dataNode) {
        return iqFactory.createIntensionalDataNode(dataNode.getProjectionAtom());
    }

    @Override
    public IQTree transformExtensionalData(ExtensionalDataNode dataNode) {
        return iqFactory.createExtensionalDataNode(
                dataNode.getRelationDefinition(),
                dataNode.getArgumentMap());
    }

    @Override
    public IQTree transformEmpty(EmptyNode node) {
        return iqFactory.createEmptyNode(node.getVariables());
    }

    @Override
    public IQTree transformTrue(TrueNode node) {
        return iqFactory.createTrueNode();
    }

    @Override
    public IQTree transformValues(ValuesNode valuesNode) {
        return iqFactory.createValuesNode(
                valuesNode.getOrderedVariables(),
                valuesNode.getValues());
    }

    @Override
    public IQTree transformConstruction(UnaryIQTree tree, ConstructionNode rootNode, IQTree child) {
        return iqFactory.createUnaryIQTree(rootNode, child.acceptVisitor(this));
    }

    @Override
    public IQTree transformAggregation(UnaryIQTree tree, AggregationNode aggregationNode, IQTree child) {
        return iqFactory.createUnaryIQTree(aggregationNode, child.acceptVisitor(this));
    }

    @Override
    public IQTree transformFilter(UnaryIQTree tree, FilterNode rootNode, IQTree child) {
        return iqFactory.createUnaryIQTree(rootNode, child.acceptVisitor(this));
    }

    @Override
    public IQTree transformFlatten(UnaryIQTree tree, FlattenNode rootNode, IQTree child) {
        return iqFactory.createUnaryIQTree(rootNode, child.acceptVisitor(this));
    }

    @Override
    public IQTree transformDistinct(UnaryIQTree tree, DistinctNode rootNode, IQTree child) {
        return iqFactory.createUnaryIQTree(rootNode, child.acceptVisitor(this));
    }

    @Override
    public IQTree transformSlice(UnaryIQTree tree, SliceNode sliceNode, IQTree child) {
        return iqFactory.createUnaryIQTree(sliceNode, child.acceptVisitor(this));
    }

    @Override
    public IQTree transformOrderBy(UnaryIQTree tree, OrderByNode rootNode, IQTree child) {
        return iqFactory.createUnaryIQTree(rootNode, child.acceptVisitor(this));
    }

    @Override
    public IQTree transformLeftJoin(BinaryNonCommutativeIQTree tree, LeftJoinNode rootNode,
                                    IQTree leftChild, IQTree rightChild) {
        return iqFactory.createBinaryNonCommutativeIQTree(
                rootNode,
                leftChild.acceptVisitor(this),
                rightChild.acceptVisitor(this));
    }

    @Override
    public IQTree transformUnion(NaryIQTree tree, UnionNode rootNode, ImmutableList<IQTree> children) {
        ImmutableList<IQTree> transformedChildren = children.stream()
                .map(c -> c.acceptVisitor(this))
                .collect(ImmutableCollectors.toList());
        return iqFactory.createNaryIQTree(rootNode, transformedChildren);
    }
}