package it.unibz.inf.ontop.query.translation.shacl;

import org.eclipse.rdf4j.query.algebra.*;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Replaces FunctionCall(IRI, args) with the expression from SHACL-AF (after substituting params). */
public final class FunctionMacroRewriter extends AbstractQueryModelVisitor<RuntimeException> {

    private final ShaclAfFunctionRegistry registry;

    public FunctionMacroRewriter(ShaclAfFunctionRegistry registry) {
        this.registry = registry;
    }

    public static TupleExpr rewrite(TupleExpr te, ShaclAfFunctionRegistry reg) {
        TupleExpr copy = te.clone();
        copy.visit(new FunctionMacroRewriter(reg));
        return copy;
    }

    @Override
    public void meet(FunctionCall fc) {
        ShaclAfFunctionRegistry.Def def = registry.get(fc.getURI()).orElse(null);
        if (def != null) {
            List<ValueExpr> args = fc.getArgs();
            if (args.size() != def.params.size()) {
                throw new IllegalArgumentException("Arity mismatch for " + def.iri +
                        " expected " + def.params.size() + " got " + args.size());
            }
            Map<String, ValueExpr> subst = new HashMap<>();
            for (int i = 0; i < args.size(); i++) {
                subst.put(def.params.get(i), args.get(i));
            }

            ValueExpr body = def.bodyExpr.clone();
            body.visit(new VarSubstitutor(subst));

            fc.replaceWith(body);
            return; // replacement done
        }
        super.meet(fc);
    }

    /** Replaces Var(name) with provided ValueExpr when name matches a parameter. */
    static final class VarSubstitutor extends AbstractQueryModelVisitor<RuntimeException> {
        private final Map<String, ValueExpr> subst;
        VarSubstitutor(Map<String, ValueExpr> subst) { this.subst = subst; }

        @Override
        public void meet(Var var) {
            ValueExpr r = subst.get(var.getName());
            if (r != null) var.replaceWith(r.clone());
        }
    }
}
