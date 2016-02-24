/**
 * 
 */
package eu.europeana.edm.shapes;

import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.topbraid.spin.util.JenaUtil;

import eu.europeana.edm.shapes.ShapesConstants.ShapesType;
import static eu.europeana.edm.shapes.ShapesConstants.*;
import static eu.europeana.jena.JenaUtils.*;
import static eu.europeana.rdf.FormatSupport.*;
import static eu.europeana.edm.shapes.ShapesUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 8 Dec 2015
 */
public class RunValidator
{
    public static final void main(String[] args) throws Exception
    {
        runInclosedOntology();
    }

    private static void runExternalFull() throws IOException
    {
        runShape("/etc/edm/data/external/example1.xml"
               , "/etc/edm/shapes/external/edm_full.ttl");
    }

    private static void runExternalSplitted() throws IOException
    {
        runShape("/etc/edm/data/external/example1.xml"
               , "/etc/edm/shapes/external/edm_split.ttl");
    }

    private static void runInclosedOntology() throws IOException
    {
        runShape("/etc/edm/data/external/example1.xml"
               , "/etc/edm/shapes/external/edm-provided-cho.ttl");
    }

    private static void runShape(String example, String shapes)
            throws IOException
    {
        Class c = RunValidator.class;
        InputStream isShapes  = c.getResourceAsStream(shapes);
        InputStream isExample = c.getResourceAsStream(example);

        Model vModel = TopBraidValidator.getValidationModel(
                getShapesModel(isShapes));

        store(new TopBraidValidator(vModel)
            .validate(isExample, RDFXML.getMimetype()), System.out);
    }
}
