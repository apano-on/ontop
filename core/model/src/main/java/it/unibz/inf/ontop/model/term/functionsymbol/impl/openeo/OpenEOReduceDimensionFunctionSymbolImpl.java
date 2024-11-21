package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;


import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.DBConstant;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.function.Function;

import static it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo.OpenEOUtils.getSRID;

public class OpenEOReduceDimensionFunctionSymbolImpl extends AbstractOpenEORasterFunctionSymbolImpl {

    private final QuadriFunction<TermFactory, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm> dbTermFct;

    public OpenEOReduceDimensionFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                  RDFDatatype xsdStringDatatype,
                                                  QuadriFunction<TermFactory, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm> dbTermFct) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, xsdStringDatatype, xsdStringDatatype),
                xsdStringDatatype);
        this.dbTermFct = dbTermFct;
    }

    @Override
    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms,
                                          TermFactory termFactory) {
        /*String sridIRI = ((DBConstant) subLexicalTerms.get(5)).getValue();
        DBConstant srid = termFactory.getDBIntegerConstant(Integer.parseInt(getSRID(sridIRI)));*/
        return getDBFunction(termFactory).apply(subLexicalTerms).simplify();
    }

    public Function<ImmutableList<ImmutableTerm>, ImmutableTerm> getDBFunction(TermFactory termFactory) {
        return terms -> dbTermFct.apply(termFactory, terms.get(0), terms.get(1), terms.get(2));
    }

    @FunctionalInterface
    public interface QuadriFunction<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);
    }



}

