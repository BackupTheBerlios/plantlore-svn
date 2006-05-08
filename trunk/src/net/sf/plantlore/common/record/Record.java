package net.sf.plantlore.common.record;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;


/**
 * The common ancestor of all records. 
 * Every record corresponds to a certain table of the database.
 * <br/>
 * This abstract class provides several methods 
 * that can ease your life.
 * 
 * @see #setValue(String, Object)
 * @see #getValue(String)
 *  
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-23
 */
public abstract class Record implements Serializable {
	
	/** 
	 * The list of "basic tables" i.e. tables related directly to the Occurence data.
	 * The other tables are database specific and are used by our System only
	 * (concerns History, LastUpdate, User, AccessRights, and possibly more).
	 */
	public final static Class[] BASIC_TABLES = new Class[] { 
		Author.class, AuthorOccurrence.class, Habitat.class,
		Metadata.class, Occurrence.class, Phytochorion.class,
		Plant.class, Publication.class, Territory.class,
		Village.class 
	};
	
	
	/**
	 * A set of tables that cannot be changed.
	 */
	public final static HashSet<Class> IMMUTABLE = new HashSet( 10 );
	
	
	/** The list of all getters (of all properties of all tables). */
	private static Hashtable<String,Method> getters = new Hashtable<String, Method>(100);
	
	
	/** Pre-load all getters. */
	static {
		
		IMMUTABLE.add(Plant.class);
		IMMUTABLE.add(Territory.class);
		IMMUTABLE.add(Village.class);
		IMMUTABLE.add(Phytochorion.class);
		IMMUTABLE.add(Metadata.class);
		
		// Take all basic tables.
		for( Class table : BASIC_TABLES)
			try {
				// Take all their columns.
				ArrayList<String> columns = ((Record) table.newInstance()).getColumns();
				for(String column : columns)
					// And store their getters. 
					getters.put(table.getSimpleName()+"."+column, getter(table, column));
			} 
			catch(IllegalAccessException e) { e.printStackTrace(); }
			catch(InstantiationException e) { e.printStackTrace(); }
	}


	/**
	 * Return the value in the specified column.
	 * 
	 * @param column	The name of the column.
	 * @return	The value this record contains in this column.
	 */
	public Object getValue(String column) {
		try {
			return getters.get(getClass().getSimpleName()+"."+column).invoke(this, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return null; 
		}
	}
	
	/**
	 * Set the value in the specified column.
	 * 
	 * @param column	The name of the column.
	 * @param value	The new value.
	 */
	public void setValue(String column, Object value) {
//		System.out.println(column + " = " + value);
	}
	
	/**
	 * Replace certain <code>columns</code> 
	 * with values contained in the other <code>record</code>.
	 * <br/>
	 * Implementation:
	 * <pre>
	 * for( column : columns )
	 *     this.setValue( column, record.getValue(column) );
	 * </pre>.
	 * 
	 * @param record	The source (containing new values).
	 * @param columns	Names of columns whose values will change.
	 */	
	public void replaceValues(Record record, String...columns) {
		if(this.getClass() != record.getClass()) return;
		for(String column : columns)
			setValue( column, record.getValue(column) );
	}

	/**
	 * Replace all collumns with values from the record,
	 * making a virtual clone of the <code>record</code>.
	 * 
	 * @param record The source record whose values will replace those of this record.
	 */
	public void replaceWith(Record record) {
		if(this.getClass() != record.getClass()) return;
		for(String column : record.getColumns())
			setValue( column, record.getValue(column) );
	}

	/** 
	 * Every record has an ID number that is unique in its table.
	 * 
	 * @return The ID number of the record.
	 */
	public abstract Integer getId();
	
	/** 
	 * Every record has an ID number that is unique in its table.
	 * 
	 * @param id The new ID number of the record.
	 */
	public abstract void setId(Integer id);
	
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
	 * @return The set of names of all foreign keys (columns that refer to other tables).
	 */
	public ArrayList<String> getForeignKeys() { return new ArrayList(0); }
	
	/**
	 * @return The set of names of all columns of the table.
	 */
	public ArrayList<String> getColumns() { return new ArrayList(0); }
	
	/**
	 * @return The set of names of columns that are not foreign keys.
	 */
	public ArrayList<String> getProperties() {
		ArrayList<String> properties = getColumns();
		properties.removeAll(getForeignKeys());
		return properties;
	}
	
	/**
	 * @return The set of names of all not-null columns (including foreign keys).
	 */
	public ArrayList<String> getNN() {
		ArrayList<String> nn = getForeignKeys();
		return nn;
	}
	
	/**
	 * Are all not null values really not null?
	 * <b>Recursive operation!</b>
	 * 
	 * @return True if all columns marked as Not Null contain some value (i.e. they are not null).
	 */
	public boolean areAllNNSet() {
		for( String column : getNN() ) { 
			Object value = getValue(column);
			System.out.println(" # "+this.getClass().getSimpleName()+"."+column+" = ["+value+"].");
			if( value == null ) return false;
			if( value instanceof Record && !((Record)value).areAllNNSet() ) return false;
		}
		return true;
	}
	
	/**
	 * Two records are equal if and only if they have exactly the same values
	 * in the same columns (and this extends to foreign keys as well).
	 * <b>Recursive operation!</b>
	 * <br/>
	 * For instance:
	 * Comparing <code>Occurrence1</code> to <code>Occurrence2</code> will also compare
	 * <code>Occurrence1.Habitat.Territory.Name</code>
	 *  to <code>Occurrence2.Habitat.Territory.Name</code>.
	 */
	@Override
	public boolean equals(Object obj) {
		if( getClass() != obj.getClass() ) return false;
		Record record = (Record) obj;
		for( String column : getColumns() ) {
			Object 
			v1 = this.getValue(column),
			v2 = record.getValue(column);
			if(v1 == null && v2 == null) continue;
			if(v1 == null || v2 == null) return false;
			if( v1.equals(v2) ) continue;
			else return false;
		}
		return true;
	}
	
	
	/**
	 * Convert an array of strings to an ArrayList<String>.
	 * 
	 * @param values	Varargs - strings.
	 * @return	ArrayList containing all values.
	 */
	public static ArrayList<String> list(String... values) {
		if(values == null) return new ArrayList<String>(0);
		ArrayList<String> list = new ArrayList<String>(values.length);
		for(String value : values) list.add(value);
		return list;
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
	
	
	@Override
	public String toString() {
		StringBuilder sigma = new StringBuilder();
		for(String property : this.getProperties()) {
			sigma.append(property);
			sigma.append(" = ");
			sigma.append(this.getValue(property));
			sigma.append("; ");
		}
		
		return sigma.toString();
	}
	
}
