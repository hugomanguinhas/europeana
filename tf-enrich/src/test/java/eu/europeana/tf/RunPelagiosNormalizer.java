package eu.europeana.tf;

import java.io.File;

import eu.europeana.tf.results.CSVNormalizerProcessor;
import eu.europeana.tf.results.CSVNormalizerProcessor.LineLoader;
import eu.europeana.tf.results.CSVNormalizerProcessor.PelagiousLineLoader;

public class RunPelagiosNormalizer {

	public static final void main(String... args)
	{
		CSVNormalizerProcessor processor = new CSVNormalizerProcessor(new PelagiousLineLoader());

		File dir = new File("D:\\work\\incoming\\taskforce\\incoming\\pelagios");
		for ( File file : dir.listFiles() )
		{
			if ( !file.getName().endsWith(".csv") ) { continue; }

			processor.process(file, null);
		}
	}
}
