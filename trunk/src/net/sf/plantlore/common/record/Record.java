package net.sf.plantlore.common.record;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;


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
 * @version 2.0
 */
public abstract class Record implements Serializable {
	
	
	private static final long serialVersionUID = 20060604000L;
	
	/** 
	 * The list of "basic tables" i.e. tables related directly to the Occurence data.
	 * The other tables are database specific and are used by our System only
	 * (concerns History, LastUpdate, User, AccessRights, and possibly more).
	 */
	public final static Set<Class> BASIC_TABLES = new HashSet<Class>( Arrays.asList(
		Occurrence.class, Habitat.class, Territory.class, Village.class, Phytochorion.class,
		Plant.class, Metadata.class, Publication.class, Author.class, AuthorOccurrence.class) 
	);
	        
	/**
	 * A set of tables that cannot be changed.
	 */
	public final static HashSet<Class> IMMUTABLE = new HashSet<Class>( Arrays.asList(
			Plant.class, Territory.class, Village.class, Phytochorion.class, Metadata.class) 
	);
	
	
	/** The list of all getters (of all properties of all tables). */
	private static Hashtable<String,Method> getters = new Hashtable<String, Method>(100);
	
	
	/** Pre-load all getters. */
	static {
		// Take all basic tables.
		for( Class table : BASIC_TABLES)
			try {
				// Take all their columns.
				List<String> columns = ((Record) table.newInstance()).getColumns();
				for(String column : columns)
					// And store their getters. 
					getters.put(table.getSimpleName()+"."+column, getter(table, column));
			} 
			catch(IllegalAccessException e) { e.printStackTrace(); }
			catch(InstantiationException e) { e.printStackTrace(); }
	}

	/**
	 * @return	Alias used for this table.
	 */
	public String alias() {
		return alias( getClass() );
	}

