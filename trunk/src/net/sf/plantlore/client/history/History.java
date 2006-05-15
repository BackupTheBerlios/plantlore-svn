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
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.HistoryChange;
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
public class History {
    
      /** Instance of a logger */
    private Logger logger;      
    /** Instance of a database management object */
    private DBLayer database;   
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
    private ArrayList<HistoryRecord> historyDataList = new ArrayList();     
    // seznam editovanych objektu (potrebny pro hromadne potvrzeni update)
    private ArrayList<Record> editObjectList = new ArrayList<Record>();
    // informace pro uzivatele o record undo
    private String messageUndo;

    //************************************pro historii jednoho nalezu*********************/
    //seznam id vsech oznacenych polozek
    private HashSet markListId = new HashSet();
    //Seznam Item + maxIdItem (nejstarsi oznacene id pro dany Item=sloupec)
    private ArrayList<Object[]> markItem = new ArrayList();
    //Informuje o tom, zda byla zvolena volba "SelectAll"
    private boolean selectAll;    
    
    //*********************Record of history, ... ***************************************//    
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
    private java.util.Date when = new Date();	
    /** Old value of attribute*/    
    private String oldValue;
    /** New value of attribute*/
    //private String newValue;
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
    /** Mapping of entities */           
    private Hashtable<String, Integer> authorsOccurrenceHash;
    private Hashtable<String, Integer> occurrenceHash; 
    private Hashtable<String, Integer> authorHash;
    private Hashtable<String, Integer> habitatHash;
    private Hashtable<String, Integer> metadataHash;
    private Hashtable<String, Integer> publicationHash;
    //pro territory, village a phytochorion neni treba tvorit mapovani
    
    /**
     * Creates a new instance of History - history of whole database
     */
    public History(DBLayer database) {
          
       logger = Logger.getLogger(this.getClass().getPackage().getName());	 
       this.database = database;
       
       //nacist vsechny data z historie -->bez podminky, jen je seradit podle casu
       searchWholeHistoryData();
       //opet funkci pro vyzadani si dat postupne
       processResult(1, displayRows);
    }
    
    /**  
     *  Creates a new instance of History - history of specific occurrence 
     *  @param database Instance of a database management object
     *  @param idOcc
     * */
    public History(DBLayer database, int idOcc)
    {
       logger = Logger.getLogger(this.getClass().getPackage().getName());	 
       this.database = database;       
       
       SelectQuery query = null;
       int resultId = 0;
       Object[] objectOccurrence = null;
       Object[] objHis = null;
       
       try {
       	    query = database.createQuery(Occurrence.class);
       	    query.addRestriction(PlantloreConstants.RESTR_EQ, Occurrence.ID, null, 1, null);            
            resultId = database.executeQuery(query);
            objectOccurrence = database.more(resultId, 0, 0);       
            objHis = (Object[])objectOccurrence[0];                            
       } catch(RemoteException e) {
       	    System.err.println("RemoteException - History(), createQuery");       	  
       } catch(DBLayerException e) {
       	    System.err.println("DBLayerException - History(), createQuery");       	  
            logger.error("Processing search occurrence results failed: "+e.toString());            
       }                                          

       //zjiskani zaznamu, pro ktery chceme historii
       occurrence = ((Occurrence)objHis[0]);
       
       //FIXME: bude nutno dovyhledavat autory
       setNameAuthor("Lada");
       setNamePlant(occurrence.getPlant().getTaxon());       
       setLocation(occurrence.getHabitat().getNearestVillage().getName());
       
       try {           
            database.closeQuery(query);
       } catch(RemoteException e) {
       	    System.err.println("RemoteException");
       }           
       
       //Searching for information about data entries concerned with specified occurrence
       searchInsertInfo();
       //Searching for information about data editing concerned with specified occurrence
       searchEditHistory();
       //Process results of a search "edit" query 
       processResult(1,displayRows);
    }	
    
    
    /**
     *  Searches for information about data entries concerned with specified occurrence.   
     */
    public void searchInsertInfo() {
            
       // Create new Select query
       SelectQuery query = null;
       try {
       	    query = database.createQuery(HistoryChange.class);
       	    query.addRestriction(PlantloreConstants.RESTR_EQ, HistoryChange.OCCURRENCE, null, occurrence, null);
       	    query.addRestriction(PlantloreConstants.RESTR_EQ, HistoryChange.OPERATION, null, HistoryChange.HISTORYCHANGE_INSERT, null);
       } catch(RemoteException e) {
       	    System.err.println("RemoteException- searchInsertInfo(), createQuery");       	  
       } catch(DBLayerException e) {
       	    System.err.println("DBLayerException - searchInsertInfo(), createQuery");
       }            
       
       int resultIdInsert = 0;
       try {
           // Execute query                    
           resultIdInsert = database.executeQuery(query); 
           // Save "insert" history data
           setInsertResult(resultIdInsert);
           database.closeQuery(query);           
       } catch (DBLayerException e) {
           // Log and set an error                   
           logger.error("Searching history data with condition 'operation = insert' failed. Unable to execute search query.");          
       } catch (RemoteException e) {		 
    	   System.err.println("RemoteException- searchInsertInfo(), executeQuery");
       }
    }
    
    
    /**     
     * Searches for information about data editing concerned with specified occurrence. 
     */
     
