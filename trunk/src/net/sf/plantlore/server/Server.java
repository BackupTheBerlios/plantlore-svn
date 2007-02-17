﻿package net.sf.plantlore.server;

import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An interface for the server management.
 * 
 * @author kaimu
 * @since 2006-03-29
 */
public interface Server extends Remote, Serializable {
	
	/**
	 * Return a list of currently connected clients.
	 * 
	 * @return Currently connected clients.
	 */
	ConnectionInfo[]	getClients() throws RemoteException;
	
	/**
	 * "Kick" the specified client. The client is disconnected immediately.
	 * 
	 * @param client The client to be kicked from the server.
	 */
	void disconnect(ConnectionInfo client) throws RemoteException;
	
	/**
	 * Start the server.
	 * 
	 */
	void start() throws RemoteException, AlreadyBoundException;
	
	/**
	 * Terminate the server. 
	 * 
	 */
	void stop() throws RemoteException;
	
	
	/**
	 *	Test whether the server is alive.
	 *
	 * @return True if the server is alive. 
	 */
	boolean ping() throws RemoteException;
	
}
