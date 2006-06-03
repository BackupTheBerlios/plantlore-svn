package net.sf.plantlore.middleware;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import net.sf.plantlore.client.login.DBInfo;
import net.sf.plantlore.common.exception.DBLayerException;

/** 
 * Interface for obtaining the (possibly remote) DBLayer object.
 * 
 * @author Erik Kratocvhíl
 * @version 1.0 final
 * @since The beginning of time.
 */
public interface DBLayerFactory {
	

	/** 
	 * Create a new instance of the DBLayer that will manage the connection to the remote database.
	 *  
	 * @param host	Hostname of the server.
	 * @param port	Port where the server is listening.
	 * @return	Remote reference of the DBLayer.
	 * @throws RemoteException		If the RMI encounters a problem.
	 * @throws NotBoundException	If the server is not running on the specified port.
	 */
	DBLayer create(DBInfo settings) throws RemoteException, NotBoundException, DBLayerException;
	
	/** 
	 * Destroy the DBLayer. This is a special measure, 
	 * so that the db connection can be closed and the object disconnected (unexported).
	 * 
	 * @param db		The DBLayer to be destroyed.
	 * @throws RemoteException		If the RMI encounters a problem.
	 */
	void destroy(DBLayer db) throws RemoteException;

}
