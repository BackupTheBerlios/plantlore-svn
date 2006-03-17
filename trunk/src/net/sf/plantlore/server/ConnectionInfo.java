package net.sf.plantlore.server;

import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.RemoteDBLayerFactory;

/**
 *  Information about the client connected to a server:
 *  <ul>
 *  <li><code>database</code> = the DBLayer object/stub that mediates the connection to the database</li> 
 *  <li><code>remote factory</code> = null if the object is created locally or reference of the factory that created the <code>database</code></li>
 *  <li><code>client host</code> = identification of the computer for which the <code>database</code> was created</li>
 *  </ul>
 * 
 * @author Erik Kratochvíl
 * @since 2006-03-13
 * @version 1.0
 */
public class ConnectionInfo {
	
	private RemoteDBLayerFactory remoteFactory = null;
	private DBLayer database = null;
	private String clientHost = null;
	
	/**
	 * Create a new record holding the information about the client's connection.
	 * 
	 * @param remoteFactory	The RemoteDBLayerFactory that created the remote object.
	 * @param db	Either the remote object or the remote reference (stub) of that object.
	 * @param client		Host name of the computer where client dwells.
	 */
	public ConnectionInfo(RemoteDBLayerFactory remoteFactory, DBLayer db, String client) {
		this.remoteFactory = remoteFactory; this.database = db; this.clientHost = client;
	}

	/** The name of the host where the client is hiding. */
	public String getClientHost() { return clientHost; }
	
	/** The remote object or remote reference (stub) that allows the client to work with the database. */
	public DBLayer getDatabase() { return database; }
	
	/** The remote factory that created the remote object and returned the stub of it. 
	 * If it is null, the object is local. */
	public RemoteDBLayerFactory getRemoteFactory() { return remoteFactory; }
	
	@Override
	public String toString() {
		return database + ((remoteFactory == null) ? " (remote object)" : " (stub)" );
	}

}
