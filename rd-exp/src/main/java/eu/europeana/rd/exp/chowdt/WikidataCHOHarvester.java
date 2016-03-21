/**
 * 
 */
package eu.europeana.rd.exp.chowdt;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import eu.europeana.rd.exp.chowdt.EntrySet.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;


/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 17 Mar 2016
 */
public class WikidataCHOHarvester
{

    private static String QUERY
        = "SELECT ?wdt ?cho WHERE { ?wdt wdt:P727 ?cho }";
    private static String ENDPOINT = "https://query.wikidata.org/sparql";

    private HttpClient _client = new HttpClient();

    public WikidataCHOHarvester()
    {
        
    }

    public EntrySet fetch() { return fetch(new EntrySet()); }

    public EntrySet fetch(EntrySet set)
    {
        QueryEngineHTTP endpoint = new QueryEngineHTTP(ENDPOINT, QUERY);
        try {
            ResultSet rs = endpoint.execSelect();
            while (rs.hasNext())
            {
                QuerySolution qs = rs.next();
                String wdt = qs.getResource("wdt").getURI();
                String cho = qs.getLiteral("cho").getString();
                set.newEntry(cho, wdt);
            }
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally             { endpoint.close();    }

        checkResources(set);

        return set;
    }

    public void fetchToCSV(File file) throws IOException
    {
        fetch().toCSV(file);
    }

    private void checkResources(EntrySet set)
    {
        Collection<String> col = new HashSet();
        for ( Entry entry : set ) { col.add(entry.wdt); }

        for ( String wdt : col )
        {
            if ( !checkResource(wdt) ) { removeResource(wdt, set); }
        }
    }

    private boolean checkResource(String url)
    {
        System.out.println("Checking resource: " + url);

        HeadMethod method = new HeadMethod(url);
        try {
            return ( _client.executeMethod(method) != 404 );
        } catch (Exception e) {
            System.err.println("Error checking resource: " + url
                             + ", reason: " + e.getMessage());
        }
        return false;
    }

    private void removeResource(String url, EntrySet set)
    {
        System.err.println("Removing resource: " + url + "...");
        Iterator<Entry> iter = set.iterator();
        while ( iter.hasNext() )
        {
            if(iter.next().wdt.equals(url)) { iter.remove(); }
        }
    }
}
