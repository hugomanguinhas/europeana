package eu.europeana.edm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
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

public class TestEntityContext {

	public static void main(String[] args) throws IOException
	{
		File file = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\EDM\\jsonld\\agent.xml");

		Model m = ModelFactory.createDefaultModel();
		
		try {
			m.read(new FileInputStream(file), null, "RDF/XML");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			return;
		}

		URL url = new URL("file:///C:/Users/Hugo/Google%20Drive/Europeana/EDM/jsonld/context.jsonld");
		new JsonLdWriter(url).write(m, new OutputStreamWriter(System.out));
	}
}
