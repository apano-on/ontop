package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.exception.MinorOntopInternalBugException;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.term.functionsymbol.impl.SPARQLFunctionSymbolImpl;
import it.unibz.inf.ontop.model.type.DBTypeFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.model.type.RDFTermType;
import it.unibz.inf.ontop.model.type.TermTypeInference;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OpenEOProcessGraphFunctionSymbolImpl extends SPARQLFunctionSymbolImpl {

    private final RDFDatatype xsdStringType;

    public OpenEOProcessGraphFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                RDFDatatype xsdStringDatatype, RDFDatatype wktLiteralType, RDFDatatype xsdDateTime, RDFDatatype xsdStringDatatype2) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, wktLiteralType, xsdDateTime, xsdDateTime, xsdStringDatatype));
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
    protected final ImmutableTerm buildTermAfterEvaluation(ImmutableList<ImmutableTerm> newTerms,
                                                           TermFactory termFactory, VariableNullability variableNullability) {


        ImmutableList<ImmutableTerm> updatedTerms = newTerms;

        // CASE 1: LOAD_COLLECTION should always the starting operation in the process graph
        if (this.getName().equals("ONTOP_OPENEO_LOAD_COLLECTION")) {
            //ImmutableList<? extends ImmutableTerm> subTerms = ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms().subList(0, 5);
            ImmutableList<? extends ImmutableTerm> subTerms = newTerms;
            //ImmutableTerm id_term = termFactory.getRDFLiteralConstant("id", termFactory.getTypeFactory().getXsdStringDatatype());
            ImmutableTerm id_term = termFactory.getRDFLiteralConstant(generateUniqueId(subTerms), termFactory.getTypeFactory().getXsdStringDatatype());
            ImmutableTerm function_term = termFactory.getRDFLiteralConstant("load_collection", termFactory.getTypeFactory().getXsdStringDatatype());
            updatedTerms = ImmutableList.<ImmutableTerm>builder()
                    .add(id_term)
                    .add(function_term)
                    .addAll(newTerms)
                    .build();


            //TODO: Here return the default function symbol??? Arity???
            return termFactory.getImmutableFunctionalTerm(new OpenEOProcessGraphFunctionSymbolImpl("ONTOP_OPENEO_BASE", this.getIRI().get(),
                            this.xsdStringType, this.xsdStringType, this.xsdStringType, this.xsdStringType, this.xsdStringType, this.xsdStringType, this.xsdStringType),
                    updatedTerms);
        }

        if (this.getName().equals("ONTOP_OPENEO_REDUCE_DIMENSION")) {
            ImmutableList<? extends ImmutableTerm> subTerms = ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms();
            ImmutableTerm id_term = termFactory.getRDFLiteralConstant(generateUniqueId(subTerms), termFactory.getTypeFactory().getXsdStringDatatype());
            ImmutableTerm id_from_node_term = termFactory.getRDFLiteralConstant(generateUniqueIdFromNode(subTerms), termFactory.getTypeFactory().getXsdStringDatatype());
            ImmutableTerm function_term = termFactory.getRDFLiteralConstant("reduce_dimension", termFactory.getTypeFactory().getXsdStringDatatype());
            //TODO: It should reference the id it is connected to
            //TODO: Add a term for the from_id of the process graph? Or just assume it is the previous id?
            ImmutableList.Builder<ImmutableTerm> builder = ImmutableList.<ImmutableTerm>builder()
                    .add(id_term)
                    .add(function_term);

            if (id_from_node_term != null) {
                builder.add(id_from_node_term);
            }

            updatedTerms = builder
                    .addAll(newTerms.subList(1, newTerms.size()))
                    .addAll(subTerms)
                    .build();

            //TODO: Here return the default function symbol??? with the updated terms
            List<RDFDatatype> myList = Collections.nCopies(updatedTerms.size(), this.xsdStringType);
            return termFactory.getImmutableFunctionalTerm(new OpenEOProcessGraphFunctionSymbolImpl("ONTOP_OPENEO_BASE", this.getIRI().get(),
                            myList.toArray(new RDFDatatype[0])),
                    updatedTerms);
        }

        if (this.getName().equals("ONTOP_OPENEO_AGGREGATE_SPATIAL")) {
            ImmutableList<? extends ImmutableTerm> subTerms = ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms();
            ImmutableTerm id_term = termFactory.getRDFLiteralConstant(generateUniqueId(subTerms), termFactory.getTypeFactory().getXsdStringDatatype());
            ImmutableTerm id_from_node_term = termFactory.getRDFLiteralConstant(generateUniqueIdFromNode(subTerms), termFactory.getTypeFactory().getXsdStringDatatype());
            ImmutableTerm function_term = termFactory.getRDFLiteralConstant("aggregate_spatial", termFactory.getTypeFactory().getXsdStringDatatype());
            ImmutableList.Builder<ImmutableTerm> builder = ImmutableList.<ImmutableTerm>builder()
                    .add(id_term)
                    .add(function_term);

            if (id_from_node_term != null) {
                builder.add(id_from_node_term);
            }

            updatedTerms = builder
                    .addAll(newTerms.subList(1, newTerms.size()))
                    .addAll(subTerms)
                    .build();

            List<RDFDatatype> myList = Collections.nCopies(updatedTerms.size(), this.xsdStringType);
            return termFactory.getImmutableFunctionalTerm(new OpenEOProcessGraphFunctionSymbolImpl("ONTOP_OPENEO_BASE", this.getIRI().get(),
                            myList.toArray(new RDFDatatype[0])),
                    updatedTerms);
        }

        if ((!tolerateNulls()
                && newTerms.stream().anyMatch(ImmutableTerm::isNull)))
            return termFactory.getNullConstant();
        if (updatedTerms.stream()
                .allMatch(t -> isRDFFunctionalTerm(t) || (t instanceof Constant))) {
            ImmutableList<ImmutableTerm> typeTerms = updatedTerms.stream()
                    .map(t -> extractRDFTermTypeTerm(t, termFactory))
                    .collect(ImmutableCollectors.toList());
            ImmutableList<ImmutableTerm> subLexicalTerms = updatedTerms.stream()
                    .map(t -> extractLexicalTerm(t, termFactory))
                    .collect(ImmutableCollectors.toList());
            ImmutableExpression.Evaluation inputTypeErrorEvaluation = evaluateInputTypeError(subLexicalTerms, typeTerms,
                    termFactory, variableNullability);
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
        }
        else
        return termFactory.getImmutableFunctionalTerm(this, newTerms);
    }

    @Override
    public Optional<TermTypeInference> inferType(ImmutableList<? extends ImmutableTerm> terms) {
        return Optional.of(TermTypeInference.declareTermType(xsdStringType));
    }

    /**
     * Compute the lexical term when there is no input type error
     */
    /*protected abstract ImmutableTerm computeLexicalTerm(ImmutableList<ImmutableTerm> subLexicalTerms,
                                                        ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory,
                                                        ImmutableTerm returnedTypeTerm);
    protected abstract ImmutableTerm computeTypeTerm(ImmutableList<? extends ImmutableTerm> subLexicalTerms,
                                                     ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory,
                                                     VariableNullability variableNullability);*/

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

    private String generateUniqueId(ImmutableList<? extends ImmutableTerm> subTerms) {
        Set<Integer> existingIds = subTerms.stream()
                .map(Object::toString)
                .filter(s -> s.startsWith("\"id_"))
                .map(s -> s.substring(4, s.indexOf("\"", 4)))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        int maxId = existingIds.stream().max(Integer::compare).orElse(0);
        return "id_" + (maxId + 1);
    }

    private String generateUniqueIdFromNode(ImmutableList<? extends ImmutableTerm> subTerms) {
        Optional<String> previousId = subTerms.stream()
                .map(Object::toString)
                .filter(s -> s.startsWith("\"id_"))
                .findFirst();

        return previousId.isEmpty()
                ? null
                : "from_node_id_" + Integer.parseInt(previousId.get().substring(4, previousId.get().indexOf("\"", 4)));
    }

    private String extractString(Constant constant) {
        return constant.isNull()
                ? ""
                : constant.getValue();
    }
}