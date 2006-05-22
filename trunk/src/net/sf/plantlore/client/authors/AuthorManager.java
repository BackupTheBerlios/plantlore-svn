/*
 * AuthorManager.java
 *
 * Created on 15. leden 2006, 2:04
 *
 */

package net.sf.plantlore.client.authors;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Observable;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.HistoryRecord;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.common.SwingWorker;
import org.apache.log4j.Logger;

/**
 * Author manager model. Contains bussines logic and data fields of the AuthorManager. Implements
 * operations including add author, edit author, delete author, search authors.
 *
 * @author Tomas Kovarik
 * @version 1.0 BETA, May 1, 2006
 *
 * TODO:    Proper exception handling
 *          Clean API (get rid of unused or unnecessary methods)
 *          Improve thread management
 */
public class AuthorManager extends Observable {
    /** Instance of a logger */
    private Logger logger;
    /** Exception with details about an error */
    private String error = null;
    /** Instance of a database management object */
    private DBLayer database;
    /** First name of the author */
    private String name;
    /** Organization of the author */
    private String organization;
    /** Role of the author */
    private String role;
    /** Address of the author */
    private String address;
    /** Phone number of the author */
    private String phoneNumber;
    /** Email of the author */
    private String email;
    /** URL of the author */
    private String url;
    /** Note of the author */
    private String note;
    /** Name field used for searching */
    private String searchName;
    /** Organization field used for searching */
    private String searchOrganization;
    /** Role field used for searching */
    private String searchRole;
    /** Email field used for searching */
    private String searchEmail;
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
    /** Index of currently selected author in the table */
    private int authorIndex;
    /** Field to be used for sorting search query results */
    private int sortField = SORT_NAME;
    /** Direction of sorting. 0 = ASC, 1 = DESC. Default is ASC */
    private int sortDirection = 0;
    /** Author we want to edit */
    private Author editAuthor;
    /** Constants used for identification of fields for sorting */
    public static final int SORT_NAME = 1;
    public static final int SORT_ORGANIZATION = 2;
    public static final int SORT_ROLE = 3;
    public static final int SORT_EMAIL = 4;
    public static final int SORT_PHONE = 5;
    public static final int SORT_URL = 6;
    
    public static final String ERROR_SEARCH = L10n.getString("authorSearchFailed");
    public static final String ERROR_SAVE = L10n.getString("authorSaveFailed");
    public static final String ERROR_RIGHTS = "You don't have sufficient rights for this operation";
    public static final String ERROR_UPDATE = L10n.getString("authorUpdateFailed");    
    public static final String ERROR_DELETE = L10n.getString("authorDeleteFailed");
    public static final String ERROR_PROCESS = L10n.getString("authorProcessResultsFailed");
    
