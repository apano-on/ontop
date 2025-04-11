package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.Constant;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;

public class OpenEOReduceDimensionFunctionSymbolImpl extends AbstractOpenEOFunctionSymbol {

    public OpenEOReduceDimensionFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                  RDFDatatype xsdStringDatatype) {
        super(functionSymbolName, functionIRI, ImmutableList.of(xsdStringDatatype, xsdStringDatatype, xsdStringDatatype),
                xsdStringDatatype);
    }

    @Override
    protected String initFunctionName() {
        return "reduce_dimension";
    }

    @Override
    protected ImmutableList<ImmutableTerm> processTermsBeforeComputation(
            ImmutableList<ImmutableTerm> terms,
            TermFactory termFactory) {
        if (terms.get(2).toString().contains("<") || terms.get(2).toString().contains(">")
                || terms.get(2).toString().contains(">=") || terms.get(2).toString().contains("<=")) {
            // Custom implementation for other cases
            return customProcessTerms(terms, termFactory);
        } else {
            // Use the parent implementation for comparison operators
            return super.processTermsBeforeComputation(terms, termFactory);
        }
    }

    private ImmutableList<ImmutableTerm> customProcessTerms(
            ImmutableList<ImmutableTerm> terms,
            TermFactory termFactory) {

        ImmutableList<? extends ImmutableTerm> subTerms = shouldExtractSubTerms() ?
                extractSubTerms(terms.get(0)) : terms;

        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(subTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                "reduce_dimension_comparison",
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

        builder.add(terms.get(1));

        ImmutableTerm t2 = termFactory.getRDFLiteralConstant(
                OpenEOUtils.convertSymbolsRegex(terms.get(2).toString()),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );
        builder.add(t2);



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
}
