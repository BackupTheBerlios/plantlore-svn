/*
 * AddEdit.java
 *
 * Created on 20. duben 2006, 14:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Set;
import net.sf.plantlore.common.DBLayerUtils;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.common.record.Village;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.exception.DBLayerException;
import org.apache.log4j.Logger;

/**
 *
 * @author fraktalek
 */
public class AddEdit extends Observable {
    public static final int WGS84 = 1;
    public static final int S42 = 2;
    public static final int SJTSK = 3;
    
    private Logger logger;
    private DBLayer database;      
    
    private int coordinateSystem;
    private Occurrence o; //original occurrence
    
    //list of authors user selects
    private ArrayList<Pair<Pair<String,Integer>,String>> authorList;
    private ArrayList<String> resultRevision;
    private HashSet<Pair<Integer,String>> originalAuthors;//authors with the same name and different role are different authors for us --> they each have their own AuthorOccurrence record
    
    //list of AuthorOccurrence objects that correspond to our Occurrence object, we need it for update
    //set by <code>getAuthorsOf()</code> method
    private HashMap<Integer,AuthorOccurrence> authorOccurrences;
    private Pair<String,Integer> village;
    
    private ArrayList<String> taxonList;
    private String taxonOriginal;
    
    private String habitatDescription;
    private Integer year;
    private String habitatNote;
    private String occurrenceNote;
    private Pair<String, Integer> territoryName;
    private Pair<String, Integer> phytName;
    private Pair<String, Integer> phytCode;
    private String phytCountry;
    private String quadrant;
    private Double altitude;
    private Double longitude;
    private Double latitude;
    private String source;
    private Pair<String,Integer> publication;
    private String herbarium;
    private Pair<String,Integer> project;
    private Integer month;
    private Integer day;
    private Date time;
    private Occurrence[] habitatSharingOccurrences = null;
    
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
    
    
    /** Creates a new instance of AddEdit */
    public AddEdit(DBLayer database, Boolean editMode) {
        this.database = database;
        this.editMode = editMode;
        logger = Logger.getLogger(this.getClass().getPackage().getName());                
    }
 
    /** Makes the model load data from the parameter ao.
     *
     * @param ao Assumes it is from database and therefore assumes WGS84 coordinate system.
     */
    public void setRecord(Integer occurrenceId) {
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
        
        Iterator it = originalAuthors.iterator();
        System.out.println("Original authors are: ");
        while (it.hasNext()) {
            Pair<Integer,String> pOA = (Pair<Integer,String>)it.next();
            System.out.println(""+pOA.getFirst()+":"+pOA.getSecond());
        }
        
        village = new Pair(o.getHabitat().getNearestVillage().getName(), o.getHabitat().getNearestVillage().getId());
        
        taxonList = new ArrayList();
        taxonOriginal = o.getPlant().getTaxon();
        taxonList.add(taxonOriginal);
        
        habitatDescription = o.getHabitat().getDescription();
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
        source = o.getDataSource();
        publication = new Pair(o.getPublication().getReferenceCitation(), o.getPublication().getId());
        herbarium = o.getHerbarium();
        month = o.getMonthCollected();
        day = o.getDayCollected();
        time = o.getTimeCollected();
                
        //we also must determine (again) who shares habitat data with us
        loadHabitatSharingOccurrences();
    }

    public Pair<String, Integer> getAuthor(int i) {
        return ((Pair<Pair<String,Integer>,String>)authorList.get(i)).getFirst();
    }
    
    public String getAuthorRole(int i) {
        return ((Pair<Pair<String,Integer>,String>)authorList.get(i)).getSecond();
    }
    
    public int getAuthorCount() {
        return authorList.size();
    }
    
    public void addAuthor(Pair<Pair<String, Integer>,String> author) {
        authorList.add(author);
        logger.debug("Added author "+author.getFirst()+" as "+author.getSecond());
    }
    
