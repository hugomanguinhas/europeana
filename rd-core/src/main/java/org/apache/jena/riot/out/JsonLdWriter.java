package org.apache.jena.riot.out;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.jena.atlas.iterator.Action;
import org.apache.jena.atlas.iterator.Iter;
import org.apache.jena.riot.out.JenaRDF2JSONLD;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public class JsonLdWriter
{
	private URL           _contextURL = null; 
	private JsonLdOptions _jsonldOpt  = null;

	public JsonLdWriter(URL contextURL)
	{ 
		_contextURL = contextURL;
		_jsonldOpt  = new JsonLdOptions(null);
		_jsonldOpt.useNamespaces = true;
        //_jsonldOpt.setUseRdfType(true);
		_jsonldOpt.setUseNativeTypes(true);
		_jsonldOpt.setCompactArrays(true);
	}


	public void write(Model m, Writer writer) throws IOException { write(m.getGraph(), writer); }

	public void write(Graph g, Writer writer) throws IOException
	{
		DatasetGraph dsg = DatasetGraphFactory.create(g);
		final Map<String, Object> ctx = new LinkedHashMap<String, Object>() ;
        addProperties(ctx, g);

        String context = _contextURL.toString();
		try {
            Object obj = JsonLdProcessor.fromRDF(dsg, _jsonldOpt, new JenaRDF2JSONLD()) ;
            
            Map<String, String> localCtx = Collections.singletonMap("@context", context) ;

            // Unclear as to the way to set better printing.
            Map<String, Object> ret = JsonLdProcessor.compact(obj, localCtx, _jsonldOpt);
            ret.put("@context", context);

            JsonUtils.writePrettyPrint(writer, ret);
            writer.write("\n");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch (JsonLdError e) {
			throw new IOException(e);
		}
	}

    private static void addProperties(final Map<String, Object> ctx, Graph graph)
    {
        // Add some properties directly so it becomes "localname": ....
        final Set<String> dups = new HashSet<>() ;
        Action<Triple> x = new Action<Triple>() {
            @Override
            public void apply(Triple item) {
                Node p = item.getPredicate() ;
                Node o = item.getObject() ;
                if ( p.equals(RDF.type.asNode()) )
                    return ;
                String x = p.getLocalName() ;
                if ( dups.contains(x) )
                    return ;

                if ( ctx.containsKey(x) ) {
                    // Check different URI
                    // pmap2.remove(x) ;
                    // dups.add(x) ;
                } else if ( o.isBlank() || o.isURI() ) {
                    // add property as a property (the object is an IRI)
                    Map<String, Object> x2 = new LinkedHashMap<>() ;
                    x2.put("@id", p.getURI()) ;
                    x2.put("@type", "@id") ;
                    ctx.put(x, x2) ;
                } else if ( o.isLiteral() ) {
                    String literalDatatypeURI = o.getLiteralDatatypeURI() ;
                    if ( literalDatatypeURI != null ) {
                        // add property as a typed attribute (the object is a
                        // typed literal)
                        Map<String, Object> x2 = new LinkedHashMap<>() ;
                        x2.put("@id", p.getURI()) ;
                        x2.put("@type", literalDatatypeURI) ;
                        ctx.put(x, x2) ;
                    } else {
                        // add property as an untyped attribute (the object is
                        // an untyped literal)
                        ctx.put(x, p.getURI()) ;
                    }
                }
            }
        } ;

        Iter.iter(graph.find(null, null, null)).apply(x) ;
    }
}
