package it.unibz.inf.ontop.model.term.functionsymbol.impl.geof;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.Constant;
import it.unibz.inf.ontop.model.term.ImmutableFunctionalTerm;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.term.functionsymbol.db.DBFunctionSymbolFactory;
import it.unibz.inf.ontop.model.type.DBTypeFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.model.type.TermTypeInference;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.BiFunction;

public class GeofDefaultAggFunctionSymbolImpl extends AbstractGeofAggregateFunctionSymbolImpl {

    private final RDFDatatype wktLiteralType;
    private final BiFunction<TermFactory, ImmutableTerm, ImmutableTerm> dbTermFct;

    public GeofDefaultAggFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI, RDFDatatype wktLiteralType,
                                          BiFunction<TermFactory, ImmutableTerm, ImmutableTerm> dbTermFct) {
        //TODO: Make the arity arbitrary
        super(functionSymbolName, functionIRI, ImmutableList.of(wktLiteralType), wktLiteralType);
        this.wktLiteralType = wktLiteralType;
        this.dbTermFct = dbTermFct;
    }

    @Override
    public Optional<TermTypeInference> inferType(ImmutableList<? extends ImmutableTerm> terms) {
        return Optional.of(TermTypeInference.declareTermType(wktLiteralType));
    }

    @Override
    public boolean canBePostProcessed(ImmutableList<? extends ImmutableTerm> arguments) {
        return false;
    }

    @Override
    protected ImmutableTerm computeLexicalTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory, ImmutableTerm returnedTypeTerm) {
        DBTypeFactory dbTypeFactory = termFactory.getTypeFactory().getDBTypeFactory();

        return termFactory.getConversion2RDFLexical(
                dbTypeFactory.getDBStringType(),
                computeDBTerm(subLexicalTerms, typeTerms, termFactory),
                wktLiteralType);
    }

    @Override
    protected ImmutableTerm computeTypeTerm(ImmutableList<? extends ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory, VariableNullability variableNullability) {
        return termFactory.getRDFTermTypeConstant(wktLiteralType);
    }

    protected ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory) {
        BiFunction<TermFactory, ImmutableTerm, ImmutableTerm> dbTermFctSTAsText = TermFactory::getDBAsText;
        BiFunction<TermFactory, ImmutableTerm, ImmutableTerm> dbTermFctSTAccum = TermFactory::getDBSTAccum;
        WKTLiteralValue v0 = GeoUtils.extractWKTLiteralValue(termFactory, subLexicalTerms.get(0));
        ImmutableTerm geoTerm = dbTermFctSTAccum.apply(termFactory, v0.getGeometry());
        geoTerm = dbTermFct.apply(termFactory, geoTerm);
        geoTerm = dbTermFctSTAsText.apply(termFactory, geoTerm);
        return geoTerm.simplify();
    }

}
