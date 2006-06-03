package net.sf.plantlore.server;

public class DatabaseSettings {
	
	private String connectionStringPrefix;
	private String connectionStringSuffix;
	private String masterUser;
	private String masterPassword;
	private int port;
	private String database;
	
	
	public DatabaseSettings(String database, int port, String suffix, String masterUser, String masterPassword) {
		this.masterUser = masterUser;
		this.masterPassword = masterPassword;
		this.connectionStringSuffix = suffix;
		this.database = database;
		this.port = port;
		this.connectionStringPrefix = 
			"jdbc:"+database+"://localhost" + 
			((port > 0) ? ":"+(new Integer(port).toString())+"/" : "/");
	}
	
	
	public String getMasterUser() {
		return masterUser;
	}
	public String getMasterPassword() {
		return masterPassword;
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
