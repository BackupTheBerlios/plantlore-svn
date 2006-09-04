/*
 * Plant.java
 *
 * Created on 16. leden 2006, 2:32
 *
 */

package net.sf.plantlore.common.record;

import java.util.List;

/**
 *  Data holder object representing TPLANTS table in the DB. This object is used as a data holder
 *  for Hibernate operations on the server side. On the side of the client, it represents a plant
 *  we are currently working with. It is being sent from client to server and back when executing
 *  database queries.
 *
 *  @author Tomas Kovarik
 *  @version 0.1, Mar 15, 2006
 */
public class Plant extends Record {
	
	private static final long serialVersionUID = 20060604022L;
	
    /** Parameters of the plant */    
    private Integer id;
    private String surveyTaxId;
    private String taxon;
    private String genus;
    private String species;
    private String scientificNameAuthor;
    private String czechName;
    private String synonyms;
    private String note;

    /** Constants with column mapping (used for building select queries) */
    public static final String ID = "id";
    public static final String SURVEYTAXID = "surveyTaxId";    
    public static final String TAXON = "taxon";    
    public static final String GENUS = "genus";    
    public static final String SPECIES = "species";    
    public static final String SCIENTIFICNAMEAUTHOR = "scientificNameAuthor";    
    public static final String CZECHNAME = "czechName";    
    public static final String SYNONYMS = "synonyms";    
    public static final String NOTE = "note";

    //public enum Column {ID, SURVEYTAXID, TAXON, GENUS, SPECIES, SCIENTIFICNAMEAUTHOR, CZECHNAME, SYNONYMS, NOTE};
    
    /** Creates a new instance of PlantRecord */
    public Plant() {
        
    }
    
    public List<String> getColumns() {
    	return asList( TAXON, GENUS, SPECIES, SCIENTIFICNAMEAUTHOR,
    			CZECHNAME, SYNONYMS, NOTE, SURVEYTAXID );
    }
    
    public List<String> getNN() {
    	return asList( SURVEYTAXID, TAXON );
    }
    
    
    @Override
    public void setValue(String column, Object value) {
    	if(value instanceof String && "".equals(value))
        	value = null;
    	
		if(column.equalsIgnoreCase(ID)) {
			if(value != null && value instanceof String)
				setId(Integer.parseInt((String)value));
			else
				setId((Integer)value);
		}
		else if(column.equalsIgnoreCase(SURVEYTAXID)) setSurveyTaxId((String)value);
		else if(column.equalsIgnoreCase(TAXON)) setTaxon((String)value);
		else if(column.equalsIgnoreCase(GENUS)) setGenus((String)value);
		else if(column.equalsIgnoreCase(SPECIES)) setSpecies((String)value);
		else if(column.equalsIgnoreCase(SCIENTIFICNAMEAUTHOR)) setScientificNameAuthor((String)value);
		else if(column.equalsIgnoreCase(CZECHNAME)) setCzechName((String)value);
		else if(column.equalsIgnoreCase(SYNONYMS)) setSynonyms((String)value);
		else if(column.equalsIgnoreCase(NOTE)) setNote((String)value);
    }
    
    /**
     *   Get plant id
     *   @return id of the plant
     *   @see setId
     */
    public Integer getId() {
        //obligatory
        return this.id;
    }    
    
    /**
     *   Set plant id
     *   @param id   id of the plant
     *   @see getId
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     *   Get Survey identifier of the plant
     *   @return Survey identifier of the plant
     *   @see setSurveyTaxId
     */
    public String getSurveyTaxId() {
        //obligatory
        return this.surveyTaxId;
    }
    
    /**
     *   Set Survey identifier of the plant
     *   @param surveyTaxId Survey identifier of the plant
     *   @see getSurveyTaxId
     */
    public void setSurveyTaxId(String surveyTaxId) {
        this.surveyTaxId = surveyTaxId;
    }
       
    /**
     *   Get taxon of the plant
     *   @return taxon of the plant
     *   @see setTaxon
     */
    public String getTaxon() {
        //obligatory
        return this.taxon;
    }
    
