package it.unibz.inf.ontop.docker.lightweight.postgresql;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.docker.lightweight.AbstractDockerRDF4JTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.io.IOException;

public class OpenEOTest extends AbstractDockerRDF4JTest {
    private static final String PROPERTIES_FILE = "/openeo/openeo.properties";
    private static final String OBDA_FILE = "/openeo/openeo.obda";
    private static final String OWL_FILE = "/openeo/openeo.owl";

    @BeforeAll
    public static void before() throws IOException, SQLException {
        initOBDA(OBDA_FILE, OWL_FILE, PROPERTIES_FILE);
    }

    @AfterAll
    public static void after() throws SQLException {
        release();
    }

    @Test
    public void getOpenEOAvg() {

        String query = "PREFIX :\t<http://www.unibz-openeo.org#>\n"
                + "PREFIX rdfs:\t<http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX geo:\t<http://www.opengis.net/ont/geosparql#>\n"
                + "PREFIX openeo:\t<http://www.openeo-ontop.org#>\n"
                + "SELECT ?v {\n"
                + "?g geo:asWKT ?xWkt .\n"
                + "?g rdfs:label ?name .\n"
                + "FILTER(LANG(?name) = \"it\" && (STR(?name) = \"Bressanone\" || STR(?name) = \"Merano\")) .\n"
                + "BIND (\"2023-09-01\"^^xsd:date AS ?start_date) .\n"
                + "BIND (\"2023-09-07\"^^xsd:date AS ?end_date) .\n"
                + "BIND (\"SENTINEL3_SLSTR\" AS ?satellite) .\n"
                + "BIND (\"S8\" AS ?band) .\n"
                + "BIND (\"EPSG:4326\" AS ?crs) .\n"
                + "BIND (openeo:avg(?start_date, ?end_date, ?xWkt, ?satellite, ?band, ?crs) AS ?kelvin_temp) .\n"
                + "BIND (?kelvin_temp - 273.15  AS ?celsius_temp\n)"
                + "BIND(ROUND(?celsius_temp * 1000) / 1000 AS ?v)"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of("\"4.438\"^^xsd:double", "\"5.64\"^^xsd:double"));
    }

}
