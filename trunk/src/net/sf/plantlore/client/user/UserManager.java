/*
 * UserManager.java
 *
 * Created on 22. duben 2006, 14:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.user;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.exception.DBLayerException;
import org.apache.log4j.Logger;

/**
 * UserManager model. Contains bussines logic and data fields of the UserManager. Implements
 * operations including add user, edit user, delete user and search user. 
 * 
 * @author Lada Oberreiterova
 * @version 1.0
 */
public class UserManager extends Observable {
    
    /** Instance of a logger */
    private Logger logger;      
    /** Instance of a database management object */
    private DBLayer database;   
    /** Exception with details about an error */
    private String error = null;
    /** Constant with default number of rows to display */
    protected static final int DEFAULT_DISPLAY_ROWS = 6;    
    /** Actual number of rows to display */
    private int displayRows = DEFAULT_DISPLAY_ROWS;   
    /** Index of the first record shown in the table */
    private int currentFirstRow;
    /** Information about current display rows*/
    private String displayRow;    
    /** Result of the search query */
    private int resultId = 0;
    /** List of data (results of a search query) displayed in the table */
    private ArrayList<User> userList = new ArrayList<User>();         
    /** Message containing information about changes in user record*/
    private String messageUser;    
    /** Type of operation - ADD, EDIT, DELETE, DETAIL*/
    private String operation = "";
    /** Containing information about closing the addEdit dialog*/
    private boolean usedClose = false;
    /** Record for add, update or delete*/
    private User userRecord;
    /** Identifier of record for add, update or delete*/
    private int idRecord;
    /** Field to be used for sorting search query results */
    private int sortField = SORT_LOGIN;
    /** Direction of sorting. 0 = ASC, 1 = DESC. Default is ASC */
    private int sortDirection = 0;
    /** Direction of type user. 0 = All user,1 = Current user. Default is All user.*/
    private int showUserDirection = 0;    
    /** User login */
    private String login;        
    /** Firstname and surname of user */
    private String wholeName;
    /** Email of user */
    private String email;
    /** Address of user */
    private String address;   
    /** User rights */
    private Right right;
    /** List of all users - for dialog add, edit*/
    private Pair<String, Integer>[] users = null;
    /** List of all used login */
    private ArrayList<String> userLogin = new ArrayList<String>(); 
    
    /** Constants used for identification of fields for sorting */
    public static final int SORT_LOGIN = 0;
    public static final int SORT_FIRST_NAME = 1;
    public static final int SORT_SURNAME = 2;
    public static final int SORT_CREATEWHEN = 3;
  
    /** Constants with error descriptions */
    public static final String ERROR_SEARCH = L10n.getString("Error.UserSearchFailed");
    public static final String ERROR_PROCESS = L10n.getString("Error.UserProcessResultsFailed");
    public static final String ERROR_NUMBER_ROWS = L10n.getString("Error.GetNumberRows");
    public static final String ERROR_ADD = L10n.getString("Error.UserAddFailed");
    public static final String ERROR_EDIT = L10n.getString("Error.UserEditFailed");
    public static final String ERROR_DELETE = L10n.getString("Error.UserDeleteFailed");    
    public static final String ERROR_DBLAYER_TITLE = L10n.getString("Error.DBLayerExceptionTitle");
    public static final String ERROR_DBLAYER = L10n.getString("Error.DBLayerException");
    public static final String ERROR_REMOTE_TITLE = L10n.getString("Error.RemoteExceptionTitle");
    public static final String ERROR_REMOTE = L10n.getString("Error.RemoteException");
    public static final String ERROR_UNKNOWEN_TITLE = L10n.getString("Error.UnknownExceptionTitle");
    public static final String ERROR_UNKNOWEN = L10n.getString("Error.UnknownException");
    public static final String ERROR_TITLE = L10n.getString("Error.MessageTitle");
    public static final String ERROR_TRANSACTION = L10n.getString("Error.TransactionRaceConditions");
    public static final String ERROR_CREATE_PAIRS = L10n.getString("Error.UserManagerCreatePairs"); 
    public static final String ERROR_LOGIN = L10n.getString("Error.UserManagerDuplicityLogin");
    
    public static final String QUESTION_DELETE_TITLE = L10n.getString("Question.DeleteUserTitle");
    public static final String QUESTION_DELETE = L10n.getString("Question.DeleteUser");
    
