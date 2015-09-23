package eu.europeana.vocs.babelnet;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

import eu.europeana.data.analysis.Analysis;
import eu.europeana.data.analysis.ObjectStat;
import static eu.europeana.vocs.VocsUtils.*;

public class BabelNetAnalysis implements Analysis {

    public ObjectStat analyse(File srcList, File src, File dst) throws IOException
    {
    	Collection<String> c = loadDataURLs(srcList, PATTERN_BABELNET);

    	Model m = ModelFactory.createDefaultModel();
    	loadModel(m, src, null);

    	ObjectStat  stat = new ObjectStat("BabelNet", true, false, true);
    	stat.addPropertyValue(m.getProperty("http://babelnet.org/model/babelnet#gloss"));

    	ResIterator iter = m.listSubjects();
    	while ( iter.hasNext() )
    	{
    		Resource r = iter.next();
    		if ( !c.contains(r.getURI()) ) { continue; }
    		stat.newObject(r);
    	}

    	if ( dst != null ) { stat.print(new PrintStream(dst, "UTF-8")); }

    	return stat;
    }
}
