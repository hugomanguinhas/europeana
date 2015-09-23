package eu.europeana.ld.negotiation;

import org.apache.jena.riot.Lang;

import eu.europeana.ld.negotiation.format.FormatConfig;
import eu.europeana.ld.negotiation.format.RDFValidator;

public class NegotiationConstants
{
    public static enum RETURN { OK, ERROR, WARNING }

    public static FormatConfig HTML
        = new FormatConfig(null, 1, "text/html", "application/xhtml+xml");

    public static FormatConfig RDFXML
        = new FormatConfig(new RDFValidator(Lang.RDFXML.getLabel()), 1 
                         , "application/rdf+xml"
                         , "rdf/xml"
                         , "application/xml"
                         , "text/xml");

    public static FormatConfig JSONLD
        = new FormatConfig(new RDFValidator(Lang.JSONLD.getLabel()), 1
                         , "application/ld+json"
                         , "application/json");

    public static FormatConfig TURTLE
        = new FormatConfig(new RDFValidator(Lang.TURTLE.getLabel()), 1
                         , "text/turtle"
                         , "application/turtle"
                         , "application/x-turtle");

    public static FormatConfig N3
        = new FormatConfig(new RDFValidator(Lang.N3.getLabel()), 1
                         , "text/n3"
                         , "text/rdf+n3"
                         , "application/n3");

    public static FormatConfig NTRIPLES
        = new FormatConfig(new RDFValidator(Lang.NTRIPLES.getLabel()), 1 
                         , "application/n-triples"
                         , "application/ntriples"
                         , "text/nt");

    public static FormatConfig TRIG
        = new FormatConfig(new RDFValidator(Lang.TRIG.getLabel()), 2 
                         , "application/trig"
                         , "application/x-trig");

    public static FormatConfig TRIX
        = new FormatConfig(new RDFValidator("TriX"), 1 
                         , "application/trix");
}
