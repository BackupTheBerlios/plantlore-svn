package net.sf.plantlore.client.login;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Vector;

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
 * @version 0.5
 */
public class Login extends Observable {
	
	public static final int MAX_NAMES = 5;

	private Vector<DBInfo> dbinfo = new Vector<DBInfo>(10);
	private DBInfo selected = null;
	
	//private String  file = System.getProperty("user.home") + "/.plantlore/db.info.xml";
	
	private DBLayerFactory factory = null;
	private DBLayer dblayer;
	
	/**
	 * Create a new login model. The DBLayer factory will be used to produce 
	 * new DBLayers.
	 *  
	 * @param factory The factory that will be used to create a new DBLayer. 
	 */
	public Login(DBLayerFactory factory) {
		this.factory = factory;
	}
	
	
	/**
	 * Load saved information about the database connections.
	 *
	 */
	protected void load() {
		// TODO: JAKUB: nacist z XML souboru se jmenem `file` informace o databazich (triplety) do kolekce dbinfo.
		
		this.setChanged(); this.notifyObservers();
	}
	
	protected void save() {
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
		save();
		setChanged(); notifyObservers();
	}
	
	/**
	 * Delete the selected record.
	 *
	 */
	public void deleteSelectedRecord() {
		dbinfo.remove(selected);
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
	}
	
	/**
	 * @return	The selected record.
	 */
	public DBInfo getSelected() {
		return selected;
	}
	
	
	public DBLayer connectToSelected(String name, String password) throws NotBoundException, RemoteException, DBLayerException {
		if(selected == null) return null;
		dblayer = factory.create(selected.host, selected.port);
		dblayer.initialize(name, password, selected.db);
		selected.promoteUser(name);
		save();
		// Everything went fine.
		this.setChanged(); this.notifyObservers(dblayer);
		return dblayer;
	}
	
	/**
	 * @return The last DBLayer that was created.  
	 */	
	public DBLayer getDBLayer() { 
		return dblayer; 
	}
}
