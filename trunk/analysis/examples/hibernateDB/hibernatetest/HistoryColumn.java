/*
 * Created on 5.1.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package hibernatetest;

/**
 * @author Lada
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HistoryColumn {

	/** Unique identifier of a record */
   private Integer id;
   
   /** Fields of the record*/
   private String tableName;    
   private String columnName;

   /**
	*   Default constructor to create new class HistoryColumn
	*/
   public HistoryColumn() {
   
   }

   /**
	*   Get historyColumn id
	*   @return id of the historyColumn
	*   @see setId
	*/
   public Integer getId() {
	  return this.id;
   }

   /**
	*   Set historyColumn id
	*   @param id   id of the historyColumn
	*   @see getId
	*/
   public void setId(Integer id) {
	  this.id = id;
   }

   /**
	*   Get name of the table where value was changed
	*   @return of the table where value was changed
	*   @see setTableName
	*/
   public String getTableName() {
	  return this.tableName;
   }

   /**
	*   Set name of the table where value was changed
	*   @param tableName string containing of the table where value was changed
	*   @see getTableName
	*/
   public void setTableName(String tableName) {
	  this.tableName = tableName;
   }

   /**
   *   Get name of the column where value was changed
   *   @return  name of the column where value was changed
   *   @see setColumnName
   */
  public String getColumnName() {
	 return this.columnName;
  }

  /**
   *   Set  name of the column where value was changed
   *   @param columnName string containing  name of the column where value was changed
   *   @see getColumnName
   */
  public void setColumnName(String columnName) {
	 this.columnName = columnName;
  }
}
