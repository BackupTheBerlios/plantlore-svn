/*
 * Occurrence.java
 *
 * Created on 23. leden 2006, 21:46
 *
 */

package net.sf.plantlore.common.record;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *  Data holder object representing TAUTHORS table in the DB. This object is used as a data holder
 *  for Hibernate operations on the server side. On the side of the client, it represents an occurrence
 *  we are currently working with. It is being sent from client to server and back when executing
 *  database queries.
 *
 *  @author Jakub Kotowski
 *  @author Tomas Kovarik
 *  @version 0.1, Mar 14, 2006
 */
public class Occurrence extends Record implements Deletable {
    /** Parameters of the occurrence. For detailed explanation see data model documentation. */
    private Integer id;
    private String unitIdDb;
    private String unitValue;
    private Habitat habitat;
    private Plant plant;
    private Integer yearCollected;
    private Integer monthCollected;
    private Integer dayCollected;
    private java.util.Date timeCollected;
    private java.util.Date isoDateTimeBegin;
    private String dataSource;
    private Publication publication;
    private String herbarium;
    private java.util.Date createdWhen;
    private User createdWho;
    private java.util.Date updatedWhen;
    private User updatedWho;
    private Metadata metadata;
    private Integer deleted;
    private String note;

    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String UNITIDDB = "unitIdDb";    
    public static final String UNITVALUE = "unitValue";
    public static final String HABITAT = "habitat";    
    public static final String PLANT = "plant";    
    public static final String YEARCOLLECTED = "yearCollected";    
    public static final String MONTHCOLLECTED = "monthCollected";    
    public static final String DAYCOLLECTED = "dayCollected";    
    public static final String TIMECOLLECTED = "timeCollected";
    public static final String ISODATETIMEBEGIN = "isoDateTimeBegin";    
    public static final String DATASOURCE = "dataSource";    
    public static final String PUBLICATION = "publication";    
    public static final String HERBARIUM = "herbarium";    
    public static final String CREATEDWHEN = "createdWhen";    
    public static final String CREATEDWHO = "createdWho";
    public static final String UPDATEDWHEN = "updatedWhen";    
    public static final String UPDATEDWHO = "updatedWho";    
    public static final String METADATA = "metadata";    
    public static final String DELETED = "deleted";
    public static final String NOTE = "note";        
    
    //public enum Column {ID, UNITIDDB, UNITVALUE, HABITAT, PLANT, YEARCOLLECTED, MONTHCOLLECTED, DAYCOLLECTED, TIMECOLLECTED, ISODATETIMEBEGIN, DATASOURCE, PUBLICATION,
    //HERBARIUM, CREATEDWHEN, CREATEDWHO, UPDATEDWHEN, UPDATEDWHO, METADATA, DELETED, NOTE};
    
    @Override
    public List<String> getForeignKeys() { 
    	return asList( HABITAT, PLANT, PUBLICATION, METADATA ); 
    }
    
    public List<String> getColumns() {
    	return asList( UNITIDDB, UNITVALUE, HABITAT, PLANT, YEARCOLLECTED,
    			MONTHCOLLECTED, DAYCOLLECTED, TIMECOLLECTED, ISODATETIMEBEGIN,
    			DATASOURCE, PUBLICATION, HERBARIUM, /*CREATEDWHEN,*/ 
    			/*UPDATEDWHEN,*/ METADATA, NOTE );
    }
    
    public List<String> getHistoryColumns() {
        return asList(  PLANT, YEARCOLLECTED, MONTHCOLLECTED, DAYCOLLECTED, TIMECOLLECTED, 
                        DATASOURCE, PUBLICATION, HERBARIUM, METADATA, NOTE);
    }
            
    public List<String> getNN() {
    	List<String> nn = getForeignKeys();
    	nn.remove(PUBLICATION);
    	nn.addAll( asList(UNITIDDB, UNITVALUE, YEARCOLLECTED/*, CREATEDWHEN*//*, UPDATEDWHEN*/) );
    	return nn;
    }
    
