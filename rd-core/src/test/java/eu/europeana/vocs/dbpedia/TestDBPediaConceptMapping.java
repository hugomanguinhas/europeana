package eu.europeana.vocs.dbpedia;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class TestDBPediaConceptMapping
{
    public static final void main(String[] args) throws TransformerFactoryConfigurationError, TransformerException
    {
        File fMap = new File("C:\\Users\\mangas\\Google Drive\\Europeana\\Semantic Enrichment\\mappings\\dbpedia_skos_concepts_new.xsl");
        File fSrc = new File("C:\\Users\\mangas\\Google Drive\\Europeana\\Semantic Enrichment\\target vocs\\concepts\\blueprint.xml");
        File fTrg = new File("C:\\Users\\mangas\\Google Drive\\Europeana\\Semantic Enrichment\\target vocs\\concepts\\dbpedia.concepts.mapped.xml");

        StreamSource s = new StreamSource(fMap);
        Transformer t = TransformerFactory.newInstance().newTransformer(s);
        t.setParameter("rdf_about", "http://dbpedia.org/resource/Blueprint");
        t.transform(new StreamSource(fSrc), new StreamResult(fTrg));
    }
}
