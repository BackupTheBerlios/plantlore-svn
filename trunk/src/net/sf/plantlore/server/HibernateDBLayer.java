/*
 * HibernateDBLayer.java
 *
 * Created on 18. únor 2006, 22:31
 *
 */

package net.sf.plantlore.server;

import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import net.sf.plantlore.client.dblayer.query.*;
import net.sf.plantlore.client.dblayer.result.Result;
import net.sf.plantlore.common.record.*;
import org.hibernate.Transaction;
import java.util.List;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import net.sf.plantlore.common.PlantloreConstants;

/**
 *  Implementation of DBLayer using Hibernate OR mapping to access the database.
 *
 *  @author Tomas Kovarik
 */
public class HibernateDBLayer implements DBLayer {
    /** Instance of a logger */
    private Logger logger;
    /** Configuration file for Hibernate */
    private File configFile;   
    /** Hibernate session */
    private Session session;
    /** Query object used for building SELECT queries */
    private DetachedCriteria query;
    /** Results of a select query */
    private ScrollableResults results;
    /** Pool of select queries */        
    private Hashtable queries;
            
    /** Creates a new instance of HibernateDBLayer */
    public HibernateDBLayer() {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
    }    
    
    /**
     *  Initialize database connection. Fire up Hibernate and open a session.
     *
     *  @throws DBLayerException when the hibernate or database connection cannot be initialized
     */
    public void initialize() throws DBLayerException {
        Configuration cfg;
        // File containing Hibernate configuration
        configFile = new File("hibernate.cfg.xml");        
        // Load Hibernate configuration
        try {
            cfg = new Configuration().configure(configFile);
        } catch (HibernateException e) {
            logger.fatal("Cannot load Hibernate configuration. Details: "+e.getMessage());
            throw new DBLayerException("Cannot load Hibernate configuration. Details: "+e.getMessage());            
        }
        cfg.setProperty("hibernate.connection.url", "jdbc:firebirdsql:localhost/3050:c:/Kovo/DatabaseTest/database/plantlore.fdb");
        cfg.setProperty("hibernate.connection.username", "sysdba");
        cfg.setProperty("hibernate.connection.password", "masterkey");        
        try {
            // Build session factory
            SessionFactory sessionFactory = cfg.buildSessionFactory();
            // Open Session
            this.session = sessionFactory.openSession();                
        } catch (HibernateException e) {
            logger.fatal("Cannot create Hibernate session. Details: "+e.getMessage());
            throw new DBLayerException("Cannot create Hibernate session. Details: "+e.getMessage());                        
        }            
    }    
    
    /**
     *  Insert data into the database.
     *
     *  @param data data to insert (one of the data holder objects)
     *  @return identifier (primary key) of the inserted row
     *  @throws DBLayerException when saving data into the database fails
     */
    public int executeInsert(Object data) throws DBLayerException {
        int recordId;        
        Transaction tx = null;        
        try {
            // Begin transaction
            tx = session.beginTransaction();            
            // Save item into the database
            recordId = (Integer)session.save(data);
            // Commit transaction
            tx.commit();                                      
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.fatal("Saving record into the database failed. Details: "+e.getMessage());
            throw new DBLayerException("Saving record into the database failed. Details: "+e.getMessage());
        } finally {
            session.close();
        }
        return recordId;
    }
    
