package eu.europeana.tf.results;

import static eu.europeana.tf.results.ResultUtils.loadEnrichments;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class CSVDuplicateCheck
{
    public void check(File input)
    {
        PrintStream ps = System.out;
        List<EnrichmentResult> list = new ArrayList();
        loadEnrichments(null, input, list);

        int len = list.size();
        for ( int i = 0; i < len; i++ ) 
        {
            EnrichmentResult r1 = list.get(i);
            for ( int e = 0; e < i; e++ ) 
            {
                EnrichmentResult r2 = list.get(e);
                if ( r1.compareTo(r2) != 0               ) { continue; }
                if ( r1.getValue().equals(r2.getValue()) ) { continue; }

                ps.print(r1.getValue());    ps.print('|');
                ps.print(r2.getValue());    ps.print('|');
                ps.print(r1.getResource()); ps.print('|');
                ps.print(r1.getProperty()); ps.println();
            }
        }
    }
}
