package net.sf.plantlore.server;

import java.io.Serializable;
import java.rmi.RemoteException;

import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.RemoteDBLayerFactory;

/**
 *  Information about the connection:
 *  <ul>
 * <li>remoteFactory	The RemoteDBLayerFactory that created the remote object.</li>
 * <li>db	 The remote object.</li>
 * <li>stub The remote reference (stub) of that object.</li>
 * <li>description		The description of the connection.</li>
 *  </ul>
 * 
 * @author Erik Kratochvíl
 * @since 2006-03-13
 * @version 1.0
 */
public class ConnectionInfo implements Serializable {
	
	
	private static final long serialVersionUID = 2006060433002L;
	
	private RemoteDBLayerFactory remoteFactory = null;
	private DBLayer database = null;
	private DBLayer stub = null;
	private String description = null;
	
	/**
	 * Create a new record holding the information about the client's connection.
	 * 
	 * @param remoteFactory	The RemoteDBLayerFactory that created the remote object.
	 * @param db	 The remote object.
	 * @param stub The remote reference (stub) of that object.
	 * @param description		The description of the connection.
	 */
	public ConnectionInfo(RemoteDBLayerFactory remoteFactory, DBLayer db, DBLayer stub, String description) {
		this.remoteFactory = remoteFactory; this.database = db; this.stub = stub; this.description = description;
	}

	/** 
	 * 
	 * @return	The description of the connection.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @return The remote object or remote reference (stub) that allows the client to
	 * work with the database.
	 */
	public DBLayer getDatabase() {
		return database;
	}

	/**
	 * @return The remote factory that created the remote object, or null, if the object is local.
	 */
	public RemoteDBLayerFactory getRemoteFactory() {
		return remoteFactory;
	}

	/** 
	 * 
	 * @return	The stub.
	 */
	public DBLayer getStub() {
		return stub;
	}

	@Override
	public String toString() {
		try {
			if( database == null && description == null )
				return "unidentified user";
			else if( database != null && description == null )
				return database.getDescription();
			else if( database == null && description != null )
				return "? @ " + description;
			else
				return database.getDescription() + " @ " + description;
		} catch( Exception e ) {
			return "? @ " + description;
		}
	}

}
