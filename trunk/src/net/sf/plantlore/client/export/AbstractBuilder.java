package net.sf.plantlore.client.export;

import java.io.IOException;


import net.sf.plantlore.common.record.Record;

/**
 * A default implementation of the Builder interface.
 * It is strongly recommended to subclass the AbstractBuilder
 * and implement the <code>output()</code> method
 * instead of implementing the whole Builder interface
 * yourself.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-27
 * @version 1.0
 * @see net.sf.plantlore.client.Builder
 * @see AbstractBuilder#output(Class, String, Object)
 */
public abstract class AbstractBuilder implements Builder {
	
	/** For an invocation of parameterless methods. */
	protected static final Object[] NO_PARAMETERS = new Object[0];
	
	protected Template template;
	
	
	/**
	 * Create a new DefaultBuilder.
	 * 
	 * @param template The template describing which columns are selected.
	 */
	public AbstractBuilder(Template template) {
		setTemplate(template);
	}
	
	/**
	 * Set another template.
	 * The builder stores a clone of the template.
	 *  
	 * @param template The new template. <b>Mustn't be <i>null</i></b>.
	 */
	public void setTemplate(Template template) {
		this.template = template.clone();
	}
	
	/**
	 * @return A clone of the template this Builder currently uses.
	 */
	public Template getTemplate() {
		return template.clone();
	}
	
	/**
	 * Write the [table, column, value] to the output.
	 * 
	 * @param table	Name of the table.
	 * @param column Name of the column.
	 * @param value Value of the <code>table.column</code>.
	 * @throws IOException If the output could not be written.
	 */
	protected abstract void output(Class table, String column, Object value) throws IOException;

	/**
	 * Send all properties to output and traverse the subrecords, too.
	 * 
	 */
	public void part(Record record) throws IOException {
		if(record == null) return;
		// Build this part of the record.
		Class table = record.getClass();
		for( String property : record.getProperties() ) 
			output( table, property, record.getValue(property) );
		// Now look at all children of this record.
		for(String key : record.getForeignKeys()) {
			// And build'em too.
			part( (Record) record.getValue(key) );
		}
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

	/** Empty. */
	public void startRecord() throws IOException {
	}

	/** Empty. */
	public void finishRecord() throws IOException {
	}

	/** Empty. */
	public void header() throws IOException {
	}

	/** Empty. */
	public void footer() throws IOException {
	}

	
}
