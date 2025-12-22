package it.unibz.inf.ontop.iq.node.normalization;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.iq.IQTree;
import it.unibz.inf.ontop.iq.IQTreeCache;
import it.unibz.inf.ontop.iq.node.LateralJoinNode;
import it.unibz.inf.ontop.utils.VariableGenerator;

/**
 * Normalizer for LATERAL inner joins.
 *
 * Unlike regular inner joins, LATERAL joins:
 * - Must preserve child ordering (left-to-right evaluation)
 * - Cannot apply commutative/associative optimizations
 * - Have right children that can reference left child variables
 */
public interface InnerJoinLateralNormalizer {

    /**
     * Normalizes a LATERAL inner join for optimization while preserving ordering constraints.
     *
     * @param lateralJoinNode the LATERAL join node to normalize
     * @param children the children of the join (order matters!)
     * @param variableGenerator generator for creating fresh variables
     * @param treeCache cache for tree metadata
     * @return the normalized IQ tree
     */
    IQTree normalizeForOptimizationLateral(LateralJoinNode lateralJoinNode,
                                           ImmutableList<IQTree> children,
                                           VariableGenerator variableGenerator,
                                           IQTreeCache treeCache);
}