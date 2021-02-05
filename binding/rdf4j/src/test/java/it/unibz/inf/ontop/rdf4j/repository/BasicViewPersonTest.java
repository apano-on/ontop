package it.unibz.inf.ontop.rdf4j.repository;

import com.google.common.collect.ImmutableList;
import org.junit.*;

import java.io.IOException;
import java.sql.SQLException;


public class BasicViewPersonTest extends AbstractRDF4JTest {
    private static final String OBDA_FILE = "/person/person_basicviews.obda";
    private static final String SQL_SCRIPT = "/person/person.sql";
    private static final String VIEW_FILE = "/person/views/basic_views.json";

    @BeforeClass
    public static void before() throws IOException, SQLException {
        initOBDA(SQL_SCRIPT, OBDA_FILE, null, null, VIEW_FILE);
    }

    @AfterClass
    public static void after() throws SQLException {
        release();
    }

    /**
     * CONCAT + UPPER functions
     */
    @Test
    public void testPersonConcat() throws Exception {
        String query = "PREFIX : <http://person.example.org/>\n" +
                "PREFIX  xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "SELECT  ?v \n" +
                "WHERE {\n" +
                " ?x a :Person . \n" +
                " ?x :fullNameAndLocality ?v . \n" +
                "}";
        runQueryAndCompare(query, ImmutableList.of("ROGER SMITH Botzen"));
    }

    /**
     * REPLACE function
     */
    @Test
    public void testPersonReplace() throws Exception {
        String query = "PREFIX : <http://person.example.org/>\n" +
                "PREFIX  xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "SELECT  ?v \n" +
                "WHERE {\n" +
                " ?x a :Person . \n" +
                " ?x :localityAbbrev ?v . \n" +
                "}";
        runQueryAndCompare(query, ImmutableList.of("Bz"));
    }

    /**
     * NULLIF function
     */
    @Test
    public void testPersonNullif() throws Exception {
        String query = "PREFIX : <http://person.example.org/>\n" +
                "PREFIX  xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "SELECT  ?v \n" +
                "WHERE {\n" +
                " ?x a :Person . \n" +
                " ?x :nullifItaly ?v . \n" +
                "}";
        runQueryAndCompare(query, ImmutableList.of());
    }

    /**
     * Cast function
     */
    @Test
    public void testPersonCast() throws Exception {
        String query = "PREFIX : <http://person.example.org/>\n" +
                "PREFIX  xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "SELECT  ?v \n" +
                "WHERE {\n" +
                " ?x a :Person . \n" +
                " ?x :stringStatus ?v . \n" +
                "}";
        /*ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        ImmutableList<Integer> textResult = ImmutableList.copyOf(list);*/
        runQueryAndCompare(query, ImmutableList.of("1"));
    }

    /**
     * IF THEN ELSE function
     */
    @Test
    public void testPersonIfThenElse() throws Exception {
        String query = "PREFIX : <http://person.example.org/>\n" +
                "PREFIX  xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "SELECT  ?v \n" +
                "WHERE {\n" +
                " ?x a :Person . \n" +
                " ?x :region ?v . \n" +
                "}";
        runQueryAndCompare(query, ImmutableList.of("Sudtirol"));
    }
}
