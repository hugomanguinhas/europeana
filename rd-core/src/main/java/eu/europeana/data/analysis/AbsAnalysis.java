package eu.europeana.data.analysis;

import static eu.europeana.vocs.VocsUtils.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;

public abstract class AbsAnalysis implements Analysis
{
    protected ObjectStat analyse(File src, File dst
                               , ObjectStat stat) throws IOException
    {
        Model m = ModelFactory.createDefaultModel();
        loadModel(m, src, null);

        return analyse(m, dst, stat);
    }

    protected ObjectStat analyse(Model m, File dst
                               , ObjectStat stat) throws IOException
    {
        return analyse(m.listSubjects(), dst, stat);
    }

    protected ObjectStat analyse(ResIterator iter, File dst
                               , ObjectStat stat) throws IOException
    {
        while ( iter.hasNext() ) { stat.newObject(iter.next()); }

        if ( dst != null ) { stat.print(new PrintStream(dst, "UTF-8")); }
        
        return stat;
    }

    protected File getDestination(File src) throws IOException
    {
        String name = src.getName().replace(".xml", "") + ".txt";
        return new File(src.getParentFile(), name);
    }

    protected File getDestination(File src, File dst) throws IOException
    {
        return (dst == null ? getDestination(src) : dst);
    }
}
