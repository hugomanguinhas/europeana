package eu.europeana.vocs.coref;

import java.util.Map;

import eu.europeana.vocs.coref.cache.CorefCache;

public class CoReferenceResolverMulti implements CoReferenceResolver
{
	private CoReferenceResolver[] _resolvers = null;
	private CorefCache            _cache     = null;


	public CoReferenceResolverMulti(CorefCache cache, CoReferenceResolver... resolvers)
	{
		_cache     = cache;
		_resolvers = resolvers;
	}

	public void resolve(Map<String, String[]> uris)
	{
		for ( Map.Entry<String, String[]> entry : uris.entrySet() )
		{
			entry.setValue(resolve(entry.getKey()));
		}
	}

	public String[] resolve(String uri)
	{
		String[] ret = getCache(uri);
		if ( ret != null ) { return ret; }

		ret = EMPTY;
		for ( CoReferenceResolver resolver : _resolvers )
		{
			ret = resolver.resolve(uri);
			if ( ret.length > 0 ) { break; }
		}
		return storeCache(uri, ret);
	}

	private String[] storeCache(String uri, String[] res)
	{
		if ( _cache != null ) { _cache.addToCache(uri, res); }
		return res;
	}

	private String[] getCache(String uri)
	{
		return ( _cache == null ? null : _cache.getFromCache(uri) );
	}
}