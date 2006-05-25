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
    private String note;
    
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
    public static final String NOTE = "note";
    
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
    	else return c != 0;
    }
    
    @Override
    public void setValue(String column, Object value) {
		if(column.equals(ID)) setId((Integer)value);
		else if(column.equals(TERRITORY)) setTerritory((Territory)value);
		else if(column.equals(PHYTOCHORION)) setPhytochorion((Phytochorion)value);
		else if(column.equals(NEARESTVILLAGE)) setNearestVillage((Village)value);
		else if(column.equals(QUADRANT)) setQuadrant((String)value);
		else if(column.equals(DESCRIPTION)) setDescription((String)value);
		else if(column.equals(COUNTRY)) setCountry((String)value);
		else if(column.equals(ALTITUDE)) 
                    if (value.getClass() == String.class) setAltitude(Double.parseDouble((String) value));
                    else setAltitude((Double)value);
		else if(column.equals(LATITUDE)) 
                    if (value.getClass() == String.class) setLatitude(Double.parseDouble((String) value));
                    else setLatitude((Double)value);
		else if(column.equals(LONGITUDE)) 
                    if (value.getClass() == String.class) setLongitude(Double.parseDouble((String) value));
                    else setLongitude((Double)value);
		else if(column.equals(DELETED)) 
                    if (value.getClass() == String.class) setDeleted(Integer.parseInt((String) value));
                    else setDeleted((Integer)value);
		else if(column.equals(NOTE)) setNote((String)value);
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
     *   Set associated Village record
     *   @param nearestVillage associated Village record
     *   @see getNearestVillage
     */
    public void setNearestVillage(Village nearestVillage) {
        this.nearestVillage = nearestVillage;
    }
    
    
    /**
     *   Get quadrant
     *   @return quadrant
     *   @see setQuadrant
     */
    public String getQuadrant() {
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
}