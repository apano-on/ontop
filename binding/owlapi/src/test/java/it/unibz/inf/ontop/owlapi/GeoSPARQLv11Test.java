package it.unibz.inf.ontop.owlapi;

import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.owlapi.connection.OWLConnection;
import it.unibz.inf.ontop.owlapi.connection.OWLStatement;
import it.unibz.inf.ontop.owlapi.exception.OntopOWLException;
import it.unibz.inf.ontop.owlapi.impl.SimpleOntopOWLEngine;
import it.unibz.inf.ontop.owlapi.resultset.BooleanOWLResultSet;
import it.unibz.inf.ontop.owlapi.resultset.OWLBindingSet;
import it.unibz.inf.ontop.owlapi.resultset.TupleOWLResultSet;
import org.h2gis.functions.factory.H2GISFunctions;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import java.sql.Connection;
import java.sql.DriverManager;

import static it.unibz.inf.ontop.utils.OWLAPITestingTools.executeFromFile;
import static org.junit.Assert.*;

public class GeoSPARQLv11Test {
    private static final String owlFile = "src/test/resources/geosparqlv1_1/geosparql.owl";
    private static final String obdaFile = "src/test/resources/geosparqlv1_1/geosparql-h2.obda";
    private static final String propertyFile = "src/test/resources/geosparqlv1_1/geosparql-h2.properties";

    private OntopOWLEngine reasoner;
    private OWLConnection conn;
    private Connection sqlConnection;

    @Before
    public void setUp() throws Exception {

        sqlConnection = DriverManager.getConnection("jdbc:h2:mem:geoms", "sa", "");
        H2GISFunctions.load(sqlConnection);
        executeFromFile(sqlConnection, "src/test/resources/geosparqlv1_1/create-h2.sql");

        OntopSQLOWLAPIConfiguration config = OntopSQLOWLAPIConfiguration.defaultBuilder()
                .ontologyFile(owlFile)
                .nativeOntopMappingFile(obdaFile)
                .propertyFile(propertyFile)
                .enableTestMode()
                .build();

        reasoner = new SimpleOntopOWLEngine(config);
        conn = reasoner.getConnection();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
        reasoner.close();
        if (!sqlConnection.isClosed()) {
            try (java.sql.Statement s = sqlConnection.createStatement()) {
                s.execute("DROP ALL OBJECTS DELETE FILES");
            } finally {
                sqlConnection.close();
            }
        }
    }

