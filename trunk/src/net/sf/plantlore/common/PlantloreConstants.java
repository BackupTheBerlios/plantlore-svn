/*
 * PlantloreConstants.java
 *
 * Created on 22. březen 2006, 1:48
 *
 */

package net.sf.plantlore.common;

import java.util.HashMap;
import java.util.Map;
import net.sf.plantlore.common.record.*;

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
    
    
    /**
	 * Some record may have been intended for a certain action.
	 * These can be:
	 * <ul>
	 * <li><b>UNKNOWN</b> - usually means the default action,</li>
	 * <li><b>INSERT</b> - the record should be inserted, or updated if it is already in the database,</li>
	 * <li><b>UPDATE</b> - the record should be updated, or inserted if it is not in the database,</li>
	 * <li><b>DELETE</b> - the record should be deleted.</li>
	 * </ul>
	 * The Default Director may use this information to perform a certain action
	 * more cleanly.
	 * 
	 * @author kaimu
	 * @since 2006-05-08
	 */
	public static enum Intention { UNKNOWN, INSERT, DELETE, UPDATE }


	public static final Map<Class, Table> classToTable = new HashMap<Class, Table>(20);
    
    static {
    	classToTable.put(AuthorOccurrence.class, Table.AUTHOROCCURRENCE);
    	classToTable.put(Habitat.class, Table.HABITAT);
    	classToTable.put(Metadata.class, Table.METADATA);
    	classToTable.put(Occurrence.class, Table.OCCURRENCE);
    	classToTable.put(Phytochorion.class, Table.PHYTOCHORION);
    	classToTable.put(Plant.class, Table.PLANT);
    	classToTable.put(Publication.class, Table.PUBLICATION);
    	classToTable.put(Territory.class, Table.TERRITORY);
    	classToTable.put(NearestVillage.class, Table.VILLAGE);
    }
    
    
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
    
    public static final int SUBQUERY_GEALL      = 30;
    public static final int SUBQUERY_LEALL      = 31;   
    public static final int SUBQUERY_IN         = 32;
    
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
    
    /** Constants for JOIN type in createAlias() */
    public static final int FULL_JOIN             = 55;
    public static final int INNER_JOIN            = 56;
    public static final int LEFT_OUTER_JOIN       = 57;
    public static final int RIGHT_OUTER_JOIN      = 58;    
    
    /** Constants for the direction of results ordering */
    public static final int DIRECT_ASC            = 60;
    public static final int DIRECT_DESC           = 61;    
            
    public static final int INSERT                = 1;
    public static final int UPDATE                = 2;
    public static final int DELETE                = 3;
    
    /** Constants used for Preferences keys */
    public static final String PREF_DYNAMIC_PAGE_SIZE = "Overview.dynamicPageSize";
    
    /** Default port numbers for different databases */
    public static final String POSTGRE_PORT = "5432";
    public static final String FIREBIRD_PORT = "3050";
    public static final String MYSQL_PORT = "3306";
    public static final String ORACLE_PORT = "1521";        
        
}