    /**
     *  Creates a new instance of AuthorManager.
     *  @param database Instance of a database management object
     */
    public AuthorManager(DBLayer database) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        this.database = database;
    }
    
    /**
     *  Save new author to the database. Information about the author are stored in data fields of this class.
     *  Operation is executed in a separate thread using <code>SwingWorker</code>. Error is set in case of an exception.
     */
    public void saveAuthor() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                // The operation is not finished yet
                done = false;
                // Create Author object for author we want to add
                Author author = new Author();
                author.setWholeName(name);
                author.setOrganization(organization);
                author.setRole(role);
                author.setAddress(address);
                author.setPhoneNumber(phoneNumber);
                author.setEmail(email);
                author.setUrl(url);
                author.setDeleted(0);
                author.setNote(note);
                int rowId = -1;
                try {
                    // Execute query
                    rowId = database.executeInsert(author);
                } catch (DBLayerException e) {
                    // Check the type of an exception
                    int errorCode = e.getErrorCode();
                    switch (errorCode) {
                        case DBLayerException.ERROR_RIGHTS:
                            logger.info("Insufficient rights for the operation");
                            setError(ERROR_RIGHTS);
                            break;
                        case DBLayerException.ERROR_SAVE:
                            logger.error("Saving author failed. Unable to execute insert query");
                            setError(ERROR_SAVE);                            
                            break;
                        default:
                            logger.error("Saving author failed. An error occurred");                            
                            setError(ERROR_SAVE);                            
                    }
                    // Set operation state to finished
                    done = true;
                    return null;
                } catch(RemoteException e) {
                    System.err.println("Kdykoliv se pracuje s DBLayer nebo SelectQuery, musite hendlovat RemoteException");
                }
                logger.info("Author "+name+" saved successfuly.");
                if (isResultAvailable()) {
                    searchAuthor();
                }                
                done = true;
                return rowId;
            }
        };
        worker.start();
    }
    
    /**
     *  Delete an author from the database. To-be-deleted author is identified by his ID and is
     *  retrieved based on the value of <code>authorIndex</code> field. Error is set in case of an exception.
     */
    public void deleteAuthor() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                // Operation not finished yet
                done = false;
                try {
                    // Execute query
                    Author delAuthor = (Author)data.get(getAuthorIndex());
                    delAuthor.setDeleted(1);
                    database.executeUpdate(delAuthor);
                } catch (DBLayerException e) {
                    int errorCode = e.getErrorCode();
                    switch (errorCode) {
                        case DBLayerException.ERROR_RIGHTS:
                            logger.info("Insufficient rights for the operation");
                            setError(ERROR_RIGHTS);
                            break;
                        case DBLayerException.ERROR_DELETE:
                            logger.error("Deleting author failed. Unable to execute delete query");
                            setError(ERROR_DELETE);
                            break;
                        default:
                            logger.error("Deleting author failed. An error occurred");                            
                            setError(ERROR_DELETE);                            
                    }                    
                    // Set operation state to finished
                    done = true;
                    return false;
                } catch(RemoteException e) {
                    System.err.println("Kdykoliv se pracuje s DBLayer nebo SelectQuery, musite hendlovat RemoteException");
                }
                logger.info("Author "+name+" deleted succesfully");
                // Execute author search - required in order to display up-to-date data in the table of authors
                searchAuthor();
                // Set operation state to finished
                done = true;
                return true;
            }
        };
        worker.start();
    }

    /**
     *  Update author in the database. To-be-updated author is stored in <code>editAuthor</code> field. Operation 
     *  is executed in a separate thread using <code>SwingWorker</code>. Error is set in case of an exception.
     */    
    public void editAuthor() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                // The operation is not finished yet
                done = false;
                // Update to*be-updated author based on user input
                Author author = getEditAuthor();
                author.setWholeName(name);
                author.setOrganization(organization);
                author.setRole(role);
                author.setAddress(address);
                author.setPhoneNumber(phoneNumber);
                author.setEmail(email);
                author.setUrl(url);
                author.setNote(note);
                try {
                    // Execute query
                    database.executeUpdate(author);
                } catch (DBLayerException e) {
                    int errorCode = e.getErrorCode();
                    switch (errorCode) {
                        case DBLayerException.ERROR_RIGHTS:
                            logger.info("Insufficient rights for the operation");
                            setError(ERROR_RIGHTS);
                            break;
                        case DBLayerException.ERROR_UPDATE:
                            logger.error("Updating author failed. Unable to execute update query");
                            setError(ERROR_UPDATE);                            
                            break;
                        default:
                            logger.error("Updating author failed. An error occurred");                            
                            setError(ERROR_UPDATE);                            
                    }                    
                    // Set operation state to finished
                    done = true;
                    return false;
                } catch(RemoteException e) {
                    System.err.println("Kdykoliv se pracuje s DBLayer nebo SelectQuery, musite hendlovat RemoteException");
                }
                logger.info("Author "+name+" updated successfuly.");
                if (isResultAvailable()) {
                    searchAuthor();
                }
                done = true;
                return true;
            }
        };
        worker.start();        
    }
    
    /**
     *  Search for authors in the database. Criteria for search are stored in data fields of this class.
     *  Operation is executed in a separate thread using <code>SwingWorker</code>. Error is set in case of an exception
     */
    public void searchAuthor() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                // Operation not finished yet
                done = false;
                SelectQuery query;
                try {
                    // Create new Select query                    
                    query = database.createQuery(Author.class);                    
                    // Display only authors who haven't been deleted
                    query.addRestriction(PlantloreConstants.RESTR_EQ, Author.DELETED, null, 0, null);
                    // Add given restrictions (WHERE clause)
                    if ((searchName != null) && (searchName != ""))
                        query.addRestriction(PlantloreConstants.RESTR_ILIKE, Author.WHOLENAME, null, "%" + searchName + "%", null);
                    if ((searchOrganization != null) && (searchOrganization != ""))
                        query.addRestriction(PlantloreConstants.RESTR_ILIKE, Author.ORGANIZATION, null, "%" + searchOrganization + "%", null);
                    if ((searchRole != null) && (searchRole != ""))
                        query.addRestriction(PlantloreConstants.RESTR_ILIKE, Author.ROLE, null, "%" + searchRole + "%", null);
                    if ((searchEmail != null) && (searchEmail != null))
                        query.addRestriction(PlantloreConstants.RESTR_ILIKE, Author.EMAIL, null, "%" + searchEmail + "%", null);
                    String field;
                    // Add ORDER BY clause
                    switch (sortField) {
                        case 1: field = Author.WHOLENAME;
                                break;
                        case 2: field = Author.ORGANIZATION;
                                break;
                        case 3: field = Author.ROLE;
                                break;
                        case 4: field = Author.EMAIL;
                                break;
                        case 5: field = Author.PHONENUMBER;
                                break;
                        case 6: field = Author.URL;
                                break;
                        default:field = Author.WHOLENAME;
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
                        int errorCode = e.getErrorCode();
                        switch (errorCode) {
                            case DBLayerException.ERROR_SELECT:
                                logger.error("Searching authors failed. Unable to execute search query.");
                                setError(ERROR_SEARCH);
                                break;
                            default:
                                logger.error("Saving author failed. An error occurred");                            
                                setError(ERROR_SEARCH);
                        }                        
                    } finally {
                        // Set operation state to finished
                        done = true;
                        logger.info("Authors successfuly retrieved from the database");
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
                        this.data.add((Author)objAuth[0]);
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
     *  Load fields with information about selected author (specified by the value of <code>authorIndex</code> field).
     *  Notify observers about this change. This is used to load a form when editing authors.
     */
    public void loadAuthor() {
        Author selectedAuth = (Author)data.get(this.getAuthorIndex());
        this.setName(selectedAuth.getWholeName());
        this.setOrganization(selectedAuth.getOrganization());
        this.setRole(selectedAuth.getRole());
        this.setAddress(selectedAuth.getAddress());
        this.setEmail(selectedAuth.getEmail());
        this.setPhoneNumber(selectedAuth.getPhoneNumber());
        this.setUrl(selectedAuth.getUrl());
        this.setNote(selectedAuth.getNote());
        setChanged();
        notifyObservers();
    }
    
    /**
     *  Return currently displayed and selected author (at the selected index in the data field)
     *
     *  @param  index   index of the author in the data field
     *  @return         Author at the given index
     */
    public Author getSelectedAuthor(int index) {
        return (Author)data.get(index);
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
     *  Get index of currently selected author. The index is used to locate author record in the data field.
     *  @return index of currently selected author
     */
    protected int getAuthorIndex() {
        return this.authorIndex;
    }
    
    /**
     *  Set index of currently selected author. The index is used to locate author record in the data field.
     *  @param index index of currently selected author
     */
    protected void setAuthorIndex(int index) {
        this.authorIndex = index;
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
     *  Get index of the first row currently displayed in the list of authors. This is an index 
     *  in the results returned by a search query.
     *
     *  @return index of the first row currently displayed in the list of authors
     */
    public int getCurrentFirstRow() {
        return this.currentFirstRow;
    }
    
    /**
     *  Set index of the forst row currently displayed in the list of authors. This is an index
     *  in the results returned by a search query.
     *
     *  @param row index of the first row currently displayed in the list of authors
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
     *  Set author we are going to edit in the add/edit author dialog
     *  @param editAuthor   Author we are going to edit
     */
    public void setEditAuthor(Author editAuthor) {
        this.editAuthor = editAuthor;
    }
    
    /**
     *  Get author we are editing in the add/edit author dialog
     *  @param editAuthor   Author we are editing
     */    
    public Author getEditAuthor() {
        return this.editAuthor;
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
     *  Set name search field.
     *  @param name name of author to search for
     */
    public void setSearchName(String name) {
        this.searchName = name;
    }
    
    /**
     *  Set organization search field.
     *  @param organization organization of author used for searching
     */
    public void setSearchOrganization(String organization) {
        this.searchOrganization = organization;
    }
    
    /**
     *  Set role search field.
     *  @param role role of author used for searching
     */
    public void setSearchRole(String role) {
        this.searchRole = role;
    }
    
    /**
     *  Set email search field.
     *  @param email email of author used for searching
     */
    public void setSearchEmail(String email) {
        this.searchEmail = email;
    }
        
    /**
     *  Get name of the author.
     *  @return string with the name of the author
     */
    public String getName() {
        return name;
    }
    
    /**
     *  Set name of the author.
     *  @param name name of the author
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     *  Get organization of the author.
     *  @return string with the organization of the author
     */
    public String getOrganization() {
        return organization;
    }
    
    /**
     *  Set organization of the author.
     *  @param organization organization of the author
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    
    /**
     *  Get role of the author.
     *  @return string with the role of the author
     */
    public String getRole() {
        return role;
    }
    
    /**
     *  Set role of the author.
     *  @param role role of the author
     */
    public void setRole(String role) {
        this.role = role;
    }
    
    /**
     *  Get address of the author.
     *  @return string with the address of the author
     */
    public String getAddress() {
        return address;
    }
    
    /**
     *  Set address of the author.
     *  @param address address of the author
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     *  Get phone number of the author.
     *  @return string with the phone number of the author
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    /**
     *  Set phone number of the author.
     *  @param phoneNumber phone number of the author
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    /**
     *  Get email of the author.
     *  @return string with the email of the author
     */
    public String getEmail() {
        return email;
    }
    
    /**
     *  Set email of the author.
     *  @param email email of the author
     */
    public void setEmail(String email) {
        this.email = email;
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