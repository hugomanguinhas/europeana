/**
 * 
 */
package eu.europeana.ld.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import eu.europeana.ld.web.util.BufferedResponseWrapper;
import eu.europeana.ld.web.util.RequestChangeWrapper;
import static eu.europeana.ld.web.util.HttpUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 13 Aug 2015
 */
public class FormatHandlerFilter implements Filter
{
    private static final String        DEFAULT_PARAM  = "format";
    private static final FormatSupport DEFAULT_FORMAT = FormatSupport.RDFXML;

    private String                    _param     = null;
    private FormatSupport             _format    = null;
    private Collection<FormatSupport> _supported = null;

    @Override
    public void init(FilterConfig cfg) throws ServletException
    {
        _param = cfg.getInitParameter("param");
        _param = (_param == null ? DEFAULT_PARAM : _param);

        _format = FormatSupport.getFormat(cfg.getInitParameter("default"));
        _format = (_format == null ? DEFAULT_FORMAT : _format);

        _supported = getFormats(cfg.getInitParameter("support"));
    }

    @Override
    public void doFilter(
            ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest  req = (HttpServletRequest)request;
        HttpServletResponse rsp = (HttpServletResponse)response;

        FormatSupport       f   = getFormat(req);
        if ( f == null ) { chain.doFilter(req, rsp); return; }

        BufferedResponseWrapper rspWrap = wrapResponse(rsp);
        chain.doFilter(wrapRequest(req), rspWrap);
        writeResponse(readModel(rspWrap), rsp, f);
    }

    @Override
    public void destroy() {}


    /***************************************************************************
     * Private Methods - Request Wrappers
     **************************************************************************/

    private RequestChangeWrapper wrapRequest(HttpServletRequest req)
    {
        RequestChangeWrapper w =  new RequestChangeWrapper(req);
        w.addParameter(_param,_format.getExtension());
        String enc = req.getHeader("Accept-Encoding");
        if ( enc != null ) { w.addHeader("Accept-Encoding", null); }
        return w;
    }

    private BufferedResponseWrapper wrapResponse(HttpServletResponse rsp)
    {
        return new BufferedResponseWrapper(rsp);
    }

    private Model readModel(BufferedResponseWrapper wrapper)
    {
        InputStream is = wrapper.getInputStream();
        try {
            Model m = ModelFactory.createDefaultModel();
            m.read(is, null, _format.getJenaFormat().getLang().getLabel());
            return m;
        }
        finally { IOUtils.closeQuietly(is); }
    }

    private FormatSupport getFormat(HttpServletRequest req)
    {
        FormatSupport f = FormatSupport.getFormat(req.getParameter(_param));
        return ( f == null || !_supported.contains(f) ) ? null : f; 
    }

    private Collection<FormatSupport> getFormats(String str)
    {
        if ( str == null ) { return Collections.EMPTY_LIST; }

        String[] strs = str.split(",");
        Collection<FormatSupport> formats = new ArrayList(strs.length);
        for ( String s : strs ) {
            s = s.trim();
            if ( s.isEmpty() ) { continue; }

            FormatSupport f = FormatSupport.getFormat(s);
            if ( f != null ) { formats.add(f); }
        }
        return formats;
    }
}