/*
 * TempClass.java
 *
 * Created on 26. duben 2006, 10:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.common;

import static net.sf.plantlore.common.PlantloreConstants.RESTR_EQ;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Deletable;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.Village;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.exception.DBLayerException;

import org.apache.log4j.Logger;

/** 
 * Class offering convenience methods for DBLayer.
 * 
 * Every class requiring high-level work with the database 
 * should use these methods so as to unite the behaviour through the application. 
 *
 * 
 *
 * @author reimei
 * @author kaimu
 */
public class DBLayerUtils {
    private DBLayer db;
    private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
    private boolean isCacheEnabled = true;
    
    
    
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
    
    
    /** Creates a new instance of TempClass */
    public DBLayerUtils(DBLayer db) {
        this(db, true);
    }
    
    /**
     * @param db
     * @param isCacheEnabled	Gives you the possibility to switch off the cache 
     * (which is enabled by default), because it is highly likely it will not contain up-to-date data.
     */
    public DBLayerUtils(DBLayer db, boolean isCacheEnabled) {
    	this.db = db;
    	this.isCacheEnabled = isCacheEnabled;
    }
    
    /** Gets an object according to it's id.
     *
     * @param id id of the row
     * @param c class of the object
     * @return Record Object of type c with id id.
     * @return null in case an exception is thrown or no row with that id exists
     */
    public Record getObjectFor(int id, Class c) throws DBLayerException, RemoteException {
        logger.debug("Looking up "+c.getName()+" object in the database for id "+id);
        SelectQuery sq = db.createQuery(c);
        sq.addRestriction(PlantloreConstants.RESTR_EQ,"id",null,id,null);
        int resultid = db.executeQuery(sq);
        int resultCount = db.getNumRows(resultid);
        if (resultCount == 0)
            return null;
        Object[] results = db.more(resultid, 0, 0);
        Object[] tmp = (Object[]) results[0];
        db.closeQuery(sq);
        return (Record)tmp[0];
    }

    public AuthorOccurrence[] getAuthorsOf(Occurrence o) throws DBLayerException, RemoteException {
        AuthorOccurrence[] authorResults = null;
        SelectQuery sq = db.createQuery(AuthorOccurrence.class);        
        sq.addRestriction(PlantloreConstants.RESTR_EQ,AuthorOccurrence.OCCURRENCE,null,o,null);
        sq.addRestriction(PlantloreConstants.RESTR_EQ,AuthorOccurrence.DELETED,null,0,null);
        int resultid = db.executeQuery(sq);
        int resultCount = db.getNumRows(resultid);
        authorResults = new AuthorOccurrence[resultCount];
        Object[] results = db.more(resultid, 0, resultCount-1);
        Object[] tmp;
        AuthorOccurrence ao;
        for (int i = 0; i < resultCount; i++) {
            tmp = (Object[]) results[i];
            ao = (AuthorOccurrence)tmp[0];
            authorResults[i] = ao;
        }
        db.closeQuery(sq);
        return authorResults;
    }

    /** Deletes Habitat for given Occurrence if needed.
     *
     * Will delete Habitat if no live Occurrence point at it.
     *
     */
    public void deleteHabitat(Habitat h) throws DBLayerException, RemoteException {
        SelectQuery sq = db.createQuery(Occurrence.class);        
        sq.addRestriction(PlantloreConstants.RESTR_EQ,Occurrence.HABITAT,null,h,null);
        sq.addRestriction(PlantloreConstants.RESTR_EQ,Occurrence.DELETED, null, 0, null);
        int resultid = db.executeQuery(sq);
        int resultCount = db.getNumRows(resultid);
        if (resultCount == 0) {
            logger.info("Deleting habitat id="+h.getId()+" with nearest village "+h.getNearestVillage().getName());
            h.setDeleted(1);
            db.executeUpdate(h);
        } else {
            logger.debug("Leaving habitat id="+h.getId()+" live. Live Occurrence records point at it.");
        }
        db.closeQuery(sq);
    }
    
