package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.Constant;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

public class OpenEOMergeCubesFunctionSymbolImpl extends AbstractOpenEOFunctionSymbol {

    public OpenEOMergeCubesFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                            RDFDatatype xsdStringDatatype) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, xsdStringDatatype, xsdStringDatatype), xsdStringDatatype);
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

        // add overlap decider
        builder.add(terms.get(2));


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
