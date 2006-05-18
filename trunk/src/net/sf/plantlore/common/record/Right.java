/*
 * Right.java
 *
 * Created on 16. březen 2006, 0:55
 *
 */

package net.sf.plantlore.common.record;

/**
 *  Data holder object representing TRIGHT table in the DB. This object is used as a data holder
 *  for Hibernate operations on the server side. On the side of the client, it represents a right
 *  record we are currently working with. It is being sent from client to server and back when 
 *  executing database queries.
 *
 * @author Tomas Kovarik
 * @author Lada Oberreiterova
 */
public class Right extends Record {
    /** Parameters of the Right record */
    private Integer id;   
    private int administrator;
    private int editAll;    
    private String editGroup;
    private String seeColumns;
    private int add;    

    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String ADMINISTRATOR = "administrator";    
    public static final String EDITALL = "editAll";          
    public static final String EDITGROUP = "editGroup";    
    public static final String SEECOLUMNS = "seeColumns";    
    public static final String ADD = "add";    

    //public enum Column {ID, ADMINISTRATOR, EDITALL, EDITGROUP, SEECOLUMNS, ADD};
    
    /**
     *   Default constructor to create new class Right
     */
    public Right() {
        
    }
    
    /**
     *   Get right id
     *   @return id of the right
     *   @see setId
     */
    public Integer getId() {
        return this.id;
    }
    
    /**
     *   Set right id
     *   @param id id of the right
     *   @see getId
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     *   Get information about the administrator right
     *   @return information about the administrator right
     *   @see setAdministrator
     */
    public int getAdministrator() {
        return this.administrator;
    }
    
    /**
     *   Set information about the administrator right
     *   @param administrator information about the administrator right
     *   @see getAdministrator
     */
    public void setAdministrator(int administrator) {
        this.administrator= administrator;
    }
    
    /**
     *   Get information about edit all occurrences right
     *   @return information about edit all occurrences right
     *   @see setEditAll
     */
    public int getEditAll() {
        return this.editAll;
    }
    
    /**
     *   Set information about edit all occurrences right
     *   @param editAll information about edit all occurrences right
     *   @see getEditAll
     */
    public void setEditAll(int editAll) {
        this.editAll= editAll;
    }
    
          
    /**
     *   Get list of users whose occurrences the user can edit
     *   @return list of users whose occurrences the user can edit
     *   @see setEditGroup
     */
    public String getEditGroup() {
        return this.editGroup;
    }
    
    /**
     *   Set list of users whose occurrences the user can edit
     *   @param editGroup list of users whose occurrences the user can edit
     *   @see getEditGroup
     */
    public void setEditGroup(String editGroup) {
        this.editGroup = editGroup;
    }
    
    /**
     *   Get the list of columns the user can see
     *   @return list of columns the user can see
     *   @see setSeeColumns
     */
    public String getSeeColumns() {
        return this.seeColumns;
    }
    
    /**
     *   Set the list of columns the user can see
     *   @param seeColumns the list of columns the user can see
     *   @see getSeeColumns
     */
    public void setSeeColumns(String seeColumns) {
        this.seeColumns = seeColumns;
    }
    
    /**
     *   Get 
     *   @return 
     *   @see setAdd
     */
    public int getAdd() {
        return this.add;
    }
    
    /**
     *   Set
     *   @param add
     *   @see getAdd
     */
    public void setAdd(int add) {
        this.add = add;
    }    
}
