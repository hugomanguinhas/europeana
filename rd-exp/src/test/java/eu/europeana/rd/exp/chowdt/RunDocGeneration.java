/**
 * 
 */
package eu.europeana.rd.exp.chowdt;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Mar 2016
 */
public class RunDocGeneration implements WikidataCHOExpConstants
{
    public static final void main(String[] args) throws IOException
    {
        Properties cfg = new Properties();
        cfg.load(ClassLoader.getSystemResourceAsStream(KEY_CONFIG));

        String src = cfg.getProperty("chowdt.src");
        String dst = cfg.getProperty("chowdt.doc");
        if ( src == null || dst == null ) { return; }

        new WikidataDocGenerator(cfg).generate(new File(src), new File(dst));
    }
}