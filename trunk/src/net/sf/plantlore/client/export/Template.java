package net.sf.plantlore.client.export;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.sf.plantlore.common.record.*;


/**
 * The template that holds the information about the selected columns and tables.
 * This information is used by the builder to decide, whether or not the currently
 * considered column of a table should be exported.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-22
 */
public class Template {
	
	/** The list of all pairs Table:Column that shall be exported. */
	private Collection<String> columns = new HashSet<String>(20);
	
	/** The list of all tables the current template covers. */
	private ArrayList<Class> tables = new ArrayList<Class>(20);
	
	/** 
	 * The list of "basic tables" i.e. tables related directly to the Occurence data.
	 */
	public final static Class[] BASIC_TABLES = new Class[] { 
			Author.class, AuthorOccurrence.class, Habitat.class,
			Metadata.class, Occurrence.class, Phytochorion.class,
			Plant.class, Publication.class, Territory.class,
			Village.class };
		
	/** 
	 * @return The table that is central (primary) to the current query. 
	 */
	public Class getRootTable() { return tables.get(0); }
	
	/** 
	 * Mark the database as set every time the database gets involved in a query!
	 * @param table	The database that is involved in a query.
	 */ 
	public void set(Class table) { 
		tables.add(table); 
	}
	
	/** 
	 * Unset a previously selected table.
	 * @param table The table to be unset. 
	 */
	public void unset(Class table) { 
		tables.remove(table);
	}
	
	/** 
	 * 
	 * @param table
	 * @return true if the some of the table's columns are seleted to be exported. 
	 */
	public boolean isSet(Class table) { 
		return tables.contains(table); 
	} 
	
	/**
	 * 
	 * @param table	The table we are interested in and that will be deleted if it is there.
	 * @return true if the table is set.
	 */
	public boolean isSetD(Class table) { return tables.remove(table); }
	
	/**
	 * 
	 * @param table
	 * @param column
	 */
	public void set(Class table, String column) { 
		columns.add(table+"."+column);
		tables.add(table);
	}
	
	
	public void unset(Class table, String column) { 
		columns.remove(table+"."+column); 
	}
	
	
	public boolean isSet(Class table, String column) { 
		return columns.contains(table+"."+column); 
	}
	
	/** 
	 * Deselect all columns of all tables.
	 */
	public void unsetAll() { 
		columns.clear(); 
		tables.clear();
	}
	
		
	/**
	 * Match this template against another one.
	 * 
	 * @param t	The template against which the matching will be held.
	 * @return	True if this template is a superset of the other template.
	 */ 
	public boolean match(Template t) { 
		return columns.containsAll(t.columns); 
	}

}
