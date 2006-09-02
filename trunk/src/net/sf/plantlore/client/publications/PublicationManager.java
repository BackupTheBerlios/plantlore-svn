
package net.sf.plantlore.client.publications;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Observable;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.common.SwingWorker;
import org.apache.log4j.Logger;

/**
 * Publication manager model. Contains bussines logic and data fields of the PublicationManager.
 * Implements operations including add publication, edit publication, delete publication, search
 * publications.
 * 
 * @author Tomas Kovarik
 * @version 1.0
 */
public class PublicationManager extends Observable {
    /* VARIOUS VARIABLES */
    /** Instance of a logger */
    private Logger logger;
    /** Instance of a database management object */
    private DBLayer database;
    /** Flag telling whether a long running operation has already finished */
    private boolean done;
    /** Result of the search query */
    private int resultId = 0;
    /** Actual number of rows to display */
    private int displayRows = DEFAULT_DISPLAY_ROWS;
    /** Data (results of a search query) displayed in the table */
    private ArrayList<Publication> data;
    /** Index of the first record shown in the table */
    private int currentFirstRow;
    /** Index of currently selected publication in the table */
    private int publicationIndex;
    /** Field to be used for sorting search query results */
    private int sortField = SORT_COLLECTION_NAME;
    /** Direction of sorting. 0 = ASC, 1 = DESC. Default is ASC */
    private int sortDirection = 0;
    /** Publication we want to edit */
    private Publication editPublication;
    /** Enum used for notifying AppCore to reload cached publications */
    private PlantloreConstants.Table[] editTypeArray = new PlantloreConstants.Table[]{PlantloreConstants.Table.PUBLICATION};
    /** Instance of currently active SelectQuery. We have to keep it so that we can close it when neccessary */
    private SelectQuery activeSelectQuery = null;
    
    /* PUBLICATION PROPERTIES */
    /** Name of the collection */
    private String collectionName;
    /** Year of publication */
    private Integer publicationYear;
    /** Name of the journal */
    private String journalName;
    /** Name of the author of the journal */
    private String journalAuthor;
    /** Reference detail */
    private String referenceDetail;
    /** URL of the author */
    private String url;
    /** Note of the author */
    private String note;
    
    /* CRITERIA FOR PUBLICATION SEARCH */
    /** Collection name field used for searching */
    private String searchCollectionName;
    /** Journal name field used for searching */
    private String searchJournalName;
    /** Reference citation field used for searching */
    private String searchReferenceCitation;
    /** Reference detail field used for searching */
    private String searchReferenceDetail;
    
    /* CONSTANTS */
    /** Constant with default number of rows to display */
    private static final int DEFAULT_DISPLAY_ROWS = 10;
    /** Constants used for identification of fields for sorting */
    public static final int SORT_COLLECTION_NAME = 1;
    public static final int SORT_PUBLICATION_YEAR = 2;
    public static final int SORT_JOURNAL_NAME = 3;
    public static final int SORT_JOURNAL_AUTHOR = 4;
    public static final int SORT_REFERENCE_CITATION = 5;
    public static final int SORT_REFERENCE_DETAIL = 6;
    /** Constants for operations */
    public static final int ADD = 1;
    public static final int EDIT = 2;
    public static final int DELETE = 3;
    
