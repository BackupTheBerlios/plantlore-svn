package net.sf.plantlore.server;


import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;


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
public class RMIServer extends UnicastRemoteObject implements Server<ConnectionInfo> {
	
	/** The default port where the rmiregistry listens. To that rmiregistry the RemoteDBLayerFactory will be bound to. */
	public static final int DEFAULT_PORT = Registry.REGISTRY_PORT;
	
	
	
	private RMIRemoteDBLayerFactory remoteFactory = null;
	private int port = DEFAULT_PORT;
	private Guard guard = null;
	
	private Logger logger;

	/** Create a new instance of RMIServer running on the default port. */
	public RMIServer(String password) throws RemoteException, AlreadyBoundException {
		this(DEFAULT_PORT, password);
	}
	
	/** Create a new instance of RMIServer running on the specified port. */
	public RMIServer(int port, String password) throws RemoteException, AlreadyBoundException {
		this.port = port;
		
		logger = Logger.getLogger(this.getClass().getPackage().getName());
		
		// Control object that will return the server after the client passed a valid certif. information
		guard = new RMIServerControl(this, password);
		RMI.bind(port, guard, Guard.ID);
	}
	
	/** Get the information about the connected clients. */
	public synchronized Collection<ConnectionInfo> getClients() { return remoteFactory.getClients(); }

	/**
	 * Disconnect the selected client.
	 * 
	 * @param client The client to be kicked.
	 */
	public synchronized void disconnect(ConnectionInfo client) {
		try { remoteFactory.destroy(client.getStub()); } 
		catch(RemoteException e) { logger.warn(e); }
	}
	
	/** 
	 * Bind a new RemoteDBLayerFactory to the rmiregistry on the specified <code>port</code>. 
	 * If the rmiregistry is not currently running on that port, it will be created. 
	 * 
	 * @throws AlreadyBoundException If another RemoteDBLayerFactory is already bound to the rmiregistry. 
	 */
	public synchronized void start() throws AlreadyBoundException {
		try {
			// Locate (or start) the rmiregistry on the specified port
			Registry registry;
			try {	registry = LocateRegistry.createRegistry(port); }
			catch(Exception e) { registry = LocateRegistry.getRegistry(port); }
			// Create a new factory or reuse an existing one.
			if(remoteFactory == null) remoteFactory = new RMIRemoteDBLayerFactory();
			// Bind the factory to the rmiregistry. 
			registry.bind(RemoteDBLayerFactory.ID, remoteFactory);
			
			logger.info("The RemoteDBLayerFactory has been bound to the rmiregistry.");
		}
		catch(RemoteException e) { logger.error(e); }
	}
	
	/** 
	 * Unbind the RemoteDBLayerFactory from the rmiregistry on the specified <code>port</code>. 
	 * The rmiregistry on the specified port is <b>not</b> stopped, because some other programs may be still
	 * using it!<br/>
	 * This will terminate the server completely.
	 * 
	 * @param harsh		Be harsh and disconnect every client connected to the server.
	 */
	public synchronized void stop(boolean harsh) {
		if(remoteFactory == null) return;
		try {
			// 1. Unbind the RemoteDBLayerFactory from the rmiregistry 
			// -> noone can obtain the stub of the remote factory anymore
			RMI.unbind(port, RemoteDBLayerFactory.ID);
			logger.debug("The RemoteDBLayerFactory was unbound from the rmiregistry.");
			
			// 2. Unexport the remote factory -> noone can make a remote call anymore
			RMI.unexport(remoteFactory);
			logger.debug("The RemoteDBLayerFactory was unexported. It cannot accept remote calls now.");
			
			// 3. Disconnect all users from the server.
			if(harsh) {
				remoteFactory.disconnectAll();
				logger.info("All clients were disconnected.");
			}

			// 4. Disconnect this object from the RMI ->
			UnicastRemoteObject.unexportObject(this, true);
			logger.info("The RMIServer now stops accepting remote calls.");
			
			// 5. Disconnect the control guard and unexport it
			RMI.unbind(port, Guard.ID);
			RMI.unexport(guard);
			logger.debug("The ServerProxy is now unavailable.");
			
			logger.info("The Server terminates. Bye.");
		}
		catch(Exception e) { logger.error(e); }
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
