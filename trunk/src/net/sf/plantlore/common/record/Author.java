/*
 * Author.java
 *
 * Created on 16. leden 2006, 2:32
 */

package net.sf.plantlore.common.record;

import java.util.ArrayList;

/**
 *  Data holder object representing TAUTHORS table in the DB. This object is used as a data holder
 *  for Hibernate operations on the server side. On the side of the client, it represents an author
 *  we are currently working with. It is being sent from client to server and back when executing
 *  database queries.
 *
 *  @author Tomas Kovarik
 *  @version 0.1, Mar 14, 2006
 */
public class Author extends Record implements Deletable {
    /** Parameters of the author. For detailed explanation see data model documentation. */
    private Integer id;
    private String wholeName;
    private String organization;
    private String role;
    private String address;
    private String phoneNumber;
    private String email;
    private String url;
    private Integer deleted;    
    private String note;
    
    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String WHOLENAME= "wholeName";
    public static final String ORGANIZATION = "organization";
    public static final String ROLE = "role";    
    public static final String ADDRESS = "address";
    public static final String PHONENUMBER = "phoneNumber";
    public static final String EMAIL = "email";    
    public static final String URL = "url";
    public static final String NOTE = "note";    
    public static final String DELETED = "deleted";
    
    /** Creates a new instance of AuthorRecord */
    public Author() {
    }
    
    public ArrayList<String> getColumns() {
    	return list( WHOLENAME, ORGANIZATION, ROLE, ADDRESS, PHONENUMBER, EMAIL, URL, NOTE, DELETED );
    }

    @Override
    public void setValue(String column, Object value) {
    	if(column.equals(ID)) setId((Integer)value);
    	else if(column.equals(WHOLENAME)) setWholeName((String)value);
    	else if(column.equals(ORGANIZATION)) setOrganization((String)value);
    	else if(column.equals(ROLE)) setRole((String)value);
    	else if(column.equals(ADDRESS)) setAddress((String)value);
    	else if(column.equals(PHONENUMBER)) setPhoneNumber((String)value);
    	else if(column.equals(EMAIL)) setEmail((String)value);
    	else if(column.equals(URL)) setUrl((String)value);
    	else if(column.equals(NOTE)) setNote((String)value);
    	else if(column.equals(DELETED)) setDeleted((Integer)value);
	}
    
    @Override 
    public boolean isDead() {
    	return getDeleted() != 0;
    }
    
    /**
     *   Set unique id of the author
     *   @param id unique id of the author
     *   @see getID
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     *   Get unique id of the author
     *   @return int unique id of the user
     *   @see setID
     */
    public Integer getId() {
        return this.id;
    }
    
    /**
     *   Get whole name of the author
     *   @return string containing whole name of the author
     *   @see setWholeName
     */
    public String getWholeName() {
        return this.wholeName;
    }
    
    /**
     *   Set wholeName of the author
     *   @param wholeName string containing whole name of the author
     *   @see getWholeName
     */
    public void setWholeName(String wholeName) {
        this.wholeName = wholeName;
    }    
    
    /**
     *   Set organization the author belongs to
     *   @param organization string containing organization the author belongs to
     *   @see getOrganization
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    
    /**
     *   Get organization the author belongs to
     *   @return string containing organization the author belongs to
     *   @see setOrganization
     */
    public String getOrganization() {
        return this.organization;
    }
    
    /**
     *   Set role of the author
     *   @param role string containing role of the author
     *   @see getRole
     */
    public void setRole(String role) {
        this.role = role;
    }
    
    /**
     *   Get role of the author
     *   @return string representation of author's role
     *   @see setRole
     */
    public String getRole() {
        return this.role;
    }
    
    /**
     *   Get address of the author
     *   @return string containing address of the author
     *   @see setAddress
     */
    public String getAddress() {
        return this.address;
    }
    
    /**
     *   Set address of the author
     *   @param address string containing address of the author
     *   @see getAddress
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     *   Get phone number of the author
     *   @return string containing phone number of the author
     *   @see setPhoneNumber
     */
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    
    /**
     *   Set email of the author
     *   @param phoneNumber string containing phone number of the author
     *   @see getPhoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    /**
     *   Get email of the author
     *   @return string containing email of the author
     *   @see setEmail
     */
    public String getEmail() {
        return this.email;
    }
    
    /**
     *   Set email of the author
     *   @param email string containing email of the author
     *   @see getEmail
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     *   Get URL of the author
     *   @return URL of the author's webpage
     *   @see setUrl
     */
    public String getUrl() {
        return this.url;
    }
    
    /**
     *   Set URL of the author
     *   @param url URL of the author's webpage
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
     *   Get note about the author
     *   @return string containing note about the author
     *   @see setNote
     */
    public String getNote() {
        return this.note;
    }
    
    /**
     *   Set note about the author
     *   @param contact string containing note about the author
     *   @see getNote
     */
    public void setNote(String note) {
        this.note = note;
    }
}
