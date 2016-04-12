/**
 * 
 */
package eu.europeana.rd.exp.chowdt;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 17 Mar 2016
 */
public class RunWikidataCHOExperiment implements WikidataCHOExpConstants
{
    public static final void main(String[] args) throws IOException
    {
        Properties cfg = new Properties();
        cfg.load(ClassLoader.getSystemResourceAsStream(KEY_CONFIG));

        String src = cfg.getProperty("chowdt.src");
        if ( src == null ) { return; }

        File file = new File(src);
        file.getParentFile().mkdirs();

        WikidataCHOExperiment exp = new WikidataCHOExperiment(cfg);
        exp.run(file);
    }
}