	/**
	 * @param table	The table whose alias we need.
	 * @return	The alias for that table.
	 */
	public static String alias(Class table) {
		return "A" + table.getSimpleName();
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
	 * Return the value of the specified subrecord's column.
	 *
	 * @param table	The subrecord you are interested in.
	 * @param column The name of the subrecord's column whose value you wish to obtain.
	 */
	public Object getValue(Class table, String column) {
		Record subrecord = (  getClass().equals(table) ? this : findSubrecord(this, table)  );
		return (subrecord == null) ? null : subrecord.getValue(column);
	}
	
	
	/**
	 * Return the subrecord of this record of the specified type.
	 * 
	 * @param type	The type of the subrecord.
	 * @return	The subrecord of the specified type.
	 */
	public Record findSubrecord(Class type) {
		return getClass().equals(type) ? this : findSubrecord(this, type);
	}

	/**
	 * Return the subrecord of the base record of the specified type.
	 * 
	 * @param base	The base record.
	 * @param type	The type of the subrecord.
	 * @return	The subrecord of the specified type.
	 */
	public Record findSubrecord(Record base, Class type) {
		for(String key : base.getForeignKeys()) {
			Record sub = (Record)base.getValue(key);
			if(sub == null) continue; // As a matter of fact this can happen - Publication can be NULL.
			if(sub.getClass().equals(type)) return sub;
			sub = findSubrecord( sub, type); // dig deeper...
			if(sub != null) return sub;
		}
		return null;
	}
	
	/**
	 * The name of the package where all records (holder objects) dwell.
	 */
	private final static String pckg = Record.class.getPackage().getName() + "."; 
	
	
	/**
	 * @return	The record with all subrecords (accessible via foreign keys) created.
	 */
	public Record createTorso() {
		StringBuilder className;
		for(String key : getForeignKeys()) {
			if(key.equals(Habitat.NEARESTVILLAGE))
				className = new StringBuilder("Village");
			else {
				className = new StringBuilder(key);
				className.setCharAt(0, Character.toUpperCase(className.charAt(0)));
			}
			try {
				Record subrecord = (Record)Class.forName(pckg+className).newInstance();
				subrecord.createTorso();
				setValue(key, subrecord);
			} catch (Exception e) { e.printStackTrace(); }
		}
		return this;
	}
	
	/**
	 * Set a <code>value</code> of the <code>column</code> of the <code>subrecord</code>.
	 * @param table	The type of the subrecord.
	 * @param column	The name of the column.
	 * @param value	The value to be set to that column.
	 */
	public void setValue(Class table, String column, Object value) {
		Record subrecord = (  getClass().equals(table) ? this : findSubrecord(this, table)  );
		if(subrecord != null) subrecord.setValue(column, value);
	}
	
	/**
	 * Set the value in the specified column.
	 * 
	 * @param column	The name of the column.
	 * @param value	The new value.
	 */
	@SuppressWarnings("unused")
	public void setValue(String column, Object value) {
		throw new Error(" This code shall not be executed. You must either override it or leave it! ");
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
	public List<String> getForeignKeys() { return new ArrayList<String>(0); }
	
	/**
	 * @return The set of names of all columns of the table.
	 */
	public List<String> getColumns() { return new ArrayList<String>(0); }
	
	/**
	 * @return The set of names of columns that are not foreign keys.
	 */
	public List<String> getProperties() {
		List<String> properties = getColumns(),
		keys = getForeignKeys();
		
		properties.removeAll( keys );
		return properties;
	}
	
	/**
	 * @return The set of names of all not-null columns (including foreign keys).
	 */
	public List<String> getNN() {
		List<String> nn = getForeignKeys();
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
			//System.out.println(" # "+this.getClass().getSimpleName()+"."+column+" = ["+value+"].");
			if( value == null ) {
				System.out.println(getClass().getSimpleName()+"."+column+" = 0");
				return false;
			}
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
		if( obj == null || getClass() != obj.getClass() ) return false;
		Record record = (Record) obj;
		for( String column : getColumns() ) {
			Object 
			v1 = this.getValue(column),
			v2 = record.getValue(column);
			if(v1 == null && v2 == null) continue;
			if(v1 == null || v2 == null) return false;
			if( v1.equals(v2) ) continue;
			return false;
		}
		return true;
	}
	
	/**
	 * Compares this record to the <code>obj</code>. 
	 * Both records are equal 
	 * if and only if values in their columns (up to the list of exceptions) match.
	 * 
	 * @param obj	The record to compare this one to.
	 * @param exceptions	The list of names of columns that shall not be tested.
	 * @return	True if both records match.
	 */
	public boolean equalsUpTo(Object obj, String...exceptions) {
		if( obj == null || getClass() != obj.getClass() ) return false;
		Record record = (Record) obj;
		List<String> columns = getColumns();
		columns.removeAll( Arrays.asList(exceptions) );
		for( String column : columns ) {
			Object 
			v1 = this.getValue(column),
			v2 = record.getValue(column);
			if(v1 == null && v2 == null) continue;
			if(v1 == null || v2 == null) return false;
			if( v1.equals(v2) ) continue;
			return false;
		}
		return true;
	}
	
	/**
	 * @return	True if and only if both have the same properties
	 * (i.e. the same values in columns that are not foreign keys).
	 */
	public boolean equalsInProperties(Record record) {
		return equalsUpTo(record, this.getForeignKeys().toArray(new String[0]) );
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
		} catch(NoSuchMethodException e) {/* Nothing we can do.. */}
		return null;
	}
	
	
	protected List<String> asList(String...values) {
		return new ArrayList<String>(Arrays.asList(values));
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		for(String property : getProperties()) {
			Object value = getValue(property);
			if( value != null)
				hash ^= value.hashCode(); 
		}
		return hash;
	}
	

	/**
	 * Convert the record into a string. 
	 * The list of values spans across subrecords as well.
	 * For debug purposes mostly.
	 */
	@Override
	public String toString() {
		StringBuilder sigma = new StringBuilder();
		for(String property : this.getProperties())
			sigma.append(getClass().getSimpleName()).append('.').
			append(property).append(" = ").append(this.getValue(property)).append(";");
		for(String key : getForeignKeys()) {
			Record subrecord = (Record)getValue(key); 
			if(subrecord != null)	sigma.append( subrecord.toString() );
		}
		return sigma.toString();
	}
	                
}
