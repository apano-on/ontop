package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;

public class OpenEOApplyKernelFunctionSymbolImpl extends AbstractOpenEOFunctionSymbol {

    public OpenEOApplyKernelFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                         RDFDatatype xsdStringDatatype, RDFDatatype xsdIntegerDatatype, RDFDatatype xsdDoubleDatatype) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, xsdIntegerDatatype, xsdDoubleDatatype), xsdStringDatatype);
    }

    @Override
    protected String initFunctionName() {
        return "apply_kernel_gaussian";
    }
}
