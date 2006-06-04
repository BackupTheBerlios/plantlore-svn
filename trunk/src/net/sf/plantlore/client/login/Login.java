package net.sf.plantlore.client.login;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Observable;
import net.sf.plantlore.client.MainConfig;
import net.sf.plantlore.common.SwingWorker;
import net.sf.plantlore.common.record.User;

import org.apache.log4j.Logger;

import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.DBLayerFactory;

/**
 * Login is responsible for the following tasks:
 * <ul>
 * <li><b>management of the list of databases</b> - 
 * 			adding, editing, removing records from the list, and the persistent storage of that list,</li>
 * <li><b>creating and initializing a new dblayer</b> - only one at a time is active,</li>
 * <li><b>destroying the current dblayer (logout)</b> - so as to make another connection possible</li>
 * </ul>
 * 
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @author Jakub Kotowski
 * @version 1.0
 */
public class Login extends Observable {
	
	/** The maximum number of usernames the System will store for each database record.*/
	public static final int MAX_NAMES = 5;
	public static final Object UPDATE_LIST = new Object();
	
	private SwingWorker worker;

	/** 
	 * The list of databases the User has accessed. This list is unique for every User
	 * and is stored in his home directory. 
	 */
	private ArrayList<DBInfo> dbinfo = new ArrayList<DBInfo>(10);
	
	/** The currently selected record. Null means nothing is selected. */
	private DBInfo selected = null;
	
	//private String  file = System.getProperty("user.home") + "/.plantlore/db.info.xml";
	
	private MainConfig mainConfig = null;
	private DBLayerFactory factory = null;
	private DBLayer dblayer;
	private Logger logger  = Logger.getLogger(this.getClass().getPackage().getName());
	
	private Right accessRights;
	private User plantloreUser;
	
	/**
	 * Create a new login model. The DBLayer factory will be used to produce 
	 * new DBLayers.
	 *  
	 * @param factory The factory that will be used to create a new DBLayer. 
	 */
	public Login(DBLayerFactory factory, MainConfig mainConfig) {
		this.factory = factory;
		this.mainConfig = mainConfig;
		load();
	}
	
	
	/**
	 * Load saved information about the database connections.
	 */
	protected void load() {
		logger.debug("Loading the stored list of databases.");
		
		for (DBInfo savedDBInfo: mainConfig.getDBinfos())
			dbinfo.add(savedDBInfo);
		this.setChanged(); this.notifyObservers(UPDATE_LIST);

		// Restore the last selected record.
		selected = mainConfig.getSelected();
		if(selected != null) {
			this.setChanged();
			this.notifyObservers( selected.clone() );
		}
	}
	
	/**
	 * Save the list of database connections for further usage.
	 */
	protected void save() {
		logger.debug("Saving the list of databases.");
		
		mainConfig.setDBInfos(dbinfo, selected);
		try {
			mainConfig.save();
		}catch(IOException e) {
			logger.warn("Saving failed. "+e.getMessage());
		}
	}
	

	/**
	 * Create a new record, add it to the list of connections and save that information for
	 * the future use.
	 * 
	
	 */
	synchronized public void createRecord(
			String alias, 
			String host, 
			int port, 
			String databaseType, 
			int databasePort, 
			String databaseIdentifier, 
			String databaseParameter, 
			String masterUser, 
			String masterPassword ) {

		DBInfo r = new DBInfo(
				alias, host, port, databaseType, databasePort, databaseIdentifier, databaseParameter,
				new String[MAX_NAMES], masterUser, masterPassword );
		dbinfo.add(r);
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
		dbinfo.remove(selected);
		logger.debug("The selected record has been removed " + selected);
		selected = null;
		save();
		this.setChanged(); this.notifyObservers(UPDATE_LIST);
	}
	
	/**
	 * Update the selected record.
	 * 
	 * @param alias	Alias of the database.
	 * @param host	Hostname of the computer where the server dwells.
	 * @param port	Port where the server listens.
	 * @param db		Identifier of the database to which the User wants to connect.
	 */
	synchronized public void updateSelectedRecord(
			String alias, 
			String host, 
			int port, 
			String databaseType, 
			int databasePort, 
			String databaseIdentifier, 
			String databaseParameter, 
			String masterUser, 
			String masterPassword ) {

		if(selected == null) return;
		
		selected.alias = alias ;
		selected.host = host; 
		selected.port = port;
		selected.databaseType = databaseType; 
		selected.databasePort = databasePort ;
		selected.databaseIdentifier = databaseIdentifier ;
		selected.databaseParameter = databaseParameter; 
		selected.masterUser = masterUser;
		selected.masterPassword = masterPassword;
		
		save();
		logger.debug("The selected record has been updated " + selected);
		this.setChanged(); this.notifyObservers(UPDATE_LIST);
	}
	
