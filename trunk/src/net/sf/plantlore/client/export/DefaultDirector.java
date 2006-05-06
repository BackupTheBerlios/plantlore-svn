package net.sf.plantlore.client.export;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Observable;

import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.Selection;
import net.sf.plantlore.common.exception.ExportException;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
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
 * @version 1.0 RC 1
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
	public DefaultDirector(Builder builder, int result, DBLayer database, Selection selection) 
	throws ExportException {
		setBuilder(builder); 
		setResult(result); 
		setDatabase(database); 
		setSelection(selection); 
	}
	
	
	protected void setBuilder(Builder builder) 
	throws ExportException {
		if(builder == null) {
			logger.warn("The builder is null!");
			throw new ExportException("The builder cannot be null!");
		}
		build = builder;
	}
	
	protected void setResult(int result) 
	throws ExportException {
		if(result < 0) {
			logger.warn("The result set is probably not valid!");
			throw new ExportException("The result set identificator cannot be negative!");
		}
		this.result =  result;
	}
	
	protected void setDatabase(DBLayer db) 
	throws ExportException {
		if(db == null) {
			logger.error("The database layer is null!");
			throw new ExportException("The database layer cannot be null!");
		}
		this.database = db;
	}
	
	protected void setSelection(Selection selection) 
	throws ExportException {
		if(selection == null || selection.isEmpty()) {
			logger.warn("The selection is null or empty!");
			throw new ExportException("The selection cannot be empty!");
		}
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
	
	
	/**
	 * The whole record is returned in a tree structure.
	 * We must traverse this structure.
	 * The children are denoted as <i>foreign keys</i> 
	 * - they contain other parts of the whole record.
	 * 
	 * @param record The "node" in the tree hierarchy representing the
	 * whole record.
	 */
	private void buildParts(Record record) throws IOException {
		if(record == null) return;
		// Build this part of the record.
		build.part(record);
		// Now look at all children of this record.
		for(String key : record.getForeignKeys()) {
			// And build'em too.
			buildParts( (Record) record.getValue(key) );
		}
	}
	
	
	
	private void loadAssociatedAuthors(Occurrence occurrence) 
	throws RemoteException, IOException, DBLayerException {
		
		SelectQuery query = database.createQuery(AuthorOccurrence.class);
		query.createAlias(AuthorOccurrence.OCCURRENCE, "OCC");

		// AuthorOccurrence.OCCURRENCE = Occurrence.ID  &&  Occurrence.ID = occ.getId()
		query.addRestriction(PlantloreConstants.RESTR_EQ_PROPERTY, AuthorOccurrence.OCCURRENCE, "OCC."+Occurrence.ID, null, null);
		query.addRestriction(PlantloreConstants.RESTR_EQ, "OCC."+Occurrence.ID, null, occurrence.getId(), null);
		int resultId = database.executeQuery( query );
		
		// Take all results and spit'em out.
		int rows = database.getNumRows( resultId );
		for(int i = 0; i < rows; i++) {
			Object[] pulp = database.more( resultId, i, i );
			AuthorOccurrence ao = (AuthorOccurrence) ((Object[])pulp[0])[0];
			ao.setOccurrence( null ); // cut off the way back to the occurrence
			buildParts( ao );
		}
		database.closeQuery( query );
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
				buildParts( record );
				
				/* -----------------------------------------------------------
				 * Deal with the one-to-many relationship
				 * of Occurence -> AuthorOccurence ~ Author
				 * ----------------------------------------------------------- */
				if( record instanceof Occurrence ) 
					loadAssociatedAuthors( (Occurrence)record );
				
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
