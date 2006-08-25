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
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.l10n.L10n;



public class XMLOccurrenceParser extends DefaultHandler implements OccurrenceParser {
	
	private Logger logger = Logger.getLogger(XMLOccurrenceParser.class.getPackage().getName());
	
	private RecordProcessor processor;
	private XMLReader xmlReader;
	private Reader reader;
	
	private AuthorOccurrence ao; 
	private Occurrence occ;
	private ArrayList<AuthorOccurrence> aos = new ArrayList<AuthorOccurrence>(30);
	private Stack<Record> stack = new Stack<Record>();
	private StringBuffer textCache = new StringBuffer( 4096 );
	
	private boolean rootNodeValidated = false;
	
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
	
	
	private void signalCorruptedFileIf(boolean condition) throws SAXException {
		if( condition ) {
			logger.error("Format of the file is corrupted! Parsing will be terminated.");
			throw new SAXException(L10n.getString("Error.CorruptedFileFormat"));
		}
	}
	
	

	@Override
	public void startElement(String uri, String name, String qname, Attributes attr) 
	throws SAXException {
		if( !rootNodeValidated ) {
			if( "occurrences".equalsIgnoreCase(name) )
				rootNodeValidated = true;
			else
				signalCorruptedFileIf( true );
		}
		else if( "occurrence".equalsIgnoreCase(name) ) {
			signalCorruptedFileIf( occ != null );
			stack.push( occ = (Occurrence) new Occurrence().createTorso() );
			aos.clear();
		} 
		else if( "authoroccurrence".equalsIgnoreCase(name) ) {
			signalCorruptedFileIf( occ == null || ao != null );
			ao = new AuthorOccurrence();
			ao.setAuthor( new Author() );
			ao.setOccurrence( occ );
			stack.push( ao );
		}
		else {
			Record current = null;
			try {
				current = stack.peek();
			} catch(Exception e) {
				signalCorruptedFileIf( true );
			}
			if( current.isForeignKey(name) ) 
				stack.push( current.findSubrecord(name) );
			else if( current.isColumn(name) ) 
				textCache.delete(0, textCache.capacity());
			else
				signalCorruptedFileIf( true );
		}
	}
	
	@Override
	public void endElement(String uri, String name, String qname) 
	throws SAXException {
		Record current = null;
		try {
			current = stack.peek();
		} catch(Exception e) {
			signalCorruptedFileIf( ! "occurrences".equalsIgnoreCase(name) );
			return;
		}
		
		if( current.getClass().getSimpleName().equalsIgnoreCase(name) ) {
			stack.pop();
			if( "authoroccurrence".equalsIgnoreCase(name) ) {
				aos.add( ao );
				ao = null;
			} 
			else if( "occurrence".equalsIgnoreCase(name) ) {
				try {
					occ = null;
					if(processor != null)
						processor.processRecord( aos.toArray(AUTHOR_OCCURRENCE_ARRAY) );
				} catch(Exception e) {
					// Wrap the exception, we cannot throw anything else.
					throw new SAXException( e );
				}
			}
		}
		else
			current.setValueSafe(name, textCache.toString());
	}
	
	@Override
	public void characters(char[] ch, int start, int length) {
		for(int i = start; i < start + length; i++)
			textCache.append( ch[i] );
	}
	

}
