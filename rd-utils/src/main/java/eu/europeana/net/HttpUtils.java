/**
 * 
 */
package eu.europeana.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;

import static org.apache.commons.io.IOUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 13 Aug 2015
 */
public class HttpUtils
{
    public static String     DEFAULT_ENCODING  = "UTF-8";
    public static String     DEFAULT_USERAGENT = "Europeana RnD";
    public static HttpClient DEFAULT_CLIENT    = null;

    public static String ACCEPT            = "Accept";
    public static String CONTENT_TYPE      = "Content-Type";
    public static String CONTENT_ENCODING  = "Content-Encoding";
    public static String CONTENT_LENGTH    = "Content-Length";
    public static String USER_AGENT        = "User-Agent";
    public static String ACCEPT_ENCODING   = "Accept-Encoding";

    public static String DEFINE_USERAGENT  = DEFAULT_USERAGENT;


    public static HttpClient getClient()
    {
        if ( DEFAULT_CLIENT == null ) { DEFAULT_CLIENT = new HttpClient(); }
        return DEFAULT_CLIENT;
    }

    public static GetMethod createGet(String url, String accept)
    {
        GetMethod  m = new GetMethod(url);
        m.setRequestHeader(USER_AGENT     , DEFINE_USERAGENT);
        m.setRequestHeader(ACCEPT_ENCODING, "gzip");
        if ( accept != null ) { m.setRequestHeader(ACCEPT, accept); }
        return m;
    }

    public static void closeMethod(HttpMethod m)
    {
        try {
            closeQuietly(m.getResponseBodyAsStream());
            m.releaseConnection();
        }
        catch (IOException e) {} 
    }

    public static InputStream getInputStream(HttpMethod method)
            throws IOException
    {
        InputStream is = method.getResponseBodyAsStream();
        Header encoding = method.getResponseHeader(CONTENT_ENCODING);
        if ( encoding == null ) { return is; }

        String value = encoding.getValue().toLowerCase();
        if ( value.equals("gzip")   ) { return new GZIPInputStream(is);     }
        if ( value.equals("deflate")) { return new InflaterInputStream(is); }
        return is;
    }

    

    public static String getResponseHeader(HttpMethod method, String param)
    {
       Header header = method.getResponseHeader(param);
       return (header == null ? null : header.getValue());
    }

    public static String getContentType(HttpMethod m, String def)
    {
        String mime = getResponseHeader(m, CONTENT_TYPE);
        return ( mime == null ? def : mime );
    }

    public synchronized static void debugMethod(HttpMethodBase method
                                              , PrintStream ps)
    {
        HttpClient  client = getClient();
        InputStream is     = null;
        try {
            ps.println(method.getName() + " "
                             + method.getURI() + " HTTP/1.1");
            client.executeMethod(method);

            ps.println(method.getStatusLine());

            boolean len = false;
            for ( Header header : method.getResponseHeaders() )
            {
                if ( CONTENT_LENGTH.equals(header.getName()) ) { len = true; }
                ps.print(header.toExternalForm());
            }

            String body = method.getResponseBodyAsString();
            if ( body == null ) { return; }

            if (!len) { ps.println(CONTENT_LENGTH + ": " + body.length());}

            dumpInputStream(getInputStream(method), ps);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally { closeMethod(method); }
    }

    private static void dumpInputStream(InputStream is, PrintStream p)
            throws IOException
    {
        char[] chars = new char[1024];
        InputStreamReader reader = new InputStreamReader(is);
        while ( reader.ready() )
        {
            int l = reader.read(chars);
            if ( l == chars.length ) { p.print(chars); continue; }
            for ( int i = 0; i < l; i++ ) { p.print(chars[i]); }
        }
    }
}
