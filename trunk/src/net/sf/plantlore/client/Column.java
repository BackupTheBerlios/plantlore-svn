/*
 * Column.java
 *
 * Created on 12. květen 2006, 13:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client;

import java.util.Date;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 *
 * @author fraktalek
 */
public class Column {
    public Type type;
    public int preferredSize;
    public String l10nKey;
    public Class columnClass;
    
    private Logger logger;
    private String nullString = "";
    private Integer nullInteger = -1;
    private Double nullDouble = new Double(-1);
    private Date nullDate = new Date(0);
    private Boolean nullBoolean = false;
    
    public enum Type {SELECTION,
    NUMBER,
    PLANT_TAXON,
    AUTHOR,
    HABITAT_NEAREST_VILLAGE_NAME,
    HABITAT_DESCRIPTION,
    OCCURRENCE_YEARCOLLECTED,
    TERRITORY_NAME,
    PHYTOCHORION_NAME,
    PHYTOCHORION_CODE,
    HABITAT_COUNTRY,
    HABITAT_QUADRANT,
    OCCURRENCE_NOTE,
    HABITAT_NOTE,
    HABITAT_ALTITUDE,
    HABITAT_LONGITUDE,
    HABITAT_LATITUDE,
    OCCURRENCE_DATASOURCE,
    PUBLICATION_COLLECTIONNAME,
    OCCURRENCE_HERBARIUM,
    METADATA_DATASETTITLE,
    OCCURRENCE_MONTHCOLLECTED,
    OCCURRENCE_DAYCOLLECTED,
    OCCURRENCE_TIMECOLLECTED,
    OCCURRENCE_ID}
    
