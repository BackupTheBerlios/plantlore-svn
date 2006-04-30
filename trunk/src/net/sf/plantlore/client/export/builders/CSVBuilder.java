package net.sf.plantlore.client.export.builders;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import net.sf.plantlore.client.export.AbstractBuilder;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.client.export.Template;


/**
 * CSV Builder.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-23
 * @version 1.0 header is missing
 */
public class CSVBuilder extends AbstractBuilder{
	
	private Writer stream;
	
	/** Is this the first column on this line? */
	private boolean firstColumnOnThisLine;
		
	private static final String DELIMITER = ",";
	private static final String DOUBLEQUOTE = "\"";
	private static final String NEWLINE = System.getProperty("line.separator");
	
	private ArrayList<Record> cache = new ArrayList<Record>(20);
	private ArrayList<Author> authors = new ArrayList<Author>(10);
	private ArrayList<AuthorOccurrence> authocc = new ArrayList<AuthorOccurrence>(10);
	
	
	/**
	 * Create a new CSV Builder. 
	 * The output is written in the format of comma separated values:<br/>
	 * <pre>
	 * Erik Kratochvíl, discontinuum@gmail.com, Gagea pratensis (Pers.) Dumort., ostružiník měkký
	 * <pre>
	 * 
	 * @param output	The writer where the output will be sent.
	 * @param tmp	The template that describes the selected columns and tables.
	 */
	public CSVBuilder(Writer output, Template tmp) {
		super(tmp);
		assert(tmp != null);
		
		this.stream = output;
	}
	
	/**
	 * Make a note that the header is yet to be created.
	 */
	public void header() throws IOException {
	}
	
	/**
	 * Insert a line breaker.
	 */
	public void footer() throws IOException {
		stream.write(NEWLINE);
	}
	
	/**
	 * Clear the cache before receiving a new record set.
	 */
	public void startRecord() throws IOException {
		 cache.clear(); authors.clear(); authocc.clear(); firstColumnOnThisLine = true;
	}
	
	/**
	 * Flush the cache.
	 */
	public void finishRecord() throws IOException {
		if(authors.size() + authocc.size() == 0) {
			for(Record record : cache) 
				writeCached( record );
			stream.write(NEWLINE);
		}
		else
			for(int i = 0; i < Math.max(authors.size(), authocc.size()); i++) {
				for(Record record : cache) 
					writeCached( record );
				if(!authors.isEmpty()) writeCached( authors.get(i) );
				if(!authocc.isEmpty()) writeCached( authocc.get(i) );
				stream.write(NEWLINE);
				firstColumnOnThisLine = true;
			}
	}
	
	/**
	 * Cache the results.
	 */
	@Override
	public void part(Record record) throws IOException {
		if(record instanceof Author) authors.add( (Author) record );
		else if(record instanceof AuthorOccurrence) authocc.add( (AuthorOccurrence) record );
		else cache.add(record);
	}
	
		
	/**
	 * Convert the value to the CSV format.
	 *  
	 *  @return The converted value.
	 */ 
	protected String convertToValidCSV(Object value) throws IOException {
		if( value == null ) return ""; // no value means no output 
		
		StringBuilder r = new StringBuilder( value.toString() );
		boolean containsDoubleQuote = r.indexOf(DOUBLEQUOTE) >= 0, 
			containsDelimiter = r.indexOf(DELIMITER) >= 0,
			containsNewline = r.indexOf(NEWLINE) >= 0;
		// Convert doublequotes to double-doublequotes: " -> ""
		if( containsDoubleQuote ) { 
			int k = 0;
			while( (k = r.indexOf(DOUBLEQUOTE, k)) >= 0 ) { 
				r.insert(k, DOUBLEQUOTE); k += 2;
			}
		}
		// Wrap problematic records.
		if( containsDoubleQuote || containsDelimiter || containsNewline ) {
			r.insert(0, DOUBLEQUOTE); r.append(DOUBLEQUOTE);
		}
		return r.toString() ;
	}
	
	
	/**
	 * Write the cached records - use the <code>part()</code> method now.
	 * 
	 * @param record
	 * @see net.sf.plantlore.client.export.AbstractBuilder#part(Record)
	 */
	protected void writeCached(Record record) throws IOException {
		super.part(record);
	}
	
	/**
	 * Send the <code>value</code> to the output.
	 */
	protected void output(Class table, String column, Object value) throws IOException {
		if( firstColumnOnThisLine ) firstColumnOnThisLine = false; 
		else stream.write(DELIMITER);
		stream.write( convertToValidCSV( value ) );
	}
	

}
