/*
 * AuthorOccurence.java
 *
 * Created on 16. březen 2006, 1:08
 *
 */

package net.sf.plantlore.common.record;

import java.util.ArrayList;

/**
 *  Data holder object representing TAUTHORSOCCURENCES table in the DB. This object is used as a 
 *  data holder for Hibernate operations on the server side. On the side of the client, it 
 *  represents a AuthorOccurrence record we are currently working with. It is being sent from 
 *  client to server and back when executing database queries.
 *
 * @author Tomas Kovarik
 * @author Lada Oberreiterova
 */
public class AuthorOccurrence extends Record {
    /** Parameters of AuthorOccurence */
    private Integer id;
    private Author author;
    private Occurrence occurrence;
    private String role;
    private String resultRevision;
    private Integer deleted;
        
    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String AUTHOR = "author";    
    public static final String OCCURRENCE = "occurrence";
    public static final String ROLE = "role";
    public static final String RESULTREVISION = "resultRevision";
    public static final String DELETED = "deleted";
    
    @Override
    public ArrayList<String> getForeignKeys() { 
    	return list( AUTHOR, OCCURRENCE ); 
    }
    
    public ArrayList<String> getColumns() {
    	return list( AUTHOR, OCCURRENCE, ROLE, RESULTREVISION, DELETED );
    }
    
    @Override
    public void setValue(String column, Object value) {
		if(column.equals(ID)) setId((Integer)value);
		else if(column.equals(AUTHOR)) setAuthor((Author)value);
		else if(column.equals(OCCURRENCE)) setOccurrence((Occurrence)value);
		else if(column.equals(ROLE)) setRole((String)value);
		else if(column.equals(RESULTREVISION)) setResultRevision((String)value);
		else if(column.equals(DELETED)) setDeleted((Integer)value);
    }
    
    @Override
    public boolean isDead() {
    	return getDeleted() != 0;
    }
    
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
    public Integer getId() {
        return this.id;
    }
    
    /**
     *   Set AuthorOccurrence id
     *   @param id id of the AuthorOccurrence
     *   @see getId
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     *   Get associated Author object
     *   @return associated Author object
     *   @see setAuthor
     */
    public Author getAuthor() {
        return this.author;
    }
    
    /**
     *   Set associated Author object
     *   @param author associated Author object
     *   @see getAuthor
     */
    public void setAuthor(Author author) {
        this.author = author;
    }
    
    /**
     *   Get associated Occurrence object
     *   @return associated Occurrence object
     *   @see setOccurrence
     */
    public Occurrence getOccurrence() {
        return this.occurrence;
    }
    
    /**
     *   Set associated Occurrence object
     *   @param occurence associated Occurrence object
     *   @see getOccurrence
     */
    public void setOccurrence(Occurrence occurrence) {
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
    
}