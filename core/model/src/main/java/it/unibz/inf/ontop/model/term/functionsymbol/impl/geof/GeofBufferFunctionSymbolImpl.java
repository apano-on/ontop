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

import java.util.Arrays;
import java.util.Optional;

import static it.unibz.inf.ontop.model.term.functionsymbol.impl.geof.DistanceUnit.*;
import static java.lang.Math.PI;

public class GeofBufferFunctionSymbolImpl extends AbstractGeofWKTFunctionSymbolImpl {

    private final IRI functionIRI;
    private static final double DEGREE_TO_RADIAN_RATIO = 180 / PI;
    private static final double METRE_TO_DEGREE_RATIO = 1.0 / (180 * PI * GeoUtils.EARTH_MEAN_RADIUS_METER);

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
        WKTLiteralValue wktLiteralValue = GeoUtils.extractWKTLiteralValue(termFactory, subLexicalTerms.get(0));
        DistanceUnit inputUnit = GeoUtils.getUnitFromSRID(wktLiteralValue.getSRID().getIRIString());
        ImmutableTerm distance = subLexicalTerms.get(1);
        ImmutableTerm geom = unwrapSTAsText(wktLiteralValue.getGeometry());

        if (subLexicalTerms.size() == 2 && functionIRI.equals(GEOF.METRICBUFFER)) {
            return computeMetricBuffer(termFactory, inputUnit, geom, distance);
        }

        DistanceUnit distanceUnit = getDistanceUnit(subLexicalTerms);

        return computeDistanceUnits(termFactory, inputUnit, distanceUnit, geom, distance);
    }

    /**
     * Returns the buffer in meters - applicable to geof:metricBuffer
     */
    private ImmutableTerm computeMetricBuffer(TermFactory termFactory, DistanceUnit inputUnit, ImmutableTerm geom,
                                              ImmutableTerm distance) {
        if (inputUnit == DEGREE) {
            return computeDegreeToMetre(termFactory, geom, distance);
        } else {
            return computeBuffer(termFactory, geom, distance);
        }
    }

    /**
     * returns the distance unit from the subLexicalTerms, or METRE as default if not specified
     */
    private DistanceUnit getDistanceUnit(ImmutableList<ImmutableTerm> subLexicalTerms) {
        return subLexicalTerms.size() > 2
                ? DistanceUnit.findByIRI(((DBConstant) subLexicalTerms.get(2)).getValue())
                : DistanceUnit.METRE;
    }

    private ImmutableTerm computeDistanceUnits(TermFactory termFactory, DistanceUnit inputUnit, DistanceUnit distanceUnit,
                                               ImmutableTerm geom, ImmutableTerm distance) {
        if (inputUnit.equals(distanceUnit) && Arrays.asList(DistanceUnit.values()).contains(inputUnit)) {
            return computeBuffer(termFactory, geom, distance);
        } else if (inputUnit == DEGREE && distanceUnit == METRE) {
            return computeDegreeToMetre(termFactory, geom, distance);
        } else if (inputUnit == DEGREE && distanceUnit == RADIAN) {
            return computeDegreeToRadian(termFactory, geom, distance);
        } else if (inputUnit == METRE && distanceUnit == DEGREE) {
            return computeMetreToDegree(termFactory, geom, distance);
        } else {
            throw new IllegalArgumentException(
                    String.format("Unsupported unit combination for geof:buffer. inputUnit=%s, outputUnit=%s ",
                            inputUnit, distanceUnit));
        }
    }

    private ImmutableTerm computeDegreeToMetre(TermFactory termFactory, ImmutableTerm geom, ImmutableTerm distance) {
        DBTypeFactory dbTypeFactory = termFactory.getTypeFactory().getDBTypeFactory();
        if (dbTypeFactory.supportsDBGeographyType()) {
            return termFactory.getDBAsText(
                            termFactory.getDBBuffer(
                                    termFactory.getDBCastFunctionalTerm(dbTypeFactory.getDBGeographyType(), geom),
                                    distance))
                    .simplify();
        } else {
            return computeBuffer(termFactory, geom, multiplyByConstant(termFactory, distance,
                    180 / (PI * GeoUtils.EARTH_MEAN_RADIUS_METER)));
        }
    }

    private ImmutableTerm computeDegreeToRadian(TermFactory termFactory, ImmutableTerm geom, ImmutableTerm distance) {
        return computeBuffer(termFactory, geom, multiplyByConstant(termFactory, distance, DEGREE_TO_RADIAN_RATIO));
    }

    private ImmutableTerm computeMetreToDegree(TermFactory termFactory, ImmutableTerm geom, ImmutableTerm distance) {
        return computeBuffer(termFactory, geom, multiplyByConstant(termFactory, distance, METRE_TO_DEGREE_RATIO));
    }

    private ImmutableTerm computeBuffer(TermFactory termFactory, ImmutableTerm geom, ImmutableTerm distance) {
        return termFactory.getDBAsText(termFactory.getDBBuffer(geom, distance)).simplify();
    }

    private ImmutableTerm multiplyByConstant(TermFactory termFactory, ImmutableTerm term, double constant) {
        DBTypeFactory dbTypeFactory = termFactory.getTypeFactory().getDBTypeFactory();
        DBFunctionSymbolFactory dbFunctionSymbolFactory = termFactory.getDBFunctionSymbolFactory();
        DBMathBinaryOperator times = dbFunctionSymbolFactory.getDBMathBinaryOperator("*",
                dbTypeFactory.getDBDoubleType());
        DBConstant constantTerm = termFactory.getDBConstant(String.valueOf(constant), dbTypeFactory.getDBDoubleType());
        return termFactory.getImmutableFunctionalTerm(times, term, constantTerm);
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
