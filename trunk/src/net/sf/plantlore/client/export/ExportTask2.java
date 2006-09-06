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
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;

/**
 * The Export task serves as a Director for the selected Builder.
 * It takes the supplied query, executes it, and hands over every
 * record, that is selected to be exported, to the Builder.
 * <br/>
 * The database layer is capable of retrieving whole records (objects)
 * from the database. 
 * Sometimes it may cost a lot of time to reconstruct the whole record and if we want
 * to export just a few columns, we would not use the rest of the record anyway.
 * This may lead to a great overhead. In order to reduce this overhead, projections can be used.
 * Projections is a list of columns the User wants to export; the list is typically very small.
 * The database layer is instructed to return only the values of those columns.
 * <br/>
 * Let's see an example:
 * <br/>
 * The User exports Occurrences and is interrested in three columns only:
 * Occurrence.UnitID, Occurrence.Plant.Taxon, Occurrence.Habitat.NearestVillage.Name.
 * It would not be wise to retrieve the whole record that comprises dozens of columns
 * some of which may contain a lot of data. We set those three projections and
 * instruct the Task to use them. The database layer will then return just those three values
 * for every record, then it will reconstruct the record from those values, 
 * and finally it will pass it to the Builder. This can speed up the Export enormously, 
 * especially when a remote connection is used.
 * <br/>
 * However, the usual use of Export is to export the whole record, 
 * therefore it is much more convenient to have the database layer 
 * reconstructed the object for us. 
 * 
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-08-01
 * @version 2.0
 */
public class ExportTask2 extends Task {
	
private Logger logger = Logger.getLogger(getClass().getPackage().getName());
	
	private Builder build;
	private Selection selection;
	private DBLayer database;
	private Integer resultId;
	private boolean ignoreDead = true;
	private boolean useProjections = false;
	private Projection projections;
	private List<Pair<Class, String>> description;
	private SelectQuery query;
	private Writer writer;
	private Record torso;
	
	
	/**
	 * Create a new Export task. 
	 * 
	 * @param dblayer	The database layer that mediates the connection with the database. 
	 * @param query	The query defining the result set which is to be iterated over.
	 * @param writer	The writer that will be used for the output.
	 * @param builder	The builder that will construct the output from records the Director supplies.
	 * @param selection	The list of selected records - only the selected records will be exported.
	 */
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
	
	/**
	 * Create a new Export task. This Export task is set to use projections.
	 * 
	 * @param dblayer	The database layer that mediates the connection with the database. 
	 * @param query	The query defining the result set which is to be iterated over.
	 * @param writer	The writer that will be used for the output.
	 * @param builder	The builder that will construct the output from records the Director supplies.
	 * @param selection	The list of selected records - only the selected records will be exported.
	 * @param projections	The list of columns that should be exported.
	 * @param rootTable	The root table (only if projections are used).
	 */
	public ExportTask2(
			DBLayer dblayer, 
			SelectQuery query, 
			Writer writer, 
			Builder builder, 
			Selection selection,
			Projection projections,
			Class rootTable) 
	throws ExportException {
		
		this(dblayer, query, writer, builder, selection);
		this.useProjections = true;
		this.projections = projections;
		this.description = projections.getDescription();
		try { 
			torso = (Record)rootTable.newInstance();
			torso.createTorso();
		} catch (Exception e) {
			throw new ExportException(L10n.getString("Error.InternalError"));
		}
	}
	
	/**
	 * Some records may be marked as deleted (so that Plantlore can revive them
	 * it the User tells him to). Some formats may wish to skip such records, others
	 * may make use of them.
	 * 
	 * @param ignore	True if the dead records should be skipped.
	 * @return	The Export task itself.
	 */
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
					record = reconstruct( torso, (Object[])database.more( resultId, i, i )[0], description );
				else
					record = (Record)((Object[])database.more( resultId, i, i )[0])[0];
				
				
				logger.debug("New record No. "+i+" fetched: "+record.toFullString());
				if( !selection.contains( record ) || (ignoreDead && record.isDead()) ) 
					continue; // Is the record selected?
				
