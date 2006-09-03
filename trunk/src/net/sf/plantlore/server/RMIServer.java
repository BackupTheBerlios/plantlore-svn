package net.sf.plantlore.server;


import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;


import org.apache.log4j.Logger;


import net.sf.plantlore.middleware.RemoteDBLayerFactory;
import net.sf.plantlore.server.tools.*;

/**
 *
 *  The RMIServer starts and terminates the server, 
 *  that is: it creates and exports/unexports the RemoteDBLayerFactory.  
 *  
 * @author Erik Kratochvíl
 * @since 2006-03-11
 * @version 1.0
 * 
 * @see net.sf.plantlore.middleware.RemoteDBLayerFactory
 */
public class RMIServer extends UnicastRemoteObject implements Server {
	
	private static final long serialVersionUID = 2006060433819775L;
	
	/** 
	 * The default port where the rmiregistry listens. 
	 * To that rmiregistry the RemoteDBLayerFactory will be bound. 
	 */
	public static final int DEFAULT_PORT = Registry.REGISTRY_PORT;
	
	
	
	private RMIRemoteDBLayerFactory remoteFactory = null;
	private Guard guard = null;
	private ServerSettings settings;
	
	private Logger logger;


	
	/** 
	 * Create a new instance of the server based on the supplied settings.
	 * 
	 * @param settings		The settings of the server.
	 * @param password	The password protecting the access to the server.
	 */
	public RMIServer(ServerSettings settings, String password) 
	throws RemoteException, AlreadyBoundException {
		logger = Logger.getLogger(this.getClass().getPackage().getName());
		this.settings = settings;
		
		// Control object that will return the server after the client passed a valid certif. information
		guard = new RMIServerControl( (Server) RemoteObject.toStub(this), password);
		RMI.bind(settings.getPort(), guard, Guard.ID);
	}
	
	/** 
	 * Get the list of currently connected clients. 
	 */
	public synchronized ConnectionInfo[] getClients() 
	throws RemoteException {
		if(remoteFactory == null) 
			return null;
		try {
			return remoteFactory.getClients();
		}
		catch(RemoteException e) {
			logger.error("Unable to obtain the list of connected clients. " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Disconnect the selected client.
	 * 
	 * @param client The client to be kicked.
	 */
	public synchronized void disconnect(ConnectionInfo client) 
	throws RemoteException {
		try { 
			remoteFactory.destroy( client.getStub() );  
		} catch(RemoteException e) { 
			logger.error("Unable to disconnect the client. " + e.getMessage());
			throw e;
		}
	}
	
	/** 
	 * Bind a new RemoteDBLayerFactory to the rmiregistry on the specified <code>port</code>. 
	 * If the rmiregistry is not currently running on that port, it will be started. 
	 * 
	 * @throws AlreadyBoundException If another RemoteDBLayerFactory is already bound to the rmiregistry. 
	 */
	public synchronized void start() 
	throws AlreadyBoundException, RemoteException {
		try {
			
			int timeout = Math.min(Math.max(1, settings.getTimeout()), 30) * 60000;
			System.setProperty( "java.rmi.dgc.leaseValue", new Integer(timeout).toString() );

			// Locate (or start) the rmiregistry on the specified port
			Registry registry;
			try {	
				registry = LocateRegistry.createRegistry(settings.getPort()); 
			}
			catch(Exception e) { 
				registry = LocateRegistry.getRegistry(settings.getPort()); 
			}
			// Create a new factory or reuse an existing one.
			if(remoteFactory == null) 
				remoteFactory = new RMIRemoteDBLayerFactory( settings );
			// Bind the factory to the rmiregistry. 
			registry.bind(RemoteDBLayerFactory.ID, remoteFactory);

			logger.info("The RemoteDBLayerFactory has been bound to the rmiregistry.");
		}
		catch(RemoteException e) { 
			logger.error("Unable to start the server. " + e.getMessage());
			throw e;
		}
	}
	
	/** 
	 * Unbind the RemoteDBLayerFactory from the rmiregistry on the specified <code>port</code>. 
	 * This will terminate the server completely (all clients will be disconnected immediately).
	 */
	public synchronized void stop() 
	throws RemoteException {
		if(remoteFactory == null) return;
		try {
			// 1. Unbind the RemoteDBLayerFactory from the rmiregistry 
			// -> noone can obtain the stub of the remote factory anymore
			RMI.unbind(settings.getPort(), RemoteDBLayerFactory.ID);
			logger.debug("The RemoteDBLayerFactory was unbound from the rmiregistry.");
			
			// 2. Unexport the remote factory -> noone can make a remote call anymore
			RMI.unexport(remoteFactory);
			logger.debug("The RemoteDBLayerFactory was unexported. It cannot accept remote calls now.");
			
			// 3. Disconnect all users from the server.
			remoteFactory.disconnectAll();
			logger.info("All clients were disconnected.");

			// 4. Disconnect this object from the RMI ->
			UnicastRemoteObject.unexportObject(this, true);
			logger.info("The RMIServer now stops accepting remote calls.");
			
			// 5. Disconnect the control guard and unexport it
			RMI.unbind(settings.getPort(), Guard.ID);
			RMI.unexport(guard);
			logger.debug("The ServerProxy is now unavailable.");
			
			logger.info("The Server terminates. Bye.");
		}
		catch(Exception e) { 
			logger.error("Unable to stop the server. " + e.getMessage());
		}
	}
	
	/**
	 * Test, whether the Server is still alive.
	 */
	public void ping() 
	throws RemoteException {
		logger.debug("Pinged!");
	}

}
