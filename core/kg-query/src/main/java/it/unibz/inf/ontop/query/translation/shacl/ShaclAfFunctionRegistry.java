package it.unibz.inf.ontop.query.translation.shacl;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import org.eclipse.rdf4j.query.algebra.Extension;
import org.eclipse.rdf4j.query.algebra.TupleExpr;
import org.eclipse.rdf4j.query.algebra.ValueExpr;
import org.eclipse.rdf4j.query.algebra.Var;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.rio.*;

import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

/** SHACL-AF function defs: ordered param names + the expression body. */
public final class ShaclAfFunctionRegistry {

    public static final class Def {
        public final String iri;
        public final List<String> params;   // ordered, e.g. ["firstName","lastName"]
        public final ValueExpr bodyExpr;    // expression inside ( ... AS ?result )
        Def(String iri, List<String> params, ValueExpr bodyExpr) {
            this.iri = iri; this.params = params; this.bodyExpr = bodyExpr;
        }
    }

    private final Map<String, Def> defs;

    private ShaclAfFunctionRegistry(Map<String, Def> defs) {
        this.defs = defs;
    }

    public static ShaclAfFunctionRegistry empty() {
        return new ShaclAfFunctionRegistry(Collections.emptyMap());
    }

    public static ShaclAfFunctionRegistry fromTurtle(String shapesTurtle) {
        final Model m;
        try {
            m = Rio.parse(new StringReader(shapesTurtle), "", RDFFormat.TURTLE);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid SHACL-AF Turtle", e);
        }

        Map<String, Def> out = new HashMap<>();

        // find subjects of type sh:SPARQLFunction
        Set<Resource> fnSubjects = m.filter(null, RDF.TYPE, SHACL.SPARQL_FUNCTION).subjects();
        for (Resource fn : fnSubjects) {
            if (!(fn instanceof IRI)) continue;
            IRI fnIri = (IRI) fn;

            // read sh:select (required)
            String select = m.filter(fnIri, SHACL.SELECT, null).objects().stream()
                    .filter(Literal.class::isInstance)
                    .map(Literal.class::cast)
                    .map(Literal::getLabel)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("sh:select missing for " + fnIri));

            // parse and obtain the first projected expression (<expr> AS ?result)
            ValueExpr expr = extractFirstProjectionExpr(select, m);

            // try to read params via sh:parameter + sh:path (ordered by sh:order)
            List<String> paramNames = readParamNamesFromModel(m, fnIri);

            // fallback: if none read from model, derive from variables used in the expr
            if (paramNames.isEmpty()) {
                paramNames = deriveParamNamesFromExpr(expr);
            }

            out.put(fnIri.stringValue(), new Def(fnIri.stringValue(), paramNames, expr));
        }

        return new ShaclAfFunctionRegistry(out);
    }

    public Optional<Def> get(String iri) {
        return Optional.ofNullable(defs.get(iri));
    }

    // ---- helpers ----
    private static ValueExpr extractFirstProjectionExpr(String select, Model model) {
        // Extract prefixes from the model and prepend them to the select string
        StringBuilder prefixBuilder = new StringBuilder();
        if (model != null) {
            Map<String, String> prefixMap = model.getNamespaces().stream()
                    .collect(Collectors.toMap(Namespace::getPrefix, Namespace::getName));
            for (Map.Entry<String, String> entry : prefixMap.entrySet()) {
                if (entry.getKey() != null && !entry.getKey().isEmpty()) {
                    prefixBuilder.append("PREFIX ")
                            .append(entry.getKey()).append(": <")
                            .append(entry.getValue()).append(">\n");
                }
            }
        }
        String selectWithPrefixes = prefixBuilder.toString() + select;
        ParsedQuery pq = new SPARQLParser().parseQuery(selectWithPrefixes, null);
        TupleExpr te = pq.getTupleExpr();

        final ValueExpr[] holder = new ValueExpr[1];
        te.visit(new org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor<RuntimeException>() {
            @Override
            public void meet(Extension ext) {
                if (holder[0] == null && !ext.getElements().isEmpty()) {
                    holder[0] = ext.getElements().get(0).getExpr();
                }
                super.meet(ext);
            }
        });

        if (holder[0] == null)
            throw new IllegalArgumentException("Expected SELECT with projected expression (<expr> AS ?result)");
        return holder[0];
    }

    private static List<String> readParamNamesFromModel(Model m, IRI fnIri) {
        final class Row { final String name; final double order; Row(String n, double o){name=n;order=o;} }
        List<Row> rows = new ArrayList<>();

        for (Statement st : m.filter(fnIri, SHACL.PARAMETER, null)) {
            if (!(st.getObject() instanceof Resource)) continue;
            Resource pn = (Resource) st.getObject();

            Optional<IRI> path = m.filter(pn, SHACL.PATH, null).objects().stream()
                    .filter(IRI.class::isInstance).map(IRI.class::cast).findFirst();
            if (!path.isPresent()) continue;

            double order = m.filter(pn, SHACL.ORDER, null).objects().stream()
                    .filter(Literal.class::isInstance).map(Literal.class::cast)
                    .findFirst()
                    .map(l -> {
                        try { return l.doubleValue(); } catch (Exception e) { return 1e9; }
                    })
                    .orElse(1e9);

            rows.add(new Row(path.get().getLocalName(), order));
        }

        rows.sort(Comparator.<Row>comparingDouble(r -> r.order).thenComparing(r -> r.name));
        return rows.stream().map(r -> r.name).collect(Collectors.toList());
    }

    // fallback when the model doesn't expose sh:parameter entries
    private static List<String> deriveParamNamesFromExpr(ValueExpr expr) {
        LinkedHashSet<String> vars = new LinkedHashSet<>();
        expr.visit(new org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor<RuntimeException>() {
            @Override
            public void meet(Var var) {
                if (!var.hasValue()) vars.add(var.getName());
            }
        });
        return new ArrayList<>(vars);
    }
}