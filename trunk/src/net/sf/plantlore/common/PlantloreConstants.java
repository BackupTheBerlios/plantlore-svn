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
    public static final String ENTITY_AUTHOR            = "author";
    public static final String ENTITY_AUTHOROCCURRENCE  = "authorOccurrence";            
    public static final String ENTITY_HABITAT           = "habitat";
    public static final String ENTITY_HISTORYCHANGE     = "historyChange";
    public static final String ENTITY_HISTORYCOLUMN     = "historyColumn";
    public static final String ENTITY_HISTORYRECORD     = "historyRecord";
    public static final String ENTITY_METADATA          = "metadata";
    public static final String ENTITY_OCCURRENCE        = "occurrence";
    public static final String ENTITY_PHYTOCHORION      = "phytochorion";
    public static final String ENTITY_PLANT             = "plant";
    public static final String ENTITY_PUBLICATION       = "publication";
    public static final String ENTITY_RIGHT             = "right";
    public static final String ENTITY_TERRITORY         = "territory";
    public static final String ENTITY_USER              = "user";
    public static final String ENTITY_VILLAGE           = "village";
    
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
