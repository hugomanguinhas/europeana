package eu.europeana.enrich.disamb;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import eu.europeana.api.RecordAPI;
import eu.europeana.entity.norm.LiteralNormalizer;
import eu.europeana.utils.CSVWriter;
import static eu.europeana.enrich.EnrichmentUtils.*;
import static eu.europeana.enrich.YorgosUtils.*;
import static eu.europeana.edm.EDMNamespace.*;
import static eu.europeana.vocs.VocsUtils.*;

public class AmbiguityFetch
{
    private Map<String,Cluster>  _index      = new HashMap();
    private ResourceDisambiguator _comp      = null;
    private RecordAPI            _api        = new RecordAPI();
    private Map<String,Integer>  _enrichHits = null;
    private Map<String,Integer>  _portalHits = null;
    private File                 _portalFile = null;

    public AmbiguityFetch(File enrich, File hits
                        , ResourceDisambiguator disambiguator)
    {
        _enrichHits = loadEnrichmentHits(enrich);
        _portalHits = loadPortalHits(hits);
        _portalFile = hits;
        _comp       = disambiguator;
    }

    public void process(File input, File output, File cluster)
    {
        Model m = ModelFactory.createDefaultModel();
        try {
            m.read(new FileReader(input), null, "RDF/XML");
        }
        catch (Exception e) {
            System.err.println("error parsing: " + input.getName() + ", error: " + e.getMessage());
            return;
        }

        _comp.init(m);

        System.out.print("fetching resources... ");
        fetchLabels(m);
        System.out.println("[" + _index.size() + "]");

        System.out.print("removing singletons... ");
        filterSingletonClusters();
        System.out.println("[" + _index.size() + "]");

        System.out.print("calculating hits...");
        updateHits(_index.values());
        System.out.println("[" + _index.size() + "]");

        System.out.print("removing duplicates...");
        Collection<Cluster> col = removeDuplicates();
        System.out.println("[" + col.size() + "]");

        System.out.print("updating enrichments...");
        updateEnrichments(col);
        System.out.println("[" + col.size() + "]");

        col = sortItems(col);

        System.out.println("printing results...");
        printClusters(col, cluster);
        printCSV(col, output);
    }

    private void fetchLabels(Model m)
    {
        Map<String,String> map = new HashMap();
        Property           p   = m.getProperty(SKOS_PREF_LABEL);

        ResIterator rIter = m.listResourcesWithProperty(m.getProperty(RDF_TYPE));
        while ( rIter.hasNext() )
        {
            Resource r = rIter.next();
            fetchAlternatives(map, r);

            StmtIterator sIter = r.listProperties(p);
            while ( sIter.hasNext() )
            {
                Statement stmt = sIter.next();
                put(stmt.getSubject(), getKey(stmt.getString(), map));
            }

            map.clear();
        }
    }

    private void fetchAlternatives(Map<String,String> map, Resource r)
    {
        Property     p    = r.getModel().getProperty(SKOS_ALT_LABEL);
        StmtIterator iter = r.listProperties(p);
        
        while ( iter.hasNext() )
        {
            fillKeyword(map, cleanExcess(iter.next().getString()));
        }
    }

    private void fillKeyword(Map<String,String> map, String str)
    {
        if (!str.contains(",")) { return; }

        String[] split  = str.toLowerCase().split(",");
        if (split.length <= 1) { return; }

        String   pref = split[0].trim() + " " + split[1].trim();
        String   alt  = split[1].trim() + " " + split[0].trim();
        map.put(alt, pref);
        //map.put(alt , pref);
    }

    private List<String> getKey(String literal, Map<String,String> map)
    {
        List<String> l = normalizeInternal(literal);

        int len = l.size();
        for (int i = 0; i < len; i++ )
        {
            String s    = l.get(i).trim().toLowerCase(); 
            l.set(i, s);

            String sAlt = map.get(s);
            if ( sAlt == null || l.contains(sAlt) ) { continue; }
            l.add(sAlt);
        }

        //System.out.println(l);
        return l;
    }

    private void put(Resource rsrc, List<String> l)
    {
        for ( String key : l ) { put(rsrc, key); }
    }

    private void put(Resource rsrc, String key)
    {
        if ( key.isEmpty() ) { return; }

        Cluster cluster = _index.get(key);
        if ( cluster == null ) { _index.put(key, new Cluster(key, rsrc)); }
        else                   { cluster.add(rsrc); }
    }

    private void filterSingletonClusters()
    {
        Iterator<Cluster> iter = _index.values().iterator();
        while ( iter.hasNext() )
        {
            if ( iter.next().isSingleton() ) { iter.remove(); }
        }
    }

    private void updateHits(Collection<Cluster> set)
    {
        for ( Cluster c : set ) { c.updateHits(); }

        updatePortalHits(_portalHits, _portalFile);
    }

