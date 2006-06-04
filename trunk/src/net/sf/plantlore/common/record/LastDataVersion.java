package net.sf.plantlore.common.record;

import java.util.Date;

/**
 *  Data holder object representing TLASTVERSION table in the DB. This object is used as a data
 *  holder for Hibernate operations on the server side. On the side of the client, it represents part
 *  of a lastUpdate record we are working with. It is being sent from client to server and back when
 *  executing database queries.
 *
 *  @author Lada Oberreiterova  
 */
public class LastDataVersion extends Record {
	
	private static final long serialVersionUID = 20060604014L;

	 /**
     * Parameters of the LastDataVersion. For detailed explanation see data model documentation.
     */
    private Integer id;
    private Integer plantsVersion;
    private Integer villagesVersion;
    private Integer territoryVersion;
    private Integer phytochoriaVersion;
    private Date date;
    
    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String DATE = "date";      
    public static final String PLANTSVERSION = "tableName";    
    public static final String VILLAGEVERSION = "lastUpdate";
    public static final String TERRITORYVERSION = "territoryVersion";
    public static final String PHYTOCHORIAVERSION = "phytochoriaVersion";
            
    //public enum Column {ID, DATE, PLANTSVERSION, VILLAGEVERSION, TERRITORYVERSION, PHYTOCHORIAVERSION};
    
    /**
     *   Default constructor to create new class LastDataVersion
     */
    public LastDataVersion() {
        
    }
    
        
    /**
     *   Get LastDataVersion record id
     * 
     * @return id of the LaLastDataVersionecord
     * @see setId
     */
    public Integer getId() {
        return this.id;
    }
    
    /**
     *   Set LastDataVersion record id
     * 
     * @param id   id of the LaLastDataVersionecord
     * @see getId
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     *   Get version of data in table tVillages
     *   @return version of data in table tVillages
     *   @see setVillagesVersion
     */
    public Integer getVillagesVersion() {
        return this.villagesVersion;
    }
    
    /**
     *   Set version of data in table tVillages
     *   @param villagesVersion string containing version of data in table tVillages
     *   @see getVillagesVersion
     */
    public void setVillagesVersion (Integer villagesVersion) {
        this.villagesVersion = villagesVersion;
    }
    
    /**
     *   Get version of data in table tTerritory
     *   @return version of data in table tTerritory
     *   @see setTerritoryVersion
     */
    public Integer getTerritoryVersion() {
        return this.territoryVersion;
    }
    
    /**
     *   Set version of data in table tTerritory
     *   @param territoryVersion string containing version of data in table tTerritory
     *   @see getTerritoryVersion
     */
    public void setTerritoryVersion (Integer territoryVersion) {
        this.territoryVersion = territoryVersion;
    }
    
    /**
     *   Get version of data in table tPlants
     *   @return version of data in table tPlants
     *   @see setPlantsVersion
     */
    public Integer getPlantsVersion() {
        return this.plantsVersion;
    }
    
    /**
     *   Set version of data in table tPlants
     *   @param tableName string containing version of data in table tPlants
     *   @see getPlantsVersion
     */
    public void setPlantsVersion (Integer plantsVersion) {
        this.plantsVersion = plantsVersion;
    }
    /**
     *   Get version of data in table tPhytochoria
     *   @return version of data in table tPhytochoria
     *   @see setPhytochoriaVersion
     */
    public Integer getPhytochoriaVersion() {
        return this.phytochoriaVersion;
    }
    
    /**
     *   Set version of data in table tPhytochoria
     *   @param hytochoriaVersion string containing version of data in table tPhytochoria
     *   @see getPhytochoriaVersion
     */
    public void setPhytochoriaVersion (Integer phytochoriaVersion) {
        this.phytochoriaVersion = phytochoriaVersion;
    }

    /**
     *   Get date and time of table last update
     *   @return date and time of table last update
     *   @see setDate
     */
    public java.util.Date getDate() {
        return this.date;
    }
    
    /**
     *   Set date and time of table last update
     *   @param date date and time of table last update
     *   @see getDate
     */
    public void setDate(java.util.Date date) {
        this.date = date;
    }
}
