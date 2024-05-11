package it.unibz.inf.ontop.model.term.functionsymbol.db.impl;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.Constant;
import it.unibz.inf.ontop.model.term.ImmutableFunctionalTerm;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.DBTermType;
import it.unibz.inf.ontop.model.type.TermType;
import it.unibz.inf.ontop.utils.ImmutableCollectors;

import java.util.Optional;

public class GeoAggDBTypedFunctionSymbol extends AbstractDBAggregationFunctionSymbol {

    protected GeoAggDBTypedFunctionSymbol(String nameInDialect, ImmutableList<TermType> expectedBaseTypes, DBTermType targetType,
                                          boolean isDistinct) {
        super(nameInDialect, expectedBaseTypes, targetType, isDistinct,
        Serializers.getRegularSerializer(dropAggSuffix(nameInDialect, "_AGG")));
        // Drop AGG from name in dialect e.g. ST_UNION_AGG -> ST_UNION
    }

    private static String dropAggSuffix(String nameInDialect, String suffix) {
        return (nameInDialect.endsWith(suffix))
                ? nameInDialect.substring(0, nameInDialect.length() - suffix.length())
                : nameInDialect;
    }

    @Override
    public ImmutableTerm simplify(ImmutableList<? extends ImmutableTerm> terms, TermFactory termFactory, VariableNullability variableNullability) {
        ImmutableList<ImmutableTerm> simplifiedTerms = terms.stream().map(this::unwrapSTAsText).collect(ImmutableCollectors.toList());
        return super.simplify(simplifiedTerms, termFactory, variableNullability);
    }

    // if term is ST_ASTEXT(arg), returns arg, otherwise the term itself
    private ImmutableTerm unwrapSTAsText(ImmutableTerm term) {
        return Optional.of(term)
                // term is a function
                .filter(t -> t instanceof ImmutableFunctionalTerm).map(ImmutableFunctionalTerm.class::cast)
                // the function symbol is ST_ASTEXT
                .filter(t -> t.getFunctionSymbol().getName().startsWith("ST_ASTEXT"))
                // extract the 0-th argument
                .map(t -> t.getTerm(0))
                // otherwise the term itself
                .orElse(term);
    }

        @Override
        public Constant evaluateEmptyBag(TermFactory termFactory) {
            return termFactory.getDBStringConstant("");
        }

        @Override
        protected boolean isAlwaysInjectiveInTheAbsenceOfNonInjectiveFunctionalTerms() {
            return false;
        }

        @Override
        protected boolean tolerateNulls() {
            return true;
        }

        @Override
        protected boolean mayReturnNullWithoutNullArguments() {
            return false;
        }
}
