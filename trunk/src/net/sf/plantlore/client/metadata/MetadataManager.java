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
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.exception.DBLayerException;
import org.apache.log4j.Logger;

/**
 * MetadataManager model. Contains bussines logic and data fields of the MetadataManager. Implements
 * operations including add metadata, edit metadata, delete metadata and search metadata. 
 * 
 * @author Lada Oberreiterova
 * @version 1.0
 */
public class MetadataManager  extends Observable {
    
    /** Instance of a logger */
    private Logger logger;      
    /** Instance of a database management object */
    private DBLayer database;   
    /** Exception with details about an error */
    private String error = null;
    /** Remote exception (network communication failed)*/
    private RemoteException remoteEx;
    /** True if MyTask finished successful*/
    private boolean  finishedTask = false;
    /** Constant with default number of rows to display */
    public static final int DEFAULT_DISPLAY_ROWS = 14;    
    /** Actual number of rows to display */
    private int displayRows = DEFAULT_DISPLAY_ROWS;   
    /** Index of the first record shown in the table */
    private int currentFirstRow;
    /** Information about current display rows*/
    private String displayRow;           
    /** Result of the search query */
    private int resultId = 0;
    /** List of data (results of a search query) displayed in the table */
    private ArrayList<Metadata> metadataList = new ArrayList<Metadata>();         
    /** Message containing information about changes in metadata*/
    private String messageMetadata;    
    /** Type of operation - ADD, EDIT, DELETE, DETAIL*/
    private String operation = "";
    /** Containing information about closing the addEdit dialog*/
    private boolean usedClose = true;
    /** Record for add, update or delete*/
    private Metadata metadataRecord;
    /** Identifier of record for add, update or delete*/
    private int idRecord;
    /** Array of type object */
    private Enum[] editTypeArray = new PlantloreConstants.Table[]{PlantloreConstants.Table.METADATA};        
    /** Field to be used for sorting search query results */
    private int sortField = 0;
    /** Direction of sorting. 0 = ASC, 1 = DESC. Default is ASC */
    private int sortDirection = 0;    
    /**Metadata - project title*/
    private String dataSetTitle;
    /**Metadata - istitution*/
    private String sourceInstitutionId;
    /**Metadata - data source*/
    private String sourceId;    
    
    /** Constants with error descriptions */
    public static final String ERROR_SEARCH = L10n.getString("Error.MetadataSearchFailed");        
    public static final String ERROR_PROCESS = L10n.getString("Error.MetadataProcessResultsFailed");    
    public static final String ERROR_NUMBER_ROWS = L10n.getString("Error.GetNumberRows");
    public static final String ERROR_ADD = L10n.getString("Error.MetadataAddFailed");
    public static final String ERROR_EDIT = L10n.getString("Error.MetadataEditFailed");
    public static final String ERROR_DELETE = L10n.getString("Error.MetadataDeleteFailed");    
    public static final String ERROR_DBLAYER_TITLE = L10n.getString("Error.DBLayerExceptionTitle");
    public static final String ERROR_DBLAYER = L10n.getString("Error.DBLayerException");
    public static final String ERROR_REMOTE_TITLE = L10n.getString("Error.RemoteExceptionTitle");
    public static final String ERROR_REMOTE = L10n.getString("Error.RemoteException");
    public static final String ERROR_UNKNOWEN_TITLE = L10n.getString("Error.UnknownExceptionTitle");
    public static final String ERROR_UNKNOWEN = L10n.getString("Error.UnknownException");
    public static final String ERROR_TITLE = L10n.getString("Error.MetadataMessageTitle");
    public static final String ERROR_CHECK_DELETE = L10n.getString("Error.MetadataCheckDelete");
    public static final String ERROR_DATASETTITLE = L10n.getString("Error.MetadataUniqueDatasetTitle"); 
    public static final String ERROR_REMOTE_EXCEPTION = "REMOTE_EXCEPTION";
    