    /**
     *  Delete data from the database.
     *
     *  @param data data we want to delete (must be one of the holder objects)
     *  @throws DBLayerException when deleting data fails
     */
    public void executeDelete(Object data) throws DBLayerException {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // Save item into the database
            session.delete(data);
            // Commit transaction
            tx.commit();                                      
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.fatal("Deleting record from the database failed. Details: "+e.getMessage());
            throw new DBLayerException("Deleting record from the database failed. Details: "+e.getMessage());
        } finally {
            session.close();
        }        
    }
    
    /**
     *  Update data in the database.
     *
     *  @param data to update (must be one of the holder objects)
     *  @throws DBLayerException when updating data fails
     */
    public void executeUpdate(Object data) throws DBLayerException {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // Save item into the database
            session.update(data);
            // Commit transaction
            tx.commit();                                      
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.fatal("Updating record in the database failed. Details: "+e.getMessage());
            throw new DBLayerException("Updating record in the database failed. Details: "+e.getMessage());
        } finally {
            session.close();
        }                
    }
    
    /**
     *  Get more rows from the current result set.
     *
     *  @param from index of the first record we want to load
     *  @param to index of the last row we want to load
     *  @return array of records from the current result set. Each item in the array can be an
     *          array as well (in case associated entities are fetched)
     *  @throws DBLayerException
     */
    public Object[] more(int from, int to) throws DBLayerException {    
        // Check validity of arguments
        if (from>to) {
            logger.error("Cannot read rows from "+from+" to "+to+" because from > to");
            throw new DBLayerException("Cannot read rows from "+from+" to "+to+" because from > to");
        }
        if (from < 1) {
            logger.error("Cannot read rows starting at the given index: "+from);
            throw new DBLayerException("Cannot read rows starting at the given index: "+from);            
        }
        // Move ResultSet to the first row we want to read. In case we want to read the first row,
        // move the pointer before the first row, else move it to the given position
        try {
            if (from > 1) {
                results.setRowNumber(from-1);
            } else {
                results.beforeFirst();
            }
        } catch (HibernateException e) {
            logger.error("Cannot move to the given row of results: "+from);
            throw new DBLayerException("Cannot move to the given row of results: "+from);
        }
        // Allocate space for data
        Object[] data = new Object[to-from+1];
        // Read all the selected rows
        try {
            for (int i=0; i<=(to-from); i++) {
                if (results.next()) {
                    data[i] = results.get();
                } else {
                    logger.error("Result doesn't have enough rows");
                    throw new DBLayerException("Result doesn't have enough rows");                
                }                
            }
        } catch (HibernateException e) {
            logger.error("Cannot read data from the results");
            throw new DBLayerException("Cannot read data from the results");            
        }
        return data;
    }
    
    /**
     *  Get next result from the current result set.
     *
     *  @return next record from the active result set. Array can contain more objects in case
     *          associated entities were fetched.
     *  @throws DBLayerException when loading the results fails
     */
    public Object[] next() throws DBLayerException {
        // In case no more rows are available, return null
        try {
            if (!results.next()) {
                return null;
            }
        } catch (HibernateException e) {
            logger.fatal("Database error occured");
            throw new DBLayerException("Database error occured");
        }        
        return results.get();        
    }
    
    /**
     *  Close hibernate session.
     *
     *  @throws DBLayerException when closing session fails
     */
    public void close(Result QRes) throws DBLayerException {    
        try {
            session.close();
        } catch (HibernateException e) {
            logger.fatal("Cannot close session");
            throw new DBLayerException("Cannot close session");            
        }
    }
    
    /**
     *  Start building select query.
     *
     *  @param classname entity we want to select from the database (given holder object class)
     *  @return an instance of <code>SelectQuery</code> used for building a query by client
     *
     *  TODO: This has to be updated by ERIK to work with RMI
     */
    public SelectQuery createQuery(Class classname) {
        SelectQuery query = new SelectQuery(session.createCriteria(classname));
        queries.put(1, query);
        return query;
    }    
    
    /**
     *  Execute constructed SELECT query. Only executes query, for retrieving results use next() and more()
     *
     *  @param query query we want to execute
     *  @throws DBLayerException when selecting records from the database fails
     */
    public void executeQuery(SelectQuery query) throws DBLayerException {
        Transaction tx = null;        
        try {
            tx = session.beginTransaction();
            // Execute detached criteria query
            query.getCriteria().scroll();
            // Commit transaction
            tx.commit();                                      
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.fatal("Selecting records from the database failed. Details: "+e.getMessage());
            throw new DBLayerException("Selecting records from the database failed. Details: "+e.getMessage());
        }
    }
}