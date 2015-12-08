/**
 * 
 */
package eu.europeana.harvester.sitemap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import eu.europeana.harvester.sitemap.SitemapHarvester.Callback;
import eu.europeana.harvester.sitemap.SitemapHarvester.URL2URNCallback;
import eu.europeana.harvester.sitemap.SitemapHarvester.PrintStreamCallback;
import static org.apache.commons.io.IOUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 7 Dec 2015
 */
public class RunSitemapHarvest
{
    private static void harvestAsURN(PrintStream ps)
    {
        Callback cb = new URL2URNCallback(new PrintStreamCallback(ps));
        try {
            new SitemapHarvester().harvest(cb);
        }
        finally { ps.flush(); }
    }

    private static void harvestToZip(File file, String name) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(file);
        ZipOutputStream zos = new ZipOutputStream(fos);

        try {
            ZipEntry zipEntry = new ZipEntry(name);
            zos.putNextEntry(zipEntry);
            try {
                harvestAsURN(new PrintStream(zos));
                zos.flush();
            }
            finally {
                zos.closeEntry();
            }
        }
        finally { closeQuietly(zos); closeQuietly(fos); }
    }

    public static final void main(String[] args) throws IOException
    {
        File dir  = new File("D:\\work\\github\\rd-harvester\\src\\main\\resources\\etc");
        File file = new File(dir, "listidentifiers.zip");

        harvestToZip(file, "listidentifiers.txt");
    }
}
