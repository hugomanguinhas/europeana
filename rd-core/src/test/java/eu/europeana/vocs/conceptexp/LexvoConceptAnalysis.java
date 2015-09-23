package eu.europeana.vocs.conceptexp;

import java.io.File;
import java.io.IOException;

import eu.europeana.vocs.lexvo.LexvoAnalysis;
import static eu.europeana.vocs.VocsUtils.*;

public class LexvoConceptAnalysis
{
    public static File SRCLIST = LexvoConceptFetch.SRC;
    public static File SRC     = LexvoConceptFetch.DST;
    public static File DST     = new File(DIR_EXP, "lexvo.concepts.rpt.txt");

    public static void main( String[] args ) throws IOException
    {
        new LexvoAnalysis().analyse(SRCLIST, SRC, DST);
    }
}
