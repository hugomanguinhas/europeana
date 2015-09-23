package eu.europeana.data.analysis;

import java.io.PrintStream;
import java.util.Collection;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import eu.europeana.data.analysis.property.DefaultPropertyStat;
import eu.europeana.data.analysis.property.PropertyStat;
import static eu.europeana.data.analysis.AnalysisUtil.*;

public class ObjectStat extends AbsStat {

    private String                     _vocName;

    private int                        _total;

    private PropDistributionStat       _propDist;

    private PropDistributionStat       _invPropDist;

    private PropValueDistributionStat  _propValueDist;

    private TypeStat                   _typeStat;

    private LangDistributionStat       _langDist;


    public ObjectStat(String vocName
                    , boolean bPropDist, boolean bInvPropDist
                    , boolean bLangDist)
    {
        _vocName        = vocName;
        _propDist       = bPropDist ? new PropDistributionStat() : null;
        _invPropDist    = bInvPropDist ? new InvPropDistributionStat() : null;
        _typeStat       = new TypeStat(this);
        _propValueDist  = new PropValueDistributionStat();
        _langDist       = bLangDist ? new LangDistributionStat() : null;
    }

    public String getVocName() { return _vocName; }

    public LangDistributionStat getLangStats() { return _langDist; }

    public void addPropertyValue(PropertyStat stat)
    {
        _propValueDist.addPropertyValue(stat);
    }

    public void addPropertyValue(Property p)
    {
        _propValueDist.addPropertyValue(new DefaultPropertyStat(p));
    }

    public void addPropertyValues(Model m)
    {
        addPropertyValues(m, getPropertyURIs(m));
    }

    public void addPropertyValues(Model m, Collection<String> saProp)
    {
        this.addPropertyValues(m, saProp, false);
    }

    public void addPropertyValues(Model m, Collection<String> saProp
                                , boolean inversed)
    {
        for ( String prop : saProp )
        {
            this.addPropertyValue(new DefaultPropertyStat(m.getProperty(prop), inversed));
        }
    }

    public void addPropertyValues(Model m, String... saProp)
    {
        addPropertyValues(m, false, saProp);
    }

    public void addPropertyValues(Model m, boolean inversed, String... saProp)
    {
        for ( String prop : saProp )
        {
            this.addPropertyValue(new DefaultPropertyStat(m.getProperty(prop), inversed));
        }
    }

    public void addPropertyValues(Model m, ResIterator iter)
    {
        addPropertyValues(m, getPropertyURIs(iter));
    }

    public void addPropertyValues(Model m, ResIterator iter, boolean inversed)
    {
        addPropertyValues(m, getPropertyURIs(iter), inversed);
    }


    public PropDistributionStat getPropertyDistributionStat() { return _propDist; }

    public int getTotal() { return _total; }

    public void newObject(Resource r)
    {
        _total++;

        if ( _langDist    != null ) { _langDist.newResource(r);    }
        if ( _propDist    != null ) { _propDist.newResource(r);    }
        if ( _invPropDist != null ) { _invPropDist.newResource(r); }

        _propValueDist.newResource(r);
    }

    public void print(PrintStream ps)
    {
        printSection(ps, "RESOURCES");
        ps.println();
        ps.println("Total n. of resources: " + _total);
        ps.println();

        if ( _propDist    != null ) { _propDist.print(ps, _total); }
        if ( _langDist    != null ) { _langDist.print(ps, _total); }
        if ( _invPropDist != null ) { _invPropDist.print(ps, _total); }

        _propValueDist.print(ps, _total);
    }

    private void fillType(Resource obj)
    {
        Property type = obj.getModel().getProperty("rdf", "type");
        StmtIterator iter = obj.listProperties(type);
        try {
            while ( iter.hasNext() )
            {
                _typeStat.newType(iter.next().getSubject());
            }
        }
        finally {
            iter.close();
        }
    }
}