import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Model;

import static eu.europeana.vocs.VocsUtils.*;

public class TestPattern {

	public static void main(String... args) {
		String pStr = "^\\s*([A-Za-z][\\.\\s$])+";
		System.out.println(pStr);

		List<String> test = Arrays.asList(
				"J", "J.", "J.F.K.", "J. F", "J F.", "J F", "John F.", "F.");
		Pattern p = Pattern.compile(pStr);
		for (String s : test) 
		{
			System.out.println(s + ": " + p.matcher(s).matches());
		}
	}
}