/*
 * Created on 3.1.2006
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
public class Territories {

	/** Unique identifier of a record */
   private Integer id;
   
   /** Fields of the record*/    
   private String name;

   /**
	*   Default constructor to create new class Territories
	*/
	   public Territories() {
   
	   }

	   /**
	*   Get territories id
	*   @return id of the territories
	*   @see setId
	*/
   public Integer getId() {
	  return this.id;
   }

   /**
	*   Set territories id
	*   @param id   id of the territories
	*   @see getId
	*/
   public void setId(Integer id) {
	  this.id = id;
   }

   /**
	*   Get name of the territories
	*   @return name of the territories 
	*   @see setName
	*/
   public String getName() {
	  return this.name;
   }

   /**
	*   Set name of the territories
	*   @param name string containing name of the territories
	*   @see getName
	*/
   public void setName(String name) {
	  this.name = name;
   }

}
