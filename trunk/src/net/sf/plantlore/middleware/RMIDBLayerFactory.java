package net.sf.plantlore.middleware;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

import net.sf.plantlore.server.ConnectionInfo;
import net.sf.plantlore.server.MyDBLayer;
import net.sf.plantlore.server.Tracker;
import net.sf.plantlore.middleware.RemoteDBLayerFactory;


/**
 * The RMIDBLayerFactory can create and destroy DBLayers - either local or remote. 
 * The client simply asks the factory to create the DBLayer and the factory shields the client from
 * all the details how the DBLayer is created or where those objects really are.   
 * <br/>
 * The RMIDBLayerFactory keeps track of all database layers it has created and where they
 * were actually created. It implements the Tracker interface so that some managing tool can have a look
 * at the information about connections.
 * 
 *  
 * TODO: Používat logger a property file, pokud dojde k nějakým výjimkám, jež je nutno logovat.
 * 
 * @author Erik Kratochvíl
 * @since 2006-03-11
 * @version 1.0 β not tested
  */
public class RMIDBLayerFactory implements DBLayerFactory, Tracker<ConnectionInfo> {

	/** Keep information about connected clients. */
	private Hashtable<DBLayer, ConnectionInfo> client = new Hashtable<DBLayer, ConnectionInfo>(10);
	

	/** Return all information about connected clients. */
	public Collection<ConnectionInfo> getClients() { return new Vector<ConnectionInfo>(client.values()); }

	/** Create a new local DBLayer. */
	public DBLayer create() {
		//Create a new DBLayer and save information about that connection 
		DBLayer db = new MyDBLayer();
		ConnectionInfo info = new ConnectionInfo(null, db, "localhost (direct connection)");
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
		RemoteDBLayerFactory remoteFactory = (RemoteDBLayerFactory) registry.lookup(RemoteDBLayerFactory.RemoteFactoryID);
		// Get the stub from the remote factory and save the information about the connection
		DBLayer stub = remoteFactory.create();
		ConnectionInfo info = new ConnectionInfo(remoteFactory, stub, "localhost -> " + host + ":" + port);
		client.put(stub, info);
		return stub;
	}

	/**
	 * Disconnect the dblayer from the server (and destroy the object on the server).
	 */
	public void destroy(DBLayer db) throws RemoteException {
		assert(db != null);
		db.close(); // close the connection
		ConnectionInfo info = client.remove(db);
		// Destroy the remote object on the server
		if(info != null && info.getRemoteFactory() != null) info.getRemoteFactory().destroy(info.getDatabase());
	}
	


}
