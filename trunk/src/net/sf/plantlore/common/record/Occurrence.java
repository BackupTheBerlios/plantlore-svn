/*
 * Occurrence.java
 *
 * Created on 23. leden 2006, 21:46
 *
 */

package net.sf.plantlore.common.record;

import java.util.ArrayList;
import java.util.Date;

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
public class Occurrence {
    /** Parameters of the occurrence. For detailed explanation see data model documentation. */
    private int id;
    private String unitIdDb;
    private String unitValue;
    private Habitat habitat;
    private Plant plant;
    private int yearCollected;
    private int monthCollected;
    private int dayCollected;
    private java.util.Date timeCollected;
    private java.util.Date isoDateTimeBegin;
    private String dateSource;
    private Publication publication;
    private String herbarium;
    private java.util.Date createWhen;
    private User createdWho;
    private java.util.Date updateWhen;
    private User updatedWho;
    private Metadata metadata;
    private int deleted;
    private String note;
        
    /** Creates a new instance of OccurrenceRecord */
    public Occurrence() {
        
    }        
    
    /**
     *   Get occurrence id
     *   @return id of the occurrence
     *   @see setId
     */
    public int getId() {
        return this.id;
    }
    
    /**
     *   Set occurrence id
     *   @param id id of the occurrence
     *   @see getId
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     *   Get unique identificator of the database
     *   @return unique identificator of the database
     *   @see setUnitIdDb
     */
    public String getUnitIdDb() {
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
    public int getYearCollected() {
        return this.yearCollected;
    }
    
    /**
     *   Set year when plant was collected
     *   @param yearCollected year when plant was collected
     *   @see getYearCollected
     */
    public void setYearCollected(int yearCollected) {
        this.yearCollected = yearCollected;
    }
    
    /**
     *   Get month when plant was collected
     *   @return month when plant was collected
     *   @see setMonthCollected
     */
    public int getMonthCollected() {
        return this.monthCollected;
    }
    
    /**
     *   Set month when plant was collected
     *   @param monthCollected year when plant was collected
     *   @see getMonthCollected
     */
    public void setMonthCollected(int monthCollected) {
        this.monthCollected = monthCollected;
    }
    
    /**
     *   Get day when plant was collected
     *   @return day when plant was collected
     *   @see setDayCollected
     */
    public int getDayCollected() {
        return this.dayCollected;
    }
    
    /**
     *   Set day when plant was collected
     *   @param dayCollected year when plant was collected
     *   @see getDayCollected
     */
    public void setDayCollected(int dayCollected) {
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
     *   Get associated publication record
     *   @return associated publication record
     *   @see setPublication
     */
    public Publication getPublication() {
        return this.publication;
    }
    
    /**
     *   Set associated publication record
     *   @param publication associated publication record
     *   @see getPublication
     */
    public void setPublicationsId(Publication publication) {
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
     *   @see setCreateWhen
     */
    public java.util.Date getCreateWhen() {
        return this.createWhen;
    }
    
    /**
     *   Set date and time when the occurrence record was inserted into database
     *   @param createWhen date and time when the occurrence record was inserted into database
     *   @see getCreateWhen
     */
    public void setCreateWhen(java.util.Date createWhen) {
        this.createWhen = createWhen;
    }
    
    /**
     *   Get date and time when the occurrence record was last updated
     *   @return date and time when the occurrence record was last updated
     *   @see setUpdateWhen
     */
    public java.util.Date getUpdateWhen() {
        return this.updateWhen;
    }
    
    /**
     *   Set date and time when the occurrence record was last updated
     *   @param updateWhen date and time when the occurrence record was last updated
     *   @see getUpdateWhen
     */
    public void setUpdateWhen(java.util.Date updateWhen) {
        this.updateWhen = updateWhen;
    }
    
    /**
     *   Get associated User record with the user who created the record
     *   @return associated User record with the user who created the record
     *   @see setCreateWho
     */
    public User getCreateWho() {
        return this.createWho;
    }
    
    /**
     *   Set associated User record with the user who created the record
     *   @param createWho associated User record with the user who created the record
     *   @see getCreateWho
     */
    public void setCreateWho(User createWho) {
        this.createWho = createWho;
    }
    
    /**
     *   Get associated User record with the user who last updated the record
     *   @return associated User record with the user who last updated the record
     *   @see setUpdateWho
     */
    public User getUpdateWho() {
        return this.updateWho;
    }
    
    /**
     *   Set associated User record with the user who last updated the record
     *   @param updateWho associated User record with the user who last updated the record
     *   @see getUpdateWho
     */
    public void setUpdateWho(User updateWho) {
        this.updateWho = updateWho;
    }
    
    /**
     *   Get associated Metadata record
     *   @return associated Metadata record
     *   @see setMetadata
     */
    public Metadata getMetadata() {
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
    public int getDeleted() {
        return this.deleted;
    }
    
    /**
     *   Set flag telling whether the record has been deleted
     *   @param deleted flag telling whether the record has been deleted. Value 1 means deleted.
     *   @see getDeleted
     */
    public void setDeleted(int deleted) {
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
     *   Set note about occurrence
     *   @param contact string containing note about occurrence
     *   @see getNote
     */
    public void setNote(String note) {
        this.note = note;
    }   
}
