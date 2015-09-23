package eu.europeana.edm.schema;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class TestEDMSchemaValidation {

	public void validate(File source, URL schemaFile) throws SAXException, IOException
	{
		Source xmlFile = new StreamSource(source);
		SchemaFactory schemaFactory = SchemaFactory
		    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(schemaFile);
		Validator validator = schema.newValidator();
		try {
		  validator.validate(xmlFile);
		  System.out.println(xmlFile.getSystemId() + " is valid");
		} catch (SAXException e) {
		  System.out.println(xmlFile.getSystemId() + " is NOT valid");
		  System.out.println("Reason: " + e.getLocalizedMessage());
		}
	}

	public static void main(String... args) throws Exception
	{
		URL schemaFile = new URL("file:///D:/work/incoming/edm/EDM-INTERNAL.xsd");
		File f = new File("D:/work/incoming/edm/EDM.xml");
		new TestEDMSchemaValidation().validate(f, schemaFile);
	}
}
