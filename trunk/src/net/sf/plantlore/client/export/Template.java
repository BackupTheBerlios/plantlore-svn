package net.sf.plantlore.client.export;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.sf.plantlore.common.record.*;


/**
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-22
 */
public class Template {
	
	/** The list of all pairs Table:Column that shall be exported. */
	private Collection<String> columns = new HashSet<String>(20);
	
	/** The list of all tables the current template covers. */
	private ArrayList<Class> tables = new ArrayList<Class>(20);
	
	/** The list of "basic tables" i.e. tables related directly to the Occurence data. */
	public final static Class[] BASIC_TABLES = new Class[] { 
			Author.class, AuthorOccurrence.class, Habitat.class,
			Metadata.class, Occurrence.class, Phytochorion.class,
			Plant.class, Publication.class, Territory.class,
			Village.class
			};
	
	public static Class whichTable(Record record) {
		for(Class c : BASIC_TABLES)
			if( c.isInstance( record ) ) return c;
		return null;
	}
	
	/** The table that is central to the current query. */
	public Class getRootTable() { return tables.get(0); }
	
	public void setTable(Class table) { tables.add(table); }
	public void unsetTable(Class table) { tables.remove(table); }
	public boolean isSetTable(Class table) { return tables.contains(table); } 
	public boolean isSetTableD(Class table) { return tables.remove(table); }
	
	public void set(Class table, String column) { columns.add(table+"."+column); }
	public void unset(Class table, String column) { columns.remove(table+"."+column); }
	public boolean isSet(Class table, String column) { return columns.contains(table+"."+column); }
	
		
	public void unsetAll() { columns.clear(); }
	
	public boolean match(Template t) { return columns.containsAll(t.columns); }

}
