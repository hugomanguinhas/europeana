package eu.europeana.vocs.conceptexp;

import java.io.File;
import java.io.IOException;

import eu.europeana.vocs.eurovoc.EurovocAnalysis;
import static eu.europeana.vocs.VocsUtils.*;

public class EurovocConceptAnalysis
{
    public static File SRCLIST = EurovocConceptFetch.SRC;
    public static File SRC     = EurovocConceptFetch.DST;
    public static File DST     = new File(DIR_EXP, "eurovoc.concepts.rpt.txt");

    public static void main( String[] args ) throws IOException
    {
        new EurovocAnalysis().analyse(SRCLIST, SRC, DST);
    }
}
