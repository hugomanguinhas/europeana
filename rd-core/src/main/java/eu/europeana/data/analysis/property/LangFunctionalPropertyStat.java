package eu.europeana.data.analysis.property;

import com.hp.hpl.jena.rdf.model.Property;

public class LangFunctionalPropertyStat extends DefaultPropertyStat 
{
    public LangFunctionalPropertyStat(Property property) { this(property, false); }

    public LangFunctionalPropertyStat(Property property, boolean inversed)
    {
        super(property);
    }
}
