package eu.europeana.vocs.dbpedia;

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

public class DBPediaAnalysis implements Analysis {

    public ObjectStat analyse(File srcList, File src, File dst) throws IOException
    {
    	Collection<String> c = loadDataURLs(srcList, PATTERN_DBPEDIA);

    	Model m = ModelFactory.createDefaultModel();
    	loadModel(m, src, null);

    	ObjectStat stat  = new ObjectStat("DBPedia", true, false, true);
      //stat.addPropertyValue(m.getProperty("http://www.w3.org/2000/01/rdf-schema#label"));
      //stat.addPropertyValue(m.getProperty("http://www.w3.org/2000/01/rdf-schema#comment"));
    	stat.addPropertyValue(m.getProperty("http://purl.org/dc/terms/subject"));
      //stat.addPropertyValue(m.getProperty("http://dbpedia.org/ontology/abstract"));

    	
    	/*
    	stat.addPropertyValue(m.getProperty("http://purl.org/dc/terms/subject")));
    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/property/movement"), true));
    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/property/movements"), true));
    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/property/class")));
    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/ontology/class"), true));
    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/property/subGrouping"), true));

    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/ontology/knownFor"), true));
    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/property/knownFor"), true));
    	stat.addPropertyValue(m.getProperty("http://live.dbpedia.org/property/knownFor"), true));
    	stat.addPropertyValue(m.getProperty("http://live.dbpedia.org/ontology/knownFor"), true));

    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/property/genre"), true));
    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/property/genres"), true));
    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/ontology/literaryGenre"), true));
    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/property/subgenres"), true));
    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/property/filmGenre"), true));
    	
    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/ontology/occupation"), true));
    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/property/otheroccupation"), true));
    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/property/relatedOccupation"), true));
    	stat.addPropertyValue(m.getProperty("http://dbpedia.org/property/currentOccupation"), true));
    	*/
    	
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
