/**
 * 
 */
package eu.europeana.harvester.fetch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 7 Dec 2015
 */
public class RunFetchHarvest
{

    public static final void main(String[] args) throws IOException
    {
        String mime  = "application/rdf+xml";
        String wskey = "api2demo";
        File src = new File("D:\\work\\data\\listidentifiers.zip");
        File dst = new File("D:\\work\\data\\dump.zip");

        ZipInputStream zis = new ZipInputStream(new FileInputStream(src));
        ZipEntry ze = null;
        while ((ze = zis.getNextEntry()) != null)
        {
            if (ze.isDirectory()) { continue; }

            new FetchHarvester(new RecordFetcher(mime, wskey, false), 100)
                .harvest(zis, dst);
            break;
        }
    }
}
