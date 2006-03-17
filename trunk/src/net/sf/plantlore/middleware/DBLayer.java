package net.sf.plantlore.middleware;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/** The interface used for the communication with the (possibly remote) database. */
public interface DBLayer extends Serializable, Remote {
	
	public void initialize() throws RemoteException;
	public void close() throws RemoteException;

}
