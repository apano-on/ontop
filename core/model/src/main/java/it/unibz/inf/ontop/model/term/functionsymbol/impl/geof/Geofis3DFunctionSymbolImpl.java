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

public class Geofis3DFunctionSymbolImpl extends AbstractGeofBooleanFunctionSymbolDirectImpl<Function<ImmutableTerm, ImmutableTerm>> {

    public Geofis3DFunctionSymbolImpl(@Nonnull IRI functionIRI, RDFDatatype wktLiteralType, RDFDatatype xsdBooleanType) {
        super("GEOF_IS_3D", functionIRI, ImmutableList.of(wktLiteralType), xsdBooleanType);
    }

    @Override
    protected ImmutableTerm computeDBBooleanTerm(ImmutableList<ImmutableTerm> subLexicalTerms,
                                                 ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {

        Object dbFunction = getDBFunction(termFactory);
        WKTLiteralValue v0 = GeoUtils.extractWKTLiteralValue(termFactory, subLexicalTerms.get(0));
        ImmutableExpression condition = termFactory.getConjunction(
                termFactory.getDBNumericInequality(GT,
                        termFactory.getDBSTCoordinateDimension(v0.getGeometry()),
                        termFactory.getDBIntegerConstant(2)),
                termFactory.getDBIsNotNull(
                        ((Function<ImmutableTerm, ImmutableTerm>) dbFunction).apply(v0.getGeometry()))
        );

        return termFactory.getIfThenElse(condition,
                termFactory.getDBBooleanConstant(true),
                termFactory.getDBBooleanConstant(false));
    }

    @Override
    public Function<ImmutableTerm, ImmutableTerm> getDBFunction(TermFactory termFactory) {
        return termFactory::getDBSTMinZ;
    }
}
