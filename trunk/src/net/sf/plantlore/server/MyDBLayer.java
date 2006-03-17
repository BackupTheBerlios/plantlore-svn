package net.sf.plantlore.server;

import java.rmi.RemoteException;

import net.sf.plantlore.middleware.DBLayer;

/** Only a test. This class will be replaced with Kovo's database layer (much more sophisticated, at least we hope). */
public class MyDBLayer implements DBLayer {

	
	public void initialize() throws RemoteException {
		System.out.println("initialized");
	}

	
	public void close() throws RemoteException {
		System.out.println("destroyed");
	}

}
