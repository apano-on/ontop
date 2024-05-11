package it.unibz.inf.ontop.query.aggregates;

import it.unibz.inf.ontop.model.vocabulary.GEOF;

public class AggBoundingBoxAggregateFactory extends GeoSPARQLAggregateFactory {

    @Override
    public String getIri() {
        return GEOF.AGGBOUNDINGBOX.getIRIString();
    }
}