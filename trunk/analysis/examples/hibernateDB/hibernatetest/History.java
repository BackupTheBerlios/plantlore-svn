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
public class History {

	/** Unique identifier of a record */
	private Integer id;
	   	
	/** Fields of the record*/    
	private String oldValue;
	private String newValue;	
	
  /** foring key referenced to table ... */
	private int columnId;
	private int changeId;
	
			
	/**
	 *   Default constructor to create new class History
	 */
	public History() {
   
	}

	/**
	 *   Get history id
	 *   @return id of the history
	 *   @see setId
	 */
	public Integer getId() {
	   return this.id;
	}

	/**
	 *   Set history id
	 *   @param id   id of the history
	 *   @see getId
	 */
	public void setId(Integer id) {
	   this.id = id;
	}

	/**
	 *   Get foreign key referenced to table THISTORYCOLUMN
	 *   @return foreign key referenced to table THISTORYCOLUMN 
	 *   @see setColumnId
	 */
	public int getColumnId() {
	   return this.columnId;
	}

	/**
	 *   Set foreign key referenced to table THISTORYCOLUMN
	 *   @param columnId foreign key referenced to table THISTORYCOLUMN
	 *   @see getColumnId
	 */
	public void setColumnId(int columnId) {
	   this.columnId = columnId;
	}
	  
  /**
	 *   Get foreign key referenced to table THISTORYCHANGE
	 *   @return foreign key referenced to table THISTORYCHANGE
	 *   @see setChangeId
	 */
	public int getChangeId() {
	   return this.changeId;
	}

	/**
	 *   Set foreign key referenced to table THISTORYCHANGE
	 *   @param changeId foreign key referenced to table THISTORYCHANGE
	 *   @see getChangeId
	 */
	public void setChangeId(int changeId) {
	   this.changeId = changeId;
	}
	  
	/**
	*   Get old value of atribute whitch was changed
	*   @return old value of atribute whitch was changed
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
