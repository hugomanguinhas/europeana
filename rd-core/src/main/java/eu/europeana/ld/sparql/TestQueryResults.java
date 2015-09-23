package eu.europeana.ld.sparql;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import eu.europeana.ld.sparql.TestQueryConfig.Type;

public class TestQueryResults {

	private Set<String> _emptyResult    = new TreeSet<String>();
	private Set<String> _incResult      = new TreeSet<String>();
	private Map<String,Long> _timePlain = new HashMap<String, Long>();
	private Map<String,Long> _timeCount = new HashMap<String, Long>();
	private Map<String,Long> _timeDist  = new HashMap<String, Long>();
	private Map<String,Long> _timeOrder = new HashMap<String, Long>();
	private TestQueryConfig  _config;
	private Map<String,TestQuery> _queries;	

	public TestQueryResults(TestQueryConfig cfg, Map<String,TestQuery> queries)
	{
		_config  = cfg;
		_queries = queries;
	}

	public void newEmptyResult(String queryID)   { _emptyResult.add(queryID); }

	public void newInconsistence(String queryID) { _incResult.add(queryID);   }

	public void newResult(String queryID, Type type, int size, long time)
	{
		if ( type == Type.PLAIN ) {
			if ( size == 0) { _emptyResult.add(queryID); }
			
		}
		switch (type) {
			case PLAIN   : _timePlain.put(queryID, time); break;
			case COUNT   : _timeCount.put(queryID, time); break;
			case DISTINCT: _timeDist.put(queryID, time); break;
			case ORDER   : _timeOrder.put(queryID, time); break;
		}
	}


	/***********************************************************************/
	/* Printing functions                                                  */
	/***********************************************************************/

	public void printAll(PrintStream printer)
	{
		printEmptyResults(printer);
		printPerformance(printer);
	}

	public void printEmptyResults(PrintStream printer)
	{
		printer.println("Queries with empty resutls: " + _emptyResult);
	}

	public void printPerformance(PrintStream printer)
	{
		printer.println("Query;With limit;With Ordering;With count");
		for ( String queryID : _queries.keySet() )
		{
			printer.print(queryID);
			printer.print(";");
			printer.print(asString(_timePlain.get(queryID)));
			printer.print(";");
			printer.print(asString(_timeCount.get(queryID)));
			printer.print(";");
			printer.print(asString(_timeDist.get(queryID)));
			printer.print(";");
			printer.print(asString(_timeOrder.get(queryID)));
			printer.println();
		}
	}

	private String asString(Object value)
	{
		return (value == null) ? "?" : value.toString();
	}
}
