/**
 * 
 */
package eu.europeana.rd.exp.chowdt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;

import eu.europeana.anno.api.AnnotationAPI;
import eu.europeana.anno.api.config.AnnotationConfig;
import eu.europeana.anno.api.impl.AnnotationAPIimpl;
import eu.europeana.github.MarkDownBuffer;
import eu.europeana.github.MarkDownTemplate;
import eu.europeana.ld.deref.Dereferencer;
import static eu.europeana.rd.exp.chowdt.WikidataCHOExpConstants.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 21 Mar 2016
 */
public class WikidataDocGenerator
{
    private static String    PLACEHOLDER_TABLE = "wkd_links";
    private static String    PLACEHOLDER_DUP   = "wkd_dup";

    private static CSVFormat _format  = CSVFormat.EXCEL;
    private static Charset   _charset = Charset.forName("UTF-8");

    private static String SKOS_PREF_LABEL
    = "http://www.w3.org/2004/02/skos/core#prefLabel";

    private Dereferencer       _dereferencer = new Dereferencer();
    private MarkDownTemplate   _template     = new MarkDownTemplate();
    private MarkDownBuffer      _sb          = new MarkDownBuffer();
    private Properties         _cfg;

    public WikidataDocGenerator(Properties cfg) throws IOException
    {
        _cfg = cfg;

        String tpl = _cfg.getProperty("chowdt.doc.template");
        if ( tpl != null ) { _template.parse(new File(tpl)); }
    }

    public void generate(File src, File doc) throws IOException
    {
        _template.newReplacement(PLACEHOLDER_TABLE
                               , buildExampleTable(getFile(src, "sample")));

        _template.newReplacement(PLACEHOLDER_DUP
                               , buildDuplicateTable(getFile(src, "dup")));

        _template.print(doc);
    }

    private String buildExampleTable(File src) throws IOException
    {
        _sb.clear();
        _sb.appendTableHeader("Europeana Object", "Wikidata Entry"
                            , "Annotation");
        _sb.appendTableCols('l', 'l', 'l');

        CSVParser parser = null;
        try {
            parser = CSVParser.parse(src, _charset, _format);
            for ( CSVRecord record : parser )
            {
                String eid = (String)record.get(0);
                String wkd = (String)record.get(1);
                String aid = (String)record.get(2);

                _sb.appendLink(getTitle(eid), eid)
                   .append(" | ")
                   .appendLink(getLabel(wkd), wkd)
                   .append(" | ")
                   .appendLink(getAnno(aid) , aid + "?wskey=apidemo")
                   .append(" |\n");
            }
        }
        catch (IOException e) { e.printStackTrace(); }
        finally { IOUtils.closeQuietly(parser); }

        return _sb.flushBuffer();
    }

    private String buildDuplicateTable(File src) throws IOException
    {
        _sb.clear();
        _sb.appendTableHeader("Wikidata Entry", "Europeana Object 1"
                            , "Europeana Object 2");
        _sb.appendTableCols('l', 'l', 'l');

        CSVParser parser = null;
        try {
            parser = CSVParser.parse(src, _charset, _format);
            for ( CSVRecord record : parser )
            {
                String wkd  = (String)record.get(0);
                String eid1 = (String)record.get(1);
                String eid2 = (String)record.get(2);

                _sb.appendLink(getLabel(wkd), wkd)
                   .append(" | ")
                   .appendLink(getTitle(eid1), eid1)
                   .append(" | ")
                   .appendLink(getTitle(eid2), eid2)
                   .append(" |\n");
            }
        }
        catch (IOException e) { e.printStackTrace(); }
        finally { IOUtils.closeQuietly(parser); }

        return _sb.flushBuffer();
    }

    private String getAnno(String url)
    {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private String getLabel(String url)
    {
        try {
            Model model = _dereferencer.dereference(url);
            if ( model == null ) { return ""; }
    
            Map<String,String> lits = getLiterals(model.getResource(url), SKOS_PREF_LABEL);
            if (lits.isEmpty()) { return ""; }

            String literal = lits.get("en");
            if ( literal != null ) { return literal; }
    
            return lits.values().iterator().next();
        }
        catch(IOException e) { e.printStackTrace(); }

        return "?";
    }

    private String getWkdID(String url)
    {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private String getTitle(String url)
    {
        try {
            Model model = _dereferencer.dereference(url);
            if ( model == null ) { return getWkdID(url); }
        
            String prop = "http://purl.org/dc/elements/1.1/title";
        
            url = url.replace("item", "proxy/provider");
        
            Map<String,String> lits = getLiterals(model.getResource(url), prop);
            if (lits.isEmpty()) { return ""; }
        
            String literal = lits.get("en");
            if ( literal != null ) { return literal; }
        
            return lits.values().iterator().next();
        }
        catch(IOException e) { e.printStackTrace(); }

        return "?";
    }

    private Map<String,String> getLiterals(Resource resource, String... props)
    {
        Model model = resource.getModel();
        Map<String,String> ret = new HashMap();
        for ( String puri : props )
        {
            Property     prop = model.getProperty(puri);
            StmtIterator iter = resource.listProperties(prop);
            while ( iter.hasNext() )
            {
                Literal literal = iter.next().getLiteral();
                ret.put(literal.getLanguage(), literal.getString());
            }
        }
        return ret;
    }
}
