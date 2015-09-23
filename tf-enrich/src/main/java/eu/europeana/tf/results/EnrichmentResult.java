package eu.europeana.tf.results;

import org.apache.commons.csv.CSVRecord;

import eu.europeana.utils.CSVWriter;

public class EnrichmentResult implements Comparable<EnrichmentResult>
{
    protected String _set;
    protected String _resource;
    protected String _property;
    protected Double _confidence;
    protected String _target;
    protected String _value;
    protected String _scheme;

    public EnrichmentResult(String set) { _set = set; }
    public EnrichmentResult() { this((String)null); }
    public EnrichmentResult(EnrichmentResult source)
    {
        _set        = source._set;
        _resource   = source._resource;
        _property   = source._property;
        _confidence = source._confidence;
        _target     = source._target;
        _value      = source._value;
        _scheme     = source._scheme;
    }

    public String getSet()        { return _set;        }
    public String getResource()   { return _resource;   }
    public String getProperty()   { return _property;   }
    public String getTarget()     { return _target;     }
    public Double getConfidence() { return _confidence; }
    public String getValue()      { return _value;      }
    public String getScheme()     { return _scheme;     }

    public void setTarget(String target)
    {
        _target = target;
        _scheme = eu.europeana.tf.results.ResultUtils.getScheme(_target);
    }

    public Object getColumn(int iCol)
    {
        switch(iCol)
        {
            case 0: return _set;
            case 1: return _resource;
            case 2: return _property;
            case 3: return _target;
            case 4: return _scheme;
            case 5: return _confidence;
            case 6: return _value;
        }
        return null;
    }

    public static String getColumnName(int iCol)
    {
        switch(iCol)
        {
            case 0: return "Participant";
            case 1: return "Resource";
            case 2: return "Property";
            case 3: return "Target URI";
            case 4: return "Target Scheme";
            case 5: return "Confidence";
            case 6: return "Source Text";
        }
        return null;
    }

    public static void printHeaderFull(CSVWriter printer)
    {
        String[] strs = new String[7];
        for ( int i = 0; i < 7; i++ ) { strs[i] = getColumnName(i); }
        printer.print(strs);
    }

    public EnrichmentResult loadRaw(CSVRecord record)
    {
        if ( record.size() < 5 ) { return null; };

        return loadRaw(record.get(0), record.get(1), record.get(2)
                     , record.get(3), record.get(4));
    }

    public EnrichmentResult loadRaw(String resource, String prop
                                  , String target, String conf
                                  , String value)
    {
        _resource   = resource.trim();
        _property   = prop.trim();
        conf = conf.trim();
        _confidence = conf.isEmpty() ? null : Double.parseDouble(conf);
        _value      = value.trim();
        setTarget(target.trim());
        return this;
    }

    public void printRaw(CSVWriter printer)
    {
        printer.println(_resource, _property, _target, _confidence, _value);
    }

    public static void printHeaderRaw(CSVWriter printer)
    {
    }

    public void printFull(CSVWriter printer)
    {
        printer.println(_set, _resource, _property, _target, _scheme, _confidence, _value);
    }

    @Override
    public int compareTo(EnrichmentResult res)
    {
        int ret = _resource.compareTo(res._resource);
        if ( ret != 0) { return ret; }

        ret = _property.compareTo(res._property);
        return ( ret != 0 ? ret : _target.compareTo(res._target) );
    }
}
