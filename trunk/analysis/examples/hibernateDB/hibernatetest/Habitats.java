/*
 * Created on 1.1.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package hibernatetest;

/**
 * 
 * @author Lada
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Habitats {
	
	  /** Unique identifier of a record */
	  private Integer id;
	   	
	  /** Fields of the record*/    
	  private String quadrant;
	  private String description;
	  private String country;
	  private double altitude;
	  private double latitude;
	  private double longitude;
	  private String note;
	
	/** foring key referenced to table ... */
	  private int territoryId;
	  private int phytochoriaId;
	  private int nearestVillageId;	  
			
	  /**
	   *   Default constructor to create new class Habitats
	   */
	  public Habitats() {
   
	  }

	  /**
	   *   Get habitat id
	   *   @return id of the habitat
	   *   @see setId
	   */
	  public Integer getId() {
		 return this.id;
	  }

	  /**
	   *   Set habitat id
	   *   @param id   id of the habitat
	   *   @see getId
	   */
	  public void setId(Integer id) {
		 this.id = id;
	  }

	  /**
	   *   Get foreign key referenced to table TTERRITORIES
	   *   @return foreign key referenced to table TTERRITORIES 
	   *   @see setTerritoryId
	   */
	  public int getTerritoryId() {
		 return this.territoryId;
	  }

	  /**
	   *   Set foreign key referenced to table TTERRITORIES
	   *   @param territoryId foreign key referenced to table TTERRITORIES
	   *   @see getTerritoryId
	   */
	  public void setTerritoryId(int territoryId) {
		 this.territoryId = territoryId;
	  }
	  
	/**
	   *   Get foreign key referenced to table TPHYTOCHRIA
	   *   @return foreign key referenced to table TPHYTOCHRIA
	   *   @see setPhytochoriaId
	   */
	  public int getPhytochoriaId() {
		 return this.phytochoriaId;
	  }

	  /**
	   *   Set foreign key referenced to table TPHYTOCHRIA
	   *   @param territoryId foreign key referenced to table TPHYTOCHRIA
	   *   @see getPhytochoriaId
	   */
	  public void setPhytochoriaId(int phytochoriaId) {
		 this.phytochoriaId = phytochoriaId;
	  }
	  
	/**
	   *   Get foreign key referenced to table TVILLAGES
	   *   @return foreign key referenced to table TVILLAGES 
	   *   @see setNearestVillageId
	   */
	  public int getNearestVillageId() {
		 return this.nearestVillageId;
	  }

	  /**
	   *   Set foreign key referenced to table TVILLAGES
	   *   @param nearestVillageId foreign key referenced to table TVILLAGES
	   *   @see getNearestVillageId
	   */
	  public void setNearestVillageId(int nearestVillageId) {
		 this.nearestVillageId = nearestVillageId;
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
		*   Get country where plant was found
		*   @return country country where plant was foun 
		*   @see setCountry
		*/
	   public String getCountry() {
		  return this.country;
	   }

	   /**
		*   Set country where plant was foun
		*   @param country  country where plant was foun
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
	   *   Get note about habitats
	   *   @return string containing note about habitats
	   *   @see setNote
	   */      
	  public String getNote() {
		 return this.note;
	  }
	
	  /**
	   *   Set note about habitats
	   *   @param contact string containing note about habitats
	   *   @see getNote
	   */         
	  public void setNote(String note) {
		 this.note = note;
	  }    
}
