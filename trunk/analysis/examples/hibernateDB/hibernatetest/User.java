package hibernatetest;

/**
 *  Class containing getter and setter methods for database table tUser. This class is used by Hibernate
 *  to store data about a user when inserting/updating/selecting data from the database. For the mapping 
 *  of database columns to fields of this class see file <code>hibernate.cfg.xml</code>
 *
 *  @author Kovo tkovarik@gmail.com
 *  @version 0.1; 13.12. 2005  
 */
public class User {
   
   /** Unique identifier of a record */
   private Integer id;
   
   /** Fields of the record*/
   private String login;
   private String firstName;
   private String surname;   
   private String email;
   private String address;
   private java.util.Date whenCreate;
   private java.util.Date whenDrop;
   private String note;
   
   /** foring key referenced to table TRIGHT */
   private int rightId;
   
   /**
    *   Default constructor to create new class User
    */
   public User() {
       
   }

   /**
    *   Get user id
    *   @return id of the user
    *   @see setId
    */
   public Integer getId() {
      return this.id;
   }

   /**
    *   Set user id
    *   @param id   id of the user
    *   @see getId
    */
   public void setId(Integer id) {
      this.id = id;
   }

   /**
    *   Get foreign key referenced to table TRIGHT
    *   @return foreign key referenced to table TRIGHT 
    *   @see setRightId
    */
   public int getRightId() {
	  return this.rightId;
   }

   /**
    *   Set foreign key referenced to table TRIGHT
    *   @param rightId foreign key referenced to table TRIGHT
    *   @see getRightId
    */
   public void setRightId(int rightId) {
 	 this.rightId = rightId;
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
    *   @see setWhenCreate
    */         
   public java.util.Date getWhenCreate() {
      return this.whenCreate;
   }

   /**
    *   Set date when the user was created
    *   @param whenCreate date when the user was added to the system
    *   @see getWhenCreate
    */            
   public void setWhenCreate(java.util.Date whenCreate) {
      this.whenCreate = whenCreate;
   }   
   
   /**
   *   Get date when the user was droped
   *   @return date when the user was deleted from the system
   *   @see setWhenDrop
   */         
  public java.util.Date getWhenDrop() {
	 return this.whenDrop;
  }

  /**
   *   Set date when the user was droped
   *   @param whenDrop date when the user was deleted from the system
   *   @see getWhenDrop
   */            
  public void setWhenDrop(java.util.Date whenDrop) {
	 this.whenDrop = whenDrop;
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
   *   @param note string containing note about the user
   *   @see getNote
   */         
  public void setNote(String note) {
	 this.note = note;
  }    
}
