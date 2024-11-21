package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.exception.MinorOntopInternalBugException;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.term.functionsymbol.db.impl.DefaultSimpleDBCastFunctionSymbol;
import it.unibz.inf.ontop.model.term.functionsymbol.impl.ReduciblePositiveAritySPARQLFunctionSymbolImpl;
import it.unibz.inf.ontop.model.type.*;
import it.unibz.inf.ontop.model.vocabulary.OPENEO;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * GeoSPARQL function must be reducible to DB functions and RDF construction and testing functions
 * It must also not reduce to a DB function before its substitution to extensional node variables
 *
 * Arity {@code >= 1 }
 */
public abstract class AbstractOpenEORasterFunctionSymbolImpl extends ReduciblePositiveAritySPARQLFunctionSymbolImpl {

    private final RDFDatatype xsdStringType;

    protected AbstractOpenEORasterFunctionSymbolImpl(
            @Nonnull String functionSymbolName,
            @Nonnull IRI functionIRI,
            ImmutableList<TermType> inputTypes,
            RDFDatatype xsdStringType) {
        super(functionSymbolName, functionIRI, inputTypes);
        this.xsdStringType = xsdStringType;
    }

    @Override
    protected final ImmutableTerm buildTermAfterEvaluation(ImmutableList<ImmutableTerm> newTerms,
                                                           TermFactory termFactory, VariableNullability variableNullability) {
        if ((!tolerateNulls()
                && newTerms.stream().anyMatch(ImmutableTerm::isNull)))
            return termFactory.getNullConstant();

        // CASE 1 - Load Collection
        if (this instanceof OpenEOLoadCollectionFunctionSymbolImpl) {
            RDFDatatype wktLiteralType = termFactory.getTypeFactory().getWktLiteralDatatype();
            RDFDatatype xsdDateTime = termFactory.getTypeFactory().getXsdDatetimeStampDatatype();
            ImmutableList<ImmutableTerm> updatedTerms = ImmutableList.<ImmutableTerm>builder()
                    .addAll(newTerms)
                    .add(termFactory.getRDFLiteralConstant("", termFactory.getTypeFactory().getXsdStringDatatype()))
                    .add(termFactory.getRDFLiteralConstant("", termFactory.getTypeFactory().getXsdStringDatatype()))
                    .build();
            return termFactory.getImmutableFunctionalTerm(
                    new OpenEODefaultAggFunctionSymbolImpl("ONTOP_OPENEO_AGG", OPENEO.AGG,
                            xsdStringType, wktLiteralType, xsdDateTime, xsdStringType, xsdStringType),
                    updatedTerms);
        }

        // CASE 2 - Other aggregation functions, every change modifies the function differently
        //TODO: Every case is handled differently ....
        //TODO: Consider generating the JSON in python and not serializing here at all???
        if (this instanceof OpenEOReduceDimensionFunctionSymbolImpl) {
            RDFDatatype wktLiteralType = termFactory.getTypeFactory().getWktLiteralDatatype();
            RDFDatatype xsdDateTime = termFactory.getTypeFactory().getXsdDatetimeStampDatatype();
            //TODO: Condition is that the subterm is an ONTOP_OPENEO_AGG function
            ImmutableList<ImmutableTerm> updatedTerms = ImmutableList.<ImmutableTerm>builder()
                    .addAll(((NonGroundFunctionalTerm) newTerms.get(0)).getTerms().subList(0, Math.min(newTerms.size(), 5)))
                    .add(newTerms.get(1))
                    .add(newTerms.get(2))
                    .build();
            return termFactory.getImmutableFunctionalTerm(
                    new OpenEODefaultAggFunctionSymbolImpl("ONTOP_OPENEO_AGG", OPENEO.AGG,
                            xsdStringType, wktLiteralType, xsdDateTime, xsdStringType, xsdStringType),
                    updatedTerms);
        }

        if (newTerms.stream()
                .allMatch(t -> isRDFFunctionalTerm(t) || (t instanceof Constant))) {

//            ImmutableList<ImmutableTerm> openeoTerms = newTerms.stream()
//                    .map(t -> extractOpenEOTerm(t, termFactory))
//                    .collect(ImmutableCollectors.toList());

            ImmutableList<ImmutableTerm> typeTerms = newTerms.stream()
                    .map(t -> extractRDFTermTypeTerm(t, termFactory))
                    .collect(ImmutableCollectors.toList());

            ImmutableList<ImmutableTerm> subLexicalTerms = newTerms.stream()
                    .map(t -> extractLexicalTerm(t, termFactory))
                    .collect(ImmutableCollectors.toList());

            ImmutableExpression.Evaluation inputTypeErrorEvaluation = evaluateInputTypeError(subLexicalTerms, typeTerms,
                    termFactory, variableNullability);

            if (inputTypeErrorEvaluation.getValue().isPresent()) {
                switch (inputTypeErrorEvaluation.getValue().get()) {
                    case FALSE:
                        // SPARQL error --> return NULL
                        return termFactory.getNullConstant();
                    case NULL:
                        throw new MinorOntopInternalBugException("This evaluation (SPARQL type error on the arguments) " +
                                "should not produce a NULL");
                        // TRUE: continue
                    default:
                        break;
                }
            }

            ImmutableTerm typeTerm = computeTypeTerm(subLexicalTerms, typeTerms, termFactory, variableNullability);
            ImmutableTerm lexicalTerm = computeLexicalTerm(subLexicalTerms, typeTerms, termFactory, typeTerm);

            Optional<ImmutableExpression> inputErrorCondition = inputTypeErrorEvaluation.getExpression();

            ImmutableExpression nonNullLexicalTermCondition = termFactory.getDBIsNotNull(lexicalTerm);

            ImmutableExpression typeCondition = inputErrorCondition
                    .map(c -> termFactory.getConjunction(c, nonNullLexicalTermCondition))
                    .orElse(nonNullLexicalTermCondition);

            return termFactory.getRDFFunctionalTerm(
                    inputErrorCondition
                            .map(c -> (ImmutableTerm) termFactory.getIfElseNull(c, lexicalTerm))
                            .orElse(lexicalTerm),
                    termFactory.getIfElseNull(typeCondition, typeTerm));
        }
        else
            return termFactory.getImmutableFunctionalTerm(this, newTerms);
    }

