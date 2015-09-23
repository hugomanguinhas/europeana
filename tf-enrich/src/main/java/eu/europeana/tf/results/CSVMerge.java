package eu.europeana.tf.results;

import static eu.europeana.tf.results.ResultUtils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import eu.europeana.utils.CSVWriter;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class CSVMerge
{
    private Collection<EnrichmentResult> _results;

    public CSVMerge() { _results = new ArrayList(); }

    public void merge(File output, Object... args)
    {
        _results.clear();

        int len = args.length;
        for ( int i = 0; i < len; i = i + 2)
        {
            loadEnrichments((String)args[i], (File)args[i+1], _results);
        }

        print(output);
    }

    private void print(File output)
    {
        CSVWriter p = new CSVWriter(output);
        p.start();
        EnrichmentResult.printHeaderFull(p);
        for ( EnrichmentResult result : _results ) { result.printFull(p); }
        p.end();
    }
}
