/**
 * 
 */
package net.sf.plantlore.client.history;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Observable;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.server.DBLayerException;
import net.sf.plantlore.client.dblayer.query.Query;
//import net.sf.plantlore.client.dblayer.query.SelectQuery;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.client.dblayer.result.Result;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.common.record.HistoryRecord;
import net.sf.plantlore.common.record.HistoryChange;
import net.sf.plantlore.common.record.HistoryColumn;
import net.sf.plantlore.common.record.Village;


import org.apache.log4j.Logger;


/**
 * @author Lada
 *
 */
public class History extends Observable {

	/** Instance of a logger */
    private Logger logger;   
    /** Exception with details about an error */
    private DBLayerException error = null;
    /** Instance of a database management object */
    private DBLayer database;   
    /** Constant with default number of rows to display */
    private static final int DEFAULT_DISPLAY_ROWS = 2;    
    /** Actual number of rows to display */
    private int displayRows = DEFAULT_DISPLAY_ROWS;   
    /** Index of the first record shown in the table */
    private int currentFirstRow;
    /** Information about current display rows*/
    private String displayRow;
    
    //*******Informations about searching Result from database*****//
    /** Result of the search query */
    private int resultId = 0;
    /** Data (results of a search query) displayed in the table */
    private Object[][] editHistoryData;
    /** List of data (results of a search query) displayed in the table */
    private ArrayList editHistoryDataList;
   
    //********************* ***************************************//
    private Occurrence occurrence;
    private Habitat habitat;
	private Publication publication;
	private Village village;
	private Phytochorion phytochorion;
	private Territory territory;
	
    //	**************Informations about HistoryRecord*************//
    /** Name of the table where value was changed*/
	private String tableName;  
	/** Name of the column where value was changed*/
	private String columnName;
	/** Unique value identified record. 
	 * Foring key referenced to table TOCCURRENCES */	
	private Integer occurrenceId;
	/**Unique value identified record in table where value was changed */
	private int recordId;
	/**Unique value identified record before changed */
	private int oldRecordId;
	/** Operation whitch was used*/   
	private String operation;
	/** Date and time when the reccord was changed*/
	private java.util.Date when = new Date();	
	/** Old value of attribute*/    
	private String oldValue;
	/** New value of attribute*/
	private String newValue;
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
     private Hashtable publicationHash;
     private Hashtable habitatHash;
     private Hashtable occurrenceHash;  
    
    /**  
     *  Creates a new instance of History 
     *  @param database Instance of a database management object
     *  @param namePlant Name of plant for specified occurrence
     *  @param nameAuthor Name of author for specified occurrence
     *  @param location Informaciton about location for specified occurrence
     * */
    public History(DBLayer database, String namePlant, String nameAuthor, String location, int idOcc)
    {
       logger = Logger.getLogger(this.getClass().getPackage().getName());	 
       this.database = database;	
       
       occurrence = new Occurrence();
       occurrence.setId(idOcc);
       
       setNamePlant(namePlant);
       setNameAuthor(nameAuthor);
       setLocation(location);
	   
       //Searching for information about data entries concerned with specified occurrence
       searchInsertInfo();
	   //Searching for information about data editing concerned with specified occurrence
	   searchEditHistory();
	   //Process results of a search "edit" query 
	   processEditResult(1,displayRows);
    }	

