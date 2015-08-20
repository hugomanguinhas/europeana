package eu.europeana.ld.web;

import static eu.europeana.ld.web.util.HttpUtils.debugMethod;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 4 Aug 2015
 */
public class TestFormats
{

    public static final void main(String... args)
    {
        //sendRequest("http://localhost:8081/lod/record/2021401/0E56CF0EB1D62A6489F709C234451CFC403FCEF7.jsonld?wskey=api2demo");
        sendRequest("http://localhost:8081/ld-web/concept?id=http://id.loc.gov/authorities/names/n79021597&format=ttl");
        //sendRequest("http://skos.europeana.eu/api/concept?id=http://id.loc.gov/authorities/names/n79021597&format=rdf");
        //sendRequest("http://localhost:8081/ld-web/examplerdf.rdf");
        //sendRequest("http://localhost:8081/lod/record/9200365/BibliographicResource_1000054838907.trig?wskey=api2demo");
    }

    private static void sendRequest(String url)
    {
        GetMethod method = new GetMethod(url);
        method.setRequestHeader("Accept-Encoding", "gzip");
        debugMethod(method);
    }
}
