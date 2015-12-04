package eu.europeana.tf.results;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;

import eu.europeana.utils.CSVWriter;
import static eu.europeana.edm.EDMNamespace.*;
import static eu.europeana.tf.results.ResultUtils.*;
import static eu.europeana.vocs.VocsUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class EnrichmentCoverageAnalysis
{
    private Map<String,Integer>   _records = null;
    private Map<String,Integer[]> _results = null;

    public EnrichmentCoverageAnalysis(File ds)
    {
        _records = new HashMap();
        _results = new LinkedHashMap();
        Model m = loadModel(ds);
        ResIterator iter = m.listSubjectsWithProperty(
                m.getProperty(RDF_TYPE), m.getResource(EDM_PROVIDEDCHO));
        while ( iter.hasNext() ) { _records.put(iter.next().getURI(), 0); }
    }

    public void analyse(File dest, Object... args)
    {
        int len = args.length;
        for ( int i = 0; i < len; i = i + 2)
        {
            String key = (String)args[i];
            _results.put(key, process(loadEnrichments(key, (File)args[i+1])));
        }

        printResults(dest);
    }

    public void analyse(File dest, File... files)
    {
        for ( File f : files )
        {
            String name = f.getName();
            _results.put(name,process(loadEnrichments(name, f)));
        }

        printResults(dest);
    }

    private void clearHits()
    {
        for ( String res : _records.keySet() ) { _records.put(res, 0); }
    }

    private Integer[] process(Collection<EnrichmentResult> results)
    {
        System.out.println(results.size());
        Set<String> set = new HashSet();
        for (EnrichmentResult r : results) { set.add(r.getResource()); }
        System.out.println(set.size());

        for(EnrichmentResult result : results )
        {
            String uri = result.getResource();
            _records.put(uri, _records.get(uri) + 1);
        }

        int count = 0;
        for ( Integer i : _records.values() )
        {
            if ( i > 0 ) { count++; }
        }

        clearHits();
        return new Integer[] { count, results.size() };
    }

    private void printResults(File file)
    {
        CSVWriter p = new CSVWriter(file);
        p.start();
        p.println("Tool", "Records", "Enrichments");
        for ( String t : _results.keySet() )
        {
            Integer[] values = _results.get(t);
            p.println(t, values[0], values[1]);
        }
        p.println("Total", _records.size());
        p.end();
    }
}
