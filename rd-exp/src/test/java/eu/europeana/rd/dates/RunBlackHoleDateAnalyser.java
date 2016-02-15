/**
 * 
 */
package eu.europeana.rd.dates;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 9 Sep 2015
 */
public class RunBlackHoleDateAnalyser
{
    private static final String FILE_DCT_CREATED
        = "etc/dp/dates/dctermscreated350000.xml";
    private static final String FILE_DCT_ISSUED
        = "etc/dp/dates/dctermsissued350000.xml";

    public static void main(String... args)
           throws TransformerException, URISyntaxException
    {
        BlackHoleDateAnalyser analyser = new BlackHoleDateAnalyser();
        analyse(analyser, FILE_DCT_CREATED, FILE_DCT_ISSUED);
    }

    private static void analyse(BlackHoleDateAnalyser analyser, String... rsrcs)
            throws URISyntaxException, TransformerException
    {
        ClassLoader cl = analyser.getClass().getClassLoader();
        for ( String resource : rsrcs )
        {
            File   file = new File(cl.getResource(resource).getFile());
            String fn   = file.getName();
            File   dir  = file.getParentFile();
            analyser.analyse(file, new File(dir, fn + ".results.csv")
                           , new File(dir, fn + ".patterns.csv"));
        }
    }
}
