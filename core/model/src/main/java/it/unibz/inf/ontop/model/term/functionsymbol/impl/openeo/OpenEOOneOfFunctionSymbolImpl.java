package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.Constant;
import it.unibz.inf.ontop.model.term.ImmutableFunctionalTerm;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.term.functionsymbol.impl.AbstractBinaryBooleanOperatorSPARQLFunctionSymbol;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenEOOneOfFunctionSymbolImpl extends OpenEOProcessGraphFunctionSymbolImpl {

    private final RDFDatatype xsdStringType;

    public OpenEOOneOfFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                         RDFDatatype xsdStringDatatype) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, xsdStringDatatype), xsdStringDatatype);
        this.xsdStringType = xsdStringDatatype;
    }

    @Override
    protected final ImmutableTerm buildTermAfterEvaluation(ImmutableList<ImmutableTerm> newTerms,
                                                           TermFactory termFactory, VariableNullability variableNullability) {
        ImmutableList<? extends ImmutableTerm> subTerms =
                ((ImmutableFunctionalTerm) newTerms.get(0)).getFunctionSymbol().getName().equals("RDF")
                        ? ((ImmutableFunctionalTerm) ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms().get(0)).getTerms()
                        : ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms();

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

        // Convert subTerms to RDFLiteralConstant where appropriate
        ImmutableList<? extends ImmutableTerm> fixedTerms = subTerms.stream()
                .map(t -> t instanceof Constant ? termFactory.getRDFLiteralConstant(((Constant) t).getValue(), termFactory.getTypeFactory().getXsdStringDatatype()) : t)
                .collect(ImmutableCollectors.toList());

        ImmutableList<ImmutableTerm> updatedTerms = builder
                //.addAll(subTerms)
                .addAll(fixedTerms)
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
}
