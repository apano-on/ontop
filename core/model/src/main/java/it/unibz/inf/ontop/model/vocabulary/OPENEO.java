package it.unibz.inf.ontop.model.vocabulary;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.simple.SimpleRDF;

public class OPENEO {
    public static final String PREFIX = "http://www.openeo-ontop.org#";
    public static final IRI AVG;
    public static final IRI MAX;
    public static final IRI MIN;
    public static final IRI AGG;
    public static final IRI RASTER;
    public static final IRI LOAD_COLLECTION;
    public static final IRI SPATIAL_AGG;
    public static final IRI TEMPORAL_AGG;
    public static final IRI SPATIAL_TEMPORAL_AGG;
    public static final IRI SPATIAL_FILTER;
    public static final IRI TEMPORAL_FILTER;
    public static final IRI BAND_FILTER;
    public static final IRI REDUCE_DIMENSION;
    public static final IRI FILTER_DIMENSION;

    static {
        org.apache.commons.rdf.api.RDF factory = new SimpleRDF();
        AVG = factory.createIRI(PREFIX + "avg");
        MAX = factory.createIRI(PREFIX + "max");
        MIN = factory.createIRI(PREFIX + "min");
        AGG = factory.createIRI(PREFIX + "agg");
        RASTER = factory.createIRI(PREFIX + "raster");
        LOAD_COLLECTION = factory.createIRI(PREFIX + "load_collection");
        SPATIAL_AGG = factory.createIRI(PREFIX + "aggregate_spatial");
        TEMPORAL_AGG = factory.createIRI(PREFIX + "temporalAgg");
        SPATIAL_TEMPORAL_AGG = factory.createIRI(PREFIX + "spatialTemporalAgg");
        SPATIAL_FILTER = factory.createIRI(PREFIX + "spatialFilter");
        TEMPORAL_FILTER = factory.createIRI(PREFIX + "temporalFilter");
        BAND_FILTER = factory.createIRI(PREFIX + "bandFilter");
        REDUCE_DIMENSION = factory.createIRI(PREFIX + "reduce_dimension");
        FILTER_DIMENSION = factory.createIRI(PREFIX + "filterDimension");
    }

}
