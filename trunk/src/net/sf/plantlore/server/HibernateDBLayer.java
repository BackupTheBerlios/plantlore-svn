﻿
package net.sf.plantlore.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.UniqueIDGenerator;
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
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.UnitIdDatabase;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.common.record.NearestVillage;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;
import net.sf.plantlore.common.exception.DBLayerException;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.cfg.Configuration;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import java.util.ArrayList;

import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.JDBCConnectionException;


/**
 *  Implementation of DBLayer using Hibernate OR mapping to access the database.
 *  
 *  @author Tomas Kovarik (database parts), 
 *  @author kaimu (RMI parts, some code purification)
 *  @version almost complete!
 */
public class HibernateDBLayer implements DBLayer, Unreferenced {
	
    private static final long serialVersionUID = 2006060433222L;
	
    /** Instance of a logger */
    private Logger logger;
    /** Pool of select queries */        
    private Hashtable<Integer, ScrollableResults> results;
    /** Maximum result ID used */
    private int maxResultId;
    
    private int maxSessionId;
    /** Session factory for creating Hibernate sessions */
    private SessionFactory sessionFactory;
    
    /** 
     * List of select queries - velmi dulezity obekt, provadi preklad ze stubu, ktere nam muzou chodit
     * na odpovidajici remote objekty. TODO: Nekam to poznamenat a zdokumentovat! 
     */
    private Hashtable<SelectQuery, SelectQuery> queries;    
    
    private Hashtable<SelectQuery, Session> sessions;
    /** Authenticated user */
    private User plantloreUser;
    /** Rights of the authenticated user */
    private Right rights;    
    /** Unique identifier of the database we are connected to */
    private String databaseID;
     /** Default prefix of database username */
    private String userPrefix = "plantlore_";
    
    private Session txSession;
    private Transaction longTx;            
            
    private static final int INITIAL_POOL_SIZE = 8;
    private static final int INSERT = 1;
    private static final int UPDATE = 2;
    private static final int DELETE = 3;
    
    private DatabaseSettings settings;
    private String currentlyConnectedUser = "";         
        
    public HibernateDBLayer(DatabaseSettings settings) {
    	this(null, settings);
    }
   
    
    /**
     *  Creates a new instance of HibernateDBLayer.
     * 
     *  @param undertaker The object that is responsible for cleanup if the client crashes. 
     */
    public HibernateDBLayer(Undertaker undertaker, DatabaseSettings settings) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        logger.debug("Constructing a new HibernateDBLayer.");
        
        this.settings = settings;         
        
        // Initialize pool of result sets, initial capacity = INITIAL POOL SIZE
        results = new Hashtable<Integer, ScrollableResults>(INITIAL_POOL_SIZE); 
        // Initialize maximum result id
        maxResultId = 0;        
        maxSessionId = 0;
        // Table of all living queries, initial capacity = INITIAL_POOL_SIZE
        queries = new Hashtable<SelectQuery, SelectQuery>(INITIAL_POOL_SIZE);        
        sessions = new Hashtable<SelectQuery, Session>(INITIAL_POOL_SIZE);
        
        this.undertaker = undertaker;
        
