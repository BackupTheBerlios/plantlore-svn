package net.sf.plantlore.client.login;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Observable;
import net.sf.plantlore.client.MainConfig;
import net.sf.plantlore.common.record.User;

import org.apache.log4j.Logger;

import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.DBLayerFactory;
import net.sf.plantlore.common.exception.DBLayerException;

/**
 * Login is responsible for the following:
 * <ul>
 * <li><b>management of the list of databases</b> - 
 * 			adding, editing, removing records from the list, and the persistent storage of that list,</li>
 * <li><b>creating and initializing a new dblayer</b> - only one at a time is active,</li>
 * <li><b>destroying the current dblayer (logout)</b> - so as to make another connection possible</li>
 * </ul>
 * 
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @version 1.0
 */
public class Login extends Observable {
	
	/** The maximum number of usernames the System will store for each database record.*/
	public static final int MAX_NAMES = 5;

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
	private Logger logger;
	
	
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
		logger = Logger.getLogger(this.getClass().getPackage().getName());
		load();
	}
	
	
	/**
	 * Load saved information about the database connections.
	 * TODO: -IMPLEMENTATION MISSING-
	 */
	protected void load() {
		logger.debug("Loading the stored list of databases.");
		// TODO: JAKUB: nacist z XML souboru se jmenem `file` informace o databazich (triplety) do kolekce dbinfo.
		

		 // TEMPORARY CODE STARTS HERE

/*				dbinfo.add(new DBInfo("Local Database But Via RMI", "data.kolej.mff.cuni.cz", -1,
						"jdbc:firebirdsql:localhost/3050:c:/downloaded/plantloreHIBdata.fdb", 
						new String[] { "sysdba", null, null, null, null }));                
*/
/*				dbinfo.add(new DBInfo("My Home Database", "", -1,
							"jdbc:firebirdsql:localhost/3050:c:/Kovo/PlantloreClean/plantloreHIBdataUTF.fdb", 
							new String[] { "sysdba", null, null, null, null }));
*/                                
                                dbinfo.add(new DBInfo("Local Database in UTF-8", "localhost", -1,
						"jdbc:firebirdsql:localhost/3050:/data/plantloreHIBdataUTF.fdb", 
						new String[] { "sysdba", null, null, null, null }));
		
				dbinfo.add(new DBInfo("Local Database", "localhost", -1,
							"jdbc:firebirdsql:localhost/3050:c:/downloaded/plantloreHIBdata.fdb", 
							new String[] { "sysdba", null, null, null, null }));				

		 // TEMPORARY CODE ENDS HERE
                                
		//dbinfo = mainConfig.getDBinfos();
		this.setChanged(); this.notifyObservers();
	}
	
	/**
	 * Save the list of database connections for further usage.
	 * TODO: -IMPLEMENTATION MISSING-
	 */
	protected void save() {
		logger.debug("Saving the list of databases.");
		// TODO: JAKUB: ulozit kolekci dbinfo zpatky do XML souboru se jmenem `file`.
		mainConfig.setDBInfos(dbinfo);
                
                //ukladat uz tady?! spis ne - co kdyby se ukladani nepovedlo? bylo by divny to porad hlasit
                //mainConfig.save();
	}
	

	/**
	 * Create a new record, add it to the list of connections and save that information for
	 * the future use.
	 * 
	 * @param alias	Alias of the database.
	 * @param host	Hostname of the computer where the server dwells.
	 * @param port	Port where the server listens.
	 * @param db		Identifier of the database to which the User wants to connect.
	 */
	public void createRecord(String alias, String host, int port, String db) {
		DBInfo r = new DBInfo(alias, host, port, db, new String[MAX_NAMES]);
		dbinfo.add(r);
		logger.debug("New database record has been created " + r);
		save();
		setChanged(); notifyObservers();
	}
	
	/**
	 * Delete the selected record from the list.
	 *
	 */
	public void deleteSelectedRecord() {
		if(selected == null) return;
		dbinfo.remove(selected);
		logger.debug("The selected record has been removed " + selected);
		selected = null;
		save();
		this.setChanged(); this.notifyObservers();
	}
	
	/**
	 * Update the selected record.
	 * 
	 * @param alias	Alias of the database.
	 * @param host	Hostname of the computer where the server dwells.
	 * @param port	Port where the server listens.
	 * @param db		Identifier of the database to which the User wants to connect.
	 */
	public void updateSelectedRecord(String alias, String host, int port, String db) {
		if(selected == null) return;
		selected.alias = alias; selected.host = host; selected.port = port; selected.db = db;
		logger.debug("The selected record has been updated " + selected);
		this.setChanged(); this.notifyObservers();
	}
	
	/**
	 * @return the list of all records.
	 */
	public DBInfo[] getRecords() {
		// Seeing is believing: http://java.sun.com/j2se/1.5.0/docs/api/java/util/Collection.html#toArray(T[])
		return dbinfo.toArray(new DBInfo[0]);
	}
	
	/**
	 * Set the selected record.
	 * 
	 * @param index	The index of the selected record. Zero means first. 
	 * Negative means nothing gets selected (deselect).
	 */
	public void setSelected(int index) {
		if(index >= 0) selected = dbinfo.get(index); 
		else selected = null;
		
		logger.debug("Selected database is " + selected);
		this.setChanged(); 
		/*------------------------------------------------------------
		 * The reason why a parameter is used here is simple:
		 * 1. you select something in the choice list in the LoginView ->
		 * 2. ListSelectionEvent is fired ->
		 * 3. model.setSelected(..) is called in the handler ->
		 * 4. notifyObservers(..) is called here ->
		 * 5. loginView.update() gets involved ->
		 * 6. without proper recognition of events setList(data)
		 *    would be called which will in turn trigger 
		 *    ListSelectionEvent -> 2.
		 *------------------------------------------------------------*/
		this.notifyObservers("[!] recursion won't be tolerated");
	}
	
	/**
	 * @return	The selected record.
	 */
	public DBInfo getSelected() {
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
	 * @return The created and initialized DBLayer.
	 * @throws NotBoundException if the server is unreachable (most likely because it is not running). 
	 * @throws RemoteException if the RMI encounters an error.  
	 * @throws DBLayerException if the initialization of the DBLayer failed - most common reasons are:
	 * 		wrong username or password, or incorrect database model (server and client have different versions).
	 */
	public DBLayer connectToSelected(String name, String password) throws NotBoundException, RemoteException, DBLayerException {
		if(selected == null) {
			logger.debug("The System cannot create a connection when nothing was selected!");
			return null;
		}
		
		try {
			logout();
		} catch (RemoteException e) { logger.info("Unable to disconnect from the server. " + e); }
		
		// The current username is moved to the top of the list of names :) Nice feature.
		selected.promoteUser(name);
		// Save the current state.
		save();
		
		// Create a new database layer.
		logger.debug("Asking the DBLayerFactory for a new DBLayer @ " + selected.host + ":" + selected.port);
		dblayer = factory.create(selected.host, selected.port);
		
		// Initialize the database layer.
		logger.debug("Initializing that DBLayer (" + selected.db + ", " + name + ", " + password + "...");
		try {
			Object[] init = dblayer.initialize(selected.db,name, password);
                        plantloreUser = (User)init[0];
                        accessRights = (Right)init[1];
		} 
		catch (DBLayerException exception) {
			logger.error("The initialization of the DBLayer failed! Here's why: " + exception);
                        exception.printStackTrace();
			// If the initialization of the DBLayer failed, the uninitialized DBLayer must be destroyed!
			// If it is not, the server's policy may not allow another connection from this client!
			factory.destroy(dblayer);
			throw exception; // rethrow that exception [so that the User also knows what happened]
		}

		logger.debug("DBLayer initialized.");
		
		// Everything went fine.
		this.setChanged(); this.notifyObservers(dblayer);
		return dblayer;
	}
	
	
	/**
	 * Disconnect from the current database. 
	 * The database connection is lost, any operation in progress will cause an exception.
	 * 
	 * @throws RemoteException if the RMI encounters an error.
	 */
	public void logout() throws RemoteException {
		if(dblayer != null) {
			factory.destroy(dblayer);
			dblayer = null;
			accessRights = null;
			logger.info("The client disconnected itself from the server. The communication may no longer be possible.");
			this.setChanged(); this.notifyObservers();
		}
	}
	
		
	/**
	 * @return The last DBLayer that has been created.  
	 */	
	public DBLayer getDBLayer() { 
		return dblayer; 
	}
	
	/**
	 * @return The list of access rights as returned by the database layer
	 * after initialization.
	 */
	public Right getAccessRights() {
		return accessRights;
	}
        
}
