package eu.europeana.ld.sparql;

import java.io.PrintStream;
import java.util.Map;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import eu.europeana.ld.sparql.cfg.TestQueryLogger;
import static eu.europeana.ld.sparql.TestQueryConfig.*;

public class SPARQLEndpointTester {

	private   TestQueryLogger       _logger;
	private   TestQueryResults      _results;

	protected Map<String,TestQuery> _queries;
	protected TestQueryConfig       _cfg;

	public SPARQLEndpointTester(TestQueryConfig cfg)
	{
		_cfg = cfg;
		_logger = new TestQueryLogger(_cfg.log);
	}

	public TestQueryResults run(Map<String,TestQuery> queries)
	{
		_queries = queries;
		_results = new TestQueryResults(_cfg, queries);
		for ( TestQuery query : _queries.values() ) { runTestQuery(query); }
		_results.printPerformance(_cfg.result);
		return _results;
	}

	protected void runTestQuery(TestQuery test)
	{
		for ( Type type : _cfg.types ) { runQuery(test, type); }
	}

	protected long runQuery(TestQuery test, Type type)
	{
		_logger.printHead(test, type);

		String query = test.getQuery(type);
		_logger.printQuery(query);

		int count  = -1;
		long start = System.currentTimeMillis();

		QueryEngineHTTP endpoint = new QueryEngineHTTP(_cfg.serviceURI, query);
		try {

			ResultSet rs = endpoint.execSelect();
			count = 0;
	        while (rs.hasNext()) {
	            QuerySolution qs = rs.next();
	            count++;
	        }
		}
		catch (Throwable t) {
			_logger.printError(t);
		}
		finally {
			endpoint.close();
		}
		long time = System.currentTimeMillis() - start;

		_results.newResult(test.getID(), type, count, time);

		_logger.printResult(count, time);
		_logger.printFooter();

		return time;
	}

	protected void printResults()
	{
		
	}
}
