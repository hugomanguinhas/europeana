package eu.europeana.vocs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.jena.riot.RiotException;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NsIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import eu.europeana.utils.GlobalUtils;

public class VocsUtils extends GlobalUtils
{
	public static boolean RDF_PREPROCESSING = false;

	public static HttpClient CLIENT = new HttpClient();

	public static File BASEDIR      = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\Entity Collection\\datasets\\");
	public static File DIR_CONCEPT  = new File(BASEDIR, "concepts");
	public static File DIR_EXP      = new File(BASEDIR, "exp");

	public static File DIR_WIKIDATA = new File(BASEDIR, "wikidata");
	
	public static String  LOCATION_EUROVOC = "C:\\Users\\mangas\\Desktop\\Europeana\\Work\\subjects\\enrichment\\Target Vocabularies\\Eurovoc\\eurovoc_skos.rdf";

	public static Pattern PATTERN_BABELNET = Pattern.compile(".*babelnet[.]org.*");
	public static Pattern PATTERN_DBPEDIA  = Pattern.compile(".*dbpedia[.]org.*");
	public static Pattern PATTERN_EUROVOC  = Pattern.compile(".*eurovoc[.]europa[.]eu.*");
	public static Pattern PATTERN_FREEBASE = Pattern.compile(".*freebase[.]com.*");
	public static Pattern PATTERN_LEXVO    = Pattern.compile(".*lexvo[.]org.*");
	public static Pattern PATTERN_WIKIDATA = Pattern.compile(".*wikidata[.]org.*");

	public static String  URI_SAMEAS    = "http://www.w3.org/2002/07/owl#sameAs";
	public static String  SPARQL_ENDPOINT  = "";

	public static String  SPARQL_DBPEDIA_EN = "http://dbpedia.org/sparql";
	public static String  SPARQL_WIKIDATA   = "http://wikisparql.org/sparql";
	//String sparql = "http://milenio.dcc.uchile.cl/sparql";


    public static DefaultHttpMethodRetryHandler RETRY_HANDLER 
    	= new DefaultHttpMethodRetryHandler(5, false);


	public static String getRDF(String url)
	{
		return getRDF(CLIENT, url);
	}

	public static String getRDF(HttpClient client, String url)
	{
		try {
			GetMethod method = new GetMethod(url);
			method.setRequestHeader("Accept", "application/rdf+xml");
			
			HttpMethodParams params = new HttpMethodParams();
	        params.setParameter(HttpMethodParams.RETRY_HANDLER, RETRY_HANDLER);
	        method.setParams(params);

			int iRet = client.executeMethod(method);
			if ( iRet != 200 ) {
				System.err.println("problem fetching: <" + url + ">, response: " + iRet);
				return null;
			}
			String s = method.getResponseBodyAsString();
			System.err.println("fetched: <" + url + ">");

			if ( RDF_PREPROCESSING ) {
				s = s.replaceAll("rdf[:]datatype[=][\"]xsd[:]string[\"]", "");
			}

			return s;
		} catch (Exception e) {
			System.err.println("cannot fetch: <" + url + ">: reason <" + e.getClass().getName() + ">, msg: " + e.getMessage());
		}
		return null;
	}

	public static String buildDESCRIBE(String uri) { return "DESCRIBE <" + uri + ">"; }

	public static Model importNamespaces(Model src, Model dst)
	{
		NsIterator iter = src.listNameSpaces();
		while ( iter.hasNext() )
		{
			String ns = iter.nextNs();
			dst.setNsPrefix(src.getNsURIPrefix(ns), ns);
		}
		return dst;
	}

	public static Model loadModel(File file) { return loadModel(null, file, null); }

	public static Model loadModel(Model m, File file, String sFormat)
	{
		if (sFormat == null) { sFormat = "RDF/XML"; } 

		Model mTemp = ModelFactory.createDefaultModel();
		try {
			mTemp.read(new FileReader(file), null, sFormat);
		}
		catch (Exception e) {
			System.err.println("error parsing: " + file.getName() + ", error: " + e.getMessage());
		}

		if ( m == null ) { m = mTemp; }
		else { m.add(mTemp); }

		return m;
	}

