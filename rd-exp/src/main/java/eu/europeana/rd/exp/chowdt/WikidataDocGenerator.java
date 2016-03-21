/**
 * 
 */
package eu.europeana.rd.exp.chowdt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import eu.europeana.anno.api.AnnotationAPI;
import eu.europeana.anno.api.config.AnnotationConfig;
import eu.europeana.anno.api.impl.AnnotationAPIimpl;
import eu.europeana.ld.deref.Dereferencer;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 21 Mar 2016
 */
public class WikidataDocGenerator
{
    private static CSVFormat _format  = CSVFormat.EXCEL;
    private static Charset   _charset = Charset.forName("UTF-8");

    private static String SKOS_PREF_LABEL
    = "http://www.w3.org/2004/02/skos/core#prefLabel";

    private Dereferencer _dereferencer = new Dereferencer();

    public WikidataDocGenerator() {}

    public void generate(File src, File dst) throws IOException
    {
        CSVParser  parser = null;
        PrintStream ps    = null;
        try {
            parser = CSVParser.parse(src, _charset, _format);
            ps     = new PrintStream(dst);

            for ( CSVRecord record : parser )
            {
                String eid = (String)record.get(0);
                String wkd = (String)record.get(1);
                String aid = (String)record.get(2);

                printLink(ps, getTitle(eid), eid);
                ps.print(" | ");
                printLink(ps, getLabel(wkd), wkd);
                ps.print(" | ");
                printLink(ps, getAnno(aid) , aid + "?wskey=apidemo");
                ps.print(" |");
                ps.println();
            }
            ps.flush();
        }
        finally
        {
            IOUtils.closeQuietly(ps);
            if ( parser  != null ) { parser.close();  }
        }
    }

    private String escape(String str)
    {
        return str.replaceAll("\n", "")
                  .replaceAll("\\|", "\\|");
        //return str.replaceAll("(|`)", "\\$0");
    }

    private void printLink(PrintStream ps, String label, String url)
    {
        label = escape(label);
        ps.print('['); ps.print(label); ps.print(']');
        ps.print('('); ps.print(url);   ps.print(')');
    }

    private String getAnno(String url)
    {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private String getLabel(String url)
    {
        Model    model = _dereferencer.deref(url);
        if ( model == null ) { return ""; }

        Map<String,String> lits = getLiterals(model.getResource(url), SKOS_PREF_LABEL);
        if (lits.isEmpty()) { return ""; }

        String literal = lits.get("en");
        if ( literal != null ) { return literal; }

        return lits.values().iterator().next();

    }

    private String getWkdID(String url)
    {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private String getTitle(String url)
    {
        Model  model   = _dereferencer.deref(url);
        if ( model == null ) { return getWkdID(url); }

        String prop = "http://purl.org/dc/elements/1.1/title";

        url = url.replace("item", "proxy/provider");

        Map<String,String> lits = getLiterals(model.getResource(url), prop);
        if (lits.isEmpty()) { return ""; }

        String literal = lits.get("en");
        if ( literal != null ) { return literal; }

        return lits.values().iterator().next();
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
