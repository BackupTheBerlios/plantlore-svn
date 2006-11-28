package net.sf.plantlore.server.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Observable;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.Utils;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.server.*;
import net.sf.plantlore.server.tools.RMI;

/**
 * The server manager can create the following tasks:
 * <ul>
 * <li>the Create a New Server task,</li>
 * <li>the Connect to an Existing Server task,</li>
 * <li>and the Terminate the Server task.</li>
 * </ol>
 * <br/>
 * After the Server Administrator is logged in, he can control the server:
 * <ul>
 * <li>see the list of connected clients,</li>
 * <li>kick some of the connected clients,</li>
 * <li>terminate the server.</li>
 * </ul>
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-21
 * @version 1.0
 */
public class ServerMng extends Observable {
	
	/**
	 * The message that is sent in case the list of connected clients
	 * shall be refreshed.
	 */
	public static final Object UPDATE_LIST = "UpdateTheListOfConnectedUsers";
	
	/**
	 * The message that is sent in case the connection to the server
	 * (or the server's creation) was successful.
	 */
	public static final Object CONNECTED = "TheConnectionToTheServerIsAvailable";
	
	private static final String SERVER_CONFIG_NAME = "plantlore.server.xml";
	private static final String PLANTLORE = "plantlore";
	
	/** The server control interface. */
	private Server server;
	private ServerSettings settings, defaultSettings;
	
	/** List of connected clients. */
	private ConnectionInfo[] clients;
	
	private boolean didWeCreateTheServer = false;
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
	public static final String settingsFileName;
	
	static {
		String userHome = System.getProperty("user.home"),
		osName = System.getProperty("os.name"),
		plantloreDirName = (osName.equals("Linux") ? "." : "") + PLANTLORE, 
		plantloreConfDir = userHome+File.separator+plantloreDirName;
		
		File plantloreConfDirFile = new File(plantloreConfDir);
		if (!plantloreConfDirFile.exists())
			plantloreConfDirFile.mkdir();
		
		settingsFileName = plantloreConfDir + File.separator + SERVER_CONFIG_NAME;
	}
	
	/**
	 * Create a new Server Manager.
	 */
	public ServerMng() {
		this(null);
	}
	
	public ServerMng(ServerSettings defaultSettings) {
		this.defaultSettings = defaultSettings;
		getSettings(true);
		RMI.addToCodebase( Utils.getCodeBasePath() );
	}
	
	
	public static ServerSettings generateDefaultServerSettings() {
		return new ServerSettings(RMIServer.DEFAULT_PORT, 3, 32, 2, 
				new DatabaseSettings("postgresql", 5432, null));
	}
	
	public static ServerSettings loadSettingsFromFile(String fileName) 
	throws DocumentException, FileNotFoundException, IOException {
		ServerSettings settings = null;
		Reader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
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
		return settings;
	}
	
	/**
	 * Obtain the settings of the server.
	 * 
	 * @param refresh	True if the settings shoud be reloaded from the configuration file.
	 * @return	The settings of the server.
	 */
	public ServerSettings getSettings(boolean refresh) {
		if( !refresh )
			return settings;

		// Create some default settings.
		try {
			settings = defaultSettings != null ? defaultSettings : loadSettingsFromFile(settingsFileName);
		} catch(Exception e) {
			settings = generateDefaultServerSettings();
		}
		return settings;
	}
	
	
	/**
	 * Store the settings of the server into the file.
	 * 
	 * @param settings	The server's settings that shall be stored to the configuration file.
	 */
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
	 * Create a new task, that will
	 * try to create and start a new server on the specified <code>port</code>.
	 * The server will be protected with a <code>password</code> so that 
	 * anyone who will try to connect to the server (to administrate it) 
	 * will have to use that password.
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
				
				didWeCreateTheServer = true;
				
				announce(CONNECTED);
				
				fireStopped(null);
				return server;
			}
		};
	}

	/**
	 * Create a new task, that will
	 * connect to an existing server on the selected <code>host</code>
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
	
	/**
	 * The notification of observers. 
	 * 
	 * @param event	The event that must be announced to the observers.
	 */
	private void announce(Object event) {
		setChanged();
		notifyObservers(event);
	}
	

	/**
	 * Crate a task, that will
	 * update the list of currently connected clients; you should use
	 * <code>getConnectedUsers()</code> to obtain the updated list.
	 * 
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
	 * Obtain the list of connected clients.
	 * 
	 * @return The (possibly outdated) list of connected clients so that you can see whom you can kick, 
	 * 	or null if we are not connected to some server.   
	 */
	public ConnectionInfo[] getConnectedUsers() {
		return clients;
	}
	
	/**
	 * Create a task, that will
	 * disconnect the selected client from the server.
	 * 
	 * @param client The client to be kicked from the server.
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
	 * Create a new task, that will
	 * terminate the server and this application.
	 * <br/>
	 * <b>Warning:</b> This will immediately kick all connected clients.
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
	
	/**
	 * 
	 * @return	True if the server is alive. 
	 */
	public boolean isAlive() {
		try{
			return didWeCreateTheServer && server.ping();
		}
		catch(Exception e) {
			return false;
		}
	}
	

}
