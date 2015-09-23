package eu.europeana.data.analysis.property;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

import eu.europeana.data.analysis.AbsStat;
import eu.europeana.data.analysis.ValueStat;

import static eu.europeana.edm.EDMNamespace.*;

public class LinkSetPropertyStat extends AbsStat implements PropertyStat
{
    private static Pattern PATTERN = Pattern.compile("(https?://[^/]*/).*");

    private Property              _property;

    private Map<String,ValueStat> _values;

    private boolean               _inversed;

    public LinkSetPropertyStat(Model m) { this(m.getProperty(OWL_SAMEAS), false); }

    public LinkSetPropertyStat(Property property) { this(property, false); }

    public LinkSetPropertyStat(Property property, boolean inversed)
    {
        _property = property;
        _inversed = inversed;
        _values   = new HashMap();
    }

    public Property getProperty() { return _property; }

    public boolean  isInversed()  { return _inversed; }

    public void newPropertyValue(RDFNode node)
    {
        if ( !node.isResource() ) { return; }

        String uri = node.asResource().getURI();
        String domain = getDomain(uri);
        if ( domain == null ) { return; }

        ValueStat stat = _values.get(domain);
        if ( stat == null ) {
            stat = new ValueStat(domain);
            _values.put(domain, stat);
        }
        stat.newItem();
    }

    public void print(PrintStream ps, int total)
    {
        printHeader(ps);
        for ( ValueStat s : new TreeSet<ValueStat>(_values.values()))
        {
            ps.print("\t");
            s.print(ps, total);
        }
    }

    private void printHeader(PrintStream ps)
    {
        printSection(ps, "SAMEAS STATISTICS");
        printLine(ps
                , "This section shows the dataset distribution of sameAs links"
                , ""
                , "Meaning of the columns:"
                , "#1: Number of sameAs properties for this domain"
                , "#2: Percentage against the total number of resources"
                , "#3: The URL of the domain");
        printSeparator(ps);
        ps.println();
    }

    private static String getDomain(String uri)
    {
        Matcher m = PATTERN.matcher(uri);
        return ( m.matches() ? m.group(1) : null );
    }
}
