package eu.europeana.vocs.lexvo;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import static eu.europeana.vocs.VocsUtils.*;

/*
   Methods:
   + Fetched resources through the URI 
   + Fetched all <lvont:translation> for each resource:
     + Imported some properties (e.g. <skosxl:literalForm>) into the "spine" resource
   + Fixed issues in source data

   Known issues:
   + rdf:datatype="xsd:string" and xml:lang together causes problems when parsing
   + Got some connection timeouts (increase HTTP retry handler to 5)
 */

public class LexvoFetch {

	public static String URI_TRANSLATION = "http://lexvo.org/ontology#translation";
	public static String URI_LITERALFORM = "http://www.w3.org/2008/05/skos-xl#literalForm";

	public LexvoFetch()
	{
		RDF_PREPROCESSING = true;
	}

	public void fetchAll(File src, File dst) throws IOException
	{
		Collection<String> saURIs  = loadDataURLs(src, PATTERN_LEXVO);

		Model m = getModel(null);
		loadModel(m, saURIs);

		Property pTranslation = m.getProperty(URI_TRANSLATION);
		Property pLiteralForm = m.getProperty(URI_LITERALFORM);

		for ( String s : saURIs )
		{
			Resource r = m.getResource(s);
			Collection<Resource> trans = getObjectRanges(r, pTranslation);
			fetchResources(m, trans);
			transferProperties(trans, r, pLiteralForm);
		}

		store(m, dst);
	}

	/*
	public Map<String,String> getTranslations(
			Model m, Collection<String> sa)
	{
		Property pTranslation = m.getProperty(URI_TRANSLATION);

		Map<String,String> map = new HashMap<String,String>();

		for ( String sURI : sa )
		{
			Resource r = m.getResource(sURI);
			StmtIterator iter = r.listProperties(pTranslation);
			while ( iter.hasNext() )
			{
				Resource obj = (Resource)iter.next().getObject();
				String sNewURI = obj.getURI();
				if ( saTotal.contains(sNewURI) ) { continue; }

				map.put(sNewURI, sURI);
				saTotal.add(sNewURI);
			}
		}

		return map;
	}
	*/
}
