package net.sf.plantlore.server.manager;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Observable;

import org.apache.log4j.Logger;


import net.sf.plantlore.server.*;


public class ServerMng extends Observable {
	
	private Server server;
	private ConnectionInfo[] clients;
	
	public enum Mode { CREATE_NEW, CONNECT_EXISTING };
	
	private Mode mode = Mode.CREATE_NEW;
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
		
	
	public void startNewServer(int port, String password) throws AlreadyBoundException, RemoteException {
		logger.info("Creating a new server at localhost:" + port);
		server = new RMIServer(port, password);
		logger.info("Starting the server ...");
		server.start();
		logger.info("Server up and running");
	}

	
	public void connectToRunningServer(String host, int port, String password) throws RemoteException, NotBoundException {
		logger.info("Connecting to a running server at " +host+":"+port);
		Registry registry = LocateRegistry.getRegistry(host, port);
		Guard guard = (Guard) registry.lookup(Guard.ID);
		server = guard.certify(password);
	}
	
	
	public void actAsInstructed(String host, int port, String password) throws RemoteException, NotBoundException, AlreadyBoundException {
		if(mode == Mode.CREATE_NEW) startNewServer(port, password);
		else connectToRunningServer(host, port, password);
	}
	
	
	public ConnectionInfo[] getConnectedUsers(boolean refresh) {
		if(server == null) return null;
		if(refresh)
			try {
				clients = server.getClients();
				logger.debug("Clients connected to the server received.");
				setChanged(); notifyObservers("PHear me!");
			} catch( RemoteException e) { logger.warn("Unable to obtain the list of connected users - network error?"); }
		return clients;
	}
	
	
	public void kick(ConnectionInfo client) throws RemoteException {
		if(server == null || clients == null) return;
		logger.info("Kicking " + client);
		server.disconnect(client);
	
		getConnectedUsers(true);
	}
	
	
	public void terminateServer() throws RemoteException {
		if(server != null) server.stop(true);
		logger.info("Server terminated - all clients disconnected.");
	}
	
	public void stopServer() throws RemoteException {
		if(server !=null) server.stop(false);
		logger.debug("Server stopped - the server will terminate after the last client finishes his work.");
	}
	
	
	public void setMode(Mode m) {
		this.mode = m;
		logger.debug("Server mode " + mode);
		setChanged(); notifyObservers(this.mode);
	}
	
	public Mode getMode() { 
		return this.mode; 
	}

}
