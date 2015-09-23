/**
 * 
 */
package eu.europeana.ld.negotiation;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 4 Aug 2015
 */
public class Test2
{
    public static void main(String... args)
    {
        sendRequest("http://localhost:8080/item/test", "application/rdf+xml");
        sendRequest("http://localhost:8080/aggregation/provider/test", "application/rdf+xml");
        sendRequest("http://localhost:8080/item/test", "application/ld+json");
        sendRequest("http://localhost:8080/concept/test", "application/rdf+xml");
    }

    private static void sendRequest(String url, String accept)
    {
        System.out.println("GET " + url + " HTTP/1.1");

        HttpClient client = new HttpClient();

        GetMethod  method = new GetMethod(url);
        method.setRequestHeader("Accept", accept);
        method.setFollowRedirects(false);

        try {
            client.executeMethod(method);
            long len = method.getResponseContentLength();
            System.out.println(method.getStatusLine());
            for ( Header header : method.getResponseHeaders() )
            {
                System.out.print(header.toExternalForm());
            }
            System.out.println();
            System.out.println(method.getResponseBodyAsString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
