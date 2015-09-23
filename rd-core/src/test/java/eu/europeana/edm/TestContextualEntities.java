package eu.europeana.edm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.WriterGraphRIOT;
import org.apache.jena.riot.out.*;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.PrefixMapFactory;

import com.github.jsonldjava.core.Context;
import com.github.jsonldjava.core.JsonLdApi;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.utils.JsonUtils;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

public class TestContextualEntities {

	public static void parseJSONLD(File file, PrintStream ps)
	{
		System.out.println("Parsing file: " + file.getName());

		Model m = ModelFactory.createDefaultModel();
		
		try {
			m.read(new FileInputStream(file), null, "JSONLD");
		}
		catch (Exception e) {
			e.printStackTrace(ps);
			System.out.println();
			return;
		}

		//new Test().test(new OutputStreamWriter(ps), m.getGraph());
		//WriterGraphRIOT r = RDFDataMgr.createGraphWriter(Lang.JSONLD);
		//.write(ps, m.getGraph(), pm, null, null);
		//RDFDataMgr.write(ps, m, Lang.JSONLD);
		m.write(ps, "RDF/XML");
		System.out.println();
	}



	/*
	private static write(Writer w) 
	{
		JsonLdOptions opts = new JsonLdOptions();
		opts.setCompactArrays(true);
		JsonLdApi api = new JsonLdApi(opts);
		JenaRDFParser parser = new JenaRDFParser();
		RDFDataset result = parser.parse(dataset);
		Object obj = api.fromRDF(result);
		final Map<String, Object> localCtx = new HashMap<String, Object>();
		localCtx.put("@context", ctx);
		obj = JsonLdProcessor.compact(obj, localCtx, opts);
		
		JsonUtils.writePrettyPrint(w, obj);
	}
	*/

	public static void main(String[] args)
	{
		File dir = new File("C:\\Users\\mangas\\Google Drive\\Europeana\\EDM\\jsonld\\");

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File f, String s) { return s.endsWith(".jsonld"); }
		};

		TestContextualEntities test = new TestContextualEntities();
		for ( File f : dir.listFiles(filter) ) { test.parseJSONLD(f, System.out); }
	}
}
