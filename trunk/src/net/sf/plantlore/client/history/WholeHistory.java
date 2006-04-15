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
import java.util.ArrayList;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.HistoryChange;
import net.sf.plantlore.common.record.HistoryRecord;
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
    // informace pro uzivatele o undo
    private String messageUndo;
    
    
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
        } catch (RemoteException e) {
                System.err.println("RemoteException- searchWholeHistoryData(), createQuery");
        }
        // Create aliases for table tHistoryChange.      
        query.createAlias("historyChange", "hc");
       //sort by date/time 	
        query.addOrder(PlantloreConstants.DIRECT_DESC, "hc.when");        
    	
        int resultId = 0;
        try {
            // Execute query                    
            resultId = database.executeQuery(query);        
        } catch (DBLayerException e) {                            
            logger.error("Searching whole history data failed. Unable to execute search query.");           
        } catch (RemoteException e) { 		   
     	   System.err.println("RemoteException- searchWholeHistoryData(), executeQuery");
        } finally {
                logger.debug("Searching whole history data ends successfully");
                // Save "edit" history data
                setResultId(resultId);                  
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
                    logger.error("Processing search results failed: "+e.toString());            
                } finally { 
                    logger.debug("Sets 'edit' data ends successfully");
                    //Update current first displayed row (only if data retrieval was successful)
                    setCurrentFirstRow(fromTable);                    
                }               
            }
        }         
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
