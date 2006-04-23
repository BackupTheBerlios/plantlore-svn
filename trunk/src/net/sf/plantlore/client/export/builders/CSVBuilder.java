package net.sf.plantlore.client.export.builders;

import java.io.IOException;
import java.io.Writer;

import net.sf.plantlore.client.export.MetaBuilder;
import net.sf.plantlore.client.export.RecordWalkBuilder;
import net.sf.plantlore.client.export.Template;


/**
 * CSV Builder.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 23.4.2006
 *
 */
public class CSVBuilder extends /*MetaBuilder*/RecordWalkBuilder {
	
	private Writer output;
	private Template tmp;
		
	private static final String DELIMITER = ",";
	private static final String DOUBLEQUOTE = "\"";
	private static final String NEWLINE = System.getProperty("line.separator");
	
	private boolean firstColumn = true;
	
		
	
	public CSVBuilder(Writer output, Template tmp) {
		super( tmp );
		this.output = output; this.tmp = tmp;
	}
	
	
	@Override
	public void finishRecord() throws IOException {
		output.write(NEWLINE);		
		firstColumn = true;
	}
	
	/** CSV */
	protected void wc(Class table, String column, Object value) throws IOException {
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
