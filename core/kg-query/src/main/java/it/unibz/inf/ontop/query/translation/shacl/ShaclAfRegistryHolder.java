package it.unibz.inf.ontop.query.translation.shacl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class ShaclAfRegistryHolder {
    private static volatile ShaclAfFunctionRegistry INSTANCE = ShaclAfFunctionRegistry.empty();
    private ShaclAfRegistryHolder() {}

    public static void initFromTomlClasspath(String tomlResourcePath) {
        String content = readResourceAsString(tomlResourcePath);
        String shapes = extractTripleQuoted(content, "shacl_af", "shapes_graph");
        if (shapes != null && !shapes.isBlank()) {
            INSTANCE = ShaclAfFunctionRegistry.fromTurtle(shapes);
        } else {
            INSTANCE = ShaclAfFunctionRegistry.empty();
        }
    }

    public static ShaclAfFunctionRegistry get() { return INSTANCE; }

    private static String readResourceAsString(String path) {
        try (InputStream in = ShaclAfRegistryHolder.class.getResourceAsStream(path)) {
            if (in == null) return "";
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    // looks for [table] ... key = """..."""  or  key = '''...'''
    private static String extractTripleQuoted(String toml, String table, String key) {
        int t = toml.indexOf("[" + table + "]");
        if (t < 0) return null;
        int k = toml.indexOf(key, t);
        if (k < 0) return null;
        int eq = toml.indexOf("=", k);
        if (eq < 0) return null;

        int startD = toml.indexOf("\"\"\"", eq);
        int startS = toml.indexOf("'''",  eq);

        boolean useSingle = (startS >= 0) && (startD < 0 || startS < startD);
        if (useSingle) {
            int start = startS + 3;
            int end = toml.indexOf("'''", start);
            return (end < 0) ? null : toml.substring(start, end);
        } else if (startD >= 0) {
            int start = startD + 3;
            int end = toml.indexOf("\"\"\"", start);
            return (end < 0) ? null : toml.substring(start, end);
        }
        return null;
    }
}