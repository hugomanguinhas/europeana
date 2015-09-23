package eu.europeana.tf.corpus;

import java.io.File;
import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import eu.europeana.tf.results.EnrichmentResult;
import eu.europeana.utils.CSVWriter;
import static eu.europeana.edm.EDMNamespace.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class GoldStandard extends ArrayList<GoldStandard.Entry>
{
    public static int SIZE = 100;

    public GoldStandard()         { this(SIZE);  }
    public GoldStandard(int size) { super(size); }


    public void print(File file)
    {
        file.getParentFile().mkdirs();

        CSVWriter p = new CSVWriter(file);
        p.start();
        try { print(p); } finally { p.end(); }
    }

    public void print(CSVWriter p)
    {
        printHeader(p);
        for ( GoldStandard.Entry entry : this ) { printLine(entry, p); }
    }

    public void printHeader(CSVWriter p)
    {
        p.println("Original Resource"
                , "Title (or description)"
                , "Property + Value"
                , "Matched Term"
                , "Target Resource"
                , "Annotation"
                , "Comments");
    }

    public void printLine(GoldStandard.Entry entry, CSVWriter p)
    {
        p.println(entry.getSourceURL()
                  , entry.getSourceTitle()
                , entry.getSourceProperty()
                , entry.getResult().getValue()
                , entry.getTargetURL()
                , "", "");
    }

    static class Entry
    {
        private EnrichmentResult _result;
        private Model            _source;
        private Model            _target;

        public Entry(EnrichmentResult result, Model source, Model target)
        {
            _result = result;
            _source = source;
            _target = target;
        }

        public EnrichmentResult getResult() { return _result; }
        public Model            getTarget() { return _target; }

        public Model  getSource()      { return _source; }
        public String getSourceURL()   { return _result.getResource(); }
        public String getSourceDesc()  { return getFirstProperty(DC_DESCRIPTION); }
        public String getSourceValue()
        {
            String       rsrc = getSourceURL();
            String       val  = _result.getValue();
            Property     prop = _source.getProperty(getSourcePropertyURI());
            StmtIterator iter = _source.getResource(rsrc).listProperties(prop);
            while ( iter.hasNext() )
            {
                Statement stmt = iter.next();
                if ( !stmt.getObject().isLiteral() ) { continue; }

                String str = stmt.getString();
                if ( str.contains(val) ) { return str; }
            }
            return "";
        }

        public String getTargetURL()   { return _result.getTarget(); }

        public String getSourceTitle()
        {
            String title =  getFirstProperty(DC_TITLE);
            return ( title != null ? title : getSourceDesc() );
        }

        public String getSourceProperty()
        {
            String value = getSourceValue().replaceAll("\\s+", " ");
            return _result.getProperty() + " \"" + value + "\"";
        }

        private String getFirstProperty(String propName)
        {
            String       rsrc = getSourceURL();
            Property     prop = _source.getProperty(propName);
            StmtIterator iter = _source.getResource(rsrc).listProperties(prop);
            return ( iter.hasNext() ? iter.next().getString() : "" );
        }

        private String getSourcePropertyURI()
        {
            String qname = _result.getProperty();
            int i = qname.indexOf(":");
            if (i <= 0) { return qname; }
            String uri  = _source.getNsPrefixURI(qname.substring(0, i));
            String name = qname.substring(i+1);
            return uri + name;
        }
    }
}
