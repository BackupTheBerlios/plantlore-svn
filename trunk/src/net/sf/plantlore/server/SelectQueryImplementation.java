/*
 * Selectcriteria.java
 *
 * Created on 24. březen 2006, 20:53
 *
 */

package net.sf.plantlore.server;


import java.util.Collection;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.middleware.SelectQuery;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * Implemetation of SelectQuery using Hibernate OR Mapping for database querying. Creates Hibernate
 * "criteria query" and sets projections, restrictions and order by clause. Also allows selecting
 * data from joined tables. For detailed explanation refer to Plantlore documentation and Hibernate 
 * reference manual.
 *
 *  FIXME: Malo by to hadzat DBLayerException...
 *  
 *  TODO: Nezapominat generovat stub! (rmic net.sf.plantlore.server.SelectQueryImplementation)
 *
 * @author Tomáš Kovařík, Erik Kratochvíl
 */
public class SelectQueryImplementation implements SelectQuery {
    // Hibernate criteria used in criteria query
    private Criteria criteria;
    
    /** Creates a new instance of SelectQueryImplementation */
    public SelectQueryImplementation(Criteria criteria) {
        this.criteria = criteria;
    }
    
    /**
     *  Return instance of Hibernate criteria object representing this query
     *
     *  @return insatnce of Hibernate criteria object representing this query
     */
    Criteria getCriteria() {
        return this.criteria;
    }
    
    /** 
     *  Create alias for joining tables. Alias can be defined for a foreign key parameter and can be
     *  used to add restrictions on the table referenced by the foreign key. For thorough explanation
     *  see documentation.
     *
     *  @param propertyName name of the column for which we want to create an alias (foreign key column)
     *  @param aliasName name of the new alias
     */
    public void createAlias(String propertyName, String aliasName) {
        criteria.createAlias(propertyName, aliasName);
    }
    
    public void setDistinct() {
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
    }
    
    /**
     *  Add restriction to the constructed criteria. Restrictions are parts of the where condition.
     *  
     *  @param type type of the restriction (see constants defined in PlantloreConstants)
     *  @param firstPropertyName argument of restrictions working with one property (table column)
     *  @param secondPropertyName argument of restrictions wirking with two properties
     *  @param value value for restrictions containg values
     *  @param values collection of values for restrictions working with more values (RESTR_IN)
     */
    public void addRestriction(int type, String firstPropertyName, String secondPropertyName, Object value, Collection values) {
        switch (type) {
            case PlantloreConstants.RESTR_BETWEEN:
                criteria.add(Restrictions.like(firstPropertyName, value));
                break;
            case PlantloreConstants.RESTR_EQ:
                criteria.add(Restrictions.eq(firstPropertyName, value));
                break;
            case PlantloreConstants.RESTR_EQ_PROPERTY:
                criteria.add(Restrictions.eqProperty(firstPropertyName, secondPropertyName));                
                break;
            case PlantloreConstants.RESTR_GE:
                criteria.add(Restrictions.ge(firstPropertyName, value));                
                break;
            case PlantloreConstants.RESTR_GE_PROPERTY:
                criteria.add(Restrictions.geProperty(firstPropertyName, secondPropertyName));                
                break;
            case PlantloreConstants.RESTR_GT:
                criteria.add(Restrictions.gt(firstPropertyName, value));                                
                break;
            case PlantloreConstants.RESTR_GT_PROPERTY:
                criteria.add(Restrictions.gtProperty(firstPropertyName, secondPropertyName));
                break;
            case PlantloreConstants.RESTR_ILIKE:
                criteria.add(Restrictions.ilike(firstPropertyName, value));
                break;
            case PlantloreConstants.RESTR_IN:
                criteria.add(Restrictions.in(firstPropertyName, values));                                                
                break;
            case PlantloreConstants.RESTR_IS_EMPTY:
                criteria.add(Restrictions.isEmpty(firstPropertyName));
                break;
            case PlantloreConstants.RESTR_IS_NOT_EMPTY:
                criteria.add(Restrictions.isNotEmpty(firstPropertyName));                
                break;
            case PlantloreConstants.RESTR_IS_NULL:
                criteria.add(Restrictions.isNull(firstPropertyName));                                
                break;
            case PlantloreConstants.RESTR_IS_NOT_NULL:
                criteria.add(Restrictions.isNotNull(firstPropertyName));                                
                break;
            case PlantloreConstants.RESTR_LE:
                criteria.add(Restrictions.le(firstPropertyName, value));                
                break;
            case PlantloreConstants.RESTR_LE_PROPERTY:
                criteria.add(Restrictions.leProperty(firstPropertyName, secondPropertyName));                
                break;                
            case PlantloreConstants.RESTR_LIKE:
                criteria.add(Restrictions.like(firstPropertyName, value));                
                break;
            case PlantloreConstants.RESTR_LT:
                criteria.add(Restrictions.lt(firstPropertyName, value));                                
                break;
            case PlantloreConstants.RESTR_LT_PROPERTY:
                criteria.add(Restrictions.ltProperty(firstPropertyName, secondPropertyName));
                break;
            case PlantloreConstants.RESTR_NE:
                criteria.add(Restrictions.ne(firstPropertyName, value));                
                break;
            case PlantloreConstants.RESTR_NE_PROPERTY:
                criteria.add(Restrictions.neProperty(firstPropertyName, secondPropertyName));
                break;
            default:
                
        }
    }
    
