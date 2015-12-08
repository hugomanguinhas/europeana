/**
 * 
 */
package eu.europeana.edm.shapes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.topbraid.spin.util.JenaUtil;

import com.hp.hpl.jena.rdf.model.Model;

import static eu.europeana.net.HttpUtils.*;
import static eu.europeana.rdf.FormatSupport.*;
import static org.apache.commons.io.IOUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 8 Dec 2015
 */
public abstract class AbsRecordValidator implements RecordValidator
{
    protected String DEFAULT_MIME = RDFXML.getMimetype();

    public Model validate(File file, String mime) throws IOException
    {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return validate(is, mime);
        }
        finally { closeQuietly(is); }
    }

    public Model validate(InputStream is, String mime) throws IOException
    {
        Model m = getModel();
        m.read(is, "", getJenaLangByMime(mime));
        return validate(m);
    }

    public Model validate(String urn) throws IOException, HttpException
    {
        GetMethod m = createGet(urn, DEFAULT_MIME);
        try {
            int r = new HttpClient().executeMethod(m);
            return ( r == 200 ? validate(getInputStream(m)
                                       , getContentType(m, DEFAULT_MIME))
                             : null );
        }
        finally { closeMethod(m); }
    }

    protected Model getModel() { return JenaUtil.createDefaultModel(); }
}