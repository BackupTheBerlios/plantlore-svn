/*
 * Publication.java
 *
 * Created on 16. leden 2006, 2:32
 *
 */

package net.sf.plantlore.common.record;

import java.util.List;

/**
 *  Data holder object representing TPUBLICATIONS table in the DB. This object is used as a data holder
 *  for Hibernate operations on the server side. On the side of the client, it represents a publication
 *  we are currently working with. It is being sent from client to server and back when executing
 *  database queries.
 *
 *  @author Tomas Kovarik
 *  @version 0.1, Mar 15, 2006
 */
public class Publication extends Record implements Deletable  {
	
	private static final long serialVersionUID = 20060604024L;
	
    /** Parameters of the publication */    
    private Integer id;
    private String collectionName;
    private Integer collectionYearPublication;
    private String journalName;
    private String journalAuthorName;
    private String referenceCitation;
    private String referenceDetail;
    private String url;
    private Integer deleted = 0;
    private User createdWho;
    private String note;
    private Integer version;
    
    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String COLLECTIONNAME = "collectionName";    
    public static final String COLLECTIONYEARPUBLICATION = "collectionYearPublication";    
    public static final String JOURNALNAME = "journalName";    
    public static final String JOURNALAUTHORNAME = "journalAuthorName";    
    public static final String REFERENCECITATION = "referenceCitation";    
    public static final String REFERENCEDETAIL = "referenceDetail";    
    public static final String URL = "url";    
    public static final String DELETED = "deleted";            
    public static final String CREATEDWHO = "createdWho";    
    public static final String NOTE = "note";
    
    //public enum Column {ID, COLLECTIONNAME, COLLECTIONYEARPUBLICATION, JOURNALNAME, JOURNALAUTHORNAME, REFERENCECITATION, REFERENCEDETAIL,
    //URL, DELETED, CREATEDWHO, NOTE};    
    
    /** Creates a new instance of PublicationRecord */
    public Publication() {
        
    }
    
    public List<String> getColumns() {
    	return asList( COLLECTIONNAME, COLLECTIONYEARPUBLICATION, JOURNALNAME,
    			JOURNALAUTHORNAME, REFERENCECITATION, REFERENCEDETAIL, URL, NOTE, DELETED );
    }
    
    public List<String> getNN() {
    	return asList(REFERENCECITATION);
    }
    
    @Override
    public void setValue(String column, Object value) {
    	if(value instanceof String && "".equals(value))
        	value = null;
    	
		if(column.equals(ID)) {
			if(value != null && value instanceof String)
				setId(Integer.parseInt((String)value));
			else
				setId((Integer)value);
		}
		else if(column.equals(COLLECTIONNAME)) setCollectionName((String)value);
		else if(column.equals(COLLECTIONYEARPUBLICATION)) { 
			if (value != null && value instanceof String)
				setCollectionYearPublication(Integer.parseInt((String) value));
			else 
				setCollectionYearPublication((Integer)value);
		}
		else if(column.equals(JOURNALNAME)) setJournalName((String)value);
		else if(column.equals(JOURNALAUTHORNAME)) setJournalAuthorName((String)value);
		else if(column.equals(REFERENCECITATION)) setReferenceCitation((String)value);
		else if(column.equals(REFERENCEDETAIL)) setReferenceDetail((String)value);
		else if(column.equals(URL)) setUrl((String)value);
		else if(column.equals(DELETED)) {
			if (value != null && value instanceof String) 
				setDeleted(Integer.parseInt((String) value));
			else 
				setDeleted((Integer)value);
		}
		else if(column.equals(CREATEDWHO)) setCreatedWho((User)value);                
		else if(column.equals(NOTE)) setNote((String)value);
    }
    
    @Override 
    public boolean isDead() {
    	Integer c = getDeleted();
    	if( c == null ) return false;
    	return c != 0;
    }
    
    /**
     *   Get publication id
     *   @return id of the publication
     *   @see setId
     */
    public Integer getId() {
        //obligatory
        return this.id;
    }
    
    /**
     *   Set publication id
     *   @param id id of the publication
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
     *   Get name of the collection
     *   @return name of the collection, returns "" instead of null
     *   @see setCollectionName
     */
    public String getCollectionNameNN() {
         if (this.collectionName == null)
             return "";
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
    public Integer getCollectionYearPublication() {
        return this.collectionYearPublication;        
    }
    
    /**
     *   Get year when the collection was published
     *   @return year when the collection was published, returns -1 instead of null
     *   @see setCollectionYearPublication
     */
    public Integer getCollectionYearPublicationNN() {
        
        if (this.collectionYearPublication == null)
             return -1;
        return this.collectionYearPublication;        
    }
    
    /**
     *   Set year when the collection was published
     *   @param collectionYearPublication year when the collection was published
     *   @see getCollectionYearPublication
     */
    public void setCollectionYearPublication(Integer collectionYearPublication) {
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
     *   Get name of the journal
     *   @return name of the journal, returns "" instead of null
     *   @see setJournalName
     */
    public String getJournalNameNN() {
        
        if (this.journalName == null)
             return "";
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
     *   Get author of the journal
     *   @return author of the journal, returns "" instead of null
     *   @see setJournalAuthorName
     */
    public String getJournalAuthorNameNN() {
        
        if (this.journalAuthorName == null)
             return "";
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
        //obligatory
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
     *   Get reference detail
     *   @return reference detail, returns "" instead of null
     *   @see setReferenceDetail
     */
    public String getReferenceDetailNN() {
        if (this.referenceDetail == null)
            return "";
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
     *   Get URL of electronic publication
     *   @return URL of electronic publication, returns "" instead of null
     *   @see setUrl
     */
    public String getUrlNN() {
        if (this.url == null)
            return "";
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
    public Integer getDeleted() {
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
     *  Get user who created this publication
     *  @return User who created this Publication
     *  @see setCreatedWho
     */
    public User getCreatedWho() {
        return this.createdWho;
    }
    
    /**
     *  Set user who created this Publication.
     *  @param createdWho user who created this Publication
     *  @see getCreatedWho
     */
    public void setCreatedWho(User createdWho) {
        this.createdWho = createdWho;
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
     *   Get note for the publication
     *   @return note for the publication, returns "" instead of null
     *   @see setNote
     */
    public String getNoteNN() {
        if (this.note == null)
            return "";
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
    
    /**
     *  Set the row version. Version column is used by Hibernate to implement optimistic locking.
     *  @param version version of the row
     */    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    /**
     *  Get the row version. Version column is used by Hibernate to implement optimistic locking.
     *  @return version of the row
     */
    public Integer getVersion() {
        return version;
    }        
}
