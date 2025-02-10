package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.RDFLiteralConstant;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class OpenEOLoadCollectionWithParameterFunctionSymbolImpl extends OpenEOProcessGraphFunctionSymbolImpl {

    private final RDFDatatype xsdStringType;

    public OpenEOLoadCollectionWithParameterFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                  RDFDatatype xsdStringDatatype,
                                                  RDFDatatype wktLiteralType, RDFDatatype xsdDateTimeType) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, wktLiteralType, xsdDateTimeType, xsdDateTimeType, xsdStringDatatype,
                        xsdStringDatatype), xsdStringDatatype);
        this.xsdStringType = xsdStringDatatype;
    }

    @Override
    protected final ImmutableTerm buildTermAfterEvaluation(ImmutableList<ImmutableTerm> newTerms,
                                                           TermFactory termFactory, VariableNullability variableNullability) {
        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(newTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                "load_collection_with_parameter",
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        String filter_operator_string = OpenEOUtils.convertSymbolsRegex(((RDFLiteralConstant) newTerms.get(5)).getValue());
        ImmutableTerm filter_operator_term = termFactory.getRDFLiteralConstant(
                filter_operator_string,
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableList<String> value_terms = OpenEOUtils.splitTerm(((RDFLiteralConstant) newTerms.get(5)).getValue());
        ImmutableTerm filtername_term = termFactory.getRDFLiteralConstant(
                value_terms.get(0),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm filtervalue_term = termFactory.getRDFLiteralConstant(
                value_terms.get(1),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        // Drop square brackets, and split down the middle the properties, ignore property term
        ImmutableList<ImmutableTerm> prunedNewTerms = newTerms.subList(2, newTerms.size()-1).stream()
                .map(t -> ((RDFLiteralConstant) t).getValue())
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
                .add(filtername_term)
                .add(filter_operator_term)
                .add(filtervalue_term)
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
}
