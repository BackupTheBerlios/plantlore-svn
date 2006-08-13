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

import net.sf.plantlore.common.Task;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.server.*;
import net.sf.plantlore.server.tools.RMI;

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
	public static final Object CONNECTED = new Object();
	
	private static final String SERVER_CONFIG_NAME = "plantlore.server.xml";
	private static final String PLANTLORE = "plantlore";
	
	private String codebase;
	
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
	
	
	public ServerMng() {
		getSettings(true);
		if(codebase != null && codebase.length() > 0)
			RMI.addToCodebase( codebase );
	}
	
	
	public ServerSettings getSettings(boolean refresh) {
		if( !refresh )
			return settings;

		// Create some default settings.
		settings = new ServerSettings(RMIServer.DEFAULT_PORT, 3, 32, 2, 
				new DatabaseSettings("postgresql", 5432, null));
		
		try {
			Reader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(settingsFileName), "UTF-8"));
			SAXReader saxReader = new SAXReader();
            Document document = saxReader.read( reader );
            reader.close();
            
            Node server = document.selectSingleNode("/config/server");
            Number portNumber = server.numberValueOf("port"),
            connectionsNumber = server.numberValueOf("connections"),
            peripNumber = server.numberValueOf("perip");
            int port = (portNumber == null) ? RMIServer.DEFAULT_PORT : portNumber.intValue(),
            connections = (connectionsNumber == null || connectionsNumber.intValue() <= 1) ? 16 : connectionsNumber.intValue(),
            perip = (peripNumber == null || peripNumber.intValue() <= 1) ? 2 : peripNumber.intValue();
            
            Node database = server.selectSingleNode("database");
            
            String databaseType = database.valueOf("engine"),
            databaseParameter = database.valueOf("parameter");
            Number databasePortNumber = database.numberValueOf("port");
            int databasePort = (databasePortNumber == null) ? 0 : databasePortNumber.intValue();
            
            settings = new ServerSettings(port, 3, connections, perip, 
            		new DatabaseSettings(databaseType, databasePort, databaseParameter));
            
            // Load the codebase
            Node path = document.selectSingleNode("/config/codebase");
            codebase = path.getText();
            
		} catch(Exception e) {
			logger.error("Unable to load settings. " + e.getMessage());
		}
		return settings;
	}
	
	
	
	public void setSettings(ServerSettings settings) {
		this.settings = settings;
		
		Document document = DocumentHelper.createDocument();
		Element config = document.addElement("config"),
		server = config.addElement("server");
		server.addElement("port").setText( "" + settings.getPort() );
		server.addElement("connections").setText( "" + settings.getConnectionsTotal() );
		server.addElement("perip").setText( "" + settings.getConnectionsPerIP() );
		
		Element db = server.addElement("database");
		db.addElement("engine").setText( settings.getDatabaseSettings().getDatabase() );
		db.addElement("port").setText( "" + settings.getDatabaseSettings().getPort() );
		db.addElement("parameter").setText(settings.getDatabaseSettings().getConnectionStringSuffix());
		
		// Store the current codebase
		config.addElement("codebase").setText(codebase);

		try {
			Writer outputWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(settingsFileName), "UTF-8"));
			
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter xmlwriter = new XMLWriter( outputWriter, format );
			xmlwriter.write( document );
			xmlwriter.close();
			outputWriter.close();
		} catch (Exception e) {
			logger.error("Unable to save settings. " + e.getMessage());
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
	 * @param password	The password protecting the access to the server.
	 */
	public Task createNewServerTask(final String password) {
		return new Task() {
			public Object task() throws Exception {
				logger.debug("Creating a new server at localhost:" + settings.getPort());
				setStatusMessage(L10n.getString("Server.Info.CreatingNewServer"));
				// Create a new server.
				server = new RMIServer(settings, password);
				
				setStatusMessage(L10n.getString("Server.Info.StartingNewServer"));
				// Start the server.
				server.start();
				
				logger.info("Server up and running (port " + settings.getPort() + ").");
				setStatusMessage(L10n.getString("Server.Info.ServerUpAndRunning"));
				
				announce(CONNECTED);
				
				fireStopped(null);
				return server;
			}
		};
	}

	/**
	 * Connect to an existing server on the selected <code>host</code>
	 * listening on the selected <code>port</code> using the <code>password</code>.
	 * 
	 * @param host The hostname of the computer where the server is running.
	 * @param port The port where the server is listening.
	 * @param password The password to gain the access to the server.
	 */
	public Task createConnectToRunningServerTask(final String host, final int port, final String password) {
		return new Task() {
			public Object task() throws Exception {
				logger.info("Connecting to a running server at " +host+":"+port);
				setStatusMessage(L10n.getString("Server.Info.ConnectingToServer"));
				// Connect to the registry and obtain the Guard.
				Registry registry = LocateRegistry.getRegistry(host, port);
				Guard guard = (Guard) registry.lookup(Guard.ID);
				// Send authorization information to gain access to the server.
				server = guard.certify(password);
				
				announce(CONNECTED);
				
				fireStopped(null);
				return server;
			}
		};
	}
	
	
	private void announce(Object event) {
		setChanged();
		notifyObservers(event);
	}
	

	/**
	 * Get the list of connected clients.
	 * @return The updated list of connected clients so that you can see whom you can kick, 
	 * 	or null if we are not connected to some server.   
	 */
	public Task createUpdateConnectedUsersTask() {
		return new Task() {
			public Object task() throws Exception {
				if(server == null) {
					fireStopped(null);
					return null;
				}
				
				setStatusMessage(L10n.getString("Server.Info.ObtainingClients"));
				
				clients = server.getClients();
				announce(UPDATE_LIST);
				
				setStatusMessage(L10n.getString("Server.Info.ClientsObtained"));
				
				fireStopped(null);
				return clients;
			}
		};
	}
	
	/**
	 * Get the list of connected clients.
	 * 
	 * @return The (possibly outdated) list of connected clients so that you can see whom you can kick, 
	 * 	or null if we are not connected to some server.   
	 */
	public ConnectionInfo[] getConnectedUsers() {
		return clients;
	}
	
	/**
	 * Disconnect the selected client from the server.
	 * 
	 * @param client The client to be kicked out of the server.
	 */
	public Task createKickTask(final ConnectionInfo client) {
		return new Task() {
			public Object task() throws Exception {
				if(server == null || client == null) {
					fireStopped(null);
					return null;
				}
				
				setStatusMessage(L10n.getString("Server.Info.KickingClient"));
				
				server.disconnect(client);
				
				logger.info(client + " was kicked out of the server.");
				setStatusMessage(L10n.getFormattedString("Server.Info.ClientKicked", client));
			
				// 	Reload the list of connected users.
				createUpdateConnectedUsersTask().start();
				
				fireStopped(null);
				return null;
			}
		};
	}
	
	/**
	 * Terminate the server.
	 * <b>Warning:</b> This will also kick all users as well.
	 */
	public Task createTerminateServerTask()  {
		return new Task() {
			public Object task() throws Exception {
				
				setStatusMessage(L10n.getString("Server.Info.TerminatingServer"));
				
				if(server != null)
					server.stop();
				
				server = null;
				
				logger.info("Server terminated.");
				setStatusMessage(L10n.getString("Server.Info.Terminated"));
				
				fireStopped(null);
				System.exit(0);
				return null;
			}
		};
	}
	
	
}
