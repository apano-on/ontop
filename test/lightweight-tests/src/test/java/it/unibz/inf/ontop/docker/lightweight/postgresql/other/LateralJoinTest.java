package it.unibz.inf.ontop.docker.lightweight.postgresql.other;

import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.docker.lightweight.AbstractDockerRDF4JTest;
import it.unibz.inf.ontop.docker.lightweight.PostgreSQLLightweightTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@PostgreSQLLightweightTest
public class LateralJoinTest extends AbstractDockerRDF4JTest {

    protected static final String OWL_FILE = "/lateral/lateral.owl";
    protected static final String OBDA_FILE = "/lateral/lateral.obda";
    private static final String PROPERTIES_FILE = "/lateral/postgresql/lateral-postgresql.properties";

    @BeforeAll
    public static void before() {
        initOBDA(OBDA_FILE, OWL_FILE, PROPERTIES_FILE);
    }

    @AfterAll
    public static void after() {
        release();
    }

    /**
     * CASE 1: Set-returning table functions where LATERAL is required
     * Example: Extract vertices from geometries using ST_DumpPoints
     */
    @Test
    public void testLateralJoinSetReturningFunction1() {
        String query = "PREFIX : <http://www.unibz-lateral.org#>\n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "\n" +
                "SELECT ?roadName ?v \n" +
                "WHERE {\n" +
                "  ?road a :Road ;\n" +
                "        :name ?roadName ;\n" +
                "        :hasVertex ?vertex .\n" +
                "  ?vertex :longitude ?lon ;\n" +
                "          :latitude ?lat ;\n" +
                "          :sequence ?v .\n" +
                "}\n" +
                "ORDER BY ?roadName ?v\n";
        executeAndCompareValues(query, ImmutableSet.of("\"2014\"^^xsd:integer", "\"2011\"^^xsd:integer",
                "\"2015\"^^xsd:integer", "\"1970\"^^xsd:integer"));
        Assertions.assertTrue(reformulate(query).toLowerCase().contains("lateral join"));
//        SELECT r.name, (dp).path[1] as sequence,
//        ST_X((dp).geom) as lon, ST_Y((dp).geom) as lat
//        FROM "Roads" r
//        JOIN LATERAL ST_DumpPoints(r.geometry) AS dp ON TRUE
//        ORDER BY r.name, (dp).path[1];
// Without LATERAL:`ST_DumpPoints(r.geometry)` cannot reference `r.geometry`
    }

    /**
     * CASE 1: Set-returning table functions where LATERAL is required
     * Example: Unnest an array using unnest()
     * NOTE: PostgreSQL supports implicit LATERAL for unnest, but we force it here for testing purposes
     */
    @Test
    public void testLateralJoinSetReturningFunction2() {
        String query = "PREFIX : <http://www.unibz-lateral.org#>\n" +
                "\n" +
                "SELECT ?docTitle ?v\n" +
                "WHERE {\n" +
                "  ?doc a :Document ;\n" +
                "       :title ?docTitle ;\n" +
                "       :hasTag ?v .\n" +
                "}\n" +
                "ORDER BY ?docTitle ?v\n";
        //TODO: This is not an explicit LATERAL, since the comma is used instead
        //Assertions.assertTrue(reformulate(query).toLowerCase().contains("lateral join"));
        executeAndCompareValues(query, ImmutableSet.of("\"AI\"^^xsd:string", "\"Machine Learning\"^^xsd:string",
                "\"Neural Networks\"^^xsd:integer", "\"Indexing\"^^xsd:integer", "\"PostgreSQL\"^^xsd:string", "\"SQL\"^^xsd:string"));
//        SELECT d.title, t.tag
//FROM "Documents" d
//LEFT JOIN LATERAL unnest(d.tags) AS t(tag) ON TRUE
//ORDER BY d.title, t.tag;
// NOTE: Without LATERAL: It fails
    }

    /**
     * CASE 2: Classical query wit top-N per group by
     */
    @Test
    public void testGetTop2MostRecentOrderperCustomer() {
        String query = "PREFIX : <http://www.unibz-lateral.org#>\n" +
                "\n" +
                "SELECT ?customer ?v ?orderDate WHERE {\n" +
                "  ?customer a :Customer .\n" +
                "  {\n" +
                "    SELECT ?customer ?v ?orderDate WHERE {\n" +
                "      ?customer :hasOrder ?v .\n" +
                "      ?order :date ?orderDate .\n" +
                "    } ORDER BY DESC(?orderDate) LIMIT 2\n" +
                "  }\n" +
                "}\n\n";
        Assertions.assertTrue(reformulate(query).toLowerCase().contains("lateral join"));
//        SELECT c.customer_id, c.name, o.order_id, o.order_date
//        FROM Customers c
//        LEFT JOIN LATERAL (
//                SELECT order_id, order_date
//                FROM Orders
//                WHERE customer_id = c.customer_id
//                ORDER BY order_date DESC
//                LIMIT 2
//        ) o ON TRUE;

    }

