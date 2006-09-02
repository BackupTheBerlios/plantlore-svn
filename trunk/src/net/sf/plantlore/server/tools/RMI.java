package net.sf.plantlore.server.tools;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;




public class RMI {
	
	
	public static final String PROPERTY_CODEBASE = "java.rmi.server.codebase";
	public static final String PROPERTY_LEASEVALUE = "java.rmi.dgc.leaseValue";
	

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
	
	
	/** 
	 * Initialize the codebase java.rmi.server.property to the specified directory or the
	 * current working directory if the specified directory is null.
	 * <br/>
	 * Do not forget that directories must end with a slash 
	 * while JAR files must not.
	 * 
	 *  @param directory	The directory that shall serve as a codebase. */
	public static void addToCodebase(String directory) {
		if( directory == null  || directory.length() <= 0 )
			return;
		
		String codebase = System.getProperty(PROPERTY_CODEBASE);
		directory = "file:/" + directory.replaceAll(" ", "%20"); // to prevent the MalformedURLException
		
		if( codebase != null && codebase.length() > 0 ) {
			if( !codebase.contains(directory) )
				codebase = codebase + " " + directory;
		}
		else
			codebase = directory;
		
		System.setProperty(PROPERTY_CODEBASE, codebase);
		System.out.println("java.rmi.server.codebase = " + codebase);
	}
	
	
	public static void setLeaseValue(int value) {
		System.setProperty( PROPERTY_LEASEVALUE, Integer.toString(value) ); 
	}

}
