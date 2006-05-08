/*
 * PublicationManager.java
 *
 * Created on 22. duben 2006, 14:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.publication;

import java.rmi.RemoteException;
import java.util.ArrayList;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.exception.DBLayerException;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class PublicationManager {
    
    /** Instance of a logger */
    private Logger logger;      
    /** Instance of a database management object */
    private DBLayer database;   
    /** Constant with default number of rows to display */
    protected static final int DEFAULT_DISPLAY_ROWS = 6;    
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
    private ArrayList<Publication> publicationList = new ArrayList();         
    // informace pro uzivatele o zmenach v tabulce publication
    private String messagePublication;
    
    //Informace o operaci, ktera se bude provadet - ADD, EDIT, DELETE, DETAIL
    private String operation = "";
    //Vyvrany zaznam v tabulce s Petadaty
    private Publication selectedRecord;
    
    //*********************Search - promenne podle,kterych se vyhledava************//
    /** Field to be used for sorting search query results */
    private int sortField = SORT_COLLECTIO_NNAME;
    /** Direction of sorting. 0 = ASC, 1 = DESC. Default is ASC */
    private int sortDirection = 0;
    private String collectionName;
    private Integer collectionYearPublication;
    private String journalName;
    private String journalAuthorName;
    
    /** Constants used for identification of fields for sorting */
    public static final int SORT_COLLECTIO_NNAME = 1;
    public static final int SORT_COLLECTION_YEAR_PUBLICATION = 2;
    public static final int SORT_JOURNAL_NAME = 3;
    public static final int SORT_JOURNAL_AUTHOR_NAME = 4;
  
    /**
     * Creates a new instance of PublicationManager
     */
    public PublicationManager(DBLayer database) {
        
       logger = Logger.getLogger(this.getClass().getPackage().getName());	 
       this.database = database;
       
       //nacteni metadat
       searchPublication();
       //opet funkci pro vyzadani si dat postupne
       processResult(1, displayRows);
    }
    
     /**
     *
     */
    public void searchPublication() {
        
        //Create new Select query
        SelectQuery query = null;       

    	//  Select data from tPublication table
        try {
                query = database.createQuery(Publication.class);         
                
                if (collectionName != null && !collectionName.equals("")) {
                    query.addRestriction(PlantloreConstants.RESTR_LIKE, Publication.COLLECTIONNAME, null, "%" + collectionName + "%", null);
                }
                if (collectionYearPublication != null && collectionYearPublication != 0) {
                    query.addRestriction(PlantloreConstants.RESTR_LIKE, Publication.COLLECTIONYEARPUBLICATION, null, "%" + collectionYearPublication + "%", null);
                }
                if (journalName != null && !journalName.equals("")) {
                    query.addRestriction(PlantloreConstants.RESTR_LIKE, Publication.JOURNALNAME, null, "%" + journalName + "%", null);
                }
                if (journalAuthorName != null && !journalAuthorName.equals("")) {
                    query.addRestriction(PlantloreConstants.RESTR_LIKE, Publication.COLLECTIONNAME, null, "%" + journalAuthorName + "%", null);
                }
                String field;
                switch (sortField) {
                case 1:
                        field = Publication.COLLECTIONNAME;
                        break;
                case 2:
                        field = Publication.COLLECTIONYEARPUBLICATION;
                        break;
                case 3:
                        field = Publication.JOURNALNAME;
                        break;
                case 4:
                        field = Publication.JOURNALAUTHORNAME;
                        break;                
                default:
                        field = Publication.COLLECTIONNAME;
                }

                if (sortDirection == 0) {
                        query.addOrder(PlantloreConstants.DIRECT_ASC, field);
                } else {
                        query.addOrder(PlantloreConstants.DIRECT_DESC, field);
                }
        } catch (RemoteException e) {
            System.err.println("RemoteException - searchPublicationData(), createQuery");
        } catch (DBLayerException e) {
            System.err.println("DBlayerException - searchPublicationData(), createQuery");
        }
                
        int resultId = 0;
        try {
            // Execute query                    
            resultId = database.executeQuery(query);
            // Save "edit" Publication data
            setResultId(resultId);    
        } catch (DBLayerException e) {                            
            logger.error("Searching metada failed. Unable to execute search query.");           
        } catch (RemoteException e) { 		   
     	   System.err.println("RemoteException- searchMetada(), executeQuery");
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
            	publicationList = new ArrayList<Publication>();  
                setDisplayRows(0);
            	setCurrentDisplayRows("0-0");
            } else {
                logger.debug("Retrieving query results: 1 - "+to);
                setCurrentDisplayRows(fromTable+ "-" + to);                
                try {                	 
                     // Retrieve selected row interval 
                	Object[] objectPublication;
                 	try {
                 		objectPublication = database.more(this.resultId, 0, to-1);  
                 	} catch(RemoteException e) {
                     	System.err.println("RemoteException- processEditResult, more");
                     	logger.debug("RemoteException- processEditResult, more");
                     	return;
                     }                   
                    int countResult = objectPublication.length;  
                    logger.debug("Results retrieved. Count: "+ countResult);
                    // Create storage for the results
                    this.publicationList = new ArrayList<Publication>();
                    // Cast the results to the Publication objects
                    for (int i=0; i<countResult; i++ ) {                    							
						Object[] objHis = (Object[])objectPublication[i];
                        this.publicationList.add((Publication)objHis[0]);
                    }           
                    //Update current first displayed row (only if data retrieval was successful)
                    setCurrentFirstRow(fromTable); 
                } catch (DBLayerException e) {                  
                    logger.error("Processing search results failed: "+e.toString());            
                }             
            }
        }         
    }
    
    public void addPublicationRecord (Publication publication) {
        try {
            database.executeInsert(publication);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
    }
    
    public void editPublicationRecord() {       
        try {
            database.executeUpdate(selectedRecord);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
    }
    
    public void deletePublicationRecord() {
        try {
            database.executeDelete(selectedRecord);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
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

    public ArrayList<Publication> getPublicationList() {
              return this.publicationList;		  
       }

     public void setPublicationList(ArrayList<Publication> publicationList) {
              this.publicationList = publicationList;		  
     } 
    
     public String getCurrentDisplayRows() {
		  return this.displayRow;		  
	   }

     public void setCurrentDisplayRows(String displayRow) {
              this.displayRow = displayRow;		  
     } 
     
     public String getMessagePublication() {
		  return this.messagePublication;		  
	   }

     public void setMessagePublication(String messagePublication) {
              this.messagePublication= messagePublication;		  
     } 
     
         /**
     *  Get index of the first row currently displayed in the list of record changes. This is an index in the results returned by a search query.
     *  @return index of the first row currently displayed in the list of Publication
     */
    public int getCurrentFirstRow() {
        return this.currentFirstRow;
    }
    
    /**
     *  Set index of the forst row currently displayed in the list of record changes. This is an index in the results returned by a search query.
     *  @param row index of the first row currently displayed in the list of Publication
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
    
    // predani informace o operaci, ktera byla zavolana - ADD, EDIT, DELETE, DETAIL
     public String getOperation() {
                  logger.debug("Operation: "+operation);
		  return this.operation;		  
	   }

     public void setOperation(String operation) {
              this.operation = operation;		  
     } 
    
    //Vraci Publication objekt vybraneho zaznam pro nasledny EDIT, DELETE ci zobrazeni DETAILU 
    public void setSelectedRecord(int selectedRecordId) {
        this.selectedRecord = (Publication)(publicationList.get(selectedRecordId));
    }
    
    public Publication getSelectedRecord() {
        return this.selectedRecord;
    }
    
        /**
     *  Set field used for sorting results of the search query.
     *  @param field numeric identificator of the field used for sorting
     */
    public void setSortField(int field) {
        this.sortField = field;
    }

    /**
     *  Set direction of sorting.
     *  @param direction direction of sorting. 0 for ascending, 1 for descending
     */
    public void setSortDirection(int direction) {
        this.sortDirection = direction;
    }
    
        /**
     *   Get name sort field
     *   @return name sort field
     *   @see setCollectionName
     */
    public String getCollectionName() {
        return this.collectionName;
    }
    
    /**
     *   Set name sort field
     *   @param collectionName sort field
     *   @see getCollectionName
     */
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;        
    }
    
    /**
     *   Get year sort field
     *   @return year sort field
     *   @see setCollectionYearPublication
     */
    public Integer getCollectionYearPublication() {
        return this.collectionYearPublication;
    }
    
    /**
     *   Set year sort field
     *   @param collectionYearPublication sort field
     *   @see getCollectionYearPublication
     */
    public void setCollectionYearPublication(int collectionYearPublication) {
        this.collectionYearPublication = collectionYearPublication;
    }
    
    /**
     *   Get name sort field
     *   @return name sort field
     *   @see setJournalName
     */
    public String getJournalName() {
        return this.journalName;
    }
    
    /**
     *   Set name sort field
     *   @param journalName sort field
     *   @see getJournalName
     */
    public void setJournalName(String journalName) {
        this.journalName = journalName;
    }
    
    /**
     *   Get author sort field
     *   @return author sort field
     *   @see setJournalAuthorName
     */
    public String getJournalAuthorName() {
        return this.journalAuthorName;
    }
    
    /**
     *   Set author sort field
     *   @param journalAuthorName sort field
     *   @see getJournalAuthorName
     */
    public void setJournalAuthorName(String journalAuthorName) {
        this.journalAuthorName = journalAuthorName;
    }
        
}
