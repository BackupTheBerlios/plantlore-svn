package net.sf.plantlore.client.imports;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;

import org.apache.log4j.Logger;

import static net.sf.plantlore.common.PlantloreConstants.RESTR_EQ;
import static net.sf.plantlore.common.PlantloreConstants.RESTR_IS_NULL;
import net.sf.plantlore.server.DBLayerException;
import net.sf.plantlore.common.exception.ImportException;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.client.imports.Parser.Action;


/**
 * The Director for the import of the Occurrence data.
 * <br/>
 * The Director continually fetches records from the <code>Parser</code>
 * i.e. from a file, and stores them in the database.
 * The <code>Parser</code> is responsible for reading and re-creating 
 * records from the given file. 
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-05-06
 * @version alpha
 */
public class DefaultDirector extends Observable implements Runnable {
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());

	private Parser parser;
	private DBLayer db;
	private int count, imported;
	
	private Action 
		lastDecision = Action.UNKNOWN,
		insertUpdateDecision = null,
		dateDecision = null;
	
	private boolean 
		useLastDecision = false,
		doNotAskAboutDateAgain = false,
		aborted = false;
	
	private User user;
	private Calendar now = Calendar.getInstance();
	
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
	 * Set a new database layer.
	 * 
	 * @param db	The database layer to be set.
	 */
	protected void setDatabase(DBLayer db) 
	throws ImportException {
		if(db == null) {
			logger.error("The database layer is null!");
			throw new ImportException("The database layer cannot be null!");
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
			throw new ImportException("The Parser cannot be null!");
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
			throw new ImportException("The User cannot be null!");
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
		//protected Record problematicRecord = null;
		
		public void run() {
			setChanged(); notifyObservers( problematicRecord );
		}
	};
	
	private ObserverNotifier observerNotifier = new ObserverNotifier();
	
	
	/**
	 * Ask the User to make his decision about the current operation.
	 * Sometimes the User must be contacted in order to resolve some
	 * conflicts or problems that occur during the import process.
	 * 
	 * @param about The record that caused the problem.
	 * @return	The decision the User has made.
	 */
	synchronized protected Action expectDecision(Record about) {
		lastDecision = Action.UNKNOWN;
		problematicRecord = about;
		observerNotifier.start();
		while( lastDecision == Action.UNKNOWN && !aborted ) {
			try { wait(); } catch( InterruptedException e ) {}
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
		
		try {
			logger.info("Import begins...");
			
			// Reset the counters.
			count = imported = 0;
			
			// Go through the whole file.
			while( !aborted && parser.hasNext() ) {
				// Get a new Occurrence.
				Occurrence occ = (Occurrence) parser.next();
				recordFromFile = occ;
				logger.debug("New record fetched: "+occ.getUnitIdDb()+"-"+occ.getUnitValue());
				// What is supposed to happen with the occurrence. 
				Action intention = parser.intentedFor();
				logger.debug("Intention: " + intention);
				
				count++;
				boolean isValid = 
					(intention == Action.DELETE) ? 
							occ.getUnitIdDb() != null && occ.getUnitValue() != null : 
							occ.areAllNNSet();
				
				if( !isValid ) {
					logger.info("The record No. "+count+" is not valid! Some of the not-null values are not specified!");
					continue;
				}
				logger.debug("The record is valid = all necessary columns are set.");
				
				
				/*----------------------------------------------------------
				 * Try to find this Occurrence record in the database.
				 *----------------------------------------------------------*/
				SelectQuery q = db.createQuery(Occurrence.class);
				q.addRestriction(RESTR_EQ, Occurrence.UNITIDDB, null, occ.getUnitIdDb(), null);
				q.addRestriction(RESTR_EQ, Occurrence.UNITVALUE, null, occ.getUnitValue(), null);
				int resultId = db.executeQuery( q ), rows = db.getNumRows( resultId );
				boolean isInDB = (rows != 0);
				if(rows > 1)
					logger.error("The database is not in a consistent state - there are " + rows + " Occurrence " +
							"records with the same Unique Identifier ("+occ.getUnitIdDb()+"-"+occ.getUnitValue()+")!");
				Occurrence
				occInDB = isInDB ? (Occurrence)((Object[])db.more(resultId, 1, 1)[0])[0]  :  null;
				recordInDatabase = occInDB;
				db.closeQuery(q); resultId = -1;
				boolean isDead = isInDB ? occInDB.isDead() : false;

				logger.debug("The record is in the database already: " + 
						occInDB.getUnitIdDb() + "-" + occInDB.getUnitValue());
				
				
				/*----------------------------------------------------------
				 * Time check. Inform the User that he may want to make changes to 
				 * a newer record.
				 *----------------------------------------------------------*/
				if( isInDB ) {
					Date 
						updateInDBOccurred = occInDB.getUpdatedWhen(),
						updateInFile = occ.getUpdatedWhen();
					
					// The record in the database is newer than the record in the file.
					if(updateInDBOccurred.after(updateInFile)) {
						logger.debug("The record in the file is OLDER than the record stored in the database.");
						if( !doNotAskAboutDateAgain ) 
							dateDecision = expectDecision( occInDB );
						
						if( dateDecision != Action.UPDATE && dateDecision != Action.INSERT ) 
							continue;
					}
				}
				
				logger.debug("Performing the requested operation.");
				
				try {
				/*----------------------------------------------------------
				 * The `occ` IS in the database as `occInDB` already.  
				 *----------------------------------------------------------*/
				if( isInDB ) {
					if( isDead )
						switch(intention) {
						case DELETE:
							// Nothing to be done.
							break;
						default:
							occInDB = (Occurrence) update( occInDB, occ );
							break;
						}
					else
						switch(intention) {
						case DELETE:
							occInDB = (Occurrence) delete( occInDB );
							break;
						default:
							occInDB = (Occurrence) update( occInDB, occ );
							break;
						}
				}
				/*----------------------------------------------------------
				 * The `occ` is NOT in the database. 
				 *----------------------------------------------------------*/
				else 
					switch(intention) {
					case DELETE:	
						break;
					default:
						occInDB = (Occurrence) insert( occ );
						break;
					}
				}
				catch(ImportException ie) {
					logger.error("The import of the record No. " + count + " was unsuccessful!");
					logger.error("The exception occured during insert/update: " + ie);
					// The user cannot do a thing. Should he be informed?					
				}
				
				
				imported++;
				
				logger.debug("Adding the associated information about Users.");
				
				/*----------------------------------------------------------
				 * Now, deal with Authors associated with this Occurrence.
				 *----------------------------------------------------------*/
				while( parser.hasNextPart(AuthorOccurrence.class) ) 
					try {
						AuthorOccurrence ao = (AuthorOccurrence)parser.nextPart(AuthorOccurrence.class);
						intention = parser.intentedFor();
						
						logger.debug("New author-occurence.");
						logger.debug("Intented for: " + intention);
						
						Record aoInDB = findMatchInDB( ao );

						logger.debug("Performing the requested operation.");
						// The AuthorOccurrence is not in the database.
						if( aoInDB == null ) {
							switch(intention) {
							case DELETE:
								break;
							default:
								aoInDB = insert( ao );
							break;
							}
						}
						// The AuthorOccurrence is in the database already.
						else {
							switch(intention) {
							case DELETE:
								aoInDB = (AuthorOccurrence) delete( (AuthorOccurrence) aoInDB );
								break;
							default:
								aoInDB = update( aoInDB, ao );
							break;
							}
						}
						
						logger.debug("Author-occurence processed.");
						
					} catch(ImportException ie) {
						logger.error("The import of the record No. " + count + " was unsuccessful!");
						logger.error("The exception occured during insert/update: " + ie);
					}
			}
		} 
		catch(DBLayerException e) {
			logger.error("The import ended prematurely. "+imported+" records imported into the database.");
			logger.error("The exception was caused by the DBLayer: " + e);
			setChanged(); notifyObservers(e);
		}
		catch(RemoteException e) {
			logger.error("The import ended prematurely. "+imported+" records imported into the database.");
			logger.error("The exception was caused by the the transport layer (RMI): " + e);
			setChanged(); notifyObservers(e);
		}
		logger.info("Import ended. " + imported + " records have been imported (out of " + count + ").");
	}
	
	
	/**
	 * Try to find a record, that has exactly the same <b>properties</b>
	 * and <b>foreign keys</b>. The ID of the record doesn't matter.
	 * 
	 * @param record	The record we are looking for in the database.
	 * @return	The matching record from the database, or null if no such record exists.
	 */
	protected Record findMatchInDB(Record record) 
	throws RemoteException, DBLayerException {
		if(record == null) return null;
		// Get the table.
		Class table = record.getClass();
		// Create a query that will look for the record with the same properties.
		SelectQuery query = db.createQuery( table );

		// Equal properties.
		for(String property : record.getProperties()) {
			Object value = record.getValue(property);
			if( value == null ) // use the database null
				query.addRestriction(RESTR_EQ, property, null, RESTR_IS_NULL, null);
			else
				query.addRestriction(RESTR_EQ, property, null, value, null);
		}
		// Equal foreign keys (by their ID's)!
		for(String key : record.getForeignKeys() ) {
			Record subrecord = (Record) record.getValue(key);
			query.addRestriction(RESTR_EQ, key, null, subrecord.getId(), null);
		}
		
		// Is there such record?
		int results = db.executeQuery( query );
		int rows = db.getNumRows( results );
		if( rows > 1 && !(record instanceof Habitat) ) 
			logger.info("Weird! There are two (or more) " +
					"identical records in the " + table.getSimpleName() + " table!");
		
		record = null;
		if( rows != 0 ) // ain't that beautiful! 
			record = (Record)((Object[])(db.more(results, 0, 0)[0]))[0];
		
		db.closeQuery( query );
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
		logger.debug("INSERTING");
		
		// Is this part of the record from an immutable table?
		boolean immutable = user.isAdmin() ?
				Record.IMMUTABLE.contains( record.getClass() ) :
				record instanceof Plant;
		
		// This part of the record is from an immutable table -
		// try to find it in the table.
		if( immutable ) {
			logger.debug("Processing an immutable table "+record.getClass().getSimpleName());
			Record counterpart = findMatchInDB( record );
			if( counterpart == null ) {
				logger.warn("The counterpart for the record (in the immutable table " +
						record.getClass().getSimpleName()	+ ") was not found!");
				throw new ImportException(L10n.getString("errorNotInAnImmutableTable"), record);
			}
			return counterpart;
		} 
		// The part of the record is from a common table.
		else {
			logger.debug("Processing a common table.");
			// Insert all of its sub-records.
			ArrayList<String> keys = record.getForeignKeys();
			// Inserting a new AuthorOccurrence MUSTN'T cause the insertion of the Occurrence.
			if(record instanceof AuthorOccurrence)
				keys.remove(AuthorOccurrence.OCCURRENCE); 
			for(String key : keys)
				record.setValue( key, insert( (Record)record.getValue(key) ) );
			
			Record counterpart = null;
			
			// If the record is the Occurrence itself, update some additional columns.
			if( record instanceof Occurrence ) {
				Occurrence occ = (Occurrence) record;
				occ.setCreatedWhen(now.getTime());
				occ.setCreatedWho(user);
				occ.setUpdatedWhen(now.getTime());
				occ.setUpdatedWho(user);
			}
			// Try to find its counterpart (if it is in the database already).
			// (The Habitat table is special, the relationship Occ->Habitat should always be 1:1.)
			else if( !(record instanceof Habitat) ) {
				counterpart = findMatchInDB( record );
			}
			
			// The record is not in the database.
			if(counterpart == null) {
				logger.debug("The record is not in the database. It will be inserted.");
				// Insert it!
				Integer newId = db.executeInsert(record);
				record.setId( newId );
				logger.info("Inserting into " + record.getClass().getSimpleName() + " takes place. " +
						"The inserted record is ID = " + newId);
				return record;
			}
			// The record is in the database.
			else {
				logger.debug("The record is in the database already (ID = " +
						counterpart.getId() + "). It will be used.");
				// Do not insert anything, use that record instead.
				return counterpart;
			}
		}
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
		logger.debug("UPDATING");
		
		boolean immutable = user.isAdmin() ?
				Record.IMMUTABLE.contains( current.getClass() ) :
				current instanceof Plant;
		/*
		 * We have an immutable table here - 
		 * therefore the replacement must match something 
		 * that is in the database already.
		 */
		if( immutable ) {
			logger.debug("Processing an immutable table "+current.getClass().getSimpleName());
			// Don't they happen to be the same?
			if( doPropertiesMatch(current, replacement) )
				return current;
			// Try to find that record in the database.
			Record counterpart = findMatchInDB( replacement );
			if( counterpart == null ) {
				logger.warn("The counterpart for the record (in the immutable table " +
						current.getClass().getSimpleName()	+ ") was not found!");
				throw new ImportException(L10n.getString("errorNotInAnImmutableTable"), replacement);
			}
			return counterpart;
		}
		/*
		 * It is a little bit trickier now, because UPDATE may sometimes in fact
		 * mean INSERT or nothing :). 
		 */
		else {
			ArrayList<String> keys = replacement.getForeignKeys();
			boolean propertiesMatch = doPropertiesMatch(current, replacement);
			
			logger.debug("Updating a record from a common table.");
			
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
				
				// This is up to the User.
				insertUpdateDecision = lastDecision;
				if(!useLastDecision) // ASK THE USER!
					insertUpdateDecision = expectDecision( replacement );
				
				if( insertUpdateDecision == Action.UPDATE ) { // update the current record
					logger.debug("Updating the current record...");
					// Replace the values with new ones - fortunately, there are no FK involved.
					current.replaceWith( replacement );
					db.executeUpdate( current );
					return current;
				}
				else /*if( decision == Action.INSERT )*/ {
					logger.debug("Inserting a new record...");
					// Insert the replacement as a new record [DEFAULT OPERATION].
					Integer newId = db.executeInsert(replacement);
					replacement.setId( newId );
					return replacement;
				}
			}
			// [B] There are some foreign keys.
			// (Habitat, Occurrence)
			else {
				logger.debug("Updating a record from a table with Foreign Keys.");
				
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
						throw new ImportException(L10n.getString("errorFKCannotBeNull"));
					
					Record 
						suggestion =  update( (Record)currentSubrecord, (Record)replacementSubrecord );
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
					if( current instanceof Occurrence ) {
						// Set some additional information.
						Occurrence occ = (Occurrence) current;
						occ.setUpdatedWhen(now.getTime());
						occ.setUpdatedWho(user);
					}
					logger.debug("Updating the current record.");
					db.executeUpdate(current);
				}
				// Return the current record (updated).
				return current;
			}
		}
	}
	
	/**
	 * Compare two records <code>a</code> and <code>b</code>.
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
	 * 
	 * @param record	The record that will be deleted. Do not forget this record has
	 * to belong to the database layer (ie. it must be something previously
	 * obtained directly from the database layer).
	 */
	public Deletable delete(Deletable record) 
	throws RemoteException, DBLayerException {
		record.setDeleted(1);
		if( record instanceof Occurrence ) {
			Occurrence occ = (Occurrence) record;
			occ.setUpdatedWhen(now.getTime());
			occ.setUpdatedWho(user);
		}
		db.executeUpdate( record );
		return record;
	}
	
	
	/**
	 * Abort the export immediately.
	 */
	public void abort() {
		aborted = true;
		logger.info("Import aborted!");
	}
	
}
