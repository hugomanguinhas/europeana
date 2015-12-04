package eu.europeana.tf.dataset;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.csv.CSVFormat;





import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import eu.europeana.edm.data.CHOAnalysis;
import eu.europeana.utils.CSVWriter;
import static eu.europeana.edm.EDMNamespace.*;
import static eu.europeana.utils.JenaUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class DatasetProcessor {

    private Property _type;
    private Property _prefLabel;
    private Property _foafName;

    public void process(File src, File all, File dst, File enrich) throws IOException
    {
        Model m = ModelFactory.createDefaultModel();
        loadFiles(src, ".xml", m);

        storeAsEDM(m, all);

        _type      = m.getProperty(RDF_TYPE);
        _prefLabel = m.getProperty(SKOS_PREF_LABEL);
        _foafName  = m.getProperty(FOAF_NAME);
        Resource pcho = m.getResource(EDM_PROVIDEDCHO);

        CSVWriter printer = new CSVWriter(enrich, CSVFormat.EXCEL);
        printer.start();
        try {
            enrichImpl(m.listSubjectsWithProperty(_type, pcho), printer);
        }
        finally {
            printer.end();
        }

        storeAsEDM(m, dst);

        new CHOAnalysis().analyse(dst);
    }

    private void enrichImpl(ResIterator iter, CSVWriter printer)
    {
        Collection<Resource> toRemove = new HashSet<Resource>();
        while (iter.hasNext())
        {
            enrichImpl(iter.next().listProperties(), toRemove, printer);
        }
        clearAll(toRemove);
    }

    private void enrichImpl(StmtIterator iter, Collection<Resource> toRemove, CSVWriter printer)
    {
        for ( Statement stmt : iter.toList())
        {
            RDFNode  node   = stmt.getObject();
            if ( !node.isResource() ) { continue; }

            Resource target = node.asResource();
            if ( !isEntity(target)  ) { continue; }

            String   label  = getLabel(target);
            if ( label == null      ) { continue; }

            printEnrichment(stmt, label, printer);

            stmt.changeObject(label);
            toRemove.add(target);
        }
    }

    private boolean isEntity(Resource rsrc)
    {
        List<String> entities = Arrays.asList(CONTEXTUAL_ENTITIES);
        StmtIterator iter = rsrc.listProperties(_type);
        while ( iter.hasNext() )
        {
            Statement stmt = iter.next();
            if (entities.contains(stmt.getObject().asResource().getURI())) { return true; }
        }
        return false;
    }

    private String getLabel(Resource rsrc)
    {
        StmtIterator iter;

        iter = rsrc.listProperties(_prefLabel);
        while ( iter.hasNext() )
        {
            RDFNode node = iter.next().getObject();
            return (node.isLiteral() ? node.asLiteral().getString() : null);
        }

        iter = rsrc.listProperties(_foafName);
        while ( iter.hasNext() )
        {
            RDFNode node = iter.next().getObject();
            return (node.isLiteral() ? node.asLiteral().getString() : null);
        }

        return null;
    }

    private void printEnrichment(Statement stmt, String label, CSVWriter printer)
    {
        Resource rsrc = stmt.getObject().asResource();
        String   prop = getQName(stmt.getPredicate());
        printer.print(stmt.getSubject().getURI(), prop, rsrc.getURI(), "", label);
    }
}
