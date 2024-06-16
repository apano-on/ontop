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

        // If i) ST_NDIMS > 2, ii) ST_Z is FALSE, then the geometry is measured
        // Covers most cases but not robust cases with missing x or y coordinates
        ImmutableExpression condition = termFactory.getConjunction(
                termFactory.getDBNumericInequality(GT,
                        termFactory.getDBSTCoordinateDimension(v0.getGeometry()),
                        termFactory.getDBIntegerConstant(2)),
                termFactory.getDBIsNotNull(
                        termFactory.getDBSTMinZ(v0.getGeometry()))
                );

        /*ImmutableExpression trueExpression = termFactory.getIsTrue(termFactory.getDBBooleanConstant(true));
        ImmutableExpression falseExpression = termFactory.getIsTrue(termFactory.getDBBooleanConstant(false));

        return termFactory.getDBBooleanCase(
                ImmutableMap.of(condition, trueExpression).entrySet().stream(),
                trueExpression,
                false
        );*/
        return termFactory.getIfThenElse(condition,
                termFactory.getDBBooleanConstant(true),
                termFactory.getDBBooleanConstant(false));
    }

    //TODO: ST_HASM will be supported in the future in PostGIS 3.5.0
    @Override
    public Function<ImmutableTerm, ImmutableTerm> getDBFunction(TermFactory termFactory) {
        return termFactory::getDBSTisMeasured;
    }
}
