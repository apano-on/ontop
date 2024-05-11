package it.unibz.inf.ontop.query.aggregates;

import it.unibz.inf.ontop.model.vocabulary.GEOF;

public class AggConcaveHullAggregateFactory extends GeoSPARQLAggregateFactory {

    @Override
    public String getIri() {
        return GEOF.AGGCONCAVEHULL.getIRIString();
    }
}