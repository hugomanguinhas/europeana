package eu.europeana.vocs.coref.cache;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CorefCache extends HashMap<String,List<String>>
{
    private static String[] DUMMY = new String[] {};

    public void load(File file)
    {
        System.out.println("Loading cache: " + file.getName());
        try {
            CSVParser parser = CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.EXCEL);
            Iterator<CSVRecord> iter = parser.iterator();
            while ( iter.hasNext() )
            {
                CSVRecord record = iter.next();
                addToCache(record.get(0), record.get(1));
            }
        }
        catch (IOException e) { System.err.println("Error loading file: " + file.getName()); }
    }

    public List<String> addToCache(String uri, String value)
    {
        List<String> sa = get(uri);
        if ( sa == null ) { sa = new ArrayList<String>(1); put(uri, sa); }
        if ( !sa.contains(value) ) { sa.add(value); }
        return sa;
    }

    public List<String> addToCache(String uri, String[] value)
    {
        List<String> sa = get(uri);
        if ( sa == null ) { sa = new ArrayList<String>(value.length); put(uri, sa); }

        for ( String s : value )
        {
            if ( !sa.contains(s) ) { sa.add(s); }
        }

        return sa;
    }

    public String[] getFromCache(String uri)
    {
        List<String> sa = get(uri);
        return (String[])(sa == null ? null : sa.toArray(DUMMY));
    }


    public static final void main(String[] args)
    {
        File cache = new File("D:\\work\\incoming\\taskforce\\cache\\onto.coref.cache.csv");
        new CorefCache().load(cache);
    }
}
