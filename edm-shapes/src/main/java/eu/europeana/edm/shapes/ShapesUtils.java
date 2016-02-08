/**
 * 
 */
package eu.europeana.edm.shapes;

import java.io.InputStream;
import java.io.PrintStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.topbraid.shacl.vocabulary.SH;
import org.topbraid.spin.util.JenaUtil;

import static eu.europeana.edm.shapes.ShapesConstants.*;
import static eu.europeana.edm.shapes.SHACLNamespace.*;
import static eu.europeana.edm.EDMNamespace.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 8 Dec 2015
 */
public class ShapesUtils
{
    /***************************************************************************
     * Public Methods
     **************************************************************************/

    public static Model getSHACL()
    {
        Model m = JenaUtil.createDefaultModel();
        InputStream is = SH.class.getResourceAsStream("/etc/shacl.ttl");
        m.read(is, SH.BASE_URI, FileUtils.langTurtle);
        return m;
    }

    // Load the shapes Model (here, includes the dataModel because that has templates in it)
    public static Model getShapesForEDMExternal()
    {
        Class c = ShapesUtils.class;
        Model model = JenaUtil.createMemoryModel();
        model.read(c.getResourceAsStream(EDM_EXTERNAL_SHAPES_LOCATION)
                 , "urn:dummy", FileUtils.langTurtle);
        return model;
    }

    public static void print(Model results, PrintStream ps)
    {
        ps.println(results);
        ResIterator iter = results.listResourcesWithProperty(
                results.getProperty(RDF_TYPE)
              , results.getResource(SHACL_RESULT));
        while(iter.hasNext()) { print(iter.nextResource(), ps); }
    }

    private static void print(Resource r, PrintStream ps)
    {
        System.out.println(r);
    }
}
