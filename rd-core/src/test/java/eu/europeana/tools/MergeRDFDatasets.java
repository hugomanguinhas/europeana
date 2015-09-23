package eu.europeana.tools;

import static eu.europeana.vocs.VocsUtils.*;

import java.io.File;
import java.io.IOException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class MergeRDFDatasets {

	public void merge(File src, File dst) throws IOException
	{
		Model m = ModelFactory.createDefaultModel();
		m.setNsPrefix("cc", "http://creativecommons.org/ns#");
		m.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
		m.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
		m.setNsPrefix("edm", "http://www.europeana.eu/schemas/edm/");
		m.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		m.setNsPrefix("odrl", "http://www.w3.org/ns/odrl/2/");
		m.setNsPrefix("ore", "http://www.openarchives.org/ore/terms/");
		m.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
		m.setNsPrefix("rdaGr2", "http://rdvocab.info/ElementsGr2/");
		m.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
		m.setNsPrefix("wgs84_pos", "http://www.w3.org/2003/01/geo/wgs84_pos#");
		m.setNsPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		m.setNsPrefix("xml", "http://www.w3.org/XML/1998/namespace");

		merge(src, m);
		//store(m, dst, "RDF/XML-ABBREV");
		store(m, dst, "RDF/XML");
	}

	public void merge(File src, Model m)
	{
		if ( !src.isDirectory() ) { loadModel(m, src, null); return; }
		for ( File f : src.listFiles() )
		{
			if ( f.isDirectory() || f.getName().endsWith(".xml") ) { merge(f, m); }
		}
	}

	public static final void main(String... args) throws IOException
	{
		File src = new File("D:\\work\\incoming\\nuno\\TF_VALIDATION_2015-04-18");
		File dst = new File("D:\\work\\incoming\\nuno\\TF_VALIDATION_2015-04-18\\All.edm.xml");
		new MergeRDFDatasets().merge(src, dst);
	}
}
