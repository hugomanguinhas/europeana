import java.io.File;

import com.hp.hpl.jena.rdf.model.Model;

import static eu.europeana.vocs.VocsUtils.*;

public class Test {

	public static void main(String... args) {
		Model m = getModel(new File("C:\\Users\\Hugo\\Downloads\\storedagents.xml"));
		m.write(System.out, "RDF/XML");
	}
}