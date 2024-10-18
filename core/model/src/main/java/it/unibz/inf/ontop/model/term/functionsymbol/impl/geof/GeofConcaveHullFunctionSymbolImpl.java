package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.DBConstant;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.DBTypeFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;

public class GeofConcaveHullFunctionSymbolImpl extends AbstractGeofWKTFunctionSymbolImpl {
    public GeofConcaveHullFunctionSymbolImpl(@Nonnull IRI functionIRI, RDFDatatype wktLiteralType) {
        super("GEOF_CONCAVEHULL", functionIRI, ImmutableList.of(wktLiteralType), wktLiteralType);
    }

    @Override
    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms,
                                          ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {
        WKTLiteralValue v0 = GeoUtils.extractWKTLiteralValue(termFactory, subLexicalTerms.get(0));
        DBTypeFactory dbTypeFactory = termFactory.getTypeFactory().getDBTypeFactory();
        DBConstant concavenessParameter = termFactory.getDBConstant(GeoUtils.CONCAVENESS_PARAMETER, dbTypeFactory.getDBDecimalType());
        return termFactory.getDBAsText(termFactory.getDBSTConcaveHull(v0.getGeometry(), concavenessParameter).simplify());
    }
}