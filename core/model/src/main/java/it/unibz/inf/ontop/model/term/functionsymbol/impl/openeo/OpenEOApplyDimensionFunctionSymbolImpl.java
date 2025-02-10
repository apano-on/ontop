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

public class OpenEOApplyDimensionFunctionSymbolImpl extends OpenEOProcessGraphFunctionSymbolImpl {

    private final RDFDatatype xsdStringType;

    public OpenEOApplyDimensionFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                         RDFDatatype xsdStringDatatype) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, xsdStringDatatype, xsdStringDatatype), xsdStringDatatype);
        this.xsdStringType = xsdStringDatatype;
    }

    @Override
    protected final ImmutableTerm buildTermAfterEvaluation(ImmutableList<ImmutableTerm> newTerms,
                                                           TermFactory termFactory, VariableNullability variableNullability) {
        return handleApplyDimension(newTerms, termFactory);
    }

    private ImmutableTerm handleApplyDimension(ImmutableList<ImmutableTerm> newTerms,
                                               TermFactory termFactory) {
        if(newTerms.get(0).isGround() && ((GroundFunctionalTerm) newTerms.get(0)).getFunctionSymbol().getName().equals("RDF")) {
            return handleApplyDimension(newTerms.stream()
                    .map(t -> t instanceof Constant ? t : ((GroundFunctionalTerm) t).getTerms().get(0))
                    .collect(ImmutableCollectors.toList()), termFactory);
        }

        ImmutableList<? extends ImmutableTerm> subTerms =
                ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms();

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

        // Convert subTerms to RDFLiteralConstant where appropriate
        ImmutableList<? extends ImmutableTerm> fixedTerms = subTerms.stream()
                .map(t -> t instanceof Constant ? termFactory.getRDFLiteralConstant(((Constant) t).getValue(), termFactory.getTypeFactory().getXsdStringDatatype()) : t)
                .collect(ImmutableCollectors.toList());

        ImmutableList<ImmutableTerm> updatedTerms = builder
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
}
