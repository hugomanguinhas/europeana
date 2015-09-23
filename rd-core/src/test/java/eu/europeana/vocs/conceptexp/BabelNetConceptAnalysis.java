package eu.europeana.vocs.conceptexp;

import java.io.File;
import java.io.IOException;

import eu.europeana.vocs.babelnet.BabelNetAnalysis;
import static eu.europeana.vocs.VocsUtils.*;

public class BabelNetConceptAnalysis
{
    public static File SRCLIST = BabelNetConceptFetch.SRC;
    public static File SRC     = BabelNetConceptFetch.DST;
    public static File DST     = new File(DIR_EXP, "babelnet.concepts.rpt.txt");

    public static void main( String[] args ) throws IOException
    {
        new BabelNetAnalysis().analyse(SRCLIST, SRC, DST);
    }
}