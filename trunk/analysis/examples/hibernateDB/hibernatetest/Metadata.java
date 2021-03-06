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
public class Metadata {

   /** Unique identifier of a record */
   private Integer id;
   
   /** Fields of the record*/
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
   
   /**
   *   Default constructor to create new class Metadata
   */
   public Metadata() {
   }

   /**
    *   Get metadata id
    *   @return id of the metadata
    *   @see setId
    */
   public Integer getId() {
 	 return this.id;
   }

   /**
    *   Set metadata id
    *   @param id   id of the metadata
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
	*   Get abbreviation of the owner's organization. Restricted to 50 characters maximum length, including blanks.
	*   @return abbreviation of the owner's organization
	*   @see setOwnerOrganizationAbbrev
	*/
   public String getOwnerOrganizationAbbrev() {
	 return this.ownerOrganizationAbbrev;
   }
 
   /**
	*   Set abbreviation of the owner's organization. Restricted to 50 characters maximum length, including blanks.
	*   @param ownerOrganizationAbbrev string containing abbreviation of the owner's organization
	*   @see getOwnerOrganizationAbbrev
	*/
   public void setOwnerOrganizationAbbrev(String ownerOrganizationAbbrev) {
	 this.ownerOrganizationAbbrev = ownerOrganizationAbbrev;
   }	   
   

   /**
	*   Get language  
	*   @return language
	*   @see setLanguage
	*/
   public String getLanguage() {
	 return this.language;
   }
 
   /**
	*   Set language
	*   @param language string containing used language
	*   @see getLanguage
	*/
   public void setLanguage(String language) {
	 this.language = language;
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
}
