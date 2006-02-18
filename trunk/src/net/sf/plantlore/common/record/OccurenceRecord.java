/*
 * OccurenceRecord.java
 *
 * Created on 23. leden 2006, 21:46
 *
 */

package net.sf.plantlore.common.record;

import java.util.ArrayList;
import java.util.Date;

/**
 *  Data holder object containing information about an occurence
 *
 *  @author Jakub Kotowski
 *  @author Tomas Kovarik
 *  @version 0.1, Feb 7, 2006
 */
public class OccurenceRecord
{
    /*******************************/    
    /* Fields of TOCCURENCES table */
    /*******************************/
    private int occurenceId;
    private String globalUniqueIdentifier;
    private int yearCollected;
    private int monthCollected;
    private int dayCollected;
    private java.util.Date timeCollected;
    private String dateSource;
    private PublicationRecord publication;      // Holder object
    private String herbarium;
    private ArrayList authors;                  // ArrayList of holder objects
    private java.util.Date createWhen;
    private UserRecord createdWho;              // Holder object
    private java.util.Date updateWhen;
    private UserRecord updatedWho;              // Holder object
    private java.util.Date isoDateTimeBegin;
    private String unitIdDb;
    private String unitValue;
    private String note;
        
    /*******************************/    
    /* Fields of TMETADATA table */
    /*******************************/
    private int metadataId;
    private String technicalContactName;
    private String technicalContactAddress;
    private String technicalContactEmail;
    private String contentContactName;
    private String contentContactAddress;
    private String contentContactEmail;
    private String dataSetTitle;
    private String dataSetDetails;
    private String sourceInstitutionId;
    private String sourceId;
    private String ownerOrganizationAbbrev;
    private String language;
    private String recordBasis;
    private java.util.Date dateCreate;
    private java.util.Date dateModified;

    
    /*******************************/    
    /* Fields of TPLANTS table */
    /*******************************/
    private int plantId;
    private String surveyTaxId;
    private String adoptedName;
    private String czechName;
    private String publishableName;
    private String genus;
    private String scientificNameAuthor;
    private String synonyms;
    private String plantNote;

    /*******************************/    
    /* Fields of THABITATS table */
    /*******************************/
    private int habitatId;
    private String quadrant;
    private String description;
    private String country;
    private double altitude;
    private double latitude;
    private double longitude;
    private String habitatNote;    

    /*******************************/    
    /* Fields of TVILLAGES table */
    /*******************************/
    private int villageId;
    private String villageName;
    
    /*******************************/    
    /* Fields of TPHYTOCHORIA table */
    /*******************************/
    private int phytochoriaId;
    private String phytochoriaCode;
    private String phytochoriaName;
    
    /*******************************/    
    /* Fields of TTERRITORIES table */
    /*******************************/
    private int territoryId;
    private String territoryName;
       
        
    /** Creates a new instance of OccurenceRecord */
    public OccurenceRecord() {
    }

    
    /*******************************/    
    /* TOCCURENCES getters/setters */
    /*******************************/    
    
    public int getoccurenceId() {
        return occurenceId;
    }

    public void setoccurenceId(int occurenceId) {
        this.occurenceId = occurenceId;
    }

    public String getGlobalUniqueIdentifier() {
        return globalUniqueIdentifier;
    }

    public void setGlobalUniqueIdentifier(String globalUniqueIdentifier) {
        this.globalUniqueIdentifier = globalUniqueIdentifier;
    }

    public int getYearCollected() {
        return yearCollected;
    }

    public void setYearCollected(int yearCollected) {
        this.yearCollected = yearCollected;
    }

    public int getMonthCollected() {
        return monthCollected;
    }

    public void setMonthCollected(int monthCollected) {
        this.monthCollected = monthCollected;
    }

    public int getDayCollected() {
        return dayCollected;
    }

    public void setDayCollected(int dayCollected) {
        this.dayCollected = dayCollected;
    }

    public java.util.Date getTimeCollected() {
        return timeCollected;
    }

    public void setTimeCollected(java.util.Date timeCollected) {
        this.timeCollected = timeCollected;
    }

    public String getDateSource() {
        return dateSource;
    }

    public void setDateSource(String dateSource) {
        this.dateSource = dateSource;
    }

    public PublicationRecord getPublication() {
        return publication;
    }

    public void setPublication(PublicationRecord publication) {
        this.publication = publication;
    }

    public String getHerbarium() {
        return herbarium;
    }

    public void setHerbarium(String herbarium) {
        this.herbarium = herbarium;
    }

