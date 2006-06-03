package net.sf.plantlore.server.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Observable;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.server.*;

/**
 * The server manager can
 * either create a new server or connect to an existing server.
 * <br/>
 * After the Server Administrator is logged in, he can control the server:
 * <ul>
 * <li>see the connected clients,</li>
 * <li>kick some of the connected clients,</li>
 * <li>terminate the server.</li>
 * </ul>
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-21
 * @version 1.0 final
 */
public class ServerMng extends Observable {
	
	public static final Object UPDATE_LIST = new Object();
	
	private static final String SERVER_CONFIG_NAME = "plantlore.server.xml";
	private static final String PLANTLORE = "plantlore";
	
	/** The server control interface. */
	private Server server;
	private ServerSettings settings;
	
	/** List of connected clients. */
	private ConnectionInfo[] clients;
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
	private String settingsFileName; {
		String userHome = System.getProperty("user.home"),
		osName = System.getProperty("os.name"),
		plantloreDirName = (osName.equals("Linux") ? "." : "") + PLANTLORE, 
		plantloreConfDir = userHome+File.separator+plantloreDirName;
		
		File plantloreConfDirFile = new File(plantloreConfDir);
		if (!plantloreConfDirFile.exists())
			plantloreConfDirFile.mkdir();
		
		settingsFileName = plantloreConfDir + File.separator + SERVER_CONFIG_NAME;
	}
	
	
	public ServerSettings getSettings() {

		// Create some default settings.
		settings = new ServerSettings(RMIServer.DEFAULT_PORT, 3, 32, 2, 
				new DatabaseSettings("postgresql", 5432, null, "", ""));
		
		try {
			Reader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(settingsFileName), "UTF-8"));
			SAXReader saxReader = new SAXReader();
            Document document = saxReader.read( reader );
            
            Node server = (Node)document.selectSingleNode("/config/server");
            Number portNumber = server.numberValueOf("port")/*,
            connectionsNumber = server.numberValueOf("connections"),
            peripNumber = server.numberValueOf("perip")*/;
            int port = (portNumber == null) ? RMIServer.DEFAULT_PORT : portNumber.intValue()/*,
            connections = (connectionsNumber == null) ? 16 : connectionsNumber.intValue(),
            perip = (peripNumber == null) ? 2 : peripNumber.intValue()*/;
            
            Node database = (Node)server.selectSingleNode("/database");
            
            String databaseType = database.valueOf("engine"),
            databaseParameter = database.valueOf("parameter"),
            databaseMasterUser = database.valueOf("masteruser");
            Number databasePortNumber = database.numberValueOf("port");
            int databasePort = (databasePortNumber == null) ? 0 : databasePortNumber.intValue();
            
            settings = new ServerSettings(port, 3, 32, 2, 
            		new DatabaseSettings(databaseType, databasePort, databaseParameter, databaseMasterUser, ""));
            
