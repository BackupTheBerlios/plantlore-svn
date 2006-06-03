package net.sf.plantlore.server;

import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Server management.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-03-29
 * @version 1.0 final
 */
public interface Server extends Remote, Serializable {
	
	/**
	 * Return a collection of currently connected clients.
	 * 
	 * @return Currently connected clients.
	 * @throws RemoteException	if the RMI encounters an error (network/server).
	 */
	ConnectionInfo[]	getClients() throws RemoteException;
	
	/**
	 * "Kick" the specified client. The client is disconnected immediately.
	 * 
	 * @param client The client to be kicked from the server.
	 * @throws RemoteException if the RMI encounters an error (network/server).
	 */
	void disconnect(ConnectionInfo client) throws RemoteException;
	
	/**
	 * Run the server on the specified port.
	 * 
	 * @throws RemoteException if the RMI encounters an error (network/server).
	 * @throws AlreadyBoundException if another server is already running on the specified port.
	 */
	void start() throws RemoteException, AlreadyBoundException;
	
	/**
	 * Terminate the server. 
	 * 
	 * @throws RemoteException if the RMI encounters an error (network/server).
	 */
	void stop() throws RemoteException;
	
}
