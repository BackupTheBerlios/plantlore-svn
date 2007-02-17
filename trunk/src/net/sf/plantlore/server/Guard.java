package net.sf.plantlore.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An interface that guards the access to the Server
 * for administration purposes.
 * <br/>
 * In order to manage the Server (terminate the Server, view
 * the list of connected clients, disconnecting the clients, etc.)
 * the User must pass the authorization procedure and
 * supply valid authorization key (password).
 * 
 * @author kaimu
 * @since 2006-05-30
 */
public interface Guard extends Remote {
	
	final static String ID = "PlantloreRMIServerGuard";

	/**
	 * In order to connect to the server to administrate it,
	 * you must pass this certification test. If the authorization information
	 * fits, the Server will be returned.
	 * 
	 * @param authorizationInfo	The password protecting the Server.
	 * @return	The server if the authorization was correct.
	 * 
	 */
	Server certify(String authorizationInfo) throws RemoteException, CertificationException;
	
}
