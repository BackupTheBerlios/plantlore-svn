package net.sf.plantlore.middleware;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import net.sf.plantlore.client.login.DBInfo;
import net.sf.plantlore.common.exception.DBLayerException;

/** 
 * The interface for the Database Layer Factory.
 * The creation of the Database Layer is very complex
 * and it is not possible to achieve the desired complexity
 * by a simple <i>new DBLayer</i>.
 * <br/> 
 * The Database Layer Factory is capable of both creating a new
 * database layer and its proper destruction.
 * 
 * @author kaimu
 * @version 1.0
 * @since 2006-03-01
 */
public interface DBLayerFactory {
	

	/** 
	 * Create a new instance of the Database Layer 
	 * that will manage the connection to the (possibly remote) database.
	 *
	 *  @param settings	The information that are necessary for the Database Layer creation.
	 *  
	 *  @see net.sf.plantlore.client.login.DBInfo
	 */
	DBLayer create(DBInfo settings) throws RemoteException, NotBoundException, DBLayerException;
	
	/** 
	 * Destroy the Database Layer properly. After this call the Database Layer should not be used anymore.
	 * 
	 * @param db		The DBLayer to be destroyed.
	 */
	void destroy(DBLayer db) throws RemoteException;

}
