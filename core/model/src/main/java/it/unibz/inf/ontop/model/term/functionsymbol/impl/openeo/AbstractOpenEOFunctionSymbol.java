package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.type.*;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

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
        List<RDFDatatype> datatypes = Collections.nCopies(processedTerms.size(), super.xsdStringType);

        return termFactory.getImmutableFunctionalTerm(
                new OpenEOProcessGraphFunctionSymbolImpl(
                        "ONTOP_OPENEO_BASE",
                        super.getIRI().get(),
                        datatypes.toArray(new RDFDatatype[0])
                ),
                processedTerms
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
}

