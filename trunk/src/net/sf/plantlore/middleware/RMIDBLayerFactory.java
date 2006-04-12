package net.sf.plantlore.middleware;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import net.sf.plantlore.server.ConnectionInfo;
import net.sf.plantlore.server.HibernateDBLayer;
import net.sf.plantlore.middleware.RemoteDBLayerFactory;


/**
 * The RMIDBLayerFactory can create and destroy DBLayers - either local or remote. 
 * The client should ask the factory to create the DBLayer and the factory will shield him from
 * all the details how the DBLayer is created or where those objects really live.
 * <br/>
 * RMIDBLayer factory lives in the client's JVM.   
 * <br/>
 * The RMIDBLayerFactory keeps track of all database layers it has created and where they
 * actually live.
 * <table>
 * <tr><th></th><th>remote factory</th><th>database</th><th>stub</th><th>client</th></tr>
 * <tr><th>local</th><td>null</td><td>yes</td><td>null</td><td>"localhost (direct connection)"</td></tr>
 * <tr><th>stub</th><td>yes</td><td>null</td><td>yes</td><td>"localhost -> server name"</td></tr>
 * </table>
 * <br/>
 * The DBLayer factory is responsible for creating and destroying DBLayers.
 * It has no responsibility for initialization of those DBLayers.
 * 
 *  
 * @author Erik Kratochvíl
 * @since 2006-03-11
 * @version 1.0 final
  */
public class RMIDBLayerFactory implements DBLayerFactory {
	
	private Logger logger; 

	/** Keep information about connections to databases. */
	private Hashtable<DBLayer, ConnectionInfo> client = new Hashtable<DBLayer, ConnectionInfo>(4);
	
	/** Create a new instance of DBLayerFactory. */
	public RMIDBLayerFactory() {
		logger = Logger.getLogger(this.getClass().getPackage().getName()); 
	}
	

	/** 
	 * Create a new local DBLayer.
	 * <br/>
	 * This method creates purely local DBLayer. The DBLayer object lives in the local JVM. 
	 * No RMI gets involved here. 
	 * It is meant for connections to local database - the overhead is minimalized.
	 * 
	 *  @return The local DBLayer object that mediates the connection with the local database.
	 */
	public synchronized DBLayer create() {
		//Create a new DBLayer and save information about that connection 
		DBLayer db = new HibernateDBLayer();
		ConnectionInfo info = new ConnectionInfo(null, db, null, "localhost (direct connection)");
		client.put(db, info);
		
		logger.info("New DBLayer created: " + info);
		
		return db;
	}
	

	/**
	 * Create a new DBLayer.
	 * <br/>
	 * This method is meant for connections to remote databases. 
	 * The DBLayerFactory asks the server's RemoteDBLayerFactory to
	 * create a new DBLayer object there. The RemoteDBLayerFactory
	 * returns a stub of that object, which is what the caller will obtain.
	 * 
	 * @param host	Hostname of the computer where server runs.
	 * @param port	Port where the server listens.
	 * 
	 * @return A stub of the remote object that lives on the server and mediates the connection
	 * with the remote database.
	 */
	public synchronized DBLayer create(String host, int port) throws RemoteException, NotBoundException {
		// Some exceptional cases are handled specially.
		if(host == null || host.equals("") || host.equalsIgnoreCase("localhost"))
			return create();
		
		System.out.println("GOING RMI");
		
		// Connect to the remote server and obtain the RemoteDBLayerFactory
		Registry registry = LocateRegistry.getRegistry(host, port);
		RemoteDBLayerFactory remoteFactory = (RemoteDBLayerFactory) registry.lookup(RemoteDBLayerFactory.ID);
		// Get the stub from the remote factory and save the information about the connection
		DBLayer stub = remoteFactory.create();
		ConnectionInfo info = new ConnectionInfo(remoteFactory, null, stub, "localhost -> " + host + ":" + port);
		client.put(stub, info);
		
		logger.info("New DBLayer created: " + info);
		
		return stub;
	}

	/**
	 * Destroy a DBLayer instance. This is necessary for remote DBLayers, 
	 * because the server keeps track of all connected clients
	 * and has a certain limit of how many clients can be connected simultaneously.
	 * Therefore we should tell the server we want to disconnect from it so as not to
	 * block others from using the server.
	 * 
	 * @param db The database layer to be taken care of.
	 */
	public synchronized void destroy(DBLayer db) throws RemoteException {
		assert(db != null);
		
		// Get the stored information about the connection
		ConnectionInfo info = client.remove(db);
		if(info == null) {
			logger.warn("The DBLayerFactory cannot destroy objects that were not created by it!");
			return;
		}
		
		logger.info("The DBLayer (" + info + ") has been destroyed.");
		
		// Disconnect us from the remote server - we have only a remote reference, the server
		// must take care of destruction of the remote DBLayer object
		if(info.getRemoteFactory() != null) 
			info.getRemoteFactory().destroy(info.getStub());
		
		// Terminate all processes within the DBLayer otherwise
		else db.shutdown(); 
	}
	


}
