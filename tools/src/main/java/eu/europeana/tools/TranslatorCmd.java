package eu.europeana.tools;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import static eu.europeana.tools.TranslatorCmdInfo.*;

public class TranslatorCmd {

	public static void main(String... args) throws Exception
	{
		Object ret = checkParameters(args);
		if ( ret instanceof String ) { printError((String)ret); return; }
		Object[] objs = (Object[])ret;

		printHeader();

		Translator t = new Translator((Transformer)objs[1]);
		try {
			t.translate((File)objs[0], (File)objs[2]);
		}
		catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	private static Object checkParameters(String[] args)
	{
		if ( args.length != 3 ) { return "Wrong number of parameters!"; }

		Object[] ret = new Object[3];
		String arg1 = (String)args[0];
		File source = new File(arg1);
		if ( source.exists() == false ) { return "Unknown file or directory: " + arg1; }
		ret[0] = source;

		String arg2 = (String)args[1];
		File xslt = new File(arg2);
		if ( xslt.exists() == false ) { return "Unknown file or directory: " + arg2; }
		try {
			ret[1] = TransformerFactory.newInstance().newTransformer(new StreamSource(xslt));
		}
		catch (Exception e) { return "Problems parsing xslt file: " + arg2; }

		String arg3 = (String)args[2];
		File target = new File(arg3);
		//if ( target.exists() )        { return "File already exists: " + arg3; }
		//if ( !arg3.endsWith(".zip") ) { return "Must be a zip file: " + arg3;  }
		ret[2] = target;

		return ret;
	}
}