    @Override
    public Optional<TermTypeInference> inferType(ImmutableList<? extends ImmutableTerm> terms) {
        return Optional.of(TermTypeInference.declareTermType(xsdStringType));
    }

    @Override
    protected ImmutableTerm computeLexicalTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory, ImmutableTerm returnedTypeTerm) {
        DBTypeFactory dbTypeFactory = termFactory.getTypeFactory().getDBTypeFactory();

        return termFactory.getConversion2RDFLexical(
                dbTypeFactory.getDBStringType(),
                computeDBTerm(subLexicalTerms, typeTerms, termFactory),
                xsdStringType);
    }

    protected abstract ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms,
                                                   ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory);


    @Override
    protected ImmutableTerm computeTypeTerm(ImmutableList<? extends ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory, VariableNullability variableNullability) {
        return termFactory.getRDFTermTypeConstant(xsdStringType);
    }

    @Override
    protected boolean isAlwaysInjectiveInTheAbsenceOfNonInjectiveFunctionalTerms() {
        return false;
    }

    @Override
    public boolean canBePostProcessed(ImmutableList<? extends ImmutableTerm> arguments) {
        return false;
    }

    private ImmutableTerm extractOpenEOTerm(ImmutableTerm term, TermFactory termFactory) {
        if (term instanceof NonGroundFunctionalTerm &&
                ((NonGroundFunctionalTerm) term).getFunctionSymbol() instanceof DefaultSimpleDBCastFunctionSymbol) {
            ImmutableTerm subTerm = ((NonGroundFunctionalTerm) term).getTerm(0);

            return termFactory.getDBStringConstant(((DBConstant) term).getValue());
        }
        return term;
    }

}

