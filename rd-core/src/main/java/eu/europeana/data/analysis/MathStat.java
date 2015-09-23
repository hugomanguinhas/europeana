package eu.europeana.data.analysis;

import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import static eu.europeana.data.analysis.AnalysisConstants.*;

public class MathStat
{
    private TreeMap<Integer,Integer> _dist = new TreeMap<Integer,Integer>();

    private int _count;


    public MathStat() {}


    public int getCount()
    {
        int count = 0;
        for ( Map.Entry<Integer, Integer> entry : _dist.entrySet() )
        {
            count += entry.getKey() * entry.getValue();
        }
        return count;
    }

    public int getTotal()
    {
        int total = 0;
        for ( Integer value : _dist.values() ) { total += value; }
        return total;
    }

    public int getMin() { return _dist.firstKey(); }

    public int getMax() { return _dist.lastKey();  }

    public int getMod()
    {
        int highest = -1;
        int key     = -1;
        for ( Map.Entry<Integer, Integer> entry : _dist.entrySet() )
        {
            int value = entry.getValue();
            if ( value > highest ) { highest = value; key = entry.getKey(); }
        }
        return key;
    }

    public void newItem()   { _count++; }

    public void endScope()
    {
        Integer size = _dist.get(_count);
        _dist.put(_count, size == null ? 1 : size + 1) ;
        _count = 0;
    }


    public void print(PrintStream ps)
    {
        int total = getTotal();
        int count = getCount();
        ps.print(toString(count, total));
        ps.print(" (");
        ps.print(FORMAT_PERCENT.format((double)count / total));
        ps.print(",");
        ps.print(getMin());
        ps.print("<#<");
        ps.print(getMax());
        ps.print(")");
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
