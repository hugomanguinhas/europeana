package eu.europeana.vocs.conceptexp;

import java.io.File;
import java.io.IOException;

import eu.europeana.vocs.dbpedia.DBPediaAnalysis;
import static eu.europeana.vocs.VocsUtils.*;

public class DBPediaConceptAnalysis
{
    public static File SRCLIST = DBPediaConceptFetch.SRC;
    public static File SRC     = DBPediaConceptFetch.DST;
    public static File DST     = new File(DIR_EXP, "dbpedia.concepts.rpt.txt");

    public static void main( String[] args ) throws IOException
    {
        new DBPediaAnalysis().analyse(SRCLIST, SRC, DST);
    }
}
