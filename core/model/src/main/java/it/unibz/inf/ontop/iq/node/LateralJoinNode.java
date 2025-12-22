package it.unibz.inf.ontop.iq.node;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.iq.IQTree;
import it.unibz.inf.ontop.iq.NaryIQTree;
import it.unibz.inf.ontop.iq.visit.IQTreeVisitor;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.substitution.InjectiveSubstitution;

/**
 * See IntermediateQueryFactory for creating a new instance.
 */
public interface LateralJoinNode extends InnerJoinLikeNode {

    @Override
    LateralJoinNode applyFreshRenaming(InjectiveSubstitution<Variable> renamingSubstitution);

    @Override
    default <T> T acceptVisitor(NaryIQTree tree, IQTreeVisitor<T> visitor, ImmutableList<IQTree> children) {
        return visitor.transformLateralJoin(tree, this, children);
    }

    /**
     * Returns the left child whose variables are available to the right child
     */
    default IQTree getLeftChild(ImmutableList<IQTree> children) {
        return children.get(0);
    }

    /**
     * Returns right children that can reference left variables
     */
    default ImmutableList<IQTree> getRightChildren(ImmutableList<IQTree> children) {
        return children.subList(1, children.size());
    }

//    ImmutableSet<Variable> getBoundVariables();
}

