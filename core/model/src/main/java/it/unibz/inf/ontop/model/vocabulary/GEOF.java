package it.unibz.inf.ontop.model.vocabulary;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.simple.SimpleRDF;

public class GEOF {

    public static final String PREFIX = "http://www.opengis.net/def/function/geosparql/";

    // Prefix for properties
    public static final String PREFIX_PROP = "http://www.opengis.net/ont/geosparql/";

    // Simple Feature functions
    public static final IRI SF_EQUALS;

    public static final IRI SF_DISJOINT;

    public static final IRI SF_INTERSECTS;

    public static final IRI SF_TOUCHES;

    public static final IRI SF_CROSSES;

    public static final IRI SF_WITHIN;

    public static final IRI SF_CONTAINS;

    public static final IRI SF_OVERLAPS;

    // Egenhofer functions
    public static final IRI EH_EQUALS;

    public static final IRI EH_DISJOINT;

    public static final IRI EH_MEET;

    public static final IRI EH_OVERLAP;

    public static final IRI EH_COVERS;

    public static final IRI EH_COVEREDBY;

    public static final IRI EH_INSIDE;

    public static final IRI EH_CONTAINS;

    // RCC8 functions
    public static final IRI RCC8_EQ;

    public static final IRI RCC8_DC;

    public static final IRI RCC8_EC;

    public static final IRI RCC8_PO;

    public static final IRI RCC8_TPPI;

    public static final IRI RCC8_TPP;

    public static final IRI RCC8_NTPP;

    public static final IRI RCC8_NTPPI;

    // non-topological and common form functions

    public static final IRI DISTANCE;

    public static final IRI BUFFER;

    public static final IRI BOUNDARY;

    public static final IRI CONVEXHULL;

    public static final IRI DIFFERENCE;

    public static final IRI ENVELOPE;

    public static final IRI INTERSECTION;

    public static final IRI GETSRID;

    public static final IRI SYMDIFFERENCE;

    public static final IRI UNION;

    // Relate with boolean results
    public static final IRI RELATE;

    // Relate with string result
    public static final IRI RELATEM;

    /**
     * GeoSPARQL 1.1
     */
    // Requirement 39: Non-topological Query Functions (Simple Features)
    public static final IRI BOUNDINGCIRCLE;
    public static final IRI METRICBUFFER;
    public static final IRI CENTROID;
    public static final IRI CONCAVEHULL;
    public static final IRI COORDINATEDIMENSION;
    public static final IRI DIMENSION;
    public static final IRI METRICDISTANCE;
    public static final IRI GEOMETRYTYPE;
    public static final IRI IS3D;
    public static final IRI ISEMPTY;
    public static final IRI ISMEASURED;
    public static final IRI ISSIMPLE;
    public static final IRI SPATIALDIMENSION;
    public static final IRI TRANSFORM;

    // Requirement 40: Non-topological Query Functions (Non Simple Features)
    public static final IRI METRICLENGTH;
    public static final IRI LENGTH;
    public static final IRI METRICPERIMETER;
    public static final IRI PERIMETER;
    public static final IRI METRICAREA;
    public static final IRI AREA;
    public static final IRI GEOMETRYN;
    public static final IRI MAXX;
    public static final IRI MAXY;
    public static final IRI MAXZ;
    public static final IRI MINX;
    public static final IRI MINY;
    public static final IRI MINZ;
    public static final IRI NUMGEOMETRIES;

    // Requirement 42: Spatial Aggregate Functions
    public static final IRI AGGBOUNDINGBOX;
    public static final IRI AGGBOUNDINGCIRCLE;
    public static final IRI AGGCENTROID;
    public static final IRI AGGCONCAVEHULL;
    public static final IRI AGGCONVEXHULL;
    public static final IRI AGGUNION;

