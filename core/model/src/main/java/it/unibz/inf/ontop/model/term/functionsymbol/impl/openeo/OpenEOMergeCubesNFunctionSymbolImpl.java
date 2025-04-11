package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.Constant;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.RDFLiteralConstant;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class OpenEOMergeCubesNFunctionSymbolImpl extends AbstractOpenEOFunctionSymbol {

    public OpenEOMergeCubesNFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                              RDFDatatype xsdStringDatatype) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, xsdStringDatatype), xsdStringDatatype);
    }

    @Override
    protected String initFunctionName() {
        return "merge_cubes";
    }

    @Override
    protected ImmutableList<ImmutableTerm> processTermsBeforeComputation(
            ImmutableList<ImmutableTerm> terms,
            TermFactory termFactory) {
        ImmutableList<? extends ImmutableTerm> subTerms = Stream.concat(
                extractSubTerms(terms.get(0)).stream(),
                extractSubTerms(terms.get(1)).stream()).collect(ImmutableCollectors.toList());

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

        // We want the from node for both the first and second subgroups, this is similar to mask but we ignore the third term
        if (shouldExtractSubTerms()) {
            for (int i = 0; i < Math.min(2, terms.size()); i++) {
                ImmutableTerm id_from_node_term = termFactory.getRDFLiteralConstant(
                        generateUniqueIdFromNode(extractSubTerms(terms.get(i))),
                        termFactory.getTypeFactory().getXsdStringDatatype()
                );
                if (id_from_node_term != null) {
                    builder.add(id_from_node_term);
                }
            }
        }

        // no overlap decider
        //builder.add(terms.get(2));


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

        return removeDuplicatePipelines(builder.build());
    }

    // With examples like OilSpills we reuse the same load collection in 2 ways, it must be removed/de-duplicated
    protected ImmutableList<ImmutableTerm> removeDuplicatePipelines(ImmutableList<ImmutableTerm> processedTerms) {
        // Create a mutable copy of the list to work with
        List<ImmutableTerm> result = new ArrayList<>();

        // Keep track of ID literals we've already seen
        Set<String> seenIds = new HashSet<>();

        boolean skipping = false;

        for (ImmutableTerm term : processedTerms) {
            // Check if term is an RDFLiteralConstant and starts with "id_"
            if (term instanceof RDFLiteralConstant) {
                RDFLiteralConstant literalTerm = (RDFLiteralConstant) term;
                String value = literalTerm.getValue();

                if (value.startsWith("id_")) {
                    // We found an ID term
                    if (seenIds.contains(value)) {
                        // This is a duplicate - start skipping
                        skipping = true;
                    } else {
                        // First time seeing this ID
                        seenIds.add(value);
                        // Add this term to the result
                        result.add(term);
                        // Stop skipping since we found a new ID
                        skipping = false;
                    }
                } else if (!skipping) {
                    // Not an ID term and not in a skipping section, so add it
                    result.add(term);
                }
            } else if (!skipping) {
                // Not an RDFLiteralConstant and not in a skipping section, so add it
                result.add(term);
            }
        }

        return ImmutableList.copyOf(result);
    }
}
