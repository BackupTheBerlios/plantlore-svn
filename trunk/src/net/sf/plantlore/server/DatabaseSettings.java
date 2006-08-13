package net.sf.plantlore.server;

public class DatabaseSettings {
	
	private static final long serialVersionUID = 2006081333004L;
	
	private String connectionStringPrefix;
	private String connectionStringSuffix;
	private int port;
	private String database;
	
	
	public DatabaseSettings(String database, int port, String suffix) {
		this.connectionStringSuffix = suffix;
		this.database = database;
		this.port = port;
		this.connectionStringPrefix = 
			"jdbc:"+database+"://localhost" + 
			((port > 0) ? ":"+(Integer.toString(port))+"/" : "/");
	}
	
	
	public String getConnectionStringSuffix() {
		return connectionStringSuffix;
	}
	public String getConnectionStringPrefix() {
		return connectionStringPrefix;
	}
	public String getDatabase() {
		return database;
	}
	public int getPort() {
		return port;
	}
	

}
