package it.unibz.inf.ontop.model.term.functionsymbol.db.impl;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.ImmutableFunctionalTerm;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.DBTermType;
import it.unibz.inf.ontop.model.type.DBTypeFactory;
import it.unibz.inf.ontop.utils.ImmutableCollectors;

import java.util.Optional;
import java.util.stream.Stream;


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

        ImmutableTerm temp0 = Optional.of(term)
                // term is a function
                .filter(t -> t instanceof ImmutableFunctionalTerm).map(ImmutableFunctionalTerm.class::cast)
                // the function symbol is ST_ASTEXT
                .filter(t -> t.getFunctionSymbol().getName().startsWith("ST_ASTEXT"))
                // extract the 0-th argument
                .map(t -> t.getTerm(0))
                // otherwise the term itself
                .orElse(term);
        if (temp0.toString().contains("ST_ASTEXT")) {
            Optional<ImmutableTerm> temp1 = Optional.of(temp0)
                    // term is a function
                    .filter(t -> t instanceof ImmutableFunctionalTerm).map(ImmutableFunctionalTerm.class::cast)
                    // there is an ST_ASTEXT defined somewhere
                    .filter(t -> t.toString().startsWith("toGEOGRAPHY") && t.getTerm(0).toString().startsWith("ST_ASTEXT"))
                    // extract the 0-th argument
                    .map(t -> t.getTerm(0))
                    .stream()
                    .map(t2 -> ((ImmutableFunctionalTerm) t2).getTerm(0)).findFirst();
            DBTypeFactory dbTypeFactory = termFactory.getTypeFactory().getDBTypeFactory();
            return (temp1.isEmpty())
                    ? temp0
                    : termFactory.getDBCastFunctionalTerm(dbTypeFactory.getDBGeographyType(), temp1.get());
        } else {
            return temp0;
        }
    }
}
