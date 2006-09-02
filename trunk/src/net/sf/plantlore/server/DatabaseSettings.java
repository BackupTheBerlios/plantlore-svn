package net.sf.plantlore.server;

/**
 * The holder object containing the settings that are needed 
 * to connect to the database on the server side.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-08-01
 * @version 1.0
 */
public class DatabaseSettings {
	
	private static final long serialVersionUID = 2006081333004L;
	
	private String connectionStringPrefix;
	private String connectionStringSuffix;
	private int port;
	private String database;
	
	/**
	 * Create new Database settings.
	 * 
	 * @param database	The database engine.
	 * @param port	The port where the database listens.
	 * @param suffix	Additional information that may be required for the connection.
	 */
	public DatabaseSettings(String database, int port, String suffix) {
		this.connectionStringSuffix = suffix;
		this.database = database;
		this.port = port;
		this.connectionStringPrefix = 
			"jdbc:"+database+"://localhost" + 
			((port > 0) ? ":"+(Integer.toString(port))+"/" : "/");
	}
	
	/**
	 * 
	 * @return The additional information that may be required for the connection
	 */
	public String getConnectionStringSuffix() {
		return connectionStringSuffix;
	}
	
	/**
	 * 
	 * @return	The first part of the JDBC connection string created from the supplied data.
	 */
	public String getConnectionStringPrefix() {
		return connectionStringPrefix;
	}
	
	/**
	 * 
	 * @return	The database engine.
	 */
	public String getDatabase() {
		return database;
	}
	
	/**
	 * 
	 * @return	The port where the database listens.
	 */
	public int getPort() {
		return port;
	}
	

}
