package net.sf.plantlore.client.export;

import java.io.IOException;
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
				Record[] records = (Record[]) database.next( result );
				if( !selection.contains( records[0] ) ) continue;
				
				count++;
				builder.startNewRecord();
				/* Why do we give the whole array of records?
				 * It is more general - the builder can 
				 * # either start with the records[0] and use the introspection,
				 * # or use the whole records array and the template
				 * to re-create the record.
				 */
				builder.writeRecord( records );
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
		catch(IOException e) {
			logger.error(e); e.printStackTrace();
		}
	}
	
	
	
	


}
