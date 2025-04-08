package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;

public class OpenEOApplyNeighborhoodFunctionSymbolImpl extends AbstractOpenEOFunctionSymbol {

    public OpenEOApplyNeighborhoodFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                           RDFDatatype xsdStringDatatype) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, xsdStringDatatype), xsdStringDatatype);
    }

    @Override
    protected String initFunctionName() {
        return "apply_neighborhood";
    }
}