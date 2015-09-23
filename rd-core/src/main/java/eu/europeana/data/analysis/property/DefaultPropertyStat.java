package eu.europeana.data.analysis.property;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

import eu.europeana.data.analysis.ValueStat;

public class DefaultPropertyStat implements PropertyStat
{
    private Property                _property;
    private Map<RDFNode,ValueStat>  _values;
    private boolean                 _inversed;

    public DefaultPropertyStat(Property property)
    {
        this(property, false);
    }

    public DefaultPropertyStat(Property property, boolean inversed)
    {
        _property = property;
        _inversed = inversed;
        _values   = new HashMap();
    }

    public Property getProperty() { return _property; }

    public boolean  isInversed()  { return _inversed; }

    public void newPropertyValue(RDFNode node)
    {
        if ( _values != null ) { newValue(node); }
    }

    public void print(PrintStream ps, int total)
    {
        ps.println("<" + _property.toString() + ">:");

        for ( ValueStat s : new TreeSet<ValueStat>(_values.values()))
        {
            ps.print("\t");
            s.print(ps, total);
        }
    }

    private void newValue(RDFNode node)
    {
        ValueStat stat = _values.get(node);
        if ( stat == null ) {
            stat = new ValueStat(node.toString());
            _values.put(node, stat);
        }
        stat.newItem();
    }
}
