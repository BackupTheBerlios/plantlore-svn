package net.sf.plantlore.client.createdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.apache.log4j.Logger;

import net.sf.plantlore.client.MainConfig;
import net.sf.plantlore.client.login.DBInfo;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.l10n.L10n;

/**
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 29.8.2006
 *
 */
public class CreateDB extends Observable {
	
	private Logger logger  = Logger.getLogger(this.getClass().getPackage().getName());
	
	private MainConfig config;
	private DBInfo info;
		
	
	public CreateDB(MainConfig config) {
		this.config = config;
	}
	
	
	public List<DBInfo> getDBInfos() {
		return config.getDBinfos();
	}
	
	public void setDBInfo(DBInfo info) {
		this.info = info;
	}
	
	protected void addDBInfoPermanently(DBInfo info) {
		ArrayList<DBInfo> dbinfos = config.getDBinfos();
		dbinfos.add( info );
		config.setDBInfos( dbinfos, info );
	}
	
	
	public synchronized Task createCreationTask(String username, String password) {
		return new CreationTask(info, username, password);
	}
	
	
	
	/**
	 * Creation of the database.
	 * 
	 * @author Erik Kratochvíl (discontinuum@gmail.com)
	 * @since 29.8.2006
	 *
	 */
	private class CreationTask extends Task {
		
		private DBInfo dbinfo;
		private transient String name, password;
		
		public CreationTask(DBInfo dbinfo, String name, String password) {
			this.dbinfo = dbinfo;
			this.name = name;
			this.password = password;
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
				 * 2. THE CREATION OF THE NEW DATABASE
				 *    Here it is up to you, I have no idea what should be done here.
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
			
			fireStopped(null);
			return null;
		}
	}
	
	

}
