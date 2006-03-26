package net.sf.plantlore.server;

import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface Server<E> extends Remote, Serializable {
	
	Collection<? extends E>	getClients() throws RemoteException;
	void disconnect(E client) throws RemoteException;
	void start() throws RemoteException, AlreadyBoundException;
	void stop(boolean harsh) throws RemoteException;
	
}