    public static final String QUESTION_DELETE_TITLE = L10n.getString("Question.DeleteMetadataTitle");
    public static final String QUESTION_DELETE = L10n.getString("Question.DeleteMetadata");
    
    public static final String PROGRESS_SEARCH = L10n.getString("Metadata.Search.ProgressTitle");
    public static final String PROGRESS_ADD = L10n.getString("Metadata.Add.ProgressTitle");
    public static final String PROGRESS_EDIT = L10n.getString("Metadata.Edit.ProgressTitle");
    public static final String PROGRESS_DELETE = L10n.getString("Metadata.Delete.ProgressTitle");
    
    public static final String WARNING_SELECTION_TITLE = L10n.getString("Warning.EmptySelectionTitle");
    public static final String WARNING_SELECTION = L10n.getString("Warning.EmptySelection");
    
    public static final String INFORMATION_RESULT_TITLE = L10n.getString("Information.NoMetadataInResultTitle");
    public static final String INFORMATION_RESULT = L10n.getString("Information.NoMetadataInResult");
    public static final String INFORMATION_SEARCH_TITLE = L10n.getString("Information.SearchMetadataTitle");
    public static final String INFORMATION_SEARCH = L10n.getString("Information.SearchMetadata");
    /**
     * Creates a new instance of MetadataManager
     * @param database Instance of a database management object
     */
    public MetadataManager(DBLayer database) {
        
       logger = Logger.getLogger(this.getClass().getPackage().getName());	 
       this.database = database;             
    }        
    
     /**
     *  Search for metadata in the database. Operation might be executed in a separate thread using the Task class (depends
     *  on the input parameters). The reason for this is that we are sometimes executing search from
     *  another long running operation, teherefore we do not need a new thread.
     *
     *  @param createTask tells whether to execute search in a separate thread
     *  @return instance of the Task with the long running operation (searching data)
     */    
    public Task searchMetadata(boolean createTask) {
       
    	if (createTask) {
	    	final Task task = new Task() {    		    		
	    		public Object task() throws DBLayerException, RemoteException {
	  		       int resultIdent;
	    			try {
	  		    	    resultIdent = search();
	  		    	    setResultId(resultIdent);
		    	   }catch (RemoteException e) {	            
			            logger.error("Searching metada failed. Remote exception caught in Metadata. Details: "+e.getMessage());			        	
		                RemoteException remex = new RemoteException(ERROR_SEARCH + e.getMessage());
		                remex.setStackTrace(e.getStackTrace());
		                throw remex; 		           
			        } catch (DBLayerException e) {
			        	logger.error("Searching metada failed. DBLayer exception caught in Metadata. Details: "+e.getMessage());       	                                                   			        
		                DBLayerException dbex = new DBLayerException(ERROR_SEARCH + e.getMessage());
		                dbex.setStackTrace(e.getStackTrace());
		                throw dbex; 		           
			        }			
			        setInfoFinishedTask(true);
                    return null;			        
				}
		    };
		    return task;
    	} else {
    		try {
    			int resultIdent = search();
    			setResultId(resultIdent);
			} catch (DBLayerException e) {
				logger.error("Searching metada failed. Remote exception caught in Metadata. Details: "+e.getMessage());
				setError(ERROR_SEARCH);				               
			} catch (RemoteException e) {
				logger.error("Searching metada failed. DBLayer exception caught in Metadata. Details: "+e.getMessage());				
				 setError(ERROR_REMOTE_EXCEPTION);	
				 setRemoteEx(e);
			}
			return null;
    	}
  }
    
