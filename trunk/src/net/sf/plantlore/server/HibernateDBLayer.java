/*
 * HibernateDBLayer.java
 *
 * Created on April 18, 2006, 22:31
 *
 */

package net.sf.plantlore.server;

import java.io.File;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.Hashtable;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.HistoryChange;
import net.sf.plantlore.common.record.HistoryColumn;
import net.sf.plantlore.common.record.HistoryRecord;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.common.record.Village;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;

import org.hibernate.Transaction;


/**
 *  Implementation of DBLayer using Hibernate OR mapping to access the database.
 *  
 *  TODO: Nezapominat generovat stub! (rmic net.sf.plantlore.server.HibernateDBLayer)
 *
 *  @author Tomas Kovarik (database parts), Erik Kratochvil (rmi parts)
 *  @version far from ready!
 */
public class HibernateDBLayer implements DBLayer, Unreferenced {
    /** Instance of a logger */
    private Logger logger;
    /** Configuration file for Hibernate */
    private File configFile;
    /** Pool of select queries */        
    private Hashtable<Integer, ScrollableResults> results;
    /** Maximum result ID used */
    private int maxResultId;
    
    private int maxSessionId;
    /** Session factory for creating Hibernate sessions */
    private SessionFactory sessionFactory;    
    /** List of select queries */
    private Hashtable<SelectQuery, SelectQuery> queries;    
    
    private Hashtable<SelectQuery, Session> sessions;
    /** Authenticated user */
    private User plantloreUser;
    /** Rights of the authenticated user */
    private Right rights;    

    private static final int INITIAL_POOL_SIZE = 8;
    
    /**
     * Creates a new instance of HibernateDBLayer.
     * 
     *  @param undertaker The object that is responsible for cleanup if the client crashes. 
     */
    public HibernateDBLayer(Undertaker undertaker) {
    	this();
    	this.undertaker = undertaker;
    	logger.debug("      completely completed.");
    }
    
    /** Creates a new instance of HibernateDBLayer */
    public HibernateDBLayer() {
        logger = Logger.getLogger(this.getClass().getPackage().getName());                
        logger.debug("      Constructing a new HibernateDBLayer ...");        
        // Initialize pool of result sets, initial capacity = INITIAL POOL SIZE
        results = new Hashtable<Integer, ScrollableResults>(INITIAL_POOL_SIZE); 
        // Initialize maximum result id
        maxResultId = 0;        
        maxSessionId = 0;
        // Table of all living queries, initial capacity = INITIAL_POOL_SIZE
        queries = new Hashtable<SelectQuery, SelectQuery>(INITIAL_POOL_SIZE);        
        sessions = new Hashtable<SelectQuery, Session>(INITIAL_POOL_SIZE);
        logger.debug("      completed.");
    }
    
    /**
     *  Initialize database connection. Fire up Hibernate and open a session.
     *  Authenticate user and Load rights of this user
     *
     *  @param dbID identifier of the database we want to connect to
     *  @param user username for the access to plantlore on the server
     *  @param password password for the access to plantlore on the server
     *  @return array with two objects - User object with logged in user (index 0) and user's rights 
     *          (Right object, index 1)
     *  @throws DBLayerException when the hibernate or database connection cannot be initialized
     */
    public Object[] initialize(String dbID, String user, String password) throws DBLayerException {
        Configuration cfg;
        int result = 0;
        
        // File containing Hibernate configuration
        configFile = new File("hibernate.cfg.xml");        
        // Load Hibernate configuration
        try {
            cfg = new Configuration().configure(configFile);
        } catch (HibernateException e) {
            logger.fatal("Cannot load Hibernate configuration. Details: "+e.getMessage());
            throw new DBLayerException("Cannot load Hibernate configuration. Details: "+e.getMessage());            
        }
        // TODO: this should be loaded from a configuration file on the server
        // We are temporarily using this for DB authetication and user athentication as well
        cfg.setProperty("hibernate.connection.url", dbID);
        cfg.setProperty("hibernate.connection.username", user);
        cfg.setProperty("hibernate.connection.password", password);        
        try {
            // Build session factory
            sessionFactory = cfg.buildSessionFactory();
        } catch (HibernateException e) {
            logger.fatal("Cannot build Hibernate session factory. Details: "+e.getMessage());
            throw new DBLayerException("Cannot build Hibernate session factory. Details: "+e.getMessage());
        }   
        
        // Authenticate user
        try {
            SelectQuery sq = this.createQuery(User.class);            
            sq.addRestriction(PlantloreConstants.RESTR_EQ, User.LOGIN, null, user, null);
            result = this.executeQuery(sq);
        } catch (RemoteException e) {
            logger.fatal("Cannot load user information. Details: "+e.getMessage());
        }
        Object[] userinfo = next(result);
        if (userinfo == null) {
            // Authentication failed, close DB connection
            sessionFactory.close();
            sessionFactory = null;
            logger.warn("Authentication of user "+user+" failed!");
            return null;
        } else {
            User clientUser = (User)userinfo[0];            
            this.rights = clientUser.getRight();           
            this.plantloreUser = clientUser;
        }
        Object[] retValue = new Object[2];
        retValue[0] = this.plantloreUser;
        retValue[1] = this.rights;
        return retValue;
    }    
    
