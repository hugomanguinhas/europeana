/**
 * 
 */
package eu.europeana.entity;

import java.io.File;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import eu.europeana.entity.harvest.DumpHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 7 Aug 2015
 */
public class RunHarvestDumps
{

    public static final void main(String... args)
    {
        File tmpDir = new File("D:\\work\\incoming\\harvest");
        File src    = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\Semantic Enrichment\\target vocs\\dbpedia\\dataset_list.txt");
        DumpHarvester harvester = new DumpHarvester(tmpDir);
        Model model = ModelFactory.createDefaultModel();
        harvester.harvestAndStore(src, model);
    }
}
