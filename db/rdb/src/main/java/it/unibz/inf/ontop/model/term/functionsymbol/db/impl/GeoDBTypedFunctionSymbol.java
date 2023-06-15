package it.unibz.inf.ontop.model.term.functionsymbol.db.impl;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.ImmutableFunctionalTerm;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.model.type.DBTermType;
import it.unibz.inf.ontop.utils.ImmutableCollectors;

import java.util.Optional;


public class GeoDBTypedFunctionSymbol extends DefaultSQLSimpleTypedDBFunctionSymbol{
    public GeoDBTypedFunctionSymbol(String nameInDialect, int arity, DBTermType targetType, boolean isInjective,
                                    DBTermType rootDBTermType) {
        super(nameInDialect, arity, targetType, isInjective, rootDBTermType);
    }

    @Override
    public ImmutableTerm simplify(ImmutableList<? extends ImmutableTerm> terms, TermFactory termFactory, VariableNullability variableNullability) {
        ImmutableList<ImmutableTerm> simplifiedTerms = terms.stream()
                .map(this::unwrapSTAsText)
                .map(t -> checkCastCondition(terms) ? castGeography(t, termFactory) : t)
                .collect(ImmutableCollectors.toList());
        return super.simplify(simplifiedTerms, termFactory, variableNullability);
    }

    // if term is ST_ASTEXT(arg), returns arg, otherwise the term itself
    private ImmutableTerm unwrapSTAsText(ImmutableTerm term) {
        if (term instanceof ImmutableFunctionalTerm) {
            ImmutableFunctionalTerm functionalTerm = (ImmutableFunctionalTerm) term;
            if (functionalTerm.getFunctionSymbol().getName().startsWith("ST_ASTEXT")) {
                ImmutableTerm argument = functionalTerm.getTerm(0);
                return unwrapSTAsText(argument);
            }
        }
        return term;
    }

    private boolean checkCastCondition(ImmutableList<? extends ImmutableTerm> terms) {
        // Condition 1: 2 args
        // Condition 2: one arg is buffer
        // Condition 3: one arg is variable
        boolean condition0 = terms.stream().count() == 2;
        boolean condition1 = terms.stream()
                .filter(t -> t instanceof ImmutableFunctionalTerm).map(ImmutableFunctionalTerm.class::cast)
                // the function symbol contains ST_BUFFER, since check applies before ST_ASTEXT unwrappping no sstarts with
                .anyMatch(t -> t.getFunctionSymbol().getName().contains("ST_BUFFER"));

        boolean condition2 = terms.stream()
                .anyMatch(t -> t instanceof Variable);

        return condition0 & condition1 && condition2;
    }

    private ImmutableTerm castGeography(ImmutableTerm term, TermFactory termFactory) {
        return term instanceof Variable
                ? termFactory.getDBSTGeogFromText(term)
                : term;
    }
}

