/*
 * Publication.java
 *
 * Created on 16. leden 2006, 2:32
 *
 */

package net.sf.plantlore.common.record;

/**
 *  Data holder object representing TPUBLICATIONS table in the DB. This object is used as a data holder
 *  for Hibernate operations on the server side. On the side of the client, it represents a publication
 *  we are currently working with. It is being sent from client to server and back when executing
 *  database queries.
 *
 *  @author Tomas Kovarik
 *  @version 0.1, Mar 15, 2006
 */
public class Publication {
    /** Parameters of the publication */    
    private int id;
    private String collectionName;
    private int collectionYearPublication;
    private String journalName;
    private String journalAuthorName;
    private String referenceCitation;
    private String referenceDetail;
    private String url;
    private int deleted;
    private String note;
    
    /** Creates a new instance of PublicationRecord */
    public Publication() {
        
    }
    /**
     *   Get publication id
     *   @return id of the publication
     *   @see setId
     */
    public int getId() {
        return this.id;
    }
    
    /**
     *   Set publication id
     *   @param id id of the publication
     *   @see getId
     */
    public void setId(int id) {
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
     *   Get year when the collection was published
     *   @return year when the collection was published
     *   @see setCollectionYearPublication
     */
    public int getCollectionYearPublication() {
        return this.collectionYearPublication;
    }
    
    /**
     *   Set year when the collection was published
     *   @param collectionYearPublication year when the collection was published
     *   @see getCollectionYearPublication
     */
    public void setCollectionYearPublication(int collectionYearPublication) {
        this.collectionYearPublication = collectionYearPublication;
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
     *   @see setUrl
     */
    public String getUrl() {
        return this.url;
    }
    
    /**
     *   Set URL of electronic publication
     *   @param url string containing URL of electronic publication
     *   @see getUrl
     */
    public void setUrl(String url) {
        this.url = url;
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
     *   Get note for the publication
     *   @return note for the publication
     *   @see setNote
     */
    public String getNote() {
        return this.note;
    }
    
    /**
     *   Set note for the publication
     *   @param note string containing note for the publication
     *   @see getNote
     */
    public void setNote(String note) {
        this.note = note;
    }    
}