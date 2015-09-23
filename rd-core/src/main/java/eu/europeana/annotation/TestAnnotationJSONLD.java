package eu.europeana.annotation;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;

public class TestAnnotationJSONLD {

    public static void readTriples(String url)
    {
        Model m = ModelFactory.createDefaultModel();
        m.read(url, null, "JSONLD");
        m.write(System.out, "RDF/XML");
    }
    
    public static void readQuads(String url)
    {
        DatasetGraph ds = DatasetGraphFactory.createMem();
        RDFDataMgr.read(ds, url, null, Lang.JSONLD);
        RDFDataMgr.write(System.out, ds, Lang.NQUADS);
    }

    public static final void main(String[] args) {
        String url = "file:///C:/Users/mangas/Google Drive/Europeana/Annotations/historypin_correction_graph.jsonld";
        readTriples(url);
    }
}