    /**
     *  Method to construct and execute the search query.
     *  Save identifier of the search result     
     *  @return id identifying the search result
     *  @throws DBLayerException in case search failed
     *  @throws RemoteException in case network communication failed
     */
    private int search() throws DBLayerException, RemoteException {
    	// Create new Select query
        SelectQuery query = null;
        
        query = database.createQuery(Metadata.class);    
        //Add restrictions
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
       
        // Add orderBy clause
        String field;                
        switch (sortField) {
        case 0:
                field = Metadata.SOURCEINSTITUTIONID;
                logger.debug("0");
                break;
        case 1:
                field = Metadata.SOURCEID;
                logger.debug("1");
                break;
        case 2:
                field = Metadata.DATASETTITLE;
                logger.debug("2");
                break;
        case 3:
                field = Metadata.TECHNICALCONTACTNAME;
                logger.debug("3");
                break;                
        case 4:
                field = Metadata.CONTENTCONTACTNAME;
                logger.debug("4");
                break;
        case 5:
                field = Metadata.DATECREATE;
                logger.debug("5");
                break;
        case 6:
                field = Metadata.DATEMODIFIED;
                logger.debug("6");
                break;
        default:
                field = Metadata.SOURCEINSTITUTIONID;
                logger.debug("Default");
        }

        if (sortDirection == 0) {
                query.addOrder(PlantloreConstants.DIRECT_ASC, field);
        } else {
                query.addOrder(PlantloreConstants.DIRECT_DESC, field);
        }
        //Execute query                    
        resultId = database.executeQuery(query);                    
        return resultId;
    }  
    
   /**
     * Process results of a search query. Retrieves results using the database management object (DBLayer) and stores them in the data field of the class. 
     * @param from number of the first row to show in table. 
     * @param count number of rows to retrieve 
     */
    public void processResult(int from, int count) throws RemoteException, DBLayerException{
        
    	if (this.resultId != 0) {    		
            int currentRow = getResultRows();
            logger.debug("Rows in the result: "+currentRow);
            logger.debug("Max available rows: "+(from+count-1));            
           
            // Find out how many rows we can retrieve - it cannot be more than number of rows in the result
            int to = Math.min(currentRow, from+count-1);             
            if (to <= 0) {
            	metadataList = new ArrayList<Metadata>(); 
            	setDisplayRows(0);
            	setCurrentDisplayRows("0-0");       
            } else {
                logger.debug("Retrieving query results: 1 -" + to);
                setCurrentDisplayRows(from+ "-" + to);                              	 
                // Retrieve selected row interval 
                Object[] objectMetadata;
             	try {
             		objectMetadata = database.more(this.resultId, 0, to-1);  
             	} catch(RemoteException e) {
             		logger.error("Remote exception caught in MetadataManager (processResult). Details: "+e.getMessage());        			
        			RemoteException remex = new RemoteException(e.getMessage());
                    remex.setStackTrace(e.getStackTrace());
                    throw remex; 		                                    	                                                                                      
                } catch (DBLayerException e) {                  
                    logger.error("Processing search results failed: " + e.getMessage());   
                    DBLayerException dbex = new DBLayerException(ERROR_PROCESS + e.getMessage());
                    dbex.setStackTrace(e.getStackTrace());
                    throw dbex; 
                }  
                if (objectMetadata == null) {
                	logger.error("tMetadata doesn`t contain required data");
                	DBLayerException dbex = new DBLayerException(ERROR_PROCESS + "tMetadata doesn`t contain required data");                   
                    throw dbex; 
                }
                int countResult = objectMetadata.length;  
                logger.debug("Results retrieved. Count: "+ countResult);                
                this.metadataList = new ArrayList<Metadata>();
                // Cast the results to the Metadata objects
                for (int i=0; i<countResult; i++ ) {                    							
                	Object[] objHis = (Object[])objectMetadata[i];
                    this.metadataList.add((Metadata)objHis[0]);
                    logger.debug("Proccess: "+ i + ": " + (Metadata)objHis[0]);
                }               
                // Update current first displayed row                
                logger.info("Results successfuly retrieved");                   
                setCurrentFirstRow(from);
            }                        
         }         
    }
    
