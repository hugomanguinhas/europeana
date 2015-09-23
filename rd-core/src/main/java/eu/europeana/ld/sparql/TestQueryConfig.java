package eu.europeana.ld.sparql;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static eu.europeana.ld.sparql.TestQueryConfig.Type.*;

public class TestQueryConfig {

	public static enum Type { PLAIN, ORDER, COUNT, DISTINCT };
	public static Set<Type> ALL = new HashSet(Arrays.asList(PLAIN, ORDER, COUNT, DISTINCT));

	public String      serviceURI;
	public PrintStream result;
	public PrintStream log;
	public Set<Type>   types;

	public TestQueryConfig(
			String serviceURI
	      , PrintStream log, PrintStream result)
	{
		this.serviceURI = serviceURI;
		this.result     = result;
		this.log        = log;
		this.types      = ALL;
	}

	public TestQueryConfig(
			String serviceURI
		  , PrintStream log, PrintStream result
	      , Type... types)
	{
		this.serviceURI = serviceURI;
		this.result     = result;
		this.log        = log;
		this.types      = new HashSet(Arrays.asList(types));
	}
}
