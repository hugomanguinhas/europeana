package eu.europeana.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Translator {

	private Transformer _transformer;
	private File        _file;

	public Translator(Transformer t)
	{
		_transformer = t;
	}

	public Translator(File f)
	{
		_file = f;
	}

	public void translate(File source, File target) throws IOException
	{
		if ( target.getName().endsWith(".zip") ) { translate2Zip(source, target); }
		else { translate2Dir(source, target); }
	}

	private Transformer getTransformer()
	{
		_transformer.clearParameters();
		_transformer.reset();
		return _transformer;
	}

	private void releaseTransformer(Transformer t)
	{
	}

	private void translate2Zip(File source, File target) throws IOException
	{
		long time = System.currentTimeMillis();
		System.out.println("Building file: " + target.getAbsolutePath());
		System.out.println();

		FileOutputStream fos = new FileOutputStream(target);
		ZipOutputStream zos = new ZipOutputStream(fos);

		try {
			File dir = source.isDirectory() ? source : source.getParentFile();
			translateInt(dir.getAbsolutePath().length()+1, source, zos);
		}
		finally {
			zos.close();
			fos.close();
		}

		long elapsed = System.currentTimeMillis() - time;
		System.out.println();
		System.out.println("Completed in " + elapsed + "ms!");
	}

	private void translate2Dir(File source, File target) throws IOException
	{
		long time = System.currentTimeMillis();
		System.out.println("Building dir: " + target.getAbsolutePath());
		System.out.println();

		target.mkdirs();

		File dir = source.isDirectory() ? source : source.getParentFile();
		translateInt(dir.getAbsolutePath().length()+1, source, target);

		long elapsed = System.currentTimeMillis() - time;
		System.out.println();
		System.out.println("Completed in " + elapsed + "ms!");
	}

	private void translateInt(int iPrefix, File source, ZipOutputStream zos) throws IOException
	{
		if ( source.isDirectory() ) { translateDir (iPrefix, source, zos); }
		else                        { translateFile(iPrefix, source, zos); }
	}

	private void translateInt(int iPrefix, File source, File dest) throws IOException
	{
		if ( source.isDirectory() ) { translateDir (iPrefix, source, dest); }
		else                        { translateFile(iPrefix, source, dest); }
	}

	private void translateDir(int iPrefix, File source, ZipOutputStream zos) throws IOException
	{
		for(File f : source.listFiles() ) { translateInt(iPrefix, f, zos); }
	}

	private void translateDir(int iPrefix, File source, File dest) throws IOException
	{
		for(File f : source.listFiles() ) { translateInt(iPrefix, f, dest); }
	}

	private void translateFile(int iPrefix, File source, ZipOutputStream zos) throws IOException
	{
		String name = source.getName();
		if ( !name.endsWith(".xml") ) { return; }

		Transformer t = getTransformer();

		ZipEntry zipEntry = new ZipEntry(source.getAbsolutePath().substring(iPrefix));
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
		//NL-HaNA_fa_3.03.02.xml
	}

	private void translateFile(int iPrefix, File source, File dest) throws IOException
	{
		String name = source.getName();
		if ( !name.endsWith(".xml") ) { return; }

		File out = new File(dest, source.getAbsolutePath().substring(iPrefix));
		out.getParentFile().mkdirs();

		try {
			System.out.println("Processing file: " + source.getAbsolutePath());
			_transformer.clearParameters();
			_transformer.reset();
			_transformer.transform(new StreamSource(source), new StreamResult(out));
		}
		catch (Throwable t) {
			System.out.println("Error processing file: " + source.getName()
					         + ", reason: " + t.getMessage());
		}
		System.gc();
	}
}