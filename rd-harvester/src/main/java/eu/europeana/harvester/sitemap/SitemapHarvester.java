/**
 * 
 */
package eu.europeana.harvester.sitemap;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import eu.europeana.harvester.InterruptHarvest;
import static java.util.Collections.*;
import static org.apache.commons.io.IOUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 7 Dec 2015
 */
public class SitemapHarvester
{
    private static String BASE = "http://www.europeana.eu/portal/europeana-sitemap-index-hashed.xml";
    private static int    SITEMAPSIZE = 650000;

    private String        _base   = null;
    private HttpClient    _client = null;
    private SitemapReader _reader = null;
    private long          _count  = 0;
    private long          _limit  = 0;

    public SitemapHarvester(String base, long limit)
    {
        _base   = base;
        _client = new HttpClient();
        _reader = new SitemapReader();
        _limit  = limit;
    }

    public SitemapHarvester() { this(BASE, 0); }


    /***************************************************************************
     * Public Methods
     **************************************************************************/

    public long harvest(Callback cb)
    {
        _count = 0;
        try {
            harvest(_base, cb);
        }
        catch (InterruptHarvest e) {}
        return _count;
    }


    /***************************************************************************
     * Private Methods
     **************************************************************************/

    private void harvest(String sitemapURL, Callback cb)
    {
        for ( String url : fetchURLs(sitemapURL) )
        {
            if ( isRecord(url) ) { newRecord(cb, url); continue; }

            harvest(url, cb);
        }
    }

    private boolean isRecord(String url) { return !url.contains("-sitemap-"); }

    private List<String> fetchURLs(String url)
    {
        System.out.print("fetching: "+ url);
        long time = System.currentTimeMillis();

        GetMethod m = new GetMethod(url);

        int code;
        try {
            try {
                code = _client.executeMethod(m);
                long elapsed = System.currentTimeMillis() - time;
                System.out.println(" in " + elapsed + "ms");
            }
            catch (Exception e) { e.printStackTrace(); return EMPTY_LIST; }

            if ( code != 200 ) { newUnexpected(code); return EMPTY_LIST; }

            return _reader.read(m);
        }
        finally { m.releaseConnection(); }
    }

    private void newRecord(Callback cb, String url)
    {
        if ( _limit > 0 && _count >= _limit ) { throw new InterruptHarvest(); }
        if ( cb != null ) { cb.newRecordURL(url); _count++; }
    }

    private void newUnexpected(int code)
    {
        System.err.println("Unexpected response code: " + code);
    }


    /***************************************************************************
     * Public Callback Classes
     **************************************************************************/

    public static interface Callback
    {
        public void newRecordURL(String url);
    }

    public static class PrintStreamCallback implements Callback
    {
        private PrintStream _ps;

        public PrintStreamCallback(PrintStream ps) { _ps = ps; }

        @Override
        public void newRecordURL(String url) { _ps.println(url); }
    }

    public static class URL2URNCallback implements Callback
    {
        private static String PREFIX = "http://www.europeana.eu/portal/record/";
        private static String SUFFIX = ".html";

        private Callback _cb;

        public URL2URNCallback(Callback cb) { _cb = cb; }

        @Override
        public void newRecordURL(String url)
        {
            if ( !url.startsWith(PREFIX) || !url.endsWith(SUFFIX) )
            {
                System.err.println("Unknown URL: " + url); return;
            }
            String id = url.substring(PREFIX.length()
                                    , url.length() - SUFFIX.length());
            String urn = "http://data.europeana.eu/item/" + id;
            _cb.newRecordURL(urn);
        }
    }


    /***************************************************************************
     * Internal Classes
     **************************************************************************/

    private class SitemapReader extends DefaultHandler
    {
        private boolean       _within = false;
        private StringBuilder _builder;
        private List<String>  _list;
        private XMLReader     _reader;

        public SitemapReader()
        {
            _builder = new StringBuilder(1024);
            _reader  = getReader();
        }


        /***********************************************************************
         * DefaultHandler Public Methods
         **********************************************************************/

        public List<String> read(HttpMethod method)
        {
            InputStream is = null;
            try {
                is = method.getResponseBodyAsStream();

                _list = new ArrayList(SITEMAPSIZE);
                _reader.parse(new InputSource(is));
                return _list;
            }
            catch (Throwable t) { t.printStackTrace(); return EMPTY_LIST; }
            finally { closeQuietly(is); _list = null; }
        }


        /***********************************************************************
         * DefaultHandler Public Overridden Methods
         **********************************************************************/

        public void startElement(String uri, String localName,
                                 String qName, Attributes attributes)
            throws SAXException
        {
            _within = qName.equals("loc");
        }

        public void endElement(String uri, String localName, String qName)
            throws SAXException
        {
            if ( _within ) { newEntry(); _within = false; }
        }

        public void characters (char ch[], int start, int length)
            throws SAXException
        {
            if ( _within ) { _builder.append(ch, start, length); }
        }


        /***********************************************************************
         * DefaultHandler Private Methods
         **********************************************************************/

        private void newEntry()
        {
            String url = _builder.toString();
            url = url.trim();
            if( !url.isEmpty() ) { _list.add(url); }
            _builder.setLength(0);
        }

        private XMLReader getReader()
        {
            try {
                XMLReader reader = SAXParserFactory.newInstance().newSAXParser()
                                                   .getXMLReader();
                reader.setContentHandler(this);
                return reader;
            }
            catch (SAXException | ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
