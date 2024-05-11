package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class GeofBoundingCircleFunctionSymbolImpl extends AbstractUnaryGeofWKTFunctionSymbolDirectImpl {
    public GeofBoundingCircleFunctionSymbolImpl(@Nonnull IRI functionIRI, RDFDatatype wktLiteralType) {
        super("GEOF_BOUNDINGCIRCLE", functionIRI, ImmutableList.of(wktLiteralType), wktLiteralType);
    }

    @Override
    public Function<ImmutableTerm, ImmutableTerm> getDBFunction(TermFactory termFactory) {
        return termFactory::getDBSTBoundingCircle;
    }
}