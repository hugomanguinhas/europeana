package eu.europeana.tf.results;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europeana.utils.CSVWriter;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class CSVNormalizerProcessor {

    private LineLoader _loader;

    public CSVNormalizerProcessor(LineLoader loader) { _loader = loader; }

    public void process(File source, File target)
    {
        List<EnrichmentResult> results = load(source);

        if ( target == null ) {
            String name = source.getName();
            target = new File(source.getParentFile()
                            , name.substring(0, name.length()-4) + "_norm.csv");
        }

        CSVWriter printer = new CSVWriter(target);
        printer.start();
        for ( EnrichmentResult result : results ) { result.printRaw(printer); }
        printer.end();
    }

    private List<EnrichmentResult> load(File file)
    {
        List<EnrichmentResult> list = new ArrayList<EnrichmentResult>();
        try {
            BufferedReader r = null;
            try {
                r = new BufferedReader(new FileReader(file));
        
                String line;
                while ((line = r.readLine()) != null)
                {
                    EnrichmentResult result = _loader.parse(line);
                    if ( result != null ) { list.add(result); continue; }
                    System.err.println("Cannot parse line: " + line);
                }
            }
            finally {
                if (r != null) { r.close(); }
            }
        }
        catch (IOException e) {}
        return list;
    }

    public static class Cursor
    {
        public String str;
        public int    i;

        public Cursor(String str) { this.str = str; this.i = 0; }
    }

    public static class LineLoader
    {
        protected String parseNext(Cursor cursor)
        {
            String line = cursor.str;
            int i = line.indexOf(";", cursor.i);
            if ( i < 0 ) { return line.substring(cursor.i); }

            String str = line.substring(cursor.i, i);
            cursor.i = i + 1;
            return str;
        }

        protected String parseRest(Cursor cursor)
        {
            return cursor.str.substring(cursor.i + 1);
        }

        public EnrichmentResult parse(String line)
        {
            Cursor cursor = new Cursor(line);
            String uri  = parseNext(cursor);
            String prop = parseNext(cursor);
            String link = parseNext(cursor);
            String conf = parseNext(cursor);
            String rest = parseRest(cursor);
            return new EnrichmentResult().loadRaw(uri, prop, link, conf, rest);
        }
    }

    public static class PelagiousLineLoader extends LineLoader
    {
        static Map<String,String> CONVERSION = new HashMap();
        
        static {
            CONVERSION.put("source", "dc:source");
            CONVERSION.put("subject", "dc:subject");
            CONVERSION.put("spatial", "dcterms:spatial");
            CONVERSION.put("publisher", "dc:publisher");
            CONVERSION.put("title", "dc:title");
            CONVERSION.put("description", "dc:description");
        }

        protected String convert(String str)
        {
            String ret = CONVERSION.get(str);
            if ( ret == null ) { System.err.println("Unknown property: " + str); }
            return ret;
        }

        public EnrichmentResult parse(String line)
        {
            Cursor cursor = new Cursor(line);
            String uri  = parseNext(cursor);
            if ( uri == null ) {  return null; }

            if ( uri.equals("provided_cho_uri") ) { return null; }

            String prop  = convert(parseNext(cursor));
            String link  = parseNext(cursor);
            String label = parseNext(cursor);
            String type  = parseNext(cursor);
            String rest  = parseNext(cursor);
            return new EnrichmentResult().loadRaw(uri, prop, link, "", rest);
        }
    }

    //Fix file first with pattern: "\n[ ]+" to " "
    public static class OntoTextLineLoader extends LineLoader
    {
        public EnrichmentResult parse(String line)
        {
            Cursor cursor = new Cursor(line);
            String uri  = parseNext(cursor);
            if ( uri == null ) { return null; }

            String prop  = parseNext(cursor);
            String link  = parseNext(cursor);
            String conf  = parseNext(cursor);
            String rest  = parseNext(cursor);
            if ( rest == null ) {
                System.err.println(line);
            } 
            return new EnrichmentResult().loadRaw(uri, prop, link, conf, rest);
        }
    }
}