    @Override
    public void setValue(String column, Object value) {
    	if(value instanceof String && "".equals(value))
        	value = null;
    	
		if(column.equals(ID)) setId((Integer)value);
		else if(column.equals(UNITIDDB)) setUnitIdDb((String)value);
		else if(column.equals(UNITVALUE)) setUnitValue((String)value);
		else if(column.equals(HABITAT)) setHabitat((Habitat)value);
		else if(column.equals(PLANT)) setPlant((Plant)value);
		else if(column.equals(YEARCOLLECTED)) {
			if (value != null && value instanceof String)  
				setYearCollected(Integer.parseInt((String) value));
			else 
				setYearCollected((Integer)value);
		}
		else if(column.equals(MONTHCOLLECTED)) {
			if (value != null && value instanceof String) 
				setMonthCollected(Integer.parseInt((String) value));
			else 
				setMonthCollected((Integer)value);
		}
		else if(column.equals(DAYCOLLECTED)) { 
			if (value != null && value instanceof String) 
				setDayCollected(Integer.parseInt((String) value));
			else  
				setDayCollected((Integer)value);
		}
		else if(column.equals(TIMECOLLECTED)) setTimeCollected(checkDate(value));
		else if(column.equals(ISODATETIMEBEGIN)) setIsoDateTimeBegin(checkDate(value));
		else if(column.equals(DATASOURCE)) setDataSource((String)value);
		else if(column.equals(PUBLICATION)) setPublication((Publication)value);
		else if(column.equals(HERBARIUM)) setHerbarium((String)value);
		else if(column.equals(CREATEDWHEN)) setCreatedWhen(checkDate(value));
		else if(column.equals(UPDATEDWHEN)) setUpdatedWhen(checkDate(value));
		else if(column.equals(METADATA)) setMetadata((Metadata)value);
		else if(column.equals(NOTE)) setNote((String)value);
		else if(column.equals(DELETED)) setDeleted((Integer)value);
    }
    
    @Override 
    public boolean isDead() {
    	Integer c = getDeleted();
    	if( c == null ) return false;
    	else return c != 0;
    }
    
    public Date checkDate(Object value) {
        if (value.getClass() == String.class) {
             DateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");                          
            try {
                return myDateFormat.parse((String) value);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }                          
        }
        return (Date) value;            
    }
    
    /** Creates a new instance of OccurrenceRecord */
    public Occurrence() {
        
    }        
    
    /**
     *   Get occurrence id
     *   @return id of the occurrence
     *   @see setId
     */
    public Integer getId() {
        //obligatory
        return this.id;
    }
    
    /**
     *   Set occurrence id
     *   @param id id of the occurrence
     *   @see getId
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     *   Get unique identificator of the database
     *   @return unique identificator of the database
     *   @see setUnitIdDb
     */
    public String getUnitIdDb() {
        //obligatory
        return this.unitIdDb;
    }
    
    /**
     *   Set unique identificator of the database
     *   @param unitIdDb unique identificator of the database
     *   @see getUnitIdDb
     */
    public void setUnitIdDb(String unitIdDb) {
        this.unitIdDb = unitIdDb;
    }
    
    /**
     *   Get unique record value
     *   @return unique record value
     *   @see setUnitValue
     */
    public String getUnitValue() {
        //obligatory
        return this.unitValue;
    }
    
    /**
     *   Set unique record value
     *   @param unitValue unique record value
     *   @see getUnitValue
     */
    public void setUnitValue(String unitValue) {
        this.unitValue = unitValue;
    }

    /**
     *   Get Habitat record associated with this occurrence
     *   @return habitat record associated with trhis occurrence
     *   @see setHabitat
     */
    public Habitat getHabitat() {
        //obligatory
        return this.habitat;
    }
    
    /**
     *   Set Habitat record associated with this occurrence
     *   @param habitat Habitat record associated with this occurrence
     *   @see getHabitat
     */
    public void setHabitat(Habitat habitat) {
        this.habitat = habitat;
    }
    
    /**
     *   Get Plant record associated with this occurrence
     *   @return Plant record associated with this occurrence
     *   @see setPlant
     */
    public Plant getPlant() {        
        //obligatory
        return this.plant;
    }
    
