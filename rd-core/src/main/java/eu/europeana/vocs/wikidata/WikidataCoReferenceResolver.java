package eu.europeana.vocs.wikidata;

import static eu.europeana.vocs.coref.CoReferenceUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import eu.europeana.vocs.coref.CoReferenceResolver;

public class WikidataCoReferenceResolver implements CoReferenceResolver
{
	private static String   QUERY = "SELECT ?x WHERE { <#URI#> <#PROPERTY#> ?x }";
	private static String[] EMPTY = new String[] {};

	private String    _sparql  = null;
	private String    _prop    = null;
	private Processor _processor = null;

	public WikidataCoReferenceResolver(String sparql, String prop, Processor processor)
	{
		_prop      = prop;
		_processor = processor;
		_sparql    = sparql;
	}

	public String[] resolve(String uri)
	{
		return execQuery(buildQuery(QUERY, _prop, uri), _processor);
	}

	public void resolve(Map<String,String[]> uris)
	{
		for ( Map.Entry<String, String[]> entry : uris.entrySet() )
		{
			entry.setValue(resolve(entry.getKey()));
		}
	}

	private String[] execQuery(String query, Processor processor)
	{
		QueryEngineHTTP endpoint = new QueryEngineHTTP(_sparql, query);
		try {
			ResultSet rs = endpoint.execSelect();
			if ( !rs.hasNext() ) { return EMPTY; }

			List<String> l = new ArrayList<String>();
	        while (rs.hasNext())
	        {
	            String uri = processor.process(rs.next().get("x"));
	            if ( uri != null ) { l.add(uri); }
	        }
	        return ( l.size() == 0 ? EMPTY : l.toArray(EMPTY) );
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		finally {
			endpoint.close();
		}
		return EMPTY;
	}

	private String buildQuery(String template, String prop, String uri)
	{
		return template.replace("#PROPERTY#", prop).replace("#URI#", uri);
	}

	public static interface Processor
	{
		public String process(RDFNode node);
	}

	public static class ResourceProcessor implements Processor
	{
		public String process(RDFNode node)
		{
			return node.asResource().getURI();
		}
	}

	public static class LiteralProcessor implements Processor
	{
		private String _pattern;

		public LiteralProcessor(String pattern) { _pattern = pattern; }

		public String process(RDFNode node)
		{
			return _pattern.replace("#VALUE#", node.asLiteral().getString());
		}
	}

	public static final void main(String[] args)
	{
		String[] res = WD_2_GN.resolve("http://www.wikidata.org/entity/Q90");
		System.out.println(Arrays.asList(res).toString());
	}
}