    /**
     *   Set taxon of the plant
     *   @param taxon string containing taxon of the plant
     *   @see getTaxon
     */
    public void setTaxon(String taxon) {
        this.taxon = taxon;
    }
    
    /**
     *   Get genus of the plant
     *   @return genus of the plant
     *   @see setGenus
     */
    public String getGenus() {
        return this.genus;
    }
    
    /**
     *   Get genus of the plant
     *   @return genus of the plant, returns "" instead of null
     *   @see setGenus
     */
    public String getGenusNN() {
        if (this.genus == null)
            return "";
        return this.genus;
    }
    
    /**
     *   Set genus of the plant
     *   @param genus string containing genus of the plant
     *   @see getGenus
     */
    public void setGenus(String genus) {
        this.genus = genus;
    }
    
    /**
     *   Get species of the plant
     *   @return species of the plant
     *   @see setSpecies
     */
    public String getSpecies() {
        return this.species;
    }
    
    /**
     *   Get species of the plant
     *   @return species of the plant, returns "" instead of null
     *   @see setSpecies
     */
    public String getSpeciesNN() {
        if (this.species == null)
            return "";
        return this.species;
    }
    
    /**
     *   Set species of the plant
     *   @param species string containing species of the plant
     *   @see getSpecies
     */
    public void setSpecies(String species) {
        this.species = species;
    }

    /**
     *   Get scientific name of the author
     *   @return scientific name of the author, returns "" instead of null
     *   @see setScientificNameAuthor
     */
    public String getScientificNameAuthorNN() {
        if (this.scientificNameAuthor == null)
            return "";
        return this.scientificNameAuthor;
    }
    
     /**
     *   Get scientific name of the author
     *   @return scientific name of the author
     *   @see setScientificNameAuthor
     */
    public String getScientificNameAuthor() {        
        return this.scientificNameAuthor;
    }
    
    /**
     *   Set scientific name of the author
     *   @param scientificNameAuthor string containin scientific name of the author
     *   @see getScientificNameAuthor
     */
    public void setScientificNameAuthor(String scientificNameAuthor) {
        this.scientificNameAuthor = scientificNameAuthor;
    }
    
    /**
     *   Get czech name of the plant
     *   @return czech name of the plant
     *   @see setCzechName
     */
    public String getCzechName() {
        return this.czechName;
    }
    
    /**
     *   Get czech name of the plant
     *   @return czech name of the plant, returns "" instead of null
     *   @see setCzechName
     */
    public String getCzechNameNN() {
        if (this.czechName == null) 
            return "";
        return this.czechName;
    }
    
    /**
     *   Set czech name of the plant
     *   @param czechName string containing czech name of the plant
     *   @see getCzechName
     */
    public void setCzechName(String czechName) {
        this.czechName = czechName;
    }
           
    /**
     *   Get synonyms of the plant name
     *   @return synonyms of the plant name
     *   @see setSynonyms
     */
    public String getSynonyms() {
        return this.synonyms;
    }
    
    /**
     *   Get synonyms of the plant name
     *   @return synonyms of the plant name, returns "" instead of null
     *   @see setSynonyms
     */
    public String getSynonymsNN() {
        if (this.synonyms == null)
            return "";
        return this.synonyms;
    }
    
    /**
     *   Set synonyms of the plant name
     *   @param synonyms string containing synonyms of the plant name
     *   @see getSynonyms
     */
    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }
    
    /**
     *   Get note about the plant
     *   @return string containing note about the plant
     *   @see setNote
     */
    public String getNote() {
        return this.note;
    }
    
    /**
     *   Get note about the plant
     *   @return string containing note about the plant, returns "" instead of null
     *   @see setNote
     */
    public String getNoteNN() {
        if (this.note == null)
            return "";
        return this.note;
    }
    
    /**
     *   Set note about the plant
     *   @param note string containing note about the plant
     *   @see getNote
     */
    public void setNote(String note) {
        this.note = note;
    }
}
