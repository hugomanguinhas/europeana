package eu.europeana.data.analysis;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.hp.hpl.jena.rdf.model.Resource;

public class TypeStat {

    private ObjectStat               _parent;

    private Map<Resource,ValueStat> _stat;

    public TypeStat(ObjectStat parent)
    {
        _parent = parent;
        _stat   = new HashMap();
    }

    public void newType(Resource type)
    {
        ValueStat stat = _stat.get(type);
        if ( stat == null ) { 
            stat = new ValueStat(type.getURI());
            _stat.put(type, stat);
        }
        stat.newItem();
    }

    public void print(PrintStream ps)
    {
        ps.println("* TYPE STATISTICS *");
        Collection<ValueStat> col = new TreeSet(_stat.values());
        int total = _parent.getTotal();
        for ( ValueStat p : col ) { p.print(ps, total); }
    }
}
