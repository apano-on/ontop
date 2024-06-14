package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.DBConstant;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;

public class GeofGeometryTypeFunctionSymbolImpl extends AbstractGeofStringFunctionSymbolImpl {

    public GeofGeometryTypeFunctionSymbolImpl(@Nonnull IRI functionIRI, RDFDatatype wktLiteralType, RDFDatatype xsdAnyUri) {
        super("GEOF_GEOMETRYTYPE", functionIRI, ImmutableList.of(wktLiteralType), xsdAnyUri);
    }

    @Override
    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {
        //TODO: InitCap for geometry type
        DBConstant simpleFeaturesPrefix = termFactory.getDBStringConstant("http://www.opengis.net/ont/sf#");
        return termFactory.getNullRejectingDBConcatFunctionalTerm(ImmutableList.of(simpleFeaturesPrefix,
                termFactory.getDBSTGeometryType(subLexicalTerms.get(0))));
    }
}