    /**
     *   Set Plant record associated with this occurrence
     *   @param plant Plant record associated with this occurrence
     *   @see getPlant
     */
    public void setPlant(Plant plant) {
        this.plant = plant;
    }
    
    /**
     *   Get year when plant was collected
     *   @return year when plant was collected
     *   @see setYearCollected
     */
    public Integer getYearCollected() {
        //obligatory
        return this.yearCollected;
    }
    
    /**
     *   Set year when plant was collected
     *   @param yearCollected year when plant was collected
     *   @see getYearCollected
     */
    public void setYearCollected(Integer yearCollected) {
        this.yearCollected = yearCollected;
    }
    
    /**
     *   Get month when plant was collected
     *   @return month when plant was collected
     *   @see setMonthCollected
     */
    public Integer getMonthCollected() {
        return this.monthCollected;
    }
    
    /**
     *   Get month when plant was collected
     *   @return month when plant was collected, returns -1 instead of null
     *   @see setMonthCollected
     */
    public Integer getMonthCollectedNN() {
        if (this.monthCollected == null)
            return -1;
        return this.monthCollected;
    }
    
    /**
     *   Set month when plant was collected
     *   @param monthCollected year when plant was collected
     *   @see getMonthCollected
     */
    public void setMonthCollected(Integer monthCollected) {
        this.monthCollected = monthCollected;
    }
    
    /**
     *   Get day when plant was collected
     *   @return day when plant was collected
     *   @see setDayCollected
     */
    public Integer getDayCollected() {
        return this.dayCollected;
    }
    
    /**
     *   Get day when plant was collected
     *   @return day when plant was collected, returns -1 instead of null
     *   @see setDayCollected
     */
    public Integer getDayCollectedNN() {
        if (this.dayCollected == null)
            return -1;
        return this.dayCollected;
    }
    
    /**
     *   Set day when plant was collected
     *   @param dayCollected year when plant was collected
     *   @see getDayCollected
     */
    public void setDayCollected(Integer dayCollected) {
        this.dayCollected = dayCollected;
    }
    
    /**
     *   Get time when the occurrence was recorded
     *   @return time when the occurrence was recorded
     *   @see setTimeCollected
     */
    public java.util.Date getTimeCollected() {
        return this.timeCollected;
    }
    
    /**
     *   Get time when the occurrence was recorded
     *   @return time when the occurrence was recorded, returns new Date(0) instead of null
     *   @see setTimeCollected
     */
    public java.util.Date getTimeCollectedNN() {
        if (this.isoDateTimeBegin == null) 
            return new Date(0);
        return this.timeCollected;
    }
    
    /**
     *   Set time when the occurrence was recorded
     *   @param timeCollected time when the occurrence was recorded
     *   @see getTimeCollected
     */
    public void setTimeCollected(java.util.Date timeCollected) {
        this.timeCollected = timeCollected;
    }
    
    /**
     *   Get time/date when the plant was collected
     *   @return time/date when the plant was collected
     *   @see setIsoDateTimeBegin
     */
    public java.util.Date getIsoDateTimeBegin() {
        return this.isoDateTimeBegin;
    }
    
    /**
     *   Get time/date when the plant was collected
     *   @return time/date when the plant was collected, returns new Date(0) instead of null
     *   @see setIsoDateTimeBegin
     */
    public java.util.Date getIsoDateTimeBeginNN() {
        if (this.isoDateTimeBegin == null) 
            return new Date(0);
        return this.isoDateTimeBegin;
    }
    
    /**
     *   Set time/date when the plant was collected
     *   @param isoDateTimeBegin time/date when the plant was collected
     *   @see getIsoDateTimeBegin
     */
    public void setIsoDateTimeBegin(java.util.Date isoDateTimeBegin) {
        this.isoDateTimeBegin = isoDateTimeBegin;
    }
    
    /**
     *   Get dataSource
     *   @return dataSource
     *   @see setDataSource
     */
    public String getDataSource() {
        return this.dataSource;
    }
    
    /**
     *   Get dataSource
     *   @return dataSource  returns "" instead of null
     *   @see setDataSource
     */
    public String getDataSourceNN() {
        if (this.dataSource == null)
            return "";
        return this.dataSource;
    }
    
