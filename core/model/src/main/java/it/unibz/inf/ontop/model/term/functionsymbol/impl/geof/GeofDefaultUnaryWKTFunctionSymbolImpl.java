package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GeofDefaultUnaryWKTFunctionSymbolImpl extends AbstractUnaryGeofWKTFunctionSymbolDirectImpl {

    private final BiFunction<TermFactory, ImmutableTerm, ImmutableTerm> dbTermFct;
    private final IRI functionIRI;

    public GeofDefaultUnaryWKTFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                 RDFDatatype wktLiteralType,
                                                 BiFunction<TermFactory, ImmutableTerm, ImmutableTerm> dbTermFct) {
        super(functionSymbolName, functionIRI, ImmutableList.of(wktLiteralType), wktLiteralType);
        this.dbTermFct = dbTermFct;
        this.functionIRI = functionIRI;
    }

    @Override
    public Function<ImmutableTerm, ImmutableTerm> getDBFunction(TermFactory termFactory) {
        return term -> dbTermFct.apply(termFactory, term);
    }
}
