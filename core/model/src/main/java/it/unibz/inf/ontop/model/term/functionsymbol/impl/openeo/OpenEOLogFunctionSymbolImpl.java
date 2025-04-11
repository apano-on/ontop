package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;

public class OpenEOLogFunctionSymbolImpl extends AbstractOpenEOFunctionSymbol {

    public OpenEOLogFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                               RDFDatatype xsdStringDatatype, RDFDatatype xsdIntegerDatatype) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, xsdIntegerDatatype), xsdStringDatatype);
    }

    @Override
    protected String initFunctionName() {
        return "log";
    }
}
