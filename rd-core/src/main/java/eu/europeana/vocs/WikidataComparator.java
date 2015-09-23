package eu.europeana.vocs;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikidataComparator implements Comparator<String> {

	private static Pattern pattern
	  = Pattern.compile("http[:][/][/]www[.]wikidata[.]org[/]entity[/][PQ](\\d+).*");

	@Override
	public int compare(String s1, String s2)
	{
		int l1 = getNumber(s1);
		int l2 = getNumber(s2);
		if ( l1 == -1 || l2 == -1 ) { return s1.compareTo(s2); }

		int ret = l1 - l2;
		return ( ret == 0 ? s1.compareTo(s2) : ret );
	}

	private int getNumber(String s1)
	{
		Matcher m = pattern.matcher(s1);
		if ( m.find() == false ) { return -1; };

		return Integer.parseInt(m.group(1));
	}
}
