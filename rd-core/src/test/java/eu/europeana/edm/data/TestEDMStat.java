package eu.europeana.edm.data;

import java.io.File;
import java.io.IOException;

import eu.europeana.edm.data.EDMAnalysis;

public class TestEDMStat {

	private static File DIR = new File("D:/work/incoming/nuno/");

	public static final void main(String... args) throws IOException
	{
		File src = new File(DIR, "all.edm.xml");
		File dst = new File(DIR, "all.edm.stat.txt");
		new EDMAnalysis().analyse(null, src, dst);
	}
}