    /**
     *  Method used to check whether it is ok to delete the metadat. If there is an occurrence
     *  linked to this metadata, metadata cannot be deleted.
     *
     *  @param index index of the selected metadata in the list of metadata
     *  @return true if metadata can be deleted, false otherwise
     */
    public boolean checkDelete(int index) {
        SelectQuery sq;
        int resId;
        
        // Get the selected metadata
        Metadata metadata = (Metadata)metadataList.get(index);        
        try {
            sq = database.createQuery(Occurrence.class);
            sq.addRestriction(PlantloreConstants.RESTR_EQ, Occurrence.METADATA, null, metadata, null);
            sq.addRestriction(PlantloreConstants.RESTR_EQ, Occurrence.DELETED, null, 0, null);
            resId = database.executeQuery(sq);
            if (database.getNumRows(resId)>0) {
                database.closeQuery(sq);
                return false;
            }
            database.closeQuery(sq);
        } catch (RemoteException e) {
            logger.error("Remote exception caught in Metadata. Details: "+e.getMessage());
            setError(ERROR_REMOTE_EXCEPTION);  
            setRemoteEx(e);
            return false;
        } catch (DBLayerException e) {
            logger.error("Loading metadata failed. Cannot determine whether metadata can be deleted");
            setError(ERROR_DELETE);
            return false;
        }        
        return true;
    }
    
    /**
     *  Method used to check whether it is already exiting some metadata record with same datasetTitle.
     *  If there is metadata record with the same datasetTitle, the new datasetTitle must be changed.
     *
     *  @param datasetTitle new name of project fill in add form
     *  @return true if datasetTile already exist, false otherwise 
     */
    public boolean uniqueDatasetTitle(String datasetTitle) {
    	SelectQuery sq;
        int resId;
        
    	try {
    		sq = database.createQuery(Metadata.class);
    		sq.addProjection(PlantloreConstants.PROJ_PROPERTY, Metadata.DATASETTITLE);
    		sq.addRestriction(PlantloreConstants.RESTR_EQ, Metadata.DATASETDETAILS, null, datasetTitle, null);
    		resId = database.executeQuery(sq);
    		if (database.getNumRows(resId)>0) {
                database.closeQuery(sq);
                return false;
            }
            database.closeQuery(sq);
    	} catch (RemoteException e) {
            logger.error("Remote exception caught in Metadata. Details: "+e.getMessage());
            setError(ERROR_REMOTE_EXCEPTION);
            setRemoteEx(e);
            return false;
        } catch (DBLayerException e) {
            logger.error("Check unique datasetTitle failed. Cannot determine whether some name of project equals new datasetTitle.");
            setError(ERROR_DATASETTITLE);
            return false;
        }        
    	return true;
    }
    
    /**
     * Save new metadata record into the database. 
     * Operation is executed in a separate thread using Task class.
     *      
     * @return instance of the Task with the long running operation (executeInsert)   
     */
    public Task addMetedataRecord () {
    	    	
    	final Task task = new Task() {    		    		
    		public Object task() throws DBLayerException, RemoteException {
    			try {
		            database.executeInsert(metadataRecord);		            		            
		        }catch (RemoteException e) {
		        	logger.error("Process add metadata failed. Remote exception caught in MetadataManager. Details: "+e.getMessage());
		        	database.rollbackTransaction();
                    RemoteException remex = new RemoteException(ERROR_ADD + e.getMessage());
                    remex.setStackTrace(e.getStackTrace());
                    throw remex; 		           		       	    
		        } catch (DBLayerException e) {
		        	logger.error("Process add metadata failed. DBLayer exception caught in MetadataManager. Details: "+e.getMessage());       	                                                   
		        	database.rollbackTransaction();
                    DBLayerException dbex = new DBLayerException(ERROR_ADD + e.getMessage());
                    dbex.setStackTrace(e.getStackTrace());
                    throw dbex; 		            
		        } 		       
		        //Tell AppCore observerber
		        setChanged(); 
		        notifyObservers(editTypeArray);	
		        setInfoFinishedTask(true);
		        return null;
    		}
	    };
	    return task;
    } 		  
    
