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
	
	private String  file = System.getProperty("user.home") + "/.plantlore/db.info.xml";
	
	private DBLayerFactory factory = null;
	private DBLayer dblayer = null;
	
	
	public Login(DBLayerFactory factory) {
		this.factory = factory;
	}
	
	
	
	protected void load() {
		// TODO: JAKUB: nacist z XML souboru se jmenem `file` informace o databazich (triplety) do kolekce dbinfo.
		
		this.hasChanged(); this.notifyObservers();
	}
	
	protected void save() {
		// TODO: JAKUB: ulozit kolekci dbinfo zpatky do XML souboru se jmenem `file`.
	}
	
	
	public void createRecord(String alias, String host, int port, String db) {
		DBInfo r = new DBInfo(alias, host, port, db, new String[5]);
		dbinfo.add(r);
		save();
		this.hasChanged(); this.notifyObservers();
	}
	
	public void deleteRecord(DBInfo info) {
		dbinfo.remove(info);
		save();
		this.hasChanged(); this.notifyObservers();
	}
	
	public void updateSelectedRecord(DBInfo info) {
		int index = dbinfo.indexOf(selected);
		dbinfo.setElementAt(info, index); // info.clone()?
		selected = info; 
		this.hasChanged(); this.notifyObservers();
	}
	
	/** Subject to change.. */
	public DBInfo[] getRecords() {
		// Well, this sucks! The ugliest way to do things... is to have a Cloneable interface and don't use it.
		// Seeing is believing: http://java.sun.com/j2se/1.5.0/docs/api/java/util/Collection.html#toArray(T[])
		return dbinfo.toArray(new DBInfo[0]);
	}
	
	public void setSelected(int index) {
		selected = dbinfo.elementAt(index);		
	}
	
	public DBLayer connectToSelected(String name, String password) throws NotBoundException, RemoteException, DBLayerException {
		DBLayer dblayer = factory.create(selected.host, selected.port);
		dblayer.initialize(name, password, selected.db);
		return dblayer;
	}
	
	public DBLayer getDBLayer() {
		return dblayer;
	}
	
	
}
