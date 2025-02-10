package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.Constant;
import it.unibz.inf.ontop.model.term.ImmutableFunctionalTerm;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class OpenEOApplyFunctionSymbolImpl extends OpenEOProcessGraphFunctionSymbolImpl {

    private final RDFDatatype xsdStringType;

    public OpenEOApplyFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                    RDFDatatype xsdStringDatatype) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype), xsdStringDatatype);
        this.xsdStringType = xsdStringDatatype;
    }

    @Override
    protected final ImmutableTerm buildTermAfterEvaluation(ImmutableList<ImmutableTerm> newTerms,
                                                           TermFactory termFactory, VariableNullability variableNullability) {
        ImmutableList<? extends ImmutableTerm> subTerms = OpenEOUtils.getOpenEOBaseTerms(newTerms);

        String apply_operator_string = OpenEOUtils.getComparisonFunctionSymbol((ImmutableFunctionalTerm) newTerms.get(0));
        ImmutableTerm apply_operator_term = termFactory.getRDFLiteralConstant(
                apply_operator_string,
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        String value_comparator_string = ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms().size() > 1
                ? ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms().get(1).toString()
                : ((ImmutableFunctionalTerm) ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms().get(0)).getTerms().get(1).toString();
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

        // Convert subTerms to RDFLiteralConstant where appropriate
        ImmutableList<? extends ImmutableTerm> fixedTerms = subTerms.stream()
                .map(t -> t instanceof Constant ? termFactory.getRDFLiteralConstant(((Constant) t).getValue(), termFactory.getTypeFactory().getXsdStringDatatype()) : t)
                .collect(ImmutableCollectors.toList());

        ImmutableList<ImmutableTerm> updatedTerms = builder
                .add(apply_operator_term)
                .add(value_comparator_term)
                .addAll(newTerms.subList(1, newTerms.size()))
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
        return buildTermAfterEvaluation(terms.stream().collect(ImmutableCollectors.toList()),
                termFactory, variableNullability);
    }
}

