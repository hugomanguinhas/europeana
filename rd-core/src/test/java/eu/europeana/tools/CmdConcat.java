package eu.europeana.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class CmdConcat {

	private static String XML_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private static String XML_2 = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:cc=\"http://creativecommons.org/ns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:edm=\"http://www.europeana.eu/schemas/edm/\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmlns:odrl=\"http://www.w3.org/ns/odrl/2/\" xmlns:ore=\"http://www.openarchives.org/ore/terms/\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdaGr2=\"http://rdvocab.info/ElementsGr2/\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:wgs84_pos=\"http://www.w3.org/2003/01/geo/wgs84_pos#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\">";
	private static String XML_3 = "</rdf:RDF>";

	public void concat(File src, File dst) throws IOException
	{
		PrintStream out = new PrintStream(dst, "UTF-8");
		out.println(XML_1);
		out.println(XML_2);
		try {
			concat(src, out);
			out.println(XML_3);
		}
		finally {
			out.flush();
			out.close();
		}
	}

	public void concat(File src, PrintStream dst)
	{
		if ( !src.isDirectory() ) { concatImpl(src, dst); return; }

		for ( File f : src.listFiles() )
		{
			if ( f.isDirectory() || f.getName().endsWith(".xml") ) { concat(f, dst); }
		}
	}

	private void concatImpl(File src, PrintStream dst)
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(src));
			while ( reader.ready() )
			{
				String sLine = reader.readLine();
				
				if ( !sLine.isEmpty() && !filter(sLine) ) { dst.println(sLine); }
			}
		}
		catch (IOException e) { System.err.println("Error reading file: " + src.getName()); }
	}

	private boolean filter(String str)
	{
		if ( str.equals(XML_1) ) { return true; }
		if ( str.equals(XML_2) ) { return true; }
		if ( str.equals(XML_3) ) { return true; }
		return false;
	}

	public static final void main(String... args) throws IOException
	{
		File src = new File("D:\\work\\incoming\\nuno\\TF_VALIDATION_2015-04-18");
		File dst = new File("D:\\work\\incoming\\nuno\\TF_VALIDATION_2015-04-18\\All.edm.xml");
		new CmdConcat().concat(src, dst);
	}
}
