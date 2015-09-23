package eu.europeana.vocs.coref;

import java.util.Map;

public interface CoReferenceResolver
{
	public static String[] EMPTY = new String[] {};

	public void resolve(Map<String,String[]> uris);

	public String[] resolve(String uri);
}
