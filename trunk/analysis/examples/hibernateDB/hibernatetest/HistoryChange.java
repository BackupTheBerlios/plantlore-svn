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
public class HistoryChange {

	/** Unique identifier of a record */
	private Integer id;
	   	
	/** Fields of the record*/    
	private String operation;
	private int recordId;
	private java.util.Date when;	
	
   /** foring key referenced to table TUSER */
	private int who;
	
	/** ??? foring key referenced to table TOCCURRENCES */
	private int occurrenceId;	
			
	/**
	 *   Default constructor to create new class HistoryChange
	 */
	public HistoryChange() {
   
	}

	/**
	 *   Get historyChange id
	 *   @return id of the historyChange
	 *   @see setId
	 */
	public Integer getId() {
	   return this.id;
	}

	/**
	 *   Set historyChange id
	 *   @param id   id of the historyChange
	 *   @see getId
	 */
	public void setId(Integer id) {
	   this.id = id;
	}

	/**
	 *   Get foreign key referenced to table TUSER
	 *   @return foreign key referenced to table TUSER
	 *   @see setWho
	 */
	public int getWho() {
	   return this.who;
	}

	/**
	 *   Set foreign key referenced to table TUSER
	 *   @param who foreign key referenced to table TUSER
	 *   @see getWho
	 */
	public void setWho(int who) {
	   this.who= who;
	}

	/**
	 *   ??? foreign key
	 *   Get foreign key referenced to table TOCCURRENCES
	 *   @return foreign key referenced to table TOCCURRENCES
	 *   @see setOccurrenceId
	 */
	public int getOccurrenceId() {
	   return this.occurrenceId;
	}

	/**
	 *   ??? foreign key
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
}
