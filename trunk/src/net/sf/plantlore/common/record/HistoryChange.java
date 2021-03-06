/*
 * HistoryChange.java
 *
 * Created on 14. březen 2006, 21:29
 */

package net.sf.plantlore.common.record;

/**
 *  Data holder object representing THISTORYCHANGE table in the DB. This object is used as a data 
 *  holder for Hibernate operations on the server side. On the side of the client, it represents part
 *  of a history record we are working with. It is being sent from client to server and back when 
 *  executing database queries.
 *
 *  @author Lada Oberreiterova
 *  @author Tomas Kovarik
 */
public class HistoryChange extends Record {
	
	private static final long serialVersionUID = 20060604010L;

    // Constants for HistoryChange operations (field COPERATION)
    public static final int HISTORYCHANGE_INSERT = 1;
    public static final int HISTORYCHANGE_EDIT = 2;
    public static final int HISTORYCHANGE_DELETE = 3;    
    
    /** Parameters of the HistoryChange. For detailed explanation see data model documentation. */
    private Integer id;
    private int recordId;    
    private int operation;   
    private java.util.Date when;
    private User who;
    private Integer version;
    
    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";     
    public static final String RECORDID = "recordId";    
    public static final String OPERATION = "operation";
    public static final String WHEN = "when";
    public static final String WHO = "who";    
    
    //public enum Column {ID, OCCURRENCE, RECORDID, OPERATION, WHEN, WHO};
    
    /**
     *   Default constructor to create new class HistoryChange
     */
    public HistoryChange() {
        
    }
    
      
    /**
     *   Get HistoryChange record id
     *   @return id of the HistoryChange record
     *   @see setId
     */
    public Integer getId() {
        //obligatory
        return this.id;
    }
    
    /**
     *   Set HistoryChange record id
     *   @param id   id of the HistoryChange record
     *   @see getId
     */
    public void setId(Integer id) {
        this.id = id;
    }
 
    /**
     *   Get identifier of the record which was changed
     *   @return identifier of the record which was changed
     *   @see setRecordId
     */
    public int getRecordId() {
        return this.recordId;
    }
    
    /**
     *  Set identifier of the record which was changed
     *  @param recordId string containing identifier of the record which was changed
     *  @see getRecordId
     */
    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    /**
     *   Get operation which was used. See constants defined for different operations.
     *
     *   @return operation which was used
     *   @see setOperation
     */
    public int getOperation() {
        return this.operation;
    }
    
    /**
     *   Set operation which was used
     *   @param operation string containing operation whitch was used
     *   @see getOperation
     */
    public void setOperation(int operation) {
        this.operation = operation;
    }    
    
    /**
     *   Get date and time when the record was changed
     *   @return date and time when the reccord was changed
     *   @see setWhen
     */
    public java.util.Date getWhen() {
        return this.when;
    }
    
    /**
     *   Set date and time when the record was changed
     *   @param when date and time when the reccord was changed
     *   @see getWhen
     */
    public void setWhen(java.util.Date when) {
        this.when = when;
    }
    
    /**
     *   Set user who edited (inserted, updated, deleted) this record
     *   @return user who edited this record
     *   @see setWho
     */
    public User getWho() {
        return this.who;
    }
    
    /**
     *   Set user who edited (inserted, updated, deleted) this record
     *   @param who user who edited this record
     *   @see getWho
     */
    public void setWho(User who) {
        this.who= who;
    }          
    
    /**
     *  Set the row version. Version column is used by Hibernate to implement optimistic locking.
     *  @param version version of the row
     */    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    /**
     *  Get the row version. Version column is used by Hibernate to implement optimistic locking.
     *  @return version of the row
     */
    public Integer getVersion() {
        return version;
    }        
}