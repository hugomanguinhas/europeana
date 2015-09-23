package eu.europeana.enrich;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import eu.europeana.utils.CSVWriter;

public class EnrichmentUtils {

    public static Map<String,Integer> loadEnrichmentHits(File file)
    {
        Map<String,Integer> ret = new HashMap();
        if ( file == null ) { return ret; }

        try {
            BufferedReader r = null;
            try {
                r = new BufferedReader(new FileReader(file));

                String sLine;
                while ((sLine = r.readLine()) != null)
                {
                    String[] sa = sLine.trim().split(";");
                    if ( sa.length < 2 ) { continue; }

                    try {
                        ret.put(sa[0], Integer.parseInt(sa[1]));
                    }
                    catch (NumberFormatException e) {}
                }
            }
            finally {
                if (r != null) { r.close(); }
            }
        }
        catch (IOException e) {}
        return ret;
    }

    public static Map<String,Integer> loadPortalHits(File file)
    {
        Map<String,Integer> ret = new HashMap();
        if ( file == null ) { return ret; }

        try {
            CSVParser parser = CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.EXCEL);
            Iterator<CSVRecord> iter = parser.iterator();
            while ( iter.hasNext() )
            {
                CSVRecord record = iter.next();
                if ( record.size() < 2 ) { continue; }

                try {
                    ret.put(record.get(0), Integer.parseInt(record.get(1)));
                }
                catch (NumberFormatException e) {}
            }
        }
        catch (IOException e) { System.err.println("Error loading file: " + file.getName()); }

        return ret;
    }

    public static void updatePortalHits(Map<String,Integer> hits, File file)
    {
        if ( file == null ) { return; }

        CSVWriter adapter = new CSVWriter(file);
        adapter.start();
        for ( String k : hits.keySet() ) { adapter.println(k, hits.get(k)); }
        adapter.end();
    }
}
