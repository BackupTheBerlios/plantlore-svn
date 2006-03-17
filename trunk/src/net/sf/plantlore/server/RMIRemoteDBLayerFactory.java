package net.sf.plantlore.server;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.RemoteDBLayerFactory;

/**
 * RMIRemoteDBLayerFactory is responsible for creating and exporting instances of DBLayer.
 * Remote references to these objects are returned to the caller. 
 * <br/>
 * The proper way to disconnect all remote clients from the server has these steps:
 * <ol>
 * <li>removing the RemoteDBLayerFactory from the rmiregistry, so that no more clients can obtain a reference to
 * the factory and ask it for creating a new dblayer</li>
 * <li>unexporting the RemoteDBLayerFactory, so that it cannot accept remote calls, so that all clients that
 * have a remote reference of this factory cannot ask it for another dblayer</li>
 * <li>"kicking" all currently connected users by unexporting their dblayer remote objects, which will terminate their
 * connections effectively</li>
 * </ol>
 * 
 * FIXME: Ověřit, jestli náhodou není možné tento objekt pomocí reflection proskenovat a zavolat metodu
 * disconnectAll(). Nemělo by to jít, ale je potřeba se ujistit! Je-li to možné, je nutné metodu odstranit! 
 * 
 * TODO: Používat logger a property file, pokud dojde k nějakým výjimkám, jež je nutno logovat.
 * 
 * @author Erik Kratochvíl
 * @since 2006-03-13
 * @version 1.0
 */
public class RMIRemoteDBLayerFactory extends UnicastRemoteObject
	implements RemoteDBLayerFactory, Tracker<ConnectionInfo> {
	
	/** Maximum number of connections from one IP. */
	private int maxConnectionsPerIP = 1;
	
	/** Keep information about connected clients. */
	private Hashtable<DBLayer, ConnectionInfo> client = new Hashtable<DBLayer, ConnectionInfo>(10);
	
	/** Create a new RMIDBLayerFactory.
	 * @throws RemoteException If the RMI encounters an error.
	 */
	public RMIRemoteDBLayerFactory() throws RemoteException {}
	
	/** Return all information about connected clients. */
	public Collection<ConnectionInfo> getClients() { return new Vector<ConnectionInfo>(client.values()); }
	
	/**
	 *  Shall the server open another conenction for the host?
	 *  This method represents the connection policy of the server.
	 * @param host	The name of the host that asks for another connection.
	 * @return	True if the connection should be allowed.
	 */
	private boolean allowConnection(String host) {
		int c = 0;
		for(ConnectionInfo info : client.values())
			if(info.getClientHost().equalsIgnoreCase(host)) c++; // HA! C++ in Java! Strange!
		return (c < maxConnectionsPerIP);
	}
	
	/**
	 * Create a new remote object, export it so that the object can accept remote calls, and return the
	 * stub of this object. The creation of new connection must adhere to the connection policy.
	 * @return Remote reference (stub) of the remote DBLayer object.
	 * @throws RemoteException If the RMI encounters an error.
	 */
	public DBLayer create() throws RemoteException {
		// Apply the connection policy ~ see AllowConnection(host)
		String clientHost = "unable to comply";
		try { clientHost = RemoteServer.getClientHost(); } catch(Exception e) {}
		if( !allowConnection(clientHost) ) throw new RemoteException("Too many connections from this IP!");
		// Create a new DBLayer, export it, and keep the stub.
		DBLayer database = new MyDBLayer();
		DBLayer stub = (DBLayer) UnicastRemoteObject.exportObject(database);
		// Save the information about this connection.
		ConnectionInfo info = new ConnectionInfo(null, database, clientHost);
		client.put(stub, info);
		return stub;
	}
	
	/**
	 * Disconnect the DBLayer from the server and unexport the object to prevent it from
	 * accepting more remote calls.
	 * 
	 * @param db	The DBLayer that should be disconnected (from the database server and RMI middleware).
	 * @throws RemoteException If the RMI encounters an error.
	 */	
	private void disconnect(DBLayer db)  throws RemoteException {
		assert(db != null);
		db.close(); // duplicate!
		// Unexport the object even if there is an action in progress.
		UnicastRemoteObject.unexportObject(db, true);
	}

	/**
	 * Destroy the remote object associated with the stub. 
	 * 
	 * @param stub The remote reference to the remote object that is to be destroyed.
	 * @throws RemoteException If the RMI encounters an error.
	 */	
	public void destroy(DBLayer stub) throws RemoteException {
		ConnectionInfo info = client.remove(stub);
		if(info != null && info.getDatabase() != null)
			disconnect(info.getDatabase());
	}
	
	/** Terminate the connection of all connected clients. */
	public void disconnectAll() {
		for (ConnectionInfo info : client.values()) {
			try { disconnect(info.getDatabase()); } catch (Exception e) {}
		}
	}

}
