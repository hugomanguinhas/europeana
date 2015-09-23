package eu.europeana.entity.filter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import static eu.europeana.edm.EDMNamespace.*;
import static eu.europeana.vocs.VocsUtils.*;

/*
 * 
 * description:
 * http://dbpedia.org/resource/Jana_Cova
 * http://dbpedia.org/resource/Bret_Wolfe
 * http://dbpedia.org/resource/Jean-Daniel_Cadinot
 * http://dbpedia.org/resource/Teagan_Presley
 * 
 * keywords:
 * porn star
 * pornographic
 * 
 * http://dbpedia.org/resource/Benjamin_DeMott
 * "Battling the Hard Man: Notes on Addiction to the Pornography of Violence"
 * I also found fotographers that are focus
 * 
 * photographers
 * http://dbpedia.org/resource/Barbara_Nitke
*/
public class EntityFilter
{
    public static ResourceFilter[] ENTITY_FILTERS = null;

    static {
        Class c = EntityFilter.class;
        ENTITY_FILTERS = new ResourceFilter[]
        {
            new ResourceFilterByURI(c.getResourceAsStream("p_uri.txt"))
          , new ResourceFilterByProperty(RDAGR2_PROFOROCCUPATION
                                       , c.getResourceAsStream("p_profession.txt"))
          , new ResourceFilterByProperty(RDAGR2_BIBINFO
                                       , c.getResourceAsStream("p_bibinfo.txt"))
        };
    }

    private ResourceFilter[] _filters;


    public EntityFilter()                          { this(ENTITY_FILTERS); }

    public EntityFilter(ResourceFilter... filters) { _filters = filters;   }


    public Collection<String> identifyMatches(File src, Collection<String> filtered)
    {
        return identifyMatches(loadModel(src), filtered);
    }

    public Collection<String> identifyMatches(Model m, Collection<String> filtered)
    {
        ResIterator iter = m.listResourcesWithProperty(m.getProperty(RDF_TYPE));
        while ( iter.hasNext() )
        {
            Resource rsrc = iter.next();
            if ( filter(rsrc) ) { filtered.add(rsrc.getURI()); }
        }
        return filtered;
    }

    public void filterIn(Model src, File dst, Collection<String> col) throws IOException
    {
        store(filterIn(src, importNamespaces(src, ModelFactory.createDefaultModel()), col), dst);
    }

    public Model filterIn(Model src, Model dst, Collection<String> col) throws IOException
    {
        if ( dst == null ) { return dst; }

        for ( String uri : col )
        {
            dst.add(src.getResource(uri).listProperties());
        }
        return dst;
    }

    public void filterOut(Model src, File dst, Collection<String> col) throws IOException
    {
        store(filterOut(src, importNamespaces(src, ModelFactory.createDefaultModel()), col), dst);
    }

    public Model filterOut(Model src, Model dst, Collection<String> col) throws IOException
    {
        if ( dst == null ) { return dst; }

        ResIterator iter = src.listResourcesWithProperty(src.getProperty(RDF_TYPE));
        while ( iter.hasNext() )
        {
            Resource r = iter.next();
            if ( col.contains(r.getURI()) ) { continue; }

            dst.add(r.listProperties());
        }
        return dst;
    }

    public boolean filter(Resource rsrc)
    {
        for ( ResourceFilter filter : _filters )
        {
            if ( filter.filter(rsrc) ) { return true; }
        }
        return false;
    }


    static interface ResourceFilter
    {
        public boolean filter(Resource rsrc);
    }

    static class ResourceFilterByURI implements ResourceFilter
    {
        public Collection<Pattern> _patterns = null;

        public ResourceFilterByURI(Collection<Pattern> patterns) { _patterns = patterns; }

        public ResourceFilterByURI(InputStream in)
        {
            try {
                List<String> lines = loadLines(in);
                _patterns = new ArrayList<Pattern>(lines.size());
                for ( String line : lines )
                {
                    line = line.trim();
                    if ( line.isEmpty() ) { continue; }

                    line = escapeURI2Pattern(line).replaceAll("XXX", ".*");
                    _patterns.add(Pattern.compile(line));
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }

        @Override
        public boolean filter(Resource rsrc)
        {
            String uri = rsrc.getURI();
            for ( Pattern p : _patterns )
            {
                if ( p.matcher(uri).matches()) { return true; }
            }
            return false;
        }
    }

    static class ResourceFilterByProperty implements ResourceFilter
    {
        private String                       _property = null;
        private Collection<LocalizedKeyword> _keywords = null;
        private Collection<String>           _uris     = null;

        public ResourceFilterByProperty(String prop, InputStream in)
        {
            _property = prop;
            _uris     = new ArrayList();
            _keywords = new ArrayList();
            try {
                for ( String line : loadLines(in) )
                {
                    if ( line.startsWith("http://") ) { _uris.add(line); continue; }

                    String[] sa = line.split("@");
                    String literal = sa[0];
                    String lang    = null;
                    if ( sa.length > 1 ) { literal = sa[0]; }
                    _keywords.add(new LocalizedKeyword(literal, lang));
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }

        @Override
        public boolean filter(Resource rsrc)
        {
            StmtIterator iter = rsrc.listProperties(rsrc.getModel().getProperty(_property));
            while ( iter.hasNext() )
            {
                RDFNode obj = iter.next().getObject();
                boolean res = false;
                     if ( obj.isLiteral()     ) { res = filterLiteral(obj.asLiteral());   }
                else if ( obj.isURIResource() ) { res = filterResource(obj.asResource()); }

                if ( res ) { return true; }
            }
            return false;
        }

        private boolean filterResource(Resource rsrc)
        {
            return _uris.contains(rsrc.getURI());
        }

        private boolean filterLiteral(Literal literal)
        {
            for ( LocalizedKeyword keyword : _keywords )
            {
                if ( keyword.check(literal) ) { return true; }
            }
            return false;
        }
    }

    static class LocalizedKeyword
    {
        private String _keyword;
        private String _lang;

        public LocalizedKeyword(String keyword, String lang)
        {
            _keyword = keyword;
            _lang    = lang;
        }

        public boolean check(Literal literal)
        {
            if ( (_lang != null) && !_lang.equals(literal.getLanguage()) ) { return false; }

            String str = literal.getString().replaceAll("\\s+", " ")
                                .trim().toLowerCase();
            return str.contains(_keyword);
        }
    }
}
