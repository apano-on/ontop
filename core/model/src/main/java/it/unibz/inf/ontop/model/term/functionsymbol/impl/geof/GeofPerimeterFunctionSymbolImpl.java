package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;

public class GeofPerimeterFunctionSymbolImpl extends AbstractGeofDoubleFunctionSymbolImpl {

    public GeofPerimeterFunctionSymbolImpl(@Nonnull IRI functionIRI, RDFDatatype wktLiteralType, RDFDatatype xsdDoubleType) {
        super("GEOF_PERIMETER", functionIRI,
                ImmutableList.of(wktLiteralType),
                xsdDoubleType);
    }

    /**
     * @param subLexicalTerms (geom1, geom2, unit)
     */
    @Override
    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {

        WKTLiteralValue v0 = GeoUtils.extractWKTLiteralValue(termFactory, subLexicalTerms.get(0));
        ImmutableTerm geom0 = v0.getGeometry();

        return termFactory.getDBSTPerimeter(geom0).simplify();
    }
}
