package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.exception.MinorOntopInternalBugException;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.term.functionsymbol.impl.SPARQLFunctionSymbolImpl;
import it.unibz.inf.ontop.model.type.*;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OpenEOProcessGraphFunctionSymbolImpl extends SPARQLFunctionSymbolImpl {

    protected final RDFDatatype xsdStringType;
    private static Set<Integer> idTerms = new HashSet<>();

    public OpenEOProcessGraphFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                ImmutableList<TermType> inputTypes, RDFDatatype xsdStringDatatype) {
        super(functionSymbolName, functionIRI, inputTypes);
        this.xsdStringType = xsdStringDatatype;
    }

    public OpenEOProcessGraphFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                RDFDatatype xsdStringDatatype, RDFDatatype wktLiteralType, RDFDatatype xsdDateTime, RDFDatatype xsdStringDatatype2) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, wktLiteralType, xsdDateTime, xsdDateTime, xsdStringDatatype));
        this.xsdStringType = xsdStringDatatype;
    }

    public OpenEOProcessGraphFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                RDFDatatype xsdStringDatatype, RDFDatatype wktLiteralType, RDFDatatype xsdDateTime, RDFDatatype xsdStringDatatype2, RDFDatatype xsdStringType3) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, wktLiteralType, xsdDateTime, xsdDateTime, xsdStringDatatype, xsdStringDatatype));
        this.xsdStringType = xsdStringDatatype;
    }

    public OpenEOProcessGraphFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                RDFDatatype xsdStringDatatype, RDFDatatype xsdStringDatatype2, RDFDatatype xsdStringDatatype3) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, xsdStringDatatype2, xsdStringDatatype3));
        this.xsdStringType = xsdStringDatatype;
    }

    public OpenEOProcessGraphFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                RDFDatatype xsdStringDatatype, RDFDatatype xsdStringDatatype2) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, xsdStringDatatype2));
        this.xsdStringType = xsdStringDatatype;
    }

    public OpenEOProcessGraphFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                RDFDatatype... datatypes) {
        super(functionSymbolName, functionIRI,
                ImmutableList.copyOf(datatypes));
        this.xsdStringType = datatypes[0];
    }


    @Override
    protected ImmutableTerm buildTermAfterEvaluation(ImmutableList<ImmutableTerm> newTerms,
                                                           TermFactory termFactory, VariableNullability variableNullability) {

        if ((!tolerateNulls()
                && newTerms.stream().anyMatch(ImmutableTerm::isNull)))
            return termFactory.getNullConstant();

        if (!this.getName().equals("ONTOP_OPENEO_BASE")) {
            return computeOpenEOTerm(newTerms, termFactory);
        }

        if (newTerms.stream()
                .allMatch(t -> isRDFFunctionalTerm(t) || (t instanceof Constant))) {
            ImmutableList<ImmutableTerm> typeTerms = newTerms.stream()
                    .map(t -> extractRDFTermTypeTerm(t, termFactory))
                    .collect(ImmutableCollectors.toList());
            ImmutableList<ImmutableTerm> subLexicalTerms = newTerms.stream()
                    .map(t -> extractLexicalTerm(t, termFactory))
                    .collect(ImmutableCollectors.toList());
            ImmutableExpression.Evaluation inputTypeErrorEvaluation = evaluateInputTypeError(subLexicalTerms, typeTerms,
                    termFactory, variableNullability);
            //TODO: Better way needed to disable checks for the base function
            if (inputTypeErrorEvaluation.getValue().isPresent() && this.getName()!="ONTOP_OPENEO_BASE") {
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
        } else {
            return termFactory.getImmutableFunctionalTerm(this, newTerms);
        }
    }

    @Override
    public Optional<TermTypeInference> inferType(ImmutableList<? extends ImmutableTerm> terms) {
        return Optional.of(TermTypeInference.declareTermType(xsdStringType));
    }

    //@Override
    protected ImmutableTerm computeLexicalTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory, ImmutableTerm returnedTypeTerm) {
        DBTypeFactory dbTypeFactory = termFactory.getTypeFactory().getDBTypeFactory();

        return termFactory.getConversion2RDFLexical(
                dbTypeFactory.getDBStringType(),
                computeDBTerm(subLexicalTerms, typeTerms, termFactory),
                xsdStringType);
    }

    //@Override
    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms,
                                          TermFactory termFactory) {
        return termFactory.getOpenEOProcessGraph(subLexicalTerms);
    }

    //@Override
    protected ImmutableTerm computeTypeTerm(ImmutableList<? extends ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory, VariableNullability variableNullability) {
        return termFactory.getRDFTermTypeConstant(xsdStringType);
    }

    protected ImmutableTerm computeOpenEOTerm(ImmutableList<ImmutableTerm> subLexicalTerms, TermFactory termFactory) {
        return termFactory.getOpenEOProcessGraph(subLexicalTerms);
    }

    protected ImmutableExpression.Evaluation evaluateInputTypeError(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms,
                                                                    TermFactory termFactory, VariableNullability variableNullability) {
        ImmutableList<ImmutableExpression> typeTestExpressions = IntStream.range(0, typeTerms.size())
                .mapToObj(i -> termFactory.getIsAExpression(typeTerms.get(i), (RDFTermType) getExpectedBaseType(i)))
                .collect(ImmutableCollectors.toList());

        return termFactory.getConjunction(typeTestExpressions)
                .evaluate(variableNullability);
    }

    @Override
    public boolean canBePostProcessed(ImmutableList<? extends ImmutableTerm> arguments) {
        return false;
    }

    @Override
    protected boolean isAlwaysInjectiveInTheAbsenceOfNonInjectiveFunctionalTerms() {
        return false;
    }

    @Override
    protected boolean tolerateNulls() {
        return false;
    }

    private ImmutableTerm simplifyOpenEOFunctionSymbol(ImmutableList<ImmutableTerm> terms, TermFactory termFactory) {
        return termFactory.getImmutableFunctionalTerm(this, terms);
    }

    protected String generateUniqueId(ImmutableList<? extends ImmutableTerm> subTerms) {
        Set<Integer> existingIds = subTerms.stream()
                .map(Object::toString)
                .filter(s -> s.startsWith("\"id_"))
                .map(s -> s.substring(4, s.indexOf("\"", 4)))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        int maxId = Stream.concat(idTerms.stream(), existingIds.stream()).max(Integer::compare).orElse(0);
        idTerms.add(maxId + 1);
        return "id_" + (maxId + 1);
    }

    protected String generateUniqueIdFromNode(ImmutableList<? extends ImmutableTerm> subTerms) {
        Optional<String> previousId = subTerms.stream()
                .map(Object::toString)
                .filter(s -> s.startsWith("\"id_"))
                .findFirst();

        return previousId.isEmpty()
                ? null
                : "from_node_id_" + Integer.parseInt(previousId.get().substring(4, previousId.get().indexOf("\"", 4)));
    }

}