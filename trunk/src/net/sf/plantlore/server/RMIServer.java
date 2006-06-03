package net.sf.plantlore.server;


import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


import org.apache.log4j.Logger;


import net.sf.plantlore.middleware.RemoteDBLayerFactory;
import net.sf.plantlore.server.tools.*;

/**
 *  
 * <br/>
 *  The server is terminated
 *  <=> 
 *  All remote objects that have been exported are unexported.
 *  
 * @see RemoteDBLayerFactory, RMIDBLayerFactory
 * @author Erik Kratochvíl
 * @since 2006-03-11
 * @version 1.0
 */
public class RMIServer extends UnicastRemoteObject implements Server {
	
	/** The default port where the rmiregistry listens. To that rmiregistry the RemoteDBLayerFactory will be bound to. */
	public static final int DEFAULT_PORT = Registry.REGISTRY_PORT;
	
	
	
	private RMIRemoteDBLayerFactory remoteFactory = null;
	private Guard guard = null;
	private ServerSettings settings;
	
	private Logger logger;


	
	/** Create a new instance of RMIServer running on the specified port. */
	public RMIServer(ServerSettings settings, String password) 
	throws RemoteException, AlreadyBoundException {
		logger = Logger.getLogger(this.getClass().getPackage().getName());
		this.settings = settings;
		
		// Control object that will return the server after the client passed a valid certif. information
		guard = new RMIServerControl(this, password);
		RMI.bind(settings.getPort(), guard, Guard.ID);
	}
	
	/** Get the information about the connected clients. */
	public synchronized ConnectionInfo[] getClients() {
		if(remoteFactory == null) return null;
		else return remoteFactory.getClients(); 
	}

	/**
	 * Disconnect the selected client.
	 * 
	 * @param client The client to be kicked.
	 */
	public synchronized void disconnect(ConnectionInfo client) {
		try { 
			remoteFactory.destroy(client.getStub()); 
		} catch(RemoteException e) { 
			logger.warn(e.getMessage()); 
		}
	}
	
	/** 
	 * Bind a new RemoteDBLayerFactory to the rmiregistry on the specified <code>port</code>. 
	 * If the rmiregistry is not currently running on that port, it will be created. 
	 * 
	 * @throws AlreadyBoundException If another RemoteDBLayerFactory is already bound to the rmiregistry. 
	 */
	public synchronized void start() throws AlreadyBoundException {
		try {
			
			int timeout = Math.min(Math.max(1, settings.getTimeout()), 30) * 60000;
			System.setProperty( "java.rmi.dgc.leaseValue", new Integer(timeout).toString() );

			// Locate (or start) the rmiregistry on the specified port
			Registry registry;
			try {	registry = LocateRegistry.createRegistry(settings.getPort()); }
			catch(Exception e) { registry = LocateRegistry.getRegistry(settings.getPort()); }
			// Create a new factory or reuse an existing one.
			if(remoteFactory == null) remoteFactory = new RMIRemoteDBLayerFactory( settings );
			// Bind the factory to the rmiregistry. 
			registry.bind(RemoteDBLayerFactory.ID, remoteFactory);

			
			// Nemel by server exportnout i sebe??

			
			logger.info("The RemoteDBLayerFactory has been bound to the rmiregistry.");
		}
		catch(RemoteException e) { logger.error(e); }
	}
	
	/** 
	 * Unbind the RemoteDBLayerFactory from the rmiregistry on the specified <code>port</code>. 
	 * The rmiregistry on the specified port is <b>not</b> stopped, because some other programs may be still
	 * using it!<br/>
	 * This will terminate the server completely.
	 */
	public synchronized void stop() {
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
			logger.error(e.getMessage()); 
		}
	}

	
	/** 
	 * Initialize the codebase java.rmi.server.property to the specified directory or the
	 * current working directory if the specified directory is null.
	 * 
	 *  @param directory	The directory that shall serve as a codebase. */
	public static void initialize(String directory) {
		String codebase = "file:/" + ((directory != null) ? directory : System.getProperty("user.dir")) + "/";
		codebase = codebase.replaceAll(" ", "%20"); // to prevent the MalformedURLException
		System.setProperty("java.rmi.server.codebase", codebase);
		//System.out.println("java.rmi.server.codebase = " + codebase);
		
		System.setProperty("java.rmi.dgc.leaseValue", "30000"); // 30 seconds, just for DEBUG.REASONS
	}
	
	
	/** Static initialization. */
	static {
		initialize(null);
	}
	

}
