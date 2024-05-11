package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class GeofisEmptyFunctionSymbolImpl extends AbstractGeofBooleanFunctionSymbolDirectImpl {

    public GeofisEmptyFunctionSymbolImpl(@Nonnull IRI functionIRI, RDFDatatype wktLiteralType, RDFDatatype xsdBooleanType) {
        super("GEOF_IS_EMPTY", functionIRI, ImmutableList.of(wktLiteralType), xsdBooleanType);
    }

    @Override
    public Function<ImmutableTerm, ImmutableTerm> getDBFunction(TermFactory termFactory) {
        return termFactory::getDBSTisEmpty;
    }
}
