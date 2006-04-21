package net.sf.plantlore.server.manager;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Observable;

import org.apache.log4j.Logger;


import net.sf.plantlore.server.*;

/**
 * The server manager can
 * either create a new server or connect to an existing server.
 * <br/>
 * After the Server Administrator is logged in, he can control the server:
 * <ul>
 * <li>see the connected clients,</li>
 * <li>kick some of the connected clients,</li>
 * <li>terminate the server.</li>
 * </ul>
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-21
 * @version 1.0 final
 */
public class ServerMng extends Observable {
	
	/** The server control interface. */
	private Server server;
	
	/** List of connected clients. */
	private ConnectionInfo[] clients;
	
	/**
	 * CREATE_NEW will create a and start a new server
	 * CONNECT_EXISTING will try connect to an existing server  
	 * 
	 * @author Erik Kratochvíl (discontinuum@gmail.com)
	 * @since 21.4.2006
	 */
	public enum Mode { CREATE_NEW, CONNECT_EXISTING };
	
	/** The current mode. */
	private Mode mode = Mode.CREATE_NEW;
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
		
	
	/**
	 * Try to create and start a new server on the specified <code>port</code>.
	 * The server will be protected with a <code>password</code> so that 
	 * anyone who will try to connect to the server will have use that password.
	 * <br/>
	 * It is <b>not</b> possible to create a server on a remote machine
	 * (because of security reasons).
	 * 
	 * @param port The port where the server should listen.
	 * @param password	The password protecting the access to the server.
	 * @throws AlreadyBoundException if another server is already running on the specified port.
	 * @throws RemoteException if the RMI encounters an error.
	 */
	public void startNewServer(int port, String password) 
	throws AlreadyBoundException, RemoteException {
		logger.debug("Creating a new server at localhost:" + port);
		server = new RMIServer(port, password);
		logger.debug("Starting the server ...");
		server.start();
		logger.info("Server up and running (port " + port + ").");
	}

	/**
	 * Connect to an existing server on the selected <code>host</code>
	 * listening on the selected <code>port</code> using the <code>password</code>.
	 * 
	 * @param host The hostname of the computer where the server is running.
	 * @param port The port where the server is listening.
	 * @param password The password to gain the access to the server.
	 * @throws RemoteException if the RMI encounters an error.
	 * @throws NotBoundException if there is no server running on the specified host:port. 
	 * @throws CertificationException if the password is incorrect.
	 */
	public void connectToRunningServer(String host, int port, String password) 
	throws RemoteException, NotBoundException, CertificationException {
		logger.info("Connecting to a running server at " +host+":"+port);
		Registry registry = LocateRegistry.getRegistry(host, port);
		Guard guard = (Guard) registry.lookup(Guard.ID);
		server = guard.certify(password);
		
		if(server == null) 
			logger.warn("The connection to the server was NOT successful - certification didn't succeed!");
	}
	
	
	/**
	 * Start a new server or connect to an existing one accordingly to the current mode.
	 * 
	 * @param host The hostname of the computer where the server is running. 
	 * (ignored when starting a new server). 
	 * @param port The port where a server is listening or shall listen.
	 * @param password The password that protects the server or shall protect the server.
	 * @throws RemoteException if the RMI encounters an error.
	 * @throws NotBoundException if there is no server running on the specified host:port
	 * @throws AlreadyBoundException if there is another server already running on the specified port
	 * @throws CertificationException if the password is incorrect and the certification failed.
	 * 
	 * @see connectToRunningServer
	 * @see startNewServer
	 * @see mode
	 */
	public void actAsInstructed(String host, int port, String password) 
	throws RemoteException, NotBoundException, AlreadyBoundException, CertificationException {
		if(mode == Mode.CREATE_NEW) startNewServer(port, password);
		else connectToRunningServer(host, port, password);
	}
	
	/**
	 * Get the list of connected clients.
	 * If <code>refresh</code> is true, the server is asked and the list of clients is refreshed.
	 * If it is false, the stored list of connected clients is returned. 
	 * 
	 * @param refresh true means get the list from the server, false means get it from the local cache. 
	 * @return The (possibly not updated) list of connected clients so that you can see whom you can kick, 
	 * 	or null if noone is connected or we are not connected (or running) to some server.   
	 */
	public ConnectionInfo[] getConnectedUsers(boolean refresh) {
		if(server == null) return null;
		if(refresh)
			try {
				// Get the list of clients directly from the server.
				clients = server.getClients();
				logger.debug("Clients connected to the server received.");
				setChanged(); notifyObservers("PHear me!");
			} catch( RemoteException e) { 
				serverIsDead();
				logger.warn("Unable to obtain the list of connected users - network error? " + e); 
			}
		return clients;
	}
	
	/**
	 * Disconnect the selected client from the server.
	 * 
	 * @param client The client to be kicked out of the server.
	 * @throws RemoteException if the RMI has problems with this drastic task.
	 */
	public void kick(ConnectionInfo client) throws RemoteException {
		if(server == null || client == null) return;
		try {
			server.disconnect(client);
		} catch(RemoteException re) { serverIsDead(); throw re; }
		
		logger.info(client + " was kicked out of the server. Muhehee.");
		// Reload the list of connected users.
		getConnectedUsers(true);
	}
	
	/**
	 * Terminate the server.
	 * <b>Warning:</b> This will also kick all users as well.
	 * 
	 * @throws RemoteException if the RMI has personal feelings for the server.
	 */
	public void terminateServer() throws RemoteException {
		if(server != null)
			try {
				server.stop(true);
			}catch(RemoteException re) { serverIsDead(); throw re; }
		logger.info("Server terminated - all clients disconnected.");
	}
	
	/**
	 * Stop the server. The server will be terminated after the last client disconnects from it.
	 * 
	 * @throws RemoteException if the RMI encounters an error.
	 */
	@Deprecated
	public void stopServer() throws RemoteException {
		if(server !=null) 
			try {
				server.stop(false);
			}catch(RemoteException re) { serverIsDead(); throw re; }
		logger.debug("Server stopped - the server will terminate after the last client finishes his work.");
	}
	
	/**
	 * Set a new mode.
	 *  
	 * @param m The new mode.
	 * @see Mode
	 */
	public void setMode(Mode m) {
		this.mode = m;
		logger.debug("Server mode " + mode);
		setChanged(); notifyObservers(this.mode);
	}
	
	/** 
	 * Get the current mode.
	 * @return The current mode.
	 * @see Mode
	 */
	public Mode getMode() { 
		return this.mode; 
	}
	
	/**
	 * Notify this model, that the server is no longer accessible. 
	 *
	 */
	protected void serverIsDead() {
		server = null; clients = null;
		logger.warn("Connection to the server has failed.");
	}

}
