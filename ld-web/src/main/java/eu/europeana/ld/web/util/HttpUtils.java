/**
 * 
 */
package eu.europeana.ld.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.commons.io.IOUtils;

import com.hp.hpl.jena.rdf.model.Model;

import eu.europeana.ld.web.FormatSupport;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 13 Aug 2015
 */
public class HttpUtils
{
    private static String DEFAULT_ENCODING = "UTF-8";

    public static InputStream getInputStream(HttpMethod method)
            throws IOException
    {
        InputStream is = method.getResponseBodyAsStream();
        Header encoding = method.getResponseHeader("Content-Encoding");
        if ( encoding == null ) { return is; }

        String value = encoding.getValue().toLowerCase();
        if ( value.equals("gzip")   ) { return new GZIPInputStream(is);     }
        if ( value.equals("deflate")) { return new InflaterInputStream(is); }
        return is;
    }

    public static void writeResponse(Model m, HttpServletResponse rsp
                                   , FormatSupport format) throws IOException
    {
        rsp.setContentType(format.getMimetype());
        rsp.setCharacterEncoding(DEFAULT_ENCODING);

        CountingOutputStream out = null;
        try {
            out = new CountingOutputStream(rsp.getOutputStream());
            RDFDataMgr.write(out, m, format.getJenaFormat());
            out.flush();
            rsp.setContentLength(out.getCount());
        }
        finally { IOUtils.closeQuietly(out); }
    }

    public static void debugMethod(HttpMethodBase method)
    {
        HttpClient  client = new HttpClient();
        InputStream is     = null; 
        try {
            System.out.println(method.getName() + " "
                             + method.getURI() + " HTTP/1.1");
            client.executeMethod(method);

            System.out.println(method.getStatusLine());

            boolean len = false;
            for ( Header header : method.getResponseHeaders() )
            {
                if ( header.getName().equals("Content-Length") ) { len = true; }
                System.out.print(header.toExternalForm());
            }

            String body = method.getResponseBodyAsString();
            if ( body == null ) { return; }

            if (!len) { System.out.println("Content-Length: " + body.length());}

            is = getInputStream(method);
            dumpInputStream(is, System.out);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally { IOUtils.closeQuietly(is); method.releaseConnection(); }
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
