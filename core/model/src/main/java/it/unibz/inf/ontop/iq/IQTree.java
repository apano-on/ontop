package it.unibz.inf.ontop.iq;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.iq.node.QueryNode;
import it.unibz.inf.ontop.iq.transform.IQTransformer;
import it.unibz.inf.ontop.model.term.ImmutableExpression;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.model.term.VariableOrGroundTerm;
import it.unibz.inf.ontop.substitution.ImmutableSubstitution;
import it.unibz.inf.ontop.utils.VariableGenerator;

import java.util.Optional;


public interface IQTree {

    QueryNode getRootNode();

    ImmutableList<IQTree> getChildren();

    ImmutableSet<Variable> getVariables();

    IQTree acceptTransformer(IQTransformer transformer);

    IQTree liftBinding(VariableGenerator variableGenerator);

    /**
     * Tries to lift unions when they have incompatible definitions
     * for a variable.
     *
     * Union branches with compatible definitions are kept together
     *
     * Assumes that a "regular" binding lift has already been applied
     *   --> the remaining "non-lifted" definitions are conflicting with
     *       others.
     */
    IQTree liftIncompatibleDefinitions(Variable variable);

    default boolean isLeaf() {
        return getChildren().isEmpty();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    IQTree applyDescendingSubstitution(
            ImmutableSubstitution<? extends VariableOrGroundTerm> descendingSubstitution,
            Optional<ImmutableExpression> constraint);

    ImmutableSet<Variable> getKnownVariables();

    /**
     * Returns true if the variable is (at least in one branch) constructed by a substitution
     * (in a construction node)
     */
    boolean isConstructed(Variable variable);

    /**
     * Returns true if corresponds to a EmptyNode
     */
    boolean isDeclaredAsEmpty();

    default boolean containsNullableVariable(Variable variable) {
        return getNullableVariables().contains(variable);
    }

    ImmutableSet<Variable> getNullableVariables();

    boolean isEquivalentTo(IQTree tree);

    /**
     * TODO: explain
     *
     * The constraint is used for pruning. It remains enforced by
     * a parent tree.
     *
     */
    IQTree propagateDownConstraint(ImmutableExpression constraint);
}