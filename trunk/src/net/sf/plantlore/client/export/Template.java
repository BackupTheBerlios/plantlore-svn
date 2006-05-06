package net.sf.plantlore.client.export;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.sf.plantlore.common.record.*;


/**
 * The template holds information about the selected columns and tables.
 * This information may used by builders and access rights managers
 * to decide, whether or not the currently
 * considered column should be exported / is accessible.
 * <br/>
 * Note: The template doesn't know anything about the structure
 * (mapping) of the database.  
 * <br/>
 * A little dictionary:
 * <ul>
 * <li><i>foreign key</i> a column of a table that refers to another table,</li>
 * <li><i>property</i> a column of a table that contains a (possibly null) value,
 * but doesn't refer to another table</li>
 * <li><i>nn</i> a property that cannot be null (defined in the db model)</li>
 * </ul>
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-22
 * @version 1.1
 */
public class Template {
	
	/** The list of all pairs Table.Column that are set. */
	private Collection<String> columns = new HashSet<String>(100);
	
	

	
	/** Create a new template. */
	public Template() {}
	
	/** A copy constructor. */
	public Template(Template template) {
		columns = new HashSet<String>( template.columns );
	}
	
	
	@Override
	public Template clone() {
		return new Template(this);
	}
	
	
	/** Select the <code>table.column</code>. */
	public void set(Class table, String column) { 
		columns.add(table.getSimpleName()+ (column == null ? "" : "."+column));
		//System.out.println(" + " + table.getSimpleName() + (column == null ? "" : "." + column));
	}
	
	/** Unselect the <code>table.column</code>. */
	public void unset(Class table, String column) { 
		columns.remove(table.getSimpleName()+ (column == null ? "" : "."+column));
		//System.out.println(" - " + table.getSimpleName() + (column == null ? "" : "." + column));
	}
	
	/** @return true if the <code>table.column</code> is set.*/
	public boolean isSet(Class table, String column) {
		return columns.contains(table.getSimpleName()+ (column == null ? "" : "."+column));
	}
	
	/** Unselect all columns of all tables. */
	public void unsetEverything() { 
		columns.clear(); 
	}
	
	/** Select all columns (properties) of all tables. */
	public void setEverything() {
		for(Class table : Record.BASIC_TABLES)
			setAllProperties(table);
	}
	
	/** Select all not null columns (properties). */
	public void setEverythingNN() {
		
	}
	
	/** Select all properties of this <code>table</code>. */
	public void setAllProperties(Class table) {
		try {
			for( String column : ((Record)table.newInstance()).getProperties() )
				set(table, column);
		} catch(IllegalAccessException e) {}
		catch(InstantiationException e) {}
	}
	
	/** Select all not-null properties of the specified <code>table</code>. */
	public void setAllNN(Class table) {
		try {
			Record record = ((Record)table.newInstance());
			ArrayList<String> nnProperties = record.getNN();
			nnProperties.removeAll(record.getForeignKeys());
			for( String column :  nnProperties )
				set(table, column);
		} catch(IllegalAccessException e) {}
		catch(InstantiationException e) {}
	}
	
	/**
	 * @return true if nothing is selected.
	 */
	public boolean isEmpty() {
		return columns.isEmpty();
	}
	
		
	/**
	 * Match this template against another one.
	 * 
	 * @param t	The template against which the matching will be done.
	 * @return	True if this template is a superset of the other template.
	 */ 
	public boolean match(Template t) { 
		return columns.containsAll(t.columns); 
	}
	
	
}
