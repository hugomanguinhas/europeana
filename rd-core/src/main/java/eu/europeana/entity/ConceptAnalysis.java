package eu.europeana.entity;

import java.io.File;
import java.io.IOException;

import com.hp.hpl.jena.rdf.model.Model;

import eu.europeana.data.analysis.AbsAnalysis;
import eu.europeana.data.analysis.ObjectStat;
import eu.europeana.data.analysis.property.LinkSetPropertyStat;
import static eu.europeana.edm.EDMNamespace.*;
import static eu.europeana.vocs.VocsUtils.*;

public class ConceptAnalysis extends AbsAnalysis
{
    public ObjectStat analyse(File src, File dst) throws IOException
    {
        return analyse(null, src, dst);
    }

    public ObjectStat analyse(File srcList, File src, File dst) throws IOException
    {
        dst = getDestination(src, dst);

        Model m = loadModel(src);

        ObjectStat stat  = new ObjectStat("Europeana", true, false, true);
        /*
        stat.addPropertyValues(m, false
                         , SKOS_PREF_LABEL, SKOS_ALT_LABEL
                         , RDAGR2_BIBINFO, RDAGR2_DATEOFBIRTH, RDAGR2_DATEOFDEATH
                         , DC_IDENTIFIER, EDM_ISRELATEDTO, EDM_END, EDM_PROFOROCCUPATION);
         */
        stat.addPropertyValue(new LinkSetPropertyStat(m.getProperty(SKOS_EXACT_MATCH)));

        super.analyse(m, dst, stat);

        return stat;
    }
}
