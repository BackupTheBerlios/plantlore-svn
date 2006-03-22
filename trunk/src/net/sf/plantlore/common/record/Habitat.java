/*
 * Habitat.java
 *
 * Created on 15. březen 2006, 0:17
 *
 */

package net.sf.plantlore.common.record;

/**
 *  Data holder object representing THABITATS table in the DB. This object is used as a data holder
 *  for Hibernate operations on the server side. On the side of the client, it represents a habitat
 *  we are currently working with. It is being sent from client to server and back when executing
 *  database queries.
 *
 * @author Tomas Kovarik
 * @author Lada Oberreiterova
 */
public class Habitat {   
    /** Parameters of the Habitat */
    private int id;
    private Territory territory;
    private Phytochorion phytochorion;    
    private Village nearestVillage;    
    private String quadrant;
    private String description;
    private String country;
    private double altitude;
    private double latitude;
    private double longitude;
    private int deleted;
    private String note;
    
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
    public int getId() {
        return this.id;
    }
    
    /**
     *   Set habitat id
     *   @param id id of the habitat
     *   @see getId
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     *   Get associated Territory record
     *   @return associated Territory record
     *   @see setTerritory
     */
    public Territory getTerritory() {
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
    public double getAltitude() {
        return this.altitude;
    }
    
    /**
     *   Set altitude
     *   @param altitude
     *   @see getAltitude
     */
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
    
    /**
     *   Get latitude
     *   @return latitude
     *   @see setLatitude
     */
    public double getLatitude() {
        return this.latitude;
    }
    
    /**
     *   Set latitude
     *   @param latitude
     *   @see getLatitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    /**
     *   Get longitude
     *   @return longitude
     *   @see setLongitude
     */
    public double getLongitude() {
        return this.longitude;
    }
    
    /**
     *   Set longitude
     *   @param longitude
     *   @see getLongitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
 
    /**
     *   Get flag telling whether the record has been deleted
     *   @return flag telling whether the record has been deleted. Value 1 means deleted.
     *   @see setDeleted
     */
    public int getDeleted() {
        return this.deleted;
    }
    
    /**
     *   Set flag telling whether the record has been deleted
     *   @param deleted flag telling whether the record has been deleted. Value 1 means deleted.
     *   @see getDeleted
     */
    public void setDeleted(int deleted) {
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
     *   Set note about habitat
     *   @param contact string containing note about habitat
     *   @see getNote
     */
    public void setNote(String note) {
        this.note = note;
    }
}