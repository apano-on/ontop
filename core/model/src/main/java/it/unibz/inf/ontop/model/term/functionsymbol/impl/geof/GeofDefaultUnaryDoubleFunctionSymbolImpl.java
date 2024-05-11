package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GeofDefaultUnaryDoubleFunctionSymbolImpl extends AbstractGeofDoubleFunctionSymbolImpl {

    private final BiFunction<TermFactory, ImmutableTerm, ImmutableTerm> dbTermFct;
    private final IRI functionIRI;

    public GeofDefaultUnaryDoubleFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                     RDFDatatype wktLiteralType, RDFDatatype xsdDoubleType,
                                                     BiFunction<TermFactory, ImmutableTerm, ImmutableTerm> dbTermFct) {
        super(functionSymbolName, functionIRI, ImmutableList.of(wktLiteralType), xsdDoubleType);
        this.dbTermFct = dbTermFct;
        this.functionIRI = functionIRI;
    }

    @Override
    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {
        WKTLiteralValue v0 = GeoUtils.extractWKTLiteralValue(termFactory, subLexicalTerms.get(0));
        return getDBFunction(termFactory).apply(v0.getGeometry()).simplify();
    }

    public Function<ImmutableTerm, ImmutableTerm> getDBFunction(TermFactory termFactory) {
        return term -> dbTermFct.apply(termFactory, term);
    }
}