        logger.debug("Construction of the HibernateDBLayer successful.");
    }
    
    /**
     * Set the language mutation in order to send messages in same the language
     * the (possibly remote) User is currently using on his machine.
     */
    public void setLanguage(String locale) 
    throws DBLayerException, RemoteException {
    	try {
    		logger.debug("Setting language according to " + locale);
    		L10n.load(locale);
    		logger.debug("Language set successfully.");
    	} catch(IOException e) {}
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
     *  @throws DBLayerException The following errors can occurr:
     *          ERROR_LOAD_CONFIG: Unable to load Hibernate configuration file
     *          ERROR_CONNECT: Unable to establish database connection (build SessionFactory)
     *          ERROR_SELECT: Unable to read contents of TUSER table
     *          ERROR_LOGIN: Authentication failed (wrong password, username or account disabled)
     */
    public User initialize(String dbID, String user, String password) 
    throws DBLayerException, RemoteException {
        Configuration cfg;
        currentlyConnectedUser = user;        
        userPrefix = dbID.toLowerCase() + "_";
        
        // Load Hibernate configuration
        try {
            cfg = new Configuration().configure(); //load the configuration from application's resource called hibernate.cfg.xml
        } catch (HibernateException e) {
            logger.fatal("Cannot load Hibernate configuration. Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Error.InvalidConfiguration"), DBLayerException.ERROR_LOAD_CONFIG, e);
        }
        // Create connections string from the provided data
        if( settings.getConnectionStringSuffix() == null || "".equals(settings.getConnectionStringSuffix()) )
        	cfg.setProperty("hibernate.connection.url", settings.getConnectionStringPrefix() + dbID);
        else
        	cfg.setProperty("hibernate.connection.url", settings.getConnectionStringPrefix() + dbID 
        			+ "?" + settings.getConnectionStringSuffix());
        // Set username and password to access database
        cfg.setProperty("hibernate.connection.username", this.userPrefix + user);
        cfg.setProperty("hibernate.connection.password", password);
        
        // Build session factory & create a new session.
        Session sess = null;
        // Authenticate user
        logger.debug("DBLayer Initialization - about to authenticate the user. user" + this.userPrefix + user);
        try {
        	logger.debug("DBLayer Initialization - building session factory.");
        	sessionFactory = cfg.buildSessionFactory();
        	logger.debug("DBLayer Initialization - opening new session.");
            sess = sessionFactory.openSession();
        	
            logger.debug("DBLayer Initialization - verifying existence of the user, part 1.");
            ScrollableResults sr = sess.createCriteria(User.class)
                .add(Restrictions.eq(User.LOGIN, user))
                .add(Restrictions.isNull(User.DROPWHEN))
                .scroll();
            
            logger.debug("DBLayer Initialization - verifying existence of the user, part 2.");

            if( ! sr.next() ) {
            	logger.debug("DBLayer Initialization - things are not going well. sr.get() returned null!");
            	throw new DBLayerException(L10n.getString("Error.MissingUserAccount"), DBLayerException.ERROR_USERNAME);
            }
            
            Object[] userinfo = sr.get();
            plantloreUser = (User)userinfo[0];
            rights = plantloreUser.getRight();
            // Load unique database identifier
            sr = sess.createCriteria(UnitIdDatabase.class).scroll();  
            if( ! sr.next() ) {
            /*
             *  Throw an exception and a message about missing unique ID identifier
             */
            } else {
                Object[] unitiddatabase = sr.get();
                this.databaseID = ((UnitIdDatabase)unitiddatabase[0]).getUnitIdDb();            
            }
            logger.debug("DBLayer Initialization - finished!");
        } 
        catch (JDBCException e) {
        	if(sessionFactory != null)
        		sessionFactory.close();
        	sessionFactory = null;
            logger.fatal("Cannot connect to the database. Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Error.ConnectionFailed"), e);
        }
        catch(DBLayerException e) {
        	if(sessionFactory != null)
        		sessionFactory.close();
        	sessionFactory = null;
            logger.fatal("Cannot connect to the database. Details: "+e.getMessage());
            throw e;
        }
        finally {
        	if(sess != null) sess.close();
        }
        
        logger.debug("User and rights ~ " + plantloreUser.toFullString() + " and " + rights.toFullString());
        
        // Return User and Right object with users details
        return plantloreUser;
    }
    
    
    public String getUniqueDatabaseIdentifier() {
        return this.databaseID;
    }
    
    /**
     *  Initialization of DBLayer when creating new DB (login without database prefix)
     *
     *  @param dbID database identifier - database we want to use
     *  @param user loginname of the user
     *  @param password password of the user
     */
    public void initializeNewDB(String dbID, String user, String password) throws DBLayerException, RemoteException {
        Configuration cfg;
        // Load Hibernate configuration
        try {
            cfg = new Configuration().configure(); //load the configuration from application's resource called hibernate.cfg.xml
        } catch (HibernateException e) {
            logger.fatal("Cannot load Hibernate configuration. Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Error.InvalidConfiguration"), DBLayerException.ERROR_LOAD_CONFIG, e);
        }
        // Create connections string from the provided data
        if( settings.getConnectionStringSuffix() == null || "".equals(settings.getConnectionStringSuffix()) )
        	cfg.setProperty("hibernate.connection.url", settings.getConnectionStringPrefix() + dbID);
        else
        	cfg.setProperty("hibernate.connection.url", settings.getConnectionStringPrefix() + dbID 
        			+ "?" + settings.getConnectionStringSuffix());
        // Set username and password to access database
        cfg.setProperty("hibernate.connection.username", user);            
        cfg.setProperty("hibernate.connection.password", password);
        try {
        	logger.debug("DBLayer Initialization - building session factory.");
        	sessionFactory = cfg.buildSessionFactory();
        } catch (HibernateException e) {
            logger.fatal("DBLayer initialization: Could not build session factory");
            throw new DBLayerException(L10n.getString("Error.ConnectionFailed"), DBLayerException.ERROR_CONNECT, e);
        }   
    }
    
    protected void touchRecord(Object record) {
    	java.util.Date now = new Date();
        if(record instanceof Occurrence) {
            Occurrence occ = (Occurrence)record;
            occ.setUpdatedWhen(now);
            occ.setUpdatedWho(this.plantloreUser);
        } else if(record instanceof Metadata) {
        	((Metadata)record).setDateModified(now);
        }
    }
        
    protected int getUniqueRecordId() throws DBLayerException {
        Session sess = sessionFactory.openSession();
        ScrollableResults sr = sess.createCriteria(Occurrence.class)
            .setProjection(Projections.max(Occurrence.UNITVALUE))
            .scroll();
        if (!sr.next()) {
            // No records in the database, set the first unitvalue to 1
            return 1;
        }        
        Object[] result = sr.get();
        sess.close();        
        // There is no occurrence in the database so far. Why the hell is result[0] null?
        if ((result != null) && (result[0] == null)) {
            return 1;
        }
        int maxUnitValue = (Integer)result[0];
        maxUnitValue++;
        return maxUnitValue;
    }
    
    protected void completeRecord(Object record) throws DBLayerException {
    	java.util.Date now = new Date();
        if(record instanceof Occurrence) {
            Occurrence occ = (Occurrence)record;
            occ.setCreatedWhen(now);
            occ.setCreatedWho(this.plantloreUser);
//            occ.getHabitat().setCreatedWho(this.plantloreUser);
            // Set UniqueID for the record and database
            occ.setUnitIdDb(this.databaseID);
            // Unique identifier of the record
            occ.setUnitValue(getUniqueRecordId());
        } else if(record instanceof Habitat) {
            ((Habitat)record).setCreatedWho(this.plantloreUser);
        } else if(record instanceof Publication) {
            ((Publication)record).setCreatedWho(this.plantloreUser);
        } else if(record instanceof Author) {
            ((Author)record).setCreatedWho(this.plantloreUser);
        } else if(record instanceof Metadata) {
            ((Metadata)record).setDateCreate(now);                
        } else if (record instanceof AuthorOccurrence) {
            AuthorOccurrence ao = (AuthorOccurrence)record;
//            ao.getOccurrence().setCreatedWhen(now);
//            ao.getOccurrence().setCreatedWho(this.plantloreUser);
//            ao.getOccurrence().setUpdatedWhen(now);
//            ao.getOccurrence().setUpdatedWho(this.plantloreUser);
//            ao.getOccurrence().getHabitat().setCreatedWho(this.plantloreUser);
            // Set UniqueID for the record and database
//            ao.getOccurrence().setUnitIdDb("aaa");
            //TODO zjiskat unikatni identifikator zaznamu !!!
//            ao.getOccurrence().setUnitValue(1);       
         }        
        touchRecord(record);
    }
    
    protected void checkConnection()
    throws DBLayerException {
    	if (sessionFactory == null) {
            logger.warn("SessionFactory not available. Not connected to the database. Must call initialize() prior to any other method!");
            throw new DBLayerException("Exception.NotConnected", DBLayerException.ERROR_CONNECT, null);
        }
    }       
    
    protected Record lowLevelOperation(int operation, Object data, boolean saveHistory, boolean useOwnTransaction) 
    throws DBLayerException, RemoteException {
    	 // Check whether the connection to the databse has been established.
        checkConnection();
        
        // INSERT: Fill in missing parts that DBLayer must complete.
        // UPDATE & DELETE: Update timestamps of the record.
        if(operation == INSERT) {
        	completeRecord(data);
        } else {
        	touchRecord(data);
        }
        // Check whether we have sufficient rights.
        checkRights(data, operation);
        // Perform the Operation.
        Transaction tx = null;

        // If we should not use our own transaction, we must use the stored one.
        Session session = useOwnTransaction ? sessionFactory.openSession() : this.txSession;
        int recordId = -1;
        Record record = null;
        try {            
            // Begin transaction, if it is required. If not, the `tx` stays null.
            if(useOwnTransaction) tx = session.beginTransaction();
            // Make changes in the database.
            switch(operation) {
                case INSERT:
                    recordId = (Integer)session.save(data);                    
                    if(saveHistory) saveHistory(session, data, INSERT, recordId);
                    record = (Record)data;
                    record.setId(recordId);
                    break;
                case UPDATE:
                    session.update(data);
                    if(saveHistory) saveHistory(session, data, UPDATE, null);
                    record = (Record)data;                    
                    break;
                case DELETE:
                    session.delete(data);
                    if(saveHistory) saveHistory(session, data, DELETE, null);
                    break;
                default:
                    throw new IllegalArgumentException(L10n.getString("Error.ImproperUse"));
            }

            // Commit transaction.
            if(useOwnTransaction) tx.commit();
            
        } 
        catch (StaleObjectStateException e) {
            if (tx != null) tx.rollback();
            logger.warn("StaleObjectStateException caught (Concurrent transactions running and trying to commit). Details: "+e.getMessage());
            throw new DBLayerException("Error.ConcurrentUpdate", DBLayerException.ERROR_TRANSACTION, e);
        } 
        catch (JDBCException e) {
            if (tx != null) tx.rollback();
            logger.fatal("JDBC Exception caught while saving the record into the database. SQL State: "+e.getSQLState()+"; Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Error.DatabaseOperationFailed"), e);
        } 
        catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.fatal("Saving record into the database failed. Details: "+e.getMessage());
            int exceptionType = -1;
            switch(operation) {
            case INSERT:
            	exceptionType = DBLayerException.ERROR_SAVE;
            	break;
            case UPDATE:
            	exceptionType = DBLayerException.ERROR_UPDATE;
            	break;
            case DELETE:
            	exceptionType = DBLayerException.ERROR_DELETE;
            	break;
            }
            throw new DBLayerException(L10n.getString("Error.DatabaseOperationFailed"), exceptionType, e);
        } finally {
            // We must close only our own sessions!
            if(useOwnTransaction) session.close();
        }
        return record;
    }
    
    
    /**
     *  Insert data into the database.
     *
     *  @param data data to insert (one of the data holder objects)
     *  @return inserted record including ID which was used as a primary key
     *  @throws DBLayerException when saving data into the database fails
     */
    public Record executeInsert(Object data) throws DBLayerException, RemoteException {
       return lowLevelOperation(INSERT, data, true, true);
    }

    /**
     *  Insert data into the database without modifying history tables
     *
     *  @param data data to insert (one of the data holder objects)
     *  @return inserted record including ID which was used as a primary key
     *  @throws DBLayerException when saving data into the database fails
     */
    public Record executeInsertHistory(Object data) throws DBLayerException, RemoteException {
      return lowLevelOperation(INSERT, data, false, true);  
    }
    
    /**
     *  Delete data from the database.
     *
     *  @param data data we want to delete (must be one of the holder objects)
     *  @throws DBLayerException when deleting data fails
     */
    public void executeDelete(Object data) throws DBLayerException, RemoteException {
        lowLevelOperation(DELETE, data, true, true);
    }

    /**
     *  Delete data from the database without modifying history tables
     *
     *  @param data data we want to delete (must be one of the holder objects)
     *  @throws DBLayerException when deleting data fails
     */
    public void executeDeleteHistory(Object data) throws DBLayerException, RemoteException {
    	lowLevelOperation(DELETE, data, false, true);
    }
    
    /**
     *  Update data in the database.
     *
     *  @param data to update (must be one of the holder objects)
     *  @return updated record
     *  @throws DBLayerException when updating data fails
     */
    public Record executeUpdate(Object data) throws DBLayerException, RemoteException {
    	return lowLevelOperation(UPDATE, data, true, true);
    }

    /**
     *  Update data in the database without modifying history tables.
     *
     *  @param data to update (must be one of the holder objects)
     *  @return updated record
     *  @throws DBLayerException when updating data fails
     */
    public Record executeUpdateHistory(Object data) throws DBLayerException, RemoteException {
    	return lowLevelOperation(UPDATE, data, false, true);
    }
    
    /**
     *  Execute DB update using a long running transaction. For this method to work, it is neccessary
     *  to begin long running transaction using beginTransaction() method of this class.
     *
     *  This method checks whether the user has appropriate priviliges and DOES NOT save history
     *
     *  @param data holder object with the record we want to update
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the update
     *  @throws RemoteException in case network connection failed
     */
    public Record executeUpdateInTransactionHistory(Object data) throws DBLayerException, RemoteException {
       return lowLevelOperation(UPDATE, data, false, false);
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
            throw new DBLayerException(L10n.getString("Exception.RetrieveData"));            
        } 
        if (from < 0) {
            logger.error("Cannot read rows starting at the given index: "+from);
            throw new DBLayerException(L10n.getString("Exception.RetrieveData"));
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
        } catch (JDBCException e) {
            logger.fatal("JDBC Exception caught while retrieving the data from the database. SQL State: "+e.getSQLState()+"; Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Exception.RetrieveData"), e);
        } catch (HibernateException e) {
            logger.error("Cannot move to the given row of results: "+from+"; Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Exception.RetrieveData"), DBLayerException.ERROR_LOAD_DATA, e);
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
                    throw new DBLayerException(L10n.getString("Exception.RetrieveData"));
                }                
            }
        } catch (HibernateException e) {
            logger.error("Cannot read data from the results");
            throw new DBLayerException(L10n.getString("Exception.RetrieveData"));
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
            logger.error("Call to next() failed");
            throw new DBLayerException(L10n.getString("Exception.RetrieveData"), DBLayerException.ERROR_LOAD_DATA, e);
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
        int numRows = 0;
        // Get results for the given resultId
        ScrollableResults res = results.get(resultId);
        
        if (res == null) {
            logger.error("HibernateDBLayer: getNumRows(): trying to ask about nonexisting resultId.");                    
            return 0; //this may be better than throwing nullpointerexception on res.getRowNumber() a few lines lower
                      //the application may well recover from this state later
        }        
        // Get the current row in the results
        int currentRow = res.getRowNumber();
        // Go to the first row of the results        
        boolean hasResults = res.first();
        if (hasResults == false) {
            return 0;
        }
        int first = res.getRowNumber();
        // Go to the last row of the results
        res.last();
        int last = res.getRowNumber();        
        // Find out the number of rows between the and the last row
        numRows = last - first + 1;
        // Return the pointer to it's original position
        if (currentRow == -1) {
            res.beforeFirst();
        } else {
            res.setRowNumber(currentRow);
        }
        return numRows;
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
        checkConnection();
        SelectQuery query = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            query = new SelectQueryImplementation(session.createCriteria(classname), queries);
        } catch (HibernateException e) {
            logger.fatal("Cannot create new query (Failed to create session)");
            throw new DBLayerException(L10n.getString("Exception.DatabaseQuery"), DBLayerException.ERROR_SELECT, e);
        }
        SelectQuery stub = query;
        
        if(undertaker != null)
        	stub = (SelectQuery) UnicastRemoteObject.exportObject(query); 
        
        queries.put(stub, query);
        sessions.put(stub, session);
        return stub;
    }    
    
    /**
     *  Create new subquery (SQL "subselect"). To work with this query, use methods of the SelectQuery
     *  interface.
     *
     *  @param classname classname of the holder object we want to use for the select.
     *  @param slias alias used for the holder specified in the first argument
     *  @return new instance of subquery
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the update
     *  @throws RemoteException in case server connection failed
     */
    public SelectQuery createSubQuery(Class classname, String alias) throws DBLayerException, RemoteException {
        // Check whether we are connected to the database
        checkConnection();
        SelectQuery query = new SubQueryImplementation(classname, alias), 
        	stub = query;
        
        if(undertaker != null)
        	stub = (SelectQuery) UnicastRemoteObject.exportObject(query); 
        
        queries.put(stub, query);
        return stub;        
    }
    
    /**
     *  Execute constructed SELECT query. Only executes query, for retrieving results use next() and more()
     *
     *  @param query query we want to execute
     *  @throws DBLayerException when selecting records from the database fails
     */
    public int executeQuery(SelectQuery query) throws DBLayerException, RemoteException {
    	SelectQuery selectQuery = queries.get(query);
    	if(selectQuery == null) {
            logger.error("You can only pass queries created by this DBLayer!");
            throw new DBLayerException(L10n.getString("Exception.DatabaseQuery"));
        }
    	
    	assert(selectQuery instanceof SelectQueryImplementation);
    	SelectQueryImplementation sq = (SelectQueryImplementation) selectQuery;
    	
    	if(sq == null) logger.fatal("Class cast failed. Why the fuck?!");
    	
        Transaction tx = null;        
        ScrollableResults res;        
        Session session = sessions.get(query);
        try {
            tx = session.beginTransaction();
            // Execute detached criteria query
            sq.setProjectionList();
            res = sq.getCriteria().scroll(); // retrieve Criteria from SelectQuery
            // Commit transaction
            tx.commit();  
        } catch (JDBCException e) {
            if (tx != null) {
                tx.rollback();
            }            
            logger.fatal("JDBC Exception caught while executing Select query. SQL State: "+e.getSQLState()+"; Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Error.DatabaseQuery"), e);            
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.fatal("Selecting records from the database failed. Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Exception.DatabaseQuery"), DBLayerException.ERROR_SELECT, e);            
        }
        // Update current maximum result id and save the results
        maxResultId++; 
        results.put(maxResultId, res);
        sq.setResultsetIdentifier( maxResultId );
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
        checkConnection();
        Transaction tx = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();            
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
       } catch (JDBCException e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.fatal("JDBC Exception caught while deleting the record from the database. SQL State: "+e.getSQLState()+"; Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Exception.DeleteRecord"), DBLayerException.ERROR_DELETE, e);
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }            
            logger.fatal("Cannot execute conditional delete on table "+tableClass.getName());
            throw new DBLayerException(L10n.getString("Exception.DeleteRecord"), e);            
        } finally {
            if (session != null) session.close();
        }
        return deletedEntities;
    }
    
    /**
     *  Close the select query.
     *
     *  @param query query we want to close
     */
    public void closeQuery(SelectQuery query) throws RemoteException, DBLayerException {
        // TODO: Problem - we don't have any session for subqueries
        // TODO: We should probably catch HibernateException...
        Session session = sessions.remove(query);
        if(session == null) {
        	logger.warn("Client wants to close a query this database layer did not create! " + query);
        	//FIXME: throw new DBLayerException(L10n.getString("Error.ClosingFakeQuery"));
        	return;
        }
        session.close();     
    	// Remove the query from the list of opened queries
        SelectQuery selectQuery = queries.remove(query);
        // Unexport the SelectQuery object
        if(undertaker != null && selectQuery != null) {
            try {
                UnicastRemoteObject.unexportObject(selectQuery, true);
            } catch(NoSuchObjectException e) {}
        }
        
        Integer resultsetIdentifier = selectQuery.getResultsetIdentifier();
        // resultsetIdentifier is null in case executeQuery() failed and no result was returned (but
        // we still have to close the query). Thx goes to Fraktalek
        if (resultsetIdentifier != null) {
            results.remove(resultsetIdentifier);
        }
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

    /**
     *  Begin long running transaction. in the current implementation, there can be only one long
     *  running transaction at a time.
     *
     *  @return true if transaction was started, false if there already is a long running transaction
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the update
     *  @throws RemoteException in case server connection failed
     */
    synchronized public boolean beginTransaction() throws DBLayerException, RemoteException {
        // Check whether we are connected to the database
        checkConnection();
        // Check whether there is some other running transaction
        if (this.longTx != null) {
            return false;               // Return with an error indicating other transaction is running
        }
        try {
            // Open new session
            this.txSession = sessionFactory.openSession();
            // Begin new transaction
            this.longTx = this.txSession.beginTransaction();
       } catch (JDBCException e) {
            logger.fatal("JDBC Exception caught while starting database transaction. SQL State: "+e.getSQLState()+"; Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Exception.StartTransaction"), e);
        } catch (HibernateException e) {
            logger.fatal("Cannot start database transaction");
            throw new DBLayerException(L10n.getString("Exception.StartTransaction"), DBLayerException.ERROR_TRANSACTION, e);
        }
        return true;                    // Transaction succesfully started
    }

    /**
     *  Commit long running transaction. In the current implementation, there can be only one long
     *  running transaction at a time.
     *
     *  @return true if commit was successful, false if there is no long running transaction
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the update
     *  @throws RemoteException in case server connection failed
     */    
    public boolean commitTransaction() throws DBLayerException, RemoteException {
        // Check whether we are connected to the database
        checkConnection();
        // Check whether we have an opened session and running transaction
        if ((this.longTx == null) || (this.txSession == null)) {
            return false;               // Return with an error indicating we don't have proper conditions
        }
        try {
            // Commit the transaction
            this.longTx.commit();
            // Set the transaction object to null
            this.longTx = null;
            // Close the session
            this.txSession.close();            
       } catch (StaleObjectStateException e) {   
            this.rollbackTransaction();
            logger.error("StaleObjectStateException caught (Concurrent transactions running and trying to commit). Details: "+e.getMessage());
            throw new DBLayerException("Error.ConcurrentUpdate", DBLayerException.ERROR_TRANSACTION, e);
        }   catch (JDBCException e) {
            this.rollbackTransaction();
            logger.fatal("JDBC Exception caught while commiting database transaction. SQL State: "+e.getSQLState()+"; Details: "+e.getMessage());
            DBLayerException ex = new DBLayerException("Exception.CommitTransaction", e);
            throw ex;
        } catch (HibernateException e) {
            this.rollbackTransaction();    
            logger.fatal("Cannot commit database transaction");
            DBLayerException ex = new DBLayerException("Exception.CommitTransaction", DBLayerException.ERROR_TRANSACTION, e);
            throw ex;            
        }

        return true;
    }
    
    /**
     *  Rollback long running transaction. In the current implementation, there can be only one 
     *  long running transaction is possible. All the DB changes made by *InHistory() methods will
     *  be rolled back.
     *
     *  @return true if rollback was successful, false if the long transaction is not in progress
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the update
     *  @throws RemoteException in case server connection failed
     */
    public boolean rollbackTransaction() throws DBLayerException, RemoteException {
        // Check whether we are connected to the database
        checkConnection();
        // Check whether we have an opened session and running transaction
        if ((this.longTx == null) || (this.txSession == null)) {
            return false;               // Return with an error indicating we don't have proper conditions
        }
        try {
            // Rollback the transaction
            this.longTx.rollback();
            // Set the transaction object to null
            this.longTx = null;
            // Close the session
            this.txSession.close();
       } catch (JDBCException e) {
            logger.fatal("JDBC Exception caught while rollbacking database transaction. SQL State: "+e.getSQLState()+"; Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Exception.RollbackTransaction"), e);
        } catch (HibernateException e) {
            logger.fatal("Cannot rollback database transaction");
            throw new DBLayerException(L10n.getString("Exception.RollbackTransaction"), DBLayerException.ERROR_TRANSACTION, e);            
        }            
        return true;        
    }

    /**
     *  Execute DB insert using a long running transaction. For this method to work, it is neccessary
     *  to begin long running transaction using beginTransaction() method of this class.
     *
     *  This method checks whether the user has appropriate priviliges, saves history and updates
     *  the holder with the author (CCREATEDWHO) and time of creation (CREATEDWHEN).
     *
     *  @param data holder object with the record we want to insert
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the insert
     *  @throws RemoteException in case server connection failed
     */    
    public Record executeInsertInTransaction(Object data) throws DBLayerException, RemoteException {
    	return lowLevelOperation(INSERT, data, true, false);
    }

    public Object executeInsertInTransactionTest(Object data) 
    throws DBLayerException, RemoteException {
        
    	 // Check whether the connection to the databse has been established.
        checkConnection();
        // Complete record
      	completeRecord(data);
        // Check whether we have sufficient rights.
        checkRights(data, 1);

        // If we should not use our own transaction, we must use the stored one.
        Session session = this.txSession;
        int recordId = -1;
        try {                       
            recordId = (Integer)session.save(data);
            ((Record)data).setId(recordId);
            saveHistory(session, data, INSERT, recordId);           
        } 
        catch (StaleObjectStateException e) {
            this.rollbackTransaction();
            logger.warn("StaleObjectStateException caught (Concurrent transactions running and trying to commit). Details: "+e.getMessage());
            throw new DBLayerException("Error.ConcurrentUpdate", DBLayerException.ERROR_TRANSACTION, e);
        } 
        catch (JDBCException e) {
            this.rollbackTransaction();
            logger.fatal("JDBC Exception caught while saving the record into the database. SQL State: "+e.getSQLState()+"; Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Error.DatabaseOperationFailed"), e);
        } 
        catch (HibernateException e) {
            this.rollbackTransaction();
            logger.fatal("Saving record into the database failed. Details: "+e.getMessage());
            int exceptionType = -1;
            throw new DBLayerException(L10n.getString("Error.DatabaseOperationFailed"), exceptionType, e);
        }
        return data;
    }
    
    /**
     *  Execute DB insert using a long running transaction. For this method to work, it is neccessary
     *  to begin long running transaction using beginTransaction() method of this class.
     *
     *  This method checks whether the user has appropriate priviliges, DOES NOT save history and 
     *  updates the holder with the author (CCREATEDWHO) and time of creation (CREATEDWHEN).
     *
     *  @param data holder object with the record we want to insert
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the insert
     *  @throws RemoteException in case server connection failed
     */    
    public Record executeInsertInTransactionHistory(Object data) throws DBLayerException, RemoteException {
    	return lowLevelOperation(INSERT, data, false, false);
    }    
    
    /**
     *  Execute DB update using a long running transaction. For this method to work, it is neccessary
     *  to begin long running transaction using beginTransaction() method of this class.
     *
     *  This method checks whether the user has appropriate priviliges and saves history
     *
     *  @param data holder object with the record we want to update
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the update
     *  @throws RemoteException in case network connection failed
     */
    public Record executeUpdateInTransaction(Object data) throws DBLayerException, RemoteException {
    	return lowLevelOperation(UPDATE, data, true, false);
    }
    
    /**
     *  Execute DB delete using a long running transaction. For this method to work, it is neccessary
     *  to begin long running transaction using beginTransaction() method of this class.
     *
     *  This method checks whether the user has appropriate priviliges and saves history
     *
     *  @param data holder object with the record we want to delete
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the delete
     *  @throws RemoteException in case network connection failed
     */
    public void executeDeleteInTransaction(Object data) throws DBLayerException, RemoteException {
    	lowLevelOperation(DELETE, data, true, false);
    }    

    /**
     *  Execute DB delete using a long running transaction. For this method to work, it is neccessary
     *  to begin long running transaction using beginTransaction() method of this class.
     *
     *  This method checks whether the user has appropriate priviliges and DOES NOT save history
     *
     *  @param data holder object with the record we want to delete
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the delete
     *  @throws RemoteException in case network connection failed
     */    
    public void executeDeleteInTransactionHistory(Object data) throws DBLayerException, RemoteException {
        lowLevelOperation(DELETE, data, false, false);
    }

    /**
     *  Method to check the rights of the user, whether he is allowed to execute given operation
     *  @data data we inserted/updated
     *  @type type of the operation (INSERT/UPDATE/DELETE)
     */
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
                if (!sc.next()) {
                    sess.close();                    
                    logger.error("To-be-updated/deleted author not found in the database. Author ID:"+((Author)data).getId());
                    throw new DBLayerException(L10n.getString("Error.RecordNotFound"));
                }
                Object[] res = sc.get();
                Author aut = (Author)res[0];
                boolean equal = false;
                // Close the session
                sess.close();
                // Check for administrator rights
                if (this.plantloreUser.getRight().getAdministrator() == 1) {
                    equal = true;
                }                
                // Check for direct ownership first. We have to compare IDs since equals doesn't work
                // for User object
                if (aut.getCreatedWho().getId().equals(this.plantloreUser.getId())) {
                    equal = true;
                }
                // Check EDITALL privilege
                if (this.plantloreUser.getRight().getEditAll() == 1) {
                    equal = true;
                }                                                                
                // Then check for indirect (group) ownership
                if (this.rights.getEditGroup() != null) {
                    String[] group = this.rights.getEditGroup().split(",");
                    String strId = aut.getCreatedWho().getId().toString();
                    for (int i=0;i<group.length;i++) {
                        if (strId.equals(group[i])) {
                            equal = true;
                            break;
                        }
                    }                    
                }
                if (equal == false) {
                    logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_AUTHOR);
                    throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);
                }
            }
            if (type == INSERT) {
                // Insert only if CADD = 1
                if (this.rights.getAdd() == 0) {
                    logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_AUTHOR);
                    throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);
                }                
            }            
        }
        // Check rights for table TUSER
        if (data instanceof User) {
            // Only admin can insert/update/delete from this table
            if (this.rights.getAdministrator() != 1) {
                logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_USER);
                throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);
            }
            // User can edit his own data except for login name
            
        }
        // Check rights for table TRIGHT, TPHYTOCHORIA, TVILLAGES, TTERRITORIES, TPLANTS, TMETADATA
        if ((data instanceof Right) || (data instanceof Phytochorion) ||
            (data instanceof NearestVillage) || (data instanceof Territory) ||
            (data instanceof Plant) || (data instanceof Metadata)) {
            String entity = "";
            if (data instanceof Right) { entity = PlantloreConstants.ENTITY_RIGHT; }
            if (data instanceof Phytochorion) { entity = PlantloreConstants.ENTITY_PHYTOCHORION; }
            if (data instanceof NearestVillage) { entity = PlantloreConstants.ENTITY_VILLAGE; }
            if (data instanceof Territory) { entity = PlantloreConstants.ENTITY_TERRITORY; }
            if (data instanceof Plant) { entity = PlantloreConstants.ENTITY_PLANT; }            
            if (data instanceof Metadata) { entity = PlantloreConstants.ENTITY_METADATA; }
            // Only admin can insert/update/delete from this table
            if (this.rights.getAdministrator() != 1) {
                logger.warn("User doesn't have sufficient rights for this operation. Entity: "+entity);
                throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);            
            }
        }
        // Check rights for table THISTORYCOLUMN
        if (data instanceof HistoryColumn) {
            logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_HISTORYCOLUMN);
            throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);        
        }
        // Check rights for table THISTORY and THISTORYCHANGE
        if ((data instanceof HistoryChange) || (data instanceof HistoryRecord)) {
            if ((type == INSERT) || (type == UPDATE)) {
                String entity = "";
                if (data instanceof HistoryChange) { entity = PlantloreConstants.ENTITY_HISTORYCHANGE; }
                if (data instanceof HistoryRecord) { entity = PlantloreConstants.ENTITY_HISTORYRECORD; }                
                logger.warn("User doesn't have sufficient rights for this operation. Entity: "+entity);
                throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);          
            } else if (type == DELETE) {
                // Tu to bude zlozitejsie...
                // TODO ???
            }           
        }        
        // Check rights for table TPUBLICATIONS
        if (data instanceof Publication) {
            if ((type == DELETE) || (type == UPDATE)) {
                // Only data of the user and those listed in CEDITGROUP
                sess = this.sessionFactory.openSession();
                ScrollableResults sc = sess.createCriteria(Publication.class)
                    .add(Restrictions.eq(Publication.ID, ((Publication)data).getId()))
                    .scroll();
                // If we haven't found the publication in the database, raise exception
                if (!sc.next()) {
                    sess.close();                    
                    logger.error("To-be-updated/deleted publication not found in the database. Publication ID:"+((Publication)data).getId());
                    throw new DBLayerException(L10n.getString("Error.RecordNotFound"));        
                }
                Object[] res = sc.get();
                Publication pub = (Publication)res[0];
                boolean equal = false;
                sess.close();
                // Check for direct ownership first                
                if (pub.getCreatedWho().getId().equals(this.plantloreUser.getId())) {
                    equal = true;
                }
                // Check for administrator rights
                if (this.plantloreUser.getRight().getAdministrator() == 1) {
                    equal = true;
                }                
                if (this.plantloreUser.getRight().getEditAll() == 1) {
                    equal = true;
                }                                
                // Then check for indirect (group) ownership
                if (this.rights.getEditGroup() != null) {   
                    String[] group = this.rights.getEditGroup().split(",");
                    String strId = pub.getCreatedWho().getId().toString();
                    for (int i=0;i<group.length;i++) {
                        if (strId.equals(group[i])) {
                            equal = true;
                            break;
                        }
                    }
                }
                if (equal == false) {
                    logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_PUBLICATION);
                    throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);          
                }
            }
            if (type == INSERT) {
                // Only if CADD = 1
                if (this.rights.getAdd() == 0) {
                    logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_PUBLICATION);
                    throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);
                }                
            }                        
        }
        // Check rights for table TOCCURRENCES
        if (data instanceof Occurrence) {
            if ((type == DELETE) || (type == UPDATE)) {
                // Only data of the user and those listed in CEDITGROUP
                sess = this.sessionFactory.openSession();
                ScrollableResults sc = sess.createCriteria(Occurrence.class)
                    .add(Restrictions.eq(Occurrence.ID, ((Occurrence)data).getId()))
                    .scroll();
                // If we haven't found the occurrence in the database, raise exception
                if (!sc.next()) {
                    sess.close();                    
                    logger.error("To-be-updated/deleted occurrence not found in the database. Occurrence ID:"+((Publication)data).getId());
                    throw new DBLayerException(L10n.getString("Error.RecordNotFound"));        
                }
                Object[] res = sc.get();
                Occurrence occ = (Occurrence)res[0];
                boolean equal = false;
                sess.close();
                // Check for direct ownership first                
                if (occ.getCreatedWho().getId().equals(this.plantloreUser.getId())) {
                    equal = true;
                }
                // Check for administrator rights
                if (this.plantloreUser.getRight().getAdministrator() == 1) {
                    equal = true;
                }
                // Check EDITALL privilege
                if (this.plantloreUser.getRight().getEditAll() == 1) {
                    equal = true;
                }                                                
                // Then check for indirect (group) ownership
                if (this.rights.getEditGroup() != null) {                
                    String[] group = this.rights.getEditGroup().split(",");
                    String strId = occ.getCreatedWho().getId().toString();
                    for (int i=0;i<group.length;i++) {
                        if (strId.equals(group[i])) {
                            equal = true;
                            break;
                        }
                    }
                }
                if (equal == false) {
                    logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_OCCURRENCE);
                    throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);
                }
            }
            if (type == INSERT) {
                // Insert only if CADD = 1
                if (this.rights.getAdd() == 0) {
                    logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_OCCURRENCE);
                    throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);
                }                
            }                            
        }
        // Check rights for table THABITATS
        if (data instanceof Habitat) {
            if ((type == DELETE) || (type == UPDATE)) {
                // Only data of the user and those listed in CEDITGROUP
                sess = this.sessionFactory.openSession();
                ScrollableResults sc = sess.createCriteria(Habitat.class)
                    .add(Restrictions.eq(Habitat.ID, ((Habitat)data).getId()))
                    .scroll();
                // If we haven't found the habitat in the database, raise exception
                if (!sc.next()) {
                    sess.close();                    
                    logger.error("To-be-updated/deleted habitat not found in the database. Occurrence ID:"+((Habitat)data).getId());
                    throw new DBLayerException(L10n.getString("Error.RecordNotFound"));        
                }
                Object[] res = sc.get();
                Habitat hab = (Habitat)res[0];
                sess.close();
                sess = this.sessionFactory.openSession();                
                boolean equal = false;
                // Check for direct ownership first. Find owner of associated occurrence
                sc = sess.createCriteria(Occurrence.class)
                    .add(Restrictions.eq(Occurrence.HABITAT, hab))
                    .scroll();
                // If no occurrence was found
                if (!sc.next()) {
                    sess.close();
                    logger.error("No occurrence references selected habitat. Habitat ID:"+hab.getId());                    
                    throw new DBLayerException(L10n.getString("Error.RecordNotFound"));        
                }    
                res = sc.get();
                Occurrence occ = (Occurrence)res[0];
                sess.close();
                if (occ.getCreatedWho().getId().equals(this.plantloreUser.getId())) {
                    equal = true;
                }       
                // Check for administrator rights
                // TODO: This should be done at the beginning to save one query if the user is admin
                if (this.plantloreUser.getRight().getAdministrator() == 1) {
                    equal = true;
                }
                // Check EDITALL privilege
                if (this.plantloreUser.getRight().getEditAll() == 1) {
                    equal = true;
                }                                                                
                // Then check for indirect (group) ownership
                if (this.rights.getEditGroup() != null) {                
                    String[] group = this.rights.getEditGroup().split(",");
                    String strId = occ.getCreatedWho().getId().toString();
                    for (int i=0;i<group.length;i++) {
                        if (strId.equals(group[i])) {
                            equal = true;
                            break;
                        }
                    }
                }
                if (equal == false) {
                    logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_HABITAT);
                    throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);
                }
            }            
            if (type == INSERT) {
                // Insert only if CADD = 1
                if (this.rights.getAdd() == 0) {
                    logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_HABITAT);
                    throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS); 
                }                
            }                                        
        }
        // Check rights for table TAUTHORSOCCURRENCES
        if (data instanceof AuthorOccurrence) {
            if ((type == DELETE) || (type == UPDATE)) {
                // Only data of the user and those listed in CEDITGROUP
                sess = this.sessionFactory.openSession();
                ScrollableResults sc = sess.createCriteria(AuthorOccurrence.class)
                    .add(Restrictions.eq(AuthorOccurrence.ID, ((AuthorOccurrence)data).getId()))
                    .scroll();
                // If we haven't found the occurrence in the database, raise exception
                if (!sc.next()) {
                    sess.close();                    
                    logger.error("To-be-updated/deleted authoroccurrence not found in the database. Occurrence ID:"+((AuthorOccurrence)data).getId());
                    throw new DBLayerException(L10n.getString("Error.RecordNotFound"));        
                }
                Object[] res = sc.get();
                AuthorOccurrence ao = (AuthorOccurrence)res[0];
                boolean equal = false;
                sess.close();
                // Check for direct ownership first                
                if (ao.getOccurrence().getCreatedWho().getId().equals(this.plantloreUser.getId())) {
                    equal = true;
                }
                // Check for administrator rights
                // TODO: This should be done at the beginning to save one query
                if (this.plantloreUser.getRight().getAdministrator() == 1) {
                    equal = true;
                }
                // Check EDITALL privilege
                if (this.plantloreUser.getRight().getEditAll() == 1) {
                    equal = true;
                }                                                                
                // Then check for indirect (group) ownership
                if (this.rights.getEditGroup() != null) {                
                    String[] group = this.rights.getEditGroup().split(",");
                    String strId = ao.getOccurrence().getCreatedWho().getId().toString();
                    for (int i=0;i<group.length;i++) {
                        if (strId.equals(group[i])) {
                            equal = true;
                            break;
                        }
                    }
                }
                if (equal == false) {
                    logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_AUTHOROCCURRENCE);
                    throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);
                }
            }            
            if (type == INSERT) {
                // Insert only if CADD = 1
                if (this.rights.getAdd() == 0) {
                    logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_AUTHOROCCURRENCE);
                    throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);   
                }                
            }            
        }        
    }
    
    /**
     *  Method for saving history of database modifications
     *
     *  @param sess session to use for databas queries
     *  @param data data we inserted/updated
     *  @param type type of operation (SAVE, UPDATE)
     *  @param recordId id of the newly created record
     *  @throws DBLayerException in case database error occurred
     */
    private void saveHistory(Session sess, Object data, int type, Integer recordId) throws DBLayerException {
        String table;
        Integer id, result = 0;
        HistoryColumn column;
        Integer recId;
        Object[] objCol = null;
        String tableId = "";
        Class updated = null;
        Integer updatedId = null;
        ScrollableResults sr;
        
      
        // Set right type of operation for AuthorOccurrence
        int aoInsert = 0;
        if (data instanceof AuthorOccurrence) {
        	if (type == INSERT) {
        		aoInsert = 1;
        		type = UPDATE;
        	}        	
        }
        
        // Saving history when new record is inserted
        if (type == INSERT) {
            HistoryChange historyChange = new HistoryChange();                                    
	        if (data instanceof Occurrence) {
	        	table = PlantloreConstants.ENTITY_OCCURRENCE;
	        } else if (data instanceof Habitat) {
	            table = PlantloreConstants.ENTITY_HABITAT;
	        } else if (data instanceof Publication) {
	            table = PlantloreConstants.ENTITY_PUBLICATION;
	        } else if (data instanceof Author) {
	            table = PlantloreConstants.ENTITY_AUTHOR;                        
	        } else if (data instanceof Phytochorion) {
	            table = PlantloreConstants.ENTITY_PHYTOCHORION;                        
	        } else if (data instanceof NearestVillage) {
	            table = PlantloreConstants.ENTITY_VILLAGE;
	        } else if (data instanceof Territory) {
	            table = PlantloreConstants.ENTITY_TERRITORY;                        
	        } else if (data instanceof Metadata) {
	            table = PlantloreConstants.ENTITY_METADATA;                    
	        } else {
	        	return;
	        }
	        historyChange.setRecordId(recordId);                        
            historyChange.setOperation(INSERT);
            historyChange.setWho(this.plantloreUser);
            historyChange.setWhen(new java.util.Date());
            // Load record from THistoryColumn table
            try {
                SelectQuery sq = this.createQuery(HistoryColumn.class);
                sq.addRestriction(PlantloreConstants.RESTR_EQ, HistoryColumn.TABLENAME, null, table, null);
                sq.addRestriction(PlantloreConstants.RESTR_IS_NULL, HistoryColumn.COLUMNNAME, null, null, null);
                result = this.executeQuery(sq);
                objCol = next(result);                
                this.closeQuery(sq);
            } catch (RemoteException e) {
                logger.error("Remote exception caught in DBLayer. This should never happen. Details: "+e.getMessage());
            }
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
            history.setOldRecordId(0);
            history.setNewValue(null);
            history.setOldValue(null);
            // Save into the database
            recId = (Integer)sess.save(historyChange);
            history.setHistoryChange(historyChange);                
            recId = (Integer)sess.save(history);                
        }
        // Saving history when record is updated        
        if (type == UPDATE){            
            HistoryChange historyChange = new HistoryChange();                        
            // Check whether we are dealing with delete (seting CDELETE to 0)
            if ((data instanceof Author) || (data instanceof Publication) || 
                (data instanceof Metadata) || (data instanceof Occurrence) || 
                (data instanceof Habitat)) {
                Integer delete = 0;                  
                if (data instanceof Author) {
                    delete = ((Author)data).getDeleted();
                    id = ((Author)data).getId();
                    table = PlantloreConstants.ENTITY_AUTHOR;
                } else
                if (data instanceof Publication) {
                    delete = ((Publication)data).getDeleted();                    
                    id = ((Publication)data).getId();         
                    table = PlantloreConstants.ENTITY_PUBLICATION;                    
                } else 
                if (data instanceof Occurrence) {
                    delete = ((Occurrence)data).getDeleted();                    
                    id = ((Occurrence)data).getId();         
                    table = PlantloreConstants.ENTITY_OCCURRENCE;                                                         
                } else 
                if (data instanceof Habitat) {
                    delete = ((Habitat)data).getDeleted();                    
                    id = ((Habitat)data).getId();         
                    table = PlantloreConstants.ENTITY_HABITAT;                                                         
                } else 
                if (data instanceof Metadata){                    
                    delete = ((Metadata)data).getDeleted();
                    id = ((Metadata)data).getId();               
                    table = PlantloreConstants.ENTITY_METADATA;                    
                } else {                	
                	id = 0;
                	table = "";
                }
                if (delete == 1) {
                    // CDELETE was set to 1, we are deleting record                    
                    historyChange.setOperation(DELETE);
                    historyChange.setRecordId(id);
                    historyChange.setWhen(new java.util.Date());
                    historyChange.setWho(this.plantloreUser);
                    HistoryRecord hist = new HistoryRecord();
                    // Save historyChange object
                    sess.save(historyChange);
                    // Read HistoryColumn table
                    sr = sess.createCriteria(HistoryColumn.class)
                        .add(Restrictions.eq(HistoryColumn.TABLENAME, table))
                        .add(Restrictions.isNull(HistoryColumn.COLUMNNAME))
                        .scroll();
                    if (!sr.next()) {
                        logger.error("tHistoryColumn doesn't contain required data");
                        DBLayerException ex = new DBLayerException("tHistoryColumn doesn't contain required data");
                        ex.setError(ex.ERROR_DB, table);
                        throw ex;                                                        
                    }
                    Object[] hc = sr.get();
                    hist.setHistoryChange(historyChange);
                    hist.setOldRecordId(0);
                    hist.setNewValue(null);
                    hist.setOldValue(null);
                    hist.setHistoryColumn((HistoryColumn)hc[0]);
                    // Save History record
                    sess.save(hist);
                    return;                    
                }
            }
            if ((data instanceof Author) || (data instanceof Publication) ||
                (data instanceof Territory) || (data instanceof Phytochorion) ||
                (data instanceof NearestVillage) || (data instanceof Metadata) ||
                (data instanceof Occurrence) || (data instanceof Habitat) ||
                (data instanceof AuthorOccurrence)) {                                                               
                historyChange.setOperation(UPDATE);
                historyChange.setWhen(new java.util.Date());
                historyChange.setWho(this.plantloreUser);
                if (data instanceof Occurrence) {
                    updated = Occurrence.class;
                    updatedId = ((Occurrence)data).getId();
                    tableId = Occurrence.ID;                    
                    historyChange.setRecordId(((Occurrence)data).getId());
                } else
                if (data instanceof AuthorOccurrence) {
                    updated = AuthorOccurrence.class;
                    updatedId = ((AuthorOccurrence)data).getId();
                    tableId = AuthorOccurrence.ID;                    
                    historyChange.setRecordId(((AuthorOccurrence)data).getOccurrence().getId());
                } else
                if (data instanceof Habitat) {
                    updated = Habitat.class;
                    updatedId = ((Habitat)data).getId();
                    tableId = Habitat.ID;                    
                    historyChange.setRecordId(((Habitat)data).getId());
                } else
                if (data instanceof Author) {
                    updated = Author.class;
                    updatedId = ((Author)data).getId();
                    tableId = Author.ID;                    
                    historyChange.setRecordId(((Author)data).getId());
                } else
                if (data instanceof Publication) {
                    updated = Publication.class;
                    updatedId = ((Publication)data).getId();                    
                    tableId = Publication.ID;                    
                    historyChange.setRecordId(((Publication)data).getId());                    
                } else
                if (data instanceof Territory) {
                    updated = Territory.class;
                    updatedId = ((Territory)data).getId();                    
                    tableId = Territory.ID;
                    historyChange.setRecordId(((Territory)data).getId());                    
                } else
                if (data instanceof Phytochorion) {
                    updated = Phytochorion.class;
                    updatedId = ((Phytochorion)data).getId();                    
                    tableId = Phytochorion.ID;
                    historyChange.setRecordId(((Phytochorion)data).getId());
                } else
                if (data instanceof Metadata) {
                    updated = Metadata.class;
                    updatedId = ((Metadata)data).getId();                    
                    tableId = Metadata.ID;
                    historyChange.setRecordId(((Metadata)data).getId());                    
                } else
                if (data instanceof NearestVillage) {
                    updated = NearestVillage.class;
                    updatedId = ((NearestVillage)data).getId();
                    tableId = NearestVillage.ID;
                    historyChange.setRecordId(((NearestVillage)data).getId());                    
                }                                  
                // Read the to-be-updated object
                Session tempSess = this.sessionFactory.openSession(); 
                ScrollableResults res = tempSess.createCriteria(updated)
                    .add(Restrictions.eq(tableId, updatedId))
                    .scroll();
                if (!res.next() && aoInsert != 1) {
                    logger.error("To-be-updated record was not found in the database. Type: "+updated.getName()+" ID:"+updatedId);
                    DBLayerException ex = new DBLayerException("To-be-updated record was not found in the database. Type: "+updated.getName()+" ID:"+updatedId);
                    ex.setError(ex.ERROR_UPDATE, updated.getName());
                    tempSess.close();
                    throw ex;
                }
                Object[] original = null;
                if (aoInsert != 1) {
                    original = res.get();
                }
                tempSess.close();
                // Object origRec, newRec;
                if (data instanceof Occurrence) {                	
                    Occurrence origRec = (Occurrence)original[0];
                    Occurrence newRec = (Occurrence)data;                    
                    // Seeing is believing.
                    List<String> cols = origRec.getHistoryColumns();
                    for(String columnName : cols) {
                    	
                    	Object 
                    	origValue = origRec.getValue(columnName),
                    	newValue = newRec.getValue(columnName);
                    	
                    	if( origValue == null && newValue == null)
                    		continue;
                    	
                    	if( origValue == null || newValue == null || !origValue.equals(newValue) ) {
                    		// Read record from THISTORYCOLUMN first
                    		res = sess.createCriteria(HistoryColumn.class).
                    		add(Restrictions.eq(HistoryColumn.TABLENAME, PlantloreConstants.ENTITY_OCCURRENCE)).
                    		add(Restrictions.eq(HistoryColumn.COLUMNNAME, columnName)).
                    		scroll();
                    		if ( !res.next() ) {
                    			logger.error("tHistoryColumn doesn't contain the required data ("+columnName+").");
                    			DBLayerException ex = new DBLayerException("Error.CorruptedDatabase");
                    			ex.setError(DBLayerException.ERROR_DB, PlantloreConstants.ENTITY_OCCURRENCE);
                    			throw ex;
                    		}
                    		Object[] colNames = res.get();
                    		// Create new history record
                    		HistoryRecord historyRecord = new HistoryRecord();
                    		// Save OldRecordId if neccessary
                    		 if (((String)columnName).equals(Occurrence.PLANT)) {
                    			 //this situation is improbability (new occurrence is insert into database during editinig of plant)   
                    			 historyRecord.setOldRecordId(((Plant)origRec.getValue(columnName)).getId());
                    			 historyRecord.setOldValue(((Plant)origValue).getTaxon());
                         		 historyRecord.setNewValue(((Plant)newValue).getTaxon());
                    		 } else if (((String)columnName).equals(Occurrence.PUBLICATION)) {
                                         if ((Publication)origRec.getValue(columnName) == null) historyRecord.setOldRecordId(0);
                                         else historyRecord.setOldRecordId(((Publication)origRec.getValue(columnName)).getId());
                                         String referenceCitation = (origValue == null) ? "" : ((Publication)origValue).getReferenceCitation();
                    			 historyRecord.setOldValue(referenceCitation);
                         		 historyRecord.setNewValue(((Publication)newValue).getReferenceCitation());
                    		 } else if (((String)columnName).equals(Occurrence.HABITAT)) {
                    			 historyRecord.setOldRecordId(((Habitat)origRec.getValue(columnName)).getId());
                    			 historyRecord.setOldValue(((Habitat)origValue).getDescription());
                         		 historyRecord.setNewValue(((Habitat)newValue).getDescription());
                    		 } else if (((String)columnName).equals(Occurrence.METADATA)) {
                    			 historyRecord.setOldRecordId(((Metadata)origRec.getValue(columnName)).getId());                    			 
                    			 historyRecord.setOldValue(((Metadata)origValue).getDataSetTitle());
                         		 historyRecord.setNewValue(((Metadata)newValue).getDataSetTitle());
                    		 } else {
                    			String origValueString = (origValue == null) ? null : origValue.toString(),
                         			   newValueString = (newValue == null) ? null : newValue.toString(); 
                    			historyRecord.setOldValue(origValueString);
                         		historyRecord.setNewValue(newValueString);
                         		historyRecord.setOldRecordId(0);
                    		 }
                    		                     		 
                    		 //Save the HistoryChange object
                             sess.save(historyChange);
                    		// Save record into THISTORY                    		
                    		historyRecord.setHistoryChange(historyChange);
                    		historyRecord.setHistoryColumn((HistoryColumn) colNames[0]);                    		
                    		sess.save(historyRecord);
                    	}
                    }
                } else if (data instanceof AuthorOccurrence) { 
                	AuthorOccurrence newRec = (AuthorOccurrence)data;                	
                	//delete == 0 ...edit information about Author in occurrence
                	//delete == 1 ...delete Auhtor from occurrence
                	//aoInsert == 1 ...add new Author to occurrence                      
                	if (newRec.getDeleted() == 1 || aoInsert == 1) {                                              
                        // Read HistoryColumn table
                        sr = sess.createCriteria(HistoryColumn.class)
                            .add(Restrictions.eq(HistoryColumn.TABLENAME, PlantloreConstants.ENTITY_AUTHOROCCURRENCE))
                            .add(Restrictions.isNull(HistoryColumn.COLUMNNAME))
                            .scroll();
                        if (!sr.next()) {
                            logger.error("tHistoryColumn doesn't contain required data");
                            DBLayerException ex = new DBLayerException("tHistoryColumn doesn't contain required data");
                            ex.setError(ex.ERROR_DB, PlantloreConstants.ENTITY_AUTHOROCCURRENCE);
                            throw ex;                                                        
                        }
                        Object[] hc = sr.get();                        
                        HistoryRecord hist = new HistoryRecord(); 
                        hist.setHistoryChange(historyChange);                        
                        hist.setHistoryColumn((HistoryColumn)hc[0]);
                        hist.setOldRecordId(newRec.getId());
                        if ( newRec.getDeleted() == 1) {
	                        hist.setNewValue(null);
	                        hist.setOldValue(newRec.getAuthor().getWholeName());
                        } else if (aoInsert == 1) {
                        	hist.setNewValue(newRec.getAuthor().getWholeName());
	                        hist.setOldValue(null);
                        }
                        // Save the HistoryChange object
                        sess.save(historyChange);
                        // Save History record
                        sess.save(hist);                                                       
                	} else {
                		AuthorOccurrence origRec = (AuthorOccurrence)original[0];
                		ArrayList cols = (ArrayList)origRec.getColumns();
                        for (int i=0;i<cols.size();i++) {
                            Object origValue = (origRec.getValue((String)cols.get(i)) == null) ? new String("") : origRec.getValue((String)cols.get(i));                        
                            Object newValue = (newRec.getValue((String)cols.get(i)) == null) ? new String("") : newRec.getValue((String)cols.get(i));                                                
                            if (!origValue.equals(newValue)) {
                            	//Read record from THISTORYCOLUMN first                              	
                                res = sess.createCriteria(HistoryColumn.class)
                                    .add(Restrictions.eq(HistoryColumn.TABLENAME, PlantloreConstants.ENTITY_AUTHOROCCURRENCE))
                                    .add(Restrictions.eq(HistoryColumn.COLUMNNAME, (String)cols.get(i)))
                                    .scroll();                                
                                if (!res.next()) {
                                    logger.error("tHistoryColumn doesn't contain required data");
                                    DBLayerException ex = new DBLayerException("tHistoryColumn doesn't contain required data");
                                    ex.setError(ex.ERROR_DB, PlantloreConstants.ENTITY_AUTHOROCCURRENCE);
                                    throw ex;                                
                                }
                            
	                            Object[] colNames = res.get();                                             
	                            
	                            // Save record into THISTORY
	                            HistoryRecord hist = new HistoryRecord();
	                            hist.setHistoryChange(historyChange);	                            	                           
	                            hist.setHistoryColumn((HistoryColumn)colNames[0]);  
	                            hist.setOldRecordId(newRec.getId());
	                            String origValueString = (origRec.getValue((String)cols.get(i)) == null) ? null : origValue.toString(),
	                           			   newValueString = (newRec.getValue((String)cols.get(i)) == null) ? null : newValue.toString(); 
	                      		hist.setOldValue(origValueString);
	                           	hist.setNewValue(newValueString);
	                            // Save the HistoryChange object
	                            sess.save(historyChange);
	                            // Save the History object
	                            sess.save(hist);  
                            }
                        }
                	} 
                } else if (data instanceof Habitat) {
                	Habitat origRec = (Habitat)original[0];
                	Habitat newRec = (Habitat)data;
                    ArrayList cols = (ArrayList)origRec.getColumns();
                    for (int i=0;i<cols.size();i++) {
                        Object origValue = (origRec.getValue((String)cols.get(i)) == null) ? new String("") : origRec.getValue((String)cols.get(i));                        
                        Object newValue = (newRec.getValue((String)cols.get(i)) == null) ? new String("") : newRec.getValue((String)cols.get(i));                                                                        
                        if (!origValue.equals(newValue)) {
                            // Read record from THISTORYCOLUMN first                           	
                            res = sess.createCriteria(HistoryColumn.class)
                                .add(Restrictions.eq(HistoryColumn.TABLENAME, PlantloreConstants.ENTITY_HABITAT))
                                .add(Restrictions.eq(HistoryColumn.COLUMNNAME, (String)cols.get(i)))
                                .scroll();
                            if (!res.next()) {
                                logger.error("tHistoryColumn doesn't contain required data");
                                //TODO
                                DBLayerException ex = new DBLayerException("tHistoryColumn doesn't contain required data");
                                ex.setError(ex.ERROR_DB, PlantloreConstants.ENTITY_HABITAT);
                                throw ex;                                
                            }
                            Object[] colNames = res.get();
                            // Create new history record
                            HistoryRecord hist = new HistoryRecord();
                            // Save OldRecordId if neccessary
                            
                    		 //TODO: Save oldRecordId
                    		 if (((String)cols.get(i)).equals(Habitat.TERRITORY)) {                     			 
                    			 hist.setOldRecordId(((Territory)origRec.getValue((String)cols.get(i))).getId());
                    			 hist.setOldValue(((Territory)origRec.getValue((String)cols.get(i))).getName());
                                 hist.setNewValue(((Territory)newRec.getValue((String)cols.get(i))).getName());
                    		 } else if (((String)cols.get(i)).equals(Habitat.PHYTOCHORION)) {                    			 
                    			 hist.setOldRecordId(((Phytochorion)origRec.getValue((String)cols.get(i))).getId());
                    			 hist.setOldValue(((Phytochorion)origRec.getValue((String)cols.get(i))).getName());
                                 hist.setNewValue(((Phytochorion)newRec.getValue((String)cols.get(i))).getName());
                    		 } else if (((String)cols.get(i)).equals(Habitat.NEARESTVILLAGE)) {                    			 
                    			 hist.setOldRecordId(((NearestVillage)origRec.getValue((String)cols.get(i))).getId());
                    			 hist.setOldValue(((NearestVillage)origRec.getValue((String)cols.get(i))).getName());
                                 hist.setNewValue(((NearestVillage)newRec.getValue((String)cols.get(i))).getName());
                    		 } else {
                    			 String origValueString = (origRec.getValue((String)cols.get(i)) == null) ? null : origValue.toString(),
                           			    newValueString = (newRec.getValue((String)cols.get(i)) == null) ? null : newValue.toString(); 
                      			 hist.setOldValue(origValueString);
                           		 hist.setNewValue(newValueString);  
                           		 hist.setOldRecordId(0);
                    		 }
                    		 
                            // Save the HistoryChange object
                             sess.save(historyChange);
                            // Save record into THISTORY                            
                            hist.setHistoryChange(historyChange);
                            hist.setHistoryColumn((HistoryColumn)colNames[0]);                                                        
                            sess.save(hist);                            
                        }
                    }
                } else if (data instanceof Author) {
                    Author origRec = (Author)original[0];
                    Author newRec = (Author)data;
                    ArrayList cols = (ArrayList)origRec.getColumns();
                    for (int i=0;i<cols.size();i++) {
                        Object origValue = (origRec.getValue((String)cols.get(i)) == null) ? new String("") : origRec.getValue((String)cols.get(i));                        
                        Object newValue = (newRec.getValue((String)cols.get(i)) == null) ? new String("") : newRec.getValue((String)cols.get(i));                                                
                        if (!origValue.equals(newValue)) {
                            // Read record from THISTORYCOLUMN first                            
                            res = sess.createCriteria(HistoryColumn.class)
                                .add(Restrictions.eq(HistoryColumn.TABLENAME, PlantloreConstants.ENTITY_AUTHOR))
                                .add(Restrictions.eq(HistoryColumn.COLUMNNAME, (String)cols.get(i)))
                                .scroll();
                            if (!res.next()) {
                                logger.error("tHistoryColumn doesn't contain required data");
                                DBLayerException ex = new DBLayerException("tHistoryColumn doesn't contain required data");
                                ex.setError(ex.ERROR_DB, PlantloreConstants.ENTITY_AUTHOR);
                                throw ex;                                
                            }
                            Object[] colNames = res.get();
                            //Save the HistoryChange object
                            sess.save(historyChange);
                            // Save record into THISTORY
                            HistoryRecord hist = new HistoryRecord();
                            hist.setHistoryChange(historyChange);
                            hist.setHistoryColumn((HistoryColumn)colNames[0]);    
                            String origValueString = (origRec.getValue((String)cols.get(i)) == null) ? null : origValue.toString(),
                        			   newValueString = (newRec.getValue((String)cols.get(i)) == null) ? null : newValue.toString(); 
                   			hist.setOldValue(origValueString);
                        	hist.setNewValue(newValueString); 
                        	hist.setOldRecordId(0);
                            sess.save(hist);                            
                        }
                    }
                } else if (data instanceof Publication) {
                    Publication origRec = (Publication)original[0];                    
                    Publication newRec = (Publication)data;
                    ArrayList cols = (ArrayList)origRec.getColumns();                    
                    for (int i=0;i<cols.size();i++) {                            
                        Object origValue = (origRec.getValue((String)cols.get(i)) == null) ? new String("") : origRec.getValue((String)cols.get(i));                        
                        Object newValue = (newRec.getValue((String)cols.get(i)) == null) ? new String("") : newRec.getValue((String)cols.get(i));                                                
                        if (!origValue.equals(newValue)) {
                            // Read record from THISTORYCOLUMN first
                            res = sess.createCriteria(HistoryColumn.class)
                                .add(Restrictions.eq(HistoryColumn.TABLENAME, PlantloreConstants.ENTITY_PUBLICATION))
                                .add(Restrictions.eq(HistoryColumn.COLUMNNAME, (String)cols.get(i)))
                                .scroll();
                            if (!res.next()) {
                                logger.error("tHistoryColumn doesn't contain required data");
                                DBLayerException ex = new DBLayerException("tHistoryColumn doesn't contain required data");
                                ex.setError(ex.ERROR_DB, PlantloreConstants.ENTITY_PUBLICATION);
                                throw ex;                                
                            }
                            Object[] colNames = res.get();
                            //Save the HistoryChange object
                            sess.save(historyChange);
                            // Save record into THISTORY
                            HistoryRecord hist = new HistoryRecord();
                            hist.setHistoryChange(historyChange);
                            hist.setHistoryColumn((HistoryColumn)colNames[0]); 
                            String origValueString = (origRec.getValue((String)cols.get(i)) == null) ? null : origValue.toString(),
                        			   newValueString = (newRec.getValue((String)cols.get(i)) == null) ? null : newValue.toString(); 
                   			hist.setOldValue(origValueString);
                        	hist.setNewValue(newValueString);  
                        	hist.setOldRecordId(0);
                            sess.save(hist);     
                        }
                    }
                } else if (data instanceof Territory) {
                    Territory origRec = (Territory)original[0];                    
                    Territory newRec = (Territory)data;
                    ArrayList cols = (ArrayList)origRec.getColumns();
                    for (int i=0;i<cols.size();i++) {
                        Object origValue = (origRec.getValue((String)cols.get(i)) == null) ? new String("") : origRec.getValue((String)cols.get(i));                        
                        Object newValue = (newRec.getValue((String)cols.get(i)) == null) ? new String("") : newRec.getValue((String)cols.get(i));                                                
                        if (!origValue.equals(newValue)) {
                            // Read record from THISTORYCOLUMN first
                            res = sess.createCriteria(HistoryColumn.class)
                                .add(Restrictions.eq(HistoryColumn.TABLENAME, PlantloreConstants.ENTITY_TERRITORY))
                                .add(Restrictions.eq(HistoryColumn.COLUMNNAME, (String)cols.get(i)))
                                .scroll();
                            if (!res.next()) {
                                logger.error("tHistoryColumn doesn't contain required data");
                                DBLayerException ex = new DBLayerException("tHistoryColumn doesn't contain required data");
                                ex.setError(ex.ERROR_DB, PlantloreConstants.ENTITY_TERRITORY);
                                throw ex;                                
                            }
                            Object[] colNames = res.get();
                            // Save the HistoryChange object
                            sess.save(historyChange);
                            // Save record into THISTORY
                            HistoryRecord hist = new HistoryRecord();
                            hist.setHistoryChange(historyChange);
                            hist.setHistoryColumn((HistoryColumn)colNames[0]);   
                            String origValueString = (origRec.getValue((String)cols.get(i)) == null) ? null : origValue.toString(),
                        			   newValueString = (newRec.getValue((String)cols.get(i)) == null) ? null : newValue.toString(); 
                   			hist.setOldValue(origValueString);
                        	hist.setNewValue(newValueString); 
                        	hist.setOldRecordId(0);
                            sess.save(hist);                            
                        }
                    }
                } else if (data instanceof Metadata) {
                    Metadata origRec = (Metadata)original[0];                    
                    Metadata newRec = (Metadata)data;
                    ArrayList cols = (ArrayList)origRec.getColumns();
                    for (int i=0;i<cols.size();i++) {
                        Object origValue = (origRec.getValue((String)cols.get(i)) == null) ? new String("") : origRec.getValue((String)cols.get(i));                        
                        Object newValue = (newRec.getValue((String)cols.get(i)) == null) ? new String("") : newRec.getValue((String)cols.get(i));                                                
                        if (!origValue.equals(newValue)) {
                            // Read record from THISTORYCOLUMN first
                            res = sess.createCriteria(HistoryColumn.class)
                                .add(Restrictions.eq(HistoryColumn.TABLENAME, PlantloreConstants.ENTITY_METADATA))
                                .add(Restrictions.eq(HistoryColumn.COLUMNNAME, (String)cols.get(i)))
                                .scroll();
                            if (!res.next()) {
                                logger.error("tHistoryColumn doesn't contain required data");
                                DBLayerException ex = new DBLayerException("tHistoryColumn doesn't contain required data");
                                ex.setError(ex.ERROR_DB, PlantloreConstants.ENTITY_METADATA);
                                throw ex;                                
                            }
                            Object[] colNames = res.get();
                            // Save the HistoryChange object
                            sess.save(historyChange);
                            // Save record into THISTORY
                            HistoryRecord hist = new HistoryRecord();
                            hist.setHistoryChange(historyChange);
                            hist.setHistoryColumn((HistoryColumn)colNames[0]);   
                            String origValueString = (origRec.getValue((String)cols.get(i)) == null) ? null : origValue.toString(),
                        			   newValueString = (newRec.getValue((String)cols.get(i)) == null) ? null : newValue.toString(); 
                   			hist.setOldValue(origValueString);
                        	hist.setNewValue(newValueString); 
                        	hist.setOldRecordId(0);
                            sess.save(hist);
                        }
                    }                    
                } else if (data instanceof Phytochorion) {
                    Phytochorion origRec = (Phytochorion)original[0];                    
                    Phytochorion newRec = (Phytochorion)data;
                    ArrayList cols = (ArrayList)origRec.getColumns();
                    for (int i=0;i<cols.size();i++) {
                        Object origValue = (origRec.getValue((String)cols.get(i)) == null) ? new String("") : origRec.getValue((String)cols.get(i));                        
                        Object newValue = (newRec.getValue((String)cols.get(i)) == null) ? new String("") : newRec.getValue((String)cols.get(i));                                                
                        if (!origValue.equals(newValue)) {
                            // Read record from THISTORYCOLUMN first
                            res = sess.createCriteria(HistoryColumn.class)
                                .add(Restrictions.eq(HistoryColumn.TABLENAME, PlantloreConstants.ENTITY_PHYTOCHORION))
                                .add(Restrictions.eq(HistoryColumn.COLUMNNAME, (String)cols.get(i)))
                                .scroll();
                            if (!res.next()) {
                                logger.error("tHistoryColumn doesn't contain required data");
                                DBLayerException ex = new DBLayerException("tHistoryColumn doesn't contain required data");
                                ex.setError(ex.ERROR_DB, PlantloreConstants.ENTITY_PHYTOCHORION);
                                throw ex;                                
                            }
                            Object[] colNames = res.get();
                            // Save the HistoryChange object
                            sess.save(historyChange);
                            // Save record into THISTORY
                            HistoryRecord hist = new HistoryRecord();
                            hist.setHistoryChange(historyChange);
                            hist.setHistoryColumn((HistoryColumn)colNames[0]);  
                            String origValueString = (origRec.getValue((String)cols.get(i)) == null) ? null : origValue.toString(),
                        			   newValueString = (newRec.getValue((String)cols.get(i)) == null) ? null : newValue.toString(); 
                   			hist.setOldValue(origValueString);
                        	hist.setNewValue(newValueString); 
                        	hist.setOldRecordId(0);
                            sess.save(hist);
                        }
                    }
                } else if (data instanceof NearestVillage) {
                    NearestVillage origRec = (NearestVillage)original[0];                    
                    NearestVillage newRec = (NearestVillage)data;
                    ArrayList cols = (ArrayList)origRec.getColumns();
                    for (int i=0;i<cols.size();i++) {                        
                        Object origValue = (origRec.getValue((String)cols.get(i)) == null) ? new String("") : origRec.getValue((String)cols.get(i));                        
                        Object newValue = (newRec.getValue((String)cols.get(i)) == null) ? new String("") : newRec.getValue((String)cols.get(i));                                                
                        if (!origValue.equals(newValue)) {    
                            // Read record from THISTORYCOLUMN first
                            res = sess.createCriteria(HistoryColumn.class)
                                .add(Restrictions.eq(HistoryColumn.TABLENAME, PlantloreConstants.ENTITY_VILLAGE))
                                .add(Restrictions.eq(HistoryColumn.COLUMNNAME, (String)cols.get(i)))
                                .scroll();
                            if (!res.next()) {
                                logger.error("tHistoryColumn doesn't contain required data");
                                DBLayerException ex = new DBLayerException("tHistoryColumn doesn't contain required data");
                                ex.setError(ex.ERROR_DB, PlantloreConstants.ENTITY_VILLAGE);
                                throw ex;                                
                            }
                            Object[] colNames = res.get();
                            // Save the HistoryChange object
                            sess.save(historyChange);
                            // Save record into THISTORY
                            HistoryRecord hist = new HistoryRecord();
                            hist.setHistoryChange(historyChange);
                            hist.setHistoryColumn((HistoryColumn)colNames[0]);     
                            String origValueString = (origRec.getValue((String)cols.get(i)) == null) ? null : origValue.toString(),
                        			   newValueString = (newRec.getValue((String)cols.get(i)) == null) ? null : newValue.toString(); 
                   			hist.setOldValue(origValueString);
                        	hist.setNewValue(newValueString);  
                        	hist.setOldRecordId(0);
                            sess.save(hist);
                        }
                    }
                }
            }                                  
        }
    }

    /**
     *  Method for creating new database user using CREATE USER statement. This method can only be called
     *  as a part of long running transaction (such as executeInsertInTransaction() method).
     *
     *  @param name     Name of the new user
     *  @param password Password for the new user
     *  @param isAdmin  Flag whether created user will or will not be an administrator (admin can create other users)
     *  @throws DBLayerException In case we do not have sufficient rights, are not connected to the DB or the 
     *                           execution of CREATE USER statement failed
     *  @throws RemoteException In case connection to the server was lost
     */
    public void createUser(String name, String password, boolean isAdmin) throws DBLayerException, RemoteException {
        // Check whether we are connected to the database
        checkConnection();
        // Check whether the user is administrator - only admin can modify users
        if (this.rights.getAdministrator() != 1) {
            logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_USER);
            throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);
        }
        // TODO: This is PostgreSQL specific. Think of a way how to provide different statements for
        //       different databases
        String admin = "";
        String role = "Plantlore_Role_User";
        if (isAdmin) {
            admin = " CREATEUSER";
            role = "Plantlore_Role_Admin";
        }        
        try {
            Connection conn = txSession.connection();                    
            PreparedStatement pstmt = conn.prepareStatement("CREATE USER " + this.userPrefix + name+ " WITH PASSWORD '" +password+ "' "+admin);
            pstmt.execute();
            PreparedStatement pstmtGrant = conn.prepareStatement("GRANT "+role+" TO  " + this.userPrefix + name);   
            pstmtGrant.execute();
        } catch (HibernateException e) {
            logger.warn("Unable to retrieve JDBC connection from the Hibernate session. Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Exception.CreateUser"), DBLayerException.ERROR_SAVE, e);            
        } catch (SQLException e) {
            logger.warn("Unable to create new database user using CREATE USER statement. Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Exception.CreateUser"), DBLayerException.ERROR_SAVE, e);            
        }
    }
    
    /**
     *  Method for modifying the database user using ALTER USER statement. This method can only 
     *  be called as a part of long running transaction (such as executeInsertInTransaction() method).
     *  Only password and admin flag can be modified. Name of the user cannot be modified.
     *
     *  @param name     Name of the user to modify
     *  @param password New password for the user. Leave blank or null if not modified.
     *  @param isAdmin  Flag whether the user should or should not be an administrator (admin can 
     *                  create other users)
     *  @parem changeRight Flag whether the right of user has been changed 
     *  @throws DBLayerException In case we do not have sufficient rights, are not connected to the 
     *                           DB or the execution of ALTER USER statement failed
     *  @throws RemoteException In case connection to the server was lost
     */    
    public void alterUser(String name, String password, boolean isAdmin, boolean changeRight ) throws DBLayerException, RemoteException {
        // Check whether we are connected to the database
        checkConnection();
        // Check whether the user is administrator - only admin can modify users
        if (this.rights.getAdministrator() != 1) {
            logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_USER);
            throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);
        }
        // TODO: This is PostgreSQL specific. Think of a way how to provide different statements for
        //       different databases
        try {
            Connection conn = txSession.connection();            
            // Now set CREATEUSER/NOCREATUSER flag
            String admin;
            String role;
            String roleOld;
            if (isAdmin) {
                admin = "CREATEUSER";
                role = "Plantlore_Role_Admin";
                roleOld = "Plantlore_Role_User";
            } else {
                admin = "NOCREATEUSER";
                role = "Plantlore_Role_User";
                roleOld = "Plantlore_Role_Admin";
            }            
            // In case password has changed, execute statement to change the password
            // In case admin right has changed, execute statement to change the access permission
            if ((password != null) && (!password.equals(""))) {                                    
                PreparedStatement pstmt = conn.prepareStatement("ALTER USER " + this.userPrefix +name+ " WITH PASSWORD '" +password+ "' " +admin);                
                pstmt.execute();
                if (changeRight) {
                    PreparedStatement pstmtRevoke = conn.prepareStatement("REVOKE "+roleOld+" FROM "+ this.userPrefix +name);                
                    pstmtRevoke.execute();
                    PreparedStatement pstmtGrant = conn.prepareStatement("GRANT "+role+" TO  " + this.userPrefix + name);   
                    pstmtGrant.execute();
                }
            } else {                
                PreparedStatement pstmt = conn.prepareStatement("ALTER USER " + this.userPrefix +name+ " " +admin);                
                pstmt.execute();  
                PreparedStatement pstmtRevoke = conn.prepareStatement("REVOKE "+roleOld+" FROM "+ this.userPrefix +name);                
                pstmtRevoke.execute();
                PreparedStatement pstmtGrant = conn.prepareStatement("GRANT "+role+" TO  " + this.userPrefix + name);   
                pstmtGrant.execute();
            }            
        } catch (HibernateException e) {
            logger.warn("Unable to retrieve JDBC connection from the Hibernate session. Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Exception.AlterUser"), DBLayerException.ERROR_SAVE, e);
        } catch (SQLException e) {
            logger.warn("Unable to alter the database user using ALTER USER statement. Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Exception.AlterUser"), DBLayerException.ERROR_SAVE, e);
        }        
    }
    
    /**
     *  Method for deleting database user using DROP USER statement. This method can only 
     *  be called as a part of long running transaction (such as executeInsertInTransaction() method).
     *  DROP USER statement will fail in case user with the given username does not exist or in case
     *  the given user is an owner of some database.
     *
     *  @param name     Name of the user to drop
     *  @throws DBLayerException In case we do not have sufficient rights, are not connected to the 
     *                           DB or the execution of DROP USER statement failed
     *  @throws RemoteException In case connection to the server was lost
     */    
    public void dropUser(String name) throws DBLayerException, RemoteException {
        // Check whether we are connected to the database
        checkConnection();
        // Check whether the user is administrator - only admin can modify users
        if (this.rights.getAdministrator() != 1) {
            logger.warn("User doesn't have sufficient rights for this operation. Entity: "+PlantloreConstants.ENTITY_USER);
            throw new DBLayerException(L10n.getString("Error.InsufficientRights"), DBLayerException.ERROR_RIGHTS);
        }
        try {
            Connection conn = txSession.connection();        
            PreparedStatement pstmt = conn.prepareStatement("DROP USER " + this.userPrefix + name);            
            pstmt.execute();
        } catch (HibernateException e) {
            logger.warn("Unable to retrieve JDBC connection from the Hibernate session. Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Exception.DropUser"), DBLayerException.ERROR_SAVE, e);            
        } catch (SQLException e) {
            logger.warn("Unable to drop database user using the DROP USER statement. Details: "+e.getMessage());
            throw new DBLayerException(L10n.getString("Exception.DropUser"), DBLayerException.ERROR_SAVE, e);            
        }
    }
            
    /**
     *  Return number of open database connections (instances of Hibernate Session class)
     *  @return number of open database connections
     *  @throws RemoteException in case network connection failed
     */    
    public int getConnectionCount() throws RemoteException {
        return sessions.size();
    }
    
    /**
     *  Method used to create new database in the PostgreSQL system.
     *  @param dbname name of the database to create
     *  @throws DBLayerException in case database error occurred
     *  @throws RemoteException in case network error occurred
     */
    public void createDatabase(String dbname) throws DBLayerException, RemoteException {
        // Check whether we are connected and obtain a session
        checkConnection();     
        // Open new session
        Session session = sessionFactory.openSession();
        // Obtain JDBC connection from session
        Connection con = session.connection();
        // Execute CREATE DATABASE statement
        try {
            con.setAutoCommit(true);
            Statement stmt = con.createStatement();
            logger.debug("----- SQL: CREATE DATABASE "+dbname+" WITH OWNER = plantlore_role_admin ENCODING = 'UTF8' TABLESPACE = pg_default");
            stmt.execute("CREATE DATABASE "+dbname+" WITH OWNER = plantlore_role_admin ENCODING = 'UTF8' TABLESPACE = pg_default");            
        } catch (SQLException e) {
            // Deal with the exception
            logger.error("SQLException caught while executing SQL script. Details: "+e.getMessage()+"; SQL State: "+e.getSQLState());
            throw new DBLayerException(L10n.getString("Error.CreateDatabase"), DBLayerException.ERROR_CREATEDB, e);            
        }
    }
    
    /**
     *  Executes given SQL script. This method is used for creating new users in the new database nad createing tables.
     *  @param scriptid id of the script to execute
     *  @param dbname name of the database to execute the script in
     *  @param username username used to connect to the database
     *  @password password used to connect to the database
     *  @throws DBLayerException in case database error occurred
     *  @throws RemoteException in case network error occurred
     */
    public void executeSQLScript(int scriptid, String dbname, String username, String password) throws DBLayerException, RemoteException {
        String file;
        switch (scriptid) {
            case DBLayer.CREATE_USERS: file = "net/sf/plantlore/config/database/createusers_postgres.sql";
                    break;
            case DBLayer.CREATE_TABLES: file = "net/sf/plantlore/config/database/createtables_postgres.sql";
                    break;
            default: file = "";
                    break;
        }
        // Check whether we are connected and obtain a session
        checkConnection();  
        // Open new session
        Session session = sessionFactory.openSession();
        // Obtain JDBC connection from session
        Connection con = session.connection();                
        // Read the script file from resources - check the existing roles first. If the roles exist, 
        // skip this step completely
        boolean skipRoles = false;
        try {
            if (scriptid == DBLayer.CREATE_USERS) {        
                Statement st = con.createStatement();
                st.execute("SELECT count(*) AS rolecount FROM pg_roles WHERE rolname = 'plantlore_role_admin'");
                ResultSet rs = st.getResultSet();
                rs.next();
                int rolecount = rs.getInt("rolecount");
                if (rolecount > 0) {
                    skipRoles = true;
                }
            }        
        } catch (SQLException e) {
            // Close session
            session.close();
            // Deal with the exception
            logger.error("SQLException caught while executing SQL script. Details: "+e.getMessage()+"; SQL State: "+e.getSQLState());
            throw new DBLayerException(L10n.getString("Error.CreateDatabase"), DBLayerException.ERROR_CREATEDB, e);
        }
        StringBuffer sql = new StringBuffer();
        if (!skipRoles) {
            try {
                ClassLoader cl = HibernateDBLayer.class.getClassLoader();
                URL sqlfile = cl.getResource(file);
                BufferedReader in = new BufferedReader(new InputStreamReader(sqlfile.openStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    sql.append(str);
                }
                in.close();
            } catch (IOException e) {
                // Close session
                session.close();
                // Deal with I/O Exception
                logger.error("I/O Exception caught. Cannot read sql script from file. Details: "+e.getMessage());
                throw new DBLayerException(L10n.getString("Error.CreateDatabase"), DBLayerException.ERROR_CREATEDB, e);
            }
        }
        // In case we are creating users, add statement to create first user for accesing plantlore
        if (scriptid == DBLayer.CREATE_USERS) {
            sql.append("CREATE USER "+dbname+"_"+username+" PASSWORD '"+password+"' SUPERUSER;");
            sql.append("CREATE USER "+dbname+"_www PASSWORD 'plantlore';");
            sql.append("GRANT Plantlore_Role_Admin TO "+dbname+"_"+username+";");
            sql.append("GRANT Plantlore_Role_www TO "+dbname+"_www;");            
        }
        // In case we are creating tables, insert data into TUSER table so that the user can login
        if (scriptid == DBLayer.CREATE_TABLES) {
            sql.append("INSERT INTO tright (cid, cadministrator, cadd, ceditall) VALUES (1,1, 1, 1);");
            sql.append("INSERT INTO tuser (clogin, cfirstname, csurname, cwholename, ccreatewhen, crightid) VALUES ('"+username+"', 'Admin', 'Admin', 'Admin Admin', 'NOW', 1);");
            // Insert default values to tphytochoria and tterritories tables
            sql.append("INSERT INTO tphytochoria (ccode, cname) VALUES ('---', '"+L10n.getString("Database.Phytochorion.NotSpecified")+"');");
            sql.append("INSERT INTO tterritories (cname) VALUES ('"+L10n.getString("Database.Territory.NotSpecified")+"');");
            // After the tables are created, insert unique ID identifier to the tUnitIDDatabase table
            UnitIdDatabase unitid = new UnitIdDatabase();
            unitid.setUnitIdDb(UniqueIDGenerator.generate());                
            if (unitid.getUnitIdDb() == null) {
                    logger.error("Failed to generate unique id for the database");
                    throw new DBLayerException(L10n.getString("Error.CreateDatabase"),  DBLayerException.ERROR_CREATEDB);
            }            
            sql.append("INSERT INTO tunitiddatabase (cid, cunitiddb) VALUES (1, '"+unitid.getUnitIdDb()+"')");
        }
        // Split the file with semicolon as the separator
        String[] statements = sql.toString().split(";");        
        // Execute batch SQL
        try {
            con.setAutoCommit(false);        
            Statement stmt = con.createStatement();
            for (int i=0;i<statements.length;i++) {
                logger.debug("----- SQL: "+statements[i]);
                stmt.addBatch(statements[i]);
            }
            int [] updateCounts = stmt.executeBatch();
            con.commit();
            con.setAutoCommit(true);                
        } catch (SQLException e) {
            // Deal with the exception
            logger.error("SQLException caught while executing SQL script. Details: "+e.getMessage()+"; SQL State: "+e.getSQLState());
            throw new DBLayerException(L10n.getString("Error.CreateDatabase"), DBLayerException.ERROR_CREATEDB, e);
        } finally {
            session.close();
        }
    }
    
    /**
     * This method is intended for final cleanup. <b>Do not call this method
     * yourself! The proper way for you to get rid of a DBLayer is to call
     * DBLayer.destroy() method!</b> <br/> Terminate all processes running in
     * this DBLayer, disconnect from the database and destroy all objects
     * created by this DBLayer. <br/> <b>After this the DBLayer will not be
     * capable of carrying out its duties.</b> <br/> This method is supposed to
     * be used by the DBLayerFactory exclusively.
     *
     * FIXME Think of a better mechanism that will hide it from users yet keep
     * it accessible to the DBLF.
     */
    public void shutdown() /* throws RemoteException */ {
    	
//    	if(undertaker != null) 
//    		for(SelectQuery sq : queries.values()) 
//    			try { UnicastRemoteObject.unexportObject(sq, true); }
//    			catch(NoSuchObjectException e) {/* Ignore it.*/}
//    	queries.clear();
    	
    	
    	// Some queries may still be open!
    	logger.debug("Closing unfinished select-queries... (there are " + sessions.size() + " select queries).");
    	Set<SelectQuery> openedQueries = new HashSet<SelectQuery>( sessions.keySet() );
    	for(SelectQuery query : openedQueries) { 
    		try {
    			closeQuery( query );    		
    		} catch(Exception e) {
    			// Maintain silence.
    		}
    	}
    	sessions.clear();
    	
    	logger.debug("Rolling back a possibly unfinished transaction...");
    	// Some transactions may still run!
    	if(txSession != null)
    		try {
    			rollbackTransaction();
    		} catch(Exception e) {
    			// Maintain silence.
    		}
    	
        // Check whether we are connected to the database
    	logger.debug("Closing the session factory itself...");
    	if (sessionFactory != null) { 
    		try {
    			sessionFactory.close();
    		} catch (Exception e) {
    			// Maintain silence...
    		}
    	}

    	logger.debug("Invalidating this database layer...");
    	sessionFactory = null;
    	
    	logger.info("Database layer destroyed properly. It will not be available any longer.");
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
		if(undertaker != null) 
			undertaker.bury(this); 
	}
	//===============================================================
	  
	
	@Override
	public String toString() {
		if(plantloreUser != null)
			return plantloreUser.getFirstName() + " " + 
				plantloreUser.getSurname() + 
				" (" + currentlyConnectedUser + ")"; 
		return currentlyConnectedUser ; 
	}
	
	/**
	 * @return description of this database layer: Name Surname (login).
	 */
	public String getDescription() {
		if(plantloreUser != null) {
			String name = plantloreUser.getFirstName(),
			surname = plantloreUser.getSurname(), 
			description = ((name != null) ? name + " " : "") + ((surname != null) ? surname : "");
			if(name == null && surname == null)
				description = currentlyConnectedUser;
			else
				description = description +	" (" + currentlyConnectedUser + ")";
			return description;
		}
		return currentlyConnectedUser ; 
	}
        
        
        /*===============================================================================
         *  It is most fortunate that God does not grant us every wish.
         *  For if He did most of my teammates would be pushing up daisies 
         *  here and there by now. As would all the people that created Swing.
         *===============================================================================*/
}