	public static boolean loadModel(Model m, String url)
	{
		String sContent = getRDF(url);
		if ( sContent == null ) { return false; }

		Model mTemp = ModelFactory.createDefaultModel();
		try {
			mTemp.read(new StringReader(sContent), url, "RDF/XML");
		}
		catch (Exception e) {
			System.err.println("error parsing: " + url + ", error: " + e.getMessage());
			return false;
		}

		m.add(mTemp);

		return true;
	}

	public static boolean loadModel(Model m, Collection<String> sa)
	{
		boolean ret = false;
		for (String sURI : sa ) { ret = loadModel(m, sURI) || ret; }
		return ret;
	}

	public static boolean loadModel(Model m, String s, String sFormat)
	{
		Model mTemp = ModelFactory.createDefaultModel();
		try {
			mTemp.read(new StringReader(s), null, sFormat);
		}
		catch (Exception e) {
			System.err.println("error parsing: " + e.getMessage());
			return false;
		}
		m.add(mTemp);
		return true;
	}

	public static void loadModelFromSPARQL(Model m, String uri, boolean filter)
	{
		String sDescribe = buildDESCRIBE(uri);

		System.out.println(sDescribe);
		QueryEngineHTTP endpoint
		    = new QueryEngineHTTP(SPARQL_ENDPOINT, sDescribe);
		try {
			Model mTemp = ModelFactory.createDefaultModel();
			endpoint.execDescribe(mTemp);
			if ( filter ) { filterSubject(mTemp, uri); }
			m.add(mTemp);
		}
		catch (RiotException e) {
			System.out.println("Error: " + e.getMessage());
		}
		finally {
			endpoint.close();
		}
	}

	public static void loadModelFromSPARQL(Model m, Collection<String> sa, boolean filter)
	{
		for ( String uri : sa ) { loadModelFromSPARQL(m, uri, filter); }
	}

	public static boolean loadModelFromCache(Model m, Collection<String> sa, Model mCache)
	{
		for ( String sURI : sa )
		{
			Resource r = mCache.getResource(sURI);
			m.add(r.listProperties());
		}
		return true;
	}

	public static boolean fetchResource(Model m, Resource r)
	{
		return loadModel(m, r.getURI());
	}

	public static boolean fetchResources(Model m, Collection<Resource> c)
	{
		boolean ret = false;
		for (Resource r : c ) { ret = loadModel(m, r.getURI()) || ret; }
		return ret;
	}

	public static void filterSubject(Model m, String uri)
	{
		StmtIterator iter = m.listStatements();
		while ( iter.hasNext() )
		{
			Statement stmt = iter.next();

			String suri = stmt.getSubject().getURI();
			if ( (suri != null) && suri.equals(uri) ) { continue; }

			iter.remove();
		}
	}

	public static void fixLanguage(StmtIterator iter, String sLang)
	{
		if ( (sLang == null) || sLang.trim().isEmpty() ) { return; }

		List<Statement> list = iter.toList();
		for ( Statement stmt : list )
		{
			RDFNode n = stmt.getObject();
			if ( !n.isLiteral() ) { continue; }

			Literal l = n.asLiteral();
			String sL = l.getLanguage();
			if ( (sL != null) && !sL.trim().isEmpty() ) { continue; }

			stmt.changeObject(l.getString(), sLang);
		}
	}

