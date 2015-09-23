package eu.europeana.vocs.wikidata;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import static eu.europeana.vocs.VocsUtils.*;

public class WikidataFetch {

	public WikidataFetch(String endpoint)
	{
		SPARQL_ENDPOINT = endpoint;
	}

	public void fetchAll(String query, String varName, File dst) throws IOException
	{
		Model m = getModel(null);

		Collection<String> saURIs = getResources(query, varName);
		loadModelFromSPARQL(m, saURIs, true);

		store(m, dst);
	}

	private Collection<String> getResources(String query, String varName)
	{
		Collection<String> ret = new HashSet<String>();
		QueryEngineHTTP endpoint = new QueryEngineHTTP(SPARQL_ENDPOINT, query);
		try {
			ResultSet rs = endpoint.execSelect();
	        while (rs.hasNext()) {
	            QuerySolution qs = rs.next();
	            ret.add(qs.getResource(varName).getURI());
	        }
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		finally {
			endpoint.close();
		}
		return ret;
	}
}
