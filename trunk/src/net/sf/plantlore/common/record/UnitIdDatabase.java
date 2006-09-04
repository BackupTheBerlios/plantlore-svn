package net.sf.plantlore.common.record;


/**
 *  Data holder object representing TUNITIDDATABASE table in the DB. This object is used as a data
 *  holder for Hibernate operations on the server side. On the side of the client, it represents part
 *  of a lastUpdate record we are working with. It is being sent from client to server and back when
 *  executing database queries.
 *
 *  @author Lada Oberreiterova  
 */
public class UnitIdDatabase extends Record {
	
	private static final long serialVersionUID = 20060604014L;

	 /**
     * Parameters of the UnitIdDatabase. For detailed explanation see data model documentation.
     */
    private Integer id;
    private String unitIdDb;   
    
    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String UNITIDDB = "unitIdDb";                         
    
    /**
     *   Default constructor to create new class UnitIdDatabase
     */
    public UnitIdDatabase() {
        
    }
    
        
    /**
     *   Get UnitIdDatabase record id        
     * @return id of the UnitIdDatabase
     * @see setId
     */
    public Integer getId() {
        return this.id;
    }
    
    /**
     *   Set UnitIdDatabase record id     
     * @param id   id of the UnitIdDatabase
     * @see getId
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     *   Get unique identificator of database
     *   @return unique identificator of database
     *   @see setUnitIdDb
     */
    public String getUnitIdDb() {
        return this.unitIdDb;
    }
    
    /**
     *   Set unique identificator of database
     *   @param unitIdDb string containing unique identificator of database
     *   @see getUnitIdDb
     */
    public void setUnitIdDb (String unitIdDb) {
        this.unitIdDb = unitIdDb;
    }    
}
