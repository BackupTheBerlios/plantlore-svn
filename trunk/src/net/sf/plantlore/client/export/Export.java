package net.sf.plantlore.client.export;

import java.rmi.RemoteException;

import net.sf.plantlore.common.Selection;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.server.DBLayerException;


import org.apache.log4j.Logger;

/**
 * The Director class (for export).
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-21
 *	@version 0.7
 */
public class Export implements Runnable {
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
	
	private Builder builder;
	private SelectQuery query;
	private Selection selection;
	private DBLayer database;
	
	
	public Export(Builder builder, SelectQuery query, DBLayer database, Selection selection) {
		this.builder = builder; this.query = query; this.database = database;
		this.selection = selection;
	}
	
	
	public void run() {
		try {
			int result = database.executeQuery( query );
			long count = 0;
			
			logger.info("Export begins...");
			builder.makeHeader();
			
			for(int i = 0; i < database.getNumRows( result ); i++) {
				Object[] objRecord = database.next( result );
				Record record = (Record)objRecord[0];
				if( !selection.contains( record ) ) continue;
				
				count++;
				builder.startNewRecord();
				builder.writeRecord( record );
				builder.finishRecord();
			}
		
			builder.makeFooter();
			logger.info("Export completed. " + count + " records sent to output.");
		}
		catch(DBLayerException e) {
			logger.error(e); e.printStackTrace();
		}
		catch(RemoteException e) {
			logger.error(e); e.printStackTrace();
		}
	}
	
	
	
	


}
