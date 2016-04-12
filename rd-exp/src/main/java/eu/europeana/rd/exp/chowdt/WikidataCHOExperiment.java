/**
 * 
 */
package eu.europeana.rd.exp.chowdt;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import eu.europeana.anno.api.AnnotationAPI;
import eu.europeana.anno.api.config.AnnotationConfig;
import eu.europeana.anno.api.config.SoftwareAgent;
import eu.europeana.anno.api.impl.AnnotationAPIimpl;
import eu.europeana.ld.deref.DereferenceChecker;
import eu.europeana.rd.exp.chowdt.EntrySet.Entry;

import static eu.europeana.rd.exp.chowdt.WikidataCHOExpConstants.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 11 Apr 2016
 */
public class WikidataCHOExperiment implements WikidataCHOExpConstants
{
    private Properties _cfg;

    public WikidataCHOExperiment(Properties cfg) { _cfg = cfg; }

    public void run(File file)
    {
        EntrySet set = harvest(file);
        set = filterInvalid(set, getFile(file, "invalid"));
        set = findDuplicates(set, getFile(file, "dup"));
        //createAnnotations(set, getFile(file, "anno"));
    }

    protected EntrySet harvest(File file)
    {
        if ( file.exists() ) { return new EntrySet().loadFromCVS(file); }

        EntrySet set = new WikidataCHOHarvester(_cfg).fetch();
        try                   { set.storeToCSV(file); }
        catch (IOException e) { e.printStackTrace();  }

        return set;
    }

    protected EntrySet filterInvalid(EntrySet set, File dst)
    {
        DereferenceChecker choChecker
            = new DereferenceChecker(getCache("chowdt.cache.cho"), true);
        DereferenceChecker wdtChecker
            = new DereferenceChecker(getCache("chowdt.cache.wdt"), true);

        CSVPrinter p = null;
        try {
            p = new CSVPrinter(new PrintStream(dst), CSVFormat.EXCEL);
            Iterator<EntrySet.Entry> iter = set.iterator();
            while ( iter.hasNext() )
            {
                EntrySet.Entry e = iter.next();
                if ( choChecker.check(e.cho)
                  && wdtChecker.check(e.wdt) ) { continue; }

                iter.remove();
                p.printRecord(e.cho, e.wdt);
            }
            p.flush();
        }
        catch (IOException e) { e.printStackTrace();     }
        finally               { IOUtils.closeQuietly(p); }

        return set;
    }

    protected EntrySet findDuplicates(EntrySet set, File dst)
    {
        CSVPrinter p = null;
        try {
            p = new CSVPrinter(new PrintStream(dst), CSVFormat.EXCEL);
            Map<String,List<Entry>> dups = set.getDuplicates();
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
        }
        catch (IOException e) { e.printStackTrace();     }
        finally               { IOUtils.closeQuietly(p); }

        return set;
    }

    protected EntrySet createAnnotations(EntrySet set, File dst)
    {
        try {
            AnnotationAPI<Map> api = new AnnotationAPIimpl(getAnnotationConfig());
            new WikidataAnnoGenerator(api).generate(set, dst);
        }
        catch (IOException e) { e.printStackTrace(); }

        return set;
    }

    private AnnotationConfig getAnnotationConfig()
    {
        String endpoint = _cfg.getProperty("chowdt.annotation.api");
        String name     = _cfg.getProperty("chowdt.annotation.agent.name");
        String homepage = _cfg.getProperty("chowdt.annotation.agent.homepage");
        String apikey   = _cfg.getProperty("chowdt.annotation.apikey");

        SoftwareAgent agent = new SoftwareAgent(null, name, homepage);
        return new AnnotationConfig(endpoint, apikey, agent);
    }

    private File getCache(String key)
    {
        String value = _cfg.getProperty(key);
        if ( value == null || value.isEmpty() ) { return null; }

        return new File(value);
    }
}
