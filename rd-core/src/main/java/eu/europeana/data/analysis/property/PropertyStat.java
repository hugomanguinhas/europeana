package eu.europeana.data.analysis.property;

import java.io.PrintStream;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

public interface PropertyStat {

    public Property getProperty();

    public boolean  isInversed();

    public void newPropertyValue(RDFNode node);

    public void print(PrintStream ps, int total);
}
