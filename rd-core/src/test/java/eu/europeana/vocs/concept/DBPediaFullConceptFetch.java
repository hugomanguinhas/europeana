package eu.europeana.vocs.concept;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static eu.europeana.vocs.VocsUtils.*;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import eu.europeana.vocs.dbpedia.DBPediaFetch;

public class DBPediaFullConceptFetch {

	public static File SRC = new File(DIR_CONCEPT, "dbpedia.concepts.list.txt");
	public static File DST = new File(DIR_CONCEPT, "dbpedia.concepts.full.xml");

	public Map<String,String> getSameAs(
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
				if ( saTotal.contains(sNewURI) ) { continue; }

				map.put(sNewURI, sURI);
				saTotal.add(sNewURI);
			}

			//get inverse
			m.listStatements(null, pSameAs, r);
			while ( iter.hasNext()  )
			{
				Resource subject = (Resource)iter.next().getSubject();
				String sNewURI = subject.getURI();
				if ( saTotal.contains(sNewURI) ) { continue; }

				map.put(sNewURI, sURI);
				saTotal.add(sNewURI);
			}
		}

		return map;
	}

	public static final void main(String[] args) throws Exception
	{
		new DBPediaFetch(true).fetchAll(SRC, DST);
	}
}