    @Test
    public void testGetMostRecentOrderperCustomer() {
        String query = "PREFIX : <http://www.unibz-lateral.org#>\n" +
                "\n" +
                "SELECT ?customerName ?mostRecentDate ?mostRecentAmount\n" +
                "WHERE {\n" +
                "  ?customer a :Customer ;\n" +
                "            rdfs:label ?customerName .\n" +
                "  \n" +
                "  OPTIONAL {\n" +
                "    {\n" +
                "      SELECT ?customer ?orderDate ?amount\n" +
                "      WHERE {\n" +
                "        ?customer :hasOrder ?order .\n" +
                "        ?order :date ?orderDate ;\n" +
                "               :amount ?amount .\n" +
                "      }\n" +
                "      ORDER BY ?customer DESC(?orderDate)\n" +
                "      LIMIT 1  # This is per customer after grouping!\n" +
                "    }\n" +
                "    BIND(?orderDate as ?mostRecentDate)\n" +
                "    BIND(?amount as ?mostRecentAmount)\n" +
                "  }\n" +
                "}\n" +
                "ORDER BY ?customerName\n";
        Assertions.assertTrue(reformulate(query).toLowerCase().contains("lateral join"));
    }
//    SELECT c.name, recent.order_date, recent.amount
//FROM "Customers" c
//LEFT JOIN LATERAL (
//    SELECT order_date, amount
//    FROM "Orders"
//    WHERE customer_id = c.customer_id
//    ORDER BY order_date DESC
//    LIMIT 1
//) recent ON TRUE
//ORDER BY c.name;

    //vs.

//    SELECT c.name, ranked.order_date, ranked.amount
//    FROM "Customers" c
//    LEFT JOIN (
//            SELECT customer_id, order_date, amount
//                    FROM (
//                    SELECT customer_id, order_date, amount,
//            ROW_NUMBER() OVER (PARTITION BY customer_id ORDER BY order_date DESC) as rn
//    FROM "Orders"
//            ) subq
//    WHERE rn = 1
//) ranked ON c.customer_id = ranked.customer_id
//    ORDER BY c.name;


    @Test
    public void testjsqlparsersanitycheck() {
        String query = "PREFIX : <http://www.unibz-lateral.org#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "\n" +
                "SELECT ?customer ?name ?order\n" +
                "    WHERE {\n" +
                "  ?customer a :Customer ;\n" +
                "        rdfs:label ?name .\n" +
                "                 ?customer :hasOrder ?order . \n" +
                "    }\n" +
                "    LIMIT 50\n";
        Assertions.assertTrue(reformulate(query).toLowerCase().contains("lateral join"));
    }


    @Test
    public void testLateralJoinSTDumps() {
        String query = "PREFIX : <http://www.unibz-lateral.org#>\n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "\n" +
                "SELECT ?roadName ?v \n" +
                "WHERE {\n" +
                "  ?road a :Road ;\n" +
                "        :name ?roadName ;\n" +
                "        :hasVertex ?vertex .\n" +
                "  ?vertex :longitude ?lon ;\n" +
                "          :latitude ?lat ;\n" +
                "          :sequence ?v .\n" +
                "}\n";
                //"ORDER BY ?roadName ?v\n";
        executeAndCompareValues(query, ImmutableSet.of("\"2014\"^^xsd:integer", "\"2011\"^^xsd:integer",
                "\"2015\"^^xsd:integer", "\"1970\"^^xsd:integer"));
        Assertions.assertTrue(reformulate(query).toLowerCase().contains("lateral join"));
//        SELECT r.name, (dp).path[1] as sequence,
//        ST_X((dp).geom) as lon, ST_Y((dp).geom) as lat
//        FROM "Roads" r
//        JOIN LATERAL ST_DumpPoints(r.geometry) AS dp ON TRUE
//        ORDER BY r.name, (dp).path[1];
// Without LATERAL:`ST_DumpPoints(r.geometry)` cannot reference `r.geometry`
    }
}
