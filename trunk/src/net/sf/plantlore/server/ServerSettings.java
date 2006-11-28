package net.sf.plantlore.server;

/**
 * The holder object containing the settings  
 * needed to start a new server.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-08-01
 * @version 1.0
 */
public class ServerSettings {

	private int port;
	private int timeout;
	private int connectionsTotal;
	private int connectionsPerIP;
	private DatabaseSettings databaseSettings;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return 
			"Port = " + port + 
			" Timeout = " + timeout + 
			" Connections Total = " + connectionsTotal + 
			" Connections Per IP = " + connectionsPerIP +
			" Database settings = " + databaseSettings;
	}
	
	/**
	 * Create new Server Settings.
	 * 
	 * @param port	The port number where the server listens.
	 * @param timeout	The ammount of time in minutes after which the crashed client's database layer
	 * is automatically destroyed. 
	 * @param connectionsTotal	The maximum number of clients connected to the server simultaneously.
	 * @param connectionsPerIP	The maximum number of clients connected to the server 
	 * simultaneously from the same IP address.
	 * @param databaseSettings		The settings describing the connection to the database.
	 */
	public ServerSettings(int port, int timeout, int connectionsTotal, int connectionsPerIP, DatabaseSettings databaseSettings) {
		this.port = port;
		this.timeout = timeout;
		this.connectionsTotal = connectionsTotal;
		this.connectionsPerIP = connectionsPerIP;
		this.databaseSettings = databaseSettings;
	}

	/**
	 * 
	 * @return	The maximum number of clients connected to the server 
	 * simultaneously from the same IP address.
	 */
	public int getConnectionsPerIP() {
		return connectionsPerIP;
	}
	
	/**
	 * 
	 * @return	The maximum number of clients connected to the server simultaneously.
	 */
	public int getConnectionsTotal() {
		return connectionsTotal;
	}
	
	/**
	 * 
	 * @return	The settings describing the connection to the database.
	 */
	public DatabaseSettings getDatabaseSettings() {
		return databaseSettings;
	}
	
	/**
	 * 
	 * @return		The port number where the server listens.
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * 
	 * @return	The ammount of time in minutes after which the crashed client's database layer
	 * is automatically destroyed. 
	 */
	public int getTimeout() {
		return timeout;
	}
	
	
}
