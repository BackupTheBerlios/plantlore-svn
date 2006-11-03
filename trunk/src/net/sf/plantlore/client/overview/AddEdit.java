/*
 * AddEdit.java
 *
 * Created on 20. duben 2006, 14:26
 *
 */

package net.sf.plantlore.client.overview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Set;
import net.sf.plantlore.common.DBLayerUtils;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.common.record.Record;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.NearestVillage;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.exception.DBLayerException;
import org.apache.log4j.Logger;

/** Model of the AddEdit dialog.
 *
 * Handles all the inner logic of the two dialogs. Gathers data from user, loads
 * data from the database for given occurrence id, stores the data into the 
 * database, creates new occurrences, updates them, deletes them if necessary.
 *
 * The {@link storeRecord()} method should use the DBLayerUtils more so that
 * it would work exactly the same as import does.
 *
 * @author fraktalek
 * @author kaimu (restore/remember default values in the Add dialog for all databases the User works with)
 */
public class AddEdit extends Observable {
    public static final int WGS84 = 0;
    public static final int SJTSK = 1;
    public static final int S42 = 2;
    //WGS-84 max and min value
    public static final double WGS84_MAX_LATITUDE = 90;   //Y    //23°
    public static final double WGS84_MIN_LATITUDE = -90;         //12°
    public static final double WGS84_MAX_LONGITUDE = 180;  //X  //51°10’
    public static final double WGS84_MIN_LONGITUDE = -180;       //47°30’
    public static final double WGS84_MAX_ALTITUDE = 8848;  //Z
    public static final double WGS84_MIN_ALTITUDE = -418;
    //S-JTSK max and min value
    public static final double SJTSK_MAX_Y = 10000000;     //-128000 + 1500000 = 1372000;   
    public static final double SJTSK_MIN_Y = -10000000;   //-930000 + 1500000 = 570000;
    public static final double SJTSK_MAX_X = 10000000;    //-900000 + 1500000 = 600000;  
    public static final double SJTSK_MIN_X =-10000000;   //-1300000 + 1500000 = 20000;
    public static final double SJTSK_MAX_Z = 100000;  
    public static final double SJTSK_MIN_Z = -1000;
    //S-42 max and min value
    public static final double S42_MAX_Y = 10000000;   
    public static final double S42_MIN_Y = -10000000;
    public static final double S42_MAX_X = 10000000;  
    public static final double S42_MIN_X = -10000000;
    public static final double S42_MAX_Z = 10000;  
    public static final double S42_MIN_Z = -1000;
    
    public static final String EMPTY_STRING = L10n.getString("Common.ComboboxNothingSelected");
    public static final Pair<String,Integer> EMPTY_PAIR = new Pair<String,Integer>(EMPTY_STRING,-1);            
     
    private static Logger logger;
    private static DBLayer database;      
    
    private int coordinateSystem = 0;
    private boolean isCancled = true;
    private Occurrence o; //original occurrence
    
    //list of authors user selects
    private ArrayList<Pair<Pair<String,Integer>,String>> authorList = new ArrayList<Pair<Pair<String,Integer>,String>>();
    private ArrayList<String> resultRevision;
    private HashSet<Pair<Integer,String>> originalAuthors;//authors with the same name and different role are different authors for us --> they each have their own AuthorOccurrence record
    
    //list of AuthorOccurrence objects that correspond to our Occurrence object, we need it for update
    //set by <code>getAuthorsOf()</code> method
    //the key is of form role+AuthorId, for example it could be "4collected" or "2identified"
    private HashMap<String,AuthorOccurrence> authorOccurrences;
    private Pair<String,Integer> village;
    
    private ArrayList<String> taxonList;
    private String taxonOriginal;
    
    private String habitatDescription;
    private Integer year;
    private String habitatNote = null;
    private String occurrenceNote = null;
    private Pair<String, Integer> territoryName;
    private Pair<String, Integer> phytName;
    private Pair<String, Integer> phytCode;    
    private String phytCountry;
    private String quadrant = null;
    private Double altitude = null;
    private Double longitude = null;
    private Double latitude = null;
    private String source = null;
    private Pair<String,Integer> publication;
    private String herbarium = null;
    private Pair<String,Integer> project;
    private Integer month = null;
    private Integer day = null;
    private Date time = null;    
    private Occurrence[] habitatSharingOccurrences = null;
    
    //---variables for old habitat values---
    private String habitatDescriptionOld;
    private String habitatNoteOld = null;
    private Pair<String, Integer> territoryNameOld;
    private Pair<String, Integer> phytNameOld;
    private Pair<String, Integer> phytCodeOld;    
    private String phytCountryOld;
    private String quadrantOld = null;
    private Double altitudeOld = null;
    private Double longitudeOld = null;
    private Double latitudeOld = null;
    private Pair<String,Integer> villageOld;
    //--------------------------------------
    
    private boolean dayValid = true;
    private boolean timeValid = true;
    
    private boolean altitudeValid = true;
    private boolean latitudeValid = true;
    private boolean longitudeValid = true;
    
    private Pair<String, Integer>[] plants = null;
    private Pair<String, Integer>[] authors = null;
    private String[] authorRoles = null;
    private Pair<String, Integer>[] villages = null;
    private Pair<String, Integer>[] territories = null;
    private Pair<String, Integer>[] phytNames = null;
    private Pair<String, Integer>[] phytCodes = null;
    private String[] countries = null;
    private String[] sources = null;
    private Pair<String, Integer>[] publications = null;
    private Pair<String, Integer>[] projects = null;

    //helper variable to avoid recursion potentially caused by phytCode and phytName updates
    private Boolean skipUpdate = false;
    private Boolean editMode = false;
    private Boolean preloadAuthors = false;
    
    private OccurrenceTableModel occurrenceTableModel;     
    
    /** Creates a new instance of AddEdit */
    public AddEdit(DBLayer database, Boolean editMode) {
        this.database = database;
        this.editMode = editMode;
        logger = Logger.getLogger(this.getClass().getPackage().getName());      
        occurrenceTableModel = new OccurrenceTableModel();
        
        // Load the default values from a file. Should work for the Add mode only!
        if( !editMode )
            getDefaultValues( true );
    }
 
    /** Makes the model load data from for the occurrence with id occurrenceId.
     *
     * @param occurrenceId id of the occurrence to load
     *
     * @throws DBLayerException
     * @throws RemoteException
     *
     * @return >=0 if everything was OK
     * @return -1 if the Occurrence table overflew
     */
    public int loadRecord(Integer occurrenceId) throws DBLayerException, RemoteException {
        logger.debug("Loading AddEdit data for occurrence id "+occurrenceId);
        
        clearOccurrence();
        clearLocation();
        
        DBLayerUtils dlu = new DBLayerUtils(database);
        this.o = (Occurrence) dlu.getObjectFor(occurrenceId, Occurrence.class);
        coordinateSystem = WGS84;
        
        authorList = getAuthorsOf(o);
        originalAuthors = new HashSet<Pair<Integer,String>>();
        Pair<Pair<String,Integer>,String> p;
        Pair<String,Integer> a;
        for (int i = 0; i < authorList.size(); i++) {
            p = authorList.get(i);
            a = p.getFirst();
            Integer id = a.getSecond();
            String role = p.getSecond();
                        
            originalAuthors.add(new Pair<Integer,String>(id,role));
        }
                
        village = new Pair(o.getHabitat().getNearestVillage().getName(), o.getHabitat().getNearestVillage().getId());
        villageOld = new Pair(o.getHabitat().getNearestVillage().getName(), o.getHabitat().getNearestVillage().getId());
        
        taxonList = new ArrayList();
        taxonOriginal = o.getPlant().getTaxon();
        taxonList.add(taxonOriginal);
        
        habitatDescription = o.getHabitat().getDescription();
        habitatDescriptionOld = o.getHabitat().getDescription();
        year = o.getYearCollected();
        
        occurrenceNote = o.getNote();
        habitatNote = o.getHabitat().getNote();
        territoryName = new Pair(o.getHabitat().getTerritory().getName(),o.getHabitat().getTerritory().getId());
        phytName = new Pair(o.getHabitat().getPhytochorion().getName(), o.getHabitat().getPhytochorion().getId());
        phytCode = new Pair(o.getHabitat().getPhytochorion().getCode(), o.getHabitat().getPhytochorion().getId());
        phytCountry = o.getHabitat().getCountry();
        quadrant = o.getHabitat().getQuadrant();
        altitude = o.getHabitat().getAltitude();
        longitude = o.getHabitat().getLongitude();
        latitude = o.getHabitat().getLatitude();

        habitatNoteOld = o.getHabitat().getNote();
        territoryNameOld = new Pair(o.getHabitat().getTerritory().getName(),o.getHabitat().getTerritory().getId());
        phytNameOld = new Pair(o.getHabitat().getPhytochorion().getName(), o.getHabitat().getPhytochorion().getId());
        phytCodeOld = new Pair(o.getHabitat().getPhytochorion().getCode(), o.getHabitat().getPhytochorion().getId());
        phytCountryOld = o.getHabitat().getCountry();
        quadrantOld = o.getHabitat().getQuadrant();
        altitudeOld = o.getHabitat().getAltitude();
        longitudeOld = o.getHabitat().getLongitude();
        latitudeOld = o.getHabitat().getLatitude();

        source = o.getDataSource();
        if (o.getPublication() != null)
            publication = new Pair(o.getPublication().getReferenceCitation(), o.getPublication().getId());
        herbarium = o.getHerbarium();
        month = o.getMonthCollected();
        day = o.getDayCollected();
        time = o.getTimeCollected();
        project = new Pair<String,Integer>(o.getMetadata().getDataSetTitle(), o.getMetadata().getId());
        
        //we also must determine (again) who shares habitat data with us
        loadHabitatSharingOccurrences();

        int result = occurrenceTableModel.load(o.getHabitat().getId());
        if (result < 0)
            return -1;
        else
            return 0;
    }
    
