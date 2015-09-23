package eu.europeana.entity.analysis;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import eu.europeana.enrich.YorgosUtils;
import static eu.europeana.edm.EDMNamespace.*;
import static eu.europeana.vocs.VocsUtils.*;

public class AgentEnrichIssuesAnalyser
{
    private static List<String> ignore = Arrays.asList("ja", "zh", "ko");

    public void analyse(File src)
    {
        Collection<String> list = new TreeSet();
        Model m = loadModel(src);

        ResIterator iter = m.listResourcesWithProperty(m.getProperty(RDF_TYPE)
                                                     , m.getResource(EDM_AGENT));
        while ( iter.hasNext() )
        {
            Resource rsrc = iter.next();
            if ( check(rsrc) ) { list.add(rsrc.getURI()); }
        }

        for ( String s : list ) { System.out.println(s); }
    }

    public boolean check(Resource r)
    {
        Property     p    = r.getModel().getProperty(SKOS_PREF_LABEL);
        StmtIterator iter = r.listProperties(p);
        
        while ( iter.hasNext() )
        {
            if ( checkLiteral(iter.next().getLiteral()) ) { return true; }
        }
        return false;
    }

    private boolean checkLiteral(Literal l)
    {
        String lang = l.getLanguage();
        if ( ignore.contains(lang) ) { return false; }
        
        String str = YorgosUtils.cleanExcess(l.getString());
        return ( str.split(" ").length <= 1);
    }
}
