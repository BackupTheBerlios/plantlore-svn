/*
 * User.java
 *
 * Created on 16. leden 2006, 2:32
 *
 */

package net.sf.plantlore.common.record;

/**
 *  Data holder object containing information about a user
 *
 *  @author Tomas Kovarik
 *  @version 0.1, Mar 15, 2006
 */
public class User {
    /** Parameters of the user */
    private int id;
    private String login;
    private String firstName;
    private String surname;
    private String wholeName;
    private String email;
    private String address;
    private java.util.Date whenCreated;
    private java.util.Date whenDropped;
    private Right right;
    private String note;
    
    /** Creates a new instance of UserRecord */
    public User() {
        
    }
    
    /**
     *   Get user id
     *   @return id of the user
     *   @see setID
     */
    public int getId() {
        return this.id;
    }
    
    /**
     *   Set user id
     *   @param id id of the user
     *   @see getID
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     *   Get login name of the user
     *   @return login name of the user
     *   @see setLogin
     */
    public String getLogin() {
        return this.login;
    }
    
    /**
     *   Set login name of the user
     *   @param login string containing login name of the user
     *   @see getLogin
     */
    public void setLogin(String login) {
        this.login = login;
    }
    
    /**
     *   Get first name of the user
     *   @return string containing the first name of the user
     *   @see setFirstName
     */
    public String getFirstName() {
        return this.firstName;
    }
    
    /**
     *   Set first name of the user
     *   @param firstName string containing the first name of the user
     *   @see getFirstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     *   Get surname of the user
     *   @return string containing surname of the user
     *   @see setSurname
     */
    public String getSurname() {
        return this.surname;
    }
    
    /**
     *   Set surname of the user
     *   @param surname string containing surname of the user
     *   @see getSurname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }
    
    /**
     *   Get whole name of the user
     *   @return string containing whole name of the user
     *   @see setWholeName
     */
    public String getWholeName() {
        return this.wholeName;
    }
    
    /**
     *   Set whole name of the user
     *   @param wholeName string containing whole name of the user
     *   @see getWholeName
     */
    public void setWholeName(String wholeName) {
        this.wholeName = wholeName;
    }
    
    /**
     *   Get email of the user
     *   @return string containing email of the user
     *   @see setEmail
     */
    public String getEmail() {
        return this.email;
    }
    
    /**
     *   Set email of the user
     *   @param email string containing email of the user
     *   @see getEmail
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     *   Get address of the user
     *   @return string containing address of the user
     *   @see setAddress
     */
    public String getAddress() {
        return this.address;
    }
    
    /**
     *   Set address of the user
     *   @param address string containing address of the user
     *   @see getAddress
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     *   Get date when the user was created
     *   @return date when the user was added to the system
     *   @see setWhenCreated
     */
    public java.util.Date getWhenCreated() {
        return this.whenCreated;
    }
    
    /**
     *   Set date when the user was created
     *   @param whenCreated date when the user was added to the system
     *   @see getWhenCreated
     */
    public void setWhenCreated(java.util.Date whenCreated) {
        this.whenCreated = whenCreated;
    }
    
    /**
     *   Get date when the user was dropped
     *   @return date when the user was deleted from the system
     *   @see setWhenDropped
     */
    public java.util.Date getWhenDropped() {
        return this.whenDropped;
    }
    
    /**
     *   Set date when the user was dropped
     *   @param whenDropped date when the user was deleted from the system
     *   @see getWhenDropped
     */
    public void setWhenDropped(java.util.Date whenDropped) {
        this.whenDropped = whenDropped;
    }

    /**
     *   Get record with the rights of the user
     *   @return record with the rights of the user
     *   @see setRight
     */
    public Right getRight() {
        return this.right;
    }
    
    /**
     *   Set record with the rights of the user
     *   @param right record with the rights of the user
     *   @see getRight
     */
    public void setRight(Right right) {
        this.right = right;
    }
    
    /**
     *   Get note about the user
     *   @return string containing note about the user
     *   @see setNote
     */
    public String getNote() {
        return this.note;
    }
    
    /**
     *   Set note about the user
     *   @param contact string containing note about the user
     *   @see getNote
     */
    public void setNote(String note) {
        this.note = note;
    }   
}
