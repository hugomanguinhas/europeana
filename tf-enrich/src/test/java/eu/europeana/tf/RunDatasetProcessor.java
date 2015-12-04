/**
 * 
 */
package eu.europeana.tf;

import java.io.File;
import java.io.IOException;

import eu.europeana.tf.dataset.DatasetProcessor;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 16 Oct 2015
 */
public class RunDatasetProcessor
{
    public static void main(String[] args) throws IOException
    {
        File dir = new File("D:\\work\\incoming\\nuno2\\");
        File src = new File(dir, "TF_VALIDATION_2015-04-24");

        File all = new File(dir, "dataset.tel.xml");
        File dst = new File(dir, "dataset.xml");
        File enr = new File(dir, "enrich.tel.csv");
        new DatasetProcessor().process(src, all, dst, enr);
    }
}