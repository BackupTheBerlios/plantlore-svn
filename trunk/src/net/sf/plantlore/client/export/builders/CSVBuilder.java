package net.sf.plantlore.client.export.builders;

import java.io.IOException;
import java.io.Writer;

import net.sf.plantlore.client.export.MetaBuilder;
//import net.sf.plantlore.client.export.RecordWalkBuilder;
import net.sf.plantlore.client.export.Template;


/**
 * CSV Builder.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-23
 */
public class CSVBuilder extends MetaBuilder {
	
	private Writer output;
	private Template tmp;
		
	private static final String DELIMITER = ",";
	private static final String DOUBLEQUOTE = "\"";
	private static final String NEWLINE = System.getProperty("line.separator");
	
	private boolean firstColumn = true;
	
		
	/**
	 * Create a new CSV Builder. 
	 * The output is written as a comma separated values.
	 * 
	 * @param output	The writer where the output will be sent.
	 * @param tmp	The template that describes the selected columns and tables.
	 */
	public CSVBuilder(Writer output, Template tmp) {
		super( tmp );
		this.output = output; this.tmp = tmp;
	}
	
	
	@Override
	public void finishRecord() throws IOException {
		output.write(NEWLINE);		
		firstColumn = true;
	}
	
	/**
	 * Send the <code>[table, column, value]</code> in the CSV format
	 * to the output.
	 */ 
	protected void w(Class table, String column, Object value) throws IOException {
		// Shall we export this column?
		if( !tmp.isSet(table, column) ) return;
		
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
		
		if( firstColumn ) firstColumn = false; 
		else output.write( DELIMITER );
		output.write( r.toString() );
			
	}
	

}
