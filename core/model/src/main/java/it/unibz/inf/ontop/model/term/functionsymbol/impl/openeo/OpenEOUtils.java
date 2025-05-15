package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.ImmutableFunctionalTerm;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.functionsymbol.impl.geof.GeoUtils;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenEOUtils {
    protected static String getSRID(String sridIRIString) {
        final String CRS_PREFIX = "http://www.opengis.net/def/crs/OGC/1.3/CRS";
        final String EPSG_PREFIX = "http://www.opengis.net/def/crs/EPSG/0/";

        // For CRS projections only support CRS84
        if (sridIRIString.startsWith(CRS_PREFIX)) {
            if (sridIRIString.substring(CRS_PREFIX.length()).equals("/84")) {
                return "4326";
            } else {
                throw new IllegalArgumentException("Unsupported SRID IRI: " + sridIRIString);
            }
        } else if (sridIRIString.startsWith(EPSG_PREFIX) && isValidSRID(sridIRIString)) {
            return sridIRIString.substring(EPSG_PREFIX.length());
        }

        throw new IllegalArgumentException("Invalid SRID IRI: " + sridIRIString);
    }

    protected static boolean isValidSRID(String srid) {
        CRSFactory factory = new CRSFactory();
        try {
            CoordinateReferenceSystem crs = factory.createFromName(GeoUtils.toProj4jName(srid));
            return crs != null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown SRID: " + srid);
        }
    }

    protected static ImmutableList<? extends ImmutableTerm> getOpenEOBaseTerms(ImmutableList<? extends ImmutableTerm> newTerms) {
        if(((ImmutableFunctionalTerm) newTerms.get(0)).getFunctionSymbol().getName().equals("ONTOP_OPENEO_BASE")
        || ((ImmutableFunctionalTerm) newTerms.get(0)).getFunctionSymbol().getName().equals("OPENEO_PROCESS_GRAPH")) {
            return ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms();
        } else {
            ImmutableTerm firstTerm = ((ImmutableFunctionalTerm) newTerms.get(0)).getTerms().get(0);
            return getOpenEOBaseTerms(((ImmutableFunctionalTerm) firstTerm).getTerms());
        }
    }

    protected static ImmutableList<? extends ImmutableTerm> getOpenEONonComparisonBaseTerms(ImmutableList<? extends ImmutableTerm> newTerms) {
        // Drop any constants, filter them out
        ImmutableTerm myTerm = newTerms.stream().filter(t -> t instanceof ImmutableFunctionalTerm).findFirst().get();
        if (((ImmutableFunctionalTerm) myTerm).getFunctionSymbol().getName().equals("ONTOP_OPENEO_BASE")
                || ((ImmutableFunctionalTerm) myTerm).getFunctionSymbol().getName().equals("OPENEO_PROCESS_GRAPH")) {
            return ((ImmutableFunctionalTerm) myTerm).getTerms();
        } else {
            ImmutableTerm firstTerm = ((ImmutableFunctionalTerm) myTerm).getTerms().stream().filter(t -> t instanceof ImmutableFunctionalTerm).findFirst().get();
            return firstTerm.toString().startsWith("OPENEO_PROCESS_GRAPH")
                    ? getOpenEONonComparisonBaseTerms(ImmutableList.of(firstTerm))
                    : getOpenEONonComparisonBaseTerms(ImmutableList.of(((ImmutableFunctionalTerm) firstTerm).getTerms().get(0)));
        }
    }

    protected static String getNonComparisonFunctionSymbol(ImmutableFunctionalTerm term) {
        if (term.getFunctionSymbol().getName().equals("SP_ADD")) {
            return "add";
        } else if (term.getFunctionSymbol().getName().equals("SP_SUBSTRACT")) {
            return "subtract";
        } else if (term.getFunctionSymbol().getName().equals("SP_MULTIPLY")) {
            return "multiply";
        } else if (term.getFunctionSymbol().getName().equals("SP_DIVIDE")) {
            return "divide";
        } else {
            throw new IllegalArgumentException("Unsupported comparison operator: " + term.getFunctionSymbol().getName());
        }
    }

    protected static String getComparisonFunctionSymbol(ImmutableFunctionalTerm term) {
        if (term.getFunctionSymbol().getName().equals("SP_LT")) {
            return "lt";
        } else if (term.getFunctionSymbol().getName().equals("SP_GT")) {
            return "gt";
        } else if (term.getFunctionSymbol().getName().equals("SP_EQ") || term.getFunctionSymbol().getName().equals("SP_NON_STRICT_EQ")) {
            return "eq";
        } else if (term.getFunctionSymbol().getName().equals("SP_NOT")) {
            ImmutableTerm subTerm = term.getTerms().get(0);
            String secondOperator = ((ImmutableFunctionalTerm) subTerm).getFunctionSymbol().getName();
            if (secondOperator.equals("SP_LT")) {
                return "gte";
            } else if (secondOperator.equals("SP_GT")) {
                return "lte";
            } else if (secondOperator.equals("SP_EQ") || secondOperator.equals("SP_NON_STRICT_EQ")) {
                return "neq";
            }
        } else {
            throw new IllegalArgumentException("Unsupported comparison operator: " + term.getFunctionSymbol().getName());
        }
        return null;
    }

    protected static String convertSymbolsRegex(String input) {
        // More robust approach using regular expressions to handle potential spacing issues
        Map<String, String> symbolMap = new HashMap<>();
        symbolMap.put(">=", "gte");
        symbolMap.put("<=", "lte");
        symbolMap.put(">", "gt");
        symbolMap.put("<", "lt");
        symbolMap.put("=", "eq");

        for (Map.Entry<String, String> entry : symbolMap.entrySet()) {
            String symbol = entry.getKey();
            String replacement = entry.getValue();

            // Escape special regex characters in the symbol
            String escapedSymbol = symbol.replaceAll("([\\.\\+\\*\\[\\]\\(\\)\\{\\}\\\\|\\^\\$])", "\\\\$1");

            // Use regex replaceAll to handle potential variations in spacing.
            input = input.replaceAll("\\s*" + escapedSymbol + "\\s*", replacement);
        }
        // Extract only the converted symbols
        Pattern pattern = Pattern.compile("gte|lte|gt|lt|eq");
        Matcher matcher = pattern.matcher(input);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            result.append(matcher.group());
        }
        return result.toString();
    }

    public static ImmutableList<String> splitTerm(String term) {
        // Regular expression to match the term and value, handling variations in operators and spacing.
        // It captures the term name (group 1) and the value (group 2).
        // The regex supports >=, <=, >, <, and =.  It also handles potential whitespace.

        // More robust regex:
        //String regex = "(\\w+)\\s*(>=|<=|>|<|=)\\s*([\\w.-]+)"; // Allows word characters, dots and minus signs in value.
        // Allow for colon like in eo:cloud_cover
        String regex = "([\\w:]+)\\s*(>=|<=|>|<|=)\\s*([\\w.-]+)"; // Allows colons in term name.
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(term);

        if (matcher.matches()) {
            String termName = matcher.group(1).trim(); // Trim whitespace
            String value = matcher.group(3).trim();     // Trim whitespace
            return ImmutableList.of(termName, value);
        } else {
            // Handle cases where the term doesn't match the expected format.
            System.err.println("Invalid term format: " + term);
            return null; // Or throw an exception, or return an empty array, depending on how you want to handle errors.
        }
    }
}
