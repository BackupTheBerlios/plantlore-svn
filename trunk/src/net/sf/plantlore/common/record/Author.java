/*
 * Author.java
 *
 * Created on 16. leden 2006, 2:32
 */

package net.sf.plantlore.common.record;

import java.io.Serializable;

/**
 *  Data holder object representing TAUTHORS table in the DB. This object is used as a data holder
 *  for Hibernate operations on the server side. On the side of the client, it represents an author
 *  we are currently working with. It is being sent from client to server and back when executing
 *  database queries.
 *
 *  @author Tomas Kovarik
 *  @version 0.1, Mar 14, 2006
 */
public class Author implements Serializable {
    /** Parameters of the author. For detailed explanation see data model documentation. */
    private int id;
    private String firstName;
    private String surname;
    private String wholeName;
    private String organization;
    private String role;
    private String address;
    private String phoneNumber;
    private String email;
    private String url;
    private String note;
    
    /** Creates a new instance of AuthorRecord */
    public Author() {
        
    }
    
    /**
     *   Set unique id of the author
     *   @param id unique id of the author
     *   @see getID
     */
    public void setID(int id) {
        this.id = id;
    }
    
    /**
     *   Get unique id of the author
     *   @return int unique id of the user
     *   @see setID
     */
    public int getID() {
        return this.id;
    }
    
    /**
     *   Get first name of the author
     *   @return string containing the first name of the author
     *   @see setFirstName
     */
    public String getFirstName() {
        return this.firstName;
    }
    
    /**
     *   Set first name of the author
     *   @param firstName string containing the first name of the author
     *   @see getFirstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     *   Get surname of the author
     *   @return string containing surname of the author
     *   @see setSurname
     */
    public String getSurname() {
        return this.surname;
    }
    
    /**
     *   Set surname of the author
     *   @param surname string containing surname of the author
     *   @see getSurname
     */
    public void setSurname(String surname) {
        this.surname = surname;
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
