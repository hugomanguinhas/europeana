package eu.europeana.vocs.conceptexp;

import java.io.File;

import eu.europeana.vocs.lexvo.LexvoFetch;
import static eu.europeana.vocs.VocsUtils.*;

public class LexvoConceptFetch
{
    public static File SRC = new File(DIR_EXP, "lexvo.concepts.list.txt");
    public static File DST = new File(DIR_EXP, "lexvo.concepts.xml");


    public static final void main(String[] args) throws Exception
    {
        LexvoFetch f = new LexvoFetch();
        f.fetchAll(SRC, DST);
    }
}
