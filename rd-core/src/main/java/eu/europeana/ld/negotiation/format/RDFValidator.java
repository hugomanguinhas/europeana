package eu.europeana.ld.negotiation.format;

import java.io.InputStream;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.httpclient.HttpMethodBase;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import eu.europeana.ld.negotiation.impl.NegotiationContext;

public class RDFValidator implements FormatValidator
{
    private String _lang;

    public RDFValidator(String lang) { _lang = lang; }

    @Override
    public boolean validate(HttpMethodBase method, NegotiationContext ctx)
    {
        InputStream is = null;
        try {
            is = method.getResponseBodyAsStream();
            Model m = ModelFactory.createDefaultModel();
            m.read(is, null, _lang);
        }
        catch (Throwable t) {
            ctx.newContentError(ctx.getResource(), t);
            return false;
        }
        finally { IOUtils.closeQuietly(is); }
        return true;
    }
}