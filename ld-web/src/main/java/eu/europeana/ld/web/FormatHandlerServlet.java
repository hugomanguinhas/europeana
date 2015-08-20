package eu.europeana.ld.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.RiotException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import static eu.europeana.ld.web.util.HttpUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 4 Aug 2015
 */
public class FormatHandlerServlet extends HttpServlet
{
    private static final long   serialVersionUID = 1L;

    private String        _reqHost   = null;
    private String        _redHost   = null;
    private String        _reqFormat = null;
    private FormatSupport _format    = null;


    /***************************************************************************
     * Overridden Methods
     **************************************************************************/
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        _reqHost   = config.getInitParameter("request_url");
        _redHost   = config.getInitParameter("redirect_url");

        _reqFormat = config.getInitParameter("request_format");
        _reqFormat = (_reqFormat == null ? "path" : _reqFormat);

        String        ext    = config.getInitParameter("request_ext");
        FormatSupport format = FormatSupport.getFormat(ext);
        _format = (format != null ? format : FormatSupport.RDFXML);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse rsp)
            throws ServletException, IOException
    {
        try {
            StringBuffer reqURL = req.getRequestURL();

            FormatSupport reqFormat = processRequest(req, reqURL);
            if ( reqFormat == null ) { rsp.sendError(406); return; }

            writeResponse(sendRequest(req, reqURL.toString()), rsp, reqFormat);
        }
        catch (ResponseThrowable t) {
            rsp.sendError(t.getCode());
        }
    }


    /***************************************************************************
     * Private Methods - Request
     **************************************************************************/

    private FormatSupport processRequest(HttpServletRequest req
                                       , StringBuffer url)
    {
        if ( (_reqHost != null) && (_redHost != null) ) {
            int i = url.indexOf(_reqHost);
            if ( i >= 0 ) { url.replace(i, _reqHost.length()+i, _redHost); }
        }

        String f = _reqFormat;
        if ( "path"  .equals(f) ) { return processFormatInPath(req, url);  }
        if ( "accept".equals(f) ) { return processFormatInAccept(req);     }
        if ( "query" .equals(f) ) { return processFormatInQuery(req, url); }
        return null;
    }

    private void appendQuery(HttpServletRequest req, StringBuffer url)
    {
        String query = req.getQueryString();
        if ( query == null ) { return; }
        url.append("?"); url.append(query);
    }

    private FormatSupport processFormatInPath(HttpServletRequest req
                                            , StringBuffer url)
    {
        int i = url.lastIndexOf(".");
        if ( i <= 0 ) { return null; }

        String ext = url.substring(++i);
        if ( ext.length() <= 1 ) { return null; }

        url.replace(i, ext.length() + i, _format.getExtension());
        appendQuery(req, url);
        return FormatSupport.getFormat(ext);
    }

    private FormatSupport processFormatInAccept(HttpServletRequest req)
    {
        String accept = req.getHeader("Accept");
        if ( accept == null ) { return null; }
        accept.trim();
        if ( accept.equals("") ) { return null; }

        return FormatSupport.getFormatByMime(accept);
    }

    private FormatSupport processFormatInQuery(HttpServletRequest req
                                             , StringBuffer url)
    {
        Enumeration<String> params = req.getParameterNames();
        if ( !params.hasMoreElements() ) { return null; }

        url.append("?");

        boolean       first  = true;
        FormatSupport format = null;
        while ( params.hasMoreElements() )
        {
            if ( first ) { first = false; }
            else { url.append("&"); }

            String        param   = params.nextElement();
            String        value   = req.getParameter(param);

            if ( format == null ) {
                format = FormatSupport.getFormat(value);
                if ( format != null ) { value = _format.getExtension(); }
            }

            url.append(param); url.append("="); url.append(value);
        }
        return format;
    }


    /***************************************************************************
     * Private Methods - Handle Translation
     **************************************************************************/

    private Model sendRequest(HttpServletRequest req, String url)
        throws ResponseThrowable
    {
        HttpClient client = new HttpClient();
        GetMethod  method = new GetMethod(url);
        method.setRequestHeader("Accept-Encoding", "gzip");
        method.setRequestHeader("Accept"         , _format.getMimetype());
        InputStream is = null;
        try {
            int rspCode = client.executeMethod(method);
            if ( rspCode != 200 ) { throw new ResponseThrowable(rspCode); }

            is = getInputStream(method);
            Model m = ModelFactory.createDefaultModel();
            m.read(is, null, _format.getJenaFormat().getLang().getLabel());
            return m;
        }
        catch (RiotException | IOException e) {
            throw new ResponseThrowable(e, 502);
        }
        finally { IOUtils.closeQuietly(is); method.releaseConnection(); }
    }


    /***************************************************************************
     * Private Classes
     **************************************************************************/
    private static class ResponseThrowable extends Throwable
    {
        private static final long serialVersionUID = 1L;

        private int _rspCode;

        private ResponseThrowable(Throwable cause, int rspCode)
        {
            super(cause);
            _rspCode = rspCode;
        }

        private ResponseThrowable(int rspCode) { _rspCode = rspCode; }

        public int getCode() { return _rspCode; }
    }
}