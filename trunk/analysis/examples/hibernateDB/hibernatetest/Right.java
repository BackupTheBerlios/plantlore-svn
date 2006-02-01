package hibernatetest;

/**
 * @author Lada
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Right {

	/** Unique identifier of a record */
	private Integer id;
	   	
	/** Fields of the record*/    
	private int administrator;
	private int editAll;
	private int editOwen;
	private String editGroup;
	private int userExport;
	private int userImport;
	private String role;
	
   
	/**
	 *   Default constructor to create new class Right
	 */
	public Right() {
   
	}

	/**
	 *   Get right id
	 *   @return id of the right
	 *   @see setId
	 */
	public Integer getId() {
	   return this.id;
	}

	/**
	 *   Set right id
	 *   @param id   id of the right
	 *   @see getId
	 */
	public void setId(Integer id) {
	   this.id = id;
	}

	/**
	 *   Get information about administrator right
	 *   @return information about administrator right
	 *   @see setAdministrator
	 */
	public int getAdministrator() {
	   return this.administrator;
	}

	/**
	 *   Set information about administrator right
	 *   @param administrator information about administrator right
	 *   @see getAdministrator
	 */
	public void setAdministrator(int administrator) {
	   this.administrator= administrator;
	}
	
	/**
	 *   Get information about edit all occurrences
	 *   @return information about edit all occurrences
	 *   @see setEditAll
	 */
	public int getEditAll() {
	   return this.editAll;
	}

	/**
	 *   Set information about edit all occurrences
	 *   @param editAll information about edit all occurrences
	 *   @see getEditAll
	 */
	public void setEditAll(int editAll) {
	   this.editAll= editAll;
	}
	
	/**
	 *   Get information about edit only owen occurrences
	 *   @return information about edit only owen occurrences
	 *   @see setEditOwen
	 */
	public int getEditOwen() {
	   return this.editOwen;
	}

	/**
	 *   Set information about edit only owen occurrences
	 *   @param editOwen information about edit only owen occurrences
	 *   @see getEditOwen
	 */
	public void setEditOwen(int editOwen) {
	   this.editOwen= editOwen;
	}

	/**
	 *   Get information about export right
	 *   @return information about export right
	 *   @see setUserExport
	 */
	public int getUserExport() {
	   return this.userExport;
	}

	/**
	 *   Set information about export right
	 *   @param userExport information about export right
	 *   @see getUserExport
	 */
	public void setUserExport(int userExport) {
	   this.userExport= userExport;
	}
	
	/**
	 *   Get information about import right
	 *   @return information about import right
	 *   @see setUserImport
	 */
	public int getUserImport() {
	   return this.userImport;
	}

	/**
	 *   Set information about import right
	 *   @param userImport information about import right
	 *   @see getUserImport
	 */
	public void setUserImport(int userImport) {
	   this.userImport= userImport;
	}	
	
	/**
	   *   Get list of users whose occurrences has the user right edit
	   *   @return list of users whose occurrences has the user right edit
	   *   @see setEditGroup
	   */      
	  public String getEditGroup() {
		 return this.editGroup;
	  }

	  /**
	   *   Set list of users whose occurrences has the user right edit
	   *   @param editGroup list of users whose occurrences has the user right edit
	   *   @see getEditGroup
	   */         
	  public void setEditGroup(String editGroup) {
		 this.editGroup = editGroup;
	  } 	
	  
		/**
	   *   Get role of the user
	   *   @return role of the user
	   *   @see setRole
	   */      
	  public String getRole() {
		 return this.role;
	  }

	  /**
	   *   Set role of the user
	   *   @param role role of the user
	   *   @see getRole
	   */         
	  public void setRole(String role) {
		 this.role = role;
	  } 		  
}	