	/**
	 * @return the list of all records.
	 */
	synchronized public DBInfo[] getRecords() {
		// Seeing is believing: http://java.sun.com/j2se/1.5.0/docs/api/java/util/Collection.html#toArray(T[])
		return dbinfo.toArray(new DBInfo[0]);
	}
	
	private int lastIndex = Integer.MIN_VALUE;
	
	/**
	 * Set the selected record.
	 * 
	 * @param index	The index of the selected record. Zero means first. 
	 * Negative means nothing gets selected (deselect).
	 */
	synchronized public void setSelected(int index) {
		if(index == lastIndex) 
			return;
		else if(index >= 0) 
			selected = dbinfo.get(index); 
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
	 * Connect to the selected database. 
	 * First, a new database layer is created,
	 * and second, that database layer is initialized.
	 * <br/>
	 * <b>Warning:</b>If there is a previously created DBLayer, 
	 * it will be destroyed using the <code>logout()</code> method. 
	 * 	  
	 * @param name The account name (used to access the database).  
	 * @param password The password to the account.
	 */
	synchronized public void connectToSelected(final String name, final String password) {
		
		if(selected == null) {
			logger.debug("The System cannot create a connection when nothing was selected!");
			return;
		}
		
		final DBInfo selectedClone = selected.clone();
		
		worker = new SwingWorker() {
			public Object construct() {
				
				logout();
				try {
					// The current username is moved to the top of the list of names :) Nice feature.
					selectedClone.promoteUser(name);
					// Save the current state.
					save();
					
					// Create a new database layer.
					logger.debug("Asking the DBLayerFactory for a new DBLayer @ " + selectedClone.host + ":" + selectedClone.port);
					setChanged(); notifyObservers(L10n.getString("Login.Connecting"));
					dblayer = factory.create( selectedClone );
					
					logger.debug("Connection successful.");
					setChanged(); notifyObservers(L10n.getString("Login.Connected"));
					
					// Initialize the database layer.
					setChanged(); notifyObservers(L10n.getString("Login.InitializingDBLayer"));
					logger.debug("Initializing that DBLayer (" + selectedClone.databaseType + ", " + name + ", " + password + "...");
					
					Object[] init = dblayer.initialize(selectedClone.getDatabaseIdentifier(), name, password);
					plantloreUser = (User)init[0];
					accessRights = (Right)init[1];
				} 
				catch (Exception e) {
					logger.error("The initialization of the DBLayer failed! " + e.getMessage());
					// If the initialization of the DBLayer failed, the uninitialized DBLayer must be destroyed!
					// If it is not, the server's policy may not allow another connection from this client!
					if(dblayer != null)
						try {
							factory.destroy(dblayer);
						} catch(RemoteException re) {
							// Nothing we can do; the server is probably in trouble, or the network connection failed. 
						}
					setChanged();
					notifyObservers( e );
					return null;
				}
				
				setChanged(); 
				notifyObservers(L10n.getString("Login.DBLayerInitialized"));
				logger.debug("DBLayer initialized.");
				
				// Everything went fine.
				setChanged(); notifyObservers(dblayer);
				return null;
			}
		};
		
		worker.start();
	}
	
	
	/**
	 * Cancel the login proces.
	 *
	 */
	synchronized public void interrupt() {
		if(worker != null) {
			worker.interrupt();
			worker = null;
		}
		logout();
		setChanged(); notifyObservers(L10n.getString("Login.Interrupted"));
	}
	
	
	
	/**
	 * Disconnect from the current database. 
	 * The database connection is lost, any operation in progress will cause an exception.
	 * 
	 * @throws RemoteException if the RMI encounters an error.
	 */
	public void logout() {
		if(dblayer != null) 
			try {
				factory.destroy(dblayer);
				dblayer = null; accessRights = null; plantloreUser = null;
				logger.info("The client disconnected itself from the server. The communication may no longer be possible.");
			} catch(RemoteException e) {
				logger.warn("Unable to disconnect from the server. " + e.getMessage());
				/*setChanged();
				notifyObservers(e);*/ // Not this time, this is supposed to be silent.
			}
	}
	
		
	/**
	 * @return The last DBLayer that has been created.  
	 */	
	synchronized public DBLayer getDBLayer() { 
		return dblayer; 
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
        
}
