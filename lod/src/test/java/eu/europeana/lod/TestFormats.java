package eu.europeana.lod;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 4 Aug 2015
 */
public class TestFormats
{

    public static final void main(String... args)
    {
        //sendRequest("http://localhost:8081/lod/record/2021401/0E56CF0EB1D62A6489F709C234451CFC403FCEF7.jsonld?wskey=api2demo");
        sendRequest("http://localhost:8081/lod/concept.jsonld?id=http://id.loc.gov/authorities/names/n79021597");
        //sendRequest("http://localhost:8081/lod/record/9200365/BibliographicResource_1000054838907.trig?wskey=api2demo");
    }

    private static void sendRequest(String url)
    {
        System.out.println("GET " + url + " HTTP/1.1");

        HttpClient client = new HttpClient();
        GetMethod  method = new GetMethod(url);
        try {
            client.executeMethod(method);
            long len = method.getResponseContentLength();
            System.out.println(method.getStatusLine());
            for ( Header header : method.getResponseHeaders() )
            {
                System.out.print(header.toExternalForm());
            }
            System.out.println("Content-Length: " + len);
            System.out.println(method.getResponseBodyAsString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
