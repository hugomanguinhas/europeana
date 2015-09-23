import java.io.File;

import com.hp.hpl.jena.rdf.model.Model;

import static eu.europeana.vocs.VocsUtils.*;

public class RunConvert2Turtle {

	public static void main(String... args)
	{
		Model m = getModel(new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\R&D\\presentations\\MDN\\example_clavecin.xml"));
		m.write(System.out, "Turtle");
	}
}