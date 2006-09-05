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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Deletable;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.NearestVillage;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.PlantloreConstants.Intention;
import net.sf.plantlore.common.exception.DBLayerException;


import org.apache.log4j.Logger;

/** 
 * Class offering convenience methods for DBLayer.
 * 
 * Every class requiring high-level work with the database 
 * should use these methods so as to unite the behaviour throughout the application. 
 *
 * 
 *
 * @author Jakub Kotowski
 * @author Erik Kratochvíl
 */
public class DBLayerUtils {
    private DBLayer db;
    private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
    private boolean isCacheEnabled = true;
    
    private Action decisionCallback;
    private Intention intention; 
    
	private static Map<Class, Class> parentTable = new Hashtable<Class, Class>();
	private static Map<Class, String> parentColumn = new Hashtable<Class, String>();
	
	static {
		parentTable.put(NearestVillage.class, Habitat.class);
		parentTable.put(Phytochorion.class, Habitat.class);
		parentTable.put(Territory.class, Habitat.class);
		parentTable.put(Metadata.class, Occurrence.class);
		parentTable.put(Plant.class, Occurrence.class);
		parentTable.put(Habitat.class, Occurrence.class);
		parentTable.put(Author.class, AuthorOccurrence.class);
		// >>Occurrence<< MUST NOT BE LISTED HERE!
		// It would cause problems with DELETE and INSERT...

		parentColumn.put(NearestVillage.class, Habitat.NEARESTVILLAGE);
		parentColumn.put(Phytochorion.class, Habitat.PHYTOCHORION);
		parentColumn.put(Territory.class, Habitat.TERRITORY);
		parentColumn.put(Metadata.class, Occurrence.METADATA);
		parentColumn.put(Plant.class, Occurrence.PLANT);
		parentColumn.put(Habitat.class, Occurrence.HABITAT);
		parentColumn.put(Author.class, AuthorOccurrence.AUTHOR);
	}
    
    
    /**
     * Create a new Utils using the supplied Database layer.
     *   
     * @param db		The database layer that should be used to perform all the operations.
     */
    public DBLayerUtils(DBLayer db) {
        this(db, null, true);
    }
    
    /**
     * Create a new Utils using the supplied Database layer.
     * 
     * @param db		The database layer that should be used to perform all the operations.
     * @param isCacheEnabled	Gives you the possibility to switch off the cache 
     * (which is enabled by default), because it is highly likely it will not contain up-to-date data.
     */
    public DBLayerUtils(DBLayer db, Action decisionCallback, boolean isCacheEnabled) {
    	this.db = db;
    	this.isCacheEnabled = isCacheEnabled;
    	this.decisionCallback = decisionCallback;
    }
    
    
    /**
     * Processing of some records require the User's assistance. It this way
     * you can set the User's true intention with the problematic record.
     * 
     * @param intention	The true intention with the record as the User decided.
     * @see #expectDecision()
     */
    public void setIntention(Intention intention) {
    	this.intention = intention;
    	this.notify();
    }
    
    
    /**
     * There are some cases in which DBLayer utils needs assistance of the User.
     * This method gives you the time needed to contact and inform the User
     * and then pass His decision using the setIntention() method.
     * <br/>
     * The execution of the operation that required the assistance 
     * will be suspended until the setIntention() method is called.
     *
     * @see #setIntention(Intention) 
     */
    private Intention expectDecision() {
    	if(decisionCallback == null)
    		return intention = Intention.INSERT;
    	
    	intention = Intention.UNKNOWN;
    	new Thread() {
    		public void run() { decisionCallback.actionPerformed(null); }
    	}.start();
    	while( intention == Intention.UNKNOWN )
    		try { wait(); } catch(InterruptedException e) { /* Do nothing.*/ }
    		
    	return intention;
    }
    
    
    /** Gets an object according to it's id.
     *
     * @param id id of the row
     * @param c class of the object
     * @return Record Object of type c with id id.
     * @return null in case an exception is thrown or no row with that id exists
     */
    public Record getObjectFor(int id, Class c) throws DBLayerException, RemoteException {
            SelectQuery sq = db.createQuery(c);
            logger.debug("Looking up "+c.getName()+" object in the database for id "+id);
            try {
                sq.addRestriction(PlantloreConstants.RESTR_EQ,"id",null,id,null);
                int resultid = db.executeQuery(sq);
                int resultCount = db.getNumRows(resultid);
                if (resultCount == 0)
                    return null;
                Object[] results = db.more(resultid, 0, 0);
                Object[] tmp = (Object[]) results[0];
                db.closeQuery(sq);
                return (Record)tmp[0];
            } catch(DBLayerException ex) {
                if (sq != null) //clean up and propagate
                    db.closeQuery(sq);
                throw ex;
            }
    }

