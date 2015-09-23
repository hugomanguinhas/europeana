package eu.europeana.vocs.concept;

import java.io.File;
import java.io.IOException;

import eu.europeana.vocs.dbpedia.DBPediaAnalysis;
import static eu.europeana.vocs.VocsUtils.*;

public class DBPediaFullConceptAnalysis {

	public static File SRCLIST = DBPediaConceptFetch.SRC;
	public static File SRC     = new File(DIR_CONCEPT, "dbpedia.concepts.full.xml");
	public static File DST     = new File(DIR_CONCEPT, "dbpedia.concepts.full.rpt.txt");

    public static void main( String[] args ) throws IOException
    {
    	new DBPediaAnalysis().analyse(SRCLIST, SRC, DST);
    }
}
