package net.sf.plantlore.server;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Hashtable;
import net.sf.plantlore.common.exception.DBLayerException;

import org.apache.log4j.Logger;

import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.server.HibernateDBLayer;
import net.sf.plantlore.middleware.RemoteDBLayerFactory;

/**
 * RMIRemoteDBLayerFactory is responsible for creating and exporting instances of DBLayer.
 * Remote references to these objects are returned to the caller. All methods are synchronized. 
 * <br/>
 * The proper way to disconnect all remote clients from the server has these steps:
 * <ol>
 * <li>removing the RemoteDBLayerFactory from the rmiregistry, so that no more clients can obtain a reference
 * of the factory and ask it for creating a new dblayer</li>
 * <li>unexporting the RemoteDBLayerFactory, so that it cannot accept remote calls, so that all clients that
 * have a remote reference of this factory cannot ask it for another dblayer</li>
 * <li>"kicking" all currently connected users by unexporting their dblayer remote objects, which will terminate their
 * connections effectively</li>
 * </ol>
 * 
 * 
 * @author Erik Kratochvíl
 * @since 2006-03-13
 * @version 1.0  final
 */
public class RMIRemoteDBLayerFactory extends UnicastRemoteObject
	implements RemoteDBLayerFactory {
	
	private static final long serialVersionUID = 2006060433002698L;
	
	private Logger logger  = Logger.getLogger(RMIRemoteDBLayerFactory.class.getPackage().getName());
	
	private ServerSettings  settings;
	private Undertaker undertaker;
	
	/** Keep information about all connected clients. */
	private Hashtable<DBLayer, ConnectionInfo> clients; 
		
	
	
	/** 
	 * Create a new RMIDBLayerFactory.
	 * @throws RemoteException If the RMI encounters an error.
	 */
	public RMIRemoteDBLayerFactory(ServerSettings settings) throws RemoteException {
		this.settings = settings;
		clients = new Hashtable<DBLayer, ConnectionInfo>( settings.getConnectionsTotal() );
		undertaker = new RMIUndertaker();
	}
	
	
	/** 
	 * Return information about connected clients.
	 * 
	 *  @return Collection holding information about currently connected clients.
	 */
	protected synchronized ConnectionInfo[] getClients() {
		Collection<ConnectionInfo> currentlyConnectedClients = clients.values();
		if(currentlyConnectedClients != null) 
			return currentlyConnectedClients.toArray(new ConnectionInfo[0]);
		return null;
	}
	
	/**
	 *  Shall the server open another conenction for the host?
	 *  This method represents the connection policy of the server.
	 *  
	 * @param host	The name of the host that asks for another connection.
	 * @return	True if the connection should be allowed.
	 */
	private boolean allowConnection(String host) {
		if(clients.size() >= settings.getConnectionsTotal()) 
			return false;
		
		int c = 0;
		for(ConnectionInfo info : clients.values())
			if(info.getClientHost().equalsIgnoreCase(host)) 
				c++; // HA! C++ in Java! Strange!
		return (c < settings.getConnectionsPerIP());
	}
	
	/**
	 * Create a new remote object, export it so that the object can accept remote calls, and return the
	 * stub of this object. The creation of new connection must adhere to the connection policy.
	 * 
	 * @return Remote reference (stub) of the remote DBLayer object or 
	 * <b>null</b> if the server doesn't approve of creating the DBLayer
	 * (too many connections from this IP or too many clients connected). 
	 * @throws RemoteException If the RMI encounters an error.
	 */
	public synchronized DBLayer create() throws RemoteException, DBLayerException {
		// Apply the connection policy ~ see AllowConnection(host)
		String clientHost = "unknown";
		try { clientHost = RemoteServer.getClientHost(); } 
		catch(Exception e) { logger.warn("Unable to retrieve the client's host name."); }
		logger.debug("Someone from the " + clientHost + " contacted us.");
		
		// Connection policy
		if( !allowConnection(clientHost) ) {
			logger.warn("Too many connections from " + clientHost + " (or the server is full)!");
			throw new DBLayerException("There are either too many connections or the server is already full!");
		}
		
		// Create a new DBLayer, export it, and keep the stub. Also set the Undertaker of this object.
		logger.debug("Creating a new HibernateDBLayer ...");
		DBLayer database = new HibernateDBLayer( undertaker, settings.getDatabaseSettings() );
		logger.debug("[Success] DBLayer created.");
		
		
		DBLayer stub = null;
		try {
			logger.debug("Exporting the database layer...");
			stub = (DBLayer) UnicastRemoteObject.exportObject(database);
			logger.debug("[Success] DBLayer exported.");
		} catch(RemoteException e) {
			logger.error("[Failure] Unable to export the DBLayer. Is the `codebase` set properly? Are stubs generated properly? " + e.getMessage());
			throw e;
		}
		
		// Save the information about this connection.
		ConnectionInfo info = new ConnectionInfo(null, database, stub, clientHost); // remoteFactory is null because of security reasons.
		clients.put(stub, info);
		
		logger.info("New remote DBLayer created (" + info + ").");

		return stub;
	}
	
	/**
	 * Disconnect the DBLayer to prevent it from
	 * accepting more remote calls.<br/> 
	 * Make sure the DBLayer performs some cleanup.  
	 * 
	 * @param db	The DBLayer object (not stub!) that should be disconnected.
	 * @throws RemoteException If the RMI encounters an error.
	 */	
	void disconnect(DBLayer db)  throws RemoteException {
		assert(db != null);
		
		// Let the database layer perform some cleanup.
		db.shutdown();
		
		// Unexport the object even if there is an action in progress.
		UnicastRemoteObject.unexportObject(db, true);
		
		logger.info("The database layer " + db + " was disconnected and destroyed.");
	}

	/**
	 * Destroy the remote object associated with the stub 
	 * (and remove it from the list of connected clients).
	 * 
	 * @param stub The remote reference to the remote object that is to be destroyed.
	 * @throws RemoteException If the RMI encounters an error.
	 */	
	public synchronized void destroy(DBLayer stub) throws RemoteException {
		if(stub == null) return;
		ConnectionInfo info = clients.remove(stub);
		if(info != null) 
			disconnect(info.getDatabase());
		else try {
			logger.warn(RemoteServer.getClientHost() + " attempts to destroy " +
						"a database layer that has was not created by this factory OR attempts to destroy an already destroyed DBLayer!");
		} catch(ServerNotActiveException e) {/* Should never happen. */}
	}
	
	/** 
	 * Terminate the connection of all connected clients. 
	 */
	synchronized void disconnectAll() {
		for (ConnectionInfo info : clients.values()) 
			try { disconnect(info.getDatabase()); } catch (Exception e) {/* Never mind. */}		
		// Clear the list of opened connections - none is now opened.
		clients.clear();
	}
	
	/** 
	 * Take care of DBLayer whose client has "crashed".
	 * Ensure this database layer is properly disconnected.
	 * 
	 * @see destroy 
	 */
	private class RMIUndertaker implements Undertaker { 
		public synchronized final void bury(DBLayer database) {
			DBLayer stub = null;
			
			// Find the ConnectionInfo object among the connected clients 
			for(ConnectionInfo info : clients.values())
				if(info.getDatabase().equals(database)) { 
					stub = info.getStub(); 
					break; 
				}
			
			// Destroy it properly
			try { 
				destroy(stub); 
			} catch(RemoteException e) {/* What else can we do? */}
		}
	}


}