    /** Deletes Habitat for given Occurrence if needed.
     *
     * Will delete Habitat if no live Occurrence point at it.
     *
     */
    public void deleteHabitatInTransaction(Habitat h) throws DBLayerException, RemoteException {
        SelectQuery sq = db.createQuery(Occurrence.class);        
        sq.addRestriction(PlantloreConstants.RESTR_EQ,Occurrence.HABITAT,null,h,null);
        sq.addRestriction(PlantloreConstants.RESTR_EQ,Occurrence.DELETED, null, 0, null);
        int resultid = db.executeQuery(sq);
        int resultCount = db.getNumRows(resultid);
        if (resultCount == 0) {
            logger.info("Deleting habitat id="+h.getId()+" with nearest village "+h.getNearestVillage().getName());
            h.setDeleted(1);
            db.executeUpdateInTransaction(h);
        } else {
            logger.debug("Leaving habitat id="+h.getId()+" live. Live Occurrence records point at it.");
        }
        db.closeQuery(sq);
    }
    
//    public static void main(String[] args) throws DBLayerException, RemoteException {
//        DBLayer db = new HibernateDBLayer();
//        db.initialize("jdbc:firebirdsql:localhost/3050:/mnt/data/temp/plantloreHIBdataUTF.fdb","sysdba","masterkey");
//        DBLayerUtils dlu = new DBLayerUtils(db);
//        Author a = (Author)dlu.getObjectFor(1,Author.class);
//        System.out.println("class "+a.getWholeName());
//        Occurrence o = (Occurrence)dlu.getObjectFor(1,Occurrence.class);
//        System.out.println("nalez "+o.getPlant().getTaxon());
//    }
    
    
    
    
	/**
	 * Find out whether some records share the supplied <code>record</code>.
	 * 
	 * @param record	The record to be tested.
	 * @return	The number of records that share the supplied <code>record</code>. 
	 */
	public int sharedBy(Record record) 
	throws RemoteException {
		if(record.getId() == null)
			return 0;
		
		SelectQuery q = null;
		int rows = 0;
		try {
			Class parent = parentTable.get( record.getClass() );
			String column = parentColumn.get( record.getClass() );
			
			q = db.createQuery( parent );
			q.addRestriction(RESTR_EQ, column, null, record, null);
			int resultset = db.executeQuery(q); 
			rows = db.getNumRows(resultset);
		} catch (DBLayerException e) {
			//e.printStackTrace();
		} finally {
			if(q != null)
				db.closeQuery(q);
		}
		return rows;
	}
    
    
	/**
	 * Find all AuthorOccurrences that share the specified Occurrence.
	 * 
	 * @param occurrence	The instance of some record
	 * @return	All AuthorOccurrences refering to the supplied Occurrence.
	 */
	public AuthorOccurrence[] findAllAuthors(Occurrence occurrence) 
	throws RemoteException, DBLayerException {
		SelectQuery q = null;
		AuthorOccurrence[] sharers = new AuthorOccurrence[0];
		try {
			q = db.createQuery(AuthorOccurrence.class);
			q.addRestriction(RESTR_EQ, AuthorOccurrence.OCCURRENCE, null, occurrence, null);
			int resultset = db.executeQuery(q),
			rows = db.getNumRows(resultset);
			if(rows > 0) {
				sharers = new AuthorOccurrence[rows];
				Object[] pulp = db.more(resultset, 0, rows - 1);
				for( int i = 0; i < rows; i++ )
					sharers[i] = ( (AuthorOccurrence)(  (Object[])pulp[i]  )[0] );
			}
		} finally {
			if( q != null )
				db.closeQuery( q );
		}
		return sharers;
	}
	
	
	/**
	 * A simple cache of records (one for each table).
	 * It is higly likely, due to the properties of Export,
	 * that records sharing the same subrecords will come together.
	 * <br/>
	 * For example:
	 * There are occurrences <i>A</i> and <i>B</i>. The <i>B</i>
	 * goes right after <i>A</i>.
	 * The probability, that both occurrences will share the same
	 * Phytochorion, Territory, Village, Publication, or Metadata,
	 * is quite high. 
	 * <br/>
	 * However, if the cache is not used for some time, its contents may
	 * be obsolete. It is extremely convenient for Import, 
	 * because it may save up to 5 select queries per one Occurrence record 
	 */
	private Hashtable<Class, Record> cache = new Hashtable<Class, Record>(50);
	
	
	/**
	 * Try to find a record, that has exactly the same <b>properties</b>
	 * and <b>foreign keys</b>. The ID of the record doesn't matter.
	 * <br/>
	 * This may come in handy if you encounter a record and you need to know
	 * whether it is already in the database or not.
	 * 
	 * @param record	The record we are looking for.
	 * @return	The matching record (counterpart) from the database, or null if no such record exists.
	 */
	public Record findMatchInDB(Record record) 
	throws RemoteException, DBLayerException {
		if(record == null) return null;
		// Get the table.
		Class table = record.getClass();
		
		// Look in the cache.
		if(isCacheEnabled) {
			Record cachedRecord = cache.get(table);
			if( cachedRecord != null && record.equals(cachedRecord))
				return cachedRecord; // hooray, one select has been saved!
		}
				
		// Create a query that will look for the record with the same properties.
		SelectQuery query = db.createQuery( table );

		// Equal properties.
		for(String property : record.getProperties()) {
			Object value = record.getValue(property);
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
		if( rows > 1 ) 
			logger.info("There are " + rows + 
					" completely identical records in the " + table.getSimpleName() + " table!");
		
		record = null;
		if( rows != 0 ) 
			record = (Record)((Object[])(db.more(results, 0, 0)[0]))[0]; // Well, THIS is ugly!
		
		db.closeQuery( query );
		
		// Update the cache appropriately - store the record for future generations.
		if( record != null && isCacheEnabled ) {
			cache.remove(table);
			cache.put(table, record);
		}
		
		
		return record;
	}
	
	
	
	/**
	 * Insert a new record into the database.
	 * The inserted record is inserted with all of its parts (subrecords)
	 * with one exception:
	 * if the inserted record is an <b>AuthorOccurrence</b>, 
	 * its Occurrence subrecord will <b>not</b> be inserted.
	 * <br/>
	 * Some parts of the record (subrecords) may be joined with suitable
	 * counterparts that already are in the database (for instance: Habitat, Metadata, ...).
	 * Some tables cannot be altered (so called immutable tables) and if a proper counterpart
	 * does not exist, the insert will fail. This behaviour prevents the User from damaging 
	 * tables containing data, that must remain intact, such as Plants or Villages. 
	 * 
	 * @param record	The record to be inserted (including all of its subrecords).
 	 * @return The inserted record with its new ID set.
	 */
	public Record insert(Record record) 
	throws RemoteException, DBLayerException {
		logger.debug("Inserting ["+record+"] into the database.");
		
		// Is this part of the record from an immutable table?
		boolean immutable = Record.IMMUTABLE.contains( record.getClass() ) ;
		
		// This part of the record is from an immutable table -
		// try to find it in the database.
		if( immutable ) {
			logger.debug("The record belongs to an immutable table "+record.getClass().getSimpleName());
			Record counterpart = findMatchInDB( record );
			if( counterpart == null ) {
				logger.fatal("The counterpart for the record (in the immutable table " +
						record.getClass().getSimpleName()	+ ") was not found!");
				throw new DBLayerException( L10n.getString("Error.RecordNotFound") );
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
			// The subrecord must be replaced with the counterpart from the database!
			record.setValue( key, insert( (Record)record.getValue(key) ) ); 
		
		Record counterpart = null;
		
		// Try to find the record's counterpart (i.e. if it is in the database already),
		// in order to reuse that record, that is already in the database. 
		counterpart = findMatchInDB( record );
		
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
	
	
	
//	/**
//	 * THIS MUST BE REVISED!!! 
//	 * 
//	 * Update the <code>current</code> record (which is in the database) 
//	 * according to the other <code>record</code>.
//	 * <br/> 
//	 * The <code>source</code> record is
//	 * always kept up-to-date and its 
//	 * members always belong to the database.
//	 * <br/>
//	 * It is possible that nothing will be inserted or updated at all 
//	 * (if everything is up-to-date).
//	 * 
//	 * @param current	The source record - the record from the database that needs to be updated.
//	 * @param replacement	The record containing changes the <code>current</code> record must undergo.
//	 * @return The <code>current</code> record updated using the <code>replacement</code>.
//	 * This record is always in the database already.
//	 */
//	public Record update(Record current, Record replacement) 
//	throws RemoteException, DBLayerException, ImportException {
//		
//		return update(current, replacement, null, null);
//	}
//
//	
//	private Record update(Record current, Record replacement, Class father, String foreignKey) 
//	throws RemoteException, DBLayerException, ImportException {		
//		logger.debug("Updating ["+current+"] with ["+replacement+"].");
//		
//		boolean immutable = Record.IMMUTABLE.contains( current.getClass() ) ;
//		/*
//		 * We have an immutable table here - 
//		 * therefore the replacement must match something 
//		 * that is in the database already.
//		 */
//		if( immutable ) {
//			logger.debug("The record belongs to an immutable table "+current.getClass().getSimpleName());
//			// Don't they happen to be the same?
//			if( doPropertiesMatch(current, replacement) )
//				return current;
//			// Try to find that record in the database.
//			Record counterpart = findMatchInDB( replacement );
//			if( counterpart == null ) {
//				logger.fatal("The counterpart for the record (in the immutable table " +
//						current.getClass().getSimpleName()	+ ") was not found!");
//				throw new ImportException(L10n.getString("Error.RecordNotFound"), replacement);
//			}
//			return counterpart;
//		}
//		
//		/*
//		 * It is a little bit trickier now, because UPDATE may sometimes in fact
//		 * mean INSERT or nothing :). 
//		 */
//		else {
//			List<String> keys = replacement.getForeignKeys();
//			boolean propertiesMatch = doPropertiesMatch(current, replacement);
//			
//			logger.debug("The record belongs to a common table "+current.getClass().getSimpleName());
//			
//			// [A] There are no foreign keys.
//			// (Publication)
//			if( keys.size() == 0 ) {
//				// Both records have the same properties.
//				if( propertiesMatch )
//					return current;
//				// Try to find a match in the database.
//				Record counterpart = findMatchInDB( replacement );
//				// A match has been found - use it.
//				if(counterpart != null)
//					return counterpart;
//				// There is no match in the table.
//				// I.e. the `replacement` is not in the table = 
//				// the record we want `current` to be transformed to was not found.
//				
//				// There are two options now.
//				//    EITHER
//				// we insert the record into the database causing no damage to other records 
//				//    OR
//				// update the existing one risking that it will affect some other records
//				// that share the `current`.
//				
////				if( sharedBy(current, father, foreignKey) > 1 ) {
////					// This is up to the User.
////					logger.info("The record ["+current+"] is shared!");
////					insertUpdateDecision = lastDecision;
////					if(!useLastDecision) // ASK THE USER!
////						insertUpdateDecision = expectDecision( replacement );
////				}
////				else
////					insertUpdateDecision = Action.UPDATE;
////				
////				if( insertUpdateDecision == Action.UPDATE ) { // update the current record
////					logger.debug("Updating the current record.");
////					// Replace the values with new ones - fortunately, there are no FK involved.
////					current.replaceWith( replacement );
////					db.executeUpdateInTransaction( current );
////					return current;
////				}
////				else /*if( decision == Action.INSERT )*/ {
//					logger.debug("Inserting a new record.");
//					// Insert the replacement as a new record [DEFAULT OPERATION].
//					Integer newId = db.executeInsertInTransaction(replacement);
//					replacement.setId( newId );
//					return replacement;
////				}
//			}
//			// [B] There are some foreign keys.
//			// (Habitat, Occurrence)
//			else {
//				logger.debug("The common table contains foreign keys.");
//				
//				// Indicate, whether the record needed some changes.
//				boolean dirty = false;
//				// Deal with the AuthorOccurence - 
//				// a new AuthorOccurrence MUSTN'T cause an update of the Occurrence.
//				if(current instanceof AuthorOccurrence)
//					keys.remove(AuthorOccurrence.OCCURRENCE);
//				// Replace all foreign keys with records that already are in the database.
//				for(String key : keys) {
//					Object 
//					currentSubrecord = current.getValue(key),
//					replacementSubrecord = replacement.getValue(key);
//					if(currentSubrecord == null || replacementSubrecord == null)
//						throw new ImportException(L10n.getString("Error.FKIsNull"));
//					
//					Record 
//					suggestion =  update( 
//							(Record)currentSubrecord, 
//							(Record)replacementSubrecord,
//							current.getClass(), key);
//					
//					// The sub-record doesn't have to be changed.
//					if( currentSubrecord == suggestion ) // == suffices (there's no need for equals()).
//						continue;
//					
//					// The replacement is needed.
//					current.setValue(key, suggestion);
//					dirty = true;
//				}
//				
//				// Replace the properties of the `current` with the ones of the `replacement`.
//				if( !propertiesMatch )
//					for(String property : current.getProperties()) 
//						current.setValue(property, replacement.getValue(property));
//				
//				// Update the record in the database.
//				if( dirty || !propertiesMatch ) {
//					logger.debug("Updating the current record.");
//					// Occurrences are always UPDATED
//					if( current instanceof Occurrence )
//						db.executeUpdateInTransaction(current);
//					else {
//						boolean shared = sharedBy(current, father, foreignKey) > 1;
//						// If the record is not shared, it is safe to performt he udpate.
//						if( !shared ) 
//							db.executeUpdateInTransaction(current);
//						else {
////							// If the record is shared.
////							insertUpdateDecision = lastDecision;
////							if(!useLastDecision) 
////								insertUpdateDecision = expectDecision( replacement );
////							if(insertUpdateDecision == Action.UPDATE) 
////								// User decided to update (potentially dangerous).
////								db.executeUpdateInTransaction(current);
////							else {
//								// User decided to insert new copy (safer).
//								Integer newId = db.executeInsertInTransaction(current);
//								current.setId(newId);
////							}
//						}
//					}
//				}
//				// Return the current record (updated).
//				return current;
//			}
//		}
//	}
	
	/**
	 * Delete the specified record properly.
	 * Do not forget the record must be in the database! 
	 * <br/>
	 * Some records must be deleted very carefully 
	 * in order to be able to "revive" them using the Undo operation, 
	 * while other records must be deleted directly from the table without
	 * creating a record in the history. 
	 * 
	 * @param record	The record that will be deleted. 
	 * @return Either the record, if it has been marked as deleted (but physically remains in the database), 
	 * or null, if the record was deleted from the database for good and is no longer available.
	 */
	public Record delete(Record record) 
	throws RemoteException, DBLayerException {
		logger.info("Deleting [" + record + "] from the database.");
		// Deletable records are records that must be marked as deleted. 
		// They will appear to be dead and the User can revive them later (using Undo).
		if(record instanceof Deletable) {
			((Deletable)record).setDeleted( 1 );
			db.executeUpdateInTransaction( record );
		} 
		// Other recods, mostly from the immutable tables, must be removed from the database
		// for good. It is the only way how to get rid of them.
		// Note that if such record is shared, it cannot be deleted - other records may refer to it!
		else if(record.getId() != null) {
			int sharers = sharedBy( record );
			if( sharers > 0 ) {
				logger.error("The "+record+" is in use by "+sharers+" other records. It cannot be deleted!");
				throw new DBLayerException(L10n.getFormattedString("Error.DeletingSharedRecord", record, sharers));
			}
			db.executeDeleteHistory( record );
			record = null;
		}
		return record;
	}
    
}
