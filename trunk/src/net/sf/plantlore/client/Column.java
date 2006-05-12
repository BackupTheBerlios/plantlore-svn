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

/**
 *
 * @author fraktalek
 */
public class Column {
    public Type type;
    public int preferredSize;
    public String l10nKey;
    public Class columnClass;
    
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
        this.type = type;
        switch (type) {
            case AUTHOR:
                preferredSize = 100;
                l10nKey = "overviewColAuthor";
                columnClass = String.class;
                break;
            case HABITAT_ALTITUDE:
                preferredSize = 50;
                l10nKey = "overviewColAltitude";
                columnClass = Double.class;
                break;
            case HABITAT_COUNTRY:
                preferredSize = 100;
                l10nKey = "overviewColCountry";
                columnClass = String.class;
                break;
            case HABITAT_DESCRIPTION:
                preferredSize = 150;
                l10nKey = "overviewColPlace";
                columnClass = String.class;
                break;
            case HABITAT_LATITUDE:
                preferredSize = 50;
                l10nKey = "overviewColLatitude";
                columnClass = Double.class;
                break;
            case HABITAT_LONGITUDE:
                preferredSize = 50;
                l10nKey = "overviewColLongitude";
                columnClass = Double.class;
                break;
            case HABITAT_NEAREST_VILLAGE_NAME:
                preferredSize = 100;
                l10nKey = "overviewColVillage";
                columnClass = String.class;
                break;
            case HABITAT_NOTE:
                preferredSize = 150;
                l10nKey = "overviewColLocNote";
                columnClass = String.class;
                break;
            case HABITAT_QUADRANT:
                preferredSize = 50;
                l10nKey = "overviewColQuadrant";
                columnClass = String.class;
                break;
            case METADATA_DATASETTITLE:
                preferredSize = 100;
                l10nKey = "overviewColMetadata";
                columnClass = String.class;
                break;
            case NUMBER:
                preferredSize = 50;
                l10nKey = "overviewColNumber";
                columnClass = Integer.class;
                break;
            case OCCURRENCE_DATASOURCE:
                preferredSize = 100;
                l10nKey = "overviewColSource";
                columnClass = String.class;
                break;
            case OCCURRENCE_DAYCOLLECTED:
                preferredSize = 50;
                l10nKey = "overviewColDay";
                columnClass = Integer.class;
                break;
            case OCCURRENCE_HERBARIUM:
                preferredSize = 50;
                l10nKey = "overviewColHerbarium";
                columnClass = String.class;
                break;
            case OCCURRENCE_ID:
                preferredSize = 50;
                l10nKey = "overviewColOccurrenceId";
                columnClass = Integer.class;
                break;
            case OCCURRENCE_MONTHCOLLECTED:
                preferredSize = 50;
                l10nKey = "overviewColMonth";
                columnClass = Integer.class;
                break;
            case OCCURRENCE_NOTE:
                preferredSize = 150;
                l10nKey = "overviewColOccNote";
                columnClass = String.class;
                break;
            case OCCURRENCE_TIMECOLLECTED:
                preferredSize = 50;
                l10nKey = "overviewColTime";
                columnClass = Date.class;
                break;
            case OCCURRENCE_YEARCOLLECTED:
                preferredSize = 50;
                l10nKey = "overviewColYear";
                columnClass = Integer.class;
                break;
            case PHYTOCHORION_CODE:
                preferredSize = 50;
                l10nKey = "overviewColPhytCode";
                columnClass = String.class;
                break;
            case PHYTOCHORION_NAME:
                preferredSize = 100;
                l10nKey = "overviewColPhyt";
                columnClass = String.class;
                break;
            case PLANT_TAXON:
                preferredSize = 100;
                l10nKey = "overviewColName";
                columnClass = String.class;
                break;
            case PUBLICATION_COLLECTIONNAME:
                preferredSize = 100;
                l10nKey = "overviewColPublication";
                columnClass = String.class;
                break;
            case SELECTION:
                preferredSize = 30;
                l10nKey = "overviewColSelection";
                columnClass = Boolean.class;
                break;
            case TERRITORY_NAME:
                preferredSize = 100;
                l10nKey = "overviewColTerritory";
                columnClass = String.class;
                break;
            default:
                preferredSize = 100;
                l10nKey = "overviewCol";
                columnClass = String.class;
        }
        
    }
    
    public Class getColumnClass() {
        return columnClass;
    }
    
    public int getPreferredSize() {
        return preferredSize;
    }
    
    public void setPreferredSize(int size) {
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