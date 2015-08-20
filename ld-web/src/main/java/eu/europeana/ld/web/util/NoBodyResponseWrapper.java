/**
 * 
 */
package eu.europeana.ld.web.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 14 Aug 2015
 */
public class NoBodyResponseWrapper extends HttpServletResponseWrapper
{
    private final NoBodyOutputStream _out    = new NoBodyOutputStream();
    private PrintWriter              _writer = null;

    public NoBodyResponseWrapper(HttpServletResponse rsp) { super(rsp); }

    @Override
    public ServletOutputStream getOutputStream() throws IOException
    {
        return _out;
    }

    @Override
    public PrintWriter getWriter() throws UnsupportedEncodingException
    {
        if (_writer == null) {
            String enc = getCharacterEncoding();
            _writer = new PrintWriter(new OutputStreamWriter(_out, enc));
        }

        return _writer;
    }

    @Override
    public void setContentLength(int l)
    {
        super.setContentLength(_out.getContentLength());
    }


    private static class NoBodyOutputStream extends ServletOutputStream
    {
        private int _len = 0;

        @Override
        public void write(int b) { _len++; }
    
        @Override
        public void write(byte b[], int o, int l) throws IOException
        {
            _len += l;
        }

        public int getContentLength() { return _len; }
    }
}
