/**
 * 
 */
package eu.europeana.ld.web;

import org.apache.jena.riot.RDFFormat;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 13 Aug 2015
 */
public enum FormatSupport
{
    RDFXML   ("rdf"   , RDFFormat.RDFXML_PLAIN , "application/rdf+xml")
  , TTL      ("ttl"   , RDFFormat.TURTLE_PRETTY, "text/turtle")
  , N3       ("n3"    , RDFFormat.TURTLE_PRETTY, "text/n3")
  , NTRIPLE  ("nt"    , RDFFormat.NTRIPLES_UTF8, "application/n-triples")
  , NQUAD    ("nq"    , RDFFormat.NQUADS_UTF8  , "application/n-quads")
  , TRIG     ("trig"  , RDFFormat.TRIG_PRETTY  , "application/trig")
  , JSONLD   ("jsonld", RDFFormat.JSONLD_PRETTY, "application/ld+json")
  , RDFTHRIFT("rt"    , RDFFormat.RDF_THRIFT   , "application/rdf+thrift")
  , RDFJSON  ("rj"    , RDFFormat.RDFJSON      , "application/rdf+json")
//, TRIX     ("trix"  , "TriX"                 , "application/trix+xml")
  ;


    private String    _ext;
    private RDFFormat _jenaCode;
    private String    _mimetype;

    private FormatSupport(String ext, RDFFormat jenaCode, String mimetype)
    {
        _ext      = ext;
        _jenaCode = jenaCode;
        _mimetype = mimetype;
    }

    public String    getExtension()  { return _ext;       }
    public RDFFormat getJenaFormat() { return _jenaCode;  }
    public String    getMimetype()   { return _mimetype;  }

    public static FormatSupport getFormat(String extension)
    {
        for ( FormatSupport f : values() )
        {
            if ( f.getExtension().equals(extension) ) { return f; }
        }
        return null;
    }

    public static FormatSupport getFormatByMime(String mime)
    {
        for ( FormatSupport f : values() )
        {
            if ( f.getMimetype().equals(mime) ) { return f; }
        }
        return null;
    }
}