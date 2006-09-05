package net.sf.plantlore.client.login;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Observable;
import net.sf.plantlore.client.MainConfig;
import net.sf.plantlore.common.DefaultExceptionHandler;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.common.record.User;

import org.apache.log4j.Logger;

import spin.Spin;

import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.DBLayerFactory;
import net.sf.plantlore.middleware.SelectQuery;

/**
 * Login is responsible for the following tasks:
 * <ul>
 * <li><b>management of the list of databases</b> - 
 * 			adding, editing, removing records from the list, 
 * 			and the invocation of methods ensuring the persistent storage of that list,</li>
 * <li><b>creation and initialization of a new dblayer</b> -
 * 			 only one at a time is active,</li>
 * <li><b>proper destruction of the current dblayer (logout)</b> - 
 * 			so as to make another connection possible</li>
 * <li><b>repeated connection (reconnect)</b> - 
 * 			to connect to the database again if the previous connection fails</li>
 * </ul>
 * 
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @author Jakub Kotowski (storage and retrieval of the list of databases)
 * @version 2.0
 */
public class Login extends Observable {
	
	/** The maximum number of usernames the System will store for each database record.*/
	public static final int MAX_NAMES = 5;
	public static final Object UPDATE_LIST = new Object();
	


	/** 
	 * The list of databases the User has accessed. This list is unique for every User
	 * and is stored in his home directory. 
	 */
	private ArrayList<DBInfo> dbinfos = new ArrayList<DBInfo>(10);
	
	/** The currently selected record. Null means nothing is selected. */
	private DBInfo selected = null;
	
	private MainConfig mainConfig = null;
	private DBLayerFactory factory = null;
	private DBLayer newDBLayer = null; 
	private DBLayerProxy proxyLayer;
	private Logger logger  = Logger.getLogger(this.getClass().getPackage().getName());
	
	private Right accessRights;
	private User plantloreUser;
	
	private Task lastConnectionTask;
	

	/**
	 * Create a new login model. The DBLayer factory will be used to produce 
	 * new DBLayers.
	 *  
	 * @param factory The factory that will be used to create a new DBLayer. 
	 * @param mainConfig The configuration tool that is capable of storing the list of databases.
	 */
	public Login(DBLayerFactory factory, MainConfig mainConfig) {
		this.factory = factory;
		this.mainConfig = mainConfig;
		load();
		this.proxyLayer = new DBLayerProxy();
	}
	
	
	/**
	 * Retrieve saved information about the database connections.
	 */
	protected void load() {
		logger.debug("Loading the stored list of databases.");
		
		for (DBInfo savedDBInfo: mainConfig.getDBinfos())
			dbinfos.add(savedDBInfo);
		this.setChanged(); this.notifyObservers(UPDATE_LIST);

		// Restore the last selected record.
		selected = mainConfig.getSelected();
		if(selected != null) {
			this.setChanged();
			this.notifyObservers( selected.clone() );
		}
	}
	
	/**
	 * Store the list of database connections for further usage.
	 */
	protected void save() {
		logger.debug("Saving the list of databases.");
		
		mainConfig.setDBInfos(dbinfos, selected);
		try {
			mainConfig.save();
		}catch(IOException e) {
			logger.warn("Saving failed. "+e.getMessage());
		}
	}
	

	/**
	 * Create a new record, add it to the list of connections, and save that information for
	 * future use.
	 * 
	 * @see DBInfo
	 */
	synchronized public void createRecord(
			String alias, 
			String host, 
			int port, 
			String databaseType, 
			int databasePort, 
			String databaseIdentifier, 
			String databaseParameter) {

		DBInfo r = new DBInfo(
				alias, host, port, databaseType, databasePort, databaseIdentifier, databaseParameter,
				new String[MAX_NAMES] );
		dbinfos.add(r);
		logger.debug("New database record has been created " + r);
		save();
		setChanged(); notifyObservers(UPDATE_LIST);
	}
	
	/**
	 * Delete the selected record from the list.
	 *
	 */
	synchronized public void deleteSelectedRecord() {
		if(selected == null) return;
		dbinfos.remove(selected);
		logger.debug("The selected record has been removed " + selected);
		selected = null;
		save();
		this.setChanged(); this.notifyObservers(UPDATE_LIST);
	}
	
