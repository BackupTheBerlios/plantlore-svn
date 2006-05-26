/*
 * Metadata.java
 *
 * Created on 15. březen 2006, 0:15
 *
 */

package net.sf.plantlore.common.record;

import java.util.List;

/**
 *  Data holder object representing TMETADATA table in the DB. This object is used as a data
 *  holder for Hibernate operations on the server side. On the side of the client, it represents
 *  Metadata record we are working with. It is being sent from client to server and back when
 *  executing database queries.
 *
 * @author Tomas Kovarik
 * @author Lada Oberreiterova
 */
public class Metadata extends Record {
    /** Parameters of Metadata */
    private Integer id;
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
    private java.util.Date dateCreate;
    private java.util.Date dateModified;
    private String recordBasis;
    private String biotopeText;    
    private Integer deleted;
    
    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String TECHNICALCONTACTNAME = "technicalContactName";    
    public static final String TECHNICALCONTACTADDRESS = "technicalContactAddress";
    public static final String TECHNICALCONTACTEMAIL = "technicalContactEmail";    
    public static final String CONTENTCONTACTNAME = "contentContactName";    
    public static final String CONTENTCONTACTADDRESS = "contentContactAddress";    
    public static final String CONTENTCONTACTEMAIL = "contentContactEmail";
    public static final String DATASETTITLE = "dataSetTitle";    
    public static final String DATASETDETAILS = "dataSetDetails";    
    public static final String SOURCEINSTITUTIONID = "sourceInstitutionId";
    public static final String SOURCEID = "sourceId";
    public static final String OWNERORGANIZATIONABBREV = "ownerOrganizationAbbrev";
    public static final String DATECREATE = "dateCreate";    
    public static final String DATEMODIFIED = "dateModified";    
    public static final String RECORDBASIS = "recordBasis";
    public static final String BIOTOPETEXT = "biotopeText";    
    public static final String DELETED = "deleted";
    
    
    //public enum Column {ID, TECHNICALCONTACTNAME, TECHNICALCONTACTADDRESS, TECHNICALCONTACTEMAIL, CONTENTCONTACTNAME, CONTENTCONTACTADDRESS, CONTENTCONTACTEMAIL,
    //DATASETTITLE, DATASETDETAILS, SOURCEINSTITUTIONID, SOURCEID, OWNERORGANIZATIONABBREV, DATECREATE, DATEMODIFIED, RECORDBASIS, BIOTOPTEXT, DELETED};
    
    /**
     *   Default constructor to create new class Metadata
     */
    public Metadata() {
    }
    
    public List<String> getColumns() {
    	return asList( TECHNICALCONTACTADDRESS, TECHNICALCONTACTEMAIL, TECHNICALCONTACTNAME,
    			CONTENTCONTACTADDRESS, CONTENTCONTACTEMAIL, CONTENTCONTACTNAME,
    			DATASETDETAILS, DATASETTITLE, SOURCEID, SOURCEINSTITUTIONID,
    			OWNERORGANIZATIONABBREV, BIOTOPETEXT, RECORDBASIS, DELETED/*,
    			DATECREATE,*//* DATEMODIFIED*/ ); // Damn this table!
    }
    
    public List<String> getNN() {
    	return asList(TECHNICALCONTACTNAME, CONTENTCONTACTNAME, DATASETTITLE, 
    			SOURCEINSTITUTIONID, SOURCEID, DELETED/*, DATECREATE*//*, DATEMODIFIED*/);
    }
    
    @Override
    public void setValue(String column, Object value) {
        if(value instanceof String && "".equals(value))
        	value = null;
    	
		if(column.equals(ID)) setId((Integer)value);
		else if(column.equals(TECHNICALCONTACTADDRESS)) setTechnicalContactAddress((String)value);
		else if(column.equals(TECHNICALCONTACTEMAIL)) setTechnicalContactEmail((String)value);
		else if(column.equals(TECHNICALCONTACTNAME)) setTechnicalContactName((String)value);
		else if(column.equals(CONTENTCONTACTADDRESS)) setContentContactAddress((String)value);
		else if(column.equals(CONTENTCONTACTEMAIL)) setContentContactEmail((String)value);
		else if(column.equals(CONTENTCONTACTNAME)) setContentContactName((String)value);
		else if(column.equals(DATASETDETAILS)) setDataSetDetails((String)value);
		else if(column.equals(DATASETTITLE)) setDataSetTitle((String)value);
		else if(column.equals(SOURCEID)) setSourceId((String)value);
		else if(column.equals(SOURCEINSTITUTIONID)) setSourceInstitutionId((String)value);
		else if(column.equals(OWNERORGANIZATIONABBREV)) setOwnerOrganizationAbbrev((String)value);
		else if(column.equals(BIOTOPETEXT)) setBiotopeText((String)value);
		else if(column.equals(RECORDBASIS)) setRecordBasis((String)value);
		else if(column.equals(DELETED)) {
			if (value != null && value instanceof String) 
				setDeleted(Integer.parseInt((String) value));
			else 
				setDeleted((Integer)value);
		}
		else if(column.equals(DATECREATE)) setDateCreate((java.util.Date)value);
		else if(column.equals(DATEMODIFIED)) setDateModified((java.util.Date)value);
    }
    
