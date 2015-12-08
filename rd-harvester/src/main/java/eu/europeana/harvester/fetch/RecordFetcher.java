/**
 * 
 */
package eu.europeana.harvester.fetch;

import java.io.OutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;

import static eu.europeana.harvester.utils.HarvesterUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 7 Dec 2015
 */
public class RecordFetcher
{
    private static String API_DEV = "http://www.europeana.eu/api/v2/";

    private String     _apiURL   = null;
    private HttpClient _client   = new HttpClient();
    private String     _wskey    = null;
    private String     _mimetype = null;
    private boolean    _resolve  = false;

    public RecordFetcher(String mimetype, String wskey, boolean resolve)
    {
        _mimetype = mimetype;
        _wskey    = wskey;
        _resolve  = resolve;
    }


    /***************************************************************************
     * Public Methods
     **************************************************************************/

    public String fetch(String url)
    {
        url = resolve(url);

        GetMethod m = new GetMethod(url);
        if ( _mimetype != null ) { m.setRequestHeader("Accept", _mimetype); }
        try {
            int iRet = _client.executeMethod(m);
            if ( iRet != 200 ) { return null; }

            return m.getResponseBodyAsString();

        } catch (Exception e) {
            System.err.println("Error retrieving record: " + url
                             + ", reason: " + e.getMessage());
        }
        return null;
    }

    public boolean fetch(String url, OutputStream out)
    {
        url = resolve(url);

        GetMethod m = new GetMethod(url);
        if ( _mimetype != null ) { m.setRequestHeader("Accept", _mimetype); }
        try {
            int iRet = _client.executeMethod(m);
            if ( iRet != 200 ) { return false; }

            IOUtils.copy(m.getResponseBodyAsStream(), out);
            out.flush();
            return true;

        } catch (Exception e) {
            System.err.println("Error retrieving record: " + url
                             + ", reason: " + e.getMessage());
        }
        return false;
    }


    /***************************************************************************
     * Private Methods
     **************************************************************************/

    private String resolve(String url)
    {
        if ( !_resolve                                ) { return url; }
        if ( !isEuropeanaURI(url)                     ) { return url; }
        if ( !_mimetype.equals("application/rdf+xml") ) { return url; }

        return "http://europeana.eu/api/v2/record/" + getLocalID(url)
             + ".rdf?wskey=" + _wskey;
    }
}