				logger.debug("The record is in the selection. It will be exported.");
				count++;
				setStatusMessage(count + " " + L10n.getString("Export.RecordsExported"));
				setPosition(count);
				
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
			setStatusMessage(count + " " + L10n.getString("Export.RecordsExported"));
			cleanup();
		}
		catch(Exception e) {
			logger.error("Export ended prematurely: "+e.getMessage());
			cleanup();
			throw e;
		}
		
		return null;
	}
	
	/**
	 * Perform the final cleanup - close the file and the query. 
	 */
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
	 * @param occurrence	The currently processed occurrence record.
	 */
	private void loadAssociatedAuthors(Occurrence occurrence) 
	throws RemoteException, IOException, DBLayerException {
		
		logger.debug("Processing the associated information about Authors.");
		
		SelectQuery query = null;
		try {
			query = database.createQuery(AuthorOccurrence.class);
			
			query.createAlias(AuthorOccurrence.OCCURRENCE, "OCC");
			/* 6 lines of new code */
			query.createAlias(AuthorOccurrence.AUTHOR, Record.alias(Author.class));
			List<Pair<Class, String>> description = null;
			if( useProjections )
				description = projections.addProjections(query, AuthorOccurrence.class, Author.class);
			
			AuthorOccurrence ao = new AuthorOccurrence();
			ao.setAuthor(new Author());
			
			// AuthorOccurrence.OCCURRENCE = Occurrence.ID  &&  Occurrence.ID = occ.getId()
			query.addRestriction(PlantloreConstants.RESTR_EQ_PROPERTY, AuthorOccurrence.OCCURRENCE, "OCC."+Occurrence.ID, null, null);
			query.addRestriction(PlantloreConstants.RESTR_EQ, "OCC."+Occurrence.ID, null, occurrence.getId(), null);
			int resultId = database.executeQuery( query );
			
			// Take all results and send them to the Builder as well.
			int rows = database.getNumRows( resultId );
			for(int i = 0; i < rows; i++) {
				logger.debug("Fetching associated data (Author, AuthorOccurrence).");
				
				/* 4 lines of new code */
				if(useProjections) 
					ao = (AuthorOccurrence) reconstruct( ao, (Object[])database.more( resultId, i, i )[0], description );
				else
					ao = (AuthorOccurrence) ((Object[])database.more( resultId, i, i )[0])[0];
				
//				Object[] pulp = database.more( resultId, i, i );
//				AuthorOccurrence ao = (AuthorOccurrence) ((Object[])pulp[0])[0];
				ao.setOccurrence( null ); // cut off the way back to the occurrence
				if(ao.isDead() && ignoreDead) continue;
				
				logger.debug("New author-occurence record: " + ao.toFullString());
				
				build.part( ao );
			}
		}
		finally {
			if( query != null) 
				try {
					database.closeQuery( query );
				} catch(Exception e) {
					// Nothing we can do.
				}
		}
		
		logger.debug("Author-occurence processed.");
	}
	
	
	
	/**
	 * Reconstruct the record from the given values, if projections were used.
	 * <br/>
	 * An example: Let us suppose that we created projection for 
	 * <code>Occurrence.ID</code> and <code>Plant.TAXON<code> and
	 * <code>NearestVillage.NAME</code>. Database layer returns
	 * [3292, "Rubus sp.", "České Budějovice"]. The `description` is the key to
	 * identificaton of those values, as it says:
	 * <code>Occurrence.ID = 3292</code>,
	 * <code>Plant.TAXON = Rubus sp.</code> and
	 * <code>NearestVillage = České Budějovice</code>.
	 * <br/>
	 * Without the description we would not know what value belongs to which column.
	 * 
	 * @param values	Values of columns (in the same order as in the Description).
	 * @param torso	The record that should be filled with values.
	 * @param description The description of columns.
	 * @return	The reconstructed record.
	 */
	private Record reconstruct(Record torso, Object[] values, List<Pair<Class, String>> description) {
		for(int i = 0; i < description.size(); i++ ) {
			Pair<Class, String> d = description.get(i);
			torso.setValue(d.getFirst(), d.getSecond(), values[i]);
		}
		return torso;
	}

}
