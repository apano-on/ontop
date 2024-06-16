package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.DBConstant;
import it.unibz.inf.ontop.model.term.ImmutableExpression;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.term.functionsymbol.db.DBFunctionSymbolFactory;
import it.unibz.inf.ontop.model.term.functionsymbol.db.DBMathBinaryOperator;
import it.unibz.inf.ontop.model.type.DBTypeFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;

import static it.unibz.inf.ontop.model.term.functionsymbol.InequalityLabel.GT;

public class GeofSpatialDimensionFunctionSymbolImpl extends AbstractGeofIntegerFunctionSymbolImpl {

    public GeofSpatialDimensionFunctionSymbolImpl(@Nonnull IRI functionIRI, RDFDatatype wktLiteralType,
                                                  RDFDatatype xsdIntegerType) {
        super("GEOF_SPATIALDIMENSION", functionIRI, ImmutableList.of(wktLiteralType), xsdIntegerType);
    }

    @Override
    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {
        WKTLiteralValue v0 = GeoUtils.extractWKTLiteralValue(termFactory, subLexicalTerms.get(0));

        DBFunctionSymbolFactory dbFunctionSymbolFactory = termFactory.getDBFunctionSymbolFactory();
        DBTypeFactory dbTypeFactory = termFactory.getTypeFactory().getDBTypeFactory();
        DBMathBinaryOperator subtract = dbFunctionSymbolFactory.getDBMathBinaryOperator("-",
                dbTypeFactory.getDBLargeIntegerType());

        //TODO: Once PostGIS 3.5.0 is released, ST_HASM will be supported
        //ImmutableTerm mDimCheck = termFactory.getDBSTisMeasured(v0.getGeometry());
        // If i) ST_NDIMS > 2, ii) ST_Z is FALSE, then the geometry is measured
        // Covers most cases but not robust cases with missing x or y coordinates
        ImmutableExpression condition = termFactory.getConjunction(
                termFactory.getDBNumericInequality(GT,
                        termFactory.getDBSTCoordinateDimension(v0.getGeometry()),
                        termFactory.getDBIntegerConstant(2)),
                termFactory.getDBIsNotNull(
                        termFactory.getDBSTMinZ(v0.getGeometry()))
        );

        ImmutableTerm coordDim = termFactory.getDBSTCoordinateDimension(v0.getGeometry());
        DBConstant mDimConstant = termFactory.getDBConstant(String.valueOf(1), dbTypeFactory.getDBLargeIntegerType());

        // Subtract 1 if the geometry is measured
        return termFactory.getIfThenElse(condition,
                termFactory.getImmutableFunctionalTerm(subtract, coordDim, mDimConstant),
                coordDim);

    }
}
