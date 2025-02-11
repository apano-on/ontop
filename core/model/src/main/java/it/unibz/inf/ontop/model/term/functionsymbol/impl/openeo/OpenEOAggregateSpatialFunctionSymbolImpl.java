package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;

public class OpenEOAggregateSpatialFunctionSymbolImpl extends AbstractOpenEOFunctionSymbol {

    public OpenEOAggregateSpatialFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                   RDFDatatype xsdStringDatatype, RDFDatatype wktLiteralType) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, wktLiteralType,
                        xsdStringDatatype), xsdStringDatatype);
    }

    @Override
    protected String initFunctionName() {
        return "aggregate_spatial";
    }
}
