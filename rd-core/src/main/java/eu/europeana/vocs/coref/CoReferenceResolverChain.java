package eu.europeana.vocs.coref;

import static eu.europeana.vocs.coref.CoReferenceUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CoReferenceResolverChain implements CoReferenceResolver
{
	private CoReferenceResolver[] _resolvers = null;

	public CoReferenceResolverChain(CoReferenceResolver... resolvers)
	{
		_resolvers = resolvers;
	}

	@Override
	public void resolve(Map<String, String[]> uris)
	{
		for ( Map.Entry<String, String[]> entry : uris.entrySet() )
		{
			entry.setValue(resolve(entry.getKey()));
		}
	}

	@Override
	public String[] resolve(String uri)
	{
		List<String> ret = new ArrayList();
		String[] keys = new String[] { uri };
		for ( CoReferenceResolver resolver : _resolvers )
		{
			ret.clear();
			for ( String key : keys )
			{
				String[] newKey = resolver.resolve(key);
				ret.addAll(Arrays.asList(newKey));
			}

			if ( ret.isEmpty() ) { break; }

			keys = ret.toArray(EMPTY);
		}
		return ret.toArray(EMPTY);
	}

	public static void main(String[] args)
	{
		System.out.println(Arrays.asList(WD_2_FB_2_DBP.resolve("http://www.wikidata.org/entity/Q210300")).toString());
	}
}
