package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.ObjectRDFType;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.model.vocabulary.GEOF;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;

public class GeofLengthFunctionSymbolImpl extends AbstractGeofDoubleFunctionSymbolImpl {

    private IRI functionIRI;

    public GeofLengthFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                      RDFDatatype wktLiteralType, ObjectRDFType iriType, RDFDatatype xsdDoubleType) {
        super(functionSymbolName, functionIRI, ImmutableList.of(wktLiteralType), xsdDoubleType);
    }

    public GeofLengthFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                      RDFDatatype wktLiteralType, RDFDatatype xsdDoubleType) {
        super(functionSymbolName, functionIRI, ImmutableList.of(wktLiteralType), xsdDoubleType);
    }

    /**
     * @param subLexicalTerms (geom1)
     */
    @Override
    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {

        WKTLiteralValue v0 = GeoUtils.extractWKTLiteralValue(termFactory, subLexicalTerms.get(0));
        ImmutableTerm geom0 = v0.getGeometry();

        if (this.functionIRI == GEOF.LENGTH) {
            return termFactory.getDBSTLength(geom0).simplify();
        } else {
            // Convert the geometry to SRID 4326, which is the SRID used by the metric functions
            // GEOF.METRICLENGTH
            ImmutableTerm metricGeom = termFactory.getDBSTSetSRID(geom0, termFactory.getDBIntegerConstant(4326));
            return termFactory.getDBSTLength(metricGeom).simplify();
        }
    }
}