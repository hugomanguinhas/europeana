package eu.europeana.vocs.conceptexp;

import java.io.File;

import eu.europeana.vocs.eurovoc.EurovocFetch;
import static eu.europeana.vocs.VocsUtils.*;

public class EurovocConceptFetch
{
    public static File SRC = new File(DIR_EXP, "eurovoc.concepts.list.txt");
    public static File DST = new File(DIR_EXP, "eurovoc.concepts.xml");

    public EurovocConceptFetch() {}

    public static final void main(String[] args) throws Exception
    {
        new EurovocFetch().fetchAll(SRC, DST);
    }
}
