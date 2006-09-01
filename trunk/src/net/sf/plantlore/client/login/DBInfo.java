package net.sf.plantlore.client.login;

/**
 * Store information about one database connection.
 * 
 * <ul>
 * <li><b>alias</b> The name that will be presented to the User (such as <i>"Home Database"</i>)</li>
 * <li><b>host</b> The host name or ip address where the Server is located.</li>
 * <li><b>port</b> The port where the Server listens.</li>
 * <li><b>databaseType</b> The type of the database engine (postgresql, oraclesql, etc.)</li>
 * <li><b>databasePort</b> The port where the database engine listens.</li>
 * <li><b>databaseIdentifier</b> The database identifier (usually a name of the database).</li>
 * <li><b>databaseParameter</b> Some additional parameter for the JDBC connection string.</li>
 * <li><b>user</b> The list of last user names that have been used during Authentication.</li>
 * </ul>
 * 
 * @author Erik Kratochvíl
 * @version 4.0
 */
public class DBInfo {
	protected String alias;
	protected String host;
	protected int port;
	protected String databaseType;
	protected int databasePort;
	protected String databaseIdentifier;
	protected String databaseParameter;
	protected String[] users;
	
	/**
	 * Move the selected name to the top of the list.
	 * 
	 * @param name		The chosen user.
	 */
	public void promoteUser(String name) {
		int id = users.length - 1;
		for(int i = 0; i < users.length && users[i] != null; i++) 
			if(users[i].equals(name)) id = i; 
		for(int i = id; i > 0; i--) users[i] = users[i - 1];
		users[0] = name;
	}
	
	/**
	 * How the DBInfo should appear to the User in the list of databases.
	 */
	@Override
	public String toString() {
		return alias;
	}
	
	/**
	 * Create a shallow copy of this DBInfo.
	 */
	public DBInfo clone() {
		return new DBInfo(
				alias, host, port, databaseType, databasePort, databaseIdentifier, databaseParameter, users );
	}

	/**
	 * Create a new DBInfo.
	 * 
	 * @param alias	The name that will be presented to the User (such as <i>"Home Database"</i>)
	 * @param host	The host name or ip address where the Server is located.
	 * @param port	The port where the Server listens.
	 * @param databaseType	The type of the database engine (postgresql, oraclesql, etc.)
	 * @param databasePort	The port where the database engine listens.
	 * @param databaseIdentifier	The database identifier (usually a name of the database).
	 * @param databaseParameter	Some additional parameter for the JDBC connection string.
	 * @param users	 The list of last user names that have been used during Authentication.
	 */
	public DBInfo(String alias, String host, int port, String databaseType, int databasePort, String databaseIdentifier, String databaseParameter, String[] users) {
		this.alias = alias;
		this.host = host;
		this.port = port;
		this.databaseType = databaseType;
		this.databasePort = databasePort;
		this.databaseIdentifier = databaseIdentifier;
		this.databaseParameter = databaseParameter;
		this.users = users;
	}

	/**
	 * 
	 * @return	The name that will be presented to the User (such as <i>"Home Database"</i>).
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * 
	 * @return	The database identifier (usually a name of the database).
	 */
	public String getDatabaseIdentifier() {
		return databaseIdentifier;
	}

	/**
	 * 
	 * @return	Some additional parameter for the JDBC connection string.
	 */
	public String getDatabaseParameter() {
		return databaseParameter;
	}

	/**
	 * 
	 * @return	The port where the database engine listens.
	 */
	public int getDatabasePort() {
		return databasePort;
	}

	/**
	 * 
	 * @return	The type of the database engine (postgresql, oraclesql, etc.)
	 */
	public String getDatabaseType() {
		return databaseType;
	}

	/**
	 * 
	 * @return	The host name or ip address where the Server is located.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * 
	 * @return	The port where the Server listens.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * 
	 * @return	The list of last user names that have been used during Authentication.
	 */
	public String[] getUsers() {
		return users;
	}


	
		
}
