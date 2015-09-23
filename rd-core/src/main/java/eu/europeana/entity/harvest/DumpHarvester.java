/**
 * 
 */
package eu.europeana.entity.harvest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.RDFFormat;

import com.hp.hpl.jena.rdf.model.Model;

import eu.europeana.utils.JenaUtils;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 7 Aug 2015
 */
public class DumpHarvester
{
    public static File[] EMPTY = {};

    private File       _tmp;
    private HttpClient _client = new HttpClient();

    public DumpHarvester(File tmp)
    {
        _tmp = tmp;
    }

    public void harvestAndStore(File src, Model model)
    {
        try
        {
            harvestAndStore(FileUtils.readLines(src, "UTF-8"), model);
        }
        catch (IOException e) {
            System.err.println("Cannot read file: " + src.getName()
                             + "reason: " + e.getMessage());
        }
    }

    public void harvestAndStore(Collection<String> urls, Model model)
    {
        for ( String url : urls )
        {
            File tmp = getTmpFile(url);
            if ( !harvest(url, tmp) ) { continue; }

            File[] files = extractFiles(tmp);
            for ( File f : files ) { load(f, model); f.delete(); }
        }
    }

    private File getTmpFile(String url)
    {
        int i = url.lastIndexOf('/');
        return new File(_tmp, url.substring(i+1));
    }

    private boolean harvest(String url, File tmp)
    {
        System.out.print("Harvesting: " + url);
        GetMethod method = new GetMethod(url);
        try {
            int rsp = _client.executeMethod(method);
            if ( rsp != HttpStatus.SC_OK ) { return false; }
        }
        catch (IOException e) { return false; }

        long length = method.getResponseContentLength();
        System.out.print(" (" + length + ")");

        InputStream in = null; OutputStream out = null;
        try
        {
            in  = method.getResponseBodyAsStream();
            out = new FileOutputStream(tmp);
            long copied = IOUtils.copyLarge(in,out);
            out.flush();
            boolean check = (length == copied);
            System.out.println(" " + (check ? "DONE" : "NOK"));
            return true;
        }
        catch (IOException e) { e.printStackTrace(); }
        finally { IOUtils.closeQuietly(in); IOUtils.closeQuietly(out); }

        return false;
    }


    private boolean load(File tmp, Model m)
    {
        RDFFormat format = JenaUtils.getJenaFormat(tmp.getName());
        if ( format == null ) { return false; }

        System.out.println("Loading file: " + tmp.getName() + "...");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(tmp);
            m.read(fis, null, format.getLang().getLabel());
            return true;
        }
        catch (FileNotFoundException e) {}
        finally { IOUtils.closeQuietly(fis); }

        return false;
    }


    /**************************************************************************/
    /* Overridden Methods                                                     */
    /**************************************************************************/

    public static File[] extractFiles(File tmp)
    {
        File ret1 = prepareCompressedFile(tmp);
        if ( ret1 != null ) { return new File[] { ret1 }; }

        File[] ret2 = prepareArchiveFile(tmp);

        if ( ret2 != null ) { return ret2; }

        return new File[] { tmp };
    }

    public static File prepareCompressedFile(File tmp)
    {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(tmp));

            CompressorInputStream cis = null; FileOutputStream fos = null;
            File tmp2;
            try {
                cis  = new CompressorStreamFactory()
                    .createCompressorInputStream(is);
                tmp2 = getNewFileWithoutExt(tmp);
                fos  = new FileOutputStream(tmp2);
                IOUtils.copyLarge(cis, fos);
                fos.flush();
            }
            finally {
                IOUtils.closeQuietly(fos);
                IOUtils.closeQuietly(cis);
            }

            FileUtils.forceDeleteOnExit(tmp);
            return tmp2;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            IOUtils.closeQuietly(is); return tmp;
        }
        catch (CompressorException e) { return null; }
    }

    public static File[] prepareArchiveFile(File tmp)
    {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(tmp));

            ArchiveInputStream ais = new ArchiveStreamFactory()
                .createArchiveInputStream(is);

            List<File> files = new ArrayList(10);
            ArchiveEntry ae;
            while ( (ae = ais.getNextEntry()) != null )
            {
                File tmp2 = getNewFileForEntry(tmp, ae);
                FileOutputStream fos = new FileOutputStream(tmp2);
                IOUtils.copyLarge(ais, fos);
                fos.flush();
                IOUtils.closeQuietly(fos);
                files.add(tmp2);
            }

            IOUtils.closeQuietly(is);
            FileUtils.forceDeleteOnExit(tmp);
            return files.toArray(EMPTY);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            IOUtils.closeQuietly(is); return new File[] { tmp };
        }
        catch (ArchiveException e) { return null; }
    }

    private static File getNewFileWithoutExt(File file)
    {
        String name = file.getName();
        int i = name.lastIndexOf('.');
        return new File(file.getParentFile(), name.substring(0,i));
    }

    private static File getNewFileForEntry(File parent, ArchiveEntry ae)
    {
        return new File(parent.getParentFile(), ae.getName());
    }

    public static final void main(String... args)
    {
        //extractFiles(new File("D:\\work\\incoming\\dbpedia\\bbcwildlife_links.nt.bz2"));
        extractFiles(new File("D:\\work\\incoming\\harvest\\article_categories_en.ttl.bz2"));
    }
}
