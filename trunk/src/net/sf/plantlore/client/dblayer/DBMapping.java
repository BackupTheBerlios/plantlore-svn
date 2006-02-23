/*
 * DBMapping.java
 *
 * Created on 15. leden 2006, 21:45
 *
 */

package net.sf.plantlore.client.dblayer;

import java.util.HashSet;
import java.util.Hashtable;
import org.apache.log4j.Logger;

/**
 *  Class containing mapping of the database to the entities in the application. Not every entity is represented by a single table, therefore
 *  we need a mapping from entities to tables and columns. This class also contains list of entities (types) which can be used.
 *
 *  @author Tomas Kovarik
 *  @version 0.1, Jan 15, 2006
 */
public class DBMapping {
    /** Mapping of entities */
    private Hashtable USER;
    private Hashtable AUTHOR;
    private Hashtable PLANT;
    private Hashtable PUBLICATION;
    private Hashtable METADATA;
    private Hashtable HABITAT;
    private Hashtable OCCURRENCE;  
    private Hashtable HISTORY;
    
    public static final int USERRECORD = 1;
    public static final int AUTHORRECORD = 2;
    public static final int PLANTRECORD = 3;
    public static final int PUBLICATIONRECORD = 4;
    public static final int METADATARECORD = 5;
    public static final int HABITATRECORD = 6;    
    public static final int OCCURENCERECORD = 7;  
    public static final int HISTORYRECORD = 8;
    
    /** List of tables for available entities*/
    private String AUTHOR_TABLES = "TAUTHORS";
    private String USER_TABLES = "TUSER, TRIGHT";
    private String PLANT_TABLES = "TPLANTS";
    private String PUBLICATION_TABLES = "TPUBLICATIONS";
    private String METADATA_TABLES = "TMETADATA";
    private String HABITAT_TABLES = "THABITATS, TVILLAGES, TPHYTOCHORIA, TTERRITORIES";
    private String OCCURENCE_TABLES = "TOCCURRENCES, TMETADATA, TPLANTS, TPUBLICATIONS, THABITATS, TUSER";
    private String HISTORY_TABLES = "THISTORY JOIN THISTORYCOLUMN JOIN THISTORYCHANGE ON (THISTORY.ccolumnid = THISTORYCOLUMN.cid) ON (THISTORY.cchangeid = THISTORYCHANGE.cid) ";
    
    /** Instance of a logger */
    private Logger logger;
    
    /**
     * Create a new instance of DBMapping. Initialize mapping of entities.
     */
    public DBMapping() {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        initUser();
        initAuthor();
        initPlant();
        initPublication();
        initMetadata();
        initHabitat();
        initOccurrence();
        initHistory();
    }
        
    /**
     *  Return list of the tables involved for the given type.
     *
     *  @param type type of the record (entity)
     *  @return String representation of the tables, multiple tables are separated with a comma. Every table comes with an
     *          alias equal to the table name
     *  @throws DBLayerException in case no table is defined for the given type
     */
    public String getTableName(int type) throws DBLayerException {
        switch (type) {
            case USERRECORD:
                return this.USER_TABLES;                
            case AUTHORRECORD:
                return this.AUTHOR_TABLES;                
            case PLANTRECORD:
                return this.PLANT_TABLES;                
            case PUBLICATIONRECORD:
                return this.PUBLICATION_TABLES;                
            case METADATARECORD:
                return this.METADATA_TABLES;                
            case HABITATRECORD:
                return this.HABITAT_TABLES;                
            case OCCURENCERECORD:
                return this.OCCURENCE_TABLES;  
            case HISTORYRECORD:
                return this.HISTORY_TABLES;
            default:
                logger.error("No table defined for type "+type);
                throw new DBLayerException("No table defined for type "+type);                
        }
    }
    
