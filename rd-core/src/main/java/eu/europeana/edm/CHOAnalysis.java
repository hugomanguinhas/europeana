package eu.europeana.edm;

import java.io.File;
import java.io.IOException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;

import eu.europeana.data.analysis.AbsAnalysis;
import eu.europeana.data.analysis.ObjectStat;
import static eu.europeana.edm.EDMNamespace.*;
import static eu.europeana.vocs.VocsUtils.*;

public class CHOAnalysis extends AbsAnalysis
{
    public ObjectStat analyse(File src, File dst) throws IOException
    {
        dst = getDestination(src, dst);

        Model m = loadModel(src);

        ObjectStat stat  = new ObjectStat("Europeana", true, false, true);
        //stat.addPropertyValue(new LinkSetPropertyStat(m));

        ResIterator iter = m.listResourcesWithProperty(
                m.getProperty(RDF_TYPE), m.getResource(EDM_PROVIDEDCHO));
        super.analyse(iter, dst, stat);

        return stat;
    }

    public ObjectStat analyse(File f, File src, File dst) throws IOException
    {
        return analyse(src, dst);
    }
}
