/*
 * Habitat.java
 *
 * Created on 15. březen 2006, 0:17
 *
 */

package net.sf.plantlore.common.record;

import java.util.List;

/**
 *  Data holder object representing THABITATS table in the DB. This object is used as a data holder
 *  for Hibernate operations on the server side. On the side of the client, it represents a habitat
 *  we are currently working with. It is being sent from client to server and back when executing
 *  database queries.
 *
 * @author Tomas Kovarik
 * @author Lada Oberreiterova
 */
public class Habitat extends Record implements Deletable {   
	
	private static final long serialVersionUID = 20060604006L;
	
    /** Parameters of the Habitat */
    private Integer id;
    private Territory territory;
    private Phytochorion phytochorion;    
    private Village nearestVillage;    
    private String quadrant;
    private String description;
    private String country;
    private Double altitude;
    private Double latitude;
    private Double longitude;
    private Integer deleted;
    private User createdWho;
    private String note;
    private Integer version;
    
    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String TERRITORY = "territory";    
    public static final String PHYTOCHORION = "phytochorion";
    public static final String NEARESTVILLAGE = "nearestVillage";
    public static final String QUADRANT = "quadrant";
    public static final String DESCRIPTION = "description";        
    public static final String COUNTRY = "country";
    public static final String ALTITUDE = "altitude";
    public static final String LATITUDE = "latitude";    
    public static final String LONGITUDE = "longitude";    
    public static final String DELETED = "deleted";    
    public static final String CREATEDWHO = "createdWho"; 
    public static final String NOTE = "note";
    
    public static final String VILLAGE = "village";
    
    //public enum Column {ID, TERRITORY, PHYTOCHORION, NEARESTVILLAGE, QUADRANT, DESCRIPTION, COUNTRY, ALTITUDE, LATITUDE, LONGITUDE, DELETED, NOTE};
    
    @Override
    public List<String> getForeignKeys() { 
    	return asList( TERRITORY, PHYTOCHORION, NEARESTVILLAGE ); 
    }
    
    public List<String> getColumns() {
    	return asList( TERRITORY, PHYTOCHORION, NEARESTVILLAGE, 
    			QUADRANT, DESCRIPTION, COUNTRY, ALTITUDE, LATITUDE, LONGITUDE, NOTE, DELETED );
    }
    
    public List<String> getHistoryColumns() {
        return asList(  TERRITORY, PHYTOCHORION, NEARESTVILLAGE, 
                        QUADRANT, DESCRIPTION, COUNTRY, ALTITUDE, LATITUDE, LONGITUDE, NOTE);
    }
    
    @Override 
    public boolean isDead() {
    	Integer c = getDeleted();
    	if( c == null ) return false;
    	return c != 0;
    }
    
    @Override
    public void setValue(String column, Object value) {
    	if(value instanceof String && "".equals(value) )
    		value = null;
    	
		if(column.equalsIgnoreCase(ID)) {
			if(value != null && value instanceof String)
				setId(Integer.parseInt((String)value));
			else
				setId((Integer)value);
		}
		else if(column.equalsIgnoreCase(TERRITORY)) setTerritory((Territory)value);
		else if(column.equalsIgnoreCase(PHYTOCHORION)) setPhytochorion((Phytochorion)value);
		else if(column.equalsIgnoreCase(NEARESTVILLAGE) || column.equals(VILLAGE)) setNearestVillage((Village)value);
		else if(column.equalsIgnoreCase(QUADRANT)) setQuadrant((String)value);
		else if(column.equalsIgnoreCase(DESCRIPTION)) setDescription((String)value);
		else if(column.equalsIgnoreCase(COUNTRY)) setCountry((String)value);
		else if(column.equalsIgnoreCase(CREATEDWHO)) setCreatedWho((User)value); 
		else if(column.equalsIgnoreCase(ALTITUDE)) {
			if (value != null && value instanceof String) 
				setAltitude(Double.parseDouble((String) value));
			else 
				setAltitude((Double)value);
		}
		else if(column.equalsIgnoreCase(LATITUDE)) {
			if (value != null && value instanceof String) 
				setLatitude(Double.parseDouble((String) value));
			else 
				setLatitude((Double)value);
		}
		else if(column.equalsIgnoreCase(LONGITUDE)) { 
			if (value != null && value instanceof String) 
				setLongitude(Double.parseDouble((String) value));
			else 
				setLongitude((Double)value);
		}
		else if(column.equalsIgnoreCase(DELETED)) {
			if (value != null && value instanceof String) 
				setDeleted(Integer.parseInt((String) value));
			else 
				setDeleted((Integer)value);
		}
		else if(column.equalsIgnoreCase(NOTE)) setNote((String)value);
    }
    