	/**
	 * Update the selected record, and save that information for
	 * future use.
	 * 
	 * @see DBInfo
	 */
	synchronized public void updateSelectedRecord(
			String alias, 
			String host, 
			int port, 
			String databaseType, 
			int databasePort, 
			String databaseIdentifier, 
			String databaseParameter ) {

		if(selected == null) return;
		
		selected.alias = alias ;
		selected.host = host; 
		selected.port = port;
		selected.databaseType = databaseType; 
		selected.databasePort = databasePort ;
		selected.databaseIdentifier = databaseIdentifier ;
		selected.databaseParameter = databaseParameter; 
		
		save();
		logger.debug("The selected record has been updated " + selected);
		this.setChanged(); 
		this.notifyObservers(UPDATE_LIST);
	}
	
	/**
	 * @return The list of all records the User has created.
	 */
	synchronized public DBInfo[] getRecords() {
		return dbinfos.toArray(new DBInfo[0]);
	}
	
	private int lastIndex = Integer.MIN_VALUE;
	
	/**
	 * Set the selected record.
	 * 
	 * @param index	The index of the selected record. Zero means first. 
	 * Negative means nothing gets selected (a.k.a. deselect).
	 */
	synchronized public void setSelected(int index) {
		if(index == lastIndex) 
			return;
		else if(index >= 0) 
			selected = dbinfos.get(index); 
		else 
			selected = null;
		
		lastIndex = index;
		
		logger.debug("Selected database is " + selected);
		this.setChanged();
		if(selected != null)
			this.notifyObservers( selected.clone() );
	}
	
	/**
	 * @return	The selected record.
	 */
	synchronized public DBInfo getSelected() {
		return selected;
	}
	
	
	/**
	 * Once the connection is established and the database layer is
	 * initialized, Login must inform its observers so that they can
	 * update their database layers.
	 */
	private void announceConnection() {
		logger.debug("New database layer created " + proxyLayer);
		setChanged(); 
		notifyObservers(proxyLayer);
		logger.debug("Observer notification finished.");
	}
	

	
	/**
	 * Create a task that will build the connection to the selected database. 
	 * <br/>
	 * <b>Warning:</b>If there is another (previously created) DBLayer, 
	 * it will be destroyed using the <code>logout()</code> method. 
	 * 	  
	 * @param name The account name (used to access the database).  
	 * @param password The password to the account.
	 * 
	 * @see #logout()
	 */
	synchronized public Task createConnectionTask(String name, String password) {
		if(selected == null) {
			logger.debug("The System cannot create a connection when nothing was selected!");
			return null;
		}
		
		logout();

		// The current username is moved to the top of the list of names :) Nice feature.
		selected.promoteUser(name);
		// Save the current state.
		save();
		
		return lastConnectionTask = new ConnectionTask( selected.clone(), name, password );

	}
	
	
	/**
	 * The task that performs the database layer "creation" 
	 * (using the supplied DatabaseLayerFactory - so in fact it is the Factory that creates
	 * that DBLayer)
	 * and it's initialization. Yet it is not the database layer that is returned - 
	 * it is a special wrapper that performs further access checks and makes the renewal
	 * of the database layer a little bit easier. 
	 * 
	 * @author Erik Kratochvíl (discontinuum@gmail.com)
	 * @since 2006-08-30
	 */
	private class ConnectionTask extends Task {
		
		private DBInfo dbinfo;
		private transient String name, password;
		
		/**
		 * Create a new connection task.
		 * 
		 * @param dbinfo	A holder object storing all neccessary information for the connection.
		 * @param name		The account name (used to access the database).
		 * @param password	The password to the account.
		 */
		public ConnectionTask(DBInfo dbinfo, String name, String password) {
			this.dbinfo = dbinfo;
			this.name = name;
			this.password = password;
		}
		
				
		@Override
		public Object task() throws Exception {
			
			try {
				if(isCanceled())
					throw new Exception(L10n.getString("Common.Canceled"));
				
				// Create a new database layer ~ ask the DBLayerFactory to create it for us..
				logger.debug("Asking the DBLayerFactory for a new DBLayer @ " + dbinfo.host + ":" + dbinfo.port);
				setStatusMessage( L10n.getString("Login.Connecting") );
				newDBLayer = factory.create( dbinfo );
				if(isCanceled())
					throw new Exception(L10n.getString("Common.Canceled"));
				
				logger.debug("Connection successful.");
				setStatusMessage( L10n.getString("Login.Connected") );
				
				// Initialize the database layer.
				setStatusMessage( L10n.getString("Login.InitializingDBLayer") );
				logger.debug("Initializing that DBLayer.");
				
				User init = newDBLayer.initialize(dbinfo.getDatabaseIdentifier(), name, password);
				if(isCanceled())
					throw new DBLayerException(L10n.getString("Common.Canceled"));
				plantloreUser = init;
				accessRights = init.getRight();
				
				logger.debug("Initialization successful.");
			} 
			catch (Exception e) {
				logger.error("The initialization of the DBLayer failed! " + e.getMessage());
				// If the initialization of the DBLayer failed, the uninitialized DBLayer must be destroyed!
				// Otherwise, the server's policy may not allow another connection from this client!
				if(newDBLayer != null)
					try {
						factory.destroy(newDBLayer);
					} catch(Exception re) {
						// There's nothing we can do; 
						// the server is probably in trouble, or the network connection failed. 
					}
				// Re-throw the exception so that the view is updated as well.
				throw e;
			}
			
			proxyLayer.wrap( newDBLayer );
			
			setStatusMessage( L10n.getString("Login.DBLayerInitialized") );
			logger.debug("DBLayer initialized.");
			
		
			fireStopped(null);
			
			// Everything went fine - 
			// there is a new DBLayer and the observers of Login must know about it.
			announceConnection();
			
			DefaultExceptionHandler.enableReconnect();
			
			return null;
		}
		
	}
	
	
	