    /**
     *   Get metadata id
     *   @return id of the metadata
     *   @see setId
     */
    public Integer getId() {
        //obligatory
        return this.id;
    }
    
    /**
     *   Set metadata id
     *   @param id id of the metadata
     *   @see getId
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     *   Get technical contact name
     *   @return technical contact name
     *   @see setTechnicalContactName
     */
    public String getTechnicalContactName() {
        //obligatory
        return this.technicalContactName;
    }
    
    /**
     *   Set technical contact name
     *   @param technicalContactName string containing technical contact name
     *   @see getTechnicalContactName
     */
    public void setTechnicalContactName(String technicalContactName) {
        this.technicalContactName = technicalContactName;
    }
    
    /**
     *   Get technical contact address
     *   @return technical contact address
     *   @see setTechnicalContactAddress
     */
    public String getTechnicalContactAddress() {
        return this.technicalContactAddress;
    }
    
    /**
     *   Get technical contact address
     *   @return technical contact address, return "" instead of null
     *   @see setTechnicalContactAddress
     */
    public String getTechnicalContactAddressNN() {
        if (this.technicalContactAddress == null)
            return "";
        return this.technicalContactAddress;
    }
    
    /**
     *   Set technical contact address
     *   @param technicalContactAddress string containing technical contact address
     *   @see getTechnicalContactAddress
     */
    public void setTechnicalContactAddress(String technicalContactAddress) {
        this.technicalContactAddress = technicalContactAddress;
    }
    
    /**
     *   Get technical contact email address
     *   @return technical contact email address
     *   @see setTechnicalContactEmail
     */
    public String getTechnicalContactEmail() {
        return this.technicalContactEmail;
    }
    
    /**
     *   Get technical contact email address
     *   @return technical contact email address, returns "" instead of null
     *   @see setTechnicalContactEmail
     */
    public String getTechnicalContactEmailNN() {
        if (this.technicalContactEmail == null)
            return "";
        return this.technicalContactEmail;
    }
    
    /**
     *   Set technical contact email address
     *   @param technicalContactEmail string containing technical contact email address
     *   @see getTechnicalContactEmail
     */
    public void setTechnicalContactEmail(String technicalContactEmail) {
        this.technicalContactEmail = technicalContactEmail;
    }
    
    /**
     *   Get content contact name
     *   @return content contact name
     *   @see setContentContactName
     */
    public String getContentContactName() {
        //obligatory
        return this.contentContactName;
    }
    
    /**
     *   Set content contact name
     *   @param contentContactName string containing content contact name
     *   @see getContentContactName
     */
    public void setContentContactName(String contentContactName) {
        this.contentContactName = contentContactName;
    }
    
    /**
     *   Get content contact address
     *   @return content contact address
     *   @see setContentContactAddress
     */
    public String getContentContactAddress() {
        return this.contentContactAddress;
    }
    
    /**
     *   Get content contact address
     *   @return content contact address, returns "" instead of null
     *   @see setContentContactAddress
     */
    public String getContentContactAddressNN() {
        if (this.contentContactAddress == null)
            return "";
        return this.contentContactAddress;
    }
    
    /**
     *   Set content contact address
     *   @param contentContactAddress string containing content contact address
     *   @see getContentContactAddress
     */
    public void setContentContactAddress(String contentContactAddress) {
        this.contentContactAddress = contentContactAddress;
    }
    
    /**
     *   Get content contact email address
     *   @return content contact email address
     *   @see setContentContactEmail
     */
    public String getContentContactEmail() {
        return this.contentContactEmail;
    }
    
    /**
     *   Get content contact email address
     *   @return content contact email address, returns "" instead of null
     *   @see setContentContactEmail
     */
    public String getContentContactEmailNN() {
        if (this.contentContactEmail == null)
            return "";
        return this.contentContactEmail;
    }
    
    /**
     *   Set content contact email address
     *   @param contentContactEmail string containing content contact email address
     *   @see getContentContactEmail
     */
    public void setContentContactEmail(String contentContactEmail) {
        this.contentContactEmail = contentContactEmail;
    }
        
    /**
     *   Get concise title of the project
     *   @return concise title of the project
     *   @see setDataSetTitle
     */
    public String getDataSetTitle() {
        //obligatory
        return this.dataSetTitle;
    }
    
    /**
     *   Set concise title of the project
     *   @param dataSetTitle string containing concise title of the project
     *   @see getDataSetTitle
     */
    public void setDataSetTitle(String dataSetTitle) {
        this.dataSetTitle = dataSetTitle;
    }
    
    /**
     *   Get free-form text containing a longer description of the project
     *   @return longer description of the project
     *   @see setDataSetDetails
     */
    public String getDataSetDetails() {
        return this.dataSetDetails;
    }
    
