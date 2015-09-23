package eu.europeana.data.analysis;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class PropDistributionStat extends AbsStat
{
    private Map<Property,ValueStat> _propStats;

    private Map<Property,Integer>   _auxCounts;

    private Map<Property,MathStat>  _propCount;

    public PropDistributionStat()
    {
        _propStats = new HashMap();
        _auxCounts = new HashMap();
        _propCount = new HashMap();
    }

    public Collection<Property> getProperties() { return _propStats.keySet(); }

    public void newResource(Resource r)
    {
        _auxCounts.clear();
        fillCounts(r, _auxCounts);
        fillCounts2(r, _propCount);
        fillStats(_auxCounts, _propStats);
    }

    public void print(PrintStream ps, int total)
    {
        printHeader(ps);
        ps.println("Total n. of properties: " + _propStats.size());
        ps.println();

        Set<ValueStat> set = new TreeSet(_propStats.values());
        for ( ValueStat r : set ) { r.print(ps, total); }
        ps.println();

        for ( Property p : _propCount.keySet() )
        {
            ps.print(p.getURI());
            _propCount.get(p).print(ps);
            ps.println();
        }
    }

    protected void fillCounts(StmtIterator iter, Map<Property,Integer> counts)
    {
        while ( iter.hasNext() )
        {
            Property p = iter.next().getPredicate();
            Integer  i = counts.get(p);
            counts.put(p, i == null ? 1 : i + 1);
        }
        iter.close();
    }

    protected void fillCounts(Resource r, Map<Property,Integer> counts)
    {
        fillCounts(r.listProperties(), counts);
    }

    protected void fillCounts2(StmtIterator iter, Map<Property,MathStat> stat)
    {
        while ( iter.hasNext() )
        {
            Property p = iter.next().getPredicate();
            MathStat m = stat.get(p);
            if ( m == null ) { m = new MathStat(); stat.put(p,m); }
            m.newItem();
        }
        iter.close();
        for ( MathStat m : stat.values() ) { m.endScope(); }
    }

    protected void fillCounts2(Resource r, Map<Property,MathStat> stat)
    {
        fillCounts2(r.listProperties(), stat);
    }

    protected void fillStats(Map<Property,Integer> counts
                         , Map<Property,ValueStat> stats)
    {
        for ( Map.Entry<Property, Integer> entry : counts.entrySet())
        {
            Property  p    = entry.getKey();
            ValueStat stat = stats.get(p);
            if ( stat == null ) { stat = new ValueStat(p.getURI()); stats.put(p, stat); }
            stat.newItem();
        }
    }

    protected void printHeader(PrintStream ps)
    {
        printSection(ps, "PROPERTIES");
        printLine(ps
                , "This section lists the properties found for the selected resources."
                , "");
        printHeaderDescription(ps);
        printSeparator(ps);
        ps.println();
    }

    protected void printHeaderDescription(PrintStream ps)
    {
        printLine(ps
                , "Meaning of the columns:"
                , "#1: Number of resources that have at least one instance of this property"
                , "#2: Percentage against the total number of resources"
                , "#3: The URI of the property");
        
    }
}
