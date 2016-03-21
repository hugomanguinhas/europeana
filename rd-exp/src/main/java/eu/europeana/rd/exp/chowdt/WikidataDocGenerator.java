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

    private void printLink(PrintStream ps, String label, String url)
    {
        label = label.replaceAll("[|]", "\\|");
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
        Property prop  = model.getProperty(SKOS_PREF_LABEL);

        StmtIterator iter = model.getResource(url).listProperties(prop);
        while ( iter.hasNext() )
        {
            Literal literal = iter.next().getLiteral();
            if ( !"en".equals(literal.getLanguage()) ) { continue; }

            return literal.getString();
        }
        return "";
    }

    private String getTitle(String url)
    {
        String keyword = "http://purl.org/dc/elements/1.1/title";
        Model    model = _dereferencer.deref(url);
        Property prop  = model.getProperty(keyword);

        url = url.replace("item", "proxy/provider");
        StmtIterator iter = model.getResource(url).listProperties(prop);

        Map<String,String> literals = new HashMap();
        while ( iter.hasNext() )
        {
            Literal literal = iter.next().getLiteral();
            literals.put(literal.getLanguage(), literal.getString());
        }
        if (literals.isEmpty()) { return ""; }

        String literal = literals.get("en");
        if ( literal != null ) { return literal; }

        return literals.values().iterator().next();
    }
}
