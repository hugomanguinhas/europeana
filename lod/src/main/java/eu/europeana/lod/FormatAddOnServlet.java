package eu.europeana.lod;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RiotException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class FormatAddOnServlet extends HttpServlet
{
	private static final long   serialVersionUID = 1L;
	private static final String DEFAULT_ENCODING = "UTF-8";

	private String           _reqHost = null;
	private String           _redHost = null;
	private String           _redExt  = null;
	private FormatSupport    _format  = null;

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
    	_reqHost = config.getInitParameter("request_url");
    	_redHost = config.getInitParameter("redirect_url");
    	_redExt  = config.getInitParameter("redirect_extension");

		FormatSupport format = FormatSupport.getFormat(config.getInitParameter("req_extension"));
		_format = (format != null ? format : FormatSupport.RDFXML);
	}

    protected void service(HttpServletRequest req, HttpServletResponse rsp)
    		throws ServletException, IOException
    {
    	try {
	    	StringBuffer reqURL = req.getRequestURL();
	
	    	FormatSupport reqFormat = processRequest(req, reqURL);
	    	if ( reqFormat == null ) { rsp.sendError(406); return; }

	    	Model m = sendRequest(req, reqURL.toString());

	    	writeResponse(m, rsp, reqFormat);
    	}
    	catch (ResponseThrowable t)
    	{
    		rsp.sendError(t.getCode());
    	}
    }

    private FormatSupport processRequest(HttpServletRequest req, StringBuffer url)
    {
    	if ( (_reqHost != null) && (_redHost != null) ) {
			int i = url.indexOf(_reqHost);
			if ( i >= 0 ) { url.replace(i, _reqHost.length()+i, _redHost); }
    	}

    	FormatSupport format = processFormat(url);
    	String        query  = req.getQueryString();
    	if ( query == null ) { return format; }

    	url.append("?"); url.append(query);
    	return format;
    }

    private FormatSupport processFormat(StringBuffer url)
    {
    	int i = url.lastIndexOf(".");
    	if ( i <= 0      ) { return null; }

    	String ext = url.substring(i);
    	if ( ext.length() <= 1 ) { return null; }

    	String rExt = _redExt != null ? _redExt : _format.getExtension();
		url.replace(i, ext.length() + i, rExt);
		return FormatSupport.getFormat(ext);
    }

    private Model sendRequest(HttpServletRequest req, String url)
    	throws ResponseThrowable
    {
		HttpClient client = new HttpClient();
		GetMethod  method = new GetMethod(url);
		method.setRequestHeader("Accept-Encoding", "gzip");
		method.setRequestHeader("Accept"         , _format.getMimetype());
		InputStream is = null;
		try {
			int rspCode = client.executeMethod(method);
			if ( rspCode != 200 ) { throw new ResponseThrowable(rspCode); }
			is = getInputStream(method);
		}
		catch (IOException e) { e.printStackTrace(); throw new ResponseThrowable(502); }

		try {
	    	Model m = ModelFactory.createDefaultModel();
			m.read(is, null, _format.getJenaFormat().getLang().getLabel());
	    	return m;
		}
		catch (RiotException e) { e.printStackTrace(); throw new ResponseThrowable(502); }
    }

    private InputStream getInputStream(HttpMethod method) throws IOException
    {
    	InputStream is = method.getResponseBodyAsStream();
		Header encoding = method.getResponseHeader("Content-Encoding");
		if ( encoding == null ) { return is; }
		return ( encoding.getValue().equals("gzip") ? new GZIPInputStream(is)
		                                            : is );
    }

    private void writeResponse(Model m, HttpServletResponse rsp, FormatSupport format) throws IOException
    {
    	rsp.setContentType(format.getMimetype());
    	rsp.setCharacterEncoding(DEFAULT_ENCODING);

    	OutputStream out = rsp.getOutputStream();
    	try {
    		RDFDataMgr.write(out, m, format.getJenaFormat());
	    	out.flush();
    	}
    	finally {
    		out.close();
    	}
    }

    private enum FormatSupport
    {
    	RDFXML   (".rdf"   , RDFFormat.RDFXML_PLAIN , "application/rdf+xml")
      , TTL      (".ttl"   , RDFFormat.TURTLE_PRETTY, "text/turtle")
      , N3       (".n3"    , RDFFormat.TURTLE_PRETTY, "text/n3")
      , NTRIPLE  (".nt"    , RDFFormat.NTRIPLES_UTF8, "application/n-triples")
      , NQUAD    (".nq"    , RDFFormat.NQUADS_UTF8  , "application/n-quads")
      , TRIG     (".trig"  , RDFFormat.TRIG_PRETTY  , "application/trig")
      , JSONLD   (".jsonld", RDFFormat.JSONLD_PRETTY, "application/ld+json")
      , RDFTHRIFT(".rt"    , RDFFormat.RDF_THRIFT   , "application/rdf+thrift")
      , RDFJSON  (".rj"    , RDFFormat.RDFJSON      , "application/rdf+json")
      //, TRIX     (".trix"  , "TriX"      , "application/trix+xml")
      ;

    	private String _ext;
    	private RDFFormat _jenaCode;
    	private String _mimetype;

    	private FormatSupport(String ext, RDFFormat jenaCode, String mimetype)
    	{
    		_ext      = ext;
    		_jenaCode = jenaCode;
    		_mimetype = mimetype;
    	}

    	public String    getExtension()  { return _ext;      }
    	public RDFFormat getJenaFormat() { return _jenaCode; }
    	public String    getMimetype()   { return _mimetype; }

    	public static FormatSupport getFormat(String extension)
    	{
    		for ( FormatSupport f : FormatSupport.values() )
    		{
    			if ( f.getExtension().equals(extension) ) { return f; }
    		}
    		return null;
    	}
    }

    private static class ResponseThrowable extends Throwable
    {
		private static final long serialVersionUID = 1L;

		private int _rspCode;

    	private ResponseThrowable(int rspCode) { _rspCode = rspCode; }

    	public int getCode() { return _rspCode; }
    }
}