    /**
     *  Searches for information about data entries concerned with specified occurrence.
     *  @param idOccurrence Unique value identified occurrence
     */
    public void searchInsertInfo() {
            
       // Create new Select query
       SelectQuery query = null;
       try {
       	    query = database.createQuery(HistoryChange.class);
       } catch(RemoteException e) {
       	    System.err.println("RemoteException- searchInsertInfo(), createQuery");       	  
       }
       // Create aliases for table tHistoryChange.      
       //query.createAlias("user", "us");
       query.addRestriction(PlantloreConstants.RESTR_EQ, HistoryChange.OCCURRENCE, null, occurrence, null);
       query.addRestriction(PlantloreConstants.RESTR_EQ, HistoryChange.OPERATION, null, HistoryChange.HISTORYCHANGE_INSERT, null);
       
       int resultIdInsert = 0;
       try {
           // Execute query                    
           resultIdInsert = database.executeQuery(query);        
       } catch (DBLayerException e) {
           // Log and set an error                   
           logger.error("Searching history (inserting) failed. Unable to execute search query.");
           //setError(e);
           // setError("Searching history failed. Please contact your administrator.");
       } catch (RemoteException e) {
		   // TODO 
		   //e.printStackTrace();
    	   System.err.println("RemoteException- searchInsertInfo(), executeQuery");
	} finally {
    	   logger.debug("Searching history (inserting) ends successfully");
           // Save "insert" history data
           setInsertResult(resultIdInsert);                    
       }              
    }
    
    
    /**     
     * Searches for information about data editing concerned with specified occurrence.
     * @param idOccurrence Unique value identified occurrence
     */
     
    public void searchEditHistory()
    {  
    	    	
        //Create new Select query
        SelectQuery query = null;       

    	//  Select data from tHistory table
        try {
			query = database.createQuery(HistoryRecord.class);
		} catch (RemoteException e) {
			System.err.println("RemoteException- searchEditHistory(), createQuery");
		}
        // Create aliases for table tHistoryChange.      
        query.createAlias("historyChange", "hc");        
        // Add restriction to CUNITVALUE column of tOccurence table
        query.addRestriction(PlantloreConstants.RESTR_EQ, "hc.operation", null, 2, null);
        query.addRestriction(PlantloreConstants.RESTR_EQ, "hc.occurrence", null, occurrence, null);    	
        query.addOrder(PlantloreConstants.DIRECT_DESC, "hc.when");        
    	
        int resultIdEdit = 0;
        try {
            // Execute query                    
            resultIdEdit = database.executeQuery(query);        
        } catch (DBLayerException e) {
            // Log and set an error                   
            logger.error("Searching history (editing) failed. Unable to execute search query.");
            //setError(e);
            // setError("Searching history failed. Please contact your administrator.");
        } catch (RemoteException e) {
 		   // TODO 
 		   //e.printStackTrace();
     	   System.err.println("RemoteException- searchInsertInfo(), executeQuery");
	 	} finally {
	 		logger.debug("Searching history (editing) ends successfully");
        	// Save "edit" history data
            setEditResult(resultIdEdit);                  
	 	}              
    }
    

    
    /**     
     * Sets information about data (date, name of user) entries concerned with specified occurrence 
     * @param result result of a database operation INSERT. Result has one row.
     */
    public void setInsertResult(int resultIdInsert) {
   	    	
    	if (getResultRows() > 1) {
    		// Log an error                   
            logger.error("Too many results for inserting query.");  
    	}
            	
    	logger.debug("Retrieving query results."); 
    	Object[] objectHistory = null;
        try {
        	 // Retrieve selected row interval         	
         	try {
         		objectHistory = database.more(resultIdInsert, 1, 1);  
         	} catch(RemoteException e) {
             	System.err.println("RemoteException- setInsertResult, more");
             	logger.debug("RemoteException- setInsertResult, more");
             	return;
             }   
         	Object[] objHis = (Object[])objectHistory[0]; 
         	//setWhen(((HistoryChange)objHis[0]).getWhen());
         	//setNameUser(((HistoryChange)objHis[0]).getWho().getWholeName());
         	
            this.when = ((HistoryChange)objHis[0]).getWhen();
            this.nameUser = ((HistoryChange)objHis[0]).getWho().getWholeName();
           
        } catch (DBLayerException e) {
            // Log and set error in case of an exception
            logger.error("Processing search (inserting) results failed: "+e.toString());            
        } finally { 
        	logger.debug("Sets 'insert' data ends successfully. When= " + this.when + " ");        	
        }        
    }
    