    /**
     *   Set dataSource
     *   @param dataSource
     *   @see getDataSource
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }        
    
    /**
     *   Get associated publication record
     *   @return associated publication record
     *   @see setPublication
     */
    public Publication getPublication() {
        //FK
        return this.publication;
    }
    
    /**
     *   Set associated publication record
     *   @param publication associated publication record
     *   @see getPublication
     */
    public void setPublication(Publication publication) {
        this.publication = publication;
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
     *   Get herbarium
     *   @return herbarium, returns "" instead of null
     *   @see setHerbarium
     */
    public String getHerbariumNN() {
        if (this.herbarium == null)
            return "";
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
     *   Get date and time when the occurrence record was inserted into database
     *   @return date and time when the occurrence record was inserted into database
     *   @see setCreatedWhen
     */
    public java.util.Date getCreatedWhen() {
        //obligatory
        return this.createdWhen;
    }
    
    /**
     *   Set date and time when the occurrence record was inserted into database
     *   @param createdWhen date and time when the occurrence record was inserted into database
     *   @see getCreatedWhen
     */
    public void setCreatedWhen(java.util.Date createdWhen) {
        this.createdWhen = createdWhen;
    }
    
    /**
     *   Get date and time when the occurrence record was last updated
     *   @return date and time when the occurrence record was last updated
     *   @see setUpdatedWhen
     */
    public java.util.Date getUpdatedWhen() {
        //obligatory
        return this.updatedWhen;
    }
    
    /**
     *   Set date and time when the occurrence record was last updated
     *   @param updatedWhen date and time when the occurrence record was last updated
     *   @see getUpdatedWhen
     */
    public void setUpdatedWhen(java.util.Date updatedWhen) {
        this.updatedWhen = updatedWhen;
    }
    
    /**
     *   Get associated User record with the user who created the record
     *   @return associated User record with the user who created the record
     *   @see setCreatedWho
     */
    public User getCreatedWho() {
        //obligatory
        return this.createdWho;
    }
    
    /**
     *   Set associated User record with the user who created the record
     *   @param createdWho associated User record with the user who created the record
     *   @see getCreatedWho
     */
    public void setCreatedWho(User createdWho) {
          this.createdWho = createdWho;
    }
    
    /**
     *   Get associated User record with the user who last updated the record
     *   @return associated User record with the user who last updated the record
     *   @see setUpdatedWho
     */
    public User getUpdatedWho() {
        //obligatory
        return this.updatedWho;
    }
    
    /**
     *   Set associated User record with the user who last updated the record
     *   @param updatedWho associated User record with the user who last updated the record
     *   @see getUpdatedWho
     */
    public void setUpdatedWho(User updatedWho) {
        this.updatedWho = updatedWho;
    }
    
    /**
     *   Get associated Metadata record
     *   @return associated Metadata record
     *   @see setMetadata
     */
    public Metadata getMetadata() {
        //obligatory
        return this.metadata;
    }
    
    /**
     *   Set associated Metadata record
     *   @param metadata associated Metadata record
     *   @see getMetadata
     */
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
    
    /**
     *   Get flag telling whether the record has been deleted
     *   @return flag telling whether the record has been deleted. Value 1 means deleted.
     *   @see setDeleted
     */
    public Integer getDeleted() {
        //obligatory
        return this.deleted;
    }
    
    /**
     *   Set flag telling whether the record has been deleted
     *   @param deleted flag telling whether the record has been deleted. Value 1 means deleted.
     *   @see getDeleted
     */
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }   
    
    /**
     *   Get note about occurrence
     *   @return string containing note about occurrence
     *   @see setNote
     */
    public String getNote() {
        return this.note;
    }
    
    /**
     *   Get note about occurrence
     *   @return string containing note about occurrence, returns "" instead of null
     *   @see setNote
     */
    public String getNoteNN() {
        if (this.note == null)
            return "";
        return this.note;
    }
    
    /**
     *   Set note about occurrence
     *   @param contact string containing note about occurrence
     *   @see getNote
     */
    public void setNote(String note) {
        this.note = note;
    }   
}