    /** Creates a new instance of Column */
    public Column(Type type) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.type = type;
        switch (type) {
            case AUTHOR:
                preferredSize = 100;
                l10nKey = "Overview.ColumnAuthor";
                columnClass = String.class;
                break;
            case HABITAT_ALTITUDE:
                preferredSize = 50;
                l10nKey = "Overview.ColumnAltitude";
                columnClass = Double.class;
                break;
            case HABITAT_COUNTRY:
                preferredSize = 100;
                l10nKey = "Overview.ColumnCountry";
                columnClass = String.class;
                break;
            case HABITAT_DESCRIPTION:
                preferredSize = 150;
                l10nKey = "Overview.ColumnPlace";
                columnClass = String.class;
                break;
            case HABITAT_LATITUDE:
                preferredSize = 50;
                l10nKey = "Overview.ColumnLatitude";
                columnClass = Double.class;
                break;
            case HABITAT_LONGITUDE:
                preferredSize = 50;
                l10nKey = "Overview.ColumnLongitude";
                columnClass = Double.class;
                break;
            case HABITAT_NEAREST_VILLAGE_NAME:
                preferredSize = 100;
                l10nKey = "Overview.ColumnVillage";
                columnClass = String.class;
                break;
            case HABITAT_NOTE:
                preferredSize = 150;
                l10nKey = "Overview.ColumnLocNote";
                columnClass = String.class;
                break;
            case HABITAT_QUADRANT:
                preferredSize = 50;
                l10nKey = "Overview.ColumnQuadrant";
                columnClass = String.class;
                break;
            case METADATA_DATASETTITLE:
                preferredSize = 100;
                l10nKey = "Overview.ColumnMetadata";
                columnClass = String.class;
                break;
            case NUMBER:
                preferredSize = 50;
                l10nKey = "Overview.ColumnNumber";
                columnClass = Integer.class;
                break;
            case OCCURRENCE_DATASOURCE:
                preferredSize = 100;
                l10nKey = "Overview.ColumnSource";
                columnClass = String.class;
                break;
            case OCCURRENCE_DAYCOLLECTED:
                preferredSize = 50;
                l10nKey = "Overview.ColumnDay";
                columnClass = Integer.class;
                break;
            case OCCURRENCE_HERBARIUM:
                preferredSize = 50;
                l10nKey = "Overview.ColumnHerbarium";
                columnClass = String.class;
                break;
            case OCCURRENCE_ID:
                preferredSize = 50;
                l10nKey = "Overview.ColumnOccurrenceId";
                columnClass = Integer.class;
                break;
            case OCCURRENCE_MONTHCOLLECTED:
                preferredSize = 50;
                l10nKey = "Overview.ColumnMonth";
                columnClass = Integer.class;
                break;
            case OCCURRENCE_NOTE:
                preferredSize = 150;
                l10nKey = "Overview.ColumnOccNote";
                columnClass = String.class;
                break;
            case OCCURRENCE_TIMECOLLECTED:
                preferredSize = 50;
                l10nKey = "Overview.ColumnTime";
                columnClass = Date.class;
                break;
            case OCCURRENCE_YEARCOLLECTED:
                preferredSize = 50;
                l10nKey = "Overview.ColumnYear";
                columnClass = Integer.class;
                break;
            case PHYTOCHORION_CODE:
                preferredSize = 50;
                l10nKey = "Overview.ColumnPhytCode";
                columnClass = String.class;
                break;
            case PHYTOCHORION_NAME:
                preferredSize = 100;
                l10nKey = "Overview.ColumnPhyt";
                columnClass = String.class;
                break;
            case PLANT_TAXON:
                preferredSize = 100;
                l10nKey = "Overview.ColumnName";
                columnClass = String.class;
                break;
            case PUBLICATION_COLLECTIONNAME:
                preferredSize = 100;
                l10nKey = "Overview.ColumnPublication";
                columnClass = String.class;
                break;
            case SELECTION:
                preferredSize = 30;
                l10nKey = "Overview.ColumnSelection";
                columnClass = Boolean.class;
                break;
            case TERRITORY_NAME:
                preferredSize = 100;
                l10nKey = "Overview.ColumnTerritory";
                columnClass = String.class;
                break;
            default:
                preferredSize = 100;
                l10nKey = "Overview.Column";
                columnClass = String.class;
        }
        
    }
    
    public Class getColumnClass() {
        return columnClass;
    }
    
    public int getPreferredSize() {
        return preferredSize;
    }
    
    public void setPreferredSize(Integer size) {
        if (size != null)
            preferredSize = size;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public String getL10nName() {
        return L10n.getString(l10nKey);
    }
    
    public String toString() {
        return L10n.getString(l10nKey);
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof Column)) 
            return false;
        
        Column col = (Column) obj;
        
        return type.equals(col.type);
    }
    
    public Object getDefaultNullValue() {
        assert columnClass != null;
        
        if (columnClass.equals(String.class))
            return nullString;
        
        if (columnClass.equals(Integer.class))
            return nullInteger;
        
        if (columnClass.equals(Double.class))
            return nullDouble;
        
        if (columnClass.equals(Date.class))
            return nullDate;
        
        if (columnClass.equals(Boolean.class))
            return nullBoolean;
        
        logger.warn("Don't have default null value for class "+columnClass.getClass().getName());
        return new Object();
    }
    
}

/*
                    switch (columns[j].type) {
                        case AUTHOR:
                            break;
                        case HABITAT_ALTITUDE:
                            break;
                        case HABITAT_COUNTRY:
                            break;
                        case HABITAT_DESCRIPTION:
                            break;
                        case HABITAT_LATITUDE:
                            break;
                        case HABITAT_LONGITUDE:
                            break;
                        case HABITAT_NEAREST_VILLAGE_NAME:
                            break;
                        case HABITAT_NOTE:
                            break;
                        case HABITAT_QUADRANT:
                            break;
                        case METADATA_DATASETTITLE:
                            break;
                        case NUMBER:
                            break;
                        case OCCURRENCE_DATASOURCE:
                            break;
                        case OCCURRENCE_DAYCOLLECTED:
                            break;
                        case OCCURRENCE_HERBARIUM:
                            break;
                        case OCCURRENCE_ID:
                            break;
                        case OCCURRENCE_MONTHCOLLECTED:
                            break;
                        case OCCURRENCE_NOTE:
                            break;
                        case OCCURRENCE_TIMECOLLECTED:
                            break;
                        case OCCURRENCE_YEARCOLLECTED:
                            break;
                        case PHYTOCHORION_CODE:
                            break;
                        case PHYTOCHORION_NAME:
                            break;
                        case PLANT_TAXON:
                            break;
                        case PUBLICATION_COLLECTIONNAME:
                            break;
                        case SELECTION:
                            break;
                        case TERRITORY_NAME:
                            break;
                        default:                        
                    }//switch
*/