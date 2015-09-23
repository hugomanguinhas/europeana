package eu.europeana.ld.sparql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import eu.europeana.ld.sparql.TestQueryConfig.Type;

public class TestQuery {

	private static String NAMESPACES
	  = "PREFIX edm: <http://www.europeana.eu/schemas/edm/>\n"
	  + "PREFIX ore: <http://www.openarchives.org/ore/terms/>\n"
	  + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
	  + "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
	  + "PREFIX dcterms: <http://purl.org/dc/terms/>\n";

	public static int DEFAULT_LIMIT = 100;

	private String   _id;
	private String   _query;
	private String[] _cols;
	private Map<Type,String> _queries;

	public TestQuery(String id, String query)
	{
		_id    = id;
		_query = query;
		_queries = new TreeMap();
	}

	public String getID()           { return _id; }

	public String getQuery()        { return _query; }

	public String getQuery(Type type)  { return _queries.get(type); }

	public void setQuery(String query) { _query = process(query); }

	private String getColumnsAsString(String[] saCols)
	{
		return getColumnsAsString(saCols, saCols.length);
	}

	private String getColumnsAsString(String[] saCols, int length)
	{
		length = Math.min(saCols.length, length);
		String s = "";
		for ( int i = 0; i < length; i++ )
		{
			s = (i == 0 ? "" : s + " ") + saCols[i];
		}
		return s;
	}

	private String process(String query)
	{
		query = NAMESPACES + query;
		_cols = getColumns(query);
		_queries.put(Type.PLAIN   , appendLimit(query));
		_queries.put(Type.DISTINCT, genDistinctQuery(query, _cols));
		_queries.put(Type.ORDER   , genOrderedQuery(query, _cols));
		_queries.put(Type.ORDER   , genCountQuery(query, _cols));
		return query;
	}

	private String genDistinctQuery(String query, String[] saCols)
	{
		return appendLimit(replaceColumns(query, " DISTINCT " + getColumnsAsString(saCols)));
	}

	private String genOrderedQuery(String query, String[] saCols)
	{
		String s = query + "\nORDER BY";
		for ( String sCol : saCols ) { s += " " + sCol; }
		return appendLimit(s);
	}

	private String genCountQuery(String query, String[] saCols)
	{
		return appendLimit(addCount(query));
	}

	private String[] getColumns(String query)
	{
		int i1 = query.indexOf("SELECT");
		int i2 = query.indexOf("WHERE", i1);
		String s = query.substring(i1+7, i2-1);

		String[]     sa    = s.split("\\s+");
		List<String> saCol = new ArrayList(sa.length);
		for ( String sCol : sa )
		{
			sCol = sCol.trim();
			if ( sCol.equalsIgnoreCase("DISTINCT") ) { continue; }

			saCol.add(sCol);
		}
		return saCol.toArray(new String[] {});
	}


	private String addCount(String query)
	{
		if (_cols.length > 1) { query = appendGroupBy(query); }

		int last = _cols.length-1;
		String sCol = getColumnsAsString(_cols, last);
		sCol = sCol + " (count(DISTINCT " + _cols[last] + ") as ?count)";
		return replaceColumns(query, sCol);
	}

	/***********************************************************************/
	/* Appenders functions                                                 */
	/***********************************************************************/

	private String replaceColumns(String query, String sCols)
	{
		int i1 = query.indexOf("SELECT");
		int i2 = query.indexOf("WHERE", i1);
		return query.substring(0, i1 + 7) + sCols + query.substring(i2-1);
	}

	private String appendGroupBy(String query)
	{
		return query + "\nGROUP BY " + getColumnsAsString(_cols, _cols.length-1);
	}

	private String appendLimit(String query)
	{
		return query + "\nLIMIT " + DEFAULT_LIMIT;
	}
}
