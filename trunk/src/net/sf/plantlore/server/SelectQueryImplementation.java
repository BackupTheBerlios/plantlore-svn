/*
 * Selectcriteria.java
 *
 * Created on 24. březen 2006, 20:53
 *
 */

package net.sf.plantlore.server;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.middleware.SelectQuery;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

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
    /* List of projections for the query */
    private ArrayList projections = new ArrayList();
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
    public void createAlias(String propertyName, String aliasName) throws RemoteException {
        criteria.createAlias(propertyName, aliasName);
    }
    
    /**
     *  Make the rows of the results to be distinct from each other. This checks whether whole rows
     *  are distinct from each other. The check is done by Hibernate after the results are retrieved
     *  from the database.
     */
    public void setDistinct() throws RemoteException {
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
    public void addRestriction(int type, String firstPropertyName, String secondPropertyName, Object value, Collection values) throws RemoteException {
        switch (type) {
            case PlantloreConstants.RESTR_BETWEEN:
                Object[] vals = values.toArray();
                if (vals.length >= 2) {
                    criteria.add(Restrictions.between(firstPropertyName, vals[0], vals[1]));
                }
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
     *  Connect restrictions with disjunction (OR) in the WHERE clause.
     *  
     *  @param items array of objects with the following structure:
     *               <ul>
     *                  <li>Index 0: type of the restriction (see PlantloreConstants for constants)</li>
     *                  <li>Index 1: first property name (column name). Used to compare column with value</li>
     *                  <li>Index 2: second property name (column name). Used when comparing two columns</li>
     *                  <li>Index 3: value for the comparison. Used to compare column with value</li>
     *              </ul>
     *              4 values make one restriction, you can pass unlimited number of restrictions which will
     *              be grouped together and connected in disjunction.
     *  @throws IllegalArgumentException in case the input array is not of the correct length 
     *  (must be at least 4 items and number of items must be divisible by 4)
     */
    public void addOrRestriction(Object[] items) throws IllegalArgumentException, RemoteException {
        int type;
        SimpleExpression se;
        Disjunction disj = Restrictions.disjunction();
        if ((items.length % 4) != 0) {
            throw new IllegalArgumentException("Incorrect number of values in the input array");
        }
        int conditions = items.length / 4;        
        for (int i=0;i<conditions;i++) {
            type = (Integer)items[0+i*4];            
            switch (type) {
                case PlantloreConstants.RESTR_EQ:
                    disj.add(Restrictions.eq(((String)items[1+i*4]), items[3+i*4]));
                    break;          
                case PlantloreConstants.RESTR_GE:
                    disj.add(Restrictions.ge(((String)items[1+i*4]), items[3+i*4]));
                    break;                
                case PlantloreConstants.RESTR_GT:
                    disj.add(Restrictions.gt(((String)items[1+i*4]), items[3+i*4]));
                    break;                
                case PlantloreConstants.RESTR_LE:
                    disj.add(Restrictions.le(((String)items[1+i*4]), items[3+i*4]));
                    break;                
                case PlantloreConstants.RESTR_LIKE:
                    disj.add(Restrictions.like(((String)items[1+i*4]), items[3+i*4]));
                    break;
                case PlantloreConstants.RESTR_LT:
                    disj.add(Restrictions.lt(((String)items[1+i*4]), items[3+i*4]));
                    break;                
                case PlantloreConstants.RESTR_NE:
                    disj.add(Restrictions.ne(((String)items[1+i*4]), items[3+i*4]));
                    break;
                case PlantloreConstants.RESTR_EQ_PROPERTY:
                    disj.add(Restrictions.eqProperty(((String)items[1+i*4]), ((String)items[2+i*4])));
                    break;
                case PlantloreConstants.RESTR_GE_PROPERTY:
                    disj.add(Restrictions.geProperty(((String)items[1+i*4]), ((String)items[2+i*4])));
                    break;
                case PlantloreConstants.RESTR_GT_PROPERTY:
                    disj.add(Restrictions.gtProperty(((String)items[1+i*4]), ((String)items[2+i*4])));
                    break;
                case PlantloreConstants.RESTR_ILIKE:
                    disj.add(Restrictions.ilike(((String)items[1+i*4]), ((String)items[2+i*4])));
                    break;
                case PlantloreConstants.RESTR_IS_EMPTY:
                    disj.add(Restrictions.isEmpty(((String)items[1+i*4])));
                    break;
                case PlantloreConstants.RESTR_IS_NOT_EMPTY:
                    disj.add(Restrictions.isNotEmpty(((String)items[1+i*4])));                
                    break;
                case PlantloreConstants.RESTR_IS_NULL:
                    disj.add(Restrictions.isNull(((String)items[1+i*4])));
                    break;
                case PlantloreConstants.RESTR_IS_NOT_NULL:
                    disj.add(Restrictions.isNotNull(((String)items[1+i*4])));
                    break;
                case PlantloreConstants.RESTR_LE_PROPERTY:
                    disj.add(Restrictions.leProperty(((String)items[1+i*4]), ((String)items[2+i*4])));
                    break;                
                case PlantloreConstants.RESTR_LT_PROPERTY:
                    disj.add(Restrictions.ltProperty(((String)items[1+i*4]), ((String)items[2+i*4])));
                    break;
                case PlantloreConstants.RESTR_NE_PROPERTY:
                    disj.add(Restrictions.neProperty(((String)items[1+i*4]), ((String)items[2+i*4])));
                    break;
                default:
                    disj.add(Restrictions.eq(((String)items[1+i*4]), items[3+i*4]));
            }
        }
        criteria.add(disj);
    }
    
    /**
     *  Add projection to constructed criteria. Projections are columns we want to select
     *  
     *  @param type type of projection (not only columns, but also aggregate and other functions)
     *  @param propertyName name of the column for the projection
     *  @see PlantloreConstants
     */
    public void addProjection(int type, String propertyName) throws RemoteException {
        ArrayList proj = new ArrayList(2);
        proj.add(type);
        proj.add(propertyName);
        this.projections.add(proj);
    }
    
    /**
     *  Add the list of projections to the query. This method is only called by DBLayer method 
     *  executeQuery() when executing Select query. Clients should add projections using 
     *  addProjection() method.
     */
    void setProjectionList() {        
        ProjectionList pList = Projections.projectionList();
        if (projections.isEmpty()) {
            return;
        }
        for (Iterator projIter = projections.iterator(); projIter.hasNext(); ) {
            ArrayList proj = (ArrayList)projIter.next();
            System.out.println("Processing projection for: "+proj.get(1));
            switch ((Integer)proj.get(0)) {
                case PlantloreConstants.PROJ_AVG:
                    pList.add(Projections.avg((String)proj.get(1)));
                    break;
                case PlantloreConstants.PROJ_COUNT:
                    pList.add(Projections.count((String)proj.get(1)));
                    break;
                case PlantloreConstants.PROJ_COUNT_DISTINCT:
                    pList.add(Projections.countDistinct((String)proj.get(1)));
                    break;
                case PlantloreConstants.PROJ_GROUP:
                    pList.add(Projections.groupProperty((String)proj.get(1)));
                    break;
                case PlantloreConstants.PROJ_MAX:
                    pList.add(Projections.max((String)proj.get(1)));
                    break;
                case PlantloreConstants.PROJ_MIN:
                    pList.add(Projections.min((String)proj.get(1)));
                    break;
                case PlantloreConstants.PROJ_PROPERTY:
                    pList.add(Projections.property((String)proj.get(1)));
                    break;
                case PlantloreConstants.PROJ_ROW_COUNT:
                    pList.add(Projections.rowCount());
                    break;
                case PlantloreConstants.PROJ_SUM:
                    pList.add(Projections.sum((String)proj.get(1)));
                    break;
                case PlantloreConstants.PROJ_DISTINCT:
                    pList.add(Projections.distinct(Projections.property((String)proj.get(1))));
            }
        }
        criteria.setProjection(pList);
    }
    
    /**
     *  Set method of fetching the results.
     *  
     *  @param associationPath
     *  @param mode 
     */
    public void setFetchMode(String associationPath, int mode) throws RemoteException {
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
    public void addOrder(int direction, String propertyName) throws RemoteException {
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
    public void addAssociation(String associationPath) throws RemoteException {
        criteria.createCriteria(associationPath);
    }
}
