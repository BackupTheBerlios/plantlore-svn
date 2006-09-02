package net.sf.plantlore.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import net.sf.plantlore.l10n.L10n;

/**
 * The Server Control guards the access to the Server 
 * and prevents unauthorized Users from shutting it down.
 * In order to manage the Server (terminate the Server, view
 * the list of connected clients, disconnecting the clients)
 * the User must pass the authorization procedure and
 * supply valid authorization key (password).
 * 
 * @author Erik Kratochvíl
 * @since 2006-05-30
 * @version 1.0
 */
public class RMIServerControl extends UnicastRemoteObject implements Guard {
	
	private static final long serialVersionUID = 2006060433140537L;
	
	private Server server;
	private String password;

	/**
	 * Create a new Guard of the Server.
	 * 
	 * @param server	The Server that shall be protected.
	 * @param password	The authorization key protecting the access to the Server.
	 */
	public RMIServerControl(Server server, String password) throws RemoteException {
		this.server = server;
		this.password = password;
	}
	
	/**
	 * The certification procedure. If the supplied authorization information
	 * matches the password protecting the Server, the Server is returned
	 * (Server is the interface for managing the Plantlore Server).
	 * 
	 * @return The Server if the 
	 */
	public Server certify(String authorizationInfo) throws RemoteException, CertificationException {
		if(authorizationInfo.equals(password)) 
			return server;
		throw new CertificationException(L10n.getString("Error.WrongAuthorizationKey"));
	}
	
}
