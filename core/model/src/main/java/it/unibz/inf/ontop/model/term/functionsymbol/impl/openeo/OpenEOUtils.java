package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import it.unibz.inf.ontop.model.term.functionsymbol.impl.geof.GeoUtils;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;

public class OpenEOUtils {
    protected static String getSRID(String sridIRIString) {
        final String CRS_PREFIX = "http://www.opengis.net/def/crs/OGC/1.3/CRS";
        final String EPSG_PREFIX = "http://www.opengis.net/def/crs/EPSG/0/";

        // For CRS projections only support CRS84
        if (sridIRIString.startsWith(CRS_PREFIX)) {
            if (sridIRIString.substring(CRS_PREFIX.length()).equals("/84")) {
                return "4326";
            } else {
                throw new IllegalArgumentException("Unsupported SRID IRI: " + sridIRIString);
            }
        } else if (sridIRIString.startsWith(EPSG_PREFIX) && isValidSRID(sridIRIString)) {
            return sridIRIString.substring(EPSG_PREFIX.length());
        }

        throw new IllegalArgumentException("Invalid SRID IRI: " + sridIRIString);
    }

    protected static boolean isValidSRID(String srid) {
        CRSFactory factory = new CRSFactory();
        try {
            CoordinateReferenceSystem crs = factory.createFromName(GeoUtils.toProj4jName(srid));
            return crs != null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown SRID: " + srid);
        }
    }
}