    /**
     *  Update metadata in the database. To-be-updated metadata is stored in <code>metadataRecord</code>. 
     *  Operation is executed in a separate thread using Task class.
     *  
     *  @return instance of the Task with the long running operation (executeUpdate)
     */
    public Task editMetadataRecord() {       
        
    	final Task task = new Task() {    		    		
    		public Object task() throws DBLayerException, RemoteException {
    			try {
		        	database.executeUpdate(metadataRecord);
		        	metadataList.set(idRecord, metadataRecord);
		        }catch (RemoteException e) {
		        	logger.error("Process update metadata failed. Remote exception caught in MetadataManager. Details: "+e.getMessage());
		        	database.rollbackTransaction();
                    RemoteException remex = new RemoteException(ERROR_EDIT + e.getMessage());
                    remex.setStackTrace(e.getStackTrace());
                    throw remex; 		           		       	    
		        } catch (DBLayerException e) {
		        	logger.error("Process update metadata failed. DBLayer exception caught in MetadataManager. Details: "+e.getMessage());       	                                                   
		        	database.rollbackTransaction();
                    DBLayerException dbex = new DBLayerException(ERROR_EDIT + e.getMessage());
                    dbex.setStackTrace(e.getStackTrace());
                    throw dbex; 		            
		        } 		        
		        //Tell AppCore observerber
		        setChanged(); 
		        notifyObservers(editTypeArray);	
		        setInfoFinishedTask(true);
		        return null;
    		}
	    };
	    return task;
    } 
    	    	    	