    /**
     * Process results of a search query. Retrieves results using the database management object (DBLayer) and stores them in the data field of the class. 
     * @param fromTable number of the first row to show in table. Number of the first row to retraieve is 1.
     * @param count number of rows to retrieve 
     */
    public void processEditResult(int fromTable, int count) {
    	
    	if (this.resultId != 0) {
    		int currentRow = getResultRows();
            logger.debug("Rows in the result: "+currentRow);
            logger.debug("Max available rows: "+(fromTable+count-1));
           
            // Find out how many rows we can retrieve - it cannot be more than number of rows in the result
            int to = Math.min(currentRow, fromTable+count-1);           
            if (to == 0) {
                this.editHistoryDataList = new ArrayList();                
            } else {
                logger.debug("Retrieving query results: 1 - "+to);
                setCurrentDisplayRow(fromTable+ "-" + to);
                try {                	 
                     // Retrieve selected row interval 
                	Object[] objectHistory;
                 	try {
                 		objectHistory = database.more(this.resultId, 1, to);  
                 	} catch(RemoteException e) {
                     	System.err.println("RemoteException- processEditResult, more");
                     	logger.debug("RemoteException- processEditResult, more");
                     	return;
                     }                   
                    int countResult = objectHistory.length;  
                    logger.debug("Results retrieved. Count: "+ countResult);
                    // Create storage for the results
                    this.editHistoryDataList = new ArrayList();
                    // Cast the results to the HistoryRecord objects
                    for (int i=0; i<countResult; i++ ) {                    							
						Object[] objHis = (Object[])objectHistory[i];
                        this.editHistoryDataList.add((HistoryRecord)objHis[0]);
                    }                     
                } catch (DBLayerException e) {
                    // Log an error in case of an exception
                    logger.error("Processing search results failed: "+e.toString());            
                } finally { 
                	logger.debug("Sets 'edit' data ends successfully");
                	//Update current first displayed row (only if data retrieval was successful)
                    setCurrentFirstRow(fromTable);                    
                }               
            }
        }         
    }
    
    /**
     * z db vzdy ziskame vysledek od 1 do "to" - tato funkce nacte data pro zobrazeni 
     * ve view (data, ktere v dane chvili uvidi uzivatel).  
     * @return Object[][] with data values for displaying in the table
     */
    public Object[][] getData() {
    	    	
    	int countResult = editHistoryDataList.size();
    	int firstRow = getCurrentFirstRow();
    	int countRow = countResult - firstRow + 1;
    	int ii = 0;
        editHistoryData = new Object[countRow][6];
    	for (int i=firstRow-1; i < countResult; i++) {
    		editHistoryData[ii][0] = new Boolean(false);
    	    editHistoryData[ii][1] = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getWhen();
    	    editHistoryData[ii][2] = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getWho().getWholeName();
    	    editHistoryData[ii][3] = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getColumnName();
    	    editHistoryData[ii][4] = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
    	    editHistoryData[ii][5] = ((HistoryRecord)editHistoryDataList.get(i)).getNewValue();
    	    ii++;
    	}  
    	return this.editHistoryData;
    	
    }
    

