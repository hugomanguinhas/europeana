package eu.europeana.ld.negotiation;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import eu.europeana.ld.negotiation.format.FormatConfig;
import eu.europeana.ld.negotiation.format.FormatValidator;
import eu.europeana.ld.negotiation.impl.NegotiationContext;
import static eu.europeana.ld.negotiation.NegotiationUtils.*;

/*
 * References:
 * http://liris.cnrs.fr/~pchampin//rdfrest/_modules/rdfrest/util/proxystore.html
 * https://github.com/cygri/pubby/blob/master/src/test/java/de/fuberlin/wiwiss/pubby/negotiation/PubbyNegotiatorTest.java
 User-Agent
 */
public class NegotiationValidator
{
    protected static String[] ACCEPTS = { "", "text/dummy" };
    protected static DefaultHttpMethodRetryHandler RETRY_HANDLER 
        = new DefaultHttpMethodRetryHandler(5, false);


    protected NegotiationConfig  _config;
    protected NegotiationContext _context;

    public NegotiationValidator(NegotiationConfig config)
    {
        _config  = config;
        _context = createContext(null, config);
    }

    public boolean check(String... urls)
    {
        boolean ret = true;
        for ( String url : urls ) { ret = check(url) && ret; }
        return ret;
    }

    public boolean check(String url)
    {
        _context.printMessage("Checking negotiation for <", url, ">");

        _context.newNegotiation(url);
        boolean ret = true;
        for ( FormatConfig format : _config.getFormats() )
        {
            _context.newFormatNegotiation(format);
            boolean status = checkFormatNegotiation(format, _context);
            _context.endFormatNegotiation(status);
            ret = status && ret;
        }
      //ret = checkDefault(_context) && ret;

        _context.endNegotiation();

        return ret;
    }


    /**************************************************************************/
    /* Initialization                                                         */
    /**************************************************************************/

    private NegotiationContext createContext(
            HttpClient client, NegotiationConfig cfg)
    {
        if ( client == null ) { client = new HttpClient(); }
        return new NegotiationContext(client, cfg);
    }


    /**************************************************************************/
    /* Format Negotiation                                                     */
    /**************************************************************************/

    private boolean checkFormatNegotiation(FormatConfig format
                                         , NegotiationContext ctx)
    {
        String url = ctx.getResource();
        boolean ret = true;
        for ( String mime : format.getMimetypes() )
        {
            ctx.printMessage();
            ctx.printMessage("Checking mimetype <", mime, ">");

            ctx.newMimeNegotiation(mime);
            ret = (handleRequest(url, ctx) && ret);
            ctx.endMimeNegotiation();
        }

        return ret;
    }

    private boolean checkContent(HttpMethodBase method
                               , NegotiationContext ctx)
    {
        FormatValidator validator = ctx.getFormat().getValidator();
        return (validator == null ? true : validator.validate(method, ctx));
    }

    private boolean checkType(HttpMethodBase method, NegotiationContext ctx)
    {
        String cType = getResponseHeader(method, "Content-Type");
        for ( String mime : ctx.getFormat().getPreferredMimetypes() ) 
        {
           if ( cType.equals(mime ) ) { return true; }
        }
        ctx.newUnexpectedContentType(cType, "");
        return false;
    }

    private boolean handleRequest(String url, NegotiationContext ctx)
    {
        HttpMethodBase m = newMethod(url, ctx.getMimetype());
        ctx.printMethodRequest(m);

        int code;
        try {
            try {
                code = ctx.getClient().executeMethod(m);
            }
            catch (Exception e) { ctx.newCannotConnect(url, e); return false; }

            ctx.printMethodResponse(m);

            if ( isRspOK(code)       ) { return handleInfoResource(m, ctx); }
            if ( isRspRedirect(code) ) { return handleRedirect(m, ctx);     }
        }
        finally { m.releaseConnection(); }

        ctx.newUnresolvableError(url, code);
        return true;
    }

    private boolean handleInfoResource(HttpMethodBase method
                                     , NegotiationContext ctx)
    {
        if ( !checkType(method, ctx) ) { return false; }

        return checkContent(method, ctx);
    }

    private boolean handleRedirect(HttpMethodBase method
                                 , NegotiationContext ctx)
    {
        String url = getResponseHeader(method, "Location");
        if ( url == null || url.trim().equals("") ) {
            ctx.newErrMissingLocation(url);
            return false;
        }

        boolean res = ctx.newTransition(method.getStatusCode(), url);
        if ( res ) { return handleRequest(url, ctx); }

        ctx.newErrCycle(url);
        return false;
    }

    protected String getResponseHeader(HttpMethodBase method, String param)
    {
       Header header = method.getResponseHeader(param);
       return (header == null ? null : header.getValue());
    }


    /***************************************************************************
     * Default and Error Negotiation                                          
     **************************************************************************/

    private boolean checkDefault(NegotiationContext ctx)
    {
        FormatConfig def = _config.getDefaultFormat();

        ctx.newFormatNegotiation(def); //should not be this one
        ctx.newMimeNegotiation(null);
        boolean res = handleRequest(ctx.getResource(), ctx);
        ctx.endMimeNegotiation();
        ctx.endFormatNegotiation(res);
        return res;
    }


    /**************************************************************************/
    /* Private Methods                                                        */
    /**************************************************************************/

    protected HttpMethodBase newMethod(String url, String accept)
    {
        HttpMethodBase method = newBaseMethod(_config.getMethod(), url);

        if ( accept != null ) { method.setRequestHeader("Accept", accept); }

        String agent = _config.getAgent();
        if ( agent != null ) { method.setRequestHeader("User-Agent", agent); }

        method.setFollowRedirects(false);

        HttpMethodParams params = new HttpMethodParams();
        params.setParameter(HttpMethodParams.RETRY_HANDLER, RETRY_HANDLER);
        method.setParams(params);

        return method;
    }

    protected HttpMethodBase newBaseMethod(String name, String url)
    {
        if (name.equals("GET"))  { return new GetMethod(url);  }
        if (name.equals("HEAD")) { return new HeadMethod(url); }
        return null;
    }
}
