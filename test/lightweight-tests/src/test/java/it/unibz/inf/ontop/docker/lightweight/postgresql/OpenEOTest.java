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
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of("\"277.5884282038762\"^^xsd:string", "\"278.78968620300293\"^^xsd:string"));
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
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band) AS ?coll1) .\n"
                + "BIND (openeo:reduce_dimension(?coll1, \"t\", \"mean\") AS ?coll2) .\n"
                + "BIND (openeo:aggregate_spatial(?coll2, ?xWkt, \"mean\") AS ?v) .\n"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of("\"277.8469262491862\"^^xsd:string"));
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
                + "BIND (\"[B04, B08]\" AS ?band1) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band1) AS ?coll1) .\n"
                + "BIND (openeo:band_math(?coll1, \"(x2 - x1) / (x2 + x1)\") AS ?coll2) .\n"
                + "BIND (openeo:reduce_dimension(?coll2, \"t\", \"max\") AS ?v) .\n"
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

        executeAndCompareValues(query, ImmutableList.of("\"[[[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]]]\"^^xsd:string ",
                "\"[[[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]]]\"^^xsd:string]"));
    }

    // This test is expected to fail because the area is too big
    @Test
    public void getOpenEOHeatwaveConstantGeom() {

        String query = "PREFIX :\t<http://www.unibz-openeo.org#>\n"
                + "PREFIX rdfs:\t<http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX geo:\t<http://www.opengis.net/ont/geosparql#>\n"
                + "PREFIX openeo:\t<http://www.openeo-ontop.org#>\n"
                + "SELECT ?v {\n"
                + "BIND(\"POLYGON((11.25 46.4, 11.75 46.4, 11.75 46.7, 11.25 46.7, 11.25 46.4))\"^^geo:wktLiteral AS ?xWkt) .\n"
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

        executeAndCompareValues(query, ImmutableList.of("\"[[[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], " +
                "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]]]\"^^xsd:string"));
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
                + "FILTER(LANG(?name) = \"it\" && (STR(?name) = \"Bressanone\")) .\n"
                + "BIND (\"2023-09-01T00:00:00Z\"^^xsd:dateTime AS ?start_time) .\n"
                + "BIND (\"2023-09-07T00:00:00Z\"^^xsd:dateTime AS ?end_time) .\n"
                + "BIND (\"SENTINEL2_L2A\" AS ?satellite) .\n"
                + "BIND (\"[B04, B08, B12]\" AS ?band1) .\n"
                + "BIND (\"SCL\" AS ?band2) .\n"
                + "BIND (\"11\"^^xsd:integer AS ?dimension) .\n"
                + "BIND (\"1.6\"^^xsd:double AS ?stdev) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band1, \"eo:cloud_cover<=90\") AS ?coll1) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band2, \"eo:cloud_cover<=90\") AS ?coll2) .\n"
                + "BIND (openeo:ndvi(?coll1) AS ?coll3) .\n"
                + "BIND (openeo:oneof(?coll2, \"3\"^^xsd:integer || \"8\"^^xsd:integer || \"9\"^^xsd:integer || \"10\"^^xsd:integer) AS ?coll4) .\n"
                + "BIND (openeo:apply_kernel(?coll4, ?dimension, ?stdev) AS ?coll5) .\n"
                + "BIND (openeo:apply(?coll5 >= 98) AS ?coll6) .\n"
                + "BIND (openeo:mask(?coll3, ?coll6) AS ?coll7) .\n"
                + "BIND (openeo:reduce_dimension(?coll7, \"t\", \"first\") AS ?v) .\n"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of("\"[[[0.2612859010696411, 0.2491968721151352, 0.23822587728500366, " +
                "0.23783287405967712, 0.23822587728500366, 0.24353741109371185, 0.25491949915885925, 0.2527666985988617, " +
                "0.2537313401699066, 0.25608137249946594, 0.24911032617092133, 0.2379232496023178, 0.24028906226158142, " +
                "0.23310962319374084, 0.23132313787937164, 0.23310962319374084, 0.22759856283664703, 0.23941150307655334, " +
                "0.23550401628017426, 0.23468931019306183, 0.24571429193019867, 0.25417765974998474, 0.2466367781162262, " +
                "0.24693042039871216, 0.2455742210149765, 0.2412220686674118, 0.24351388216018677, 0.24932248890399933, " +
                "0.23924730718135834, 0.24321921169757843, 0.24057450890541077, 0.23328785598278046, 0.2302781641483307, " +
                "0.2221709042787552, 0.22160664200782776, 0.2237318903207779, 0.21545454859733582, 0.21349312365055084, " +
                "0.2048611044883728, 0.2126929610967636, 0.21336761116981506, 0.21739129722118378, 0.21<...>300211548805237, " +
                "0.4728434383869171, 0.46528157591819763, 0.4622487723827362, 0.46267029643058777, 0.452314555644989, " +
                "0.4614964425563812, 0.4610712230205536, 0.4520089328289032, 0.44935646653175354, 0.4474576413631439, " +
                "0.4590846002101898, 0.4586799740791321, 0.46132442355155945, 0.4563758373260498, 0.4493742883205414, " +
                "0.48498332500457764, 0.4879120886325836, 0.4866369664669037, 0.48638787865638733, 0.5317875742912292, " +
                "0.6010143756866455, 0.6245027780532837, 0.6291079521179199, 0.6248037815093994, 0.6051467657089233, " +
                "0.6054860949516296, 0.6045845150947571, 0.6098867058753967, 0.6010016798973083, 0.6000000238418579, " +
                "0.610668957233429, 0.6191085577011108, 0.6098418235778809, 0.5400733351707458, 0.5270344018936157, " +
                "0.5110861659049988, 0.5020219683647156, 0.4958677589893341, 0.5074922442436218, 0.5051078200340271, " +
                "0.5139535069465637, 0.4825260639190674, 0.4871317744255066, 0.47667086124420166, 0.48672565817832947, " +
                "0.49754300713539124, 0.4847819209098816, 0.4654255211353302]]]\"^^xsd:string"));
    }

    @Test
    public void getOpenEOWildfireGeomConstant() {

        String query = "PREFIX :\t<http://www.unibz-openeo.org#>\n"
                + "PREFIX rdfs:\t<http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX geo:\t<http://www.opengis.net/ont/geosparql#>\n"
                + "PREFIX openeo:\t<http://www.openeo-ontop.org#>\n"
                + "SELECT ?v {\n"
                + "BIND(\"POLYGON((11.25 46.4, 11.75 46.4, 11.75 46.7, 11.25 46.7, 11.25 46.4))\"^^geo:wktLiteral AS ?xWkt) .\n"
                + "BIND (\"2023-09-01T00:00:00Z\"^^xsd:dateTime AS ?start_time) .\n"
                + "BIND (\"2023-09-07T00:00:00Z\"^^xsd:dateTime AS ?end_time) .\n"
                + "BIND (\"SENTINEL2_L2A\" AS ?satellite) .\n"
                + "BIND (\"[B04, B08, B12]\" AS ?band1) .\n"
                + "BIND (\"SCL\" AS ?band2) .\n"
                + "BIND (\"11\"^^xsd:integer AS ?dimension) .\n"
                + "BIND (\"1.6\"^^xsd:double AS ?stdev) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band1, \"eo:cloud_cover<=90\") AS ?coll1) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band2, \"eo:cloud_cover<=90\") AS ?coll2) .\n"
                + "BIND (openeo:ndvi(?coll1) AS ?coll3) .\n"
                + "BIND (openeo:oneof(?coll2, \"3\"^^xsd:integer || \"8\"^^xsd:integer || \"9\"^^xsd:integer || \"10\"^^xsd:integer) AS ?coll4) .\n"
                + "BIND (openeo:apply_kernel(?coll4, ?dimension, ?stdev) AS ?coll5) .\n"
                + "BIND (openeo:apply(?coll5 >= 98) AS ?coll6) .\n"
                + "BIND (openeo:mask(?coll3, ?coll6) AS ?coll7) .\n"
                + "BIND (openeo:reduce_dimension(?coll7, \"t\", \"first\") AS ?v) .\n"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of("\"[[[0.2612859010696411, 0.2491968721151352, 0.23822587728500366, " +
                "0.23783287405967712, 0.23822587728500366, 0.24353741109371185, 0.25491949915885925, 0.2527666985988617, " +
                "0.2537313401699066, 0.25608137249946594, 0.24911032617092133, 0.2379232496023178, 0.24028906226158142, " +
                "0.23310962319374084, 0.23132313787937164, 0.23310962319374084, 0.22759856283664703, 0.23941150307655334, " +
                "0.23550401628017426, 0.23468931019306183, 0.24571429193019867, 0.25417765974998474, 0.2466367781162262, " +
                "0.24693042039871216, 0.2455742210149765, 0.2412220686674118, 0.24351388216018677, 0.24932248890399933, " +
                "0.23924730718135834, 0.24321921169757843, 0.24057450890541077, 0.23328785598278046, 0.2302781641483307, " +
                "0.2221709042787552, 0.22160664200782776, 0.2237318903207779, 0.21545454859733582, 0.21349312365055084, " +
                "0.2048611044883728, 0.2126929610967636, 0.21336761116981506, 0.21739129722118378, 0.21<...>300211548805237, " +
                "0.4728434383869171, 0.46528157591819763, 0.4622487723827362, 0.46267029643058777, 0.452314555644989, " +
                "0.4614964425563812, 0.4610712230205536, 0.4520089328289032, 0.44935646653175354, 0.4474576413631439, " +
                "0.4590846002101898, 0.4586799740791321, 0.46132442355155945, 0.4563758373260498, 0.4493742883205414, " +
                "0.48498332500457764, 0.4879120886325836, 0.4866369664669037, 0.48638787865638733, 0.5317875742912292, " +
                "0.6010143756866455, 0.6245027780532837, 0.6291079521179199, 0.6248037815093994, 0.6051467657089233, " +
                "0.6054860949516296, 0.6045845150947571, 0.6098867058753967, 0.6010016798973083, 0.6000000238418579, " +
                "0.610668957233429, 0.6191085577011108, 0.6098418235778809, 0.5400733351707458, 0.5270344018936157, " +
                "0.5110861659049988, 0.5020219683647156, 0.4958677589893341, 0.5074922442436218, 0.5051078200340271, " +
                "0.5139535069465637, 0.4825260639190674, 0.4871317744255066, 0.47667086124420166, 0.48672565817832947, " +
                "0.49754300713539124, 0.4847819209098816, 0.4654255211353302]]]\"^^xsd:string"));
    }

    @Test
    public void getOpenEOLandSlidesNDVI() {

        String query = "PREFIX :\t<http://www.unibz-openeo.org#>\n"
                + "PREFIX rdfs:\t<http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX geo:\t<http://www.opengis.net/ont/geosparql#>\n"
                + "PREFIX openeo:\t<http://www.openeo-ontop.org#>\n"
                + "SELECT ?v {\n"
                + "BIND(\"POLYGON((11.25 46.4, 11.75 46.4, 11.75 46.7, 11.25 46.7, 11.25 46.4))\"^^geo:wktLiteral AS ?xWkt) .\n"
                + "BIND (\"2023-09-01T00:00:00Z\"^^xsd:dateTime AS ?start_time) .\n"
                + "BIND (\"2023-09-07T00:00:00Z\"^^xsd:dateTime AS ?end_time) .\n"
                + "BIND (\"2023-09-08T00:00:00Z\"^^xsd:dateTime AS ?start_time2) .\n"
                + "BIND (\"2023-09-15T00:00:00Z\"^^xsd:dateTime AS ?end_time2) .\n"
                + "BIND (\"SENTINEL2_L2A\" AS ?satellite) .\n"
                + "BIND (\"[B04, B08]\" AS ?band1) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band1) AS ?coll1) .\n"
                + "BIND (openeo:ndvi(?coll1) AS ?coll2) .\n"
                + "BIND (openeo:reduce_dimension(?coll2, \"t\", \"mean\") AS ?coll3) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time2, ?end_time2, ?band1) AS ?coll4) .\n"
                + "BIND (openeo:ndvi(?coll4) AS ?coll5) .\n"
                + "BIND (openeo:reduce_dimension(?coll5, \"t\", \"mean\") AS ?coll6) .\n"
                + "BIND (openeo:merge_cubes(?coll6, ?coll3, '{\"overlap_resolver\": \"subtract\"}') AS ?v) .\n"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of());
    }

    @Test
    public void getOpenEOOilSpills() {

        String query = "PREFIX :\t<http://www.unibz-openeo.org#>\n"
                + "PREFIX rdfs:\t<http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX geo:\t<http://www.opengis.net/ont/geosparql#>\n"
                + "PREFIX openeo:\t<http://www.openeo-ontop.org#>\n"
                + "SELECT ?v {\n"
                + "BIND(\"POLYGON((11.25 46.4, 11.75 46.4, 11.75 46.7, 11.25 46.7, 11.25 46.4))\"^^geo:wktLiteral AS ?xWkt) .\n"
                + "BIND (\"2023-09-01T00:00:00Z\"^^xsd:dateTime AS ?start_time) .\n"
                + "BIND (\"2023-09-07T00:00:00Z\"^^xsd:dateTime AS ?end_time) .\n"
                + "BIND (\"SENTINEL1_GRD\" AS ?satellite) .\n"
                + "BIND (\"VV\" AS ?band1) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band1) AS ?coll1) .\n"
                + "BIND (openeo:sar_backscatter(?coll1, {coefficient: \"sigma0-ellipsoid\"}) AS ?coll2) .\n"
                + "BIND (openeo:apply(10 * log(?coll2, 10)) AS ?coll3) .\n"
                + "BIND (openeo:apply_kernel(?coll3, {kernel: filter_window, factor: factor}) AS ?coll4) .\n"
                + "BIND (openeo:rename_labels(?coll3, \"bands\", \"amplitude\") AS ?coll5) .\n"
                + "BIND (openeo:apply(?coll5 - 3.5) AS ?coll6) .\n"
                + "BIND (openeo:rename_labels(?coll6, \"bands\", \"threshold\") AS ?coll7) .\n"
                + "BIND (openeo:merge_cubes(?coll4, ?coll7, '{\"overlap_resolver\": null}') AS ?coll8) .\n"
                + "BIND (openeo:reduce_dimension(?coll8, \"bands\", \"amplitude\" < \"threshold\") AS ?v) .\n"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of());
    }

    @Test
    public void getOpenEORankComposites() {

        String query = "PREFIX :\t<http://www.unibz-openeo.org#>\n"
                + "PREFIX rdfs:\t<http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX geo:\t<http://www.opengis.net/ont/geosparql#>\n"
                + "PREFIX openeo:\t<http://www.openeo-ontop.org#>\n"
                + "SELECT ?v {\n"
                + "BIND(\"POLYGON((11.25 46.4, 11.75 46.4, 11.75 46.7, 11.25 46.7, 11.25 46.4))\"^^geo:wktLiteral AS ?xWkt) .\n"
                + "BIND (\"2023-09-01T00:00:00Z\"^^xsd:dateTime AS ?start_time) .\n"
                + "BIND (\"2023-09-07T00:00:00Z\"^^xsd:dateTime AS ?end_time) .\n"
                + "BIND (\"SENTINEL2_L2A\" AS ?satellite) .\n"
                + "BIND (\"[B04, B08, SCL]\" AS ?band1) .\n"
                + "BIND (\"SCL\" AS ?band2) .\n"
                + "BIND (\"[B02, B03, B04]\" AS ?band3) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band1, \"eo:cloud_cover<=95\") AS ?coll1) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band2, \"eo:cloud_cover<=95\") AS ?coll2) .\n"
                + "BIND (openeo:to_scl_distillation_mask(?coll2, {erosion_kernel_size: 3, kernel1_size: 17, kernel2_size: 77, mask1_values: [2, 4, 5, 6, 7], mask2_values: [3, 8, 9, 10, 11]}) AS ?coll3) .\n"
                + "BIND (openeo:mask(?coll1, ?coll3) AS ?coll4) .\n"
                + "BIND (openeo:ndvi(?coll4) AS ?coll5) .\n"
                //set dimension to null, get max over all dimensions?? double check!!
                + "BIND (openeo:reduce_dimension(?coll5, \"null\", \"max\") AS ?max_ndvi) .\n"
                //equality returns true or false, then int to 1 or 0
                //could just true or false be enough?
                + "BIND (openeo:apply(int(?coll5 = ?max_ndvi)) AS ?coll6) .\n"
                + "BIND (openeo:apply_neighborhood(?coll5, ?coll6, size=[{'dimension': 'x', 'unit': 'px', 'value': 1}, {'dimension': 'y', 'unit': 'px', 'value': 1},\n" +
                "              {'dimension': 't', 'value': \"month\"}], overlap=[], ) AS ?coll7) .\n"
                + "BIND (openeo:linear_scale_range(?coll7, {\"inputMin\": 0, \"inputMax\": 200, \"outputMin\": 0, \"outputMax\": 200}) AS ?coll8) .\n"
                + "BIND (openeo:merge_cubes(?coll8, ?coll3) AS ?coll9) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band3, \"eo:cloud_cover<=95\") AS ?coll10) .\n"
                + "BIND (openeo:mask(?coll10, ?coll9) AS ?coll11) .\n"
                + "BIND (openeo:aggregate_temporal_period(?coll11, \"month\", \"first\") AS ?coll12) .\n"
                + "BIND (openeo:filter_bbox(?coll12, ?xWkt) AS ?v) .\n"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of());
    }

    @Test
    public void getOpenEORadarVegetationIndex() {

        String query = "PREFIX :\t<http://www.unibz-openeo.org#>\n"
                + "PREFIX rdfs:\t<http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX geo:\t<http://www.opengis.net/ont/geosparql#>\n"
                + "PREFIX openeo:\t<http://www.openeo-ontop.org#>\n"
                + "SELECT ?v {\n"
                + "BIND(\"POLYGON((11.25 46.4, 11.75 46.4, 11.75 46.7, 11.25 46.7, 11.25 46.4))\"^^geo:wktLiteral AS ?xWkt) .\n"
                + "BIND (\"2023-09-01T00:00:00Z\"^^xsd:dateTime AS ?start_time) .\n"
                + "BIND (\"2023-09-07T00:00:00Z\"^^xsd:dateTime AS ?end_time) .\n"
                + "BIND (\"SENTINEL1_GRD\" AS ?satellite) .\n"
                + "BIND (\"[VV, VH]\" AS ?band1) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band1) AS ?coll1) .\n"
                + "BIND (openeo:sar_backscatter(?coll1, '{\"coefficient\": \"sigma0-ellipsoid\"}') AS ?coll2) .\n"
                + "BIND (openeo:band_math(?coll2, \"4 * x2 / (x1 + x2)\") AS ?v) .\n"
                + "}\n";
        //+ "BIND (openeo:filter_bands(?coll2, \"VV\") AS ?coll3) .\n"
        //+ "BIND (openeo:filter_bands(?coll2, \"VH\") AS ?coll4) .\n"
        //Alternatively use a process for multiply, add, filter_bands

        executeAndCompareValues(query, ImmutableList.of());
    }

    @Test
    public void getOpenEOSurfaceSoilMoisture() {

        String query = "PREFIX :\t<http://www.unibz-openeo.org#>\n"
                + "PREFIX rdfs:\t<http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX geo:\t<http://www.opengis.net/ont/geosparql#>\n"
                + "PREFIX openeo:\t<http://www.openeo-ontop.org#>\n"
                + "SELECT ?v {\n"
                + "BIND(\"POLYGON((11.25 46.4, 11.75 46.4, 11.75 46.7, 11.25 46.7, 11.25 46.4))\"^^geo:wktLiteral AS ?xWkt) .\n"
                + "BIND (\"2023-09-01T00:00:00Z\"^^xsd:dateTime AS ?start_time) .\n"
                + "BIND (\"2023-09-07T00:00:00Z\"^^xsd:dateTime AS ?end_time) .\n"
                + "BIND (\"2023-09-14T00:00:00Z\"^^xsd:dateTime AS ?end_time2) .\n"
                + "BIND (\"SENTINEL1_GRD\" AS ?satellite) .\n"
                + "BIND (\"VV\" AS ?band1) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band1) AS ?coll1) .\n"
                + "BIND (openeo:sar_backscatter(?coll1, {coefficient: \"sigma0-ellipsoid\"}) AS ?coll2) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time2, ?band1) AS ?coll3) .\n"
                + "BIND (openeo:sar_backscatter(?coll3, {coefficient: \"sigma0-ellipsoid\"}) AS ?coll4) .\n"
                + "BIND (openeo:reduce_dimension(?coll4, \"t\", \"last\") AS ?coll5) .\n"
                + "BIND (openeo:reduce_dimension(?coll2, \"t\", \"min\") AS ?coll6) .\n"
                + "BIND (openeo:reduce_dimension(?coll2, \"t\", \"max\") AS ?coll7) .\n"
                + "BIND (openeo:merge_cubes(?coll5, ?coll6, \"subtract\") AS ?coll8) .\n"
                + "BIND (openeo:merge_cubes(?coll7, ?coll6, \"subtract\") AS ?coll9) .\n"
                + "BIND (openeo:merge_cubes(?coll8, ?coll9, \"divide\") AS ?coll10) .\n"
                + "BIND (openeo:reduce_dimension(?coll2, \"t\", \"mean\") AS ?coll11) .\n"
                + "BIND (openeo:apply(10 * log(?coll11, 10)) AS ?coll12) .\n"
                + "BIND (openeo:filter_bands(?coll12, \"VV\") AS ?coll13) .\n"
                //Alternatively use a process for gt, lt, or
                + "BIND (openeo:apply(((?coll13 > -6) | (?coll13 < -17))) AS ?coll14) .\n"
                + "BIND (openeo:filter_bands(?coll10, ?coll14) AS ?mask) .\n"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of());
    }

    @Test
    public void getOpenEOParcelDelineation() {

        String query = "PREFIX :\t<http://www.unibz-openeo.org#>\n"
                + "PREFIX rdfs:\t<http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX geo:\t<http://www.opengis.net/ont/geosparql#>\n"
                + "PREFIX openeo:\t<http://www.openeo-ontop.org#>\n"
                + "SELECT ?v {\n"
                + "BIND(\"POLYGON((11.25 46.4, 11.75 46.4, 11.75 46.7, 11.25 46.7, 11.25 46.4))\"^^geo:wktLiteral AS ?xWkt) .\n"
                + "BIND (\"2023-09-01T00:00:00Z\"^^xsd:dateTime AS ?start_time) .\n"
                + "BIND (\"2023-09-07T00:00:00Z\"^^xsd:dateTime AS ?end_time) .\n"
                + "BIND (\"SENTINEL2_L2A\" AS ?satellite) .\n"
                + "BIND (\"[B04, B08, SCL]\" AS ?band1) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band1, \"eo:cloud_cover<=20\") AS ?coll1) .\n"
                + "BIND (openeo:filter_bands(?coll1, \"SCL\") AS ?coll2) .\n"
                + "BIND (openeo:oneof(?coll2, \"3\"^^xsd:integer || \"8\"^^xsd:integer || \"9\"^^xsd:integer) AS ?coll3) .\n"
                + "BIND (openeo:aggregate_spatial(?coll3, ?xWkt, \"mean\") AS ?coll4) .\n"
                // Next they sort data with xarray ... not something we can do ...
                + "BIND (import xarray as xr\n" +
                "from pandas import to_datetime\n" +
                "\n" +
                "def apply_timeseries(data: xr.DataArray, context: dict):\n" +
                "    nb = context.get(\"nb_of_timesteps\", 12)\n" +
                "    sorted_data = data.sortby(data)\n" +
                "    best_dates = sorted_data[\"t\"].values[:nb]\n" +
                "    # Convert to ISO date strings (YYYY-MM-DD)\n" +
                "    return [str(to_datetime(d).date()) for d in best_dates]\n AS ?udf) .\n"
                + "BIND (openeo:reduce_dimension(?coll4, \"t\", udf={?udf, context={\"nb_of_timesteps\": nb_of_timesteps}) AS ?coll5) .\n"
                + "BIND (openeo:filter_temporal(?coll1, ?coll5) AS ?coll6) .\n"
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of());
    }

    @Test
    public void getOpenEONO2Emissions() {

        String query = "PREFIX :\t<http://www.unibz-openeo.org#>\n"
                + "PREFIX rdfs:\t<http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX geo:\t<http://www.opengis.net/ont/geosparql#>\n"
                + "PREFIX openeo:\t<http://www.openeo-ontop.org#>\n"
                + "SELECT ?v {\n"
                + "BIND(\"POLYGON((11.25 46.4, 11.75 46.4, 11.75 46.7, 11.25 46.7, 11.25 46.4))\"^^geo:wktLiteral AS ?xWkt) .\n"
                + "BIND (\"2023-09-01T00:00:00Z\"^^xsd:dateTime AS ?start_time) .\n"
                + "BIND (\"2023-09-07T00:00:00Z\"^^xsd:dateTime AS ?end_time) .\n"
                + "BIND (\"SENTINEL_5P_L2\" AS ?satellite) .\n"
                + "BIND (\"NO2\" AS ?band1) .\n"
                + "BIND (openeo:load_collection(?satellite, ?xWkt, ?start_time, ?end_time, ?band1) AS ?coll1) .\n"
                + "BIND (openeo:aggregate_temporal_period(?coll2, ?xWkt, \"mean\", \"day\") AS ?coll3) .\n"
                + "BIND (openeo:aggregate_spatial(?coll3, ?xWkt, \"mean\") AS ?coll4) .\n"
                //TODO: Do the same pre/post COVID
                + "}\n";

        executeAndCompareValues(query, ImmutableList.of());
    }

}
