package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class OpenEOApplyKernelFunctionSymbolImpl extends OpenEOProcessGraphFunctionSymbolImpl {

    private final RDFDatatype xsdStringType;

    public OpenEOApplyKernelFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                         RDFDatatype xsdStringDatatype, RDFDatatype xsdIntegerDatatype, RDFDatatype xsdDoubleDatatype) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, xsdIntegerDatatype, xsdDoubleDatatype), xsdStringDatatype);
        this.xsdStringType = xsdStringDatatype;
    }

    @Override
    protected final ImmutableTerm buildTermAfterEvaluation(ImmutableList<ImmutableTerm> newTerms,
                                                           TermFactory termFactory, VariableNullability variableNullability) {
        ImmutableList<? extends ImmutableTerm> subTerms = !(newTerms.get(0).isGround())
                ? ((NonGroundFunctionalTerm) newTerms.get(0)).getTerms()
                : ((GroundFunctionalTerm) ((GroundFunctionalTerm) newTerms.get(0)).getTerms().get(0)).getTerms();

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

        newTerms.subList(1, newTerms.size()).stream()
                .map(t -> ((RDFLiteralConstant) t).getValue())
                .map(t -> termFactory.getRDFLiteralConstant(t, termFactory.getTypeFactory().getXsdStringDatatype()))
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
}
