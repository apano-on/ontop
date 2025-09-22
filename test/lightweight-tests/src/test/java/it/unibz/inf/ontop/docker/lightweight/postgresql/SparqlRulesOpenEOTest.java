package it.unibz.inf.ontop.docker.lightweight.postgresql;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.docker.lightweight.AbstractDockerRDF4JTest;
import it.unibz.inf.ontop.query.translation.shacl.ShaclAfRegistryHolder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.io.IOException;

public class SparqlRulesOpenEOTest extends AbstractDockerRDF4JTest {
    private static final String PROPERTIES_FILE = "/openeo/openeo.properties";
    private static final String OBDA_FILE = "/openeo/openeo.obda";
    private static final String OWL_FILE = "/openeo/openeo.owl";
    private static final String SPARQL_RULES = "/openeo/openeo-rules-function.toml";

    @BeforeAll
    public static void before() throws IOException, SQLException {
        ShaclAfRegistryHolder.initFromTomlClasspath(SPARQL_RULES);
        initOBDA(OBDA_FILE, OWL_FILE, PROPERTIES_FILE, null, null, SPARQL_RULES);
    }

    @AfterAll
    public static void after() throws SQLException {
        release();
    }

    @Test
    public void getOpenEOProcessGraphAvgConstantGeom() {

        String query = "PREFIX :\t<http://www.unibz-openeo.org#>\n"
                + "PREFIX rdfs:\t<http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX geo:\t<http://www.opengis.net/ont/geosparql#>\n"
                + "PREFIX openeo:\t<http://www.openeo-ontop.org#>\n"
                + "SELECT ?v {\n"
                + "BIND(\"POLYGON((11.25 46.4, 11.75 46.4, 11.75 46.7, 11.25 46.7, 11.25 46.4))\"^^geo:wktLiteral AS ?xWkt) .\n"
                + "BIND (\"2023-09-01T00:00:00Z\"^^xsd:dateTime AS ?start_time) .\n"
                + "BIND (\"2023-09-07T00:00:00Z\"^^xsd:dateTime AS ?end_time) .\n"
                + "BIND (\"SENTINEL3_SLSTR\" AS ?satellite) .\n"
                + "BIND (\"S8\" AS ?band) .\n"
                /*+ "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band) AS ?coll1) .\n"
                + "BIND (openeo:reduce_dimension(?coll1, \"t\", \"mean\") AS ?coll2) .\n"
                + "BIND (openeo:aggregate_spatial(?coll2, ?xWkt, \"mean\") AS ?v) .\n"*/
                + "BIND (openeo:meanTemperature(?satellite, ?xWkt, ?start_time, ?end_time, ?band) AS ?v) .\n"
                + "}\n";



        executeAndCompareValues(query, ImmutableList.of("\"277.8469262491862\"^^xsd:string"));
    }

}