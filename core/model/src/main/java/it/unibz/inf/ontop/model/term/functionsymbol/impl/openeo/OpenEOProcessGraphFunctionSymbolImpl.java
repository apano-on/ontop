package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.exception.MinorOntopInternalBugException;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.term.functionsymbol.impl.AbstractBinaryBooleanOperatorSPARQLFunctionSymbol;
import it.unibz.inf.ontop.model.term.functionsymbol.impl.SPARQLFunctionSymbolImpl;
import it.unibz.inf.ontop.model.type.DBTypeFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.model.type.RDFTermType;
import it.unibz.inf.ontop.model.type.TermTypeInference;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OpenEOProcessGraphFunctionSymbolImpl extends SPARQLFunctionSymbolImpl {

    private final RDFDatatype xsdStringType;
    private static Set<Integer> idTerms = new HashSet<>();

    //TODO: Load Collection, not a robust implementation should have its own FS
    public OpenEOProcessGraphFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                RDFDatatype xsdStringDatatype, RDFDatatype wktLiteralType, RDFDatatype xsdDateTime, RDFDatatype xsdStringDatatype2) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, wktLiteralType, xsdDateTime, xsdDateTime, xsdStringDatatype));
        this.xsdStringType = xsdStringDatatype;
    }
    //TODO: Load Collection with Parameter, not a robust implementation should have its own FS
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
    protected final ImmutableTerm buildTermAfterEvaluation(ImmutableList<ImmutableTerm> newTerms,
                                                           TermFactory termFactory, VariableNullability variableNullability) {


        switch (this.getName()) {
            case "ONTOP_OPENEO_LOAD_COLLECTION":
                return handleLoadCollection(newTerms, termFactory);

            case "ONTOP_OPENEO_LOAD_COLLECTION_WITH_PARAMETER":
                return handleLoadCollectionWithParameter(newTerms, termFactory);

            case "ONTOP_OPENEO_REDUCE_DIMENSION":
                return handleReduceDimension(newTerms, termFactory);

            case "ONTOP_OPENEO_AGGREGATE_SPATIAL":
                return handleAggregateSpatial(newTerms, termFactory);

            case "ONTOP_OPENEO_BAND_MATH":
                return handleBandMath(newTerms, termFactory);

            case "ONTOP_OPENEO_APPLY":
                return handleApply(newTerms, termFactory);

            case "ONTOP_OPENEO_APPLY_DIMENSION":
                return handleApplyDimension(newTerms, termFactory);

            case "ONTOP_OPENEO_APPLY_KERNEL":
                return handleApplyKernel(newTerms, termFactory);

            case "ONTOP_OPENEO_MASK":
                return handleMask(newTerms, termFactory);

            case "ONTOP_OPENEO_NDVI":
                return handleNDVI(newTerms, termFactory);

            case "ONTOP_OPENEO_ONEOF":
                return handleoneof(newTerms, termFactory);
        }

        if ((!tolerateNulls()
                && newTerms.stream().anyMatch(ImmutableTerm::isNull)))
            return termFactory.getNullConstant();
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
        }
        else
        return termFactory.getImmutableFunctionalTerm(this, newTerms);
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

        int maxId = Stream.concat(idTerms.stream(), existingIds.stream()).max(Integer::compare).orElse(0);
        idTerms.add(maxId + 1);
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

    private ImmutableFunctionalTerm handleLoadCollection(ImmutableList<ImmutableTerm> newTerms, TermFactory termFactory) {
        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(newTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                "load_collection",
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableList<ImmutableTerm> updatedTerms = ImmutableList.<ImmutableTerm>builder()
                .add(id_term)
                .add(function_term)
                .addAll(newTerms)
                .build();

        List<RDFDatatype> datatypes = Collections.nCopies(updatedTerms.size(), this.xsdStringType);

        return termFactory.getImmutableFunctionalTerm(
                new OpenEOProcessGraphFunctionSymbolImpl(
                        "ONTOP_OPENEO_BASE",
                        this.getIRI().get(),
                        datatypes.toArray(new RDFDatatype[0])
                ),
                updatedTerms
        );
    }

    private ImmutableFunctionalTerm handleLoadCollectionWithParameter(ImmutableList<ImmutableTerm> newTerms, TermFactory termFactory) {
        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(newTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                "load_collection_with_parameter",
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        // Drop square brackets, and split down the middle the properties
        ImmutableList<ImmutableTerm> prunedNewTerms = newTerms.subList(2, newTerms.size()).stream()
                .map(t -> t.toString())
                .map(t -> {
                    String[] parts = t.split("=");
                    if (parts.length == 2) {
                        return Stream.of(parts[0], parts[1]);
                    }
                    return Stream.of(t.replaceAll("^\"\\[|\\]\"$", ""));
                })
                .flatMap(Function.identity())
                .map(t -> t.replaceAll("\\^\\^xsd:dateTime", ""))
                .map(t -> termFactory.getRDFLiteralConstant(t, termFactory.getTypeFactory().getXsdStringDatatype()))
                .collect(ImmutableCollectors.toList());

        ImmutableList<ImmutableTerm> updatedTerms = ImmutableList.<ImmutableTerm>builder()
                .add(id_term)
                .add(function_term)
                .add(newTerms.get(0))
                .add(newTerms.get(1))
                .addAll(prunedNewTerms)
                .build();

        List<RDFDatatype> datatypes = Collections.nCopies(updatedTerms.size(), this.xsdStringType);

        return termFactory.getImmutableFunctionalTerm(
                new OpenEOProcessGraphFunctionSymbolImpl(
                        "ONTOP_OPENEO_BASE",
                        this.getIRI().get(),
                        datatypes.toArray(new RDFDatatype[0])
                ),
                updatedTerms
        );
    }

    private ImmutableFunctionalTerm handleReduceDimension(ImmutableList<ImmutableTerm> newTerms, TermFactory termFactory) {
        ImmutableList<? extends ImmutableTerm> subTerms =
                ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms();

        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm id_from_node_term = termFactory.getRDFLiteralConstant(
                generateUniqueIdFromNode(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                "reduce_dimension",
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableList.Builder<ImmutableTerm> builder = ImmutableList.<ImmutableTerm>builder()
                .add(id_term)
                .add(function_term);

        if (id_from_node_term != null) {
            builder.add(id_from_node_term);
        }

        ImmutableList<ImmutableTerm> updatedTerms = builder
                .addAll(newTerms.subList(1, newTerms.size()))
                .addAll(subTerms)
                .build();
        List<RDFDatatype> datatypes = Collections.nCopies(updatedTerms.size(), this.xsdStringType);

        return termFactory.getImmutableFunctionalTerm(
                new OpenEOProcessGraphFunctionSymbolImpl(
                        "ONTOP_OPENEO_BASE",
                        this.getIRI().get(),
                        datatypes.toArray(new RDFDatatype[0])
                ),
                updatedTerms
        );
    }

    private ImmutableFunctionalTerm handleAggregateSpatial(ImmutableList<ImmutableTerm> newTerms, TermFactory termFactory) {
        ImmutableList<? extends ImmutableTerm> subTerms =
                ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms();

        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm id_from_node_term = termFactory.getRDFLiteralConstant(
                generateUniqueIdFromNode(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                "aggregate_spatial",
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableList.Builder<ImmutableTerm> builder = ImmutableList.<ImmutableTerm>builder()
                .add(id_term)
                .add(function_term);

        if (id_from_node_term != null) {
            builder.add(id_from_node_term);
        }

        ImmutableList<ImmutableTerm> updatedTerms = builder
                .addAll(newTerms.subList(1, newTerms.size()))
                .addAll(subTerms)
                .build();

        List<RDFDatatype> datatypes = Collections.nCopies(updatedTerms.size(), this.xsdStringType);

        return termFactory.getImmutableFunctionalTerm(
                new OpenEOProcessGraphFunctionSymbolImpl(
                        "ONTOP_OPENEO_BASE",
                        this.getIRI().get(),
                        datatypes.toArray(new RDFDatatype[0])
                ),
                updatedTerms
        );
    }

    private ImmutableFunctionalTerm handleBandMath(ImmutableList<ImmutableTerm> newTerms, TermFactory termFactory) {
        ImmutableList<? extends ImmutableTerm> subTerms =
                ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms();

        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm id_from_node_term = termFactory.getRDFLiteralConstant(
                generateUniqueIdFromNode(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                "process_graph",
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableList.Builder<ImmutableTerm> builder = ImmutableList.<ImmutableTerm>builder()
                .add(id_term)
                .add(function_term);

        if (id_from_node_term != null) {
            builder.add(id_from_node_term);
        }

        ImmutableList<ImmutableTerm> updatedTerms = builder
                .addAll(newTerms.subList(1, newTerms.size()))
                .addAll(subTerms)
                .build();

        List<RDFDatatype> datatypes = Collections.nCopies(updatedTerms.size(), this.xsdStringType);

        return termFactory.getImmutableFunctionalTerm(
                new OpenEOProcessGraphFunctionSymbolImpl(
                        "ONTOP_OPENEO_BASE",
                        this.getIRI().get(),
                        datatypes.toArray(new RDFDatatype[0])
                ),
                updatedTerms
        );
    }

    private ImmutableFunctionalTerm handleApply(ImmutableList<ImmutableTerm> newTerms, TermFactory termFactory) {
        ImmutableList<? extends ImmutableTerm> subTerms = OpenEOUtils.getOpenEOBaseTerms(newTerms);

        String apply_operator_string = OpenEOUtils.getComparisonFunctionSymbol((NonGroundFunctionalTerm) newTerms.get(0));
        ImmutableTerm apply_operator_term = termFactory.getRDFLiteralConstant(
                apply_operator_string,
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        String value_comparator_string = ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms().size() > 1
                ? ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms().get(1).toString()
                : ((NonGroundFunctionalTerm) ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms().get(0)).getTerms().get(1).toString();
        ImmutableTerm value_comparator_term = termFactory.getRDFLiteralConstant(
                value_comparator_string.replaceAll("^\"|\"\\^\\^.*$", ""),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm id_from_node_term = termFactory.getRDFLiteralConstant(
                generateUniqueIdFromNode(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                "apply",
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableList.Builder<ImmutableTerm> builder = ImmutableList.<ImmutableTerm>builder()
                .add(id_term)
                .add(function_term);

        if (id_from_node_term != null) {
            builder.add(id_from_node_term);
        }

        ImmutableList<ImmutableTerm> updatedTerms = builder
                .add(apply_operator_term)
                .add(value_comparator_term)
                .addAll(newTerms.subList(1, newTerms.size()))
                .addAll(subTerms)
                .build();

        List<RDFDatatype> datatypes = Collections.nCopies(updatedTerms.size(), this.xsdStringType);

        return termFactory.getImmutableFunctionalTerm(
                new OpenEOProcessGraphFunctionSymbolImpl(
                        "ONTOP_OPENEO_BASE",
                        this.getIRI().get(),
                        datatypes.toArray(new RDFDatatype[0])
                ),
                updatedTerms
        );
    }

    private ImmutableFunctionalTerm handleApplyDimension(ImmutableList<ImmutableTerm> newTerms, TermFactory termFactory) {
        ImmutableList<? extends ImmutableTerm> subTerms =
                ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms();

        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm id_from_node_term = termFactory.getRDFLiteralConstant(
                generateUniqueIdFromNode(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                "apply_dimension",
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableList.Builder<ImmutableTerm> builder = ImmutableList.<ImmutableTerm>builder()
                .add(id_term)
                .add(function_term);

        if (id_from_node_term != null) {
            builder.add(id_from_node_term);
        }

        ImmutableList<ImmutableTerm> updatedTerms = builder
                .addAll(newTerms.subList(1, newTerms.size()))
                .addAll(subTerms)
                .build();

        List<RDFDatatype> datatypes = Collections.nCopies(updatedTerms.size(), this.xsdStringType);

        return termFactory.getImmutableFunctionalTerm(
                new OpenEOProcessGraphFunctionSymbolImpl(
                        "ONTOP_OPENEO_BASE",
                        this.getIRI().get(),
                        datatypes.toArray(new RDFDatatype[0])
                ),
                updatedTerms
        );
    }

    private ImmutableFunctionalTerm handleApplyKernel(ImmutableList<ImmutableTerm> newTerms, TermFactory termFactory) {
        /*ImmutableList<? extends ImmutableTerm> subTerms =
                ((GroundFunctionalTerm) ((GroundFunctionalTerm) newTerms.get(0)).getTerms().get(0)).getTerms();*/
        ImmutableList<? extends ImmutableTerm> subTerms =
                ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms();

        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm id_from_node_term = termFactory.getRDFLiteralConstant(
                generateUniqueIdFromNode(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                "apply_kernel",
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableList.Builder<ImmutableTerm> builder = ImmutableList.<ImmutableTerm>builder()
                .add(id_term)
                .add(function_term);

        if (id_from_node_term != null) {
            builder.add(id_from_node_term);
        }

        /*ImmutableList<RDFLiteralConstant> fixedTerms = subTerms.stream()
                .map(t -> termFactory.getRDFLiteralConstant(((DBConstant) t).getValue().replaceAll("^\"|\"$", ""), termFactory.getTypeFactory().getXsdStringDatatype()))
                .collect(ImmutableList.toImmutableList());

        newTerms.subList(1, newTerms.size()).stream()
                .map(t -> ((RDFLiteralConstant) t).getValue())
                .map(t -> termFactory.getRDFLiteralConstant(t, termFactory.getTypeFactory().getXsdStringDatatype()))
                .forEach(builder::add);*/

        ImmutableList<ImmutableTerm> updatedTerms = builder
                .addAll(subTerms.subList(1, subTerms.size()))
                //.addAll(fixedTerms)
                .build();

        List<RDFDatatype> datatypes = Collections.nCopies(updatedTerms.size(), this.xsdStringType);

        return termFactory.getImmutableFunctionalTerm(
                new OpenEOProcessGraphFunctionSymbolImpl(
                        "ONTOP_OPENEO_BASE",
                        this.getIRI().get(),
                        datatypes.toArray(new RDFDatatype[0])
                ),
                updatedTerms
        );
    }

    private ImmutableFunctionalTerm handleMask(ImmutableList<ImmutableTerm> newTerms, TermFactory termFactory) {
        ImmutableList<? extends ImmutableTerm> subTerms = Stream.of(
                        ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms(),
                        ((NonGroundFunctionalTerm) newTerms.get(1)).getTerms())
                .flatMap(List::stream)
                .collect(ImmutableList.toImmutableList());

        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm id_from_node_term = termFactory.getRDFLiteralConstant("to_node_" +
                ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms().get(0).toString().replaceAll("^\"|\"$", ""),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm id_to_node_term = termFactory.getRDFLiteralConstant("from_node_" +
                ((NonGroundFunctionalTerm) newTerms.get(1)).getTerms().get(0).toString().replaceAll("^\"|\"$", ""),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                "mask",
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableList.Builder<ImmutableTerm> builder = ImmutableList.<ImmutableTerm>builder()
                .add(id_term)
                .add(function_term)
                .add(id_from_node_term)
                .add(id_to_node_term);

        ImmutableList<ImmutableTerm> updatedTerms = builder
                //.addAll(newTerms.subList(1, newTerms.size()))
                .addAll(subTerms)
                .build();

        List<RDFDatatype> datatypes = Collections.nCopies(updatedTerms.size(), this.xsdStringType);

        return termFactory.getImmutableFunctionalTerm(
                new OpenEOProcessGraphFunctionSymbolImpl(
                        "ONTOP_OPENEO_BASE",
                        this.getIRI().get(),
                        datatypes.toArray(new RDFDatatype[0])
                ),
                updatedTerms
        );
    }

    private ImmutableFunctionalTerm handleNDVI(ImmutableList<ImmutableTerm> newTerms, TermFactory termFactory) {
        ImmutableList<? extends ImmutableTerm> subTerms = ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms();

        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm id_from_node_term = termFactory.getRDFLiteralConstant(
                generateUniqueIdFromNode(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                "ndvi",
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableList.Builder<ImmutableTerm> builder = ImmutableList.<ImmutableTerm>builder()
                .add(id_term)
                .add(function_term);

        if (id_from_node_term != null) {
            builder.add(id_from_node_term);
        }

        /*ImmutableList<RDFLiteralConstant> fixedTerms = subTerms.stream()
                .map(t -> termFactory.getRDFLiteralConstant(((DBConstant) t).getValue().replaceAll("^\"|\"$", ""), termFactory.getTypeFactory().getXsdStringDatatype()))
                .collect(ImmutableList.toImmutableList());*/

        ImmutableList<ImmutableTerm> updatedTerms = builder
                .addAll(newTerms.subList(1, newTerms.size()))
                //.addAll(fixedTerms)
                .addAll(subTerms)
                .build();

        List<RDFDatatype> datatypes = Collections.nCopies(updatedTerms.size(), this.xsdStringType);

        return termFactory.getImmutableFunctionalTerm(
                new OpenEOProcessGraphFunctionSymbolImpl(
                        "ONTOP_OPENEO_BASE",
                        this.getIRI().get(),
                        datatypes.toArray(new RDFDatatype[0])
                ),
                updatedTerms
        );
    }

    private ImmutableFunctionalTerm handleoneof(ImmutableList<ImmutableTerm> newTerms, TermFactory termFactory) {
        ImmutableList<? extends ImmutableTerm> subTerms =
                ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms();

        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm id_from_node_term = termFactory.getRDFLiteralConstant(
                generateUniqueIdFromNode(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                "oneof",
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableList.Builder<ImmutableTerm> builder = ImmutableList.<ImmutableTerm>builder()
                .add(id_term)
                .add(function_term);

        if (id_from_node_term != null) {
            builder.add(id_from_node_term);
        }

        parseExpression(newTerms.get(1).toString())
                .stream().map(t -> termFactory.getRDFLiteralConstant(t, termFactory.getTypeFactory().getXsdStringDatatype()))
                .forEach(builder::add);

        /*ImmutableList<RDFLiteralConstant> fixedTerms = subTerms.stream()
                .map(t -> termFactory.getRDFLiteralConstant(((DBConstant) t).getValue().replaceAll("^\"|\"$", ""), termFactory.getTypeFactory().getXsdStringDatatype()))
                .collect(ImmutableList.toImmutableList());*/

        ImmutableList<ImmutableTerm> updatedTerms = builder
                .addAll(subTerms.subList(1, subTerms.size()))
                //.addAll(fixedTerms)
                .build();

        List<RDFDatatype> datatypes = Collections.nCopies(updatedTerms.size(), this.xsdStringType);

        return termFactory.getImmutableFunctionalTerm(
                new OpenEOProcessGraphFunctionSymbolImpl(
                        "ONTOP_OPENEO_BASE",
                        this.getIRI().get(),
                        datatypes.toArray(new RDFDatatype[0])
                ),
                updatedTerms
        );
    }

    @Override
    public ImmutableTerm simplify(ImmutableList<? extends ImmutableTerm> terms,
                                  TermFactory termFactory, VariableNullability variableNullability) {

        if (this.getName() != "ONTOP_OPENEO_ONEOF") {
            return super.simplify(terms, termFactory, variableNullability);
        }

        ImmutableList<ImmutableTerm> newTerms = terms.stream()
                .map(t -> (t instanceof ImmutableFunctionalTerm && !(((ImmutableFunctionalTerm) t).getFunctionSymbol() instanceof AbstractBinaryBooleanOperatorSPARQLFunctionSymbol))
                        ? t.simplify(variableNullability)
                        : t)
                .collect(ImmutableCollectors.toList());

        if ((!tolerateNulls()) && newTerms.stream().anyMatch(ImmutableTerm::isNull))
            return termFactory.getNullConstant();

        return buildTermAfterEvaluation(newTerms, termFactory, variableNullability);
    }

    private static List<String> parseExpression(String input) {
        List<String> result = new ArrayList<>();
        result.add("or"); // Always start with "or"
        extractNumbers(input, result);
        return result;
    }

    private static void extractNumbers(String input, List<String> result) {
        Matcher matcher = Pattern.compile("\"(\\d+)\"\\^\\^xsd:integer").matcher(input);
        while (matcher.find()) {
            result.add(matcher.group(1)); // Extract the number
        }
    }

    public static void main(String[] args) {
        String input = "SP_OR(SP_EBV(\"3\"^^xsd:integer),SP_OR(SP_EBV(\"8\"^^xsd:integer),SP_OR(SP_EBV(\"9\"^^xsd:integer),SP_EBV(\"10\"^^xsd:integer))))";
        List<String> output = parseExpression(input);
        System.out.println(output); // Expected: [or, 3, 8, 9, 10]
    }
}