package net.sf.plantlore.client.login;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Vector;

import org.apache.log4j.Logger;

import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.DBLayerFactory;
import net.sf.plantlore.server.DBLayerException;

/**
 * 
 * Preliminary usage: <br/>
 * <ul>
 * <li>Login login = new Login(new RMIDBLayerFactory());</li>
 * <li>login.connectToSelected(...)</li>
 * </ul>
 * 
 * 
 * @author Erik Kratochvíl, Jakub Kotowski
 * @version 0.9
 */
public class Login extends Observable {
	
	public static final int MAX_NAMES = 5;

	private Vector<DBInfo> dbinfo = new Vector<DBInfo>(10);
	private DBInfo selected = null;
	
	//private String  file = System.getProperty("user.home") + "/.plantlore/db.info.xml";
	
	private DBLayerFactory factory = null;
	private DBLayer dblayer;
	private Logger logger;
	
	/**
	 * Create a new login model. The DBLayer factory will be used to produce 
	 * new DBLayers.
	 *  
	 * @param factory The factory that will be used to create a new DBLayer. 
	 */
	public Login(DBLayerFactory factory) {
		this.factory = factory;
		logger = Logger.getLogger(this.getClass().getPackage().getName());
		load();
	}
	
	
	/**
	 * Load saved information about the database connections.
	 *
	 */
	protected void load() {
		logger.debug("Loading the stored list of databases.");
		// TODO: JAKUB: nacist z XML souboru se jmenem `file` informace o databazich (triplety) do kolekce dbinfo.
		dbinfo.add(new DBInfo("# Testovací databáze #", "", -1,
				"jdbc:firebirdsql:natalka.kolej.mff.cuni.cz/3050:/mnt/data/temp/plantloreHIBdata.fdb", 
				new String[] { "sysdba", null, null, null, null }));
		
		this.setChanged(); this.notifyObservers();
	}
	
	protected void save() {
		logger.debug("Saving the list of databases.");
		// TODO: JAKUB: ulozit kolekci dbinfo zpatky do XML souboru se jmenem `file`.
		
	}
	

	/**
	 * Create a new record, add it to the list of connections and save that information for
	 * future use.
	 * 
	 * @param alias	Alias of the database.
	 * @param host	Hostname of the computer where the server dwells.
	 * @param port	Port where the server listens.
	 * @param db		Identifier of the database to which the User wants to connect.
	 */
	public void createRecord(String alias, String host, int port, String db) {
		DBInfo r = new DBInfo(alias, host, port, db, new String[5]);
		dbinfo.add(r);
		logger.debug("New database record has been created " + r);
		save();
		setChanged(); notifyObservers();
	}
	
	/**
	 * Delete the selected record.
	 *
	 */
	public void deleteSelectedRecord() {
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
		selected.alias = alias;
		selected.host = host;
		selected.port = port;
		selected.db = db;
		logger.debug("The selected record has been updated " + selected);
		this.setChanged(); this.notifyObservers();
	}
	
	/**
	 * 
	 * @return
	 */
	public DBInfo[] getRecords() {
		// Well, this sucks! The ugliest way to do things... is to have a Cloneable interface and don't use it.
		// Seeing is believing: http://java.sun.com/j2se/1.5.0/docs/api/java/util/Collection.html#toArray(T[])
		return dbinfo.toArray(new DBInfo[0]);
	}
	
	/**
	 * Set the selected record.
	 * 
	 * @param index	The index of the selected record. Zero means first. Negative means nothing gets selected.
	 */
	public void setSelected(int index) {
		if(index >= 0) selected = dbinfo.elementAt(index); else selected = null;
		logger.debug("Selected database is " + selected);
		this.setChanged(); 
		/*------------------------------------------------------------
		 * This here is a particularly mystique code.
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
	
	
	public DBLayer connectToSelected(String name, String password) throws NotBoundException, RemoteException, DBLayerException {
		if(selected == null) {
			logger.debug("The System cannot create a connection when nothing was selected!");
			return null;
		}
		selected.promoteUser(name);
		
		// Create a new database layer.
		logger.info("Asking the DBLayerFactory for a new DBLayer @ " + selected.host + ":" + selected.port);
		dblayer = factory.create(selected.host, selected.port);
		// Initialize the database layer.
		logger.info("Initializing that DBLayer...");
		dblayer.initialize(selected.db,name, password);
		logger.info("DBLayer initialized.");
		// Save the current state.
		save();
		// Everything went fine.
		this.setChanged(); this.notifyObservers(dblayer);
		return dblayer;
	}
	
	public void reconnect() {
		
	}
	
	/**
	 * @return The last DBLayer that was created.  
	 */	
	public DBLayer getDBLayer() { 
		return dblayer; 
	}
}
