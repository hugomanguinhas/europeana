package eu.europeana.ld.sparql.cfg;

import java.io.PrintStream;

import eu.europeana.ld.sparql.TestQuery;
import eu.europeana.ld.sparql.TestQueryConfig.Type;

public class TestQueryLogger {

	private PrintStream _print;

	public TestQueryLogger(PrintStream print) { _print = print; }

	public void printHead(TestQuery query, Type type)
	{
		printSeparator();
		printLine("* ", query.getID(), ": ", type.name());
		printSeparator();
		_print.println();
	}

	public void printFooter()
	{
//		_print.println();
	}

	public void printQuery(String query)
	{
		_print.println(query);
		_print.println();
	}

	public void printError(Throwable t)
	{
		t.printStackTrace(_print);
		_print.println();
	}

	public void printResult(int count, long time)
	{
		_print.println("Executed: " + count + " records (in " + time + "ms)");
		_print.println();
	}

	public void printSeparator()
	{
		for ( int i = 0; i < 80; i++ ) { _print.print('*'); }
		_print.println();
	}

	public void printLine(String... strs)
	{
		for ( String s : strs ) { _print.print(s); }
		_print.println();
	}
}