    private void updateEnrichments(Collection<Cluster> set)
    {
        for ( Cluster c : set ) { c.updateEnrichments(); }
    }

    private Collection<Cluster> removeDuplicates()
    {
        List<Cluster> list = new ArrayList<Cluster>(_index.size());
        for ( Cluster cluster : _index.values() )
        {
            int i = findMatch(list, cluster);
            //int i = list.indexOf(cluster);
            if ( i < 0 ) { list.add(cluster);          }
            else         { list.get(i).merge(cluster); }
        }
        return list;
    }

    private int findMatch(List<Cluster> list, Cluster c)
    {
        int i = 0;
        for ( Cluster cNew : list )
        {
            if ( cNew.containsAll(c) || c.containsAll(cNew) ) { return i; }
            i++;
        }
        return -1;
    }

    private Collection<Cluster> sortItems(Collection<Cluster> col)
    {
        TreeSet<Cluster> set = new TreeSet<Cluster>(CLUSTER_COMPARATOR);
        for ( Cluster c : col ) { c.sortItems(); set.add(c); }
        return set;
    }

    private void printClusters(Collection<Cluster> col, File dir)
    {
        if ( !dir.exists() ) { dir.mkdirs(); }

        for (Cluster cluster : col)
        {
            printCluster(cluster, new File(dir, URLEncoder.encode(cluster.getKey()) + ".xml"));
        }
    }

    private void printCluster(Cluster cluster, File file)
    {
        Model m = ModelFactory.createDefaultModel();
        for ( Resource rsrc : cluster._sortedSet ) { m.add(rsrc.listProperties()); }
        try {
            store(m, file);
        }
        catch ( IOException e ) {
            System.err.println("Error writting to file: " + file.getName()
                             + ", reason: " + e.getMessage());
        }
        m.removeAll();
    }

    private void printCSV(Collection<Cluster> set, File file)
    {
        CSVWriter p = new CSVWriter(file);
        p.start();

        //header
        p.print("Name", "Enrichments", "Portal Hits", "Best Candidate", "Y|N|P");
        int max = getMaxResources(set);
        for ( int i = 2; i < max; i++ ) { p.print("Candidate " + i, "Y|N|P"); }
        p.println();

        //lines
        for ( Cluster c : set ) { c.print(p); }

        p.end();
    }

    private int getMaxResources(Collection<Cluster> set)
    {
        int max = 0;
        for ( Cluster c : set ) { max = Math.max(max, c.size()); }
        return max;
    }

    class Cluster extends HashSet<Resource>
    {
        private List<String>  _keys;
        private Set<Resource> _sortedSet;
        private int           _hits;
        private int           _enrichments;

        public Cluster(String key, Resource rsrc)
        {
            _keys = new ArrayList<String>(1);
            _keys.add(key);
            add(rsrc);
            _sortedSet = new TreeSet(_comp);
        }

        public String getKey() { return _keys.get(0); }

        public boolean isSingleton() { return (this.size() <= 1); }

        public void updateHits()
        {
            String  key   = getKey();
            Integer count = _portalHits.get(key);
            if ( count != null ) { _hits = count; return; }

            try {
                System.err.println("Getting hits for key: " + key);
                _hits = _api.countWhoExactMatch(key);
                _portalHits.put(key, _hits);
            } catch (IOException e) {
                System.err.println("Error getting hits for: " + key
                                 + ", reason: " + e.getMessage());
            }
        }

        public void sortItems()
        {
            _sortedSet.clear();
            _sortedSet.addAll(this);
        }

        public void updateEnrichments()
        {
            int total = 0;
            for (Resource rsrc : this)
            {
                Integer i = _enrichHits.get(rsrc.getURI());
                total += (i == null ? 0 : i);
            }
            _enrichments = total;
        }

        public Cluster merge(Cluster c)
        {
            if ( c == null ) { return this; }

            Cluster master = this, slave = c;
            //Cluster master = null, slave = null;
            //if ( CLUSTER_COMPARATOR.compare(this, c) > 0 ) { master = this; slave = c; }
            //else { master = c; slave = this;  }

            master.addAll(slave);
            master._keys.addAll(slave._keys);
            master._hits += slave._hits;
            return master;
        }

        private void print(CSVWriter p)
        {
            p.print(StringUtils.join(_keys, ';'), _enrichments, _hits);
            for ( Resource rsrc : _sortedSet ) { p.print(rsrc.getURI(), ""); }
            p.println();
        }
    }

    private static ClusterComparator CLUSTER_COMPARATOR = new ClusterComparator();

    static class ClusterComparator implements Comparator<Cluster>
    {
        public int compare(Cluster c1, Cluster c2)
        {
            int i = (c2._enrichments - c1._enrichments);
            if ( i != 0 ) { return i; }

            i = (c2._hits - c1._hits);
            return ( i == 0 ? c1.getKey().compareTo(c2.getKey()) : i);
        }
    }

    
}
