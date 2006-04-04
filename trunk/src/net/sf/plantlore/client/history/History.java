/**
 * 
 */
package net.sf.plantlore.client.history;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Observable;

import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.server.DBLayerException;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.PlantloreConstants;
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
 * @author Lada Oberreiterova
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
    private ArrayList<HistoryRecord> editHistoryDataList;
   
    //********************* ***************************************//    
    private Occurrence occurrence;
	private HistoryRecord historyRecord;
	private HistoryChange historyChange;
	
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
     private Hashtable<String, Integer> publicationHash;
     private Hashtable<String, Integer> habitatHash;
     private Hashtable<String, Integer> occurrenceHash;  
    
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
       
 /*
  *  v konstruktoru se bude predavat reference na OCCURRENCE pro ktery je vyvolana
  *  histori, takze nasledujici cast kodu nebude potreba. 
  */             
       // Create new Select query
       SelectQuery query = null;
       try {
       	    query = database.createQuery(Occurrence.class);
       } catch(RemoteException e) {
       	    System.err.println("RemoteException - History(), crateQuery");       	  
       }      
       query.addRestriction(PlantloreConstants.RESTR_EQ, Occurrence.ID, null, idOcc, null);      
       
       int resultId = 0;
       try {
           // Execute query                    
           resultId = database.executeQuery(query);        
       } catch (DBLayerException e) {
           // Log and set an error                   
           logger.error("Searching occurence failed.");          
       } catch (RemoteException e) {
    	   System.err.println("RemoteException- History(), executeQuery");
	   } finally {
	    	   logger.debug("Searching occurrence ends successfully");                           
	       }   
	   
	   Object[] objectOccurrence = null;
	   Object[] objHis = null;
       try {
       	 // Retrieve selected row interval         	
        	try {
        		objectOccurrence = database.more(resultId, 1, 1);  
        	} catch(RemoteException e) {            	  
            	return;
            }   
        	objHis = (Object[])objectOccurrence[0];                            
       } catch (DBLayerException e) {
           // Log and set error in case of an exception
           logger.error("Processing search occurrence results failed: "+e.toString());            
       } finally { 
       	   logger.debug("Sets occurrence data ends successfully.");        	
       } 
              
       occurrence = ((Occurrence)objHis[0]);
       
       setNamePlant(namePlant);
       setNameAuthor(nameAuthor);
       setLocation(location);
