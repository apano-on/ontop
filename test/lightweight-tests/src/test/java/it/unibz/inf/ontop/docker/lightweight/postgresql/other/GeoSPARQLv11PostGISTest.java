package it.unibz.inf.ontop.docker.lightweight.postgresql.other;

import it.unibz.inf.ontop.docker.lightweight.AbstractGeoSPARQLv11Test;
import it.unibz.inf.ontop.docker.lightweight.PostgreSQLLightweightTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

@PostgreSQLLightweightTest
public class GeoSPARQLv11PostGISTest extends AbstractGeoSPARQLv11Test {

    private static final String PROPERTIES_FILE = "/geospatial/postgis/geospatialv1_1-postgis.properties";

    @BeforeAll
    public static void before() {
        initOBDA(OBDA_FILE, OWL_FILE, PROPERTIES_FILE);
    }

    @AfterAll
    public static void after() {
        release();
    }
}
