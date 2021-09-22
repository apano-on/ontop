package it.unibz.inf.ontop.docker.spark;

import it.unibz.inf.ontop.docker.AbstractVirtualModeTest;
import it.unibz.inf.ontop.owlapi.OntopOWLReasoner;
import it.unibz.inf.ontop.owlapi.connection.OntopOWLConnection;
import it.unibz.inf.ontop.owlapi.connection.OntopOWLStatement;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.util.ArrayList;
import java.util.List;

public class SparkSedonaDatatypesTest extends AbstractVirtualModeTest {

    static final String owlfile = "/spark/datatypes.owl";
    static final String obdafile = "/spark/datatypes.obda";
    static final String propertiesfile = "/spark/datatypes.properties";
    //static final String viewsfile = "/spark/datatypes.json";
    static final String viewsfile = "/home/albulen/IdeaProjects/ontop/test/docker-tests/src/test/resources/spark/datatypes.json";

    private static OntopOWLReasoner REASONER;
    private static OntopOWLConnection CONNECTION;

    @BeforeClass
    public static void before() throws OWLOntologyCreationException {
        REASONER = createReasoner(owlfile, obdafile, propertiesfile, null, viewsfile);
        CONNECTION = REASONER.getConnection();
    }

    @Override
    protected OntopOWLStatement createStatement() throws OWLException {
        return CONNECTION.createStatement();
    }

    @AfterClass
    public static void after() throws OWLException {
        CONNECTION.close();
        REASONER.dispose();
    }

    /**
     * Test use of quoted mixed case table name
     * @throws Exception
     */
	/*@Test
	public void testLowercaseUnquoted() throws Exception {
		String query = "PREFIX : <http://www.semanticweb.org/ontologies/2013/7/untitled-ontology-150#> SELECT ?x WHERE {?x a :Country} ORDER BY ?x";
		String val = runQueryAndReturnStringOfIndividualX(query);
		assertEquals("<http://www.semanticweb.org/ontologies/2013/7/untitled-ontology-150#Country-a>", val);
	}*/

    @Test
    public void testUppercaseAlias() throws Exception {
        String query = "PREFIX uom: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
                "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "SELECT ?sub ?geom ?dist WHERE {\n" +
                "  ?sub geo:asWKT ?geom .\n" +
                "  #BIND(geof:distance('POINT(1 100)'^^geo:wktLiteral, ?geom, uom:degree) AS ?dist) .\n" +
                "  FILTER(geof:sfIntersects(?geom, 'POLYGON((0 100, 2 100, 2 102, 0 102, 0 100))'^^geo:wktLiteral))\n" +
                "} ";
        String val = runQueryAndReturnStringOfIndividualX(query);

        List<String> expectedValues = new ArrayList<>();
        expectedValues.add("\"false\"^^xsd:boolean");
        expectedValues.add("\"false\"^^xsd:boolean");
        expectedValues.add("\"true\"^^xsd:boolean");
        expectedValues.add("\"true\"^^xsd:boolean");

        checkReturnedValuesUnordered(expectedValues, val);
        //assertEquals("<http://www.semanticweb.org/ontologies/2013/7/untitled-ontology-150#Country4-a>", val);
    }

}
