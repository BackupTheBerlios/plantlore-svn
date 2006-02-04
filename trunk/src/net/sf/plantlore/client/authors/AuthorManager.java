/*
 * AuthorManager.java
 *
 * Created on 15. leden 2006, 2:04
 *
 */

package net.sf.plantlore.client.authors;

import java.util.ArrayList;
import java.util.Observable;
import net.sf.plantlore.client.dblayer.AuthorRecord;
import net.sf.plantlore.client.dblayer.DBLayer;
import net.sf.plantlore.client.dblayer.DBLayerException;
import net.sf.plantlore.client.dblayer.DeleteQuery;
import net.sf.plantlore.client.dblayer.InsertQuery;
import net.sf.plantlore.client.dblayer.QueryResult;
import net.sf.plantlore.client.dblayer.SelectQuery;
import net.sf.plantlore.common.SwingWorker;
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
    /** Error message to be displayed */
    private String errorMsg = null;
    private DBLayer database;
    private String firstName;
    private String surname;
    private String organization;
    private String role;    
    private String address;
    private String phoneNumber;
    private String email;
    private String url;
    private String note;
    private boolean done;
    private QueryResult queryResult;
    private static final int DEFAULT_DISPLAY_ROWS = 10;    
    private int displayRows = DEFAULT_DISPLAY_ROWS;
    private ArrayList data;
    private int currentFirstRow;
    private int authorIndex;
    
    /** Creates a new instance of AuthorManager */
    public AuthorManager(DBLayer database) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.database = database;
    }    
    
    /**
     *  Save new author to the database. Information about the author are stored in data fields of this class.
     *  Operation is executed in a separate thread using <code>SwingWorker</code>
     */
    public void saveAuthor() {     
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                done = false;
                // Create new Insert query
                InsertQuery q = new InsertQuery();
                // Set type of data we want to insert
                try {
                    q.setType("AUTHOR");
                } catch (DBLayerException e) {
                    // TODO: update
                    System.out.println("ERROR: "+e);
                    done = true;
                    return 0;
                }
                // XXX: temporary delay to show that progress bar works
/*                try {
                    System.out.println("going to sleep...");
                    Thread.sleep(5000);
                    System.out.println("waking up...");                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
 */
                // --- End of temporary code ---
                
                // Fill query with information about the author
                q.addData("id", "gen_id(GEN_TAUTHORS, 1)");
                q.addData("firstname", firstName);
                q.addData("surname", surname);        
                q.addData("organization", organization);                
                q.addData("role", role);
                q.addData("address", address);        
                q.addData("phonenumber", phoneNumber);                
                q.addData("email", email);                
                q.addData("url", url);                        
                q.addData("note", note);                        
                QueryResult QRes = null;
                // Execute query
                try {
                    QRes = database.executeQuery(q);        
                } catch (DBLayerException e) {
                    // TODO: update
                    System.out.println("ERROR: "+e);            
                    done = true;
                    return 0;
                }         
                // TODO: update
                if (QRes != null) {
                    System.out.println("Success: "+QRes.getNumRows());
                }
                done = true;
                return 1;
            }
        };
        worker.start();
    }    
    
    public void deleteAuthor() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                // perform delete
                done = false;
                // Create new Insert query
                DeleteQuery query = new DeleteQuery();
                // Set type of data we want to delete
                try {
                    query.setType("AUTHOR");
                } catch (DBLayerException e) {
                    // TODO: update
                    System.out.println("ERROR: "+e);
                    done = true;
                    return null;
                }
                query.addWhere("id", "=", ((AuthorRecord)data.get(getAuthorIndex())).getID()+"");
                QueryResult qRes = null;
                try {
                    qRes = database.executeQuery(query);        
                } catch (DBLayerException e) {
                    logger.error("Deleting author failed");
                    setError("Deleting author failed. Please contact your administrator.");
                } finally {
                    done = true;       
                    return qRes;                    
                }      
            }        
        };
        worker.start();            
    }
    
    public void editAuthor() {
        
    }
    
    /**
     *  Search for authors in the database. Criteria for search are stored in data fields of this class.
     *  Operation is executed in a separate thread using <code>SwingWorker</code>
     */
    public void searchAuthor() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                done = false;                
                // Create new Select query
                SelectQuery query = new SelectQuery();
                try {
                    query.setType("AUTHOR");
                } catch (DBLayerException e) {
                    System.out.println("ERROR: "+e);
                    done = true;
                    return null;
                }
