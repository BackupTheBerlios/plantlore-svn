package net.sf.plantlore.client.tableimport;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import net.sf.plantlore.client.tableimport.TableParser.Action;
import net.sf.plantlore.common.DBLayerUtils;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.common.exception.ImportException;
import net.sf.plantlore.common.exception.ParserException;
import net.sf.plantlore.common.record.*; // Don't tell me anything about "sloppy programming" here!
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;


/**
 * The Table Import task acts as the Director from the Builder design pattern.
 * It repeatedly asks for records and processes them accordingly.
 * <br/>
 * <ul>
 * <li>Records that are meant to be inserted are inserted only if they are not in the database already.</li>
 * <li>Records that are meant to be deleted are removed only if there is no other record in the database
 * that would refer to it, so that the reference integrity is kept.</li>
 * <li>Records that are meant to be updated may be treated in three different ways:
 * 		<ol>
 * 		<li>if the record to be updated is not in the database, then the replacement is inserted;</li>
 * 		<li>if the record to be updated is in the database but the replacement is not, the record
 * 		in the database is updated accordingly; </li>
 * 		<li>if both the record to be updated and the replacement are in the database, 
 * 		the (original) record is deleted.</li>
 * 		</ol>
 * </li>
 * </ul>
 * <br/>
 * The Table Import is potentially a dangerous operation which is why only the Administrator can perform it.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-05-31
 * @version 1.0
 */
public class TableImportTask extends Task {
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());

	private TableParser parser;
	private DBLayer db;
	private int count = 0, inserted = 0, updated = 0, deleted = 0;
	private DBLayerUtils dbutils;
	
//	private static Set<Class> supportedTables = new HashSet<Class>(
//			Arrays.asList(Metadata.class, Plant.class, NearestVillage.class, Phytochorion.class, Territory.class));
	
	/**
	 * Create a new Table Import task.
	 * 
	 * @param db	The database where the data will be imported.
	 * @param TableParser	The TableParser that is responsible for parsing the file.
	 */
	public TableImportTask(DBLayer db, TableParser parser) {
		this.db = db;
		this.parser = parser;
		
		dbutils = new DBLayerUtils( db );
	}
	
	
	/**
	 * Import is a very delicate procedure, it should not be restarted.
	 * 
	 */
	@Override
	public void proceed() {
		// Empty implementation. Just to make sure no one will try to resurrect this operation.
	}
	
	
	/**
	 * Start the import procedure.
	 */
	public Object task() throws Exception {
		logger.info("Table Import begins...");
		
		setStatusMessage(L10n.getString("Import.Initializing"));
		Class table = parser.initialize();
		
		setStatusMessage(L10n.getString("Import.Initialized"));
		setLength( parser.getNumberOfRecords() );
				
		while( !isCanceled() && parser.hasNext() ) {
			DataHolder data = null;
			try {
				count++;
				setPosition( count );
				setStatusMessage(L10n.getFormattedString("Import.RecordsImported", count));
				data = parser.getNext();
			} catch( ParserException pe ) {
				logger.warn("The record is corrupted. " + pe);
				setStatusMessage(L10n.getFormattedString("Import.CompletelyCorruptedRecord", count));
				continue;
			}
			
			if( data.record == null
					|| !data.record.areAllNNSet()
					|| (data.action == TableParser.Action.UPDATE 
							&& (data.replacement == null || !data.replacement.areAllNNSet()))   ) {
				
				logger.info("Rejecting the record No. "+count+"! Some of the not-null values are not specified!");
				setStatusMessage(L10n.getFormattedString("Import.IncompleteRecord", count));					
			}
			
			logger.debug(data.action+" "+data.record.toFullString() + 
					((data.action == Action.UPDATE) ? 
							" ==> " + data.replacement.toFullString() :
					""));
			
			
			Record recordInDB = null;
			try {
				dbutils.findMatchInDB( data.record );
			} catch(DBLayerException e) {
				continue;
			}
			boolean isRecordInDB = recordInDB != null;
			
			// Take action.
			try {
				switch(data.action) {
				case INSERT:
					if( !isRecordInDB )
						insert( data.record );
					inserted++;						
					break;
				case DELETE:
					if( isRecordInDB )
						delete( recordInDB );
					deleted++;
					break;
				case UPDATE:
					Record replacementInDB = dbutils.findMatchInDB( data.replacement ); 
					if( replacementInDB == null ) {
						if( !isRecordInDB )
							insert( data.replacement );
						else
							update( recordInDB, data.replacement );
					} 
					else if( isRecordInDB )
						delete( recordInDB );
					updated ++;
					break;
				}
			} catch(ImportException ie) {
				logger.error("The import of the record No. " + count + " was unsuccessful! " + ie.getMessage());
				//setStatusMessage( ie.getMessage() );
			} catch(DBLayerException de) {
				logger.error("Delete/update/insert failed! " + de.getMessage());
				//setStatusMessage( L10n.getFormattedString("Import.UnableToProcess", count) + " " + 
				//		((de.getMessage() == null) ? L10n.getString("Import.UnknownReason") : de.getMessage()) );
			}
		}
		
		
		if( !isCanceled() ) {
			logger.info("Import completed. "+count+" records processed ("+
					inserted+" inserted, "+updated+" updated, "+deleted+" deleted).");
			setStatusMessage(L10n.getString("Import.Completed"));
		}
		else {
			logger.info("Import aborted. "+count+" records processed ("+
					inserted+" inserted, "+updated+" updated, "+deleted+" deleted).");
			setStatusMessage(L10n.getString("Import.Aborted"));
		}
		
		setStatusMessage(L10n.getString("Import.UpdatingEnvironment"));
		setChanged();
		notifyObservers( new PlantloreConstants.Table[] { PlantloreConstants.classToTable.get(table) } );
		
		fireStopped(null);
		return null;
	}
	
	
	/**
	 * Insert the record into the database.
	 */
	protected void insert(Record record) 
	throws RemoteException, DBLayerException {
		Integer newId = db.executeInsertHistory( record );
		record.setId( newId ); // It is not important here - these records won't be referenced (in here).
	}
	
	/**
	 * Update the record in the database.
	 */
	protected void update(Record current, Record replacement) 
	throws RemoteException, DBLayerException {
		
		for(String property : current.getProperties())
			current.setValue(property, replacement.getValue(property));
		
		db.executeUpdateHistory( current );
	}
		
	/**
	 * Remove the record from the database if possible.
	 */
	protected void delete(Record record) 
	throws RemoteException, DBLayerException, ImportException {
		if(record.getId() != null) {
			int sharers = dbutils.sharedBy( record );
			if( sharers > 0 ) {
				logger.error("The "+record+" is in use by "+sharers+" other records. It cannot be deleted!");
				throw new ImportException(L10n.getFormattedString("Error.DeletingSharedRecord", record, sharers));
			}
			db.executeDeleteHistory( record );
		}
	}
	
	
}
