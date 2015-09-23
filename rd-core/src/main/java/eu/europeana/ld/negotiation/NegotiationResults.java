/**
 * 
 */
package eu.europeana.ld.negotiation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

import eu.europeana.ld.negotiation.format.FormatConfig;
import static eu.europeana.ld.negotiation.NegotiationResults.*;
import static eu.europeana.ld.negotiation.NegotiationUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 12 Aug 2015
 */
public class NegotiationResults extends LinkedHashMap<String,ResourceResult>
{
    private static final long serialVersionUID = 1L;

    public static class ResourceResult
        extends LinkedHashMap<FormatConfig,FormatResult>
    {
        private String  _url;
        private boolean _result;

        public ResourceResult(String url) { _url = url; }

        public String getURL() { return _url; }
    }

    public static class FormatResult extends LinkedHashMap
    {
        private FormatConfig _format;
        private boolean      _status;

        public FormatResult(FormatConfig format) { _format = format; }

        public FormatConfig getFormat() { return _format; }
        public boolean      getStatus() { return _status; }

        public void         setStatus(boolean status) { _status = status; }
    }

    public static class MimeResult extends Stack<PathTransition>
    {
        private String       _mime = null;
        private int          _code = 0;
        private List<String> _msgs = new ArrayList(0);

        public MimeResult(String mime) { _mime = mime; }


        public String getMimetype() { return _mime; }

        public boolean hasTransition(String url)
        {
            for ( PathTransition t : this )
            {
                if ( t.getURL().equals(url) ) { return true; }
            }
            return false;
        }

        public boolean isNonInformationResource()
        {
            return (!isEmpty() && (firstElement()._code == 303));
        }

        public boolean isResolved() { return isRspOK(_code); }

        public void newMessage(String msg) { _msgs.add(msg); }
    }

    public static class PathTransition
    {
        private int    _code;
        private String _url;

        public PathTransition(int code, String url)
        {
            _code = code;
            _url  = url;
        }

        public int    getCode() { return _code; }
        public String getURL()  { return _url;  }
    }
}