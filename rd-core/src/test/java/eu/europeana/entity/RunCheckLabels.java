package eu.europeana.entity;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import static eu.europeana.edm.EDMNamespace.*;
import static eu.europeana.TestingResources.*;
import static eu.europeana.vocs.VocsUtils.*;

public class RunCheckLabels
{
    public static final void main(String[] args)
    {
        PrintStream out = System.out;

        Model m = loadModel(FILE_AGENTS_DBPEDIA_SRC);

        Set<Resource> noLang   = new HashSet();
        Set<Resource> dupLbl   = new HashSet();
        Set<Resource> dupEnLbl = new HashSet();
        ResIterator iter = m.listSubjects();
        while ( iter.hasNext() ) { checkResource(iter.next(), noLang, dupEnLbl, dupLbl); }

        //print
        out.println("Resource with no language: " + noLang.size());
        //for ( Resource rsrc : noLang ) { out.println(rsrc.getURI()); }

        out.println("Resource with duplicate en labels: " + dupEnLbl.size());
        //for ( Resource rsrc : dupEnLbl ) { out.println(rsrc.getURI()); }

        out.println("Resource with duplicate labels in other langs: " + dupLbl.size());
        //for ( Resource rsrc : dupLbl ) { out.println(rsrc.getURI()); }
    }

    private static final void checkResource(Resource rsrc
            , Set<Resource> noLang, Set<Resource> dupEnLbl
            , Set<Resource> dupLbl)
    {
        Map<String,Integer> stat = new HashMap();

        Property p = rsrc.getModel().getProperty(SKOS_PREF_LABEL);
        StmtIterator iter = rsrc.listProperties(p);
        while ( iter.hasNext() )
        {
            RDFNode node = iter.next().getObject();
            if ( node.isURIResource() ) { continue; }

            String lang = node.asLiteral().getLanguage();
            if ( lang == null || lang.trim().isEmpty() ) { noLang.add(rsrc); continue; }

            lang = lang.trim();
            Integer i = stat.get(lang);
            stat.put(lang, i == null ? 1 : i + 1);
        }

        for ( String lang : stat.keySet() )
        {
            Integer i = stat.get(lang);
            if ( i <= 1 ) { continue; }

            if ( lang.equals("en") ) { dupEnLbl.add(rsrc); }
            else { dupLbl.add(rsrc); }
        }

        System.out.println(stat);
    }
}