    @Test
    public void testBoundingCircle() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:boundingCircle(?xWkt) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("POLYGON ((12.071067811865476 5, 11.935199226610738 3.620503103585285, " +
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
                "11.935199226610736 6.379496896414718, 12.071067811865476 5))", val);
    }

    @Test
    public void testMetricBuffer() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:metricBuffer(?xWkt, 1) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("POLYGON ((-0.0000089932358214 0, -0.0000089932358214 10, -0.0000088204333169 10.000001754493272, " +
                "-0.0000083086665065 10.000003441562352, -0.0000074776023018 10.000004996374122, " +
                "-0.0000063591780341 10.000006359178034, -0.0000049963741209 10.000007477602301, " +
                "-0.0000034415623522 10.000008308666507, -0.0000017544932724 10.000008820433317, " +
                "0 10.000008993235822, 10 10.000008993235822, 10.000001754493272 10.000008820433317, " +
                "10.000003441562352 10.000008308666507, 10.000004996374122 10.000007477602301, " +
                "10.000006359178034 10.000006359178034, 10.000007477602301 10.000004996374122, " +
                "10.000008308666507 10.000003441562352, 10.000008820433317 10.000001754493272, 10.000008993235822 10, " +
                "10.000008993235822 0, 10.000008820433317 -0.0000017544932724, 10.000008308666507 -0.0000034415623522, " +
                "10.000007477602301 -0.0000049963741209, 10.000006359178034 -0.0000063591780341, " +
                "10.000004996374122 -0.0000074776023018, 10.000003441562352 -0.0000083086665065, " +
                "10.000001754493272 -0.0000088204333169, 10 -0.0000089932358214, 0 -0.0000089932358214, " +
                "-0.0000017544932724 -0.0000088204333169, -0.0000034415623522 -0.0000083086665065, " +
                "-0.0000049963741209 -0.0000074776023018, -0.0000063591780341 -0.0000063591780341, " +
                "-0.0000074776023018 -0.0000049963741209, -0.0000083086665065 -0.0000034415623522, " +
                "-0.0000088204333169 -0.0000017544932724, -0.0000089932358214 0))", val);
    }

    @Test
    public void testCentroid() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:centroid(?xWkt) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("POINT (5 5)", val);
    }

    @Test
    public void testConcaveHull() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:concaveHull(?xWkt) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("POLYGON ((10 0, 0 0, 0 10, 10 10, 10 0))", val);
    }

    @Test
    public void testCoordinateDimension() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:coordinateDimension(?xWkt) AS ?x)\n" +
                "}\n";
        int val = Integer.parseInt(runQueryAndReturnString(query));
        assertEquals(2, val);
    }

    @Test
    public void testDimension() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:dimension(?xWkt) AS ?x)\n" +
                "}\n";
        int val = Integer.parseInt(runQueryAndReturnString(query));
        assertEquals(2, val);
    }

    @Test
    public void testMetricDistance() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":6 a :Geom; geo:asWKT ?xWkt.\n" +
                ":7 a :Geom; geo:asWKT ?yWkt.\n" +
                "BIND (geof:metricDistance(?xWkt, ?yWkt) AS ?x)\n" +
                "}\n";
        double val = runQueryAndReturnDoubleX(query);
        assertEquals(339241.29739240103, val, 0.01);
    }

    @Test
    public void testGeometryType() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:geometryType(?xWkt) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnIRI(query);
        // NOTE: No GeoSPARQL restriction on polygon name depending on engine
        assertEquals("<http://www.opengis.net/ont/sf#POLYGON>", val);
    }

    @Ignore("We use ST_HASM which is not supported in H2GIS")
    @Test
    public void testis3DFalse() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:is3D(?xWkt) AS ?x)\n" +
                "}\n";
        boolean val = runQueryAndReturnString(query).equals("false");
        assertTrue(val);
    }

    @Ignore("We use ST_HASM which is not supported in H2GIS")
    @Test
    public void testis3DTrue() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":geomz1 a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:is3D(?xWkt) AS ?x)\n" +
                "}\n";
        boolean val = runQueryAndReturnString(query).equals("true");
        assertTrue(val);
    }

    @Ignore("We use ST_HASM which is not supported in H2GIS")
    @Test
    public void testisMeasured() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:isMeasured(?xWkt) AS ?x)\n" +
                "}\n";
        boolean val = runQueryAndReturnString(query).equals("true");
        assertFalse(val);
    }

    @Test
    public void testisEmpty() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "ASK WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "FILTER (geof:isEmpty(?xWkt))\n" +
                "}\n";
        boolean val = runQueryAndReturnBooleanX(query);
        assertFalse(val);
    }

    @Test
    public void testisSimple() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "ASK WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "FILTER (geof:isSimple(?xWkt))\n" +
                "}\n";
        boolean val = runQueryAndReturnBooleanX(query);
        assertTrue(val);
    }

    @Ignore("We use ST_HASM which is not supported in H2GIS")
    @Test
    public void testSpatialDimension() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:spatialDimension(?xWkt) AS ?x)\n" +
                "}\n";
        int val = Integer.parseInt(runQueryAndReturnString(query));
        assertEquals(2, val);
    }

    @Test
    public void testTransformEPSG3044() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":4 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:transform(?xWkt, <http://www.opengis.net/def/crs/EPSG/0/3044>) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("POINT (-280405.62793311477 222731.06606052682)", val);
    }

    @Test
    public void testTransformCRS84() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":4 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:transform(?xWkt, <http://www.opengis.net/def/crs/OGC/1.3/CRS/84>) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("POINT (2 2)", val);
    }

    @Test(expected = OntopOWLException.class)
    public void testTransformIncorrectSRID() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":4 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:transform(?xWkt, <http://www.opengis.net/def/crs/EPSG/0/32439523>) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("POINT (2 2)", val);
    }

    @Test
    public void testGeometryN() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":5 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:geometryN(?xWkt, 2) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("POLYGON ((10 10, 15 10, 15 15, 10 15, 10 10))", val);
    }

    @Test
    public void testArea() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:area(?xWkt) AS ?x)\n" +
                "}\n";
        double val = runQueryAndReturnDoubleX(query);
        assertEquals(100.0, val, 1);
    }

    @Test
    public void testMetricArea() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:metricArea(?xWkt) AS ?x)\n" +
                "}\n";
        double val = runQueryAndReturnDoubleX(query);
        assertEquals(100.0, val, 1);
    }

    @Test
    public void testPerimeter() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:perimeter(?xWkt, uom:metre) AS ?x)\n" +
                "}\n";
        double val = runQueryAndReturnDoubleX(query);
        assertEquals(40.0, val, 1);
    }

    @Test
    public void testMetricPerimeter() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:metricPerimeter(?xWkt) AS ?x)\n" +
                "}\n";
        double val = runQueryAndReturnDoubleX(query);
        assertEquals(40.0, val, 1);
    }

    @Test
    public void testLength() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":3 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:length(?xWkt) AS ?x)\n" +
                "}\n";
        double val = runQueryAndReturnDoubleX(query);
        assertEquals(14.142135623730951, val, 0.01);
    }

    @Test
    public void testMetricLength() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":3 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:metricLength(?xWkt) AS ?x)\n" +
                "}\n";
        double val = runQueryAndReturnDoubleX(query);
        assertEquals(14.142135623730951, val, 0.01);
    }

    @Test
    public void testMaxX() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:maxX(?xWkt) AS ?x)\n" +
                "}\n";
        double val = runQueryAndReturnDoubleX(query);
        assertEquals(10.0, val, 1);
    }

    @Test
    public void testMaxY() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:maxY(?xWkt) AS ?x)\n" +
                "}\n";
        double val = runQueryAndReturnDoubleX(query);
        assertEquals(10.0, val, 1);
    }

    @Test
    public void testMaxZ() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":geomz2 a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:maxZ(?xWkt) AS ?x)\n" +
                "}\n";
        double val = runQueryAndReturnDoubleX(query);
        assertEquals(2.0, val, 1);
    }

    @Test
    public void testMinX() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:minX(?xWkt) AS ?x)\n" +
                "}\n";
        double val = runQueryAndReturnDoubleX(query);
        assertEquals(0.0, val, 1);
    }

    @Test
    public void testMinY() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:minY(?xWkt) AS ?x)\n" +
                "}\n";
        double val = runQueryAndReturnDoubleX(query);
        assertEquals(0.0, val, 1);
    }

    @Test
    public void testMinZ() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":geomz2 a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:minZ(?xWkt) AS ?x)\n" +
                "}\n";
        double val = runQueryAndReturnDoubleX(query);
        assertEquals(0.0, val, 1);
    }

    @Test
    public void testNumGeometries() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:numGeometries(?xWkt) AS ?x)\n" +
                "}\n";
        int val = Integer.parseInt(runQueryAndReturnString(query));
        assertEquals(1, val);
    }

    @Test
    public void testAggBoundingBox() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                "?g a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggBoundingBox(?xWkt) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("POLYGON ((0 0, 0 15, 15 15, 15 0, 0 0))", val);
    }

    @Test
    public void testAggBoundingCircle() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                "?g a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggBoundingCircle(?xWkt) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("POLYGON ((0 0, 0 15, 15 15, 15 0, 0 0))", val);
    }

    @Test
    public void testAggCentroid() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                "?g a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggCentroid(?xWkt) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("POINT (7.5 7.5)", val);
    }

    @Test
    public void testAggConcaveHull() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                "?g a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggConcaveHull(?xWkt) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("POLYGON Z((5 5 0, 0 10 1, 5 15 2, 15 15 2, 10 10 0, 15 5 2, 10 0 0, 0 0 0, 5 5 0))", val);
    }

    @Test
    public void testAggConvexHull() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                "?g a :GeomZ; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggConvexHull(?xWkt) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("POLYGON Z((0 0 0, 0 10 1, 5 15 2, 15 15 2, 15 5 2, 10 0 0, 0 0 0))", val);
    }

    @Test
    public void testAggUnion() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                "?g a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:aggUnion(?xWkt) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("GEOMETRYCOLLECTION (POINT (-0.0754 51.5055), POINT (2.2945 48.8584), " +
                "POLYGON ((0 5, 0 10, 5 10, 5 15, 10 15, 15 15, 15 10, 15 5, 10 5, 10 0, 5 0, 0 0, 0 5)))", val);
    }

    @Ignore("RDF4J does not allow custom aggregate functions to have arity > 1")
    @Test
    public void testAggUnionMultipleArg() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                "?g a :Geom; geo:asWKT ?xWkt.\n" +
                "?gz a :GeomZ; geo:asWKT ?yWkt.\n" +
                "BIND (geof:aggUnion(?xWkt, ?yWkt) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("GEOMETRYCOLLECTION (POINT (-0.0754 51.5055), POINT (2.2945 48.8584), " +
                "POLYGON ((0 5, 0 10, 5 10, 5 15, 10 15, 15 15, 15 10, 15 5, 10 5, 10 0, 5 0, 0 0, 0 5)))", val);
    }

    @Ignore("Not supported by H2GIS with given input data. H2GIS expects all geometries in a UNION operation to have " +
            "the same dimensionality (2D or 3D). Not an issue for PostGIS where if you mix 2D (POLYGON) and " +
            "3D (POLYGON Z) geometries in an operation, PostGIS treats them both as 2D. Test would work with just 2D data.")
    @Test
    public void testAggUnionWithGroupBy() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "SELECT (geof:aggUnion(?xWkt) AS ?v) ?label WHERE {\n" +
                "?g a :Geom; geo:asWKT ?xWkt.\n" +
                "?g rdfs:label ?label .\n" +
                "}\n" +
                "GROUP BY ?label\n" +
                "ORDER BY ?label\n" +
                "LIMIT 1";
        String val = runQueryAndReturnString(query);
        assertEquals("\"POINT(2.2945 48.8584)\"^^geo:wktLiteral", val);
        /* All records: "\"POINT(2.2945 48.8584)\"^^geo:wktLiteral",
                "\"LINESTRING(0 0,5 5,10 10)\"^^geo:wktLiteral",
                "\"MULTIPOLYGON(((0 5,5 5,5 0,0 0,0 5)),((10 15,15 15,15 10,10 10,10 15)))\"^^geo:wktLiteral",
                "\"POINT(2 2)\"^^geo:wktLiteral",
                "\"POLYGON((0 0,10 0,10 10,0 10,0 0))\"^^geo:wktLiteral",
                "\"POLYGON((5 5,15 5,15 15,5 15,5 5))\"^^geo:wktLiteral",
                "\"POINT(-0.0754 51.5055)\"^^geo:wktLiteral" */
    }

    @Ignore("Not supported yet")
    @Test
    public void testAggUnionWithValues() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                    "VALUES (?geom) {\n" +
                        "(\"POINT(1 1)\"^^geo:wktLiteral)\n" +
                        "(\"POINT(2 2)\"^^geo:wktLiteral)\n" +
                        "(\"POINT(3 3)\"^^geo:wktLiteral)\n" +
                    "}\n" +
                "BIND(geof:aggUnion(?geom) AS ?x)\n" +
                "}\n";
        String val = runQueryAndReturnString(query);
        assertEquals("MULTIPOINT((1 1), (2 2), (3 3))", val);
    }

    private boolean runQueryAndReturnBooleanX(String query) throws Exception {
        try (OWLStatement st = conn.createStatement()) {
            BooleanOWLResultSet rs = st.executeAskQuery(query);
            return rs.getValue();
        }
    }

    private double runQueryAndReturnDoubleX(String query) throws Exception {
        try (OWLStatement st = conn.createStatement()) {
            TupleOWLResultSet rs = st.executeSelectQuery(query);
            assertTrue(rs.hasNext());
            final OWLBindingSet bindingSet = rs.next();
            OWLLiteral ind1 = bindingSet.getOWLLiteral("x");
            return ind1.parseDouble();
        }
    }

    private String runQueryAndReturnString(String query) throws Exception {
        try (OWLStatement st = conn.createStatement()) {
            TupleOWLResultSet rs = st.executeSelectQuery(query);
            assertTrue(rs.hasNext());
            final OWLBindingSet bindingSet = rs.next();
            OWLLiteral ind1 = bindingSet.getOWLLiteral("x");
            return ind1.getLiteral();
        }
    }

    private String runQueryAndReturnIRI(String query) throws Exception {
        try (OWLStatement st = conn.createStatement()) {
            TupleOWLResultSet rs = st.executeSelectQuery(query);
            assertTrue(rs.hasNext());
            final OWLBindingSet bindingSet = rs.next();
            OWLIndividual ind1 = bindingSet.getOWLIndividual("x");
            return ind1.toString();
        }
    }

}
