package eu.europeana.tf.agreement;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import eu.europeana.utils.CSVWriter;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class AgreementRatings
{
    private Map<String,List<EnrichmentAnnotation>> _mRatings;
    private Map<String,List<EnrichmentAnnotation>> _mAnnotators;

    public AgreementRatings()
    {
        _mRatings    = new TreeMap();
        _mAnnotators = new TreeMap();
    }

    public void buildAgreementTable(File file)
    {
        Set<String> params = getUsedParameters();
        CSVWriter p = new CSVWriter(file);
        p.start();

        try {
            p.print("Rows"); p.println(params);
            for ( String row : _mRatings.keySet() )
            {
                p.print(row);
                for ( String param : params ) { p.print(countParams(row, param)); }
                p.println();
            }
        }
        finally {
            p.end();
        }
    }

    public int getRatersCount()             { return _mAnnotators.size();   }
    public Collection<String> getRaters()   { return _mAnnotators.keySet(); }
    public int getSubjectsCount()           { return _mRatings.size();      }
    public Collection<String> getSubjects() { return _mRatings.keySet();    }
    public int getRatingsPerSubject()       { return getRatersCount();      }

    public int countRatings(String subject, RatingCategory cat)
    {
        int count = 0;
        for ( EnrichmentAnnotation ann : _mRatings.get(subject) )
        {
            if ( ann.getParameters().hasCategory(cat) ) { count++; }
        }
        return count;
    }

    public int countRatings(String sbj, RatingCategory... cats)
    {
        int count = 0;
        for ( RatingCategory cat : cats ) { count += countRatings(sbj, cat); }
        return count;
    }

    public int countRatings(RatingCategory cat)
    {
        int count = 0;
        for ( String sbj : getSubjects() ) { count += countRatings(sbj, cat); }
        return count;
    }

    public void load(File dir) throws IOException
    {
        if ( !dir.isDirectory() ) { return; }

        for ( File file : dir.listFiles() )
        {
            String name = file.getName();
            if ( !name.startsWith("#") || !name.endsWith(".csv") ) { continue; }

            String annotator = name.substring(1, name.length()-4);
            load(annotator, file);
        }
    }

    public void load(String annotator, File file) throws IOException
    {
        System.out.println("loading annotations from: " + annotator);

        CSVParser p = CSVParser.parse(file, Charset.forName("UTF-8")
                                    , CSVFormat.EXCEL);

        Iterator<CSVRecord> iter = p.iterator();
        if ( iter.hasNext() ) { iter.next(); }

        while ( iter.hasNext() ) { parseAnnotation(annotator, iter.next()); }
    }

    private int countParams(String row, String key)
    {
        int count = 0;
        for ( EnrichmentAnnotation ann : _mRatings.get(row) )
        {
            if ( ann.getParameters().getKey().equals(key) ) { count++; }
        }
        return count;
    }

    private Set<String> getUsedParameters()
    {
        Set<String> params = new HashSet();
        for ( String sRow : _mRatings.keySet() )
        {
            for ( EnrichmentAnnotation ann : _mRatings.get(sRow) )
            {
                params.add(ann.getParameters().getKey());
            }
        }
        return params;
    }

    
    private void parseAnnotation(String annotator, CSVRecord record)
    {
        String ref = record.get(0);

        AnnotationParameters params = new AnnotationParameters()
            .parse(record.get(6), record.get(7), record.get(8));

        EnrichmentAnnotation ann = new EnrichmentAnnotation(
                annotator, ref, annotator, params, record.get(9));

        addAnnotation(_mRatings   , ref      , ann);
        addAnnotation(_mAnnotators, annotator, ann);
    }

    private void addAnnotation(Map<String,List<EnrichmentAnnotation>> m
                             , String key, EnrichmentAnnotation ann)
    {
        List<EnrichmentAnnotation> l = m.get(key);
        if ( l == null ) { l = new ArrayList(); m.put(key, l); }
        l.add(ann);
    }
}
