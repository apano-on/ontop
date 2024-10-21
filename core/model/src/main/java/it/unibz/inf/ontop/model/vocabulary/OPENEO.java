package it.unibz.inf.ontop.model.vocabulary;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.simple.SimpleRDF;

public class OPENEO {
    public static final String PREFIX = "http://www.openeo-ontop.org#";
    public static final IRI AVG;
    public static final IRI MAX;
    public static final IRI MIN;
    public static final IRI AGG;

    static {
        org.apache.commons.rdf.api.RDF factory = new SimpleRDF();
        AVG = factory.createIRI(PREFIX + "avg");
        MAX = factory.createIRI(PREFIX + "max");
        MIN = factory.createIRI(PREFIX + "min");
        AGG = factory.createIRI(PREFIX + "agg");
    }

}
