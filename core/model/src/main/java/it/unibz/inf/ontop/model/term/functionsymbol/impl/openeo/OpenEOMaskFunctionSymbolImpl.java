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
import java.util.stream.Stream;

public class OpenEOMaskFunctionSymbolImpl extends OpenEOProcessGraphFunctionSymbolImpl {

    private final RDFDatatype xsdStringType;

    public OpenEOMaskFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                         RDFDatatype xsdStringDatatype) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, xsdStringDatatype), xsdStringDatatype);
        this.xsdStringType = xsdStringDatatype;
    }

    @Override
    protected final ImmutableTerm buildTermAfterEvaluation(ImmutableList<ImmutableTerm> newTerms,
                                                           TermFactory termFactory, VariableNullability variableNullability) {
        return handleMask(newTerms, termFactory);
    }

    private ImmutableFunctionalTerm handleMask(ImmutableList<ImmutableTerm> newTerms, TermFactory termFactory) {

        if(newTerms.get(0).isGround() && ((GroundFunctionalTerm) newTerms.get(0)).getFunctionSymbol().getName().equals("RDF")
                && newTerms.get(0).isGround() && ((GroundFunctionalTerm) newTerms.get(0)).getFunctionSymbol().getName().equals("RDF")) {
            return handleMask(newTerms.stream()
                    .map(t -> ((GroundFunctionalTerm) t).getTerms().get(0))
                    .collect(ImmutableCollectors.toList()), termFactory);
        }

        ImmutableList<? extends ImmutableTerm> subTerms = Stream.of(
                        ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms(),
                        ((ImmutableFunctionalTerm) newTerms.get(1)).getTerms())
                .flatMap(List::stream)
                .collect(ImmutableList.toImmutableList());

        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        ImmutableTerm id_from_node_term = termFactory.getRDFLiteralConstant("to_node_" +
                        ((Constant) ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms().get(0)).getValue(),
                termFactory.getTypeFactory().getXsdStringDatatype());

        ImmutableTerm id_to_node_term = termFactory.getRDFLiteralConstant("from_node_" +
                        ((Constant) ((ImmutableFunctionalTerm) newTerms.get(1)).getTerms().get(0)).getValue(),
                termFactory.getTypeFactory().getXsdStringDatatype());

        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                "mask",
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableList.Builder<ImmutableTerm> builder = ImmutableList.<ImmutableTerm>builder()
                .add(id_term)
                .add(function_term)
                .add(id_from_node_term)
                .add(id_to_node_term);

        // Convert subTerms to RDFLiteralConstant where appropriate
        ImmutableList<? extends ImmutableTerm> fixedTerms = subTerms.stream()
                .map(t -> t instanceof Constant ? termFactory.getRDFLiteralConstant(((Constant) t).getValue(), termFactory.getTypeFactory().getXsdStringDatatype()) : t)
                .collect(ImmutableCollectors.toList());

        ImmutableList<ImmutableTerm> updatedTerms = builder
                //.addAll(newTerms.subList(1, newTerms.size()))
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
