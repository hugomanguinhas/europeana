package eu.europeana.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class GlobalUtils
{
    private static char[] PATTERN_KEYCHARS
        = { '.', '(', ')', '[', ']', '\\', '$', '^' };

    public static List<String> loadLines(InputStream in) throws IOException
    {
        List<String> list = new ArrayList();
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            while ( r.ready() )
            {
                String line = r.readLine().trim();
                if ( !line.isEmpty() ) { list.add(line); }
            }
        }
        finally { IOUtils.closeQuietly(in); }
        return list;
    }

    public static String escapeURI2Pattern(String uri)
    {
        StringBuilder sb = new StringBuilder();
        int l = uri.length();
        for ( int i = 0; i < l; i++ )
        {
            char c = uri.charAt(i);
            if ( isPatternKeyword(c) ) { sb.append('\\'); }
            sb.append(c);
        }
        return sb.toString();
    }

    private static boolean isPatternKeyword(char c)
    {
        for ( char pc : PATTERN_KEYCHARS)
        {
            if ( c == pc ) { return true; }
        }
        return false;
    }

}
