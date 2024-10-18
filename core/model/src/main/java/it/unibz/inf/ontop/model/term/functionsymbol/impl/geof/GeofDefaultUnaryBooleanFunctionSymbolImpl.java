package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GeofDefaultUnaryBooleanFunctionSymbolImpl extends AbstractGeofBooleanFunctionSymbolDirectImpl<Function<ImmutableTerm, ImmutableTerm>> {

    private final BiFunction<TermFactory, ImmutableTerm, ImmutableTerm> dbTermFct;

    public GeofDefaultUnaryBooleanFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                     RDFDatatype wktLiteralType, RDFDatatype xsdBooleanType,
                                                     BiFunction<TermFactory, ImmutableTerm, ImmutableTerm> dbTermFct) {
        super(functionSymbolName, functionIRI, ImmutableList.of(wktLiteralType), xsdBooleanType);
        this.dbTermFct = dbTermFct;
    }

    public Function<ImmutableTerm, ImmutableTerm> getDBFunction(TermFactory termFactory) {
        return term -> dbTermFct.apply(termFactory, term);
    }
}