    public void searchEditHistory()
    {  
    	    	
        //Create new Select query
        SelectQuery query = null;       

    	//  Select data from tHistory table
        try {
		query = database.createQuery(HistoryRecord.class);
		// Create aliases for table tHistoryChange.      
	        query.createAlias("historyChange", "hc");        
	        // Add restriction to COPERATION column of tJistoryChange table
	        query.addRestriction(PlantloreConstants.RESTR_EQ, "hc.operation", null, HistoryChange.HISTORYCHANGE_EDIT, null);        
	        query.addRestriction(PlantloreConstants.RESTR_EQ, "hc.occurrence", null, occurrence, null);    	
	        query.addOrder(PlantloreConstants.DIRECT_DESC, "hc.when");
	} catch (RemoteException e) {
		System.err.println("RemoteException - searchEditHistory(), createQuery");
	} catch (DBLayerException e) {
		System.err.println("DBLayerException - searchEditHistory(), createQuery");
	}                

        int resultIdEdit = 0;
        try {
            // Execute query                    
            resultIdEdit = database.executeQuery(query); 
            // Save "edit" history data
            setResultId(resultIdEdit);
        } catch (DBLayerException e) {
            // Log and set an error                   
            logger.error("Searching history data with condition 'operation = edit' failed. Unable to execute search query.");           
        } catch (RemoteException e) { 		   
     	   System.err.println("RemoteException- searchEditHistory(), executeQuery");
	 	}    
        
        //zde nejde zavrit session closeQuery
    }
    
    
    /**     
     * Sets information about data (date, name of user) entries concerned with specified occurrence 
     * @param result result of a database operation INSERT. Result has one row.
     */
    public void setInsertResult(int resultIdInsert) {
   	    	
    	if (getResultRows() > 1) {                
            logger.error("Too many results for searching insert operation.");  
    	}
            	
    	logger.debug("Retrieving query results."); 
    	Object[] objectHistory = null;
        try {
        	 // Retrieve selected row interval         	
         	try {
         		objectHistory = database.more(resultIdInsert, 0, 0);  
         	} catch(RemoteException e) {             	
             	logger.debug("RemoteException- setInsertResult, more");
             	return;
             }   
         	Object[] objHis = (Object[])objectHistory[0];                 
         	setWhen(((HistoryChange)objHis[0]).getWhen());
         	setNameUser(((HistoryChange)objHis[0]).getWho().getWholeName());         	
        } catch (DBLayerException e) {         
            logger.error("Processing search (inserting) results failed: "+e.toString());            
        }       
    }
    
    /**
     *
     */
    public void searchWholeHistoryData() {
        
        //Create new Select query
        SelectQuery query = null;       

    	//  Select data from tHistory table
        try {
			query = database.createQuery(HistoryRecord.class);
			// Create aliases for table tHistoryChange.
			query.createAlias("historyChange", "hc");
			// sort by date/time
			query.addOrder(PlantloreConstants.DIRECT_DESC, "hc.when");
	} catch (RemoteException e) {
                System.err.println("RemoteException - searchWholeHistoryData(), createQuery");
	} catch (DBLayerException e) {
                System.err.println("DBLayerException - searchWholeHistoryData(), createQuery");
        }

    	
        int resultId = 0;
        try {
            // Execute query                    
            resultId = database.executeQuery(query);
            // Save "edit" history data
            setResultId(resultId);    
        } catch (DBLayerException e) {                            
            logger.error("Searching whole history data failed. Unable to execute search query.");           
        } catch (RemoteException e) { 		   
     	   System.err.println("RemoteException- searchWholeHistoryData(), executeQuery");
        }   
        
        //zde nelze zavrit session closeQuery
    }
    
