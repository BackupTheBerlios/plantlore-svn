package net.sf.plantlore.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import net.sf.plantlore.middleware.RemoteDBLayerFactory;

/**
 * The "starter" class. This class is capable of binding a particular implementation
 * of the RemoteDBLayer interface to the rmiregistry. 
 * <br/>
 *  If the server is terminated, all remote objects that live within the server's memory are destroyed as well!
 *  
 * TODO: 
 * 1. Použít při oznamování chyb Logger. 
 * 2. Chyby by se měly zobrazovat lokalizovaně - použít odpovídající property file.
 * 
 * 
 * @see RemoteDBLayerFactory, RMIDBLayerFactory
 * @author Erik Kratochvíl
 * @since 2006-03-11
 * @version 0.9 Under construction
 */
public class Server {
	
	/** The default port where the rmiregistry listens. To that rmiregistry the RemoteDBLayerFactory will be bound to. */
	public static final short DEFAULT_PORT = Registry.REGISTRY_PORT;
	
	/** Initialize the codebase java.rmi.server.property to the specified directory or the
	 * current working directory if the specified directory is null. */
	public static void initialize(String directory) {
		String codebase = "file:/" + ((directory != null) ? directory : System.getProperty("user.dir")) + "/";
		codebase = codebase.replaceAll(" ", "%20"); // to prevent the MalformedURLException
		System.setProperty("java.rmi.server.codebase", codebase);
	}

	/** 
	 * Bind a new RemoteDBLayerFactory to the rmiregistry on the specified <code>port</code>. 
	 * If the rmiregistry is not currently running on that port, it will be created. 
	 * 
	 * @return The RemoteDBLayerFactory <b>object</b> (not stub!) that has been bound to the rmiregistry or null if the call was unsuccessful.
	 * @throws AlreadyBoundException If another RemoteDBLayerFactory is already bound to the rmiregistry. 
	 */
	public static RemoteDBLayerFactory start(short port) throws AlreadyBoundException {
		RemoteDBLayerFactory remoteFactory = null;
		try {
			// Locate (or start) the rmiregistry on the specified port
			Registry registry;
			try {	registry = LocateRegistry.createRegistry(port); }
			catch(Exception e) { registry = LocateRegistry.getRegistry(port); }
			// Rebind a new factory. This can be potentially dangerous as 
			remoteFactory = new RMIRemoteDBLayerFactory();
			registry.bind(RemoteDBLayerFactory.RemoteFactoryID, remoteFactory);
		}
		catch(RemoteException e) { System.err.println(e); } // FIXME: use logger
		
		return remoteFactory;
	}
	
	/** Unbind the RemoteDBLayerFactory from the rmiregistry on the specified <code>port</code>. 
	 * The rmiregistry on the specified port is <b>not</b> stopped, because some other programs may be still
	 * using it!
	 * <hr/> 
	 * Note: The RemoteDBLayerFactory is not destroyed either! It is because some clients may have some opened
	 * connections and they may ask the RemoteDBLayerFactory do destroy their DBLayer objects eventually. 
	 * The RemoteDBLayerFactory will
	 * be <i>destroyed</i> (here it means <i>becomes unaccessible</i>) 
	 * when all clients are disconnected (i.e. when they forget their remote references to the RemoteDBLayerFactory), 
	 * because only then all references and remote references will be lost
	 * -> the garbage collector will be able to sweep it.
	 */
	public static void stop(RemoteDBLayerFactory remoteFactory, short port) {
		try {
			// 1. Unbind the RemoteDBLayerFactory from the rmiregistry 
			// -> noone can obtain the stub of the remote factory anymore
			Registry registry = LocateRegistry.getRegistry(port);
			registry.unbind(RemoteDBLayerFactory.RemoteFactoryID);
			// 2. Unexport the remote factory -> noone can make a remote call anymore
			UnicastRemoteObject.unexportObject(remoteFactory, true);
			// 3. Disconnect all users from the server.
			/* SHALL I? */
		}
		catch(Exception e) { System.err.println(e); } // FIXME: use logger
	}
	


}
