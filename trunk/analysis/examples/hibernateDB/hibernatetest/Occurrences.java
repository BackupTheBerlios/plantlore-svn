/*
 * Created on 2.1.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package hibernatetest;

import java.util.Date;

/**
 * @author Lada
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Occurrences {

	  /** Unique identifier of a record */
	  private Integer id;
   	
	  /** Fields of the record*/ 
// !!! ma byt typu smallint   
	  private int yearCollected;
	  private int monthCollected;
	  private int dayCollected;
// !!! timeCollected ma byt typu Time
	  private java.util.Date timeCollected;
	  private String dateSource;
	  private String herbarium;
	  private java.util.Date createWhen;
	  private java.util.Date updateWhen;
	  private java.util.Date isoDateTimeBegin;
	  private String unitIdDb;
	  private String unitValue;
	  private String note;

	/** foring key referenced to table ... */
	  private int habitatId;
	  private int plantId;
	  private int publicationsId;
	  private int createWho;
	  private int updateWho;
	  private int metadataId;
	  	
		
	  /**
	   *   Default constructor to create new class Occurrences
	   */
		  public Occurrences() {
   
		  }

		  /**
	   *   Get occurence id
	   *   @return id of the occurence
	   *   @see setId
	   */
	  public Integer getId() {
		 return this.id;
	  }

	  /**
	   *   Set occurence id
	   *   @param id   id of the occurence
	   *   @see getId
	   */
	  public void setId(Integer id) {
		 this.id = id;
	  }

	  /**
	   *   Get foreign key referenced to table THABITATS
	   *   @return foreign key referenced to table THABITATS 
	   *   @see setHabitatId
	   */
	  public int getHabitatId() {
		 return this.habitatId;
	  }

	  /**
	   *   Set foreign key referenced to table THABITATS
	   *   @param habitatId foreign key referenced to table THABITATS
	   *   @see getHabitatId
	   */
	  public void setHabitatId(int habitatId) {
		 this.habitatId = habitatId;
	  }

	/**
	   *   Get foreign key referenced to table TPLANT
	   *   @return foreign key referenced to table TPLANT 
	   *   @see setPlantId
	   */
	  public int getPlantId() {
		 return this.plantId;
	  }

	  /**
	   *   Set foreign key referenced to table TPLANT
	   *   @param plantId foreign key referenced to table TPLANT
	   *   @see getPlantId
	   */
	  public void setPlantId(int plantId) {
		 this.plantId = plantId;
	  }
	  
	 /**
	   *   Get foreign key referenced to table TPUBLICATIONS
	   *   @return foreign key referenced to table TPUBLICATIONS 
	   *   @see setPublicationsId
	   */
	  public int getPublicationsId() {
		 return this.publicationsId;
	  }

	  /**
	   *   Set foreign key referenced to table TPUBLICATIONS
	   *   @param publicationsId foreign key referenced to table TPUBLICATIONS
	   *   @see getPublicationsId
	   */
	  public void setPublicationsId(int publicationsId) {
		 this.publicationsId = publicationsId;
	  }
	  
	 /**
	   *   Get foreign key referenced to table TUSER
	   *   @return foreign key referenced to table TUSER
	   *   @see setCreateWho
	   */
	  public int getCreateWho() {
		 return this.createWho;
	  }

	  /**
	   *   Set foreign key referenced to table TUSER
	   *   @param createWho foreign key referenced to table TUSER
	   *   @see getCreateWho
	   */
	  public void setCreateWho(int createWho) {
		 this.createWho = createWho;
	  }
	  
	/**
	   *   Get foreign key referenced to table TUSER
	   *   @return foreign key referenced to table TUSER
	   *   @see setUpdateWho
	   */
	  public int getUpdateWho() {
		 return this.updateWho;
	  }

	  /**
	   *   Set foreign key referenced to table TUSER
	   *   @param updateWho foreign key referenced to table TUSER
	   *   @see getUpdateWho
	   */
	  public void setUpdateWho(int updateWho) {
		 this.updateWho = updateWho;
	  }		  		
		  
	/**
	   *   Get foreign key referenced to table TMETADATA
	   *   @return foreign key referenced to table TMETADATA
	   *   @see setMetadataId
	   */
	  public int getMetadataId() {
		 return this.metadataId;
	  }

	  /**
	   *   Set foreign key referenced to table TMETADATA
	   *   @param metadataId foreign key referenced to table TMETADATA
	   *   @see getMetadataId
	   */
	  public void setMetadataId(int metadataId) {
		 this.metadataId = metadataId;
	  }		  
	  
	/**
	   *   Get year where plant was collected
	   *   @return year where plant was collected
	   *   @see setYearCollected
	   */
	  public int getYearCollected() {
		 return this.yearCollected;
	  }

	  /**
	   *   Set year where plant was collected
	   *   @param yearCollected year where plant was collected
	   *   @see getYearCollected
	   */
	  public void setYearCollected(int yearCollected) {
		 this.yearCollected = yearCollected;
	  }		 
		  
	/**
	   *   Get month where plant was collected
	   *   @return month where plant was collected
	   *   @see setMonthCollected
	   */
	  public int getMonthCollected() {
		 return this.monthCollected;
	  }

	  /**
	   *   Set month where plant was collected
	   *   @param monthCollected year where plant was collected
	   *   @see getMonthCollected
	   */
	  public void setMonthCollected(int monthCollected) {
		 this.monthCollected = monthCollected;
	  }	
	  
	/**
	   *   Get day where plant was collected
	   *   @return day where plant was collected
	   *   @see setDayCollected
	   */
	  public int getDayCollected() {
		 return this.dayCollected;
	  }

	  /**
	   *   Set day where plant was collected
	   *   @param dayCollected year where plant was collected
	   *   @see getDayCollected
	   */
	  public void setDayCollected(int dayCollected) {
		 this.dayCollected = dayCollected;
	  }		 
		
	/**
	   *   Get unique identigicator of database
	   *   @return unique identigicator of database
	   *   @see setUnitIdDb
	   */      
	  public String getUnitIdDb() {
		 return this.unitIdDb;
	  }

	  /**
	   *   Set unique identigicator of database
	   *   @param unitIdDb unique identigicator of database
	   *   @see getUnitIdDb
	   */         
	  public void setUnitIdDb(String unitIdDb) {
		 this.unitIdDb = unitIdDb;
	  } 
		  	
	/**
	   *   Get unique value
	   *   @return unique value
	   *   @see setUnitValue
	   */      
	  public String getUnitValue() {
		 return this.unitValue;
	  }

	  /**
	   *   Set unique value
	   *   @param unitValue unique value
	   *   @see getUnitValue
	   */         
	  public void setUnitValue(String unitValue) {
		 this.unitValue = unitValue;
	  } 	
		
	/**
	   *   Get dateSource
	   *   @return dateSource
	   *   @see setDateSource
	   */      
	  public String getDateSource() {
		 return this.dateSource;
	  }

	  /**
	   *   Set dateSource
	   *   @param dateSource
	   *   @see getNote
	   */         
	  public void setDateSource(String dateSource) {
		 this.dateSource = dateSource;
	  }  
		  
	/**
	   *   Get herbarium
	   *   @return herbarium
	   *   @see setHerbarium
	   */      
	  public String getHerbarium() {
		 return this.herbarium;
	  }

	  /**
	   *   Set herbarium
	   *   @param herbarium
	   *   @see getHerbarium
	   */         
	  public void setHerbarium(String herbarium) {
		 this.herbarium = herbarium;
	  }  
	  
	/**
	   *   Get note about occurence
	   *   @return string containing note about occurence
	   *   @see setNote
	   */      
	  public String getNote() {
		 return this.note;
	  }

	  /**
	   *   Set note about occurence
	   *   @param contact string containing note about occurence
	   *   @see getNote
	   */         
	  public void setNote(String note) {
		 this.note = note;
	  }  
	  
	/**
	*   Get date and time when the reccord of occurence was inserted into database
	*   @return date and time when the reccord of occurence was inserted into database
	*   @see setCreateWhen
	*/         
   public java.util.Date getCreateWhen() {
	  return this.createWhen;
   }

   /**
	*   Set date and time when the reccord of occurence was inserted into database
	*   @param createWhen date and time when the reccord of occurence was inserted into database
	*   @see getCreateWhen
	*/            
   public void setCreateWhen(java.util.Date createWhen) {
	  this.createWhen = createWhen;
   }   
	   
   /**
   *   Get date and time when the reccord of occurence was updated 
   *   @return date and time when the reccord of occurence was updated 
   *   @see setUpdateWhen
   */         
  public java.util.Date getUpdateWhen() {
	 return this.updateWhen;
  }

  /**
   *   Set date and time when the reccord of occurence was updated 
   *   @param updateWhen date and time when the reccord of occurence was updated
   *   @see getUpdateWhen
   */            
  public void setUpdateWhen(java.util.Date updateWhen) {
	 this.updateWhen = updateWhen;
  }   	
	
  /**
	*   Get time when the reccord of occurence was inserted into database 
	*   @return time when the reccord of occurence was inserted into database
	*   @see setTimeCollected
	*/         
   public java.util.Date getTimeCollected() {
	  return this.timeCollected;
   }

   /**
	*   Set time when the reccord of occurence was inserted into database
	*   @param timeCollected time when the reccord of occurence was inserted into database
	*   @see getTimeCollected
	*/            
   public void setTimeCollected(java.util.Date timeCollected) {
	  this.timeCollected = timeCollected;
   }   		
		
   /**
	*   Get time/date when the plant was collected
	*   @return time/date when the plant was collected
	*   @see setISODateTimeBegin
	*/         
  public java.util.Date getISODateTimeBegin() {
	  return this.isoDateTimeBegin;
  }

  /**
	*   Set time/date when the plant was collected
	*   @param isoDateTimeBegin time/date when the plant was collected
	*   @see getISODateTimeBegin
	*/            
  public void setISODateTimeBegin(java.util.Date isoDateTimeBegin) {
	  this.isoDateTimeBegin = isoDateTimeBegin;
  }      
}
