package it.unibz.inf.ontop.rdf4j.repository.rules;

import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.rdf4j.repository.AbstractRDF4JTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import it.unibz.inf.ontop.query.translation.shacl.ShaclAfRegistryHolder;

public class SparqlRuleFunctionEmployeeTest extends AbstractRDF4JTest {

    private static final String OBDA_FILE = "/employee/employee.obda";
    private static final String SQL_SCRIPT = "/employee/employee.sql";
    private static final String SPARQL_RULES = "/employee/employee-rules-function.toml";

    @BeforeClass
    public static void before() throws IOException, SQLException {
        ShaclAfRegistryHolder.initFromTomlClasspath(SPARQL_RULES);
        initOBDA(SQL_SCRIPT, OBDA_FILE, null, null, null, null, SPARQL_RULES);
    }

    @AfterClass
    public static void after() throws SQLException {
        release();
    }

    @Test
    public void testFirstNameLength() {
        String q = "PREFIX : <http://employee.example.org/voc#>\n" +
                "SELECT ?v WHERE {\n" +
                "  ?e :firstNameLength ?v .\n" +
                "}";
        runQueryAndCompare(q, ImmutableSet.of("5","4"));   // order irrelevant
    }

    @Test
    public void testFullNameUpper() {
        String q = "PREFIX : <http://employee.example.org/voc#>\n" +
                "SELECT ?v WHERE {\n" +
                "  ?e :fullNameUpper ?v .\n" +
                "}";
        runQueryAndCompare(q, ImmutableSet.of("ROGER SMITH","ANNA GROSS"));
    }

    @Test
    public void testLastNameLower() {
        String q = "PREFIX : <http://employee.example.org/voc#>\n" +
                "SELECT ?v WHERE { ?e :lastNameLower ?v }";
        runQueryAndCompare(q, ImmutableSet.of("smith","gross"));
    }

    @Test
    public void testInitials() {
        String q = "PREFIX : <http://employee.example.org/voc#>\n" +
                "SELECT ?v WHERE { ?e :initials ?v }";
        runQueryAndCompare(q, ImmutableSet.of("R.S.","A.G."));
    }

    @Test
    public void testEmail() {
        String q = "PREFIX : <http://employee.example.org/voc#>\n" +
                "SELECT ?v WHERE { ?e :email ?v }";
        runQueryAndCompare(q, ImmutableSet.of(
                "roger.smith@company.com",
                "anna.gross@company.com"));
    }

    @Test
    public void testMakeEmailFunctionBind() {
        String q = "PREFIX : <http://employee.example.org/voc#>\n" +
                "SELECT ?v WHERE {\n" +
                "  ?e :firstName ?fn ; :lastName ?ln .\n" +
                "  BIND(:makeEmail(?fn, ?ln) AS ?v)\n" +
                "}";
        runQueryAndCompare(q, ImmutableSet.of(
                "roger.smith@company.com",
                "anna.gross@company.com"));
    }

}
