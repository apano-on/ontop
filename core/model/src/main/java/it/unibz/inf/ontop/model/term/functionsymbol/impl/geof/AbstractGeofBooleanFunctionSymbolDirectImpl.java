package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.model.type.TermType;
import org.apache.commons.rdf.api.IRI;

import java.util.function.BiFunction;
import java.util.function.Function;

// direct implementation by translating to a corresponding DB function
public abstract class AbstractGeofBooleanFunctionSymbolDirectImpl<T> extends AbstractGeofBooleanFunctionSymbolImpl {

    protected AbstractGeofBooleanFunctionSymbolDirectImpl(String functionSymbolName, IRI functionIRI, ImmutableList<TermType> inputTypes, RDFDatatype xsdBooleanType) {
        super(functionSymbolName, functionIRI, inputTypes, xsdBooleanType);
    }

    @Override
    protected ImmutableTerm computeDBBooleanTerm(ImmutableList<ImmutableTerm> subLexicalTerms,
                                                 ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {
        Object dbFunction = getDBFunction(termFactory);
        WKTLiteralValue v0 = GeoUtils.extractWKTLiteralValue(termFactory, subLexicalTerms.get(0));

        if (subLexicalTerms.size() > 1) {
            WKTLiteralValue v1 = GeoUtils.extractWKTLiteralValue(termFactory, subLexicalTerms.get(1));
            return ((BiFunction<ImmutableTerm, ImmutableTerm, ImmutableTerm>) dbFunction).apply(v0.getGeometry(), v1.getGeometry()).simplify();
        }
        return ((Function<ImmutableTerm, ImmutableTerm>) dbFunction).apply(v0.getGeometry()).simplify();
    }

    public abstract <R, S> T getDBFunction(TermFactory termFactory);
}
