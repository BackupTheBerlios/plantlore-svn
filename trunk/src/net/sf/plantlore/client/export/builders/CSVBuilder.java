package net.sf.plantlore.client.export.builders;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;

import net.sf.plantlore.client.export.Builder;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.client.export.Template;


/**
 * CSV Builder.
 * An ingenious use of introspection :]
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-23
 */
public class CSVBuilder implements Builder{
	
	private Writer output;
	
	private boolean first;
		
	private static final String DELIMITER = ",";
	private static final String DOUBLEQUOTE = "\"";
	private static final String NEWLINE = System.getProperty("line.separator");
	
	private static final Object[] NO_PARAMETERS = new Object[0];
	
	
	private ArrayList<Record> cache = new ArrayList<Record>(20);
	private ArrayList<Author> authors = new ArrayList<Author>(10);
	
	private Hashtable<Class, ArrayList<Method>> properties = 
		new Hashtable<Class, ArrayList<Method>>(20);
		
	/**
	 * Create a new CSV Builder. 
	 * The output is written as a comma separated values.
	 * 
	 * @param output	The writer where the output will be sent.
	 * @param tmp	The template that describes the selected columns and tables.
	 */
	public CSVBuilder(Writer output, Template tmp) {
		this.output = output;
		for( Class table : Template.BASIC_TABLES)
			try {
				// Get the list of all properties.
				ArrayList<String> columns = ((Record) table.newInstance()).getProperties();
				// Create a list of getters of those properties.
				ArrayList<Method> methods = new ArrayList<Method>( columns.size() );
				properties.put(table, methods);
				// Check if these properties (columns) are set to be exported. 
				for(String column : columns)  
					if( !tmp.isSet(table, column) ) // yes -> store the getter
						try {
							methods.add( table.getMethod( methodName(column), new Class[0] ) );
						} catch(NoSuchMethodException e) {}
			} 
			catch(IllegalAccessException e) {}
			catch(InstantiationException e) {}
	}
	
	
	public void header() throws IOException {
		
	}
	
	
	public void footer() throws IOException {
		output.write(NEWLINE);
	}
	
	
	public void startRecord() throws IOException {
		 cache.clear(); authors.clear(); first = true;
	}
	
	
	public void finishRecord() throws IOException {
		if(authors.size() == 0)
			for(Record record : cache) write( record );
		else
			for(Author author : authors) {
				for(Record record : cache) write( record );
				write( author );
			}
		
		output.write(NEWLINE);		
	}
	
	
	public void part(Record record) throws IOException {
		if(record instanceof Author) authors.add( (Author) record);
		else cache.add(record);
	}
	
	
	/**
	 * Send the <code>value</code> in the CSV format
	 * to the output.
	 */ 
	protected void w(Object value) throws IOException {
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
		
		output.write( r.toString() );
	}
	
	/**
	 * Construct the getter method name from the name of the <code>column</code>
	 * 
	 * @param column	The name of the column whose value we need to obtain. 
	 * @return	The getter's name.
	 */
	private String methodName(String column) {
		StringBuilder s = new StringBuilder("get" + column); // prepend `get` in front of  the name
		s.setCharAt(3, Character.toUpperCase(s.charAt(3))); // convert the first letter of the name to uppercase
		return s.toString();
	}

	
	
	protected void write(Record record) throws IOException {
		Class table =  record.getClass();
		ArrayList<Method> methods = properties.get( table );
		for(Method getter : methods) 
			try {
				// Invoke the getter.
				Object value = getter.invoke( record, NO_PARAMETERS );
				if( !first ) output.write(DELIMITER); else first = false;
				w( value );
			} 
			catch(IllegalAccessException e) {}
			catch(InvocationTargetException e) {}
	}
	

}
