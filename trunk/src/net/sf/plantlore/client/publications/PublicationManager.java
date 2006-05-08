/*
 * PublicationManager.java
 *
 * Created on 15. leden 2006, 2:04
 *
 */

package net.sf.plantlore.client.publications;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Observable;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Publication;
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
 * @version 1.0 BETA, May 1, 2006
 * 
 * TODO:    Proper exception handling
 *          Clean API (get rid of unused or unnecessary methods)
 *          Improve thread management
 */
public class PublicationManager extends Observable {
    /** Instance of a logger */
    private Logger logger;
    /** Exception with details about an error */
    private String error = null;
    /** Instance of a database management object */
    private DBLayer database;
    /** Name of the collection */
    private String collectionName;
    /** Year of publication */
    private int publicationYear;
    /** Name of the journal */
    private String journalName;
    /** Name of the author of the journal */
    private String journalAuthor;
    /** Reference citation */
    private String referenceCitation;
    /** Reference detail */
    private String referenceDetail;
    /** URL of the author */
    private String url;
    /** Note of the author */
    private String note;
    /** Collection name field used for searching */
    private String searchCollectionName;
    /** Journal name field used for searching */
    private String searchJournalName;
    /** Reference citation field used for searching */
    private String searchReferenceCitation;
    /** Reference detail field used for searching */
    private String searchReferenceDetail;
    /** Flag telling whether a long running operation has already finished */
    private boolean done;
    /** Result of the search query */
    private int resultId = 0;
    /** Constant with default number of rows to display */
    private static final int DEFAULT_DISPLAY_ROWS = 10;
    /** Actual number of rows to display */
    private int displayRows = DEFAULT_DISPLAY_ROWS;
    /** Data (results of a search query) displayed in the table */
    private ArrayList data;
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
    /** Constants used for identification of fields for sorting */
    public static final int SORT_COLLECTION_NAME = 1;
    public static final int SORT_PUBLICATION_YEAR = 2;
    public static final int SORT_JOURNAL_NAME = 3;
    public static final int SORT_JOURNAL_AUTHOR = 4;
    public static final int SORT_REFERENCE_CITATION = 5;
    public static final int SORT_REFERENCE_DETAIL = 6;
    
    public static final int FIELD_COLLECTION_NAME = 1;
    public static final int FIELD_COLLECTION_YEAR = 2;
    public static final int FIELD_JOURNAL_NAME = 3;
    public static final int FIELD_JOURNAL_AUTHOR = 4;
    public static final int FIELD_REFERENCE_CITATION = 5;
    public static final int FIELD_REFERENCE_DETAIL = 6;    
    public static final int FIELD_URL = 7;    
    public static final int FIELD_NOTE = 8;    
    