    /**
     * Default constructor to create new class Habitat
     */
    public Habitat() {
        
    }
    
    /**
     *   Get habitat id
     *   @return id of the habitat
     *   @see setId
     */
    public Integer getId() {
        //obligatory
        return this.id;
    }
    
    /**
     *   Set habitat id
     *   @param id id of the habitat
     *   @see getId
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     *   Get associated Territory record
     *   @return associated Territory record
     *   @see setTerritory
     */
    public Territory getTerritory() {
        //obligatory
        return this.territory;
    }
    
    /**
     *   Set associated Territory record
     *   @param territory associated Territory record
     *   @see getTerritory
     */
    public void setTerritory(Territory territory) {
        this.territory = territory;
    }
    
    /**
     *   Get associated Phytochorion record
     *   @return associated Phytochorion record
     *   @see setPhytochorion
     */
    public Phytochorion getPhytochorion() {
        //obligatory
        return this.phytochorion;
    }
    
    /**
     *   Set associated Phytochorion record
     *   @param phytochorion associated Phytochorion record
     *   @see getPhytochorion
     */
    public void setPhytochorion(Phytochorion phytochorion) {
        this.phytochorion = phytochorion;
    }
    
    /**
     *   Get associated Village record
     *   @return associated Village record
     *   @see setNearestVillage
     */
    public Village getNearestVillage() {
        //obligatory
        return this.nearestVillage;
    }
    
    /**
     *   Get associated Village record
     *   @return associated Village record
     *   @see setNearestVillage
     */
    public Village getVillage() {
        //obligatory
        return getNearestVillage();
    }
    
    /**
     *   Set associated Village record
     *   @param nearestVillage associated Village record
     *   @see getNearestVillage
     */
    public void setNearestVillage(Village nearestVillage) {
        this.nearestVillage = nearestVillage;
    }
    
    /**
     *   Set associated Village record
     *   @param nearestVillage associated Village record
     *   @see getNearestVillage
     */
    public void setVillage(Village nearestVillage) {
        //obligatory
        setNearestVillage(nearestVillage);
    }
    
    
    /**
     *   Get quadrant
     *   @return quadrant
     *   @see setQuadrant
     */
    public String getQuadrant() {
        return this.quadrant;
    }
    
    /**
     *   Get quadrant
     *   @return quadrant, returns empty String instead of null
     *   @see setQuadrant
     */
    public String getQuadrantNN() {
        if (this.quadrant == null)
            return "";
        return this.quadrant;
    }
    
    /**
     *   Set quadrant
     *   @param quadrant
     *   @see getQuadrant
     */
    public void setQuadrant(String quadrant) {
        this.quadrant = quadrant;
    }
    
    /**
     *   Get descripton of the place where plant was found
     *   @return description of the place where plant was found
     *   @see setDescription
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     *   Get descripton of the place where plant was found
     *   @return description of the place where plant was found, returns empty string instead of null
     *   @see setDescription
     */
    public String getDescriptionNN() {
        if (this.description == null)
            return "";
        return this.description;
    }
    
