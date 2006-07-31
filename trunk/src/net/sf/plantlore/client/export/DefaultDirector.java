package net.sf.plantlore.client.export;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Observable;

import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.Selection;
import net.sf.plantlore.common.exception.ExportException;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.exception.DBLayerException;

import org.apache.log4j.Logger;

/**
 * The Director class (for export).
 * Purpose: The Director continualy fetches results of the resultset
 * identified by the <code>result</code>.
 * The selected results (records containted in the <code>selection</code>)
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
 * <br/>
 * The DefaultDirector can run in two "modes" - either using projections
 * or regular records. 
 * If projections are used, the records are reconstructed so that the
 * same Builders can be used in both cases. 
 *
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-21
 * @version 2.0
 *
 * @see net.sf.plantlore.client.common.Selection
 * @see net.sf.plantlore.client.export.Builder
 * @see net.sf.plantlore.middleware.DBLayer
 */
@Deprecated
public class DefaultDirector extends Observable implements Runnable {
	
	private Logger logger = Logger.getLogger(getClass().getPackage().getName());
	
	private Builder build;
	private Selection selection;
	private DBLayer database;
	private int result;
	private int totalNumberOfRecords = Integer.MAX_VALUE;
	
	private boolean ignoreDead = true;
	
	private boolean aborted = false;
	private boolean useProjections = false;
	
	private List<Pair<Class, String>> description;
	
	private int count = 0;
	
	//private Class rootTable;


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
	 * @param useProjections	Use projections instead of standard records.
	 * @param description	The list of [Table, Column] - values of these columns will be returned 
	 * by the database layer if projections are used
	 */
	public DefaultDirector(Builder builder, int result, DBLayer database, Selection selection, 
			boolean useProjections, List<Pair<Class, String>> description, Class rootTable) 
	throws ExportException {
		this(builder, result, database, selection);
		this.useProjections = useProjections;
		this.description = description;
		//this.rootTable = rootTable;
		if(rootTable != null) try { 
			torso = (Record)rootTable.newInstance();
			torso.createTorso();
		} catch (Exception e) {
			// What shall we do? The torso is not complete!
		}
	}
	
	
	/**
	 * Set a new Builder
	 * @param builder	The builder to be used.
	 * @throws ExportException	If the builder is not valid.
	 */
	protected void setBuilder(Builder builder) 
	throws ExportException {
		if(builder == null) {
			logger.warn("The builder is null!");
			throw new ExportException(L10n.getString("Error.InvalidBuilder"));
		}
		build = builder;
	}

	/**
	 * Set a new resultset identificator.
	 * 
	 * @param result	The identifier of the resultset.
	 * @throws ExportException	If the identifier is not valid.
	 */
	protected void setResult(int result) 
	throws ExportException {
		if(result < 0) {
			logger.warn("The result set is probably not valid!");
			throw new ExportException(L10n.getString("Error.InvalidResultset"));
		}
		this.result =  result;
	}
	
	/**
	 * Set a new database layer.
	 * @param db	The database layer to be set.
	 * @throws ExportException	If the database layer is not valid.
	 */
	protected void setDatabase(DBLayer db) 
	throws ExportException {
		if(db == null) {
			logger.error("The database layer is null!");
			throw new ExportException(L10n.getString("Error.InvalidDBLayer"));
		}
		this.database = db;
	}
	
	/**
	 * Set a new selection.	
	 * @param selection	The selection to be cloned.
	 * @throws ExportException	If the selection is not valid.
	 */
	protected void setSelection(Selection selection) 
	throws ExportException {
		if(selection == null || selection.isEmpty()) {
			logger.warn("The selection is null or empty!");
			throw new ExportException(L10n.getString("Error.InvalidSelection"));
		}
		this.selection = selection.clone();
	}
	
	/**
	 * Set whether records marked as dead should be omited. Default is true.
	 * 
	 * @param ignore	True if dead records should be omited.
	 */
	public void ignoreDead(boolean ignore) {
		this.ignoreDead = ignore;
	}
	
	
	public void setExpectedNumberOfRecords(int total) {
		this.totalNumberOfRecords = total;
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
	 * Find all AuthorOccurrences associated with the <code>Occurrence</code>
	 * and send them to the Builder, too.
	 * 
	 * @param occurrence	The currently processed occurrence data.
	 */
	private void loadAssociatedAuthors(Occurrence occurrence) 
	throws RemoteException, IOException, DBLayerException {
		
		logger.debug("Processing the associated information about Authors.");
		
		SelectQuery query = database.createQuery(AuthorOccurrence.class);
		query.createAlias(AuthorOccurrence.OCCURRENCE, "OCC");

		// AuthorOccurrence.OCCURRENCE = Occurrence.ID  &&  Occurrence.ID = occ.getId()
		query.addRestriction(PlantloreConstants.RESTR_EQ_PROPERTY, AuthorOccurrence.OCCURRENCE, "OCC."+Occurrence.ID, null, null);
		query.addRestriction(PlantloreConstants.RESTR_EQ, "OCC."+Occurrence.ID, null, occurrence.getId(), null);
		int resultId = database.executeQuery( query );
		
		// Take all results and spit'em out.
		int rows = database.getNumRows( resultId );
		for(int i = 0; i < rows; i++) {
			logger.debug("Fetching associated data (Author, AuthorOccurrence).");
			
			Object[] pulp = database.more( resultId, i, i );
			AuthorOccurrence ao = (AuthorOccurrence) ((Object[])pulp[0])[0];
			ao.setOccurrence( null ); // cut off the way back to the occurrence
			if(ao.isDead() && ignoreDead) continue;
			
			logger.debug("New author-occurence record: " + ao);
			
			build.part( ao );
		}
		database.closeQuery( query );
		
		logger.debug("Author-occurence processed.");
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
			count = 0;
			for(int i = 0; i < rows && !aborted && count < totalNumberOfRecords; i++) {
				
				logger.debug("Fetching a new record from the database.");
				
				Record record;
				if(useProjections) 
					record = reconstruct( (Object[])database.more( result, i, i )[0] );
				else
					record = (Record)((Object[])database.more( result, i, i )[0])[0];
				
				
				logger.debug("New record No. "+i+" fetched: "+record);
				if( !selection.contains( record ) || (ignoreDead && record.isDead()) ) 
					continue; // Is the record selected?
				
				logger.debug("The record is in the selection. It will be exported.");
			
				count++;
				build.startRecord();
				
				// Build this part of the record.
				build.part( record );
				
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
			logger.info("Export completed. " + count + " records sent to output. ");
		}
		catch(Exception e) {
			logger.error("Export ended prematurely. Only "+count+" records exported.");
			logger.error("The problem: "+e);
			e.printStackTrace();
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
	
	/**
	 * The base of the record to be reconstructed.
	 */
	private Record torso;
	
	/**
	 * Reconstruct the record from the given values. 
	 * 
	 * @param values	Values of columns (in the same order as in the Description).
	 * @return	The reconstructed record.
	 */
	private Record reconstruct(Object[] values) {
		for(int i = 0; i < description.size(); i++ ) {
			Pair<Class, String> d = description.get(i);
			torso.setValue(d.getFirst(), d.getSecond(), values[i]);
		}
		return torso;
	}

}
