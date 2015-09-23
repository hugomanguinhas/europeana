package eu.europeana.entity;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import static eu.europeana.edm.EDMNamespace.*;
import static eu.europeana.vocs.VocsUtils.loadModel;

public class RunGetRolesInLabels
{
    private static Pattern  PATTERN  = Pattern.compile(".+\\((.+)\\).*");
    private static File DIR = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\Entity Collection\\entities\\agents");

    public static final void main(String[] args)
    {
        File src = new File(DIR, "agents_dbpedia_full.xml");
        Model m = loadModel(src);
        Collection<String> roles = new TreeSet();

        ResIterator iter = m.listResourcesWithProperty(m.getProperty(RDF_TYPE));
        //while ( iter.hasNext() ) { getRolesInLabel(iter.next(), roles); }
        while ( iter.hasNext() ) { getRolesInResource(iter.next(), roles); }

        for ( String role : roles ) { System.out.println(role); }
    }

    private static void getRolesInResource(Resource rsrc, Collection<String> roles)
    {
        getRole(rsrc.getURI(), roles);
    }

    private static void getRolesInLabel(Resource rsrc, Collection<String> roles)
    {
        Model    m = rsrc.getModel();
        Property p = m.getProperty(SKOS_PREF_LABEL);
        StmtIterator iter = rsrc.listProperties(p);
        while ( iter.hasNext() )
        {
            Statement stmt = iter.next();
            if ( !stmt.getObject().isLiteral() ) { continue; }

            getRole(stmt.getString(), roles);
        }
    }

    private static void getRole(String label, Collection<String> roles)
    {
        Matcher m = PATTERN.matcher(label);
        if ( !m.matches() ) { return; }

        roles.add(m.group(1).toLowerCase().trim());
    }
}
