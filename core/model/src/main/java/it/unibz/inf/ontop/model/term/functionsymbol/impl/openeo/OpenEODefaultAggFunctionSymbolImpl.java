package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.DBConstant;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class OpenEODefaultAggFunctionSymbolImpl extends AbstractOpenEORasterFunctionSymbolImpl {


    public OpenEODefaultAggFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                  RDFDatatype xsdStringDatatype, RDFDatatype wktLiteralType,
                                              RDFDatatype xsdDateTime) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, wktLiteralType, xsdDateTime, xsdDateTime, xsdStringDatatype,
                        xsdStringDatatype, xsdStringDatatype, xsdStringDatatype),
                xsdStringDatatype);
        //this.dbTermFct = dbTermFct;
    }

    @Override
    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms,
                                          TermFactory termFactory) {
        /*return termFactory.getOpenEOAgg(
                subLexicalTerms.size() > 0 ? subLexicalTerms.get(0) : null,
                subLexicalTerms.size() > 1 ? subLexicalTerms.get(1) : null,
                subLexicalTerms.size() > 2 ? subLexicalTerms.get(2) : null,
                subLexicalTerms.size() > 3 ? subLexicalTerms.get(3) : null,
                subLexicalTerms.size() > 4 ? subLexicalTerms.get(4) : null,
                subLexicalTerms.size() > 5 ? subLexicalTerms.get(5) : null,
                subLexicalTerms.size() > 6 ? subLexicalTerms.get(6) : null,
                subLexicalTerms.size() > 7 ? subLexicalTerms.get(7) : null);*/
        return termFactory.getOpenEOAgg(subLexicalTerms.get(0), subLexicalTerms.get(1), subLexicalTerms.get(2),
                subLexicalTerms.get(3), subLexicalTerms.get(4), subLexicalTerms.get(5), subLexicalTerms.get(6),
                subLexicalTerms.get(7));
    }

}
