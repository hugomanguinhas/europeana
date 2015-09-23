package eu.europeana.tf;

import java.io.File;

import eu.europeana.tf.results.EnrichmentAnalysis;

public class RunEnrichmentAnalysis
{

	public static void main(String[] args)
	{
		File result;

		File dir = new File("D:\\work\\incoming\\taskforce\\results");

		File eur = new File(dir, "enrich.europeana.csv");
		File tel = new File(dir, "enrich.tel.csv");
		File lcl = new File(dir, "enrich.locloud.bglink.csv");
		File lcv = new File(dir, "enrich.locloud.vocmatch.csv");
		File pwd = new File(dir, "enrich.pelagios.wikidata.csv");
		File pcr = new File(dir, "enrich.pelagios.coref.csv");
		File ov1 = new File(dir, "enrich.ontotext.v1.coref.csv");
		File ov2 = new File(dir, "enrich.ontotext.v2.coref.csv");

		Object[] oa = new Object[] {
			"Europeana", eur, "TEL", tel
          , "BgLinks", lcl, "VocMatch", lcv
          , "Pelagios WD", pwd, "Pelagios Coref", pcr
          , "Ontotext v1", ov1, "Ontotext v2", ov2
		};

		result = new File(dir, "stat.property.csv");
		new EnrichmentAnalysis(2).analyse(result, oa);

		result = new File(dir, "stat.scheme.csv");
		new EnrichmentAnalysis(4).analyse(result, oa);

		result = new File(dir, "stat.property.scheme.csv");
		new EnrichmentAnalysis(2,4).analyse(result, oa);
	}
}
