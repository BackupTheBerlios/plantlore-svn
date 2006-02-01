/*
 * Created on 31.12.2005
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
public class Villages {

   /** Unique identifier of a record */
   private Integer id;
	   
   /** Fields of the record*/    
   private String name;
	
   /**
    *   Default constructor to create new class Villlages
	*/
   public Villages() {
   
   }

   /**
	*   Get village id
	*   @return id of the village
	*   @see setId
	*/
   public Integer getId() {
	  return this.id;
   }

   /**
	*   Set village id
	*   @param id   id of the village
	*   @see getId
	*/
   public void setId(Integer id) {
	  this.id = id;
   }

   /**
	*   Get name of the village
	*   @return name of the village 
	*   @see setName
	*/
   public String getName() {
	  return this.name;
   }

   /**
	*   Set name of the village
	*   @param name string containing name of the village
	*   @see getName
	*/
   public void setName(String name) {
	  this.name = name;
   }
}
