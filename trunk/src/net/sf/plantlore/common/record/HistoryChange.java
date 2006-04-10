/*
 * HistoryChange.java
 *
 * Created on 14. březen 2006, 21:29
 */

package net.sf.plantlore.common.record;

import java.io.Serializable;

/**
 *  Data holder object representing THISTORYCHANGE table in the DB. This object is used as a data 
 *  holder for Hibernate operations on the server side. On the side of the client, it represents part
 *  of a history record we are working with. It is being sent from client to server and back when 
 *  executing database queries.
 *
 *  @author Lada Oberreiterova
 *  @author Tomas Kovarik
 */
public class HistoryChange implements Serializable {

    // Constants for HistoryChange operations (field COPERATION)
    public static final int HISTORYCHANGE_INSERT = 1;
    public static final int HISTORYCHANGE_EDIT = 2;
    public static final int HISTORYCHANGE_DELETE = 3;
    public static final int HISTORYCHANGE_EDITGROUP = 4;    
    
    /** Parameters of the HistoryChange. For detailed explanation see data model documentation. */
    private int id;
    private Occurrence occurrence;
    private int recordId;
    private int oldRecordId;
    private int operation;   
    private java.util.Date when;
    private User who;
    
    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String OCCURRENCE = "occurrence";    
    public static final String RECORDID = "recordId";
    public static final String OLDRECORDID = "oldRecordId";
    public static final String OPERATION = "operation";
    public static final String WHEN = "when";
    public static final String WHO = "who";    
    
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
    public int getId() {
        return this.id;
    }
    
    /**
     *   Set HistoryChange record id
     *   @param id   id of the HistoryChange record
     *   @see getId
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *  Get occurrence associated with this HistoryChange record
     *  @return occurrence associated with this record
     *  @see setOccurrence
     */
    public Occurrence getOccurrence() {
        return this.occurrence;
    }
    
    /**
     *  Set occurrence associated with this HistoryChange record
     *  @param occurrence occurrence associated with this record
     *  @see getOccurrence
     */
    public void setOccurrence(Occurrence occurrence) {
        this.occurrence= occurrence;
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
     *   Get old identifier of the record before his changed. 
     *   This is defined only in case if the identifier was changed.
     *   @return identifier of the record before his changed. 
     *   @see setOldRecordId
     */
    public int getOldRecordId() {
        return this.oldRecordId;
    }
    
    /**
     *  Set old identifier of the record before his changed. 
     *  This is defined only in case if the identifier was changed.
     *  @param oldRecordId string containing identifier of the record before his changed.
     *  @see getOldRecordId
     */
    public void setOldRecordId(int oldRecordId) {
        this.oldRecordId = oldRecordId;
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
}
