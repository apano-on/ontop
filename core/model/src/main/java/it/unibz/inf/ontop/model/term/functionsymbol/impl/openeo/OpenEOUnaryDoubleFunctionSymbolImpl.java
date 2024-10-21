package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;


import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class OpenEOUnaryDoubleFunctionSymbolImpl extends AbstractOpenEOFunctionSymbolImpl {

    private final SeptFunction<TermFactory, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm> dbTermFct;

    /*public OpenEOUnaryDoubleFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                               RDFDatatype xsdDate, RDFDatatype wktLiteralType, RDFDatatype xsdStringDatatype,
                                               RDFDatatype xsdDoubleType,
                                               SeptFunction<TermFactory, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm> dbTermFct) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdDate, xsdDate, wktLiteralType, xsdStringDatatype, xsdStringDatatype, xsdStringDatatype, xsdStringDatatype),
                xsdDoubleType);
        this.dbTermFct = dbTermFct;
    }*/

    public OpenEOUnaryDoubleFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                               RDFDatatype xsdDate, RDFDatatype wktLiteralType, RDFDatatype xsdStringDatatype,
                                               RDFDatatype xsdDoubleType,
                                               SeptFunction<TermFactory, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm> dbTermFct) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdDate, xsdDate, wktLiteralType, xsdStringDatatype, xsdStringDatatype, xsdStringDatatype),
                xsdDoubleType);
        this.dbTermFct = dbTermFct;
    }

    @Override
    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {
        return getDBFunction(termFactory).apply(subLexicalTerms).simplify();
    }

    public Function<ImmutableList<ImmutableTerm>, ImmutableTerm> getDBFunction(TermFactory termFactory) {
        return terms -> dbTermFct.apply(termFactory, terms.get(0), terms.get(1), terms.get(2), terms.get(3), terms.get(4), terms.get(5));
    }

    @FunctionalInterface
    public interface SeptFunction<A, B, C, D, E, F, G, R> {
        R apply(A a, B b, C c, D d, E e, F f, G g);
    }
}
