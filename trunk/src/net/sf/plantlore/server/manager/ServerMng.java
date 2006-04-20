package net.sf.plantlore.server.manager;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;

import org.apache.log4j.Logger;


import net.sf.plantlore.server.*;


public class ServerMng extends Observable {
	
	private Server<ConnectionInfo> server;
	private Collection<ConnectionInfo> clients, selected;
	
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
	
	
	public ConnectionInfo[] getConnectedUsers() {
		if(server == null) return null;
		try {
			clients = /*(Collection<ConnectionInfo>)*/server.getClients();
			logger.debug("Clients connected to the server received.");
			setChanged(); notifyObservers("PH");
		} catch( RemoteException e) { logger.warn("Unable to obtain the list of connected users - network error?"); }
		return clients.toArray(new ConnectionInfo[0]);
	}
	
	
	public void setSelectedClients(ConnectionInfo[] selected) {
		this.selected = new ArrayList<ConnectionInfo>(selected.length + 2);
		for(ConnectionInfo client : selected) this.selected.add(client);
		logger.debug("Selected clients stored.");
	}
	
	
	public void kickSelectedClients() throws RemoteException {
		if(server == null) return;
		for(ConnectionInfo client : selected) {
			logger.info("Kicking " + client);
			server.disconnect(client);
		}
		logger.debug("All selected clients have been kicked.");
		getConnectedUsers();
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