    /**     
     *  Update metadata in the database. To-be-updated metadata is stored in <code>metadataRecord</code> field.
     *  Operation is executed in a separate thread using Task class.
     *  
     *  @return instance of the Task with the long running operation (executeDelete) 
     */
    public Task deleteMetadataRecord() {
    	
    	final Task task = new Task() {    		    		
    		public Object task() throws DBLayerException, RemoteException {
    			try {
		        	metadataRecord.setDeleted(1);
		            database.executeUpdate(metadataRecord);		            
		        }catch (RemoteException e) {
		        	logger.error("Process delete metadata failed. Remote exception caught in MetadataManager. Details: "+e.getMessage());
		        	database.rollbackTransaction();
                    RemoteException remex = new RemoteException(ERROR_DELETE + e.getMessage());
                    remex.setStackTrace(e.getStackTrace());
                    throw remex; 		           		       	    
		        } catch (DBLayerException e) {
		        	logger.error("Process delete metadata failed. DBLayer exception caught in MetadataManager. Details: "+e.getMessage());       	                                                   
		        	database.rollbackTransaction();
                    DBLayerException dbex = new DBLayerException(ERROR_DELETE + e.getMessage());
                    dbex.setStackTrace(e.getStackTrace());
                    throw dbex; 		            
		        } 			        
		        //Tell AppCore observerber
		        setChanged(); 
		        notifyObservers(editTypeArray);		
		        setInfoFinishedTask(true);
		        return null;
    		}
	    };
	    return task;
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
     *  @return true if an error occured and error message is available, false otherwise
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
    
    /**
     *  Set an remote exception (network communication failed)
     *  @param ex  RemoteException (network communication failed)
     */
    public void setRemoteEx(RemoteException ex) {
        this.remoteEx = ex;
    }
    
    /**
     *  Get remote exception (network communication failed)
     *  @return remote exception (network communication failed)
     */
    public RemoteException getRemoteEx() {
        return this.remoteEx;
    }  
    
    /** 
     *  Set true if operation in separate thread using the Task class was successful
     *  @param true if operation in separate thread using the Task class was successful, false in other ways
     */ 
    public void setInfoFinishedTask(boolean finishedTask) {
    	this.finishedTask = finishedTask;
    }
    
    /**
     * Get true if operation in separate thread using the Task class was successful, false in other ways
     * @return true if operation in separate thread using the Task class was successful, false in other ways
     */
    public boolean isFinishedTask() {
    	return this.finishedTask;
    }
    
    /**
     * Set true if addEdit dialog was closed by CLOSE button
     * @param usedClose containing information about closing the addEdit dialog
     */
    public void setUsedClose(boolean usedClose) {
    	this.usedClose = usedClose;
    }
    
    /**
     * Get information about closing the addEdit dialog
     * @return true if addEdit dialog was closed by CLOSE button
     */
    public boolean usedClose() {
    	return this.usedClose;
    }
    
    
    /**
	  * Set result of a database operation. This is used only for search operations.
     * @param resultId id of the SelectQuery result	  
	  */
    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
    
    /**
     *  Get results of last database operation. This is used only for search operations.
     *  @return resultId identifying the SelectQuery result
     */
    public int getResultId() {
        return this.resultId;
    }
    
    /**
     * Get the number of results for the current SelectQuery
     * @return number of results for the current SelectQuery
     */
    public int getResultRows() {
        int resultCount = 0;
        if (resultId != 0) try {
                resultCount = database.getNumRows(resultId);        	
        } catch(RemoteException e) {
        	logger.error("Get number of results failed.Remote exception caught in MetadataManager. Details: "+e.getMessage());  
        	setError(ERROR_NUMBER_ROWS);
        }
        return resultCount;
    }

    /**
	  * Get results of a search query for dislpaying in metadata dialog
	  * @return results of a search query for dislpaying in metadata dialog
	  */
    public ArrayList<Metadata> getMetadataList() {
              return this.metadataList;		  
       }

    /**
	  * Set results of a search query for dislpaying in metadata dialog
	  * @param metadataList results of a search query for dislpaying in metadata dialog
	  */
     public void setMetadataList(ArrayList<Metadata> metadataList) {
              this.metadataList = metadataList;		  
     } 
    
 	 /**
 	 * Get information about current display rows (from - to)
 	 * @return information about current display rows (from - to)
 	 */
     public String getCurrentDisplayRows() {
		  return this.displayRow;		  
	   }

     /**
 	 * Set information about current display rows (from - to)
 	 * @param displayRow information about current display rows (from - to)
 	 */
     public void setCurrentDisplayRows(String displayRow) {
              this.displayRow = displayRow;		  
     } 
     
     /**
 	 * Get message with information for user
 	 * @return message with information for user
 	 */
     public String getMessageMetadata() {
		  return this.messageMetadata;		  
	   }

     /**
 	 * Set message with information for user
 	 * @param messageMetadata message with information for user
 	 */
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
       
    /**
     * Get information about selected operation (ADD, EDIT, DELETE, DETAIL)
     * @return information about selected operation (ADD, EDIT, DELETE, DETAIL)
     */
     public String getOperation() {                  
		  return this.operation;		  
	   }

     /**
      * Set information about selected operation (ADD, EDIT, DELETE, DETAIL)
      * @param operation information about selected operation (ADD, EDIT, DELETE, DETAIL)
      */
     public void setOperation(String operation) {
              this.operation = operation;		  
     } 
       
     /**
      *  Set selected Metadata 
      *  @param metadataRecordId index of the selected metadata in the list of metadata
      */
    public void setMetadataRecord(int metadataRecordId) {
        this.metadataRecord = (Metadata)(metadataList.get(metadataRecordId));
        this.idRecord = metadataRecordId;
    }
    
    /**
     *  Set new Metadata 
     *  @param metadataRecord new instantce of Metadata
     */
   public void setNewMetadataRecord(Metadata metadataRecord) {
       this.metadataRecord = metadataRecord;
   }
    
    /**
     * Get selected Metadata
     * @return selected Metadata
     */
    public Metadata getMetadataRecord() {
        return this.metadataRecord;
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

