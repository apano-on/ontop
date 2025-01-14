package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.NonGroundFunctionalTerm;
import it.unibz.inf.ontop.model.term.functionsymbol.impl.GreaterThanSPARQLFunctionSymbolImpl;
import it.unibz.inf.ontop.model.term.functionsymbol.impl.LessThanSPARQLFunctionSymbolImpl;
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

    protected static ImmutableList<? extends ImmutableTerm> getOpenEOBaseTerms(ImmutableList<? extends ImmutableTerm> newTerms) {
        if(((NonGroundFunctionalTerm) newTerms.get(0)).getFunctionSymbol().getName().equals("ONTOP_OPENEO_BASE") ) {
            return ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms();
        } else {
            ImmutableTerm firstTerm = ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms().get(0);
            return getOpenEOBaseTerms(((NonGroundFunctionalTerm) firstTerm).getTerms());
        }
    }

    protected static String getComparisonFunctionSymbol(NonGroundFunctionalTerm term) {
        if (term.getFunctionSymbol().getName().equals("SP_LT")) {
            return "lt";
        } else if (term.getFunctionSymbol().getName().equals("SP_GT")) {
            return "gt";
        } else if (term.getFunctionSymbol().getName().equals("SP_EQ")) {
            return "eq";
        } else if (term.getFunctionSymbol().getName().equals("SP_NOT")) {
            ImmutableTerm subTerm = ((NonGroundFunctionalTerm) term).getTerms().get(0);
            String secondOperator = ((NonGroundFunctionalTerm) subTerm).getFunctionSymbol().getName();
            if (secondOperator.equals("SP_LT")) {
                return "gte";
            } else if (secondOperator.equals("SP_GT")) {
                return "lte";
            } else if (secondOperator.equals("SP_EQ")) {
                return "neq";
            }
        } else {
            throw new IllegalArgumentException("Unsupported comparison operator: " + term.getFunctionSymbol().getName());
        }
        return null;
    }
}
