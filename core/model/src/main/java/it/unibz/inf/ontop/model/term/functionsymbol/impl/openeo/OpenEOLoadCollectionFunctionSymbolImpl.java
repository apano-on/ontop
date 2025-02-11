package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;

public class OpenEOLoadCollectionFunctionSymbolImpl extends AbstractOpenEOFunctionSymbol {

    public OpenEOLoadCollectionFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                  RDFDatatype xsdStringDatatype,
                                                  RDFDatatype wktLiteralType, RDFDatatype xsdDateTimeType) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, wktLiteralType, xsdDateTimeType, xsdDateTimeType,
                        xsdStringDatatype), xsdStringDatatype);
    }

    @Override
    protected String initFunctionName() {
        return "load_collection";
    }

    @Override
    protected boolean shouldExtractSubTerms() {
        return false;
    }

    @Override
    protected boolean loadCollection() {
        return true;
    }
}