    /**
     *  Get the name of the database column represented by the given key in the mapping.
     *
     *  @param key  Key to look for in the mapping
     *  @param type type of the record (entity)
     *  @throws DBLayerException in case that wrong type is given or the specified key could not be found
     */
    public String getFieldName(String key, int type) throws DBLayerException {
        String value;

        switch (type) {
            case USERRECORD:
                value = getUserField(key);
                break;
            case AUTHORRECORD:
                value = getAuthorField(key);
                break;
            case PLANTRECORD:
                value = getPlantField(key);
                break;
            case PUBLICATIONRECORD:
                value = getPublicationField(key);
                break;
            case METADATARECORD:
                value = getMetadataField(key);
                break;
            case HABITATRECORD:
                value = getHabitatField(key);
                break;
            case OCCURENCERECORD:
                value = getOccurrenceField(key);
                break;
            case HISTORYRECORD:
            	value = getHistoryField(key);
            	break;
            default:            
                logger.error("No fields defined for type "+type);
                throw new DBLayerException("No fields defined for type "+type);
        }

        if (value == null) {
            logger.error("Key '"+key+"' not found in DB mapping for type "+type);
            throw new DBLayerException("Key '"+key+"' not found in DB mapping for type "+type);
        }
        return value;
    }
    
    /**
     *  Get a specified field from user mapping.
     *
     *  @param key  String key representing a database column
     *  @return     name of the database column. If the given key is not found <code>null</code> is returned
     */
    private String getUserField(String key) {
        if (USER.contains(key)) {
            return (String)USER.get(key);
        } else {
            return null;
        }
    }
    
    /**
     *  Get a specified field from author mapping.
     *
     *  @param key  String key representing a database column
     *  @return     name of the database column. If the given key is not found <code>null</code> is returned
     */
    private String getAuthorField(String key) {
        if (AUTHOR.containsKey(key)) {
            return (String)AUTHOR.get(key);
        } else {
            return null;
        }
    }
    
    /**
     *  Get a specified field from plant mapping.
     *
     *  @param key  String key representing a database column
     *  @return     name of the database column. If the given key is not found <code>null</code> is returned
     */
    private String getPlantField(String key) {
        if (PLANT.contains(key)) {
            return (String)PLANT.get(key);
        } else {
            return null;
        }
    }
    
    /**
     *  Get a specified field from publication mapping.
     *
     *  @param key  String key representing a database column
     *  @return     name of the database column. If the given key is not found <code>null</code> is returned
     */
    private String getPublicationField(String key) {
        if (PUBLICATION.contains(key)) {
            return (String)PUBLICATION.get(key);
        } else {
            return null;
        }
    }
    
    /**
     *  Get a specified field from metadata mapping.
     *
     *  @param key  String key representing a database column
     *  @return     name of the database column. If the given key is not found <code>null</code> is returned
     */
    private String getMetadataField(String key) {
        if (METADATA.contains(key)) {
            return (String)METADATA.get(key);
        } else {
            return null;
        }
    }

    /**
     *  Get a specified field from habitat mapping.
     *
     *  @param key  String key representing a database column
     *  @return     name of the database column. If the given key is not found <code>null</code> is returned
     */
    private String getHabitatField(String key) {
        if (HABITAT.contains(key)) {
            return (String)HABITAT.get(key);
        } else {
            return null;
        }
    }

    /**
     *  Get a specified field from occurrence mapping.
     *
     *  @param key  String key representing a database column
     *  @return     name of the database column. If the given key is not found <code>null</code> is returned
     */
    private String getOccurrenceField(String key) {
        if (OCCURRENCE.contains(key)) {
            return (String)OCCURRENCE.get(key);
        } else {
            return null;
        }
    }

    /**
     *  Get a specified field from history mapping.
     *
     *  @param key  String key representing a database column
     *  @return     name of the database column. If the given key is not found <code>null</code> is returned
     */
    private String getHistoryField(String key) {       
    	if (HISTORY.containsKey(key)) {
    		System.out.println((String)HISTORY.get(key));
            return (String)HISTORY.get(key);
        } else {        	
            return null;
        }
    }    
    