    public ArrayList getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList authors) {
        this.authors = authors;
    }

    public java.util.Date getCreateWhen() {
        return createWhen;
    }

    public void setCreateWhen(java.util.Date createWhen) {
        this.createWhen = createWhen;
    }

    public UserRecord getCreatedWho() {
        return createdWho;
    }

    public void setCreatedWho(UserRecord createdWho) {
        this.createdWho = createdWho;
    }

    public java.util.Date getUpdateWhen() {
        return updateWhen;
    }

    public void setUpdateWhen(java.util.Date updateWhen) {
        this.updateWhen = updateWhen;
    }

    public UserRecord getUpdatedWho() {
        return updatedWho;
    }

    public void setUpdatedWho(UserRecord updatedWho) {
        this.updatedWho = updatedWho;
    }

    public java.util.Date getIsoDateTimeBegin() {
        return isoDateTimeBegin;
    }

    public void setIsoDateTimeBegin(java.util.Date isoDateTimeBegin) {
        this.isoDateTimeBegin = isoDateTimeBegin;
    }

    public String getUnitIdDb() {
        return unitIdDb;
    }

    public void setUnitIdDb(String unitIdDb) {
        this.unitIdDb = unitIdDb;
    }

    public String getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(String unitValue) {
        this.unitValue = unitValue;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    /*******************************/    
    /* TMETADATA getters/setters   */
    /*******************************/    
    
    public int getmetadataId() {
        return metadataId;
    }

    public void setmetadataId(int metadataId) {
        this.metadataId = metadataId;
    }

    public String getTechnicalContactName() {
        return technicalContactName;
    }

    public void setTechnicalContactName(String technicalContactName) {
        this.technicalContactName = technicalContactName;
    }

    public String getTechnicalContactAddress() {
        return technicalContactAddress;
    }

    public void setTechnicalContactAddress(String technicalContactAddress) {
        this.technicalContactAddress = technicalContactAddress;
    }

    public String getTechnicalContactEmail() {
        return technicalContactEmail;
    }

    public void setTechnicalContactEmail(String technicalContactEmail) {
        this.technicalContactEmail = technicalContactEmail;
    }

    public String getContentContactName() {
        return contentContactName;
    }

    public void setContentContactName(String contentContactName) {
        this.contentContactName = contentContactName;
    }

    public String getContentContactAddress() {
        return contentContactAddress;
    }

    public void setContentContactAddress(String contentContactAddress) {
        this.contentContactAddress = contentContactAddress;
    }

    public String getContentContactEmail() {
        return contentContactEmail;
    }

    public void setContentContactEmail(String contentContactEmail) {
        this.contentContactEmail = contentContactEmail;
    }

    public String getDataSetTitle() {
        return dataSetTitle;
    }

    public void setDataSetTitle(String dataSetTitle) {
        this.dataSetTitle = dataSetTitle;
    }

    public String getDataSetDetails() {
        return dataSetDetails;
    }

    public void setDataSetDetails(String dataSetDetails) {
        this.dataSetDetails = dataSetDetails;
    }

    public String getSourceInstitutionId() {
        return sourceInstitutionId;
    }

    public void setSourceInstitutionId(String sourceInstitutionId) {
        this.sourceInstitutionId = sourceInstitutionId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getOwnerOrganizationAbbrev() {
        return ownerOrganizationAbbrev;
    }

    public void setOwnerOrganizationAbbrev(String ownerOrganizationAbbrev) {
        this.ownerOrganizationAbbrev = ownerOrganizationAbbrev;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getRecordBasis() {
        return recordBasis;
    }

    public void setRecordBasis(String recordBasis) {
        this.recordBasis = recordBasis;
    }

    public java.util.Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(java.util.Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public java.util.Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(java.util.Date dateModified) {
        this.dateModified = dateModified;
    }

    /*******************************/    
    /* TPLANTS getters/setters     */
    /*******************************/
    
    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public String getSurveyTaxId() {
        return surveyTaxId;
    }

    public void setSurveyTaxId(String surveyTaxId) {
        this.surveyTaxId = surveyTaxId;
    }

    public String getAdoptedName() {
        return adoptedName;
    }

    public void setAdoptedName(String adoptedName) {
        this.adoptedName = adoptedName;
    }

    public String getCzechName() {
        return czechName;
    }

    public void setCzechName(String czechName) {
        this.czechName = czechName;
    }

    public String getPublishableName() {
        return publishableName;
    }

    public void setPublishableName(String publishableName) {
        this.publishableName = publishableName;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getScientificNameAuthor() {
        return scientificNameAuthor;
    }

    public void setScientificNameAuthor(String scientificNameAuthor) {
        this.scientificNameAuthor = scientificNameAuthor;
    }

    public String getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }

    public String getPlantNote() {
        return plantNote;
    }

    public void setPlantNote(String plantNote) {
        this.plantNote = plantNote;
    }

    /*******************************/    
    /* THABITATS getters/setters   */
    /*******************************/
    
    public int getHabitatId() {
        return habitatId;
    }

    public void setHabitatId(int habitatId) {
        this.habitatId = habitatId;
    }

    public String getQuadrant() {
        return quadrant;
    }

    public void setQuadrant(String quadrant) {
        this.quadrant = quadrant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getHabitatNote() {
        return habitatNote;
    }

    public void setHabitatNote(String habitatNote) {
        this.habitatNote = habitatNote;
    }

    /*******************************/    
    /* TVILLAGES getters/setters   */
    /*******************************/
    
    public int getVillageId() {
        return villageId;
    }

    public void setVillageId(int villageId) {
        this.villageId = villageId;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    /********************************/    
    /* TPHYTOCHORIA getters/setters */
    /********************************/
    
    public int getPhytochoriaId() {
        return phytochoriaId;
    }

    public void setPhytochoriaId(int phytochoriaId) {
        this.phytochoriaId = phytochoriaId;
    }

    public String getPhytochoriaCode() {
        return phytochoriaCode;
    }

    public void setPhytochoriaCode(String phytochoriaCode) {
        this.phytochoriaCode = phytochoriaCode;
    }

    public String getPhytochoriaName() {
        return phytochoriaName;
    }

    public void setPhytochoriaName(String phytochoriaName) {
        this.phytochoriaName = phytochoriaName;
    }

    /********************************/    
    /* TTERRITORIES getters/setters */
    /********************************/
    
    public int getTerritoryId() {
        return territoryId;
    }

    public void setTerritoryId(int territoryId) {
        this.territoryId = territoryId;
    }

    public String getTerritoryName() {
        return territoryName;
    }

    public void setTerritoryName(String territoryName) {
        this.territoryName = territoryName;
    }    
}
