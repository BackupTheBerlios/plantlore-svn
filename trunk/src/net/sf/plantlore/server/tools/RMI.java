package net.sf.plantlore.server.tools;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;




public class RMI {
	
	public static void bind(Remote object, String name) 
	throws RemoteException, AccessException, AlreadyBoundException {
		
		bind(Registry.REGISTRY_PORT, object, name);
	}
	
	
	public static void bind(int port, Remote object, String name) 
	throws RemoteException, AccessException, AlreadyBoundException {
		
		// Locate (or start) the rmiregistry on the specified port
		Registry registry;
		try {	registry = LocateRegistry.createRegistry(port); }
		catch(Exception e) { registry = LocateRegistry.getRegistry(port); }
	
		// Bind the object to the rmiregistry.
		registry.bind(name, object);
	}
	
	
	
	public static void unbind(String name)
	throws RemoteException, AccessException, NotBoundException {
		
		unbind(Registry.REGISTRY_PORT, name);
	}
	
	
	public static void unbind(int port, String name) 
	throws RemoteException, AccessException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(port);
		registry.unbind(name);
	}
	
	
	public static void unexport(Remote object) throws NoSuchObjectException {
		UnicastRemoteObject.unexportObject(object, true);
	}

}
