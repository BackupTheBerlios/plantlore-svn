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
public class Phytochoria {

	/** Unique identifier of a record */
   private Integer id;
   
   /** Fields of the record*/
   private String code;    
   private String name;

   /**
	*   Default constructor to create new class Phytochoria
	*/
   public Phytochoria() {
   
   }

   /**
	*   Get phytochoria id
	*   @return id of the phytochoria
	*   @see setId
	*/
   public Integer getId() {
	  return this.id;
   }

   /**
	*   Set phytochoria id
	*   @param id   id of the phytochoria
	*   @see getId
	*/
   public void setId(Integer id) {
	  this.id = id;
   }

   /**
	*   Get name of the phytochoria
	*   @return name of the phytochoria 
	*   @see setName
	*/
   public String getName() {
	  return this.name;
   }

   /**
	*   Set name of the phytochoria
	*   @param name string containing name of the phytochoria
	*   @see getName
	*/
   public void setName(String name) {
	  this.name = name;
   }

   /**
   *   Get code of the phytochoria
   *   @return code of the phytochoria 
   *   @see setName
   */
  public String getCode() {
	 return this.code;
  }

  /**
   *   Set code of the phytochoria
   *   @param code string containing code of the phytochoria
   *   @see getCode
   */
  public void setCode(String code) {
	 this.code = code;
  }
}
