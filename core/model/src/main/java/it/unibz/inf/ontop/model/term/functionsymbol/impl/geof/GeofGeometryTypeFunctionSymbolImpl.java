package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.type.ObjectRDFType;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;

public class GeofGeometryTypeFunctionSymbolImpl extends AbstractGeofIRIFunctionSymbolImpl {

    public GeofGeometryTypeFunctionSymbolImpl(@Nonnull IRI functionIRI, RDFDatatype wktLiteralType, ObjectRDFType IRIType) {
        super("GEOF_GEOMETRYTYPE", functionIRI, ImmutableList.of(wktLiteralType), IRIType);
    }

    @Override
    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {
        DBConstant simpleFeaturesPrefix = termFactory.getDBStringConstant("http://www.opengis.net/ont/sf#");
        ImmutableTerm geometryTypeTerm = termFactory.getDBSTGeometryType(subLexicalTerms.get(0));
        ImmutableFunctionalTerm conditionalGeometryTypeTerm = termFactory.getIfThenElse(
                termFactory.getDBStartsWith(
                        ImmutableList.of(geometryTypeTerm, termFactory.getDBStringConstant("ST_"))),
                termFactory.getDBSubString2(
                        geometryTypeTerm, termFactory.getDBStringConstant("4")),
                geometryTypeTerm);

        return termFactory.getNullRejectingDBConcatFunctionalTerm(ImmutableList.of(simpleFeaturesPrefix,
                conditionalGeometryTypeTerm));
    }
}
