package eu.europeana.enrich.disamb;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import static eu.europeana.edm.EDMNamespace.*;

public class SameAsDisambiguator implements ResourceDisambiguator
{
    private Property _sameAs;

    public SameAsDisambiguator() {}

    @Override
    public void init(Model m)
    {
        _sameAs = m.getProperty(OWL_SAMEAS);
    }

    @Override
    public int compare(Resource r1, Resource r2)
    {
        int i = countSameAs(r2) - countSameAs(r1);
        if ( i == 0 ) { return r1.getURI().compareTo(r2.getURI()); }
        return i;
    }

    private int countSameAs(Resource r)
    {
        int i = 0;
        StmtIterator iter = r.listProperties(_sameAs);
        while ( iter.hasNext()  ) { iter.next(); i++; }
        return i;
    }
}
