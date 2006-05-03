package net.sf.plantlore.client.imports;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;

import org.apache.log4j.Logger;

import static net.sf.plantlore.common.PlantloreConstants.RESTR_EQ;
import static net.sf.plantlore.common.PlantloreConstants.RESTR_IS_NULL;
import net.sf.plantlore.server.DBLayerException;
import net.sf.plantlore.common.exception.ImportException;
import net.sf.plantlore.common.record.*;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.client.imports.Parser.Action;
import net.sf.plantlore.client.export.Template;

public class DefaultDirector extends Observable implements Runnable {
	
	
	public static Object DECISION_EXPECTED = new Object();
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());

	private Parser parser;
	private DBLayer db;
	
	private int count;
	
	private Action lastDecision = Action.UNKNOWN;
	
	private boolean aborted = false;
	
	
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
			count = 0;
			
			while( parser.hasNext() ) {
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
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		/*
		 * Now, deal with Authors!
		 */
		
	}
	
	
	/**
	 * Try to find a record, that has exactly the same properties.
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
			Object value = Template.get(record, property);
			if( value == null ) // use the database null
				query.addRestriction(RESTR_EQ, property, null, RESTR_IS_NULL, null);
			else
				query.addRestriction(RESTR_EQ, property, null, value, null);
		}
		// Equal foreign keys.
		for(String key : record.getForeignKeys() ) {
			Record subrecord = (Record) Template.get(record, key);
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
	
	
	public void insert(Record record) 
	throws RemoteException, DBLayerException {
		
	}
	
	/**
	 * Update the <code>current</code> record from the database 
	 * according to the other <code>record</code>.
	 * 
	 * @param current	The source record - the record from the database that needs to be updated.
	 * @param replacement	The record containing changes the <code>current</code> record must undergo.
	 * @return The <code>current</code> record updated using the <code>replacement</code>.
	 */
	public Record update(Record current, Record replacement) 
	throws RemoteException, DBLayerException, ImportException {
		boolean immutable = Template.IMMUTABLE.contains( current.getClass() );
		/*
		 * We have an immutable table here - 
		 * it must match something in the database.
		 * (Plant, Phytochorion, Territory, Village). 
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
			// (Metadata, Publication)
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
				if( decision == Action.UPDATE ) {
					replacement.setId( current.getId() );
					db.executeUpdate( replacement );
				}
				else if( decision == Action.INSERT ){
					Integer newId = db.executeInsert(replacement);
					replacement.setId( newId );
				}
				// Replacement is in both cases in the database.
				return replacement;
			}
			// [B] There are some foreign keys.
			// (Habitat, Occurrence)
			else {
				boolean dirty = false;
				// Replace all foreign keys with records
				// that already are in the database.
				for(String key : keys) {
					Object 
						originalSubrecord = Template.get(current, key),
						replacementSubrecord = Template.get(replacement, key);
					if(originalSubrecord == null || replacementSubrecord == null)
						throw new ImportException("Foreign keys cannot be null!");
					Object suggestion = update( (Record)originalSubrecord, (Record)replacementSubrecord );
					// The sub-record doesn't have to change.
					if( originalSubrecord == suggestion ) // == suffices (there's no need for equals()).
						continue;
					
					dirty = true;
					// replacementSubrecord
					
				}
				// No changes were needed
				if( !dirty && propertiesMatch )
					return current;
					
				
				
			}
		}
		return null;
	}
	
	
	protected boolean propertiesMatch(Record a, Record b) {
		Class table = a.getClass();
		if( table != b.getClass() ) return false;
		for( String property : a.getProperties() ) { 
			Object 
			valueA = Template.get(a, property),
			valueB = Template.get(b, property);
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
			((Occurrence)record).setDeleted(1);
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
			((Occurrence)record).setDeleted(0);
			db.executeUpdate( record );
		}
	}

	
	
	
	
	public static void main(String[] args) {
	}

}
