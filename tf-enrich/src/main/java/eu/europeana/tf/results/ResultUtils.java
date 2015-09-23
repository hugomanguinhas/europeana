package eu.europeana.tf.results;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import eu.europeana.utils.CSVWriter;

public class ResultUtils
{
    private static String[] SCHEMES
        = { "http://culturaitalia.it/pico/thesaurus/"
          , "http://www.eionet.europa.eu/gemet/"
          , "http://d-nb.info/gnd/"
          , "http://purl.org/heritagedata/"
          , "http://purl.org/dismarc/"
          , "http://semium.org/time/"
          , "http://skos.um.es/unescothes/" };

    private static Pattern PATTERN = Pattern.compile("(https?://[^/]*/).*");

    public static String getScheme(String uri)
    {
        for ( String scheme : SCHEMES )
        {
            if ( uri.startsWith(scheme) ) { return scheme; }
        }
        return getDomain(uri);
    }

    public static Collection<EnrichmentResult> loadEnrichments(
            String set, File f, Collection<EnrichmentResult> results)
    {
        System.out.println("Loading result file: " + f.getName());
        try {
            CSVParser parser = CSVParser.parse(f, Charset.forName("UTF-8"), CSVFormat.EXCEL);
            Iterator<CSVRecord> iter = parser.iterator();
            while ( iter.hasNext() ) { loadResult(set, iter.next(), results); }
        }
        catch (IOException e) { System.err.println("Error loading file: " + f.getName()); }
        return results;
    }

    public static Collection<EnrichmentResult> loadEnrichments(String set, File f)
    {
        return loadEnrichments(set, f, new TreeSet());
    }

    public static Map<String,Collection<EnrichmentResult>> getPartitionByProperty(Collection<EnrichmentResult> col)
    {
        Map<String,Collection<EnrichmentResult>> map = new TreeMap();
        for ( EnrichmentResult res : col )
        {
            String  key   = res.getProperty();
            Collection<EnrichmentResult> partition = map.get(key);
            if ( partition == null ) { partition = new TreeSet(); map.put(key, partition); }
            partition.add(res);
        }
        return map;
    }

    public static void storeEnrichments(Collection<EnrichmentResult> results, File output, boolean full)
    {
        CSVWriter printer = new CSVWriter(output);
        printer.start();
        if ( full ) {
            EnrichmentResult.printHeaderFull(printer);
            for ( EnrichmentResult result : results ) { result.printFull(printer); }
        }
        else {
            EnrichmentResult.printHeaderRaw(printer);
            for ( EnrichmentResult result : results ) { result.printRaw(printer); }
        }
        printer.end();
    }

    private static String getDomain(String uri)
    {
        Matcher m = PATTERN.matcher(uri);
        return ( m.matches() ? m.group(1) : null );
    }

    private static void loadResult(String set, CSVRecord record
                                 , Collection<EnrichmentResult> results)
    {
        EnrichmentResult result = new EnrichmentResult(set).loadRaw(record);
        if ( result != null ) { results.add(result); }
    }
}
