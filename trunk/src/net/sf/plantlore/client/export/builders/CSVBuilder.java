package net.sf.plantlore.client.export.builders;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import net.sf.plantlore.client.export.AbstractBuilder;
import net.sf.plantlore.client.export.Builder;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.client.export.Projection;


/**
 * CSV Builder.
 * 
 * The builder produces a comma separated values file format.
 * The first line is the list of names of exported columns;
 * the rest of the file contains values of those columns.
 * <br/>
 * A sample output:
 * <br/>
 * <pre>
 * Author.WholeName, Author.Email, Plant.Taxon, Plant.CzechName 
 * Erik Kratochvíl, discontinuum@gmail.com, Gagea pratensis (Pers.) Dumort., ostružiník měkký
 * <pre>
 *  
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-23
 * @version 1.2
 */
public class CSVBuilder implements Builder {
	
	private Writer stream;
	private Projection template;
	
	/** Is this the first column on this line? */
	private boolean firstColumnOnThisLine;
		
	private static final String DELIMITER = ",";
	private static final String DOUBLEQUOTE = "\"";
	private static final String NEWLINE = System.getProperty("line.separator");
	
	/**
	 * The basic record.
	 */
	private Record cache;
	/**
	 * The list of associated records.
	 * Due to the one-to-many relationship.
	 */
	private ArrayList<AuthorOccurrence> authocc = new ArrayList<AuthorOccurrence>(10);
	
	
	/**
	 * Create a new CSV Builder. 

	 * 
	 * @param output	The writer where the output will be sent.
	 * @param tmp	The template that describes the selected columns and tables.
	 */
	public CSVBuilder(Writer output, Projection tmp) {
		this.stream = output;
		this.template = tmp;
	}
	
	/**
	 * Create the header - comma separated list of names of columns.
	 */
	public void header() throws IOException {
		firstColumnOnThisLine = true;
		
		AuthorOccurrence sample = (AuthorOccurrence) new AuthorOccurrence().createTorso();
		constructHeader( sample.getOccurrence() );
		sample.setOccurrence(null);
		constructHeader(sample);
		stream.write(NEWLINE);
	}
	
	/**
	 * Traverse the record and send names of relevant columns to the output. 
	 * 
	 * @param record	The record to be traversed.
	 * @throws IOException
	 */
	private void constructHeader(Record record) throws IOException {
		if(record == null) return;
		Class table = record.getClass();
		for( String property : record.getProperties() ) 
			if( template.isSet(table, property) ) {
				output( table.getSimpleName()+"."+property );
			}
		for(String key : record.getForeignKeys()) {
			constructHeader( (Record)record.getValue(key) );
		}
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
		 cache = null; authocc.clear(); firstColumnOnThisLine = true;
		 stream.write(NEWLINE);
	}
	
	/**
	 * Flush the cache.
	 */
	public void finishRecord() throws IOException {
		if(authocc.size() == 0) {
			writeCached( cache );
			stream.write(NEWLINE);
		}
		else
			for(int i = 0; i < authocc.size(); i++) {
				writeCached( cache );
				writeCached( authocc.get(i) );
				stream.write(NEWLINE);
				firstColumnOnThisLine = true;
			}
		//stream.flush();
	}
	
	/**
	 * Cache the results.
	 */
	public void part(Record record) throws IOException {
		if(record instanceof AuthorOccurrence) authocc.add( (AuthorOccurrence) record );
		else cache = record;
	}
	
	/**
	 * Call <code>part(Record)</code> repeatedly.
	 * 
	 * @see AbstractBuilder#part(Record)
	 */
	public void part(Record... records) throws IOException {
		for(Record r : records) 
			part( r );		
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
	 * Write the cached records.
	 *  
	 * @param record	A record from the cache.
	 * @see net.sf.plantlore.client.export.AbstractBuilder#part(Record)
	 */
	protected void writeCached(Record record) throws IOException {
		if(record == null) return;
		// Build this part of the record.
		Class table = record.getClass();
		for( String property : record.getProperties() ) 
			if( template.isSet(table, property) ) {
				output(record.getValue(property) );
			}
		// Now look at all children of this record.
		for(String key : record.getForeignKeys()) {
			// And build'em too.
			writeCached( (Record) record.getValue(key) );
		}
	}
	
	/**
	 * Send the <code>value</code> to the output.
	 */
	protected void output(Object value) throws IOException {
		if( firstColumnOnThisLine ) firstColumnOnThisLine = false; 
		else stream.write(DELIMITER);
		stream.write( convertToValidCSV( value ) );
	}
	

}
