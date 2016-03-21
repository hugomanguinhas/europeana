/**
 * 
 */
package eu.europeana.rd.exp.chowdt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import eu.europeana.anno.api.AnnotationAPI;
import eu.europeana.anno.api.config.AnnotationConfig;
import eu.europeana.anno.api.impl.AnnotationAPIimpl;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 21 Mar 2016
 */
public class WikidataAnnoGenerator
{
    private static CSVFormat _format  = CSVFormat.EXCEL;
    private static Charset   _charset = Charset.forName("UTF-8");

    private AnnotationAPI<Map> _api;

    public WikidataAnnoGenerator(AnnotationAPI<Map> api) { _api = api; }

    public void generate(File src, File dst) throws IOException
    {
        CSVPrinter printer = null;
        CSVParser  parser  = null;
        try {
             printer = new CSVPrinter(new PrintStream(dst), _format);
             parser  = CSVParser.parse(src, _charset, _format);

            for ( CSVRecord record : parser )
            {
                String eid = "http://data.europeana.eu/item/" + record.get(0);
                Map ret = _api.newSemanticTag(eid, record.get(1));
                if ( ret == null ) { System.err.println("error"); continue; }

                printer.printRecord(eid, record.get(1), ret.get("@id"));
            }
            printer.flush();
        }
        finally
        {
            if ( printer != null ) { printer.close(); }
            if ( parser  != null ) { parser.close();  }
        }
    }
}
