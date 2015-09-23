package eu.europeana.ld.sparql;

import java.io.IOException;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

public class QueryTestReader extends DefaultHandler2 implements ContentHandler {

	protected Map<String,TestQuery> _queries;

	protected TestQuery             _cursor;

	protected StringBuilder         _sb = new StringBuilder(1024);

	public QueryTestReader()
	{
	}

	public synchronized void parse(InputSource is, Map<String,TestQuery> queries) throws SAXException, IOException
	{
		_queries = queries;
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(this);
			reader.parse(is);
		}
		finally {
			_queries = null; _cursor = null; _sb.setLength(0);
		}
	}

	@Override
	public void startElement(
			String uri, String localName, String qName,
			Attributes atts) throws SAXException
	{
		if ( "query".equals(localName) ) {
			String id = atts.getValue("id");
			_cursor = new TestQuery(id, null);
		}
	}

	@Override
	public void endElement(
			String uri, String localName, String qName)
			throws SAXException
	{
		if ( "query".equals(localName) ) {
			_cursor.setQuery(_sb.toString().trim());
			_queries.put(_cursor.getID(), _cursor);
			_cursor = null; _sb.setLength(0);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if ( _cursor == null ) { return; }

		_sb.append(ch, start, length);
	}
}
