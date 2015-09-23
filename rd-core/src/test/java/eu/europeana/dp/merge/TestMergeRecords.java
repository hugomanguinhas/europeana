/**
 * 
 */
package eu.europeana.dp.merge;

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
public class TestMergeRecords
{

    public static final void main(String... args) throws TransformerException
    {
        File dir  = new File("D:\\work\\incoming\\cecile");
        File file = new File(dir, "ESEmerge.xsl");
        File src  = new File(dir, "08566-1.xml");
        File dst  = new File(dir, "08566-1.merged.xml");

        Transformer t = TransformerFactory.newInstance().newTransformer(new StreamSource(file));
        t.transform(new StreamSource(src), new StreamResult(dst));
    }
}
