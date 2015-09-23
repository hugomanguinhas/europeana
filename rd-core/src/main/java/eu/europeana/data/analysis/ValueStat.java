package eu.europeana.data.analysis;

import java.io.PrintStream;

import static eu.europeana.data.analysis.AnalysisConstants.*;

public class ValueStat implements Comparable<ValueStat>
{
    private String _str;
    private int    _count;

    public ValueStat(String str) { _str = str; }


    public String getValue() { return _str; }

    public int  getCount() { return _count; }

    public void setCount(int count) { _count = count; }

    public void newItem() { _count++; }


    public void print(PrintStream ps, int total)
    {
            ps.print(toString(_count, total));
            ps.print(" (");
            ps.print(FORMAT_PERCENT.format((double)_count / total));
            ps.print("): ");
            ps.println(_str);
    }

    @Override
    public int compareTo(ValueStat v)
    {
        int iDif = v._count - _count;
        return ( iDif != 0 ? iDif : _str.compareTo(v._str) );
    }

    private String toString(int value, int total)
    {
        int digits = String.valueOf(total).length();
        StringBuilder sb = new StringBuilder(digits);
        String str = String.valueOf(value);
        for (int i = digits - str.length(); i > 0; i--) { sb.append(' '); }
        sb.append(str);
        return sb.toString();
    }
}
