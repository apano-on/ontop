package it.unibz.inf.ontop.iq.optimizer.impl;

import com.google.inject.Inject;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.iq.IQ;
import it.unibz.inf.ontop.iq.IQTree;
import it.unibz.inf.ontop.iq.optimizer.AccessPatternEnforcementOptimizer;
import it.unibz.inf.ontop.iq.transform.impl.AccessPatternToLateralJoinTransformerImpl;

public class AccessPatternEnforcementOptimizerImpl implements AccessPatternEnforcementOptimizer {

    private final IntermediateQueryFactory iqFactory;
    private final AccessPatternToLateralJoinTransformerImpl transformer;

    @Inject
    public AccessPatternEnforcementOptimizerImpl(IntermediateQueryFactory iqFactory) {
        this.iqFactory = iqFactory;
        this.transformer = new AccessPatternToLateralJoinTransformerImpl(iqFactory);
    }

    @Override
    public IQ optimize(IQ query) {
        IQTree transformedTree = transformer.transform(query.getTree());

        // If nothing changed, return original query
        if (transformedTree.equals(query.getTree())) {
            return query;
        }

        // Create new IQ with transformed tree
        return iqFactory.createIQ(query.getProjectionAtom(), transformedTree)
                .normalizeForOptimization();
    }
}