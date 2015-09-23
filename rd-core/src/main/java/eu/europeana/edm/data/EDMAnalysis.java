package eu.europeana.edm.data;

import java.io.File;
import java.io.IOException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

import eu.europeana.data.analysis.AbsAnalysis;
import eu.europeana.data.analysis.ObjectStat;
import eu.europeana.data.analysis.property.LinkSetPropertyStat;
import static eu.europeana.edm.EDMNamespace.*;
import static eu.europeana.vocs.VocsUtils.*;

public class EDMAnalysis extends AbsAnalysis
{
    public ObjectStat analyse(File srcList, File src, File dst) throws IOException
    {
        if ( dst == null ) { dst = getDestination(src); }

        Model m = loadModel(src);
        Property type = m.getProperty(RDF_TYPE);
        Resource pcho = m.getResource(EDM_PROVIDEDCHO);

        ObjectStat stat  = new ObjectStat("Europeana", true, false, true);

        stat.addPropertyValue(new LinkSetPropertyStat(m));
        ResIterator iter = m.listSubjectsWithProperty(type, pcho);
        stat.addPropertyValues(m, iter, false);

        super.analyse(m.listSubjectsWithProperty(type, pcho), dst, stat);

        return stat;
    }
}
