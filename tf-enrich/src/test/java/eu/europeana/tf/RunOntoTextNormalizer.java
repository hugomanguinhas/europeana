package eu.europeana.tf;

import java.io.File;

import eu.europeana.tf.results.CSVNormalizerProcessor;
import eu.europeana.tf.results.CSVNormalizerProcessor.OntoTextLineLoader;

public class RunOntoTextNormalizer {

	public static final void main(String... args)
	{
		CSVNormalizerProcessor processor = new CSVNormalizerProcessor(new OntoTextLineLoader());

		File dir = new File("D:\\work\\incoming\\taskforce\\incoming\\ontotext");
		for ( File file : dir.listFiles() )
		{
			if ( !file.getName().endsWith(".csv") ) { continue; }

			processor.process(file, null);
		}
	}
}
