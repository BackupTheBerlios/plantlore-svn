﻿/*
 * DBLayer.java
 *
 * Created on 14. leden 2006, 23:25
 *
 */

package net.sf.plantlore.middleware;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import net.sf.plantlore.common.record.Record;

import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.common.exception.DBLayerException;

/**
 * The interface for a database layer wrapper.
 * 
 * @author Tomáš Kovařík
 * @author kaimu
 */
public interface DBLayer extends Remote, Serializable {
	
    public static final int CREATE_USERS = 1;
    public static final int CREATE_TABLES = 2;
    
    /**
     *  Initialize database connection. Authenticate user and Load rights of this user
     *
     *  @param dbID identifier of the database we want to connect to
     *  @param user username for the access to plantlore on the server
     *  @param password password for the access to plantlore on the server
 t     *  @return array with two objects - User object with logged in user (index 0) and user's rights 
     *          (Right object, index 1)
     *  @throws DBLayerException when the database connection cannot be initialized
     */
    public User initialize(String dbID, String user, String password) throws DBLayerException, RemoteException;

    public void initializeNewDB(String dbID, String user, String password) throws DBLayerException, RemoteException;
    /**
     * The database layer performs several kinds of operations each of which may
     * fail at any time. In order to notify the User and present him with a reasonable
     * string describing the problem in the language that the User has set
     * on the (possibly remote) client, database layer must allow the client to set
     * the language mutation.
     * 
     * @param locale	The string describing the language so that it can be used
     * in L10n.load() method.
     */
    public void setLanguage(String locale) throws DBLayerException, RemoteException;
    
    /**
     *  Insert data into the database.
     *
     *  @param data data to insert (one of the data holder objects)
     *  @return inserted record including the primary key ID
     *  @throws DBLayerException when saving data into the database fails
     */    
    public Record executeInsert(Object data) throws DBLayerException, RemoteException;
    
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
     *  @return updated record
     *  @throws DBLayerException when updating data fails
     */    
    public Record executeUpdate(Object data) throws DBLayerException, RemoteException;

    /**
     *  Insert data into the database without modifying history tables
     *
     *  @param data data to insert (one of the data holder objects)
     *  @return inserted record including the primary key ID
     *  @throws DBLayerException when saving data into the database fails
     */    
    public Record executeInsertHistory(Object data) throws DBLayerException, RemoteException;
    
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
     *  @return updated record
     *  @throws DBLayerException when updating data fails
     */    
    public Record executeUpdateHistory(Object data) throws DBLayerException, RemoteException;

    /**
     *  Execute DB update using a long running transaction. For this method to work, it is neccessary
     *  to begin long running transaction using beginTransaction() method of this class.
     *
     *  This method checks whether the user has appropriate priviliges and DOES NOT save history
     *
     *  @param data holder object with the record we want to update
     *  @return updated record
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the update
     *  @throws RemoteException in case network connection failed
     */
    public Record executeUpdateInTransactionHistory(Object data) throws DBLayerException, RemoteException;
    
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
    public void closeQuery(SelectQuery query) throws RemoteException, DBLayerException;

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
     *  @return inserted record including the primary key ID
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the insert
     *  @throws RemoteException in case server connection failed
     */        
    public Record executeInsertInTransaction(Object data) throws DBLayerException, RemoteException;
                
    /**
     *  Execute DB insert using a long running transaction. For this method to work, it is neccessary
     *  to begin long running transaction using beginTransaction() method of this class.
     *
     *  This method checks whether the user has appropriate priviliges, DOES NOT save history and 
     *  updates the holder with the author (CCREATEDWHO) and time of creation (CREATEDWHEN).
     *
     *  @param data holder object with the record we want to insert
     *  @return inserted record including the primary key ID
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the insert
     *  @throws RemoteException in case server connection failed
     */    
    public Record executeInsertInTransactionHistory(Object data) throws DBLayerException, RemoteException;
    
    /**
     *  Execute DB update using a long running transaction. For this method to work, it is neccessary
     *  to begin long running transaction using beginTransaction() method of this class.
     *
     *  This method checks whether the user has appropriate priviliges and saves history
     *
     *  @param data holder object with the record we want to update
     *  @return updated record
     *  @throws DBLayerException in case we are not connected to the database or an error occurred
     *                           while executing the update
     *  @throws RemoteException in case network connection failed
     */
    public Record executeUpdateInTransaction(Object data) throws DBLayerException, RemoteException;
    
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
    public void executeDeleteInTransactionHistory(Object data) throws DBLayerException, RemoteException;    
    
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
    public void createUser(String name, String password, boolean isAdmin) throws DBLayerException, RemoteException;
    
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
    public void alterUser(String name, String password, boolean isAdmin, boolean changeRight) throws DBLayerException, RemoteException;
    
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
    public void dropUser(String name) throws DBLayerException, RemoteException;
        
    /**
     *  Return number of open database connections (instances of Hibernate Session class)
     *  @return number of open database connections
     *  @throws RemoteException in case network connection failed
     */
    public int getConnectionCount() throws RemoteException;
    
    /**
     *  Method used to create new database in the PostgreSQL system.
     *  @param dbname name of the database to create
     *  @throws DBLayerException in case database error occurred
     *  @throws RemoteException in case network error occurred
     */
    public void createDatabase(String dbname) throws DBLayerException, RemoteException;

    /**
     *  Executes given SQL script. This method is used for creating new users in the new database nad createing tables.
     *  @param scriptid id of the script to execute
     *  @param dbname name of the database to execute the script in
     *  @param username username used to connect to the database
     *  @password password used to connect to the database
     *  @throws DBLayerException in case database error occurred
     *  @throws RemoteException in case network error occurred
     */
    public void executeSQLScript(int scriptid, String dbname, String username, String password) throws DBLayerException, RemoteException;
    
    /**
     * This method is intended for final cleanup. <b>Do not call this method
     * yourself! The proper way for you to get rid of a DBLayer is to call
     * DBLayerFactory.destroy() method!</b> <br/> Terminates all processes running in
     * this DBLayer, disconnects from the database and destroys all objects
     * created by this DBLayer. <br/> 
     */
    public void shutdown() throws RemoteException;
    
    
    /**
     * 
     * @return	description of this database layer so that the user can easily identify it
     * @throws RemoteException	in case network error occurred
     */
    public String getDescription() throws RemoteException;
    
    
    /**
     * @return	the unique identifier of the database you are currently working with
     * @throws RemoteException	in case network error occurred
     */
    public String getUniqueDatabaseIdentifier() throws RemoteException;
           
}