    static {
        org.apache.commons.rdf.api.RDF factory = new SimpleRDF();

        // Simple Feature functions
        SF_EQUALS = factory.createIRI(PREFIX + "sfEquals");

        SF_DISJOINT = factory.createIRI(PREFIX + "sfDisjoint");

        SF_INTERSECTS = factory.createIRI(PREFIX + "sfIntersects");

        SF_TOUCHES = factory.createIRI(PREFIX + "sfTouches");

        SF_CROSSES = factory.createIRI(PREFIX + "sfCrosses");

        SF_WITHIN = factory.createIRI(PREFIX + "sfWithin");

        SF_CONTAINS = factory.createIRI(PREFIX + "sfContains");

        SF_OVERLAPS = factory.createIRI(PREFIX + "sfOverlaps");

        // Egenhofer functions
        EH_EQUALS = factory.createIRI(PREFIX + "ehEquals");

        EH_DISJOINT = factory.createIRI(PREFIX + "ehDisjoint");

        EH_MEET = factory.createIRI(PREFIX + "ehMeet");

        EH_OVERLAP = factory.createIRI(PREFIX + "ehOverlap");

        EH_COVERS = factory.createIRI(PREFIX + "ehCovers");

        EH_COVEREDBY = factory.createIRI(PREFIX + "ehCoveredBy");

        EH_INSIDE = factory.createIRI(PREFIX + "ehInside");

        EH_CONTAINS = factory.createIRI(PREFIX + "ehContains");

        // RCC8 functions
        RCC8_EQ = factory.createIRI(PREFIX + "rcc8eq");

        RCC8_DC = factory.createIRI(PREFIX + "rcc8dc");

        RCC8_EC = factory.createIRI(PREFIX + "rcc8ec");

        RCC8_PO = factory.createIRI(PREFIX + "rcc8po");

        RCC8_TPP = factory.createIRI(PREFIX + "rcc8tpp");

        RCC8_TPPI = factory.createIRI(PREFIX + "rcc8tppi");

        RCC8_NTPP = factory.createIRI(PREFIX + "rcc8ntpp");

        RCC8_NTPPI = factory.createIRI(PREFIX + "rcc8ntppi");

        // non-topological and common form functions

        DISTANCE = factory.createIRI(PREFIX + "distance");

        BUFFER = factory.createIRI(PREFIX + "buffer");

        BOUNDARY = factory.createIRI(PREFIX + "boundary");

        CONVEXHULL = factory.createIRI(PREFIX + "convexHull");

        DIFFERENCE = factory.createIRI(PREFIX + "difference");

        ENVELOPE = factory.createIRI(PREFIX + "envelope");

        INTERSECTION = factory.createIRI(PREFIX + "intersection");

        GETSRID = factory.createIRI(PREFIX + "getSRID");

        SYMDIFFERENCE = factory.createIRI(PREFIX + "symDifference");

        UNION = factory.createIRI(PREFIX + "union");

        // geof:relate --> Boolean result
        RELATE = factory.createIRI(PREFIX + "relate");

        // geof:relate --> String result
        RELATEM = factory.createIRI(PREFIX + "relate");

        /**
         * GeoSPARQL 1.1
         */
        BOUNDINGCIRCLE = factory.createIRI(PREFIX + "boundingCircle");
        METRICBUFFER = factory.createIRI(PREFIX + "metricBuffer");
        CENTROID = factory.createIRI(PREFIX + "centroid");
        CONCAVEHULL = factory.createIRI(PREFIX + "concaveHull");
        COORDINATEDIMENSION = factory.createIRI(PREFIX + "coordinateDimension");
        DIMENSION = factory.createIRI(PREFIX + "dimension");
        METRICDISTANCE = factory.createIRI(PREFIX + "metricDistance");
        GEOMETRYTYPE = factory.createIRI(PREFIX + "geometryType");
        IS3D = factory.createIRI(PREFIX + "is3D");
        ISEMPTY = factory.createIRI(PREFIX + "isEmpty");
        ISMEASURED = factory.createIRI(PREFIX + "isMeasured");
        ISSIMPLE = factory.createIRI(PREFIX + "isSimple");
        SPATIALDIMENSION = factory.createIRI(PREFIX + "spatialDimension");
        TRANSFORM = factory.createIRI(PREFIX + "transform");

        LENGTH = factory.createIRI(PREFIX + "length");
        METRICLENGTH = factory.createIRI(PREFIX + "metricLength");
        PERIMETER = factory.createIRI(PREFIX + "perimeter");
        METRICPERIMETER = factory.createIRI(PREFIX + "metricPerimeter");
        AREA = factory.createIRI(PREFIX + "area");
        METRICAREA = factory.createIRI(PREFIX + "metricArea");
        GEOMETRYN = factory.createIRI(PREFIX + "geometryN");
        MAXX = factory.createIRI(PREFIX + "maxX");
        MAXY = factory.createIRI(PREFIX + "maxY");
        MAXZ = factory.createIRI(PREFIX + "maxZ");
        MINX = factory.createIRI(PREFIX + "minX");
        MINY = factory.createIRI(PREFIX + "minY");
        MINZ = factory.createIRI(PREFIX + "minZ");
        NUMGEOMETRIES = factory.createIRI(PREFIX + "numGeometries");

        AGGBOUNDINGBOX = factory.createIRI(PREFIX + "aggBoundingBox");
        AGGBOUNDINGCIRCLE = factory.createIRI(PREFIX + "aggBoundingCircle");
        AGGCENTROID = factory.createIRI(PREFIX + "aggCentroid");
        AGGCONCAVEHULL = factory.createIRI(PREFIX + "aggConcaveHull");
        AGGCONVEXHULL = factory.createIRI(PREFIX + "aggConvexHull");
        AGGUNION = factory.createIRI(PREFIX + "aggUnion");



    }
}

