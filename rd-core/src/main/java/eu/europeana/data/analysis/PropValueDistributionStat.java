package eu.europeana.data.analysis;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import eu.europeana.data.analysis.property.DefaultPropertyStat;
import eu.europeana.data.analysis.property.PropertyStat;

public class PropValueDistributionStat extends AbsStat {

    private Map<Property,PropertyStat> _propValues;

    public PropValueDistributionStat()
    {
        _propValues = new HashMap();
    }

    public void addPropertyValue(PropertyStat stat)
    {
        Property p = stat.getProperty();
        if ( !_propValues.containsKey(p) ) { _propValues.put(p, stat); }
    }

    public void newResource(Resource r)
    {
        Model m = r.getModel();
        for ( Property p : _propValues.keySet() )
        {
            PropertyStat stat = _propValues.get(p);
            if ( stat.isInversed() ) {
                StmtIterator iter = m.listStatements(null, p, r);
                while ( iter.hasNext() )
                {
                    stat.newPropertyValue(iter.next().getSubject());
                }
            }
            else {
                StmtIterator iter = r.listProperties(p);
                while ( iter.hasNext() )
                {
                    stat.newPropertyValue(iter.next().getObject());
                }
            }
        }
    }

    public void print(PrintStream ps, int total)
    {
        if ( _propValues.isEmpty() ) { return; }

        printHeader(ps);
        for ( PropertyStat p : _propValues.values() )
        {
            if ( !(p instanceof DefaultPropertyStat) ) { continue; }
            p.print(ps, total);
            ps.println();
        }
        ps.println();

        for ( PropertyStat p : _propValues.values() )
        {
            if ( p instanceof DefaultPropertyStat ) { continue; }
            p.print(ps, total);
            ps.println();
        }
    }

    protected void printHeader(PrintStream ps)
    {
        printSection(ps, "PROPERTY VALUES STATISTICS");
        printLine(ps
                , "This section lists the values found for a specific property."
                , ""
                , "Meaning of the columns:"
                , "#1: Number of properties with this literal"
                , "#2: Percentage against the total number of resources"
                , "#3: The literal value and language if defined (see @)");
        printSeparator(ps);
        ps.println();
    }
}
