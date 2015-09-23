package eu.europeana.vocs.dbpedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class DBPediaDatasetStat {

	private static String[] EXTENSIONS = { ".ttl.bz2", ".nt.bz2" };

	private HttpClient _client = new HttpClient();

	private Collection<String> _files = new TreeSet(new DatasetComparator());

	public long getSize(String url)
	{
		HeadMethod method = new HeadMethod(url);
		try {
    		int iRet = _client.executeMethod(method);
    		if (iRet != 200) { return 0; }

    		long iLen = getLength(method);
    		return (iLen == 177 ? 0 : iLen);
    	}
    	catch (Exception e) { 
    		e.printStackTrace(); return 0;
    	}
	}

	public long getSumSize(File listing, String base, String lang) throws IOException
	{
		long sum = 0;
		for ( String fn : getListing(listing, base, lang) )
		{
			long   size = 0;
			String url  = null;
			for ( String ext : EXTENSIONS )
			{
				url  = fn + ext;
				size = getSize(url);
				if ( size > 0 ) { break; }
			}
			System.out.println(url + ": " + size);

			if ( size <= 0 ) { continue; }
			_files.add(url);
			sum += size;
		}
		return sum;
	}

	public void printLangs(String base, File listing, File langs) throws IOException
	{
		Map<String,Long> map = new TreeMap();
		for ( String lang : getLangs(langs) )
		{
			long size = getSumSize(listing, base, lang);
			System.out.println("Getting listings for language: " + lang);
			System.out.println("Total size: " + size);
			System.out.println();
			map.put(lang, size);
		}

		System.out.println("Summary");
		for ( String lang : map.keySet() )
		{
			System.out.println(lang + ";" + map.get(lang));
		}
	}

	public void printFiles(PrintStream out)
	{
		for ( String file : _files ) { out.println(file); }
	}

	protected Collection<String> getListing(File listing, String base, String lang) throws IOException
	{
		Collection<String> l = new HashSet();
		BufferedReader reader = new BufferedReader(new FileReader(listing));
		while ( reader.ready() )
		{
			String sLine = reader.readLine();
			if ( lang != null ) { sLine = sLine.replaceAll("\\{country_code\\}", lang); }
			l.add(base + sLine);
		}
		return l;
	}

	protected void printListing(String base, File... listing) throws IOException
	{
		for ( File file : listing )
		{
			System.out.println("Getting listings from: " + file.getName());
			System.out.println("Total size: " + getSumSize(file, base, null));
			System.out.println();
		}
	}

	protected long printListing(String base, String lang, File listing) throws IOException
	{
		long size = getSumSize(listing, base, lang);
		System.out.println("Getting listings for language: " + lang);
		System.out.println("Total size: " + size);
		System.out.println();
		return size;
	}

	protected Collection<String> getLangs(File langs) throws IOException
	{
		Collection<String> l = new HashSet();
		BufferedReader reader = new BufferedReader(new FileReader(langs));
		while ( reader.ready() )
		{
			String sLine = reader.readLine();
			if ( sLine.isEmpty() ) { continue; }
			l.add(sLine);
		}
		return l;
	}

	protected long getLength(HttpMethodBase method)
    {
    	Header header = method.getResponseHeader("Content-Length");
    	return Long.parseLong(header.getValue());
    }

	class DatasetComparator implements Comparator<String> {

		@Override
		public int compare(String s1, String s2)
		{
			int i1 = getRate(s1);
			int i2 = getRate(s2);
			return ( i1 == i2 ? s1.compareTo(s2) : i2 - i1);
		}

		private int getRate(String s)
		{
			if ( s.contains("/2014/en/")    ) { return 2; }
			if ( s.contains("/2014/links/") ) { return 1; }
			else { return 0; }
		}
	}

	public static void main(String[] args) throws IOException
	{
		String base = "http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/";
		File dir = new File("C:/Users/Hugo/Google Drive/Europeana/Semantic Enrichment/target vocs/dbpedia");

		File fLinks = new File(dir, "dataset_base.txt");
		File fEn    = new File(dir, "dataset_en.txt");
		File fLang  = new File(dir, "dataset_lang.txt");
		File fLangs = new File(dir, "langs.txt");

		DBPediaDatasetStat stat = new DBPediaDatasetStat();
		stat.printListing(base, fLinks, fEn);
		stat.printLangs(base, fLang, fLangs);

		System.out.println();
		stat.printFiles(System.out);
	}
}
