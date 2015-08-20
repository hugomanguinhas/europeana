/**
 * 
 */
package eu.europeana.ld.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import eu.europeana.ld.web.util.NoBodyResponseWrapper;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 13 Aug 2015
 */
public class HeadHandlerFilter implements Filter
{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(
            ServletRequest request, ServletResponse response,
            FilterChain c) throws IOException, ServletException
    {
        HttpServletRequest  req = (HttpServletRequest)request;
        HttpServletResponse rsp = (HttpServletResponse)response;
        if ( isHead(req) ) { c.doFilter(wrapRequest(req), wrapResponse(rsp)); }
        else { c.doFilter(req, rsp); }
    }

    @Override
    public void destroy() {}


    /***************************************************************************
     * Private Methods - Handle Translation
     **************************************************************************/

    private HttpServletRequest wrapRequest(HttpServletRequest req)
    {
        return new GetRequestWrapper(req);
    }

    private HttpServletResponse wrapResponse(HttpServletResponse rsp)
    {
        return new NoBodyResponseWrapper(rsp);
    }

    private boolean isHead(HttpServletRequest req)
    {
        return "HEAD".equals(req.getMethod());
    }

    private class GetRequestWrapper extends HttpServletRequestWrapper
    {
        public GetRequestWrapper(HttpServletRequest req) { super(req); }

        public String getMethod() { return "GET"; }
    }
}
