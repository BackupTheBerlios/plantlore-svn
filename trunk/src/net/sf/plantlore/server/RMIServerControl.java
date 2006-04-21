package net.sf.plantlore.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerControl extends UnicastRemoteObject implements Guard {
	
	private Server server;
	private String password;

	public RMIServerControl(Server server, String password) throws RemoteException {
		this.server = server;
		this.password = password;
	}
	
	public Server certify(String authorizationInfo) throws RemoteException, CertificationException {
		if(authorizationInfo.equals(password)) return server;
		throw new CertificationException("Wrong password.");
		//return null;
	}
	
}
