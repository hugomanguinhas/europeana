package eu.europeana.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class CmdCat {

	public void cat(PrintStream out, int from, int to, File input)
	{
		try {
			cat(new BufferedReader(new FileReader(input)), out, from, to);
		}
		catch (IOException e) { e.printStackTrace(); }
	}

	public void cat(PrintStream out, int from, int to, File... inputs)
	{
		for ( File input : inputs ) { cat(out, from, to, input); }
	}

	private void cat(BufferedReader r, PrintStream out, int from, int to) throws IOException
	{
		if ( r == null ) { return; }

		try {
			for (int i = 0; i < to; i++)
			{
				String sLine = r.readLine();
				if ( sLine == null ) { return;   }
				if ( i     <  from ) { continue; }
				out.println(sLine);
			}
		}
		finally {
			out.flush(); r.close();
		}
	}

	public static void main(String[] args) throws FileNotFoundException
	{
		File file = new File("D:\\work\\incoming\\dbpedia\\infobox_properties_en.ttl");
		File out = new File("D:\\work\\incoming\\dbpedia\\infobox_properties_en_1.ttl");
		new CmdCat().cat(new PrintStream(out), 30000000, 40000000, file);
	}
}
