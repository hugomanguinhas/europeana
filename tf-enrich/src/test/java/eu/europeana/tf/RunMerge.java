package eu.europeana.tf;

import java.io.File;

import eu.europeana.tf.results.CSVMerge;

public class RunMerge
{

	public static void main(String[] args)
	{
		File dir = new File("D:\\work\\incoming\\taskforce\\results");

		File merge = new File(dir, "enrich.all.csv");

		File eur = new File(dir, "enrich.europeana.csv");
		File tel = new File(dir, "enrich.tel.csv");
		File lcl = new File(dir, "enrich.locloud.bglink.csv");
		File lcv = new File(dir, "enrich.locloud.vocmatch.csv");
		File pwd = new File(dir, "enrich.pelagios.wikidata.csv");
		File pcr = new File(dir, "enrich.pelagios.coref.csv");
		File ov1 = new File(dir, "enrich.ontotext.v1.csv");
		File ov2 = new File(dir, "enrich.ontotext.v2.csv");

		new CSVMerge().merge(
				merge, "Europeana", eur, "TEL", tel
              , "BgLinks", lcl, "VocMatch", lcv, "Pelagios WD", pwd, "Pelagios Coref", pcr
              , "Ontotext v1", ov1, "Ontotext v2", ov2);
	}
}
