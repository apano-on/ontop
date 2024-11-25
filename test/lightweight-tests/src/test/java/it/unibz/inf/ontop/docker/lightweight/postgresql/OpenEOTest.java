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
                + "BIND (<http://www.opengis.net/def/crs/EPSG/0/4326> AS ?crs) .\n"
                + "BIND (openeo:avg(?start_date, ?end_date, ?xWkt, ?satellite, ?band, ?crs) AS ?kelvin_temp) .\n"
                + "BIND (?kelvin_temp - 273.15  AS ?celsius_temp\n)"
                + "BIND(ROUND(?celsius_temp * 1000) / 1000 AS ?v)"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of("\"4.438\"^^xsd:double", "\"5.64\"^^xsd:double"));
    }

    @Test
    public void getOpenEOMin() {

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
                + "BIND (<http://www.opengis.net/def/crs/EPSG/0/4326> AS ?crs) .\n"
                + "BIND (openeo:min(?start_date, ?end_date, ?xWkt, ?satellite, ?band, ?crs) AS ?kelvin_temp) .\n"
                + "BIND (?kelvin_temp - 273.15  AS ?celsius_temp\n)"
                + "BIND(ROUND(?celsius_temp * 1000) / 1000 AS ?v)"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of("\"-22.13\"^^xsd:double", "\"-34\"^^xsd:double"));
    }

    @Test
    public void getOpenEOMax() {

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
                + "BIND (<http://www.opengis.net/def/crs/EPSG/0/4326> AS ?crs) .\n"
                + "BIND (openeo:max(?start_date, ?end_date, ?xWkt, ?satellite, ?band, ?crs) AS ?kelvin_temp) .\n"
                + "BIND (?kelvin_temp - 273.15  AS ?celsius_temp\n)"
                + "BIND(ROUND(?celsius_temp * 1000) / 1000 AS ?v)"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of("\"15.94\"^^xsd:double", "\"18.02\"^^xsd:double"));
    }

    @Test
    public void getOpenEOProcessGraphAvg() {

        String query = "PREFIX :\t<http://www.unibz-openeo.org#>\n"
                + "PREFIX rdfs:\t<http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX geo:\t<http://www.opengis.net/ont/geosparql#>\n"
                + "PREFIX openeo:\t<http://www.openeo-ontop.org#>\n"
                + "SELECT ?v {\n"
                + "?g geo:asWKT ?xWkt .\n"
                + "?g rdfs:label ?name .\n"
                + "FILTER(LANG(?name) = \"it\" && (STR(?name) = \"Bressanone\" || STR(?name) = \"Merano\")) .\n"
                + "BIND (\"2023-09-01T00:00:00Z\"^^xsd:dateTime AS ?start_time) .\n"
                + "BIND (\"2023-09-07T00:00:00Z\"^^xsd:dateTime AS ?end_time) .\n"
                + "BIND (\"SENTINEL3_SLSTR\" AS ?satellite) .\n"
                + "BIND (\"S8\" AS ?band) .\n"
                //+ "BIND (<http://www.opengis.net/def/crs/EPSG/0/4326> AS ?crs) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band) AS ?coll1) .\n"
                + "BIND (openeo:reduce_dimension(?coll1, \"t\", \"mean\") AS ?coll2) .\n"
                + "BIND (openeo:aggregate_spatial(?coll2, ?xWkt, \"mean\") AS ?v) .\n"
                //+ "BIND (?kelvin_temp - 273.15  AS ?celsius_temp\n)"
                //+ "BIND(ROUND(?celsius_temp * 1000) / 1000 AS ?v)"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of("\"[[277.5884282038762]]\"^^xsd:string", "\"[[278.78968620300293]]\"^^xsd:string"));
    }

}