    /*public Pair<Pair<String, Integer>,String> removeAuthor(int i) {
        Pair<Pair<String, Integer>,String> author = authorList.remove(i);
        logger.debug("Removed author "+author.getFirst()+" "+author.getSecond());
        return author;
    }*/

    public Pair<String, Integer> getVillage() {
        return village;
    }

    public void setVillage(Pair<String, Integer> village) {
        this.village = village;
        logger.debug("Village set to "+village);
    }

    public String getTaxon(int i) {
        return (String) taxonList.get(i);
    }

    public String getHabitatDescription() {
        return habitatDescription;
    }

    public void setHabitatDescription(String habitatDescription) {
        this.habitatDescription = habitatDescription;
        logger.debug("HabitatDescription set to "+habitatDescription);
    }

    public Integer getYear() {
        if (year == null)
            year = Calendar.getInstance().get(Calendar.YEAR);
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
        logger.debug("Year set to "+ year);
    }

    public String getHabitatNote() {
        return habitatNote;
    }

    public void setHabitatNote(String habitatNote) {
        this.habitatNote = habitatNote;
        logger.debug("HabitatNote set to "+ habitatNote);
    }

    public String getOccurrenceNote() {
        return occurrenceNote;
    }

    public void setOccurrenceNote(String occurrenceNote) {
        this.occurrenceNote = occurrenceNote;
        logger.debug("OccurrenceNote set to "+occurrenceNote);
    }

    public Pair<String, Integer> getTerritoryName() {
        return territoryName;
    }

    public void setTerritoryName(Pair<String, Integer> territoryName) {
        this.territoryName = territoryName;
        logger.debug("TerritoryName set to "+territoryName);
    }

    public Pair<String, Integer> getPhytName() {
        return phytName;
    }

    public void setPhytName(Pair<String, Integer> phytName) {
        if (phytName == null)
            return;
        if (skipUpdate) {
            skipUpdate = false;
            logger.debug("Skipping setPhytName");
            return;
        }
        this.phytName = phytName;
        for (int i=0; i < phytCodes.length; i++) {
            if (phytCodes[i].getSecond().equals(phytName.getSecond())) {
                phytCode = phytCodes[i];
                logger.debug("SetPhytName For "+phytName+" found "+phytCode);
                skipUpdate = true;
                break;
            } 
        }
        logger.debug("PhytName set to "+phytName);
        setChanged();
        notifyObservers(new Pair<String,Integer>("updateCode",-1));
    }

    public Pair<String, Integer> getPhytCode() {
        return phytCode;
    }

    public void setPhytCode(Pair<String, Integer> phytCode) {
        if (phytCode == null)
            return;
        
        if (skipUpdate) {
            skipUpdate = false;
            logger.debug("Skipping setPhytCode");
            return;
        }
        this.phytCode = phytCode;
        for (int i=0; i < phytNames.length; i++) {
            if (phytNames[i].getSecond().equals(phytCode.getSecond())) {
                phytName = phytNames[i];
                logger.debug("SetPhytCode For "+phytCode+" found "+phytName);
                skipUpdate = true;
                break;
            }
        }
        logger.debug("PhytCode set to "+phytCode);
        setChanged();
        notifyObservers(new Pair<String,Integer>("updateName",-1));
    }

    public String getPhytCountry() {
        return phytCountry;
    }

    public void setPhytCountry(String phytCountry) {
        this.phytCountry = phytCountry;
        logger.debug("PhytCountry set to "+phytCountry);
    }

    public String getQuadrant() {
        return quadrant;
    }

    public void setQuadrant(String quadrant) {
        this.quadrant = quadrant;
        logger.debug("Quadrant set to "+ quadrant);
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
        logger.debug("Altitude set to "+altitude);
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
        logger.debug("Longitude set to "+longitude);
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
        logger.debug("Latitude set to "+latitude);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
        logger.debug("Source set to "+source);
    }

