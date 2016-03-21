/**
 * 
 */
package eu.europeana.rd.exp.chowdt;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import eu.europeana.rd.exp.chowdt.EntrySet.Entry;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 17 Mar 2016
 */
public class RunWikidataCHOExperiment
{
    public static final void main(String[] args) throws IOException
    {
        File dir   = new File("D:\\work\\github\\rd-exp\\src\\test\\resources\\");
        File file1 = new File(dir, "etc\\chowdt\\links.csv");
        File file2 = new File(dir, "etc\\chowdt\\links280.csv");
        File file3 = new File(dir, "etc\\chowdt\\dups.csv");

        WikidataCHOHarvester harvester = new WikidataCHOHarvester();

        EntrySet results = harvester.fetch();
        EntrySet res280  = new EntrySet();

        for ( EntrySet.Entry entry : results )
        {
            if ( entry.cho.contains("_280_") ) { res280.add(entry); }
        }

        results.toCSV(file1);
        res280.toCSV(file2);

        CSVPrinter p = new CSVPrinter(new PrintStream(file3), CSVFormat.EXCEL);
        Map<String,List<Entry>> dups = results.getDuplicates();
        for ( String key : dups.keySet() )
        {
            p.print(key);
            for ( Entry entry : dups.get(key) )
            {
                if ( !key.equals(entry.cho) ) { p.print(entry.cho); }
                if ( !key.equals(entry.wdt) ) { p.print(entry.wdt); }
            }
            p.println();
        }
        p.flush();
        p.close();
    }
}
