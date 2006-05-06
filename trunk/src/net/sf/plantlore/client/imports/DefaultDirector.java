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
import net.sf.plantlore.common.exception.ExportException;
import net.sf.plantlore.common.exception.ImportException;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.client.imports.Parser.Action;



public class DefaultDirector extends Observable implements Runnable {
	
	/**
	 * The parameter sent to the Observers when a decision has to be made
	 * (ie. the User must intervene because the import procedure is unsure
	 * what to do with the current record).
	 */
	public static Object DECISION_EXPECTED = new Object();
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());

	private Parser parser;
	private DBLayer db;
	private int count, imported;
	private Action lastDecision = Action.UNKNOWN;
	
	private boolean aborted = false, isAdmin = false;
	private User user;
	private Calendar now = Calendar.getInstance();

	
	public DefaultDirector(DBLayer db, Parser parser, User user) 
	throws ImportException {
		setDatabase(db);
		setParser(parser);
		setUser(user);
	}
	
	
	protected void setDatabase(DBLayer db) 
	throws ImportException {
		if(db == null) {
			logger.error("The database layer is null!");
			throw new ImportException("The database layer cannot be null!");
		}
		this.db = db;
	}
	
	protected void setParser(Parser parser) 
	throws ImportException {
		if(parser == null) {
			logger.error("The Parser is null!");
			throw new ImportException("The Parser cannot be null!");
		}
		this.parser = parser;
	}
	
	protected void setUser(User user) 
	throws ImportException {
		if(user == null) {
			logger.error("The User is null!");
			throw new ImportException("The User cannot be null!");
		}
		this.user = user;
		
	}
	
	
	synchronized public void makeDecision(Action decision) {
		this.lastDecision = decision;
		notify();
	}
	
	
	synchronized protected Action expectDecision() {
		lastDecision = Action.UNKNOWN;
		setChanged(); 
		// TODO: Mozna bude muset volat jine vlakno, protoze by to mohlo udelat DEADLOCK na synchronized metodach.
		notifyObservers( DECISION_EXPECTED );
		
		while( lastDecision == Action.UNKNOWN && !aborted ) {
			try {
				wait();
			} catch( InterruptedException e ) {}
		}
		return lastDecision;
	}
	
	
	
	public void run() {
		
		try {
			count = imported = 0;
			
			while( !aborted && parser.hasNext() ) {
				Occurrence occ = (Occurrence) parser.next();
				Action action = parser.intentedFor();
				count++;
				boolean isValid = 
					(action == Action.DELETE) ? 
							occ.getUnitIdDb() != null && occ.getUnitValue() != null : 
								occ.areAllNNSet();
				
				if( !isValid ) 
					// TODO: Let the User know the record was not valid ?
					continue; 
				
				/*
				 * Try to find this Occurrence record in the database.
				 */
				SelectQuery q = db.createQuery(Occurrence.class);
				q.addRestriction(RESTR_EQ, Occurrence.UNITIDDB, null, occ.getUnitIdDb(), null);
				q.addRestriction(RESTR_EQ, Occurrence.UNITVALUE, null, occ.getUnitValue(), null);
				int resultId = db.executeQuery( q ), rows = db.getNumRows( resultId );
				boolean isInDB = (rows != 0);
				if(rows > 1)
					logger.error("The database is not in a consistent state - there are " + rows + " Occurrence " +
							"record with the same Unique Identifier ("+occ.getUnitIdDb()+"-"+occ.getUnitValue()+")!");
				Occurrence
				occInDB = isInDB ? (Occurrence)((Object[])db.more(resultId, 1, 1)[0])[0]  :  null;
				db.closeQuery(q); resultId = -1;
				boolean isDead = isInDB ? occInDB.isDead() : false;
				
				/*
				 * Time check. Inform the User that he may want to make changes to 
				 * a newer record.
				 */
				if( isInDB ) {
					Date 
						updateInDBOccurred = occInDB.getUpdatedWhen(),
						updateInFile = occ.getUpdatedWhen();
					if(updateInDBOccurred.after(updateInFile))
						// TODO: Problem here, the User must intervene - the record in the database is NEWER than the one we are importing!
						;
				}
				
				/*
				 * The `occ` which comes from the external file
				 * IS in the database as `occInDB`.  
				 */
				if( isInDB ) {
					if( isDead )
						switch(action) {
						case DELETE:
							// Nothing to be done.
							break;
						case UNKNOWN:
						case INSERT:
						case UPDATE:
							revive( occInDB );
							update( occInDB, occ );
							break;
						}
					else
						switch(action) {
						case DELETE:
							delete( occInDB );
							break;
						case UNKNOWN:
						case INSERT:
						case UPDATE:
							update( occInDB, occ );
							break;
						}
				}
				/*
				 * The `occ` which comes from the external file
				 * is NOT in the database. 
				 */
				else 
					switch(action) {
					case DELETE:	
						// Nothing to be done.
						break;
					case UNKNOWN:
					case INSERT:
					case UPDATE:
						insert( occ );
						break;
					}
			}
			
			imported++;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		/*
		 * Now, deal with Authors!
		 */
		
	}
	
	
	/**
	 * Try to find a record, that has exactly the same properties
	 * and foreign keys.
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
		if( rows > 1 ) 
			logger.warn("Weird! There are two (or more) " +
					"identical records in the table of " + table.getSimpleName() );
		
		record = null;
		if( rows != 0 ) // ain't that beautiful! 
			record = (Record)((Object[])(db.more(results, 0, 0)[0]))[0];
		
		db.closeQuery( query );
		return record;
	}
	
	/**
	 * Insert.
	 * 
	 * @param record
	 * @throws RemoteException
	 * @throws DBLayerException
	 */
	public Record insert(Record record) 
	throws RemoteException, DBLayerException, ImportException {
		boolean immutable = isAdmin ?
				Record.IMMUTABLE.contains( record.getClass() ) :
				record instanceof Plant;
		
		if( immutable ) {
			Record counterpart = findMatchInDB( record );
			if( counterpart == null )
				throw new ImportException("The record is not in the immutable table!");
			return counterpart;
		} 
		else {
			for(String key : record.getForeignKeys())
				record.setValue( key, insert( (Record)record.getValue(key) ) );
			
			if( record instanceof Occurrence ) {
				Occurrence occ = (Occurrence) record;
				occ.setCreatedWhen(now.getTime());
				occ.setCreatedWho(user);
				occ.setUpdatedWhen(now.getTime());
				occ.setUpdatedWho(user);
			}
			
			db.executeInsert(record);			
		}
		
		
		return null;
	}
	
	/**
	 * Update the <code>current</code> record from the database 
	 * according to the other <code>record</code>.
	 * <br/> 
	 * The source record is
	 * always kept up-to-date and its members always belong to the database.
	 * 
	 * @param current	The source record - the record from the database that needs to be updated.
	 * @param replacement	The record containing changes the <code>current</code> record must undergo.
	 * @return The <code>current</code> record updated using the <code>replacement</code>.
	 */
	public Record update(Record current, Record replacement) 
	throws RemoteException, DBLayerException, ImportException {
		boolean immutable = isAdmin ?
				Record.IMMUTABLE.contains( current.getClass() ) :
				current instanceof Plant;
		/*
		 * We have an immutable table here - 
		 * it must match something in the database.
		 * (Plant, Phytochorion, Territory, Village, Metadata). 
		 */
		if( immutable ) {
			// Don't they happen to be the same?
			if( propertiesMatch(current, replacement) )
				return current;
			// Try to find that record in the database.
			Record counterpart = findMatchInDB( replacement );
			if( counterpart == null )
				throw new ImportException("The record is not in the immutable table!");
			return counterpart;
		}
		/*
		 * It is a little bit trickier now.  
		 */
		else {
			ArrayList<String> keys = replacement.getForeignKeys();
			boolean propertiesMatch = propertiesMatch(current, replacement);
			
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
				// We must insert the record into the database 
				//    OR
				// update the existing one.
				// This is up to the User.
				Action decision = expectDecision();
				if( decision == Action.UPDATE ) { // update the current record
					current.replaceWith( replacement );
					db.executeUpdate( current );
					return current;
				}
				else /*if( decision == Action.INSERT )*/ {
					Integer newId = db.executeInsert(replacement);
					replacement.setId( newId );
					return replacement;
				}
			}
			// [B] There are some foreign keys.
			// (Habitat, Occurrence)
			else {
				boolean dirty = false;
				// Replace all foreign keys with records
				// that already are in the database.
				for(String key : keys) {
					Object 
						currentSubrecord = current.getValue(key),
						replacementSubrecord = replacement.getValue(key);
					if(currentSubrecord == null || replacementSubrecord == null)
						throw new ImportException("Foreign keys cannot be null!");
					Record 
						suggestion =  update( (Record)currentSubrecord, (Record)replacementSubrecord );
					// The sub-record doesn't have to change.
					if( currentSubrecord == suggestion ) // == suffices (there's no need for equals()).
						continue;
					
					current.setValue(key, suggestion);
					dirty = true;
				}
				
				if( !propertiesMatch )
					for(String property : current.getProperties()) 
						current.setValue(property, replacement.getValue(property));
				
				// Update the record in the database.
				if( dirty || !propertiesMatch ) {
					if( current instanceof Occurrence ) {
						Occurrence occ = (Occurrence) current;
						occ.setUpdatedWhen(now.getTime());
						occ.setUpdatedWho(user);
					}
					db.executeUpdate(current);
				}
				// Return the current record (updated).
				return current;
			}
		}
	}
	
	
	protected boolean propertiesMatch(Record a, Record b) {
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
	public void delete(Record record) 
	throws RemoteException, DBLayerException {
		if( record instanceof Occurrence ) {
			Occurrence occ = (Occurrence) record;
			occ.setDeleted(1);
			occ.setUpdatedWhen(now.getTime());
			occ.setUpdatedWho(user);
			db.executeUpdate( record );
		}
	}
	
	/**
	 * Undelete the specified record.
	 * (Technically: unmark the record as deleted = make it appear alive.)
	 * 
	 * @param record	The record that will be undeleted. Do not forget this record has
	 * to belong to the database layer (ie. it must be something previously
	 * obtained directly from the database layer).
	 */
	public void revive(Record record)
	throws RemoteException, DBLayerException {
		if( record instanceof Occurrence ) {
			Occurrence occ = (Occurrence) record;
			occ.setDeleted(0);
			occ.setUpdatedWhen(now.getTime());
			occ.setUpdatedWho(user);
			db.executeUpdate( record );
		}
	}

	
	
	/**
	 * Abort the export immediately.
	 */
	public void abort() {
		aborted = true;
	}
	
}