    public Pair<String, Integer> getPublication() {
        return publication;
    }

    public void setPublication(Pair<String, Integer> publication) {
        this.publication = publication;
        logger.debug("Publication set to "+publication);
    }

    public String getHerbarium() {
        return herbarium;
    }

    public void setHerbarium(String herbarium) {
        this.herbarium = herbarium;
        logger.debug("Herbarium set to "+herbarium);
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
        logger.debug("Month set to "+month);
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
        logger.debug("Day set to "+day);
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
        logger.debug("Time set to "+time);
    }
    
    public DBLayer getDatabase() {
        return database;
    }

    public void setDatabase(DBLayer database) {
        this.database = database;
    }

    public Pair<String, Integer> getProject() {
        return project;
    }

    public void setProject(Pair<String, Integer> project) {
        logger.debug("Project set to "+project);
        this.project = project;
    }

    public int getCoordinateSystem() {
        return coordinateSystem;
    }

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
                logger.debug("CoordinateSystem set to SJSTK");
                break;
        }
    }
    
    /** Helper method to find id of given taxon according to <code>plants[]</code>
     *
     * @return Id of the taxon if found
     * @return -1 if not found
     */
    private Integer lookupPlant(String taxon) {
        logger.debug("Looking up id for #"+taxon+"#");
        for (int i=0; i < plants.length ; i++) {
            System.out.print("Trying #"+plants[i]+"#");
            if (taxon.equals(plants[i].getFirst())) {
                return plants[i].getSecond();
            }
        }
        return -1;
    }
    
    /** Pre-processes data gathered from the user.
     *
     * @paran author one of the authors of this occurrence to be processed
     * @param newRecord if true then new record is to be created - e.g. we are in Add mode, otherwise the record is updated
     * @param updateAllPlants if true then the shared habitat is updated, if false then a new habitat is created and asociated with our AuthorOccurrence object o. Has only sense if newRecord is true.
     * @return AuthorOccurrence the object that will be created or updated
     * @return true the object has to be updated
     * @return false the object has to be created
     */
    private Occurrence prepareNewOccurrence(String taxon, Habitat h) {
        DBLayerUtils dlu = new DBLayerUtils(database);
        Occurrence occ;
        Author a;
        Metadata m;
        Plant plant;
        Publication publ ;
        
        occ = new Occurrence();
        
        m = new Metadata();
        if (project != null)
            m = (Metadata)dlu.getObjectFor(project.getSecond(),Metadata.class);

        plant = new Plant();
        Integer id = lookupPlant(taxon);
        if (!id.equals(-1))
            plant = (Plant)dlu.getObjectFor(id,Plant.class);

        publ = new Publication();
        if (publication != null && !publication.getSecond().equals(-1))
            publ = (Publication)dlu.getObjectFor(publication.getSecond(),Publication.class);
            
        occ.setDayCollected(day);
        occ.setHabitat(h);
        occ.setHerbarium(herbarium);
        
        //cIsoDateTimeBegin construction
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH,day);
        Calendar temp = Calendar.getInstance();
        temp.setTime(time);
        c.set(Calendar.HOUR_OF_DAY,temp.get(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE,temp.get(Calendar.MINUTE));
        occ.setIsoDateTimeBegin(c.getTime());
        
        occ.setMetadata(m);
        occ.setMonthCollected(month);
        occ.setNote(occurrenceNote);
        occ.setPlant(plant);
        if (publication != null)
            occ.setPublication(publ);
        occ.setTimeCollected(time);
        occ.setYearCollected(year);
        
        occ.setDeleted(0);
        
        //#### 2BE REMOVED
        occ.setCreatedWhen(new Date());
        occ.setUpdatedWhen(new Date());
        occ.setCreatedWho((User) dlu.getObjectFor(2,User.class));
        occ.setUpdatedWho((User) dlu.getObjectFor(2,User.class));        
        //----------
        occ.setUnitIdDb("docasna unitIdDb");
        occ.setUnitValue("docasna unit value");
        //####        
        
        return occ;
    }//prepareNewOccurrence

    /** prepares the original occurrence record for the original taxon for update 
     * modifies the AddEdit's occurrence o. Can insert a new habitat into the database if updateAllPlants is false.
     * updates the existing habitat if updateAllPlants is true
     *
     */
    private void prepareOccurrenceUpdate(boolean updateAllPlants) throws DBLayerException, RemoteException {
        Habitat h;
        Village v;
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
        
        
        v = (Village)dlu.getObjectFor(village.getSecond(),Village.class);
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
            database.executeUpdate(h);
        } else {
            //we've already created and set up a new Habitat now we have to store it into the database
            h.setDeleted(0);
            database.executeInsert(h);
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
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH,day);
        Calendar temp = Calendar.getInstance();
        temp.setTime(time);
        c.set(Calendar.HOUR_OF_DAY,temp.get(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE,temp.get(Calendar.MINUTE));
        o.setIsoDateTimeBegin(c.getTime());
        
        o.setMetadata(m);
        o.setMonthCollected(month);
        o.setNote(occurrenceNote);
        o.setPlant(plant);
        o.setPublication(publ);
        o.setTimeCollected(time);
        o.setYearCollected(year);        
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
        
        database.executeInsert(hTmp);
        
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
        
        //#### 2BE REMOVED
        DBLayerUtils dlu = new DBLayerUtils(database);
        occTmp.setCreatedWhen(new Date());
        occTmp.setUpdatedWhen(new Date());
        occTmp.setCreatedWho((User) dlu.getObjectFor(2,User.class));
        occTmp.setUpdatedWho((User) dlu.getObjectFor(2,User.class));        
        //####
        
        return occTmp;
    }
    
    /** Deletes Habitat for given Occurrence if needed.
     *
     * Will delete Habitat if no live Occurrence point at it.
     *
     */
    private void deleteHabitat(Habitat h) {
        try {
            SelectQuery sq = database.createQuery(Occurrence.class);        
            sq.addRestriction(PlantloreConstants.RESTR_EQ,Occurrence.HABITAT,null,h,null);
            sq.addRestriction(PlantloreConstants.RESTR_NE,Occurrence.DELETED, null, 1, null);
            int resultid = database.executeQuery(sq);
            int resultCount = database.getNumRows(resultid);
            if (resultCount == 0) {
                logger.info("Deleting habitat id="+h.getId()+" with nearest village "+h.getNearestVillage().getName());
                h.setDeleted(1);
                database.executeUpdate(h);
            } else {
                logger.debug("Leaving habitat id="+h.getId()+" live. Live Occurrence records point at it.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (DBLayerException e) {
            e.printStackTrace();
        }
        
    }
    
    public void storeRecord(boolean updateAllPlants) {
        DBLayerUtils dlu = new DBLayerUtils(database);

        logger.info("Storing occurrence record...");
        //FIXME: exception catching/throwing
        try {     
                if (editMode) {
                    boolean originalTaxonSurvived = false;
                    
                    for (int t = 0; t < taxonList.size(); t++) {
                        System.out.print("#"+taxonOriginal + "# vs #"+taxonList.get(t)+"#");
                        if (taxonOriginal.equals(taxonList.get(t))) {
                            originalTaxonSurvived = true;
                            break;
                        }
                        System.out.println(" NO");
                    }

                    prepareOccurrenceUpdate(updateAllPlants);
                    if (originalTaxonSurvived) {
                        // update original occurrence
                        logger.info("Updating original occurrence");
                        database.executeUpdate(o);
                        logger.debug("Original occurrence id="+o.getId()+" updated.");
                    } else {
                        // delete original occurrence and bound author occurrences
                        logger.info("Deleting original occurrence and associated author occurrences");
                        o.setDeleted(1);
                        database.executeUpdate(o);
                        deleteHabitat(o.getHabitat());
                        logger.debug("Occurrence id "+o.getId()+" "+o.getPlant().getTaxon()+" deleted.");
                        Set<Map.Entry<Integer,AuthorOccurrence>> aoSet = authorOccurrences.entrySet();
                        Iterator it = aoSet.iterator();
                        while (it.hasNext()) {
                            Map.Entry<Integer, AuthorOccurrence> entry = (Entry<Integer, AuthorOccurrence>) it.next();
                            AuthorOccurrence tmp = entry.getValue();
                            tmp.setDeleted(1);
                            database.executeUpdate(tmp);
                            logger.debug("AuthorOccurrence id "+tmp.getId()+" "+tmp.getAuthor().getWholeName()+" deleted.");
                        }
                        //clear the authorOccurrences so that we don't try to delete them once again further in this method
                        authorOccurrences.clear();
                        originalAuthors.clear();//user
                    }//original taxon didn't survive
                    
                    /* originalni taxon prezil, ale byl ubran autor
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
                            aoTmp = authorOccurrences.get(auth.getFirst());
                            aoTmp.setDeleted(1);
                            database.executeUpdate(aoTmp);
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
                            AuthorOccurrence aoTmp = authorOccurrences.get(pTmp.getFirst().getSecond());
                            aoTmp.setRole(pTmp.getSecond());
                            aoTmp.setNote(resultRevision.get(j));
                            database.executeUpdate(aoTmp);
                            logger.debug("AuthorOccurrence id="+aoTmp.getId()+" "+pTmp.getFirst().getFirst()+" updated");
                        } 
                    }
                    
                    /*At this point we've deleted all that we should have deleted
                     *
                     *So we can start inserting
                     */
                    
                    //K++ A?
                    //pro kazdou novou kytku vytvorit Occurrence a k nemu pro kazdeho autora vytvorit AuthorOccurrence
                    for (int j = 0; j < taxonList.size(); j++) {
                        if (taxonOriginal.equals(taxonList.get(j)))
                            continue; //skip the original taxon, it's been already taken care of 
                        logger.info("Creating a new occurrence for "+taxonList.get(j));
                        Occurrence occTmp = cloneOccurrence();
                        occTmp.setPlant((Plant) dlu.getObjectFor(lookupPlant(taxonList.get(j)),Plant.class));
                        database.executeInsert(occTmp);
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
                            database.executeInsert(aoTmp);
                            logger.debug("AuthorOccurrence for "+pTmp.getFirst().getFirst()+" inserted. Id="+aoTmp.getId());
                        }
                    }
                    
                    
                    //A++ K-orig
                    //pro puvodni kytku updatnout puvodni occurrence (uz jsme udelali) a author occurrence (neni potreba) 
                    //a pro nove autory pro ni vytvorit author occurrence
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
                                database.executeInsert(aoTmp);    
                                logger.debug("AuthorOccurrence for "+pTmp.getFirst().getFirst()+" inserted. Id="+aoTmp.getId());                            
                            }
                        }
                    
                } else { //Add Mode
                    Village v;
                    Phytochorion p;
                    Territory t;
                    Habitat h = new Habitat();
                    v = (Village)dlu.getObjectFor(village.getSecond(),Village.class);
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
                    h.setDeleted(0);
                    logger.info("Creating a shared habitat");
                    database.executeInsert(h);//insert the shared habitat
                    logger.debug("Shared habitat created. Id="+h.getId());
                        
                    for (int j = 0; j < taxonList.size(); j++) {
                        logger.info("Creating an Occurrence using the shared habitat");
                        Occurrence occ = prepareNewOccurrence(taxonList.get(j), h);//share the habitat
                        database.executeInsert(occ);
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
                            database.executeInsert(aoTmp);                            
                        }//for authorList
                    }// for taxonList
                }//add mode
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }        
    }//createRecord()

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
    
    public Pair<Boolean,String> checkData() {
        //TODO: check that the author set contains reasonable (not null) values
        if (authorList.size() < 1)
            return new Pair<Boolean,String>(false, "You have to add at least one author!");
        if (taxonList == null || taxonList.size() < 1)
            return new Pair<Boolean,String>(false, "You have to add at least one taxon!");
        
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
                    return new Pair<Boolean,String>(false, "Author can appear only once in each role. Please modify "+ai.getFirst().getFirst());
                }
            }
        }
        
        if (!isNotEmpty(village)) {
            return new Pair<Boolean,String>(false,"You have to enter all requested data. Please fill in the nearest village.");
        }
        
        if (!isNotEmpty(territoryName)) {
            return new Pair<Boolean,String>(false,"You have to enter all requested data. Please fill in the territory name.");            
        }
        
        if (!isNotEmpty(phytCode)) {
            return new Pair<Boolean,String>(false,"You have to enter all requested data. Please fill in the phytochoria code or name.");            
        }

        if (!isNotEmpty(project)) {
            return new Pair<Boolean,String>(false,"You have to enter all requested data. Please fill in the project.");            
        }

        return new Pair<Boolean,String>(true,"");
    }
    
    private void loadHabitatSharingOccurrences() {
        Habitat h = o.getHabitat();
        //FIXME:
        try {
            SelectQuery sq = database.createQuery(Occurrence.class);        
            sq.addRestriction(PlantloreConstants.RESTR_EQ,Occurrence.HABITAT,null,h,null);
            int resultid = database.executeQuery(sq);
            int resultCount = database.getNumRows(resultid);
            habitatSharingOccurrences = new Occurrence[resultCount];
            
            Object[] results = database.more(resultid, 0, resultCount-1);
            Object[] tmp;
            Occurrence occurrence;
            for (int i = 0; i < resultCount; i++) {
                tmp = (Object[]) results[i];
                occurrence = (Occurrence)tmp[0];
                habitatSharingOccurrences[i] = occurrence;
            }
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
    
    /** returns all occurrences sharing the habitat - that means including the current working occurrence
     */
    public  Occurrence[] getHabitatSharingOccurrences() {
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
    private ArrayList<Pair<Pair<String,Integer>,String>> getAuthorsOf(Occurrence o) {
        ArrayList<Pair<Pair<String,Integer>,String>> authorResults = new ArrayList<Pair<Pair<String,Integer>,String>>();
        authorOccurrences = new HashMap<Integer,AuthorOccurrence>();
        resultRevision = new ArrayList();
        //FIXME:
        try {
            //Pair<Pair<String,Integer>,Pair<String,Integer>> p;
            SelectQuery sq = database.createQuery(AuthorOccurrence.class);        
            sq.addRestriction(PlantloreConstants.RESTR_EQ,AuthorOccurrence.OCCURRENCE,null,o,null);
            sq.addRestriction(PlantloreConstants.RESTR_NE, AuthorOccurrence.DELETED, null, 1, null);
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
                authorOccurrences.put(a.getId(),ao);
                resultRevision.add(ao.getNote());
            }
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return authorResults;
    }
    
    public void clearAuthors() {
        authorList = new ArrayList<Pair<Pair<String,Integer>,String>>();
        resultRevision = new ArrayList<String>();        
    }
    
    public void addAuthorRow() {
        authorList.add(new Pair<Pair<String,Integer>,String>(new Pair<String,Integer>("",0),""));
        resultRevision.add(null);
        logger.info("Adding a new author row");
        setChanged();
        notifyObservers(new Pair<String,Integer>("addAuthorRow",-1));
    }
    
    public void removeAuthorRow(int i) {
        authorList.remove(i);
        resultRevision.remove(i);
        logger.info("AddEdit: Removing author row #"+i);
        setChanged();
        notifyObservers(new Pair<String,Integer>("removeAuthorRow",i));        
    }
    
    public void setAuthor(int i, Pair<String,Integer> author) {
        authorList.get(i).setFirst(author);
        logger.debug("Author name in row "+i+" set to "+author);
    }
    
    public void setAuthorRole(int i, String role) {
        authorList.get(i).setSecond(role);
        logger.debug("Author role in row "+i+" set to "+role);
    }
    
    public void setResultRevision(int i, String revision) {
        if (revision != null) {
            resultRevision.set(i, revision);
            logger.debug("AuthorOccurrence note #"+i+" set to "+revision);
        }
    }
    
    public String getResultRevision(int i) {
        return resultRevision.get(i);
    }

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

    public Pair<String, Integer>[] getPlants() {
        return plants;
    }

    public void setPlants(Pair<String, Integer>[] plants) {
        logger.debug(""+plants.length+" plants set.");
        this.plants = plants;
    }

    public Pair<String, Integer>[] getAuthors() {
        return authors;
    }

    public void setAuthors(Pair<String, Integer>[] authors) {
        logger.debug(""+authors.length+" authors set.");
        this.authors = authors;
    }

    public String[] getAuthorRoles() {
        return authorRoles;
    }

    public void setAuthorRoles(String[] authorRoles) {
        logger.debug(""+authorRoles.length+" author roles set.");
        this.authorRoles = authorRoles;
    }

    public Pair<String, Integer>[] getVillages() {
        return villages;
    }

    public void setVillages(Pair<String, Integer>[] villages) {
        logger.debug(""+villages.length+" villages set.");
        this.villages = villages;
    }

    public Pair<String, Integer>[] getTerritories() {
        return territories;
    }

    public void setTerritories(Pair<String, Integer>[] territories) {
        logger.debug(""+territories.length+" territories set.");
        this.territories = territories;
    }

    public Pair<String, Integer>[] getPhytNames() {
        return phytNames;
    }

    public void setPhytNames(Pair<String, Integer>[] phytNames) {
        logger.debug(""+phytNames.length+" phytochorion names set.");
        this.phytNames = phytNames;
    }

    public Pair<String, Integer>[] getPhytCodes() {
        return phytCodes;
    }

    public void setPhytCodes(Pair<String, Integer>[] phytCodes) {
        logger.debug(""+phytCodes.length+" phytochorion codes set.");
        this.phytCodes = phytCodes;
    }

    public String[] getCountries() {
        return countries;
    }

    public void setCountries(String[] countries) {
        logger.debug(""+countries.length+" set.");
        this.countries = countries;
    }

    public String[] getSources() {
        return sources;
    }

    public void setSources(String[] sources) {
        logger.debug(""+sources.length+" set.");
        this.sources = sources;
    }

    public Pair<String, Integer>[] getPublications() {
        return publications;
    }

    public void setPublications(Pair<String, Integer>[] publications) {
        logger.debug(""+publications.length+" set.");
        this.publications = publications;
    }

    public Pair<String, Integer>[] getProjects() {
        return projects;
    }

    public void setProjects(Pair<String, Integer>[] projects) {
        logger.debug(""+projects.length+" set.");
        this.projects = projects;
    }

    //for add mode
    //we need to clear, create default values for all values that can be null
    //the not null values are forced by calling checkData before store()
    public void clear() {
        logger.debug("Clearing add model");
        clearAuthors();
        habitatDescription = "";
        year = Calendar.getInstance().get(Calendar.YEAR);
        habitatNote = "";
        occurrenceNote = "";
        phytCountry = "";
        quadrant = "";
        altitude = 0.0;
        longitude = 0.0;
        latitude = 0.0;
        source = "";
        publication = new Pair<String,Integer>("",-1);
        herbarium = "";
        
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        time = new Date(0);
    }
    
}