    /** Preloads habitat data. Used in add mode when adding into a habitat (using the habitat tree).
     *
     * @throws DBLayerException
     * @throws RemoteException
     *
     * @return >=0 if everything was OK
     * @return -1 if the Occurrence table overflew
     */
    public int setHabitat(Integer habitatId) throws DBLayerException, RemoteException {
        logger.debug("Loading AddEdit habitat data for habitat id "+habitatId);
        
        DBLayerUtils dlu = new DBLayerUtils(database);
        Habitat h = (Habitat) dlu.getObjectFor(habitatId, Habitat.class);
                
        village = new Pair(h.getNearestVillage().getName(), h.getNearestVillage().getId());                
        habitatDescription = h.getDescription();
        habitatNote = h.getNote();
        territoryName = new Pair(h.getTerritory().getName(),h.getTerritory().getId());
        phytName = new Pair(h.getPhytochorion().getName(), h.getPhytochorion().getId());
        phytCode = new Pair(h.getPhytochorion().getCode(), h.getPhytochorion().getId());
        phytCountry = h.getCountry();
        quadrant = h.getQuadrant();
        altitude = h.getAltitude();
        longitude = h.getLongitude();
        latitude = h.getLatitude();

        int result = occurrenceTableModel.load(h.getId());
        if (result < 0)
            return -1;
        else
            return 0;        
    }//setHabitat

    /** Convenience method for {@link hasHabitatChanged()}.
     * @param o1 object to compare
     * @param o2 object to compare
     *
     * @return true if o1.equals(o2) or both objects are null
     * @return false otherwise
     */
    private boolean equal(Object o1, Object o2) {
        if (o1 == null && o2 != null)
            return false;
        return (o1 == null && o2 == null) || o1.equals(o2);
    }
    
    /** Determines whether the habitat changed from the last call to {@link setRecord()} method.
     *
     * We need to keep track of changes to habitat for the case that it is shared by two and more occurrences. 
     * In that case we have to ask user whether he wants to divide this habitat or change it for all occurrences.
     *
     * Compares values preserved in *Old habitat variables with the actual habitat variables.
     *
     * @return true if all every habitat variable is {@link equal()} to it's counterpart.
     */
    public boolean hasHabitatChanged() {
    return !( 
            equal(habitatDescriptionOld,habitatDescription) &&
            equal(habitatNoteOld,habitatNote)  &&
            equal(territoryNameOld,territoryName) &&
            equal(phytNameOld,phytNameOld) &&
            equal(phytCodeOld,phytCode) &&
            equal(phytCountryOld,phytCountry) &&
            equal(quadrantOld,quadrant) &&
            equal(altitudeOld,altitude) &&
            equal(longitudeOld,longitude) &&
            equal(latitudeOld,latitude) &&
            equal(villageOld,village)
            );
    }
    
    /** Author getter. */
    public Pair<String, Integer> getAuthor(int i) {
        return ((Pair<Pair<String,Integer>,String>)authorList.get(i)).getFirst();
    }
    
    /** Author role getter. */
    public String getAuthorRole(int i) {
        return ((Pair<Pair<String,Integer>,String>)authorList.get(i)).getSecond();
    }
    
    /** Determines the number of author that the model holds.
     * @return the number of authors
     */
    public int getAuthorCount() {
        return authorList == null ? 0 : authorList.size();
    }
    
    /** Adds an author to the list of authors held by the model.
     *
     * @param author the author to be added
     */
    public void addAuthor(Pair<Pair<String, Integer>,String> author) {
        authorList.add(author);
        logger.debug("Added author "+author.getFirst()+" as "+author.getSecond());
    }
    
    /** Village getter. */
    public Pair<String, Integer> getVillage() {
        return village;
    }

    /** Village setter. */
    public void setVillage(Pair<String, Integer> village) {
        this.village = village;
        logger.debug("NearestVillage set to "+village);
    }

    /** Taxon getter. */
    public String getTaxon(int i) {
        if (taxonList == null || taxonList.size() == 0)
            return "";
        
        return (String) taxonList.get(i);
    }

    /** Habitat description getter. */
    public String getHabitatDescription() {
        return habitatDescription;
    }

    /** Habitat description setter. */
    public void setHabitatDescription(String habitatDescription) {
        this.habitatDescription = habitatDescription;
        logger.debug("HabitatDescription set to "+habitatDescription);
    }

    /** Year getter. 
     * In case the year hasn't been set yet or was set to null this method
     * determines the current year and returns it.
     */
    public Integer getYear() {
        if (year == null)
            year = Calendar.getInstance().get(Calendar.YEAR);
        return year;
    }

    /** Year setter. */
    public void setYear(Integer year) {
        this.year = year;
        logger.debug("Year set to "+ year);
        setChanged();
        notifyObservers("YEAR_CHANGED");
    }

    /** Habitat note getter. */
    public String getHabitatNote() {
        return habitatNote;
    }

    /** Habitat note setter. */
    public void setHabitatNote(String habitatNote) {
        this.habitatNote = habitatNote;
        logger.debug("HabitatNote set to "+ habitatNote);
    }

    /** Occurrence note getter. */
    public String getOccurrenceNote() {
        return occurrenceNote;
    }

    /** Occurrence note setter. */
    public void setOccurrenceNote(String occurrenceNote) {
        this.occurrenceNote = occurrenceNote;
        logger.debug("OccurrenceNote set to "+occurrenceNote);
    }

    /** Territory name getter. */
    public Pair<String, Integer> getTerritoryName() {
        return territoryName;
    }

    /** Territory name setter. */
    public void setTerritoryName(Pair<String, Integer> territoryName) {
        this.territoryName = territoryName;
        logger.debug("TerritoryName set to "+territoryName);
    }

    /** Phytochorion name getter. */
    public Pair<String, Integer> getPhytName() {
        return phytName;
    }

    /** Phytochorion name setter. 
     * Also updates the phytochorion code and notifies observers about that change.
     */
    public void setPhytName(Pair<String, Integer> phytName) {
        if (phytName == null)
            return;
        if (skipUpdate) {
            skipUpdate = false;
            logger.debug("Skipping setPhytName");
            return;
        }
        this.phytName = phytName;

        
        if (phytCodes != null)
            for (int i=0; i < phytCodes.length; i++) {
                if (phytCodes[i].getSecond().equals(phytName.getSecond())) {
                    phytCode = phytCodes[i];
                    logger.debug("SetPhytName For "+phytName+" found "+phytCode);
                    skipUpdate = true;
                    break;
                } 
            }
        else return;
        logger.debug("PhytName set to "+phytName);
        setChanged();
        notifyObservers(new Pair<String,Integer>("updateCode",-1));
    }
    
    /** Phytochorion code getter. */
    public Pair<String, Integer> getPhytCode() {
        return phytCode;
    }

    /** Phytochorion code setter. 
     * Also updates the phytochorion name and notifies observers about that change.
     */
    public void setPhytCode(Pair<String, Integer> phytCode) {
        if (phytCode == null)
            return;
        
        if (skipUpdate) {
            skipUpdate = false;
            logger.debug("Skipping setPhytCode");
            return;
        }
        this.phytCode = phytCode;


        if (phytNames != null)
            for (int i=0; i < phytNames.length; i++) {
                if (phytNames[i].getSecond().equals(phytCode.getSecond())) {
                    phytName = phytNames[i];
                    logger.debug("SetPhytCode For "+phytCode+" found "+phytName);
                    skipUpdate = true;
                    break;
                }
            }
        else
            return;
        logger.debug("PhytCode set to "+phytCode);
        setChanged();
        notifyObservers(new Pair<String,Integer>("updateName",-1));
    }

