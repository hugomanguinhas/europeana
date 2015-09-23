/**
 * 
 */
package eu.europeana.ld.negotiation;

import java.io.PrintStream;

import eu.europeana.ld.negotiation.format.FormatConfig;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 10 Aug 2015
 */
public class NegotiationConfig
{
    private PrintStream    _err;
    private PrintStream    _out;
    private String         _method;
    private String         _agent;
    private FormatConfig   _def;
    private FormatConfig[] _formats;

    public NegotiationConfig(PrintStream err, PrintStream out
                           , String method, String agent
                           , FormatConfig def, FormatConfig... formats)
    {
        _err     = err;
        _out     = out;
        _def     = def;
        _method  = method;
        _agent   = agent;
        _formats = formats;
    }

    public PrintStream    getOutput()        { return _out;     }
    public PrintStream    getError()         { return _err;     }
    public FormatConfig   getDefaultFormat() { return _def;     }
    public FormatConfig[] getFormats()       { return _formats; }
    public String         getMethod()        { return _method;  }
    public String         getAgent()         { return _agent;   }

    public void setFormats(FormatConfig... formats) { _formats = formats; }
    public void setFormatDefault(FormatConfig def)  { _def = def;         }
    
}
