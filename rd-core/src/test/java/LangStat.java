

import java.io.PrintStream;

import static eu.europeana.data.analysis.AnalysisConstants.*;

public class LangStat implements Comparable<LangStat> {

	private String _lang;
	private int    _count;

	public LangStat(String lang)
	{
		_lang  = lang;
		_count = 0;
	}

	public String getLanguage() { return _lang; }

	public int  getCount() { return _count; }

	public void newLanguage() { _count++; }

	public void setCount(int count) { _count = count; }

	public void print(PrintStream ps, int total)
	{
		ps.print(_lang);
		ps.print(": ");
		ps.print(FORMAT_COUNT.format(_count));
		ps.print(" (");
		ps.print(FORMAT_PERCENT.format((double)_count / total));
		ps.println(")");
	}

	@Override
	public int compareTo(LangStat s)
	{
		int iDif = s._count - _count;

		return ( iDif != 0 ? iDif : _lang.compareTo(s._lang) );
	}
}
