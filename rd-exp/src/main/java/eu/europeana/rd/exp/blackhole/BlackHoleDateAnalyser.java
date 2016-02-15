/**
 * 
 */
package eu.europeana.rd.exp.blackhole;

import java.io.File;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 9 Sep 2015
 */
public class BlackHoleDateAnalyser
{
    public static String FILE_DATE_RESULTS_XSLT
        = "etc/blackhole/DateResults.xsl";
    public static String FILE_DATE_PATTERNS_XSLT
        = "etc/blackhole/DatePatterns.xsl";

    private Transformer _tDateResult;
    private Transformer _tDatePatterns;

    public BlackHoleDateAnalyser()
           throws TransformerConfigurationException
    {
        this(BlackHoleDateAnalyser.class.getClassLoader().getResourceAsStream(FILE_DATE_RESULTS_XSLT)
           , BlackHoleDateAnalyser.class.getClassLoader().getResourceAsStream(FILE_DATE_PATTERNS_XSLT));
    }

    public BlackHoleDateAnalyser(InputStream dateResult, InputStream datePatterns)
           throws TransformerConfigurationException
    {
        this(new StreamSource(dateResult), new StreamSource(datePatterns));
    }

    public BlackHoleDateAnalyser(StreamSource dateResult, StreamSource datePatterns) 
           throws TransformerConfigurationException
    {
        TransformerFactory tf = TransformerFactory.newInstance();
        _tDateResult   = tf.newTransformer(dateResult);
        _tDatePatterns = tf.newTransformer(datePatterns);
    }

    public void analyse(File src, File dateResult, File datePatterns)
           throws TransformerException
    {
        analyse(new StreamSource(src)
              , new StreamResult(dateResult), new StreamResult(datePatterns));
    }

    public void analyse(StreamSource src
                      , StreamResult dateResult, StreamResult datePatterns)
           throws TransformerException
    {
         _tDateResult  .transform(src, dateResult);
         _tDatePatterns.transform(src, datePatterns);
    }
}
