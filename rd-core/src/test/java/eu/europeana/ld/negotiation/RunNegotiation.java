/**
 * 
 */
package eu.europeana.ld.negotiation;

import static eu.europeana.ld.negotiation.NegotiationConstants.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 11 Aug 2015
 */
public class RunNegotiation
{
    private static String AGENT = "Content Negotiation Check";

    public static void main(String[] args)
    {
        testLocalNegotiation();
    }

    private static void testMIMO()
    {
        NegotiationConfig cfg = new NegotiationConfig(
                System.out, System.out, "GET", AGENT, HTML
              , HTML, RDFXML, JSONLD, TURTLE, N3, NTRIPLES, TRIG, TRIX);
        new NegotiationValidator(cfg)
            .check("http://www.mimo-db.eu/InstrumentsKeywords/2251"
                 , "http://data.mimo-db.eu/InstrumentsKeywords/2230"
                 , "http://data.mimo-db.eu/");
    }

    private static void testEuropeana()
    {
        NegotiationConfig cfg = new NegotiationConfig(
                System.out, System.out, "GET", AGENT, HTML
              , HTML, RDFXML, JSONLD, TURTLE);
        new NegotiationValidator(cfg)
            .check("http://data.europeana.eu/item/2021401/0E56CF0EB1D62A6489F709C234451CFC403FCEF7");
    }

    private static void testLocalNegotiation()
    {
        NegotiationConfig cfg = new NegotiationConfig(
                System.out, System.out, "GET", AGENT, HTML);
        NegotiationValidator v = new NegotiationValidator(cfg);

        //void
        cfg.setFormats(HTML, TURTLE);
        v.check("http://localhost:8080/");
        v.check("http://localhost:8080/.well-known/void");
        v.check("http://localhost:8080/sparql");

        cfg.setFormats(HTML, RDFXML, JSONLD);
        v.check("http://localhost:8080/item/2021401/0E56CF0EB1D62A6489F709C234451CFC403FCEF7");
        v.check("http://localhost:8080/concept/loc/sh85148236");
    }

}
