package eu.europeana.enrich.disamb;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class AmbiguitySelection
{
    private static Collection<String> ACCEPTABLE = Arrays.asList("Y", "N", "P");

    private Collection<String> _toFilter;

    public AmbiguitySelection(String... toFilter)
    {
        _toFilter = Arrays.asList(toFilter);
    }

    public void selectAmbiguities(File src, File dst)
    {
        Collection<String> col = new TreeSet();
        try {
            CSVParser parser = CSVParser.parse(src, Charset.forName("UTF-8"), CSVFormat.EXCEL);
            select(parser, col);
        }
        catch (IOException e) { System.err.println("Error loading file: " + src.getName()); }

        storeURIs(col, dst);
    }

    private void storeURIs(Collection<String> col, File dst)
    {
        PrintStream p;
        try {
            p = new PrintStream(dst, "UTF-8");
        }
        catch (IOException e) { e.printStackTrace(); return; }

        try {
            for ( String s : col ) { p.println(s); }
        }
        finally {
            p.flush(); p.close();
        }
    }

    private Collection<String> select(CSVParser parser, Collection<String> col)
    {
        Iterator<CSVRecord> iter = parser.iterator();
        if ( iter.hasNext() ) { iter.next(); }

        while ( iter.hasNext() ) { select(iter.next(), col); }

        return col;
    }
    

    private void select(CSVRecord record, Collection<String> col)
    {
        int len = record.size();

        for ( int i = 3; i < len; i = i+2)
        {
            String uri  = record.get(i).trim();
            if ( uri.isEmpty() ) { continue; }

            String code = record.get(i+1).toUpperCase().trim();
            checkCode(code, uri);

            if ( _toFilter.contains(code) ) { col.add(uri); }
        }
    }

    private void checkCode(String code, String uri)
    {
        if ( code.isEmpty() || ACCEPTABLE.contains(code) ) { return; }
        
        System.err.println("Wrong code <" + code + "> for URI: " + uri);
    }
}
