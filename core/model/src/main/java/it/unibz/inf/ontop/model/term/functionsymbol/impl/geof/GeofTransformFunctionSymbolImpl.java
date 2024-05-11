package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.DBConstant;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.ObjectRDFType;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;

import javax.annotation.Nonnull;

public class GeofTransformFunctionSymbolImpl extends AbstractGeofWKTFunctionSymbolImpl {

    public GeofTransformFunctionSymbolImpl(@Nonnull IRI functionIRI, RDFDatatype wktLiteralType, ObjectRDFType iriType) {
        super("GEOF_TRANSFORM", functionIRI, ImmutableList.of(wktLiteralType, iriType), wktLiteralType);
    }

    @Override
    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {
        WKTLiteralValue v0 = GeoUtils.extractWKTLiteralValue(termFactory, subLexicalTerms.get(0));
        String sridIRI = ((DBConstant) subLexicalTerms.get(1)).getValue();
        DBConstant srid = termFactory.getDBIntegerConstant(Integer.valueOf(getSRID(sridIRI)));
        return termFactory.getDBAsText(
                termFactory.getDBSTTransform(v0.getGeometry(), srid).simplify());
    }

    private static String getSRID(String sridIRIString) {
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

    private static boolean isValidSRID(String srid) {
        CRSFactory factory = new CRSFactory();
        try {
            CoordinateReferenceSystem crs = factory.createFromName(GeoUtils.toProj4jName(srid));
            return crs != null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown SRID: " + srid);
        }
    }
}
