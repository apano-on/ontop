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
    public static final IRI LOAD_COLLECTION_P;
    public static final IRI SPATIAL_AGG;
    public static final IRI TEMPORAL_AGG;
    public static final IRI SPATIAL_TEMPORAL_AGG;
    public static final IRI SPATIAL_FILTER;
    public static final IRI FILTER_TEMPORAL;
    public static final IRI BAND_FILTER;
    public static final IRI REDUCE_DIMENSION;
    public static final IRI FILTER_DIMENSION;
    public static final IRI BAND_MATH;
    public static final IRI APPLY;
    public static final IRI MASK;
    public static final IRI APPLY_DIMENSION;
    public static final IRI NDVI;
    public static final IRI APPLY_KERNEL;
    public static final IRI APPLY_KERNEL_C;
    public static final IRI ONEOF;
    public static final IRI CUBE_MATH;
    public static final IRI MERGE_CUBES;
    public static final IRI MERGE_CUBES_N;
    public static final IRI SAR_BACKSCATTER;
    public static final IRI RENAME_LABELS;
    public static final IRI TO_SCL_DILATION_MASK;
    public static final IRI APPLY_NEIGHBORHOOD;
    public static final IRI LINEAR_SCALE_RANGE;
    public static final IRI AGGREGATE_TEMPORAL_PERIOD;
    public static final IRI FILTER_BBOX;
    public static final IRI FILTER_BANDS;
    public static final IRI LOG;

    static {
        org.apache.commons.rdf.api.RDF factory = new SimpleRDF();
        AVG = factory.createIRI(PREFIX + "avg");
        MAX = factory.createIRI(PREFIX + "max");
        MIN = factory.createIRI(PREFIX + "min");
        AGG = factory.createIRI(PREFIX + "agg");
        RASTER = factory.createIRI(PREFIX + "raster");
        LOAD_COLLECTION = factory.createIRI(PREFIX + "load_collection");
        LOAD_COLLECTION_P = factory.createIRI(PREFIX + "load_collection");
        SPATIAL_AGG = factory.createIRI(PREFIX + "aggregate_spatial");
        TEMPORAL_AGG = factory.createIRI(PREFIX + "temporalAgg");
        SPATIAL_TEMPORAL_AGG = factory.createIRI(PREFIX + "spatialTemporalAgg");
        SPATIAL_FILTER = factory.createIRI(PREFIX + "spatialFilter");
        FILTER_TEMPORAL = factory.createIRI(PREFIX + "filter_temporal");
        BAND_FILTER = factory.createIRI(PREFIX + "bandFilter");
        REDUCE_DIMENSION = factory.createIRI(PREFIX + "reduce_dimension");
        FILTER_DIMENSION = factory.createIRI(PREFIX + "filterDimension");
        BAND_MATH = factory.createIRI(PREFIX + "band_math");
        APPLY = factory.createIRI(PREFIX + "apply");
        MASK = factory.createIRI(PREFIX + "mask");
        APPLY_DIMENSION = factory.createIRI(PREFIX + "apply_dimension");
        NDVI = factory.createIRI(PREFIX + "ndvi");
        APPLY_KERNEL = factory.createIRI(PREFIX + "apply_kernel");
        APPLY_KERNEL_C = factory.createIRI(PREFIX + "apply_kernel");
        ONEOF = factory.createIRI(PREFIX + "oneof");
        CUBE_MATH = factory.createIRI(PREFIX + "cube_math");
        MERGE_CUBES = factory.createIRI(PREFIX + "merge_cubes");
        MERGE_CUBES_N = factory.createIRI(PREFIX + "merge_cubes");
        SAR_BACKSCATTER = factory.createIRI(PREFIX + "sar_backscatter");
        RENAME_LABELS = factory.createIRI(PREFIX + "rename_labels");
        TO_SCL_DILATION_MASK = factory.createIRI(PREFIX + "to_scl_dilation_mask");
        APPLY_NEIGHBORHOOD = factory.createIRI(PREFIX + "apply_neighborhood");
        LINEAR_SCALE_RANGE = factory.createIRI(PREFIX + "linear_scale_range");
        AGGREGATE_TEMPORAL_PERIOD = factory.createIRI(PREFIX + "aggregate_temporal_period");
        FILTER_BBOX = factory.createIRI(PREFIX + "filter_bbox");
        FILTER_BANDS = factory.createIRI(PREFIX + "filter_bands");
        LOG = factory.createIRI(PREFIX + "log");
    }

}