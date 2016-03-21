/**
 * 
 */
package eu.europeana.rd.exp.chowdt;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.hp.hpl.jena.rdf.model.Literal;

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

    public void toCSV(File file) throws IOException
    {
        CSVPrinter p = new CSVPrinter(new PrintStream(file), CSVFormat.EXCEL);
        try {
            for ( Entry entry : this ) { p.printRecord(entry.cho, entry.wdt); }
            p.flush();
        }
        finally { p.close(); }
    }

    private String getKey(Map<String,String> map, String value)
    {
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            if ( entry.getValue().equals(value) ) { return entry.getKey(); }
        }
        return null;
    }

    private void addToMap(Map<String,List<Entry>> map
                        , String key, Entry entry)
    {
        List<Entry> list = map.get(key);
        if ( list == null ) { list = new ArrayList(1); map.put(key, list); }
        list.add(entry);
    }

    private void newDuplicate(String cho, String wdt1, String wdt2)
    {
        System.err.println("Found duplicate: " + cho
                         + ", " + wdt1 + ", " + wdt2);
    }

    private String getURI(Literal europeanaID)
    {
        return ("http://data.europeana.eu/item/" + europeanaID.getString());
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
