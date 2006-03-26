package net.sf.plantlore.middleware;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.Collection;

import net.sf.plantlore.common.PlantloreConstants;

/**
 * Select query constructor.
 * 
 * FIXME Dopsat sem popis! Kowo - co to dela, jak to pouzivat apod...
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com), Tomáš Kovařík
 * @since 2006-03-26
 */
public interface SelectQuery extends Serializable, Remote {
	
	/**
     *  Add restriction to the constructed criteria. Restrictions are parts of the where condition.
     *  
     *  @param type type of the restriction (see constants defined in PlantloreConstants)
     *  @param firstPropertyName argument of restrictions working with one property (table column)
     *  @param secondPropertyName argument of restrictions wirking with two properties
     *  @param value value for restrictions containg values
     *  @param values collection of values for restrictions working with more values (RESTR_IN)
     */
	void addRestriction(int type, String firstPropertyName, String secondPropertyName, Object value, Collection values);
	
    /**
     *  Add projection to constructed criteria. Projections are columns we want to select
     *  
     *  @param type type of projection (not only columns, but also aggregate and other functions)
     *  @param propertyName name of the column for the projection
     *  @see PlantloreConstants
     */
	void addProjection(int type, String propertyName);
	
	/**
     *  Set method of fetching the results.
     *  
     *  @param associationPath
     *  @param mode 
     */
	void setFetchMode(String associationPath, int mode);
	
	 /**
     *  Add orderby clause to the constructed criteria.
     *
     *  @param direction direction of ordering (ASC or DESC)
     *  @param propertyName property we want to use for ordering the results
     */
	void addOrder(int direction, String propertyName);
	
	 /**
     *  Add association to the criteria. Association means that given associated record (from a 
     *  different table) should be loaded as well.
     *
     *  @param associationPath path of associated entities
     */
	void addAssociation(String associationPath);

}
