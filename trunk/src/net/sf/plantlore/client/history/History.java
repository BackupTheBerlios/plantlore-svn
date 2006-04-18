/**
 * 
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
import java.util.Set;

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
    private ArrayList<HistoryRecord> editHistoryDataList = new ArrayList();    
    //seznam id vsech oznacenych polozek
    private HashSet markListId = new HashSet();
    //Seznam Item + maxIdItem (nejstarsi oznacene id pro dany Item=sloupec)
    private ArrayList<Object[]> markItem = new ArrayList();
    //Informuje o tom, zda byla zvolena volba "SelectAll"
    private boolean selectAll;
    //Informuje o tom zda exituje pro dany nalez vazba 1:1 mezi tabulkami tHabitats a tOccurrences
    private boolean relationship;
    //Informuje o tom zda doslo k editaci polozky z tabulky tHabitat
    private boolean editHabitat;
    //zprava pro uzivatele
    private String messageUndo;
    //zaznamenani ITEM, jejichz zmena ovlibni vice polozek
   private ArrayList<String> itemAffectMore = new ArrayList();
    
    //*********************Record of history ***************************************//    
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
    private int operation;
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
       	    query.addRestriction(PlantloreConstants.RESTR_EQ, Occurrence.ID, null, idOcc, null);
       } catch(RemoteException e) {
       	    System.err.println("RemoteException - History(), crateQuery");       	  
       }      
             
       
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
       }            
              
       int resultIdInsert = 0;
       try {
           // Execute query                    
           resultIdInsert = database.executeQuery(query);        
       } catch (DBLayerException e) {
           // Log and set an error                   
           logger.error("Searching history data with condition 'operation = insert' failed. Unable to execute search query.");          
       } catch (RemoteException e) {		 
    	   System.err.println("RemoteException- searchInsertInfo(), executeQuery");
	} finally {
    	   logger.debug("Searching history data with condition 'operation = insert' ends successfully");
           // Save "insert" history data
           setInsertResult(resultIdInsert);                    
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
			System.err.println("RemoteException- searchEditHistory(), createQuery");
		}
                
    	
        int resultIdEdit = 0;
        try {
            // Execute query                    
            resultIdEdit = database.executeQuery(query);        
        } catch (DBLayerException e) {
            // Log and set an error                   
            logger.error("Searching history data with condition 'operation = edit' failed. Unable to execute search query.");           
        } catch (RemoteException e) { 		   
     	   System.err.println("RemoteException- searchEditHistory(), executeQuery");
	 	} finally {
	 		logger.debug("Searching history data with condition 'operation = edit' ends successfully");
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
            logger.error("Too many results for searching insert operation.");  
    	}
            	
    	logger.debug("Retrieving query results."); 
    	Object[] objectHistory = null;
        try {
        	 // Retrieve selected row interval         	
         	try {
         		objectHistory = database.more(resultIdInsert, 1, 1);  
         	} catch(RemoteException e) {             	
             	logger.debug("RemoteException- setInsertResult, more");
             	return;
             }   
         	Object[] objHis = (Object[])objectHistory[0]; 
         	setWhen(((HistoryChange)objHis[0]).getWhen());
         	setNameUser(((HistoryChange)objHis[0]).getWho().getWholeName());         	
        } catch (DBLayerException e) {         
            logger.error("Processing search (inserting) results failed: "+e.toString());            
        } finally { 
        	logger.debug("Sets 'insert' data ends successfully.");        	
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
            	editHistoryDataList = new ArrayList<HistoryRecord>(); 
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
                     	System.err.println("RemoteException- processResult, more");
                     	logger.debug("RemoteException- processResult, more");
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
     * 
     * @param id
     * @return
     */
    public Object[] searchObject(String typeObject, int id) { 
    	
    	SelectQuery query = null;
    	if (typeObject.equals("Habitat")){
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
     * UNDO - funce projde oznacene zaznamy a nastavi stare hodnoty
     * Pokud dojde zmenou k ovlivneni vice nalezu, tak o tom informuje uzivatele
     */
    public void updateOlderChanges()
    {    	
    	    	
    	//Inicalization of hashTable
    	initOccurrenceHash();
    	initHabitatHash();    	  
        	
    	//number of result
    	int countResult = getEditResult();
    	// Pomocne hodnoty pro zjisteni zda zmena ovlivni vice nalezu
    	relationship = false;
    	editHabitat = false;
    	
    	//take from younger record to older record
    	for( int i=0; i < countResult; i++) {
    		if (! markListId.contains(i)) {
    			continue;
    		}
    		
    		//init history data about edit of record
    		historyRecord = (HistoryRecord)editHistoryDataList.get(i);    		
    		historyChange = historyRecord.getHistoryChange();
    		tableName = historyRecord.getHistoryColumn().getTableName();
    		columnName = historyRecord.getHistoryColumn().getColumnName();    		    			
            oldRecordId = historyChange.getOldRecordId();
            recordId = historyChange.getRecordId();
            occurrenceId = historyChange.getOccurrence().getId();		   
            operation = historyChange.getOperation();
            oldValue = historyRecord.getOldValue();
						
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
    			  
    			//init Calendar    		
    			Calendar isoDateTime = new GregorianCalendar();
    			
    			logger.debug("ColumnConstant: "+ columnConstant);
    			logger.debug("ColumnName: "+ columnName);
    			logger.debug("OldValue: "+ oldValue);    			     			
    			
    			switch (columnConstant) {
	                case 1: //Taxon  
	                	if (oldRecordId > 0 ) {
                                    //Select record Plant where id = oldRocordId 
                                    Object[] object = searchObject("Plant",oldRecordId);
                                    Plant plant = (Plant)object[0];
                                    //Set old value to attribute plantID
                                    occurrence.setPlant(plant);
                                    logger.debug("Set selected value for update of attribute Taxon.");	
                                    }else {
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
    		} else if (tableName.equals("Habitat") || tableName.equals("Village")
    				   || tableName.equals("Territory") || tableName.equals("Phytochorion")){
    			
                // Get a specified number of columnName from habitat mapping.
                int columnConstant;
                if (habitatHash.containsKey(columnName)) {
                         columnConstant = (Integer)habitatHash.get(columnName); 
    	        } else {
    	             columnConstant = 0;
    	        }        	    			
    			  
    			//informuje o tom, ze byla editovana tabulka tHabitat 
    			editHabitat = true;                       
    			
    			logger.debug("ColumnConstant: "+ columnConstant);
    			logger.debug("ColumnName: "+ columnName);
    			logger.debug("OldValue: "+ oldValue);  
    			
    			
    			// Save new value for the column        		
     			switch (columnConstant) {
 	                case 1:  //Quadrant     	                	
                		/* pokud doslo ke zmene vazeb mezi tHabitats a tOccurrences z 1:N na 1:1, tak v tOccurrences.cHabitatId
                		 * bude jiz vzdy ulozeno id nove insertovany zaznamu do tHabitats a nikdy uz nedojde k jeho zmene, tzn.
                		 * vazba mezi tabulkami pro dany nalez jiz bude na vzdy 1:1 
                		 */ 	                		  
                		occurrence.getHabitat().setQuadrant(oldValue);		                	
	                	logger.debug("Set selected value for update of attribute Quadrant.");
	                	if (operation == historyChange.HISTORYCHANGE_EDIT) {
	                		// existuji dva edity EDIT (ovlivni jeden nalez) a EDITGROUP (ovlivni vice nalezu)
	                		// potrebujeme zjistit, zda pro dany nalez je vazeba mezi tHabitats a tOccurrences vzdy 1:N
	                		// nebo zda editaci nalezu vznikla vazvba 1:1
	                		relationship = true;                                                                            
	                	} 	                	
 	                    break;
 	                case 2: //Place description 	                	 	                			                		 
                		occurrence.getHabitat().setDescription(oldValue);		                	
	                	logger.debug("Set selected value for update of attribute Description.");
	                	if (operation == historyChange.HISTORYCHANGE_EDIT) {	                		
	                		relationship = true;                                         
	                	} 	              	
 	                	break;
 	                case 3:  //Country 	                	 	                			                		 
                		occurrence.getHabitat().setCountry(oldValue);		                	
	                	logger.debug("Set selected value for update of attribute Country.");
	                	if (operation == historyChange.HISTORYCHANGE_EDIT) {	                		
	                		relationship = true;                                          
	                	} 	
 	                    break;
 	                case 4: //Altitude 	                	                			                		 
                		occurrence.getHabitat().setAltitude(Double.parseDouble(oldValue));		                	
	                	logger.debug("Set selected value for update of attribute Altitude.");
	                	if (operation == historyChange.HISTORYCHANGE_EDIT) {	                		
	                		relationship = true;                                          
	                	} 	
 	                	break;
 	                case 5:  //Latitude   	                		                			                		  
                		occurrence.getHabitat().setLatitude(Double.parseDouble(oldValue));		                	
	                	logger.debug("Set selected value for update of attribute Latitude.");
	                	if (operation == historyChange.HISTORYCHANGE_EDIT) {	                		
	                		relationship = true;                                        
	                	} 	
 	                    break;
 	                case 6: //Longitude 	                		                			                		
                		occurrence.getHabitat().setLongitude(Double.parseDouble(oldValue));		                	
	                	logger.debug("Set selected value for update of attribute Longitude.");
	                	if (operation == historyChange.HISTORYCHANGE_EDIT) {	                		
	                		relationship = true;                                         
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
	                	if (operation == historyChange.HISTORYCHANGE_EDIT) {	                		
	                		relationship = true;                                        
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
	                	if (operation == historyChange.HISTORYCHANGE_EDIT) {	                		
	                		relationship = true;                                        
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
	                	if (operation == historyChange.HISTORYCHANGE_EDIT) {	                		
	                		relationship = true;                                      
	                	} 	        	
	                    break;
	                case 10: //Note habitat	                		                			                		  
                		occurrence.getHabitat().setNote(oldValue);		                	
	                	logger.debug("Set selected value for update of attribute Note.");
	                	if (operation == historyChange.HISTORYCHANGE_EDIT) {	                		
	                		relationship = true;                                        
	                	} 	
	                	break;
 	                default:            
 	                    logger.error("No column defined for name "+ columnName);	                   
     			}  	    		
    		} else {
    			logger.error("No table defined");
    		}    			    		
    	}
 
    	//informovat uzivatele, co bude provedeno (viz seznam markLIstId, markItem) a dat mu volbu, zda ano ci ne... zde je nutne, aby fungovalo spravne 
    	//executeUpdate, aby k update doslo az po jeho zavolani....!!!!
    	//zavolat delete na vsechny vracene polozky Historiie - projit seznam s ID
    	//v tabulce tOccurrences by se meli aktualizovat polozky cUpdateWhen a cUpdatewho na uzivatele a cas, ktery zavolal undo
    	//zavolat znovu dotaz do db, aby se aktualizovaly vysledky ... createQuery, atd.
    	
    	//vygenerovani zpravy pro uzivatele    	
    	generateMessageUndo();
    }
    
    public void generateMessageUndo() {    	
    	messageUndo = "Budou provedeny následující změny:\n";      
    	int count = markItem.size();
    	for (int i=0; i<count; i++) {
    		Object[] itemList = (Object[])(markItem.get(i));
    		String item = (String)itemList[0];
    		Integer maxId = (Integer)itemList[1];      		
    		oldValue = ((HistoryRecord)editHistoryDataList.get(maxId)).getOldValue(); 
    		messageUndo = messageUndo + item + " --> " + oldValue + "\n";
    	}
    	if (!relationship && editHabitat) {
    		logger.debug(relationship);
    		logger.debug(editHabitat);
    		messageUndo = "\n" + messageUndo + "Tyto změny ovlivní více nálezů.\n";
    	}
    }
    
    public void commitUpdate() {
    	try {
			database.executeUpdate(occurrence);
		} catch (RemoteException e) {
			logger.error("CommitUpdate - RemoteException: "+e.toString());
		} catch (DBLayerException e) {
			logger.error("CommitUpdate - DBLayerException: "+e.toString());
		}
    }
  
    /**
     * Projde oznacene zaznamy a postupne je smaze z tabulek historie.
     * Pri mazani z tabulky tHistoryChange overi, zda na dany zaznam neni vice vazeb.
     */
    public void deleteHistoryRecords() {

    	//count of selected record
    	int count = editHistoryDataList.size();
    	
    	//take from younger record to older record
    	for( int i=0; i < count; i++) {
    		if (! markListId.contains(i)) {
    			continue;
    		}    		    		
    		historyRecord = (HistoryRecord)editHistoryDataList.get(i); 
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
    	markListId.clear();
    	markItem.clear();
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
    
    public ArrayList<HistoryRecord> getEditHistoryDataList() {
		  return this.editHistoryDataList;		  
	   }

	 public void setEditHistoryDataList(ArrayList<HistoryRecord> editHistoryDataList) {
		  this.editHistoryDataList = editHistoryDataList;		  
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
	public int getOperation() {
	  return this.operation;
	}

	/**
	*   Set operation whitch was used
	*   @param operation string containing operation whitch was used 
	*/
	public void setOperation(int operation) {
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
