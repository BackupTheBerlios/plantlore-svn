/*
 * Created on 30.12.2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package hibernatetest;

/**
 * @author Lada
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Authors {

	/** Unique identifier of a record */
	   private Integer id;
   
	   /** Fields of the record*/
	   private String firstName;
	   private String surname;
	   private String wholeName;   
	   private String organization;
	   private String role;   
	   private String address;	      
	   private String email;
	   private String URL;   
	   private String note;	
	   
	   

	   /**
		*   Default constructor to create new class Author
		*/
	   public Authors() {
       
	   }

	   /**
		*   Get author id
		*   @return id of the author
		*   @see setId
		*/
	   public Integer getId() {
		  return this.id;
	   }

	   /**
		*   Set author id
		*   @param id   id of the author
		*   @see getId
		*/
	   public void setId(Integer id) {
		  this.id = id;
	   }

	   /**
		*   Get firstname of the author of occurence
		*   @return first name of the author 
		*   @see setFirstName
		*/
	   public String getFirstName() {
		  return this.firstName;
	   }

	   /**
		*   Set firstName of the author of occurence
		*   @param firstName string containing first name of the author
		*   @see getFirstName
		*/
	   public void setFirstName(String firstName) {
		  this.firstName = firstName;
	   }
	
	   /**
		*   Get surname of the author of occurence
		*   @return surname of the author 
		*   @see setSurname
		*/
	   public String getSurname() {
		  return this.surname;
	   }

	   /**
		*   Set surname of the author of occurence
		*   @param surname string containing surname of the author
		*   @see getSurName
		*/
	   public void setSurname(String surname) {
		  this.surname = surname;
	   }
	   
	   /**
		*   Get whole name of the author of occurence
		*   @return name of the author 
		*   @see setWholeName
		*/
	   public String getWholeName() {
		  return this.wholeName;
	   }

	   /**
		*   Set whole name of the author of occurence
		*   @param wholeName string containing whole name of the author
		*   @see getWholeName
		*/
	   public void setWholeName(String wholeName) {
		  this.wholeName = wholeName;
	   }
	   
		/**
		 *   Get name of the organization where author is member
		 *   @return name of the organization 
		 *   @see setOrganization
		 */
	   public String getOrganization() {
		   return this.organization;
		}
	
		/**
		 *   Set name of the organization where author is member
		 *   @param organization string containing name of the organization
		 *   @see getOrganization
		 */
	   public void setOrganization(String organization) {
		   this.organization = organization;
		}
		
		/**
		 *   Get role of the author
		 *   @return role of the author 
		 *   @see setRole
		 */
		public String getRole() {
		   return this.role;
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
		 *   Get address of the author
		 *   @return address of the author 
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
		 *   Get email of the author
		 *   @return email of the author 
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
		 *   @return URL of the author 
		 *   @see setURL
		 */
		public String getURL() {
		   return this.URL;
		}

		/**
		 *   Set URL of the author
		 *   @param URL string containing URL of the author
		 *   @see getURL
		 */
		public void setURL(String URL) {
		   this.URL = URL;
		}	
		
		/**
		 *   Get note of the author
		 *   @return note of the author 
		 *   @see setNote
		 */
		public String getNote() {
		   return this.note;
		}

		/**
		 *   Set note of the author
		 *   @param note string containing note of the author
		 *   @see getNote
		 */
		public void setNote(String note) {
		   this.note = note;
		}   		
}
