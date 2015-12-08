/**
 * 
 */
package eu.europeana.edm.shapes;

import java.io.InputStream;

import org.topbraid.shacl.vocabulary.SH;
import org.topbraid.spin.util.JenaUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileUtils;

import static eu.europeana.edm.shapes.ShapesConstants.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 8 Dec 2015
 */
public class ShapesUtils
{
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
}
