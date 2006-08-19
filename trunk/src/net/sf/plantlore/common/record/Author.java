/*
 * Author.java
 *
 * Created on 16. leden 2006, 2:32
 */

package net.sf.plantlore.common.record;

import java.util.List;

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
	
	private static final long serialVersionUID = 20060604002L;
	
	
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
    private User createdWho;
    private String note;
    private Integer version;
    
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
    public static final String CREATEDWHO = "createdWho";    
    public static final String DELETED = "deleted";
    
    /** Creates a new instance of AuthorRecord */
    public Author() {
    }
    
    public List<String> getColumns() {
    	return asList( WHOLENAME, ORGANIZATION, ROLE, ADDRESS, PHONENUMBER, EMAIL, URL, NOTE, DELETED );
    }
    
    @Override
    public List<String> getNN() {
    	return asList( WHOLENAME );
    }

    @Override
    public void setValue(String column, Object value) {
    	if(value instanceof String && "".equals(value) )
    		value = null;
    	
    	if(column.equalsIgnoreCase(ID)) {
			if(value != null && value instanceof String)
				setId(Integer.parseInt((String)value));
			else
				setId((Integer)value);
		}
    	else if(column.equalsIgnoreCase(WHOLENAME)) setWholeName((String)value);
    	else if(column.equalsIgnoreCase(ORGANIZATION)) setOrganization((String)value);
    	else if(column.equalsIgnoreCase(ROLE)) setRole((String)value);
    	else if(column.equalsIgnoreCase(ADDRESS)) setAddress((String)value);
    	else if(column.equalsIgnoreCase(PHONENUMBER)) setPhoneNumber((String)value);
    	else if(column.equalsIgnoreCase(EMAIL)) setEmail((String)value);
    	else if(column.equalsIgnoreCase(URL)) setUrl((String)value);
    	else if(column.equalsIgnoreCase(NOTE)) setNote((String)value);
        else if(column.equalsIgnoreCase(CREATEDWHO)) setCreatedWho((User)value);                        
        else if(column.equalsIgnoreCase(DELETED)) { 
        	if (value != null && value instanceof String) 
        		setDeleted(Integer.parseInt((String) value));
        	else 
        		setDeleted((Integer)value);
        }
    }
    
    @Override 
    public boolean isDead() {
    	Integer c = getDeleted();
    	if( c == null ) return false;
    	return c != 0;
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
        //obligatory
        return this.id;
    }
    
    /**
     *   Get whole name of the author 
     *   @return string containing whole name of the author
     *   @see setWholeName
     */
    public String getWholeName() {
        //obligatory
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
     *   Get organization the author belongs to
     *   @return string containing organization the author belongs to, returns empty string instead of null
     *   @see setOrganization
     */
    public String getOrganizationNN() {
        if (this.organization == null)
            return "";
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
     *   Get role of the author
     *   @return string representation of author's role, returns empty string instead of null
     *   @see setRole
     */
    public String getRoleNN() {
        if (this.role == null) 
            return "";        
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
     *   Get address of the author
     *   @return string containing address of the author, returns empty string instead of null
     *   @see setAddress
     */
    public String getAddressNN() {
        if (this.address == null)
            return "";
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
     *   Get phone number of the author
     *   @return string containing phone number of the author, returns empty string instead of null
     *   @see setPhoneNumber
     */
    public String getPhoneNumberNN() {
        if (this.phoneNumber == null)
            return "";
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
     *   Get email of the author
     *   @return string containing email of the author, returns empty string instead of null
     *   @see setEmail
     */
    public String getEmailNN() {
        if (this.email == null)
            return "";
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
     *   Get URL of the author
     *   @return URL of the author's webpage, returns empty sring instead of null
     *   @see setUrl
     */
    public String getUrlNN() {
        if (this.url == null)
            return "";
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
    
    /**
     *  Get user who created this author 
     *  @return User who created this Author
     *  @see setCreatedWho
     */
    public User getCreatedWho() {        
        //obligatory
        return this.createdWho;
    }
    
    /**
     *  Set user who created this Author.
     *  @param createdWho user who created this Author
     *  @see getCreatedWho
     */
    public void setCreatedWho(User createdWho) {
        this.createdWho = createdWho;
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
     *   Get note about the author
     *   @return string containing note about the author, returns empty string instead of null
     *   @see setNote
     */
    public String getNoteNN() {
        if (this.note == null)
            return "";
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