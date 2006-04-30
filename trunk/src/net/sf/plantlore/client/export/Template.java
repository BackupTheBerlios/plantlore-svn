package net.sf.plantlore.client.export;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;

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
	
	/** 
	 * The list of "basic tables" i.e. tables related directly to the Occurence data.
	 * The other tables are database specific and are used by our System only
	 * (concerns History, LastUpdate, User, AccessRights, and possibly more).
	 */
	public final static Class[] BASIC_TABLES = new Class[] { 
			Author.class, AuthorOccurrence.class, Habitat.class,
			Metadata.class, Occurrence.class, Phytochorion.class,
			Plant.class, Publication.class, Territory.class,
			Village.class };
	
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
	
		
	/** The list of all getters (of all properties of all tables). */
	private static Hashtable<String,Method> getters = new Hashtable<String, Method>(100);
	
	/**
	 * Return the getter of this Table.Column property.
	 * <br/>
	 * This method is fast because all getters are pre-loaded.
	 * 
	 * @param table	The class identifying the table.
	 * @param column The name of the column.
	 * @return The getter of table.column
	 */
	public static Method getMethod(Class table, String column) {
		return getters.get(table+"."+column);
	}
		
	/** Pre-load all getters. */
	static {
		// Take all basic tables.
		for( Class table : Template.BASIC_TABLES)
			try {
				// Take all their columns.
				ArrayList<String> columns = ((Record) table.newInstance()).getColumns();
				for(String column : columns)
					// And store their getters. 
					getters.put(table+"."+column, getter(table, column));
			} 
			catch(IllegalAccessException e) {}
			catch(InstantiationException e) {}
	}
	
	/**
	 * Return the method that corresponds with the getter of <code>table.column</code>.  
	 * 
	 * @return The getter of <code>table.column</code>. 
	 */
	private static Method getter(Class table, String column) {
		try {
			// Create the name of the getter.
			StringBuilder s = new StringBuilder("get" + column); 
			s.setCharAt(3, Character.toUpperCase(s.charAt(3)));
			// Take it. 
			return table.getMethod( s.toString(), new Class[0] );
		} catch(NoSuchMethodException e) {}
		return null;
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
		for(Class table : BASIC_TABLES)
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
