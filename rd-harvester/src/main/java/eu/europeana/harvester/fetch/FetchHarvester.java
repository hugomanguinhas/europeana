/**
 * 
 */
package eu.europeana.harvester.fetch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import eu.europeana.harvester.InterruptHarvest;
import static eu.europeana.harvester.utils.HarvesterUtils.*;
import static org.apache.commons.io.IOUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 7 Dec 2015
 */
public class FetchHarvester
{

    private RecordFetcher   _fetcher = null;
    private ZipOutputStream _zos     = null;
    private long            _count   = 0;
    private long            _limit   = 0;

    public FetchHarvester(RecordFetcher fetcher, long limit)
    {
        _fetcher = fetcher;
        _limit   = limit;
    }


    /***************************************************************************
     * Public Methods
     **************************************************************************/

    public long harvest(Collection<String> col, File dst) throws IOException
    {
        long time = System.currentTimeMillis();

        start(dst);
        try {
            for ( String url : col ) { store(url); }
        }
        catch ( InterruptHarvest e ) {}
        finally { end(); }

        long elapsed = System.currentTimeMillis() - time;
        System.out.println("Harvested " + _count
                         + " records in " + elapsed + "ms");

        return _count;
    }

    public long harvest(InputStream is, File dst) throws IOException
    {
        long time = System.currentTimeMillis();

        start(dst);

        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            while ( r.ready() ) { store(r.readLine()); }
        }
        catch ( InterruptHarvest e ) {}
        catch ( IOException e ) { e.printStackTrace(); }
        finally { end(); }

        long elapsed = System.currentTimeMillis() - time;
        System.out.println("Harvested " + _count
                         + " records in " + elapsed + "ms");

        return _count;
    }


    /***************************************************************************
     * Private Methods
     **************************************************************************/

    private void start(File file) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(file);
        _zos = new ZipOutputStream(fos);
    }

    private void store(String url)
    {
        if ( url == null ) { return; }

        if ( _limit > 0 && _count >= _limit ) { throw new InterruptHarvest(); }

        if ( _count > 0 && _count % 1000 == 0 ) {
            System.out.println("harvested " + _count + " records...");
        }

        ZipEntry zipEntry = new ZipEntry(getLocalID(url) + ".rdf");
        try
        {
            _zos.putNextEntry(zipEntry);
            try {
                _fetcher.fetch(url, _zos);
                _zos.flush();
            }
            finally {
                _zos.closeEntry();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        _count++;
    }

    private void end()
    {
        closeQuietly(_zos);
        _zos = null;
    }
}
