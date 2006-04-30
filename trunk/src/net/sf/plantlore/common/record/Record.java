package net.sf.plantlore.common.record;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The common ancestor of all records. 
 * Every record corresponds to a certain table of the database.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-23
 */
public abstract class Record implements Serializable {

	/** 
	 * Every record has an ID number that is unique in its table.
	 * 
	 * @return The ID number of the record.
	 */
	public abstract Integer getId();
	
	/**
	 * @return The set of all foreign keys (columns that refer to other tables).
	 */
	public ArrayList<String> getForeignKeys() { return new ArrayList(0); }
	
	/**
	 * @return The set of all columns of the table.
	 */
	public ArrayList<String> getColumns() { return new ArrayList(0); }
	
	/**
	 * @return The set of columns that are not foreign keys.
	 */
	public ArrayList<String> getProperties() {
		ArrayList<String> properties = getColumns();
		properties.removeAll(getForeignKeys());
		return properties;
	}
	
	/**
	 * 
	 * @return All not-null columns (including foreign keys).
	 */
	public ArrayList<String> getNN() {
		ArrayList<String> nn = getForeignKeys();
		return nn;
	}
	
	
	protected static ArrayList<String> list(String... values) {
		if(values == null) return new ArrayList<String>(0);
		ArrayList<String> list = new ArrayList<String>(values.length);
		for(String value : values) list.add(value);
		return list;
	}
	
}
