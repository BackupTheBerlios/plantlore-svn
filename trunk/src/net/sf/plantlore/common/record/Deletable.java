package net.sf.plantlore.common.record;

import java.io.Serializable;

/**
 * An interface that serves as an annotation that some records 
 * must be deleted from the database in a different way - 
 * they should be marked as deleted (instead of just removed from 
 * the database for good).
 * 
 * @author kaimu
 */
public interface Deletable extends Serializable {
	
	/**
	 * The name of the column. It is the same for all `deletable` records.
	 */
	static final String DELETED = "deleted";
	
	/**
	 * 
	 * @param arg	The "delete level". 
	 * 0 = alive (not deleted), 
	 * 1 = deleted (dead), 
	 * 2 = deleted (because the associated record was deleted)
	 */
	void setDeleted(Integer arg);
	
	/**
	 * 
	 * @return	The "delete level".
	 */
	Integer getDeleted();

}
