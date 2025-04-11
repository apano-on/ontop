package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.type.*;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * OpenEO function symbol that represents a process graph function.
 */
public abstract class AbstractOpenEOFunctionSymbol extends OpenEOProcessGraphFunctionSymbolImpl {
    protected final String openEOFunctionName;

    protected AbstractOpenEOFunctionSymbol(@Nonnull String functionSymbolName,
                                               @Nonnull IRI functionIRI,
                                               @Nonnull ImmutableList<TermType> inputDatatypes,
                                                RDFDatatype xsdStringDatatype) {
        super(functionSymbolName, functionIRI, inputDatatypes, xsdStringDatatype);
        this.openEOFunctionName = initFunctionName();
    }

    // Template method to be implemented by subclasses to provide their specific function name
    protected abstract String initFunctionName();

    protected ImmutableTerm computeOpenEOTerm(ImmutableList<ImmutableTerm> subLexicalTerms,
                                          TermFactory termFactory) {
        ImmutableList<ImmutableTerm> processedTerms = processTermsBeforeComputation(subLexicalTerms, termFactory);
        ImmutableList<ImmutableTerm> newProcessedTerms = removeDuplicatePipelines(processedTerms);
        List<RDFDatatype> datatypes = Collections.nCopies(newProcessedTerms.size(), super.xsdStringType);

        return termFactory.getImmutableFunctionalTerm(
                new OpenEOProcessGraphFunctionSymbolImpl(
                        "ONTOP_OPENEO_BASE",
                        super.getIRI().get(),
                        datatypes.toArray(new RDFDatatype[0])
                ),
                newProcessedTerms
        );
    }

    protected ImmutableList<ImmutableTerm> processTermsBeforeComputation(
            ImmutableList<ImmutableTerm> terms,
            TermFactory termFactory) {
        ImmutableList<? extends ImmutableTerm> subTerms = shouldExtractSubTerms() ?
                extractSubTerms(terms.get(0)) : terms;

        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                openEOFunctionName,
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableList.Builder<ImmutableTerm> builder = ImmutableList.<ImmutableTerm>builder()
                .add(id_term)
                .add(function_term);

        if (shouldExtractSubTerms()) {
            ImmutableTerm id_from_node_term = termFactory.getRDFLiteralConstant(
                    generateUniqueIdFromNode(subTerms),
                    termFactory.getTypeFactory().getXsdStringDatatype()
            );
            if (id_from_node_term != null) {
                builder.add(id_from_node_term);
            }
        }

        if (customAdditionalTerms()) {
            builder.addAll(additionalTerms(terms, termFactory));
        }

        // Add remaining terms
        if (loadCollection()) {
            // Special case load_collection, it is always a starting node
            builder.addAll(terms);
        } else if (terms.size() > 1) {
            builder.addAll(terms.subList(1, terms.size()));
        }

        // Process subTerms if needed
        if (shouldExtractSubTerms()) {
            ImmutableList<? extends ImmutableTerm> fixedTerms = subTerms.stream()
                    .map(t -> t instanceof Constant ?
                            termFactory.getRDFLiteralConstant(
                                    ((Constant) t).getValue(),
                                    termFactory.getTypeFactory().getXsdStringDatatype()
                            ) : t)
                    .collect(ImmutableCollectors.toList());
            builder.addAll(fixedTerms);
        }

        return builder.build();
    }

    protected ImmutableList<? extends ImmutableTerm> extractSubTerms(ImmutableTerm term) {
        if (!(term instanceof ImmutableFunctionalTerm)) {
            return ImmutableList.of(term);
        }

        ImmutableFunctionalTerm functionalTerm = (ImmutableFunctionalTerm) term;
        String name = functionalTerm.getFunctionSymbol().getName();

        if (name.equals("OPENEO_PROCESS_GRAPH") || name.equals("ONTOP_OPENEO_BASE")) {
            return functionalTerm.getTerms();
        }

        return ((ImmutableFunctionalTerm) functionalTerm.getTerms().get(0)).getTerms();
    }

    // Override this in subclasses to control whether subTerms should be extracted
    protected boolean shouldExtractSubTerms() {
        return true;
    }

    // Override for load collection, as all terms are considered
    protected boolean loadCollection() {
        return false;
    }

    // Override for load collection, as all terms are considered
    protected boolean customAdditionalTerms() {
        return false;
    }

    // Override for specific functions like apply
    protected ImmutableList<ImmutableTerm> additionalTerms(ImmutableList<ImmutableTerm> terms, TermFactory termFactory) {
        return ImmutableList.of();
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

