/*
 * PlantloreConstants.java
 *
 * Created on 22. březen 2006, 1:48
 *
 */

package net.sf.plantlore.common;

/**
 *
 * @author Tomas Kovarik
 */
public class PlantloreConstants {

    /** Constants with names of entities */
    public static final String ENTITY_AUTHOR            = Table.AUTHOR.toString();
    public static final String ENTITY_AUTHOROCCURRENCE  = Table.AUTHOROCCURRENCE.toString();            
    public static final String ENTITY_HABITAT           = Table.HABITAT.toString();
    public static final String ENTITY_HISTORYCHANGE     = Table.HISTORYCHANGE.toString();
    public static final String ENTITY_HISTORYCOLUMN     = Table.HISTORYCOLUMN.toString();
    public static final String ENTITY_HISTORYRECORD     = Table.HISTORYRECORD.toString();
    public static final String ENTITY_METADATA          = Table.METADATA.toString();
    public static final String ENTITY_OCCURRENCE        = Table.OCCURRENCE.toString();
    public static final String ENTITY_PHYTOCHORION      = Table.PHYTOCHORION.toString();
    public static final String ENTITY_PLANT             = Table.PLANT.toString();
    public static final String ENTITY_PUBLICATION       = Table.PUBLICATION.toString();
    public static final String ENTITY_RIGHT             = Table.RIGHT.toString();
    public static final String ENTITY_TERRITORY         = Table.TERRITORY.toString();
    public static final String ENTITY_USER              = Table.USER.toString();
    public static final String ENTITY_VILLAGE           = Table.VILLAGE.toString();
    
    public enum Table {AUTHOR, 
    AUTHOROCCURRENCE,
    HABITAT,
    HISTORYCHANGE,
    HISTORYCOLUMN,
    HISTORYRECORD,
    METADATA,
    OCCURRENCE,
    PHYTOCHORION,
    PLANT,
    PUBLICATION,
    RIGHT,
    TERRITORY,
    USER,
    VILLAGE};
    
    /** Constants for restrictions in select query */
    public static final int RESTR_BETWEEN       = 1;
    public static final int RESTR_EQ            = 2;
    public static final int RESTR_EQ_PROPERTY   = 3;           
    public static final int RESTR_GE            = 4;
    public static final int RESTR_GE_PROPERTY   = 5;    
    public static final int RESTR_GT            = 6;    
    public static final int RESTR_GT_PROPERTY   = 7;    
    public static final int RESTR_ILIKE         = 8;
    public static final int RESTR_IN            = 9;
    public static final int RESTR_IS_EMPTY      = 10;
    public static final int RESTR_IS_NOT_EMPTY  = 11;    
    public static final int RESTR_IS_NULL       = 12;    
    public static final int RESTR_IS_NOT_NULL   = 13;    
    public static final int RESTR_LE            = 14;        
    public static final int RESTR_LE_PROPERTY   = 15;    
    public static final int RESTR_LIKE          = 16;
    public static final int RESTR_LT            = 17;
    public static final int RESTR_LT_PROPERTY   = 18;
    public static final int RESTR_NE            = 19;
    public static final int RESTR_NE_PROPERTY   = 20;    
    
    /** Constants for projections in select query */
    public static final int PROJ_AVG            = 40;
    public static final int PROJ_COUNT          = 41;
    public static final int PROJ_COUNT_DISTINCT = 42;
    public static final int PROJ_GROUP          = 43;
    public static final int PROJ_MAX            = 44;
    public static final int PROJ_MIN            = 45;
    public static final int PROJ_PROPERTY       = 46;       
    public static final int PROJ_ROW_COUNT      = 47;
    public static final int PROJ_SUM            = 48;
    public static final int PROJ_DISTINCT       = 49;
    
    /** Constants for fetch mode of associated entities */
    public static final int FETCH_JOIN            = 50;
    public static final int FETCH_SELECT          = 51;
    
    /** Constants for the direction of results ordering */
    public static final int DIRECT_ASC            = 60;
    public static final int DIRECT_DESC           = 61;    
            
    public static final int INSERT                = 1;
    public static final int UPDATE                = 2;
    public static final int DELETE                = 3;
    
    /** Creates a new instance of PlantloreConstants */
    public PlantloreConstants() {
    }
    
}
