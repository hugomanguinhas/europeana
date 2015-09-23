package eu.europeana.vocs.coref;

import static eu.europeana.vocs.VocsUtils.*;

import java.util.regex.Pattern;

import eu.europeana.vocs.wikidata.WikidataCoReferenceResolver;
import eu.europeana.vocs.wikidata.WikidataCoReferenceResolver.LiteralProcessor;
import eu.europeana.vocs.wikidata.WikidataCoReferenceResolver.ResourceProcessor;

public class CoReferenceUtils
{
	public static CoReferenceResolver WD_2_FB
		= new WikidataCoReferenceResolver(
				SPARQL_WIKIDATA
			  , "http://www.wikidata.org/entity/P646-freebase"
			  , new ResourceProcessor());

	public static CoReferenceResolver WD_2_GN
		= new WikidataCoReferenceResolver(
				SPARQL_WIKIDATA
			  , "http://www.wikidata.org/entity/P1566c"
			  , new LiteralProcessor("http://sws.geonames.org/#VALUE#/"));

	public static CoReferenceResolver WD_2_DBP
		= new CoReferenceResolverInv(
				SPARQL_DBPEDIA_EN
			  , Pattern.compile("http://dbpedia[.]org.*"));

	public static CoReferenceResolver FB_2_DBP
		= new CoReferenceResolverInv(
				SPARQL_DBPEDIA_EN
			  , Pattern.compile("http://dbpedia[.]org.*"));

	public static CoReferenceResolver WD_2_FB_2_DBP
		= new CoReferenceResolverChain(WD_2_FB, FB_2_DBP);


	public static CoReferenceResolver ONTO_2_DBP
		= new CoReferenceResolverOnto(
			"http://mediagraph.ontotext.com/repositories/c5"
		  , Pattern.compile("http://dbpedia[.]org.*"));
}