    /**
     *  Initialization of the mapping of Users.
     */
    private void initUser() {
        // Initialize USER hashtable
        USER = new Hashtable(15);
        USER.put("id", "TUSER.CID");
        USER.put("login", "TUSER.CLOGIN");
        USER.put("firstname", "TUSER.CFIRSTNAME");
        USER.put("surname", "TUSER.CSURNAME");
        USER.put("email", "TUSER.CEMAIL");
        USER.put("address", "TUSER.CADDRESS");
        USER.put("whencreated", "TUSER.CWHENCREATE");
        USER.put("whendropped", "TUSER.CWHENDROP");
        USER.put("export", "TRIGHT.CEXPORT");
        USER.put("import", "TRIGHT.CIMPORT");
        USER.put("role", "TRIGHT.CROLE");
        USER.put("note", "CNOTE");
    }
    
    /**
     *  Initialization of the mapping of Authors.
     */
    private void initAuthor() {
        AUTHOR = new Hashtable(15);
        AUTHOR.put("id", "TAUTHORS.CID");        
        AUTHOR.put("firstname", "TAUTHORS.CFIRSTNAME");
        AUTHOR.put("surname", "TAUTHORS.CSURNAME");
        AUTHOR.put("wholename", "TAUTHORS.CWHOLENAME");
        AUTHOR.put("organization", "TAUTHORS.CORGANIZATION");
        AUTHOR.put("role", "TAUTHORS.CROLE");
        AUTHOR.put("address", "TAUTHORS.CADDRESS");
        AUTHOR.put("phonenumber", "TAUTHORS.CPHONENUMBER");
        AUTHOR.put("email", "TAUTHORS.CEMAIL");
        AUTHOR.put("url", "TAUTHORS.CURL");
        AUTHOR.put("note", "TAUTHORS.CNOTE");
    }
    
    /**
     *  Initialization of the mapping of Plants.
     */
    private void initPlant() {
        PLANT = new Hashtable(10);
        PLANT.put("id", "TPLANTS.CID");        
        PLANT.put("adoptedname","TPLANTS.CADOPTEDNAME");
        PLANT.put("czechname","TPLANTS.CCZECHNAME");
        PLANT.put("publishablename","TPLANTS.CPUBLISHABLENAME");
        PLANT.put("abbreviation","TPLANTS.CABBREVIATION");
        PLANT.put("note","TPLANTS.CNOTE");
    }
    
    /**
     *  Initialization of the mapping of Publications.
     */
    private void initPublication() {
        PUBLICATION = new Hashtable(10);
        PUBLICATION.put("id", "TPUBLICATIONS.CID");                
        PUBLICATION.put("collectionname","TPUBLICATIONS.CCOLLECTIONNAME");
        PUBLICATION.put("publicationyear","TPUBLICATIONS.CCOLLECTIONYEARPUBLICATION");
        PUBLICATION.put("journalname","TPUBLICATIONS.CJOURNALNAME");
        PUBLICATION.put("journalauthor","TPUBLICATIONS.CJOURNALAUTHORNAME");
    }
    
    private void initMetadata() {
        METADATA = new Hashtable(20);
        METADATA.put("id","TMETADATA.CID");
        METADATA.put("techcontactname","TMETADATA.CTECHNICALCONTACTNAME");
        METADATA.put("techcontactemail","TMETADATA.CTECHNICALCONTACTEMAIL");
        METADATA.put("techcontactaddress","TMETADATA.CTECHNICALCONTACTADDRESS");
        METADATA.put("contentcontactname","TMETADATA.CCONTENTCONTACTNAME");
        METADATA.put("contentcontactemail","TMETADATA.CCONTENTCONTACTEMAIL");
        METADATA.put("contentcontactaddress","TMETADATA.CCONTENTCONTACTADDRESS");
        METADATA.put("datasettitle","TMETADATA.CDATASETTITLE");
        METADATA.put("datasetdetails","TMETADATA.CDATASETDETAILS");
        METADATA.put("sourceinstitutionid","TMETADATA.CSOURCEINSTITUTIONID");
        METADATA.put("sourceid","TMETADATA.CSOURCEID");
        METADATA.put("ownerorganizationabbrev","TMETADATA.COWNERORGANIZATIONABBREV");
        METADATA.put("datecreated","TMETADATA.CDATECREATE");
        METADATA.put("datemodified","TMETADATA.CDATEMODIFIED");
        METADATA.put("language","TMETADATA.CLANGUAGE");
        METADATA.put("recordbasis","TMETADATA.CRECORDBASIS");
    }
    
