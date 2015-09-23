package eu.europeana.tf.results;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

import eu.europeana.utils.CSVWriter;
import eu.europeana.enrich.EnrichmentAPI;
import static eu.europeana.edm.EDMNamespace.*;
import static eu.europeana.vocs.VocsUtils.*;

public class EuropeanaDatasetEnrich {

    private EnrichmentAPI _api = new EnrichmentAPI();

    public void enrich(File src, File dst)
    {
        Model m = loadModel(src);

        Property type = m.getProperty(RDF_TYPE);
        Resource pcho = m.getResource(EDM_PROVIDEDCHO);
        
        CSVWriter p = new CSVWriter(dst);
        p.start();

        try {
            enrichImpl(m.listSubjectsWithProperty(type, pcho), p);
        }
        finally {
            p.end();
        }
    }

    private void enrichImpl(ResIterator iter, CSVWriter p)
    {
        while (iter.hasNext())
        {
            Resource rsrc = iter.next();
            System.out.print("Enriching resource: " + rsrc.getURI());

            List<Map> l = _api.enrich(rsrc);
            if ( l == null ) { System.out.println(); continue; }

            int count = 0;
            for ( Map m : l )
            {
                count++;
                p.print(rsrc.getURI(), m.get("field")
                      , m.get("enrichment"), "", m.get("value"));
            }

            System.out.println(" [" + count + "]");
        }
    }

    public static void main(String[] args)
    {
        File src = new File("D:\\work\\incoming\\nuno2\\dataset.xml");
        File dst = new File("D:\\work\\incoming\\nuno2\\enrich.europeana.csv");
        new EuropeanaDatasetEnrich().enrich(src, dst);
    }
}
