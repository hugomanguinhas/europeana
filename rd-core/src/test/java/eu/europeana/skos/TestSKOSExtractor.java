/**
 * 
 */
package eu.europeana.skos;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import static eu.europeana.edm.EDMNamespace.*;
import static eu.europeana.utils.JenaUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 10 Sep 2015
 */
public class TestSKOSExtractor
{
    public static final void main(String... args) throws IOException
    {
        runNISV();
    }

    private static void runNISV() throws IOException
    {
        File dir = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\Vocabularies\\NISV");
        run(new File(dir, "items"), new File(dir, "nisv.concepts.rdf"));
    }

    private static void runCNRS() throws IOException
    {
        File dir = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\Vocabularies\\CNRS");
        run(new File(dir, "items"), new File(dir, "cnrs.concepts.rdf"));
    }

    private static void runBL_Keith() throws IOException
    {
        File dir = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\Vocabularies\\BL\\keith");
        run(new File(dir, "items"), new File(dir, "bl.concepts.rdf"));
    }

    private static void runBL_Cook() throws IOException
    {
        File dir = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\Vocabularies\\BL\\cook");
        run(new File(dir, "items"), new File(dir, "bl.concepts.rdf"));
    }

    private static void run(File src, File trg) throws IOException
    {
        String base = "http://www.europeanasounds.eu/data/concepts#";
        //String base = "http://mint-projects.image.ntua.gr/data/concepts#";
        Model mSrc = loadFiles(src, ".xml", ModelFactory.createDefaultModel());
        Model mTrg = ModelFactory.createDefaultModel();
        new SKOSExtractor(base).extract(mSrc, mTrg);
        createScheme(mTrg, base);
        Map prefs = Collections.singletonMap("xmlbase", base);
        store(mTrg, trg, "RDF/XML-ABBREV", prefs);
    }

    private static void createScheme(Model m, String base)
    {
        Resource rsrc = m.getResource(base + "ConceptScheme");
        rsrc.addProperty(m.getProperty(SKOS_NOTE)
                       , "A virtual scheme containing concepts that were extracted from a set of source records in order to be matched against a target vocabulary.");
    }
}
