package it.unibz.inf.ontop.rdf4j.repository;

import com.google.common.collect.ImmutableList;
import org.junit.*;

import java.io.IOException;
import java.sql.SQLException;


public class FlattenedViewPersonTest extends AbstractRDF4JTest {
    private static final String OBDA_FILE = "/person/person_flattenedviews.obda";
    private static final String SQL_SCRIPT = "/person/person_flatteneddb.sql";
    private static final String VIEW_FILE = "/person/views/flattened_views.json";

    @BeforeClass
    public static void before() throws IOException, SQLException {
        initOBDA(SQL_SCRIPT, OBDA_FILE, null, null, VIEW_FILE);
    }

    @AfterClass
    public static void after() throws SQLException {
        release();
    }

    @Test // Flatten - text tag
    public void testPersonFlatten() throws Exception {
        String query = "PREFIX : <http://person.example.org/>\n" +
                "PREFIX  xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "SELECT  ?v \n" +
                "WHERE {\n" +
                " ?x a :Person . \n" +
                " ?x :tagStr ?v . \n" +
                "}";
        runQueryAndCompare(query, ImmutableList.of("34"));
    }
}