    /**
     *  Insert data into the database.
     *
     *  @param data data to insert (one of the data holder objects)
     *  @return identifier (primary key) of the inserted row
     *  @throws DBLayerException when saving data into the database fails
     */
    public int executeInsert(Object data) throws DBLayerException {
        int recordId, id, result = 0;        
        String table;
        HistoryColumn column;
        
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable");
            throw new DBLayerException("SessionFactory not available");
        }        
        Session session = sessionFactory.openSession();
        Transaction tx = null;                
        try {
            // Begin transaction
            tx = session.beginTransaction();            
            // Save item into the database
            recordId = (Integer)session.save(data);            
            // Save data to history tables - only for selected tables
            if ((data instanceof Publication) || (data instanceof Territory) ||
                (data instanceof Village) || (data instanceof Phytochorion) ||
                (data instanceof Author) || (data instanceof Occurrence)) {
                HistoryChange historyChange = new HistoryChange();
                if (data instanceof Occurrence) {
                    historyChange.setOccurrence((Occurrence)data);
                    historyChange.setRecordId(0);
                    table = PlantloreConstants.ENTITY_OCCURRENCE;
                } else {
                    historyChange.setOccurrence(null);
                    if (data instanceof Publication) {
                        table = PlantloreConstants.ENTITY_PUBLICATION;
                    } else if (data instanceof Territory) {
                        table = PlantloreConstants.ENTITY_TERRITORY;                        
                    } else if (data instanceof Village) {
                        table = PlantloreConstants.ENTITY_VILLAGE;                        
                    } else if (data instanceof Phytochorion) {
                        table = PlantloreConstants.ENTITY_PHYTOCHORION;                        
                    } else if (data instanceof Author) {
                        table = PlantloreConstants.ENTITY_AUTHOR;                        
                    } else {
                        table = "";
                    }
                    historyChange.setRecordId(recordId);
                }
                historyChange.setOldRecordId(0);
                historyChange.setOperation(PlantloreConstants.INSERT);
                historyChange.setWho(this.plantloreUser);
                historyChange.setWhen(new java.util.Date());
                
                // Load record from THistoryColumn table
                try {
                    SelectQuery sq = this.createQuery(HistoryColumn.class);
                    sq.addRestriction(PlantloreConstants.RESTR_EQ, HistoryColumn.TABLENAME, null, table, null);
                    sq.addRestriction(PlantloreConstants.RESTR_IS_NULL, HistoryColumn.COLUMNNAME, null, null, null);
                    result = this.executeQuery(sq);
                } catch (RemoteException e) {
                    logger.fatal("Cannot load HistoryChange information. Details: "+e.getMessage());
                }
                Object[] objCol = next(result);
                if (objCol == null) {                
                    logger.error("tHistoryColumn doesn't contain required data");
                    throw new DBLayerException("tHistoryColumn doesn't contain required data");                    
                } else {
                    column = (HistoryColumn)objCol[0];
                }                
                HistoryRecord history = new HistoryRecord();
                history.setHistoryColumn(column);
                history.setNewValue(null);
                history.setOldValue(null);
                // Save into the database
                recordId = (Integer)session.save(historyChange);
                history.setHistoryChange(historyChange);                
                recordId = (Integer)session.save(history);                
            }
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
     *  Insert data into the database without modifying history tables
     *
     *  @param data data to insert (one of the data holder objects)
     *  @return identifier (primary key) of the inserted row
     *  @throws DBLayerException when saving data into the database fails
     */
    public int executeInsertHistory(Object data) throws DBLayerException {
        int recordId;        
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable");
            throw new DBLayerException("SessionFactory not available");
        }        
        Session session = sessionFactory.openSession();
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
        String table;
        int id, result = 0;
        HistoryColumn column;        
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable");
            throw new DBLayerException("SessionFactory not available");
        }        
        Session session = sessionFactory.openSession();        
        Transaction tx = null;
        // Save records to history if required
        try {        
            if ((data instanceof Publication) || (data instanceof Author) || (data instanceof Occurrence)) {
                HistoryChange historyChange = new HistoryChange();
                if (data instanceof Occurrence) {
                    historyChange.setOccurrence((Occurrence)data);
                    historyChange.setRecordId(0);
                    table = PlantloreConstants.ENTITY_OCCURRENCE;
                } else {
                    historyChange.setOccurrence(null);
                    if (data instanceof Publication) {
                        id = ((Publication)data).getId();
                        table = PlantloreConstants.ENTITY_PUBLICATION;
                    } else if (data instanceof Author) {
                        id = ((Author)data).getId();
                        table = PlantloreConstants.ENTITY_AUTHOR;                        
                    } else {
                        id = 0;
                        table = "";
                    }
                    historyChange.setRecordId(id);
                }
                historyChange.setOldRecordId(0);
                historyChange.setOperation(PlantloreConstants.DELETE);
                historyChange.setWho(this.plantloreUser);
                historyChange.setWhen(new java.util.Date());
                
                // Load record from THistoryColumn table
                try {
                    SelectQuery sq = this.createQuery(HistoryColumn.class);
                    sq.addRestriction(PlantloreConstants.RESTR_EQ, HistoryColumn.TABLENAME, null, table, null);
                    sq.addRestriction(PlantloreConstants.RESTR_IS_NULL, HistoryColumn.COLUMNNAME, null, null, null);
                    result = this.executeQuery(sq);
                } catch (RemoteException e) {
                    logger.fatal("Cannot load HistoryChange information. Details: "+e.getMessage());
                }
                Object[] objCol = next(result);
                if (objCol == null) {                
                    logger.error("tHistoryColumn doesn't contain required data");
                    throw new DBLayerException("tHistoryColumn doesn't contain required data");                    
                } else {
                    column = (HistoryColumn)objCol[0];
                }                
                HistoryRecord history = new HistoryRecord();
                history.setHistoryChange(historyChange);
                history.setHistoryColumn(column);
                history.setNewValue(null);
                history.setOldValue(null);
                // Save into the database
                session.save(historyChange);
                session.save(history);                
            }        
            // Save the data itself
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
     *  Delete data from the database without modifying history tables
     *
     *  @param data data we want to delete (must be one of the holder objects)
     *  @throws DBLayerException when deleting data fails
     */
    public void executeDeleteHistory(Object data) throws DBLayerException {
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable");
            throw new DBLayerException("SessionFactory not available");
        }
        Session session = sessionFactory.openSession();        
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
        int id;
        
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable");
            throw new DBLayerException("SessionFactory not available");
        }
        Session session = sessionFactory.openSession();
        Transaction tx = null;        
        try {
            tx = session.beginTransaction();            
/*            
            // Save data into history tables if required
            if ((data instanceof Occurrence) || (data instanceof Author) ||
                (data instanceof Publication) || (data instanceof Territory) ||
                (data instanceof Village) || (data instanceof Phytochorion)) {
                
                HistoryChange historyChange = new HistoryChange();            
                historyChange.setOperation(PlantloreConstants.UPDATE);
                historyChange.setWhen(new java.util.Date());
                historyChange.setWho(this.plantloreUser);
                if (data instanceof Occurrence) {
                    historyChange.setOccurrence((Occurrence)data);
                    
                    id = ((Publication)data).getId();                    
                } else {
                    historyChange.setOccurrence(null);
                    historyChange.setOldRecordId(0);
                    if (data instanceof Publication) {
                        id = ((Publication)data).getId();
                    } else if (data instanceof Author) {
                        id = ((Author)data).getId();
                    } else if (data instanceof Territory) {
                        id = ((Territory)data).getId();                        
                    } else if (data instanceof Village) {
                        id = ((Village)data).getId();
                    } else if (data instanceof Phytochorion) {
                        id = ((Phytochorion)data).getId();
                    } else {
                        id = 0;
                    }
                    historyChange.setRecordId(id);
                }                
            }
*/            
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
     *  Update data in the database without modifying history tables.
     *
     *  @param data to update (must be one of the holder objects)
     *  @throws DBLayerException when updating data fails
     */
    public void executeUpdateHistory(Object data) throws DBLayerException {
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable");
            throw new DBLayerException("SessionFactory not available");
        }
        Session session = sessionFactory.openSession();
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
     *  @param resultId id of the result from which we want to read
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
        if (from < 0) {
            logger.error("Cannot read rows starting at the given index: "+from);
            throw new DBLayerException("Cannot read rows starting at the given index: "+from);            
        }
        // Get results for the given resultId
        ScrollableResults res = results.get(resultId);
        // Move ResultSet to the first row we want to read. In case we want to read the first row,
        // move the pointer before the first row, else move it to the given position
        try {
            if (from >= 1) {
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
            System.out.println("to-from = "+(to-from));
            for (int i=0; i<=(to-from); i++) {
                logger.debug("About to get result number "+i);
                if (res.next()) {
                    data[i] = res.get();
                } else {
                    logger.debug("res.get() would return: "+res.get());
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
     *  @param resultId id of the result from which we want to read
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
    
    /**
     *  Get the number of rows returned in the result.
     *
     *  @param  resultId id of the result we want the number of rows for
     *  @return number of rows in the given result
     */
    public int getNumRows(int resultId) {
        int numRows;
                
        // Get results for the given resultId
        ScrollableResults res = results.get(resultId);        
        int currentRow = res.getRowNumber();        
        res.afterLast();
        if (res.getRowNumber() != currentRow) {
            numRows = res.getRowNumber();
            res.setRowNumber(currentRow);
        } else {
            numRows = 0;
        }
        return numRows;
    }
    
    /**
     *  Close the DBLayer. Close the session factory
     *
     *  @throws DBLayerException when closing session fails
     */
    public void close() throws DBLayerException {    
        if (sessionFactory == null) {
            logger.warn("SessionFactory not available");
            throw new DBLayerException("SessionFactory not available");
        }        
        try {
            sessionFactory.close();
        } catch (HibernateException e) {
            logger.fatal("Cannot close session factory");
            throw new DBLayerException("Cannot close session factory");            
        }
    }
    
    /**
     *  Start building a select query.
     *
     *  @param classname entity we want to select from the database (given holder object class)
     *  @return an instance of <code>SelectQuery</code> used for building a query by client
     *
     */
    // TODO: Pridat throws DBLayerException
    public SelectQuery createQuery(Class classname) throws RemoteException {
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable");
            // throw new DBLayerException("SessionFactory not available");
        }
        Session session = sessionFactory.openSession();
        SelectQuery query = new SelectQueryImplementation(session.createCriteria(classname)), 
        	stub = query;
        
        if(undertaker != null)
        	stub = (SelectQuery) UnicastRemoteObject.exportObject(query); 
        
        queries.put(stub, query);
        sessions.put(stub, session);
        return stub;
    }    
    
    /**
     *  Execute constructed SELECT query. Only executes query, for retrieving results use next() and more()
     *
     *  @param query query we want to execute
     *  @throws DBLayerException when selecting records from the database fails
     */
    public int executeQuery(SelectQuery query) throws DBLayerException {

    	SelectQuery selectQuery = queries.remove(query);
    	if(selectQuery == null) throw new DBLayerException("You can only pass queries created by this DBLayer!");
    	
    	if(undertaker != null) 
    		try { UnicastRemoteObject.unexportObject(selectQuery, true); }
                catch(NoSuchObjectException e) {}
    	
    	assert(selectQuery instanceof SelectQueryImplementation);
    	SelectQueryImplementation sq = (SelectQueryImplementation) selectQuery;
    	
    	if(sq == null) logger.fatal("Class cast failed. Why the fuck?!");
    	
        Transaction tx = null;        
        ScrollableResults res;        
        Session session = sessions.get(query);
        try {
            tx = session.beginTransaction();
            // Execute detached criteria query
            res = sq.getCriteria().scroll(); // retrieve Criteria from SelectQuery
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
    
    /**
     *  Execute SQL delete with condition. Only administrator should be allowed to run this.
     *  The method doesn't handle foreign key constraints
     *  
     *  @param tableClass class representing the table we want to delete data from
     *  @param column name of the column in the condition
     *  @param operation operation in the condition (must be SQL compatible, e.g. =, >, <)
     *  @param value value in the condition. Must be either String, Integer or Date
     *  @return number of rows deleted
     */
    public int conditionDelete(Class tableClass, String column, String operation, Object value) throws DBLayerException {
        String tableName;
        int deletedEntities = 0;
        
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable");
            // throw new DBLayerException("SessionFactory not available");
        }
        Transaction tx = null;
        try {
            Session session = sessionFactory.openSession();
            tx = session.beginTransaction();
            Query hqlQuery;
            String hqlDelete = "delete "+tableClass.getName(); 
            if (column != null) {
                hqlDelete += " where "+column+" "+operation+" :value";
                hqlQuery = session.createQuery(hqlDelete);
                if (value instanceof String) {                
                    hqlQuery.setString("value", ((String)value));
                } else if (value instanceof Integer) {
                    hqlQuery.setInteger("value", ((Integer)value));
                } else if (value instanceof java.util.Date) {
                    hqlQuery.setDate("value", ((java.util.Date)value));
                }
            } else {
                hqlQuery = session.createQuery(hqlDelete);            
            }
            deletedEntities = hqlQuery.executeUpdate();
            tx.commit();
            session.close();                    
        } catch (HibernateException e) {
            logger.fatal("Cannot execute conditional delete on table "+tableClass.getName());
            throw new DBLayerException("Cannot execute conditional delete on table "+tableClass.getName());
        }
        return deletedEntities;
    }
    
    /**
     *  Close the select query.
     *
     *  @param query query we want to close
     */
    public void closeQuery(SelectQuery query) {
        Session session = sessions.get(query);
        session.close();
        sessions.remove(query);
    }
    
    /**
     *  Method to get the currently logged user. Returns null if there is no user logged in.
     *  @return currently logged in user or null, if there is no user logged in.
     */
    public User getUser() {
        return this.plantloreUser;
    }
    
    /**
     *  Method to get the rights of the currently logged in user. Returns null if there is no user logged in
     *  @return rights of the currently logged in user or null if there is no user logged in.
     */
    public Right getUserRights() {
        return this.rights;
    }
    
    /**
     * This method is intended for final cleanup. <b>Do not call this method yourself!
     * The proper way for you to get rid of a DBLayer is to call DBLayer.destroy() method!</b>
     * <br/>
     * Terminate all processes running in this DBLayer,
     * disconnect from the database and 
     * destroy all objects created by this DBLayer.
     * <br/>
     * <b>After this the DBLayer will not be capable of carrying out its duties.</b>
     * <br/>
     * This method is supposed to be used by the DBLayerFactory exclusively.
     * 
     * FIXME Think of a better mechanism that will hide it from users yet keep it accessible to the DBLF.
     */
    public void shutdown() /* throws RemoteException */ {
    	
    	if(undertaker != null) 
    		for(SelectQuery sq : queries.values()) 
    			try { UnicastRemoteObject.unexportObject(sq, true); }
    			catch(NoSuchObjectException e) {}
    	queries.clear();
    	
    	//kovo by mel asi nejak poukoncovat otevreny vysledky
    	//for each unfinished (unclosed) result do close(result)  
    	
    }
    
    
    
    //===============================================================
    // What happens to unreferenced objects? They get buried by the untertaker!
    
    /** 
     * The object that is responsible for destroying the database in case all remote references
     * have been lost. This can happen if the client crashes or doesn't destroy its remote
     * DBLayers properly. 
     */
	private Undertaker undertaker = null; // sort of a callback here
	
	/**
	 * Make sure this instance of DBLayer will be a subject of a proper cleanup.<br/>
	 * This method is called by the RMI mechanism if all remote references of this
	 * object have been lost.
	 * 
	 * @see java.rmi.dgc.leaseValue
	 */
	public void unreferenced() { 
		if(undertaker != null) undertaker.bury(this); 
	}
	//===============================================================
	      
}
