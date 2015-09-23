package eu.europeana.vocs.dbpedia;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import static eu.europeana.vocs.VocsUtils.*;

public class DBPediaFetch {

	private static List<String> PROPERTIES_TO_REMOVE = Arrays.asList( 
			"http://dbpedia.org/ontology/wikiPageWikiLink"
	);

	private static String URI_SAMEAS = "http://www.w3.org/2002/07/owl#sameAs";


	private boolean _sameAs = false;

	public DBPediaFetch(boolean sameAs) { _sameAs = sameAs; }

	public void fetchAll(File src, File dst) throws IOException
	{
		Collection<String> saURIs  = loadDataURLs(src, PATTERN_DBPEDIA);

		Model m = getModel(null);
		loadModel(m, saURIs);

		if ( _sameAs ) { importSameAs(m, saURIs); }

		store(m, dst);
	}

	private void importSameAs(Model m, Collection<String> saURIs)
	{
		Collection<String> saTotal = new HashSet<String>(saURIs);

		while ( true )
		{
			Map<String,String> map = fetchSameAs(m, saURIs, saTotal);
			if ( map.isEmpty() ) { break; }

			for ( String sSrc : map.keySet() )
			{
				if ( !loadModel(m, sSrc) ) { continue; }

				mergeResources(m.getResource(sSrc), m.getResource(map.get(sSrc)));
			}
		}

		normalizeModel(m);
	}

	private boolean isDBPedia(String sURI)
	{
		return PATTERN_DBPEDIA.matcher(sURI).matches();
	}

	private Map<String,String> fetchSameAs(
			Model m, Collection<String> sa, Collection<String> saTotal)
	{
		Property pSameAs = m.getProperty(URI_SAMEAS);

		Map<String,String> map = new HashMap<String,String>();

		for ( String sURI : sa )
		{
			Resource r = m.getResource(sURI);
			
			// get direct sameAs
			StmtIterator iter = r.listProperties(pSameAs);
			while ( iter.hasNext()  )
			{
				Resource obj = (Resource)iter.next().getObject();
				String sNewURI = obj.getURI();
				if ( !isDBPedia(sNewURI) || saTotal.contains(sNewURI) ) { continue; }

				map.put(sNewURI, sURI);
				saTotal.add(sNewURI);
			}

			//get inverse
			m.listStatements(null, pSameAs, r);
			while ( iter.hasNext()  )
			{
				Resource subject = (Resource)iter.next().getSubject();
				String sNewURI = subject.getURI();
				if ( !isDBPedia(sNewURI) || saTotal.contains(sNewURI) ) { continue; }

				map.put(sNewURI, sURI);
				saTotal.add(sNewURI);
			}
		}

		return map;
	}

	private void normalizeModel(Model m)
	{
		StmtIterator iter = m.listStatements();
		while (iter.hasNext())
		{
			Statement stmt = iter.next();
			String sURI = stmt.getPredicate().getURI();
			if ( PROPERTIES_TO_REMOVE.contains(sURI) ) { iter.remove(); }
		}
	}
}
