/**
 * 
 */
package eu.europeana.edm.shapes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.rdf.model.Model;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 8 Dec 2015
 */
public interface RecordValidator
{
    public Model validate(Model data);
    public Model validate(File file     , String mime) throws IOException;
    public Model validate(InputStream in, String mime) throws IOException;
    public Model validate(String urn)                  throws IOException;
}
