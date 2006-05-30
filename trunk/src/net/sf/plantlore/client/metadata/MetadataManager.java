/*
 * MetadataManager.java
 *
 * Created on 22. duben 2006, 14:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.metadata;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Observable;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.exception.DBLayerException;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class MetadataManager  extends Observable {
    
    /** Instance of a logger */
    private Logger logger;      
    /** Instance of a database management object */
    private DBLayer database;   
    /** Constant with default number of rows to display */
    public static final int DEFAULT_DISPLAY_ROWS = 6;    
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
    private ArrayList<Metadata> metadataList = new ArrayList();         
    // informace pro uzivatele o zmenach v tabulce metadata
    private String messageMetadata;
    
    //Informace o operaci, ktera se bude provadet - ADD, EDIT, DELETE, DETAIL
    private String operation = "";
    //Vyvrany zaznam v tabulce s metadaty
    private Metadata selectedRecord;
    
    private Enum[] editTypeArray = new Enum[]{PlantloreConstants.Table.METADATA};
    
    //*********************Search************//
    /** Field to be used for sorting search query results */
    private int sortField = 0;
    /** Direction of sorting. 0 = ASC, 1 = DESC. Default is ASC */
    private int sortDirection = 0;
    
    //***********Metadata*******************//
    private String dataSetTitle;    
    private String sourceInstitutionId;
    private String sourceId;        
    
    /**
     * Creates a new instance of MetadataManager
     */
    public MetadataManager(DBLayer database) {
        
       logger = Logger.getLogger(this.getClass().getPackage().getName());	 
       this.database = database;
       
       //nacteni metadat
       searchMetadata();
       //opet funkci pro vyzadani si dat postupne
       processResult(1, displayRows);
    }
    
     /**
     *
     *
     */
    public void searchMetadata() {
        
        //Create new Select query
        SelectQuery query = null;       

    	//  Select data from tMetadata table
        try {
                query = database.createQuery(Metadata.class);     
                query.addRestriction(PlantloreConstants.RESTR_EQ, Metadata.DELETED, null, 0, null);
                
                if (sourceInstitutionId != null && !sourceInstitutionId.equals("")) {
                    query.addRestriction(PlantloreConstants.RESTR_LIKE, Metadata.SOURCEINSTITUTIONID, null, "%" + sourceInstitutionId + "%", null);
                }
                if (sourceId != null && !sourceId.equals("")) {
                    query.addRestriction(PlantloreConstants.RESTR_LIKE, Metadata.SOURCEID, null, "%" + sourceId + "%", null);
                }
                if (dataSetTitle != null && !dataSetTitle.equals("")) {
                    query.addRestriction(PlantloreConstants.RESTR_LIKE, Metadata.DATASETTITLE, null, "%" + dataSetTitle + "%", null);
                }
               
                
                String field;                
                switch (sortField) {
                case 0:
                        field = Metadata.SOURCEINSTITUTIONID;
                        break;
                case 1:
                        field = Metadata.SOURCEID;
                        break;
                case 2:
                        field = Metadata.DATASETTITLE;
                        break;
                case 3:
                        field = Metadata.TECHNICALCONTACTNAME;
                        break;                
                case 4:
                        field = Metadata.CONTENTCONTACTNAME;
                        break;
                case 5:
                        field = Metadata.DATECREATE;
                        break;
                case 6:
                        field = Metadata.DATEMODIFIED;
                        break;
                default:
                        field = Metadata.SOURCEINSTITUTIONID;
                }

                if (sortDirection == 0) {
                        query.addOrder(PlantloreConstants.DIRECT_ASC, field);
                } else {
                        query.addOrder(PlantloreConstants.DIRECT_DESC, field);
                }
                
                
        } catch (RemoteException e) {
            System.err.println("RemoteException - searchMetadataData(), createQuery");
        } catch (DBLayerException e) {
            System.err.println("DBLayerException - searchMetadataData(), createQuery");
        }
                
        int resultId = 0;
        try {
            // Execute query                    
            resultId = database.executeQuery(query);
            // Save "edit" metadata data
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
            	metadataList = new ArrayList<Metadata>(); 
            	setDisplayRows(0);
            	setCurrentDisplayRows("0-0");
            } else {
                logger.debug("Retrieving query results: 1 - "+to);
                setCurrentDisplayRows(fromTable+ "-" + to);
                try {                	 
                     // Retrieve selected row interval 
                	Object[] objectMetadata;
                 	try {
                 		objectMetadata = database.more(this.resultId, 0, to-1);  
                 	} catch(RemoteException e) {
                     	System.err.println("RemoteException- processEditResult, more");
                     	logger.debug("RemoteException- processEditResult, more");
                     	return;
                     }                   
                    int countResult = objectMetadata.length;  
                    logger.debug("Results retrieved. Count: "+ countResult);
                    // Create storage for the results
                    this.metadataList = new ArrayList<Metadata>();
                    // Cast the results to the Metadata objects
                    for (int i=0; i<countResult; i++ ) {                    							
						Object[] objHis = (Object[])objectMetadata[i];
                        this.metadataList.add((Metadata)objHis[0]);
                    }           
                    //Update current first displayed row (only if data retrieval was successful)
                    setCurrentFirstRow(fromTable); 
                } catch (DBLayerException e) {                  
                    logger.error("Processing search results failed: "+e.toString());            
                }             
            }
        }         
    }
    
    public void addMetedataRecord (Metadata metadata) {
        try {
            database.executeInsert(metadata);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
        setChanged(); 
        notifyObservers(editTypeArray);
    }
    
    public void editMetadataRecord() {       
        try {
            database.executeUpdate(selectedRecord);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
        setChanged(); 
        notifyObservers(editTypeArray);
    }
    
    public void deleteMetadataRecord() {
        try {
            selectedRecord.setDeleted(1);
            database.executeUpdate(selectedRecord);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
        setChanged(); 
        notifyObservers(editTypeArray);
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

    public ArrayList<Metadata> getMetadataList() {
              return this.metadataList;		  
       }

     public void setMetadataList(ArrayList<Metadata> metadataList) {
              this.metadataList = metadataList;		  
     } 
    
     public String getCurrentDisplayRows() {
		  return this.displayRow;		  
	   }

     public void setCurrentDisplayRows(String displayRow) {
              this.displayRow = displayRow;		  
     } 
     
     public String getMessageMetadata() {
		  return this.messageMetadata;		  
	   }

     public void setMessageMetadata(String messageMetadata) {
              this.messageMetadata= messageMetadata;		  
     } 
     
         /**
     *  Get index of the first row currently displayed in the list of record changes. This is an index in the results returned by a search query.
     *  @return index of the first row currently displayed in the list of metadata
     */
    public int getCurrentFirstRow() {
        return this.currentFirstRow;
    }
    
    /**
     *  Set index of the forst row currently displayed in the list of record changes. This is an index in the results returned by a search query.
     *  @param row index of the first row currently displayed in the list of metadata
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
    
    //Vraci metadata objekt vybraneho zaznam pro nasledny EDIT, DELETE ci zobrazeni DETAILU 
    public void setSelectedRecord(int selectedRecordId) {
        this.selectedRecord = (Metadata)(metadataList.get(selectedRecordId));
    }
    
    public Metadata getSelectedRecord() {
        return this.selectedRecord;
    }
    
        /**
     *   Get concise title of the project
     *   @return concise title of the project
     *   @see setDataSetTitle
     */
    public String getDataSetTitle() {
        return this.dataSetTitle;
    }
    
    /**
     *   Set concise title of the project
     *   @param dataSetTitle string containing concise title of the project
     *   @see getDataSetTitle
     */
    public void setDataSetTitle(String dataSetTitle) {
        this.dataSetTitle = dataSetTitle;
    }
    
    /**
     *   Get unique identifier (code or name) of the institution holding the original data source
     *   @return unique identifier of the institution holding the original data source.
     *   @see setSourceInstitutionId
     */
    public String getSourceInstitutionId() {
        return this.sourceInstitutionId;
    }
    
    /**
     *   Set unique identifier (code or name) of the institution holding the original data source
     *   @param sourceInstitutionId string containing unique identifier of the institution holding the original data source
     *   @see getSourceInstitutionId
     */
    public void setSourceInstitutionId(String sourceInstitutionId) {
        this.sourceInstitutionId = sourceInstitutionId;
    }    
    
    /**
     *   Get name or code of the data source
     *   @return name or code of the data source
     *   @see setSourceId
     */
    public String getSourceId() {
        return this.sourceId;
    }
    
    /**
     *   Set name or code of the data source
     *   @param sourceId string containing name or code of the data source
     *   @see getTechnicalContactName
     */
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
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
    
}

