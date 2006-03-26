package net.sf.plantlore.middleware;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Hashtable;

import net.sf.plantlore.server.ConnectionInfo;
import net.sf.plantlore.server.HibernateDBLayer;
import net.sf.plantlore.middleware.RemoteDBLayerFactory;


/**
 * The RMIDBLayerFactory can create and destroy DBLayers - either local or remote. 
 * The client simply asks the factory to create the DBLayer and the factory shields the client from
 * all the details how the DBLayer is created or where those objects really are.   
 * <br/>
 * The RMIDBLayerFactory keeps track of all database layers it has created and where they
 * were actually created.
 * <table>
 * <tr><th></th><th>remote factory</th><th>database</th><th>stub</th><th>client</th></tr>
 * <tr><th>local</th><td>null</td><td>yes</td><td>null</td><td>"localhost (direct connection)"</td></tr>
 * <tr><th>stub</th><td>yes</td><td>null</td><td>yes</td><td>"localhost -> server name"</td></tr>
 * </table>
 * 
 *  
 * TODO: Používat logger a property file, pokud dojde k nějakým výjimkám, jež je nutno logovat.
 * 
 * @author Erik Kratochvíl
 * @since 2006-03-11
 * @version 1.0 β not tested
  */
public class RMIDBLayerFactory implements DBLayerFactory {

	/** Keep information about connected clients. */
	private Hashtable<DBLayer, ConnectionInfo> client = new Hashtable<DBLayer, ConnectionInfo>(10);
	

	/** Create a new local DBLayer. */
	public DBLayer create() {
		//Create a new DBLayer and save information about that connection 
		DBLayer db = new HibernateDBLayer();
		ConnectionInfo info = new ConnectionInfo(null, db, null, "localhost (direct connection)");
		client.put(db, info);
		return db;
	}

	/**
	 * Create a new remote DBLayer
	 * 
	 * @param host	Hostname of the computer where server runs.
	 * @param port	Port where the server listens.
	 */
	public DBLayer create(String host, short port) throws RemoteException, NotBoundException {
		// Connect to the remote server and obtain the RemoteDBLayerFactory
		Registry registry = LocateRegistry.getRegistry(host, port);
		RemoteDBLayerFactory remoteFactory = (RemoteDBLayerFactory) registry.lookup(RemoteDBLayerFactory.ID);
		// Get the stub from the remote factory and save the information about the connection
		DBLayer stub = remoteFactory.create();
		ConnectionInfo info = new ConnectionInfo(remoteFactory, null, stub, "localhost -> " + host + ":" + port);
		client.put(stub, info);
		return stub;
	}

	/**
	 * Disconnect the dblayer from the server (and destroy the object on the server).
	 */
	public void destroy(DBLayer db) throws RemoteException {
		if(db == null) return;
		ConnectionInfo info = client.remove(db);
		if(info == null) return;
		if(info.getRemoteFactory() != null) info.getRemoteFactory().destroy(info.getStub());
		//else db.close(); // close the connection
	}
	


}
