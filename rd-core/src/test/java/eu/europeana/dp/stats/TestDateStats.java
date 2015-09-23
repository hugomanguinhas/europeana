/**
 * 
 */
package eu.europeana.dp.stats;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 9 Sep 2015
 */
public class TestDateStats
{

    public static final void main(String... args) throws TransformerException
    {
        File dir  = new File("D:\\work\\incoming\\pablo");
        File src1  = new File(dir, "dctermscreated350000.xml");
        File src2  = new File(dir, "dctermsissued350000.xml");

        File dst11 = new File(dir, "result\\created_result.csv");
        File dst12 = new File(dir, "result\\created_patterns.csv");

        File dst21 = new File(dir, "result\\issued_result.csv");
        File dst22 = new File(dir, "result\\issued_patterns.csv");

        StreamSource xslt1 = new StreamSource(TestDateStats.class.getResourceAsStream("DateExport.xsl"));
        StreamSource xslt2 = new StreamSource(TestDateStats.class.getResourceAsStream("DatePatterns.xsl"));

        Transformer t;
        t = TransformerFactory.newInstance().newTransformer(xslt1);
        t.transform(new StreamSource(src1), new StreamResult(dst11));
        t.transform(new StreamSource(src2), new StreamResult(dst21));

        t = TransformerFactory.newInstance().newTransformer(xslt2);
        t.transform(new StreamSource(src1), new StreamResult(dst12));
        t.transform(new StreamSource(src2), new StreamResult(dst22));
    }
}