    public static final String PROGRESS_SEARCH = L10n.getString("User.Search.ProgressTitle");
    public static final String PROGRESS_ADD = L10n.getString("User.Add.ProgressTitle");
    public static final String PROGRESS_EDIT = L10n.getString("User.Edit.ProgressTitle");
    public static final String PROGRESS_DELETE = L10n.getString("User.Delete.ProgressTitle");
    
    public static final String WARNING_SELECTION_TITLE = L10n.getString("Warning.EmptySelectionTitle");
    public static final String WARNING_SELECTION = L10n.getString("Warning.EmptySelection");
    
    public static final String INFORMATION_RESULT_TITLE = L10n.getString("Information.NoUserInResultTitle");
    public static final String INFORMATION_RESULT = L10n.getString("Information.NoUserInResult");
    public static final String INFORMATION_SEARCH_TITLE = L10n.getString("Information.SearchUserTitle");
    public static final String INFORMATION_SEARCH = L10n.getString("Information.SearchUser");    
    public static final String INFORMATION_DELETE_TITLE = L10n.getString("Information.UserDeleteTitle");
    public static final String INFORMATION_DELETE = L10n.getString("Information.UserDelete");    
   
    /**
     * Creates a new instance of UserManager
     * @param Instance of a database management object
     */
    public UserManager(DBLayer database) {
        
       logger = Logger.getLogger(this.getClass().getPackage().getName());	 
       this.database = database;              
    }
    
