package net.sf.plantlore.server;

public class ServerSettings {

	private int port;
	private int timeout;
	private int connectionsTotal;
	private int connectionsPerIP;
	private DatabaseSettings databaseSettings;
	
	
	public ServerSettings(int port, int timeout, int connectionsTotal, int connectionsPerIP, DatabaseSettings databaseSettings) {
		this.port = port;
		this.timeout = timeout;
		this.connectionsTotal = connectionsTotal;
		this.connectionsPerIP = connectionsPerIP;
		this.databaseSettings = databaseSettings;
	}

	public int getConnectionsPerIP() {
		return connectionsPerIP;
	}
	public int getConnectionsTotal() {
		return connectionsTotal;
	}
	public DatabaseSettings getDatabaseSettings() {
		return databaseSettings;
	}
	public int getPort() {
		return port;
	}
	public int getTimeout() {
		return timeout;
	}
	
	
}
