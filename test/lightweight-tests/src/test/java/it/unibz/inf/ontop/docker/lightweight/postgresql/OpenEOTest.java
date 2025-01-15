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

    @Test
    public void getOpenEONDVIAvg() {

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
                + "BIND (\"SENTINEL2_L2A\" AS ?satellite) .\n"
                + "BIND (\"[\"B04\",\"B08\"]\" AS ?band) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band) AS ?coll1) .\n"
                + "BIND (openeo:band_math(?coll1, ?band, \"x2 - x1 / x2 + x1\") AS ?math_op) .\n"
                + "BIND (openeo:reduce_dimension(?coll1, \"bands\", ?math_op) AS ?coll2) .\n"
                + "BIND (openeo:reduce_dimension(?coll1, \"t\", \"max\") AS ?coll2) .\n"
                //+ "BIND (?kelvin_temp - 273.15  AS ?celsius_temp\n)"
                //+ "BIND(ROUND(?celsius_temp * 1000) / 1000 AS ?v)"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of("\"[[277.5884282038762]]\"^^xsd:string", "\"[[278.78968620300293]]\"^^xsd:string"));
    }

    @Test
    public void getOpenEOTempMasking() {

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
                + "BIND (\"SENTINEL3_SLSTR_L2_LST\" AS ?satellite) .\n"
                + "BIND (\"LST\"]\" AS ?band) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band) AS ?coll_temp) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, \"confidence_in\") AS ?coll_mask) .\n"
                + "BIND (openeo:filter(?coll_mask >= 16384) AS ?coll_mask) .\n"
                + "BIND (openeo:band_math(?coll1, ?band, \"x2 - x1 / x2 + x1\") AS ?math_op) .\n"
                + "BIND (openeo:reduce_dimension(?coll1, \"bands\", ?math_op) AS ?coll2) .\n"
                + "BIND (openeo:reduce_dimension(?coll1, \"t\", \"max\") AS ?coll2) .\n"
                //+ "BIND (?kelvin_temp - 273.15  AS ?celsius_temp\n)"
                //+ "BIND(ROUND(?celsius_temp * 1000) / 1000 AS ?v)"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of("\"[[277.5884282038762]]\"^^xsd:string", "\"[[278.78968620300293]]\"^^xsd:string"));
    }

    @Test
    public void getOpenEOHeatwave() {

        String query = "PREFIX :\t<http://www.unibz-openeo.org#>\n"
                + "PREFIX rdfs:\t<http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX geo:\t<http://www.opengis.net/ont/geosparql#>\n"
                + "PREFIX openeo:\t<http://www.openeo-ontop.org#>\n"
                + "SELECT ?v {\n"
                + "?g geo:asWKT ?xWkt .\n"
                + "?g rdfs:label ?name .\n"
                + "FILTER(LANG(?name) = \"it\" && (STR(?name) = \"Bressanone\" || STR(?name) = \"Merano\")) .\n"
                + "BIND (\"2023-07-01T00:00:00Z\"^^xsd:dateTime AS ?start_time) .\n"
                + "BIND (\"2023-07-07T00:00:00Z\"^^xsd:dateTime AS ?end_time) .\n"
                + "BIND (\"SENTINEL3_SLSTR_L2_LST\" AS ?satellite) .\n"
                + "BIND (\"LST\" AS ?band1) .\n"
                + "BIND (\"confidence_in\" AS ?band2) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band1) AS ?coll1) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band2) AS ?coll2) .\n"
                + "BIND (openeo:apply(?coll2 >= 16384) AS ?coll3) .\n"
                + "BIND (openeo:mask(?coll1, ?coll3) AS ?coll4) .\n"
                + "BIND (\"import xarray\\\\nimport numpy as np\\\\nfrom openeo.udf import inspect\\\\n\\\\ndef apply_datacube(cube: xarray.DataArray, context: dict) -> xarray.DataArray:\\\\n    \\\\n    array = cube.values\\\\n    inspect(data=[array.shape], message = \\\\\\\"Array dimensions\\\\\\\")\\\\n    res_arr=np.zeros(array.shape)\\\\n    for i in range(array.shape[0]-4):\\\\n        ar_sub=np.take(array,  range(i, i+5), axis=0)\\\\n        res_arr[i]=(np.all(ar_sub>295,axis=0)) & (np.nansum(ar_sub>300,axis=0)>2)\\\\n    return xarray.DataArray(res_arr, dims=cube.dims, coords=cube.coords)\" AS ?udf) .\n"
                + "BIND (openeo:apply_dimension(?coll4, \"t\", ?udf) AS ?coll5) .\n"
                + "BIND (openeo:reduce_dimension(?coll5, \"t\", \"sum\") AS ?v) .\n"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of("\"[[277.5884282038762]]\"^^xsd:string", "\"[[278.78968620300293]]\"^^xsd:string"));
    }

    @Test
    public void getOpenEOWildfire() {

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
                + "BIND (\"SENTINEL2_L2A\" AS ?satellite) .\n"
                + "BIND (\"[\"B04\",\"B08\",\"B12\"]\" AS ?band1) .\n"
                + "BIND (\"SCL\" AS ?band2) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band1, \"cloud_cover=90\") AS ?coll1) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band2, \"cloud_cover=90\") AS ?coll2) .\n"
                + "BIND (openeo:ndvi(?coll1) AS ?coll3) .\n"
                + "BIND (openeo:oneof(?coll2, \"3,8,9,10\") AS ?coll4) .\n"
                + "BIND (openeo:apply_kernel(?coll4, ?kernel) AS ?coll5) .\n"
                + "BIND (openeo:apply(?coll5 >= 98) AS ?coll6) .\n"
                + "BIND (openeo:mask(?coll3, ?coll6) AS ?coll7) .\n"
                + "BIND (openeo:reduce_dimension(?coll7, \"t\", \"first\") AS ?v) .\n"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of("\"[[277.5884282038762]]\"^^xsd:string", "\"[[278.78968620300293]]\"^^xsd:string"));
    }

}
