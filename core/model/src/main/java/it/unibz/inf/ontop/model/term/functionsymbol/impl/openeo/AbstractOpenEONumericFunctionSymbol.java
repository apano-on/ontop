package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.term.functionsymbol.impl.ReduciblePositiveAritySPARQLFunctionSymbolImpl;
import it.unibz.inf.ontop.model.type.*;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * GeoSPARQL function must be reducible to DB functions and RDF construction and testing functions
 * It must also not reduce to a DB function before its substitution to extensional node variables
 *
 * Arity {@code >= 1 }
 */
public abstract class AbstractOpenEONumericFunctionSymbol extends ReduciblePositiveAritySPARQLFunctionSymbolImpl {

    private final RDFDatatype xsdDoubleType;

    protected AbstractOpenEONumericFunctionSymbol(
            @Nonnull String functionSymbolName,
            @Nonnull IRI functionIRI,
            ImmutableList<TermType> inputTypes,
            RDFDatatype xsdDoubleType) {
        super(functionSymbolName, functionIRI, inputTypes);
        this.xsdDoubleType = xsdDoubleType;
    }

    @Override
    public Optional<TermTypeInference> inferType(ImmutableList<? extends ImmutableTerm> terms) {
        return Optional.of(TermTypeInference.declareTermType(xsdDoubleType));
    }

    @Override
    protected ImmutableTerm computeLexicalTerm(ImmutableList<ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory, ImmutableTerm returnedTypeTerm) {
        DBTypeFactory dbTypeFactory = termFactory.getTypeFactory().getDBTypeFactory();

        return termFactory.getConversion2RDFLexical(
                dbTypeFactory.getDBStringType(),
                computeDBTerm(subLexicalTerms, typeTerms, termFactory),
                xsdDoubleType);
    }

    protected abstract ImmutableTerm computeDBTerm(ImmutableList<ImmutableTerm> subLexicalTerms,
                                                   ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory);


    @Override
    protected ImmutableTerm computeTypeTerm(ImmutableList<? extends ImmutableTerm> subLexicalTerms, ImmutableList<ImmutableTerm> typeTerms, TermFactory termFactory, VariableNullability variableNullability) {
        return termFactory.getRDFTermTypeConstant(xsdDoubleType);
    }

    @Override
    protected boolean isAlwaysInjectiveInTheAbsenceOfNonInjectiveFunctionalTerms() {
        return false;
    }

    @Override
    public boolean canBePostProcessed(ImmutableList<? extends ImmutableTerm> arguments) {
        return false;
    }

}