    		setChanged();
    		notifyObservers(L10n.getString("Server.SettingsLoaded"));
            
		} catch(Exception e) {
			logger.error("Settings could not be loaded. " + e.getMessage());
			setChanged();
			notifyObservers(L10n.getString("Server.SettingsNotLoaded"));
		}
		return settings;
	}
	
	
	
	public void setSettings(ServerSettings settings) {
		this.settings = settings;
		
		Document document = DocumentHelper.createDocument();
		Element config = document.addElement("config"),
		server = config.addElement("server");
		server.addElement("port").setText( "" + settings.getPort() );
		//server.addElement("connections").setText( "" + settings.getConnectionsTotal() );
		//server.addElement("perip").setText( "" + settings.getConnectionsPerIP() );
		
		Element db = server.addElement("database");
		db.addElement("engine").setText( settings.getDatabaseSettings().getDatabase() );
		db.addElement("port").setText( "" + settings.getDatabaseSettings().getPort() );
		db.addElement("parameter").setText(settings.getDatabaseSettings().getConnectionStringSuffix());
		db.addElement("masteruser").setText(settings.getDatabaseSettings().getMasterUser());

		try {
			Writer outputWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(settingsFileName), "UTF-8"));
			
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter xmlwriter = new XMLWriter( outputWriter, format );
			xmlwriter.write( document );
			xmlwriter.close();
			outputWriter.close();
			
			setChanged();
			notifyObservers(L10n.getString("Server.SettingsSaved"));
			
		} catch (java.io.IOException e) {
			logger.error("Settings could not be saved. " + e.getMessage());
			setChanged();
			notifyObservers(L10n.getString("Server.SettingsNotSaved"));
		}
	}
	
	
	
		
	
	/**
	 * Try to create and start a new server on the specified <code>port</code>.
	 * The server will be protected with a <code>password</code> so that 
	 * anyone who will try to connect to the server will have use that password.
	 * <br/>
	 * It is <b>not</b> possible to create a server on a remote machine
	 * (because of security reasons).
	 * 
	 * @param port The port where the server should listen.
	 * @param password	The password protecting the access to the server.
	 * @throws AlreadyBoundException if another server is already running on the specified port.
	 * @throws RemoteException if the RMI encounters an error.
	 */
	synchronized public boolean startNewServer(String password) {
		try {
			logger.debug("Creating a new server at localhost:" + settings.getPort());
			setChanged();
			notifyObservers(L10n.getString("Server.Info.CreatingNewServer"));
			// Create a new server.
			server = new RMIServer(settings, password);
			
			setChanged();
			notifyObservers(L10n.getString("Server.Info.StartingNewServer"));
			// Start the server.
			server.start();
			
			logger.info("Server up and running (port " + settings.getPort() + ").");
			setChanged();
			notifyObservers(L10n.getString("Server.Info.ServerUpAndRunning"));
			
			return true;
			
		} catch(AlreadyBoundException e) {
			logger.fatal("There is another server running on the specified port ("+settings.getPort()+").");
			setChanged();
			notifyObservers(e);
		} catch(RemoteException e) {
			logger.fatal("The server creation failed. "+e.getMessage());
			setChanged();
			notifyObservers(e);
		}
		return false;
	}

	/**
	 * Connect to an existing server on the selected <code>host</code>
	 * listening on the selected <code>port</code> using the <code>password</code>.
	 * 
	 * @param host The hostname of the computer where the server is running.
	 * @param port The port where the server is listening.
	 * @param password The password to gain the access to the server.
	 * @throws RemoteException if the RMI encounters an error.
	 * @throws NotBoundException if there is no server running on the specified host:port. 
	 * @throws CertificationException if the password is incorrect.
	 */
	synchronized public boolean connectToRunningServer(String host, int port, String password) {
		try {
			logger.info("Connecting to a running server at " +host+":"+port);
			setChanged();
			notifyObservers(L10n.getString("Server.Info.ConnectingToServer"));
			// Connect to the registry and obtain the Guard.
			Registry registry = LocateRegistry.getRegistry(host, port);
			Guard guard = (Guard) registry.lookup(Guard.ID);
			// Send authorization information to gain access to the server.
			server = guard.certify(password);
			
			return true;
			
		} catch(RemoteException e) {
			logger.fatal("Connecting to the server failed. "+e.getMessage());
			setChanged();
			notifyObservers(e);
		} catch(NotBoundException e) {
			logger.fatal("There is no server running on "+host+":"+port+". " + e.getMessage());
			setChanged();
			notifyObservers(e);
		} catch(CertificationException e) {
			logger.fatal("The certification failed. "+e.getMessage());
			setChanged();
			notifyObservers(e);
		}
		return false;
	}
	

	/**
	 * Get the list of connected clients.
	 * If <code>refresh</code> is true, the server is asked and the list of clients is refreshed.
	 * If it is false, the stored list of connected clients is returned. 
	 * 
	 * @param refresh true means get the list from the server, false means get it from the local cache. 
	 * @return The (possibly not updated) list of connected clients so that you can see whom you can kick, 
	 * 	or null if noone is connected or we are not connected (or running) to some server.   
	 */
	synchronized public ConnectionInfo[] getConnectedUsers(boolean refresh) {
		try {
			if(server == null) 
				return null;
			
			if(refresh) {
				clients = server.getClients();
				setChanged();
				notifyObservers(UPDATE_LIST);
			}
			
			setChanged(); 
			notifyObservers(L10n.getString("Server.Info.ClientsObtained"));
			
		} catch( RemoteException e) { 
			logger.fatal("Unable to obtain the list of connected users. " + e.getMessage());
			setChanged(); 
			notifyObservers(e);
		}
		return clients;
	}
	
	/**
	 * Disconnect the selected client from the server.
	 * 
	 * @param client The client to be kicked out of the server.
	 * @throws RemoteException if the RMI has problems with this drastic task.
	 */
	synchronized public void kick(ConnectionInfo client) {
		try {
			if(server == null || client == null) 
				return;
			
			server.disconnect(client);
			
			logger.info(client + " was kicked out of the server. Muhehee.");
			setChanged(); 
			notifyObservers(L10n.getFormattedString("Server.Info.ClientKicked", client));
			
		} catch(RemoteException e) {
			logger.fatal("Unable to kick the user "+client+". " + e.getMessage());
			setChanged(); 
			notifyObservers(e);
		}
		
		// Reload the list of connected users.
		getConnectedUsers(true);
	}
	
	/**
	 * Terminate the server.
	 * <b>Warning:</b> This will also kick all users as well.
	 * 
	 * @throws RemoteException if the RMI has personal feelings for the server.
	 */
	synchronized public void terminateServer()  {
		try {
			if(server != null)
				server.stop();

			server = null;
			
			logger.info("Server terminated.");
			setChanged(); 
			notifyObservers(L10n.getString("Server.Info.Terminated"));
			
		}catch(RemoteException e) { 
			logger.fatal("Unable to stop the server. " + e.getMessage());
			setChanged(); 
			notifyObservers(e);
		}
	}
	

}
