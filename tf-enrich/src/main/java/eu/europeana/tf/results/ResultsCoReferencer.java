package eu.europeana.tf.results;

import static eu.europeana.tf.results.ResultUtils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europeana.vocs.coref.CoReferenceResolver;

public class ResultsCoReferencer
{

    public void process(File input, File output
                      , CoReferenceResolver resolver)
    {
        List<EnrichmentResult> list = new ArrayList();
        loadEnrichments(null, input, list);

        Map uris = new HashMap();
        collectURIs(list, uris);
        resolver.resolve(uris);
        storeEnrichments(updateURIs(list, uris), output, false);
    }

    private void collectURIs(List<EnrichmentResult> list, Map<String,String[]> uris)
    {
        for ( EnrichmentResult result : list )
        {
            uris.put(result.getTarget(), null);
        }
    }

    private List<EnrichmentResult> updateURIs(
            List<EnrichmentResult> list, Map<String,String[]> uris)
    {
        List<EnrichmentResult> ret = new ArrayList<EnrichmentResult>(list.size());
        for ( EnrichmentResult result : list )
        {
            String   target = result.getTarget();
            String[] res    = uris.get(target);
            if ( res.length == 0 ) { ret.add(result); continue; }

            for ( String s : res )
            {
                EnrichmentResult r = new EnrichmentResult(result);
                r.setTarget(s);
                ret.add(r);
            }
        }
        return ret;
    }
}
