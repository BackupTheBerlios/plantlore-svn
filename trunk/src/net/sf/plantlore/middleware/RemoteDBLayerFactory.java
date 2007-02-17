﻿package net.sf.plantlore.middleware;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.sf.plantlore.common.exception.DBLayerException;

/** 
 * Interface for obtaining remote references of the DBLayer objects running on some remote server.
 * 
 * @author kaimu
 * @version 1.0
 * @since	2006-03-13	
 */
public interface RemoteDBLayerFactory extends Remote {
	
	/** The name the Remote Factory will use in the RMIRegistry. */
	static final String ID = "PlantloreRemoteDBLayerFactoryID";
	
	/** 
	 * Create a new DBLayer on the server and return a remote reference of that object. 
	 * The object is "private" = unique for every client.
	 * 
	 * The number of connections from one host may be limited as well as the total number of
	 * all clients connected to the server.
	 * 
	 * @see RMIRemoteDBLayerFactory
	 * 
	 * @return The remote reference of the DBLayer (that lives on the server side).
	 * @throws RemoteException If the RMI encounters a problem.
	 */
	DBLayer create() throws RemoteException, DBLayerException;
	
	/** 
	 * Destroy the remote object, i.e. ensure some cleanup. 
	 * 
	 * @param stub	The stub of the remote object, that should be destroyed.
	 * @throws RemoteException		If the RMI encounters a problem.
	 */
	void destroy(DBLayer stub) throws RemoteException;

}
