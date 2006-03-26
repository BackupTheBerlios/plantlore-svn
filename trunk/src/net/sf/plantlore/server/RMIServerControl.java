package net.sf.plantlore.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerControl extends UnicastRemoteObject implements Guard {
	
	private Server server;

	public RMIServerControl(Server server) throws RemoteException {
		this.server = server;
	}
	
	public Server certify(String authorizationInfo) throws RemoteException {
		if(authorizationInfo.equals("poweroverwhelming")) return server;
		return null;
	}
	
}
