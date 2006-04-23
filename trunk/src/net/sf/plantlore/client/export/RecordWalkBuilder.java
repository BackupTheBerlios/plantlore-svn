package net.sf.plantlore.client.export;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

import net.sf.plantlore.common.record.Record;

/**
 * RecordWalkBuilder. Immune to the database model changes.
 * The RecordWalkBuilder has the same purpose as the MetaBuilder,
 * and in fact it does the same thing but in a different way.
 * It is slower than MetaBuilder - it is the price for its more general design.
 * <br/>
 * RecordBuilder is a partial implementation of the Builder interface
 * and implements the most annoying and always-repeating parts:
 * the traversal through all tables and their columns.
 * For each column, that has to be exported, 
 * the <code>w()</code> method is called.
 * <br/>
 * <code>w() </code> is the only method that has to be implemented;
 * it says how the [table, column, value] should be written to the output.
 * 
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-23
 * @see net.sf.plantlore.client.export.MetaBuilder#w(Class, String, Object)
 * @see net.sf.plantlore.client.export.MetaBuilder
 */
public abstract class RecordWalkBuilder implements Builder {
	
	/** 
	 * The particular template that is used while building the output.
	 * @see net.sf.plantlore.client.export.Template
	 */ 
	private Template tmp;
	
	/**
	 * "Create" a new RecordWalkBuilder. Since RecordWalkBuilder is an abstract class,
	 * you cannot instantiate it.
	 * 
	 * @param tmp The template that is used to decide, whether a particular
	 * 		table interests us or not.
	 * @see net.sf.plantlore.client.export.Template
	 */
	public RecordWalkBuilder(Template tmp) {
		this.tmp = tmp;
	}
	
	/**
	 * An implementation of the Builder::writeRecord().
	 * It completely traverses the record starting with <code>args[0]</code>
	 * as a root table. It traverses all tables that are seleted in the Template.
	 * <br/>
	 * This task is carried out more elegantly (or more slowly if you want)
	 * than in the MetaBuilder. This method uses the introspection to 
	 * invoke the getters on the columns of tables.
	 * 
	 * @see net.sf.plantlore.client.export.MetaBuilder
	 */	
	public void writeRecord(Record[] args) throws IOException {
		// Well, deal with every part (part corresponds with a table) of the record separately 
		for(Record record : args) dealWith( record );
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
	
	/**
	 * Deal with <b>this</b> partial record. 
	 * The record corresponds to a particular table
	 * (i.e. Author, Plant, etc.).
	 * The traversal of all parts of the record is not done here.
	 * 
	 * @param record	Part of the output that corresponds to one of the basic tables.
	 * @throws IOException
	 */
	protected void dealWith(Record record) throws IOException {
		// Find out to which table this record corresponds.
		Class table = Template.whichTable( record );
		// Is this table important to us?
		if( table == null ||  !tmp.isSetTableD( table ) ) return;
		
		// Obtain the column names and names of columns holding foreign keys.
		String[] foreignKeys = record.getForeignKeys(); // I love inheritance ;)
		String[] allColumns = record.getColumns();
		
		// The foreignKeys table will be questioned repeatedly -> create a hash set.
		HashSet<String> fk = new HashSet<String>(20);
		if( foreignKeys != null ) for(String key : foreignKeys) fk.add( key );
		
		// Export every column of this table, that is marked to be exported.
		for(String column : allColumns) {
			// Foreign keys and unwanted columns will be skipped.
			if( fk.contains(column) || !tmp.isSet(table, column) ) continue;
			try {
				// Invoke the getter on the `record` to obtain the particular value
				// of that column.
				Object value = table.getMethod( methodName(column), new Class[0] ).invoke( record, new Object[0] );
				// Send the value to the output.
				w(table, column, value);
			} 
			catch(NoSuchMethodException e) {
				System.err.println(e); e.printStackTrace();
			}
			catch(IllegalAccessException e) {
				System.err.println(e); e.printStackTrace();
			}
			catch(InvocationTargetException e) {
				System.err.println(e); e.printStackTrace();
			}
		}
	}
	
	/**
	 * Write down the [table, column, value] to the output in a particular format.
	 * It is up to the subclass to specify this method. 
	 * 
	 * @param table	The currently processed table.
	 * @param column	The currently considered column of the table.
	 * @param value	The particular value contained in that column.
	 * @throws IOException	when an IO error occurs.
	 */
	protected abstract void w(Class table, String column, Object value) throws IOException;
		
		
	/** Empty implementation (does nothing). */
	public void makeHeader() throws IOException {}
	
	/** Empty implementation (does nothing). */
	public void makeFooter() throws IOException {}
	
	/** Empty implementation (does nothing). */
	public void startNewRecord() throws IOException {}
	
	/** Empty implementation (does nothing). */
	public void finishRecord() throws IOException {}
}
