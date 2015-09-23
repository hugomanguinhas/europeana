package eu.europeana.sparql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.europeana.ld.sparql.QueryTestReader;
import eu.europeana.ld.sparql.SPARQLEndpointTester;
import eu.europeana.ld.sparql.TestQuery;
import eu.europeana.ld.sparql.TestQueryConfig;

public class TestSPARQLendpoint {

	public static final void main(String[] args) throws Exception
	{
		File dir  = new File("C:\\Users\\mangas\\Google Drive\\Europeana\\Linked Data\\");
		File file = new File(dir, "test.queries.xml");
		File log  = new File(dir, "test.queries.log");
		File rpt  = new File(dir, "test.queries.rpt.txt");
		

		Map<String,TestQuery> queries = new TreeMap<String,TestQuery>();
		new QueryTestReader().parse(new InputSource(new FileInputStream(file)), queries);
		SPARQLEndpointTester tester = new SPARQLEndpointTester(
				new TestQueryConfig("http://europeana-test.ontotext.com/sparql"
						          , System.out
						          , System.out//new PrintStream(rpt)
						          , TestQueryConfig.Type.PLAIN));
		tester.run(queries);
	}
}
