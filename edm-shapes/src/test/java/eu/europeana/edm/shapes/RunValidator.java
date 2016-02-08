/**
 * 
 */
package eu.europeana.edm.shapes;

import java.io.InputStream;

import org.topbraid.spin.util.JenaUtil;

import eu.europeana.edm.shapes.ShapesConstants.ShapesType;
import static eu.europeana.edm.shapes.ShapesConstants.EDM_EXTERNAL_SHAPES_LOCATION;
import static eu.europeana.jena.JenaUtils.*;
import static eu.europeana.rdf.FormatSupport.RDFXML;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 8 Dec 2015
 */
public class RunValidator
{
    public static final void main(String[] args) throws Exception
    {
        Class c = RunValidator.class;
        InputStream in = c.getResourceAsStream("/etc/edm/data/external/example1.xml");
        store(new TopBraidValidator(ShapesType.EXTERNAL).validate(in, RDFXML.getMimetype())
            , System.out);
    }
}
