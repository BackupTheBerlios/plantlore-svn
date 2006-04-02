package net.sf.plantlore.server;

import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

/**
 * Server management.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 29.3.2006
 * @version 1.0 final
 *
 * @param <E>	The class storing information about currently connected clients.
 */
public interface Server<E> extends Remote, Serializable {
	
	/**
	 * Return a collection of currently connected clients.
	 * 
	 * @return Currently connected clients.
	 * @throws RemoteException	if the RMI encounters an error (network/server).
	 */
	Collection<? extends E>	getClients() throws RemoteException;
	
	/**
	 * "Kick" the specified client. The client is disconnected immediately.
	 * 
	 * @param client The client to be kicked from the server.
	 * @throws RemoteException if the RMI encounters an error (network/server).
	 */
	void disconnect(E client) throws RemoteException;
	
	/**
	 * Run the server on the specified port.
	 * 
	 * @throws RemoteException if the RMI encounters an error (network/server).
	 * @throws AlreadyBoundException if another server is already running on the specified port.
	 */
	void start() throws RemoteException, AlreadyBoundException;
	
	/**
	 * Terminate the server. If <code>harsh</code> is true, the server terminates immediately,
	 * if not, it waits, until the last client disconnects. 
	 * 
	 * @param harsh Should all currently connected clients be disconnected?
	 * @throws RemoteException if the RMI encounters an error (network/server).
	 */
	void stop(boolean harsh) throws RemoteException;
	
}
