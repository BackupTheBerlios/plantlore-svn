package net.sf.plantlore.common.record;


/**
 *  Data holder object containing information about history
 *
 *  @author Lada Oberreiterova
 */
public class HistoryRecord {

       
    // Fields of THISTORYCOLUMN table 
	/** primary key of the table THISTORYCOLUMN */
	private int historyColumnId;
    /** Name of the table where value was changed*/
	private String tableName;  
	/** Name of the column where value was changed*/
	private String columnName;
	
	//Fields of THISTORYCHANGE table
	/** primary key of the table THISTORYCHANGE */
	private int historyChangeId;
	/** Unique value identified record. 
	 * Foring key referenced to table TOCCURRENCES */	
	private int occurrenceId;
	/**Unique value identified record in table where value was changed */
	private int recordId;
	/** Operation whitch was used*/   
	private String operation;
	/** Date and time when the reccord was changed*/
	private java.util.Date when;	
	/** Name of user who did changed*/
	private String nameUser;

	// Fields of THISTORY table
	/** primary key of the table THISTORY */
	private int historyId;
	/** Old value of attribute*/    
	private String oldValue;
	/** New value of attribute*/
	private String newValue;
       
    /** Creates a new instance of HistoryRecord */
    public HistoryRecord()
    {              
    }	
    
    //*****************************************************
    //Get and set metods
    
    
    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }
    public int getHistoryColumnId() {
        return historyColumnId;
    }

    public void setHistoryColumnId(int historyColumnId) {
        this.historyColumnId = historyColumnId;
    }
    public int getHistoryChangeId() {
        return historyChangeId;
    }

    public void setHistoryChangeId(int historyChangeId) {
        this.historyChangeId = historyChangeId;
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
	/**
	 *   Get foreign key referenced to table TOCCURRENCES
	 *   @return foreign key referenced to table TOCCURRENCES
	 *   @see setOccurrenceId
	 */
	public int getOccurrenceId() {
	   return this.occurrenceId;
	}

	/**  
	 *   Set foreign key referenced to table TOCCURRENCES
	 *   @param occurrenceId foreign key referenced to table TOCCURRENCES
	 *   @see getOccurrenceId
	 */
	public void setOccurrenceId(int occurrenceId) {
	   this.occurrenceId= occurrenceId;
	}	
	
/**
	 *   Get identifier of the record whitch was changed
	 *   @return identifier of the record whitch was changed
	 *   @see setRecordId
	 */
	public int getRecordId() {
	   return this.recordId;
	}

	/**
	 *   Set identifier of the record whitch was changed
	 *   @param recordId string containing identifier of the record whitch was changed
	 *   @see getRecordId
	 */
	public void setRecordId(int recordId) {
	   this.recordId = recordId;
	}
	  
	/**
	*   Get operation whitch was used
	*   @return operation whitch was used
	*   @see setOperation
	*/
	public String getOperation() {
	  return this.operation;
	}

	/**
	*   Set operation whitch was used
	*   @param operation string containing operation whitch was used 
	*   @see getOperation
	*/
	public void setOperation(String operation) {
	  this.operation = operation;
	}
	 
	   
	/**
	*   Get date and time when the reccord was changed
	*   @return date and time when the reccord was changed
	*   @see setWhen
	*/         
    public java.util.Date getWhen() {
	  return this.when;
    }
 
   /**
	*   Set date and time when the reccord was changed
	*   @param when date and time when the reccord was changed
	*   @see getWhen
	*/            
    public void setWhen(java.util.Date when) {
	  this.when = when;
    }     	  
  
	/**
	*   Get name of user who did changed
	*   @return name of user who did changed
	*   @see setNameUser
	*/
    public String getNameUser() {
	  return this.nameUser;
    }

    /**
	*   Set name of user who did changed
	*   @param nameUser string containing name of user who did changed
	*   @see getNameUser
	*/
    public void setNameUser(String nameUser) {
	  this.nameUser = nameUser;
    }    
    
	/**
	*   Get old value of atribute whitch was changed
	*   @return name old value of atribute whitch was changed
	*   @see setOldValue
	*/
    public String getOldValue() {
	  return this.oldValue;
    }

    /**
	*   Set old value of atribute whitch was changed
	*   @param oldValue string containing old value of atribute whitch was changed
	*   @see getOldValue
	*/
    public void setOldValue(String oldValue) {
	  this.oldValue = oldValue;
    }
	   
	 /**
	*   Get new value of atribute whitch was changed
	*   @return new value of atribute whitch was changed 
	*   @see setNewValue
	*/
    public String getNewValue() {
	  return this.newValue;
    }

    /**
	*   Set new value of atribute whitch was changed
	*   @param newValue string containing new value of atribute whitch was changed
	*   @see getNewValue
	*/
    public void setNewValue(String newValue) {
	  this.newValue = newValue;
    }    
}