    public static final String ERROR_SEARCH = L10n.getString("publicationSearchFailed");
    public static final String ERROR_SAVE = L10n.getString("publicationSaveFailed");
    public static final String ERROR_UPDATE = L10n.getString("publicationUpdateFailed");    
    public static final String ERROR_DELETE = L10n.getString("publicationDeleteFailed");
    public static final String ERROR_PROCESS = L10n.getString("publicationProcessResultsFailed");
    
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
     *  Save new publication to the database. Information about the publication are stored in data fields of this class.
     *  Operation is executed in a separate thread using <code>SwingWorker</code>. Error is set in case of an exception.
     */
    public void savePublication() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                // The operation is not finished yet
                done = false;
                // Create Publication object for publication we want to add
                Publication publication = new Publication();
                publication.setCollectionName(collectionName);
                publication.setCollectionYearPublication(publicationYear);
                publication.setJournalName(journalName);
                publication.setJournalAuthorName(journalAuthor);
                publication.setReferenceCitation(referenceCitation);
                publication.setReferenceDetail(referenceDetail);
                publication.setUrl(url);
                publication.setNote(note);
                int rowId = -1;
                try {
                    // Execute query
                    rowId = database.executeInsert(publication);
                } catch (DBLayerException e) {
                    // Log and set an error
                    logger.error("Saving publication failed. Unable to execute insert query");
                    setError(ERROR_SAVE);
                    // Set operation state to finished
                    done = true;
                    return null;
                } catch(RemoteException e) {
                    System.err.println("Kdykoliv se pracuje s DBLayer nebo SelectQuery, musite hendlovat RemoteException");
                }
                logger.info("Publication "+collectionName+" saved successfuly.");
                if (isResultAvailable()) {
                    searchPublication();
                }
                done = true;
                return rowId;
            }
        };
        worker.start();
    }
    
    /**
     *  Delete a publication from the database. To-be-deleted publication is identified by the ID and is
     *  retrieved based on the value of <code>publicationIndex</code> field. Error is set in case of an exception.
     */
    public void deletePublication() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                // Operation not finished yet
                done = false;
                try {
                    // Execute query
                    database.executeDelete((Publication)data.get(getPublicationIndex()));
                } catch (DBLayerException e) {
                    // Log and set an error
                    logger.error("Deleting publication failed. Unable to execute delete query.");
                    setError(ERROR_DELETE);
                    // Set operation state to finished
                    done = true;
                    return false;
                } catch(RemoteException e) {
                    System.err.println("Kdykoliv se pracuje s DBLayer nebo SelectQuery, musite hendlovat RemoteException");
                }
                logger.info("Publication "+collectionName+" deleted succesfully");
                // Execute publication search - required in order to display up-to-date data in the table of publications
                searchPublication();
                // Set operation state to finished
                done = true;
                return true;
            }
        };
        worker.start();
    }

    /**
     *  Update publication in the database. To-be-updated publication is stored in <code>editPublication</code> field. Operation 
     *  is executed in a separate thread using <code>SwingWorker</code>. Error is set in case of an exception.
     */    
    public void editPublication() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                // The operation is not finished yet
                done = false;
                // Update to-be-updated publication based on user input
                Publication publication = getEditPublication();
                publication.setCollectionName(collectionName);
                publication.setCollectionYearPublication(publicationYear);
                publication.setJournalName(journalName);
                publication.setJournalAuthorName(journalAuthor);
                publication.setReferenceCitation(referenceCitation);
                publication.setReferenceDetail(referenceDetail);
                publication.setUrl(url);
                publication.setNote(note);
                try {
                    // Execute query
                    database.executeUpdate(publication);
                } catch (DBLayerException e) {
                    // Log and set an error
                    logger.error("Update publication failed. Unable to execute update query");
                    setError(ERROR_UPDATE);
                    // Set operation state to finished
                    done = true;
                    return false;
                } catch(RemoteException e) {
                    System.err.println("Kdykoliv se pracuje s DBLayer nebo SelectQuery, musite hendlovat RemoteException");
                }
                logger.info("Publication "+collectionName+" updated successfuly.");
                if (isResultAvailable()) {
                    searchPublication();
                }
                done = true;
                return true;
            }
        };
        worker.start();        
    }
    
    /**
     *  Search for publications in the database. Criteria for search are stored in data fields of this class.
     *  Operation is executed in a separate thread using <code>SwingWorker</code>. Error is set in case of an exception
     */
    public void searchPublication() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                // Operation not finished yet
                done = false;
                SelectQuery query;
                try {
                    // Create new Select query                    
                    query = database.createQuery(Publication.class);                    
                    // Add given restrictions (WHERE clause)
                    if ((searchCollectionName != null) && (searchCollectionName != ""))
                        query.addRestriction(PlantloreConstants.RESTR_LIKE, Publication.COLLECTIONNAME, null, "%" + searchCollectionName + "%", null);
                    if ((searchJournalName != null) && (searchJournalName != ""))
                        query.addRestriction(PlantloreConstants.RESTR_LIKE, Publication.JOURNALNAME, null, "%" + searchJournalName + "%", null);
                    if ((searchReferenceCitation != null) && (searchReferenceCitation != ""))
                        query.addRestriction(PlantloreConstants.RESTR_LIKE, Publication.REFERENCECITATION, null, "%" + searchReferenceCitation + "%", null);
                    if ((searchReferenceDetail != null) && (searchReferenceDetail != null))
                        query.addRestriction(PlantloreConstants.RESTR_LIKE, Publication.REFERENCEDETAIL, null, "%" + searchReferenceDetail + "%", null);
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
                    
                    if (sortDirection == 0) {
                        query.addOrder(PlantloreConstants.DIRECT_ASC, field);
                    } else {
                        query.addOrder(PlantloreConstants.DIRECT_DESC, field);
                    }
                    int resultId = 0;
                    try {
                        // Execute query
                        resultId = database.executeQuery(query);
                    } catch (DBLayerException e) {
                        // Log and set an error
                        logger.error("Searching publications failed. Unable to execute search query.");
                        setError(ERROR_SEARCH);
                    } finally {
                        // Set operation state to finished
                        done = true;
                        logger.info("Publications successfuly retrieved from the database");
                        // Save the results
                        setResult(resultId);
                    }
                    return resultId;
                } catch (RemoteException e) {
                    System.err.println("Kdykoliv se pracuje s DBLayer nebo SelectQuery, musite hendlovat RemoteException");
                    return null;
                } catch (DBLayerException e) {
                    System.err.println("Kdykoliv se pracuje s DBLayer nebo SelectQuery, musite hendlovat RemoteException");
                    return null;
                }
            }
        };
        worker.start();
    }
    
    /**
     * Checks whether an error is set. If yes, notifies observers to display it.
     * Finally unsets the error flag.
     *
     * @return <code>true</code> if an error was set (and observers were notified), <code>false</code> otherwise
     */
    public boolean processErrors() {
        if (this.error != null) {
            setChanged();
            notifyObservers();
            this.error = null;
            return true;
        }
        return false;
    }
    
    /**
     * Process results of a search query. Retrieves results using the database
     * management object (DBLayer) and stores them in the data field of the
     * class. Notifies observers about the changes. Sets an error in case of an
     * exception.
     *
     * @param from  index of the first row to retrieve.
     * @param count number of rows to retrieve
     */
    public void processResults(int from, int count) {
        if (this.resultId != 0) {
            logger.info("Processing "+count+" results from "+from);
            logger.debug("Rows in the result: "+getResultRows());
            // Find out how many rows we can retrieve - it cannot be more than number of rows in the result
            int to = Math.min(getResultRows(), from+count-1);
            if (to == 0) {
                this.data = new ArrayList();
            } else {
                try {
                    // Retrieve selected row interval
                    Object[] objArray;
                    try {
                        // FIXME: Should change all the usages of processResults to use 0 as the index of the forst row
                        // from-1 and to-1 just temporary
                        objArray = database.more(resultId, from-1, to-1);
                    } catch(RemoteException e) {
                        System.err.println("Kdykoliv se pracuje s DBLayer nebo SelectQuery, musite hendlovat RemoteException");
                        return;
                    }
                    logger.debug("Results retrieved. Count: "+objArray.length);
                    // Create storage for the results
                    this.data = new ArrayList(objArray.length);
                    // Cast the results to the AuthorRecord objects
                    for (int i=0;i<objArray.length;i++) {
                        Object[] objAuth = (Object[])objArray[i];
                        this.data.add((Publication)objAuth[0]);
                    }
                } catch (DBLayerException e) {
                    // Log and set error in case of an exception
                    logger.error("Processing search results failed: "+e.toString());
                    setError(this.ERROR_PROCESS);
                }
                // Update current first displayed row (only if data retrieval was successful).
                if (!this.isError()) {
                    logger.info("Results successfuly retrieved");
                    // Update current first displayed row
                    setCurrentFirstRow(from);
                }
            }
            // Tell observers to update
            setChanged();
            notifyObservers();
            // Clean error flag (if it was set)
            this.error = null;
        }
    }
    
    /**
     *  Load fields with information about selected publication (specified by the value of <code>publicationIndex</code> field).
     *  Notify observers about this change. This is used to load a form when editing publications.
     */
    public void loadPublication() {
        Publication selectedPubl = (Publication)data.get(this.getPublicationIndex());
        this.setCollectionName(selectedPubl.getCollectionName());
        this.setPublicationYear(selectedPubl.getCollectionYearPublication());
        this.setJournalName(selectedPubl.getJournalName());
        this.setJournalAuthor(selectedPubl.getJournalAuthorName());
        this.setReferenceCitation(selectedPubl.getReferenceCitation());
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
    public int getResultRows() {
        int result = 0;
        if (resultId != 0) try {
            result = database.getNumRows(resultId);
        } catch(RemoteException e) {
            System.err.println("Kdykoliv se pracuje s DBLayer nebo SelectQuery, musite hendlovat RemoteException");
        }
        return result;
    }
    
    /**
     *  Set an error flag.
     *  @param msg  message explaining the error which occured
     */
    public void setError(String msg) {
        this.error = msg;
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
     *  Get error message for the error that occured
     *  @return message explaining the error which occured
     */
    public String getError() {
        return this.error;
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
     *  @param editPublication   Publication we are editing
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
    public int getPublicationYear() {
        return publicationYear;
    }
    
    /**
     *  Set year of collection publication.
     *  @param publicationYear year of collection publication
     */
    public void setPublicationYear(int publicationYear) {
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
     *  Get the reference citation.
     *  @return reference citation
     */
    public String getReferenceCitation() {
        return referenceCitation;
    }
    
    /**
     *  Set the reference citation.
     *  @param referenceCitation reference citation
     */
    public void setReferenceCitation(String referenceCitation) {
        this.referenceCitation = referenceCitation;
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