	/**
	 * Disconnect the User from the current database. 
	 * The database connection will cease to exist - 
	 * everyone using the database layer will get an exception from now on. 
	 */
	public void logout() {
		if(newDBLayer != null) {
			try {
				factory.destroy(newDBLayer);
			} catch(RemoteException e) {
				logger.warn("Unable to disconnect from the server. " + e.getMessage());
				// Not this time, this is supposed to be silent.
			} finally {
				newDBLayer = null; accessRights = null; plantloreUser = null;
				proxyLayer.wrap( null );
				DefaultExceptionHandler.disableReconnect();
				logger.info("The client disconnected itself from the server. The communication may no longer be possible.");
			}
		}
	}
	
	
	
	/**
	 * 
	 * @return The last connection task, ie. the last task that was used to create a connection
	 * to the database. Restarting that task will lead to the database layer renewal, 
	 * however, this may lead to problems as old queries and resultset identifiers will
	 * not be valid with the new database layer!
	 */
	synchronized public Task getLastConnectionTask() {
		return lastConnectionTask;
	}
	
		
	/**
	 * @return The new DBLayer that has been created.  
	 */	
	synchronized public DBLayer getDBLayer() { 
		return proxyLayer; 
	}
	
	/**
	 * @return The currently logged user.
	 */
	synchronized public User getLoggedUser() {
		return plantloreUser;
	}
	
	/**
	 * @return The list of access rights as returned by the database layer
	 * after initialization.
	 */
	synchronized public Right getAccessRights() {
		return accessRights;
	}
	
	
	
	/**
	 * The DBLayerProxy constitutes a simple wrapper of the received database layer.
	 * It performs some additional access checks.
	 * <br/>
	 * The advantage of the wrapper is that its wrapped database layer can be easily
	 * switched without clients noting anything. 
	 * However, in some cases clients using the wrapper must be notified 
	 * in order to restart their queries and refresh their resultsets.
	 * 
	 * @author Erik Kratochvíl (discontinuum@gmail.com)
	 * @since 2006-08-30
	 * 
	 * @see DBLayer
	 */
	private class DBLayerProxy implements DBLayer {
		
		private DBLayer wrappedDBLayer;
		
		private void verifyValidity() {
			if(wrappedDBLayer == null)
				throw new Error(L10n.getString("Error.SloppyProgramming"));
		}
		
				
		synchronized void wrap(DBLayer db) {
			if(db == null)
				this.wrappedDBLayer = db;
			else
				this.wrappedDBLayer = (DBLayer) Spin.off( db );
		}

		synchronized public User initialize(String dbID, String user, String password) throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.initialize(dbID, user, password);
		}

		synchronized public void initializeNewDB(String dbID, String user, String password) throws DBLayerException, RemoteException {
			verifyValidity();
			wrappedDBLayer.initializeNewDB(dbID, user, password);
		}
                
		synchronized public void setLanguage(String locale) throws DBLayerException, RemoteException {
			verifyValidity();
			wrappedDBLayer.setLanguage(locale);
		}

