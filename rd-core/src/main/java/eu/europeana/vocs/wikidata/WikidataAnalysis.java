package eu.europeana.vocs.wikidata;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.ResourceUtils;

import eu.europeana.data.analysis.Analysis;
import eu.europeana.data.analysis.ObjectStat;
import eu.europeana.data.analysis.PropDistributionStat;
import eu.europeana.data.analysis.property.DefaultPropertyStat;
import static eu.europeana.vocs.VocsUtils.*;

public class WikidataAnalysis implements Analysis {

	private static Pattern pattern
	  = Pattern.compile("http[:][/][/]www[.]wikidata[.]org[/]entity[/]P\\d+([a-z])");

	private static Collection<String> langLiterals = Arrays.asList(
			"http://www.w3.org/2000/01/rdf-schema#label", "http://www.w3.org/2004/02/skos/core#altLabel", 
			"http://schema.org/description");

    public ObjectStat analyse(File srcList, File src, File dst) throws IOException
    {
    	Model m = ModelFactory.createDefaultModel();
    	loadModel(m, src, null);

      //fixSubjects(m);
      //fixPredicates(m);

    	ObjectStat stat  = new ObjectStat("Wikidata", true, false, true);

    	Collection<String> saURIs = ( srcList == null ? getPropertyURIs(m)
    			                                      : getPropertyURIsFromFile(srcList));
    	for ( String uri : saURIs )
    	{
    		Property p = m.getProperty(uri);
    		if ( langLiterals.contains(uri) ) {
    			stat.addPropertyValue(new DefaultPropertyStat(p, false));
    		}
    		else {
    			stat.addPropertyValue(p);
    		}
    	}

    	ResIterator iter = m.listSubjects();
    	while ( iter.hasNext() )
    	{
    		Resource r = iter.next();
    		if ( !PATTERN_WIKIDATA.matcher(r.getURI()).matches() ) { continue; }
    		stat.newObject(r);
    	}

    	if ( dst != null ) { stat.print(new PrintStream(dst, "UTF-8")); }

    	return stat;
    }

    private Model fixSubjects(Model m)
    {
    	Collection<String> sa = new HashSet();

    	ResIterator iter = m.listSubjects();
    	while ( iter.hasNext() )
    	{
    		String uri = iter.next().getURI();
    		if ( PATTERN_WIKIDATA.matcher(uri).matches() ) { sa.add(uri); }
    	}

    	for ( String s : sa )
    	{
    		String sNew = getNew(s);
    		if ( sNew == null ) { continue; }

    		ResourceUtils.renameResource(m.getResource(s), sNew);
    	}

    	return m;
    }

    private Model fixPredicates(Model m)
    {
    	List<Statement> l = new ArrayList();
    	StmtIterator iter = m.listStatements();
    	String uri;
    	while ( iter.hasNext() )
    	{
    		Statement stmt = iter.next();

    		uri = stmt.getPredicate().getURI();
    		if ( !PATTERN_WIKIDATA.matcher(uri).matches() ) { continue; }

    		String sNew = getNew(uri);
    		if ( sNew == null ) { continue; }

    		iter.remove();

    		l.add(stmt);
    	}

    	for ( Statement stmt : l )
    	{
    		String sNew = getNew(stmt.getPredicate().getURI());
    		m.add(stmt.getSubject(), m.getProperty(sNew), stmt.getObject());
    	}

		return m;
    }

    private static String getNew(String s)
    {
    	Matcher m = pattern.matcher(s);
		if ( m.find() == false ) { return null; };

		int i = m.start(1);
		return s.substring(0, i);
    }

    private Collection<String> getPropertyURIs(Model m)
    {
    	Collection<String> ret = new HashSet();
    	StmtIterator iter = m.listStatements();
    	while ( iter.hasNext() ) {
    		ret.add(iter.next().getPredicate().getURI());
    	}
    	return ret;
    }

    private Collection<String> getPropertyURIsFromFile(File f)
    {
    	return loadDataURLs(f, PATTERN_WIKIDATA);
    }
}
