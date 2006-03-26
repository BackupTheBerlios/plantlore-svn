package net.sf.plantlore.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

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

	/** Create a new instance of RMIServer running on the default port. */
	public RMIServer() throws RemoteException, AlreadyBoundException {
		this(DEFAULT_PORT);
	}
	
	/** Create a new instance of RMIServer running on the specified port. */
	public RMIServer(int port) throws RemoteException, AlreadyBoundException { 
		this.port = port;
		// Control object that will return the server after the client passed a valid certif. information
		guard = new RMIServerControl(this);
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
		try { remoteFactory.destroy(client.getStub()); } catch(RemoteException e) {}
	}
	
	/** 
	 * Bind a new RemoteDBLayerFactory to the rmiregistry on the specified <code>port</code>. 
	 * If the rmiregistry is not currently running on that port, it will be created. 
	 * 
	 * @return The RemoteDBLayerFactory.
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
		}
		catch(RemoteException e) { System.err.println(e); } // FIXME: use logger
	}
	
	/** 
	 * Unbind the RemoteDBLayerFactory from the rmiregistry on the specified <code>port</code>. 
	 * The rmiregistry on the specified port is <b>not</b> stopped, because some other programs may be still
	 * using it!
	 * 
	 * FIXME: Krom toho stejně neumím rmiregistry ukončit pomocí javy.
	 * 
	 * @param harsh		Be harsh and disconnect every client connected to the server.
	 */
	public synchronized void stop(boolean harsh) {
		if(remoteFactory == null) return;
		try {
			// 1. Unbind the RemoteDBLayerFactory from the rmiregistry 
			// -> noone can obtain the stub of the remote factory anymore
			RMI.unbind(port, RemoteDBLayerFactory.ID);
			
			// 2. Unexport the remote factory -> noone can make a remote call anymore
			RMI.unexport(remoteFactory);
			
			// 3. Disconnect all users from the server.
			if(harsh) remoteFactory.disconnectAll();

			// 4. Disconnect this object from the RMI ->
			UnicastRemoteObject.unexportObject(this, true);
			
			// 5. Disconnect the control guard and unexport it
			RMI.unbind(port, Guard.ID);
			RMI.unexport(guard);
		}
		catch(Exception e) { System.err.println(e); } // FIXME: use logger
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
		System.out.println("java.rmi.server.codebase = " + codebase);
		
		System.setProperty("java.rmi.dgc.leaseValue", "30000"); // 30 seconds, just for DEBUG.REASONS
	}
	
	
	/** Static initialization. */
	static {
		initialize(null);
	}
	

}