		synchronized public Record executeInsert(Object data) throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.executeInsert(data);
		}

		synchronized public void executeDelete(Object data) throws DBLayerException, RemoteException {
			verifyValidity();
			wrappedDBLayer.executeDelete(data);
		}

		synchronized public Record executeUpdate(Object data) throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.executeUpdate(data);
		}

		synchronized public Record executeInsertHistory(Object data) throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.executeInsertHistory(data);
		}

		synchronized public void executeDeleteHistory(Object data) throws DBLayerException, RemoteException {
			verifyValidity();
			wrappedDBLayer.executeDeleteHistory(data);
		}

                synchronized public void executeDeleteInTransactionHistory(Object data) throws DBLayerException, RemoteException {
			verifyValidity();   
			wrappedDBLayer.executeDeleteInTransactionHistory(data);
		}
                
		synchronized public Record executeUpdateHistory(Object data) throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.executeUpdateHistory(data);
		}

		synchronized public Record executeUpdateInTransactionHistory(Object data) throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.executeUpdateInTransactionHistory(data);
		}

		synchronized public Object[] more(int resultId, int from, int to) throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.more(resultId, from, to);
		}

		synchronized public Object[] next(int resultId) throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.next(resultId);
		}

		synchronized public int getNumRows(int resultId) throws RemoteException {
			verifyValidity();
			return wrappedDBLayer.getNumRows(resultId);
		}

		synchronized public SelectQuery createQuery(Class classname) throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.createQuery(classname);
		}

		synchronized public SelectQuery createSubQuery(Class classname, String alias) throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.createSubQuery(classname, alias);
		}

		synchronized public int executeQuery(SelectQuery query) throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.executeQuery(query);
		}

		synchronized public void closeQuery(SelectQuery query) throws RemoteException, DBLayerException {
			verifyValidity();
			wrappedDBLayer.closeQuery(query);
		}

		synchronized public int conditionDelete(Class tableClass, String column, String operation, Object value) throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.conditionDelete(tableClass, column, operation, value);
		}

		synchronized public User getUser() throws RemoteException {
			verifyValidity();
			return wrappedDBLayer.getUser();
		}

		synchronized public Right getUserRights() throws RemoteException {
			verifyValidity();
			return wrappedDBLayer.getUserRights();
		}

		synchronized public boolean beginTransaction() throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.beginTransaction();
		}

		synchronized public boolean commitTransaction() throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.commitTransaction();
		}

		synchronized public boolean rollbackTransaction() throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.rollbackTransaction();
		}

		synchronized public Record executeInsertInTransaction(Object data) throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.executeInsertInTransaction(data);
		}
               
		synchronized public Record executeInsertInTransactionHistory(Object data) throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.executeInsertInTransactionHistory(data);
		}

		synchronized public Record executeUpdateInTransaction(Object data) throws DBLayerException, RemoteException {
			verifyValidity();
			return wrappedDBLayer.executeUpdateInTransaction(data);
		}

		synchronized public void executeDeleteInTransaction(Object data) throws DBLayerException, RemoteException {
			verifyValidity();
			wrappedDBLayer.executeDeleteInTransaction(data);
			
		}

		synchronized public void createUser(String name, String password, boolean isAdmin) throws DBLayerException, RemoteException {
			verifyValidity();
			wrappedDBLayer.createUser(name, password, isAdmin);
		}

		synchronized public void alterUser(String name, String password, boolean isAdmin, boolean changeRight) throws DBLayerException, RemoteException {
			verifyValidity();
			wrappedDBLayer.alterUser(name, password, isAdmin, changeRight);
			
		}

		synchronized public void dropUser(String name) throws DBLayerException, RemoteException {
			verifyValidity();
			wrappedDBLayer.dropUser(name);
			
		}

		synchronized public int getConnectionCount() throws RemoteException {
			//verifyValidity();
			if( wrappedDBLayer != null )
				return wrappedDBLayer.getConnectionCount();
			return -1;
		}

		synchronized public void shutdown() throws RemoteException {
			throw new Error("It is forbidden to call this method. The proper way to destroy a database layer is the logout() method!");
		}
		
                synchronized public void createDatabase(String dbname) throws RemoteException, DBLayerException {
			verifyValidity();
			wrappedDBLayer.createDatabase(dbname);
                }

                synchronized public void executeSQLScript(int scriptid, String dbname, String username, String password) throws RemoteException, DBLayerException {
			verifyValidity();
			wrappedDBLayer.executeSQLScript(scriptid, dbname, username, password);
                }                
                
		@Override
		public String toString() {
			return "Safety wrapper of " + super.toString();
		}
		
	}
        
}
