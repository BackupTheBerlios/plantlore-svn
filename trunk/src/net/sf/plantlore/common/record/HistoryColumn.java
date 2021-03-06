/*
 * HistoryColumn.java
 *
 * Created on 14. březen 2006, 21:29
 */

package net.sf.plantlore.common.record;

/**
 *  Data holder object representing THISTORYCOLUMN table in the DB. This object is used as a data
 *  holder for Hibernate operations on the server side. On the side of the client, it represents part
 *  of a history record we are working with. It is being sent from client to server and back when
 *  executing database queries.
 *
 *  @author Lada Oberreiterova
 *  @author Tomas Kovarik
 */
public class HistoryColumn extends Record {
	
	private static final long serialVersionUID = 20060604008L;
	
    /** Parameters of the HistoryColumn. For detailed explanation see data model documentation. */
    private Integer id;    
    private String tableName;
    private String columnName;

    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String TABLENAME = "tableName";    
    public static final String COLUMNNAME = "columnName";

    //public enum Column {ID, TABLENAME, COLUMNNAME};
    
    /**
     *   Default constructor to create new class HistoryColumn
     */
    public HistoryColumn() {
        
    }
    
      
    /**
     *   Get HistoryColumn id
     *   @return id of the HistoryColumn
     *   @see setId
     */
    public Integer getId() {
        return this.id;
    }
    
    /**
     *   Set HistoryColumn id
     *   @param id id of the HistoryColumn
     *   @see getId
     */
    public void setId(Integer id) {
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
     *   Get name of the column where the value was changed
     *   @return  name of the column where the value was changed
     *   @see setColumnName
     */
    public String getColumnName() {
        return this.columnName;
    }
    
    /**
     *   Set  name of the column where the value was changed
     *   @param columnName string containing  name of the column where the value was changed
     *   @see getColumnName
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
