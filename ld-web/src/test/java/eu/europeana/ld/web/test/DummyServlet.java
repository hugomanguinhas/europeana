package eu.europeana.ld.web.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RiotException;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 4 Aug 2015
 */
public class DummyServlet extends HttpServlet
{

    /**************************************************************************/
    /* Overridden Methods                                                     */
    /**************************************************************************/
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse rsp)
            throws ServletException, IOException
    {
        InputStream is = this.getClass().getResourceAsStream("examplerdf.rdf");
        IOUtils.copy(is, rsp.getOutputStream());
        IOUtils.closeQuietly(is);
    }
}