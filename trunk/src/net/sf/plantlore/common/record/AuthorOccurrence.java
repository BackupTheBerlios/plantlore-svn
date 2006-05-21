/*
 * AuthorOccurence.java
 *
 * Created on 16. březen 2006, 1:08
 *
 */

package net.sf.plantlore.common.record;

import java.util.List;

/**
 *  Data holder object representing TAUTHORSOCCURENCES table in the DB. This object is used as a 
 *  data holder for Hibernate operations on the server side. On the side of the client, it 
 *  represents a AuthorOccurrence record we are currently working with. It is being sent from 
 *  client to server and back when executing database queries.
 *
 * @author Tomas Kovarik
 * @author Lada Oberreiterova
 */
public class AuthorOccurrence extends Record implements Deletable {
    /** Parameters of AuthorOccurence */
    private Integer id;
    private Author author;
    private Occurrence occurrence;
    private String role;
    private String note;
    private Integer deleted;
        
    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String AUTHOR = "author";    
    public static final String OCCURRENCE = "occurrence";
    public static final String ROLE = "role";
    public static final String NOTE = "note";
    public static final String DELETED = "deleted";
    
    //public enum Column {ID, AUTHOR, OCCURRENCE, ROLE, NOTE, DELETED }
    
    @Override
    public List<String> getForeignKeys() { 
    	return asList( AUTHOR, OCCURRENCE ); 
    }
    
    public List<String> getColumns() {
    	return asList( AUTHOR, OCCURRENCE, ROLE, NOTE, DELETED );
    }
    
    @Override
    public void setValue(String column, Object value) {
		if(column.equals(ID)) setId((Integer)value);
		else if(column.equals(AUTHOR)) setAuthor((Author)value);
		else if(column.equals(OCCURRENCE)) setOccurrence((Occurrence)value);
		else if(column.equals(ROLE)) setRole((String)value);
		else if(column.equals(NOTE)) setNote((String)value);
		else if(column.equals(DELETED)) setDeleted((Integer)value);
    }
    
    @Override
    public boolean isDead() {
    	Integer c = getDeleted();
    	if( c == null ) return false;
    	else return c != 0;
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
        //obligatory
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
        //obligatory
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
        //obligatory
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
        if ( this.role == null)
            return "";
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
     *   @return authors note or result revision
     *   @see setNote
     */
    public String getNote() {
        if (this.note == null)
            return "";
        return this.note;
    }
    
    /**
     *   Set
     *   @param authors note or resutl revision
     *   @see getNote
     */
    public void setNote(String note) {
        this.note = note;
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