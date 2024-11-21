package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;


import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.DBConstant;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.ObjectRDFType;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.function.Function;

import static it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo.OpenEOUtils.getSRID;

public class OpenEORasterFunctionSymbolImpl extends AbstractOpenEORasterFunctionSymbolImpl {

    private final SeptFunction<TermFactory, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm,
            ImmutableTerm, ImmutableTerm> dbTermFct;

    public OpenEORasterFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                               RDFDatatype xsdDate, RDFDatatype wktLiteralType, RDFDatatype xsdStringDatatype,
                                               ObjectRDFType iriType,
                                               SeptFunction<TermFactory, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm> dbTermFct) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdDate, xsdDate, wktLiteralType, xsdStringDatatype, xsdStringDatatype, iriType),
                xsdStringDatatype);
        this.dbTermFct = dbTermFct;
    }

    public OpenEORasterFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                          RDFDatatype xsdStringDatatype, RDFDatatype wktLiteralType, RDFDatatype xsdDateTime,
                                          SeptFunction<TermFactory, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm, ImmutableTerm> dbTermFct) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, wktLiteralType, xsdDateTime, xsdDateTime, xsdStringDatatype),
                xsdStringDatatype);
        this.dbTermFct = dbTermFct;
    }

    @Override
    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms,
                                          TermFactory termFactory) {
        String sridIRI = ((DBConstant) subLexicalTerms.get(5)).getValue();
        DBConstant srid = termFactory.getDBIntegerConstant(Integer.parseInt(getSRID(sridIRI)));
        return getDBFunction(termFactory, srid).apply(subLexicalTerms).simplify();
    }

    public Function<ImmutableList<ImmutableTerm>, ImmutableTerm> getDBFunction(TermFactory termFactory, DBConstant srid) {
        return terms -> dbTermFct.apply(termFactory, terms.get(0), terms.get(1), terms.get(2), terms.get(3), terms.get(4),
                termFactory.getDBStringConstant("EPSG:" + srid.getValue()));
    }

    @FunctionalInterface
    public interface SeptFunction<A, B, C, D, E, F, G, R> {
        R apply(A a, B b, C c, D d, E e, F f, G g);
    }



}

