/*
 * DBLayer.java
 *
 * Created on 14. leden 2006, 23:25
 *
 */

package net.sf.plantlore.middleware;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.common.exception.DBLayerException;

/**
 * Interface for database access.
 * 
 * TODO: Zdokumentovat metody interface!
 *
 * @author Tomáš Kovařík, Erik Kratochvíl
 */
public interface DBLayer extends Remote, Serializable {
	
    /**
     *  Initialize database connection. Authenticate user and Load rights of this user
     *
     *  @param dbID identifier of the database we want to connect to
     *  @param user username for the access to plantlore on the server
     *  @param password password for the access to plantlore on the server
     *  @return array with two objects - User object with logged in user (index 0) and user's rights 
     *          (Right object, index 1)
     *  @throws DBLayerException when the database connection cannot be initialized
     */
    public Object[] initialize(String dbID, String user, String password) throws DBLayerException, RemoteException;

    /**
     *  Insert data into the database.
     *
     *  @param data data to insert (one of the data holder objects)
     *  @return identifier (primary key) of the inserted row
     *  @throws DBLayerException when saving data into the database fails
     */    
    public int executeInsert(Object data) throws DBLayerException, RemoteException;
    
    /**
     *  Delete data from the database.
     *
     *  @param data data we want to delete (must be one of the holder objects)
     *  @throws DBLayerException when deleting data fails
     */    
    public void executeDelete(Object data) throws DBLayerException, RemoteException;

    /**
     *  Update data in the database.
     *
     *  @param data to update (must be one of the holder objects)
     *  @throws DBLayerException when updating data fails
     */    
    public void executeUpdate(Object data) throws DBLayerException, RemoteException;

    /**
     *  Insert data into the database without modifying history tables
     *
     *  @param data data to insert (one of the data holder objects)
     *  @return identifier (primary key) of the inserted row
     *  @throws DBLayerException when saving data into the database fails
     */    
    public int executeInsertHistory(Object data) throws DBLayerException, RemoteException;
    
    /**
     *  Delete data from the database without modifying history tables
     *
     *  @param data data we want to delete (must be one of the holder objects)
     *  @throws DBLayerException when deleting data fails
     */    
    public void executeDeleteHistory(Object data) throws DBLayerException, RemoteException;
        
    /**
     *  Update data in the database without modifying history tables.
     *
     *  @param data to update (must be one of the holder objects)
     *  @throws DBLayerException when updating data fails
     */    
    public void executeUpdateHistory(Object data) throws DBLayerException, RemoteException;

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
    public void executeUpdateInTransactionHistory(Object data) throws DBLayerException, RemoteException;
    
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
    public Object[] more(int resultId, int from, int to) throws DBLayerException, RemoteException;

    /**
     *  Get next result from the current result set.
     *
     *  @param resultId id of the result from which we want to read
     *  @return next record from the active result set. Array can contain more objects in case
     *          associated entities were fetched.
     *  @throws DBLayerException when loading the results fails
     */    
    public Object[] next(int resultId) throws DBLayerException, RemoteException;

    /**
     *  Get the number of rows returned in the result.
     *
     *  @param  resultId id of the result we want the number of rows for     
     *  @return number of rows in the given result
     */    
    public int getNumRows(int resultId) throws RemoteException;
       
    /**
     *  Close the DBLayer. Close the session factory
     *
     *  @throws DBLayerException when closing session fails
     */    
    public void close() throws DBLayerException, RemoteException;
    
    /**
     *  Start building a select query.
     *
     *  @param classname entity we want to select from the database (given holder object class)
     *  @return an instance of <code>SelectQuery</code> used for building a query by client
     *
     */
    public SelectQuery createQuery(Class classname) throws DBLayerException, RemoteException;

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
    public SelectQuery createSubQuery(Class classname, String alias) throws DBLayerException, RemoteException;
    
    /**
     *  Execute constructed SELECT query. Only executes query, for retrieving results use next() and more()
     *
     *  @param query query we want to execute
     *  @throws DBLayerException when selecting records from the database fails
     */    
    public int executeQuery(SelectQuery query) throws DBLayerException, RemoteException;
    
    /**
     *  Close the select query.
     *
     *  @param query query we want to close
     */    
    public void closeQuery(SelectQuery query) throws RemoteException;

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
    public int conditionDelete(Class tableClass, String column, String operation, Object value) throws DBLayerException, RemoteException;

    /**
     *  Method to get the currently logged user. Returns null if there is no user logged in.
     *  @return currently logged in user or null, if there is no user logged in.
     */    
    public User getUser() throws RemoteException;
    
    /**
     *  Method to get the rights of the currently logged in user. Returns null if there is no user logged in
     *  @return rights of the currently logged in user or null if there is no user logged in.
     */    
    public Right getUserRights() throws RemoteException;

    /**
     *  Begin long running transaction. in the current implementation, there can be only one long
     *  running transaction at a time.
     *
     *  @return true if transaction was started, false if there already is a long running transaction
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the update
     *  @throws RemoteException in case server connection failed
     */    
    public boolean beginTransaction() throws DBLayerException, RemoteException;

    /**
     *  Commit long running transaction. In the current implementation, there can be only one long
     *  running transaction at a time.
     *
     *  @return true if commit was successful, false if there is no long running transaction
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the update
     *  @throws RemoteException in case server connection failed
     */     
    public boolean commitTransaction() throws DBLayerException, RemoteException;
            
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
    public boolean rollbackTransaction() throws DBLayerException, RemoteException;    
    
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
    public int executeInsertInTransaction(Object data) throws DBLayerException, RemoteException;
            
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
    public int executeInsertInTransactionHistory(Object data) throws DBLayerException, RemoteException;
    
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
    public void executeUpdateInTransaction(Object data) throws DBLayerException, RemoteException;
    
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
    public void executeDeleteInTransaction(Object data) throws DBLayerException, RemoteException;
                        
    /**
     * This method is intended for final cleanup. <b>Do not call this method
     * yourself! The proper way for you to get rid of a DBLayer is to call
     * DBLayer.destroy() method!</b> <br/> Terminate all processes running in
     * this DBLayer, disconnect from the database and destroy all objects
     * created by this DBLayer. <br/> <b>After this the DBLayer will not be
     * capable of carrying out its duties.</b> <br/> This method is supposed to
     * be used by the DBLayerFactory exclusively.
     */
    public void shutdown() throws RemoteException;
    
    public void destroy() throws RemoteException;
        
}
