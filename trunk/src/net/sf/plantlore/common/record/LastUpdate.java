package net.sf.plantlore.common.record;

/**
 *  Data holder object representing TLASTUPDATE table in the DB. This object is used as a data
 *  holder for Hibernate operations on the server side. On the side of the client, it represents part
 *  of a lastUpdate record we are working with. It is being sent from client to server and back when
 *  executing database queries.
 *
 *  @author Lada Oberreiterova  
 */
public class LastUpdate extends Record {

	 /** Parameters of the LastUpdate. For detailed explanation see data model documentation. */
    private int id;
    private String tableName;
    private java.util.Date lastUpdate;    
    
    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String TABLENAME = "tableName";    
    public static final String LASTUPDATE = "lastUpdate";
   
    /**
     *   Default constructor to create new class LastUpdate
     */
    public LastUpdate() {
        
    }
    
    /**
     *   Get LastUpdate record id
     *   @return id of the LastUpdate record
     *   @see setId
     */
    public int getId() {
        return this.id;
    }
    
    /**
     *   Set LastUpdate record id
     *   @param id   id of the LastUpdate record
     *   @see getId
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     *   Get name of the table where the value was changed
     *   @return of the table where the value was changed
     *   @see setTableName
     */
    public String getTableName() {
        return this.tableName;
    }
    
    /**
     *   Set name of the table where the value was changed
     *   @param tableName string containing of the table where the value was changed
     *   @see getTableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     *   Get date and time of table last update
     *   @return date and time of table last update
     *   @see setLastUpdate
     */
    public java.util.Date getLastUpdate() {
        return this.lastUpdate;
    }
    
    /**
     *   Set date and time of table last update
     *   @param lastUpdate date and time of table last update
     *   @see getLastUpdate
     */
    public void setLastUpdate(java.util.Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
