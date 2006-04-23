package net.sf.plantlore.common.record;

import java.io.Serializable;

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
	public String[] getForeignKeys() { return null; }
	
	/**
	 * @return The set of all columns of the table.
	 */
	public String[] getColumns() { return null; }
	
}
