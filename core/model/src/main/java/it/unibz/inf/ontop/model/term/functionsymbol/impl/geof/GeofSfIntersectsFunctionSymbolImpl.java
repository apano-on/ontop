package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.ImmutableFunctionalTerm;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.DBTypeFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.BiFunction;

public class GeofSfIntersectsFunctionSymbolImpl extends AbstractGeofBooleanFunctionSymbolDirectImpl {

    public GeofSfIntersectsFunctionSymbolImpl(@Nonnull IRI functionIRI, RDFDatatype wktLiteralType, RDFDatatype xsdBooleanType) {
        super("GEOF_SF_INTERSECTS", functionIRI, ImmutableList.of(wktLiteralType, wktLiteralType), xsdBooleanType);
    }

    @Override
    protected ImmutableTerm computeDBBooleanTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {
        DBTypeFactory dbTypeFactory = termFactory.getTypeFactory().getDBTypeFactory();

        WKTLiteralValue v0 = GeoUtils.extractWKTLiteralValue(termFactory, subLexicalTerms.get(0));
        WKTLiteralValue v1 = GeoUtils.extractWKTLiteralValue(termFactory, subLexicalTerms.get(1));

        if (!v0.getSRID().equals(v1.getSRID())) {
            throw new IllegalArgumentException(String.format("SRIDs do not match: %s and %s", v0.getSRID(), v1.getSRID()));
        }

        /**
         * If the database supports GEOGRAPHY (e.g. PostGIS v13) cast inputs to geography. Otherwise to geometry.
         * ST_INTERSECTS accepts both GEOGRAPHY and GEOMETRY inputs for PostGIS, thus if types are not explicitly
         * defined there is an error since PostGIS may not know which function to choose.
         * @see https://postgis.net/docs/ST_Intersects.html
         */
        //TODO: If buffer in string, do not cast to geography!
        //TODO: What about TEXT?
        if (dbTypeFactory.supportsDBGeographyType()) {
            ImmutableTerm term0 = (!v0.getGeometry().isGround() && v0.getGeometry().toString().contains("ST_BUFFER"))
                    ? v0.getGeometry()
                    : termFactory.getDBCastFunctionalTerm(dbTypeFactory.getDBGeographyType(), v0.getGeometry());
            ImmutableTerm term1 = (!v1.getGeometry().isGround() && v1.getGeometry().toString().contains("ST_BUFFER"))
                    ? v1.getGeometry()
                    : termFactory.getDBCastFunctionalTerm(dbTypeFactory.getDBGeographyType(), v1.getGeometry());
            return getDBFunction(termFactory).apply(
                    unwrapSTAsText(term0),
                    unwrapSTAsText(term1))
                    .simplify();

        }

        return getDBFunction(termFactory).apply(v0.getGeometry(), v1.getGeometry()).simplify();
    }

    @Override
    public BiFunction<ImmutableTerm, ImmutableTerm, ImmutableTerm> getDBFunction(TermFactory termFactory) {
        return termFactory::getDBSTIntersects;
    }

    private ImmutableTerm unwrapSTAsText(ImmutableTerm term) {
        return Optional.of(term)
                // term is a function
                .filter(t -> t instanceof ImmutableFunctionalTerm).map(ImmutableFunctionalTerm.class::cast)
                // the function symbol is ST_ASTEXT
                .filter(t -> t.getFunctionSymbol().getName().startsWith("ST_ASTEXT"))
                // extract the 0-th argument
                .map(t -> t.getTerm(0))
                // otherwise the term itself
                .orElse(term);
    }
}
