/**
 * 
 */
package eu.europeana.rd.exp.chowdt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import eu.europeana.anno.api.AnnotationAPI;
import eu.europeana.anno.api.AnnotationConstants;
import eu.europeana.anno.api.config.AnnotationConfig;
import eu.europeana.anno.api.config.SoftwareAgent;
import eu.europeana.anno.api.impl.AnnotationAPIimpl;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Mar 2016
 */
public class RunWikidataToEuropeanaLinks implements AnnotationConstants
{
    private static SoftwareAgent _agent  = new SoftwareAgent(
            null, "Wikidata Coref Extractor"
          , "https://github.com/hugomanguinhas/");

    public static final void main(String[] args) throws IOException
    {
        File dir = new File("D:\\work\\github\\rd-exp\\src\\test\\resources\\etc\\chowdt\\");
        File src = new File(dir, "links280.csv");
        File dst = new File(dir, "links280_anno.csv");

        AnnotationConfig   cfg = new AnnotationConfig(ENDPOINT_TEST, "apidemo", _agent);
        AnnotationAPI<Map> api = new AnnotationAPIimpl(cfg);

        new WikidataAnnoGenerator(api).generate(src, dst);
    }
}