    /**
     *  Search for user in the database. Operation might be executed in a separate thread using the Task class (depends
     *  on the input parameters). The reason for this is that we are sometimes executing search from
     *  another long running operation, teherefore we do not need a new thread.
     *
     *  @param createTask tells whether to execute search in a separate thread
     *  @return instance of the Task with the long running operation (searching data)
     */    
    public Task searchUser(boolean createTask) {
       
    	if (createTask) {
	    	final Task task = new Task() {    		    		
	    		public Object task() throws DBLayerException, RemoteException {
	  		       int resultIdent;
	    			try {
	  		    	    resultIdent = search();
	  		    	    setResultId(resultIdent);	
	  		    	    loadLoginUsers();
		    	   } catch (RemoteException e) {	            
			            logger.error("Searching user failed. Remote exception caught in User. Details: "+e.getMessage());
			        	database.rollbackTransaction();
		                RemoteException remex = new RemoteException(ERROR_SEARCH + e);
		                remex.setStackTrace(e.getStackTrace());
		                throw remex; 		           
			        } catch (DBLayerException e) {
			        	logger.error("Searching user failed. DBLayer exception caught in User. Details: "+e.getMessage());       	                                                   
			        	database.rollbackTransaction();
		                DBLayerException dbex = new DBLayerException(ERROR_SEARCH + e);
		                dbex.setStackTrace(e.getStackTrace());
		                throw dbex; 		           
			        }					       
                    return null;			        
				}
		    };
		    return task;
    	} else {
    		try {
    			int resultIdent = search();
    			setResultId(resultIdent);
			} catch (DBLayerException e) {
				logger.error("Searching user failed. Remote exception caught in User. Details: "+e.getMessage());
				setError(ERROR_SEARCH);
				setChanged();
                notifyObservers();                
			} catch (RemoteException e) {
				logger.error("Searching user failed. DBLayer exception caught in User. Details: "+e.getMessage());
				setError(ERROR_SEARCH);
				setChanged();
                notifyObservers();                
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
        //Create new Select query
        SelectQuery query = null;   
        int resultId = 0;

    	//  Select data from tUser table        
        query = database.createQuery(User.class);         
        
        if (wholeName != null && !wholeName.equals("")) {
            query.addRestriction(PlantloreConstants.RESTR_LIKE, User.WHOLENAME, null, "%" + wholeName + "%", null);
        }
        if (login != null && !login.equals("")) {
            query.addRestriction(PlantloreConstants.RESTR_LIKE, User.LOGIN, null, "%" + login + "%", null);
        }
        if (email != null && !email.equals("")) {
            query.addRestriction(PlantloreConstants.RESTR_LIKE, User.EMAIL, null, "%" + email + "%", null);
        }
        if (address != null && !address.equals("")) {
            query.addRestriction(PlantloreConstants.RESTR_LIKE, User.ADDRESS, null, "%" + address + "%", null);
        }
        
        if ( this.showUserDirection == 1 ) {
            query.addRestriction(PlantloreConstants.RESTR_IS_NULL, User.DROPWHEN, null, null, null);
        }
        
        String field;
        logger.debug("SortField: " + sortField);
        switch (sortField) {
        case 0:
                field = User.LOGIN;
                break;
        case 1:
                field = User.FIRSTNAME;
                break;
        case 2:
                field = User.SURNAME;
                break;
        case 3:
                field = User.CREATEWHEN;
                break;                
        default:
                field = User.LOGIN;
        }

        logger.debug("Order by: "+ field);
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
     * @param from number of the first row to show in table. Number of the first row to retraieve is 1.
     * @param count number of rows to retrieve 
     */
    public void processResult(int from, int count) {
        
        if (this.resultId != 0) {
            int currentRow = getResultRows();
            logger.debug("Rows in the result: "+currentRow);
            logger.debug("Max available rows: "+(from+count-1));
           
            // Find out how many rows we can retrieve - it cannot be more than number of rows in the result
            int to = Math.min(currentRow, from+count-1);             
            if (to <= 0) {
            	userList = new ArrayList<User>(); 
            	setDisplayRows(0);
            	setCurrentDisplayRows("0-0");       
            } else {
                logger.debug("Retrieving query results: 1 -" + to);
                setCurrentDisplayRows(from+ "-" + to);                              	 
                // Retrieve selected row interval 
                Object[] objectUser;
             	try {
             		objectUser = database.more(this.resultId, 0, to-1);  
             	} catch(RemoteException e) {
             		logger.error("Remote exception caught in UserManager (processResult). Details: "+e.getMessage());
        			setError(ERROR_PROCESS);
        			setChanged();
                    notifyObservers();
                 	return;                                                                                       
                } catch (DBLayerException e) {                  
                    logger.error("Processing search results failed: " + e.getMessage());   
                    setError(ERROR_PROCESS);
                    setChanged();
                    notifyObservers();
                    return;
                }  
                if (objectUser == null) {
                	logger.error("tUser doesn`t contain required data");
                	setError(ERROR_PROCESS);
                    setChanged();
                    notifyObservers();
                    return;
                }
                int countResult = objectUser.length;  
                logger.debug("Results retrieved. Count: "+ countResult);                
                this.userList = new ArrayList<User>();
                // Cast the results to the User objects
                for (int i=0; i<countResult; i++ ) {                    							
                	Object[] objHis = (Object[])objectUser[i];
                    this.userList.add((User)objHis[0]);
                    logger.debug("Proccess: "+ i + ": " + (User)objHis[0]);
                }               
                // Update current first displayed row                
                logger.info("Results successfuly retrieved");                   
                setCurrentFirstRow(from);
            }                        
         }         
    }
            
     
    /**
     * Save new user into the database. 
     * Operation is executed in a separate thread using Task class.
     *      
     * @return instance of the Task with the long running operation (executeInsert)   
     */
    public Task addUserRecord() {
    	    	
    	final Task task = new Task() {    		    		
    		public Object task() throws DBLayerException, RemoteException {
    			try {
    				int rightId = database.executeInsert(right);
    	            right.setId(rightId);
    	            database.executeInsert(userRecord);		
    	            logger.debug("EXECUTE OK");
    	            //Add new name (login) of user to user list    	            
    	            int count = users.length;
    	            Pair<String, Integer>[] usersNew = new Pair[count+1];
    	            for(int i=0; i<count; i++) {
    	            	usersNew[i] = users[i];
    	            	logger.debug(i + ": " + usersNew.length);
    	            }
    	            usersNew[count] = new Pair(userRecord.getWholeName()+ " (" + userRecord.getLogin() + " )", userRecord.getId());
    	            logger.debug(userRecord.getId());
    	            //TODO overit, zda znam cID nebo je zjistit jinak ... pravdepodobne se rovna count+1    	            
    	            users = usersNew;
		        }catch (RemoteException e) {
		        	logger.error("Process add User failed. Remote exception caught in UserManager. Details: "+e.getMessage());		        	
                    RemoteException remex = new RemoteException(ERROR_ADD + e);
                    remex.setStackTrace(e.getStackTrace());
                    throw remex; 		           		       	    
		        } catch (DBLayerException e) {
		        	logger.error("Process add User failed. DBLayer exception caught in UserManager. Details: "+e.getMessage());       	                                                   		        
                    DBLayerException dbex = new DBLayerException(ERROR_ADD + e);
                    dbex.setStackTrace(e.getStackTrace());
                    throw dbex; 		            
		        } 		       		       					        
		        return null;
    		}
	    };
	    return task;
    } 		  
    
    /**
     *  Update User in the database. To-be-updated User is stored in <code>UserRecord</code>. 
     *  Operation is executed in a separate thread using Task class.
     *  
     *  @return instance of the Task with the long running operation (executeUpdate)
     */
    public Task editUserRecord() {       
           	
    	final Task task = new Task() {    		    		
    		public Object task() throws DBLayerException, RemoteException {
    			
    			boolean ok = false;
    			ok = database.beginTransaction();
		        if (!ok) {
		            logger.debug("UserManager.editUserRecord(): Can't create transaction. Another is probably already running.");
		            throw new DBLayerException(ERROR_TRANSACTION);
		        }		      
    			try {
    				database.executeUpdateInTransaction(userRecord.getRight());
    	            database.executeUpdateInTransaction(userRecord);
    	            userList.set(idRecord, userRecord);
    	            //Update list of names (logins) of users
    	            users[userRecord.getId()].setFirst(userRecord.getWholeName()+ " (" + userRecord.getLogin() + " )");
		        }catch (RemoteException e) {
		        	logger.error("Process update User failed. Remote exception caught in UserManager. Details: "+e.getMessage());
		        	database.rollbackTransaction();
		        	database.rollbackTransaction();
                    RemoteException remex = new RemoteException(ERROR_EDIT + e);
                    remex.setStackTrace(e.getStackTrace());
                    throw remex; 		           		       	    
		        } catch (DBLayerException e) {
		        	logger.error("Process update User failed. DBLayer exception caught in UserManager. Details: "+e.getMessage());       	                                                   
		        	database.rollbackTransaction();
		        	database.rollbackTransaction();
                    DBLayerException dbex = new DBLayerException(ERROR_EDIT + e);
                    dbex.setStackTrace(e.getStackTrace());
                    throw dbex; 		            
		        } 	
		        database.commitTransaction();
		        return null;
    		}
	    };
	    return task;
    } 
    	    	    	
    /**     
     *  Update User in the database. To-be-updated User is stored in <code>UserRecord</code> field.
     *  Operation is executed in a separate thread using Task class.
     *  
     *  @return instance of the Task with the long running operation (executeDelete) 
     */
    public Task deleteUserRecord() {
    	
    	
 //TODO pokud bude nastaveno zobrazeni jen uzivatelu, kteri maji pristup do databaze, tak je nutne smazat uzivatele z listu
        
        //Set actual time into param DROPWHEN - inform about deactive user account     	
        userRecord.setDropWhen(new Date());    	
    	final Task task = new Task() {    		    		
    		public Object task() throws DBLayerException, RemoteException {    			    			
    			try {
    				 database.executeUpdate(userRecord);		            
		        }catch (RemoteException e) {
		        	logger.error("Process delete User failed. Remote exception caught in UserManager. Details: "+e.getMessage());		        	
                    RemoteException remex = new RemoteException(ERROR_DELETE + e);
                    remex.setStackTrace(e.getStackTrace());
                    throw remex; 		           		       	    
		        } catch (DBLayerException e) {
		        	logger.error("Process delete User failed. DBLayer exception caught in UserManager. Details: "+e.getMessage());       	                                                   		        	
                    DBLayerException dbex = new DBLayerException(ERROR_DELETE + e);
                    dbex.setStackTrace(e.getStackTrace());
                    throw dbex; 		            
		        } 			        		      			        
		        return null;
    		}
	    };
	    return task;
    } 
    	  
    /** 
     * Load all login of users into autoTextArea (add/edit dialog).
     * @return pairs of objects - (name of user (login), identifier of record)
     * @throws DBLayerException in case search failed
     * @throws RemoteException in case network communication failed
     */
    public Pair<String, Integer>[] loadLoginUsers() throws DBLayerException, RemoteException{
        if (users == null)
        {  
            SelectQuery sq;
            int resultLoginid;
            int resultsCount;
            Object[] records;
            Object[] record;
            
            //Search all users
            try {
                sq = database.createQuery(User.class);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, User.WHOLENAME);
                sq.addProjection(PlantloreConstants.PROJ_PROPERTY, User.WHOLENAME);
                sq.addProjection(PlantloreConstants.PROJ_PROPERTY, User.ID);
                sq.addProjection(PlantloreConstants.PROJ_PROPERTY, User.LOGIN);
                resultLoginid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultLoginid);
                logger.debug("getUsers(): we got "+resultsCount+" results.");
                records = database.more(resultLoginid, 0, resultsCount-1);
                users = new Pair[resultsCount];
                userLogin.clear();
                for (int i = 0; i < resultsCount; i++)
                {
                    record = (Object[])records[i];
                    users[i] = new Pair((String)record[0] + " (" + (String)record[2] + ")", (Integer)record[1]);                    
                    userLogin.add((String)record[2]);
                }
                database.closeQuery(sq);
            }catch (RemoteException e) {	            
	            logger.error("Searching user and creating Pairs (login, identifier) failed. Remote exception caught in User. Details: "+e.getMessage());	        	
                RemoteException remex = new RemoteException(ERROR_CREATE_PAIRS + e);
                remex.setStackTrace(e.getStackTrace());
                throw remex; 		           
	        } catch (DBLayerException e) {
	        	logger.error("Searching user and creating Pairs (login, identifier) failed. DBLayer exception caught in User. Details: "+e.getMessage());       	                                                   
                DBLayerException dbex = new DBLayerException(ERROR_CREATE_PAIRS + e);
                dbex.setStackTrace(e.getStackTrace());
                throw dbex; 		           
	        }					       
            return users;
        } else
            return users;
     }       
   
    /** 
     *  Check whther exists login of usr adding by add dialog.
     *  @param login login of user adding by add dialog 
     *  @return true if login doesn`t exist yet, false in other way
     */
    public boolean uniqueLogin(String login) {               
        return userLogin.contains(login);
    }
    
    //****************************//
    //****Get and set metods*****//
    //**************************//
   
     /** 
      * Set list of users with right to edit records created by specific user
      * @param userList users names list
      */ 
    //TODO userList - urcite se to musi jmenovat jinak- jde o jmena v texArea u ADD, EDIT!!!
   // public void setEditGroup(ArrayList<String> userLogin) {       
    //    this.userLogin = userLogin;
    //}
    
    /** 
     * Create and get list of names of users which have right to edit records created by specific user     
     * @param editGroupId containing index of users separated by comma
     * @return users names list
     */
    public String getEditGroup(String editGroupId) {        
            String[] tmpUserId = editGroupId.split(",");
            logger.debug("tmpUserId: " + tmpUserId[0]);
            logger.debug("editGroupId: "+editGroupId);
           
            String editGroup = "";
            for (int i=0 ; i < tmpUserId.length ; i++) {
                for (int j=0; j < users.length; j++) {                    
                    if (users[j].getSecond() == Integer.parseInt(tmpUserId[i])) {
                        editGroup = editGroup + users[j].getFirst() + "\n";
                    }
                }
            }
            return editGroup;        
    }
        
    /**
     *  Get string containing index of users from userList separated by comma. This string is generated for saving into database.
     *  @return string containing index of users separated by comma
     */
    public String getEditGroupID() {
        ArrayList<Integer> tmpUserId = new ArrayList<Integer>();
        for (int i=0 ; i < userList.size() ; i++) {
            for (int j=0; j < users.length; j++) {
                if (users[j].getFirst().equals(userList.get(i))) {
                    Integer userId = users[j].getSecond();
                    //Check duplicity
                    if (!tmpUserId.contains(userId)) {
                        tmpUserId.add(userId);
                    }
                }
            }
        }
        
        String editGroupId="";
        for (int i=0; i < tmpUserId.size(); i++) {
            editGroupId = editGroupId + tmpUserId.get(i).toString() + ",";
        }               
        return editGroupId;
    }
 
    
    /**
     * Set new list of pair (name of user(login), index of user).
     * @param usersNew ist of pair (name of user(login), index of user)
     */
    public void setUsers(Pair<String, Integer>[] usersNew) {
        this.users = usersNew;
    }
   
    /**
     * Get ist of pair (name of user(login), index of user).
     * @return ist of pair (name of user(login), index of user)
     */
    public Pair<String, Integer>[] getUsers() {
        return this.users;
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
    	if (this.usedClose == true) {
    		usedClose = false;
    		return true;
    	}
    	return false;
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
                logger.error("Get number of results failed.Remote exception caught in UserManager. Details: "+e.getMessage());  
            	setError(ERROR_NUMBER_ROWS);
        }
        return resultCount;
    }

