package net.sf.plantlore.middleware;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

import net.sf.plantlore.common.PlantloreConstants;

/**
 * Interface for building SELECT queries. Different parts of the query can be built using methods
 * of this interface. Main parts include list of the selected columns (projection), WHERE condition
 * (restrictions) and ORDER BY clauses. Also allows joining of tables using aliases. For detailed
 * explanation refer to Plantlore dosumnetation.
 *
 * FIXME Tie metody by asi mali hadzat DBLayerException a prekladat vyhadzovane HibernateExceptions
 * FIXME Chyba tu zoskupovanie podmienok vo WHERE klauzuli pomocou AND a OR
 *
 * @author Erik Kratochvíl (discontinuum@gmail.com), Tomáš Kovařík (kovo@matfyz.cz)
 * @since 2006-03-26
 */
public interface SelectQuery extends Serializable, Remote {

    /** 
     *  Create alias for joining tables. Alias can be defined for a foreign key parameter and can be
     *  used to add restrictions on the table referenced by the foreign key. For thorough explanation
     *  see documentation.
     *
     *  @param propertyName name of the column for which we want to create an alias (foreign key column)
     *  @param aliasName name of the new alias
     */    
    void createAlias(String propertyName, String aliasName)  throws RemoteException;
    
    /**
     *  Add restriction to the constructed criteria. Restrictions are parts of the where condition.
     *
     *  @param type type of the restriction (see constants defined in PlantloreConstants)
     *  @param firstPropertyName argument of restrictions working with one property (table column)
     *  @param secondPropertyName argument of restrictions wirking with two properties
     *  @param value value for restrictions containg values
     *  @param values collection of values for restrictions working with more values (RESTR_IN)
     */
    void addRestriction(int type, String firstPropertyName, String secondPropertyName, Object value, Collection values)  throws RemoteException;
    
    /**
     *  Add projection to constructed criteria. Projections are columns we want to select
     *
     *  @param type type of projection (not only columns, but also aggregate and other functions)
     *  @param propertyName name of the column for the projection
     *  @see PlantloreConstants
     */
    void addProjection(int type, String propertyName)  throws RemoteException;
    
    /**
     *  Set method of fetching the results.
     *
     *  @param associationPath
     *  @param mode
     */
    void setFetchMode(String associationPath, int mode)  throws RemoteException;
    
    /**
     *  Add orderby clause to the constructed criteria.
     *
     *  @param direction direction of ordering (ASC or DESC)
     *  @param propertyName property we want to use for ordering the results
     */
    void addOrder(int direction, String propertyName)  throws RemoteException;
    
    /**
     *  Add association to the criteria. Association means that given associated record (from a
     *  different table) should be loaded as well.
     *
     *  @param associationPath path of associated entities
     */
    void addAssociation(String associationPath)  throws RemoteException;
    
}
