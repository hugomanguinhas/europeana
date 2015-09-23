package eu.europeana.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.client.utils.URLEncodedUtils;

import com.github.jsonldjava.utils.JsonUtils;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class RecordAPI {

    private static String API_DEV = "http://www.europeana.eu/api/v2/";

    private String     _apiURL = null;
    private HttpClient _client = new HttpClient();
    private String     _wskey  = "api2demo";


    public RecordAPI() { this(API_DEV); }

    public RecordAPI(String apiURL) { _apiURL = apiURL; }


    public String getRecord(String uri, String mimetype) { return null; }

    public boolean loadRecord(String uri, Model m)
    {
        loadRDF(toURL(uri), m);
        return true;
    }
    

    public Map search(String query) throws IOException
    {
        long time = System.currentTimeMillis();
        System.out.print("Searching: " + query);
        try {
            return asJSON(getQuery(query));
        }
        finally {
            time = System.currentTimeMillis() - time;
            System.out.println(" [" + time + "ms]");
        }
    }
    
    public Map searchByID(String id) throws IOException
    {
        return search("europeana_id:*" + id + "*");
    }


    public int countWhoExactMatch(String str) throws IOException
    {
        Map json = getJSON(getQueryWhoExactMatch(str));
        return (int)json.get("totalResults");
    }

    public int countExactMatch(String property, String str) throws IOException
    {
        Map json = getJSON(getQueryMatch(property, str));
        return (int)json.get("totalResults");
    }

    private String getQuery(String str)
    {
        return _apiURL + "search.json?wskey=api2demo&query=" + encode(str);
    }

    private String getQueryWhoExactMatch(String str)
    {
        String url = _apiURL + "search.json?profile=standard&query=who%3A%22" + str.replaceAll(" ", "+") + "%22&rows=0&wskey=api2demo&start=1";

        return url;
    }

    private String getQueryMatch(String property, String str)
    {
        str = URLEncoder.encode(str);
        String url = _apiURL  + "search.json?profile=standard&query="
                   + property + "%3A%22" + str + "%22&rows=0&wskey=api2demo&start=1";
        System.out.println(url);
        return url;
    }

    private Map getJSON(String url) throws IOException
    {
        Object content = new URL(url).getContent();
        return (Map)JsonUtils.fromInputStream((InputStream)content);
    }


    private void loadRDF(String url, Model m)
    {
        GetMethod method = new GetMethod(url + "?wskey=" + _wskey);
        try {
            int iRet = _client.executeMethod(method);
            if ( iRet != 200 ) { return; }

            m.read(method.getResponseBodyAsStream(), url, "RDF/XML");

        } catch (Exception e) {
            System.err.println("Error retrieving record: " + url
                             + ", reason: " + e.getMessage());
        }
    }

    private Map asJSON(String url)
    {
        GetMethod method = new GetMethod(url);
        try {
            int iRet = _client.executeMethod(method);
            if ( iRet != 200 ) { return null; }

            return (Map)JsonUtils.fromInputStream(method.getResponseBodyAsStream());

        } catch (Exception e) {
            System.err.println("Error retrieving record: " + url
                             + ", reason: " + e.getMessage());
        }
        return null;
    }


    public String toURL(String uri)
    {
        uri = uri.replace("http://data.europeana.eu/item/", "http://europeana.eu/api/v2/record/");
        return uri + ".rdf";
    }

    private String encode(String str)
    {
        return URLEncoder.encode(str);
    }

    public static final void main(String[] args) throws Exception
    {
        //http://www.europeana.eu/portal/search.html?query=edm_place:*geonames*
        //http://www.europeana.eu/portal/search.html?query=skos_concept%3A*dbpedia*
        //int count = new RecordAPI().countMatch("leonardo vinci");
        int count = new RecordAPI().countExactMatch("edm_agent","http://dbpedia.org/resource/Leonardo_da_Vinci");
        
        System.out.println(count);
    }
}
