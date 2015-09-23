package eu.europeana.vocs.coref.cache;

import java.io.File;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import eu.europeana.utils.CSVWriter;

public class CoRefCacheHarvester
{
    private String  _sparql;

    public CoRefCacheHarvester(String  sparql)
    {
        _sparql = sparql;
    }
    
    public void harvest(String query, File fileCache)
    {
        CSVWriter p = new CSVWriter(fileCache);
        p.start();
        try { harvest(query, p); } finally { p.end(); }
    }
    
    private void harvest(String query, CSVWriter p)
    {
        QueryEngineHTTP endpoint = new QueryEngineHTTP(_sparql, query);
        try {
            System.err.println(query);
            ResultSet rs = endpoint.execSelect();
            if ( !rs.hasNext() ) { return; }

            int cursor = 0;
            while (rs.hasNext())
            {
                QuerySolution sol = rs.next();
                String src = sol.getResource("src").getURI();
                String trg = sol.getResource("trg").getURI();
                p.println(src, trg);
                if (++cursor % 1000 == 0) { System.out.println("fetched: " + cursor); }
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        finally {
            endpoint.close();
        }
    }

    public static final void main(String[] args)
    {
        String query = "PREFIX tgsi: <http://data.tagasauris.com/ontologies/core/> "
                     + "SELECT ?src ?trg { GRAPH <urn:c5:matches> "
                       + "{ ?src tgsi:exactMatch ?trg . FILTER strstarts(str(?trg), \"http://dbpedia.org/resource/\") } "
                     + "}";
        File cache = new File("D:\\work\\incoming\\taskforce\\cache\\onto.coref.cache.csv");

        new CoRefCacheHarvester("http://mediagraph.ontotext.com/repositories/c5").harvest(query, cache);
    }
}
