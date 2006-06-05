package net.sf.plantlore.client.imports;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;

import org.apache.log4j.Logger;

import static net.sf.plantlore.common.PlantloreConstants.RESTR_EQ;
//import static net.sf.plantlore.common.PlantloreConstants.RESTR_IS_NULL;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.common.exception.ImportException;
import net.sf.plantlore.common.exception.ParserException;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.client.imports.Parser.Action;


/**
 * The Director for importing the <b>Occurrence data</b>.
 * <br/>
 * Warning: <b>This Director is NOT meant for import of simple
 * records, such as Plants.</b> The Import's Default Director is
 * less versatile than the Export's Default Director.
 * 
 * <br/>
 * The Director continually fetches records from the <code>Parser</code>
 * i.e. from a file, and stores them in the database.
 * The <code>Parser</code> is responsible for reading and re-creating 
 * records from the given file. 
 * 
 * <br/>
 * Furthermore, there should be only one Director running at a time.
 * The Director may have to wish to interact with the User and 
 * it might not be easy to recognise to which Import the question relates.    
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-05-06
 * @version 1.0
 * @see net.sf.plantlore.client.imports.Parser
 */
public class DefaultDirector extends Observable implements Runnable {
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());

	private Parser parser;
	private DBLayer db;
	private int count = 0, inserted = 0, updated = 0, deleted = 0;
	
	private Action 
		lastDecision = Action.UNKNOWN,
		insertUpdateDecision = null,
		dateDecision = null;
	
	private boolean 
		useLastDecision = false,
		doNotAskAboutDateAgain = false,
		aborted = false,
		cacheEnabled = true;
	
	private User user;
	
	private Record 
		recordFromFile = null,
		recordInDatabase = null,
		problematicRecord = null;

	/**
	 * Create a new Default Director capable of importing the Occurrence data.
	 * This Director is not inteded for other ways of import! 
	 * 
	 * @param db	The database where the data will be imported.
	 * @param parser	The parser that is responsible for parsing the file.
	 * @param user	The current  User who performs the import.
	 * @throws ImportException	If some parameters are null.
	 */
	public DefaultDirector(DBLayer db, Parser parser, User user) 
	throws ImportException {
		setDatabase(db);
		setParser(parser);
		setUser(user);
	}
	
	/**
	 * Sometimes the record that is currently in the database 
	 * may be newer than the record that comes from the file.
	 * The User must always decide, whether he wants to replace
	 * the record or keep it. This may be annoying; this function 
	 * allows him to inform the import procedure to use the
	 * previous decision from now on.
	 * 
	 * @param arg	True if the User should not be bothered again.
	 */
	public void useLastDecisionInTimeIssues(boolean arg) {
		doNotAskAboutDateAgain = arg;
	}
	
	/**
	 * Sometimes the record, that is currently in the database
	 * and that is to be updated, is shared among more records.
	 * Thus the change done to this record may (possibly) affect some
	 * other records. The User must always decide, whether the 
	 * colliding record should be updated, or inserted.
	 * This may be annoying; this function allows him to inform
	 * the import procedure to use the previous decision from
	 * now on.
	 * 
	 * @param arg	True if the User should not be involved again.
	 */
	public void useLastDecisionInUpdateInsertIssues(boolean arg) {
		useLastDecision = arg;
	}
	
	/**
	 * Limit the usage of a cache memory.
	 * It is recommended to leave the cache enabled since it may
	 * significantly boost the performance of the import.
	 * 
	 * @param use	Set to <b>false</b> if you no longer want the cache to be used.
	 */
	public void useCache(boolean use) {
		cacheEnabled = use;
	}
	
	/**
	 * Set a new database layer.
	 * 
	 * @param db	The database layer to be set.
	 */
	protected void setDatabase(DBLayer db) 
	throws ImportException {
		if(db == null) {
			logger.error("The database layer is null!");
			throw new ImportException(L10n.getString("Error.InvalidDBLayer"));
		}
		this.db = db;
	}
	
	/**
	 * Set a new parser.
	 * 	
	 * @param parser	The parser to be set.
	 * @throws ImportException
	 */
	protected void setParser(Parser parser) 
	throws ImportException {
		if(parser == null) {
			logger.error("The Parser is null!");
			throw new ImportException(L10n.getString("Error.InvalidParser"));
		}
		this.parser = parser;
	}
	
	/**
	 * Set a new user. This object identifies the user that performs
	 * the import. This may affect the access rights the import uses.
	 * 
	 * @param user The user that is performing the import.
	 * @throws ImportException
	 */
	protected void setUser(User user) 
	throws ImportException {
		if(user == null) {
			logger.error("The User is null!");
			throw new ImportException(L10n.getString("Error.InvalidUser"));
		}
		this.user = user;
	}
	
	
	/**
	 * Inform the Import Default Director that a decision has been
	 * made and it may continue with the import procedure.
	 *  
	 * @param decision	The decision the User has come to.
	 * @see #expectDecision()
	 */
	synchronized public void makeDecision(Action decision) {
		this.lastDecision = decision;
		notify();
	}
	
	/**
	 * An extra thread that performs the notification of all observers.
	 */
	private class ObserverNotifier extends Thread {
		public void run() {
			setChanged(); notifyObservers( problematicRecord );
		}
	};
	
	private ObserverNotifier observerNotifier = new ObserverNotifier();
	
	
	/**
	 * Ask the User to make his decision about the current operation.
	 * Sometimes the User must be contacted in order to resolve some
	 * conflicts or problems that occur during the import process.
	 * <br/>
	 * The notification of Observers is performed by a separate thread.
	 * 
	 * @param about The record that caused the problem.
	 * @return	The decision the User has made.
	 * @see #observerNotifier
	 */
	synchronized protected Action expectDecision(Record about) {
		lastDecision = Action.UNKNOWN;
		problematicRecord = about;
		observerNotifier.start(); // must be another thread
		while( lastDecision == Action.UNKNOWN && !aborted ) {
			try { wait(); } catch( InterruptedException e ) {/* Never mind. */}
		}
		return lastDecision;
	}
	
	/**
	 * @return The currently processed record (from the file).
	 */
	public Record getProcessedRecordFromFile() {
		return recordFromFile;		
	}
	
	/**
	 * @return The currently processed record (in the database).
	 */
	public Record getProcessedRecordInDatabase() {
		return recordInDatabase;
	}
	
	/**
	 * @return	The (sub)record that caused the problem. 
	 */
	public Record getProblematic() {
		return problematicRecord;
	}
	
	/**
	 * Start the import procedure.
	 */
	public void run() {
		boolean transactionInProgress = false;
		
		try {
			logger.debug("Import begins...");
			
			// Reset the counters.
			count = inserted = updated = deleted = 0;
			Action takenAction = Action.UNKNOWN;
			
			// Go through the whole file.
			while( !aborted && parser.hasNextRecord() ) {
				
				int numberOfUndeadAuthors = 0; 
				
				logger.debug("Fetching a new record from the Parser.");
				// What is supposed to happen with the occurrence.
				Action intention = parser.fetchNextRecord(); 
				// Get a new Occurrence.
				Occurrence occ = null;
				try {
					count++;
					occ = (Occurrence) parser.getNextPart(Occurrence.class);
				} catch( ParserException e) {
					logger.warn("The record is corrupted. " + e);
					logger.info("Skipping the record No. " + count);
					
					setChanged();
					if(occ == null)
						notifyObservers(L10n.getFormattedString("Import.CompletelyCorruptedRecord", count));
					else
						notifyObservers(L10n.getFormattedString("Import.PartialyCorruptedRecord", count, occ.getUnitIdDb(), occ.getUnitValue()));
					continue;
				}
				recordFromFile = occ;
				
				// If the record in the file is dead, then it is clearly meant to be deleted!
				if( recordFromFile.isDead() )
					intention = Action.DELETE;
				
				logger.debug("New record No. "+count+" fetched: "+occ);
				logger.debug("Intention: " + intention);
				
				boolean isValid = occ.areAllNNSet();
				if( !isValid ) {
					logger.info("Rejecting the record No. "+count+"! Some of the not-null values are not specified!");
					setChanged();
					notifyObservers(L10n.getFormattedString("Import.IncompleteRecord", count));
					continue;
				}
				logger.debug("The record is valid.");
				
				
				
				/*----------------------------------------------------------
				 * Try to find this Occurrence record in the database.
				 *----------------------------------------------------------*/
				SelectQuery q = db.createQuery(Occurrence.class);
				q.addRestriction(RESTR_EQ, Occurrence.UNITIDDB, null, occ.getUnitIdDb(), null);
				q.addRestriction(RESTR_EQ, Occurrence.UNITVALUE, null, occ.getUnitValue(), null);
				int resultId = db.executeQuery( q );
				int rows = db.getNumRows( resultId );
				boolean isInDB = (rows != 0);
				if(rows > 1) {
					logger.error("The database is not in a consistent state - there are " + rows + " Occurrence " +
							"records with the same Unique Identifier ("+occ.getUnitIdDb()+"-"+occ.getUnitValue()+")!");
					setChanged();
					notifyObservers(L10n.getFormattedString("Import.DuplicateRecord", count, occ.getUnitIdDb(), occ.getUnitValue()));
					continue; // it is not clear which record should be updated!
				}
				Occurrence
				occInDB = isInDB ? (Occurrence)((Object[])db.more(resultId, 0, 0)[0])[0]  :  null;
				recordInDatabase = occInDB;
				db.closeQuery(q); resultId = -1;
				boolean isDead = isInDB ? occInDB.isDead() : false;

				
				
				/*----------------------------------------------------------
				 * Time check. Inform the User that he may want to make changes to 
				 * a newer record.
				 *----------------------------------------------------------*/
				if( isInDB ) {
					logger.debug("The record is in the database already.");
					
					Date 
						updateInDBOccurred = occInDB.getUpdatedWhen(),
						updateInFile = occ.getUpdatedWhen();
					
					// The record in the database is newer than the record in the file.
					if(updateInDBOccurred != null && updateInFile != null &&
							updateInDBOccurred.after(updateInFile)) {
						logger.info("The record in the file is older than the record stored in the database.");
						if( !doNotAskAboutDateAgain ) 
							dateDecision = expectDecision( occInDB );
						
						if( dateDecision != Action.UPDATE && dateDecision != Action.INSERT ) 
							continue;
					}
				}
				
				logger.debug("About to perform the requested operation.");
				
				// Begin a new transaction.
				transactionInProgress = db.beginTransaction();
				if( !transactionInProgress )
					throw new ImportException(L10n.getString("Error.TransactionRaceConditions"));
				
				
				try {
					/*----------------------------------------------------------
					 * The `occ` IS in the database as `occInDB` already.  
					 *----------------------------------------------------------*/
					if( isInDB ) {
						if( isDead )
							switch(intention) {
							case DELETE:
								// Nothing to be done, the record is already dead.
								takenAction = Action.DELETE;
								break;
							default:
								occInDB = (Occurrence) update( occInDB, occ );
								takenAction = Action.UPDATE;
								break;
							}
						else
							switch(intention) {
							case DELETE:
								occInDB = (Occurrence) delete( occInDB, 1 );
								// By a common decision: If the habitat is not shared it should be marked as deleted, too.
								if( sharedBy(occInDB.getHabitat(), Occurrence.class, Occurrence.HABITAT) > 1 )
									delete( occInDB.getHabitat(), 1 );
								takenAction = Action.DELETE;
								break;
							default:
								if(occInDB.equals(occ))
									occ = occInDB;
								else
									occInDB = (Occurrence) update( occInDB, occ );
								takenAction = Action.UPDATE;
								break;
							}
					}
					/*----------------------------------------------------------
					 * The `occ` is NOT in the database. 
					 *----------------------------------------------------------*/
					else 
						switch(intention) {
						case DELETE:	
							// There's nothing to delete.
							takenAction = Action.DELETE;
							break;
						default:
							occInDB = (Occurrence) insert( occ );
							takenAction = Action.INSERT;
							break;
						}
				}
				catch(Exception ie) {
					String msg = ie.getMessage();
					logger.error("The import of the record No. " + count + " was unsuccessful!");
					logger.error("This exception occured during insert/update/delete: " + msg);
					// Roll back the transaction.
					db.rollbackTransaction();
					transactionInProgress = false;
					// The user cannot do a thing. Should he be informed?
					String userMsg = L10n.getFormattedString("Import.ProblematicRecord", count, occ.getUnitIdDb(), occ.getUnitValue())
						+ " " + ( (msg == null) ? L10n.getString("Import.UnknownReason") : msg );
					setChanged(); notifyObservers(userMsg);
					continue;
				}
				
				
				logger.debug("Processing the associated records - Authors, AuthorOccurrences.");
				
			
				/*----------------------------------------------------------
				 * Now, deal with Authors associated with this Occurrence.
				 *----------------------------------------------------------*/
				
				// Is the record in the database?
				AuthorOccurrence[] sharers = new AuthorOccurrence[0];
				if( isInDB ) 
					sharers = findAllSharers(occInDB);
				// If the Occurrence record should have been DELETED, all associated AuthorOccurrences should be deleted as well.
				if( intention == Action.DELETE && sharers != null ) {
					logger.debug("Deleting all associated data (Author, AuthorOccurrence).");
					numberOfUndeadAuthors = 1; // so that the transaction is confirmed
					
					for(AuthorOccurrence ao : sharers) 
						if( !ao.isDead() )
							delete( ao, 2 );					
				}
				// The intention was to ADD or UPDATE the existing Occurrence record.  
				else {
					// Compute the number of undead authors (authors, that are not marked as deleted) 
					// the Occurrence record has in the database.
					// We must make sure that every Occurrence record has at least one (undead) author! 
					for(AuthorOccurrence ao : sharers) {
						ao.setOccurrence( null ); // simplify the comparison
						if( !ao.isDead() ) numberOfUndeadAuthors++ ;
					}
					
					while( parser.hasNextPart(AuthorOccurrence.class) ) {
						// Get the AuthorOccurrence from the Parser.
						AuthorOccurrence ao;
						logger.debug("Fetching associated data (Author, AuthorOccurrence).");
						try {
							ao = (AuthorOccurrence)parser.getNextPart(AuthorOccurrence.class);
						} catch (ParserException e) {
							logger.warn("The associated record is not valid. " + e.getMessage());
							continue;
						}
						
						// Validity check.
						if( !ao.areAllNNSet() ) {
							logger.warn("The AuthorOccurrence is incomplete - the Author is missing or a NN column is not set!");
							continue;
						}

						// Simplify the comparison (the Occurrence is known...)
						ao.setOccurrence( null );
						
						// Check if that AuthorOccurrence is already in the database.
						AuthorOccurrence aoInDB = null;
						for( AuthorOccurrence alpha : sharers ) {
							if( alpha.equalsUpTo( ao, AuthorOccurrence.DELETED ) ) {
								aoInDB = alpha;
								break;
							}
						}
						
						//	The Occurrence `occInDB` is in the database, that is for sure.
						// The ao.Occurrence, however, is NOT from the database.
						ao.setOccurrence( occInDB ); // now it's fine
						
						// The intention with this AuthorOccurrence. 
						intention = parser.intentedFor();
						// If the record is dead, we are supposed to delete it.
						if( ao.isDead() )
							intention = Action.DELETE;
						
						logger.debug("New author-occurence record: " + ao);
						logger.debug("Intention: " + intention);
						
						try {
							// [A] AO is not in the database.
							if(aoInDB == null)
								switch(intention) {
								case DELETE:
									break;
								default:
									Record counterpart = findMatchInDB( ao.getAuthor() );
									Author authorInDB = (counterpart == null) ? null : (Author)counterpart;

									// The Author is not in the database - we shall add him.
									if(authorInDB == null) {
										authorInDB = ao.getAuthor(); // technically, it is the authorToBeInDB
										Integer newId = db.executeInsertInTransaction( authorInDB );
										authorInDB.setId(newId);
									}
									// Set the correct author in the AO.
									ao.setAuthor(authorInDB);
									
									// Now the AuthorOccurrence is complete.
									if( occInDB != null )
										db.executeInsertInTransaction( ao );
									else
										db.executeInsertInTransactionHistory( ao );
									
									numberOfUndeadAuthors++;
								}
							// [B] AO is in the database already.
							else
								switch(intention) {
								case DELETE:
									if( !aoInDB.isDead() ) {
										aoInDB.setOccurrence( occInDB ); // repair the simplified record!
										delete( aoInDB, 1 );
										numberOfUndeadAuthors--;
									}
									break;
								case UNKNOWN:
								case INSERT:
								case UPDATE:
									// AO is already in the database (with the same properties a FKs!)
									break;
								}
						} catch (DBLayerException e) {
							logger.error("The associated record was not processed properly. "  + e.getMessage() );
							continue;
						}
						
						logger.debug("Author-occurence processed.");
					}
				}
				
				// Transaction is valid iff everything went fine and the number of undead authors is positive. 
				if( numberOfUndeadAuthors > 0 ) {
					transactionInProgress = ! db.commitTransaction();
					logger.debug("Transaction commited.");
				}
				else {
					transactionInProgress = ! db.rollbackTransaction();
					logger.warn("The current Occurrence record was not added - it would not have any Author left in the database!");
					setChanged();
					notifyObservers(L10n.getFormattedString("Import.NoAuthorsLeft", count));
				}

				switch( takenAction ) {
				case DELETE:
					deleted++;
					break;
				case UPDATE:
					updated++;
					break;
				case INSERT:
					inserted++;
					break;
				}
				setChanged(); notifyObservers( takenAction );
			}
		} 
		catch(Exception e) {
			logger.error("The import ended prematurely. "+count+" records processed. " + e.getMessage());

			e.printStackTrace();
			
			if( transactionInProgress ) 
				try {
					transactionInProgress = ! db.rollbackTransaction();
				} catch (Exception e2) {/* Nothing we can do, can we? */}
			
			setChanged(); notifyObservers(e);
			return;
		}
		
		logger.info("Import completed. " + count + " records processed. ("+
				inserted+" inserted, "+updated+" updated, "+deleted+" deleted).");
	}
	
	
	/**
	 * Find out whether the record is shared among other records.
	 * <br/>
	 * 
	 * @param record	The instance of some record
	 * @param father	The table that contains records possibly sharing the <code>record</code>.
	 * @param column	The name of the foreign key.	
	 * @return	The number of records in from the <code>father</code> table that share the <code>record</code>. 
	 */
	public int sharedBy(Record record, Class father, String column) 
	throws RemoteException {
		
		System.out.println(">>>> SHARE TEST: "+father+"."+column+" = " +record);
		
		SelectQuery q = null;
		int rows = 0;
		try {
			q = db.createQuery(father);
			q.addRestriction(RESTR_EQ, column, null, record, null);
			int resultset = db.executeQuery(q); 
			rows = db.getNumRows(resultset);
		} catch (DBLayerException e) {
			e.printStackTrace();
		} finally {
			if(q != null)
				db.closeQuery(q);
		}
		
		System.out.println(">>>> RESULT: " + rows);
		
		return rows;
	}
	
	/**
	 * Find all records that share the specified one.
	 * <br/>
	 * 
	 * @param shared	The instance of some record
	 * @param father	The table that contains records possibly sharing the <code>record</code>.
	 * @param column	The name of the foreign key.	
	 * @return	All sharers. 
	 */
	protected AuthorOccurrence[] findAllSharers(Record shared) 
	throws RemoteException {
		SelectQuery q = null;
		AuthorOccurrence[] sharers = new AuthorOccurrence[0];
		try {
			q = db.createQuery(AuthorOccurrence.class);
			q.addRestriction(RESTR_EQ, AuthorOccurrence.OCCURRENCE, null, shared, null);
			int resultset = db.executeQuery(q),
			rows = db.getNumRows(resultset);
			if(rows > 0) {
				sharers = new AuthorOccurrence[rows];
				Object[] pulp = db.more(resultset, 0, rows - 1);
				for( int i = 0; i < rows; i++ )
					sharers[i] = ( (AuthorOccurrence)(  (Object[])pulp[i]  )[0] );
			}
		} catch (DBLayerException e) {
			e.printStackTrace();			
		} finally {
			if(q != null)
				db.closeQuery(q);
		}
		return sharers;
	}
	
	/**
	 * A simple cache of records (one for each table).
	 * It is higly likely, due to the properties of the export procedure,
	 * that records sharing the same subrecords will come together.
	 * <br/>
	 * For example:
	 * There are occurrences <i>A</i> and <i>B</i>. The <i>B</i>
	 * goes right after <i>A</i>.
	 * The probability, that both occurrence will share the same
	 * Phytochorion, Territory, Village, Publication, or Metadata,
	 * is quite high. 
	 * <br/>
	 * The cache may save up to 5 select queries per one Occurrence 
	 * record.
	 */
	private Hashtable<Class, Record> cache = new Hashtable<Class, Record>(50);
	
	
	/**
	 * Try to find a record, that has exactly the same <b>properties</b>
	 * and <b>foreign keys</b>. The ID of the record doesn't matter.
	 * 
	 * @param record	The record we are looking for in the database.
	 * @return	The matching record from the database, or null if no such record exists.
	 */
	public Record findMatchInDB(Record record) 
	throws RemoteException, DBLayerException {
		if(record == null) return null;
		// Get the table.
		Class table = record.getClass();
		
		// Look in the cache.
		if(cacheEnabled) {
			Record cachedRecord = cache.get(table);
			if( cachedRecord != null && record.equals(cachedRecord))
				return cachedRecord; // hooray, one select has been saved!
		}
				
		// Create a query that will look for the record with the same properties.
		SelectQuery query = db.createQuery( table );

		// Equal properties.
		for(String property : record.getProperties()) {
			Object value = record.getValue(property);
//			System.out.println(" + "+table.getSimpleName()+"."+property+"="+value);
			if( value != null ) 
				query.addRestriction(RESTR_EQ, property, null, value, null);
		}
		// Equal foreign keys (by their ID's)!
		for(String key : record.getForeignKeys() ) {
			Record subrecord = (Record) record.getValue(key);
			query.addRestriction(RESTR_EQ, key, null, subrecord, null);
		}
		
		// Is there such record?
		int results = db.executeQuery( query );
		int rows = db.getNumRows( results );
		if( rows > 1 && !(record instanceof Habitat) ) 
			logger.info("There are " + rows + 
					" completely identical records in the " + table.getSimpleName() + " table!");
		
		record = null;
		if( rows != 0 ) // ain't that beautiful! 
			record = (Record)((Object[])(db.more(results, 0, 0)[0]))[0];
		
		db.closeQuery( query );
		
		// Update the cache appropriately - store the record for future generations.
		if( record != null && cacheEnabled ) {
			cache.remove(table);
			cache.put(table, record);
		}
		
		
		return record;
	}
	
	/**
	 * Insert new data into the table.
	 * The inserted record is inserted along with all of its parts.
	 * <br/>
	 * If the inserted record is an <b>AuthorOccurrence</b>, 
	 * its Occurrence subrecord will <b>not</b> be inserted again. 
	 * 
	 * @param record	The record to be inserted (with all of its subrecords).
 	 * @return The inserted record with its new ID set.
	 * @throws RemoteException	If the transport layer encounters a problem.
	 * @throws DBLayerException	If the database layer encounters a problem.
	 */
	public Record insert(Record record) 
	throws RemoteException, DBLayerException, ImportException {
		logger.debug("Inserting ["+record+"] into the database.");
		
		// Is this part of the record from an immutable table?
		boolean immutable = user.isAdmin() ?
				record instanceof Plant :
				Record.IMMUTABLE.contains( record.getClass() ) ;
		
		// This part of the record is from an immutable table -
		// try to find it in the table.
		if( immutable ) {
			logger.debug("The record belongs to an immutable table "+record.getClass().getSimpleName());
			Record counterpart = findMatchInDB( record );
			if( counterpart == null ) {
				logger.fatal("The counterpart for the record (in the immutable table " +
						record.getClass().getSimpleName()	+ ") was not found!");
				throw new ImportException(L10n.getString("Error.RecordNotFound"), record);
			}
			return counterpart;
		} 
		
		// The part of the record is from a common table.
		logger.debug("The record belongs to a common table "+record.getClass().getSimpleName());
		// Insert all of its sub-records.
		List<String> keys = record.getForeignKeys();
		// Inserting a new AuthorOccurrence MUSTN'T cause the insertion of the Occurrence.
		if(record instanceof AuthorOccurrence)
			keys.remove(AuthorOccurrence.OCCURRENCE); 
		for(String key : keys)
			record.setValue( key, insert( (Record)record.getValue(key) ) );
		
		Record counterpart = null;
		
		// Try to find its counterpart (if it is in the database already).
		// (The Habitat table is special, the relationship Occ->Habitat should always be 1:1.)
		if( !(record instanceof Habitat) ) {
			counterpart = findMatchInDB( record );
		}
		
		// The record is not in the database.
		if(counterpart == null) {
			logger.debug("The record is not in the database. It will be inserted.");
			// Insert it!
			Integer newId = db.executeInsertInTransaction(record);
			record.setId( newId );
			return record;
		}
		
		// The record is in the database.
		logger.debug("The record is in the database already. It will be used.");
		// Do not insert anything, use that record instead.
		return counterpart;
		
	}
	
	/**
	 * Update the <code>current</code> record (which is in the database) 
	 * according to the other <code>record</code>.
	 * <br/> 
	 * The <code>source</code> record is
	 * always kept up-to-date and its 
	 * members always belong to the database.
	 * <br/>
	 * It is possible that nothing will be inserted or updated at all 
	 * (if everything is up-to-date).
	 * 
	 * @param current	The source record - the record from the database that needs to be updated.
	 * @param replacement	The record containing changes the <code>current</code> record must undergo.
	 * @return The <code>current</code> record updated using the <code>replacement</code>.
	 * This record is always in the database already.
	 */
	public Record update(Record current, Record replacement) 
	throws RemoteException, DBLayerException, ImportException {
		
		return update(current, replacement, null, null);
	}
		
		
		
		
		
	private Record update(Record current, Record replacement, Class father, String foreignKey) 
		throws RemoteException, DBLayerException, ImportException {		
		logger.debug("Updating ["+current+"] with ["+replacement+"].");
		
		boolean immutable = user.isAdmin() ?
				current instanceof Plant :
				Record.IMMUTABLE.contains( current.getClass() ) ;
		/*
		 * We have an immutable table here - 
		 * therefore the replacement must match something 
		 * that is in the database already.
		 */
		if( immutable ) {
			logger.debug("The record belongs to an immutable table "+current.getClass().getSimpleName());
			// Don't they happen to be the same?
			if( doPropertiesMatch(current, replacement) )
				return current;
			// Try to find that record in the database.
			Record counterpart = findMatchInDB( replacement );
			if( counterpart == null ) {
				logger.fatal("The counterpart for the record (in the immutable table " +
						current.getClass().getSimpleName()	+ ") was not found!");
				throw new ImportException(L10n.getString("Error.RecordNotFound"), replacement);
			}
			return counterpart;
		}
		
		/*
		 * It is a little bit trickier now, because UPDATE may sometimes in fact
		 * mean INSERT or nothing :). 
		 */
		else {
			List<String> keys = replacement.getForeignKeys();
			boolean propertiesMatch = doPropertiesMatch(current, replacement);
			
			logger.debug("The record belongs to a common table "+current.getClass().getSimpleName());
			
			// [A] There are no foreign keys.
			// (Publication)
			if( keys.size() == 0 ) {
				// Both records have the same properties.
				if( propertiesMatch )
					return current;
				// Try to find a match in the database.
				Record counterpart = findMatchInDB( replacement );
				// A match has been found - use it.
				if(counterpart != null)
					return counterpart;
				// There is no match in the table.
				// I.e. the `replacement` is not in the table = 
				// the record we want `current` to be transformed to was not found.
				
				// There are two options now.
				//    EITHER
				// we insert the record into the database causing no damage to other records 
				//    OR
				// update the existing one risking that we will (possibly) affect some other records
				// that share the `current`.
				
				if( sharedBy(current, father, foreignKey) > 1 ) {
					// This is up to the User.
					logger.info("The record ["+current+"] is shared!");
					insertUpdateDecision = lastDecision;
					if(!useLastDecision) // ASK THE USER!
						insertUpdateDecision = expectDecision( replacement );
				}
				else
					insertUpdateDecision = Action.UPDATE;
				
				if( insertUpdateDecision == Action.UPDATE ) { // update the current record
					logger.debug("Updating the current record.");
					// Replace the values with new ones - fortunately, there are no FK involved.
					current.replaceWith( replacement );
					db.executeUpdateInTransaction( current );
					return current;
				}
				else /*if( decision == Action.INSERT )*/ {
					logger.debug("Inserting a new record.");
					// Insert the replacement as a new record [DEFAULT OPERATION].
					Integer newId = db.executeInsertInTransaction(replacement);
					replacement.setId( newId );
					return replacement;
				}
			}
			// [B] There are some foreign keys.
			// (Habitat, Occurrence)
			else {
				logger.debug("The common table contains foreign keys.");
				
				// Indicate, whether the record needed some changes.
				boolean dirty = false;
				// Deal with the AuthorOccurence - 
				// a new AuthorOccurrence MUSTN'T cause an update of the Occurrence.
				if(current instanceof AuthorOccurrence)
					keys.remove(AuthorOccurrence.OCCURRENCE);
				// Replace all foreign keys with records that already are in the database.
				for(String key : keys) {
					Object 
						currentSubrecord = current.getValue(key),
						replacementSubrecord = replacement.getValue(key);
					if(currentSubrecord == null || replacementSubrecord == null)
						throw new ImportException(L10n.getString("Error.FKIsNull"));
					
					Record 
						suggestion =  update( 
								(Record)currentSubrecord, 
								(Record)replacementSubrecord,
								current.getClass(), key);
					
					// The sub-record doesn't have to be changed.
					if( currentSubrecord == suggestion ) // == suffices (there's no need for equals()).
						continue;
					
					// The replacement is needed.
					current.setValue(key, suggestion);
					dirty = true;
				}
				
				// Replace the properties of the `current` with the ones of the `replacement`.
				if( !propertiesMatch )
					for(String property : current.getProperties()) 
						current.setValue(property, replacement.getValue(property));
				
				// Update the record in the database.
				if( dirty || !propertiesMatch ) {
					logger.debug("Updating the current record.");
					// Occurrences are always UPDATED
					if( current instanceof Occurrence )
						db.executeUpdateInTransaction(current);
					else {
						boolean shared = sharedBy(current, father, foreignKey) > 1;
						// If the record is not shared, it is safe to performt he udpate.
						if( !shared ) 
							db.executeUpdateInTransaction(current);
						// If the shared record is Habitat, a new record will be created.
						// Required by: Lada and the DB Model demands.
						else if( current instanceof Habitat ) {
							Integer newId = db.executeInsertInTransaction(current);
							current.setId(newId);
						} else {
							// If the shared record is something else, the User's intervention may be needed.
							insertUpdateDecision = lastDecision;
							if(!useLastDecision) 
								insertUpdateDecision = expectDecision( replacement );
							if(insertUpdateDecision == Action.UPDATE) 
								// User decided to update (potentially dangerous).
								db.executeUpdateInTransaction(current);
							else {
								// User decided to insert new copy (safer).
								Integer newId = db.executeInsertInTransaction(current);
								current.setId(newId);
							}
						}
					}
				}
				// Return the current record (updated).
				return current;
			}
		}
	}
	
	/**
	 * Compare two records <code>a</code> and <code>b</code>
	 * by their properties.
	 * 
	 * @param a	The first record.
	 * @param b	The second record.
	 * @return	True if and only they have the same properties
	 * (i.e. the same values in columns that are not foreign keys).
	 */
	protected boolean doPropertiesMatch(Record a, Record b) {
		Class table = a.getClass();
		if( table != b.getClass() ) return false;
		for( String property : a.getProperties() ) { 
			Object 
			valueA = a.getValue(property),
			valueB = b.getValue(property);
			if(valueA == null && valueB == null) continue;
			if(valueA == null || valueB == null) return false;
			if( !valueA.equals(valueB) ) return false;
		}
		return true;
	}
	
	
	/**
	 * Delete the specified record.
	 * (Technically: mark the record as deleted = make it appear dead.)
	 * <br/>
	 * 
	 * @param record	The record that will be deleted. Do not forget this record has
	 * to belong to the database layer (ie. it must be something previously
	 * obtained directly from the database layer).
	 */
	public Record delete(Record record, int deleteLevel) 
	throws RemoteException, DBLayerException {
		if(record instanceof Deletable) {
			logger.info("Deleting ["+record+"] from the database.");
			((Deletable)record).setDeleted(deleteLevel);
			db.executeUpdateInTransaction( record );
		}
		return record;
	}
	
	
	/**
	 * Abort the export immediately.
	 */
	public void abort() {
		aborted = true;
		logger.info("Import aborted!");
	}
	
	/**
	 * @return The number of records that were actually inserted into the database.
	 */
	public int getNumberOfInserted() {
		return inserted;
	}
	
	/**
	 * @return The number of records that were deleted from the database.
	 */
	public int getNumberOfDeleted() {
		return deleted;
	}
	
	/**
	 * @return The number of records that were updated in the database.
	 */
	public int getNumberOfUpdated() {
		return updated;
	}
	
	/**
	 * @return The total number of loaded records (some of them may have been rejected).
	 */
	public int getNumberOfProcessed() {
		return count;
	}
}
