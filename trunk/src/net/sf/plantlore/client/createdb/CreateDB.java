package net.sf.plantlore.client.createdb;

import java.util.ArrayList;
import java.util.Observable;

import org.apache.log4j.Logger;

import net.sf.plantlore.client.MainConfig;
import net.sf.plantlore.client.login.DBInfo;
import net.sf.plantlore.client.login.Login;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.l10n.L10n;

/**
 * CreateDB is a CreationTask factory. 
 * Its sole purpose is to gather information required for the
 * creation of that task and create a new record in the list
 * of databases the User accessed (see Login).
 * <br/>
 * The CreationTask is a long running operation that 
 * <ol>
 * <li>
 * Connects to the database engine and creates a new database.
 * The creation of the database includes the creation of all tables,
 * the default administrator (if it is not already created) and filling those
 * tables with values that are necessary for proper work with Plantlore. 
 * </li>
 * <li>
 * Optionally fills all (so called) immutable tables with records 
 * that apply to the Czech Republic. 
 * Those are tables are: villages, plants, territories, and phytochoria.
 * </li>
 * </ol>
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-08-29
 *
 */
public class CreateDB extends Observable {
	
	private Logger logger  = Logger.getLogger(this.getClass().getPackage().getName());
	
	private MainConfig config;
	private DBInfo info;
	private boolean leaveEmpty = false;
		
	/**
	 * Create a new CreateDB.
	 * 
	 * @param config	The configuration that manages the list of databases the User accessed.
	 */
	public CreateDB(MainConfig config) {
		this.config = config;
	}
	
	/**
	 * Set information about the datbase that shall come to existence.
	 *  
	 * @param alias	The name that will be presented to the User.
	 * @param engine	The type of the database (the database engine).
	 * @param port	The port where the database engine listens.
	 * @param identifier	The name of the database that will be created.
	 * @param leaveEmpty		True if the immutable tables shall remain empty. 
	 */
	public void setDBInfo(String alias, String engine, int port, String identifier, boolean leaveEmpty) {
		info = new DBInfo(alias, null, 0, engine, port, identifier, "", new String[Login.MAX_NAMES]);
		this.leaveEmpty = leaveEmpty; 
	}
	
	/**
	 * Add the database info to the list of databases permanently.
	 * 
	 * @param info	The info to be added.
	 */
	protected void addDBInfoPermanently(DBInfo info) {
		ArrayList<DBInfo> dbinfos = config.getDBinfos();
		dbinfos.add( info );
		config.setDBInfos( dbinfos, info );
		try {
			config.save();
		} catch(java.io.IOException e) { /* Never mind. */ }
		
		setChanged();
		notifyObservers( info );
	}
	
	/**
	 * Create a new Creation Task that is capable of creating the new database.
	 *  
	 * @param username	The name of the account that allows us to connect to the database
	 * engine and perform all actions necessary (create DB, create roles and users, ...) - and Administrator
	 * typically.
	 * @param password	The password protecting that account.
	 * 
	 * @return	The Creation task.
	 */
	public synchronized Task createCreationTask(String username, String password) {
		// Set the first user name to the Administrator of the database.
		info.getUsers()[0] = username;
		return new CreationTask(info, username, password, leaveEmpty);
	}
	
	
	
	/**
	 * The Creation of the database.
	 * 
	 * @author Erik Kratochvíl (The skeleton of the task.)
	 * @author Tomáš Kovařík (at least I hope so)
	 * @since 2006-08-29
	 *
	 */
	private class CreationTask extends Task {
		
		private DBInfo dbinfo;
		private transient String name, password;
		private boolean leaveEmpty;
		
		/**
		 * Create a new Creation Task.
		 * 
		 * @param dbinfo	The holder object containing information about the new database.
		 * @param name		The account name that shall be used to connect to the database.
		 * @param password	The password protecting that account.
		 * @param leaveEmpty		Leave the immutable tables empty (do not fill them).
		 */
		public CreationTask(DBInfo dbinfo, String name, String password, boolean leaveEmpty) {
			this.dbinfo = dbinfo;
			this.name = name;
			this.password = password;
			this.leaveEmpty = leaveEmpty;
		}
		
		@Override
		public Object task() throws Exception {
			
			try {
				if(isCanceled())
					throw new Exception(L10n.getString("Common.Canceled"));
				
				/*
				 * TODO:
				 * 
				 * HERE GOES YOUR CODE THAT PERFORMS 
				 * 1. THE CONNECTION TO THE DATABASE ENGINE
				 *    You should use ifnormation stored in dbinfo and the stored name and password.
				 *    See HibernateDBLayer.initialize().
				 * 2. THE CREATION OF THE NEW DATABASE
				 *    Here it is up to you, I have no idea what should be done here.
				 *    Creation of all tables and roles + user admin with some default password +
				 *    neccessary columns (history).
				 * 3. PRE-FILLING THE DATABASE WITH SOME VALUES
				 *    Polluting the database with all villages, plants, territories, and phytochoria.
				 *    This may be optional - only if leaveEmpty is true.
				 * 
				 * Everything you want do, do it here in the CreationTask!
				 *  
				 */

			} 
			catch (Exception e) {
				logger.error("The creation of the database failed! " + e.getMessage());
				
				// Re-throw the exception so that the view is updated as well.
				throw e;
			}
			
			// Everything went fine.
			addDBInfoPermanently( dbinfo );
			logger.info("The creation of a new database was successful.");
			
			fireStopped(null);
			return null;
		}
	}
	
	

}
