package it.unibz.inf.ontop.dbschema;

import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.exception.MetadataExtractionException;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JoinViewWithAccessPatternPersonTest {
    private static final String VIEW_FILE = "src/test/resources/person/join_views_access_pattern.json";
    private static final String DBMETADATA_FILE = "src/test/resources/person/person_with_constraints.db-extract.json";

    private final ImmutableSet<Lens> viewDefinitions = LensParsingTest.loadLensesH2(VIEW_FILE, DBMETADATA_FILE);

    public JoinViewWithAccessPatternPersonTest() throws Exception {
    }

    @Test
    public void testAccessPatternInputs() {
        ImmutableSet<String> inputs = viewDefinitions.stream()
                .flatMap(v -> v.getAccessPatterns().stream())
                .flatMap(ap -> ap.getInputs().stream())
                .map(a -> a.getID().getName())
                .collect(ImmutableCollectors.toSet());

        assertEquals(ImmutableSet.of("id"), inputs);
    }

    @Test
    public void testAccessPatternOutputs() {
        ImmutableSet<String> outputs = viewDefinitions.stream()
                .flatMap(v -> v.getAccessPatterns().stream())
                .flatMap(ap -> ap.getOutputs().stream())
                .map(a -> a.getID().getName())
                .collect(ImmutableCollectors.toSet());

        assertTrue(outputs.contains("country"));
        assertTrue(outputs.contains("c_name"));
    }

    @Test
    public void testInvalidAccessPatternFails() {
        assertThrows(MetadataExtractionException.class, () ->
                LensParsingTest.loadLensesH2(
                        "src/test/resources/person/join_views_invalid_access_pattern.json",
                        DBMETADATA_FILE));
    }
}
