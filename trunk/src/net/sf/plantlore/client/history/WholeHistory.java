/*
 * WholeHistory.java
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
import java.util.Hashtable;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.HistoryChange;
import net.sf.plantlore.common.record.HistoryRecord;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.Village;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.server.DBLayerException;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class WholeHistory {
    
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
    private ArrayList<Object> editObjectList = new ArrayList();
    // informace pro uzivatele o record undo
    private String messageUndo;
    
    //*********************Record of history, ... ***************************************//    
    private Occurrence occurrence;
    private HistoryRecord historyRecord;
    private HistoryChange historyChange;
    private Publication publication;
    private Author author;
    private Village village;
    private Territory territory;
    private Phytochorion phytochorion;
    
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
    //private java.util.Date when = new Date();	
    /** Old value of attribute*/    
    private String oldValue;
    /** New value of attribute*/
    //private String newValue;
   /** Name of user who did changed*/
    //private String nameUser;
    
    //********************************************************//
    /** Mapping of entities */
    private Hashtable<String, Integer> publicationHash;
    private Hashtable<String, Integer> habitatHash;
    private Hashtable<String, Integer> occurrenceHash;  
    private Hashtable<String, Integer> authorHash;
    
    
    /** Creates a new instance of WholeHistory */
    public WholeHistory(DBLayer database) {
          
       logger = Logger.getLogger(this.getClass().getPackage().getName());	 
       this.database = database;
       
       //nacist vsechny data z historie -->bez podminky, jen je seradit podle casu
       searchWholeHistoryData();
       //opet funkci pro vyzadani si dat postupne
       processResult(1, displayRows);
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
                System.err.println("RemoteException- searchWholeHistoryData(), createQuery");
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
                 		objectHistory = database.more(this.resultId, 1, to);  
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
    	initOccurrenceHash();
    	initHabitatHash();   
        initPublicationHash();
        initAuthorHash();       
        	
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
                } else if (operation == HistoryChange.HISTORYCHANGE_EDIT || operation == HistoryChange.HISTORYCHANGE_EDITGROUP) {
                    undoEdit();
                } else if (operation == HistoryChange.HISTORYCHANGE_DELETE) {
                    undoInsertDelete(1);
                } else {
                    logger.error("Incorrect opreration code: "+ operation);
                }                
        }
    }
    
    /**
     * ??? Habitat - nemuselo by se zaznamenavat cDelete
     * ??? Phytochorion, Village, Territory - asi bude potreba cDelete, abychom nezobrazovali nektere polozky, co se historii odstrani
     * delete == 1 ... smazat
     * delete == 0 ... obnovit
     */
    public void undoInsertDelete(int delete) {
        if (tableName.equals("Occurrence")){
             Object[] object = searchObject("Occurrence",recordId);             
             Occurrence occurrence = (Occurrence)object[delete];
             occurrence.setDeleted(1);
      //  } else if (tableName.equals("Habitat")) {
      //       Object[] object = searchObject("v",recordId);  
      //       Habitat habitat = (Habitat)object[delete];
      //       habitat.setDeleted(1);
        } else if (tableName.equals("Publication")) {
             Object[] object = searchObject("Publication",recordId);  
             Publication publication = (Publication)object[delete];
             publication.setDeleted(1);
        } else if (tableName.equals("Author")) {
             Object[] object = searchObject("Author",recordId);   
             Author author = (Author)object[delete];
             author.setDeleted(1);
        } else if (tableName.equals("Phytochorion")) {
             Object[] object = searchObject("Phytochorion",recordId);   
             Phytochorion phytochorion = (Phytochorion)object[delete];             
             //phytochorion.setDelete(1);
        } else if (tableName.equals("Territoriy")) {
             Object[] object = searchObject("Territory",recordId); 
             Territory territory = (Territory)object[delete];             
             //territory.setDelete(1);
        } else if (tableName.equals("Village")) {
             Object[] object = searchObject("Village",recordId); 
             Village village = (Village)object[delete];             
             //village.setDelete(1);
        } else {
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
        
        if (tableName.equals("Occurrence")){
                undoOccurrence();
        } else if (tableName.equals("Habitat")) {
                undoHabitat();
        } else if (tableName.equals("Publication")) {
                undoPublication();
        } else if (tableName.equals("Author")) {
                undoAuthor();
        } else if (tableName.equals("Phytochorion")) {
                undoPhytochorion();
        } else if (tableName.equals("Territory")) {
                undoTerritory();
        } else if (tableName.equals("Village")) {
                undoVillage();
        } else {
            logger.error("No table defined");
        }
    }

    /**
     *
     */
    public void undoOccurrence() {
        
        //zaznam v ramci, ktereho doslo k editaci tabulky tOccurrences
        occurrence = historyChange.getOccurrence();
        
        boolean objectList = editObjectList.contains(occurrence);
        if (!objectList) {
        	//pridani objektu do listu - informace o tom, ze byl dany objekt editovan
            editObjectList.add(occurrence);
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
        occurrence = historyChange.getOccurrence();
      
        //K editaci tabulky tHabitats dojde jen v pripade editace nejakeho konkretniho nalezu, proto nam staci nacist
        //data z tHabitats pres tOccurrence.cHabitatId a nasledne staci zavolat update jen na occurrence
        boolean objectList = editObjectList.contains(occurrence); 
        if (!objectList) {
        	//pridani objektu do listu - informace o tom, ze byl dany objekt editovan (editace habitat vzdy v ramci occurrence)
            editObjectList.add(occurrence);
        }
        logger.debug("editObjectList: "+objectList);
        logger.debug("Habitat - OccurrenceID: "+occurrence.getId());
        logger.debug("columnName: "+columnName);
        
        // Get a specified number of columnName from habitat mapping.
        int columnConstant;
        if (habitatHash.containsKey(columnName)) {
                 columnConstant = (Integer)habitatHash.get(columnName); 
        } else {
             columnConstant = 0;
        }        	    			

        //informuje o tom, ze byla editovana tabulka tHabitat 
//        editHabitat = true;

        // Save new value for the column        		
        switch (columnConstant) {
        case 1:  //Quadrant     	                	
                /* pokud doslo ke zmene vazeb mezi tHabitats a tOccurrences z 1:N na 1:1, tak v tOccurrences.cHabitatId
                 * bude jiz vzdy ulozeno id nove insertovany zaznamu do tHabitats a nikdy uz nedojde k jeho zmene, tzn.
                 * vazba mezi tabulkami pro dany nalez jiz bude na vzdy 1:1 
                 */ 	                		  
                occurrence.getHabitat().setQuadrant(oldValue);		                	
                logger.debug("Set selected value for update of attribute Quadrant.");
                if (operation == HistoryChange.HISTORYCHANGE_EDIT) {
                // existuji dva edity EDIT (ovlivni jeden nalez) a EDITGROUP (ovlivni vice nalezu)
                // potrebujeme zjistit, zda pro dany nalez je vazeba mezi tHabitats a tOccurrences vzdy 1:N
                // nebo zda editaci nalezu vznikla vazvba 1:1
//                relationship = true;
                } 	                	
            break;
        case 2: //Place description 	                	 	                			                		 
                occurrence.getHabitat().setDescription(oldValue);		                	
                logger.debug("Set selected value for update of attribute Description.");
                if (operation == HistoryChange.HISTORYCHANGE_EDIT) {	                		
//                        relationship = true;
                } 	              	
                break;
        case 3:  //Country 	                	 	                			                		 
                occurrence.getHabitat().setCountry(oldValue);		                	
                logger.debug("Set selected value for update of attribute Country.");
                if (operation == HistoryChange.HISTORYCHANGE_EDIT) {	                		
//                        relationship = true;
                } 	
            break;
        case 4: //Altitude 	                	                			                		 
                occurrence.getHabitat().setAltitude(Double.parseDouble(oldValue));		                	
                logger.debug("Set selected value for update of attribute Altitude.");
                if (operation == HistoryChange.HISTORYCHANGE_EDIT) {	                		
//                        relationship = true;
                } 	
                break;
        case 5:  //Latitude   	                		                			                		  
                occurrence.getHabitat().setLatitude(Double.parseDouble(oldValue));		                	
                logger.debug("Set selected value for update of attribute Latitude.");
                if (operation == HistoryChange.HISTORYCHANGE_EDIT) {	                		
//                        relationship = true;
                } 	
            break;
        case 6: //Longitude 	                		                			                		
                occurrence.getHabitat().setLongitude(Double.parseDouble(oldValue));		                	
                logger.debug("Set selected value for update of attribute Longitude.");
                if (operation == HistoryChange.HISTORYCHANGE_EDIT) {	                		
//                        relationship = true;
                } 	
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
                if (operation == HistoryChange.HISTORYCHANGE_EDIT) {	                		
//                        relationship = true;
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
                if (operation == HistoryChange.HISTORYCHANGE_EDIT) {	                		
//                        relationship = true;
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
                if (operation == HistoryChange.HISTORYCHANGE_EDIT) {	                		
//                        relationship = true;
                } 	        	
            break;
        case 10: //Note habitat	                		                			                		  
                occurrence.getHabitat().setNote(oldValue);		                	
                logger.debug("Set selected value for update of attribute Note.");
                if (operation == HistoryChange.HISTORYCHANGE_EDIT) {	                		
//                        relationship = true;
                } 	
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
        boolean objectList = editObjectList.contains(publication); 
        if (!objectList) {
        	//pridani objektu do listu - informace o tom, ze byl dany objekt editovan
            editObjectList.add(publication);
        }
        logger.debug("editObjectList.contains: "+objectList);
        logger.debug("Publication: "+publication.getId());
        logger.debug("columnName: "+columnName);
        
       // Get a specified number of columnName from habitat mapping.
        int columnConstant;
        if (publicationHash.containsKey(columnName)) {
                 columnConstant = (Integer)habitatHash.get(columnName); 
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
       boolean objectList = editObjectList.contains(author); 
       if (!objectList) {
       	//pridani objektu do listu - informace o tom, ze byl dany objekt editovan
           editObjectList.add(author);
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
    public void undoPhytochorion() {
        
        Object[] object = searchObject("Phytochorion", recordId);
        phytochorion = (Phytochorion)object[0];     
        
        //test, zda jiz dany zaznam byl editovan
        boolean objectList = editObjectList.contains(phytochorion); 
        if (!objectList) {
        	//pridani objektu do listu - informace o tom, ze byl dany objekt editovan
            editObjectList.add(phytochorion);
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
        boolean objectList = editObjectList.contains(village); 
        if (!objectList) {
        	//pridani objektu do listu - informace o tom, ze byl dany objekt editovan
            editObjectList.add(village);
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
        boolean objectList = editObjectList.contains(territory); 
        if (!objectList) {
        	//pridani objektu do listu - informace o tom, ze byl dany objekt editovan
            editObjectList.add(territory);
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
    	if (typeObject.equals("Occurrence")){
            try {
            	query = database.createQuery(Occurrence.class);
            	query.addRestriction(PlantloreConstants.RESTR_EQ, Occurrence.ID, null, id , null);
            } catch(RemoteException e) {
            	    System.err.println("RemoteException, searchObject() - Occurrence, createQuery");       	  
            }            
            
    	} else if (typeObject.equals("Habitat")){
            try {
            	query = database.createQuery(Habitat.class);
            	query.addRestriction(PlantloreConstants.RESTR_EQ, Habitat.ID, null, id , null);
            } catch(RemoteException e) {
            	    System.err.println("RemoteException, searchObject() - Habitat, createQuery");       	  
            }            
            
    	} else if (typeObject.equals("Plant")){
            try {
            	query = database.createQuery(Plant.class);
            	query.addRestriction(PlantloreConstants.RESTR_EQ, Plant.ID, null, id , null);
            } catch(RemoteException e) {
            	    System.err.println("RemoteException, searchObject() - Plant, createQuery");       	  
            }            
            
    	} else if (typeObject.equals("Author")){
            try {
            	query = database.createQuery(Author.class);
            	query.addRestriction(PlantloreConstants.RESTR_EQ, Author.ID, null, id , null);
            } catch(RemoteException e) {
            	    System.err.println("RemoteException, searchObject() - Author, createQuery");       	  
            }            
            
    	} else if (typeObject.equals("Publication")){
            try {
            	query = database.createQuery(Publication.class);
            	query.addRestriction(PlantloreConstants.RESTR_EQ, Publication.ID, null, id , null);
            } catch(RemoteException e) {
            	    System.err.println("RemoteException, searchObject() - Publication, createQuery");       	  
            }            
            
    	} else if (typeObject.equals("Village")){
            try {
            	query = database.createQuery(Village.class);
            	query.addRestriction(PlantloreConstants.RESTR_EQ, Village.ID, null, id, null);
            } catch(RemoteException e) {
            	    System.err.println("RemoteException, searchObject()- Village, createQuery");       	  
            }            
            
    	}  else if  (typeObject.equals("Territory")){
            try {
            	query = database.createQuery(Territory.class);
            	query.addRestriction(PlantloreConstants.RESTR_EQ, Territory.ID, null, id , null);
            } catch(RemoteException e) {
            	    System.err.println("RemoteException, searchObject()- Territory, createQuery");       	  
            }            
             
    	} else if (typeObject.equals("Phytochorion")){
            try {
            	query = database.createQuery(Phytochorion.class);
            	query.addRestriction(PlantloreConstants.RESTR_EQ, Phytochorion.ID, null, id , null);
            } catch(RemoteException e) {
            	    System.err.println("RemoteException, searchObject()- Phytochorion, createQuery");       	  
            }            
            
    	} else {
    		logger.error("SearchObject() - Incorrect type of object.");
    	}
                        
        int resultIdPlant = 0;
        try {                   
            resultIdPlant = database.executeQuery(query);        
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
                 objects = database.more(resultIdPlant, 1, 1);  
            } catch(RemoteException e) {            	
                logger.debug("RemoteException- searchObject, more");            	
            }   
        	object = (Object[])objects[0];           
       } catch (DBLayerException e) {
           // Log and set error in case of an exception
           logger.error("Processing search " +typeObject+ " results failed: "+e.toString());            
       }     	    
        return object; 	       	          	   
           	        
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
                database.executeUpdate(editObjectList.get(i));
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
    public void deleteHistory(int toResult) {

    	//count of selected record
 //   	int count = historyDataList.size();
    	
    	//take from younger record to older record
    	for( int i=0; i < toResult; i++) {
//    		if (! markListId.contains(i)) {
//    			continue;
//    		}    		    		
    		historyRecord = (HistoryRecord)historyDataList.get(i); 
    		historyChange = historyRecord.getHistoryChange(); 
    		
	    	try {
				database.executeDelete(historyRecord);
				logger.debug("Deleting historyRecord successfully. Number of result: "+i);
			} catch (RemoteException e) {
				logger.error("Deleting historyRecord - remoteException. "+e.toString());
			} catch (DBLayerException e) {
				logger.error("Deleting historyRecord failed. "+e.toString());
			}
			int countResult = searchHistoryChangeId(historyChange.getId());			
			if (countResult == 0) {
				//samzat zaznam z tabulky tHistoryChange - muzeme protoze neexistuji dalsi FK z tHistory.cChngeId
				//pokud po smazani zaznamu z tHistory jsme nasli alespon jeden zaznam, ktery ma stejny FK na zaznam z tChangeHistory
				try {
					database.executeDelete(historyChange);
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
//    	markListId.clear();
//    	markItem.clear();
    } 
    
    /**
     * Zjisteni kolik zaznamu v tabulce tHistory je provazano s konkretnim zaznamem z tHistoryHange 
     * @param id
     * @return
     */
    public int searchHistoryChangeId(int id){    	
    	SelectQuery query = null;
        try {
        	    query = database.createQuery(HistoryRecord.class);
        	    // Create aliases for table tHistoryChange.      
                query.createAlias("historyChange", "hc");  
                // Add restriction to cChangeId column 
                query.addRestriction(PlantloreConstants.RESTR_EQ, "hc.id", null, id , null);
        } catch(RemoteException e) {
        	    System.err.println("RemoteException- searchHistoryChangeId(), createQuery");       	  
        }        
        
        
        int resultIdChange = 0;
        try {                   
        	resultIdChange = database.executeQuery(query);        
        } catch (DBLayerException e) {                   
            logger.error("Searching historyChangeId failed. Unable to execute search query.");
        } catch (RemoteException e) {		 
     	   System.err.println("RemoteException- searchHistoryChangeId(), executeQuery");
 	    } 
        
 	    int countResult = 100;
 	    try {
			countResult = database.getNumRows(resultIdChange);
			logger.debug("SearchHistoryChangeId - Number of result: "+countResult);
		} catch (RemoteException e) {
			System.err.println("RemoteException- searchHistoryChangeId(), getNumRows");
		}		
		return countResult;
    }
    
     //***************************//
    //****Init Hashtable*********//
    //**************************//
    
    private void initOccurrenceHash() {
    	occurrenceHash = new Hashtable<String, Integer>(9); 
        occurrenceHash.put("plantId",1);
        occurrenceHash.put("yearCollected",2);
        occurrenceHash.put("monthCollected",3);
        occurrenceHash.put("dayCollected",4);
        occurrenceHash.put("timeCollected",5);           
        occurrenceHash.put("dataSource",6);
        occurrenceHash.put("herbarium",7);        
        occurrenceHash.put("noteOccurrence",8);
        occurrenceHash.put("publicationId",9);       
        //occurrenceHash.put("metadataId",10);
    }    
    
    private void initHabitatHash() {
    	habitatHash = new Hashtable<String, Integer>(11);         
        habitatHash.put("quadrant",1);
        habitatHash.put("description",2);
        habitatHash.put("country",3);
        habitatHash.put("altitude",4);
        habitatHash.put("latitude",5);
        habitatHash.put("longitude",6);      
        habitatHash.put("nameVillage",7);      
        habitatHash.put("namePhytochorion",8);
        habitatHash.put("code",8);
        habitatHash.put("nameTerritory",9);
        habitatHash.put("noteHabitat",10);
    }    
    
    private void initPublicationHash() {
        publicationHash = new Hashtable<String, Integer>(6);
        publicationHash.put("collectionName",0);
        publicationHash.put("collectionYearPublication",1);
        publicationHash.put("journalName",2);
        publicationHash.put("journalAuthorName",3);
        publicationHash.put("referenceDetail",4);
        publicationHash.put("urlPublication",5);      
    }
    
    private void initAuthorHash() {
        authorHash = new Hashtable<String, Integer>(8);
        authorHash.put("firstName",0);
        authorHash.put("surname",1);
        authorHash.put("organization",2);
        authorHash.put("role",3);
        authorHash.put("address",4);
        authorHash.put("email",5);
        authorHash.put("urlAuthor",6);
        authorHash.put("noteAuthor",7);        
    }              
    
       
    //****************************//
    //****Get and set metods*****//
    //**************************//
    
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
    
}
