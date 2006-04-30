package net.sf.plantlore.client.export;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Observable;

import net.sf.plantlore.common.Selection;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.server.DBLayerException;

import org.apache.log4j.Logger;

/**
 * The Director class (for export).
 * Purpose: The Director continualy fetches results of the resultset
 * identified by the <code>result</code>.
 * The selected results (records whose ID is in the <code>selection</code>)
 * are passed to the <code>builder</code> - the builder is responsible for
 * creating a corresponing output.
 * <br/>
 * The Director is supposed to run in a separate thread which is why
 * all exceptions are handled in the <code>run()</code> method
 * in a following manner:
 * <pre>
 * catch(AnException e) { setChanged(); notifyObservers( e ); }
 * </pre> 
 *
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-21
 * @version 1.0 BETA - might still slightly change
 *
 * @see net.sf.plantlore.client.common.Selection
 * @see net.sf.plantlore.client.export.Builder
 * @see net.sf.plantlore.middleware.DBLayer
 */
public class DefaultDirector extends Observable implements Runnable {
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
	
	private Builder build;
	private Selection selection;
	private DBLayer database;
	private int result;
	
	private boolean aborted = false;
	
	private int count = 0;

	/**
	 * Create a new export Director. The Director iterates over the results 
	 * of the <code>query</code> (<code>database.executeQuery(query)</code>)
	 * and selected records (i.e. records in the <code>selection</code>)
	 * passes to the <code>builder</code>.
	 * 
	 * @param builder	The particular builder used to construct the final output.
	 * @param result	The result describing the resultset that will be iterated over.
	 * @param database	The database layer that will quench the Director's thirst for more results.
	 * @param selection	The set of selected records.
	 */
	public DefaultDirector(Builder builder, int result, DBLayer database, Selection selection) {
		this.build = builder; this.result = result; this.database = database;
		this.selection = selection.clone(); 
	}
	
	
	/** 
	 * How many records have been exported.
	 * 
	 * @return The exact number of exported records.
	 */
	public int exportedRecords() { 
		return count;
	}
	
	
	private static Object[] NO_PARAM = new Object[0];
	
	/**
	 * The whole record is returned in a tree structure.
	 * We must traverse this structure.
	 * The children are denoted as <i>foreign keys</i> 
	 * - they contain other parts of the whole record.
	 * 
	 * @param record The "node" in the tree hierarchy representing the
	 * whole record.
	 */
	private void buildPart(Record record) throws IOException {
		// Build this part of the record.
		build.part(record);
		// Now look at all children of this record.
		for(String key : record.getForeignKeys()) {
			Method getter = Template.getMethod(record.getClass(), key);
			try {
				// And build'em too.
				buildPart( (Record) getter.invoke( record, NO_PARAM ) );
			}
			catch(IllegalAccessException e) { e.printStackTrace(); }
			catch(InvocationTargetException e) { e.printStackTrace(); }
		}
	}
	
	
	/** 
	 * Execute the exporting procedure -
	 * fetch all results from the resultset and those that are selected
	 * send to the builder to process them.
	 */
	public void run() {
		try {
			logger.info("Export begins...");
			build.header();
			
			// Iterate over the result of the query.
			int rows = database.getNumRows( result );
			for(int i = 0; i < rows && !aborted; i++) {
				
				// Abandon the database.nect() Object[] records = database.next( result );
				Object[] records = database.more( result, i, i );
				
				Record record = (Record) ((Object[])records[0])[0]; // [0][0] since we use `more`
				if( !selection.contains( record ) ) continue; // Is the record selected?
			
				count++;
				build.startRecord();
				
				// Parse the record.
				buildPart( record );
				
				// ONE-TO-MANY HACK:
				// Occurrence -> AuthorOccurrences & Authors
				// FIXME: Jak se to ma Director dozvedet, ze ma delat tuhle iteraci :\
				
				
				
				
				build.finishRecord();
				
				setChanged(); notifyObservers( count );
			}

			build.footer();
			logger.info("Export completed. " + count + " records sent to output.");
		}
		catch(DBLayerException e) {
			logger.error("Export ended prematurely " + e);
			setChanged(); notifyObservers( e ); 
		}
		catch(RemoteException e) {
			logger.error("Export ended prematurely " + e);
			setChanged(); notifyObservers( e );
		}
		catch(IOException e) {
			logger.error("Export ended prematurely " + e); 
			setChanged(); notifyObservers( e );
		}
		if(aborted) logger.info("Export aborted. " + count + " records sent to output.");
	}
	
	/**
	 * Abort the export immediately.
	 */
	public void abort() {
		aborted = true;
	}

}
