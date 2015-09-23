package eu.europeana.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.riot.RDFFormat;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;

import eu.europeana.edm.EDMXMLWriter;

public class JenaUtils
{
    public static ResourceComparator RESOURCE_COMPARATOR
        = new ResourceComparator();

    private static Map<String,RDFFormat> _exts = new HashMap();

    static {
        _exts.put(".rdf"   , RDFFormat.RDFXML_PLAIN);
        _exts.put(".ttl"   , RDFFormat.TURTLE_PRETTY);
        _exts.put(".n3"    , RDFFormat.TURTLE_PRETTY);
        _exts.put(".nt"    , RDFFormat.NTRIPLES_UTF8);
        _exts.put(".nq"    , RDFFormat.NQUADS_UTF8);
        _exts.put(".trig"  , RDFFormat.TRIG_PRETTY);
        _exts.put(".jsonld", RDFFormat.JSONLD_PRETTY);
    }

    public static void clearAll(Collection<Resource> col)
    {
        for ( Resource rsrc : col ) { rsrc.removeProperties(); }
    }

    public static String getQName(Resource rsrc)
    {
        String p = rsrc.getModel().getNsURIPrefix(rsrc.getNameSpace());
        return ( p == null ? rsrc.getURI() : (p + ":" + rsrc.getLocalName()) );
    }

    public static Model loadModel(File file) { return loadModel(file, "RDF/XML"); }

    public static Model loadModel(File file, String format)
    {
        Model m = ModelFactory.createDefaultModel();
        try {
            m.read(new FileReader(file), null, format);
        } catch (IOException e) {
            System.out.println("Could not read file: " + file.getName()
                             + ", reason:" + e.getMessage());
        }
        return m;
    }

    public static Model loadFiles(File dir, String ext, Model m)
    {
        for ( File file : dir.listFiles() )
        {
            if ( file.isDirectory() ) { loadFiles(file, ext, m); continue; }

            try {
                m.read(new FileReader(file), null, "RDF/XML");
            } catch (IOException e) {
                System.out.println("Could not read file: " + file.getName()
                                 + ", reason:" + e.getMessage());
            }
        }
        return m;
    }

    public static void store(Model model, File dest) throws IOException
    {
        store(model, dest, "RDF/XML", Collections.EMPTY_MAP);
    }

    public static void store(Model model, OutputStream out) throws IOException
    {
        store(model, out, "RDF/XML");
    }

    public static void store(Model model, OutputStream out, String sFormat) throws IOException
    {
        try {
            RDFWriter w = model.getWriter(sFormat);
            w.setProperty("allowBadURIs", "true");
            w.write(model, out, null);
            out.flush();
        }
        finally {
            out.close();
        }
    }

    public static void store(Model model, File dest, String sFormat
                           , Map<String,String> props) throws IOException
    {
        FileOutputStream out = new FileOutputStream(dest);
        try {
            RDFWriter w = model.getWriter(sFormat);
            for ( String k : props.keySet() ) { w.setProperty(k, props.get(k)); }
            //w.setProperty("allowBadURIs", "true");
            w.write(model, out, null);
            out.flush();
        }
        finally {
            out.close();
        }
    }

    public static void storeAsEDM(Model m, File out) throws IOException
    {
        new EDMXMLWriter(EDMXMLWriter.ALL_CLASSES).write(m, out);
    }

    public static class ResourceComparator implements Comparator<Resource> {

        @Override
        public int compare(Resource r1, Resource r2)
        {
            return r1.getURI().compareTo(r2.getURI());
        }
    }

    public static RDFFormat getJenaFormat(String filename)
    {
        for ( String ext : _exts.keySet() )
        {
            if ( filename.endsWith(ext) ) { return _exts.get(ext); }
        }
        return null;
    }
}
