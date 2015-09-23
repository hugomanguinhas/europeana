package eu.europeana.data.analysis;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class LangDistributionStat extends AbsStat
{
    private LangStat               _langs     = new LangStat();
    private Map<Property,LangStat> _propLangs = new HashMap();


    public LangDistributionStat() {}


    public Collection<ValueStat> getStats() { return _langs.getStats(); }

    public ValueStat getStat(String sLang)  { return _langs.getStat(sLang); }

    public void newResource(Resource r)
    {
        Set<String> langDist = new HashSet<String>();
        StmtIterator iter = r.listProperties();
        while ( iter.hasNext() )
        {
            Statement stmt = iter.next();
            RDFNode   node = stmt.getObject();
            if ( !(node instanceof Literal) ) { continue; }

            String    lang = node.asLiteral().getLanguage();
            newLangStat(stmt.getPredicate(), lang);
            langDist.add(lang);
        }

        for ( String lang : langDist ) { _langs.newLang(lang); }
    }

    public void print(PrintStream ps, int total)
    {
        printHeader(ps);
        ps.println("Total n. of languages: " + _langs.getSize());
        ps.println();

        _langs.setTotal(total);
        _langs.print(ps);

        for ( Property p : _propLangs.keySet() )
        {
            LangStat stat = _propLangs.get(p);
            if (!stat.hasLanguages()) { continue; }

            ps.println("<" + p + ">:");
            stat.print(ps);
        }
    }


    protected void printHeader(PrintStream ps)
    {
        printSection(ps, "LANGUAGE DISTRIBUTION");
        printLine(ps
                , "This section lists the languages found for the selected resources."
                , ""
                , "Meaning of the columns:"
                , "#1: Code of the Language (i.e. found on literal ranges)"
                , "#2: Number of resources that have at least one literal range of this lang"
                , "#3: Percentage against the total number of resources");
        printSeparator(ps);
        ps.println();
    }


    private void newLangStat(Property p, String lang)
    {
        LangStat stat = _propLangs.get(p);
        if ( stat == null ) { stat = new LangStat(); _propLangs.put(p, stat); }
        stat.newLang(lang);
    }
}