    /**
     * Tato funkce bude menit hodnoty v DB (DELETE v historii, zmena v jakekoliv 
     * tabulce pro kterou se zaznamenava historie) - UNDO
     * 
     * bude volana z CTR po stisku klavesy
     * jako parametr dostane seznam oznacenych zmen, ktere se maji vratit
     * 
     * jde o update database :-)
     */
    public void updateOlderChanges(ArrayList markResult)
    {    	
    	//Occurrence occurrence = new Occurrence(); ... uz jsme si tento objek vytvorili v konsturktoru 
    	habitat = new Habitat();
    	publication = new Publication();
    	village = new Village();
    	phytochorion = new Phytochorion();
    	territory = new Territory(); 
    	
    	//Inicalization of hashTable
    	initOccurrenceHash();
    	initHabitatHash();
    	initPublicationHash();
    	
    
        	
    	//number of selected rows
    	int countMark = markResult.size();
    	//take from older record to younger record
    	for( int i=countMark-1; i >= 0; i--) {
    		logger.debug("Number of selected row: "+markResult.get(i));
    		tableName = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getTableName();
    		columnName = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getColumnName();
    		
    		if (tableName.equals("Occurrence")){
    			
    			//Get a specified number from occurrence mapping.
    			int value;
    			if (occurrenceHash.containsKey(columnName)) {
    				 value = (Integer)occurrenceHash.get(columnName); 
    	        } else {
    	             value = 0;
    	        }
        	    
    			//Init oldRecordId
    			oldRecordId = 0;
    			
    			//Save new value for the column
    			switch (value) {
	                case 1: //Taxon  
	                	//test, zda starsi hodnota byla pro occurrence nastavena 
	                	//(info o mladsi zmene bude uz jen vymazano s tabulky historie} 
	                	if ( occurrence.getPlant() == null ){
	                		oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
		                	//recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
		                	//oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
		                	//newValue = ((HistoryRecord)editHistoryDataList.get(i)).getNewValue();
		                	/* 
		                	 * oldValue a newValue nepotrebujeme ...pro Taxon nam staci znat jak se zmenilo jeho ID
		                	 * pro occurrence nepotrebujem ani recordID, protoze to je stejne s occurrenceID - jen 
		                	 * bychom mohli otestovat, zda to je opravdu stejne, kdyz ne tak vznikla nekde pri ukladani
		                	 * dat do historie chyba
		                	 * 
		                	 * if (occurrence.getId() != recordId) {
		                	 *     loggerr.error("Incorrect identifier for OCCURRENCE");
		                	 *  }
		                	 *  
		                	 * ?? kdyz se nasledovne priradi a nasledne ulozi plant, bude to spravne????	       
		                	 */
		                	Plant plant = new Plant();
		                	plant.setId(oldRecordId);
		                	occurrence.setPlant(plant);	
		                	/*
		                	 * po zmene plant je potreba projit jeste vysledky od 1 do currentFirstRow nebo SELECT s posminkou
		                	 * pokud v te dobe doslo jeste ke zmnene Plant, tak je potreba tuto zmenu vymazat z historie
		                	 * a upozornit na to uzivatele, ze kdyz pozaduje vratit zmnenu k datu xxx, tak budou zruseny
		                	 * i zmeny z datumu yyy, atd..
		                	 */
	                	} else {
	                		//zavolame smazani zaznamu v historii (tHistory a s otestovanim z tHistoryChange)
	                	}
	                    break;
	                case 2: //Year	
	                	if (occurrence.getYearCollected() == 0) {
	                		oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
		                	occurrence.setYearCollected(Integer.parseInt(oldValue));
	                	}else {
	                		//zavolame smazani zaznamu v historii (tHistory a s otestovanim z tHistoryChange)
	                	}	                	
	                	break;
	                case 3: //Month 
	                	if (occurrence.getMonthCollected() == 0) {
	                		oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                		occurrence.setMonthCollected(Integer.parseInt(oldValue));
	                	} else {
	                		// zavolame smazani zaznamu v historii (tHistory a s otestovanim z tHistoryChange)
	                	}
	                    break;
	                case 4: //Day	                	
	                	if (occurrence.getDayCollected() == 0) {
	                		oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
		                	occurrence.setDayCollected(Integer.parseInt(oldValue));
	                	} else {
	                		// zavolame smazani zaznamu v historii (tHistory a s otestovanim z tHistoryChange)
	                	}
	                	break;
	                case 5: //Time 	                	
	                	if (occurrence.getTimeCollected() == null) {
	                		oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();	                	
		                	//occurrence.setTimeCollected(Integer.parseInt(oldValue));
	                	} else {
	                		// zavolame smazani zaznamu v historii (tHistory a s otestovanim z tHistoryChange)
	                	}
	                    break;
	                case 6: //Source	                	
	                	if (occurrence.getDataSource() == null) {
		                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
		                	occurrence.setDataSource(oldValue);
	                	} else {
	                		//zavolame smazani zaznamu v historii (tHistory a s otestovanim z tHistoryChange)
	                	}
	                	break;
	                case 7: //Herbarium
	                	if (occurrence.getHerbarium() == null){
	                		oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                		occurrence.setHerbarium(oldValue);
	                	} else {
	                		//zavolame smazani zaznamu v historii (tHistory a s otestovanim z tHistoryChange)
	                	}
	                    break;
	                case 8: //Note occurrence	
	                	if (occurrence.getNote() == null) {	                			            
	                		oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                		occurrence.setNote(oldValue);
	                	} else {
	                		//zavolame smazani zaznamu v historii (tHistory a s otestovanim z tHistoryChange)
	                	}
	                	break;
	                default:            
	                    logger.error("No column defined for name "+ columnName);	                   
    			}  	
    		} else if (tableName.equals("Habitat") || tableName.equals("Village")
    				   || tableName.equals("Territory") || tableName.equals("Phytochorion")){
    			
    			// Get a specified number from habitat mapping.
    			int value;
    			if (habitatHash.containsKey(columnName)) {
    				value = (Integer)habitatHash.get(columnName); 
    	        } else {
    	             value = 0;
    	        }    
    			
    			// Init oldRecordId
    			oldRecordId = 0;
    			
    			// Save new value for the column        		
     			switch (value) {
 	                case 1:  //Quadrant    
 	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		habitat.setId(recordId);
		                	habitat.setQuadrant(oldValue);
	                	}
	                	
	                	/*
	                	 * Pro konkretni OCCURRENCE je stejne recordId ... pokud by doslo k jeho zmene, tak by byla 
	                	 * zaznamenana v polozce oldRecordId - ve chvili kdy dojde k teto zmene, tak se to musi osetrit
	                	 * 1. v tOccurrence zmenit polozku cHabitat (s odmazanim daneho zaznamu v tHabitats to bude asi 
	                	 *    slozitejsi,protoze se na nej muzou jiz odkazovat dalsi zaznamy --> neodmazavat nebo otestovat
	                	 *    a pak teprve odmazat (maze se nastavenim priznaku CDELETE)) 
	                	 * 2. v tOccurrence nemenit polozku cHabitat -- tak to nepujde
	                	 * 
	                	 * 
	                	 *  
	                	 */
 	                    break;
 	                case 2: //Place description
 	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		habitat.setId(recordId);
		                	habitat.setDescription(oldValue);
	                	}	                	
 	                	break;
 	                case 3:  //Country
 	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		habitat.setId(recordId);
		                	habitat.setCountry(oldValue);
	                	}	
	                	
 	                    break;
 	                case 4: //Altitude
 	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		habitat.setId(recordId);
		                	//habitat.setAltitude(Integer.parseInt(oldValue));
	                	}	
	                	
