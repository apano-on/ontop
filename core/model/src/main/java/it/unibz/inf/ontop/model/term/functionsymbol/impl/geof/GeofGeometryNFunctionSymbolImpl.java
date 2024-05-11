package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;

public class GeofGeometryNFunctionSymbolImpl extends AbstractGeofWKTFunctionSymbolImpl {

    public GeofGeometryNFunctionSymbolImpl(@Nonnull IRI functionIRI, RDFDatatype wktLiteralType, RDFDatatype xsdIntegerType) {
        super("GEOF_GEOMETRYN", functionIRI, ImmutableList.of(wktLiteralType, xsdIntegerType), wktLiteralType);
    }

    @Override
    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {
        WKTLiteralValue v0 = GeoUtils.extractWKTLiteralValue(termFactory, subLexicalTerms.get(0));
        return termFactory.getDBAsText(
                termFactory.getDBSTGeometryN(v0.getGeometry(), subLexicalTerms.get(1)))
                .simplify();
    }
}