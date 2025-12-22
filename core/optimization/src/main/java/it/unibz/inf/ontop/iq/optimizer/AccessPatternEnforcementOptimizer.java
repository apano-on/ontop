package it.unibz.inf.ontop.iq.optimizer;

import it.unibz.inf.ontop.iq.IQ;

/**
 * Transforms InnerJoins into LateralJoins when required by access patterns.
 *
 * This optimizer must run early in the optimization pipeline, after lens unfolding
 * but before other join optimizations that might reorder joins.
 */
public interface AccessPatternEnforcementOptimizer {

    /**
     * Transforms the IQ tree to enforce access pattern constraints by
     * converting inner joins to lateral joins where necessary.
     */
    IQ optimize(IQ query);
}