package eu.europeana.api;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class API2RDFConverter extends javax.servlet.http.HttpServlet {

    public void doGet(HttpServletRequest req
                    , HttpServletResponse resp) throws IOException
    {
        String uri = req.getRequestURI();
        convert(uri, resp.getOutputStream(), getFormat(uri));
    }

    public void convert(String url, OutputStream out, String format)
    {
        Model m = ModelFactory.createDefaultModel();
        m.read(url, null, "RDF/XML");
        m.write(out, format);
    }

    private String getFormat(String uri) { return null; }

    public static void main(String[] args)
    {
        String url = "http://europeana.eu/api/v2/record/2022304/281ADDF7054D3455A5330BDB13BE1C600C6C0606.rdf?wskey=api2demo";
        new API2RDFConverter().convert(url, System.out, "N3");
    }
}
