package eu.europeana.data.analysis;

import java.util.Collection;
import java.util.HashSet;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class AnalysisUtil {

    public static Collection<String> getPropertyURIs(Model m)
    {
        Collection<String> ret = new HashSet();
        StmtIterator iter = m.listStatements();
        while ( iter.hasNext() ) { ret.add(iter.next().getPredicate().getURI()); }
        return ret;
    }

    public static Collection<String> getPropertyURIs(ResIterator iter)
    {
        Collection<String> ret = new HashSet();
        while ( iter.hasNext() )
        {
            Resource rsrc = iter.next();

            StmtIterator iter2 = rsrc.listProperties();
            while ( iter2.hasNext() ) { ret.add(iter2.next().getPredicate().getURI()); }
        }
        return ret;
    }
}
