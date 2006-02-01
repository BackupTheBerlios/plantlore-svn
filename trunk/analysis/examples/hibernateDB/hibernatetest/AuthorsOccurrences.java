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
public class AuthorsOccurrences {

	/** Unique identifier of a record */
	private Integer id;
	   
	/** foring key referenced to table TAUTHORS */
	private int authorId;

	/** foring key referenced to table TOCCURRENCES */
	private int occurrenceId;


   /**
	*   Default constructor to create new class AuthorOccurrences
	*/
	public AuthorsOccurrences() {
   
	   }

   /**
	*   Get AuthorOccurences id
	*   @return id of the AuthorOccurrences
	*   @see setId
	*/
	public Integer getId() {
		  return this.id;
	   }

   /**
	*   Set AuthorOccurrences id
	*   @param id   id of the AuthorOccurrences
	*   @see getId
	*/
	public void setId(Integer id) {
		  this.id = id;
	   }		   

	/**
	*   Get foreign key referenced to table TAUTHORS
	*   @return foreign key referenced to table TAUTHORS
	*   @see setAuthorId
	*/
	public int getAuthorId() {
		  return this.authorId;
	   }

   /**
	*   Set foreign key referenced to table TAUTHORS
	*   @param authorId  foreign key referenced to table TAUTHORS
	*   @see getAuthorId
	*/
	public void setAuthorId(int authorId) {
		  this.authorId = authorId;
	   }	
	   
	/**
	*   Get foreign key referenced to table TOCCURRENCES
	*   @return foreign key referenced to table TOCCURRENCES
	*   @see setOccurrenceId
	*/
	public int getOccurrenceId() {
		  return this.occurrenceId;
	   }

   /**
	*   Set foreign key referenced to table TOCCURRENCES
	*   @param occurenceId  foreign key referenced to table TOCCURRENCES
	*   @see getOccurrenceId
	*/
	public void setOccurrenceId(int occurrenceId) {
		  this.occurrenceId = occurrenceId;
	   }
}
