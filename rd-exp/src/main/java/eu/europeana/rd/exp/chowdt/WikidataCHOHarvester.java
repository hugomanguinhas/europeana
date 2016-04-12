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
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import eu.europeana.rd.exp.chowdt.EntrySet.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;


/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 17 Mar 2016
 */
public class WikidataCHOHarvester implements WikidataCHOExpConstants
{

    private static String QUERY
        = "SELECT ?wdt ?cho WHERE { ?wdt wdt:P727 ?cho }";

    private Properties _cfg    = null;

    public WikidataCHOHarvester(Properties cfg) { _cfg = cfg; }

    public EntrySet fetch() { return fetch(new EntrySet()); }

    public EntrySet fetch(EntrySet set)
    {
        String endpoint = _cfg.getProperty(KEY_SPARQL);
        if ( endpoint == null ) { return set; }

        QueryEngineHTTP engine   = new QueryEngineHTTP(endpoint, QUERY);
        try {
            ResultSet rs = engine.execSelect();
            while (rs.hasNext())
            {
                QuerySolution qs = rs.next();
                String wdt = qs.getResource("wdt").getURI();
                String cho = qs.getLiteral("cho").getString();
                cho = "http://data.europeana.eu/item/" + cho;
                set.newEntry(cho, wdt);
            }
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally             { engine.close();      }

        return set;
    }
}
