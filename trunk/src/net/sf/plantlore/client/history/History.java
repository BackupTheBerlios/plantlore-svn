/**
 * 
 */
package net.sf.plantlore.client.history;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Observable;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.server.DBLayerException;
import net.sf.plantlore.client.dblayer.query.Query;
//import net.sf.plantlore.client.dblayer.query.SelectQuery;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.client.dblayer.result.Result;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.common.record.HistoryRecord;
import net.sf.plantlore.common.record.HistoryChange;
import net.sf.plantlore.common.record.HistoryColumn;


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
    private static final int DEFAULT_DISPLAY_ROWS = 5;    
    /** Actual number of rows to display */
    private int displayRows = DEFAULT_DISPLAY_ROWS;   
    /** Index of the first record shown in the table */
    private int currentFirstRow;
    
    //*******Informations about searching Result from database*****//
    /** Result of the search query */
    private int resultId = 0;
    /** Data (results of a search query) displayed in the table */
    private Object[][] editHistoryData;
    /** List of data (results of a search query) displayed in the table */
    private ArrayList editHistoryDataList;
   
    //**************Informations about HistoryRecord*************//
    /** Name of the table where value was changed*/
	private String tableName;  
	/** Name of the column where value was changed*/
	private String columnName;
	/** Unique value identified record. 
	 * Foring key referenced to table TOCCURRENCES */	
	private Integer occurrenceId;
	/**Unique value identified record in table where value was changed */
	private int recordId;
	/** Operation whitch was used*/   
	private String operation;
	/** Date and time when the reccord was changed*/
	private java.util.Date when;	
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
	
	//***********************************************************//
	 private static final Integer INSERT = 1;
	 private static final Integer EDIT = 2;
	 private static final Integer EDITINSERT = 3;
	 private static final Integer DELETE = 4;
	
    
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
       
       setOccurrenceId(0);
       setOccurrenceId(idOcc);
       setNamePlant(namePlant);
       setNameAuthor(nameAuthor);
       setLocation(location);
	   
       //Searching for information about data entries concerned with specified occurrence
       searchInsertInfo(occurrenceId);
	   //Searching for information about data editing concerned with specified occurrence
	   searchEditHistory(occurrenceId);
	   //Process results of a search "edit" query 
	   processEditResult(1,displayRows);
    }	

    /**
     *  Searches for information about data entries concerned with specified occurrence.
     *  @param idOccurrence Unique value identified occurrence
     */
    public void searchInsertInfo(Integer occurrenceId) {
     
       // Create new Select query
       SelectQuery query = null;
       try {
       	    query = database.createQuery(HistoryChange.class);
       } catch(RemoteException e) {
       	    System.err.println("RemoteException- searchInsertInfo(), createQuery");       	  
       }
       if (occurrenceId > 0 ) {
           //query.addRestriction(PlantloreConstants.RESTR_EQ, HistoryChange.OCCURRENCE, null, occurrenceId, null);      
           //query.addRestriction(PlantloreConstants.RESTR_EQ, HistoryChange.OPERATION, null, this.INSERT, null);     
    	   query.addRestriction(PlantloreConstants.RESTR_LIKE, HistoryChange.OPERATION, null, this.EDIT.toString(), null);
       }
       
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
     
    public void searchEditHistory(Integer occurrenceId)
    {  
    	
        //Create new Select query
        SelectQuery query = null;
        try {
        	    query = database.createQuery(HistoryChange.class);
        } catch(RemoteException e) {
        	    System.err.println("RemoteException- searchEditHistory(), createQuery");       	  
        }
        if (occurrenceId > 0) {
            //query.addRestriction(PlantloreConstants.RESTR_LIKE, HistoryChange.OCCURRENCE, null, occurrenceId.toString(), null);      
            query.addRestriction(PlantloreConstants.RESTR_LIKE, HistoryChange.OPERATION, null, this.EDIT.toString(), null);
            //query.addOrder(PlantloreConstants.DIRECT_DESC, HistoryChange.WHEN); 
        }
    	
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
   	    	
    	//if (getResultRows() > 1) {
    		// Log an error                   
        //    logger.error("Too many results for inserting query.");  
    	//}
            	
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
            this.when = ((HistoryChange)objectHistory[1]).getWhen();
            this.nameUser = ((HistoryChange)objectHistory[0]).WHO;
           
        } catch (DBLayerException e) {
            // Log and set error in case of an exception
            logger.error("Processing search (inserting) results failed: "+e.toString());            
        } finally { 
        	logger.debug("Sets 'insert' data ends successfully. When= " + this.when + " ");        	
        }        
    }
    
    /**
     * Process results of a search query. Retrieves results using the database management object (DBLayer) and stores them in the data field of the class. 
     * @param from number of the first row to retrieve
     * @param to number of rows to retrieve 
     */
    public void processEditResult(int from, int count) {
    	
    	if (this.resultId != 0) {
            //logger.debug("Rows in the result: "+getResultRows());
            logger.debug("Max available rows: "+(from+count-1));
           
            // Find out how many rows we can retrieve - it cannot be more than number of rows in the result
            //int to = Math.min(getResultRows(), from+count-1);
            int to = 2;
            if (to == 0) {
                this.editHistoryDataList = new ArrayList();                
            } else {
                logger.debug("Retrieving query results: "+from+" - "+to);
                try {                	 
                     // Retrieve selected row interval 
                	Object[] objectHistory;
                 	try {
                 		objectHistory = database.more(resultId, from, to);  
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
                    	editHistoryDataList.add((HistoryChange)objectHistory[i]);
                    }                    
                } catch (DBLayerException e) {
                    // Log an error in case of an exception
                    logger.error("Processing search results failed: "+e.toString());            
                } finally { 
                	logger.debug("Sets 'edut' data ends successfully");
                	//Update current first displayed row (only if data retrieval was successful)
                    setCurrentFirstRow(from);                    
                }               
            }
        }         
    }
    
    /**
     *      
     * @return Object[][] with data values for displaying in the table
     */
    public Object[][] getData() {
    	
    	//int count = editHistoryDataList.size();
    	int count = 2;
        editHistoryData = new Object[count][6];
    	for (int i=0; i < count; i++) {
    		editHistoryData[i][0] = new Boolean(false);
    	    editHistoryData[i][1] = ((HistoryChange)editHistoryDataList.get(i)).getWhen();
    	    editHistoryData[i][2] = ((HistoryChange)editHistoryDataList.get(i)).getWho().getFirstName();
    	    //editHistoryData[i][3] = ((HistoryChange)editHistoryDataList.get(i)).getHistoryColumn().getColumnName();
    	    //editHistoryData[i][4] = ((HistoryChange)editHistoryDataList.get(i)).getOldValue();
    	    //editHistoryData[i][5] = ((HistoryChange)editHistoryDataList.get(i)).getNewValue();
    	    editHistoryData[i][3] = "xx";
    	    editHistoryData[i][4] = "xx";
    	    editHistoryData[i][5] = "xx";
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
    public void updateOlderChanges(int idResult)
    {
    	
    }
    
    /**
     * Tato funkce bude overovat prava, zda dany uzivatel ma
     * pravo provest UNDO - spravne by se uzivateli nevela
     * historie zobrazit pokud nema prava, takze overeni prav
     * musi byt volano jiz v konstruktoru nebo na miste, kde 
     * se vola undo pro dany zaznam, coz by bylo asi nejlogictejsi
     * !!! OVERIT TUTO MYSLENKU 
     * pokud to tak bude, tak po zavolani historie vime, ze je autor nebo ma prava
     * jeste je tu otazka, zda editace od admina zakaze zruseni teto editace neadminem 
     */
    public void checkRight()
    {
    	
    }

          

    //****************************//
    //****Get and set metods*****//
    //**************************//
    
    
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
