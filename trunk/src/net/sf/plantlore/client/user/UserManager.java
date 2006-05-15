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
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.exception.DBLayerException;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class UserManager {
    
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
    private ArrayList<User> userList = new ArrayList();         
    // informace pro uzivatele o zmenach v tabulce User
    private String messageUser;
    
    //Informace o operaci, ktera se bude provadet - ADD, EDIT, DELETE, DETAIL
    private String operation = "";
    //Vyvrany zaznam v tabulce s Petadaty
    private User selectedRecord;
    
    //*********************Search - promenne podle,kterych se vyhledava************//
    /** Field to be used for sorting search query results */
    private int sortField = SORT_SURNAME;
    /** Direction of sorting. 0 = ASC, 1 = DESC. Default is ASC */
    private int sortDirection = 0;
    /** Direction of type user. 0 = All user,1 = Current user. Default is All user.*/
    private int showUserDirection = 0;
    /** Data about user*/
    private String login;
    private String password;
    private String firstName;
    private String surname;
    private String wholeName;
    private String email;
    private String address;
    private Date createWhen;
    private Date dropWhen;
    private Right right;
    private String note;
    private ArrayList<String> editGroup;
    
    /** list of all users - for dialog add, edit*/
    private Pair<String, Integer>[] users = null;
    
    /** Constants used for identification of fields for sorting */
    public static final int SORT_LOGIN = 1;
    public static final int SORT_FIRST_NAME = 2;
    public static final int SORT_SURNAME = 3;
    public static final int SORT_CREATEWHEN = 4;
  
    /**
     * Creates a new instance of UserManager
     */
    public UserManager(DBLayer database) {
        
       logger = Logger.getLogger(this.getClass().getPackage().getName());	 
       this.database = database;
       
       //nacteni dat o uzivatelovi
       searchUser();
       //opet funkci pro vyzadani si dat postupne
       processResult(1, displayRows);
    }
    
     /**
     *
     */
    public void searchUser() {
        
        //Create new Select query
        SelectQuery query = null;       

    	//  Select data from tUser table
        try {
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
                switch (sortField) {
                case 1:
                        field = User.LOGIN;
                        break;
                case 2:
                        field = User.FIRSTNAME;
                        break;
                case 3:
                        field = User.SURNAME;
                        break;
                case 4:
                        field = User.CREATEWHEN;
                        break;                
                default:
                        field = User.SURNAME;
                }

                if (sortDirection == 0) {
                        query.addOrder(PlantloreConstants.DIRECT_ASC, field);
                } else {
                        query.addOrder(PlantloreConstants.DIRECT_DESC, field);
                }
                                
        } catch (RemoteException e) {
            System.err.println("RemoteException - searchUserData(), createQuery");
        } catch (DBLayerException e) {
            System.err.println("DBLayerException - searchUserData(), createQuery");
        }
                
        int resultId = 0;
        try {
            // Execute query                    
            resultId = database.executeQuery(query);
            // Save "edit" User data
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
            	userList = new ArrayList<User>();  
                setDisplayRows(0);
            	setCurrentDisplayRows("0-0");
            } else {
                logger.debug("Retrieving query results: 1 - "+to);
                setCurrentDisplayRows(fromTable+ "-" + to);                
                try {                	 
                     // Retrieve selected row interval 
                	Object[] objectUser;
                 	try {
                 		objectUser = database.more(this.resultId, 0, to-1);  
                 	} catch(RemoteException e) {
                     	System.err.println("RemoteException- processEditResult, more");
                     	logger.debug("RemoteException- processEditResult, more");
                     	return;
                     }                   
                    int countResult = objectUser.length;  
                    logger.debug("Results retrieved. Count: "+ countResult);
                    // Create storage for the results
                    this.userList = new ArrayList<User>();
                    // Cast the results to the User objects
                    for (int i=0; i<countResult; i++ ) {                    							
						Object[] objHis = (Object[])objectUser[i];
                        this.userList.add((User)objHis[0]);
                    }           
                    //Update current first displayed row (only if data retrieval was successful)
                    setCurrentFirstRow(fromTable); 
                } catch (DBLayerException e) {                  
                    logger.error("Processing search results failed: "+e.toString());            
                }             
            }
        }         
    }
    
    public void addUserRecord (User user, Right right) {
        try {
            database.executeInsert(right);
            database.executeInsert(user);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
    }
    
    public void editUserRecord() {       
        try {
            database.executeUpdate(selectedRecord.getRight());
            database.executeUpdate(selectedRecord);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
    }
    
    public void deleteUserRecord() {
        
        //pokud bude nastaveno zobrazeni jen uzivatelu, kteri maji pristup do databaze, tak je nutne smazat uzivatele z listu
        
        //pri mazani uzivatele se mu nastavi DROPWHEN na aktualni cas - nebude fyzicky smazan z databaze
        selectedRecord.setDropWhen(new Date());
                
        try {
            database.executeUpdate(selectedRecord);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
    }      
        
    
    /**nacteni vsech seznamu uzivatelu pro autoTextArea 
     *
     */
    public Pair<String, Integer>[] getUsers() {
        if (users == null)
        {  
            SelectQuery sq;
            int resultid;
            int resultsCount;
            Object[] records;
            Object[] row;
            
            //vyhledani vsech uzivatelu pro autoTextArea
            try {
                sq = database.createQuery(User.class);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, User.WHOLENAME);
                sq.addProjection(PlantloreConstants.PROJ_PROPERTY, User.WHOLENAME);
                sq.addProjection(PlantloreConstants.PROJ_PROPERTY, User.ID);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                System.out.println("getUsers(): we got "+resultsCount+" results.");
                records = database.more(resultid, 0, resultsCount-1);
                users = new Pair[resultsCount];
                for (int i = 0; i < resultsCount; i++)
                {
                    row = (Object[])records[i];
                    users[i] = new Pair((String)row[0], (Integer)row[1]);
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }            
            return users;
        } else
            return users;
     }
    
    //****************************//
    //****Get and set metods*****//
    //**************************//
    
    // seznam jmen uzivatelu, kteri smeji editovat nalezy vybraneho uzivatele
    public void setEditGroup(ArrayList userList) {       
        this.userList = userList;
    }
    
    // podle seznamu id je nutne vygenerovat seznam jmen jednotlivych uzivatelu
    public String getEditGroup(String editGroupId) {
        
        return "";
    }
    
    //funkce vezme userList a users a vygeneruje String id1;id2;id3 pro ulozeni do databaze
    //ohlidaji se i duplicity
    public String getEditGroupID() {
        ArrayList<Integer> tmpUserId = new ArrayList<Integer>();
        for (int i=0 ; i < userList.size() ; i++) {
            for (int j=0; j < users.length; j++) {
                if (users[j].getFirst().equals(userList.get(i))) {
                    Integer userId = users[j].getSecond();
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
        logger.debug("EDITGROUPID: "+ editGroupId);
        //vrati string pro ulozeni do databaze
        return editGroupId;
    }
    
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

    public ArrayList<User> getUserList() {
              return this.userList;		  
       }

     public void setUserList(ArrayList<User> userList) {
              this.userList = userList;		  
     } 
    
     public String getCurrentDisplayRows() {
		  return this.displayRow;		  
	   }

     public void setCurrentDisplayRows(String displayRow) {
              this.displayRow = displayRow;		  
     } 
     
     public String getMessageUser() {
		  return this.messageUser;		  
	   }

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
    
    // predani informace o operaci, ktera byla zavolana - ADD, EDIT, DELETE, DETAIL
     public String getOperation() {
                  logger.debug("Operation: "+operation);
		  return this.operation;		  
	   }

     public void setOperation(String operation) {
              this.operation = operation;		  
     } 
    
    //Vraci User objekt vybraneho zaznam pro nasledny EDIT, DELETE ci zobrazeni DETAILU 
    public void setSelectedRecord(int selectedRecordId) {
        this.selectedRecord = (User)(userList.get(selectedRecordId));
    }
    
    public User getSelectedRecord() {
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
     *   Get first name of the user
     *   @return string containing the first name of the user
     *   @see setFirstName
     */
    public String getFirstName() {
        return this.firstName;
    }
    
    /**
     *   Set first name of the user
     *   @param firstName string containing the first name of the user
     *   @see getFirstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     *   Get surname of the user
     *   @return string containing surname of the user
     *   @see setSurname
     */
    public String getSurname() {
        return this.surname;
    }
    
    /**
     *   Set surname of the user
     *   @param surname string containing surname of the user
     *   @see getSurname
     */
    public void setSurname(String surname) {
        this.surname = surname;
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
     *   Get date when the user was created
     *   @return date when the user was added to the system
     *   @see setWhenCreated
     */
    public java.util.Date getWhenCreated() {
        return this.createWhen;
    }
    
    /**
     *   Set date when the user was created
     *   @param whenCreated date when the user was added to the system
     *   @see getWhenCreated
     */
    public void setWhenCreated(java.util.Date createWhen) {
        this.createWhen = createWhen;
    }
    
    /**
     *   Get date when the user was dropped
     *   @return date when the user was deleted from the system
     *   @see setWhenDropped
     */
    public java.util.Date getWhenDropped() {
        return this.dropWhen;
    }
    
    /**
     *   Set date when the user was dropped
     *   @param whenDropped date when the user was deleted from the system
     *   @see getWhenDropped
     */
    public void setWhenDropped(java.util.Date dropWhen) {
        this.dropWhen = dropWhen;
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
    
    /**
     *   Get note about the user
     *   @return string containing note about the user
     *   @see setNote
     */
    public String getNote() {
        return this.note;
    }
    
    /**
     *   Set note about the user
     *   @param contact string containing note about the user
     *   @see getNote
     */
    public void setNote(String note) {
        this.note = note;
    }   
        
}
