package net.sf.plantlore.client.occurrenceimport.parsers;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Stack;

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



public class XMLOccurrenceParser extends DefaultHandler implements OccurrenceParser {
	
	private RecordProcessor processor;
	private XMLReader xmlReader;
	private Reader reader;
	
	private AuthorOccurrence ao; 
	private ArrayList<AuthorOccurrence> aos = new ArrayList<AuthorOccurrence>(30);
	private Stack<Record> stack = new Stack<Record>();
	private StringBuffer textCache = new StringBuffer( 8192 );
	
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
	throws IOException, SAXException {
		try {
			
			xmlReader.parse( new InputSource(reader) );
			
		} finally {
			reader.close();
		}
	}
	
	
	
	@Override
	public void startDocument() {
		ao = (AuthorOccurrence) new AuthorOccurrence().createTorso();
	}
	
	@Override
	public void startElement(String uri, String name, String qname, Attributes attr) {
		Record 
			subRecord = "authoroccurrence".equalsIgnoreCase(name) ?
					ao : stack.peek().findSubrecord( name );
		if( subRecord != null )
			stack.push(subRecord);
		else
			textCache.delete(0, textCache.capacity());
		if(name.equalsIgnoreCase("occurrence"))
			aos.clear();
	}
	
	@Override
	public void endElement(String uri, String name, String qname) throws SAXException{
		Record element = stack.peek();
		if( element.getClass().getSimpleName().equalsIgnoreCase(name) )
			stack.pop();
		else
			element.setValue(name, textCache.toString());
		
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
				throw new SAXException( e );
			}
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		for(int i = start; i < start + length; i++)
			textCache.append( ch[i] );
	}
	

}
