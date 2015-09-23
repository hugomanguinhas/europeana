package eu.europeana.vocs.eurovoc;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import static eu.europeana.vocs.VocsUtils.*;

/*
Methods:
+ Downloaded eurovoc RDF/XML dump.
+ Selected from the dump the resources in the list

Notes:
+ RDF resource descriptions are split into several declarations.

Known issues:
+ None
*/
public class EurovocFetch {

	public EurovocFetch() {}

	public void fetchAll(File src, File dst) throws IOException
	{
		Collection<String> saURIs  = loadDataURLs(src, PATTERN_EUROVOC);

		Model m = getModel(null);
		loadModelFromCache(m, saURIs, getEuroVocCache());

		store(m, dst);
	}

	private Model getEuroVocCache()
	{
		Model m = ModelFactory.createDefaultModel();
		try {
			m.read(new FileReader(LOCATION_EUROVOC), null, "RDF/XML");
		}
		catch (Exception e) {
			System.err.println("error parsing: " + e.getMessage());
		}
		return m;
	}
}
