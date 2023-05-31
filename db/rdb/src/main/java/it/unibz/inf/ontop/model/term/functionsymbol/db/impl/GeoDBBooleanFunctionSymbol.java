package it.unibz.inf.ontop.model.term.functionsymbol.db.impl;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.ImmutableFunctionalTerm;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.term.functionsymbol.FunctionSymbol;
import it.unibz.inf.ontop.model.type.DBTermType;
import it.unibz.inf.ontop.utils.ImmutableCollectors;


public class GeoDBBooleanFunctionSymbol extends DefaultSQLSimpleDBBooleanFunctionSymbol {

    public GeoDBBooleanFunctionSymbol(String nameInDialect, int arity, DBTermType targetType, DBTermType rootDBTermType) {
        super(nameInDialect, arity, targetType, rootDBTermType);
    }

    @Override
    public ImmutableTerm simplify(ImmutableList<? extends ImmutableTerm> terms, TermFactory termFactory, VariableNullability variableNullability) {
        ImmutableList<ImmutableTerm> simplifiedTerms = terms.stream()
                .map(term -> unwrapSTAsText(term, termFactory))
                .collect(ImmutableCollectors.toList());
        return super.simplify(simplifiedTerms, termFactory, variableNullability);
    }

    //TODO: This is insufficient, it needs to lift even more nested terms e.g. ST_INTERSECTS(ST_BUFFER(?x), ST_ASTEXT(y))
    //TODO: Effectively make the function below recursive

    // if term is ST_ASTEXT(arg), returns arg, otherwise the term itself
    private ImmutableTerm unwrapSTAsText(ImmutableTerm term, TermFactory termFactory) {
        if (term instanceof ImmutableFunctionalTerm) {
            ImmutableFunctionalTerm functionalTerm = (ImmutableFunctionalTerm) term;
            FunctionSymbol functionSymbol = functionalTerm.getFunctionSymbol();
            if (functionSymbol.getName().startsWith("ST_ASTEXT")) {
                ImmutableTerm argument = functionalTerm.getTerm(0);
                return unwrapSTAsText(argument, termFactory);
            }
        }
        return term;
    }
}