	public static Model getModel(File f)
	{
		Model m = ModelFactory.createDefaultModel();
		if ((f == null) || !f.exists()) { return m; }

		try {
			FileInputStream fis = new FileInputStream(f);
			try {
				 m.read(fis, "RDF/XML");
			}
			finally { fis.close(); }
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return m;
	}

	public static Collection<Resource> getObjectRanges(Resource r, Property p)
	{
		Collection<Resource> ret = new HashSet<Resource>();
		StmtIterator iter = r.listProperties(p);
		while ( iter.hasNext() )
		{
			RDFNode node = iter.next().getObject();
			if ( node instanceof Resource ) { ret.add((Resource)node); }
		}
		return ret;
	}

	public static void importAll(Resource r, Property prop, boolean isRemove)
	{
		Model m = r.getModel();
		StmtIterator iter = r.listProperties(prop);
		while ( iter.hasNext() )
		{
			Resource obj = (Resource)iter.next().getObject();
			loadModel(m, obj.getURI());
			//transferProperties(src, trg);
		}
	}

	public static void importSome(Resource src, Property prop, Property... pSel)
	{
		Collection<Resource> ra = new HashSet<Resource>();
		StmtIterator iter = src.listProperties(prop);
		while ( iter.hasNext() )
		{
			ra.add((Resource)iter.next().getObject());
		}

		Model m = src.getModel();
		for ( Resource r : ra )
		{
			loadModel(m, r.getURI());
			transferProperty(r, src, pSel);
		}
	}

	public static Collection<String> loadDataURLs(File f, Pattern p)
	{
		Collection<String> s = new TreeSet<String>();
		try {
			BufferedReader r = null;
			try {
				r = new BufferedReader(new FileReader(f));
		
				String sLine;
				while ( (sLine = r.readLine()) != null )
				{
					sLine = sLine.trim();
					if ( sLine.isEmpty() || sLine.startsWith("#") ) { continue; }
					if ( (p != null) && !p.matcher(sLine).matches() ) { continue; }
		
					s.add(sLine);
				}
			}
			finally {
				if (r != null ) { r.close(); }
			}
		}
		catch (IOException e) {}

		return s;
	}

	public static void normalizeModel(Model m)
	{
		StmtIterator iter = m.listStatements();
		while (iter.hasNext())
		{
			Statement stmt = iter.next();
			try { new URI(stmt.getSubject().getURI()); }
			catch (URISyntaxException e) { iter.remove(); }
		}
	}

	public static void store(Model model, File dest) throws IOException
	{
        store(model, dest, "RDF/XML");
	}

	public static void store(Model model, OutputStream out) throws IOException
	{
        store(model, out, "RDF/XML");
	}

	public static void store(Model model, OutputStream out, String sFormat) throws IOException
	{
        try {
        	RDFWriter w = model.getWriter(sFormat);
        	w.setProperty("allowBadURIs", "true");
        	w.write(model, out, null);
        	out.flush();
        }
        finally {
        	out.close();
        }
	}

	public static void store(Model model, File dest, String sFormat) throws IOException
	{
        FileOutputStream out = new FileOutputStream(dest);
        try {
        	RDFWriter w = model.getWriter(sFormat);
        	w.setProperty("allowBadURIs", "true");
        	w.write(model, out, null);
        	out.flush();
        }
        finally {
        	out.close();
        }
	}

	public static void transferProperty(Resource src, Resource trg, Property... pSel)
	{
		for ( Property p : pSel )
		{
			StmtIterator iter = src.listProperties(p);
			while ( iter.hasNext() )
			{
				trg.addProperty(p, iter.nextStatement().getObject());
			}
		}
	}

	public static void transferProperties(Collection<Resource> srcs, Resource trg, Property... pSel)
	{
		for ( Resource r : srcs ) { transferProperty(r, trg, pSel); }
	}

	public static void mergeResources(Resource src, Resource trg)
	{
		Property sameAs = src.getModel().getProperty(URI_SAMEAS);

		StmtIterator iter = src.listProperties();
		while ( iter.hasNext() )
		{
			Statement stmt = iter.nextStatement();

			Property p = stmt.getPredicate();
			if ( p.equals(sameAs) ) { continue; }

			trg.addProperty(p, stmt.getObject());
			iter.remove();
		}
	}

	/***********************************************************************/
	/* File Utils                                                          */
	/***********************************************************************/

	public static List<File> listFilesWithExtension(
			File file, String extension, List<File> list)
	{
		if ( list == null ) { list = new ArrayList<File>(); }

		if ( file.isFile() ) {
			if ( file.getName().endsWith(extension) ) { list.add(file); }
			return list;
		}

		for ( File f : file.listFiles() )
		{
			listFilesWithExtension(f, extension, list);
		}
		return list;
	}

	public static String getExtension(File file)
	{
		String name = file.getName();
		int i = name.lastIndexOf('.');
		return ( i < 0 ? "" : name.substring(i+1) );
	}

	public static String getNameWithoutExtension(File file)
	{
		String name = file.getName();
		int i = name.lastIndexOf('.');
		return ( i < 0 ? name : name.substring(0, i) );
	}

	public static String getContent(File f)
	{
		StringBuffer buffer = new StringBuffer();

		try {
			FileReader r    = new FileReader(f);
			char[]     cbuf = new char[1024];
			try {
				while ( true )
				{
					int i = r.read(cbuf);
					if ( i <= 0 ) { break; }
					buffer.append(cbuf, 0, i);
				}
			}
			finally {
				r.close();
			}
		}
		catch (IOException e) {}

		return buffer.toString();
	}
}