/*                                
                try {
                    System.out.println("going to sleep...");
                    Thread.sleep(5000);
                    System.out.println("waking up...");                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
 */
                if (firstName != null)
                    query.addWhere("firstname", "LIKE", "%"+firstName+"%");
                if (organization != null) 
                    query.addWhere("organization", "LIKE", "%"+organization+"%");
                if (role != null)
                    query.addWhere("role", "LIKE", "%"+role+"%");
                if (email != null) 
                    query.addWhere("email", "LIKE", "%"+email+"%");
                
                QueryResult qRes = null;
                try {
                    qRes = database.executeQuery(query);        
                } catch (DBLayerException e) {
                    logger.error("Searching authors failed");
                    setError("Searching authors failed. Please contact your administrator.");
                } finally {
                    done = true;                    
                    setResult(qRes);
                    return qRes;                    
                }      
            }
        };
        worker.start();
    }

    public boolean processErrors() {
        if (this.errorMsg != null) {
            logger.debug("Notifying observers about an error");
            setChanged();
            notifyObservers();        
            this.errorMsg = null;
            return true;
        }
        return false;
    }

    public void processResults(int from, int count) {
        if (this.queryResult != null) {
            logger.debug("Rows in the result: "+this.queryResult.getNumRows());
            logger.debug("Max available rows: "+(from+count-1));
            int to = Math.min(this.queryResult.getNumRows(), from+count-1);
            logger.debug("Retrieving query results: "+from+" - "+to);
            try {
                Object[] objArray = database.more(this.queryResult, from-1, to-1);                
                logger.debug("Results retrieved. Count: "+objArray.length);
                this.data = new ArrayList(objArray.length);
                for (int i=0;i<objArray.length;i++) {
                    this.data.add((AuthorRecord)objArray[i]);
                }
            } catch (DBLayerException e) {
                logger.error("Processing search results failed: "+e.toString());
                this.setError("Searching authors failed. Please contact your administrator.");
            }
            // Update current first displayed row (only if data retrieval was successful)
            if (!this.isError()) {
                // Update current first displayed row
                setCurrentFirstRow(from);            
            }
            // Tell observers to update
            setChanged();
            notifyObservers();
            // Clean error flag (if it was set)
            this.errorMsg = null;
        }
    }
    
    public void setResult(QueryResult qRes) {
        this.queryResult = qRes;
    }
    
    public QueryResult getResult() {
        return this.queryResult;
    }
    
    public void setError(String msg) {
        this.errorMsg = msg;
    }
    
    public boolean isError() {
        if (this.errorMsg != null) {
            return true;
        } else {
            return false;
        }
    }
    
    public String getErrorMessage() {
        return this.errorMsg;
    }
        
    public ArrayList getData() {
        return this.data;
    }
    
    protected int getAuthorIndex() {
        return this.authorIndex;
    }
    
    protected void setAuthorIndex(int index) {
        this.authorIndex = index;
    }
    
    public int getDisplayRows() {
        return this.displayRows;
    }
    
    public void setDisplayRows(int rows) {
        this.displayRows = rows;
    }
    
    public int getCurrentFirstRow() {
        return this.currentFirstRow;
    }
    
    public void setCurrentFirstRow(int row) {
        this.currentFirstRow = row;
    }
    
    public void removeAuthorRecord() {
        // Remove record from the search results
        this.data.remove(this.getAuthorIndex());
        // Force observers to reflect the data changes
        setChanged();
        notifyObservers();        
    }    
    
    /**
     *  Indicates whether a long running operation executed in a separate thread has already finished.
     *  @return true if the operation is finished (no operation running), false otherwise
     */
    public boolean isOperationDone() {
        return this.done;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }    
}