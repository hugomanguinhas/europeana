package eu.europeana.tf;

import java.io.File;

import eu.europeana.tf.results.CSVDuplicateCheck;

public class RunDublicateCheck
{

	public static void main(String[] args)
	{
		File dir = new File("D:\\work\\incoming\\taskforce\\results");
		File eur = new File(dir, "enrich.europeana.csv");
		File tel = new File(dir, "enrich.tel.csv");
		File lcl = new File(dir, "enrich.locloud.bglink.csv");
		File lcv = new File(dir, "enrich.locloud.vocmatch.csv");
		File wd  = new File(dir, "enrich.pelagios.wikidata.csv");
		new CSVDuplicateCheck().check(lcl);
	}
}