 	                	break;
 	                case 5:  //Latitude  
 	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		habitat.setId(recordId);
		                	//habitat.setLatitude(Integer.parseInt(oldValue));
	                	}	
	                	
 	                    break;
 	                case 6: //Longitude
 	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		habitat.setId(recordId);
		                	//habitat.setLongitude(Integer.parseInt(oldValue));
	                	}	
 	                	break;
 	                case 7: //Nearest bigger seat  	
 	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		habitat.setId(recordId);	                		       		
	                		village.setName(oldValue);
		                	habitat.setNearestVillage(village);
	                	}	
 	                    break;
 	                case 8: //Phytochorion
 	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		habitat.setId(recordId);	                		              		
	                		phytochorion.setName(oldValue);
		                	habitat.setPhytochorion(phytochorion);
	                	}	
 	                	break;
 	               case 9: //Phytochorion code
 	            	    recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		habitat.setId(recordId);	                		              		
	                		phytochorion.setCode(oldValue);
		                	habitat.setPhytochorion(phytochorion);
	                	}
	                	break;
	                case 10:  //Territory        
	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		habitat.setId(recordId);	                		              		
	                		territory.setName(oldValue);
		                	habitat.setTerritory(territory);
	                	}
	                    break;
	                case 11: //Note habitat
	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		habitat.setId(recordId);
		                	habitat.setNote(oldValue);
	                	}	
	                	break;
 	                default:            
 	                    logger.error("No column defined for name "+ columnName);	                   
     			}  	
    		} else if (tableName.equals("Publication")){
    			
    			// Get a specified number from publication mapping.
    			int value;
    			if (publicationHash.containsKey(columnName)) {
    				value = (Integer)publicationHash.get(columnName); 
    	        } else {
    	             value = 0;
    	        }
    			
    			// Init oldRecordId
    			oldRecordId = 0;    			
    			
    			// Save new value for the column    			         		
    			switch (value) {
	                case 1: //Collection     
	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		publication.setId(recordId);
		                	publication.setCollectionName(oldValue);
	                	}	
	                    break;
	                case 2: //Year of publication
	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		publication.setId(recordId);
		                	publication.setCollectionYearPublication(Integer.parseInt(oldValue));
	                	}	
	                	break;
	                case 3: //Journal  
	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		publication.setId(recordId);
		                	publication.setJournalName(oldValue);
	                	}	
	                    break;
	                case 4: //Author of journal
	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		publication.setId(recordId);
		                	publication.setJournalAuthorName(oldValue);
	                	}	
	                	break;
	                case 5: //Reference citation = collectionName + collectionYearPublication + journalName + journalAuthorName
	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		publication.setId(recordId);
		                	publication.setReferenceCitation(oldValue);
	                	}	
	                    break;
	                case 6: //Reference detail
	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		publication.setId(recordId);
		                	publication.setReferenceDetail(oldValue);
	                	}	
	                	break;
	                case 7: //URL    
	                	recordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getRecordId();
 	                	oldRecordId = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryChange().getOldRecordId();
	                	oldValue = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
	                	if (oldRecordId != 0 ){
	                		//musime zavolat funkci, ktera to osetri
	                	}else {
	                		publication.setId(recordId);
		                	publication.setUrl(oldValue);
	                	}	
	                    break;	                
	                default:            
	                    logger.error("No column defined for name "+ columnName);	                   
    			}  	
    		} else {
    			logger.equals("No table defined");
    		}    			
    		
    	}
    	//number of rows in result
    	int countResult = editHistoryDataList.size();
    	for( int i=0; i<countResult; i++) {
    		
    	}
    }

    
     //***************************//
    //****Init Hashtable*********//
    //**************************//
    
    private void initOccurrenceHash() {
        occurrenceHash = new Hashtable(9); 
        occurrenceHash.put("Taxon",1);
        occurrenceHash.put("Year",2);
        occurrenceHash.put("Month",3);
        occurrenceHash.put("Day",4);
        occurrenceHash.put("Time",5);           
        occurrenceHash.put("Source",6);
        occurrenceHash.put("Herbarium",7);        
        occurrenceHash.put("Note occurrence",8);
        //isoDataTimeBegin je slozena s Year + Month + Day + Time ... mela by se zmenit vzdy, kdyz
        // dojde ke zmene nektere z techto polozek ... jak to nejlepe zaridit???
        occurrenceHash.put("isoDataTimeBegin",9);
        /*
         * Jak se bude chovat cUpdateWhen, cUpdateWho v historii - asi se nastavi 
         * cas vyvolani undo v historii a uzivatel, ktery to vyvolal
         */
        //occurrenceHash.put("cUpdateWhen",10);
        //occurrenceHash.put("cUpdateWho",11);
    }    
    
    private void initHabitatHash() {
        habitatHash = new Hashtable(11);        
        habitatHash.put("Quadrant",1);
        habitatHash.put("Place description",2);
        habitatHash.put("Country",3);
        habitatHash.put("Altitude",4);
        habitatHash.put("Latitude",5);
        habitatHash.put("Longitude",6);      
        habitatHash.put("Nearest bigger seat",7);      
        habitatHash.put("Phytochorion",8);
        habitatHash.put("Phytochorion code",9);
        habitatHash.put("Territory",10);
        habitatHash.put("Note habitat",11);
    }
    
    private void initPublicationHash() {
        publicationHash = new Hashtable(7);                       
        publicationHash.put("Collection",1);
        publicationHash.put("Year of publication",2);
        publicationHash.put("Journal",3);
        publicationHash.put("Author of journal",4);
        publicationHash.put("Reference citation",5);
        publicationHash.put("Reference detail",6);
        publicationHash.put("URL",7);
    }
    
    //****************************//
    //****Get and set metods*****//
    //**************************//
    
     public String getCurrentDisplayRows() {
		  return this.displayRow;		  
	   }

	 public void setCurrentDisplayRow(String displayRow) {
		  this.displayRow = displayRow;		  
	 } 
     
    
    /**
     *  Set result of a database operation. This is used only for search operations.
     *  @param int 
     */
    public void setEditResult(int resultIdEdit) {
        this.resultId = resultIdEdit;
    }
    
    /**
     *  Get results of last database operation. This is used only for search operations.
     *  @return 
     */
    public int getEditResult() {
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
    
    /**
     *  Set an error flag (message).
     *  @param msg  message explaining the error which occured
     */
    public void setError(DBLayerException e) {
        this.error = e;
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
     *  Get error message for the error which occured
     *  @return message explaining the error which occured
     */
    public DBLayerException getError() {
        return this.error;
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
	*   Get name of the table where value was changed
	*   @return of the table where value was changed	
	*/
   public String getTableName() {
	  return this.tableName;
   }

   /**
	*   Set name of the table where value was changed
	*   @param tableName string containing of the table where value was changed
	*/
   public void setTableName(String tableName) {
	  this.tableName = tableName;
   }

   /**
   *   Get name of the column where value was changed
   *   @return  name of the column where value was changed
   */
  public String getColumnName() {
	 return this.columnName;
  }

  /**
   *   Set  name of the column where value was changed
   *   @param columnName string containing  name of the column where value was changed
   */
  public void setColumnName(String columnName) {
	 this.columnName = columnName;
  }
	/**
	 *   Get identifier of the occurrence whitch was changed
	 *   @return foreign identifier of the occurrence whitch was changed
	 */
	public int getOccurrenceId() {
	   return this.occurrenceId;
	}

	/**
	 *   Set identifier of the occurrence whitch was changed
	 *   @param occurrenceId identifier of the occurrence whitch was changed
	 */
	public void setOccurrenceId(int occurrenceId) {
	   this.occurrenceId= occurrenceId;
	}	
	
   /**
	 *   Get identifier of the record whitch was changed
	 *   @return identifier of the record whitch was changed
	 */
	public int getId() {
	   return this.recordId;
	}

	/**
	 *   Set identifier of the record whitch was changed
	 *   @param recordId string containing identifier of the record whitch was changed
	 */
	public void setId(int recordId) {
	   this.recordId = recordId;
	}
	  
	/**
	*   Get operation whitch was used
	*   @return operation whitch was used
	*/
	public String getOperation() {
	  return this.operation;
	}

	/**
	*   Set operation whitch was used
	*   @param operation string containing operation whitch was used 
	*/
	public void setOperation(String operation) {
	  this.operation = operation;
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
    
	/**
	*   Get old value of atribute whitch was changed
	*   @return old value of atribute whitch was changed
	*/
    public String getOldValue() {
	  return this.oldValue;
    }

    /**
	*   Set old value of atribute whitch was changed
	*   @param oldValue string containing old value of atribute whitch was changed
	*/
    public void setOldValue(String oldValue) {
	  this.oldValue = oldValue;
    }
	   
	 /**
	*   Get new value of atribute whitch was changed
	*   @return new value of atribute whitch was changed 
	*/
    public String getNewValue() {
	  return this.newValue;
    }

    /**
	*   Set new value of atribute whitch was changed
	*   @param newValue string containing new value of atribute whitch was changed
	*/
    public void setNewValue(String newValue) {
	  this.newValue = newValue;
    }    
}
