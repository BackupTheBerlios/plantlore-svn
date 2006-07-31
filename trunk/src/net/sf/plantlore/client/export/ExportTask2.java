package net.sf.plantlore.client.export;

import java.io.IOException;
import java.io.Writer;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.log4j.Logger;

import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.Selection;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.common.exception.ExportException;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;

public class ExportTask2 extends Task {
	
private Logger logger = Logger.getLogger(getClass().getPackage().getName());
	
	private Builder build;
	private Selection selection;
	private DBLayer database;
	private Integer resultId;
	private boolean ignoreDead = true;
	private boolean useProjections = false;
	private List<Pair<Class, String>> description;
	private SelectQuery query;
	private Writer writer;
	private Record torso;
	
	
	
	public ExportTask2(
			DBLayer dblayer, 
			SelectQuery query, 
			Writer writer, 
			Builder builder, 
			Selection selection) {
		
		this.database = dblayer;
		this.query = query;
		this.writer = writer;
		this.selection = selection;
		this.build = builder;
	}
	
	// Pro pouziti s projekcema
	public ExportTask2(
			DBLayer dblayer, 
			SelectQuery query, 
			Writer writer, 
			Builder builder, 
			Selection selection,
			List<Pair<Class, String>> description, 
			Class rootTable) 
	throws ExportException {
		
		this(dblayer, query, writer, builder, selection);
		this.useProjections = true;
		this.description = description;
		try { 
			torso = (Record)rootTable.newInstance();
			torso.createTorso();
		} catch (Exception e) {
			throw new ExportException(L10n.getString("Error.InternalError"));
		}
	}
	
	public ExportTask2 ignoreDead(boolean ignore) {
		this.ignoreDead = ignore;
		return this;
	}

	@Override
	public Object task() throws Exception {
		try {
			logger.info("Export begins...");
			setStatusMessage(L10n.getString("Export.Initializing"));
			
			resultId = database.executeQuery( query );
			
			build.header();
			
			// Iterate over the result of the query.
			int rows = database.getNumRows( resultId ),
			expectedNumberOfRecords = selection.size( rows ),
			count = 0;
			
			if(expectedNumberOfRecords > 0)
				setLength(expectedNumberOfRecords);
			
			for(int i = 0; i < rows && !isCanceled() && count < expectedNumberOfRecords; i++) {
				
				logger.debug("Fetching a new record from the database.");
				
				Record record;
				if(useProjections) 
					record = reconstruct( (Object[])database.more( resultId, i, i )[0] );
				else
					record = (Record)((Object[])database.more( resultId, i, i )[0])[0];
				
				
				logger.debug("New record No. "+i+" fetched: "+record);
				if( !selection.contains( record ) || (ignoreDead && record.isDead()) ) 
					continue; // Is the record selected?
				
				logger.debug("The record is in the selection. It will be exported.");
				count++;
				setStatusMessage(count + " " + L10n.getString("Export.RecordsExported"));
				setLength(count);
				
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
				
			}

			build.footer();
			logger.info("Export completed. " + count + " records sent to output. ");
			cleanup();
		}
		catch(Exception e) {
			logger.error("Export ended prematurely: "+e.getMessage());
			cleanup();
			throw e;
		}
		
		return null;
	}
	
	
	
	private void cleanup() {
		try {
			writer.close();
		} catch(Exception e) { /* Never mind. */ }
		try {
			database.closeQuery( query );
		} catch(Exception e) {/* Never mind. */}
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
