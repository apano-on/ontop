package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.term.functionsymbol.SPARQLAggregationFunctionSymbol;
import it.unibz.inf.ontop.model.term.functionsymbol.impl.SPARQLFunctionSymbolImpl;
import it.unibz.inf.ontop.model.type.*;
import it.unibz.inf.ontop.model.vocabulary.GEO;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import it.unibz.inf.ontop.utils.VariableGenerator;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.stream.IntStream;

public abstract class AbstractGeofAggregateFunctionSymbolImpl extends SPARQLFunctionSymbolImpl
        implements SPARQLAggregationFunctionSymbol {

    private final RDFDatatype wktLiteralType;

    protected AbstractGeofAggregateFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                             @Nonnull ImmutableList<TermType> expectedBaseTypes, RDFDatatype wktLiteralType) {
        super(functionSymbolName, functionIRI, expectedBaseTypes);
        this.wktLiteralType = wktLiteralType;
        if (expectedBaseTypes.isEmpty())
            throw new IllegalArgumentException("The arity must be >= 1");
    }

    @Override
    protected ImmutableTerm buildTermAfterEvaluation(ImmutableList<ImmutableTerm> newTerms, TermFactory termFactory,
                                                     VariableNullability variableNullability) {
        if ((!tolerateNulls()
                && newTerms.stream().anyMatch(ImmutableTerm::isNull)))
            return termFactory.getNullConstant();

        ImmutableTerm newTerm = newTerms.get(0);
        if (newTerms.stream()
                .allMatch(t -> isRDFFunctionalTerm(t) || (t instanceof Constant))) {

            ImmutableList<ImmutableTerm> typeTerms = newTerms.stream()
                    .map(t -> extractRDFTermTypeTerm(t, termFactory))
                    .collect(ImmutableCollectors.toList());

            ImmutableList<ImmutableTerm> subLexicalTerms = newTerms.stream()
                    .map(t -> extractLexicalTerm(t, termFactory))
                    .collect(ImmutableCollectors.toList());

            ImmutableTerm typeTerm = computeTypeTerm(subLexicalTerms, typeTerms, termFactory, variableNullability);
            ImmutableTerm lexicalTerm = computeLexicalTerm(subLexicalTerms, typeTerms, termFactory, typeTerm);

            return termFactory.getRDFLiteralFunctionalTerm(
                            termFactory.getConversion2RDFLexical(lexicalTerm, wktLiteralType), wktLiteralType)
                    .simplify(variableNullability);
        }
        else if (newTerm.isNull()) {
            return termFactory.getRDFLiteralConstant("NULL", GEO.GEO_WKT_LITERAL);
        }

        return termFactory.getImmutableFunctionalTerm(this, newTerms);
    }

    /**
     * By default, does not tolerate receiving NULLs (SPARQL errors) as input
     */
    @Override
    protected boolean tolerateNulls() {
        return false;
    }

    @Override
    public boolean isAlwaysInjectiveInTheAbsenceOfNonInjectiveFunctionalTerms() {
        return false;
    }

    @Override
    public boolean isAggregation() {
        return true;
    }

    /**
     * MUST detect ALL the cases where the SPARQL function would produce an error (that is a NULL)
     * {@code ---> } the resulting condition must determine if the output of the SPARQL function is NULL (evaluates to FALSE or NULL)
     *      or not (evaluates to TRUE).
     *
     * Default implementation, can be overridden
     *
     */
    protected ImmutableExpression.Evaluation evaluateInputTypeError(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms,
                                                                    TermFactory termFactory, VariableNullability variableNullability) {
        ImmutableList<ImmutableExpression> typeTestExpressions = IntStream.range(0, typeTerms.size())
                .mapToObj(i -> termFactory.getIsAExpression(typeTerms.get(i), (RDFTermType) getExpectedBaseType(i)))
                .collect(ImmutableCollectors.toList());

        return termFactory.getConjunction(typeTestExpressions)
                .evaluate(variableNullability);
    }

    /**
     * Compute the lexical term when there is no input type error
     */
    protected abstract ImmutableTerm computeLexicalTerm(ImmutableList<ImmutableTerm> subLexicalTerms,
                                                        ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory,
                                                        ImmutableTerm returnedTypeTerm);

    protected abstract ImmutableTerm computeTypeTerm(ImmutableList<? extends ImmutableTerm> subLexicalTerms,
                                                     ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory,
                                                     VariableNullability variableNullability);


    @Override
    public Optional<AggregationSimplification> decomposeIntoDBAggregation(ImmutableList<? extends ImmutableTerm> subTerms, ImmutableList<ImmutableSet<RDFTermType>> possibleRDFTypes, boolean hasGroupBy, VariableNullability variableNullability, VariableGenerator variableGenerator, TermFactory termFactory) {
        if (possibleRDFTypes.size() != getArity()) {
            throw new IllegalArgumentException("The size of possibleRDFTypes is expected to match the arity of " +
                    "the function symbol");
        }

        return Optional.empty();
    }

    @Override
    public Constant evaluateEmptyBag(TermFactory termFactory) {
        return termFactory.getRDFLiteralConstant("", wktLiteralType);
    }
}
