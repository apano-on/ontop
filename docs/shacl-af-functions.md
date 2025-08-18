# SHACL-AF Functions in Rules TOML — Implementation Notes

**Author:** Nushrat Jahan 

**Branch:** `feature/sparql-function-rules`

---

This implementation provides a lightweight pipeline that lets users declare **`sh:SPARQLFunction`** entries in a TOML “rules” file and then call them from SPARQL as normal functions. At startup, we parse the TOML’s `shapes_graph` (Turtle), register each function, and **rewrite function calls** in the RDF4J algebra into plain SPARQL expressions **before** Ontop translation. No new dependencies; everything targets **Java 11**.

---

## How it works

**Flow:**
TOML (`[shacl_af].shapes_graph` Turtle) → parse to RDF4J Model → build **Function Registry** → visit RDF4J algebra and **inline** calls → pass to Ontop as a normal query.

1. **Load shapes graph from TOML :**
   During test/boot setup, we read `[shacl_af].shapes_graph` (triple-quoted) from a classpath TOML resource and parse it as Turtle.

2. **Build a function registry :**
   We scan the model for subjects of type `sh:SPARQLFunction`. For each function:

    * **Parameters:** read from `sh:parameter / sh:path` and **order** by `sh:order` (0..n).
    * **Body:** parse `sh:select` and extract the single projected expression `(<expr> AS ?result)` as the function body.

3. **Inline function calls :**
   We visit the RDF4J algebra and replace any `FunctionCall(IRI, args)` that matches a registered function with the parsed expression, after substituting formal parameters with the actual arguments.

4. **Hand off to Ontop :**
   After rewrite, the query has no custom functions only built-ins/expressions so Ontop proceeds unchanged.

---

## Files and responsibilities

* **`it.unibz.inf.ontop.query.translation.shacl.ShaclAfRegistryHolder` :**
  Singleton holder. Loads the TOML resource, extracts `shapes_graph` (triple-quoted), and exposes a process-wide registry.

* **`it.unibz.inf.ontop.query.translation.shacl.ShaclAfFunctionRegistry` :**
  Parses Turtle (RDF4J Rio) and builds:

  ```
  function IRI → { ordered params, ValueExpr body }
  ```

    * Parameters come from `sh:parameter / sh:path`, **sorted by `sh:order`**.
    * Body is the expression bound to `?result` in `sh:select`.

* **`it.unibz.inf.ontop.query.translation.shacl.FunctionMacroRewriter` :**
  RDF4J algebra visitor. For a known function IRI, checks **arity**, substitutes parameter variables with the call’s arguments, and replaces the `FunctionCall` node with the function body `ValueExpr`.

* **`it.unibz.inf.ontop.query.translation.impl.RDF4JQueryTranslatorImpl` (edits) :**
  We run the macro rewrite **before** existing Ontop translation:

    * `translateQuery(...)`
    * `translateAskQuery(...)`
    * `translateInsertExpression(...)` (on both WHERE and INSERT parts)

---

## Configuration (TOML)

Keep SHACL functions inside a single triple-quoted Turtle string under `[shacl_af].shapes_graph`. Triple-single quotes (`'''...'''`) are recommended so inner SPARQL triple-double quotes remain intact.

```toml
[shacl_af]
shapes_graph = '''
@prefix :   <http://employee.example.org/voc#> .
@prefix sh:  <http://www.w3.org/ns/shacl#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

:makeEmail a sh:SPARQLFunction ;
  sh:parameter [ sh:path :firstName ; sh:datatype xsd:string ; sh:order 0 ] ;
  sh:parameter [ sh:path :lastName  ; sh:datatype xsd:string ; sh:order 1 ] ;
  sh:message "Generates an email from first and last name." ;
  sh:select """
    SELECT (CONCAT(LCASE(?firstName), ".", LCASE(?lastName), "@company.com") AS ?result)
    WHERE {}
  """ .
'''
```

> **Classpath, not filesystem:** pass a **classpath** resource to the initializer (e.g., `"/rules/employee_functions.toml"`). Don’t use absolute OS paths.

---

### Adding functions to the **same** TOML file

Append another `sh:SPARQLFunction` **inside the existing** `shapes_graph` triple-quoted Turtle block.

**Steps**

1. Open the TOML already loaded at init (e.g., `/rules/employee_functions.toml`).
2. In the existing `shapes_graph = ''' ... '''`, append a new function subject.
3. Use a **unique function IRI** and set `sh:order` on all parameters (`0..n`).
4. End the Turtle subject with a final `.`

**Example (same block)**

