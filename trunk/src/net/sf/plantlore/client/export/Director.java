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
 * Purpose: The Director continualy fetches results of the <code>query</code>.
 * The selected queries (queries whose ID is in the <code>selection</code>)
 * are passed to the <code>builder</code> - the builder is responsible for
 * creating a corresponing output.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-21
 *	@version wrong (must be rewritten!) 
 *
 * @see net.sf.plantlore.client.common.Selection
 * @see net.sf.plantlore.client.export.Builder
 * @see net.sf.plantlore.middlerware.DBLayer
 */
public class Director implements Runnable {
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
	
	private Builder build;
	private SelectQuery query;
	private Selection selection;
	private DBLayer database;
	

	/**
	 * Create a new export Director. The Director iterates over the results 
	 * of the <code>query</code> (<code>database.executeQuery(query)</code>)
	 * and selected records (i.e. records in the <code>selection</code>)
	 * passes to the <code>builder</code>.
	 * 
	 * @param builder	The particular builder used to construct the final output.
	 * @param query	The query which will be iterated over.
	 * @param database	The database layer that will carry out the execution of the query.
	 * @param selection	The set of selected records.
	 */
	public Director(Builder builder, SelectQuery query, DBLayer database, Selection selection) {
		this.build = builder; this.query = query; this.database = database;
		this.selection = selection;
	}
	
	
	/** Execute the exporting procedure as described. */
	public void run() {
		try {
			// The result identifier.
			int result = database.executeQuery( query );
			long count = 0; // how many records were exported
			
			logger.info("Export begins...");
			
			// Create the header of the file (some opening tags possibly).
			build.header();
			// Iterate over the result of the query.
			for(int i = 0; i < database.getNumRows( result ); i++) {
				Record[] records = (Record[]) database.next( result );
				if( !selection.contains( records[0] ) ) continue; // is it selected?
				
				count++;
				// Write down this record.
				build.startRecord();
				
				// Parse the record.
				//build.writeRecord( records );
				
				
				build.finishRecord();
			}
			// Create the footer of the file (some closing tags possibly).
			build.footer();
			logger.info("Export completed. " + count + " records sent to output.");
		}
		// FIXME: Since the run() method comes from the Runnable interface, it cannot throw
		// any exceptions :( This is yet to be solved!
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
