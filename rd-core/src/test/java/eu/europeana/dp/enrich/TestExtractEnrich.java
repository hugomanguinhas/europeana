/**
 * 
 */
package eu.europeana.dp.enrich;

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
public class TestExtractEnrich
{

    public static final void main(String... args) throws TransformerException
    {
        File dir  = new File("D:\\work\\incoming\\F&D");
        File src  = new File(dir, "collection_1.xml");
        File dst1 = new File(dir, "collection_1_int.xml");
        File dst2 = new File(dir, "collection_1_ext.xml");

        StreamSource xslt1 = new StreamSource(TestExtractEnrich.class.getResourceAsStream("EnrichExtractorInt.xsl"));
        StreamSource xslt2 = new StreamSource(TestExtractEnrich.class.getResourceAsStream("EnrichExtractorExt.xsl"));

        Transformer t;
        t = TransformerFactory.newInstance().newTransformer(xslt1);
        t.transform(new StreamSource(src), new StreamResult(dst1));

        t = TransformerFactory.newInstance().newTransformer(xslt2);
        t.transform(new StreamSource(src), new StreamResult(dst2));
}
}
