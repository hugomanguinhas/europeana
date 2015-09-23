package eu.europeana.ld.negotiation.impl;

import java.io.PrintStream;
import java.util.Stack;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.URIException;

import eu.europeana.ld.negotiation.NegotiationConfig;
import eu.europeana.ld.negotiation.NegotiationResults;
import eu.europeana.ld.negotiation.format.FormatConfig;
import static eu.europeana.ld.negotiation.NegotiationUtils.*;
import static eu.europeana.ld.negotiation.NegotiationResults.*;

public class NegotiationContext
{
    private HttpClient         _client;
    private NegotiationConfig  _config;

    private NegotiationResults _results;
    private ResourceResult     _resRsrc;
    private FormatResult       _resFormat;
    private MimeResult         _resMime;

    private StringBuilder      _sb = new StringBuilder(512);


    public NegotiationContext(HttpClient client, NegotiationConfig config)
    {
        _config  = config;
        _client  = client;
        _results = new NegotiationResults();
    }

    public HttpClient   getClient() { return _client; }
    public PrintStream  getOutput() { return _config.getOutput(); }
    public PrintStream  getError()  { return _config.getError();  }

    public FormatConfig getFormat()   { return _resFormat.getFormat(); }
    public String       getMimetype() { return _resMime.getMimetype(); }
    public String       getResource() { return _resRsrc.getURL();      }

/*
    public String      getBaseStep()            { return _steps.firstElement();}
    public String      getStep()                { return _steps.peek();        }
    public boolean     newStep(String url)
    {
        if ( _steps.contains(url) ) { return false; }
        _steps.push(url);
        return true;
    }
*/


    /***************************************************************************
     * Workflow Methods
     **************************************************************************/

    public void newNegotiation(String url)
    {
        _resRsrc = new ResourceResult(url);
    }

    public void endNegotiation()
    {
        _results.put(_resRsrc.getURL(), _resRsrc);
        _resRsrc = null;
    }

    public void newFormatNegotiation(FormatConfig format)
    {
        _resFormat = new FormatResult(format);
    }

    public void endFormatNegotiation(boolean status)
    {
        _resFormat.setStatus(status);
        _resRsrc.put(_resFormat.getFormat(), _resFormat);
        _resFormat = null;
    }

    public void newMimeNegotiation(String mimetype)
    {
        _resMime = new MimeResult(mimetype);
    }

    public void endMimeNegotiation()
    {
        _resFormat.put(_resMime.getMimetype(), _resMime);
        _resMime = null;
    }

    public boolean newTransition(int code, String url)
    {
        if ( _resMime.hasTransition(url) ) { return false; }

        _resMime.add(new PathTransition(code, url));
        return true;
    }


    /***************************************************************************
     * Messages
     **************************************************************************/

    public void printMethodRequest(HttpMethodBase method)
    {
        String uri = "?";
        try { uri = method.getURI().toString(); } catch (URIException e) {}
        printMessage();
        printMessage(method.getName(), " ", uri, " HTTP/1.1");
        printHeader(method.getRequestHeader("Accept"));
        printHeader(method.getRequestHeader("User-Agent"));
    }

    public void printMethodResponse(HttpMethodBase method)
    {
        int code = method.getStatusCode();
        printMessage(method.getStatusLine().toString());
        printHeader(method.getResponseHeader("Location"));
        if ( isRspOK(code) ) {
            printHeader(method.getResponseHeader("Content-Type"));
            printHeader(method.getResponseHeader("Content-Length"));
        }
    }

    private void printHeader(Header header)
    {
        if ( header == null ) { return; }
        printMessage(header.getName(), ": ", header.getValue());
    }


    /***************************************************************************
     * Public Methods - Errors
     **************************************************************************/

    public void newCannotConnect(String url, Throwable t)
    {
        newError("Cannot connect to <" , url, ">");
    }
    
    public void newUnexpectedResponseCode(String url)
    {
        newError("Expecting 200 response for <", url, ">");
    }

    public void newContentError(String url, Throwable t)
    {
        newError("Error parsing content <" , url, ">: "
               , t.getLocalizedMessage());
    }

    public void newUnresolvableError(String url, int iRsp)
    {
        newError("Resource cannot be resolved <", url , ">: code=" + iRsp);
    }

    public void newErrCycle(String url)
    {
        newError("Cycle detected for <", url, ">");
    }

    public void newErrMissingLocation(String url)
    {
        newError("Missing location redirect for <", url , ">");
    }

    public void newUnexpectedContentType(String ct, String expected)
    {
        newError("Content type not expected <", ct, ">"
               , ", expected <", expected, ">");
    }


    /***********************************************************************/
    /*                      Printing Functions                             */
    /***********************************************************************/

    private void newError(String... strs)
    {
        String msg = newMessage("Err", strs);
        _resMime.newMessage(msg);
        getError().println(msg);
    }

    private String newMessage(String type, String... strs)
    {
        try {
            _sb.append(type); _sb.append(": ");
            for ( String str : strs ) { _sb.append(str); }
            return _sb.toString();
        }
        finally { _sb.setLength(0); }
    }


    /*
    private void printError(String str)
    {
        PrintStream err = getError();
        err.println("Error: " + str);
    }

    private void printError(String... strs)
    {
        PrintStream err = getError();
        err.print("Error: ");
        for ( String str : strs ) { err.print(str); }
        err.println();
    }

    private void printMessage(String str) { getOutput().println(str); }

    private void printMessage(String... strs)
    {
        PrintStream out = getOutput();
        for ( String str : strs ) { out.print(str); }
        out.println();
    }

    private void printWarning(String str)
    {
        getError().println("Warn: " + str);
    }

    private void printWarning(String... strs)
    {
        PrintStream err = getError();
        err.print("Warn: ");
        for ( String str : strs ) { err.print(str); }
        err.println();
    }
    */
}
