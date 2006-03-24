/*
 * AuthorManager.java
 *
 * Created on 15. leden 2006, 2:04
 *
 */

package net.sf.plantlore.client.authors;

import java.util.ArrayList;
import java.util.Observable;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.server.DBLayer;
import net.sf.plantlore.server.DBLayerException;
import net.sf.plantlore.client.dblayer.query.DeleteQuery;
import net.sf.plantlore.client.dblayer.query.InsertQuery;
import net.sf.plantlore.client.dblayer.query.Query;
import net.sf.plantlore.client.dblayer.result.Result;
import net.sf.plantlore.server.SelectQuery;
import net.sf.plantlore.common.SwingWorker;
import net.sf.plantlore.common.record.Author;
import org.apache.log4j.Logger;

/**
 * Author manager model.
 *
 * @author Tomas Kovarik
 * @version 0.1, 15.1. 2006
 */
public class AuthorManager extends Observable {
    /** Instance of a logger */
    private Logger logger;
    /** Exception with details about an error */
    private DBLayerException error = null;
    /** Instance of a database management object */
    private DBLayer database;
    /** First name of the author */
    private String firstName;
    /** Sutname of the author */
    private String surname;
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
    private Result queryResult;
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
    
    /** Constants used for identification of fields for sorting */
    public static final int SORT_NAME = 1;
    public static final int SORT_ORGANIZATION = 2;
    public static final int SORT_ROLE = 3;
    public static final int SORT_EMAIL = 4;
    public static final int SORT_PHONE = 5;    
    public static final int SORT_URL = 6;        
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
                Author author = new Author();
                author.setFirstName(firstName);
                author.setSurname(surname);
                author.setOrganization(organization);
                author.setRole(role);
                author.setAddress(address);
                author.setPhoneNumber(phoneNumber);
                author.setEmail(email);
                author.setUrl(url);
                author.setNote(note);
                // Execute query
                int rowId;
                try {
                    // Execute query
                    rowId = database.executeInsert(author);
                } catch (DBLayerException e) {
                    // Log and set an error
                    logger.error("Saving author failed. Unable to execute insert query");
                    setError(e);
                    // Set operation state to finished
                    done = true;
                    return null;
                }         
                logger.info("Author "+firstName+" "+surname+" saved successfuly.");
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
                    database.executeDelete((Author)data.get(getAuthorIndex()));                            
                } catch (DBLayerException e) {
                    // Log and set an error
                    logger.error("Deleting author failed. Unable to execute delete query.");
                    setError(e);
                    // Set operation state to finished                    
                    done = true;       
                    return null;
                }
                // Execute author search - required in order to display up-to-date data in the table of authors
                searchAuthor();                
                // Set operation state to finished
                done = true;       
                return null;
            }        
        };
        worker.start();            
    }
    
    public void editAuthor() {
        
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
                // Create new Select query
                SelectQuery query = database.createQuery(Author.class);
                if (searchName != null)
                    query.addRestriction(PlantloreConstants.RESTR_LIKE, "firstName", null, "%"+searchName+"%", null);
                if (searchOrganization != null) 
                    query.addRestriction(PlantloreConstants.RESTR_LIKE, "organization", null, "%"+searchOrganization+"%", null);
                if (searchRole != null)
                    query.addRestriction(PlantloreConstants.RESTR_LIKE, "role", null, "%"+searchRole+"%", null);
                if (searchEmail != null) 
                    query.addRestriction(PlantloreConstants.RESTR_LIKE, "email", null, "%"+searchEmail+"%", null);                
                String field;
                switch (sortField) {
                    case 1: field = "firstName";
                            break;
                    case 2: field = "organization";
                            break;
                    case 3: field = "role";
                            break;
                    case 4: field = "email";
                            break;
                    case 5: field = "phone";
                            break;                            
                    case 6: field = "url";
                            break;          
                    default:field = "firstName";
                }
                
                if (sortDirection == 0) {
                    query.addOrder(PlantloreConstants.DIRECT_ASC, field);
                } else {
                    query.addOrder(PlantloreConstants.DIRECT_DESC, field);                    
                }                
                Result qRes = null;
                try {
                    // Execute query                    
                    database.executeQuery(query);        
                } catch (DBLayerException e) {
                    // Log and set an error                   
                    logger.error("Searching authors failed. Unable to execute search query.");
                    setError(e);
                    // setError("Searching authors failed. Please contact your administrator.");
                } finally {
                    // Set operation state to finished
                    done = true;                    
                    // Save the results
                    setResult(qRes);
                    return qRes;                    
                } 
            }
        };
        worker.start();
    }

    /**
     *  Checks whether an error is set. If yes, notifies observers to display it. Finally unsets the error flag.
     *  @return <code>true</code> if an error was set (and observers were notified), <code>false</code> otherwise
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
     *  Process results of a search query. Retrieves results using the database management object (DBLayer) and stores them in the data field
     *  of the class. Notifies observers about the changes. Sets an error in case of an exception.
     *
     *  @param from number of the first row to retrieve.
     *  @param count number of rows to retrieve 
     */
    public void processResults(int from, int count) {
        if (this.queryResult != null) {
//            logger.debug("Rows in the result: "+this.queryResult.getNumRows());
            logger.debug("Max available rows: "+(from+count-1));
            // Find out how many rows we can retrieve - it cannot be more than number of rows in the result
//            int to = Math.min(this.queryResult.getNumRows(), from+count-1);
            int to = from+count-1;
            if (to == 0) {
                this.data = new ArrayList();                
            } else {
                logger.debug("Retrieving query results: "+from+" - "+to);                
                try {
                    // Retrieve selected row interval
                    Object[] objArray = database.more(from, to);                
                    logger.debug("Results retrieved. Count: "+objArray.length);
                    // Create storage for the results
                    this.data = new ArrayList(objArray.length);
                    // Cast the results to the AuthorRecord objects
                    for (int i=0;i<objArray.length;i++) {
                        this.data.add((Author)objArray[i]);
                    }
                } catch (DBLayerException e) {
                    // Log and set error in case of an exception
                    logger.error("Processing search results failed: "+e.toString());
                    setError(e);
                }
                // Update current first displayed row (only if data retrieval was successful). 
                if (!this.isError()) {
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
        this.setFirstName(selectedAuth.getFirstName());
        this.setSurname(selectedAuth.getSurname());
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
     *  Set result of a database operation. This is used only for search operations.
     *  @param qRes <code>QueryResult</code> object with the details about the result of a database operation
     */
    public void setResult(Result qRes) {
        this.queryResult = qRes;
    }
    
    /**
     *  Get results of last database operation. This is used only for search operations.
     *  @return <code>QueryResult</code> object with the details about the result of last database operation
     */
    public Result getResult() {
        return this.queryResult;
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
     *  Get index of the first row currently displayed in the list of authors. This is an index in the results returned by a search query.
     *  @return index of the first row currently displayed in the list of authors
     */
    public int getCurrentFirstRow() {
        return this.currentFirstRow;
    }
    
    /**
     *  Set index of the forst row currently displayed in the list of authors. This is an index in the results returned by a search query.
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
        if (this.queryResult != null) {
            return true;
        }
        return false;
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
     *  Get first name of the author.
     *  @return string with the first name of the author
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *  Set first name of the author.
     *  @param firstName first name of the author
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *  Get surname of the author.
     *  @return string with the surname of the author
     */
    public String getSurname() {
        return surname;
    }

    /**
     *  Set surname of the author.
     *  @param surname surname of the author
     */
    public void setSurname(String surname) {
        this.surname = surname;
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