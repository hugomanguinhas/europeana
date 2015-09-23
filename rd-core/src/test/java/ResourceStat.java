

import java.io.PrintStream;

import com.hp.hpl.jena.rdf.model.RDFNode;

import static eu.europeana.data.analysis.AnalysisConstants.*;

public class ResourceStat implements Comparable<ResourceStat>
{

	private RDFNode  _resource;
	private int      _count;

	public ResourceStat(RDFNode resource) { _resource = resource; }

	public void newResource() { _count++; }

	public void print(PrintStream ps, int total)
	{
		ps.print(FORMAT_COUNT.format(_count));
		ps.print(" (");
		ps.print(FORMAT_PERCENT.format((double)_count / total));
		ps.print("): ");
		ps.println(_resource.toString());
	}

	@Override
	public int compareTo(ResourceStat r)
	{
		int i = r._count - _count;
		return (i != 0 ? i :  _resource.toString().compareTo(r._resource.toString()));
	}
}
