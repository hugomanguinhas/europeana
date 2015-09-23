package eu.europeana.vocs.conceptexp;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import eu.europeana.data.analysis.LangDistributionStat;
import eu.europeana.data.analysis.LangStat;
import eu.europeana.data.analysis.ObjectStat;
import eu.europeana.vocs.babelnet.BabelNetAnalysis;
import eu.europeana.vocs.dbpedia.DBPediaAnalysis;
import eu.europeana.vocs.eurovoc.EurovocAnalysis;
import eu.europeana.vocs.freebase.FreebaseAnalysis;
import eu.europeana.vocs.lexvo.LexvoAnalysis;
import static eu.europeana.vocs.VocsUtils.*;

public class MultiVocConceptAnalysis {

    private static DecimalFormat FORMAT_PERCENT = new DecimalFormat("0%");

    public void analyse(File dest) throws IOException
    {
        List<ObjectStat> stats = new ArrayList<ObjectStat>();
        stats.add(new DBPediaAnalysis().analyse(
                DBPediaConceptAnalysis.SRCLIST, DBPediaConceptAnalysis.SRC, null));
        stats.add(new FreebaseAnalysis().analyse(
                FreebaseConceptAnalysis.SRCLIST, FreebaseConceptAnalysis.SRC, null));
        stats.add(new BabelNetAnalysis().analyse(
                BabelNetConceptAnalysis.SRCLIST, BabelNetConceptAnalysis.SRC, null));
        stats.add(new LexvoAnalysis().analyse(
                LexvoConceptAnalysis.SRCLIST, LexvoConceptAnalysis.SRC, null));
        stats.add(new EurovocAnalysis().analyse(
                EurovocConceptAnalysis.SRCLIST, EurovocConceptAnalysis.SRC, null));

        Collection<LangStat> set = getLanguageStat(stats);

        PrintStream ps = new PrintStream(dest, "UTF-8");
        printCoverage(ps, stats);
        ps.println();
        printLangs(ps, stats, set);
    }

    private void printColumns(PrintStream ps, String label, List<ObjectStat> stats, String... saExtra)
    {
        ps.print(label);
        for ( ObjectStat stat : stats )
        {
            ps.print(',');
            ps.print(stat.getVocName());
        }
        for ( String sExtra : saExtra ) { ps.print(','); ps.print(sExtra); }
        ps.println();
    }

    private void printCoverage(PrintStream ps, List<ObjectStat> stats)
    {
        printColumns(ps, "", stats);

        ps.print("N. of Concepts");
        for ( ObjectStat stat : stats )
        {
            ps.print(',');
            ps.print(stat.getTotal());
        }
        ps.println();
    }

    private void printLangs(PrintStream ps
                          , List<ObjectStat> stats, Collection<LangStat> set)
    {
        printColumns(ps, "Lang", stats, "Language description");

        //print totals
        ps.print("N. of Langs");
        for ( ObjectStat stat : stats )
        {
            ps.print(',');
            ps.print(stat.getLangStats().getStats().size());
        }
        ps.println();
        

        for ( LangStat langStat : set )
        {
            String sLang = langStat.getLanguage();
            if ( sLang.contains("?") ) { continue; }

            ps.print(sLang);
            for ( ObjectStat stat : stats )
            {
                ps.print(',');

                LangStat vocStat = stat.getLangStats().getStat(sLang);
                double d = (vocStat == null ? 0 : vocStat.getCount());
                d = d / stat.getTotal();
                ps.print(FORMAT_PERCENT.format(d));
            }
            
            ps.print(',');
            ps.print(getLanguageName(sLang));

            ps.println();
        }
    }

    private String getLanguageName(String sLang)
    {
        Locale l = Locale.forLanguageTag(sLang);
        return (l == null ? "?" : l.getDisplayName());
    }

    private Collection<LangStat> getLanguageStat(List<ObjectStat> stats)
    {
        Map<String,LangStat> ret = new TreeMap<String,LangStat>();
        for ( ObjectStat stat : stats )
        {
            LangDistributionStat langDist = stat.getLangStats();
            for ( LangStat lang : langDist.getStats() )
            {
                String sLang = lang.getLanguage();
                LangStat s = ret.get(sLang);
                if ( s == null ) { s = new LangStat(sLang); ret.put(sLang, s); }
                s.setCount(Math.max(s.getCount(), lang.getCount()));
            }
        }

        return new TreeSet<LangStat>(ret.values());
    }

    public static void main( String[] args ) throws IOException
    {
        File dest = new File(DIR_EXP, "vocs.concept.csv");
        new MultiVocConceptAnalysis().analyse(dest);
    }
}