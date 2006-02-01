/*
 * PublicationRecord.java
 *
 * Created on 16. leden 2006, 2:32
 *
 */

package net.sf.plantlore.client.dblayer;

/**
 *  Data holder object containing information about a publication
 *
 *  @author Tomas Kovarik
 *  @version 0.1, Jan 16, 2006
 */
public class PublicationRecord {
    // Parameters of the publication
    private int id;
    private String collectionName;
    private int publicationYear;
    private String journalName;
    private String journalAuthor;
    
    /** Creates a new instance of PublicationRecord */
    public PublicationRecord() {
        
    }
    
    public void setID(int id) {
        this.id = id;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
    
    public String getCollectionName() {
        return this.collectionName;
    }
    
    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }
    
    public int getPublicationYear() {
        return this.publicationYear;
    }
    
    public void setJournalName(String journalName) {
        this.journalName = journalName;
    }
    
    public String getJournalName() {
        return this.journalName;
    }
    
    public void setJournalAuthor(String journalAuthor) {
        this.journalAuthor = journalAuthor;
    }
    
    public String getJournalAuthor() {
        return this.journalAuthor;
    }
}