    private void initHabitat() {
        HABITAT = new Hashtable(15);
        HABITAT.put("id","THABITATS.CID");
        HABITAT.put("quadrant","THABITATS.CQUADRANT");
        HABITAT.put("description","THABITATS.CDESCRIPTION");
        HABITAT.put("country","THABITATS.CCOUNTRY");
        HABITAT.put("altitude","THABITATS.CALTITUDE");
        HABITAT.put("latitude","THABITATS.CLATITUDE");
        HABITAT.put("longitude","THABITATS.CLONGITUDE");
        HABITAT.put("note","THABITATS.CNOTE");
        HABITAT.put("village","TVILLAGES.CNAME");
        HABITAT.put("phytochoriacode","TPHYTOCHORIA.CCODE");
        HABITAT.put("phytochorianame","TPHYTOCHORIA.CNAME");
        HABITAT.put("territoryname","TTERRITORIES.CNAME");
    }
    
    private void initOccurrence() {
        OCCURRENCE = new Hashtable(15);
        OCCURRENCE.put("id","TOCCURRENCES.CID");
        OCCURRENCE.put("unitiddb","TOCCURRENCES.CUNITIDDB");
        OCCURRENCE.put("unitvalue","TOCCURRENCES.CUNITVALUE");
        OCCURRENCE.put("year","TOCCURRENCES.CYEARCOLLECTED");
        OCCURRENCE.put("month","TOCCURRENCES.CDAYCOLLECTED");
        OCCURRENCE.put("time","TOCCURRENCES.CTIMECOLLECTED");
        OCCURRENCE.put("isodatetimebegin","TOCCURRENCES.CISODATETIMEBEGIN");
        OCCURRENCE.put("datesource","TOCCURRENCES.CDATESOURCE");
        OCCURRENCE.put("herbarium","TOCCURRENCES.CHERBARIUM");
        OCCURRENCE.put("createdwhen","TOCCURRENCES.CCREATEWHEN");
        OCCURRENCE.put("createdwho","TOCCURRENCES.CCREATEWHO");
        OCCURRENCE.put("updatedwhen","TOCCURRENCES.CUPDATEWHEN");
        OCCURRENCE.put("updatedwho","TOCCURRENCES.CUPDATEWHO");
        OCCURRENCE.put("note","TOCCURRENCES.CNOTE");                
    }    
    
    private void initHistory() {
    	HISTORY = new Hashtable(10);
    	HISTORY.put("cid","THISOTRY.CID");
    	HISTORY.put("oldValue","THISTORY.COLDVALUE");
    	HISTORY.put("newValue","THISTORY.CNEWVALUE");
    	HISTORY.put("tableName","THISTORYCOLUMN.CTABLENAME");
    	HISTORY.put("columnName","THISTORYCOLUMN.CCOLUMNNAME");
    	HISTORY.put("occurrenceId","THISTORYCHANGE.COCCURRENCEID");
    	HISTORY.put("recordId","THISTORYCHANGE.CRECORDID");
    	HISTORY.put("operation","THISTORYCHANGE.COPEREATION");
    	HISTORY.put("when","THISTORYCHANGE.CWHEN");
    	HISTORY.put("who","THISTORYCHANGE.CWHO");    	
    }
}
