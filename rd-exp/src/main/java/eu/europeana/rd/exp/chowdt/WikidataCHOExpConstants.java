/**
 * 
 */
package eu.europeana.rd.exp.chowdt;

import java.io.File;

import org.apache.jena.util.FileUtils;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 22 Mar 2016
 */
public interface WikidataCHOExpConstants
{
    public static final String KEY_CONFIG        = "etc/chowdt/config.prop";
    public static final String KEY_SPARQL        = "wikidata.sparql";
    public static final String KEY_RESOURCES_DIR = "chowdt.resources";
    public static final String KEY_DOC_DIR       = "chowdt.doc";

    public static File getFile(File master, String suffix)
    {
        String name = master.getName();
        String ext  = FileUtils.getFilenameExt(name);
        name = name.substring(0, name.length() - ext.length() - 1)
             + "_" + suffix + "." + ext;
        return new File(master.getParentFile(), name);
    }
}
