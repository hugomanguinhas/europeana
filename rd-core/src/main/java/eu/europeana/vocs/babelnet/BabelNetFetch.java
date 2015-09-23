package eu.europeana.vocs.babelnet;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import static eu.europeana.vocs.VocsUtils.*;

/*
Methods:
+ Fetched resources through the URI 
+ Fetched all <bn:definition> for each resource:
  + By making an initial request to retrieve all definition
  + , e.g.: http://babelnet.org/rdf/values.data/bn-lemon:definition/sXXXXX
  + Fixed xml:lang for literals by filling it with the content of <lemon:language>
  + Imported some properties (e.g. <bn-lemon:gloss>) into the "spine" resource

Known issues:
+ Literals do not have a xml:lang attribute, language is defined in a separate property <lemon:language>
+ Spine resource does not contain all <bn:definition>. Requires an additional request to retrieve them.
*/


public class BabelNetFetch {

	public static String URI_BN_DEF   = "http://babelnet.org/model/babelnet#definition";
	public static String URI_BN_GLOSS = "http://babelnet.org/model/babelnet#gloss";
	public static String URI_LEMON_LANG = "http://www.lemon-model.net/lemon#language";

	public BabelNetFetch() {}

	public void fetchAll(File src, File dst) throws IOException
	{
		Collection<String> saURIs  = loadDataURLs(src, PATTERN_BABELNET);

		Model m = getModel(null);
		loadModel(m, saURIs);

		fetchDefinitions(m, saURIs);

		Property pDef   = m.getProperty(URI_BN_DEF);
		Property pGloss = m.getProperty(URI_BN_GLOSS);
		for ( String sURI : saURIs )
		{
			Resource r = m.getResource(sURI);
			Collection<Resource> trans = getObjectRanges(r, pDef);
			fetchResources(m, trans);

			for ( Resource r2 : trans )
			{
				String sLang = getLanguage(r2);
				fixLanguage(r2.listProperties(pGloss), sLang);
			}

			transferProperties(trans, r, pGloss);
		}

		store(m, dst);
	}

	private String getLanguage(Resource r)
	{
		Property pLang = r.getModel().getProperty(URI_LEMON_LANG);
		Statement stmt = r.getProperty(pLang);
		if ( stmt == null ) { return null; }
		return stmt.getLiteral().getString().toLowerCase();
	}

	private String getDefinitionURI(String s)
	{
		return s.replace("http://babelnet.org/rdf/"
			           , "http://babelnet.org/rdf/values.data/bn-lemon:definition/");
	}

	private void fetchDefinitions(Model m, Collection<String> sa)
	{
		for ( String sURI : sa )
		{
			String sDefURI = getDefinitionURI(sURI);
			loadModel(m, sDefURI);
		}
		
		//http://babelnet.org/rdf/values.data/bn-lemon:definition/s00057563n
	}
}
