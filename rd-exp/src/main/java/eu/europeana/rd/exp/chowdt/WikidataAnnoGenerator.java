/**
 * 
 */
package eu.europeana.rd.exp.chowdt;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import eu.europeana.anno.api.AnnotationAPI;

import static org.apache.commons.io.IOUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 21 Mar 2016
 */
public class WikidataAnnoGenerator
{
    private static CSVFormat _format  = CSVFormat.EXCEL;
    private static String    _charset = "UTF-8";

    private AnnotationAPI<Map> _api;

    public WikidataAnnoGenerator(AnnotationAPI<Map> api) { _api = api; }

    public void generate(EntrySet set, File dst) throws IOException
    {
        CSVPrinter printer = null;
        try {
            printer = new CSVPrinter(new PrintStream(dst, _charset), _format);
            for ( EntrySet.Entry entry : set )
            {
                Map ret = _api.newSemanticTag(entry.cho, entry.wdt);
                if ( ret == null ) { System.err.println("error"); continue; }

                printer.printRecord(entry.cho, entry.wdt, ret.get("@id"));
            }
            printer.flush();
        }
        finally { closeQuietly(printer); }
    }

    public void generate(File src, File dst) throws IOException
    {
        generate(new EntrySet().loadFromCVS(src), dst);
    }
}
