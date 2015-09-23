package eu.europeana.data.analysis;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class LangStat extends AbsStat
{
    private static String NULL = "? ";

    private Map<String,ValueStat> _langs;
    private int                   _total = 0;

    public LangStat()
    {
        _langs = new HashMap<String,ValueStat>();
    }

    public int getSize()  { return _langs.size(); }

    public int getTotal() { return _total;        }

    public boolean hasLanguages()
    {
        return ( _langs.size() > 1 || !_langs.containsKey(NULL) );
    }

    public void setTotal(int total) { _total = total; }

    public Collection<ValueStat> getStats() { return _langs.values(); }

    public ValueStat getStat(String sLang)  { return _langs.get(sLang); }

    public ValueStat getNoLangStat()        { return _langs.get(NULL); }

    public void newLang(String lang)
    {
        _total++;
        if ( (lang == null) || lang.trim().isEmpty() ) { lang = NULL; }

        ValueStat s = _langs.get(lang);
        if ( s == null ) { s = new ValueStat(lang); _langs.put(lang, s); }

        s.newItem();
    }

    public void print(PrintStream ps)
    {
        for ( ValueStat s : new TreeSet<ValueStat>(_langs.values()))
        {
            ps.print('\t');
            s.print(ps, _total);
        }

        ps.println();
    }
}
