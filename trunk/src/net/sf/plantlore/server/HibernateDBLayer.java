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
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.HistoryChange;
import net.sf.plantlore.common.record.HistoryColumn;
import net.sf.plantlore.common.record.HistoryRecord;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.common.record.Village;
import org.apache.log4j.Logger;
import net.sf.plantlore.common.exception.DBLayerException;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;


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

    private Session txSession;
    private Transaction longTx;            
            
    private static final int INITIAL_POOL_SIZE = 8;
    private static final int INSERT = 1;
    private static final int DELETE = 2;
    private static final int UPDATE = 3;
    
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
    public Object[] initialize(String dbID, String user, String password) throws DBLayerException, RemoteException {
        Configuration cfg;
        int result = 0;
        
        // File containing Hibernate configuration
        configFile = new File("hibernate.cfg.xml");        
        // Load Hibernate configuration
        try {
            cfg = new Configuration().configure(configFile);
        } catch (HibernateException e) {
            logger.fatal("Cannot load Hibernate configuration. Details: "+e.getMessage());
            DBLayerException ex = new DBLayerException("Cannot load Hibernate configuration. Details: "+e.getMessage());
            ex.setError(ex.ERROR_LOAD_CONFIG, null);
            throw ex;
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
            DBLayerException ex = new DBLayerException("Cannot build Hibernate session factory. Details: "+e.getMessage());
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
        }   
        
        // Authenticate user
        try {
            SelectQuery sq = this.createQuery(User.class);            
            sq.addRestriction(PlantloreConstants.RESTR_EQ, User.LOGIN, null, user, null);
            // TODO: Password should probably be encrypted
            // sq.addRestriction(PlantloreConstants.RESTR_EQ, User.PASSWORD, null, password, null);
            sq.addRestriction(PlantloreConstants.RESTR_IS_NULL, User.DROPWHEN, null, null, null);
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
            DBLayerException ex = new DBLayerException("Authentication of user "+user+" failed!");
            ex.setError(ex.ERROR_LOGIN, null);
            throw ex;
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
    public int executeInsert(Object data) throws DBLayerException, RemoteException {
        int recordId, id, result = 0;        
        String table;
        HistoryColumn column;
        
        // Check whether we are connected to the database
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable. Not connected to the database.");
            DBLayerException ex = new DBLayerException("SessionFactory not available. Not connected to the database.");
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
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
                    DBLayerException ex = new DBLayerException("tHistoryColumn doesn't contain required data");
                    ex.setError(ex.ERROR_DB, PlantloreConstants.ENTITY_HISTORYCOLUMN);
                    throw ex;
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
            DBLayerException ex = new DBLayerException("Saving record into the database failed. Details: "+e.getMessage());
            ex.setError(ex.ERROR_SAVE, null);
            throw ex;            
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
    public int executeInsertHistory(Object data) throws DBLayerException, RemoteException {
        int recordId;        

        // Check whether we are connected to the database
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable. Not connected to the database.");
            DBLayerException ex = new DBLayerException("SessionFactory not available. Not connected to the database.");
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
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
            DBLayerException ex = new DBLayerException("Saving record into the database failed. Details: "+e.getMessage());
            ex.setError(ex.ERROR_SAVE, null);
            throw ex;            
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
    public void executeDelete(Object data) throws DBLayerException, RemoteException {
        String table;
        int id, result = 0;
        HistoryColumn column;        

        // Check whether we are connected to the database
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable. Not connected to the database.");
            DBLayerException ex = new DBLayerException("SessionFactory not available. Not connected to the database.");
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
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
                    DBLayerException ex = new DBLayerException("tHistoryColumn doesn't contain required data");
                    ex.setError(ex.ERROR_DB, PlantloreConstants.ENTITY_HISTORYCOLUMN);
                    throw ex;
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
            DBLayerException ex = new DBLayerException("Deleting record from the database failed. Details: "+e.getMessage());
            ex.setError(ex.ERROR_DELETE, null);
            throw ex;            
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
    public void executeDeleteHistory(Object data) throws DBLayerException, RemoteException {
        // Check whether we are connected to the database
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable. Not connected to the database.");
            DBLayerException ex = new DBLayerException("SessionFactory not available. Not connected to the database.");
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
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
            DBLayerException ex = new DBLayerException("Deleting record from the database failed. Details: "+e.getMessage());
            ex.setError(ex.ERROR_DELETE, null);
            throw ex;
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
    public void executeUpdate(Object data) throws DBLayerException, RemoteException {
        int id;
        
        // Check whether we are connected to the database
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable. Not connected to the database.");
            DBLayerException ex = new DBLayerException("SessionFactory not available. Not connected to the database.");
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
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
            DBLayerException ex = new DBLayerException("Updating record in the database failed. Details: "+e.getMessage());
            ex.setError(ex.ERROR_UPDATE, null);
            throw ex;            
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
    public void executeUpdateHistory(Object data) throws DBLayerException, RemoteException {
        // Check whether we are connected to the database
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable. Not connected to the database.");
            DBLayerException ex = new DBLayerException("SessionFactory not available. Not connected to the database.");
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
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
            DBLayerException ex = new DBLayerException("Updating record in the database failed. Details: "+e.getMessage());
            ex.setError(ex.ERROR_UPDATE, null);
            throw ex;
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
    public Object[] more(int resultId, int from, int to) throws DBLayerException, RemoteException {
        // Check validity of arguments
        if (from>to) {
            logger.error("Cannot read rows from "+from+" to "+to+" because from > to");            
            DBLayerException ex = new DBLayerException("Cannot read rows from "+from+" to "+to+" because from > to");
            ex.setError(ex.ERROR_OTHER, null);
            throw ex;
        } 
        if (from < 0) {
            logger.error("Cannot read rows starting at the given index: "+from);
            DBLayerException ex = new DBLayerException("Cannot read rows starting at the given index: "+from);
            ex.setError(ex.ERROR_OTHER, null);
            throw ex;
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
            DBLayerException ex = new DBLayerException("Cannot move to the given row of results: "+from);
            ex.setError(ex.ERROR_OTHER, null);
            throw ex;            
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
            DBLayerException ex = new DBLayerException("Cannot read data from the results");
            ex.setError(ex.ERROR_LOAD_DATA, null);
            throw ex;            
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
    public Object[] next(int resultId) throws DBLayerException, RemoteException {
        // Get results for the given resultId
        ScrollableResults res = results.get(resultId);
        // In case no more rows are available, return null
        try {
            if (!res.next()) {
                return null;
            }
        } catch (HibernateException e) {
            logger.fatal("Database error occured");
            DBLayerException ex = new DBLayerException("Database error occured");
            ex.setError(ex.ERROR_OTHER, null);
            throw ex;            
        }        
        return res.get();        
    }
    
    /**
     *  Get the number of rows returned in the result.
     *
     *  @param  resultId id of the result we want the number of rows for
     *  @return number of rows in the given result
     */
    public int getNumRows(int resultId) throws RemoteException {
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
    public void close() throws DBLayerException, RemoteException {    
        // Check whether we are connected to the database
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable. Not connected to the database.");
            DBLayerException ex = new DBLayerException("SessionFactory not available. Not connected to the database.");
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
        }
        try {
            sessionFactory.close();
        } catch (HibernateException e) {
            logger.fatal("Cannot close session factory");
            DBLayerException ex = new DBLayerException("Cannot close session factory");
            ex.setError(ex.ERROR_CLOSE, null);
            throw ex;            
        }
    }
    
    /**
     *  Start building a select query.
     *
     *  @param classname entity we want to select from the database (given holder object class)
     *  @return an instance of <code>SelectQuery</code> used for building a query by client
     *
     */
    public SelectQuery createQuery(Class classname) throws DBLayerException, RemoteException {
        // Check whether we are connected to the database
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable. Not connected to the database.");
            DBLayerException ex = new DBLayerException("SessionFactory not available. Not connected to the database.");
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
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
    public int executeQuery(SelectQuery query) throws DBLayerException, RemoteException {

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
            System.out.println(sq.getCriteria().toString());            
            res = sq.getCriteria().scroll(); // retrieve Criteria from SelectQuery
            // Commit transaction
            tx.commit();                                      
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.fatal("Selecting records from the database failed. Details: "+e.getMessage());
            DBLayerException ex = new DBLayerException("Selecting records from the database failed. Details: "+e.getMessage());
            ex.setError(ex.ERROR_SELECT, null);
            throw ex;            
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
    public int conditionDelete(Class tableClass, String column, String operation, Object value) throws DBLayerException, RemoteException {
        String tableName;
        int deletedEntities = 0;
        
        // Check whether we are connected to the database
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable. Not connected to the database.");
            DBLayerException ex = new DBLayerException("SessionFactory not available. Not connected to the database.");
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
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
            DBLayerException ex = new DBLayerException("Cannot execute conditional delete on table "+tableClass.getName());
            ex.setError(ex.ERROR_DELETE, tableClass.getName());
            throw ex;
        }
        return deletedEntities;
    }
    
    /**
     *  Close the select query.
     *
     *  @param query query we want to close
     */
    public void closeQuery(SelectQuery query) throws RemoteException {
        Session session = sessions.get(query);
        session.close();
        sessions.remove(query);
    }
    
    /**
     *  Method to get the currently logged user. Returns null if there is no user logged in.
     *  @return currently logged in user or null, if there is no user logged in.
     */
    public User getUser() throws RemoteException {
        return this.plantloreUser;
    }
    
    /**
     *  Method to get the rights of the currently logged in user. Returns null if there is no user logged in
     *  @return rights of the currently logged in user or null if there is no user logged in.
     */
    public Right getUserRights() throws RemoteException {
        return this.rights;
    }

    public boolean beginTransaction() throws DBLayerException, RemoteException {
        // Check whether we are connected to the database
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable. Not connected to the database.");
            DBLayerException ex = new DBLayerException("SessionFactory not available. Not connected to the database.");
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
        }
        // Check whether there is some other running transaction
        if (this.longTx != null) {
            return false;               // Return with an error indicating other transaction is running
        }
        // Open new session
        this.txSession = sessionFactory.openSession();
        // Begin new transaction
        this.longTx = this.txSession.beginTransaction();        
        return true;                    // Transaction succesfully started
    }
    
    public boolean commitTransaction() throws DBLayerException, RemoteException {
        // Check whether we are connected to the database
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable. Not connected to the database.");
            DBLayerException ex = new DBLayerException("SessionFactory not available. Not connected to the database.");
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
        }
        // Check whether we have an opened session and running transaction
        if ((this.longTx == null) || (this.txSession == null)) {
            return false;               // Return with an error indicating we don't have proper conditions
        }
        // Commit the transaction
        this.longTx.commit();
        return true;
    }
    
    public boolean rollbackTransaction() throws DBLayerException, RemoteException {
        // Check whether we are connected to the database
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable. Not connected to the database.");
            DBLayerException ex = new DBLayerException("SessionFactory not available. Not connected to the database.");
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
        }
        // Check whether we have an opened session and running transaction
        if ((this.longTx == null) || (this.txSession == null)) {
            return false;               // Return with an error indicating we don't have proper conditions
        }
        // Rollback the transaction
        this.longTx.rollback();
        return true;        
    }
    
    public int executeInsertInTransaction(Object data) throws DBLayerException, RemoteException {
        int recordId;
        
        // Check whether we are connected to the database
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable. Not connected to the database.");
            DBLayerException ex = new DBLayerException("SessionFactory not available. Not connected to the database.");
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
        }
        // Check whether we have rights for this operation
        checkRights(data, INSERT);
        // Save item into the database
        recordId = (Integer)this.txSession.save(data);            
        // Save data to history tables - only for selected tables
        saveHistory(txSession, data, INSERT, recordId);
        // Return new record identifier
        return recordId;        
    }
    
    public void executeUpdateInTransaction(Object data) throws DBLayerException, RemoteException {
        // Check whether we are connected to the database
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable. Not connected to the database.");
            DBLayerException ex = new DBLayerException("SessionFactory not available. Not connected to the database.");
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
        }
        // Check whether we have rights for this operation
        checkRights(data, UPDATE);
        // Save history record for this change
        saveHistory(txSession, data, UPDATE, null);
        // Save item into the database
        txSession.update(data);
    }
    
    public void executeDeleteInTransaction(Object data) throws DBLayerException, RemoteException {
        // Check whether we are connected to the database
        if (sessionFactory == null) {
            logger.warn("SessionFactory not avilable. Not connected to the database.");
            DBLayerException ex = new DBLayerException("SessionFactory not available. Not connected to the database.");
            ex.setError(ex.ERROR_CONNECT, null);
            throw ex;
        }
        // Check whether we have rights for this operation
        checkRights(data, DELETE);
        // Save history record for this change
        saveHistory(txSession, data, DELETE, null);
        // Save item into the database
        txSession.delete(data);
    }    
    
    private void checkRights(Object data, int type) throws DBLayerException {
        DBLayerException ex;
        Session sess;
        
        // Check rights for table TAUTHORS
        if (data instanceof Author) {
            if ((type == DELETE) || (type == UPDATE)) {
                // Only data of the user and those listed in CEDITGROUP
                sess = this.sessionFactory.openSession();
                ScrollableResults sc = sess.createCriteria(Author.class)
                    .add(Restrictions.eq(Author.ID, ((Author)data).getId()))
                    .scroll();
                // If we haven't found the author in the database, raise exception
                if (sc.next()) {
                    logger.error("To-be-updated/deleted author not found in the database. Author ID:"+((Author)data).getId());
                    ex = new DBLayerException("To-be-updated/deleted author not found in the database. Author ID:"+((Author)data).getId());
                    ex.setError(ex.ERROR_OTHER, null);
                    throw ex;                                        
                }
                Object[] res = sc.get();
                Author aut = (Author)res[0];
                // Check for direct ownership first
                if (!aut.getCreatedWho().equals(this.plantloreUser)) {
                }
                // Then check for indirect (group) ownership
                String[] group = this.rights.getEditGroup().split(",");                    
                for (int i=0;i<group.length;i++) {
                    // if ()
                }
            }
            if (type == INSERT) {
                // Insert only if CADD = 1
                if (this.rights.getAdd() == 0) {
                    logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_AUTHOR);
                    ex = new DBLayerException("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_AUTHOR);
                    ex.setError(ex.ERROR_RIGHTS, PlantloreConstants.ENTITY_AUTHOR);
                    throw ex;                    
                }                
            }            
        }
        // Check rights for table TUSER
        if (data instanceof User) {
            // Only admin can insert/update/delete from this table
            if (this.rights.getAdministrator() != 1) {
                logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_USER);
                ex = new DBLayerException("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_USER);
                ex.setError(ex.ERROR_RIGHTS, PlantloreConstants.ENTITY_USER);
                throw ex;
            }
            // User can edit his own data except for login name
            
        }
        // Check rights for table TRIGHT, TPHYTOCHORIA, TVILLAGES, TTERRITORIES, TPLANTS, TMETADATA
        if ((data instanceof Right) || (data instanceof Phytochorion) ||
            (data instanceof Village) || (data instanceof Territory) ||
            (data instanceof Plant) || (data instanceof Metadata)) {
            String entity = "";
            if (data instanceof Right) { entity = PlantloreConstants.ENTITY_RIGHT; }
            if (data instanceof Phytochorion) { entity = PlantloreConstants.ENTITY_PHYTOCHORION; }
            if (data instanceof Village) { entity = PlantloreConstants.ENTITY_VILLAGE; }
            if (data instanceof Territory) { entity = PlantloreConstants.ENTITY_TERRITORY; }
            if (data instanceof Plant) { entity = PlantloreConstants.ENTITY_PLANT; }            
            if (data instanceof Metadata) { entity = PlantloreConstants.ENTITY_METADATA; }
            // Only admin can insert/update/delete from this table
            if (this.rights.getAdministrator() != 1) {
                logger.warn("User doesn't have sufficient rights for this operation. Entity: "+entity);
                ex = new DBLayerException("User doesn't have sufficient rights for this operation. Entity: "+entity);
                ex.setError(ex.ERROR_RIGHTS, entity);
                throw ex;
            }            
        }
        // Check rights for table THISTORYCOLUMN
        if (data instanceof HistoryColumn) {
            logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_HISTORYCOLUMN);
            ex = new DBLayerException("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_HISTORYCOLUMN);
            ex.setError(ex.ERROR_RIGHTS, PlantloreConstants.ENTITY_HISTORYCOLUMN);
            throw ex;            
        }
        // Check rights for table THISTORY and THISTORYCHANGE
        if ((data instanceof HistoryChange) || (data instanceof HistoryRecord)) {
            if ((type == INSERT) || (type == UPDATE)) {
                String entity = "";
                if (data instanceof HistoryChange) { entity = PlantloreConstants.ENTITY_HISTORYCHANGE; }
                if (data instanceof HistoryRecord) { entity = PlantloreConstants.ENTITY_HISTORYRECORD; }                
                logger.warn("User doesn't have sufficient rights for this operation. Entity: "+entity);
                ex = new DBLayerException("User doesn't have sufficient rights for this operation. Entity: "+entity);
                ex.setError(ex.ERROR_RIGHTS, entity);
                throw ex;                            
            } else if (type == DELETE) {
                // Tu to bude zlozitejsie...
            }           
        }        
        // Check rights for table TPUBLICATIONS
        if (data instanceof Publication) {
            if ((type == DELETE) || (type == UPDATE)) {
                // Only data of the user and those listed in CEDITGROUP
            }
            if (type == INSERT) {
                // Only if CADD = 1
            }                        
        }
        // Check rights for table TOCCURRENCES
        if (data instanceof Occurrence) {
           
        }
        // Check rights for table THABITATS
        if (data instanceof Habitat) {
            
        }
        // Check rights for table TAUTHORSOCCURRENCES
        if (data instanceof AuthorOccurrence) {
            
        }        
    }
    
    private void saveHistory(Session sess, Object data, int type, Integer recordId) throws DBLayerException {
        
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
