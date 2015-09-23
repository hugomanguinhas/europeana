package eu.europeana.vocs.concept;

import java.io.File;

import eu.europeana.vocs.dbpedia.DBPediaFetch;
import static eu.europeana.vocs.VocsUtils.*;

public class DBPediaConceptFetch {

	public static File SRC = new File(DIR_CONCEPT, "dbpedia.concepts.list.txt");
	public static File DST = new File(DIR_CONCEPT, "dbpedia.concepts.xml");

	public static final void main(String[] args) throws Exception
	{
		new DBPediaFetch(false).fetchAll(SRC, DST);
	}
}
