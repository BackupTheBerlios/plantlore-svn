package net.sf.plantlore.common.record;

import java.io.Serializable;

/**
 *  Data holder object representing THISTORY table in the DB. This object is used as a data holder
 *  for Hibernate operations on the server side. On the side of the client, it represents a history
 *  record we are working with. It is being sent from client to server and back when executing
 *  database queries.
 *
 *  @author Lada Oberreiterova
 *  @author Tomas Kovarik
 */
public class HistoryRecord implements Serializable {
    /** Parameters of the author. For detailed explanation see data model documentation. */
    private int id;
    private HistoryColumn historyColumn;
    private HistoryChange historyChange;
    private String oldValue;
    private String newValue;
    
    /** Creates a new instance of HistoryRecord */
    public HistoryRecord() {
        
    }
    
    /**
     *   Get History record id
     *   @return History record id
     *   @see setId
     */          
    public int getId() {
        return this.id;
    }

    /**
     *   Set History record id
     *   @param id History record id
     *   @see getId
     */            
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     *   Get associated HistoryColumn record
     *   @return instance of the associated HistoryColumn record
     *   @see setHistoryColumn
     */    
    public HistoryColumn getHistoryColumn() {
        return this.historyColumn;
    }

    /**
     *   Set associated HistoryColumn record
     *   @param historyColumn instance of the associated HistoryColumn record
     *   @see getHistoryColumn
     */        
    public void setHistoryColumn(HistoryColumn historyColumn) {
        this.historyColumn = historyColumn;
    }
    
    /**
     *   Get associated HistoryChange record
     *   @return instance of the associated HistoryChange record
     *   @see setHistoryChange
     */        
    public HistoryChange getHistoryChange() {
        return this.historyChange;
    }

    /**
     *   Set associated HistoryChange record
     *   @param historyChange instance of the associated HistoryChange record
     *   @see getHistoryChange
     */    
    public void setHistoryChange(HistoryChange historyChange) {
        this.historyChange = historyChange;
    }
    
    /**
     *   Get old value of the atribute which was changed
     *   @return name old value of the atribute which was changed
     *   @see setOldValue
     */
    public String getOldValue() {
        return this.oldValue;
    }
    
    /**
     *   Set old value of the atribute which was changed
     *   @param oldValue string containing old value of the atribute whitch was changed
     *   @see getOldValue
     */
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
    
    /**
     *   Get new value of the atribute which was changed
     *   @return new value of the atribute which was changed
     *   @see setNewValue
     */
    public String getNewValue() {
        return this.newValue;
    }
    
    /**
     *   Set new value of the atribute which was changed
     *   @param newValue string containing new value of the atribute which was changed
     *   @see getNewValue
     */
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}