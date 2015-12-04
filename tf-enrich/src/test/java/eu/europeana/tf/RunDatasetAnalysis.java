/**
 * 
 */
package eu.europeana.tf;

import java.io.File;
import java.io.IOException;

import eu.europeana.edm.data.CHOAnalysis;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 16 Oct 2015
 */
public class RunDatasetAnalysis
{
    public static void main(String[] args) throws IOException
    {
        File dir = new File("D:\\work\\incoming\\taskforce\\dataset\\");
        File src = new File(dir, "dataset.xml");
        File rpt = new File(dir, "dataset_2.txt");

        new CHOAnalysis().analyse(src).print(rpt);
    }
}