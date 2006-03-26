/*
 * HibernateDBLayer.java
 *
 * Created on 18. únor 2006, 22:31
 *
 */

package net.sf.plantlore.server;

import java.io.File;
import java.rmi.server.Unreferenced;
import java.util.Hashtable;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import net.sf.plantlore.client.dblayer.result.Result;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;

import org.hibernate.Transaction;


/**
 *  Implementation of DBLayer using Hibernate OR mapping to access the database.
 *
 *  @author Tomáš Kovařík (database parts), Erik Kratochvíl (rmi parts)
 *  @version far from ready
 */
public class HibernateDBLayer implements DBLayer, Unreferenced {
    /** Instance of a logger */
    private Logger logger;
    /** Configuration file for Hibernate */
    private File configFile;   
    /** Hibernate session */
    private Session session;
    /** Pool of select queries */        
    private Hashtable<Integer, ScrollableResults> results;
    /** Maximum result ID used */
    private int maxResultId;
    
    /** Creates a new instance of HibernateDBLayer.
     * 
     *  @param undertaker The object that is responsible for cleanup if the client crashes. 
     */
    public HibernateDBLayer(Undertaker undertaker) {
    	this();
    	this.undertaker = undertaker; 
    }
    
    /** Creates a new instance of HibernateDBLayer */
    public HibernateDBLayer() {
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        // Initialize pool of select queries, initial capacity = 8
        results = new Hashtable<Integer, ScrollableResults>(8); 
        // Initialize maximum result id
        maxResultId = 0;
    }    
    
    /**
     *  Initialize database connection. Fire up Hibernate and open a session.
     *  
     *  FIXME prepracovat initialize tak, aby pouzival zaslane informace & nacitala prava!
     *  
     *  @throws DBLayerException when the hibernate or database connection cannot be initialized
     */
    public void initialize(String dbID, String user, String password) throws DBLayerException {
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
    public Object[] more(int resultId, int from, int to) throws DBLayerException {    
        // Check validity of arguments
        if (from>to) {
            logger.error("Cannot read rows from "+from+" to "+to+" because from > to");
            throw new DBLayerException("Cannot read rows from "+from+" to "+to+" because from > to");
        }
        if (from < 1) {
            logger.error("Cannot read rows starting at the given index: "+from);
            throw new DBLayerException("Cannot read rows starting at the given index: "+from);            
        }
        // Get results for the given resultId
        ScrollableResults res = results.get(resultId);
        // Move ResultSet to the first row we want to read. In case we want to read the first row,
        // move the pointer before the first row, else move it to the given position
        try {
            if (from > 1) {
                res.setRowNumber(from-1);
            } else {
                res.beforeFirst();
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
                if (res.next()) {
                    data[i] = res.get();
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
    public Object[] next(int resultId) throws DBLayerException {
        // Get results for the given resultId
        ScrollableResults res = results.get(resultId);
        // In case no more rows are available, return null
        try {
            if (!res.next()) {
                return null;
            }
        } catch (HibernateException e) {
            logger.fatal("Database error occured");
            throw new DBLayerException("Database error occured");
        }        
        return res.get();        
    }
    
    public int getNumRows(int resultId) {
        // Get results for the given resultId
        ScrollableResults res = results.get(resultId);        
        int currentRow = res.getRowNumber();
        res.afterLast();
        int numRows = res.getRowNumber();
        res.setRowNumber(currentRow);
        return numRows;
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
        SelectQuery query = new SelectQueryImplementation(session.createCriteria(classname));
        // TODO Tady se objekt query zaregistruje a exportuje pro remote usage.
        
        
        return query;
    }    
    
    /**
     *  Execute constructed SELECT query. Only executes query, for retrieving results use next() and more()
     *
     *  @param query query we want to execute
     *  @throws DBLayerException when selecting records from the database fails
     */
    public int executeQuery(SelectQuery query) throws DBLayerException {
        Transaction tx = null;        
        ScrollableResults res;
        try {
            tx = session.beginTransaction();
            // Execute detached criteria query
            res = query.getCriteria().scroll();
            // Commit transaction
            tx.commit();                                      
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.fatal("Selecting records from the database failed. Details: "+e.getMessage());
            throw new DBLayerException("Selecting records from the database failed. Details: "+e.getMessage());
        }
        // Update current maximum result id and save the results
        maxResultId++;
        results.put(maxResultId, res);
        return maxResultId;
    }
    
    
    
    
    
    
    //===============================================================
    // What happens to unreferenced objects? They get buried by the untertaker!
    
	private Undertaker undertaker = null;
	public void unreferenced() { if(undertaker != null) undertaker.bury(this); }
	//===============================================================
	      
}