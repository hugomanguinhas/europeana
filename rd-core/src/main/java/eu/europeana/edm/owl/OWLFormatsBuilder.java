package eu.europeana.edm.owl;

import static eu.europeana.vocs.VocsUtils.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;

public class OWLFormatsBuilder {

	private static Map<String,String> MAP = new HashMap<String,String>();

	static {
		MAP.put("TTL","ttl");
		MAP.put("NT" ,"nt");
		MAP.put("N3" ,"n3");
	}

	private File getFormatFile(File file, String suffix)
	{
		String sFN = file.getName();
		if ( sFN.endsWith(".owl") ) { 
			sFN = sFN.substring(0, sFN.length() - 3) + suffix;
		}
		return new File(file.getParentFile(), sFN);
	}

	public void generateFormats(File file) throws IOException
	{
		Model m = getModel(file);

		for ( String format : MAP.keySet() )
		{
			File dest = getFormatFile(file, MAP.get(format));
			System.err.println(dest.getAbsolutePath());
			store(m, dest, format);
		}
	}

	public static void main(String[] args) throws IOException {
		File file = new File("C:\\Users\\mangas\\Google Drive\\Europeana\\EDM\\owl\\edm.owl");
		new OWLFormatsBuilder().generateFormats(file);
	}
}
