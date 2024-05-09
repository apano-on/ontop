package it.unibz.inf.ontop.owlapi;

import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.owlapi.connection.OWLConnection;
import it.unibz.inf.ontop.owlapi.connection.OWLStatement;
import it.unibz.inf.ontop.owlapi.impl.SimpleOntopOWLEngine;
import it.unibz.inf.ontop.owlapi.resultset.BooleanOWLResultSet;
import it.unibz.inf.ontop.owlapi.resultset.OWLBindingSet;
import it.unibz.inf.ontop.owlapi.resultset.TupleOWLResultSet;
import org.h2gis.functions.factory.H2GISFunctions;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLLiteral;

import java.sql.Connection;
import java.sql.DriverManager;

import static it.unibz.inf.ontop.utils.OWLAPITestingTools.executeFromFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeoSPARQLv1_1Test {
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
    public void testSelectPerimeter() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":1 a :Geom; geo:asWKT ?xWkt.\n" +
                "BIND (geof:perimeter(?xWkt) AS ?x)\n" +
                "}\n";
        double val = runQueryAndReturnDoubleX(query);
        assertEquals(40.0, val, 1);
    }

    @Ignore("TODO: Implement UNION with arbitrary arity")
    @Test
    public void testSelectAggBoundingBox() throws Exception {
        String query = "PREFIX : <http://ex.org/> \n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "SELECT ?x WHERE {\n" +
                ":6 a :Geom; geo:asWKT ?xWkt.\n" +
                ":7 a :Geom; geo:asWKT ?yWkt.\n" +
                "BIND (geof:aggBoundingBox(?xWkt, ?yWkt) AS ?x)\n" +
                "}\n";
        double val = runQueryAndReturnDoubleX(query);
        assertEquals(26.0, val, 1);
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

}
