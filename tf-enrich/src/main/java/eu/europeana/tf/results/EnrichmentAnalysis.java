package eu.europeana.tf.results;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import eu.europeana.utils.CSVWriter;
import static eu.europeana.tf.results.ResultUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class EnrichmentAnalysis
{
    private Cluster      _cluster = new Cluster(null, "Total");
    private List<String> _dims    = new ArrayList();
    private int[]        _cols;

    public EnrichmentAnalysis(int... cols) { _cols = cols; }

    public void analyse(File dest, Object... args)
    {
        int len = args.length;
        for ( int i = 0; i < len; i = i + 2)
        {
            String key = (String)args[i];
            _dims.add(key);
            _cluster.put(key, loadEnrichments(key, (File)args[i+1]));
        }

        makePartition(_cluster, 0);

        printResults(dest);
    }

    public void analyse(File dest, File... files)
    {
        for ( File f : files )
        {
            String name = f.getName();
            _dims.add(name);
            _cluster.put(name, loadEnrichments(name, f));
        }

        makePartition(_cluster, 0);

        printResults(dest);
    }

    private void makePartition(Cluster c, int index)
    {
        int col = _cols[index];
        for ( String dim : _dims )
        {
            Collection<EnrichmentResult> results = c.getResults(dim);
            for(EnrichmentResult result : results )
            {
                Object value = result.getColumn(col);
                c.getPartition(value).addResult(dim, result);
            }
        }

        if (++index >= _cols.length) { return; }

        for ( Cluster partition : c.getPartitions() )
        {
            makePartition(partition, index);
        }
    }

    private void printResults(File file)
    {
        CSVWriter printer = new CSVWriter(file);
        printer.start();
        printHeader(printer);
        printResults(_cluster, printer);
        printer.end();
    }

    private void printResults(Cluster c, CSVWriter p)
    {
        int depth = c.getDepth();
        for ( int i = 1; i < depth; i++ ) { p.print(""); }
        p.print(c.getKey().toString());
        for (String dim : _dims ) { p.print(String.valueOf(c.getDimensionSize(dim))); }

        if ( depth > 0 ) { p.println(); }

        if ( !c.hasPartitions() ) { return; }

        for ( Cluster cluster : c.getPartitions() ) { printResults(cluster, p); }

        //if ( depth == 0 ) { p.print(l); }
    }

    private void printHeader(CSVWriter printer)
    {
        List<String> strs = new ArrayList();
        for ( int col : _cols ) { strs.add(EnrichmentResult.getColumnName(col)); }
        strs.addAll(_dims);
        printer.print(strs);
    }

    static class Cluster extends HashMap<String,Collection<EnrichmentResult>>
    {
        private Cluster             _parent     = null;
        private Object              _key        = null;
        private Map<Object,Cluster> _partitions = null;

        public Cluster(Cluster parent, Object key)
        {
            _parent = parent;
            _key    = key;
        }

        public Cluster() { this(null, null); }

        public Collection<EnrichmentResult> getResults(String dim)
        {
            Collection<EnrichmentResult> ret = get(dim);
            return (ret == null ? Collections.EMPTY_LIST : ret);
        }

        public Collection<String> getDimensions() { return this.keySet(); }
        
        public int getDimensionSize(String dim)
        {
            Collection<EnrichmentResult> ret = get(dim);
            return (ret == null ? 0 : ret.size());
        }

        public int getDepth()
        {
            return (_parent == null ? 0 : _parent.getDepth() + 1);
        }

        public Object getKey() { return _key; }

        public Cluster getParent() { return _parent; }

        public Cluster getPartition(Object value)
        {
            if ( _partitions == null ) { _partitions = new TreeMap(); }

            Cluster c = _partitions.get(value);
            if ( c != null ) { return c; }

            c = new Cluster(this, value);
            _partitions.put(value, c);
            return c;
        }

        public Collection<Cluster> getPartitions() { return _partitions.values(); }
        public boolean hasPartitions() { return _partitions != null; }

        public void addResult(String dim, EnrichmentResult result)
        {
            Collection<EnrichmentResult> col = get(dim);
            if ( col == null ) { col = new ArrayList(); put(dim, col); }
            col.add(result);
        }
    }
}
