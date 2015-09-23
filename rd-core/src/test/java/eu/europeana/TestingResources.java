package eu.europeana;

import java.io.File;

public class TestingResources
{
	public static File DIR_ENTITIES
		= new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\Entity Collection\\entities");


	//AGENTS
	
	public static File DIR_AGENTS
		= new File(DIR_ENTITIES, "agents");

	public static File FILE_AGENTS_DBPEDIA_SRC
		= new File(DIR_AGENTS, "agents_dbpedia_src.xml");

	public static File FILE_AGENTS_DBPEDIA_OUT
		= new File(DIR_AGENTS, "agents_dbpedia_out.xml");

	public static File FILE_AGENTS_DBPEDIA_OUT_CSV
		= new File(DIR_AGENTS, "agents_dbpedia_out.csv");

	public static File FILE_AGENTS_DBPEDIA_AMBIGUITY
		= new File(DIR_AGENTS, "agents_dbpedia_amb.csv");

	public static File FILE_AGENTS_DBPEDIA_AMBIGUITY_OUT
		= new File(DIR_AGENTS, "agents_dbpedia_amb_out.csv");

	public static File FILE_AGENTS_DBPEDIA_AMBIGUITY_ANN
		= new File(DIR_AGENTS, "agents_dbpedia_amb_annotated.csv");

	public static File FILE_AGENTS_DBPEDIA_CLUSTERS
		= new File(DIR_AGENTS, "clusters");

	public static File FILE_AGENTS_DBPEDIA
		= new File(DIR_AGENTS, "agents_dbpedia.xml");


	//CONCEPTS

	public static File DIR_CONCEPTS
		= new File(DIR_ENTITIES, "concepts");

	public static File FILE_CONCEPTS_DBPEDIA
		= new File(DIR_CONCEPTS, "concepts_dbpedia.xml");

	public static File FILE_CONCEPTS_DBPEDIA_AMBIGUITY
		= new File(DIR_CONCEPTS, "concepts_dbpedia_amb.csv");

	public static File FILE_CONCEPTS_DBPEDIA_AMBIGUITY_OUT
		= new File(DIR_CONCEPTS, "concepts_dbpedia_amb_out.csv");

	public static File FILE_CONCEPTS_DBPEDIA_AMBIGUITY_ANN
		= new File(DIR_CONCEPTS, "concepts_dbpedia_amb_annotated.csv");

	public static File FILE_CONCEPTS_DBPEDIA_CLUSTERS
		= new File(DIR_CONCEPTS, "clusters");

}