    /**
     *   Set descripton of the place where plant was found
     *   @param descripton  description of the place where plant was found
     *   @see getDescription
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     *   Get country where the plant was found
     *   @return country country where the plant was foun
     *   @see setCountry
     */
    public String getCountry() {
        return this.country;
    }
    
    /**
     *   Get country where the plant was found
     *   @return country country where the plant was foun, returns an empty string instead of null
     *   @see setCountry
     */
    public String getCountryNN() {
        if (this.country == null)
            return "";
        return this.country;
    }
    

    /**
     *   Set country where the plant was foun
     *   @param country  country where the plant was foun
     *   @see getCountry
     */
    public void setCountry(String country) {
        this.country = country;
    }
    
    /**
     *   Get altitude
     *   @return altitude
     *   @see setAltitude
     */
    public Double getAltitude() {
        return this.altitude;
    }
    
    /**
     *   Get altitude
     *   @return altitude, returns -1 instead of null
     *   @see setAltitude
     */
    public Double getAltitudeNN() {
        if (this.altitude == null)
            return new Double(-1);
        return this.altitude;
    }
    
    /**
     *   Set altitude
     *   @param altitude
     *   @see getAltitude
     */
    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }
    
    /**
     *   Get latitude
     *   @return latitude
     *   @see setLatitude
     */
    public Double getLatitude() {
        return this.latitude;
    }
    
    /**
     *   Get latitude
     *   @return latitude, returns -1 instead of null
     *   @see setLatitude
     */
    public Double getLatitudeNN() {
        if (this.latitude == null)
            return new Double(-1);
        return this.latitude;
    }
    
    /**
     *   Set latitude
     *   @param latitude
     *   @see getLatitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    /**
     *   Get longitude
     *   @return longitude
     *   @see setLongitude
     */
    public Double getLongitude() {
        return this.longitude;
    }
    
    /**
     *   Get longitude
     *   @return longitude, returns -1 instead of null
     *   @see setLongitude
     */
    public Double getLongitudeNN() {
        if (this.longitude == null)
            return new Double(-1);
        return this.longitude;
    }
    
    /**
     *   Set longitude
     *   @param longitude
     *   @see getLongitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
 
    /**
     *   Get flag telling whether the record has been deleted
     *   @return flag telling whether the record has been deleted. Value 1 means deleted.
     *   @see setDeleted
     */
    public Integer getDeleted() {
        //obligatory
    	return this.deleted;
    }
    
    /**
     *   Set flag telling whether the record has been deleted
     *   @param deleted flag telling whether the record has been deleted. Value 1 means deleted.
     *   @see getDeleted
     */
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }   
    
    /**
     *   Get note about habitat
     *   @return string containing note about habitat
     *   @see setNote
     */
    public String getNote() {
        return this.note;
    }
    
    /**
     *   Get note about habitat
     *   @return string containing note about habitat, returns "" instead of null
     *   @see setNote
     */
    public String getNoteNN() {
        if (this.note == null)
            return "";
        return this.note;
    }
    
    /**
     *   Set note about habitat
     *   @param contact string containing note about habitat
     *   @see getNote
     */
    public void setNote(String note) {
        this.note = note;
    }
    
    /**
     *  Get user who created this Habitat 
     *  @return User who created this Habitat
     *  @see setCreatedWho
     */
    public User getCreatedWho() {        
        //obligatory
        return this.createdWho;
    }
    
    /**
     *  Set user who created this Habitat
     *  @param createdWho user who created this Habitat
     *  @see getCreatedWho
     */
    public void setCreatedWho(User createdWho) {
        this.createdWho = createdWho;
    }    

    /**
     *  Set the row version. Version column is used by Hibernate to implement optimistic locking.
     *  @param version version of the row
     */    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    /**
     *  Get the row version. Version column is used by Hibernate to implement optimistic locking.
     *  @return version of the row
     */
    public Integer getVersion() {
        return version;
    }        
}