package it.unibz.inf.ontop.docker.lightweight.postgresql.other;

import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.docker.lightweight.AbstractDockerRDF4JTest;
import it.unibz.inf.ontop.docker.lightweight.PostgreSQLLightweightTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@PostgreSQLLightweightTest
public class InnerJoinLateralTest extends AbstractDockerRDF4JTest {

    protected static final String OWL_FILE = "/lateral/lateral.owl";
    protected static final String OBDA_FILE = "/lateral/innerjoinlateral.obda";
    private static final String PROPERTIES_FILE = "/lateral/postgresql/lateral-postgresql.properties";
    private static final String LENS_FILE = "/lateral/lateral.json";

    @BeforeAll
    public static void before() {
        initOBDA(OBDA_FILE, OWL_FILE, PROPERTIES_FILE, LENS_FILE);
    }

    @AfterAll
    public static void after() {
        release();
    }

    @Test
    public void testLateralJoinLens1() {
        String query = "PREFIX : <http://www.unibz-lateral.org#>\n" +
                "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
                "\n" +
                "SELECT ?c ?order ?date ?v \n" +
                "WHERE {\n" +
                "  ?c a :Customer .\n" +
                "  ?c :hasOrder ?order .\n" +
                "  ?order :orderDate ?date .\n" +
                "  ?order :amount ?v .\n" +
                "}\n";
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
