package eu.europeana.vocs.conceptexp;

import java.io.File;
import java.io.IOException;

import eu.europeana.vocs.freebase.FreebaseAnalysis;
import static eu.europeana.vocs.VocsUtils.*;

public class FreebaseConceptAnalysis
{
    public static File SRCLIST = FreebaseConceptFetch.SRC;
    public static File SRC     = FreebaseConceptFetch.DST;
    public static File DST     = new File(DIR_EXP, "freebase.concepts.rpt.txt");

    public static void main( String[] args ) throws IOException
    {
        new FreebaseAnalysis().analyse(SRCLIST, SRC, DST);
    }
}
