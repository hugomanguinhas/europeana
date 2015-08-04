package eu.europeana.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Translator
{
    private Transformer _transformer;
    private File        _file;


    public Translator(Transformer t) { _transformer = t; }

    public Translator(File f)        { _file = f; }


    /**************************************************************************/
    /* Public Methods
    /**************************************************************************/
    public void translate(File src, File dst) throws IOException
    {
        if ( dst.getName().endsWith(".zip") ) { translate2Zip(src, dst); }
        else                                  { translate2Dir(src, dst); }
    }


    /**************************************************************************/
    /* Private Methods
    /**************************************************************************/
    private Transformer getTransformer()
    {
        _transformer.clearParameters();
        _transformer.reset();
        return _transformer;
    }

    private void releaseTransformer(Transformer t)
    {
    }

    private void translate2Zip(File src, File dst) throws IOException
    {
        long time = System.currentTimeMillis();
        System.out.println("Building file: " + dst.getAbsolutePath());
        System.out.println();

        FileOutputStream fos = new FileOutputStream(dst);
        ZipOutputStream zos = new ZipOutputStream(fos);

        try {
            File dir = src.isDirectory() ? src : src.getParentFile();
            translateInt(dir.getAbsolutePath().length()+1, src, zos);
        }
        finally {
            zos.close();
            fos.close();
        }

        long elapsed = System.currentTimeMillis() - time;
        System.out.println();
        System.out.println("Completed in " + elapsed + "ms!");
    }

    private void translate2Dir(File src, File dst) throws IOException
    {
        long time = System.currentTimeMillis();
        System.out.println("Building dir: " + dst.getAbsolutePath());
        System.out.println();

        dst.mkdirs();

        File dir = src.isDirectory() ? src : src.getParentFile();
        translateInt(dir.getAbsolutePath().length()+1, src, dst);

        long elapsed = System.currentTimeMillis() - time;
        System.out.println();
        System.out.println("Completed in " + elapsed + "ms!");
    }

    private void translateInt(int indexP
                            , File src, ZipOutputStream zos) throws IOException
    {
        if ( src.isDirectory() ) { translateDir (indexP, src, zos); }
        else                     { translateFile(indexP, src, zos); }
    }

    private void translateInt(int indexP, File src, File dst) throws IOException
    {
        if ( src.isDirectory() ) { translateDir (indexP, src, dst); }
        else                     { translateFile(indexP, src, dst); }
    }

    private void translateDir(int iPrefix, File src
                            , ZipOutputStream zos) throws IOException
    {
        for(File f : src.listFiles() ) { translateInt(iPrefix, f, zos); }
    }

    private void translateDir(int iPrefix
                            , File src, File dst) throws IOException
    {
        for(File f : src.listFiles() ) { translateInt(iPrefix, f, dst); }
    }

    private void translateFile(int iPrefix, File source
                             , ZipOutputStream zos) throws IOException
    {
        String name = source.getName();
        if ( !name.endsWith(".xml") ) { return; }

        Transformer t = getTransformer();

        String path = source.getAbsolutePath().substring(iPrefix);
        ZipEntry zipEntry = new ZipEntry(path);
        zos.putNextEntry(zipEntry);
        try {
            System.out.println("Processing file: " + source.getAbsolutePath());
            t.transform(new StreamSource(source), new StreamResult(zos));
            zos.flush();
        }
        catch (Throwable th) {
            System.out.println("Error processing file: " + source.getName()
                             + ", reason: " + th.getMessage());
        }
        finally {
            zos.closeEntry();
            releaseTransformer(t);
        }
        System.gc();
    }

    private void translateFile(int iPrefix, File src
                             , File dst) throws IOException
    {
        String name = src.getName();
        if ( !name.endsWith(".xml") ) { return; }

        File out = new File(dst, src.getAbsolutePath().substring(iPrefix));
        out.getParentFile().mkdirs();

        try {
            System.out.println("Processing file: " + src.getAbsolutePath());
            _transformer.clearParameters();
            _transformer.reset();
            _transformer.transform(new StreamSource(src)
                                 , new StreamResult(out));
        }
        catch (Throwable t) {
            System.out.println("Error processing file: " + src.getName()
                             + ", reason: " + t.getMessage());
        }
        System.gc();
    }
}