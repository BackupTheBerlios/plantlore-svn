/*
 * PlantRecord.java
 *
 * Created on 16. leden 2006, 2:32
 *
 */

package net.sf.plantlore.common.record;

/**
 *  Data holder object containing information about a plant
 *
 *  @author Tomas Kovarik
 *  @version 0.1, Jan 16, 2006
 */
public class PlantRecord {
    // Paraneters of the plant
    private int id;
    private String adoptedName;
    private String czechName;
    private String publishableName;
    private String abbreviation;
    private String note;
    
    /** Creates a new instance of PlantRecord */
    public PlantRecord() {
        
    }
    
    public void setID(int id) {
        this.id = id;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setAdoptedName(String adoptedName) {
        this.adoptedName = adoptedName;
    }
    
    public String getAdoptedName() {
        return this.adoptedName;
    }
    
    public void setCzechName(String czechName) {
        this.czechName = czechName;
    }
    
    public String getCzechName() {
        return this.czechName;
    }
    
    public void setPublishableName(String publishableName) {
        this.publishableName = publishableName;
    }
    
    public String getPublishableName() {
        return this.publishableName;
    }
    
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
    
    public String getAbbreviation() {
        return this.abbreviation;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public String getNote() {
        return this.note;
    }
}
