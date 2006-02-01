package hibernatetest;

/**
 *  Class containing getter and setter methods for database table tUser. This class is used by Hibernate
 *  to store data about a user when inserting/updating/selecting data from the database. For the mapping 
 *  of database columns to fields of this class see file <code>hibernate.cfg.xml</code>
 *
 *  @author Tomáš Kovaøík, tkovarik@gmail.com
 *  @version 0.1; 13.12. 2005  
 */
public class User {
   
   /** Unique identifier of a record */
   private Integer id;
   
   /** Fields of the record*/
   private String login;
   private String name;
   private String surname;   
   private String contact;
   private java.util.Date whenCreate;

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
    *   @see setName
    */
   public String getName() {
      return this.name;
   }

   /**
    *   Set first name of the user
    *   @param name string containing the first name of the user
    *   @see getName
    */
   public void setName(String name) {
      this.name = name;
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
    *   Get contact details of the user
    *   @return string containing contact details of the user
    *   @see setContact
    */      
   public String getContact() {
      return this.contact;
   }

   /**
    *   Set contact details of the user
    *   @param contact string containing contact details of the user
    *   @see getContact
    */         
   public void setContact(String contact) {
      this.contact = contact;
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
}
