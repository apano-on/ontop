package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.ImmutableExpression;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.function.Function;

import static it.unibz.inf.ontop.model.term.functionsymbol.InequalityLabel.GT;

public class GeofisMeasuredFunctionSymbolImpl extends AbstractGeofBooleanFunctionSymbolDirectImpl<Function<ImmutableTerm, ImmutableTerm>> {

    public GeofisMeasuredFunctionSymbolImpl(@Nonnull IRI functionIRI, RDFDatatype wktLiteralType, RDFDatatype xsdBooleanType) {
        super("GEOF_IS_MEASURED", functionIRI, ImmutableList.of(wktLiteralType), xsdBooleanType);
    }

    @Override
    protected ImmutableTerm computeDBBooleanTerm(ImmutableList<ImmutableTerm> subLexicalTerms,
                                                 ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {

        if (subLexicalTerms.size() > 1) {
            throw new IllegalArgumentException("GEOF_IS_MEASURED expects only one argument");
        }

        WKTLiteralValue v0 = GeoUtils.extractWKTLiteralValue(termFactory, subLexicalTerms.get(0));

        return termFactory.getDBSTisMeasured(v0.getGeometry()).simplify();
    }

    @Override
    public Function<ImmutableTerm, ImmutableTerm> getDBFunction(TermFactory termFactory) {
        return termFactory::getDBSTisMeasured;
    }
}
