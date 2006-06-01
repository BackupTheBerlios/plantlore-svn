package net.sf.plantlore.client.imports.table;

import java.rmi.RemoteException;
import java.util.*;

import org.apache.log4j.Logger;

import static net.sf.plantlore.common.PlantloreConstants.RESTR_EQ;
import net.sf.plantlore.client.imports.table.TableParser.Action;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.common.exception.ImportException;
import net.sf.plantlore.common.exception.ParserException;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;


/**
 * DefaultDirector
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-05-31
 * @version 1.0
 */
public class TableImportTask extends Task {
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());

	private TableParser parser;
	private DBLayer db;
	private Class table;
	private int count = 0, inserted = 0, updated = 0, deleted = 0;
	
	private static Set<Class> supportedTables = new HashSet<Class>(
			Arrays.asList(Metadata.class, Plant.class, Village.class, Phytochorion.class, Territory.class));
	
	private static Map<Class, Class> parentTable = new Hashtable<Class, Class>();
	private static Map<Class, String> parentColumn = new Hashtable<Class, String>();
	
	static {
		parentTable.put(Village.class, Habitat.class);
		parentTable.put(Phytochorion.class, Habitat.class);
		parentTable.put(Territory.class, Habitat.class);
		parentTable.put(Metadata.class, Occurrence.class);
		parentTable.put(Plant.class, Occurrence.class);

		parentColumn.put(Village.class, Habitat.VILLAGE);
		parentColumn.put(Phytochorion.class, Habitat.PHYTOCHORION);
		parentColumn.put(Territory.class, Habitat.TERRITORY);
		parentColumn.put(Metadata.class, Occurrence.METADATA);
		parentColumn.put(Plant.class, Occurrence.PLANT);
	}
	
	

	/**
	 * 
	 * @param db	The database where the data will be imported.
	 * @param TableParser	The TableParser that is responsible for parsing the file.
	 * @throws ImportException	If some parameters are null.
	 */
	public TableImportTask(DBLayer db, Class table, TableParser parser) 
	throws ImportException, RemoteException {
		if(db == null) {
			logger.error("The database layer is null!");
			throw new ImportException(L10n.getString("Error.InvalidDBLayer"));
		}
		if(parser == null) {
			logger.error("The TableParser is null!");
			throw new ImportException(L10n.getString("Error.InvalidParser"));
		}
		if( table == null || !supportedTables.contains(table) ) {
			logger.error("The Table is null!");
			throw new ImportException(L10n.getString("Error.UnsupportedTable"));
		}
		this.db = db;
		this.parser = parser;
		this.table = table;
	}
	
	
	/**
	 * Import is a very delicate procedure, it should not be restarted.
	 * 
	 */
	@Override
	public void proceed() {
	}
	
	
	/**
	 * Start the import procedure.
	 */
	public Object task() throws Exception {
		SelectQuery q = null;
		boolean transactionInProgress =  false;
		
		logger.info("Import into "+table+" begins...");
		
		setLength( parser.getNumberOfRecords() );
				
		try {
			
			q = db.createQuery(table);
			int resultId = db.executeQuery( q ),
			rows = db.getNumRows( resultId );
			
			Collection<Record> cache = new HashSet<Record>(rows);
			
			for(int i = 0; i < rows; i++) {
				Object[] pulp = db.more(resultId, i, i);
				cache.add( (Record)((Object[])(pulp[0]))[0] );
			}
			
			db.closeQuery( q );
			q = null;
			
			transactionInProgress = db.beginTransaction();
			if( ! transactionInProgress )
				throw new ImportException(L10n.getString("Error.TransactionRaceConditions"));
						
			while( !isCanceled() && parser.hasNext() ) {
				DataHolder data = null;
				try {
					count++;
					setPosition( count );
					data = parser.getNext();
				} catch( ParserException pe ) {
					logger.warn("The record is corrupted. " + pe);
					logger.info("Skipping the record No. " + count);
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
				
				logger.debug(data.action+" "+data.record + 
						((data.action == Action.UPDATE) ? 
								" ==> " + data.replacement :
								""));
				
				// Take action.
				try {
					switch(data.action) {
					case INSERT:
						if( !cache.contains(data.record) ) {
							insert( data.record );
							cache.add( data.record );
						}
						inserted++;						
						break;
					case DELETE:
						if( cache.contains(data.record) ) {
							delete( data.record );
							cache.remove( data.record );
						}
						deleted++;
						break;
					case UPDATE:
						if( !cache.contains(data.replacement) )
							if( !cache.remove(data.record) ) {
								insert( data.replacement );
								cache.add( data.replacement );
							}
							else {
								update( data.record, data.replacement );
								cache.add( data.replacement );
							}
						updated ++;
						break;
					}
				} catch(ImportException ie) {
					logger.error("The import of the record No. " + count + " was unsuccessful! " + ie);
					setStatusMessage( ie.getMessage() );
				} catch(DBLayerException de) {
					logger.error("Delete/update/insert failed! " + de);
					setStatusMessage( L10n.getFormattedString("Error.UnableToProcess", count) + " " + 
							((de.getMessage() == null) ? L10n.getString("Import.UnknownReason") : de.getMessage()) );
				}
			}

			
			if( !isCanceled() ) {
				transactionInProgress = ! db.commitTransaction();
				logger.info("Import completed. "+count+" records processed ("+
						inserted+" inserted, "+updated+" updated, "+deleted+" deleted).");
				setStatusMessage(L10n.getString("Import.Completed"));
			}
			else {
				transactionInProgress = ! db.rollbackTransaction();
				logger.info("Import aborted. Nothing will be stored in the database.");
				setStatusMessage(L10n.getString("Import.Aborted"));
			}
			
		} catch(Exception e) { // Nothing can leave undetected. 
			try {
				if(q != null) 
					db.closeQuery( q );
				if(transactionInProgress) 
					transactionInProgress = ! db.rollbackTransaction();
			} catch(Exception re) {}
			
			logger.fatal("Import ended prematurely. "+count+" records processed ("+
					inserted+" inserted, "+updated+" updated, "+deleted+" deleted).");
			setStatusMessage(L10n.getString("Import.Failed"));
			
			// This is a serious exception, the User should see it.
			throw e;
		}
				
		fireStopped(null);
		return null;
	}
	
	
	/**
	 * Find out whether the record is shared among other records.
	 * <br/>
	 * 
	 * @param record	The instance of some record
	 * @return	The number of records that share the supplied <code>record</code>. 
	 */
	protected int sharedBy(Record record) 
	throws RemoteException {
		SelectQuery q = null;
		int rows = 0;
		try {
			Class parent = parentTable.get( record.getClass() );
			String column = parentColumn.get( record.getClass() );
			
			System.out.println(parent.getSimpleName()+"."+column);
			
			q = db.createQuery( parent );
			q.addRestriction(RESTR_EQ, column, null, record, null);
			int resultset = db.executeQuery(q); 
			rows = db.getNumRows(resultset);
		} catch (DBLayerException e) {
			e.printStackTrace();
		} finally {
			if(q != null)
				db.closeQuery(q);
		}
		return rows;
	}
	
	/**
	 */
	protected void insert(Record record) 
	throws RemoteException, DBLayerException {
		db.executeInsertInTransaction( record );

	}
	
	/**
	 */
	protected void update(Record current, Record replacement) 
	throws RemoteException, DBLayerException {
		for(String property : current.getProperties())
			current.setValue(property, replacement.getValue(property));
		
		db.executeUpdateInTransaction( current );
	}
		
	/**
	 */
	protected void delete(Record record) 
	throws RemoteException, DBLayerException, ImportException {
		int sharers = sharedBy( record );
		if( sharers > 0 ) {
			logger.error("The "+record+" is in use by "+sharers+" other records. It cannot be deleted!");
			throw new ImportException(L10n.getFormattedString("Error.DeletingSharedRecord", record, sharers));
		}
		db.executeDeleteInTransaction( record );
	}
	
	
}
