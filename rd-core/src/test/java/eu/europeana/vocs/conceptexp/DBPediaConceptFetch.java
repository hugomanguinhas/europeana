package eu.europeana.vocs.conceptexp;

import java.io.File;

import eu.europeana.vocs.dbpedia.DBPediaFetch;
import static eu.europeana.vocs.VocsUtils.*;

public class DBPediaConceptFetch
{
    public static File SRC = new File(DIR_EXP, "dbpedia.concepts.list.txt");
    public static File DST = new File(DIR_EXP, "dbpedia.concepts.xml");

    public DBPediaConceptFetch() {}

    public static final void main(String[] args) throws Exception
    {
        new DBPediaFetch(true).fetchAll(SRC, DST);
    }
}
