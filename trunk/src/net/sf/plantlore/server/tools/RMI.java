package net.sf.plantlore.server.tools;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;




public class RMI {
	
	private static Logger logger = Logger.getLogger(RMI.class.getPackage().getName());
	
	public static final String PROPERTY_CODEBASE = "java.rmi.server.codebase";
	public static final String PROPERTY_LEASEVALUE = "java.rmi.dgc.leaseValue";
	public static final String PROPERTY_HOSTNAME = "java.rmi.server.hostname";
	

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
		logger.info("java.rmi.server.codebase = " + codebase);
	}
	
	
	public static void setLeaseValue(int value) {
		System.setProperty( PROPERTY_LEASEVALUE, Integer.toString(value) ); 
	}
	
	
	public static String setHostName() {
		// Skip the autodetection if the User supplied another value
		// so as not to undermine His effort.
		String hostnameFromUser = System.getProperty(PROPERTY_HOSTNAME);
		if( hostnameFromUser != null && !"127.0.0.1".equals(hostnameFromUser) )
			return hostnameFromUser;
		
		try {
			// Obtain the IP of this host.
			InetAddress address = InetAddress.getLocalHost();
			String ip = address.getHostAddress();
			
			// getLocalHost().getHostAddress() failed!
			if( "127.0.0.1".equals(ip) ) {
				// Try to obtain the IP using the host name instead of getLocalHost().
				String name = address.getHostName();
				logger.info("The name of this machine is " + name);
				address = InetAddress.getByName(name);
				ip = address.getHostAddress();
				// Again, no result. Try to scan all IP addresses and return the first that is not 127.0.0.1
				if( "127.0.0.1".equals(ip) ) {
					logger.warn("I'm getting a little desperate here!");
					InetAddress[] addresses = InetAddress.getAllByName(name);
					for(InetAddress inetAddress : addresses) {
						ip = inetAddress.getHostAddress();
						if( !"127.0.0.1".equals(ip) )
							break;
					}
					if(  "127.0.0.1".equals(ip) ) {
						// It is not possible to autodetect the IP address.
						logger.fatal("Unable to obtain the " + name + "'s ip! Remote connections may not be possible! Please specify it yourself by adding java -Djava.rmi.server.hostname=YourIP");
						return null;
					}

				}
			}
			// Set the hostname IP address.
			System.setProperty(PROPERTY_HOSTNAME, ip);
			logger.info("Hostname set to " + ip);
			return ip;
		} catch (UnknownHostException e) {
			logger.fatal("Unable to obtain the host ip! Remote connections may not be possible! " + e.getMessage());
			return null;
		}
	}

}
