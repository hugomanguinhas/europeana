package eu.europeana.vocs.conceptexp;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import eu.europeana.enrichment.knowledgebase.report.FreebaseFetch;
import eu.europeana.vocs.VocsUtils;
import static eu.europeana.vocs.VocsUtils.*;

public class FreebaseConceptFetch
{
    public static File SRC = new File(DIR_EXP, "freebase.concepts.list.txt");
    public static File DST = new File(DIR_EXP, "freebase.concepts.xml");

    public FreebaseConceptFetch() {}

    public void fetchAll(File src, File dst) throws IOException
    {
        Collection<String> saURIs  = VocsUtils.loadDataURLs(src, null);

        FreebaseFetch fetch = new FreebaseFetch();
        Model m = ModelFactory.createDefaultModel();
        for ( String sURI : saURIs )
        {
            String s = fetch.fetch(sURI + "?key=" + "AIzaSyDCCLF_1yZu6dP91cgqaWQ6Ra7AByF1gNo");
            VocsUtils.loadModel(m, s, "N3");
        }

        VocsUtils.store(m, dst);
    }

    public static final void main(String[] args) throws Exception
    {
        FreebaseConceptFetch f = new FreebaseConceptFetch();
        f.fetchAll(SRC, DST);
    }
}
