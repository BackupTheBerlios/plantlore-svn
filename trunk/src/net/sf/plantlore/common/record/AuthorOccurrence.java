/*
 * AuthorOccurence.java
 *
 * Created on 16. březen 2006, 1:08
 *
 */

package net.sf.plantlore.common.record;

/**
 *  Data holder object representing TAUTHORSOCCURENCES table in the DB. This object is used as a 
 *  data holder for Hibernate operations on the server side. On the side of the client, it 
 *  represents a AuthorOccurrence record we are currently working with. It is being sent from 
 *  client to server and back when executing database queries.
 *
 * @author Tomas Kovarik
 * @author Lada Oberreiterova
 */
public class AuthorOccurrence {
    /** Parameters of AuthorOccurence */
    private int id;
    private int author;
    private int occurrence;
    private String role;
    private String resultRevision;
        
    /**
     *   Default constructor to create new class AuthorOccurrences
     */
    public AuthorOccurrence() {
        
    }
    
    /**
     *   Get AuthorOccurrence id
     *   @return id of the AuthorOccurrence
     *   @see setId
     */
    public int getId() {
        return this.id;
    }
    
    /**
     *   Set AuthorOccurrence id
     *   @param id id of the AuthorOccurrence
     *   @see getId
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     *   Get associated Author object
     *   @return associated Author object
     *   @see setAuthor
     */
    public int getAuthor() {
        return this.author;
    }
    
    /**
     *   Set associated Author object
     *   @param author associated Author object
     *   @see getAuthor
     */
    public void setAuthor(int author) {
        this.author = author;
    }
    
    /**
     *   Get associated Occurrence object
     *   @return associated Occurrence object
     *   @see setOccurrence
     */
    public int getOccurrence() {
        return this.occurrence;
    }
    
    /**
     *   Set associated Occurrence object
     *   @param occurence associated Occurrence object
     *   @see getOccurrence
     */
    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }
    
    /**
     *   Get 
     *   @return 
     *   @see setRole
     */
    public String getRole() {
        return this.role;
    }
    
    /**
     *   Set
     *   @param role
     *   @see getRole
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     *   Get
     *   @return
     *   @see setResultRevision
     */
    public String getResultRevision() {
        return this.resultRevision;
    }
    
    /**
     *   Set
     *   @param resultRevision
     *   @see getResultRevision
     */
    public void setResultRevision(String resultRevision) {
        this.resultRevision = resultRevision;
    }    
}