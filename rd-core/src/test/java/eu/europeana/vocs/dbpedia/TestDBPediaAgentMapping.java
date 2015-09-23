package eu.europeana.vocs.dbpedia;

import java.io.File;
import java.net.URLEncoder;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class TestDBPediaAgentMapping
{
    public static final void main(String[] args) throws TransformerFactoryConfigurationError, TransformerException
    {
        File fMap = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\Entity Collection\\mappings\\dbpedia2agent.xsl");
        File fSrc = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\Entity Collection\\datasets\\dbpedia\\test_record.rdf");
        File fTrg = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\Entity Collection\\datasets\\dbpedia\\test_record_map.rdf");

        StreamSource s = new StreamSource(fMap);
        Transformer t = TransformerFactory.newInstance().newTransformer(s);
        //t.setParameter("rdf_about", "http://dbpedia.org/resource/Blueprint");
        t.transform(new StreamSource(fSrc), new StreamResult(System.out));
    }
}
