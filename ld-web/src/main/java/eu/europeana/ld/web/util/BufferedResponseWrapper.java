/**
 * 
 */
package eu.europeana.ld.web.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 13 Aug 2015
 */
public class BufferedResponseWrapper extends HttpServletResponseWrapper
{
    private ServletOutputStreamWrapper _outWrapper;

    public BufferedResponseWrapper(HttpServletResponse rsp)
    {
        super(rsp);
        _outWrapper = new ServletOutputStreamWrapper();
    }

    public InputStream getInputStream()
    {
        try { _outWrapper.flush(); } catch (IOException e) {}
        return _outWrapper.getInputStream();
    }

    public ServletOutputStream getOutputStream() throws IOException
    {
        return _outWrapper;
    }

    public PrintWriter getWriter() throws IOException
    {
        return new PrintWriter(_outWrapper);
    }

    public static class ServletOutputStreamWrapper extends ServletOutputStream
    {
        private static int SIZE = 1024 * 10; //10k

        private ByteArrayOutputStream _out;

        public ServletOutputStreamWrapper()
        {
            super();
            _out = new ByteArrayOutputStream(SIZE);
        }

        public InputStream getInputStream()
        {
            return new ByteArrayInputStream(_out.toByteArray());
        }

        @Override
        public void write(int b) throws IOException { _out.write(b); }

        @Override
        public void write(byte[] b) throws IOException { _out.write(b); }

        @Override
        public void write(byte[] b, int s, int l) throws IOException
        {
            _out.write(b, s, l);
        }
    }
}
