package net.sf.plantlore.server;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.server.HibernateDBLayer;
import net.sf.plantlore.middleware.RemoteDBLayerFactory;

/**
 * RMIRemoteDBLayerFactory is responsible for creating and exporting instances of DBLayer.
 * Remote references to these objects are returned to the caller. All methods are synchronized. 
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
 * TODO: Používat logger a property file, pokud dojde k nějakým výjimkám, jež je nutno logovat.
 * TODO:  maxConnectionsPerIP & maxConnectionsTotal by se asi měly načítat z nějakého config souboru.
 * 
 * @author Erik Kratochvíl
 * @since 2006-03-13
 * @version 1.0 
 */
public class RMIRemoteDBLayerFactory extends UnicastRemoteObject
	implements RemoteDBLayerFactory, Undertaker {
	
	
	
	/** Maximum number of connections from one IP. */
	private int maxConnectionsPerIP = 1;
	
	/** Maximum number of clients. */
	private int maxConnectionsTotal = 8;
	
	/** Keep information about connected clients. */
	private Hashtable<DBLayer, ConnectionInfo> client = 
		new Hashtable<DBLayer, ConnectionInfo>(maxConnectionsTotal);
	
	/** 
	 * Create a new RMIDBLayerFactory.
	 * @throws RemoteException If the RMI encounters an error.
	 */
	public RMIRemoteDBLayerFactory() throws RemoteException {}
	
	/** Return all information about connected clients. */
	public synchronized Collection<ConnectionInfo> getClients() { return new Vector<ConnectionInfo>(client.values()); }
	
	/**
	 *  Shall the server open another conenction for the host?
	 *  This method represents the connection policy of the server.
	 * @param host	The name of the host that asks for another connection.
	 * @return	True if the connection should be allowed.
	 */
	private boolean allowConnection(String host) {
		if(client.size() >= maxConnectionsTotal) return false;
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
	public synchronized DBLayer create() throws RemoteException {
		// Apply the connection policy ~ see AllowConnection(host)
		String clientHost = "unable to comply";
		try { clientHost = RemoteServer.getClientHost(); } catch(Exception e) {}
		if( !allowConnection(clientHost) ) 
			throw new RemoteException("Too many connections from this IP (or the server is full)!");
		// Create a new DBLayer, export it, and keep the stub. Also set the Undertaker of this object.
		DBLayer database = new HibernateDBLayer(this);
		DBLayer stub = (DBLayer) UnicastRemoteObject.exportObject(database);
		// Save the information about this connection.
		ConnectionInfo info = new ConnectionInfo(null, database, stub, clientHost); // remoteFactory is null because of security reasons.
		client.put(stub, info);

		return stub;
	}
	
	/**
	 * Disconnect the DBLayer from the server and unexport the object to prevent it from
	 * accepting more remote calls. 
	 * 
	 * @param db	The DBLayer object (not stub!) that should be disconnected.
	 * @throws RemoteException If the RMI encounters an error.
	 */	
	protected void disconnect(DBLayer db)  throws RemoteException {
		if(db == null) return;
		//db.close();
		// Unexport the object even if there is an action in progress.
		UnicastRemoteObject.unexportObject(db, true);
	}

	/**
	 * Destroy the remote object associated with the stub (and remove it from the list of connected clients).
	 * 
	 * @param stub The remote reference to the remote object that is to be destroyed.
	 * @throws RemoteException If the RMI encounters an error.
	 */	
	public synchronized void destroy(DBLayer stub) throws RemoteException {
		ConnectionInfo info = client.remove(stub);
		if(info != null) disconnect(info.getDatabase());
	}
	
	/** Terminate the connection of all connected clients. */
	public synchronized void disconnectAll() {
		for (ConnectionInfo info : client.values()) 
			try { disconnect(info.getDatabase()); } catch (Exception e) {}		
		// Clear the list of opened connections - none is now opened.
		client.clear();
	}
	
	/** Take care of DBLayer whose client has "crashed". */
	public synchronized void bury(DBLayer database) {
		DBLayer stub = null;
		for(ConnectionInfo info : client.values())
			if(info.getDatabase().equals(database)) { stub = info.getStub(); break; }
		try { destroy(stub); } catch(RemoteException e) { /* eat */}
	}
	


}
