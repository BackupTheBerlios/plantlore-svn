/*
 * UserRecord.java
 *
 * Created on 16. leden 2006, 2:32
 *
 */

package net.sf.plantlore.client.dblayer;

/**
 *  Data holder object containing information about a user
 *
 *  @author Tomas Kovarik
 *  @version 0.1, Jan 16, 2006
 */
public class UserRecord {
    /** Parameters of the user */
    private int id;
    private String login;
    private String firstName;
    private String surname;
    private String email;
    private String address;
    private java.util.Date whenCreated;
    private java.util.Date whenDropped;
    private String note;
    private int exportRight;
    private int importRight;
    private String role;
    
    /** Creates a new instance of UserRecord */
    public UserRecord() {
        
    }
    
    /**
     *   Get user id
     *   @return id of the user
     *   @see setID
     */
    public int getID() {
        return this.id;
    }
    
    /**
     *   Set user id
     *   @param id   id of the user
     *   @see getID
     */
    public void setID(int id) {
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
     *   Get email of the user
     *   @return string containing email of the user
     *   @see setEmail
     */
    public String getEmail() {
        return this.email;
    }
    
    /**
     *   Set email of the user
     *   @param contact string containing email of the user
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
     *   @param contact string containing address of the user
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
     *   Get date when the user was droped
     *   @return date when the user was deleted from the system
     *   @see setWhenDropped
     */
    public java.util.Date getWhenDropped() {
        return this.whenDropped;
    }
    
    /**
     *   Set date when the user was droped
     *   @param whenDropped date when the user was deleted from the system
     *   @see getWhenDropped
     */
    public void setWhenDropped(java.util.Date whenDropped) {
        this.whenDropped = whenDropped;
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
    
    /**
     *  Get the information about the export right of the user
     *  @return information about the export right of the user
     *  @see setExportRight
     */
    public int getExportRight() {
        return this.exportRight;
    }
    
    /**
     *   Set the information about the export right of the user
     *   @param exportRight information about the export right of the user
     *   @see getExportRight
     */
    public void setExportRight(int exportRight) {
        this.exportRight = exportRight;
    }
    
    /**
     *  Get the information about the import right of the user
     *  @return information about the import right of the user
     *  @see setImportRight
     */
    public int getImportRight() {
        return this.importRight;
    }
    
    /**
     *   Set the information about the imoprt right of the user
     *   @param importRight information about the import right of the user
     *   @see getImportRight
     */
    public void setImportRight(int importRight) {
        this.importRight = importRight;
    }
    
    /**
     *  Get role of the user
     *  @return String representation of the role of the user
     *  @see setRole
     */
    public String getRole() {
        return this.role;
    }
    
    /**
     *   Set the role of the user
     *   @param role String representation of the role of the user
     *   @see getRole
     */
    public void setRole(String role) {
        this.role = role;
    }
}
