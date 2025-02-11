package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;

public class OpenEOBandMathFunctionSymbolImpl extends AbstractOpenEOFunctionSymbol {

    public OpenEOBandMathFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                               RDFDatatype xsdStringDatatype) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, xsdStringDatatype), xsdStringDatatype);
    }

    @Override
    protected String initFunctionName() {
        return "band_math";
    }
}