    /**
     * Creates a new instance of PublicationManager.
     *
     * @param database Instance of a database management object
     */
    public PublicationManager(DBLayer database) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        this.database = database;
    }
    
    /**
     *  Save new publication to the database. Information about the publication are stored in the
     *  data fields of this class. Operation is executed in a separate thread using Task class.
     *
     *  @return instance of the Task with the long running operation (saving data)
     */
    public Task savePublication() {
        // Create Task
        final Task task = new Task() {
            public Object task() throws Exception {
                boolean first = true;
                setStatusMessage(L10n.getString("Publications.ProgressBar.Save"));
                // Construct Reference citation
                StringBuffer refCitation = new StringBuffer();
                if ((journalAuthor != null) && (!journalAuthor.equals(""))) {
                    first = false;
                    refCitation.append(journalAuthor);
                }
                if (publicationYear != null) {
                    if (first == false) {
                        refCitation.append(", ");
                    }
                    refCitation.append(publicationYear);
                }
                if ((journalName != null) && (!journalName.equals(""))) {
                    if (first == false) {
                        refCitation.append(", ");
                    }
                    refCitation.append(journalName);
                }
                if ((collectionName != null) && (!collectionName.equals(""))) {
                    if (first == false) {
                        refCitation.append(", ");
                    }
                    refCitation.append(collectionName);
                }
                // Create Publication object for publication we want to add
                Publication publication = new Publication();
                publication.setCollectionName(collectionName);
                publication.setCollectionYearPublication(publicationYear);
                publication.setJournalName(journalName);
                publication.setJournalAuthorName(journalAuthor);
                publication.setReferenceCitation(refCitation.toString());
                publication.setReferenceDetail(referenceDetail);
                publication.setUrl(url);
                publication.setNote(note);
                int rowId = -1;
                // Clear variables with publication properties
                clearDataHolders();
                // Execute query
                rowId = database.executeInsert(publication);
                logger.info("Publication "+collectionName+" saved successfuly.");
                // Stop the Task
                fireStopped(null);               
                return rowId;
            }
        };        
        return task;
    }
    
    /**
     *  Delete a publication from the database. To-be-deleted publication is identified by the ID 
     *  and is retrieved based on the value of <code>publicationIndex</code> field. The operation
     *  is executed in a separate thread using the Task class.
     *
     *  In fact we are not deleting the publication, we just set the delete flag and update the record.
     *
     *  @return instance of the Task with the long running operation (deleting data)
     */
    public Task deletePublication() {
        // Create new Task
        final Task task = new Task() {
            public Object task() throws Exception {
                setStatusMessage(L10n.getString("Publications.ProgressBar.Delete"));                
                Publication pub = (Publication)data.get(getPublicationIndex());
                // Set deleted flag of the publication
                pub.setDeleted(1);
                // Execute DB query                
                database.executeUpdate(pub);
                logger.info("Publication "+collectionName+" deleted succesfully");
                // Stop the Task
                fireStopped(null);
                return true;
            }
        };
        return task;
    }
    
    /**
     *  Update publication in the database. To-be-updated publication is stored in 
     *  <code>editPublication</code> field. Operation is executed in a separate thread using 
     *  the Task class.
     *
     *  @return instance of the Task with the long running operation (updating data)
     */
    public Task editPublication() {
        // Create the Task
        final Task task = new Task() {
            public Object task() throws Exception {
                boolean first = true;
                setStatusMessage(L10n.getString("Publications.ProgressBar.Save"));
                // Construct Refernce citation
                StringBuffer refCitation = new StringBuffer();
                if ((journalAuthor != null) && (!journalAuthor.equals(""))) {
                    first = false;
                    refCitation.append(journalAuthor);
                }
                if (publicationYear != null) {
                    if (first == false) {
                        refCitation.append(", ");
                    }
                    refCitation.append(publicationYear);
                }
                if ((journalName != null) && (!journalName.equals(""))) {
                    if (first == false) {
                        refCitation.append(", ");
                    }
                    refCitation.append(journalName);
                }
                if ((collectionName != null) && (!collectionName.equals(""))) {
                    if (first == false) {
                        refCitation.append(", ");
                    }
                    refCitation.append(collectionName);
                }
                // Update to-be-updated publication based on user input
                Publication publication = getEditPublication();
                publication.setCollectionName(collectionName);
                publication.setCollectionYearPublication(publicationYear);
                publication.setJournalName(journalName);
                publication.setJournalAuthorName(journalAuthor);
                publication.setReferenceCitation(refCitation.toString());
                publication.setReferenceDetail(referenceDetail);
                publication.setUrl(url);
                publication.setNote(note);
                // Clear variables with publication properties
                clearDataHolders();
                // Execute query
                database.executeUpdate(publication);
                logger.info("Publication "+collectionName+" updated successfuly.");
                // Stop the Task
                fireStopped(null);
                return true;
            }
        };
        return task;
    }
    
    /**
     *  Search for publications in the database. Criteria for search are stored in data fields of 
     *  this class. Operation might be executed in a separate thread using the Task class (depends
     *  on the input parameters). the reason for this is that we are sometimes executing search from
     *  another long running operation, teherefore we do not need a new thread.
     *
     *  @param createTask tells whether to execute search in a separate thread
     *  @return instance of the Task with the long running operation (searching data)
     *  @see #search()
     */
    public Task searchPublication(boolean createTask) {
        // Use the Task class to execute the search in a new thread
        if (createTask) {
            final Task task = new Task() {
                public Object task() throws Exception {
                    setStatusMessage(L10n.getString("Publications.ProgressBar.Search"));                    
                    // Search the data
                    int resultId = search();
                    setResult(resultId);
                    logger.info("Publications successfuly retrieved from the database");
                    // Stop the Task
                    fireStopped(null);
                    return resultId;
                }
            };
            return task;
        } else {
            // Do not use Task. Catch exceptions but do not display error.
            try {
                int resultId = search();
                setResult(resultId);
            } catch (DBLayerException ex1) {
                logger.error("DBLayerException caught while searching the database. Details: "+ex1.getMessage());
                ex1.printStackTrace();
                return null;
            } catch (RemoteException ex2) {
                logger.error("RemoteException caught while searching the database. Details: "+ex2.getMessage());
                ex2.printStackTrace();
                return null;
            }
            logger.info("Publications successfuly retrieved from the database");
            return null;
        }
    }
    
    /**
     *  Method to construct and execute the search query. This method is called from searchPublication method.
     *
     *  @return id identifying the search result
     *  @throws DBLayerException in case search failed
     *  @throws RemoteException in case network communication failed
     *  @see #searchPublication(boolean)
     */
    private Integer search() throws DBLayerException, RemoteException {
        SelectQuery query;
        // Create new Select query
        query = database.createQuery(Publication.class);
        // Add given restrictions (WHERE clause)
        query.addRestriction(PlantloreConstants.RESTR_EQ, Publication.DELETED, null, 0, null);
        if ((searchCollectionName != null) && (searchCollectionName != ""))
            query.addRestriction(PlantloreConstants.RESTR_ILIKE, Publication.COLLECTIONNAME, null, "%" + searchCollectionName + "%", null);
        if ((searchJournalName != null) && (searchJournalName != ""))
            query.addRestriction(PlantloreConstants.RESTR_ILIKE, Publication.JOURNALNAME, null, "%" + searchJournalName + "%", null);
        if ((searchReferenceCitation != null) && (searchReferenceCitation != ""))
            query.addRestriction(PlantloreConstants.RESTR_ILIKE, Publication.REFERENCECITATION, null, "%" + searchReferenceCitation + "%", null);
        if ((searchReferenceDetail != null) && (searchReferenceDetail != null))
            query.addRestriction(PlantloreConstants.RESTR_ILIKE, Publication.REFERENCEDETAIL, null, "%" + searchReferenceDetail + "%", null);
        String field;
        // Add ORDER BY clause
        switch (sortField) {
            case 1: field = Publication.COLLECTIONNAME;
            break;
            case 2: field = Publication.COLLECTIONYEARPUBLICATION;
            break;
            case 3: field = Publication.JOURNALNAME;
            break;
            case 4: field = Publication.JOURNALAUTHORNAME;
            break;
            case 5: field = Publication.REFERENCECITATION;
            break;
            case 6: field = Publication.REFERENCEDETAIL;
            break;
            default:field = Publication.COLLECTIONNAME;
        }
        // Add the direction of searching
        if (sortDirection == 0) {
            query.addOrder(PlantloreConstants.DIRECT_ASC, field);
        } else {
            query.addOrder(PlantloreConstants.DIRECT_DESC, field);
        }
        int resultId = 0;
        // Execute query
        resultId = database.executeQuery(query);
        // If there is a previous active query, close it first
        if (this.activeSelectQuery != null) {
            database.closeQuery(this.activeSelectQuery);
        }
        // Save the query as the active search query        
        this.activeSelectQuery = query;
        return resultId;
    }
      
    /**
     *  Notify observers  about the change in the list of publications (used to reload cached publications)
     */
    public void reloadCache() {
        // Notify observers about the change in the list of publications. Used to reload cached publications
        setChanged();
        notifyObservers(editTypeArray);        
    }
    
    /**
     *  Check whether the user has appropriate rights for the given operation.
     *
     *  @param  operation operation that is executed (for operation codes see constants)
     *  @return true if the user has the right to execute the operation, false otherwise
     *  @throws RemoteException in case we could not get user's access privileges
     */
    public boolean hasRights(int operation) throws RemoteException {
        String[] group;
        SelectQuery sq;
        int result;
        Object[] resData;
        ArrayList groupList;
        User groupUser;
        // Administrator can add, edit and delete any record
        if (database.getUserRights().getAdministrator() == 1) {
            return true;
        }
        if (operation == ADD) {
            if (database.getUserRights().getAdd() == 1) {
                return true;
            } else {
                return false;
            }
        } else { // EDIT AND DELETE - the same rights apply
            // Check whether the user can edit all the records
            if (database.getUserRights().getEditAll() == 1) {
                return true;
            }
            // Check whether the user can edit the record through some other user
            group = database.getUserRights().getEditGroup().split(",");
            // We will need Publication that will be edited
            Publication selectedPubl = (Publication)data.get(this.getPublicationIndex());
            // Check whether someone in the group is an owner of the publication
            for (int i=0;i<group.length;i++) {
                if (selectedPubl.getCreatedWho().getId().toString().equals(group[i])) {
                    return true;
                }
            }
            // No rights to edit the record
            return false;
        }
    }
    
    /**
     *  Clear the variables with publication properties
     */
    private void clearDataHolders() {
        this.collectionName = null;
        this.journalAuthor = null;
        this.journalName = null;
        this.note = null;
        this.publicationYear = null;
        this.referenceDetail = null;
        this.url = null;
    }
    
    /**
     *  Method used to check whether it is ok to delete the publication. If there is an occurrence
     *  linked to this publication, publication cannot be deleted.
     *
     *  @param index index of the selected publication in the list of publications
     *  @return true if publication can be deleted, false otherwise
     *  @throws RemoteException in case we were not able to get to-be-deleted publication because of network error
     *  @throws DBLayerException in case we were not able to get to-be-deleted publication because database query failed 
     */
    public boolean checkDelete(int index) throws RemoteException, DBLayerException {
        SelectQuery sq;
        int resId;
        
        // Get the selected publication
        Publication publ = (Publication)data.get(index);
        // Find out whether we can delete this publication
        sq = database.createQuery(Occurrence.class);
        sq.addRestriction(PlantloreConstants.RESTR_EQ, Occurrence.PUBLICATION, null, publ, null);
        sq.addRestriction(PlantloreConstants.RESTR_EQ, Occurrence.DELETED, null, 0, null);
        resId = database.executeQuery(sq);
        if (database.getNumRows(resId)>0) {
            database.closeQuery(sq);
            return false;
        }
        database.closeQuery(sq);
        return true;
    }
    
    /**
     * Process results of a search query. Retrieves results using the database
     * management object (DBLayer) and stores them in the data field of the
     * class. Notifies observers about the changes. Sets an error in case of an
     * exception.
     *
     * @param from  index of the first row to retrieve.
     * @param count number of rows to retrieve
     * @throws RemoteException in case of a network error
     * @throws DBLayerException in case of a database error 
     */
    public void processResults(int from, int count) throws RemoteException, DBLayerException {
        if (this.resultId != 0) {
            // Find out how many rows we can retrieve - it cannot be more than number of rows in the result
            int to = Math.min(getResultRows(), from+count-1);
            if (to == 0) {
                this.data = new ArrayList<Publication>();
            } else {
                // Retrieve selected row interval
                Object[] objArray;
                // FIXME: Should change all the usages of processResults to use 0 as the index of the forst row
                // from-1 and to-1 just temporary
                objArray = database.more(resultId, from-1, to-1);
                logger.debug("Results retrieved. Count: "+objArray.length);
                // Create storage for the results
                this.data = new ArrayList<Publication>(objArray.length);
                // Cast the results to the AuthorRecord objects
                for (int i=0;i<objArray.length;i++) {
                    Object[] objAuth = (Object[])objArray[i];
                    this.data.add((Publication)objAuth[0]);
                }
                logger.info("Results successfuly retrieved");
                // Update current first displayed row
                setCurrentFirstRow(from);
            }
            // Tell observers to update
            setChanged();
            notifyObservers();
        }
    }
    
    /**
     *  Load fields with information about selected publication (specified by the value of 
     *  <code>publicationIndex</code> field). Notify observers about this change. This is used 
     *  to load a form when editing publications.
     */
    public void loadPublication() {
        Publication selectedPubl = (Publication)data.get(this.getPublicationIndex());
        this.setCollectionName(selectedPubl.getCollectionName());
        this.setPublicationYear(selectedPubl.getCollectionYearPublication());
        this.setJournalName(selectedPubl.getJournalName());
        this.setJournalAuthor(selectedPubl.getJournalAuthorName());
        this.setReferenceDetail(selectedPubl.getReferenceDetail());
        this.setUrl(selectedPubl.getUrl());
        this.setNote(selectedPubl.getNote());
        setChanged();
        notifyObservers();
    }
    
    /**
     *  Return currently displayed and selected publication (at the selected index in the data field)
     *
     *  @param  index   index of the publication in the data field
     *  @return         Publication at the given index
     */
    public Publication getSelectedPublication(int index) {
        return (Publication)data.get(index);
    }
    
    /**
     *  Set result of a database operation. This is used only for search operations.
     *  @param resultId id of the SelectQuery result
     */
    public void setResult(int resultId) {
        this.resultId = resultId;
    }
    
    /**
     *  Get results of last database operation. This is used only for search operations.
     *  @return resultId identifying the SelectQuery result
     */
    public int getResult() {
        return this.resultId;
    }
    
    /**
     *  Get the number of results for the current SelectQuery
     *
     *  @return number of results for the current SelectQuery
     */
    public int getResultRows(){
        int result = 0;
        if (resultId != 0) {
            try {
                result = database.getNumRows(resultId);
            } catch (RemoteException ex) {
                logger.error("RemoteException caught while retrieving number of rows in the result. Details: "+ex.getMessage());
                ex.printStackTrace();
                return 0;
            }
        }
        return result;
    }
    
    /**
     *  Close active SelectQuery (if there is one)
     *
     *  @throws DBLayerException    In case database operation failed
     *  @throws RemoteException     In case network operation failed
     */
    public void closeActiveQuery() throws DBLayerException, RemoteException {
        if (this.activeSelectQuery != null) {
            database.closeQuery(this.activeSelectQuery);
            this.activeSelectQuery = null;
        }
    }
    /**
     *  Get data returned by the last search query. Returns only currently displayed data.
     *  @return data returned by the last search query
     */
    public ArrayList getData() {
        return this.data;
    }
    
    /**
     *  Get index of currently selected publication. The index is used to locate publication
     *  record in the data field.
     *
     *  @return index of currently selected publication
     */
    protected int getPublicationIndex() {
        return this.publicationIndex;
    }
    
    /**
     *  Set index of currently selected publication. The index is used to locate publication
     *  record in the data field.
     *
     *  @param index index of currently selected publication
     */
    protected void setPublicationIndex(int index) {
        this.publicationIndex = index;
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
     *  Get index of the first row currently displayed in the list of publications. This is an index
     *  in the results returned by a search query.
     *
     *  @return index of the first row currently displayed in the list of publications
     */
    public int getCurrentFirstRow() {
        return this.currentFirstRow;
    }
    
    /**
     *  Set index of the forst row currently displayed in the list of publications. This is an index
     *  in the results returned by a search query.
     *
     *  @param row index of the first row currently displayed in the list of publications
     */
    public void setCurrentFirstRow(int row) {
        this.currentFirstRow = row;
    }
    
    /**
     *  Indicates whether a long running operation executed in a separate thread has already finished.
     *  @return true if the operation is finished (no operation running), false otherwise
     */
    public boolean isOperationDone() {
        return this.done;
    }
    
    /**
     *  Indicate whether result of a search query is available at the momoent
     *  @return true if search query result is available
     */
    public boolean isResultAvailable() {
        if (this.resultId!= 0) {
            return true;
        }
        return false;
    }
    
    /**
     *  Set publication we are going to edit in the add/edit publication dialog
     *  @param editPublication   Publication we are going to edit
     */
    public void setEditPublication(Publication editPublication) {
        this.editPublication = editPublication;
    }
    
    /**
     *  Get publication we are editing in the add/edit publication dialog
     *  @return Publication we are editing
     */
    public Publication getEditPublication() {
        return this.editPublication;
    }
    
    /**
     *  Set field used for sorting the results of the search query.
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
     *  Set collection name search field.
     *  @param collectionName name of the collection to search for
     */
    public void setSearchCollectionName(String collectionName) {
        this.searchCollectionName = collectionName;
    }
    
    /**
     *  Set journal name search field.
     *  @param journalName name of the journal used for searching
     */
    public void setSearchJournalName(String journalName) {
        this.searchJournalName = journalName;
    }
    
    /**
     *  Set reference citation search field.
     *  @param referenceCitation reference citation used for searching
     */
    public void setSearchReferenceCitation(String referenceCitation) {
        this.searchReferenceCitation = referenceCitation;
    }
    
    /**
     *  Set reference detail search field.
     *  @param referenceDetail reference detail used for searching
     */
    public void setSearchReferenceDetail(String referenceDetail) {
        this.searchReferenceDetail = referenceDetail;
    }
    
    /**
     *  Get collection name.
     *  @return collection name for the publication
     */
    public String getCollectionName() {
        return collectionName;
    }
    
    /**
     *  Set collection name.
     *  @param collectionName collection name for the publication
     */
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
    
    /**
     *  Get year of collection publication.
     *  @return year of collection publication
     */
    public Integer getPublicationYear() {
        return publicationYear;
    }
    
    /**
     *  Set year of collection publication.
     *  @param publicationYear year of collection publication
     */
    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }
    
    /**
     *  Get the name of the journal where published.
     *  @return name of the journal where published
     */
    public String getJournalName() {
        return journalName;
    }
    
    /**
     *  Set the name of the journal where published.
     *  @param journalName name of the journal where published
     */
    public void setJournalName(String journalName) {
        this.journalName = journalName;
    }
    
    /**
     *  Get the name of the author of the journal where published.
     *  @return name of the author of the journal where published
     */
    public String getJournalAuthor() {
        return journalAuthor;
    }
    
    /**
     *  Set the name of the author of the journal where published.
     *  @param journalAuthor name of the author of the journal where published
     */
    public void setJournalAuthor(String journalAuthor) {
        this.journalAuthor = journalAuthor;
    }
    
    /**
     *  Get the reference detail.
     *  @return reference detail
     */
    public String getReferenceDetail() {
        return referenceDetail;
    }
    
    /**
     *  Set the reference detail.
     *  @param referenceDetail reference detail
     */
    public void setReferenceDetail(String referenceDetail) {
        this.referenceDetail = referenceDetail;
    }
    
    /**
     *  Get URL of the author.
     *  @return string with the URL of the author
     */
    public String getUrl() {
        return url;
    }
    
    /**
     *  Set URL of the author.
     *  @param url URL of the author
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    /**
     *  Get note of the author.
     *  @return string with the note of the author
     */
    public String getNote() {
        return note;
    }
    
    /**
     *  Set note of the author.
     *  @param note note of the author
     */
    public void setNote(String note) {
        this.note = note;
    }
}