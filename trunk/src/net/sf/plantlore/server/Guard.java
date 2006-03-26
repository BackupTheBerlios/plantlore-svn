package net.sf.plantlore.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Guard extends Remote {
	
	final static String ID = "PlantloreRMIServerGuard";

	Server certify(String authorizationInfo) throws RemoteException;
	
}
