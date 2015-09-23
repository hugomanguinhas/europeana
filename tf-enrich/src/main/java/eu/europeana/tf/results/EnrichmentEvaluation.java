package eu.europeana.tf.results;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import eu.europeana.utils.CSVWriter;
import static eu.europeana.tf.results.ResultUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class EnrichmentEvaluation
{
    private SetComparator            _comp    = new SetComparator();
    private Map<Set<String>,Cluster> _enrichs = new TreeMap(_comp);
    private List<String>             _labels  = new ArrayList();

    public void compare(File dir, Object... args)
    {
        dir.mkdirs();

        int len = args.length;
        for ( int i = 0; i < len; i = i + 2)
        {
            load((String)args[i], (File)args[i+1]);
        }

        doCompare();

        print(new File(dir, "comparison.csv"));
        printClusters(new File(dir, "clusters"), _enrichs);
    }

    public void compare(File... files)
    {
        char c = 'A';
        for ( File f : files ) { load(String.valueOf(c++), f); }

        doCompare();
    }

    private void doCompare()
    {
        Map<Set<String>,Cluster> map = new TreeMap(_comp);
        while ( true )
        {
            getNewSets(map);
            if ( map.isEmpty() ) { break; }

            _enrichs.putAll(map);

            map.clear();
        }

        for ( Set<String> key : _enrichs.keySet() )
        {
            if ( key.size() != 1 ) { continue; }

            computeDifference(_enrichs.get(key));
        }
    }

    private void computeDifference(Cluster cluster) 
    {
        String name = cluster.getKey().iterator().next();
        Collection<EnrichmentResult> results = cluster.getResults();
        for ( Set<String> key : _enrichs.keySet() )
        {
            if ( key.size() == 1 || key.contains(name) ) { continue; }

            results.removeAll(_enrichs.get(key).getResults());
        }
    }

    private void load(String label, File file)
    {
        _labels.add(label);
        Set<String> key = Collections.singleton(label);
        _enrichs.put(key, new Cluster(key, loadEnrichments(label, file)));
    }

    private void getNewSets(Map<Set<String>,Cluster> map)
    {
        for ( Set<String> set1 : _enrichs.keySet() ) 
        {
            for ( Set<String> set2 : _enrichs.keySet() ) 
            {
                if ( (set1 == set2) || intersects(set1, set2) ) { continue; }

                Set<String> set = new TreeSet(set1); set.addAll(set2);
                if ( _enrichs.containsKey(set) || map.containsKey(set) ) { continue; };

                map.put(set, intersection(set, _enrichs.get(set1), _enrichs.get(set2)));
            }
        }
    }

    private boolean intersects(Set<String> set1, Set<String> set2)
    {
        for ( String str1 : set1 ) 
        {
            if ( set2.contains(str1) ) { return true; }
        }
        return false;
    }

    
    /*
     * Map with A, B, C, D, AB, AC, AD, BC, BD, CD, ABC, ABD, ACD, ...
     * iterativelly pick the first and compute with the second
     * comute intersection
     * compare two by two
     * 
     * Partition total per type of entity
     * 
     */

    private void printClusters(
            File dir, Map<Set<String>,Cluster> results)
    {
        dir.mkdir();

        int i = 2;
        Set<Cluster> clusters = new TreeSet(results.values());
        for ( Cluster cluster : clusters )
        {
            if ( cluster.getResults().isEmpty() ) { continue; }

            String name = "#" + (i++) + "_" + cluster.getName();
            printCluster(new File(dir, name), cluster.getResults());
        }
    }

    private void printCluster(File output
                            , Collection<EnrichmentResult> results)
    {
        CSVWriter p = new CSVWriter(output);
        p.start();
        EnrichmentResult.printHeaderRaw(p);
        for ( EnrichmentResult res : results ) { res.printRaw(p); }
        p.end();
    }

    private void print(File output)
    {
        CSVWriter p = new CSVWriter(output);
        p.start();
        printResults(p, _enrichs);
        p.end();
    }

    private void printResults(CSVWriter p
                            , Map<Set<String>,Cluster> results)
    {
        p.print(_labels);
        p.println("Count");

        Set<Cluster> clusters = new TreeSet(results.values());

        for ( Cluster cluster : clusters )
        {
            if ( cluster.isEmpty() ) { continue; }

            String tip = ( cluster.getKey().size() == 1 ? "N/A" : "A" );

            for ( String lbl : _labels ) { p.print(cluster.getKey().contains(lbl) ? tip : "" ); }
            p.println(cluster.getResults().size());
        }
    }

    private Cluster intersection(
            Set<String> key, Cluster c1, Cluster c2)
    {
        Collection<EnrichmentResult> results = new TreeSet();
        if ( c1.isEmpty() || c2.isEmpty() ) { return new Cluster(key, results); }

        for ( EnrichmentResult r : c1.getResults() )
        {
            if ( c2.getResults().contains(r) ) { results.add(r); }
        }
        return new Cluster(key, results);
    }

    private Collection<EnrichmentResult> intersection2(
            Collection<EnrichmentResult> c1, Collection<EnrichmentResult> c2)
    {
        Collection<EnrichmentResult> results = new TreeSet();
        if ( c1.isEmpty() || c2.isEmpty() ) { return results; }

        Iterator<EnrichmentResult> i1  = c1.iterator();
        Iterator<EnrichmentResult> i2  = c2.iterator();
        EnrichmentResult           r1  = i1.next();
        EnrichmentResult           r2  = i2.next();
        boolean                    end = false;
        while ( true )
        {
            int comp = r1.compareTo(r2);
            while ( comp < 0 )
            {
                if ( !i1.hasNext() ) { end = true; break; }
                r1 = i1.next(); comp = r1.compareTo(r2);
            }
            if ( end ) { break; }

            comp = -comp;
            while ( comp < 0 )
            {
                if ( !i2.hasNext() ) { end = true; break; }
                r2 = i2.next(); comp = r2.compareTo(r1);
            }
            if ( end ) { break; }

            if ( comp == 0 ) {
                results.add(r1);
                if ( !i1.hasNext() || !i2.hasNext() ) { break; }
                r1 = i1.next(); r2 = i2.next();
            }
        }
        System.out.println("intersection:" + results.size());
        return results;
    }

    class Cluster implements Comparable<Cluster>
    {
        private Collection<EnrichmentResult> _results;
        private Set<String>                  _key;

        public Cluster(Set<String> key, Collection<EnrichmentResult> results)
        {
            _key     = key;
            _results = results;
        }

        public Set<String>                  getKey()     { return _key;     }

        public Collection<EnrichmentResult> getResults() { return _results; }
        public boolean isEmpty()                         { return _results.isEmpty(); }

        public String getName()
        {
            String name = StringUtils.join(_key, '_').replace(' ', '_');
            return ( _key.size() == 1 ? "only_" + name : name );
        }

        @Override
        public int compareTo(Cluster c)
        {
            int ret = c._key.size() - _key.size();
            if ( ret != 0 ) { return ret; }

            ret = c._results.size() - _results.size();
            if ( ret != 0 ) { return ret; }

            return c.hashCode() - hashCode();
        }
    }

    class SetComparator implements Comparator<Set<String>>
    {
        @Override
        public int compare(Set<String> set1, Set<String> set2)
        {
            int ret = set1.size() - set2.size();
            if ( ret != 0 ) { return ret; }

            Iterator<String> iter1 = set1.iterator();
            Iterator<String> iter2 = set2.iterator();
            while (iter1.hasNext() )
            {
                ret = compareKey(iter1.next(), iter2.next());
                if ( ret != 0 ) { return ret; }
            }
            return 0;
        }

        private int compareKey(String key1, String key2)
        {
            return (_labels.indexOf(key1) - _labels.indexOf(key2));
        }
    }
}
