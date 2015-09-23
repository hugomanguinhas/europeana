/**
 * 
 */
package eu.europeana.skos;

import java.net.URLEncoder;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import static eu.europeana.edm.EDMNamespace.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 10 Sep 2015
 */
public class SKOSExtractor
{
    private static String[] PROPERTIES = { DC_SUBJECT };

    private String _base;

    public SKOSExtractor(String base) { _base = base; }

    public void extract(Model src, Model trg)
    {
        trg.setNsPrefix("skos", SKOS_NS);
        Property pType = trg.createProperty(RDF_TYPE);
        Property pPLbl = trg.createProperty(SKOS_PREF_LABEL);
        Property pNote = trg.createProperty(SKOS_NOTE);
        Property pInS  = trg.createProperty(SKOS_IN_SCHEME);

        Resource rCpt  = trg.createResource(SKOS_CONCEPT);
        Resource rSch  = trg.createResource(SKOS_CONCEPT_SCHEME);

        Resource scheme = trg.createResource(_base + "ConceptScheme");
        scheme.addProperty(pType, rSch);

        for ( String pName : PROPERTIES )
        {
            Property p = src.getProperty(pName);
            StmtIterator iter = src.listStatements(null, p, (RDFNode)null);
            while ( iter.hasNext() )
            {
                Statement stmt = iter.nextStatement();
                RDFNode   node = stmt.getObject();
                if ( node.isResource() ) { continue; }

                Literal  l    = node.asLiteral();
                Resource rsrc = trg.createResource(createURI(l));
                rsrc.addProperty(pType, rCpt);
                rsrc.addProperty(pPLbl, l.getString(), l.getLanguage());
              //rsrc.addProperty(pNote, stmt.getSubject().getURI());
                rsrc.addProperty(pNote, stmt.getSubject());
                rsrc.addProperty(pInS , scheme);
            }
        }
    }

    private String createURI(Literal l)
    {
        return _base + URLEncoder.encode(l.getString());
    }
}