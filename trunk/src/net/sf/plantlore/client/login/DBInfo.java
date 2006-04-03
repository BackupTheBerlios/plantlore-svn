package net.sf.plantlore.client.login;

/**
 * Store information about one DB.
 * <ul>
 * <li><b>alias</b> ~ the name that will be presented to the user (such as <i>"Home Database"</i>)</li>
 * <li><b>host</b> ~ the host name of the server where the database is located</li>
 * <li><b>port</b> ~ the port number where the server listens</li>
 * <li><b>db</b> ~ the database identificator</li>
 * <li><b>user</b> ~ last five user names that have been used for authentication</li>
 * </ul>
 * 
 * @author Erik Kratochvíl
 * @version 1.0 final
 */
public class DBInfo {
	protected String alias;
	protected String host;
	protected String db;
	protected String[] users;	
	protected int port;
	
	/**
	 * Create a new record.
	 * 
	 * @param alias The name the user will be presented with.
	 * @param host The hostname where the server runs.
	 * @param port The port number where the server listens, 0 means default.
	 * @param db The database identificator.
	 * @param users The list of user names used lately.
	 */
	public DBInfo(String alias, String host, int port, String db, String[] users) {
		this.alias = alias; this.host = host; this.port = (port <= 0) ? 1099 : port; this.db = db; this.users = users;
	}
	

	/**
	 * Pick a user. The selected name will be moved to the start of the list.
	 * The selected user will be at the top of the list next time.
	 * 
	 * @param name	The chosen user.
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
		
}
