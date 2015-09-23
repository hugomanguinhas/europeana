package eu.europeana.tech;

import java.io.File;
import java.io.FileReader;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;

import eu.europeana.edm.TestContextualEntities;

public class TestMetadataJSONLD {

	public static void readTriples(String url)
	{
		Model m = ModelFactory.createDefaultModel();
		m.read(url, null, "JSON-LD");
		m.write(System.out, "RDF/XML");
	}
	
	public static void readQuads(String url)
	{
		DatasetGraph ds = DatasetGraphFactory.createMem();
		RDFDataMgr.read(ds, url, null, Lang.JSONLD);
		RDFDataMgr.write(System.out, ds, Lang.TURTLE);
	}

	public static final void main(String[] args) {
		String file = "D:/work/incoming/tech metadata/DR_4710.jsonld";

		/*
		Model m = ModelFactory.createDefaultModel();
		try {
			m.read(new FileReader(file), null, "JSONLD");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		*/
		TestContextualEntities.parseJSONLD(new File(file), System.out);
	}
}
