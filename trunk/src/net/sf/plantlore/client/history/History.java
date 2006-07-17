/*
 * History.java
 *
 * Created on 14. duben 2006, 15:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.history;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Observable;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.HistoryChange;
import net.sf.plantlore.common.record.HistoryColumn;
import net.sf.plantlore.common.record.HistoryRecord;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.Village;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.exception.DBLayerException;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class History extends Observable {
    
    /** Instance of a logger */
    private Logger logger;      
    /** Instance of a database management object */
    private DBLayer database;   
    /** Exception with details about an error */
    private String error = null;
    /** Constant with default number of rows to display */
    private static final int DEFAULT_DISPLAY_ROWS = 6;    
    /** Actual number of rows to display */
    private int displayRows = DEFAULT_DISPLAY_ROWS;   
    /** Index of the first record shown in the table */
    private int currentFirstRow;
    /** Information about current display rows*/
    private String displayRow;    
    
    //*******Informations about searching Result from database*****//
    /** Result of the search query */
    private int resultId = 0;
    /** List of data (results of a search query) displayed in the table */
    private ArrayList<HistoryRecord> historyDataList = new ArrayList<HistoryRecord>();     
    // seznam editovanych objektu (potrebny pro hromadne potvrzeni update)
    private ArrayList<Object> editObjectList = new ArrayList<Object>();    
    // informace pro uzivatele o record undo
    private String messageUndo;

    //************************************pro historii jednoho nalezu*********************/
    //seznam id vsech oznacenych polozek
    private HashSet markListId = new HashSet();
    //Seznam Item + maxIdItem (nejstarsi oznacene id pro dany Item=sloupec)
    private ArrayList<Object[]> markItem = new ArrayList<Object[]>();
    //Informuje o tom, zda byla zvolena volba "SelectAll"
    private boolean selectAll;    
    
    //*********************Record of history, ... ***************************************//   
    private Object data;
    private Occurrence occurrence;
    private Habitat habitat;
    private AuthorOccurrence authorOccurrence;
    private HistoryRecord historyRecord;
    private HistoryChange historyChange;
    private Publication publication;
    private Author author;
    private Village village;
    private Territory territory;
    private Phytochorion phytochorion;
    private Metadata metadata;
    
     //	**************Informations about HistoryRecord*************//	
    /** Name of the table where value was changed*/
    private String tableName;  
    /** Name of the column where value was changed*/
    private String columnName;
    /** Unique value identified occurrence */	
    private Integer occurrenceId;
    /**Unique value identified record in table where value was changed */
    private int recordId;
    /**Unique value identified record before changed */
    private int oldRecordId;
    /** Operation whitch was used*/   
    private int operation;
    /** Date and time when the reccord was changed*/
    private java.util.Date when = null;	
    /** Old value of attribute*/    
    private String oldValue;      
   /** Name of user who did changed*/
    private String nameUser;
    
    //**************Informations about occurrences***************//
    /** Name of plant for specified occurrenc*/
    private String namePlant;
    /** Name of author for specified occurrenc*/
    private String nameAuthor;
    /** Informaciton about location for specified occurrenc*/
    private String location;
    
    //********************************************************//          
    private Hashtable<String, Integer> authorsOccurrenceHash;
    private Hashtable<String, Integer> occurrenceHash; 
    private Hashtable<String, Integer> authorHash;
    private Hashtable<String, Integer> habitatHash;
    private Hashtable<String, Integer> metadataHash;
    private Hashtable<String, Integer> publicationHash;
    private Hashtable<String, Enum> editTypeHash;
    
    //*********************************************************//
    public static final String ERROR_SEARCH_RECORD = L10n.getString("Error.historyRecordSearchFailed");
    public static final String ERROR_SEARCH_DATA = L10n.getString("Error.historyDataSearchFailed");
    public static final String ERROR_SEARCH_OBJECT = L10n.getString("Error.historyObjectSearchFailed");
    public static final String ERROR_SEARCH_AUTHOR = L10n.getString("Error.historyAuthorSearchFailed");
    public static final String ERROR_PROCESS = L10n.getString("Error.historyProcessResultsFailed");
    public static final String ERROR_UPDATE = L10n.getString("Error.historyUpdateResultsFailed");
    public static final String ERROR_DELETE = L10n.getString("Error.historyDeleteResultsFailed");  
        
    /**
     * Creates a new instance of History - history of Occurrences, Habitats, Authors, 
     * Publications, Metadata, Territories, Phytochorions, Villages
     * @param database Instance of a database management object
     */
    public History(DBLayer database){
          
       logger = Logger.getLogger(this.getClass().getPackage().getName());	 
       this.database = database;
       
       //Search history data (without condition, order by date)
       searchWholeHistoryData();
       //Process results 
       processResult(1, displayRows);
    }
    
    /**  
     *  Creates a new instance of History - history of specific occurrence 
     *  @param database Instance of a database management object
     *  @param idObj identifier of specific occurrence
     * */
    public History(DBLayer database, int idObj)
    {
       logger = Logger.getLogger(this.getClass().getPackage().getName());	 
       this.database = database;       
       
       SelectQuery query = null;
       int resultIdRecord = 0;
       Object[] object = null;      
       
       try {    	  
		    query = database.createQuery(Occurrence.class);
      	    query.addRestriction(PlantloreConstants.RESTR_EQ, Occurrence.ID, null, idObj, null);
      	    query.addRestriction(PlantloreConstants.RESTR_EQ, Occurrence.DELETED, null, 0, null);    	                   
            resultIdRecord = database.executeQuery(query);
            object = database.next(resultIdRecord);                    
            database.closeQuery(query);
       } catch(RemoteException e) {
       	    logger.error("Remote exception caught in History. Details: "+e.getMessage());
       	    setError(ERROR_SEARCH_RECORD);
       	    return;
       } catch(DBLayerException e) {
    	    logger.error("Search selected occurrence failed. DBLayer exception caught in History. Details: "+e.getMessage());       	                                                   
            setError(ERROR_SEARCH_RECORD);             
            return;
       }                                          
       if (object == null) {
    	   logger.error("tOccurrence doesn't contain required data");  
    	   setError(ERROR_SEARCH_RECORD); 
       } else {
    	   
    	   occurrence = (Occurrence)object[0];    	   
    	   setData(occurrence);
    	   
	       //Save basic information about specific occurrence 
	       setNameAuthor(getAllNameOfAuthors(getAllAuthors(occurrence, 0)));
	       setNamePlant(occurrence.getPlant().getTaxon());       
	       setLocation(occurrence.getHabitat().getNearestVillage().getName());	            
	       
	       //Save information about data entries concerned with specific occurrence
	       setWhen(occurrence.getCreatedWhen());
	       setNameUser(occurrence.getCreatedWho().getWholeName());
	       
	       //Searching for information about data editing concerned with specific occurrence
	       searchEditHistory(occurrence);
	       
	       //Process results of a search "edit" query 
	       processResult(1,displayRows);
       }
    }	

    /**  
     *  Creates a new instance of History - history of specific habitat 
     *  @param database Instance of a database management object
     *  @param idObj integer containing identifier of specific habitat
     *  @param infoHabitat
     * */
    public History(DBLayer database, int idObj, String infoHabitat)
    {
       logger = Logger.getLogger(this.getClass().getPackage().getName());	 
       this.database = database;       
       
       SelectQuery query = null;
       int resultIdRecord = 0;
       Object[] object = null;      
       
       try {    	  
		    query = database.createQuery(Habitat.class);
      	    query.addRestriction(PlantloreConstants.RESTR_EQ, Habitat.ID, null, idObj, null);
      	    query.addRestriction(PlantloreConstants.RESTR_EQ, Habitat.DELETED, null, 0, null);    	                   
            resultIdRecord = database.executeQuery(query);
            object = database.next(resultIdRecord);                    
            database.closeQuery(query);
       } catch(RemoteException e) {
       	    logger.error("Remote exception caught in History. Details: "+e.getMessage());
       	    setError(ERROR_SEARCH_RECORD);
       	    return;
       } catch(DBLayerException e) {
    	    logger.error("Search selected habitat failed. DBLayer exception caught in History. Details: "+e.getMessage());       	                                                   
            setError(ERROR_SEARCH_RECORD);             
            return;
       }                                          
       if (object == null) {
    	   logger.error("tHabitat doesn't contain required data");  
    	   setError(ERROR_SEARCH_RECORD); 
       } else {
    	   
    	   habitat = (Habitat)object[0];    	   
    	   setData(habitat);
    	   
    	   //TODO
	       //Save basic information about specific habitat	             	      	  
	       setLocation(habitat.getNearestVillage().getName());	            	       	       
	       
	       //Searching for information about data editing concerned with specific habitat
	       searchEditHistory(habitat);
	       
	       //Process results of a search "edit" query 
	       processResult(1,displayRows);
       }
    }	
    
    
    /**     
     * Searches for information about data editing concerned with specific occurrence or habitat. 
     * @param data object containing specific occurrence or habitat
     */     
    public void searchEditHistory(Object data)
    {      	    	
        //Create new Select query
        SelectQuery query = null; 
        int resultIdEdit = 0;

    	//  Select data from tHistory table
        try {
		    query = database.createQuery(HistoryRecord.class);
		    // Create aliases for table tHistoryChange.      
	        query.createAlias("historyChange", "hc");        
	        // Add restriction to COPERATION column of tHistoryChange table
	        if (data instanceof Occurrence) {	        	       
		        query.addRestriction(PlantloreConstants.RESTR_EQ, "hc.recordId", null, ((Occurrence)data).getId(), null);  
	        } else if (data instanceof Habitat) {	        	        
		        query.addRestriction(PlantloreConstants.RESTR_EQ, "hc.recordId", null, ((Habitat)data).getId(), null);  
	        }	
	        query.addRestriction(PlantloreConstants.RESTR_EQ, "hc.operation", null, HistoryChange.HISTORYCHANGE_EDIT, null);
	        query.addOrder(PlantloreConstants.DIRECT_DESC, "hc.when");
	        resultIdEdit = database.executeQuery(query); 
		} catch (RemoteException e) {
			logger.error("Remote exception caught in History (searchEditHistory). Details: "+e.getMessage());
			setError(ERROR_SEARCH_DATA);
		} catch (DBLayerException e) {
			logger.error("DBLayer exception caught in History (searchEditHistory). Details: "+e.getMessage());
			setError(ERROR_SEARCH_DATA);            
		}                
		setResultId(resultIdEdit);                
    }
    
       
    /**
     * Searches for information about data concerned with occurrence, habitat, author, metadata, publication,
     * territory, village and phytochorion.
     */
    public void searchWholeHistoryData() {
        
        //Create new Select query
        SelectQuery query = null;
        int resultIdWHistory = 0;

    	//  Select data from tHistory table
        try {
			query = database.createQuery(HistoryRecord.class);
			// Create aliases for table tHistoryChange.
			query.createAlias("historyChange", "hc");			
			query.addOrder(PlantloreConstants.DIRECT_DESC, "hc.when");
			resultIdWHistory = database.executeQuery(query);
		} catch (RemoteException e) {
			logger.error("Remote exception caught in History (searchWholeHistoryData). Details: "+e.getMessage());
			setError(ERROR_SEARCH_DATA);
		} catch (DBLayerException e) {
			logger.error("DBLayer exception caught in History (searchWholeHistoryData). Details: "+e.getMessage());
			setError(ERROR_SEARCH_DATA);
	    }
		setResultId(resultIdWHistory);                 
    }
    
   /**
     * Process results of a search query. Retrieves results using the database management object (DBLayer) and stores them in the data field of the class. 
     * @param from number of the first row to show in table. Number of the first row to retraieve is 1.
     * @param count number of rows to retrieve 
     */
    public void processResult(int from, int count) {
        
        if (this.resultId != 0) {
            int currentRow = getResultRows();
            logger.debug("Rows in the result: "+currentRow);
            logger.debug("Max available rows: "+(from+count-1));
           
            // Find out how many rows we can retrieve - it cannot be more than number of rows in the result
            int to = Math.min(currentRow, from+count-1);           
            if (to <= 0) {
            	historyDataList = new ArrayList<HistoryRecord>(); 
            	setDisplayRows(0);
            	setCurrentDisplayRows("0-0");
            } else {
                logger.debug("Retrieving query results: 1 - "+to);
                setCurrentDisplayRows(from+ "-" + to);
                try {                	 
                     // Retrieve selected row interval 
                	Object[] objectHistory;
                 	try {
                 		objectHistory = database.more(this.resultId, 0, to-1);  
                 	} catch(RemoteException e) {
                     	System.err.println("RemoteException- processEditResult, more");
                     	logger.debug("RemoteException- processEditResult, more");
                     	return;
                     }                   
                    int countResult = objectHistory.length;  
                    logger.debug("Results retrieved. Count: "+ countResult);
                    // Create storage for the results
                    this.historyDataList = new ArrayList<HistoryRecord>();
                    // Cast the results to the HistoryRecord objects
                    for (int i=0; i<countResult; i++ ) {                    							
						Object[] objHis = (Object[])objectHistory[i];
                        this.historyDataList.add((HistoryRecord)objHis[0]);
                    }                               
                } catch (DBLayerException e) {                  
                    logger.error("Processing search results failed: "+e.toString());   
                    setError(this.ERROR_PROCESS);
                }    
                // Update current first displayed row (only if data retrieval was successful).
                if (!this.isError()) {
                    logger.info("Results successfuly retrieved");                   
                    setCurrentFirstRow(from);
                }
            }
            // Tell observers to update
            setChanged();
            notifyObservers();
            // Clean error flag 
            this.error = null;
         }         
    }
    
    /**
     *  Rollback changes from now to date of selected record 
     *  @param toResult int containing identifier of selected record
     */
    public void undoToDate(int toResult) {
        
        //Initialization of hashTable
        initAuthorsOccurrenceHash();
    	initOccurrenceHash();
    	initHabitatHash();   
        initPublicationHash();
        initAuthorHash();      
        initMetadataHash();
        	    	
    	//read record from younger to older until selected row        
    	for( int i=0; i < toResult; i++) {    		
    		//init history data 
    		historyRecord = (HistoryRecord)historyDataList.get(i);    		
    		historyChange = historyRecord.getHistoryChange();
    		tableName = historyRecord.getHistoryColumn().getTableName();
            recordId = historyChange.getRecordId();
            operation = historyChange.getOperation();
		  
            if (operation == HistoryChange.HISTORYCHANGE_INSERT) {
                undoInsertDelete(1);
            } else if (operation == HistoryChange.HISTORYCHANGE_EDIT) {
                undoEdit();
            } else if (operation == HistoryChange.HISTORYCHANGE_DELETE) {
                undoInsertDelete(0);
            } else {
                logger.error("Incorrect opreration code: "+ operation);
            }                
        }
    }
    
    /**
     *  Rollback selected data editing concerned with specific occurrence or habitat. 
     */
    public void undoSelected() {
    	
    	// Inicalization of hashTable
        initAuthorsOccurrenceHash();
    	initOccurrenceHash();
    	initHabitatHash();    	  
        	
    	//number of result
    	int countResult = getResultRows();    	   
    	
    	//take record from younger to older
    	for( int i=0; i < countResult; i++) {
    		if (! markListId.contains(i)) {
    			continue;
    		}
    		
    		// init history data about editing concerned with record
    		historyRecord = (HistoryRecord)historyDataList.get(i);    		
    		historyChange = historyRecord.getHistoryChange();
    		tableName = historyRecord.getHistoryColumn().getTableName();    		  		    			           
            recordId = historyChange.getRecordId();           	   
            operation = historyChange.getOperation();
                               
            undoEdit();    		
    	}
    	//generated information message for user
    	generateMessageUndo();
    }
    
    /**
     *   Rollback operation insert or delete. 
     *   @param isDelete int containing informaciton about insertion or erasure of record. 
     */
    public void undoInsertDelete(int isDelete) {
        if (tableName.equals(PlantloreConstants.ENTITY_OCCURRENCE)){
             Object[] object = searchObject("Occurrence",recordId);             
             Occurrence occurrence = (Occurrence)object[0];             
             occurrence.setDeleted(isDelete);              
             //Add to list of changed Record
             if (!editObjectList.contains((Record)occurrence))                 
                editObjectList.add((Record)occurrence);       
             //Update author of specific occurrence
             isDelete = (isDelete == 1) ? 2 : isDelete;
             Object[] objects = getAllAuthors(occurrence, 2-isDelete);
             int countResult = objects.length;               
             for (int i=0; i<countResult; i++ ) {                    							
                Object[] autOcc = (Object[])objects[i];          
                AuthorOccurrence authorOccurrence = (AuthorOccurrence)autOcc[0];
                authorOccurrence.setDeleted(isDelete);
                 //Add to list of changed Record
                 if (!editObjectList.contains((Record)authorOccurrence))                 
                    editObjectList.add((Record)authorOccurrence);                 
            }                                     
        } else if (tableName.equals(PlantloreConstants.ENTITY_AUTHOROCCURRENCE)) {
             Object[] object = searchObject("AuthorOccurrence",recordId);  
             AuthorOccurrence authorOccurrence = (AuthorOccurrence)object[0];
             authorOccurrence.setDeleted(isDelete);             
             //Add to list of changed Record             
             if (!editObjectList.contains((Record)authorOccurrence))                 
                editObjectList.add((Record)authorOccurrence);             
       } else if (tableName.equals(PlantloreConstants.ENTITY_HABITAT)) {            
               Object[] object = searchObject("Habitat",recordId);  
               Habitat habitat = (Habitat)object[0];
               habitat.setDeleted(isDelete);
               //Add to list of changed Record             
               if (!editObjectList.contains((Record)habitat))                 
                   editObjectList.add((Record)habitat);             
        } else if (tableName.equals(PlantloreConstants.ENTITY_METADATA)) {
             Object[] object = searchObject("Metadata",recordId);  
             Metadata metadata = (Metadata)object[0];
             metadata.setDeleted(isDelete);
             //Add to list of changed Record             
             if (!editObjectList.contains((Record)metadata))                 
                editObjectList.add((Record)metadata);             
        } else if (tableName.equals(PlantloreConstants.ENTITY_PUBLICATION)) {
             Object[] object = searchObject("Publication",recordId);  
             Publication publication = (Publication)object[0];
             publication.setDeleted(isDelete);
             //Add to list of changed Record             
             if (!editObjectList.contains((Record)publication))                 
                editObjectList.add((Record)publication);             
        } else if (tableName.equals(PlantloreConstants.ENTITY_AUTHOR)) {
             logger.debug("AUTHOR HISTORY: "+ isDelete);
             Object[] object = searchObject("Author",recordId);   
             Author author = (Author)object[0];
             author.setDeleted(isDelete);
             //Add to list of changed Record             
             if (!editObjectList.contains((Record)author))                 
                editObjectList.add((Record)author);             
        }  else {
            logger.error("Name of table is incorrect.");
        }
    }
    
    /**
     * Rollaback operation Update.
     */
    public void undoEdit() {
        
        //init history data about editing concerned with record
        columnName = historyRecord.getHistoryColumn().getColumnName();    		    			
        oldRecordId = historyChange.getOldRecordId();                        	           
        oldValue = historyRecord.getOldValue();
        
        if (tableName.equals(PlantloreConstants.ENTITY_AUTHOROCCURRENCE)) {
                undoAuthorOccurrence();
        } else if (tableName.equals(PlantloreConstants.ENTITY_OCCURRENCE)){
                undoOccurrence();
        } else if (tableName.equals(PlantloreConstants.ENTITY_HABITAT)) {
                undoHabitat();
        } else if (tableName.equals(PlantloreConstants.ENTITY_PUBLICATION)) {
                undoPublication();
        } else if (tableName.equals(PlantloreConstants.ENTITY_AUTHOR)) {
                undoAuthor();
        } else if (tableName.equals(PlantloreConstants.ENTITY_METADATA)) {
                undoMetadata();
        } else if (tableName.equals(PlantloreConstants.ENTITY_PHYTOCHORION)) {
                undoPhytochorion();
        } else if (tableName.equals(PlantloreConstants.ENTITY_TERRITORY)) {
                undoTerritory();
        } else if (tableName.equals(PlantloreConstants.ENTITY_VILLAGE)) {
                undoVillage();
        } else {
            logger.error("Name of table is incorrect.");
        }
    }

    
     /**
     *  Rollback data editing concerned with specific occurrence and author
     */
    public void undoAuthorOccurrence() {
        
    	int authorOccId = historyChange.getRecordId();
    	occurrenceId = historyChange.getRecordId();	
    	AuthorOccurrence authorOccurrence = null;
    	int placings = 0;
    	boolean contain = false;
    	for (int i=0; i < editObjectList.size(); i++) {
    		if (editObjectList.get(i) instanceof AuthorOccurrence) {    			
    			int listAutOccId = ((AuthorOccurrence)(editObjectList.get(i))).getId();
    			if (authorOccId == listAutOccId) {
    				contain = true;
    				placings = i;   
    				authorOccurrence = (AuthorOccurrence)(editObjectList.get(i));
    				break;
    			}
    		}
    	}    
    	
    	if (!contain) {
        	// Select record AuthorOccurrence where id = authorOccurrenceId 
    		Object[] object = searchObject("AuthorOccurrence", authorOccId);
            authorOccurrence = (AuthorOccurrence)object[0];
        }     	                 
                
        boolean objectList = editObjectList.contains((Record)authorOccurrence); 
        if (!objectList) {
        	//add object to list of editing object
            editObjectList.add((Record)authorOccurrence);
        }
       
       // Get number of columnName from authorOccurrence mapping
        int columnConstant;
        if (columnName == null) {
        	columnConstant = 1;
        } else if (authorsOccurrenceHash.containsKey(columnName)) {
            columnConstant = (Integer)authorsOccurrenceHash.get(columnName); 
        } else {
            columnConstant = 0;
        }        	    			
      
        // Save new value for the column        		
        switch (columnConstant) {
            case 1:  //Author of occurrence - add author, remove author or change role of author
                if (authorOccurrence.getDeleted() == 1) {
                    authorOccurrence.setDeleted(0);
                } else {
                    authorOccurrence.setDeleted(1);
                }                
                break;
            case 2: //Role of author
                authorOccurrence.setRole(oldValue);
                logger.debug("Set selected value for update of attribe Role of Author.");
                break;
            case 3: //Result of revisition
                authorOccurrence.setNote(oldValue);
                logger.debug("Set selected value for update of attribute Result of revision.");
                break;
            default:
                logger.error("No column defined for name "+ columnName);	                   
        }
        
        if (contain) {        	
        	editObjectList.set(placings, (Record)authorOccurrence);        
        } else {
        	// add object to list of editing object
            logger.debug("ObjectList - add authorOccurrence");
            editObjectList.add(authorOccurrence);
        }
    }
    
    /**
     *  Rollback data editing concerned with specific occurrence
     */
    public void undoOccurrence() {
               
    	occurrenceId = historyChange.getRecordId();
    	Occurrence occ = null;
    	int placings = 0;
    	boolean contain = false;
    	for (int i=0; i < editObjectList.size(); i++) {
    		if (editObjectList.get(i) instanceof Occurrence) {    			
    			int listOccId = ((Occurrence)(editObjectList.get(i))).getId();
    			if (occurrenceId.equals(listOccId)) {
    				contain = true;
    				placings = i;   
    				occ = (Occurrence)(editObjectList.get(i));
    				break;
    			}
    		}
    	}    	    	               
                
        if (!contain) {
        	// Select record Occurrence where id = occurrenceId 
            Object[] objectOcc = searchObject("Occurrence",occurrenceId);
            occ = (Occurrence)objectOcc[0];                    	
        }    
                
        //Get number of columnName from occurrence mapping.
        int columnConstant;
        if (occurrenceHash.containsKey(columnName)) {
                 columnConstant = (Integer)occurrenceHash.get(columnName); 
        } else {
             columnConstant = 0;
        }        	    			

        //init Calendar    		
        Calendar isoDateTime = new GregorianCalendar();

        switch (columnConstant) {
        case 1: //Taxon  
            if (oldRecordId > 0 ) {
                //Select record Plant where id = oldRocordId 
                Object[] object = searchObject("Plant",oldRecordId);
                Plant plant = (Plant)object[0];
                //Set old value to attribute plantID
                occ.setPlant(plant);
                logger.debug("Set selected value for update of attribute Taxon.");	
            } else {
                 logger.error("UNDO - Incorrect oldRecordId for Phytochoria.");
            } 
            break;
        case 2: //Year	
               //Set old value to attribute Year          		
                occ.setYearCollected(Integer.parseInt(oldValue));
                logger.debug("Set selected value for update of attribute Year.");
                //Update attribute isoDateTimeBegin (Year + Mont + Day + Time)		                	                		
                isoDateTime.setTime(occ.getTimeCollected());
                isoDateTime.set(Integer.parseInt(oldValue),occ.getMonthCollected(),occ.getDayCollected());
                occ.setIsoDateTimeBegin(isoDateTime.getTime());	                	              	            	
                break;
        case 3: //Month 
                // Set old value to attribute Month 
                occ.setMonthCollected(Integer.parseInt(oldValue));
                logger.debug("Set selected value for update of attribute Month.");
                // Update attribute isoDateTimeBegin (Year + Mont + Day + Time)		                	
                isoDateTime.setTime(occ.getTimeCollected());
                isoDateTime.set(occ.getYearCollected(), Integer.parseInt(oldValue), occ.getDayCollected());
                occ.setIsoDateTimeBegin(isoDateTime.getTime());              		
            break;
        case 4: //Day	                	
                // Set old value to attribute Day            		
                occ.setDayCollected(Integer.parseInt(oldValue));
                logger.debug("Set selected value for update of attribute Day.");
                // Update attribute isoDateTimeBegin (Year + Mont + Day + Time)		                	
                isoDateTime.setTime(occ.getTimeCollected());
                isoDateTime.set(occ.getYearCollected(), occ.getMonthCollected(), Integer.parseInt(oldValue));
                occ.setIsoDateTimeBegin(isoDateTime.getTime());
                break;
        case 5: //Time 	                		                	
                // Set old value to attribute Time   
                Date time = new Date();
                SimpleDateFormat df = new SimpleDateFormat( "HH:mm:ss.S" );
                try {
                        time = df.parse( oldValue );
                } catch (ParseException e) {
                        logger.error("Parse time failed. "+ e);
                }
                occ.setTimeCollected(time);
                logger.debug("Set selected value for update of attribute Time.");
                // Update attribute isoDateTimeBegin (Year + Mont + Day + Time)		                	
                isoDateTime.setTime(time);
                isoDateTime.set(occ.getYearCollected(), occ.getMonthCollected(), occ.getDayCollected());
                occ.setIsoDateTimeBegin(isoDateTime.getTime());
            break;
        case 6: //Source	                	
                // Set old value to attribute Source 
                occ.setDataSource(oldValue);
                logger.debug("Set selected value for update of attribute DataSource.");		                	            	
                break;
        case 7: //Herbarium
                // Set old value to attribute Herbarium
                occ.setHerbarium(oldValue);
                logger.debug("Set selected value for update of attribute Herbarium.");	                		          
            break;
        case 8: //Note occurrence	
                // Set old value to attribute Note occurence	                	
                occ.setNote(oldValue);
                logger.debug("Set selected value for update of attribute NoteOccurrence.");	                		        	
                break;
        case 9: //Publication  
                //Select record Publication where id = oldRocordId 
                if (oldRecordId > 0){
                    Object[] objectPubl = searchObject("Publication",oldRecordId);
                    Publication publication = (Publication)objectPubl[0];
                    //Set old value to attribute publicationID
                    occ.setPublication(publication);
                    logger.debug("Set selected value for update of attribute Publication.");
                }else {
                    logger.error("UNDO - Incorrect oldRecordId for Phytochoria.");
                }
            break;
        case 10: //metadata
        		//Select record Publication where id = oldRocordId 
	            if (oldRecordId > 0){
	                Object[] objectMetadata = searchObject("Metadata",oldRecordId);
	                Metadata metadata = (Metadata)objectMetadata[0];
	                //Set old value to attribute metadataID
	                occ.setMetadata(metadata);
	                logger.debug("Set selected value for update of attribute Metadata.");
	            }else {
	                logger.error("UNDO - Incorrect oldRecordId for Metadata.");
	            }
        	break;
        case 11: //habitat
        		//Select record Publication where id = oldRocordId 
	            if (oldRecordId > 0){
	                Object[] objectHabitat = searchObject("Habitat",oldRecordId);
	                Habitat habitat = (Habitat)objectHabitat[0];
	                //Set old value to attribute habitatID
	                occ.setHabitat(habitat);
	                logger.debug("Set selected value for update of attribute Habitat.");
	            }else {
	                logger.error("UNDO - Incorrect oldRecordId for Habitat.");
	            }
        	break;
        default:            
            logger.error("No column defined for name "+ columnName);	                   
        }    
        
        if (contain) {        	
        	editObjectList.set(placings, (Record)occ);        
        } else {
        	// add object to list of editing object
            logger.debug("ObjectList - add occurrences");
            editObjectList.add(occ);
        }
    }
        
    /**
     * Rollback data editing concerned with specific habitat
     */
    public void undoHabitat() {
                         
        int habitatId = historyChange.getRecordId();	        
        Habitat hab = null;
    	int placings = 0;
    	boolean contain = false;
    	for (int i=0; i < editObjectList.size(); i++) {
    		if (editObjectList.get(i) instanceof Habitat) {    			
    			int listHabId = ((Habitat)(editObjectList.get(i))).getId();
    			if (habitatId == listHabId) {
    				contain = true;
    				placings = i; 
    				hab = (Habitat)(editObjectList.get(i));
    				break;
    			}
    		}
    	} 
    	
    	if (!contain) {
        	// Select record Habitat where id = habitatId 
    		Object[] object = searchObject("Habitat",habitatId);        
            hab = (Habitat)object[0];
        } 
                             
        // Get number of columnName from habitat mapping.
        int columnConstant;
        if (habitatHash.containsKey(columnName)) {
                 columnConstant = (Integer)habitatHash.get(columnName); 
        } else {
             columnConstant = 0;
        }        	    			

        // Save new value for the column        		
        switch (columnConstant) {
        case 1:  //Quadrant     	                	                	                		  
                hab.setQuadrant(oldValue);		                	
                logger.debug("Set selected value for update of attribute Quadrant.");                	
            break;
        case 2: //Place description 	                	 	                			                		 
                hab.setDescription(oldValue);		                	
                logger.debug("Set selected value for update of attribute Description.");              	              	
                break;
        case 3:  //Country 	                	 	                			                		 
                hab.setCountry(oldValue);		                	
                logger.debug("Set selected value for update of attribute Country.");                
            break;
        case 4: //Altitude 	                	                			                		 
                hab.setAltitude(Double.parseDouble(oldValue));		                	
                logger.debug("Set selected value for update of attribute Altitude.");                
                break;
        case 5:  //Latitude   	                		                			                		  
                hab.setLatitude(Double.parseDouble(oldValue));		                	
                logger.debug("Set selected value for update of attribute Latitude.");                
            break;
        case 6: //Longitude 	                		                			                		
                hab.setLongitude(Double.parseDouble(oldValue));		                	
                logger.debug("Set selected value for update of attribute Longitude.");                
                break;
        case 7: //Nearest bigger seat   	                	 	                			                		 
                //Select record Village where id = oldRocordId 
                if (oldRecordId != 0){
                        Object[] objectVill = searchObject("Village",oldRecordId);
                        Village village = (Village)objectVill[0];
                hab.setNearestVillage(village);
                logger.debug("Set selected value for update of attribute NearesVillage.");
                } else {
                        logger.error("UNDO - Incorrect oldRecordId for Village.");
                }                
            break;
        case 8: //Phytochorion or phytochorion code 	                	             			                		 
                // Select record Phytochoria where id = oldRocordId 
                if (oldRecordId != 0){
                        Object[] objectPhyt = searchObject("Phytochorion",oldRecordId);
                        Phytochorion phytochorion = (Phytochorion)objectPhyt[0];
                        hab.setPhytochorion(phytochorion);
                        logger.debug("Set selected value for update of attribute Phytochorion.");
                }else {
                        logger.error("UNDO - Incorrect oldRecordId for Phytochoria.");
                }                
            break; 	               
        case 9:  //Territory   	                	                			                		  
                // Select record Territory where id = oldRocordId 
                if (oldRecordId != 0){
                        Object[] objectTerr = searchObject("Territory",oldRecordId);
                        Territory territory = (Territory)objectTerr[0];
                        hab.setTerritory(territory);
                        logger.debug("Set selected value for update of attribute Territory.");
                }else {
                        logger.error("UNDO - Incorrect oldRecordId for Territory.");
                }	               
            break;
        case 10: //Note habitat	                		                			                		  
                hab.setNote(oldValue);		                	
                logger.debug("Set selected value for update of attribute Note.");                
                break;
        default:            
            logger.error("Habitat - No column defined for name "+ columnName);	                   
        }
        
        if (contain) {        	
        	editObjectList.set(placings,(Record)hab);        
        } else {
        	// add object to list of editing object
            logger.debug("ObjectList - add habitat");
            editObjectList.add(hab);
        }
    }
    
    /**
     * Rollback data editing concerned with specific publication
     */
    public void undoPublication() {
         
    	int publicationId = historyChange.getRecordId();	        
        Publication publication = null;
    	int placings = 0;
    	boolean contain = false;
    	for (int i=0; i < editObjectList.size(); i++) {
    		if (editObjectList.get(i) instanceof Publication) {    			
    			int listPubId = ((Publication)(editObjectList.get(i))).getId();
    			if (publicationId == listPubId) {
    				contain = true;
    				placings = i; 
    				publication = (Publication)(editObjectList.get(i));
    				break;
    			}
    		}
    	} 
    	
    	if (!contain) {
        	// Select record Publication where id = publicationId 
    		Object[] object = searchObject("Publication", publicationId);
            publication = (Publication)object[0];
        } 
            	        
       // Get number of columnName from publication mapping.
        int columnConstant;
        if (publicationHash.containsKey(columnName)) {
                 columnConstant = (Integer)publicationHash.get(columnName); 
        } else {
             columnConstant = 0;
        }        	    			
      
        // Save new value for the column        		
        switch (columnConstant) {
        case 1:  //CollectionName     	                	
                publication.setCollectionName(oldValue);	 
                publication.setReferenceCitation(publication.getCollectionName()+", "+publication.getCollectionYearPublication()+ ", "+publication.getJournalName()+", "+publication.getJournalAuthorName());
                logger.debug("Publication - Set selected value for update of attribute collectionName.");                 	
            break;
        case 2: //collectionYearPublication	                	 	                			                		 
                publication.setCollectionYearPublication(Integer.parseInt(oldValue));	          
                publication.setReferenceCitation(publication.getCollectionName()+", "+publication.getCollectionYearPublication()+ ", "+publication.getJournalName()+", "+publication.getJournalAuthorName());
                logger.debug("Publication - Set selected value for update of attribute collectionYearPublication.");                 	             	
                break;
         case 3: //journalName	                	 	                			                		 
                publication.setJournalName(oldValue);	       
                publication.setReferenceCitation(publication.getCollectionName()+", "+publication.getCollectionYearPublication()+ ", "+publication.getJournalName()+", "+publication.getJournalAuthorName());
                logger.debug("Publication - Set selected value for update of attribute journalName.");                 	             	
                break;
        case 4: //journalAuthorName	                	 	                			                		 
                publication.setJournalAuthorName(oldValue);	 
                publication.setReferenceCitation(publication.getCollectionName()+", "+publication.getCollectionYearPublication()+ ", "+publication.getJournalName()+", "+publication.getJournalAuthorName());
                logger.debug("Publication - Set selected value for update of attribute journalAuthorName.");                 	             	
                break;
        case 5: //referenceDetail 	                	 	                			                		 
                publication.setReferenceDetail(oldValue);	                	
                logger.debug("Publication - Set selected value for update of attribute referenceDeatail.");                 	             	
                break;
        case 6: //URL 	                	 	                			                		 
                publication.setUrl(oldValue);	                	
                logger.debug("Publication - Set selected value for update of attribute url.");                 	             	
                break;
        default:            
            logger.error("Publication - No column defined for name "+ columnName);	                   
        } 
        
        if (contain) {        	
        	editObjectList.set(placings,(Record)publication);        
        } else {
        	// add object to list of editing object
            logger.debug("ObjectList - add publication");
            editObjectList.add(publication);
        }
    }
    
    /**
     * Rollback data editing concerned with specific author
     */
    public void undoAuthor() {
        
    	int authorId = historyChange.getRecordId();	        
        Author author = null;
    	int placings = 0;
    	boolean contain = false;
    	for (int i=0; i < editObjectList.size(); i++) {
    		if (editObjectList.get(i) instanceof Author) {    			
    			int listAutId = ((Author)(editObjectList.get(i))).getId();
    			if (authorId == listAutId) {
    				contain = true;
    				placings = i; 
    				author = (Author)(editObjectList.get(i));
    				break;
    			}
    		}
    	} 
    	
    	if (!contain) {
        	// Select record Author where id = authorId 
    		Object[] object = searchObject("Author", authorId);
            author = (Author)object[0];
        } 	
       
    	// Get number of columnName from author mapping.
        int columnConstant;
        if (authorHash.containsKey(columnName)) {
                 columnConstant = (Integer)authorHash.get(columnName); 
        } else {
             columnConstant = 0;
        }        	    			
      
        // Save new value for the column        		
        switch (columnConstant) {
        case 1:  //wholeName     	                	
                author.setWholeName(oldValue);	                 
                logger.debug("Author - Set selected value for update of attribute WholeName.");                 	
            break;
        case 2: //address	                	 	                			                		 
                author.setAddress(oldValue);
                logger.debug("Author - Set selected value for update of attribute Address.");                 	             	
                break;
         case 3: //mail	                	 	                			                		 
                author.setEmail(oldValue);	                      
                logger.debug("Author - Set selected value for update of attribute Email.");                 	             	
                break;
        case 4: //phoneNumber	                	 	                			                		 
                author.setPhoneNumber(oldValue);	                
                logger.debug("Author - Set selected value for update of attribute PhoneNumber.");                 	             	
                break;
        case 5: //organization 	                	 	                			                		 
                author.setOrganization(oldValue);                	
                logger.debug("Author - Set selected value for update of attribute organization.");                 	             	
                break;
        case 6: //URL 	                	 	                			                		 
                author.setUrl(oldValue);	                	
                logger.debug("Author - Set selected value for update of attribute url.");                 	             	
                break;
        case 7: //role	                	 	                			                		 
                author.setRole(oldValue);	                	
                logger.debug("Author - Set selected value for update of attribute Role.");                 	             	
                break;
        case 8: //note 	                	 	                			                		 
                author.setNote(oldValue);	                	
                logger.debug("Author - Set selected value for update of attribute Note.");                 	             	
                break;
        default:            
            logger.error("Author - No column defined for name "+ columnName);	                   
        } 
        
        if (contain) {        	
        	editObjectList.set(placings,(Record)author);        
        } else {
        	// add object to list of editing object
            logger.debug("ObjectList - add author");
            editObjectList.add(author);
        }
    }
    
    /**
     * Rollback data editing concerned with specific metadata
     */
    public void undoMetadata() {
       
    	int metadataId = historyChange.getRecordId();	        
        Metadata metadata = null;
    	int placings = 0;
    	boolean contain = false;
    	for (int i=0; i < editObjectList.size(); i++) {
    		if (editObjectList.get(i) instanceof Metadata) {    			
    			int listOccId = ((Metadata)(editObjectList.get(i))).getId();
    			if (metadataId == listOccId) {
    				contain = true;
    				placings = i; 
    				author = (Author)(editObjectList.get(i));
    				break;
    			}
    		}
    	} 
    	
    	if (!contain) {
        	// Select record Metadata where id = metadataId 
    		Object[] object = searchObject("Metadata", metadataId);
    	    metadata = (Metadata)object[0];
        }
    	
        // Get number of columnName from metadata mapping.
        int columnConstant;
        if (metadataHash.containsKey(columnName)) {
                 columnConstant = (Integer)metadataHash.get(columnName); 
        } else {
             columnConstant = 0;
        }        	    			
      
        // Save new value for the column        		
        switch (columnConstant) {
        case 1:  //technicalContactName     	                	
                metadata.setTechnicalContactName(oldValue);
                logger.debug("Metadata - Set selected value for update of attribute technicalContactName.");                 	
            break;
        case 2:  //technicalContactEmail     	                	
            metadata.setTechnicalContactEmail(oldValue);
            logger.debug("Metadata - Set selected value for update of attribute technicalContactEmail.");                 	
        break;
        case 3:  //technicalContactAddress     	                	
            metadata.setTechnicalContactName(oldValue);
            logger.debug("Metadata - Set selected value for update of attribute technicalContactAddress.");                 	
        break;
        case 4:  //contentContactName     	                	
            metadata.setContentContactName(oldValue);
            logger.debug("Metadata - Set selected value for update of attribute contentContactName.");                 	
        break;
        case 5:  //contentContactEmail     	                	
            metadata.setContentContactEmail(oldValue);
            logger.debug("Metadata - Set selected value for update of attribute technicalContactEmail.");                 	
        break;
        case 6:  //contentContactAddress     	                	
            metadata.setContentContactAddress(oldValue);
            logger.debug("Metadata - Set selected value for update of attribute contentContactAddress.");                 	
        break;        
        case 7:  //dataSetTitle
            metadata.setDataSetTitle(oldValue);
            logger.debug("Metadata - Set selected value for update of attribute dataSetTitle.");                 	
        break;
        case 8:  //dataSetDetail     	                	
            metadata.setDataSetDetails(oldValue);
            logger.debug("Metadata - Set selected value for update of attribute dataSetDetails.");                 	
        break;
        case 9:  //sourceInstitutionId
            metadata.setSourceInstitutionId(oldValue);
            logger.debug("Metadata - Set selected value for update of attribute sourceInstitutionId.");                 	
        break;
        case 10:  //sourceId     	                	
            metadata.setSourceId(oldValue);
            logger.debug("Metadata - Set selected value for update of attribute sourceId.");                 	
        break;
        case 11:  //owenOraganicationAbbrev     	                	
            metadata.setOwnerOrganizationAbbrev(oldValue);
            logger.debug("Metadata - Set selected value for update of attribute owenOraganicationAbbrev.");                 	
        break;
        case 12:  //recordbasis    	                	
            metadata.setRecordBasis(oldValue);
            logger.debug("Metadata - Set selected value for update of attribute recordbasis.");                 	
        break;         
         case 13:  //biotopetext     	                	
            metadata.setBiotopeText(oldValue);
            logger.debug("Metadata - Set selected value for update of attribute biotopetext.");                 	
        break;         
        default:            
            logger.error("Metadata - No column defined for name "+ columnName);	                   
        } 
        
        if (contain) {        	
        	editObjectList.set(placings,(Record)metadata);        
        } else {
        	// add object to list of editing object
            logger.debug("ObjectList - add metadata");
            editObjectList.add(metadata);
        }
    }
    
    
    /**
     * Rollback data editing concerned with specific phytochorion
     */
    public void undoPhytochorion() {
        
    	int phytId = historyChange.getRecordId();	        
        Phytochorion phytochorion = null;
    	int placings = 0;
    	boolean contain = false;
    	for (int i=0; i < editObjectList.size(); i++) {
    		if (editObjectList.get(i) instanceof Phytochorion) {    			
    			int listPhytId = ((Phytochorion)(editObjectList.get(i))).getId();
    			if (phytId == listPhytId) {
    				contain = true;
    				placings = i; 
    				phytochorion = (Phytochorion)(editObjectList.get(i));
    				break;
    			}
    		}
    	} 
    	
    	if (!contain) {
        	// Select record Phytochorion where id = phytochorionId 
    		Object[] object = searchObject("Phytochorion", phytId);
            phytochorion = (Phytochorion)object[0];     
        }
    	
    	if (columnName.equals("namePhytochorion")) {
            phytochorion.setCode(oldValue);	                 
            logger.debug("Phytochorion - Set selected value for update of attribute WholeName.");                 	
        } else if (columnName.equals("code")) {
             phytochorion.setName(oldValue);
             logger.debug("Phytochorion - Set selected value for update of attribute Address.");                 	             	
        } else {
            logger.error("Phytochorion - No column defined for name "+ columnName);
        }   
    	
    	if (contain) {        	
        	editObjectList.set(placings,(Record)phytochorion);        
        } else {
        	// add object to list of editing object
            logger.debug("ObjectList - add phytochorion");
            editObjectList.add(phytochorion);
        }
    }
    
    /**
     *  Rollback data editing concerned with specific village
     */
    public void undoVillage() {
       
    	int villageId = historyChange.getRecordId();	        
        Village village = null;
    	int placings = 0;
    	boolean contain = false;
    	for (int i=0; i < editObjectList.size(); i++) {
    		if (editObjectList.get(i) instanceof Village) {    			
    			int listVillId = ((Village)(editObjectList.get(i))).getId();
    			if (villageId == listVillId) {
    				contain = true;
    				placings = i; 
    				village = (Village)(editObjectList.get(i));
    				break;
    			}
    		}
    	} 
    	
    	if (!contain) {
        	// Select record Village where id = villageId 
    		Object[] object = searchObject("Village", villageId);
            village = (Village)object[0];     
        }
       
       // Save new value for the column
        if (columnName.equals("nameVillage")) {
            village.setName(oldValue);	                 
            logger.debug("Village - Set selected value for update of attribute Name.");                 	
        } else {
            logger.error("Village - No column defined for name "+ columnName);	                   
        }   
        
        if (contain) {        	
        	editObjectList.set(placings,(Record)village);        
        } else {
        	// add object to list of editing object
            logger.debug("ObjectList - add village");
            editObjectList.add(village);
        }
    }
    
    /**
     *  Rollback data editing concerned with specific territory
     */
    public void undoTerritory() {
       
    	int territoryId = historyChange.getRecordId();	        
        Territory territory = null;
    	int placings = 0;
    	boolean contain = false;
    	for (int i=0; i < editObjectList.size(); i++) {
    		if (editObjectList.get(i) instanceof Territory) {    			
    			int listTerrId = ((Territory)(editObjectList.get(i))).getId();
    			if (territoryId == listTerrId) {
    				contain = true;
    				placings = i; 
    				territory = (Territory)(editObjectList.get(i));
    				break;
    			}
    		}
    	} 
    	
    	if (!contain) {
        	// Select record Territory where id = territoryId 
    		Object[] object = searchObject("Territory", territoryId);
    		territory = (Territory)object[0];     
        }
    	        
       if (columnName.equals("nameTerritory")) {
           territory.setName(oldValue);	                 
           logger.debug("Territory - Set selected value for update of attribute Name.");                 	 
       } else {
           logger.error("Territory - No column defined for name "+ columnName);	                   
       }     
       
       if (contain) {        	
       	   editObjectList.set(placings,(Record)territory);        
       } else {
       	// add object to list of editing object
           logger.debug("ObjectList - add territory");
           editObjectList.add(territory);
       }
    }
    
    /**
     * 
     * @param typeObject string containing information about type of object
     * @param id int containing identifier of record
     * @return object defined by parameters typeObject and id
     */
    public Object[] searchObject(String typeObject, int id) {       
    	SelectQuery query = null;
    	int resultIdObject = 0;
    	Object[] object = null;
    	
        try {
            if (typeObject.equals("Occurrence")){
                query = database.createQuery(Occurrence.class);
                query.addRestriction(PlantloreConstants.RESTR_EQ, Occurrence.ID, null, id , null);
            } else if (typeObject.equals("AuthorOccurrence")){
                query = database.createQuery(AuthorOccurrence.class);
                query.addRestriction(PlantloreConstants.RESTR_EQ, AuthorOccurrence.ID, null, id , null);
            } else if (typeObject.equals("Habitat")){
                query = database.createQuery(Habitat.class);
                query.addRestriction(PlantloreConstants.RESTR_EQ, Habitat.ID, null, id , null);
            } else if (typeObject.equals("Plant")){
                query = database.createQuery(Plant.class);
                query.addRestriction(PlantloreConstants.RESTR_EQ, Plant.ID, null, id , null);
            } else if (typeObject.equals("Author")){
                query = database.createQuery(Author.class);
                query.addRestriction(PlantloreConstants.RESTR_EQ, Author.ID, null, id , null);
            } else if (typeObject.equals("Publication")){
                query = database.createQuery(Publication.class);
                query.addRestriction(PlantloreConstants.RESTR_EQ, Publication.ID, null, id , null);
            } else if (typeObject.equals("Village")){
                query = database.createQuery(Village.class);
                query.addRestriction(PlantloreConstants.RESTR_EQ, Village.ID, null, id, null);
            }  else if  (typeObject.equals("Territory")){
                query = database.createQuery(Territory.class);
                query.addRestriction(PlantloreConstants.RESTR_EQ, Territory.ID, null, id , null);
            } else if (typeObject.equals("Phytochorion")){
                query = database.createQuery(Phytochorion.class);
                query.addRestriction(PlantloreConstants.RESTR_EQ, Phytochorion.ID, null, id , null);
            } else if (typeObject.equals("Metadata")){
                query = database.createQuery(Metadata.class);
                query.addRestriction(PlantloreConstants.RESTR_EQ, Metadata.ID, null, id , null);
            } else {
                logger.error("SearchObject() - Incorrect type of object.");
                
            }
            resultIdObject = database.executeQuery(query);
            object = database.next(resultIdObject);
            database.closeQuery(query);
        } catch(RemoteException e) {
        	logger.error("Remote exception caught in History. Details: "+e.getMessage());
       	    setError(ERROR_SEARCH_OBJECT);
	       	 //Tell observers to update
	         setChanged();
	         notifyObservers();
        } catch(DBLayerException e) {
        	logger.error("Search selected " + typeObject  +" failed. DBLayer exception caught in History. Details: "+e.getMessage());       	                                                   
            setError(ERROR_SEARCH_OBJECT);   
            //Tell observers to update
            setChanged();
            notifyObservers();
        }
       
        //TODO
	   if (object == null) {
		   logger.error("t"+typeObject+ " doesn't contain required data");  
		   setError(ERROR_SEARCH_OBJECT);		   
		   //Tell observers to update
           setChanged();
           notifyObservers();
	   } 	   
       return object; 	       	          	              	        
    }
    
    /**     
     * Seach all authors concerned with specific occurrence
     * @param occurrence 
     * @param idDelete 
     */
    public Object[] getAllAuthors(Occurrence occurrence, int isDelete) {        
        
        SelectQuery query = null;        
        int resultIdAuthors = 0; 
        Object[] objects = null;
        try {
            query = database.createQuery(AuthorOccurrence.class);
            query.addRestriction(PlantloreConstants.RESTR_EQ, AuthorOccurrence.OCCURRENCE, null, occurrence , null);
            if (isDelete == 0) {
                query.addRestriction(PlantloreConstants.RESTR_EQ, AuthorOccurrence.DELETED, null, 0 , null);
            } else {
            	query.addRestriction(PlantloreConstants.RESTR_EQ, AuthorOccurrence.DELETED, null, 2 , null);                               
            }
            resultIdAuthors = database.executeQuery(query);
            int to = database.getNumRows(resultIdAuthors); 
            if (to > 0)
                objects = database.more(resultIdAuthors, 0, to-1);
            database.closeQuery(query);
        } catch(RemoteException e) {
        	logger.error("Remote exception caught in History. Details: "+e.getMessage());
       	    setError(ERROR_SEARCH_AUTHOR);
       	    //Tell observers to update
            setChanged();
            notifyObservers();
        } catch(DBLayerException e) {
        	logger.error("Search authors failed. DBLayer exception caught in History. Details: "+e.getMessage());       	                                                   
            setError(ERROR_SEARCH_AUTHOR); 
            //Tell observers to update
            setChanged();
            notifyObservers();
        }     
        
        //TODO
        if(objects == null ) {
        	logger.error("tAuthorOccurrence doesn't contain required data");  
 		    setError(ERROR_SEARCH_AUTHOR);
 		    //Tell observers to update
            setChanged();
            notifyObservers();
        }
        
       return objects;
    }
    
    /**
     *  Get names of authors for specific occurrence
     *  @param objects  
     */
    public String getAllNameOfAuthors(Object[] objects) {
        if (objects == null)
            return "";
        String allAuthor = "";
        int countResult = objects.length;  
        logger.debug("Authos of occurrence. Results retrieved. Count: "+ countResult);        
        // Cast the results to the AuthorOccurrence objects
        for (int i=0; i<countResult; i++ ) {                    							
            Object[] object = (Object[])objects[i];          
            String author = ((AuthorOccurrence)object[0]).getAuthor().getWholeName();
            String role = ((AuthorOccurrence)object[0]).getRole();
            allAuthor = allAuthor + author + " (" + L10n.getString(PlantloreConstants.ENTITY_AUTHOR + "." + Author.ROLE + "." +role) + ")" + ", ";
        }                        
       return allAuthor;     	    
    }
    
    /**
     *  Update data in the database.
     */
    public void commitUpdate() {    	                
        
        ArrayList<Enum> editType = new ArrayList<Enum>();
        String type;
        Enum key;
        initEditTypeHash();
        
    	int count = editObjectList.size();        
    	for (int i=0; i< count; i++) {
    		try {
    			logger.debug("Object for update: "+ ((Record)editObjectList.get(i)).getId());                         
                type = editObjectList.get(i).getClass().getSimpleName();
                 if (editTypeHash.containsKey(type)) {
                         key = (Enum)editTypeHash.get(type); 
                         if(!editType.contains(key))
                             editType.add(key);
                }                                                     
                database.executeUpdateHistory(editObjectList.get(i));
	        } catch (RemoteException e) {
	        	logger.error("Update data failed.Remote exception caught in History. Details: "+e.getMessage());
	       	    setError(ERROR_UPDATE);
	       	    //Tell observers to update
	            setChanged();
	            notifyObservers();
	        } catch (DBLayerException e) {
	        	logger.error("Update data failed. DBLayer exception caught in History. Details: "+e.getMessage());       	                                                   
	            setError(ERROR_UPDATE); 
	            //Tell observers to update
	            setChanged();
	            notifyObservers();
	        }                
       }    	
    	//Create array of editing object and call notifyObservers
        informMethod(editType);
    }
    
    /**
     *  Create array of editing object and give this array to parrent
     *  @param editType
     */
    public void informMethod(ArrayList<Enum> editType) {
        int count = editType.size();
        Enum[] editTypeArray = new PlantloreConstants.Table[count];
        for(int i=0; i < count; i++) {
            logger.debug("Type of editing object (array for appcore): " + editType.get(i));
            editTypeArray[i] = editType.get(i);
        }
        setChanged(); 
        notifyObservers(editTypeArray);
    }
    
    /**
     *  Clear list of editing object
     */
    public void clearEditObjectList() {
    	editObjectList.clear();
    }
       
    /**
     * Delete selected data from history table. During delete data from table tHistoryChange verify foring key from table tHistory.
     * @param toResult
     * @param typeHistory
     */
    public void deleteHistory(int toResult, boolean typeHistory) {
   	
    	//take from younger record to older record
    	for( int i=0; i < toResult; i++) {
    		if (typeHistory && !markListId.contains(i)) {    			
    			//History of occurence or habitat. The record is not selected.
    			continue;    			
    		}    		    		
    		historyRecord = (HistoryRecord)historyDataList.get(i); 
    		historyChange = historyRecord.getHistoryChange(); 
    		
	    	try {
				database.executeDeleteHistory(historyRecord);
				logger.debug("Deleting historyRecord successfully. Number of result: "+i);
			} catch (RemoteException e) {				
				logger.error("Deleting historyRecord failed.Remote exception caught in History. Details: "+e.getMessage());
	       	    setError(ERROR_DELETE);
	       	    //Tell observers to update
	            setChanged();
	            notifyObservers();
			} catch (DBLayerException e) {				
				logger.error("Deleting historyRecord failed. DBLayer exception caught in History. Details: "+e.getMessage());       	                                                   
	            setError(ERROR_DELETE); 
	            //Tell observers to update
	            setChanged();
	            notifyObservers();
			}
			int countResult = getRelationshipHistoryChange(historyChange.getId());			
			if (countResult == 0) {				
				try {
					database.executeDeleteHistory(historyChange);
					logger.debug("Deleting historyChange successfully.");
				} catch (RemoteException e) {
					logger.error("Deleting historyChange failed.Remote exception caught in History. Details: "+e.getMessage());
		       	    setError(ERROR_DELETE);
		       	    //Tell observers to update
		            setChanged();
		            notifyObservers();
				} catch (DBLayerException e) {
					logger.error("Deleting historyChange failed. DBLayer exception caught in History. Details: "+e.getMessage());       	                                                   
		            setError(ERROR_DELETE); 
		            //Tell observers to update
		            setChanged();
		            notifyObservers();
				}
			} else {
				logger.debug("Exist other record in the table tHistory, whitch has the same value of attribute cChangeId.");
			}
    	}    	
		//Clear lists 
    	markListId.clear();
    	markItem.clear();    		
    } 
    
    /**
     * Get number of record from tHistory, whitch has the value of attribute cChangeId equals id
     * @param id identifier of historyChange record 
     * @return int number of record from tHistory, whitch has the value of attribute cChangeId equals id
     */
    public int getRelationshipHistoryChange(int id){    	
    	SelectQuery query = null;
    	int resultIdChange = 0;
    	int countResult = 100;
        try {
                query = database.createQuery(HistoryRecord.class);
                // Create aliases for table tHistoryChange.      
                query.createAlias("historyChange", "hc");  
                // Add restriction to cChangeId column 
                query.addRestriction(PlantloreConstants.RESTR_EQ, "hc.id", null, id , null);
                resultIdChange = database.executeQuery(query);
                countResult = database.getNumRows(resultIdChange);
                database.closeQuery(query);
        } catch(RemoteException e) {
        	logger.error("Searching historyChange failed.Remote exception caught in History. Details: "+e.getMessage());
       	    setError(ERROR_SEARCH_RECORD);       	   
            setChanged();
            notifyObservers();       	  
        } catch(DBLayerException e) {
        	logger.error("Searching historyChange failed. DBLayer exception caught in History. Details: "+e.getMessage());       	                                                   
            setError(ERROR_SEARCH_RECORD);            
            setChanged();
            notifyObservers();       	  
        }        
       	return countResult;
    }
         
    /**
     * Create message contains information about selected items. 
     */
    public void generateMessageUndo() {    	
    	messageUndo = "";      
    	int count = markItem.size();    	
    	for (int i=0; i<count; i++) {
    		Object[] itemList = (Object[])(markItem.get(i));
    		String item = (String)itemList[0];
    		Integer maxId = (Integer)itemList[1];      		
    		oldValue = ((HistoryRecord)historyDataList.get(maxId)).getOldValue(); 
    		messageUndo = messageUndo + item + "  -->  " + oldValue + "\n";
    	}       
    }    

    /**
     * Create message containinig information about operation which will be realised    
     * @param toDate  Date to which all the changes will be turned back.
     * @return message containinig information about operation which will be realised
     */
    public String getMessageUndoToDate(String toDate) {
        String message = "Všechny změny do " + toDate + " budou navráceny."; //All changes to 12.4.06 will be turned back.
        return message;
    }
    
    /**
     *  Create message containing details about record
     *  @param resultNumber 
     */
    public String getDetailsMessage(int resultNumber) {
        
        //details about object
        String detailsMessage = "";
       
        //data z historie pro konktetni radek tabulky
        historyRecord = (HistoryRecord)historyDataList.get(resultNumber);    		
        historyChange = historyRecord.getHistoryChange();
        tableName = historyRecord.getHistoryColumn().getTableName();
        recordId = historyChange.getRecordId();
                        
        if (tableName.equals(PlantloreConstants.ENTITY_OCCURRENCE) || tableName.equals(PlantloreConstants.ENTITY_HABITAT) || tableName.equals(PlantloreConstants.ENTITY_AUTHOROCCURRENCE)) {           
              //Get details for occurrence
              int occurrenceId = historyChange.getRecordId();
              //Select record Occurrence where id = occurrenceId 
              Object[] objectOcc = searchObject("Occurrence",occurrenceId);
              Occurrence occurrence = (Occurrence)objectOcc[0]; 
              detailsMessage = L10n.getString("History.DetailsOccurrence") + "\n\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_OCCURRENCE + "."+ Occurrence.PLANT) + ": "+ occurrence.getPlant().getTaxon()+"\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_AUTHOROCCURRENCE+"."+ AuthorOccurrence.AUTHOR) + ": " +getAllNameOfAuthors(getAllAuthors(occurrence, 0)) + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_OCCURRENCE +"."+ Occurrence.ISODATETIMEBEGIN) + ": " + occurrence.getIsoDateTimeBegin() +"\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_HABITAT +"."+ Habitat.NEARESTVILLAGE) + ": "+ occurrence.getHabitat().getNearestVillage().getName() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_HABITAT +"."+ Habitat.DESCRIPTION) + ": "+ occurrence.getHabitat().getDescription() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_HABITAT +"."+ Habitat.TERRITORY) + ": "+ occurrence.getHabitat().getTerritory().getName() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_HABITAT +"."+ Habitat.PHYTOCHORION) + ": "+ occurrence.getHabitat().getPhytochorion().getName() +" (Code: " + occurrence.getHabitat().getPhytochorion().getCode() + ")\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_HABITAT +"."+ Habitat.COUNTRY) + ": " + occurrence.getHabitat().getCountry() +"\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_OCCURRENCE +"."+ Occurrence.DATASOURCE) + ": " + occurrence.getDataSource() + "\n";
              if (occurrence.getPublication() != null)
            	  detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_OCCURRENCE +"."+ Occurrence.PUBLICATION) + ": " + occurrence.getPublication().getReferenceCitation() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_OCCURRENCE +"."+ Occurrence.HERBARIUM) + ": " + occurrence.getHerbarium() +"\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_OCCURRENCE +"."+ Occurrence.NOTE) + ": " + occurrence.getNote() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_HABITAT +"."+ Habitat.NOTE) + ": " + occurrence.getHabitat().getNote() +"\n";
        }else if (tableName.equals(PlantloreConstants.ENTITY_HABITAT)) {
        	  //Get details for Publication
              Object[] object = searchObject("Habitat",recordId);
              Habitat habitat = (Habitat)object[0];
              detailsMessage = L10n.getString("History.DetailsOccurrence") + "\n\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_HABITAT +"."+ Habitat.NEARESTVILLAGE) + ": "+ occurrence.getHabitat().getNearestVillage().getName() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_HABITAT +"."+ Habitat.DESCRIPTION) + ": "+ occurrence.getHabitat().getDescription() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_HABITAT +"."+ Habitat.TERRITORY) + ": "+ occurrence.getHabitat().getTerritory().getName() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_HABITAT +"."+ Habitat.PHYTOCHORION) + ": "+ occurrence.getHabitat().getPhytochorion().getName() +" (Code: " + occurrence.getHabitat().getPhytochorion().getCode() + ")\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_HABITAT +"."+ Habitat.COUNTRY) + ": " + occurrence.getHabitat().getCountry() +"\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_HABITAT +"."+ Habitat.NOTE) + ": " + occurrence.getHabitat().getNote() +"\n";
    	}else if (tableName.equals(PlantloreConstants.ENTITY_PUBLICATION)) {
              //Get details for Publication
              Object[] object = searchObject("Publication",recordId); 
              Publication publication = (Publication)object[0];
              detailsMessage = L10n.getString("History.DetailsPublication") + "\n\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_PUBLICATION +"."+ Publication.COLLECTIONNAME) + ": " + publication.getCollectionName() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_PUBLICATION +"."+ Publication.COLLECTIONYEARPUBLICATION) + ": " + publication.getCollectionYearPublication() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_PUBLICATION +"."+ Publication.JOURNALNAME) + ": " + publication.getJournalName() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_PUBLICATION +"."+ Publication.JOURNALAUTHORNAME) + ": " + publication.getJournalAuthorName() +"\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_PUBLICATION +"."+ Publication.URL) + ": " + publication.getUrl() +"\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_PUBLICATION +"."+ Publication.NOTE) + ": " + publication.getNote() + "\n";
        } else if (tableName.equals(PlantloreConstants.ENTITY_AUTHOR)) {
              //Get details for Author
              Object[] object = searchObject("Author",recordId);   
              Author author = (Author)object[0];
              detailsMessage = L10n.getString("History.DetailsAuthor") + "\n\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_AUTHOR +"."+ Author.WHOLENAME)+ ": " + author.getWholeName() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_AUTHOR +"."+ Author.ORGANIZATION)+ ": "  + author.getOrganization() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_AUTHOR +"."+ Author.ROLE)+ ": " + author.getRole() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_AUTHOR +"."+ Author.ADDRESS)+ ": "  + author.getAddress() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_AUTHOR +"."+ Author.EMAIL)+ ": "  + author.getEmail() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_AUTHOR +"."+ Author.PHONENUMBER)+ ": "  + author.getPhoneNumber() + "\n";            
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_AUTHOR +"."+ Author.URL)+ ": "  + author.getUrl() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_AUTHOR +"."+ Author.NOTE)+ ": "  + author.getNote() + "\n";
        }  else if (tableName.equals(PlantloreConstants.ENTITY_METADATA)) {
             //Get details for Metadata
              Object[] object = searchObject("Metadata",recordId);   
              Metadata metadata = (Metadata)object[0];
              detailsMessage = L10n.getString("History.DetailsMetadata") + "\n\n";
              detailsMessage = detailsMessage + L10n.getString("History.DetailsMetadata.Institution") + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_METADATA +"."+ Metadata.SOURCEINSTITUTIONID) + ": " + metadata.getSourceInstitutionId() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_METADATA +"."+ Metadata.OWNERORGANIZATIONABBREV) + ": " + metadata.getOwnerOrganizationAbbrev() + "\n\n";
              detailsMessage = detailsMessage + L10n.getString("History.DetailsMetadata.TechnicalContact")+ "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_METADATA +"."+ Metadata.TECHNICALCONTACTNAME) + ": " + metadata.getTechnicalContactName() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_METADATA +"."+ Metadata.TECHNICALCONTACTEMAIL) + ": " + metadata.getTechnicalContactEmail() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_METADATA +"."+ Metadata.TECHNICALCONTACTADDRESS) + ": " + metadata.getTechnicalContactAddress() + "\n\n";
              detailsMessage = detailsMessage + L10n.getString("History.DetailsMetadata.ContentContact") + " \n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_METADATA +"."+ Metadata.CONTENTCONTACTNAME) + ": " + metadata.getContentContactName() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_METADATA +"."+ Metadata.CONTENTCONTACTEMAIL) + ": " + metadata.getContentContactEmail() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_METADATA +"."+ Metadata.CONTENTCONTACTADDRESS) + ": " + metadata.getContentContactAddress() + "\n\n";
              detailsMessage = detailsMessage + L10n.getString("History.DetailsMetadata.Project") + " \n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_METADATA +"."+ Metadata.DATASETTITLE) + ": " + metadata.getDataSetTitle() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_METADATA +"."+ Metadata.DATASETDETAILS) + ": " + metadata.getDataSetDetails() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_METADATA +"."+ Metadata.SOURCEID) + ": " + metadata.getSourceId() +"\n\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_METADATA +"."+ Metadata.DATECREATE) + ": " +metadata.getDateCreate() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_METADATA +"."+ Metadata.DATEMODIFIED) + ": " +metadata.getDateModified() + "\n\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_METADATA +"."+ Metadata.RECORDBASIS) + ": " + metadata.getRecordBasis() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_METADATA +"."+ Metadata.BIOTOPETEXT) + ": " + metadata.getBiotopeText() + "\n";                      
        } else if (tableName.equals(PlantloreConstants.ENTITY_PHYTOCHORION)) {
              //Get details for Phytochorion
              Object[] object = searchObject("Phytochorion",recordId); 
              Phytochorion  phytochorion = (Phytochorion)object[0];
              detailsMessage = L10n.getString("History.DetailsPhytochorion") + "\n\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_PHYTOCHORION +"."+ Phytochorion.NAME) + ": " + phytochorion.getName() + "\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_PHYTOCHORION +"."+ Phytochorion.CODE) + ": " + phytochorion.getCode() + "\n";
        } else if (tableName.equals(PlantloreConstants.ENTITY_TERRITORY)) {
              //Get details for Territory
              Object[] object = searchObject("Territory",recordId); 
              Territory territory = (Territory)object[0];
              detailsMessage = L10n.getString("History.DetailsTerritory") + "\n\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_TERRITORY +"."+ Territory.NAME) + ": " + territory.getName() + "\n";
        } else if (tableName.equals(PlantloreConstants.ENTITY_VILLAGE)) {
              //Get details for Village
              Object[] object = searchObject("Village",recordId);  
              Village village = (Village)object[0];
              detailsMessage = L10n.getString("History.detailsVillage") + "\n\n";
              detailsMessage = detailsMessage + L10n.getString(PlantloreConstants.ENTITY_VILLAGE +"."+ Village.NAME) + ": " + village.getName() + "\n";
        } else {
            logger.error("No table defined");
            detailsMessage = "No details for selected row.";
        }        
        
        logger.debug("detailsMessage: "+ detailsMessage);
        return detailsMessage;
    }
    
    /**
     *  Delete all date from tables tHistorz and tHistoryChange
     */
    public void clearHistory() {        
        
        try {
            //delete data from table tHistory
            database.conditionDelete(HistoryRecord.class, HistoryRecord.ID, ">", 0);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
        
        try {            
            //delete data from  table tHistoryChange
            database.conditionDelete(HistoryChange.class, HistoryChange.ID, ">", 0);
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        
    }
    
    /**
     * Delete all record with cdelete equals 1 from table tAuthors, tAuthorsOccurrences, tOccurrences, tHabitats, tPublications
     */
    public void clearDatabase() {
        try {
            
            database.conditionDelete(Author.class, Author.DELETED, "=", 1);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
        try {
            database.conditionDelete(AuthorOccurrence.class, AuthorOccurrence.DELETED, "=", 1);
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        try {
            database.conditionDelete(Occurrence.class, Occurrence.DELETED, "=", 1);
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        try {
            database.conditionDelete(Habitat.class, Habitat.DELETED, "=", 1);
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        try {
            database.conditionDelete(Publication.class, Publication.DELETED, "=", 1);
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
    
     //***************************//
    //****Init Hashtable*********//
    //**************************//
    
    private void initAuthorsOccurrenceHash() {
        authorsOccurrenceHash = new Hashtable<String, Integer>(3);
        authorsOccurrenceHash.put(AuthorOccurrence.AUTHOR, 1);
        authorsOccurrenceHash.put(AuthorOccurrence.ROLE, 2);
        authorsOccurrenceHash.put(AuthorOccurrence.NOTE, 3);
    }
    
    private void initOccurrenceHash() {
    	occurrenceHash = new Hashtable<String, Integer>(10); 
        occurrenceHash.put(Occurrence.PLANT, 1);
        occurrenceHash.put(Occurrence.YEARCOLLECTED, 2);
        occurrenceHash.put(Occurrence.MONTHCOLLECTED, 3);
        occurrenceHash.put(Occurrence.DAYCOLLECTED, 4);
        occurrenceHash.put(Occurrence.TIMECOLLECTED, 5);           
        occurrenceHash.put(Occurrence.DATASOURCE, 6);
        occurrenceHash.put(Occurrence.HERBARIUM, 7);        
        occurrenceHash.put(Occurrence.NOTE, 8);
        occurrenceHash.put(Occurrence.PUBLICATION, 9);       
        occurrenceHash.put(Occurrence.METADATA, 10);
        occurrenceHash.put(Occurrence.HABITAT, 11);
    }    
    
    private void initHabitatHash() {
    	habitatHash = new Hashtable<String, Integer>(11);         
        habitatHash.put(Habitat.QUADRANT, 1);
        habitatHash.put(Habitat.DESCRIPTION, 2);
        habitatHash.put(Habitat.COUNTRY, 3);
        habitatHash.put(Habitat.ALTITUDE, 4);
        habitatHash.put(Habitat.LATITUDE, 5);
        habitatHash.put(Habitat.LONGITUDE, 6);      
        habitatHash.put(Habitat.NEARESTVILLAGE, 7);      
        habitatHash.put(Habitat.PHYTOCHORION, 8);
        //habitatHash.put(Phytochorion.CODE, 8);
        habitatHash.put(Habitat.TERRITORY,9);
        habitatHash.put(Habitat.NOTE, 10);
    }    
    
    private void initMetadataHash() {
        metadataHash = new Hashtable<String, Integer>(16);
        metadataHash.put(Metadata.TECHNICALCONTACTNAME, 1);
        metadataHash.put(Metadata.TECHNICALCONTACTEMAIL, 2);
        metadataHash.put(Metadata.TECHNICALCONTACTADDRESS, 3);
        metadataHash.put(Metadata.CONTENTCONTACTNAME, 4);
        metadataHash.put(Metadata.CONTENTCONTACTEMAIL, 5);
        metadataHash.put(Metadata.CONTENTCONTACTADDRESS, 6);
        metadataHash.put(Metadata.DATASETTITLE, 7);
        metadataHash.put(Metadata.DATASETDETAILS, 8);
        metadataHash.put(Metadata.SOURCEINSTITUTIONID, 9);
        metadataHash.put(Metadata.SOURCEID, 10);
        metadataHash.put(Metadata.OWNERORGANIZATIONABBREV, 11);                
        metadataHash.put(Metadata.RECORDBASIS, 12);
        metadataHash.put(Metadata.BIOTOPETEXT, 13);        
    }
    
    private void initPublicationHash() {
        publicationHash = new Hashtable<String, Integer>(6);
        publicationHash.put(Publication.COLLECTIONNAME, 1);
        publicationHash.put(Publication.COLLECTIONYEARPUBLICATION, 2);
        publicationHash.put(Publication.JOURNALNAME, 3);
        publicationHash.put(Publication.JOURNALAUTHORNAME, 4);
        publicationHash.put(Publication.REFERENCEDETAIL, 5);
        publicationHash.put(Publication.URL, 6);      
    }
    
    private void initAuthorHash() {
        authorHash = new Hashtable<String, Integer>(7);
        authorHash.put(Author.WHOLENAME, 1);        
        authorHash.put(Author.ORGANIZATION, 2);
        authorHash.put(Author.ROLE, 3);
        authorHash.put(Author.PHONENUMBER, 4);
        authorHash.put(Author.EMAIL, 5);
        authorHash.put(Author.URL, 6);
        authorHash.put(Author.NOTE, 7);        
    }              
    
    private void initEditTypeHash() {
        editTypeHash = new Hashtable<String, Enum>(5);
        editTypeHash.put("Occurrence", PlantloreConstants.Table.OCCURRENCE);                
        editTypeHash.put("Habitat", PlantloreConstants.Table.OCCURRENCE);
        editTypeHash.put("AuthorOccurrence", PlantloreConstants.Table.OCCURRENCE);                
        editTypeHash.put("Author", PlantloreConstants.Table.AUTHOR);
        editTypeHash.put("Metadata", PlantloreConstants.Table.METADATA);
        editTypeHash.put("Publication", PlantloreConstants.Table.PUBLICATION);
        editTypeHash.put("Village", PlantloreConstants.Table.VILLAGE);
        editTypeHash.put("Territory", PlantloreConstants.Table.TERRITORY);
        editTypeHash.put("Phytochorion", PlantloreConstants.Table.PHYTOCHORION);        
    }
       
    //****************************//
    //****Get and set metods*****//
    //**************************//
    
    /**
     *  Set an error flag.
     *  @param msg  message explaining the error which occured
     */
    public void setError(String msg) {
        this.error = msg;
    }
    
    /**
     *  Checks whether an error flag is set.
     *  return true if an error occured and error message is available, false otherwise
     */
    public boolean isError() {
        if (this.error != null) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     *  Get error message for the error that occured
     *  @return message explaining the error which occured
     */
    public String getError() {
        return this.error;
    }
    
    
    public Object getData() {
    	return data;
    }
    
    public void setData(Object data) {
    	this.data = data;
    }
    
    /**
     * @return true if all recorda were selected.
     */
    public boolean getSelectAll() {
		  return this.selectAll;		  
	   }

    /**
     * Set information if all records were selected. 
     * @param selectAll
     */
	 public void setSelectAll(boolean selectAll) {
		  this.selectAll = selectAll;		  
	 } 
    
	 public HashSet getMarkListId() {
		  return this.markListId;		  
	   }

	 public void setMarkListId(HashSet markListId) {
		  this.markListId = markListId;		  
	 } 
	 
    public ArrayList<Object[]> getMarkItem() {
		  return this.markItem;		  
	   }

	 public void setMarkItem(ArrayList<Object[]> markItem) {
		  this.markItem = markItem;		  
	 } 
    
    
    //id vysledku po vyhledavani v db
    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
    
    public int getResultId() {
        return this.resultId;
    }
    
    public int getResultRows() {
        int resultCount = 0;
        if (resultId != 0) try {
                resultCount = database.getNumRows(resultId);        	
        } catch(RemoteException e) {
                System.err.println("Kdykoliv se pracuje s DBLayer nebo SelectQuery, musite hendlovat RemoteException");
        }
        return resultCount;
    }

    public ArrayList<HistoryRecord> getHistoryDataList() {
              return this.historyDataList;		  
       }

     public void setHistoryDataList(ArrayList<HistoryRecord> historyDataList) {
              this.historyDataList = historyDataList;		  
     } 
    
     public String getCurrentDisplayRows() {
		  return this.displayRow;		  
	   }

     public void setCurrentDisplayRows(String displayRow) {
              this.displayRow = displayRow;		  
     } 
     
     public String getMessageUndo() {
		  return this.messageUndo;		  
	   }

     public void setMessageUndo(String messageUndo) {
              this.messageUndo = messageUndo;		  
     } 
     
         /**
     *  Get index of the first row currently displayed in the list of record changes. This is an index in the results returned by a search query.
     *  @return index of the first row currently displayed in the list of history
     */
    public int getCurrentFirstRow() {
        return this.currentFirstRow;
    }
    
    /**
     *  Set index of the forst row currently displayed in the list of record changes. This is an index in the results returned by a search query.
     *  @param row index of the first row currently displayed in the list of history
     */
    public void setCurrentFirstRow(int row) {
        this.currentFirstRow = row;
    }    

    /**
     *  Get number of rows to be displayed on one page.
     *  @return number of rows to be displayed per page
     */
    public int getDisplayRows() {
        return this.displayRows;
    }
    
    /**
     *  Set number of rows to be displayed on one page
     *  @param rows number of rows ro be displayed per page
     */
    public void setDisplayRows(int rows) {
        this.displayRows = rows;
    }
    
    public String getNamePlant() {
		  return this.namePlant;
	   }

    public void setNamePlant(String namePlant) {
		  this.namePlant = namePlant;
	}   
    
    public String getNameAuthor() {
		  return this.nameAuthor;
	   }

	 public void setNameAuthor(String nameAuthor) {
		  this.nameAuthor = nameAuthor;
	 } 
	 
	 public String getLocation() {
		  return this.location;
	   }
	
	 public void setLocation(String location) {
		  this.location = location;
	}    

	/**
	*   Get date and time when the reccord was changed
	*   @return date and time when the reccord was changed
	*/         
    public java.util.Date getWhen() {
	  return this.when;
    }
 
   /**
	*   Set date and time when the reccord was changed
	*   @param when date and time when the reccord was changed
	*/            
    public void setWhen(java.util.Date when) {    	
    	this.when = when;    	 
    }     	  
 
	/**
	*   Get name of user who did changed
	*   @return name of user who did changed
	*/
    public String getNameUser() {
	  return this.nameUser;
    }

    /**
	*   Set name of user who did changed
	*   @param nameUser string containing name of user who did changed	
	*/
    public void setNameUser(String nameUser) {
	  this.nameUser = nameUser;
    }  
    
}
