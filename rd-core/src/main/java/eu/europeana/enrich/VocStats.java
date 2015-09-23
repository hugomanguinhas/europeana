package eu.europeana.enrich;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import eu.europeana.api.RecordAPI;
import eu.europeana.enrich.disamb.AmbiguityFetch;
import eu.europeana.utils.CSVWriter;

import static eu.europeana.edm.EDMNamespace.*;

public class VocStats {

    private Map<Resource,Stat> _index;
    private RecordAPI          _api = new RecordAPI();

    public VocStats() {
        _index = new HashMap<Resource,Stat>();
    }

    public void process(File input, File output)
    {
        Model m = ModelFactory.createDefaultModel();
        try {
            m.read(new FileReader(input), null, "RDF/XML");
        }
        catch (Exception e) {
            System.err.println("error parsing: " + input.getName() + ", error: " + e.getMessage());
            return;
        }
        
        System.out.print("fetching resources... ");
        fetchResources(m.getResource(EDM_AGENT));
        System.out.println("[" + _index.size() + "]");

        System.out.print("calculating hits...");
        updateHits();
        System.out.println("[" + _index.size() + "]");

        System.out.println("printing results...");
        printCSV(output);
    }

    private void fetchResources(Resource type)
    {
        Model m = type.getModel();
        Property pType = m.getProperty(RDF_TYPE);
        StmtIterator iter = m.listStatements(null, pType, type);
        while ( iter.hasNext() )
        {
            Resource rsrc = iter.next().getSubject();
            _index.put(rsrc,  new Stat(rsrc));
        }
    }

    private void updateHits()
    {
        for ( Stat stat : _index.values() ) { stat.updateHits(); }
    }

    private void printCSV(File file)
    {
        CSVWriter printer = new CSVWriter(file);
        printer.start();
        printer.print("Resource", "Hits");
        for ( Stat stat : _index.values() ) { stat.print(printer); }
        printer.end();
    }

    class Stat
    {
        private Resource _rsrc;
        private int      _hits = 0;

        public Stat(Resource rsrc) { _rsrc = rsrc; }

        public void updateHits()
        {
            try {
                _hits = _api.countExactMatch("who", _rsrc.getURI());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        public void print(CSVWriter printer)
        {
            printer.print(_rsrc.getURI(), _hits);
        }
    }

    public static final void main(String[] args)
    {
        String file = "D:/work/incoming/cesare/storedagents.xml";
        String out  = "D:/work/incoming/cesare/agent_dup.csv";
        new AmbiguityFetch().process(new File(file), new File(out));
    }

}