    /** Phytochorion country getter. */
    public String getPhytCountry() {
        return phytCountry;
    }

    /** Phytochorion country setter. */
    public void setPhytCountry(String phytCountry) {
        if (phytCountry != null && !phytCountry.equals(EMPTY_STRING)) {
            this.phytCountry = phytCountry;
            logger.debug("PhytCountry set to "+phytCountry);
        } else {
            this.phytCountry = null;
            logger.debug("PhytCountry set to null");
        }
    }

    /** Quadrant getter. */
    public String getQuadrant() {
        return quadrant;
    }

    /** Quadrant getter. */
    public void setQuadrant(String quadrant) {
        this.quadrant = quadrant;
        logger.debug("Quadrant set to "+ quadrant);
    }

    /** Altitude getter. */
    public Double getAltitude() {
        return altitude;
    }

    /** Altitude setter. */
    public void setAltitude(Double altitude) {
        this.altitude = altitude;
        logger.debug("Altitude set to "+altitude);
        altitudeValid = true;
    }

    /** Sets whether the value returned by {@link getAltitude()} should be considered valid.
     */
    public void setAltitudeValid(boolean valid) {
        altitudeValid = valid;
        if (valid)
            logger.debug("Altitude set VALID");
        else
            logger.debug("Altitude set INVALID");
    }

    /** Longitude getter. */
    public Double getLongitude() {
        return longitude;
    }

