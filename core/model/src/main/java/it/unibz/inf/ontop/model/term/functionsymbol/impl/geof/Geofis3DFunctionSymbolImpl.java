package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class Geofis3DFunctionSymbolImpl extends AbstractGeofBooleanFunctionSymbolDirectImpl {

    public Geofis3DFunctionSymbolImpl(@Nonnull IRI functionIRI, RDFDatatype wktLiteralType, RDFDatatype xsdBooleanType) {
        super("GEOF_IS_3D", functionIRI, ImmutableList.of(wktLiteralType), xsdBooleanType);
    }

    @Override
    public Function<ImmutableTerm, ImmutableTerm> getDBFunction(TermFactory termFactory) {
        return termFactory::getDBSTis3D;
    }
}
