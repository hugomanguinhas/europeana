package eu.europeana.vocs.wikidata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.riot.RiotException;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import eu.europeana.data.analysis.ObjectStat;
import eu.europeana.data.analysis.PropDistributionStat;
import eu.europeana.vocs.WikidataComparator;
import eu.europeana.vocs.dbpedia.DBPediaAnalysis;
import eu.europeana.vocs.wikidata.WikidataAnalysis;
import static eu.europeana.vocs.VocsUtils.*;

public class TestWikidataAnalysis
{
    private static Map<String,String> cache = new HashMap();

    private static Map<String,String> map = new HashMap();

    private static Pattern pattern
      = Pattern.compile("http[:][/][/]www[.]wikidata[.]org[/]entity[/]P\\d+(.*)");
      //= Pattern.compile("http[:][/][/]www[.]wikidata[.]org[/]entity[/]P\\d+([-].*)");

    static {
        //map.put("http://schema.org/description", "");
        //map.put("http://www.w3.org/2004/02/skos/core#altLabel", "");

        map.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.wikidata.org/entity/P31");
        map.put("http://www.w3.org/2000/01/rdf-schema#subClassOf", "http://www.wikidata.org/entity/P279");

        cache.put("http://schema.org/description"               , "Description");
        cache.put("http://www.w3.org/2000/01/rdf-schema#label"  , "Label");
        cache.put("http://www.w3.org/2004/02/skos/core#altLabel", "Alternative Label");
        
        
    }

    private static String fixURI(String s)
    {
        Matcher m = pattern.matcher(s);
        if ( m.find() == false ) { return s; };

        int i = m.start(1);
        if ( i < 0 ) { return s; }
        return s.substring(0, i);
    }

    private static Map<String,String> getProperties(PropDistributionStat pstat)
    {
        Map<String,String> ret = new TreeMap(new WikidataComparator());
        for ( Property p : pstat.getProperties() )
        {
            String uri = fixURI(p.getURI());
            ret.put(map.containsKey(uri) ? map.get(uri) : uri, p.getURI());
        }
        return ret;
    }

    private static String fetchPropertyLabel(String uri)
    {
        String sDescribe = buildDESCRIBE(uri);
        System.out.println(sDescribe);

        Map<String,String> labels = new HashMap();
        QueryEngineHTTP endpoint
            = new QueryEngineHTTP(SPARQL_ENDPOINT, sDescribe);
        try {
            Model m = ModelFactory.createDefaultModel();
            endpoint.execDescribe(m);
            String label = "http://www.w3.org/2000/01/rdf-schema#label";
            StmtIterator iter = m.getResource(uri).listProperties();
            while ( iter.hasNext() )
            {
                Statement stmt = iter.next();
                if ( !stmt.getPredicate().getURI().equals(label) ) { continue; }

                RDFNode r = stmt.getObject();
                if ( !r.isLiteral() ) { continue; }

                Literal literal = r.asLiteral();
                String  lang    = literal.getLanguage();
                labels.put(lang == null ? "" : lang, literal.getString());
            }

            if ( labels.isEmpty() ) { return "?"; }

            System.out.println("labels: " + labels);
            String literal = labels.get("en");
            if (literal != null) { return literal; }

            return labels.values().iterator().next();
        }
        catch (RiotException e) {
            System.out.println("Error: " + e.getMessage());
        }
        finally {
            endpoint.close();
        }
        return "?";
    }

    private static String getPropertyLabel(String uri)
    {
        if ( cache.containsKey(uri) ) { return cache.get(uri); }
        String value = fetchPropertyLabel(uri);
        cache.put(uri, value);
        return value;
    }

    private static void storeProps(PropDistributionStat pstat, File prop) 
            throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintStream print = new PrintStream(prop, "UTF-8");
        for ( Map.Entry<String,String> entry : getProperties(pstat).entrySet() )
        {
            print.print(getPropertyLabel(entry.getKey()));
            print.print(",");
            print.print(entry.getKey());
            print.println();
        }
        print.flush();
        print.close();
    }

    public static void main( String[] args ) throws IOException
    {
        SPARQL_ENDPOINT = SPARQL_WIKIDATA;
        WikidataAnalysis analysis = new WikidataAnalysis();

        List<File> files = listFilesWithExtension(DIR_WIKIDATA, ".xml", new ArrayList(20));
        for ( File f : files )
        {
            System.out.println("Processing file: " + f.getName());
            File dir = f.getParentFile();
            String name = getNameWithoutExtension(f);
            File dst1 = new File(dir, name + ".rpt.txt");
            File dst2 = new File(dir, name + ".full.rpt.txt");
            File lst  = new File(dir, name + ".list");
            File prop = new File(dir, name + ".prop.csv");

            if ( lst.exists() ) { analysis.analyse(lst, f, dst1); }

            ObjectStat stat = analysis.analyse(null, f, dst2);

            PropDistributionStat pstat = stat.getPropertyDistributionStat();
            storeProps(pstat, prop);
        }
    }
}
