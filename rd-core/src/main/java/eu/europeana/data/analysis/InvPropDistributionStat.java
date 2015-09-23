package eu.europeana.data.analysis;

import java.io.PrintStream;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class InvPropDistributionStat extends PropDistributionStat {

    public InvPropDistributionStat() { super(); }

    @Override
    protected void fillCounts(Resource r, Map<Property,Integer> counts)
    {
        fillCounts(r.getModel().listStatements(null, null, r), counts);
    }

    @Override
    protected void printHeader(PrintStream ps)
    {
        ps.println("* INVERSE PROPERTY STATISTICS *");
    }
}
