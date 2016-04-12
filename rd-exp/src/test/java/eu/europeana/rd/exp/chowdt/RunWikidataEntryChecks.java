/**
 * 
 */
package eu.europeana.rd.exp.chowdt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import eu.europeana.ld.deref.DereferenceChecker;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 22 Mar 2016
 */
public class RunWikidataEntryChecks implements WikidataCHOExpConstants
{

    public static final void main(String[] args) throws IOException
    {
        Properties cfg = new Properties();
        cfg.load(ClassLoader.getSystemResourceAsStream(KEY_CONFIG));

        String src   = cfg.getProperty("chowdt.src");
        String cache = cfg.getProperty("chowdt.cache.wdt");
        if ( src == null || cache == null ) { return; }

        DereferenceChecker checker = new DereferenceChecker(new File(cache),true);
        CSVParser  parser = null;
        try {
            parser = CSVParser.parse(new File(src), Charset.forName("UTF-8")
                                   , CSVFormat.EXCEL);
            for ( CSVRecord record : parser ) { checker.check(record.get(1)); }
        }
        finally { IOUtils.closeQuietly(parser); }
    }
}
