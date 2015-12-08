/**
 * 
 */
package eu.europeana.edm.shapes;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 8 Dec 2015
 */
public interface RecordValidator
{
    public Model validate(Model data);
}
