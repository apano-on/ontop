package it.unibz.inf.ontop.docker.lightweight;

import com.google.common.collect.ImmutableList;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class AbstractGeoSPARQLv11Test extends AbstractDockerRDF4JTest {

    protected static final String OWL_FILE = "/geospatial/geospatial.owl";
    protected static final String OBDA_FILE = "/geospatial/geospatialv1_1.obda";

    @Disabled("PostGIS requires MinimumBoundingCircle function")
    @Test
    public void testSelectBoundingCircle() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:boundingCircle(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POLYGON ((12.071067811865476 5, 11.935199226610738 3.620503103585285, " +
                "11.532814824381884 2.294019499269015, 10.879378012096794 1.0715252080644895, 10 0, " +
                "8.92847479193551 -0.8793780120967938, 7.705980500730986 -1.5328148243818829, " +
                "6.379496896414715 -1.9351992266107372, 5 -2.0710678118654755, 3.6205031035852855 -1.9351992266107372, " +
                "2.2940194992690155 -1.5328148243818829, 1.0715252080644908 -0.8793780120967956, 0 -0.0000000000000009, " +
                "-0.8793780120967947 1.0715252080644895, -1.5328148243818829 2.294019499269014, -1.9351992266107372 3.6205031035852824, " +
                "-2.0710678118654755 4.999999999999999, -1.9351992266107372 6.379496896414716, -1.5328148243818838 7.705980500730984, " +
                "-0.8793780120967956 8.928474791935509, -0.0000000000000009 10, 1.0715252080644895 10.879378012096794, " +
                "2.294019499269011 11.53281482438188, 3.620503103585282 11.935199226610736, 4.999999999999999 12.071067811865476, " +
                "6.379496896414715 11.935199226610738, 7.705980500730987 11.532814824381882, 8.928474791935509 10.879378012096796, " +
                "10 10, 10.879378012096794 8.92847479193551, 11.53281482438188 7.705980500730989, " +
                "11.935199226610736 6.379496896414718, 12.071067811865476 5))\"^^geo:wktLiteral"));
    }

    @Test
    public void testSelectMetricBuffer() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:metricBuffer(?xWkt, 1) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POLYGON((-0.00000898054534 -0.000000153417746," +
                "-0.000009183768039 9.999999736438603,-0.000009035580128 10.000001500063481," +
                "-0.000008537021531 10.000003205133346,-0.000007707424744 10.00000478553108," +
                "-0.000006578958848 10.000006179973948,-0.000005195382108 10.000007334389938," +
                "-0.000003610345179 10.000008204014527,-0.000001885310688 10.000008755126462," +
                "-0.000000087169914 10.000008966355395,10.000000223234656 10.00000902928204," +
                "10.000001964323957 10.000008826065915,10.000003632775899 10.000008296107476," +
                "10.000005166893967 10.000007459003683,10.000006509949088 10.000006345709211," +
                "10.000007612277367 10.000004997391787,10.000008433116587 10.00000346390991," +
                "10.000008942113512 10.000001801969116,10.00000912044631 10.000000073025156," +
                "10.00000891567233 0.000000050008457,10.00000872948539 -0.000001753948305," +
                "10.000008198146011 -0.000003488556233,10.00000734266265 -0.000005085231214," +
                "10.00000619686001 -0.000006480842798,10.000004806041632 -0.000007620210328," +
                "10.000003225198654 -0.000008458284704,10.000001516835537 -0.000008961929572," +
                "9.999999748498732 -0.000009111231466,-0.000000147805818 -0.000009048845147," +
                "-0.000001897375156 -0.000008905448475,-0.000003573997228 -0.000008419669295," +
                "-0.000005113211946 -0.000007610184076,-0.000006455842025 -0.00000650811461," +
                "-0.000007550268178 -0.000005155831522,-0.000008354413632 -0.000003605325224," +
                "-0.000008837361892 -0.000001916207125,-0.00000898054534 -0.000000153417746))\"^^geo:wktLiteral"));
    }

    @Test
    public void testSelectCentroid() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:centroid(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POINT(5 5)\"^^geo:wktLiteral"));
    }

    @Disabled("PostGIS ST_CONCAVEHULL requires a second parameter")
    @Test
    public void testSelectConcaveHull() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:concaveHull(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POLYGON ((10 0, 0 0, 0 10, 10 10, 10 0))\"^^geo:wktLiteral"));
    }

    @Test
    public void testSelectCoordinateDimension() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:coordinateDimension(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"2\"^^xsd:integer"));
    }

    @Test
    public void testSelectDimension() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:dimension(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"2\"^^xsd:integer"));
    }

    @Test
    public void testSelectMetricDistance() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":6 a :Geom; geo:asWKT ?xWkt.\n" +
                ":7 a :Geom; geo:asWKT ?yWkt.\n" +
                "BIND (geof:metricDistance(?xWkt, ?yWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"339241.2973924\"^^xsd:double"));
    }

    @Test
    public void testSelectGeometryType() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:geometryType(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("<http://www.opengis.net/ont/sf#Polygon>"));
    }

    /*@Test
    public void testAskis3DFalse() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:is3D(?xWkt) AS ?v)\n" +
                "}\n";

        executeAndCompareValues(query, ImmutableList.of("\"false\"^^xsd:boolean"));
    }

    @Test
    public void testAskis3DTrue() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":geomz1 a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:is3D(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"true\"^^xsd:boolean"));
    }*/

    @Test
    public void testAskisMeasured() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND(geof:isMeasured(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"false\"^^xsd:boolean"));
    }

    @Test
    public void testAskisEmpty() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:isEmpty(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"false\"^^xsd:boolean"));
    }

    @Test
    public void testAskisSimple() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:isSimple(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"true\"^^xsd:boolean"));
    }

    @Test
    public void testSelectSpatialDimension() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:spatialDimension(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"2\"^^xsd:integer"));
    }

    @Test
    public void testSelectTransformEPSG3044() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":4 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:transform(?xWkt, <http://www.opengis.net/def/crs/EPSG/0/3044>) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POINT(-280405.62793313444 222731.06606055034)\"^^geo:wktLiteral"));
    }

    @Test
    public void testSelectTransformCRS84() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":4 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:transform(?xWkt, <http://www.opengis.net/def/crs/OGC/1.3/CRS/84>) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POINT(2 2)\"^^geo:wktLiteral"));
    }

    @Test
    public void testSelectTransformIncorrectSRID() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":4 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:transform(?xWkt, <http://www.opengis.net/def/crs/EPSG/0/32439523>) AS ?v)\n" +
                "}\n";
        var error = assertThrows(QueryEvaluationException.class, () -> this.runQuery(query));
        assertEquals("it.unibz.inf.ontop.exception.OntopReformulationException: java.lang.IllegalArgumentException: " +
                "Unknown SRID: http://www.opengis.net/def/crs/EPSG/0/32439523", error.getMessage());
    }

    @Test
    public void testSelectGeometryN() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":5 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:geometryN(?xWkt, 2) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POLYGON((10 10,15 10,15 15,10 15,10 10))\"^^geo:wktLiteral"));
    }

    @Test
    public void testSelectArea() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:area(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"100\"^^xsd:double"));
    }

    @Test
    public void testSelectMetricArea() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:metricArea(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"100\"^^xsd:double"));
    }

    @Test
    public void testSelectPerimeter() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:perimeter(?xWkt, uom:metre) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"40\"^^xsd:double"));
    }

    @Test
    public void testSelectMetricPerimeter() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:metricPerimeter(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"40\"^^xsd:double"));
    }

    @Test
    public void testSelectLength() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":3 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:length(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"14.142135623730951\"^^xsd:double"));
    }

    @Test
    public void testSelectMetricLength() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":3 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:metricLength(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"14.142135623730951\"^^xsd:double"));
    }

    @Test
    public void testSelectMaxX() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:maxX(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"10\"^^xsd:double"));
    }

    @Test
    public void testSelectMaxY() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:maxY(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"10\"^^xsd:double"));
    }

    @Test
    public void testSelectMaxZ() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":geomz2 a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:maxZ(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"2\"^^xsd:double"));
    }

    @Test
    public void testSelectMinX() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:minX(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"0\"^^xsd:double"));
    }

    @Test
    public void testSelectMinY() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:minY(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"0\"^^xsd:double"));
    }

    @Test
    public void testSelectMinZ() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":geomz2 a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:minZ(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"0\"^^xsd:double"));
    }

    @Test
    public void testSelectNumGeometries() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:numGeometries(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"1\"^^xsd:integer"));
    }

    @Test
    public void testSelectAggBoundingBox() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "?g a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggBoundingBox(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POLYGON((0 0,0 15,15 15,15 0,0 0))\"^^geo:wktLiteral"));
    }

    @Test
    public void testSelectAggBoundingCircle() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "?g a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggBoundingCircle(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POLYGON((0 0,0 15,15 15,15 0,0 0))\"^^geo:wktLiteral"));
    }

    @Test
    public void testSelectAggCentroid() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "?g a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggCentroid(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POINT(7.5 7.5)\"^^geo:wktLiteral"));
    }

    @Disabled
    @Test
    public void testSelectAggConcaveHull() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "?g a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggConcaveHull(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POLYGON Z((5 5 0, 0 10 1, 5 15 2, 15 15 2, 10 10 0, 15 5 2, " +
                "10 0 0, 0 0 0, 5 5 0))\"^^geo:wktLiteral"));
    }

    @Test
    public void testSelectAggConvexHull() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "?g a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggConvexHull(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"POLYGON Z ((0 0 0,0 10 1,5 15 2,15 15 2,15 5 2,10 0 0," +
                "0 0 0))\"^^geo:wktLiteral"));
    }

    /*@Test
    public void testSelectAggUnion() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "?g a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggUnion(?xWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"GEOMETRYCOLLECTION (POINT (-0.0754 51.5055), " +
                "POINT (2.2945 48.8584), " +
                "POLYGON ((0 5, 0 10, 5 10, 5 15, 10 15, 15 15, 15 10, 15 5, 10 5, 10 0, 5 0, 0 0, 0 5)))\"^^geo:wktLiteral"));
    }*/

    @Disabled("Not supported yet")
    @Test
    public void testSelectAggUnionMultipleArg() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "?g a :Geom; geo:asWKT ?xWkt.\n" +
                "?gz a :GeomZ; geo:asWKT ?yWkt.\n" +
                "BIND (geof:aggUnion(?xWkt, ?yWkt) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"GEOMETRYCOLLECTION (POINT (-0.0754 51.5055), " +
                "POINT (2.2945 48.8584), " +
                "POLYGON ((0 5, 0 10, 5 10, 5 15, 10 15, 15 15, 15 10, 15 5, 10 5, 10 0, 5 0, 0 0, 0 5)))\"^^geo:wktLiteral"));
    }

    @Test
    public void testSelectAggUnionWithGroupBy() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "SELECT (geof:aggUnion(?xWkt) AS ?v) ?label WHERE {\n" +
                "?g a :Geom; geo:asWKT ?xWkt.\n" +
                "?g rdfs:label ?label .\n" +
                "BIND (geof:aggUnion(?xWkt) AS ?v)\n" +
                "}\n" +
                "GROUP BY ?label\n" +
                "ORDER BY ?label";
        executeAndCompareValues(query, ImmutableList.of("\"POINT (2.2945 48.8584)\"^^geo:wktLiteral"));
    }

    /*@Test
    public void testSelectAggUnionWithValues() {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?v WHERE {\n" +
                "VALUES (?geom) {\n" +
                "(\"POINT(1 1)\"^^geo:wktLiteral)\n" +
                "(\"POINT(2 2)\"^^geo:wktLiteral)\n" +
                "(\"POINT(3 3)\"^^geo:wktLiteral)\n" +
                "}\n" +
                "BIND(geof:aggUnion(?geom) AS ?v)\n" +
                "}\n";
        executeAndCompareValues(query, ImmutableList.of("\"MULTIPOINT((1 1), (2 2), (3 3))\"^^geo:wktLiteral"));
    }*/
}