```toml
[shacl_af]
shapes_graph = '''
@prefix :   <http://employee.example.org/voc#> .
@prefix sh:  <http://www.w3.org/ns/shacl#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

:makeEmail a sh:SPARQLFunction ;
  sh:parameter [ sh:path :firstName ; sh:datatype xsd:string ; sh:order 0 ] ;
  sh:parameter [ sh:path :lastName  ; sh:datatype xsd:string ; sh:order 1 ] ;
  sh:select """
    SELECT (CONCAT(LCASE(?firstName), ".", LCASE(?lastName), "@company.com") AS ?result)
    WHERE {}
  """ .

:displayName a sh:SPARQLFunction ;
  sh:parameter [ sh:path :firstName ; sh:datatype xsd:string ; sh:order 0 ] ;
  sh:parameter [ sh:path :lastName  ; sh:datatype xsd:string ; sh:order 1 ] ;
  sh:select """
    SELECT (CONCAT(?firstName, " ", ?lastName) AS ?result)
    WHERE {}
  """ .
'''
```

**Do / Don’t (same file)**

* One `[shacl_af]` table and **one** `shapes_graph` key per TOML file
* Unique IRI per function (`:makeEmail`, `:displayName`, …)
* Set `sh:order` 0..n on all parameters
* Don’t add a second `shapes_graph` key (TOML would override the first)
* Don’t redefine the same function IRI twice

---

### Adding functions via a **new** TOML file

If separation is preferred, create another TOML on the **classpath** and initialize with that file.

**Steps**

1. Create `/rules/my_team_functions.toml`.
2. Add one `shapes_graph` block with one or more `sh:SPARQLFunction` subjects.
3. Initialize the registry with this file **once** during setup.

**Example TOML**

```toml
[shacl_af]
shapes_graph = '''
@prefix :   <http://employee.example.org/voc#> .
@prefix sh:  <http://www.w3.org/ns/shacl#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

:initials a sh:SPARQLFunction ;
  sh:parameter [ sh:path :firstName ; sh:datatype xsd:string ; sh:order 0 ] ;
  sh:parameter [ sh:path :lastName  ; sh:datatype xsd:string ; sh:order 1 ] ;
  sh:select """
    SELECT (CONCAT(UCASE(SUBSTR(?firstName,1,1)), ".", UCASE(SUBSTR(?lastName,1,1)), ".") AS ?result)
    WHERE {}
  """ .
'''
```

**Init call**

```java
// Do this once before running queries (classpath resource)
ShaclAfRegistryHolder.initFromTomlClasspath("/rules/my_team_functions.toml");
```

> **Current behavior:** the holder is a simple singleton; **last init wins**.
> Need multiple files at once? Either merge their Turtle into one `shapes_graph`, or extend the holder to accept a list and merge registries.

---

### Quick sanity checklist (TOML)

* [ ] Using a **classpath** TOML (e.g., `/rules/...toml`)
* [ ] Exactly **one** `[shacl_af]` and **one** `shapes_graph` per TOML file
* [ ] Each function ends with a final `.` in Turtle
* [ ] Every parameter has `sh:path` and **`sh:order`** (`0..n`)
* [ ] `sh:select` projects **one** expression `AS ?result`
* [ ] SPARQL call arity matches the number of parameters

---

## Usage (SPARQL)

```sparql
PREFIX : <http://employee.example.org/voc#>

SELECT ?e ?email WHERE {
  ?e :firstName ?fn ; :lastName ?ln .
  BIND(:makeEmail(?fn, ?ln) AS ?email)
}
```

This works in tests and in any query path (including INSERT).

---

## Error handling & diagnostics

* **Invalid shapes graph** → `IllegalArgumentException("Invalid SHACL-AF Turtle", …)`
* **Missing `sh:select`** → `IllegalArgumentException("sh:select missing for <fn>")`
* **Projection shape** (not exactly one `AS ?result`) →
  `IllegalArgumentException("Expected SELECT with projected expression (<expr> AS ?result)")`
* **Arity mismatch** → `IllegalArgumentException("Arity mismatch for <fn> expected N got M")`

These fail early (init or rewrite) with straightforward logs.

---

## Tests

We extended the employee tests with a direct function call:

* `SparqlRuleFunctionEmployeeTest#testMakeEmailFunctionBind`
  Confirms expected emails for the two employees via `:makeEmail`.

Existing rule tests continue to pass. Error messages include expected vs. actual arity for clarity.

---

## Compatibility & performance

* **Compatibility:** Java 11; no new dependencies. If `[shacl_af]` is absent, behavior is unchanged.
* **Performance:** One algebra walk per query/update to inline macros; no runtime cost beyond that.

---

## Limitations & follow-ups

* **Function body scope:** The current implementation treats the function body as a **pure expression** (`SELECT (<expr> AS ?result) WHERE {}`); graph patterns inside the function body are not evaluated. Extending this to support graph patterns would require further development.
* **Typing:** `sh:datatype` is read but not enforced at rewrite time; Ontop’s typing applies. Future work could add explicit casts during substitution. 
* **Initialization:** In the current setup, initialization is done in tests via `@BeforeClass`. For production use, the initialization should be integrated into the standard bootstrap process so that shapes are consistently loaded whenever rules are loaded.


