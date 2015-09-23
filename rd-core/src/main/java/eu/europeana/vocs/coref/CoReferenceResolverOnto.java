package eu.europeana.vocs.coref;

import java.util.Arrays;
import java.util.regex.Pattern;

public class CoReferenceResolverOnto extends CoReferenceResolverImpl
{
	private static String QUERY = "PREFIX tgsi: <http://data.tagasauris.com/ontologies/core/> "
			                    + "SELECT ?x WHERE { <#URI#> tgsi:exactMatch ?x }";
	//"FILTER strstarts(str(?x), \"http://dbpedia.org/resource/\")
	
	public CoReferenceResolverOnto(String sparql, Pattern accept)
	{
		super(sparql, accept);
	}

	protected String getTemplate() { return QUERY; }

	public static void main(String[] args)
	{
		CoReferenceResolverOnto cr = new CoReferenceResolverOnto(
				"http://mediagraph.ontotext.com/repositories/c5"
			  , Pattern.compile("http://dbpedia[.]org.*"));
		System.out.println(Arrays.asList(cr.resolve("http://data.tagasauris.com/instances/m9b74ypsz2hq")).toString());
	}
}
