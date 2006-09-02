package net.sf.plantlore.middleware;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import net.sf.plantlore.server.ConnectionInfo;
import net.sf.plantlore.server.DatabaseSettings;
import net.sf.plantlore.client.login.DBInfo;
import net.sf.plantlore.common.Utils;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.server.HibernateDBLayer;
import net.sf.plantlore.server.tools.RMI;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.RemoteDBLayerFactory;


/**
 * The RMIDBLayerFactory can create and destroy Database Layers (DBLayers in short) 
 * - either local or remote. 
 * The client should ask the factory to create the DBLayer and the factory will shield him from
 * all the details how the DBLayer is created or where those objects really live.
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
 * <br/>
 * The RMIDBLayerFactory is a singleton.
 * 
 *  
 * @author Erik Kratochvíl
 * @since 2006-03-11
 * @version 1.0
  */
public class RMIDBLayerFactory implements DBLayerFactory {
	
	private static Logger logger = Logger.getLogger(RMIDBLayerFactory.class.getPackage().getName()); 
	
	private static RMIDBLayerFactory databaseLayerFactory;
	
	
	/** Keep information about connections to databases. */
	private Hashtable<DBLayer, ConnectionInfo> client = new Hashtable<DBLayer, ConnectionInfo>(4);
	
	
	/** 
	 * Create a new instance of DBLayerFactory.
	 * The RMIDBLayerFactory is a singleton and can be obtained via getDBLayerFactory() only. 
	 */
	private RMIDBLayerFactory() {}
	
	/**
	 * Construct a new Database Layer factory. 
	 * 
	 * @return	The new Database Layer factory.
	 */
	public static DBLayerFactory getDBLayerFactory() {
		if(databaseLayerFactory == null) {
			String codebase = Utils.getCodeBasePath();
			if(codebase == null)
				logger.warn("The codebase may not have been set! This may cause serious problems when trying to connect to a remote database! It is imperative you make sure the codebase is set properly!!");
			else
				net.sf.plantlore.server.tools.RMI.addToCodebase( codebase );
			
			databaseLayerFactory = new RMIDBLayerFactory();
		}
		return databaseLayerFactory;
	}
	
	
	/**
	 * Create a new DBLayer.
	 * <br/>
	 * The Database Layer may be either local or remote, 
	 * but the caller should spot no difference. 
	 * 
	 * @param settings	The information that are necessary for the Database Layer creation.
	 * @return The newly constructed Database Layer.
	 */
	public synchronized DBLayer create(DBInfo settings) 
	throws RemoteException, NotBoundException, DBLayerException {
		String host = settings.getHost();
		
		// ~~~ LOCAL CONNECTION ~~~
		if( host == null || host.equals("") ) {
			
			DatabaseSettings dbSettings = new DatabaseSettings(
					settings.getDatabaseType(),
					settings.getDatabasePort(),
					settings.getDatabaseParameter()
			);
			
			DBLayer db = new HibernateDBLayer( dbSettings );
			ConnectionInfo info = new ConnectionInfo(null, db, null, "localhost (direct connection)");
			client.put(db, info);
			
			logger.info("New DBLayer created: " + info);
			
			return db;
		}

		// Perform a simple check.
		String codebase = System.getProperty(RMI.PROPERTY_CODEBASE); 
		if( codebase  == null || codebase.length() == 0)
			logger.warn("The codebase is not set! It is highly likely it will be impossible obtain the remote DBLayer factory or the DBLayer!");
		
		// ~~~ FULL REMOTE CONNECTION ~~~
		int port = settings.getPort();
		
		logger.debug("Creating a new DBLayer using the RMI:");
		
		// Connect to the remote server and obtain the RemoteDBLayerFactory
		logger.debug("1. Connecting to the remote registry @ " + host + ":" + port +" ...");
		Registry registry = LocateRegistry.getRegistry(host, port);
		
		
		logger.debug("2. Obtaining the remote dblayer factory ...");
		RemoteDBLayerFactory remoteFactory = (RemoteDBLayerFactory) registry.lookup(RemoteDBLayerFactory.ID);
		
		
		// Get the stub from the remote factory and save the information about the connection
		logger.debug("3. Creating a new dblayer...");
		DBLayer stub = remoteFactory.create(); // DBLayerException can spawn here (too many users!)
		
		
		ConnectionInfo info = new ConnectionInfo(remoteFactory, null, stub, "localhost -> " + host + ":" + port);
		client.put(stub, info);
		
		// Set the same language on the server side.
		logger.debug("4. Setting the language...");
		stub.setLanguage( L10n.getLanguageLocale() );
		
		logger.info("New DBLayer created: " + info);
		
		return stub;
	}

	/**
	 * Destroy a Database Layer. This is necessary especially with remote DBLayers, 
	 * because the server keeps track of all connected clients
	 * and has a certain limit of how many clients can be connected simultaneously.
	 * Therefore we should tell the server we want to disconnect from it so as not to
	 * block others from using the server.
	 * 
	 * @param db The database layer to be taken care of.
	 */
	public synchronized void destroy(DBLayer db) throws RemoteException {
		if(db == null)
			return;
		
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