    /** Longitude setter. */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
        logger.debug("Longitude set to "+longitude);
        longitudeValid = true;
    }

    /** Sets whether the value returned by {@link getLongitude()} should be considered valid.
     */
    public void setLongitudeValid(boolean valid) {
        longitudeValid = valid;
        if (valid)
            logger.debug("Longitude set VALID");
        else
            logger.debug("Longitude set INVALID");
    }
    
    /** Latitude getter. */
    public Double getLatitude() {
        return latitude;
    }

    /** Latitude setter. */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
        logger.debug("Latitude set to "+latitude);
        latitudeValid = true;
    }

    /** Sets whether the value returned by {@link getLatitude()} should be considered valid.
     */
    public void setLatitudeValid(boolean valid) {
        latitudeValid = valid;
        if (valid)
            logger.debug("Latitude set VALID");
        else
            logger.debug("Latitude set INVALID");
    }
    
    /** Source getter. */
    public String getSource() {
        return source;
    }

    /** Source setter. 
     * If the parameter source euqals to {@link EMPTY_STRING} then it sets the source to null.
     */
    public void setSource(String source) {
        if (source != null && !source.equals(EMPTY_STRING)) {
            this.source = source;
            logger.debug("Source set to "+source);
        } else {
            this.source = null;
            logger.debug("Source set to null.");
        }
    }

    /** Publication getter. */
    public Pair<String, Integer> getPublication() {
        return publication;
    }

    /** Publication setter.
     * If the parameter source euqals to {@link EMPTY_STRING} then it sets the publication to null.
     */
    public void setPublication(Pair<String, Integer> publication) {
        if (publication != null && !publication.equals(EMPTY_PAIR)) {
            this.publication = publication;
            logger.debug("Publication set to "+publication);
        } else {
            this.publication = null;
            logger.debug("Publication set to null");
        }
    }

    /** Herbarium getter. */
    public String getHerbarium() {
        return herbarium;
    }

    /** Herbarium setter. */
    public void setHerbarium(String herbarium) {
        this.herbarium = herbarium;
        logger.debug("Herbarium set to "+herbarium);
    }

    /** Month getter. */
    public Integer getMonth() {
        return month;
    }

    /** Month setter. 
     * Notifies observers that the day chooser should be updated.
     */
    public void setMonth(Integer month) {
        this.month = month;
        logger.debug("Month set to "+month);
        setChanged();
        notifyObservers("updateDayChooser");
    }

    /** Day getter. */
    public Integer getDay() {
        return day;
    }

    /** Sets the model's day variable and sets <code>dayValid</code> to true
     *
     */
    public void setDay(Integer day) {
        this.day = day;
        logger.debug("Day set to "+day);
        setDayValid(true);
    }
    
    /** Sets whether the value returned by {@link getDay()} method should be considered valid.
     *
     */
    public void setDayValid(boolean valid) {
        if (valid)
            logger.debug("Day set VALID.");
        else
            logger.debug("Day set INVALID.");                    
        dayValid = valid;
    }

    /** Time getter. */
    public Date getTime() {
        return time;
    }

    /** Sets the model's time variable and sets <code>timeValid</code> to true
     *
     */
    public void setTime(Date time) {
        this.time = time;
        logger.debug("Time set to "+time);
        setTimeValid(true);
    }
    
    /** Sets whether the value returned by {@link getTime()} method should be considered valid.
     *
     */
    public void setTimeValid(boolean valid) {
        if (valid)
            logger.debug("Time set VALID.");
        else
            logger.debug("Time set INVALID.");                    
        timeValid = valid;
    }

    /** DBLayer getter. */
    public DBLayer getDatabase() {
        return database;
    }

    /** Sets new database layer.
     * Now it actually only notifies  about a reconnect thanks to the change
     *that the DBLayer is now only a proxy to the actual DBLayer.
     */
    public void setDatabase(DBLayer database) {
        this.database = database;
        occurrenceTableModel.setDBLayer(database);
    }

    /** Project getter.     */
    public Pair<String, Integer> getProject() {
        return project;
    }

    /** Project setter. */
    public void setProject(Pair<String, Integer> project) {
        logger.debug("Project set to "+project);
        this.project = project;
    }

    /** Coordinate system getter. */
    public int getCoordinateSystem() {
        return coordinateSystem;
    }

    /** Coordinate system setter. */
    public void setCoordinateSystem(int coordinateSystem) {
        this.coordinateSystem = coordinateSystem;       
        switch (coordinateSystem) {
            case WGS84:
                logger.debug("CoordinateSystem set to WGS84");
                break;
            case S42:
                logger.debug("CoordinateSystem set to S42");
                break;
            case SJTSK:
                logger.debug("CoordinateSystem set to SJTSK");
                break;
        }
    }
    
    /** Helper method to find id of given taxon according to <code>plants[]</code>
     *
     * @return Id of the taxon if found
     * @return -1 if not found
     */
    private Integer lookupPlant(String taxon) {
        if (plants == null)
            return -1;
        
        logger.debug("Looking up id for #"+taxon+"#");
        for (int i=0; i < plants.length ; i++) {
            if (taxon.equals(plants[i].getFirst())) {
                return plants[i].getSecond();
            }
        }
        return -1;
    }
    
    /** Pre-processes data gathered from the user.
     *
     * @param author one of the authors of this occurrence to be processed
     * @param newRecord if true then new record is to be created - e.g. we are in Add mode, otherwise the record is updated
     * @param updateAllPlants if true then the shared habitat is updated, if false then a new habitat is created and asociated with our AuthorOccurrence object o. Has only sense if newRecord is true.
     *
     * @return AuthorOccurrence the object that will be created or updated
     * @return true the object has to be updated
     * @return false the object has to be created
     */
    private Occurrence prepareNewOccurrence(String taxon, Habitat h) throws DBLayerException, RemoteException {
        DBLayerUtils dlu = new DBLayerUtils(database);
        Occurrence occ;
        Author a;
        Metadata m;
        Plant plant;
        Publication publ ;
        
        occ = new Occurrence();
        
        m = new Metadata();
        m = (Metadata)dlu.getObjectFor(project.getSecond(),Metadata.class);
        occ.setMetadata(m);

        plant = new Plant();
        Integer id = lookupPlant(taxon);
        if (!id.equals(-1))
            plant = (Plant)dlu.getObjectFor(id,Plant.class);

        if (publication != null && !publication.getSecond().equals(-1)) {
            publ = (Publication)dlu.getObjectFor(publication.getSecond(),Publication.class);
            occ.setPublication(publ);
        }
            
        if (day != null) occ.setDayCollected(day);
        occ.setHabitat(h);
        if (herbarium != null) occ.setHerbarium(herbarium);
        
        //cIsoDateTimeBegin construction
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        if (month != null && day != null) { 
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH,day);
        } else {
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.DAY_OF_MONTH,1);
        }

        if (time != null) {
            //set the time itself
            occ.setTimeCollected(time);
            
            //and prepare it for cIsoDateTimeBegin
            Calendar temp = Calendar.getInstance();
            temp.setTime(time);
            c.set(Calendar.HOUR_OF_DAY,temp.get(Calendar.HOUR_OF_DAY));
            c.set(Calendar.MINUTE,temp.get(Calendar.MINUTE));
        } else {
            c.set(Calendar.HOUR_OF_DAY,0);
            c.set(Calendar.MINUTE,1); //so that we avoid possible problems with strong inequality when searching - we set the from minute in search by default to 0
        }
        occ.setIsoDateTimeBegin(c.getTime());
        
        if (month != null) occ.setMonthCollected(month); 
        if (occurrenceNote != null) occ.setNote(occurrenceNote);
        occ.setPlant(plant);
        occ.setYearCollected(year);
        occ.setDataSource(source);        
        
        occ.setDeleted(0);
                
        return occ;
    }//prepareNewOccurrence

    /** Prepares the original occurrence record for the original taxon for update.
     * Modifies the AddEdit's occurrence o. Can insert a new habitat into the database if updateAllPlants is false.
     *
     * @param updateAllPlants updates the existing habitat if true, otherwise creates a new habitat.
     */
    private void prepareOccurrenceUpdate(boolean updateAllPlants) throws DBLayerException, RemoteException {
        Habitat h;
        NearestVillage v;
        Phytochorion p;
        Territory t;
        Metadata m;
        Plant plant;
        Publication publ;
        DBLayerUtils dlu = new DBLayerUtils(database);
        
        if (updateAllPlants)  
            h = o.getHabitat();
        else 
            h = new Habitat();
        
        
        v = (NearestVillage)dlu.getObjectFor(village.getSecond(),NearestVillage.class);
        p = (Phytochorion)dlu.getObjectFor(phytCode.getSecond(),Phytochorion.class);
        t = (Territory)dlu.getObjectFor(territoryName.getSecond(),Territory.class);

        h.setAltitude(altitude);
        h.setCountry(phytCountry);
        h.setDescription(habitatDescription);
        h.setLatitude(latitude);
        h.setLongitude(longitude);
        h.setNearestVillage(v);
        h.setNote(habitatNote);
        h.setPhytochorion(p);
        h.setQuadrant(quadrant);
        h.setTerritory(t);
        
        if (updateAllPlants) {
            database.executeUpdateInTransaction(h);
        } else {
            //we've already created and set up a new Habitat now we have to store it into the database
            h.setDeleted(0);
            h = (Habitat)database.executeInsertInTransaction(h);
        }
        
        m = o.getMetadata();
        if (project != null)
            m = (Metadata)dlu.getObjectFor(project.getSecond(),Metadata.class);

        plant = o.getPlant();
        Integer id = lookupPlant(taxonOriginal);
        if (!id.equals(-1)) 
                plant = (Plant)dlu.getObjectFor(id,Plant.class);

        publ = o.getPublication();
        if (publication != null)
            publ = (Publication)dlu.getObjectFor(publication.getSecond(),Publication.class);
            
        o.setDayCollected(day);
        o.setHabitat(h);
        o.setHerbarium(herbarium);
        
        //cIsoDateTimeBegin construction
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        if (month != null && day != null) { //user entered month
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH,day);
        } else { //user didn't enter month, however we still have to compose cIsoDateTimeBegin column
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.DAY_OF_MONTH,1);            
        }
        Calendar temp = Calendar.getInstance();
        if (time != null) {
            temp.setTime(time);
            c.set(Calendar.HOUR_OF_DAY,temp.get(Calendar.HOUR_OF_DAY));
            c.set(Calendar.MINUTE,temp.get(Calendar.MINUTE));
        } else {
            c.set(Calendar.HOUR_OF_DAY,0);
            c.set(Calendar.MINUTE,1); //to avoid problems with possible strong inequality in search
        }
        o.setIsoDateTimeBegin(c.getTime());
        
        o.setMetadata(m);
        o.setMonthCollected(month);
        
        o.setNote(occurrenceNote);
        o.setPlant(plant);
        o.setPublication(publ);
        o.setTimeCollected(time);
        o.setYearCollected(year);     
        o.setDataSource(source);
    }
    
    /** Creates a clone of the AddEdit's occurrence o.
     * However, creates a new Habitat for this new occurrence and inserts it into the database.
     */
    private Occurrence cloneOccurrence() throws DBLayerException, RemoteException {
        Occurrence occTmp = new Occurrence();
        Habitat hTmp = new Habitat();
        hTmp.setAltitude(o.getHabitat().getAltitude());
        hTmp.setCountry(o.getHabitat().getCountry());
        hTmp.setDescription(o.getHabitat().getDescription());
        hTmp.setLatitude(o.getHabitat().getLatitude());
        hTmp.setLongitude(o.getHabitat().getLongitude());
        hTmp.setNearestVillage(o.getHabitat().getNearestVillage());
        hTmp.setNote(o.getHabitat().getNote());
        hTmp.setPhytochorion(o.getHabitat().getPhytochorion());
        hTmp.setQuadrant(o.getHabitat().getQuadrant());
        hTmp.setTerritory(o.getHabitat().getTerritory());
        hTmp.setDeleted(0);
        
        hTmp = (Habitat)database.executeInsertInTransaction(hTmp);
        
        occTmp.setDataSource(o.getDataSource());
        occTmp.setDayCollected(o.getDayCollected());
        occTmp.setHabitat(hTmp);
        occTmp.setHerbarium(o.getHerbarium());
        occTmp.setIsoDateTimeBegin(o.getIsoDateTimeBegin());
        occTmp.setMetadata(o.getMetadata());
        occTmp.setMonthCollected(o.getMonthCollected());
        occTmp.setNote(o.getNote());
        occTmp.setPlant(o.getPlant());
        occTmp.setPublication(o.getPublication());
        occTmp.setTimeCollected(o.getTimeCollected());
        occTmp.setUnitIdDb(o.getUnitIdDb());
        occTmp.setUnitValue(o.getUnitValue());
        occTmp.setYearCollected(o.getYearCollected());
        occTmp.setDeleted(0);
                
        return occTmp;
    }
    
    /** Determines whether the original taxon from the occurrence that the user began to edit
     * survived the editation - e.g. whether it has or hasn't been removed.
     *
     * @return true if the taxon survived
     * @return false if it was removed by the user
     */
    private boolean originalTaxonSurvived() {
        for (int t = 0; t < taxonList.size(); t++) {
            if (taxonOriginal.equals(taxonList.get(t))) {
                return true;
            }
        }        
        return false;
    }
    
    /** The big method handling the process of saving changes the user made to the occurrence.
     *
     * Works in two modes - add and edit mode. Determines the mode according to the inner variable editMode
     * the value of which is passed to this object during it's construction.
     *
     * @param updateAllPlants says whether to create a new habitat if the user made a change to a habitat that is used also by other occurrences.
     */
    public Task storeRecord(final boolean updateAllPlants) {
        return new Task() {
            public Object task() throws RemoteException, DBLayerException {
                
                boolean newOccurrenceInserted = false;
                DBLayerUtils dlu = new DBLayerUtils(database);

                logger.info("Storing occurrence record...");
                try {     
                        if (editMode) {
                            boolean originalTaxonSurvived = originalTaxonSurvived();

                            boolean ok = database.beginTransaction();
                            if (!ok) {
                                logger.debug("AppCore.deleteSelected(): Can't create transaction. Another is probably already running.");
                                throw new DBLayerException("Can't create transaction. Another already running.");
                            }
                            Habitat habitatToCheck = null;
                            
                            prepareOccurrenceUpdate(updateAllPlants);
                            if (originalTaxonSurvived) {
                                // update original occurrence
                                logger.info("Updating original occurrence");
                                database.executeUpdateInTransaction(o);
                                logger.debug("Original occurrence id="+o.getId()+" updated.");
                            } else {
                                // delete original occurrence and bound author occurrences
                                logger.info("Deleting original occurrence and associated author occurrences");
                                o.setDeleted(1);
                                database.executeUpdateInTransaction(o);
                                habitatToCheck = o.getHabitat(); //we'll maybe have to delete this habitat
                                logger.debug("Occurrence id "+o.getId()+" "+o.getPlant().getTaxon()+" deleted.");
                                Set<Map.Entry<String,AuthorOccurrence>> aoSet = authorOccurrences.entrySet();
                                Iterator it = aoSet.iterator();
                                while (it.hasNext()) {
                                    Map.Entry<String, AuthorOccurrence> entry = (Entry<String, AuthorOccurrence>) it.next();
                                    AuthorOccurrence tmp = entry.getValue();
                                    tmp.setDeleted(2);
                                    database.executeUpdateInTransactionHistory(tmp);
                                    logger.debug("AuthorOccurrence id "+tmp.getId()+" "+tmp.getAuthor().getWholeName()+" deleted.");
                                }
                                //clear the authorOccurrences so that we don't try to delete them once again further in this method
                                authorOccurrences.clear();
                                originalAuthors.clear();//user
                            }//original taxon didn't survive

                            /* original taxon survived but an author was removed
                             *
                             */


                            //If the user removed some of the original authors then delete the corresponding authorOccurrences
                            Iterator it = originalAuthors.iterator();
                            while (it.hasNext()) {
                                boolean originalSurvived = false;
                                Pair<Integer,String> auth = (Pair<Integer,String>)it.next();
                                AuthorOccurrence aoTmp = null;
                                for (int i = 0; i < authorList.size(); i++) {
                                    Pair<Pair<String,Integer>,String> p = (Pair<Pair<String,Integer>,String>) authorList.get(i);
                                    Pair<String,Integer> pp = p.getFirst();
                                    if (p.getFirst().getSecond().equals(auth.getFirst()) && p.getSecond().equals(auth.getSecond())) {
                                        originalSurvived = true;
                                        break;
                                    }
                                }
                                if (!originalSurvived) {
                                    aoTmp = authorOccurrences.get(auth.getSecond()+auth.getFirst());
                                    aoTmp.setDeleted(1);
                                    database.executeUpdateInTransaction(aoTmp);
                                    logger.debug("AuthorOccurrence id="+aoTmp.getId()+" "+aoTmp.getAuthor().getWholeName()+" deleted.");
                                }
                            }

                            //Update original authors roles 
                            for (int j = 0; j < authorList.size(); j++) {
                                Pair<Pair<String,Integer>,String> pTmp = authorList.get(j);
                                String role = pTmp.getSecond();
                                if (role == null)
                                    role = "";
                                if (originalAuthors.contains(
                                        new Pair<Integer,String>(pTmp.getFirst().getSecond(),role) )
                                    ) {
                                    logger.info("Updating authorOccurrence properties for "+pTmp.getFirst().getFirst());
                                    AuthorOccurrence aoTmp = authorOccurrences.get(pTmp.getSecond()+pTmp.getFirst().getSecond());
                                    aoTmp.setRole(pTmp.getSecond());
                                    aoTmp.setNote(resultRevision.get(j));
                                    database.executeUpdateInTransaction(aoTmp);
                                    logger.debug("AuthorOccurrence id="+aoTmp.getId()+" "+pTmp.getFirst().getFirst()+" updated");
                                } 
                            }

                            /*At this point we've deleted all that we should have deleted
                             *
                             *So we can start inserting
                             */

                            //K++ A?
                            //create a new Occurrence for each new flower and also create new AuthorOccurrences
                            for (int j = 0; j < taxonList.size(); j++) {
                                if (taxonOriginal.equals(taxonList.get(j)))
                                    continue; //skip the original taxon, it's been already taken care of 
                                logger.info("Creating a new occurrence for "+taxonList.get(j));
                                Occurrence occTmp = cloneOccurrence();
                                occTmp.setPlant((Plant) dlu.getObjectFor(lookupPlant(taxonList.get(j)),Plant.class));
                                occTmp = (Occurrence)database.executeInsertInTransaction(occTmp);
                                logger.debug("Occurrence for "+taxonList.get(j)+" inserted. Id="+occTmp.getId());
                                Integer id = lookupPlant(taxonList.get(j));
                                if (!id.equals(-1)) {
                                    Plant plTmp = (Plant) dlu.getObjectFor(id, Plant.class);
                                    occTmp.setPlant(plTmp);
                                }

                                for (int k = 0; k < authorList.size(); k++) {
                                    Pair<Pair<String,Integer>,String> pTmp = authorList.get(k);
                                    logger.info("Creating a new authorOccurrence for "+taxonList.get(j)+" and "+pTmp.getFirst().getFirst());
                                    AuthorOccurrence aoTmp = new AuthorOccurrence();
                                    aoTmp.setAuthor((Author)dlu.getObjectFor(pTmp.getFirst().getSecond(),Author.class));
                                    aoTmp.setRole(pTmp.getSecond());
                                    aoTmp.setNote(resultRevision.get(k));
                                    aoTmp.setOccurrence(occTmp);
                                    aoTmp.setDeleted(0);
                                    database.executeInsertInTransactionHistory(aoTmp);
                                    logger.debug("AuthorOccurrence for "+pTmp.getFirst().getFirst()+" inserted. Id="+aoTmp.getId());
                                    newOccurrenceInserted = true;
                                }
                            }


                            //A++ K-orig
                            //for the original taxon update the original taxon (that has already been done) and the author occurrence ( not needed)
                            //and for new authors create a new author occurrence 
                            if (originalTaxonSurvived)
                                for (int k = 0; k < authorList.size(); k++) {
                                    Pair<Pair<String,Integer>,String> pTmp = authorList.get(k);
                                    String role = pTmp.getSecond();
                                    if (role == null)
                                        role = "";
                                    if (!originalAuthors.contains(
                                            new Pair<Integer,String>(pTmp.getFirst().getSecond(),role) )
                                        ) {
                                        AuthorOccurrence aoTmp = new AuthorOccurrence();
                                        logger.info("Creating authorOccurrence for "+o.getPlant().getTaxon()+" and "+pTmp.getFirst().getFirst());
                                        aoTmp.setAuthor((Author)dlu.getObjectFor(pTmp.getFirst().getSecond(),Author.class));
                                        aoTmp.setRole(pTmp.getSecond());
                                        aoTmp.setNote(resultRevision.get(k));
                                        aoTmp.setOccurrence(o);
                                        aoTmp.setDeleted(0);
                                        database.executeInsertInTransaction(aoTmp);    
                                        logger.debug("AuthorOccurrence for "+pTmp.getFirst().getFirst()+" inserted. Id="+aoTmp.getId());                            
                                    }
                                }

                            database.commitTransaction();
                            if (habitatToCheck != null)
                                dlu.deleteHabitat(habitatToCheck);
                            occurrenceTableModel.reload();
                            newOccurrenceInserted = false;

                        } else { //Add Mode
                            NearestVillage v;
                            Phytochorion p;
                            Territory t;
                            Habitat h = new Habitat();
                            v = (NearestVillage)dlu.getObjectFor(village.getSecond(),NearestVillage.class);
                            p = (Phytochorion)dlu.getObjectFor(phytCode.getSecond(),Phytochorion.class);
                            t = (Territory)dlu.getObjectFor(territoryName.getSecond(),Territory.class);                    
                            if (altitude != null) h.setAltitude(altitude);
                            if (phytCountry != null) h.setCountry(phytCountry);
                            if (habitatDescription != null) h.setDescription(habitatDescription);
                            if (latitude != null) h.setLatitude(latitude);
                            if (longitude != null) h.setLongitude(longitude);
                            h.setNearestVillage(v);
                            if (habitatNote != null)h.setNote(habitatNote);
                            h.setPhytochorion(p);
                            if (quadrant != null) h.setQuadrant(quadrant);
                            h.setTerritory(t);
                            h.setDeleted(0);

                            boolean ok = database.beginTransaction();
                            if (!ok) {
                                logger.debug("AppCore.deleteSelected(): Can't create transaction. Another is probably already running.");
                                throw new DBLayerException(L10n.getString("Error.TransactionRaceConditions"),DBLayerException.ERROR_TRANSACTION);
                            }

                            logger.info("Creating a shared habitat");
                            Record rec = dlu.findMatchInDB(h);

                            if (rec == null) {
                                logger.debug("THIS HABITAT is NOT in the database yet. Creating a new one.");
                                h = (Habitat)database.executeInsertInTransaction(h);//insert the shared habitat                                
                                logger.debug("Shared habitat created. Id="+h.getId());
                            } else {
                                logger.debug("THIS HABITAT ALREADY IS in the database! Using it.");
                                h = (Habitat) rec;
                            }
                            int aoTmpId = 0;
                            for (int j = 0; j < taxonList.size(); j++) {
                                logger.info("Creating an Occurrence using the shared habitat");
                                Occurrence occ = prepareNewOccurrence(taxonList.get(j), h);//share the habitat
                                occ = (Occurrence)database.executeInsertInTransaction(occ);
                                logger.debug("Occurrence for "+taxonList.get(j)+" inserted. Id="+occ.getId());
                                
                                for (int k = 0; k < authorList.size(); k++) {
                                    Pair<Pair<String,Integer>,String> pTmp = authorList.get(k);
                                    logger.info("Creating an AuthorOccurrence for "+occ.getPlant().getTaxon()+" and "+pTmp.getFirst().getFirst());
                                    AuthorOccurrence aoTmp = new AuthorOccurrence();
                                    aoTmp.setRole(pTmp.getSecond());
                                    aoTmp.setAuthor((Author)dlu.getObjectFor(pTmp.getFirst().getSecond(),Author.class));
                                    aoTmp.setNote(resultRevision.get(k));
                                    aoTmp.setOccurrence(occ);
                                    aoTmp.setDeleted(0);
                                    aoTmp = (AuthorOccurrence)database.executeInsertInTransactionHistory(aoTmp);
                                }//for authorList
                            }// for taxonList                            
                            database.commitTransaction();
                            occurrenceTableModel.load(h.getId());                                
                        }//add mode
                } catch (DBLayerException ex) {
                    database.rollbackTransaction();
                    throw ex;
                }        

                checkAndPropagateChanges();
                
                return null;
          }//task()
        };//return new Task() {

    }//storeRecord()

    /** Checks whether the user created a new country or data source.
     *
     * If yes then notifies the rest of the application about that change.
     *
     */
    private void checkAndPropagateChanges() {
        int i = 0;
        
        boolean isCountryNew = true;
        if (countries != null) { //for example after installation there are initially no countries
            for (String country : countries)
                if (country.equals(phytCountry)) {
                    isCountryNew = false;
                    break;
                }
            if (isCountryNew)
                i++;
        } else
            isCountryNew = false;
        
        boolean isSourceNew = true;
        if (sources != null) { //sources also can quite possibly be empty
            for (String s : sources)
                if (s.equals(source)) {
                    isSourceNew = false;
                    break;
                }
            if (isSourceNew)
                i++;
        } else
            isSourceNew = false;
            
        if (i > 0) { //there's been some change we must report
            logger.debug("REPORTING CHANGE!");
            PlantloreConstants.Table[] changedTables = new PlantloreConstants.Table[i];
            if (isCountryNew) {
                i--;
                changedTables[i] = PlantloreConstants.Table.HABITAT;
            }
            if (isSourceNew) {
                i--;
                changedTables[i] = PlantloreConstants.Table.OCCURRENCE;
            }
            
            setChanged();
            notifyObservers(changedTables);
        }
    }
    
    public boolean isNotEmpty(String s) {
        if (s != null && !s.equals(""))
            return true;
        else
            return false;
    }

    public boolean isNotEmpty(Pair<String,Integer> p) {
        if (p != null && p.getFirst() != null &&!p.getFirst().equals(""))
            return true;
        else
            return false;
    }
    
    public boolean isNotEmpty(Number i) {
        if (i != null && !i.equals(0))
            return true;
        else
            return false;
    }
    
    public boolean isNotEmpty(Date d) {
        if (d != null && !d.equals(new Date(0)))
            return true;
        else
            return false;
    }
    
    /** Checks that user entered all compulsory data and that it is correct.
     *
     * @return Pair<Boolean,String> where the Boolean is true if the data is fine, otherwise it is false in which case the String contains a message for the user
     */
    public Pair<Boolean,String> checkData() throws RemoteException {
        if (authorList.size() < 1)
            return new Pair<Boolean,String>(false, L10n.getString("AddEdit.CheckMessage.AtLeastOneAuthor"));
        else {
            for (Pair<Pair<String,Integer>,String> author : authorList) {
                if (author.getFirst().getFirst().equals(""))
                    return new Pair<Boolean,String>(false, L10n.getString("AddEdit.CheckMessage.EmptyAuthor"));
            }
        }
       
       if (getCoordinateSystem() != AddEdit.WGS84) {
           logger.debug("Transformation coordinate system to WGS84 (for saving into database).");
            transformationCoordinateSystem(AddEdit.WGS84);            
       }
        
        if (taxonList == null || taxonList.size() < 1)
            return new Pair<Boolean,String>(false, L10n.getString("AddEdit.CheckMessage.AtLeastOneTaxon"));
        
        if (editMode && taxonList.size() > 1 && database.getUserRights().getAdd() != 1 && database.getUserRights().getAdministrator() != 1) { //the user is not allowed to add new records
            return new Pair<Boolean,String>(false, L10n.getString("AddEdit.InsufficientAddRights"));
        }
        
        if (editMode && !originalTaxonSurvived() && database.getUserRights().getAdd() != 1 && database.getUserRights().getAdministrator() != 1) { //in case the user replaced original taxon by some other but doesn't have rights for adding
            return new Pair<Boolean,String>(false, L10n.getString("AddEdit.InsufficientAddRights"));            
        }
        
        Pair<Pair<String,Integer>,String> ai, aj;
        for (int i=0; i < authorList.size() ; i++) {
            ai = authorList.get(i);
            Integer aiId = ai.getFirst().getSecond();
            String aiRole = ai.getSecond();
            for (int j=i+1; j < authorList.size() ; j++) {
                aj = authorList.get(j);
                Integer ajId = aj.getFirst().getSecond();
                String ajRole = aj.getSecond();
                if (aiId.equals(ajId) && aiRole.equals(ajRole)) {
                    return new Pair<Boolean,String>(false, L10n.getString("AddEdit.CheckMessage.OneRolePerAuthor")+ai.getFirst().getFirst());
                }
            }
        }
        
        if (!isNotEmpty(village)) {
            return new Pair<Boolean,String>(false,L10n.getString("AddEdit.CheckMessage.CompulsoryNearestVillage"));
        }
        
        if (!isNotEmpty(territoryName)) {
            return new Pair<Boolean,String>(false,L10n.getString("AddEdit.CheckMessage.CompulsoryTerritory"));            
        }
        
        if (!isNotEmpty(phytCode)) {
            return new Pair<Boolean,String>(false,L10n.getString("AddEdit.CheckMessage.CompulsoryPhytochorion"));            
        }
        
        if (!isNotEmpty(habitatDescription)) {
            return new Pair<Boolean,String>(false,L10n.getString("AddEdit.CheckMessage.CompulsoryDescription"));            
        }

        if (!isNotEmpty(project)) {
            return new Pair<Boolean,String>(false,L10n.getString("AddEdit.CheckMessage.CompulsoryProject"));            
        }

        if (day != null && month != null) //it is not possible to set the year null in the form
        {
            try {
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);           
            cal.getTime();  //throws an exception if the date is invalid          
            } catch(Exception ex) {
                return new Pair<Boolean,String>(false,L10n.getString("AddEdit.CheckMessage.InvalidDate"));
            }
        }
        
        if (!dayValid) {
                return new Pair<Boolean,String>(false,L10n.getString("AddEdit.CheckMessage.InvalidDay"));            
        }
        
        if (!timeValid) {
                return new Pair<Boolean,String>(false,L10n.getString("AddEdit.CheckMessage.InvalidTime"));                        
        }
        
        if (!altitudeValid) {
                return new Pair<Boolean,String>(false,L10n.getString("AddEdit.CheckMessage.InvalidAltitude"));            
        }
        
        if (!longitudeValid) {
                return new Pair<Boolean,String>(false,L10n.getString("AddEdit.CheckMessage.InvalidLongitude"));            
        }
        
        if (!latitudeValid) {
                return new Pair<Boolean,String>(false,L10n.getString("AddEdit.CheckMessage.InvalidLatitude"));            
        }
        
        return new Pair<Boolean,String>(true,"");
    }
    
    /** Loads all occurrences that share the currently edited habitat.
     *
     * Loads the occurrences into the habitatSharingOccurrences variable.
     */
    private void loadHabitatSharingOccurrences() throws DBLayerException, RemoteException {
        Habitat h = o.getHabitat();
        SelectQuery sq = database.createQuery(Occurrence.class);        
        sq.addRestriction(PlantloreConstants.RESTR_EQ,Occurrence.HABITAT,null,h,null);
        int resultid = database.executeQuery(sq);
        int resultCount = database.getNumRows(resultid);
        if (resultCount <= 0) {
            logger.error("AddEdit: some problem occurred a habitat seems not to have any occurrence, although it's perhaps being edited..!");
            return;
        }
        habitatSharingOccurrences = new Occurrence[resultCount];

        Object[] results = database.more(resultid, 0, resultCount-1);
        Object[] tmp;
        Occurrence occurrence;
        for (int i = 0; i < resultCount; i++) {
            tmp = (Object[]) results[i];
            occurrence = (Occurrence)tmp[0];
            habitatSharingOccurrences[i] = occurrence;
        }
        database.closeQuery(sq);
    }
    
    /** Returns all occurrences sharing the habitat - that means including the current working occurrence.
     * Loads the occurences using the {@link loadHabitatSharingOccurrences()} method.
     */
    public  Occurrence[] getHabitatSharingOccurrences() throws DBLayerException, RemoteException {
        //sdili je? mozna s kym
            //ano - zmena u vsech? ... zmenime to normalne - to co mame
            //ne  - zmena jen u naseho zaznamu, tj. new Habitat h, insert(h), o.setHabitat(h), update(o) 
        if (habitatSharingOccurrences != null)
            return habitatSharingOccurrences;   
        else {
            loadHabitatSharingOccurrences();
            return habitatSharingOccurrences;
        }
    }
    
    
    /**As a side effect stores the AuthorOccurrence objects into <code>authorOccurrences</code>
     * Also creates new resultRevision arrayList and loads data into it... :-/
     */
    private ArrayList<Pair<Pair<String,Integer>,String>> getAuthorsOf(Occurrence o) throws RemoteException, DBLayerException {
        ArrayList<Pair<Pair<String,Integer>,String>> authorResults = new ArrayList<Pair<Pair<String,Integer>,String>>();
        authorOccurrences = new HashMap<String,AuthorOccurrence>();
        resultRevision = new ArrayList();

        //Pair<Pair<String,Integer>,Pair<String,Integer>> p;
        SelectQuery sq = database.createQuery(AuthorOccurrence.class);        
        sq.addRestriction(PlantloreConstants.RESTR_EQ,AuthorOccurrence.OCCURRENCE,null,o,null);
        sq.addRestriction(PlantloreConstants.RESTR_EQ, AuthorOccurrence.DELETED, null, 0, null);
        int resultid = database.executeQuery(sq);
        int resultCount = database.getNumRows(resultid);
        Object[] results = database.more(resultid, 0, resultCount-1);
        Object[] tmp;
        AuthorOccurrence ao;
        Author a;
        for (int i = 0; i < resultCount; i++) {
            tmp = (Object[]) results[i];
            ao = (AuthorOccurrence)tmp[0];
            a = ao.getAuthor();
            String role = ao.getRole();
            if (role == null)
                role = "";/* avoid problems with null value... (we need to compare role for example in checkData() where we do role.equals())
                        * so if we didn't set it here to empty string a NullPointerException could be thrown
                        */
            authorResults.add(new Pair<Pair<String,Integer>,String>(
                    new Pair<String,Integer>(a.getWholeName(),a.getId()),role ) );
            authorOccurrences.put(role+a.getId(),ao);
            resultRevision.add(ao.getNote());
        }
        database.closeQuery(sq);
        
        return authorResults;
    }
    
    /** Clears the authorList and resultRevision variables. */
    public void clearAuthors() {
        authorList = new ArrayList<Pair<Pair<String,Integer>,String>>();
        resultRevision = new ArrayList<String>();        
    }
    
    /** Adds a new space for an author. Adds a new Pair into the authorList variable and a null to the resultRevision. 
     * Notifies observers.
     */
    public void addAuthorRow() {
        authorList.add(new Pair<Pair<String,Integer>,String>(new Pair<String,Integer>("",0),""));
        resultRevision.add(null);
        logger.info("Adding a new author row");
        setChanged();
        notifyObservers(new Pair<String,Integer>("addAuthorRow",-1));
    }
    
    /** Removes the asked author row.
     * Does nothing if the index is out of bounds.
     */
    public void removeAuthorRow(int i) {
        if (i < 0 || i > authorList.size()) {
            logger.error("AddEdit.removeAuthorRow(): "+i+" is out of bounds of the authorList array! Doing nothing. ");
            return;
        }
        authorList.remove(i);
        resultRevision.remove(i);
        logger.info("AddEdit: Removing author row #"+i);
        setChanged();
        notifyObservers(new Pair<String,Integer>("removeAuthorRow",i));        
    }
    
    /** Auhtor setter.
     *
     */
    public void setAuthor(int i, Pair<String,Integer> author) {
        if (i >= authorList.size()) {
            logger.error("AddEdit: trying to add an author to a non-existent row!");
            return;
        }
        authorList.get(i).setFirst(author);
        logger.debug("Author name in row "+i+" set to "+author);
    }
    
    /** Author role setter. */
    public void setAuthorRole(int i, String role) {
        if (i >= authorList.size()) {
            logger.error("AddEdit: trying to add an author to a non-existent row!");
            return;            
        }
        authorList.get(i).setSecond(role);
        logger.debug("Author role in row "+i+" set to "+role);
    }
    
    /** Author occurrence note setter. */
    public void setResultRevision(int i, String revision) {
        if (revision != null) {
            resultRevision.set(i, revision);
            logger.debug("AuthorOccurrence note #"+i+" set to "+revision);
        }
    }
    
    /** Author occurrence note getter. */
    public String getResultRevision(int i) {
        return resultRevision.get(i);
    }

    /** Taxons setter. Used by the dialog form to offer options to the user. 
     * Removes duplicates if some contained in the list.
     */
    public void setTaxons(ArrayList taxonList) {
        //remove duplicities
        for (int i=0 ; i < taxonList.size() ; i++) {
            for (int j=i+1 ; j < taxonList.size() ; j++) {
                if (taxonList.get(i).equals(taxonList.get(j)))
                    taxonList.remove(j);
            }
        }
        this.taxonList = taxonList;
        for (int i = 0; i < taxonList.size(); i++) {
            logger.debug("Taxon list contains plant #"+taxonList.get(i)+"#");
        }
    }

    /** Plants getter. Used by the dialog form to offer options to the user. */
    public Pair<String, Integer>[] getPlants() {
        return plants;
    }

    /** Plants setter. Notifies observers. */
    public void setPlants(Pair<String, Integer>[] plants) {
        logger.debug(""+(plants == null ? 0 : plants.length)+" plants set.");
        this.plants = plants;
        setChanged(); notifyObservers("PLANTS_CHANGED");
    }

    /** Authors getter. Used by the dialog form to offer options to the user. */
    public Pair<String, Integer>[] getAuthors() {
        return authors;
    }

    /** Authors setter. Notifies observers. */
    public void setAuthors(Pair<String, Integer>[] authors) {
        logger.debug(""+(authors==null?0:authors.length)+" authors set.");
        this.authors = authors;
        setChanged(); notifyObservers("AUTHORS_CHANGED");
    }

    /** Author roles getter. Used by the dialog form to offer options to the user. */
    public String[] getAuthorRoles() {
        return authorRoles;
    }

    /** Author roles setter. */
    public void setAuthorRoles(String[] authorRoles) {
        logger.debug(""+(authorRoles==null? 0 : authorRoles.length)+" author roles set.");
        this.authorRoles = authorRoles;
        setChanged(); notifyObservers("AUTHORROLES_CHANGED");
    }

    /** Villages setter. */
    public Pair<String, Integer>[] getVillages() {
        return villages;
    }

    /** Villages getter. Used by the dialog form to offer options to the user. */
    public void setVillages(Pair<String, Integer>[] villages) {
        logger.debug(""+(villages == null? 0: villages.length)+" villages set.");
        this.villages = villages;
        setChanged(); notifyObservers("VILLAGES_CHANGED");
    }

    /** Territories getter. Used by the dialog form to offer options to the user. */
    public Pair<String, Integer>[] getTerritories() {
        return territories;
    }

    /** Territories setter. Used by the dialog form to offer options to the user. */
    public void setTerritories(Pair<String, Integer>[] territories) {
        logger.debug(""+(territories==null ? 0 : territories.length)+" territories set.");
        this.territories = territories;
        setChanged(); notifyObservers("TERRITORIES_CHANGED");
    }

    /** Phytochorion names getter. Used by the dialog form to offer options to the user. */
    public Pair<String, Integer>[] getPhytNames() {
        return phytNames;
    }

    /** Phytochorion names setter. Used by the dialog form to offer options to the user. */
    public void setPhytNames(Pair<String, Integer>[] phytNames) {
        logger.debug(""+(phytNames == null ? 0 : phytNames.length)+" phytochorion names set.");
        this.phytNames = phytNames;
        setChanged(); notifyObservers("PHYTNAMES_CHANGED");
    }

    /** Phytochorion codes getter. Used by the dialog form to offer options to the user. */
    public Pair<String, Integer>[] getPhytCodes() {
        return phytCodes;
    }

    /** Phytochorion codes setter. Used by the dialog form to offer options to the user. */
    public void setPhytCodes(Pair<String, Integer>[] phytCodes) {
        logger.debug(""+(phytCodes == null ? 0 : phytCodes.length)+" phytochorion codes set.");
        this.phytCodes = phytCodes;
        setChanged(); notifyObservers("PHYTCODES_CHANGED");
    }

    /** Countries getter. Used by the dialog form to offer options to the user. */
    public String[] getCountries() {
        return countries;
    }

    /** Countries setter. Used by the dialog form to offer options to the user. */
    public void setCountries(String[] countries) {
        logger.debug(""+(countries == null ? 0 : countries.length)+" countries set.");
        this.countries = countries;
        setChanged(); notifyObservers("COUNTRIES_CHANGED");
    }

    /** Sources getter. Used by the dialog form to offer options to the user. */
    public String[] getSources() {
        return sources;
    }

    /** Sources setter. Used by the dialog form to offer options to the user. */
    public void setSources(String[] sources) {
        logger.debug(""+(sources == null ? 0 : sources.length)+" sources set.");
        this.sources = sources;
        setChanged(); notifyObservers("SOURCES_CHANGED");
    }

    /** Publications getter. Used by the dialog form to offer options to the user. */
    public Pair<String, Integer>[] getPublications() {
        return publications;
    }

    /** Publications setter. Used by the dialog form to offer options to the user. */
    public void setPublications(Pair<String, Integer>[] publications) {
        logger.debug(""+(publications == null ? 0 : publications.length)+" publications set.");
        this.publications = publications;
        setChanged(); notifyObservers("PUBLICATIONS_CHANGED");
    }

    /** Projects getter. Used by the dialog form to offer options to the user. */
    public Pair<String, Integer>[] getProjects() {
        return projects;
    }

    /** Projects setter. Used by the dialog form to offer options to the user. */
    public void setProjects(Pair<String, Integer>[] projects) {
        logger.debug(""+(projects == null ? 0 : projects.length)+" projects set.");
        this.projects = projects;
        setChanged(); notifyObservers("PROJECTS_CHANGED");
    }

    /** Clears all variables storing values of the actual record currently held by the model.
     * we need to clear, create default values for all values that can be null
     * the not null values are forced by calling checkData before store()
     */
    public void clear() {
        logger.debug("Clearing add model");
        clearLocation();
        clearOccurrence();
    }
    
    /** Clears the variables holding information about the location of the occurrence.
     * Notifies observers. Sets coordinate system to {@link WGS84}.
     */
    public void clearLocation() {
        logger.debug("Clearing Location part of add model");
        
        village = null;
        habitatDescription = null;
        habitatNote = null;
        phytCountry = null;
        phytCode = null;
        phytName = null;
        territoryName = null;
        quadrant = null;
        altitude = null;
        longitude = null;
        latitude = null;
        setCoordinateSystem(AddEdit.WGS84);

        occurrenceTableModel.clear();

        setChanged();
        notifyObservers("CLEAR_LOCATION");        
    }
 
    /** Clears the variables holding information about the occurrence of the occurrence.
     * Notifies observers. Sets the date to today's date.
     */
    public void clearOccurrence() {
        logger.debug("Clearing Occurrence part of add model");
        
        clearAuthors();
        year = Calendar.getInstance().get(Calendar.YEAR);
        occurrenceNote = null;
        source = null;
        publication = null;//new Pair<String,Integer>("",-1);
        herbarium = null;
        
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        time = null;
        
        taxonList = null;
        
        setChanged();
        notifyObservers("CLEAR_OCCURRENCE");
    }

    /** Returns the model of this dialog's occurrence table model. */
    public OccurrenceTableModel getOccurrenceTableModel() {
        return occurrenceTableModel;
    }
    
    /**
     * Transformation coordinate system
     * @param newSystem 
     */
    public void transformationCoordinateSystem(int selectedSystem) {
        Transformation tr = new Transformation();
        Double[] newCoordinate = new Double[3];
        Double[] newCoordinateTmp = new Double[3];
        //Check null value and set 0.0 if value is null      
        double tmpLatitude;
        double tmpLongitude;
        double tmpAltitude;
        if (latitude == null) tmpLatitude = 0.0;
        else tmpLatitude = latitude;
        if (longitude == null) tmpLongitude = 0.0;
        else tmpLongitude = longitude;
        if (altitude == null) tmpAltitude = 0.0;
        tmpAltitude = altitude;
        
        //WGS-84 --> S-JTSK
        if (getCoordinateSystem() == AddEdit.WGS84 && selectedSystem == AddEdit.SJTSK) {
            newCoordinate = tr.transform_WGS84_to_SJTSK(tmpLatitude, tmpLongitude, tmpAltitude);
        }
        //S-JTSK --> WGS-84
        if (getCoordinateSystem() == AddEdit.SJTSK && selectedSystem == AddEdit.WGS84) {
            newCoordinate = tr.transform_SJTSK_to_WGS84(tmpLatitude, tmpLongitude, tmpAltitude);
        }
        //WGS-84 --> S-42
         if (getCoordinateSystem() == AddEdit.WGS84 && selectedSystem == AddEdit.S42) {
            newCoordinate = tr.transform_WGS84_to_S42(tmpLatitude, tmpLongitude, tmpAltitude);
         }
        //S-42 --> WGA-84
         if (getCoordinateSystem() == AddEdit.S42 && selectedSystem == AddEdit.WGS84) {
            newCoordinate = tr.transform_S42_to_WGS84(tmpLatitude, tmpLongitude, tmpAltitude);
         }
        //S-JTSK --> S-42
        if (getCoordinateSystem() == AddEdit.SJTSK && selectedSystem == AddEdit.S42) {
            newCoordinateTmp = tr.transform_SJTSK_to_WGS84(tmpLatitude, tmpLongitude, tmpAltitude);
            newCoordinate  = tr.transform_WGS84_to_S42(newCoordinateTmp[0], newCoordinateTmp[1], newCoordinateTmp[2]);
        }
        //S-42 --> S-JTSK
        if (getCoordinateSystem() == AddEdit.S42 && selectedSystem == AddEdit.SJTSK) {
            newCoordinateTmp = tr.transform_S42_to_WGS84(tmpLatitude, tmpLongitude, tmpAltitude);
            newCoordinate  = tr.transform_WGS84_to_SJTSK(newCoordinateTmp[0], newCoordinateTmp[1], newCoordinateTmp[2]);
        }
        this.setCoordinateSystem(selectedSystem);     
        if (latitude == null) newCoordinate[0] = null;
        if (longitude == null) newCoordinate[1] = null;
        if (altitude == null) newCoordinate[2] = null;        
    /*  if (latitude == 0) newCoordinate[0] = 0.0;
        if (longitude == 0) newCoordinate[1] = 0.0;
        if (altitude == 0) newCoordinate[2] = 0.0;
     */ if (newCoordinate[0].isNaN()) this.setLatitude(null);
        if (newCoordinate[1].isNaN()) this.setLongitude(null);
        if (newCoordinate[2].isNaN()) this.setAltitude(null);
        this.setLatitude(newCoordinate[0]);
        this.setLongitude(newCoordinate[1]);
        this.setAltitude(newCoordinate[2]);       
    }     
    
    
    
    
    public void setIsCancle(boolean isCancle) {
        this.isCancled = isCancle;
    }
    
    public boolean getIsCancle() {
        return this.isCancled;
    }
    
    
    
    
    /*----------------------------------------------------------------------------------------
     * Save and load the default values from a separate configuration file.
     *
     *----------------------------------------------------------------------------------------*/
    private String defaultValuesFileName; {
        String userHome = System.getProperty("user.home"),
        osName = System.getProperty("os.name"),
        plantloreDirName = (osName.equals("Linux") ? "." : "") + net.sf.plantlore.client.Plantlore.PLANTLORE, 
        plantloreConfDir = userHome+File.separator+plantloreDirName;

        File plantloreConfDirFile = new File(plantloreConfDir);
        if (!plantloreConfDirFile.exists())
                plantloreConfDirFile.mkdir();

        defaultValuesFileName = plantloreConfDir + File.separator + "add-default";
    }
    
    
    
    /*
     * The hashtable stores default values for different databases (these databases are
     * recognized by their Unique Identifier).
     *
     * Thus the default values may differ for each database the User works with.
     * It was a necessary step because some of the stored values may not be in the other database at all.
     */
    private Hashtable<String, DefaultValues> storedValues = new Hashtable<String, DefaultValues>(8);

    
    /**
     * Load the table with default values.
     */
    private void load()
    throws IOException, ClassNotFoundException {
        logger.debug("Loading the list with default values.");
        ObjectInputStream ois = new ObjectInputStream( new FileInputStream(defaultValuesFileName) );
        storedValues = (Hashtable<String, DefaultValues>) ois.readObject();
        ois.close();
        
        System.out.println("~~~ LIST OF KEYS " + storedValues.keySet());
    }

    /**
     * Store the table with default values.
     */
    private void save()
    throws IOException {
        logger.debug("Saving the list with default values.");
        ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(defaultValuesFileName) );
        oos.writeObject( storedValues );
        oos.close();
    }
    
    

    public void setDefaultValues(DefaultValues defaultValues) {
        try {
             // Gather the information from dialogs.
            String databaseID = database.getUniqueDatabaseIdentifier();
            storedValues.remove( databaseID );
            if(defaultValues != null)
                storedValues.put( databaseID, defaultValues );
            // Store them.
            save();
        } catch(Exception e) {
            logger.error("Unable to remember default values! " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    public DefaultValues getDefaultValues(boolean reload) {
        try {
            if(reload)
                load();
            if(database == null)
                return null;
            String databaseID = database.getUniqueDatabaseIdentifier();
            return storedValues.get( databaseID );            
        } catch(Exception e) {
            logger.error("Unable to restore default values! " + e.getMessage());
            e.printStackTrace();
        }
        return null; 
    }
    
    
    public void restoreDefaultValuesInView() {
        setChanged();
        notifyObservers( getDefaultValues(false) );
    }

    


    /* 
     *
     * This `Netbeans 5.0` is a real shit . Thank God to IBM for their vastly superior Eclipse 3.1
     * What Netbeans cannot do properly:
     * 1. indent the code automatically (when inserted...),
     * 2. find usages (invoked on a method definition)
     * 3. refactor method names
     *
     */
}