    /**
     *   Get free-form text containing a longer description of the project
     *   @return longer description of the project, returns "" instead of null
     *   @see setDataSetDetails
     */
    public String getDataSetDetailsNN() {
        if (this.dataSetDetails == null)
            return "";
        return this.dataSetDetails;
    }
    
    /**
     *   Set free-form text containing a longer description of the project
     *   @param dataSetDetails string containing longer description of the project
     *   @see getDataSetDetails
     */
    public void setDataSetDetails(String dataSetDetails) {
        this.dataSetDetails = dataSetDetails;
    }
    
    /**
     *   Get unique identifier (code or name) of the institution holding the original data source
     *   @return unique identifier of the institution holding the original data source.
     *   @see setSourceInstitutionId
     */
    public String getSourceInstitutionId() {
        //obligatory
        return this.sourceInstitutionId;
    }
    
    /**
     *   Set unique identifier (code or name) of the institution holding the original data source
     *   @param sourceInstitutionId string containing unique identifier of the institution holding the original data source
     *   @see getSourceInstitutionId
     */
    public void setSourceInstitutionId(String sourceInstitutionId) {
        this.sourceInstitutionId = sourceInstitutionId;
    }    
    
    /**
     *   Get name or code of the data source
     *   @return name or code of the data source
     *   @see setSourceId
     */
    public String getSourceId() {
        //obligatory
        return this.sourceId;
    }
    
    /**
     *   Set name or code of the data source
     *   @param sourceId string containing name or code of the data source
     *   @see getTechnicalContactName
     */
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
        
    /**
     *   Get abbreviation of the owner's organization. Restricted to 50 characters, including blanks.
     *   @return abbreviation of the owner's organization
     *   @see setOwnerOrganizationAbbrev
     */
    public String getOwnerOrganizationAbbrev() {
        return this.ownerOrganizationAbbrev;
    }
    
    /**
     *   Get abbreviation of the owner's organization. Restricted to 50 characters, including blanks.
     *   @return abbreviation of the owner's organization, returns "" instead of null
     *   @see setOwnerOrganizationAbbrev
     */
    public String getOwnerOrganizationAbbrevNN() {
        if (this.ownerOrganizationAbbrev == null)
            return "";
        return this.ownerOrganizationAbbrev;
    }
    
    /**
     *   Set abbreviation of the owner's organization. Restricted to 50 characters, including blanks.
     *   @param ownerOrganizationAbbrev string containing abbreviation of the owner's organization
     *   @see getOwnerOrganizationAbbrev
     */
    public void setOwnerOrganizationAbbrev(String ownerOrganizationAbbrev) {
        this.ownerOrganizationAbbrev = ownerOrganizationAbbrev;
    }
           
    /**
     *   Get indication of what the unit record describes
     *   @return indication of what the unit record describes
     *   @see setRecordBasis
     */
    public String getRecordBasis() {
        return this.recordBasis;
    }
    
    /**
     *   Get indication of what the unit record describes
     *   @return indication of what the unit record describes, returns "" instead of null
     *   @see setRecordBasis
     */
    public String getRecordBasisNN() {
        if (this.recordBasis == null)
            return "";
        return this.recordBasis;
    }
    
    /**
     *   Set indication of what the unit record describes
     *   @param recordBasis string containing indication of what the unit record describes
     *   @see getRecordBasis
     */
    public void setRecordBasis(String recordBasis) {
        this.recordBasis = recordBasis;
    }
        
    /**
     *   Get date/time when the intellectual content (project, term, description, etc.) was created
     *   @return date/time when the intellectual content was created
     *   @see setDateCreate
     */
    public java.util.Date getDateCreate() {
        //obligatory
        return this.dateCreate;
    }
    
    /**
     *   Set date/time when the intellectual content was created
     *   @param dateCreate string containing date/time when the intellectual content was created
     *   @see getDateCreate
     */
    public void setDateCreate(java.util.Date dateCreate) {
        this.dateCreate = dateCreate;
    }
    
    /**
     *   Get date/time when the last modification of the object was made
     *   @return date/time when the last modification of the object was made
     *   @see setDateModified
     */
    public java.util.Date getDateModified() {
        //obligatory
        return this.dateModified;
    }
    
    /**
     *   Set date/time when the last modification of the object was made
     *   @param dateModified string containing date/time when the last modification of the object was made
     *   @see getDateModified
     */
    public void setDateModified(java.util.Date dateModified) {
        this.dateModified = dateModified;
    }
    
    /**
     *   Get
     *   @return
     *   @see setBiotopeText
     */
    public String getBiotopeText() {
        return this.biotopeText;
    }
    
    /**
     *   Get
     *   @return returns Biotope text, returns "" instead of null
     *   @see setBiotopeText
     */
    public String getBiotopeTextNN() {
        if (this.biotopeText == null)
            return "";
        return this.biotopeText;
    }
    
    /**
     *   Set
     *   @param biotopeText
     *   @see getBiotopeText
     */
    public void setBiotopeText(String biotopeText) {
        this.biotopeText = biotopeText;
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
}

