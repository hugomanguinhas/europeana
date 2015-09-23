package eu.europeana.tf;

import java.io.File;

import eu.europeana.tf.results.EnrichmentEvaluation;

public class RunEnrichmentEvaluation
{

	public static void main(String[] args)
	{
		File dir  = new File("D:\\work\\incoming\\taskforce\\results");
		File eval = new File("D:\\work\\incoming\\taskforce\\eval");

		File eur = new File(dir, "enrich.europeana.csv");
		File tel = new File(dir, "enrich.tel.csv");
		File lcl = new File(dir, "enrich.locloud.bglink.csv");
		File lcv = new File(dir, "enrich.locloud.vocmatch.csv");
		File pcr = new File(dir, "enrich.pelagios.coref.csv");
		File ov1 = new File(dir, "enrich.ontotext.v1.coref.csv");
		File ov2 = new File(dir, "enrich.ontotext.v2.coref.csv");

		new EnrichmentEvaluation().compare(
				eval, "Europeana", eur, "TEL", tel
              , "BgLinks", lcl, "VocMatch", lcv
              , "Pelagios Coref", pcr
              , "Ontotext v1 Coref", ov1, "Ontotext v2 Coref", ov2);
	}
}
