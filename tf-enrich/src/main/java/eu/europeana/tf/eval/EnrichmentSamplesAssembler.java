package eu.europeana.tf.eval;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import eu.europeana.tf.agreement.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class EnrichmentSamplesAssembler
{
    private DecimalFormat FORMAT = new DecimalFormat("00");

    private Map<String,List<EnrichmentAnnotation>> _annSamples;
    private Map<String,List<EnrichmentAnnotation>> _annClusterResults;
    private List<EnrichmentAnnotation>             _annResults;


    public EnrichmentSamplesAssembler()
    {
        _annSamples        = new HashMap();
        _annClusterResults = new LinkedHashMap();
        _annResults        = new ArrayList();
    }

    public void loadAnnotatedSample(String sID, File file)
    {
        String fn = file.getName();
        System.out.println("Loading result file: " + fn);
        try {
            List<EnrichmentAnnotation> sample = getSample(sID);

            CSVParser parser = CSVParser.parse(file, Charset.forName("UTF-8")
                                             , CSVFormat.EXCEL);

            Iterator<CSVRecord> iter = parser.iterator();
            if ( iter.hasNext() ) { iter.next(); }

            while ( iter.hasNext() ) { loadAnnotation(sID, sample, iter.next()); }
        }
        catch (IOException e) {
            System.err.println("Error loading file: " + fn);
        }
    }

    public void rebuild()
    {
        rebuild("Europeana"  , 2, 5, 6, 15, 17, 18, 23, 28);
        rebuild("TEL"        , 5, 15, 16, 29);
        rebuild("BgLinks"    , 2, 3, 4, 13, 14, 22, 23, 30);
        rebuild("Pelagios"   , 3, 5, 7, 16, 18, 19, 22, 27);
        rebuild("VocMatch"   , 25);
        rebuild("Ontotext v1", 2, 3, 4, 6, 7, 12, 13, 26);
        rebuild("Ontotext v2", 2, 3, 4, 6, 7, 12, 14, 17, 19, 24);

        Map<Object,EnrichmentAnnotation> m = new HashMap();
        for ( List<EnrichmentAnnotation> list : _annClusterResults.values() )
        {
            for (EnrichmentAnnotation ann : list ) { m.put(ann.getSource(), ann); }
        }
        _annResults.addAll(m.values());
    }

    public void rebuild(String id, int... indexes)
    {
        String[] sampleIDs = new String[indexes.length];
        int i = 0;
        for ( int index : indexes ) { sampleIDs[i++] = getID(index); }
        rebuild(id, sampleIDs);
    }

    public void rebuild(String id, String... sampleIDs)
    {
        Map<Object,EnrichmentAnnotation> result = new HashMap();
        for ( String sampleID : sampleIDs )
        {
            List<EnrichmentAnnotation> sample = _annSamples.get(sampleID);
            if ( sample == null ) { continue; }

            for (EnrichmentAnnotation ann : sample )
            {
                EnrichmentAnnotation old = result.put(ann.getSource(), ann);
                if ( old == null ) { continue; }

                if ( old.getParameters().equals(ann.getParameters())) { continue; }

                printDuplicate(System.err, old, ann);
            }
        }
        _annClusterResults.put(id, new ArrayList(result.values()));
    }

    public Collection<String> getResultClusters() { return _annClusterResults.keySet(); }


    public Collection<EnrichmentAnnotation> getClusteredResults(String id)
    {
        return _annClusterResults.get(id);
    }

    public Map<String,List<EnrichmentAnnotation>> getClusteredResults()
    {
        return _annClusterResults;
    }

    public Collection<EnrichmentAnnotation> getResults() { return _annResults; }

    private List<EnrichmentAnnotation> getSample(String id)
    {
        List<EnrichmentAnnotation> l = _annSamples.get(id);
        if ( l == null ) { l = new ArrayList(); _annSamples.put(id, l); }
        return l;
    }

    private void loadAnnotation(String sampleID
                              , List<EnrichmentAnnotation> sample
                              , CSVRecord record)
    {
        AnnotationParameters params = new AnnotationParameters()
            .parse(record.get(5), record.get(6), record.get(7));

        List<String> key = Arrays.asList(record.get(0), record.get(2)
                                       , record.get(3), record.get(4));

        EnrichmentAnnotation ann = 
            new EnrichmentAnnotation(sampleID, key, null, params, record.get(8));

        sample.add(ann);
    }

    private String getID(int index) { return "#" + FORMAT.format(index); }

    private void printDuplicate(PrintStream p
                              , EnrichmentAnnotation ann1, EnrichmentAnnotation ann2)
    {
        List<String> source = (List)ann1.getSource();
        p.println(source.get(0));
        p.println(source.get(1));
        p.print("Matched term: ");
        p.print(source.get(2));
        p.print(", Target resource: ");
        p.println(source.get(3));
        p.print("Annotator A: ");
        p.print(ann1.getParameters().toFullString());
        p.println(" (Sample " + ann1.getSampleID() + ")");
        p.print("Annotator B: ");
        p.print(ann2.getParameters().toFullString());
        p.println(" (Sample " + ann2.getSampleID() + ")");
        p.println();
    }
}
