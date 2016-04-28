/**
 * 
 */
package eu.europeana.rd.exp.chowdt;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.rdf.model.Literal;

import static org.apache.commons.io.IOUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 17 Mar 2016
 */
public class EntrySet extends TreeSet<EntrySet.Entry>
{
    public void newEntry(String cho, String wdt)
    {
        if ( add(new Entry(cho, wdt)) ) { return; }
        System.err.println("Duplicate entry: " + cho + ", " + wdt);
    }

    public Map<String,List<Entry>> getDuplicates()
    {
        Map<String,List<Entry>> dups = new TreeMap();

        for ( Entry entry : this )
        {
            addToMap(dups, entry.cho, entry);
            addToMap(dups, entry.wdt, entry);
        }

        Iterator<List<Entry>> iter = dups.values().iterator();
        while ( iter.hasNext() )
        {
            if ( iter.next().size() == 1) { iter.remove(); }
        }
        return dups;
    }


    public void storeToCSV(File file) throws IOException
    {
        CSVPrinter p = new CSVPrinter(new PrintStream(file), CSVFormat.EXCEL);
        try {
            for ( Entry entry : this ) { p.printRecord(entry.cho, entry.wdt); }
            p.flush();
        }
        finally { closeQuietly(p); }
    }

    public EntrySet loadFromCVS(File file)
    {
        CSVParser  parser = null;
        try {
            parser = CSVParser.parse(file, Charset.forName("UTF-8")
                                   , CSVFormat.EXCEL);
            for ( CSVRecord r : parser ) { newEntry(r.get(0), r.get(1)); }
        }
        catch (IOException e) { e.printStackTrace();  }
        finally               { closeQuietly(parser); }

        return this;
    }

    private void addToMap(Map<String,List<Entry>> map
                        , String key, Entry entry)
    {
        List<Entry> list = map.get(key);
        if ( list == null ) { list = new ArrayList(1); map.put(key, list); }
        list.add(entry);
    }


    static class Entry implements Comparable<Entry>
    {
        public final String cho;
        public final String wdt;

        public Entry(String cho, String wdt)
        {
            this.cho = cho;
            this.wdt = wdt;
        }

        public int compareTo(Entry e)
        {
            int ret = this.cho.compareTo(e.cho);
            return (ret != 0 ? ret : this.wdt.compareTo(e.wdt));
        }
    }
}
