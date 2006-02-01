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
public class Publications {
	
	 /** Unique identifier of a record */
	 private Integer id;
	   
	 /** Fields of the record*/
	 private String collectionName;
	 private String journalName;
	 private String journalAuthorName;
	 private int collectionYearPublication;
	 private String referenceCitation;
	 private String referenceDetail;
	 private String URL;
	   
	 /**
	 *   Default constructor to create new class Publications
	 */
	  public Publications() {
	   
	  }
	
	  /**
	   *   Get publication id
	   *   @return id of the publication
	   *   @see setId
	   */
	  public Integer getId() {
		 return this.id;
	  }
	
	  /**
	   *   Set publication id
	   *   @param id   id of the publication
	   *   @see getId
	   */
	  public void setId(Integer id) {
		 this.id = id;
	  }
	
	  /**
	   *   Get name of the collection
	   *   @return name of the collection
	   *   @see setCollectionName
	   */
	  public String getCollectionName() {
		 return this.collectionName;
	  }
	
	  /**
	   *   Set name of the collection
	   *   @param collectionName name of the collection
	   *   @see getCollectionName
	   */
	  public void setCollectionName(String collectionName) {
		 this.collectionName = collectionName;
		
	  }
	  	 
       /**
	   *   Get name of the journal
	   *   @return name of the journal
	   *   @see setJournalName
	   */
	  public String getJournalName() {
		 return this.journalName;
	  }

	  /**
	   *   Set name of the journal
	   *   @param journalName name of the journal
	   *   @see getJournalName
	   */
	  public void setJournalName(String journalName) {
		 this.journalName = journalName;
	  }

	  /**
	   *   Get author of the journal
	   *   @return author of the journal
	   *   @see setJournalAuthorName
	   */
	  public String getJournalAuthorName() {
		 return this.journalAuthorName;
	  }

	  /**
	   *   Set author of the journal
	   *   @param journalAuthorName author of the journal
	   *   @see getJournalAuthorName
	   */
	  public void setJournalAuthorName(String journalAuthorName) {
		 this.journalAuthorName = journalAuthorName;
	  }
		 
	  /**
	   *   Get year when the collection was publicated
	   *   @return year when the collection was publicated
	   *   @see setCollectionYearPublication
	   */
	  public int getCollectionYearPublication() {
		 return this.collectionYearPublication;
	  }

	  /**
	   *   Set year when the collection was publicated
	   *   @param collectionYearPublication year when the collection was publicated
	   *   @see getCollectionYearPublication
	   */
	  public void setCollectionYearPublication(int collectionYearPublication) {
		 this.collectionYearPublication = collectionYearPublication;
	  }	

	  /**
	   *   Get reference citation
	   *   @return reference citation
	   *   @see setReferenceCitation
	   */
	  public String getReferenceCitation() {
		 return this.referenceCitation;
	  }

	  /**
	   *   Set reference citation
	   *   @param referenceCitation string containing reference citation
	   *   @see getReferenceCitation
	   */
	  public void setReferenceCitation(String referenceCitation) {
		 this.referenceCitation = referenceCitation;
	  }

	  /**
	   *   Get reference detail
	   *   @return reference detail
	   *   @see setReferenceDetail
	   */
	  public String getReferenceDetail() {
		 return this.referenceDetail;
	  }

	  /**
	   *   Set reference detail
	   *   @param referenceDetail string containing reference detail
	   *   @see getReferenceDetail
	   */
	  public void setReferenceDetail(String referenceDetail) {
		 this.referenceDetail = referenceDetail;
	  }
		 
	  /**
	   *   Get URL of electronic publication
	   *   @return URL of electronic publication
	   *   @see setURL
	   */
	  public String getURL() {
		 return this.URL;
	  }

	  /**
	   *   Set URL of electronic publication
	   *   @param URL string containing URL of electronic publication
	   *   @see getURL
	   */
	  public void setURL(String URL) {
		 this.URL = URL;
	  }
		 	  
}
