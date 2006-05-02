package net.sf.plantlore.common.record;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import net.sf.plantlore.client.export.Template;

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
	 * Some records in the database are marked as deleted.
	 * A record is considered <i>dead</i> if it was marked as deleted. 
	 * 
	 * @return True if the record was marked as deleted.
	 */
	public boolean isDead() {
		return false;
	}
	
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
	
	/**
	 * Two records are equal if they have exactly the same values
	 * in the same columns (and this extends to foreign keys as well).
	 * <br/>
	 * For instance:
	 * Comparing <code>Occurrence1</code> to <code>Occurrence2</code> will also compare
	 * <code>Occurrence1.Habitat.Territory.Name</code>
	 *  to <code>Occurrence2.Habitat.Territory.Name</code>.
	 */
	@Override
	public boolean equals(Object obj) {
		if( !(obj instanceof Record) ) return false;
		Record record = (Record) obj;
		Class table = record.getClass();
		for(String column : record.getColumns() ) {
			Method get = Template.getMethod(table, column);
			try {
				Object 
					v1 = get.invoke(this, new Object[0]),
					v2 = get.invoke(obj, new Object[0]);
				if(v1 == null && v2 == null) return true;
				if(v1 == null || v2 == null) return false;
				return v1.equals(v2); // possible recursion here!
			} 
			catch(IllegalAccessException e) { return false; }
			catch(InvocationTargetException e) { return false; }
		}
		return false;
	}
	
	
	/**
	 * Convert an array of strings to an ArrayList<String>.
	 * 
	 * @param values	Varargs - strings.
	 * @return	ArrayList containing all values.
	 */
	protected static ArrayList<String> list(String... values) {
		if(values == null) return new ArrayList<String>(0);
		ArrayList<String> list = new ArrayList<String>(values.length);
		for(String value : values) list.add(value);
		return list;
	}
	
}
