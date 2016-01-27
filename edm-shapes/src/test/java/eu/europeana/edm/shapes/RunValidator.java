/**
 * 
 */
package eu.europeana.edm.shapes;

import eu.europeana.edm.shapes.ShapesConstants.ShapesType;

import static eu.europeana.jena.JenaUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 8 Dec 2015
 */
public class RunValidator
{
    public static final void main(String[] args) throws Exception
    {
        String urn = "http://data.europeana.eu/item/09102/_UEDIN_214";
        store(new TopBraidValidator(ShapesType.EXTERNAL).validate(urn)
            , System.out);
    }
}
