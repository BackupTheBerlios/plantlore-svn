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
import net.sf.plantlore.server.DBLayerException;

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
    public SelectQuery createQuery(Class classname) throws RemoteException;

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
    public void closeQuery(SelectQuery query);

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
    public int conditionDelete(Class tableClass, String column, String operation, Object value) throws DBLayerException;

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
        
    public void shutdown() throws RemoteException;
        
}
