package eu.europeana.vocs.conceptexp;

import java.io.File;

import eu.europeana.vocs.babelnet.BabelNetFetch;
import static eu.europeana.vocs.VocsUtils.*;

public class BabelNetConceptFetch
{
    public static File SRC = new File(DIR_EXP, "babelnet.concepts.list.txt");
    public static File DST = new File(DIR_EXP, "babelnet.concepts.xml");

    public BabelNetConceptFetch() {}

    public static final void main(String[] args) throws Exception
    {
        new BabelNetFetch().fetchAll(SRC, DST);
    }
}
