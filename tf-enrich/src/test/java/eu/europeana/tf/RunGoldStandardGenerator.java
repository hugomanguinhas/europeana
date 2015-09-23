package eu.europeana.tf;

import static eu.europeana.tf.TaskForceConstants.*;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.europeana.tf.corpus.GoldStandardGenerator;

public class RunGoldStandardGenerator
{
	public static final void main(String[] args)
	{
		GoldStandardGenerator gen = new GoldStandardGenerator(FILE_DATASET);

		File dir = DIR_EVAL_CLUSTERS;
		for ( File file : dir.listFiles() )
		{
			String name = getKey(file.getName()) + ".csv";
			gen.genGoldStandard(file, new File(DIR_GOLD_STANDARD, name));
		}
	}

	private static String getKey(String name)
	{
		Pattern p = Pattern.compile("(\\#\\d+)\\_.*");
		Matcher m = p.matcher(name);
		if ( m.matches() ) { return m.group(1); }
		return "?";
	}
}
