/**
 * 
 */
package eu.europeana.ld.web.util;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import static java.util.Collections.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 13 Aug 2015
 */
public class RequestChangeWrapper extends HttpServletRequestWrapper
{
    private Map<String,String> _params;
    private Map<String,String> _header;

    public RequestChangeWrapper(HttpServletRequest req)
    {
        super(req);
    }

    public void addParameter(String name, String value)
    {
        _params = addValue(_params, name, value);
    }

    public void addHeader(String name, String value)
    {
        _header = addValue(_header, name, value);
    }

    @Override
    public String getParameter(String name)
    {
        return getValue(_params, name, super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name)
    {
        return getValues(_params, name, super.getParameterValues(name));
    }

    @Override
    public String getHeader(String name)
    {
        return getValue(_header, name, super.getHeader(name));
    }

    @Override
    public Enumeration getHeaders(String name)
    {
        return getValuesAsEnum(_header, name, super.getHeaders(name));
    }

    private Map<String,String> addValue(Map<String,String> map
                                      , String name, String value)
    {
        if ( map == null ) { map = new LinkedHashMap(); }
        map.put(name, value);
        return map;
    }

    private String getValue(Map<String,String> map, String key, String def)
    {
        if ( map == null || !map.containsKey(key) ) { return def; }
        return map.get(key);
    }

    private String[] getValues(Map<String,String> map, String key, String[] def)
    {
        if ( map == null || !map.containsKey(key) ) { return def; }
        String value = map.get(key);
        return (value == null ? new String[] {} : new String[] { value });
    }

    private Enumeration getValuesAsEnum(Map<String,String> map
                                      , String key, Enumeration def)
    {
        if ( map == null || !map.containsKey(key) ) { return def; }

        String value = map.get(key);
        return enumeration(value == null ? EMPTY_LIST : singletonList(value));
    }
}