/*
 * konec casti kodu, ktera bude dobudoucna nahrazena.
 * 
 * Konstruktor History(DBLayer database,Occurrence occurrenceRec, String[] nameAuthors)
 * 
 *     this.ocurrence = occurrenceRec;
 *     setNamePlant(occurrence.getPlant().getTaxon());       
 *     setLocation(occurrence.getHabitat().getNearestVillage().getName()); 
 *
 * 	   setNameAuthor(nameAuthors);	  
 *     ...musit to byt retezec autoru - muze jich byt vice
 *     ...v historii se editace autoru zaznamenavat nebude 
 */       
              	   
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
        query.addRestriction(PlantloreConstants.RESTR_EQ, "hc.operation", null, HistoryChange.HISTORYCHANGE_EDIT, null);
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
                this.editHistoryDataList = new ArrayList<HistoryRecord>();                
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
                    this.editHistoryDataList = new ArrayList<HistoryRecord>();
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
    	    editHistoryData[ii][3] = L10n.getString((((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getColumnName()));
    	    editHistoryData[ii][4] = ((HistoryRecord)editHistoryDataList.get(i)).getOldValue();
    	    editHistoryData[ii][5] = ((HistoryRecord)editHistoryDataList.get(i)).getNewValue();
    	    ii++;
    	}  
    	return this.editHistoryData;
    	
    }    
    
  
    /**
     * 
     * @param id
     * @return
     */
    public int searchHistoryChangeId(int id){
    	SelectQuery query = null;
        try {
        	    query = database.createQuery(HistoryRecord.class);
        } catch(RemoteException e) {
        	    System.err.println("RemoteException- searchHistoryChangeId(), createQuery");       	  
        }                    
        // Create aliases for table tHistoryChange.      
        query.createAlias("historyChange", "hc");        
        // Add restriction to CUNITVALUE column of tOccurence table
        query.addRestriction(PlantloreConstants.RESTR_EQ, "hc.id", null, id , null);
        
        int resultIdChange = 0;
        try {                   
        	resultIdChange = database.executeQuery(query);        
        } catch (DBLayerException e) {                   
            logger.error("Searching historyChangeId failed. Unable to execute search query.");
        } catch (RemoteException e) {		 
     	   System.err.println("RemoteException- searchHistoryChangeId(), executeQuery");
 	    } finally {
     	   logger.debug("Searching historyChangeId ends successfully.");
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
    
    /**
     * 
     * @param id
     * @return
     */
    public Object[] searchObject(String typeObject, int id, String oldRecordValue) { 
    	
    	SelectQuery query = null;
    	if (typeObject.equals("Habitat")){
    		try {
            	query = database.createQuery(Habitat.class);	        		        	    
            } catch(RemoteException e) {
            	    System.err.println("RemoteException, searchObject() - Habitat, createQuery");       	  
            }            
            query.addRestriction(PlantloreConstants.RESTR_EQ, Habitat.ID, null, id , null);
    	} else if (typeObject.equals("Plant")){
    		try {
            	query = database.createQuery(Plant.class);	        		        	    
            } catch(RemoteException e) {
            	    System.err.println("RemoteException, searchObject() - Plant, createQuery");       	  
            }            
            query.addRestriction(PlantloreConstants.RESTR_EQ, Plant.TAXON, null, oldRecordValue , null);
    	}else if (typeObject.equals("Village")){
    		try {
            	query = database.createQuery(Village.class);	        		        	    
            } catch(RemoteException e) {
            	    System.err.println("RemoteException, searchObject()- Village, createQuery");       	  
            }            
            query.addRestriction(PlantloreConstants.RESTR_EQ, Village.NAME, null, oldRecordValue, null);
    	}  else if  (typeObject.equals("Territory")){
    		try {
            	query = database.createQuery(Territory.class);	        		        	    
            } catch(RemoteException e) {
            	    System.err.println("RemoteException, searchObject()- Territory, createQuery");       	  
            }            
            query.addRestriction(PlantloreConstants.RESTR_EQ, Territory.NAME, null, oldRecordValue , null); 
    	} else if (typeObject.equals("Phytochorion")){
    		try {
            	query = database.createQuery(Phytochorion.class);	        		        	    
            } catch(RemoteException e) {
            	    System.err.println("RemoteException, searchObject()- Phytochorion, createQuery");       	  
            }            
            query.addRestriction(PlantloreConstants.RESTR_EQ, Phytochorion.NAME, null, oldRecordValue , null);
    	} else if (typeObject.equals("PhytochorionCode")){
    		try {
            	query = database.createQuery(Phytochorion.class);	        		        	    
            } catch(RemoteException e) {
            	    System.err.println("RemoteException, searchObject()- Phytochorion code, createQuery");       	  
            }            
            query.addRestriction(PlantloreConstants.RESTR_EQ, Phytochorion.CODE, null, oldRecordValue , null);
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
 	    } finally {
     	   logger.debug("Searching " +typeObject+ " ends successfully");
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
       } finally {     	    
    	   	return object; 	       	          	   
       }     	        
    }
    
    /**
     * 
     *
     */
    public void deleteHistoryRecords() {
    	try {
			database.executeDelete(historyRecord);
			logger.debug("Deleting historyRecord successfully.");
		} catch (RemoteException e) {
			logger.error("Deleting historyRecord - remoteException. "+e.toString());
		} catch (DBLayerException e) {
			logger.error("Deleting historyRecord failed. "+e.toString());
		}
		int countResult = searchHistoryChangeId(historyRecord.getHistoryChange().getId());
		if (countResult == 1) {
			//samzat zaznam z tabulky tHistoryChange - muzeme protoze neexistuji dalsi FK z tHistory.cChngeId
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
    	    	
    	//Inicalization of hashTable
    	initOccurrenceHash();
    	initHabitatHash();
    	initPublicationHash();   
    	    	
    	//number of selected rows
    	int countMark = markResult.size();
    	// Index of firt row currently displayed
    	int indexFirstRow = getCurrentFirstRow();
    	//List of changed ITEM
    	ArrayList<String> changedList = new ArrayList<String>();
    	//take from older record to younger record
    	for( int i=countMark-1; i >= 0; i--) {    	
    		logger.debug("Number of selected row: "+markResult.get(i));
    		historyRecord = (HistoryRecord)editHistoryDataList.get((Integer)markResult.get(i)+ getCurrentFirstRow()-1);    		
    		historyChange = historyRecord.getHistoryChange();
    		tableName = historyRecord.getHistoryColumn().getTableName();
    		columnName = historyRecord.getHistoryColumn().getColumnName();
    		// oldRecordId je defautne nastaveno v databazi na hodnotu 0 !!!    			
			oldRecordId = historyChange.getOldRecordId();
			recordId = historyChange.getRecordId();
			occurrenceId = historyChange.getOccurrence().getId();
			oldValue = historyRecord.getOldValue();
			
			this.editHistoryDataList.remove((Integer)markResult.get(i)+ getCurrentFirstRow()-1);
			
    		if (tableName.equals("Occurrence")){  
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
    			    			
    			logger.debug("ColumnConstant: "+ columnConstant);
    			logger.debug("ColumnName: "+ columnName);
    			logger.debug("OldValue: "+ oldValue);    			     			
    			
    			switch (columnConstant) {
	                case 1: //Taxon  
	                	//test, zda byla nastavena hodnota pro update pro Taxon 
	                	// pokud ano, tak se jedna o mladsi zmenu, ktera bude jen smazana z tabulek historie
	                	if ( ! changedList.contains("taxon") ){
	                		changedList.add("taxon");		                		
                			Object[] object = searchObject("Plant",0,"oldValue");
		                	Plant plant = (Plant)object[0];
		                	occurrence.setPlant(plant);
		                	logger.debug("Set selected value for update of attribute Taxon.");	                		
	                	} else {
	                		logger.debug("Later edit of Taxon. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();	                			                		                			                
	                    break;
	                case 2: //Year	
	                	if (! changedList.contains("year")) {
	                		changedList.add("year");	                		
		                	occurrence.setYearCollected(Integer.parseInt(oldValue));
		                	logger.debug("Set selected value for update of attribute Year.");
		                	//Update attribute isoDateTimeBegin (Year + Mont + Day + Time)		                	
	                		Date time = occurrence.getTimeCollected();
	                		Date isoDateTime = new Date();
	                		isoDateTime.setDate(occurrence.getDayCollected());
	                		isoDateTime.setMonth(occurrence.getMonthCollected());
	                		isoDateTime.setYear(Integer.parseInt(oldValue));	                		
	                		//occurrence.setIsoDateTimeBegin(isoDateTime);
	                		
	                	} else {
	                		logger.debug("Later edit of Year. ");	                		
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();             	
	                	break;
	                case 3: //Month 
	                	if (! changedList.contains("month")) {
	                		changedList.add("month");	                		
	                		occurrence.setMonthCollected(Integer.parseInt(oldValue));
	                		logger.debug("Set selected value for update of attribute Month.");
	                	} else {
	                		logger.debug("Later edit of Month. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
	                    break;
	                case 4: //Day	                	
	                	if (! changedList.contains("day")) {
	                		changedList.add("day");	                		
		                	occurrence.setDayCollected(Integer.parseInt(oldValue));
		                	logger.debug("Set selected value for update of attribute Day.");
	                	} else {
	                		logger.debug("Later edit of Day. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
	                	break;
	                case 5: //Time 	                	
	                	if (! changedList.contains("time")) {
	                		changedList.add("time");
	                		//hodnota se bude muset rozdelit na hodiny:minuty:sekundy a pak se ulozit pomoci get a set metod pro Date
		                	//occurrence.setTimeCollected(Integer.parseInt(oldValue));
	                		logger.debug("Set selected value for update of attribute Time.");
	                	} else {
	                		logger.debug("Later edit of Time. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
	                    break;
	                case 6: //Source	                	
	                	if (! changedList.contains("source")) {
	                		changedList.add("source");
		                	occurrence.setDataSource(oldValue);
		                	logger.debug("Set selected value for update of attribute DataSource.");
	                	} else {
	                		logger.debug("Later edit of DataSource. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
	                	break;
	                case 7: //Herbarium
	                	if (! changedList.contains("hebarium")){
	                		changedList.add("herbarium");
	                		occurrence.setHerbarium(oldValue);
	                		logger.debug("Set selected value for update of attribute Herbarium.");
	                	} else {
	                		logger.debug("Later edit of Herbarium. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
	                    break;
	                case 8: //Note occurrence	
	                	if (! changedList.contains("noteOccurrence")) {
	                		changedList.add("noteOccurrence");
	                		occurrence.setNote(oldValue);
	                		logger.debug("Set selected value for update of attribute NoteOccurrence.");
	                	} else {
	                		logger.debug("Later edit of NoteOccurrence. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
	                	break;
	                default:            
	                    logger.error("No column defined for name "+ columnName);	                   
    			}  	
    		} else if (tableName.equals("Habitat") || tableName.equals("Village")
    				   || tableName.equals("Territory") || tableName.equals("Phytochorion")){
    			
    			// Get a specified number of columnName from habitat mapping.
    			int columnConstant;
    			if (occurrenceHash.containsKey(columnName)) {
    				 columnConstant = (Integer)occurrenceHash.get(columnName); 
    	        } else {
    	             columnConstant = 0;
    	        }        	    			
    			    			
    			logger.debug("ColumnConstant: "+ columnConstant);
    			logger.debug("ColumnName: "+ columnName);
    			logger.debug("OldValue: "+ oldValue);  
    			
    			
    			// Save new value for the column        		
     			switch (columnConstant) {
 	                case 1:  //Quadrant    
 	                	if (! changedList.contains("quadrant")) {
	                		changedList.add("quadrant");
	                		if (oldRecordId != 0 ){
		                		/*
		                		 * Zmenou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
		                		 * Do occurrence.cHabitatId ulozit nacteny objekt Habitat
		                		 * Zjistit, zda na aktulani objkt Habitat existuji jeste nejake FK z tOccurrence.cHabitatID
		                		 * pokud neexistuji, tak ho smazem ...Pozor na to,ze smazane zaznamy jsou oznaceny jeko cDelete=1 !!!
		                		 */
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute Qudrant.");
		                	}else {		                	
			                	occurrence.getHabitat().setQuadrant(oldValue);
		                	}
	                		logger.debug("Set selected value for update of attribute Quadrant.");
	                	} else {
	                		logger.debug("Later edit of Quadrant. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();	                	              		               
 	                    break;
 	                case 2: //Place description
 	                	if (! changedList.contains("description")) {
	                		changedList.add("description");
	                		if (oldRecordId != 0 ){
		                		//Zmenou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute Description.");
		                	}else {		                	
			                	occurrence.getHabitat().setDescription(oldValue);
		                	}
	                		logger.debug("Set selected value for update of attribute Description Habitat.");
	                	} else {
	                		logger.debug("Later edit of Description Habitat. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
 	                	break;
 	                case 3:  //Country
 	                	if (! changedList.contains("country")) {
	                		changedList.add("country");
	                		if (oldRecordId != 0 ){
		                		//menou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute Country.");
		                	}else {		                	
			                	occurrence.getHabitat().setCountry(oldValue);
		                	}
	                		logger.debug("Set selected value for update of attribute Country.");
	                	} else {
	                		logger.debug("Later edit of Country. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
 	                    break;
 	                case 4: //Altitude
 	                	if (! changedList.contains("altitude")) {
	                		changedList.add("altitude");
	                		if (oldRecordId != 0 ){
		                		//menou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute Altitude.");
		                	}else {		                	
			                	occurrence.getHabitat().setAltitude(Double.parseDouble(oldValue));
		                	}
	                		logger.debug("Set selected value for update of attribute Altitude.");
	                	} else {
	                		logger.debug("Later edit of Altitude. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
 	                	break;
 	                case 5:  //Latitude  
 	                	if (! changedList.contains("latitude")) {
	                		changedList.add("latitude");
	                		if (oldRecordId != 0 ){
		                		//menou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute Latitude.");
		                	}else {		                	
			                	occurrence.getHabitat().setLatitude(Double.parseDouble(oldValue));
		                	}
	                		logger.debug("Set selected value for update of attribute Latitude.");
	                	} else {
	                		logger.debug("Later edit of Latitude. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
 	                    break;
 	                case 6: //Longitude
 	                	if (! changedList.contains("longitude")) {
	                		changedList.add("longitude");
	                		if (oldRecordId != 0 ){
		                		//menou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute Longitude.");
		                	}else {		                	
			                	occurrence.getHabitat().setLongitude(Double.parseDouble(oldValue));
		                	}
	                		logger.debug("Set selected value for update of attribute Longitude.");
	                	} else {
	                		logger.debug("Later edit of Longitude. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
 	                	break;
 	                case 7: //Nearest bigger seat  	
 	                	if (! changedList.contains("nameVillage")) {
	                		changedList.add("nameVillage");
	                		if (oldRecordId != 0 ){
		                		//zmenou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);	   
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute Village.");
		                	}else {		                	
		                		// Nacteni Village pro nasledny update tHabitat.cNearestVillageId
	                			Object[] object = searchObject("Village",0,oldValue);
	                			Village village = (Village) object[0];
			                	occurrence.getHabitat().setNearestVillage(village);
			                	logger.debug("Set selected value for update of attribute NearesVillage.");
		                	}	                		
	                	} else {
	                		logger.debug("Later edit of Village. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
 	                    break;
 	                case 8: //Phytochorion or hytochorion code 	                	
 	                	if (! changedList.contains("phytochorion")) {
	                		changedList.add("phytochorion");
	                		if (oldRecordId != 0 ){
		                		//zmenou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);	 	    
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute Phytochorion.");
		                	}else {		                	
		                		// Nacteni Phytochorion pro nasledny update tHabitat.cPhytochorionId
	                			Object[] object = searchObject("Phytochorion",0,oldValue);
	                			Phytochorion phytochorion = (Phytochorion) object[0];
			                	occurrence.getHabitat().setPhytochorion(phytochorion);
			                	logger.debug("Set selected value for update of attribute Phytochorion.");
		                	}	                		
	                	} else {
	                		logger.debug("Later edit of Phytochorion. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
 	                	break; 	
 	               case 9: //Phytochorion code	                	
	                	if (! changedList.contains("phytochorionCode")) {
	                		changedList.add("phytochorionCode");
	                		if (oldRecordId != 0 ){
		                		//zmenou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);	 	   
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute PhytochorionCode.");
		                	}else {		                	
		                		// Nacteni Phytochorion pro nasledny update tHabitat.cPhytochorionId
	                			Object[] object = searchObject("PhytochorionCode",0,oldValue);
	                			Phytochorion phytochorion = (Phytochorion) object[0];
			                	occurrence.getHabitat().setPhytochorion(phytochorion);
			                	logger.debug("Set selected value for update of attribute Phytochorion code.");
		                	}	                		
	                	} else {
	                		logger.debug("Later edit of Phytochorion code. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
	                	break; 	     
	                case 10:  //Territory        
	                	if (! changedList.contains("nameTerritory")) {
	                		changedList.add("nameTerritory");
	                		if (oldRecordId != 0 ){
		                		//zmenou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);	 	
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute Territory.");
		                	}else {		                	
		                		// Nacteni Territory pro nasledny update tHabitat.cTerritory
	                			Object[] object = searchObject("Territory",0,oldValue);
	                			Territory territory = (Territory) object[0];
			                	occurrence.getHabitat().setTerritory(territory);
			                	logger.debug("Set selected value for update of attribute Territory.");
		                	}	                		
	                	} else {
	                		logger.debug("Later edit of Territory. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
	                    break;
	                case 11: //Note habitat
	                	if (! changedList.contains("noteHabitat")) {
	                		changedList.add("noteHabitat");
	                		if (oldRecordId != 0 ){
		                		//zmenou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute NoteHabitat");
		                	}else {		                	
			                	occurrence.getHabitat().setNote(oldValue);
		                	}
	                		logger.debug("Set selected value for update of attribute NoteHabitat.");
	                	} else {
	                		logger.debug("Later edit of NoteHabitat. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
	                	break;
 	                default:            
 	                    logger.error("No column defined for name "+ columnName);	                   
     			}  	
    		} else if (tableName.equals("Publication")){
    			
    			// Get a specified number of columnName from publication mapping.
    			int columnConstant;
    			if (occurrenceHash.containsKey(columnName)) {
    				 columnConstant = (Integer)occurrenceHash.get(columnName); 
    	        } else {
    	             columnConstant = 0;
    	        }        	    			
    			    			
    			logger.debug("ColumnConstant: "+ columnConstant);
    			logger.debug("ColumnName: "+ columnName);
    			logger.debug("OldValue: "+ oldValue);  
    			 			
    			
    			// Save new value for the column    			         		
    			switch (columnConstant) {
	                case 1: //Collection Name   
	                	if (! changedList.contains("collectionName")) {
	                		changedList.add("collectionName");
	                		if (oldRecordId != 0 ){
		                		//zmenou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute CollectionName.");
		                	}else {		                	
			                	occurrence.getPublication().setCollectionName(oldValue);
			                	//Update atribute isoDataTimeBegin (CollectionName + CollectionYearPublication + JournalName + JournalAuthor)
			                	Integer publicationYear = occurrence.getPublication().getCollectionYearPublication();
			                	String journalName = occurrence.getPublication().getJournalName();
			                	String journalAuthor = occurrence.getPublication().getJournalAuthorName();
			                	occurrence.getPublication().setReferenceCitation(oldValue+" "+publicationYear+" "+journalName+" "+journalAuthor);
		                	}
	                		logger.debug("Set selected value for update of attribute CollectionName .");
	                	} else {
	                		logger.debug("Later edit of CollectionName . ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
	                    break;
	                case 2: //Year of publication
	                	if (! changedList.contains("colletionYearPublication")) {
	                		changedList.add("colletionYearPublication");
	                		if (oldRecordId != 0 ){
		                		//zmenou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute CollectionYearPublication.");
		                	}else {		                	
			                	occurrence.getPublication().setCollectionYearPublication(Integer.parseInt(oldValue));
			                	//Update atribute isoDataTimeBegin (CollectionName + CollectionYearPublication + JournalName + JournalAuthor)
			                	String collectionName = occurrence.getPublication().getCollectionName();			                	
			                	String journalName = occurrence.getPublication().getJournalName();
			                	String journalAuthor = occurrence.getPublication().getJournalAuthorName();
			                	occurrence.getPublication().setReferenceCitation(collectionName+" "+oldValue+" "+journalName+" "+journalAuthor);
		                	}
	                		logger.debug("Set selected value for update of Year of publication .");
	                	} else {
	                		logger.debug("Later edit of Year of publication. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
	                	break;
	                case 3: //Journal  
	                	if (! changedList.contains("journalName")) {
	                		changedList.add("journalName");
	                		if (oldRecordId != 0 ){
		                		//zmenou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute JournalName.");
		                	}else {		                	
			                	occurrence.getPublication().setJournalName(oldValue);
			                	// Update atribute isoDataTimeBegin (CollectionName + CollectionYearPublication + JournalName + JournalAuthor)			                	
			                	String collectionName = occurrence.getPublication().getCollectionName();
			                	Integer publicationYear = occurrence.getPublication().getCollectionYearPublication();			                	
			                	String journalAuthor = occurrence.getPublication().getJournalAuthorName();
			                	occurrence.getPublication().setReferenceCitation(collectionName+" "+publicationYear+" "+oldValue+" "+journalAuthor);
		                	}
	                		logger.debug("Set selected value for update of attribute JournalName .");
	                	} else {
	                		logger.debug("Later edit of JournalName . ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
	                    break;
	                case 4: //Author of journal
	                	if (! changedList.contains("journalAuthor")) {
	                		changedList.add("journalAuthor");
	                		if (oldRecordId != 0 ){
		                		//zmenou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute JournalAuthor.");
		                	}else {		                	
			                	occurrence.getPublication().setJournalAuthorName(oldValue);
			                	//Update atribute isoDataTimeBegin (CollectionName + CollectionYearPublication + JournalName + JournalAuthor)			                	
			                	String collectionName = occurrence.getPublication().getCollectionName();
			                	Integer publicationYear = occurrence.getPublication().getCollectionYearPublication();
			                	String journalName = occurrence.getPublication().getJournalName();			                	
			                	occurrence.getPublication().setReferenceCitation(collectionName+" "+publicationYear+" "+journalName+" "+oldValue);
		                	}
	                		logger.debug("Set selected value for update of attribute JournalAuthor .");
	                	} else {
	                		logger.debug("Later edit of JournalAuthor. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
	                	break;	                
	                case 5: //Reference detail
	                	if (! changedList.contains("referenceDetail")) {
	                		changedList.add("referenceDetail");
	                		if (oldRecordId != 0 ){
		                		//zmenou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute ReferenceDetail.");
		                	}else {		                	
			                	occurrence.getPublication().setReferenceDetail(oldValue);
		                	}
	                		logger.debug("Set selected value for update of attribute ReferenceDetail.");
	                	} else {
	                		logger.debug("Later edit of ReferenceDetail. ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
	                	break;
	                case 6: //URL    
	                	if (! changedList.contains("url")) {
	                		changedList.add("url");
	                		if (oldRecordId != 0 ){
		                		//zmenou polozky doslo k insertu a prenastaveni occurrence.cHabitatId --> nutno nacist a ulozitHabitat s id = oldRecordId
	                			Object[] object = searchObject("Habitat",oldRecordId,"");
	                			Habitat habitatOld = (Habitat) object[0];
			                	occurrence.setHabitat(habitatOld);
			                	logger.debug("Change tOccurrence.cHabitatId. The change was created by attribute URLpublication.");
		                	}else {		                	
			                	occurrence.getPublication().setUrl(oldValue);
		                	}
	                		logger.debug("Set selected value for update of attribute URL (publication).");
	                	} else {
	                		logger.debug("Later edit of ReferenceDetail (publication). ");
	                	}
	                	//Delete record from tHistory and tHistoryChange
	                	deleteHistoryRecords();
	                    break;	                
	                default:            
	                    logger.error("No column defined for name "+ columnName);	                   
    			}  	
    		} else {
    			logger.equals("No table defined");
    		}    			
    		
    	}
    	
    	//projdem vysledky od 0 do zobrazeneho vysledku
    	//pokud je tu informace o editaci polozky, ktere byla vracena starsi hodnota, tak se tato informace smaze 
    	for( int i=0; i<indexFirstRow-1; i++) {
    		String columnName = ((HistoryRecord)editHistoryDataList.get(i)).getHistoryColumn().getColumnName();
    		if (changedList.contains(columnName)){
    			
    		}
    			
    	}

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
        //isoDataTimeBegin je slozena s Year + Month + Day + Time ... mela by se zmenit vzdy, kdyz
        // dojde ke zmene nektere z techto polozek ... jak to nejlepe zaridit???
        //occurrenceHash.put("isoDataTimeBegin",9);
        /*
         * Jak se bude chovat cUpdateWhen, cUpdateWho v historii - asi se nastavi 
         * cas vyvolani undo v historii a uzivatel, ktery to vyvolal
         */
        //occurrenceHash.put("cUpdateWhen",10);
        //occurrenceHash.put("cUpdateWho",11);
        /*
         * Uvadet nejakou informaci z metadat - pripadne jakou
         */
        //occurrenceHash.put("metadataId",12);
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
        habitatHash.put("code",9);
        habitatHash.put("nameTerritory",10);
        habitatHash.put("noteHabitat",11);
    }
    
    private void initPublicationHash() {
        publicationHash = new Hashtable<String, Integer>(7);                       
        publicationHash.put("collectionName",1);
        publicationHash.put("colletionYearPublication",2);
        publicationHash.put("journalName",3);
        publicationHash.put("journalAuthorName",4);
        //publicationHash.put("referenceCitation",7);
        publicationHash.put("referenceDetail",5);
        publicationHash.put("url",6);
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
