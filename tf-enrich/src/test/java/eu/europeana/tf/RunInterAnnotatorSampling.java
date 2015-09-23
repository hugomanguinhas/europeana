package eu.europeana.tf;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

public class RunInterAnnotatorSampling
{

	public static final void main(String[] args)
	{
		Map<Integer,Integer> samples = new TreeMap();
		samples.put(2 , 12);
		samples.put(3 , 10);
		samples.put(4 , 100);
		samples.put(5 , 100);
		samples.put(6 , 68);
		samples.put(7 , 22);
		samples.put(12, 100);
		samples.put(13, 100);
		samples.put(14, 100);
		samples.put(15, 100);
		samples.put(16, 100);
		samples.put(17, 100);
		samples.put(18, 100);
		samples.put(19, 100);
		samples.put(22, 16);
		samples.put(23, 15);
		samples.put(24, 100);
		samples.put(25, 100);
		samples.put(26, 100);
		samples.put(27, 100);
		samples.put(28, 100);
		samples.put(29, 100);
		samples.put(30, 100);

		Collection<Integer> sel = new TreeSet<Integer>();
		Random r = new Random();
		for ( Integer i : samples.keySet() )
		{
			while( sel.size() < 2 ) { sel.add(r.nextInt(samples.get(i))); }
			System.out.println("#" + i + ": ");
		}
	}
}
