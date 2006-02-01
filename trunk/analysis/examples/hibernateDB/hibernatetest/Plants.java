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
public class Plants {

   /** Unique identifier of a record */
   private Integer id;
   
   /** Fields of the record*/
   private String surveyTaxId;
   private String adoptedName;
   private String czechName;
   private String publishableName;
   private String genus;
   private String scientificNameAuthor;
   private String synonyms;
   private String note;
   
   /**
    *   Default constructor to create new class Plants
    */
   public Plants() {
   }

   /**
	*   Get plant id
	*   @return id of the plant
	*   @see setId
	*/
   public Integer getId() {
	  return this.id;
   }	 
	     
   
   /**
	*   Set plant id
	*   @param id   id of the plant
	*   @see getId
	*/
   public void setId(Integer id) {
	  this.id = id;
   }

	/**
    *   Get survey identifier of the plant 
    *   @return survey identifier of the plant 
    *   @see setSurveyTaxId
    */
   public String getSurveyTaxId() {
 	 return this.surveyTaxId;
   }
 
   /**
    *   Set survey identifier of the plant 
    *   @param surveyTaxId survey identifier of the plant 
    *   @see getSurveyTaxId
    */
   public void setSurveyTaxId(String surveyTaxId) {
  	 this.surveyTaxId = surveyTaxId;
   }	   
   
   /**
	*   Get adopted name of the plant
	*   @return adopted name of the plant 
	*   @see setAdoptedName
	*/
   public String getAdoptedName() {
	  return this.adoptedName;
   }

   /**
	*   Set adopted name of the plant
	*   @param adoptedName string containing adopted name of the plant
	*   @see getAdoptedName
	*/
   public void setAdoptedName(String adoptedName) {
	  this.adoptedName = adoptedName;
   }	
   
   /**
    *   Get czech name of the plant
    *   @return czech name of the plant 
    *   @see setCzechName
    */
   public String getCzechName() {
	 return this.czechName;
    }

   /**
    *   Set czech name of the plant
    *   @param czechName string containing czech name of the plant
    *   @see getCzechName
    */
   public void setCzechName(String czechName) {
	 this.czechName = czechName;
   }	
	  
   /**
    *   Get publishable name of the plant
    *   @return publishable name of the plant 
    *   @see setPublishableName
    */
   public String getPublishableName() {
	 return this.publishableName;
   }

   /**
    *   Set publishable name of the plant
    *   @param publishableName string containing publishable name of the plant
    *   @see getPublishableName
    */
   public void setPublishableName(String publishableName) {
  	 this.publishableName = publishableName;
   }		
	   
   /**
    *   Get genus of the plant
    *   @return genus of the plant
    *   @see setGenus
    */
   public String getGenus() {
	 return this.genus;
   }

   /**
   *   Set genus of the plant
   *   @param genus string containing genus of the plant
   *   @see getGenus
   */
   public void setGenus(String genus) {
	 this.genus = genus;
   }	

   /**
    *   Get scientific name of author
    *   @return scientific name of author
    *   @see setScientificNameAuthor
    */
   public String getScientificNameAuthor() {
	 return this.scientificNameAuthor;
   }

   /**
   *   Set scientific name of author
   *   @param scientificNameAuthor string containin scientific name of author
   *   @see getScientificNameAuthor
   */
   public void setScientificNameAuthor(String scientificNameAuthor) {
	 this.scientificNameAuthor = scientificNameAuthor;
   }	
   
   /**
    *   Get synonyms of the plant
    *   @return synonyms of the plant
    *   @see setSynonyms
    */
   public String getSynonyms() {
	 return this.synonyms;
   }

   /**
   *   Set synonyms of the plant
   *   @param synonyms string containing synonyms of the plant
   *   @see getSynonyms
   */
	   public void setSynonyms(String synonyms) {
		 this.synonyms = synonyms;
	   }	
   
   /**
    *   Get note about the plant
    *   @return string containing note about the plant
    *   @see setNote
    */      
   public String getNote() {
	 return this.note;
   }
	
   /**
    *   Set note about the plant
	*   @param note string containing note about the plant
	*   @see getNote
	*/         
   public void setNote(String note) {
	   this.note = note;
   }    		   
}
