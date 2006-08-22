package net.sf.plantlore.client.occurrenceimport.parsers;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import net.sf.plantlore.client.occurrenceimport.OccurrenceParser;
import net.sf.plantlore.client.occurrenceimport.RecordProcessor;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.l10n.L10n;



public class XMLOccurrenceParser extends DefaultHandler implements OccurrenceParser {
	
	private Logger logger = Logger.getLogger(XMLOccurrenceParser.class.getPackage().getName());
	
	private RecordProcessor processor;
	private XMLReader xmlReader;
	private Reader reader;
	
	private AuthorOccurrence ao; 
	private ArrayList<AuthorOccurrence> aos = new ArrayList<AuthorOccurrence>(30);
	private Stack<Record> stack = new Stack<Record>();
	private StringBuffer textCache = new StringBuffer( 4096 );
	
	private static AuthorOccurrence[] AUTHOR_OCCURRENCE_ARRAY = new AuthorOccurrence[0];
	
	
	public XMLOccurrenceParser(Reader reader) 
	throws SAXException {
		xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(this);
		xmlReader.setErrorHandler(this);
		
		this.reader = reader;
	}
	
	public void setRecordProcessor(RecordProcessor processor) {
		this.processor = processor;
	}

	
	public void startParsing() 
	throws Exception {
		try {
			
			logger.debug("Executing the parsing routine...");
			xmlReader.parse( new InputSource(reader) );
			
		} finally {
			reader.close();
		}
	}
	
	
	
	@Override
	public void startDocument() {
		ao = (AuthorOccurrence) new AuthorOccurrence().createTorso();
		stack.push(ao);
	}
	
	@Override
	public void startElement(String uri, String name, String qname, Attributes attr) 
	throws SAXException {
		if( stack.peek() == null )
			throw new SAXException(L10n.getString("Error.CorruptedFileFormat"));
		
		Record subRecord = "authoroccurrence".equalsIgnoreCase(name) ? 
				ao : stack.peek().findSubrecord( name );
		// Open the sub-record for processing
		if( subRecord != null )
			stack.push(subRecord);
		// or prepare the text cache.
		else
			textCache.delete(0, textCache.capacity());
		
		if( "occurrence".equalsIgnoreCase(name) )
			aos.clear();
	}
	
	@Override
	public void endElement(String uri, String name, String qname) 
	throws SAXException{
		Record current = stack.peek();
		if( current == null )
			throw new SAXException(L10n.getString("Error.CorruptedFileFormat"));
		
		if( current.getClass().getSimpleName().equalsIgnoreCase(name) )
			stack.pop();
		else
			current.setValueSafe(name, textCache.toString());
		
		if( name.equalsIgnoreCase("authoroccurrence") ) {
			aos.add( ao );
			AuthorOccurrence newAO = new AuthorOccurrence();
			newAO.setAuthor( new Author() );
			newAO.setOccurrence( ao.getOccurrence() );
			ao = newAO;
		}
		else if (name.equalsIgnoreCase("occurrence") ) {
			ao = (AuthorOccurrence) new AuthorOccurrence().createTorso();
			try {
				processor.processRecord( aos.toArray(AUTHOR_OCCURRENCE_ARRAY) );
			} catch(Exception e) {
				// Wrap the exception, we cannot throw anything else.
				throw new SAXException( e );
			}
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) {
		for(int i = start; i < start + length; i++)
			textCache.append( ch[i] );
	}
	

}
