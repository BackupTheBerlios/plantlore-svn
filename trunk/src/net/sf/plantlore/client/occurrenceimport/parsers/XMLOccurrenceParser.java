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


/**
 * The XML Occurrence Parser is capable of reading the selected file in the PlantloreNative
 * file format, that is based on the XML, and reconstructing records stored in that format.
 * The reconstructed record is handed over to the Record Processor.
 * <br/>
 * The XML Occurrence Parser uses SAX to parse the input. It is fast and requires next to
 * no memory (which is why DOM is not suitable in this case). The Parser can handle
 * files of virtually any size.
 * <br/>
 * The input must be a valid PlantloreNative format - if it is not, the input is considered corrupted
 * and the parsing is terminated.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-08-14
 *	@version 1.0
 */
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
	
	/**
	 * Create a new XML Occurrence Parser.
	 * 
	 * @param reader	The reader that allows us to read the input. 
	 * It will be closed when the end of the input is reached. 
	 */
	public XMLOccurrenceParser(Reader reader) 
	throws SAXException {
		xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(this);
		xmlReader.setErrorHandler(this);
		
		this.reader = reader;
	}
	
	/**
	 * Set another record processor.
	 * 
	 * @param processor	The record processor which will be used to process the reconstructed record.
	 */
	public void setRecordProcessor(RecordProcessor processor) {
		this.processor = processor;
	}

	/**
	 * Start parsing the input.
	 */
	public void startParsing() 
	throws Exception {
		try {
			
			logger.debug("Executing the parsing routine...");
			xmlReader.parse( new InputSource(reader) );
			
		} finally {
			reader.close();
		}
	}
	
	/**
	 * If the condition is satisfied
	 * throw an exception that will signal that the file format is corrupted.
	 * 
	 * 
	 * @param condition	If the condition is false, throw the exception.
	 * @throws SAXException	The exception signalling a corrupted file format.
	 */
	private void signalCorruptedFileIf(boolean condition) throws SAXException {
		if( condition ) {
			logger.error("Format of the file is corrupted! Parsing will be terminated.");
			throw new SAXException(L10n.getString("Error.CorruptedFileFormat"));
		}
	}
	
	
	/**
	 * Process the opening tag. Make some format validity checks 
	 * and throw an exception if the validity constraints are violated.
	 */
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
				signalCorruptedFileIf( true ); // an unknown tag
		}
	}
	
	/**
	 * Process the closing tag. Make some format validity checks 
	 * and throw an exception, if the validity constraints are violated.
	 * <br/>
	 * When the whole Occurrence record is reconstructed, pass it to the
	 * Record Processor.
	 */
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
	
	/**
	 * Gather the data stored in between the opening and closing tag.
	 * Limit the maximum length of the text to 4096 characters.
	 */
	@Override
	public void characters(char[] ch, int start, int length) {
		for(int i = start; i < start + length; i++) {
			if( textCache.length() >= textCache.capacity() )
				break;
			textCache.append( ch[i] );
		}
	}
	

}
