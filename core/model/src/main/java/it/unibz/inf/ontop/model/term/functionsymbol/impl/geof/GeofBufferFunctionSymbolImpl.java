package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.DBConstant;
import it.unibz.inf.ontop.model.term.ImmutableFunctionalTerm;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.term.functionsymbol.db.DBFunctionSymbolFactory;
import it.unibz.inf.ontop.model.term.functionsymbol.db.DBMathBinaryOperator;
import it.unibz.inf.ontop.model.type.DBTypeFactory;
import it.unibz.inf.ontop.model.type.ObjectRDFType;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.model.vocabulary.GEOF;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;

import java.util.Optional;

import static it.unibz.inf.ontop.model.term.functionsymbol.impl.geof.DistanceUnit.*;
import static java.lang.Math.PI;

public class GeofBufferFunctionSymbolImpl extends AbstractGeofWKTFunctionSymbolImpl {

    private final IRI functionIRI;

    public GeofBufferFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                        RDFDatatype wktLiteralType, RDFDatatype decimalType, ObjectRDFType iriType) {
        super(functionSymbolName, functionIRI, ImmutableList.of(wktLiteralType, decimalType, iriType), wktLiteralType);
        this.functionIRI = functionIRI;
    }

    public GeofBufferFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                        RDFDatatype wktLiteralType, RDFDatatype decimalType) {
        super(functionSymbolName, functionIRI, ImmutableList.of(wktLiteralType, decimalType), wktLiteralType);
        this.functionIRI = functionIRI;
    }

    /**
     * @param subLexicalTerms (geom, dist, unit)
     */
    @Override
    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {
        ImmutableTerm term = subLexicalTerms.get(0);
        WKTLiteralValue wktLiteralValue = GeoUtils.extractWKTLiteralValue(termFactory, term);

        DistanceUnit inputUnit = GeoUtils.getUnitFromSRID(wktLiteralValue.getSRID().getIRIString());
        ImmutableTerm distance = subLexicalTerms.get(1);

        DBFunctionSymbolFactory dbFunctionSymbolFactory = termFactory.getDBFunctionSymbolFactory();
        DBTypeFactory dbTypeFactory = termFactory.getTypeFactory().getDBTypeFactory();
        DBMathBinaryOperator times = dbFunctionSymbolFactory.getDBMathBinaryOperator("*", dbTypeFactory.getDBDoubleType());

        ImmutableTerm geom = unwrapSTAsText(wktLiteralValue.getGeometry());

        if (subLexicalTerms.size() == 2 && functionIRI.equals(GEOF.METRICBUFFER)) {
            return computeMetricBuffer(termFactory, dbTypeFactory, times, inputUnit, geom, distance);
        }

        DistanceUnit distanceUnit = getDistanceUnit(subLexicalTerms, termFactory);

        return computeDistanceUnits(termFactory, dbTypeFactory, times, inputUnit, distanceUnit, geom, distance);
    }

    private ImmutableTerm computeMetricBuffer(TermFactory termFactory, DBTypeFactory dbTypeFactory, DBMathBinaryOperator times, DistanceUnit inputUnit, ImmutableTerm geom, ImmutableTerm distance) {
        if (inputUnit == DEGREE) {
            return computeDegreeToMetre(termFactory, dbTypeFactory, times, geom, distance);
        } else {
            return termFactory.getDBAsText(termFactory.getDBBuffer(geom, distance)).simplify();
        }
    }

    private DistanceUnit getDistanceUnit(ImmutableList<ImmutableTerm> subLexicalTerms, TermFactory termFactory) {
        return subLexicalTerms.size() > 2
                ? DistanceUnit.findByIRI(((DBConstant) subLexicalTerms.get(2)).getValue())
                : DistanceUnit.METRE;
    }

    private ImmutableTerm computeDistanceUnits(TermFactory termFactory, DBTypeFactory dbTypeFactory, DBMathBinaryOperator times, DistanceUnit inputUnit, DistanceUnit distanceUnit, ImmutableTerm geom, ImmutableTerm distance) {
        if (inputUnit == DEGREE && distanceUnit == METRE) {
            return computeDegreeToMetre(termFactory, dbTypeFactory, times, geom, distance);
        } else if (inputUnit == DEGREE && distanceUnit == DEGREE) {
            return termFactory.getDBAsText(termFactory.getDBBuffer(geom, distance)).simplify();
        } else if (inputUnit == DEGREE && distanceUnit == RADIAN) {
            return computeDegreeToRadian(termFactory, dbTypeFactory, times, geom, distance);
        } else if (inputUnit == METRE && distanceUnit == METRE) {
            return termFactory.getDBAsText(termFactory.getDBBuffer(geom, distance)).simplify();
        } else {
            throw new IllegalArgumentException(
                    String.format("Unsupported unit combination for geof:buffer. inputUnit=%s, outputUnit=%s ",
                            inputUnit, distanceUnit));
        }
    }

    private ImmutableTerm computeDegreeToMetre(TermFactory termFactory, DBTypeFactory dbTypeFactory, DBMathBinaryOperator times, ImmutableTerm geom, ImmutableTerm distance) {
        if (dbTypeFactory.supportsDBGeographyType()) {
            return termFactory.getDBAsText(
                            termFactory.getDBBuffer(
                                    termFactory.getDBCastFunctionalTerm(dbTypeFactory.getDBGeographyType(), geom),
                                    distance))
                    .simplify();
        } else {
            final double EARTH_MEAN_RADIUS_METER = 6370986;
            final double ratio = 180 / PI / EARTH_MEAN_RADIUS_METER;
            DBConstant ratioConstant = termFactory.getDBConstant(String.valueOf(ratio), dbTypeFactory.getDBDoubleType());
            ImmutableFunctionalTerm distanceInDegree = termFactory.getImmutableFunctionalTerm(times, distance, ratioConstant);
            return termFactory.getDBAsText(
                            termFactory.getDBBuffer(geom, distanceInDegree))
                    .simplify();
        }
    }

    private ImmutableTerm computeDegreeToRadian(TermFactory termFactory, DBTypeFactory dbTypeFactory, DBMathBinaryOperator times, ImmutableTerm geom, ImmutableTerm distance) {
        final double ratio = 180 / PI;
        DBConstant ratioConstant = termFactory.getDBConstant(String.valueOf(ratio), dbTypeFactory.getDBDoubleType());
        ImmutableFunctionalTerm distanceInDegree = termFactory.getImmutableFunctionalTerm(times, distance, ratioConstant);
        return termFactory.getDBAsText(termFactory.getDBBuffer(geom, distanceInDegree)).simplify();
    }

    // if term is ST_ASTEXT(arg), returns arg, otherwise the term itself
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
