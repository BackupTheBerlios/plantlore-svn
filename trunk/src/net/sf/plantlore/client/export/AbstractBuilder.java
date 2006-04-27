package net.sf.plantlore.client.export;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;

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
	
	/** A list of getters of selected columns for every table. */
	protected Hashtable<Class, ArrayList<Method>> properties = 
		new Hashtable<Class, ArrayList<Method>>(20);
	
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
		assert(template != null);
		this.template = template.clone();
		
		// Store all getters of all selected columns.
		for( Class table : Template.BASIC_TABLES)
			try {
				// Get the list of all properties.
				ArrayList<String> columns = ((Record) table.newInstance()).getProperties();
				// Create a list of getters of those properties.
				ArrayList<Method> methods = new ArrayList<Method>( columns.size() );
				properties.put(table, methods);
				// Check if these properties (columns) are set to be exported. 
				for(String column : columns)  
					if( template.isSet(table, column) )
						methods.add( Template.getMethod( table, column ) );
			} 
			catch(IllegalAccessException e) { /*e.printStackTrace();*/ }
			catch(InstantiationException e) { /*e.printStackTrace();*/ }
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
	 * Send the value of all properties (i.e. all columns that are not foreign keys)
	 * of this record to the output. Uses the <code>output()</code> method.
	 * 
	 * @see AbstractBuilder#output(Class, String, Object)
	 */
	public void part(Record record) throws IOException {
		if(record == null) return;
		Class table = record.getClass();
		ArrayList<Method> methods = properties.get( table );
		for(Method getter : methods) 
			try {
				// Invoke the getter.
				Object value = getter.invoke( record, NO_PARAMETERS );
				output( table, getter.getName().substring(3), value );
			} 
			catch(IllegalAccessException e) { /*e.printStackTrace();*/ }
			catch(InvocationTargetException e) { /*e.printStackTrace();*/ }
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