    /**
	  * Get results of a search query for dislpaying in userManager dialog
	  * @return results of a search query for dislpaying in userManager dialog
	  */
    public ArrayList<User> getUserList() {
              return this.userList;		  
       }

    /**
	  * Set results of a search query for dislpaying in userManager dialog
	  * @param userList results of a search query for dislpaying in userManager dialog
	  */
     public void setUserList(ArrayList<User> userList) {
              this.userList = userList;		  
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
     public String getMessageUser() {
		  return this.messageUser;		  
	   }

     /**
  	 * Set message with information for user
  	 * @param messageUser message with information for user
  	 */
     public void setMessageUser(String messageUser) {
              this.messageUser= messageUser;		  
     } 
     
    /**
     *  Get index of the first row currently displayed in the list of record changes. This is an index in the results returned by a search query.
     *  @return index of the first row currently displayed in the list of User
     */
    public int getCurrentFirstRow() {
        return this.currentFirstRow;
    }
    
    /**
     *  Set index of the forst row currently displayed in the list of record changes. This is an index in the results returned by a search query.
     *  @param row index of the first row currently displayed in the list of User
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
                  logger.debug("Operation: "+operation);
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
      *  Set selected User  
      *  @param userId index of the selected metadata in the list of user
      */ 
    public void setUserRecord(int userId) {
    	logger.debug("User: "+ userId + ": " + ((User)userList.get(userId)).getFirstName() + " list.length= " + userList.size());
        this.userRecord = (User)(userList.get(userId));
        this.idRecord = userId;
    }
    
    /**
     * Get selected User
     * @return selected User
     */
    public User getUserRecord() {
        return this.userRecord;
    }
    
    /**
     *  Set new user 
     *  @param user new instantce of User
     */
   public void setNewUserRecord(User user) {
       this.userRecord = user;
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
     *  Set direction of type user (All, current).
     *  @param direction direction of type user. 0 show all user, 1 show only current user
     */
    public void setShowUserDirection(int direction) {
        this.showUserDirection = direction;
    }
    
     /**
     *   Get login name of the user
     *   @return login name of the user
     *   @see setLogin
     */
    public String getLogin() {
        return this.login;
    }
    
    /**
     *   Set login name of the user
     *   @param login string containing login name of the user
     *   @see getLogin
     */
    public void setLogin(String login) {
        this.login = login;
    }
       
    /**
     *   Get whole name of the user
     *   @return string containing whole name of the user
     *   @see setWholeName
     */
    public String getWholeName() {
        return this.wholeName;
    }
    
    /**
     *   Set whole name of the user
     *   @param wholeName string containing whole name of the user
     *   @see getWholeName
     */
    public void setWholeName(String wholeName) {
        this.wholeName = wholeName;
    }
    
    /**
     *   Get email of the user
     *   @return string containing email of the user
     *   @see setEmail
     */
    public String getEmail() {
        return this.email;
    }
    
    /**
     *   Set email of the user
     *   @param email string containing email of the user
     *   @see getEmail
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     *   Get address of the user
     *   @return string containing address of the user
     *   @see setAddress
     */
    public String getAddress() {
        return this.address;
    }
    
    /**
     *   Set address of the user
     *   @param address string containing address of the user
     *   @see getAddress
     */
    public void setAddress(String address) {
        this.address = address;
    }  
    
    /**
     *   Get record with the rights of the user
     *   @return record with the rights of the user
     *   @see setRight
     */
    public Right getRight() {
        return this.right;
    }

    
    /**
     *   Set record with the rights of the user
     *   @param right record with the rights of the user
     *   @see getRight
     */
    public void setRight(Right right) {
        this.right = right;
    }
    
  }
