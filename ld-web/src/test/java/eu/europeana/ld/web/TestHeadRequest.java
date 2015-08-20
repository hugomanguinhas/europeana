package eu.europeana.ld.web;

import org.apache.commons.httpclient.methods.HeadMethod;

import static eu.europeana.ld.web.util.HttpUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 4 Aug 2015
 */
public class TestHeadRequest
{
    //DefaultServlet serveResource
    public static final void main(String... args)
    {
        sendRequest("http://localhost:8081/ld-web/examplerdf.rdf?format=jsonld", false);
        sendRequest("http://localhost:8081/ld-web/examplerdf.rdf?format=jsonld", true);
    }

    private static void sendRequest(String url, boolean zip)
    {
        HeadMethod method = new HeadMethod(url);
        if ( zip ) { method.setRequestHeader("Accept-Encoding", "gzip"); }
        debugMethod(method);
    }
}