    /**
     * 
     * @param o
     * @return
     * @throws DBLayerException
     * @throws RemoteException
     * 
     * @see #findAllAuthors(Occurrence)
     */
    @Deprecated
    public AuthorOccurrence[] getAuthorsOf(Occurrence o) throws DBLayerException, RemoteException {
        AuthorOccurrence[] authorResults = null;
        SelectQuery sq = db.createQuery(AuthorOccurrence.class);        
        sq.addRestriction(PlantloreConstants.RESTR_EQ,AuthorOccurrence.OCCURRENCE,null,o,null);
        sq.addRestriction(PlantloreConstants.RESTR_EQ,Deletable.DELETED,null,0,null);
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
        sq.addRestriction(PlantloreConstants.RESTR_EQ,Deletable.DELETED, null, 0, null);
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
        sq.addRestriction(PlantloreConstants.RESTR_EQ,Deletable.DELETED, null, 0, null);
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
	 * Find out how many records (alive or not) share the supplied <code>record</code>.
	 *  
	 * @param record	The record in question.
	 * @return	The number of records that share the supplied <code>record</code>. 
	 */
    public int sharedBy(Record record)
    throws RemoteException, DBLayerException {
    	return sharedBy(record, true);
    }
    
	/**
	 * Find out how many records share the supplied <code>record</code>.
	 * 	 * 
	 * @param record	The record in question.
	 * @param aliveOnly	True if it should ommit records marked as deleted.
	 * @return	The number of records that share the supplied <code>record</code>. 
	 */
	public int sharedBy(Record record, boolean aliveOnly) 
	throws RemoteException, DBLayerException {
		if(record.getId() == null)
			return 0;
		
		SelectQuery q = null;
		int rows = 0;
		try {
			Class parent = parentTable.get( record.getClass() );
			String column = parentColumn.get( record.getClass() );
			// Records from this table do not have a parent table.
			if(parent == null || column == null)
				return 0;
			
			q = db.createQuery( parent );
			q.addRestriction(RESTR_EQ, column, null, record, null);
			if( aliveOnly && record instanceof Deletable )
				q.addRestriction(RESTR_EQ, Deletable.DELETED, null, 0, null);
			int resultset = db.executeQuery(q); 
			rows = db.getNumRows(resultset);
		} finally {
			if(q != null)
				db.closeQuery(q);
		}
		return rows;
	}
    
	/**
	 * Find all AuthorOccurrences that share the specified Occurrence.
	 * 
	 * @param occurrence	The instance of some record.
	 * @return	All AuthorOccurrences refering to the supplied Occurrence.
	 */	
	public AuthorOccurrence[] findAllAuthors(Occurrence occurrence)
	throws RemoteException, DBLayerException {
		return findAllAuthors(occurrence, true);
	}
    
	/**
	 * Find all AuthorOccurrences that share the specified Occurrence.
	 * 
	 * @param occurrence	The instance of some record.
	 * @param aliveOnly	Ommit records marked as deleted.
	 * @return	All AuthorOccurrences refering to the supplied Occurrence.
	 */
	public AuthorOccurrence[] findAllAuthors(Occurrence occurrence, boolean aliveOnly) 
	throws RemoteException, DBLayerException {
		SelectQuery q = null;
		AuthorOccurrence[] sharers = null;
		try {
			q = db.createQuery(AuthorOccurrence.class);
			q.addRestriction(RESTR_EQ, AuthorOccurrence.OCCURRENCE, null, occurrence, null);
			if( aliveOnly )
				q.addRestriction(RESTR_EQ, Deletable.DELETED, null, 0, null);
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
	 * Phytochorion, Territory, NearestVillage, Publication, or Metadata,
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
		
		logger.debug("Finding match for " + record.toFullString());
		
		// Look in the cache.
		if(isCacheEnabled) {
			Record cachedRecord = cache.get(table);
			if( cachedRecord != null && record.equals(cachedRecord))
				return cachedRecord; // hooray, one select has been saved!
		}
				
		// Create a query that will look for the record with the same properties.
		SelectQuery query = db.createQuery( table );
		
		//logger.debug("Table is " + table.getSimpleName());

		try {
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
			if( rows > 1 ) {
				logger.warn("There are " + rows + " completely identical records in the " + table.getSimpleName() + " table: " + record.toFullString());
				throw new DBLayerException(L10n.getString("Error.AmbiguousRecord"));
			}
			
			record = null;
			if( rows != 0 ) 
				record = (Record)((Object[])(db.more(results, 0, 0)[0]))[0]; 
		} finally {
			db.closeQuery( query );
		}
		
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
	public Record highLevelInsert(Record record) 
	throws RemoteException, DBLayerException {
		if( !db.beginTransaction() )
			throw new DBLayerException(L10n.getString("Error.TransactionRaceConditions"));
		try {
			Record r = insert( record );
			db.commitTransaction();
			return r;
		} catch( DBLayerException e ) {
			db.rollbackTransaction();
			throw e;
		} catch( RemoteException e ) {
			db.rollbackTransaction();
			throw e;
		}
	}
	
	private Record insert(Record record) 
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
				logger.fatal("The counterpart for the " + record.toFullString() + " (in the immutable table " +
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
			return db.executeInsertInTransaction(record);
		}
		
		// The record is in the database.
		logger.debug("The record is in the database already. It will be used.");
		// Do not insert anything, use that record instead.
		return counterpart;
		
	}
	
	/**
	 * Insert a record that belongs to an immutable table into the database.
	 * 
	 * @param record	The record to be inserted.
	 * @return	The inserted record.
	 */
	public Record insertImmutableRecord(Record record)
	throws RemoteException, DBLayerException {
		if( !Record.IMMUTABLE.contains(record) )
			throw new IllegalArgumentException(L10n.getString("Error.ImproperUse"));
		
		Record recordInDB = findMatchInDB( record );
		if( recordInDB != null)
			return recordInDB;
		
		return db.executeInsertHistory( record );
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
	public Record highLeveUpdate(Record current, Record replacement) 
	throws RemoteException, DBLayerException {
		if( !db.beginTransaction() )
			throw new DBLayerException(L10n.getString("Error.TransactionRaceConditions"));
		try {
			Record r = update(current, replacement);
			db.commitTransaction();
			return r;
		} catch( DBLayerException e ) {
			db.rollbackTransaction();
			throw e;
		} catch( RemoteException e ) {
			db.rollbackTransaction();
			throw e;
		}
	}

	
	private Record update(Record current, Record replacement) 
	throws RemoteException, DBLayerException{		
		logger.debug("Updating ["+current+"] with ["+replacement+"].");
		
		boolean 
			immutable = Record.IMMUTABLE.contains( current.getClass() ),
			propertiesMatch = current.equalsInProperties( replacement );
		
		/*
		 * We have an immutable table here - 
		 * therefore the replacement must match something 
		 * that is in the database already.
		 */
		if( immutable ) {
			logger.debug("The record belongs to an immutable table "+current.getClass().getSimpleName());
			// Don't they happen to be the same?
			if( propertiesMatch )
				return current;
			// Try to find that record in the database.
			Record counterpart = findMatchInDB( replacement );
			if( counterpart == null ) {
				logger.fatal("The counterpart for the record (in the immutable table " +
						current.getClass().getSimpleName()	+ ") was not found!");
				throw new DBLayerException(L10n.getFormattedString("Error.RecordNotFound", replacement.getClass()) );
			}
			return counterpart;
		}
		
		/*
		 * It is a little bit trickier now, because UPDATE may sometimes in fact
		 * mean INSERT or nothing :). 
		 */
		logger.debug("The record belongs to a common table "+current.getClass().getSimpleName());
		List<String> keys = replacement.getForeignKeys();
			
			
		// Indicate, whether the record needed some changes.
		boolean dirty = !propertiesMatch;
				
		// Deal with the AuthorOccurence - 
		// a new AuthorOccurrence MUSTN'T cause an update of the Occurrence.
		if(current instanceof AuthorOccurrence)
			keys.remove(AuthorOccurrence.OCCURRENCE);
		
		// Replace all foreign keys with records that already are in the database
		// (where possible).
		if( !keys.isEmpty() ) {
			for(String key : keys) {
				Object 
					currentSubrecord = current.getValue(key),
					replacementSubrecord = replacement.getValue(key);
				if(currentSubrecord == null || replacementSubrecord == null)
					throw new DBLayerException(L10n.getString("Error.FKIsNull"));
				
				Record suggestion =  update( 
						(Record)currentSubrecord, 
						(Record)replacementSubrecord 
				);
				
				// The sub-record doesn't have to be changed.
				if( currentSubrecord == suggestion ) // == suffices (there's no need for equals()).
					continue;
				
				// The replacement is needed.
				current.setValue(key, suggestion);
				dirty = true;
			}
		}
				
		// Replace the properties of the `current` with the ones of the `replacement`.
		if( !propertiesMatch )
			for(String property : current.getProperties()) 
				current.setValue(property, replacement.getValue(property));
		
				
		// Finally, update the record in the database.
		if( dirty ) {
			logger.debug("Updating the current record.");
			
			// Occurrences are always UPDATED
			if( current instanceof Occurrence )
				db.executeUpdateInTransaction(current);
			
			else {
				// If the record is not shared, it is safe to perform the udpate.
				if( sharedBy(current) <= 1 ) 
					db.executeUpdateInTransaction(current);
				// The record is shared - the assistance of a "supreme authority" is desired.
				else {
					if( expectDecision() == Intention.UPDATE) 
						// User decided to update (potentially dangerous).
						db.executeUpdateInTransaction(current);
					else {
						// User decided to insert new copy (safer).
						current = db.executeInsertInTransaction(current);
					}
				}
			}
		}
		// Return the current record (updated).
		return current;
	}
	
	/**
	 * Replace a record that belongs to an immutable table by another record.
	 * 
	 * @param current	The record to be replaced.
	 * @param replacement	The replacement of the current record.
	 * @return	The replacement.
	 */
	public Record updateImmutableRecord(Record current, Record replacement) 
	throws RemoteException, DBLayerException {
		if( !Record.IMMUTABLE.contains(current) ||
				replacement.getClass() != current.getClass()
		)
			throw new IllegalArgumentException(L10n.getString("Error.ImproperUse"));
		
		Record
			currentInDB = findMatchInDB(current),
			replacementInDB = findMatchInDB(replacement);
		
		if( replacementInDB == null ) {
			if( currentInDB == null ) {
				replacement = db.executeInsertHistory( replacement );
				return replacement;
			}
			else {
				for(String property : current.getProperties())
					currentInDB.setValue(property, replacement.getValue(property));
				db.executeUpdateHistory( current );
				return current;
			}
		}
		
		return replacementInDB;
	}
	
	
	
	/**
	 * High-level delete.
	 * Delete the specified record properly.
	 * Do not forget the record must be in the database!
	 * <b>Intended use: records from basic tables only!</b> 
	 * Records from other tables may require a special attention when deleted. 
	 * <br/>
	 * We recognize three types of records:
	 * <ol>
	 * <li>
	 * <b>(O)</b> Occurrence records. Deleting an Occurrence requires the deletion of
	 * all associated authors (AuthorOccurrences) and the associated habitat. 
	 * </li>
	 * <li>
	 * <b>(D)</b> Deletable records. Deletable records should be only marked as deleted so that
	 * the User can revive them later (using the History.Undo operation). It is not possible 
	 * to delete a record of the type (D), if there are at least two undeleted records refering to it.
	 * Deletable records are: Author, AuthorOccurrence, Habitat, Publication. 
	 * </li>
	 * <li>
	 * <b>(I)</b> Immutable records. Immutable records belong to immutable tables and should be deleted
	 * for good. <b>This kind of records cannot be handled in here!</b>
	 * </li>
	 * <br/>
	 * 
	 * @param record	The record that will be deleted. 
	 * @return Either the record, if it has been marked as deleted (but physically remains in the database), 
	 * or null, if the record was deleted from the database for good and is no longer available.
	 * 
	 * @see #deleteImmutableRecord(Record)
	 */
	public Record highLevelDelete(Record record) 
	throws RemoteException, DBLayerException {
		if( !db.beginTransaction() )
			throw new DBLayerException(L10n.getString("Error.TransactionRaceConditions"));
		try {
			Record r = delete( record );
			db.commitTransaction();
			return r;
		} catch( DBLayerException e ) {
			db.rollbackTransaction();
			throw e;
		} catch( RemoteException e ) {
			db.rollbackTransaction();
			throw e;
		}
	}

	private boolean deletingAllAuthorOccurrences = false;
	
	private Record delete(Record record) 
	throws RemoteException, DBLayerException {
		logger.info("Deleting [" + record + "] from the database.");
		if( !Record.BASIC_TABLES.contains(record.getClass()) )
			throw new IllegalArgumentException(L10n.getString("Error.ImproperUse"));
		
		// (O) OCCURRENCE RECORDS
		if( record instanceof Occurrence ) {
			Occurrence occ = (Occurrence)record;
			occ.setDeleted( 1 );
			// Delete the Occurrence first.
			db.executeUpdateInTransaction( occ );
			// Try to delete the Habitat of this Occurrence.
			try {
				delete( occ.getHabitat() );
			} catch(DBLayerException e) {/* No matter. */}
			// Delete all authors associated with this Occurrence
			AuthorOccurrence[] aos = findAllAuthors( occ );
			deletingAllAuthorOccurrences = true;
			if( aos != null ) // Although this should not happen, every Occurrence must have at least one AO!
				for( AuthorOccurrence ao : aos )
					delete( ao );
			deletingAllAuthorOccurrences = false;
		}
		// (D) DELETABLE RECORDS
		if(record instanceof Deletable) {
			// How many records, that are alive, share this one?
			int sharers = sharedBy( record );
			if( sharers == 0 ) {
				// Mark the record as deleted.
				// [!] If all AuthorOccurrences are to be deleted because their Occurrence is deleted, 
				// the delete value is 2 (instead of 1), so that they can be revived properly 
				// when the Occurrence is revived!  
				((Deletable)record).setDeleted( 
						(record instanceof AuthorOccurrence && deletingAllAuthorOccurrences) ? 2 : 1 );
				db.executeUpdateInTransaction( record );
			}
			else {
				logger.warn("The "+record+" is in use by "+sharers+" other records. It cannot be deleted!");
				throw new DBLayerException(L10n.getString("Error.DeletingSharedRecord"));
			}
		}
		// (I) IMMUTABLE RECORDS
		else {
			throw new IllegalArgumentException(L10n.getString("Error.ImproperUse"));
			/* 
			 * Proper handling:
			 * 
			int sharers = sharedBy( record, false );
			if( sharers > 0 ) {
				logger.error("The "+record+" is in use by "+sharers+" other record(s). It cannot be deleted!");
				throw new DBLayerException(L10n.getFormattedString("Error.DeletingSharedRecord", record, sharers));
			}
			
			db.executeDeleteHistory( record );
			record = null;
			 */
		}
		
		return record;
	}
	
	/**
	 * It is impossible to delete a record of the type (I), if there is at least one
	 * record refering to it (undeleted as well as deleted).
	 * 
	 * @param The record that is to be deleted. 
	 */
	public void deleteImmutableRecord(Record record)
	throws RemoteException, DBLayerException {
		if( !Record.IMMUTABLE.contains(record) )
			throw new IllegalArgumentException(L10n.getString("Error.ImproperUse"));
		
		int sharers = sharedBy( record, false );
		if( sharers > 0 ) {
			logger.error("The "+record+" is in use by "+sharers+" other record(s). It cannot be deleted!");
			throw new DBLayerException(L10n.getString("Error.DeletingSharedRecord"));
		}
		
		db.executeDeleteHistory( record );
	}
	
	
	/**
	 * In case you wish to present some record(s) to the User
	 * a simple table will be created from supplied record(s).
	 * 
	 * @param records
	 * @return A table model containing all records.
	 */
	public TableModel createTableFor(Record... records) {
		return new RecordTable(records);
	}
	

	private class RecordTable extends AbstractTableModel {
		
		private ArrayList<String>[] value;
		private String[] columnNames;
		
		@SuppressWarnings("unchecked")
		public RecordTable(Record... records) {
			int n = records.length;
			value = new ArrayList[ n + 1 ];
			columnNames = new String[ n + 1 ];
			for(int i = 0; i <= n; i++) {
				value[i] = new ArrayList<String>(20);
				columnNames[i] = L10n.getString("Record.Value") + " " + i; 
			}
			columnNames[0] =  L10n.getString("Record.Property"); 
			
			traverse(records);
		}

		
		private void traverse(Record...r) {
			int n;
			for(n = 0; n < r.length; n++) 
				if(r[n] != null) break;
			
			if(r[n] == null)
				return;
			
			Class table = r[n].getClass(); 
			for( String property : r[n].getProperties()) {
				value[0].add(L10n.getString(table.getSimpleName()+"."+property));
				
				for(int i = 0; i < r.length; i++) { 
					Object v = (r[i] == null) ? null : r[i].getValue(property);
					value[i + 1].add( (v == null) ? "" : v.toString() );
				}
			}
			
			
			Record[] subrecords = new Record[r.length];
			for( String key : r[n].getForeignKeys() ) {
				for(int i = 0; i < r.length; i++)
					subrecords[i] = (Record)r[i].getValue(key);
				
				traverse( subrecords );
			}
		}
		
		
		public int getRowCount() {
			return value[0].size(); 
		}
		
		public int getColumnCount() {
			return columnNames.length;
		}
		
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		public Object getValueAt(int row, int column) {
			return value[column].get(row);
		}
	}
	
	
	
	
	/**
	 * High-level occurrence data processor. 
	 * Incorporates all rules that bind the record processing. 
	 * 
	 * @param occ	The basic occurrence record.
	 * @param aos	All AuthorOccurrences belonging to the <code>occ</code>.
	 */
	public void processRecord(Occurrence occ, AuthorOccurrence... aos) 
	throws DBLayerException, RemoteException {
		
		// Preliminary validity check.
		if( occ == null || !occ.areAllNNSet() ) {
			logger.error("Cannot process incomplete records ["+occ+"]");
			throw new DBLayerException(L10n.getString("Error.IncompleteRecord"));
		}
		
		logger.debug("Processing new occurrence data [" + occ.toFullString() + "].");
		
		// If the record is dead, then it is clearly meant to be deleted!
		Intention intention =  occ.isDead() ? Intention.DELETE : Intention.UNKNOWN; 
		logger.debug("The intention with the data is " + intention);
			
		boolean isInDB, isDead;
		Occurrence occInDB;
		 // Try to find a matching Occurrence record in the database.
		logger.debug("Looking for a match in the database.");
		SelectQuery q = db.createQuery(Occurrence.class);
		try {
			q.addRestriction(RESTR_EQ, Occurrence.UNITIDDB, null, occ.getUnitIdDb(), null);
			q.addRestriction(RESTR_EQ, Occurrence.UNITVALUE, null, occ.getUnitValue(), null);
			int resultId = db.executeQuery( q );
			int rows = db.getNumRows( resultId );
			if(rows > 1) {
				logger.error("There are " + rows + " identical (with the same unique id) occurrence records! Which one shall be processed?");
				throw new DBLayerException(L10n.getString("Error.AmbiguousUniqueIdentifier"));
			}
			
			isInDB = (rows != 0);
			occInDB = isInDB ? (Occurrence)((Object[])db.more(resultId, 0, 0)[0])[0]  :  null;
			isDead = isInDB ? occInDB.isDead() : false;
			
			logger.debug("Matching record found? " + isInDB +" Is dead? " + isDead);
			
		} finally {
			db.closeQuery(q);	
		}
			
		// Begin a new transaction.
		db.beginTransaction();
		logger.debug("Performing the reqested operation...");
			
		try {
			// The `occ` IS in the database as `occInDB` already.  
			if( isInDB ) {
				if( isDead )
					switch(intention) {
					case DELETE:
						// Nothing to be done, the record is already dead.
						break;
					default:
						occInDB = (Occurrence) update( occInDB, occ );
					}
				else
					switch(intention) {
					case DELETE:
						occInDB = (Occurrence) delete( occInDB );
						break;
					default:
						if( occInDB.equals(occ) )
							occ = occInDB;
						else
							occInDB = (Occurrence) update( occInDB, occ );
					}
			}
			// The `occ` is NOT in the database. 
			else  
				switch(intention) {
				case DELETE:	
					// There's nothing to delete.
					break;
				default:
					occInDB = (Occurrence) insert( occ );
				}
			
			logger.debug("Occurrence processed. About to start processing author-occurrences...");
			// Now, deal with Authors associated with this Occurrence.
			int numberOfUndeadAuthors = 0; 
			
			// If the Occurrence record should have been DELETED,
			// there is nothing more to be done.
			if( intention == Intention.DELETE ) {
				db.commitTransaction();
				return;
			}
			// The intention was to ADD or UPDATE the existing Occurrence record.  
			else {
				AuthorOccurrence[] aosInDB = new AuthorOccurrence[0];
				logger.debug("Loading all authors of the occurrence record from the database...");
				if( isInDB ) 
					aosInDB = findAllAuthors(occInDB, false);
				// Compute the number of undead authors (authors, that are not marked as deleted). 
				for(AuthorOccurrence ao : aosInDB) {
					ao.setOccurrence( null ); // simplify the comparison
					if( !ao.isDead() ) numberOfUndeadAuthors++ ;
				}
				
				for( AuthorOccurrence ao : aos ) {
					if( !ao.areAllNNSet() ) {
						logger.warn("The AuthorOccurrence is incomplete! It will be skipped.");
						continue;
					}
					
					logger.debug("The AO seems ok. See [" + ao.toFullString() + "].");
					
					// Simplify the comparison (the Occurrence is known...)
					ao.setOccurrence( null );
					
					// Check if that AuthorOccurrence is already in the database.
					AuthorOccurrence aoInDB = null;
					for( AuthorOccurrence alpha : aosInDB ) {
						if( alpha.equalsUpTo( ao, Deletable.DELETED ) ) {
							aoInDB = alpha;
							break;
						}
					}
					
					logger.debug("Is the AO in the database already? " + aoInDB != null);
					
					//	The Occurrence `occInDB` is in the database, that is for sure.
					// The ao.Occurrence, however, is NOT from the database.
					ao.setOccurrence( occInDB ); // now it's fine
					
					// The intention with this AuthorOccurrence. 
					intention = ao.isDead() ? Intention.DELETE : Intention.UNKNOWN;
					
					logger.debug("The intention with the AO is " + intention);
					

					// [A] AO is not in the database.
					if(aoInDB == null)
						switch(intention) {
						case DELETE:
							break;
						default:
							insert( ao );							
						numberOfUndeadAuthors++;
						}
					// [B] AO is in the database already.
					else
						switch(intention) {
						case DELETE:
							if( !aoInDB.isDead() ) {
								aoInDB.setOccurrence( occInDB ); // repair the simplified ao
								delete( aoInDB ); // delete the ao
								numberOfUndeadAuthors--;
							}
							break;
						default:
							if( aoInDB.isDead() ) {
								aoInDB.setOccurrence( occInDB ); // repair the simplified ao
								aoInDB.setDeleted(0);
								db.executeUpdateInTransaction( aoInDB ); // revive the ao
								numberOfUndeadAuthors++;
							}
						}
					
					logger.debug("AO processed.");
				}
			
				// Transaction is valid iff everything went fine and the number of undead authors is positive.
				if( numberOfUndeadAuthors <= 0 ) {
					logger.error("It is not possible to store a record without an author. It will be rejected.");
					throw new DBLayerException(L10n.getString("Error.NoAuthorsLeft"));
				}
				
				logger.debug("Processing of the occurrence data completed.");
				db.commitTransaction();
			}
			
		} catch(DBLayerException e) {
			// Roll back the transaction.
			e.printStackTrace();
			db.rollbackTransaction();
			throw e;
		} catch(RemoteException e) {
			// Roll back the transaction.
			db.rollbackTransaction();
			throw e;
		}
	}
	
}
