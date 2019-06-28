package it.unibz.inf.ontop.iq.node.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.iq.IQProperties;
import it.unibz.inf.ontop.iq.IQTree;
import it.unibz.inf.ontop.iq.IntermediateQuery;
import it.unibz.inf.ontop.iq.exception.InvalidIntermediateQueryException;
import it.unibz.inf.ontop.iq.exception.QueryNodeTransformationException;
import it.unibz.inf.ontop.iq.node.*;
import it.unibz.inf.ontop.iq.node.normalization.AggregationNormalizer;
import it.unibz.inf.ontop.iq.transform.IQTreeVisitingTransformer;
import it.unibz.inf.ontop.iq.transform.node.HeterogeneousQueryNodeTransformer;
import it.unibz.inf.ontop.iq.transform.node.HomogeneousQueryNodeTransformer;
import it.unibz.inf.ontop.iq.visit.IQVisitor;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.substitution.ImmutableSubstitution;
import it.unibz.inf.ontop.substitution.SubstitutionFactory;
import it.unibz.inf.ontop.substitution.impl.ImmutableSubstitutionTools;
import it.unibz.inf.ontop.substitution.impl.ImmutableUnificationTools;
import it.unibz.inf.ontop.utils.CoreUtilsFactory;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import it.unibz.inf.ontop.utils.VariableGenerator;

import java.util.Optional;

public class AggregationNodeImpl extends ConstructionOrAggregationNodeImpl implements AggregationNode {


    private static final String AGGREGATE_NODE_STR = "AGGREGATE";

    private final ImmutableSet<Variable> projectedVariables;
    private final ImmutableSet<Variable> groupingVariables;
    private final ImmutableSubstitution<ImmutableFunctionalTerm> substitution;
    private final ImmutableSet<Variable> childVariables;
    private final AggregationNormalizer aggregationNormalizer;

    @AssistedInject
    protected AggregationNodeImpl(@Assisted ImmutableSet<Variable> groupingVariables,
                                  @Assisted ImmutableSubstitution<ImmutableFunctionalTerm> substitution,
                                  SubstitutionFactory substitutionFactory, IntermediateQueryFactory iqFactory,
                                  AggregationNormalizer aggregationNormalizer,
                                  ImmutableUnificationTools unificationTools, ConstructionNodeTools constructionNodeTools,
                                  ImmutableSubstitutionTools substitutionTools, TermFactory termFactory,
                                  CoreUtilsFactory coreUtilsFactory) {
        super(substitutionFactory, iqFactory, unificationTools, constructionNodeTools, substitutionTools,
                termFactory, coreUtilsFactory);
        this.groupingVariables = groupingVariables;
        this.substitution = substitution;
        this.aggregationNormalizer = aggregationNormalizer;
        this.projectedVariables = Sets.union(groupingVariables, substitution.getDomain()).immutableCopy();
        this.childVariables = Sets.union(groupingVariables,
                substitution.getImmutableMap().values().stream()
                .flatMap(ImmutableTerm::getVariableStream)
                        .collect(ImmutableCollectors.toSet())).immutableCopy();
    }

    @Override
    public IQTree normalizeForOptimization(IQTree child, VariableGenerator variableGenerator, IQProperties currentIQProperties) {
        return aggregationNormalizer.normalizeForOptimization(this, child, variableGenerator,
                currentIQProperties);
    }

    @Override
    public VariableNullability getVariableNullability(IQTree child) {
        throw new RuntimeException("TODO: implement");
    }

    @Override
    public boolean isConstructed(Variable variable, IQTree child) {
        return substitution.isDefining(variable)
                || (groupingVariables.contains(variable) && child.isConstructed(variable));
    }

    @Override
    public boolean isDistinct(IQTree child) {
        return true;
    }

    /**
     * By default does not lift.
     * TODO: see if in some cases we could lift
     */
    @Override
    public IQTree liftIncompatibleDefinitions(Variable variable, IQTree child) {
        return iqFactory.createUnaryIQTree(this, child);
    }

    @Override
    public IQTree acceptTransformer(IQTree tree, IQTreeVisitingTransformer transformer, IQTree child) {
        throw new RuntimeException("TODO: implement");
    }

    @Override
    public <T> T acceptVisitor(IQVisitor<T> visitor, IQTree child) {
        throw new RuntimeException("TODO: implement");
    }

    @Override
    public void acceptVisitor(QueryNodeVisitor visitor) {
        throw new RuntimeException("TODO: implement");
    }

    @Override
    public AggregationNode acceptNodeTransformer(HomogeneousQueryNodeTransformer transformer) throws QueryNodeTransformationException {
        throw new RuntimeException("TODO: implement");
    }

    @Override
    public NodeTransformationProposal acceptNodeTransformer(HeterogeneousQueryNodeTransformer transformer) {
        throw new RuntimeException("TODO: implement");
    }

    @Override
    public ImmutableSet<Variable> getLocalVariables() {
        return Sets.union(getChildVariables(), substitution.getDomain()).immutableCopy();
    }

    @Override
    public boolean isVariableNullable(IntermediateQuery query, Variable variable) {
        // TODO: implement seriously!
        return true;
    }

    @Override
    public boolean isSyntacticallyEquivalentTo(QueryNode node) {
        return Optional.of(node)
                .filter(n -> n instanceof AggregationNode)
                .map(n -> (AggregationNode) n)
                .filter(n -> n.getGroupingVariables().equals(groupingVariables))
                .filter(n -> n.getSubstitution().equals(substitution))
                .isPresent();
    }

    @Override
    public ImmutableSet<Variable> getLocallyRequiredVariables() {
        return getChildVariables();
    }

    @Override
    public ImmutableSet<Variable> getRequiredVariables(IntermediateQuery query) {
        return getLocallyRequiredVariables();
    }

    @Override
    public ImmutableSet<Variable> getLocallyDefinedVariables() {
        return substitution.getDomain();
    }

    @Override
    public boolean isEquivalentTo(QueryNode queryNode) {
        return isSyntacticallyEquivalentTo(queryNode);
    }

    /**
     * TODO: implement seriously
     */
    @Override
    public void validateNode(IQTree child) throws InvalidIntermediateQueryException {
        // TODO: check that the grouping variables and the substitution domain are disjoint
    }

    @Override
    public ImmutableSet<ImmutableSubstitution<NonVariableTerm>> getPossibleVariableDefinitions(IQTree child) {
        throw new RuntimeException("TODO: implement");
    }

    /**
     * By default, blocks the distinct removal
     * TODO: detect when we can do it (absence of cardinality-sensitive aggregation functions)
     */
    @Override
    public IQTree removeDistincts(IQTree child, IQProperties iqProperties) {
        return iqFactory.createUnaryIQTree(this, child, iqProperties.declareDistinctRemovalWithoutEffect());
    }

    @Override
    public ImmutableSubstitution<ImmutableFunctionalTerm> getSubstitution() {
        return substitution;
    }

    @Override
    public ImmutableSet<Variable> getGroupingVariables() {
        return groupingVariables;
    }

    @Override
    public ImmutableSet<Variable> getChildVariables() {
        return childVariables;
    }

    @Override
    public ImmutableSet<Variable> getVariables() {
        return projectedVariables;
    }

    @Override
    public AggregationNode clone() {
        return iqFactory.createAggregationNode(groupingVariables, substitution);
    }

    @Override
    public String toString() {
        // TODO: display the query modifiers
        return AGGREGATE_NODE_STR + " " + groupingVariables + " " + "[" + substitution + "]" ;
    }
}