   /**
     * Process results of a search query. Retrieves results using the database management object (DBLayer) and stores them in the data field of the class. 
     * @param fromTable number of the first row to show in table. Number of the first row to retraieve is 1.
     * @param count number of rows to retrieve 
     */
    public void processResult(int fromTable, int count) {
        
        if (this.resultId != 0) {
            int currentRow = getResultRows();
            logger.debug("Rows in the result: "+currentRow);
            logger.debug("Max available rows: "+(fromTable+count-1));
           
            // Find out how many rows we can retrieve - it cannot be more than number of rows in the result
            int to = Math.min(currentRow, fromTable+count-1);           
            if (to <= 0) {
            	historyDataList = new ArrayList<HistoryRecord>(); 
            	setDisplayRows(0);
            	setCurrentDisplayRows("0-0");
            } else {
                logger.debug("Retrieving query results: 1 - "+to);
                setCurrentDisplayRows(fromTable+ "-" + to);
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
                    //Update current first displayed row (only if data retrieval was successful)
                    setCurrentFirstRow(fromTable); 
                } catch (DBLayerException e) {                  
                    logger.error("Processing search results failed: "+e.toString());            
                }             
            }
        }         
    }
    
    /**
     *
     */
    public void undoToDate(int toResult) {
        
        //Inicalization of hashTable
        initAuthorsOccurrenceHash();
    	initOccurrenceHash();
    	initHabitatHash();   
        initPublicationHash();
        initAuthorHash();      
        initMetadataHash();
        	
    	//number of result
    	//int countResult = this.historyDataList.size();
    	// Pomocne hodnoty pro zjisteni zda zmena ovlivni vice nalezu
    	//relationship = false;
    	//editHabitat = false;
    	
    	//take from younger record to older record, undo tu selected row
    	for( int i=0; i < toResult; i++) {
    		
    		//init history data 
    		historyRecord = (HistoryRecord)historyDataList.get(i);    		
    		historyChange = historyRecord.getHistoryChange();
    		tableName = historyRecord.getHistoryColumn().getTableName();
                recordId = historyChange.getRecordId();
                operation = historyChange.getOperation();
    		                                                
               /** 
                * Pri insertu a editu nedohledavam column --> prvne rozdelit podle operace a pro edit dale rozdelit podle column
                */
                if (operation == HistoryChange.HISTORYCHANGE_INSERT) {
                    undoInsertDelete(0);
                } else if (operation == HistoryChange.HISTORYCHANGE_EDIT) {
                    undoEdit();
                } else if (operation == HistoryChange.HISTORYCHANGE_DELETE) {
                    undoInsertDelete(1);
                } else {
                    logger.error("Incorrect opreration code: "+ operation);
                }                
        }
    }
    
    /**
     *  Volani UNDO z historie pro jeden nalez
     */
    public void undoSelected() {
    	
    	// Inicalization of hashTable
        initAuthorsOccurrenceHash();
    	initOccurrenceHash();
    	initHabitatHash();    	  
        	
    	//number of result
    	int countResult = getResultRows();
    	// Pomocne hodnoty pro zjisteni zda zmena ovlivni vice nalezu    	
    	
    	//take from younger record to older record
    	for( int i=0; i < countResult; i++) {
    		if (! markListId.contains(i)) {
    			continue;
    		}
    		
    		// init history data about edit of record
    		historyRecord = (HistoryRecord)historyDataList.get(i);    		
    		historyChange = historyRecord.getHistoryChange();
    		tableName = historyRecord.getHistoryColumn().getTableName();    		  		    			           
                recordId = historyChange.getRecordId();           	   
                operation = historyChange.getOperation();
       
            
            //zavolani funkce, ktera undo pro operaci edit
            undoEdit();    		
    	}
    	//generated information form user
    	generateMessageUndo();
    }
    
    /**
     * ??? Habitat - nemuselo by se zaznamenavat cDelete
     * v tabulkach Phytochorion, Village, Territory nebude možno mazat zaznamy (ani se nepredpoklada, ze by k této operaci mělo dochazet), proto v nich není ani CDELETE.
     * delete == 1 ... smazat
     * delete == 0 ... obnovit
     */
    public void undoInsertDelete(int isDelete) {
        if (tableName.equals(PlantloreConstants.ENTITY_OCCURRENCE)){
             Object[] object = searchObject("Occurrence",recordId);             
             Occurrence occurrence = (Occurrence)object[0];
             occurrence.setDeleted(isDelete);
        } else if (tableName.equals(PlantloreConstants.ENTITY_AUTHOROCCURRENCE)) {
             Object[] object = searchObject("AuthorOccurrence",recordId);  
             AuthorOccurrence authorOccurrence = (AuthorOccurrence)object[0];
             authorOccurrence.setDeleted(isDelete);             
       } else if (tableName.equals(PlantloreConstants.ENTITY_HABITAT)) {
            //jeste rozmyslet, zda to tu bude
               Object[] object = searchObject("Habitat",recordId);  
               Habitat habitat = (Habitat)object[0];
               habitat.setDeleted(isDelete);
        } else if (tableName.equals(PlantloreConstants.ENTITY_METADATA)) {
             Object[] object = searchObject("Metadata",recordId);  
             Metadata metadata = (Metadata)object[0];
             metadata.setDeleted(isDelete);
        } else if (tableName.equals(PlantloreConstants.ENTITY_PUBLICATION)) {
             Object[] object = searchObject("Publication",recordId);  
             Publication publication = (Publication)object[0];
             publication.setDeleted(isDelete);
        } else if (tableName.equals(PlantloreConstants.ENTITY_AUTHOR)) {
             Object[] object = searchObject("Author",recordId);   
             Author author = (Author)object[0];
             author.setDeleted(isDelete);
        }  else {
            logger.error("No table defined");
        }
    }
    
    /**
     *
     */
    public void undoEdit() {
        
        //init history data about edit of record
        columnName = historyRecord.getHistoryColumn().getColumnName();    		    			
        oldRecordId = historyChange.getOldRecordId();                
        occurrenceId = historyChange.getOccurrence().getId();		           
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
            logger.error("No table defined");
        }
    }

    
      /**
     *
     */
    public void undoAuthorOccurrence() {
        
        Object[] object = searchObject("AuthorOccurrence", recordId);
        AuthorOccurrence authorOccurrence = (AuthorOccurrence)object[0];        
        
        //test, zda jiz dany zaznam byl editovan
        boolean objectList = editObjectList.contains((Record)authorOccurrence); 
        if (!objectList) {
        	//pridani objektu do listu - informace o tom, ze byl dany objekt editovan
            editObjectList.add((Record)authorOccurrence);
        }
        logger.debug("editObjectList.contains: "+objectList);
        logger.debug("authorOccurrence: "+ authorOccurrence.getId());
        logger.debug("columnName: "+columnName);
        
       // Get a specified number of columnName from habitat mapping.
        int columnConstant;
        if (authorsOccurrenceHash.containsKey(columnName)) {
                 columnConstant = (Integer)authorsOccurrenceHash.get(columnName); 
        } else {
             columnConstant = 0;
        }        	    			
      
        // Save new value for the column        		
        switch (columnConstant) {
            case 1:  //Author
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
    }
    
    /**
     *
     */
    public void undoOccurrence() {
        
        //zaznam v ramci, ktereho doslo k editaci tabulky tOccurrences
        occurrence = historyChange.getOccurrence();               
        
        boolean objectList = editObjectList.contains((Record)occurrence);
        if (!objectList) {
        	//pridani objektu do listu - informace o tom, ze byl dany objekt editovan
            logger.debug("ObjectList... add occurrences");
            editObjectList.add((Record)occurrence);
        }
        
        logger.debug("editObjectList: "+objectList);
        logger.debug("OccurrenceID: "+occurrence.getId());
        logger.debug("columnName: "+columnName);
                
        if (occurrenceId != recordId){
            logger.error("Inccorect information in history tables --> occurrenceId != recordId ... Incorrect identifier of Occurrence.");
        }

        //Get a specified number of columnName from occurrence mapping.
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
                occurrence.setPlant(plant);
                logger.debug("Set selected value for update of attribute Taxon.");	
            } else {
                 logger.error("UNDO - Incorrect oldRecordId for Phytochoria.");
            } 
            break;
        case 2: //Year	
            //Set old value to attribute Year          		
                occurrence.setYearCollected(Integer.parseInt(oldValue));
                logger.debug("Set selected value for update of attribute Year.");
                //Update attribute isoDateTimeBegin (Year + Mont + Day + Time)		                	                		
                isoDateTime.setTime(occurrence.getTimeCollected());
                isoDateTime.set(Integer.parseInt(oldValue),occurrence.getMonthCollected(),occurrence.getDayCollected());
                occurrence.setIsoDateTimeBegin(isoDateTime.getTime());	                	              	            	
                break;
        case 3: //Month 
                // Set old value to attribute Month 
                occurrence.setMonthCollected(Integer.parseInt(oldValue));
                logger.debug("Set selected value for update of attribute Month.");
                // Update attribute isoDateTimeBegin (Year + Mont + Day + Time)		                	
                isoDateTime.setTime(occurrence.getTimeCollected());
                isoDateTime.set(occurrence.getYearCollected(), Integer.parseInt(oldValue), occurrence.getDayCollected());
                occurrence.setIsoDateTimeBegin(isoDateTime.getTime());              		
            break;
        case 4: //Day	                	
                // Set old value to attribute Day            		
                occurrence.setDayCollected(Integer.parseInt(oldValue));
                logger.debug("Set selected value for update of attribute Day.");
                // Update attribute isoDateTimeBegin (Year + Mont + Day + Time)		                	
                isoDateTime.setTime(occurrence.getTimeCollected());
                isoDateTime.set(occurrence.getYearCollected(), occurrence.getMonthCollected(), Integer.parseInt(oldValue));
                occurrence.setIsoDateTimeBegin(isoDateTime.getTime());
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
                occurrence.setTimeCollected(time);
                logger.debug("Set selected value for update of attribute Time.");
                // Update attribute isoDateTimeBegin (Year + Mont + Day + Time)		                	
                isoDateTime.setTime(time);
                isoDateTime.set(occurrence.getYearCollected(), occurrence.getMonthCollected(), occurrence.getDayCollected());
                occurrence.setIsoDateTimeBegin(isoDateTime.getTime());
            break;
        case 6: //Source	                	
                // Set old value to attribute Source 
                occurrence.setDataSource(oldValue);
                logger.debug("Set selected value for update of attribute DataSource.");		                	            	
                break;
        case 7: //Herbarium
                // Set old value to attribute Herbarium
                occurrence.setHerbarium(oldValue);
                logger.debug("Set selected value for update of attribute Herbarium.");	                		          
            break;
        case 8: //Note occurrence	
                // Set old value to attribute Note occurence	                	
                occurrence.setNote(oldValue);
                logger.debug("Set selected value for update of attribute NoteOccurrence.");	                		        	
                break;
        case 9: //Publication  
                //Select record Publication where id = oldRocordId 
                if (oldRecordId > 0){
                    Object[] objectPubl = searchObject("Publication",oldRecordId);
                    Publication publication = (Publication)objectPubl[0];
                    //Set old value to attribute publicationID
                    occurrence.setPublication(publication);
                    logger.debug("Set selected value for update of attribute Publication.");
                }else {
                    logger.error("UNDO - Incorrect oldRecordId for Phytochoria.");
                }
            break;
        default:            
            logger.error("No column defined for name "+ columnName);	                   
        }         
    }
        
    /**
     *
     */
    public void undoHabitat() {
        
        //zaznam v ramci, ktereho doslo k editaci tabulky tHabitats        
        Habitat habitat = historyChange.getOccurrence().getHabitat();
      
        //K editaci tabulky tHabitats dojde jen v pripade editace nejakeho konkretniho nalezu
        //protoze neni k dispozici kaskadovy update musi se do seznamu objektu pridat i Habitat, i kdyz na nej muzem pristupovat pres konkretni zaznam
        boolean objectList = editObjectList.contains((Record)habitat); 
        if (!objectList) {
            //pridani objektu do listu - informace o tom, ze byl dany objekt editovan (editace habitat vzdy v ramci occurrence)
            editObjectList.add((Record)habitat);
        }
        logger.debug("editObjectList: "+objectList);
        logger.debug("Habitat - OccurrenceID: "+habitat.getId());
        logger.debug("columnName: "+columnName);
        
        // Get a specified number of columnName from habitat mapping.
        int columnConstant;
        if (habitatHash.containsKey(columnName)) {
                 columnConstant = (Integer)habitatHash.get(columnName); 
        } else {
             columnConstant = 0;
        }        	    			

        // Save new value for the column        		
        switch (columnConstant) {
        case 1:  //Quadrant     	                	
                /* pokud doslo ke zmene vazeb mezi tHabitats a tOccurrences z 1:N na 1:1, tak v tOccurrences.cHabitatId
                 * bude jiz vzdy ulozeno id nove insertovany zaznamu do tHabitats a nikdy uz nedojde k jeho zmene, tzn.
                 * vazba mezi tabulkami pro dany nalez jiz bude na vzdy 1:1 
                 */ 	                		  
                occurrence.getHabitat().setQuadrant(oldValue);		                	
                logger.debug("Set selected value for update of attribute Quadrant.");                	
            break;
        case 2: //Place description 	                	 	                			                		 
                occurrence.getHabitat().setDescription(oldValue);		                	
                logger.debug("Set selected value for update of attribute Description.");              	              	
                break;
        case 3:  //Country 	                	 	                			                		 
                occurrence.getHabitat().setCountry(oldValue);		                	
                logger.debug("Set selected value for update of attribute Country.");                
            break;
        case 4: //Altitude 	                	                			                		 
                occurrence.getHabitat().setAltitude(Double.parseDouble(oldValue));		                	
                logger.debug("Set selected value for update of attribute Altitude.");                
                break;
        case 5:  //Latitude   	                		                			                		  
                occurrence.getHabitat().setLatitude(Double.parseDouble(oldValue));		                	
                logger.debug("Set selected value for update of attribute Latitude.");                
            break;
        case 6: //Longitude 	                		                			                		
                occurrence.getHabitat().setLongitude(Double.parseDouble(oldValue));		                	
                logger.debug("Set selected value for update of attribute Longitude.");                
                break;
        case 7: //Nearest bigger seat   	                	 	                			                		 
                //Nacteni Village pro nasledny update tHabitat.cNearestVillageId
                if (oldRecordId != 0){
                        Object[] objectVill = searchObject("Village",oldRecordId);
                        Village village = (Village)objectVill[0];
                occurrence.getHabitat().setNearestVillage(village);
                logger.debug("Set selected value for update of attribute NearesVillage.");
                } else {
                        logger.error("UNDO - Incorrect oldRecordId for Village.");
                }                
            break;
        case 8: //Phytochorion or phytochorion code 	                	             			                		 
                // Nacteni Phytochorion pro nasledny update tHabitat.cPhytochorionId
                if (oldRecordId != 0){
                        Object[] objectPhyt = searchObject("Phytochorion",oldRecordId);
                        Phytochorion phytochorion = (Phytochorion)objectPhyt[0];
                        occurrence.getHabitat().setPhytochorion(phytochorion);
                        logger.debug("Set selected value for update of attribute Phytochorion.");
                }else {
                        logger.error("UNDO - Incorrect oldRecordId for Phytochoria.");
                }                
            break; 	               
        case 9:  //Territory   	                	                			                		  
                // Nacteni Territory pro nasledny update tHabitat.cTerritory
                if (oldRecordId != 0){
                        Object[] objectTerr = searchObject("Territory",oldRecordId);
                        Territory territory = (Territory)objectTerr[0];
                        occurrence.getHabitat().setTerritory(territory);
                        logger.debug("Set selected value for update of attribute Territory.");
                }else {
                        logger.error("UNDO - Incorrect oldRecordId for Territory.");
                }	               
            break;
        case 10: //Note habitat	                		                			                		  
                occurrence.getHabitat().setNote(oldValue);		                	
                logger.debug("Set selected value for update of attribute Note.");                
                break;
        default:            
            logger.error("Habitat - No column defined for name "+ columnName);	                   
        }  	          
    }
    
    /**
     *
     */
    public void undoPublication() {
                
        Object[] object = searchObject("Publication", recordId);
        publication = (Publication)object[0];
        
        //test, zda jiz dany zaznam byl editovan
        boolean objectList = editObjectList.contains((Record)publication); 
        if (!objectList) {
        	//pridani objektu do listu - informace o tom, ze byl dany objekt editovan
            editObjectList.add((Record)publication);
        }
        logger.debug("editObjectList.contains: "+objectList);
        logger.debug("Publication: "+publication.getId());
        logger.debug("columnName: "+columnName);
        
       // Get a specified number of columnName from habitat mapping.
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
    }
    
    /**
     *
     */
    public void undoAuthor() {
        
       Object[] object = searchObject("Author", recordId);
       author = (Author)object[0];
       
       //test, zda jiz dany zaznam byl editovan
       boolean objectList = editObjectList.contains((Record)author); 
       if (!objectList) {
       	//pridani objektu do listu - informace o tom, ze byl dany objekt editovan
           editObjectList.add((Record)author);
       }
       logger.debug("editObjectList.contains: "+objectList);
       logger.debug("author: "+author.getId());
       logger.debug("columnName: "+columnName);
        
       // Get a specified number of columnName from habitat mapping.
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
    }
    
    /**
     *
     */
    public void undoMetadata() {
        
       Object[] object = searchObject("Metadata", recordId);
       metadata = (Metadata)object[0];
       
       //test, zda jiz dany zaznam byl editovan
       boolean objectList = editObjectList.contains((Record)metadata); 
       if (!objectList) {
       	//pridani objektu do listu - informace o tom, ze byl dany objekt editovan
           editObjectList.add((Record)metadata);
       }
       logger.debug("editObjectList.contains: "+objectList);
       logger.debug("metadata: "+ metadata.getId());
       logger.debug("columnName: "+columnName);
        
       // Get a specified number of columnName from habitat mapping.
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
    }
    
    
    /**
     *
     */
    public void undoPhytochorion() {
        
        Object[] object = searchObject("Phytochorion", recordId);
        phytochorion = (Phytochorion)object[0];     
        
        //test, zda jiz dany zaznam byl editovan
        boolean objectList = editObjectList.contains((Record)phytochorion); 
        if (!objectList) {
        	//pridani objektu do listu - informace o tom, ze byl dany objekt editovan
            editObjectList.add((Record)phytochorion);
        }
        logger.debug("editObjectList.contains: "+objectList);
        logger.debug("phytochorion: "+phytochorion.getId());
        logger.debug("columnName: "+columnName);
      
        if (columnName.equals("namePhytochorion")) {
            phytochorion.setCode(oldValue);	                 
            logger.debug("Phytochorion - Set selected value for update of attribute WholeName.");                 	
        } else if (columnName.equals("code")) {
             phytochorion.setName(oldValue);
             logger.debug("Phytochorion - Set selected value for update of attribute Address.");                 	             	
        } else {
            logger.error("Phytochorion - No column defined for name "+ columnName);
        }              
    }
    
    /**
     *
     */
    public void undoVillage() {
       
        Object[] object = searchObject("Village", recordId);
        village = (Village)object[0];
        
        // test, zda jiz dany zaznam byl editovan
        boolean objectList = editObjectList.contains((Record)village); 
        if (!objectList) {
        	//pridani objektu do listu - informace o tom, ze byl dany objekt editovan
            editObjectList.add((Record)village);
        }
        logger.debug("editObjectList.contains: "+objectList);
        logger.debug("village: "+village.getId());
        logger.debug("columnName: "+columnName);
        
       // Save new value for the column
        if (columnName.equals("nameVillage")) {
            village.setName(oldValue);	                 
            logger.debug("Village - Set selected value for update of attribute Name.");                 	
        } else {
            logger.error("Village - No column defined for name "+ columnName);	                   
        }       
    }
    
    /**
     *
     */
    public void undoTerritory() {
       
        Object[] object = searchObject("Territory", recordId);
        territory = (Territory)object[0];
        
        // test, zda jiz dany zaznam byl editovan
        boolean objectList = editObjectList.contains((Record)territory); 
        if (!objectList) {
        	//pridani objektu do listu - informace o tom, ze byl dany objekt editovan
            editObjectList.add((Record)territory);
        }
        logger.debug("editObjectList.contains: "+objectList);
        logger.debug("territory: "+territory.getId());
        logger.debug("columnName: "+columnName);
        
       if (columnName.equals("nameTerritory")) {
           territory.setName(oldValue);	                 
           logger.debug("Territory - Set selected value for update of attribute Name.");                 	 
       } else {
           logger.error("Territory - No column defined for name "+ columnName);	                   
       }        
    }
    
    /**
     * 
     * @param id
     * @return
     */
    public Object[] searchObject(String typeObject, int id) {       
    	SelectQuery query = null;

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
        } catch(RemoteException e) {
            System.err.println("RemoteException, searchObject() - "+typeObject+", createQuery");
        } catch(DBLayerException e) {
            System.err.println("DBLayerException, searchObject() - "+typeObject+", createQuery");
        }
        
        int resultId = 0;
        try {                   
            resultId = database.executeQuery(query);        
        } catch (DBLayerException e) {                   
            logger.error("Searching " +typeObject+ " failed. Unable to execute search query.");
        } catch (RemoteException e) {		 
     	   System.err.println("RemoteException- executeQuery " +typeObject);
 	} 
       Object[] objects = null;
       Object[] object = null;
       try {
       	    // Retrieve selected row interval         	
            try {
                 objects = database.more(resultId, 0, 0);  
            } catch(RemoteException e) {            	
                logger.debug("RemoteException- searchObject, more");            	
            }   
            object = (Object[])objects[0];           
            //close session
            database.closeQuery(query);            
       } catch (DBLayerException e) {
           // Log and set error in case of an exception
           logger.error("Processing search " +typeObject+ " results failed: "+e.toString());            
       } catch (RemoteException e) {
           // Log and set error in case of an exception
           logger.error("Processing search " +typeObject+ " results failed: "+e.toString());            
       }       
       return object; 	       	          	   
           	        
    }
    
    /*
     * Funkce, ktera mi dohleda vsechny autory ke konkretnimu nalezu
     */
    public String getAllAuthors(Occurrence occurrence) {
        String allAuthor = "";
        SelectQuery query = null;
        int resultId = 0;
        
        try {
            query = database.createQuery(AuthorOccurrence.class);
            query.addRestriction(PlantloreConstants.RESTR_EQ, AuthorOccurrence.OCCURRENCE, null, occurrence , null);
            resultId = database.executeQuery(query);
        } catch(RemoteException e) {
            System.err.println("RemoteException, getAllAuthors() - AuthorOccurrence, createQuery");
        } catch(DBLayerException e) {
            System.err.println("RemoteException, getAllAuthors() - AuthorOccurrence, createQuery");
        }
       Object[] objects = null;       
       try {
            objects = database.more(resultId, 0, 0);  
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }  
        
        int countResult = objects.length;  
        logger.debug("Authos of occurrence. Results retrieved. Count: "+ countResult);        
        // Cast the results to the AuthorOccurrence objects
        for (int i=0; i<countResult; i++ ) {                    							
            Object[] object = (Object[])objects[i];          
            String author = ((AuthorOccurrence)object[0]).getAuthor().getWholeName();
            String role = ((AuthorOccurrence)object[0]).getRole();
            allAuthor = allAuthor + role + ": " + author + "\n";
        }           
       //close session
        try {
            database.closeQuery(query);
        } catch(RemoteException e) {
            System.err.println("RemoteException, getAllAuthors() - AuthorOccurrence, createQuery");
        }
        
       return allAuthor;     	    
    }
    
    /**
     *  ..... pri whole history se bude do promennych occurrence, atd. nacitat vice ruznych objektu s jinym ID
     *  ....Musim si ty jednotlive objekty pamatovat --> pole objektu, kde budou jednotlive editovane objekty
     */
    public void commitUpdate() {
    	
    	int count = editObjectList.size();
    	for (int i=0; i< count; i++) {
    		try {
    			logger.debug("Object for update: "+editObjectList.get(i));
                        database.executeUpdateHistory(editObjectList.get(i));
	        } catch (RemoteException e) {
	                logger.error("CommitUpdate - RemoteException: "+e.toString());
	        } catch (DBLayerException e) {
	                logger.error("CommitUpdate - DBLayerException: "+e.toString());
	        }
       }    	
    }
    
    /**
     *  vycisteni seznamu editovany objektu: 
     */
    public void clearEditObjectList() {
    	editObjectList.clear();
    }
       
    /**
     * Projde oznacene zaznamy a postupne je smaze z tabulek historie.
     * Pri mazani z tabulky tHistoryChange overi, zda na dany zaznam neni vice vazeb.
     */
    public void deleteHistory(int toResult, boolean typeHistory) {
   	
    	//take from younger record to older record
    	for( int i=0; i < toResult; i++) {
    		if (typeHistory && !markListId.contains(i)) {
    			logger.debug("History of one occurence: "+markListId.contains(i));
    			continue;    			
    		}    		    		
    		historyRecord = (HistoryRecord)historyDataList.get(i); 
    		historyChange = historyRecord.getHistoryChange(); 
    		
	    	try {
				database.executeDeleteHistory(historyRecord);
				logger.debug("Deleting historyRecord successfully. Number of result: "+i);
			} catch (RemoteException e) {
				logger.error("Deleting historyRecord - remoteException. "+e.toString());
			} catch (DBLayerException e) {
				logger.error("Deleting historyRecord failed. "+e.toString());
			}
			int countResult = getRelationshipHistoryChange(historyChange.getId());			
			if (countResult == 0) {
				//samzat zaznam z tabulky tHistoryChange - muzeme protoze neexistuji dalsi FK z tHistory.cChngeId
				//pokud po smazani zaznamu z tHistory jsme nasli alespon jeden zaznam, ktery ma stejny FK na zaznam z tChangeHistory
				try {
					database.executeDeleteHistory(historyChange);
					logger.debug("Deleting historyChange successfully.");
				} catch (RemoteException e) {
					logger.error("Deleting historyChange - remoteException. "+e.toString());
				} catch (DBLayerException e) {
					logger.error("Deleting historyChange failed. "+e.toString());
				}
			} else {
				logger.debug("Exist other record in the table tHistory, whitch has the same value of attribute cChangeId.");
			}
    	}    	
		//Clear list 
    	markListId.clear();
    	markItem.clear();    		
    } 
    
    /**
     * Zjisteni kolik zaznamu v tabulce tHistory je provazano s konkretnim zaznamem z tHistoryHange 
     * @param id
     * @return
     */
    public int getRelationshipHistoryChange(int id){    	
    	SelectQuery query = null;
        try {
                query = database.createQuery(HistoryRecord.class);
                // Create aliases for table tHistoryChange.      
                query.createAlias("historyChange", "hc");  
                // Add restriction to cChangeId column 
                query.addRestriction(PlantloreConstants.RESTR_EQ, "hc.id", null, id , null);
        } catch(RemoteException e) {
        	System.err.println("RemoteException - searchHistoryChangeId(), createQuery");       	  
        } catch(DBLayerException e) {
        	System.err.println("DBLayerException - searchHistoryChangeId(), createQuery");       	  
        }
        
        
        int resultIdChange = 0;
        try {                   
        	resultIdChange = database.executeQuery(query);        
        } catch (DBLayerException e) {                   
            logger.error("Searching historyChangeId failed. Unable to execute search query.");
        } catch (RemoteException e) {		 
     	   System.err.println("RemoteException- getRelationshipHistoryChange(), executeQuery");
        } 

        int countResult = 100;
        try {
            countResult = database.getNumRows(resultIdChange);
            logger.debug("getRelationshipHistoryChange - Number of result: "+countResult);
            //close session
            database.closeQuery(query);
        } catch (RemoteException e) {
            System.err.println("RemoteException- getRelationshipHistoryChange(), getNumRows");
        }
	return countResult;
    }
    
    /*
     * Tato funkce vrati pocet zaznamu z tOccurrence, ktere jsou provazany s konktretnim zaznamem v tHabitats
     */
    public int getRelationshipHabitat() {
        SelectQuery query = null;
        try {
                query = database.createQuery(Occurrence.class);                
                query.addRestriction(PlantloreConstants.RESTR_EQ, Occurrence.HABITAT , null, occurrence.getHabitat() , null);
        } catch(RemoteException e) {
        	System.err.println("RemoteException - getRelationshipHabitat(), createQuery");       	  
        } catch(DBLayerException e) {
        	System.err.println("DBLayerException - getRelationshipHabitat(), createQuery");       	  
        }        
        
        int resultIdHabitat = 0;
        try {                   
        	resultIdHabitat = database.executeQuery(query);        
        } catch (DBLayerException e) {                   
            logger.error("Searching habitatId failed. Unable to execute search query.");
        } catch (RemoteException e) {		 
     	   System.err.println("RemoteException- getRelationshipHabitat(), executeQuery");
        } 

        int countResult = 100;
        try {
            countResult = database.getNumRows(resultIdHabitat);
            logger.debug("getRelationshipHabitat - Number of result: "+countResult);
            //close session
            database.closeQuery(query);
        } catch (RemoteException e) {
            System.err.println("RemoteException- searchHistoryChangeId(), getNumRows");
        }
	return countResult;
    }
    
    /*
     * Tato funkce je volana jen pro UNDO RECORD, coz znamena, ze pracuje jen s konkretnim nalezem
     */
    public void generateMessageUndo() {    	
    	messageUndo = "Budou provedeny následující změny:\n";      
    	int count = markItem.size();
    	for (int i=0; i<count; i++) {
    		Object[] itemList = (Object[])(markItem.get(i));
    		String item = (String)itemList[0];
    		Integer maxId = (Integer)itemList[1];      		
    		oldValue = ((HistoryRecord)historyDataList.get(maxId)).getOldValue(); 
    		messageUndo = messageUndo + item + " --> " + oldValue + "\n";
    	}
        //pracuji stale s konkretnim occurrence
        int countResult = getRelationshipHabitat();			
        if (countResult > 1) {
            messageUndo = "\n" + messageUndo + "Tyto změny ovlivní více nálezů.\n";
    	}
    }    

    public String getMessageUndoToDate(String toDate) {
        String message = "Všechny změny od " + toDate + " budou zrušeny."; 
        return message;
    }
    
    /*
     * Funkce ktera zjisti podrobne informace o polozce, ktera byla editovana
     */
    public String getDetailsMessage(int resultNumber) {
        
        //details about object
        String detailsMessage = "";
       
        //data z historie pro konktetni radek tabulky
        historyRecord = (HistoryRecord)historyDataList.get(resultNumber);    		
        historyChange = historyRecord.getHistoryChange();
        tableName = historyRecord.getHistoryColumn().getTableName();
        recordId = historyChange.getRecordId();
                
        
        //podle tableName najdeme podrobnosti o konkretnim objektu (autor, publikace, nalez,...)
         if (tableName.equals(PlantloreConstants.ENTITY_OCCURRENCE) || tableName.equals(PlantloreConstants.ENTITY_HABITAT) || tableName.equals(PlantloreConstants.ENTITY_AUTHOROCCURRENCE)) {           
              //Get details for occurrence
              Occurrence occurrence = historyChange.getOccurrence();             
              detailsMessage = L10n.getString("detailsOccurrence") + "\n\n";
              detailsMessage = detailsMessage + L10n.getString("occurrence.plant") + ": "+ occurrence.getPlant().getTaxon()+"\n";
              detailsMessage = detailsMessage + L10n.getString("authorOccurrence.author") + ": " +getAllAuthors(occurrence);
              detailsMessage = detailsMessage + L10n.getString("occurrence.isoDateTime") + ": " + occurrence.getIsoDateTimeBegin() +"\n";
              detailsMessage = detailsMessage + L10n.getString("habitat.nearestVillage") + ": "+ occurrence.getHabitat().getNearestVillage().getName() + "\n";
              detailsMessage = detailsMessage + L10n.getString("habitat.description") + ": "+ occurrence.getHabitat().getDescription() + "\n";
              detailsMessage = detailsMessage + L10n.getString("habitat.territory") + ": "+ occurrence.getHabitat().getTerritory().getName() + "\n";
              detailsMessage = detailsMessage + L10n.getString("habitat.phytochorion") + ": "+ occurrence.getHabitat().getPhytochorion().getName() +" (Code: " + occurrence.getHabitat().getPhytochorion().getCode() + ")\n";
              detailsMessage = detailsMessage + L10n.getString("habitat.country") + ": " + occurrence.getHabitat().getCountry() +"\n";
              detailsMessage = detailsMessage + L10n.getString("occurrence.dataSource") + ": " + occurrence.getDataSource() + "\n";
              detailsMessage = detailsMessage + L10n.getString("occurrence.publication") + ": " + occurrence.getPublication().getReferenceCitation() + "\n";
              detailsMessage = detailsMessage + L10n.getString("occurrence.herbarium") + ": " + occurrence.getHerbarium() +"\n";
              detailsMessage = detailsMessage + L10n.getString("occurrence.note") + ": " + occurrence.getNote() + "\n";
              detailsMessage = detailsMessage + L10n.getString("habitat.note") + ": " + occurrence.getHabitat().getNote() +"\n";
        } else if (tableName.equals(PlantloreConstants.ENTITY_PUBLICATION)) {
              //Get details for Publication
              Object[] object = searchObject("Publication",recordId); 
              Publication publication = (Publication)object[0];
              detailsMessage = L10n.getString("detailsPublication") + "\n\n";
              detailsMessage = detailsMessage + "Name of collection: " + publication.getCollectionName() + "\n";
              detailsMessage = detailsMessage + "Year of published collection: " + publication.getCollectionYearPublication() + "\n";
              detailsMessage = detailsMessage + "Name of journal: " + publication.getJournalName() + "\n";
              detailsMessage = detailsMessage + "Author of journal: " + publication.getJournalAuthorName() +"\n";
              detailsMessage = detailsMessage + "URL: " + publication.getUrl() +"\n";
              detailsMessage = detailsMessage + "Note: " + publication.getNote() + "\n";
        } else if (tableName.equals(PlantloreConstants.ENTITY_AUTHOR)) {
              //Get details for Author
              Object[] object = searchObject("Author",recordId);   
              Author author = (Author)object[0];
              detailsMessage = L10n.getString("detailsAuthor") + "\n\n";
              detailsMessage = detailsMessage + "Name: " + author.getWholeName() + "\n";
              detailsMessage = detailsMessage + "Organization: " + author.getOrganization() + "\n";
              detailsMessage = detailsMessage + "Role: " + author.getRole() + "\n";
              detailsMessage = detailsMessage + "Address: " + author.getAddress() + "\n";
              detailsMessage = detailsMessage + "Email: " + author.getEmail() + "\n";
              detailsMessage = detailsMessage + "Telephone number: " + author.getPhoneNumber() + "\n";            
              detailsMessage = detailsMessage + "URL: " + author.getUrl() + "\n";
              detailsMessage = detailsMessage + "Note: " + author.getNote() + "\n";
        }  else if (tableName.equals(PlantloreConstants.ENTITY_METADATA)) {
             //Get details for Metadata
              Object[] object = searchObject("Metadata",recordId);   
              Metadata metadata = (Metadata)object[0];
              detailsMessage = L10n.getString("detailsMetadata") + "\n\n";
              detailsMessage = detailsMessage + L10n.getString("institution") + "\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.sourceInstitutionId") + ": " + metadata.getSourceInstitutionId() + "\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.ownerOrganizationAbbrev") + ": " + metadata.getOwnerOrganizationAbbrev() + "\n\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.technicalContact")+ ":\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.technicalContactName") + ": " + metadata.getTechnicalContactName() + "\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.technicalContactEmail") + ": " + metadata.getTechnicalContactEmail() + "\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.technicalContactAddress") + ": " + metadata.getTechnicalContactAddress() + "\n\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.contentContact") + ": \n\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.contentContactName") + ": " + metadata.getContentContactName() + "\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.contentContactEmail") + ": " + metadata.getContentContactEmail() + "\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.contentContactAddress") + ": " + metadata.getContentContactAddress() + "\n\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.project") + ": \n";
              detailsMessage = detailsMessage + L10n.getString("metadata.dataSetTitle") + ": " + metadata.getDataSetTitle() + "\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.dataSetDetails") + ": " + metadata.getDataSetDetails() + "\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.sourceId") + ": " + metadata.getSourceId() +"\n\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.dateCreate") + ": " +metadata.getDateCreate() + "\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.dateModified") + ": " +metadata.getDateModified() + "\n\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.recordbasic") + ": " + metadata.getRecordBasis() + "\n";
              detailsMessage = detailsMessage + L10n.getString("metadata.biotopetext") + ": " + metadata.getBiotopeText() + "\n";                      
        } else if (tableName.equals(PlantloreConstants.ENTITY_PHYTOCHORION)) {
              //Get details for Phytochorion
              Object[] object = searchObject("Phytochorion",recordId); 
              Phytochorion  phytochorion = (Phytochorion)object[0];
              detailsMessage = L10n.getString("detailsPhytochorion") + "\n\n";
              detailsMessage = detailsMessage + "Phytochorion: " + phytochorion.getName() + "\n";
              detailsMessage = detailsMessage + "Code of phytochorion: " + phytochorion.getCode() + "\n";
        } else if (tableName.equals(PlantloreConstants.ENTITY_TERRITORY)) {
              //Get details for Territory
              Object[] object = searchObject("Territory",recordId); 
              Territory territory = (Territory)object[0];
              detailsMessage = L10n.getString("detailsTerritory") + "\n\n";
              detailsMessage = detailsMessage + "Territory: " + territory.getName() + "\n";
        } else if (tableName.equals(PlantloreConstants.ENTITY_VILLAGE)) {
              //Get details for Village
              Object[] object = searchObject("Village",recordId);  
              Village village = (Village)object[0];
              detailsMessage = L10n.getString("detailsVillage") + "\n\n";
              detailsMessage = detailsMessage + "Village: " + village.getName() + "\n";
        } else {
            logger.error("No table defined");
            detailsMessage = "No details for selected row.";
        }        
        
        logger.debug("detailsMessage: "+ detailsMessage);
        return detailsMessage;
    }
    
    /*
     * Tato funkce smaze vsechny data z tabulky tHistoryChange a z tHistory
     * delete from tHistory;
     * delete from tHistoryChange;
     */
    public void clearHistory() {        
        
        try {
            //smazani dat z tabulky tHistory
            database.conditionDelete(HistoryRecord.class, HistoryRecord.ID, ">", 0);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
        
        try {            
            //smazani dat z tabulky tHistoryChange
            database.conditionDelete(HistoryChange.class, HistoryChange.ID, ">", 0);
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        
    }
    
    /*
     * Tato funkce projde tabulky s cdelete - tAuthors, tAuthorsOccurrences, tOccurrences, tHabitats, tPublications
     * a smaze v techto tabulkach zaznamy, ktere maji cdelete == 1
     * delete from tAuthors where cdelete = 1;
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
    
       
    //****************************//
    //****Get and set metods*****//
    //**************************//
    
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
