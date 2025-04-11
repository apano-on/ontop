package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.term.functionsymbol.impl.AbstractBinaryComparisonSPARQLFunctionSymbol;
import it.unibz.inf.ontop.model.term.functionsymbol.impl.AbstractUnaryBooleanSPARQLFunctionSymbol;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.*;

public class OpenEOApplyFunctionSymbolImpl extends OpenEOProcessGraphFunctionSymbolImpl {

    private final RDFDatatype xsdStringType;

    public OpenEOApplyFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                    RDFDatatype xsdStringDatatype) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype), xsdStringDatatype);
        this.xsdStringType = xsdStringDatatype;
    }

    /*@Override
    protected String initFunctionName() {
        return "apply";
    }

    @Override
    protected ImmutableList<ImmutableTerm> additionalTerms(ImmutableList<ImmutableTerm> terms, TermFactory termFactory) {
        ImmutableList<? extends ImmutableTerm> subTerms = OpenEOUtils.getOpenEOBaseTerms(terms);

        String apply_operator_string = OpenEOUtils.getComparisonFunctionSymbol((ImmutableFunctionalTerm) terms.get(0));
        ImmutableTerm apply_operator_term = termFactory.getRDFLiteralConstant(
                apply_operator_string,
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        String value_comparator_string = ((ImmutableFunctionalTerm) terms.get(0)).getTerms().size() > 1
                ? ((ImmutableFunctionalTerm) terms.get(0)).getTerms().get(1).toString()
                : ((ImmutableFunctionalTerm) ((ImmutableFunctionalTerm) terms.get(0)).getTerms().get(0)).getTerms().get(1).toString();
        ImmutableTerm value_comparator_term = termFactory.getRDFLiteralConstant(
                value_comparator_string.replaceAll("^\"|\"\\^\\^.*$", ""),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        return ImmutableList.of(apply_operator_term, value_comparator_term);
    }*/

    @Override
    protected final ImmutableTerm buildTermAfterEvaluation(ImmutableList<ImmutableTerm> newTerms,
                                                           TermFactory termFactory, VariableNullability variableNullability) {



        // CASE 1: Comparison in apply
        if ((((ImmutableFunctionalTerm) newTerms.get(0)).getFunctionSymbol() instanceof AbstractUnaryBooleanSPARQLFunctionSymbol) ||
        (((ImmutableFunctionalTerm) newTerms.get(0)).getFunctionSymbol() instanceof AbstractBinaryComparisonSPARQLFunctionSymbol)) {

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

        } else {
            ImmutableList<? extends ImmutableTerm> subTerms = OpenEOUtils.getOpenEONonComparisonBaseTerms(newTerms);

            String apply_operator_string = OpenEOUtils.getNonComparisonFunctionSymbol((ImmutableFunctionalTerm) newTerms.get(0));
            ImmutableTerm apply_operator_term = termFactory.getRDFLiteralConstant(
                    apply_operator_string,
                    termFactory.getTypeFactory().getXsdStringDatatype()
            );
            //TODO: Not good enough, it must apply to an arbitrary number of terms
            String v1_string = ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms().get(0) instanceof RDFLiteralConstant
                    ? ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms().get(0).toString()
                    : ((ImmutableFunctionalTerm) ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms().get(0)).getTerms().get(0).toString();
            ImmutableTerm v1_term = termFactory.getRDFLiteralConstant(
                    v1_string.replaceAll("^\"|\"\\^\\^.*$", ""),
                    termFactory.getTypeFactory().getXsdStringDatatype()
            );
            String v2_string = ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms().get(1) instanceof RDFLiteralConstant
                    ? ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms().get(1).toString()
                    : ((ImmutableFunctionalTerm) ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms().get(1)).getFunctionSymbol().getName().replace("ONTOP_OPENEO_","").toLowerCase();
            ImmutableTerm v2_term = termFactory.getRDFLiteralConstant(
                    v2_string.replaceAll("^\"|\"\\^\\^.*$", ""),
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
                    .add(function_term)
                    .add(id_from_node_term);

            //TODO: Handles current cases forcing constant before operation term, but not robust
            if(!v1_string.contains("OPENEO_PROCESS_GRAPH")) {
                builder.add(v1_term)
                        .add(apply_operator_term)
                        .add(v2_term);
            } else {
                builder.add(v2_term)
                        .add(apply_operator_term);
            }

            ImmutableList<? extends ImmutableTerm> fixedTerms = subTerms.stream()
                    .map(t -> t instanceof Constant ?
                            termFactory.getRDFLiteralConstant(
                                    ((Constant) t).getValue(),
                                    termFactory.getTypeFactory().getXsdStringDatatype()
                            ) : t)
                    .collect(ImmutableCollectors.toList());
            builder.addAll(fixedTerms);

            List<RDFDatatype> datatypes = Collections.nCopies(builder.build().size(), this.xsdStringType);

            return termFactory.getImmutableFunctionalTerm(
                    new OpenEOProcessGraphFunctionSymbolImpl(
                            "ONTOP_OPENEO_BASE",
                            this.getIRI().get(),
                            datatypes.toArray(new RDFDatatype[0])
                    ),
                    removeDuplicatePipelines(builder.build())
            );
        }
    }

    // Apply checks for boolean expressions, they should not be simplified
    // TODO: Check if this is the correct approach
    @Override
    public ImmutableTerm simplify(ImmutableList<? extends ImmutableTerm> terms,
                                  TermFactory termFactory, VariableNullability variableNullability) {
        return buildTermAfterEvaluation(terms.stream().collect(ImmutableCollectors.toList()),
                termFactory, variableNullability);
    }

    // With examples like OilSpills we reuse the same load collection in 2 ways, it must be removed/de-duplicated
    protected ImmutableList<ImmutableTerm> removeDuplicatePipelines(ImmutableList<ImmutableTerm> processedTerms) {
        // Create a mutable copy of the list to allow for removal
        List<ImmutableTerm> mutableTerms = new ArrayList<>(processedTerms);

        // Keep track of ID literals we've already seen
        Set<String> seenIds = new HashSet<>();

        // For tracking ranges to remove
        int startRemovalIndex = -1;

        for (int i = 0; i < mutableTerms.size(); i++) {
            ImmutableTerm term = mutableTerms.get(i);

            // Check if term is an RDFLiteralConstant and starts with "id_"
            if (term instanceof RDFLiteralConstant) {
                RDFLiteralConstant literalTerm = (RDFLiteralConstant) term;
                String value = literalTerm.getValue();

                if (value.startsWith("id_")) {
                    if (seenIds.contains(value)) {
                        // This is a duplicate - mark the start of removal range
                        startRemovalIndex = i;
                    } else {
                        // First time seeing this ID
                        seenIds.add(value);

                        // If we have a pending removal range, execute the removal now
                        if (startRemovalIndex != -1) {
                            // Remove all terms from startRemovalIndex up to (but not including) current index
                            for (int j = i - 1; j >= startRemovalIndex; j--) {
                                mutableTerms.remove(j);
                            }

                            // Adjust current index after removal
                            i -= (i - startRemovalIndex);

                            // Reset removal marker
                            startRemovalIndex = -1;
                        }
                    }
                }
            }
        }

        // Handle case where the removal range extends to the end of the list
        if (startRemovalIndex != -1) {
            for (int j = mutableTerms.size() - 1; j >= startRemovalIndex; j--) {
                mutableTerms.remove(j);
            }
        }

        // Replace the contents of processedTerms with our filtered list
        // Note: Since ImmutableList is immutable, we would need the actual implementation
        // to determine how to update it. This code assumes there's a way to update it.
        // If processedTerms is truly immutable, you might need to return the new list instead.

        // Possible solution if processedTerms can be reassigned:
        // processedTerms = ImmutableList.copyOf(mutableTerms);

        // Or if you need to modify the method signature:
        return ImmutableList.copyOf(mutableTerms);
    }
}