    /**
     *  Add projection to constructed criteria. Projections are columns we want to select
     *  
     *  @param type type of projection (not only columns, but also aggregate and other functions)
     *  @param propertyName name of the column for the projection
     *  @see PlantloreConstants
     */
    public void addProjection(int type, String propertyName) {
        switch (type) {
            case PlantloreConstants.PROJ_AVG:
                criteria.setProjection(Projections.avg(propertyName));
                break;
            case PlantloreConstants.PROJ_COUNT:
                criteria.setProjection(Projections.count(propertyName));
                break;
            case PlantloreConstants.PROJ_COUNT_DISTINCT:
                criteria.setProjection(Projections.countDistinct(propertyName));
                break;
            case PlantloreConstants.PROJ_GROUP:
                criteria.setProjection(Projections.groupProperty(propertyName));
                break;
            case PlantloreConstants.PROJ_MAX:
                criteria.setProjection(Projections.max(propertyName));
                break;
            case PlantloreConstants.PROJ_MIN:
                criteria.setProjection(Projections.min(propertyName));
                break;
            case PlantloreConstants.PROJ_PROPERTY:
                criteria.setProjection(Projections.property(propertyName));
                break;
            case PlantloreConstants.PROJ_ROW_COUNT:
                criteria.setProjection(Projections.rowCount());
                break;
            case PlantloreConstants.PROJ_SUM:
                criteria.setProjection(Projections.sum(propertyName));
                break;
            case PlantloreConstants.PROJ_DISTINCT:
                criteria.setProjection(Projections.distinct(Projections.property(propertyName)));
            default:
                
        }
    }
    
    /**
     *  Set method of fetching the results.
     *  
     *  @param associationPath
     *  @param mode 
     */
    public void setFetchMode(String associationPath, int mode) {
        switch (mode) {
            case PlantloreConstants.FETCH_SELECT:
                criteria.setFetchMode(associationPath, FetchMode.SELECT);
                break;
            case PlantloreConstants.FETCH_JOIN:
                criteria.setFetchMode(associationPath, FetchMode.JOIN);             
                break;
            default:
                criteria.setFetchMode(associationPath, FetchMode.DEFAULT);                
        }
    }
    
    /**
     *  Add orderby clause to the constructed criteria.
     *
     *  @param direction direction of ordering (ASC or DESC)
     *  @param propertyName property we want to use for ordering the results
     */
    public void addOrder(int direction, String propertyName) {
        switch (direction) {
            case PlantloreConstants.DIRECT_ASC: criteria.addOrder(Order.asc(propertyName));                    
                                break;
            case PlantloreConstants.DIRECT_DESC:criteria.addOrder(Order.desc(propertyName));
                                break;
            default: criteria.addOrder(Order.asc(propertyName));
        }        
    }
    
    /**
     *  Add association to the criteria. Association means that given associated record (from a 
     *  different table) should be loaded as well.
     *
     *  @param associationPath path of associated entities
     */
    public void addAssociation(String associationPath) {
        criteria.createCriteria(associationPath);
    }
}
