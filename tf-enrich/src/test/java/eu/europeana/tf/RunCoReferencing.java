package eu.europeana.tf;

import java.io.File;

import eu.europeana.tf.results.ResultsCoReferencer;
import eu.europeana.vocs.coref.CoReferenceResolver;
import eu.europeana.vocs.coref.CoReferenceResolverMulti;
import eu.europeana.vocs.coref.cache.CorefCache;
import static eu.europeana.tf.TaskForceConstants.*;
import static eu.europeana.vocs.coref.CoReferenceUtils.*;

public class RunCoReferencing
{

	private static void corefOnto()
	{
		File cacheFile = new File("D:\\work\\incoming\\taskforce\\cache\\onto.coref.cache.csv");

		CorefCache cache = new CorefCache();
		cache.load(cacheFile);

		CoReferenceResolver resolvers = new CoReferenceResolverMulti(cache, ONTO_2_DBP);

		File dir = new File("D:\\work\\incoming\\taskforce\\results");
		File src = new File(dir, "enrich.ontotext.v2.csv");
		File trg = new File(dir, "enrich.ontotext.v2.coref.csv");

		new ResultsCoReferencer().process(src, trg, resolvers);
	}

	private static void corefPelagios()
	{
		CoReferenceResolver resolvers = new CoReferenceResolverMulti(
				new CorefCache(), WD_2_GN, WD_2_DBP);

		File src = new File(DIR_RESULTS, "enrich.pelagios.wikidata.csv");
		File trg = new File(DIR_RESULTS, "enrich.pelagios.coref.2.csv");

		new ResultsCoReferencer().process(src, trg, resolvers);
	}
	
	public static void main(String[] args)
	{
		corefPelagios();
	}
}
