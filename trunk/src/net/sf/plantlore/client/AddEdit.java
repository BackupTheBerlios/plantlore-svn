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
import java.util.Observable;
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
import net.sf.plantlore.common.record.Village;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.server.DBLayerException;
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
    private Occurrence o;
    
    //list of authors user selects
    private ArrayList<Pair<Pair<String,Integer>,String>> authorList;
    
    //list of AuthorOccurrence objects that correspond to our Occurrence object, we need it for update
    private HashMap<Integer,AuthorOccurrence> authorOccurrences;
    private Pair<String,Integer> village;
    private String taxon;
    private String localityDescription;
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
    public void setRecord(Occurrence o) {
        this.o = o;
        coordinateSystem = WGS84;
        authorList = getAuthorsOf(o);
        village = new Pair(o.getHabitat().getNearestVillage().getName(), o.getHabitat().getNearestVillage().getId());
        taxon = o.getPlant().getTaxon();
        localityDescription = o.getHabitat().getDescription();
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
    
    public Pair<Pair<String, Integer>,String> removeAuthor(int i) {
        Pair<Pair<String, Integer>,String> author = authorList.remove(i);
        logger.debug("Removed author "+author.getFirst()+" "+author.getSecond());
        return author;
    }

    public Pair<String, Integer> getVillage() {
        return village;
    }

    public void setVillage(Pair<String, Integer> village) {
        this.village = village;
        logger.debug("Village set to "+village);
    }

    public String getTaxon() {
        return taxon;
    }

    public void setTaxon(String taxon) {
        this.taxon = taxon;
        logger.debug("Taxon set to "+ taxon);
    }

    public String getLocalityDescription() {
        return localityDescription;
    }

    public void setLocalityDescription(String localityDescription) {
        this.localityDescription = localityDescription;
        logger.debug("LocalityDescription set to "+localityDescription);
    }

    public Integer getYear() {
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
    
    public Pair<String, Integer>[] getPlants() {
        if (plants == null)
        {
            SelectQuery sq;
            int resultid;
            int resultsCount;
            Object[] records;
            Plant p;
            
            //FIXME:
            try {
                sq = database.createQuery(Plant.class);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, Plant.TAXON);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                System.out.println("getPlants(): we got "+resultsCount+" results.");
                records = database.more(resultid, 1, resultsCount);
                plants = new Pair[resultsCount];
                for (int i = 1; i <= resultsCount; i++)
                {
                    p = (Plant)((Object[])records[i-1])[0];
                    plants[i-1] = new Pair(p.getTaxon(), p.getId());
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }            
            return plants;
        } else
            return plants;
    }
    
    public Pair<String, Integer>[] getAuthors() {
        if (authors == null)
        {
            SelectQuery sq;
            int resultid;
            int resultsCount;
            Object[] records;
            Author a;
            //FIXME:
            try {
                sq = database.createQuery(Author.class);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, Author.WHOLENAME);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 1, resultsCount);
                authors = new Pair[resultsCount];
                for (int i = 1; i <= resultsCount; i++)
                {
                    a = (Author)((Object[])records[i-1])[0];
                    authors[i-1] = new Pair<String, Integer>(a.getWholeName(), a.getId());
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }            
            return authors;
        } else
            return authors;
    }
    
    public String[] getAuthorRoles() {
        if (authorRoles == null)
        {
            SelectQuery sq;
            int resultid;
            int resultsCount;
            Object[] records;
            AuthorOccurrence ao;
            //FIXME:
            try {
                sq = database.createQuery(AuthorOccurrence.class);
                sq.addProjection(PlantloreConstants.PROJ_DISTINCT,AuthorOccurrence.ROLE);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, AuthorOccurrence.ROLE);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 1, resultsCount);
                authorRoles = new String[resultsCount];
                String r;
                for (int i = 1; i <= resultsCount; i++)
                {
                    r = (String)((Object[])records[i-1])[0];
                    authorRoles[i-1] = r;
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }            
            return authorRoles;
        } else
            return authorRoles;
    }

    public Pair<String, Integer>[] getVillages() {
        if (villages == null)
        {
            SelectQuery sq;
            int resultid;
            int resultsCount;
            Object[] records;
            Village v;
            
            //FIXME:
            try {
                sq = database.createQuery(Village.class);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, Village.NAME);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 1, resultsCount);
                villages = new Pair[resultsCount];
                for (int i = 1; i <= resultsCount; i++)
                {
                    v = (Village)((Object[])records[i-1])[0];
                    villages[i-1] = new Pair<String, Integer>(v.getName(), v.getId());
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }            
            return villages;
        } else
            return villages;
    }

    public Pair<String, Integer>[] getTerritories() {
        if (territories == null)
        {
            SelectQuery sq;
            int resultid;
            int resultsCount;
            Object[] records;
            Territory t;
            
            //FIXME:
            try {
                sq = database.createQuery(Territory.class);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, Territory.NAME);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 1, resultsCount);
                territories = new Pair[resultsCount];
                for (int i = 1; i <= resultsCount; i++)
                {
                    t = (Territory)((Object[])records[i-1])[0];
                    territories[i-1] = new Pair<String,Integer>(t.getName(), t.getId());
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }            
            return territories;
        } else
            return territories;
    }
    
    public Pair<String, Integer>[] getPhytNames() {
        if (phytNames == null)
        {
            SelectQuery sq;
            int resultid;
            int resultsCount;
            Object[] records;
            Phytochorion p;
            
            //FIXME:
            try {
                sq = database.createQuery(Phytochorion.class);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, Phytochorion.NAME);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 1, resultsCount);
                phytNames = new Pair[resultsCount];
                for (int i = 1; i <= resultsCount; i++)
                {
                    p = (Phytochorion)((Object[])records[i-1])[0];
                    phytNames[i-1] = new Pair<String,Integer>(p.getName(), p.getId());
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }            
            return phytNames;
        } else
            return phytNames;
    }
    
    public Pair<String, Integer>[] getPhytCodes() {
        if (phytCodes == null)
        {
            SelectQuery sq;
            int resultid;
            int resultsCount;
            Object[] records;
            Phytochorion p;
            
            //FIXME:
            try {
                sq = database.createQuery(Phytochorion.class);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, Phytochorion.CODE);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 1, resultsCount);
                phytCodes = new Pair[resultsCount];
                for (int i = 1; i <= resultsCount; i++)
                {
                    p = (Phytochorion)((Object[])records[i-1])[0];
                    phytCodes[i-1] = new Pair<String,Integer>(p.getCode(), p.getId());
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }            
            return phytCodes;
        } else
            return phytCodes;
    }
    
    public String[] getCountries() {
        if (countries == null)
        {
            SelectQuery sq;
            int resultid;
            int resultsCount;
            int uniqueCount = 0;
            String[] countriesTemp;
            Object[] records;
            Habitat h;
            
            //FIXME:
            try {
                sq = database.createQuery(Habitat.class);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, Habitat.COUNTRY);
                resultid = database.executeQuery(sq); // the values can be doubled, we need to filter them 
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 1, resultsCount);
                countriesTemp = new String[resultsCount];
                for (int i = 1; i <= resultsCount; i++)
                {
                    h = (Habitat)((Object[])records[i-1])[0];
                    if (h.getCountry() ==  null)
                        System.out.println("\twas null");
                    if (i == 1) {
                        countriesTemp[0] = h.getCountry();
                        uniqueCount++;
                        continue;
                    }
                        
                    if (h.getCountry()!=null && !h.getCountry().equals(countriesTemp[uniqueCount-1])) { //filter duplicates and null values
                        countriesTemp[uniqueCount] = h.getCountry();
                        uniqueCount++;
                    }
                }
                
                countries = new String[uniqueCount];
                for (int i = 0; i < uniqueCount; i++) {
                    countries[i] = countriesTemp[i];
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }            
            return countries;
        } else
            return countries;        
    }
    
    public String[] getSources() {
        if (sources == null)
        {
            SelectQuery sq;
            int resultid;
            int resultsCount;
            Object[] records;
            Occurrence o;
            
            //FIXME:
            try {
                sq = database.createQuery(Occurrence.class);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, Occurrence.DATASOURCE);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 1, resultsCount);
                sources = new String[resultsCount];
                for (int i = 1; i <= resultsCount; i++)
                {
                    o = (Occurrence)((Object[])records[i-1])[0];
                    sources[i-1] = o.getDataSource();
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }            
            return sources;
        } else
            return sources;
    }
    
    public Pair<String, Integer>[] getPublications() {
        if (publications == null)
        {
            SelectQuery sq;
            int resultid;
            int resultsCount;
            Object[] records;
            Publication p;
            
            //FIXME:
            try {
                sq = database.createQuery(Publication.class);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, Publication.REFERENCECITATION);
                //sq.addProjection(PlantloreConstants.PROJ_DISTINCT,Publication.REFERENCECITATION);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 1, resultsCount);
                publications = new Pair[resultsCount];
                for (int i = 1; i <= resultsCount; i++)
                {
                    p = (Publication)((Object[])records[i-1])[0];
                    publications[i-1] = new Pair(p.getReferenceCitation(), p.getId());
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }            
            return publications;
        } else
            return publications;
    }

    public Pair<String, Integer>[] getProjects() {
        if (projects == null)
        {
            SelectQuery sq;
            int resultid;
            int resultsCount;
            Object[] records;
            Metadata m;
            
            //FIXME:
            try {
                sq = database.createQuery(Metadata.class);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, Metadata.DATASETTITLE);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 1, resultsCount);
                projects = new Pair[resultsCount];
                for (int i = 1; i <= resultsCount; i++)
                {
                    m = (Metadata)((Object[])records[i-1])[0];
                    projects[i-1] = new Pair(m.getDataSetTitle(), m.getId());
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }            
            return projects;
        } else
            return projects;
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
    
    /** Pre-processes data gathered from the user.
     *
     * @return AuthorOccurrence the object that will be created or updated
     * @return true the object has to be updated
     * @return false the object has to be created
     */
    private Pair<AuthorOccurrence,Boolean> prepareAuthorOccurrence(boolean newRecord, Pair<Pair<String,Integer>,String> author) {
        Pair<AuthorOccurrence,Boolean> result;
        DBLayerUtils dlu = new DBLayerUtils(database);
        Occurrence o;
        Author a;
        Habitat h;
        Village v; 
        Phytochorion p;
        Territory t;
        Metadata m;
        Plant plant;
        Publication publ ;
        
        if (newRecord)
            o = new Occurrence();
        else 
            o = this.o;
        
        assert authorList.size() > 0;
        if (newRecord) {
            a = new Author();
            a.setId(author.getFirst().getSecond());
        } else {
            if (authorOccurrences.containsKey(author.getFirst().getSecond()))
                a = authorOccurrences.get(author.getFirst().getSecond()).getAuthor(); //we already have the author Object
            else
                a = (Author) dlu.getObjectFor(author.getFirst().getSecond(), Author.class); //have to retrieve the author object from database
        }
        
        if (newRecord)
            h = new Habitat();
        else 
            h = o.getHabitat();
        
        if (newRecord)
            v = new Village();
        else
            v = h.getNearestVillage();
        v.setId(village.getSecond());

        
        if (newRecord)
            p = new Phytochorion();
        else
            p = h.getPhytochorion();
        p.setId(phytCode.getSecond());
        
        if (newRecord)
            t = new Territory();
        else
            t = h.getTerritory();
        t.setId(territoryName.getSecond());

        h.setAltitude(altitude);
        h.setCountry(phytCountry);
        h.setDescription(localityDescription);
        h.setLatitude(latitude);
        h.setLongitude(longitude);
        h.setNearestVillage(v);
        h.setNote(habitatNote);
        h.setPhytochorion(p);
        h.setQuadrant(quadrant);
        h.setTerritory(t);
        
        if (newRecord)
            m = new Metadata();
        else
            m = o.getMetadata();
        if (project != null)
            m.setId(project.getSecond());

        if (newRecord)
            plant = new Plant();
        else
            plant = o.getPlant();
        for (int i=0; i < plants.length; i++)
            if (plants[i].equals(taxon))
                plant.setId(plants[i].getSecond());

        if (newRecord)
            publ = new Publication();
        else
            publ = o.getPublication();
        if (publication != null)
            publ.setId(publication.getSecond());
            
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

        
        if (newRecord) {
            AuthorOccurrence newAO = new AuthorOccurrence();
            newAO.setAuthor(a);
            newAO.setOccurrence(o);
            return new Pair<AuthorOccurrence,Boolean>(newAO, false);
        } else {
            AuthorOccurrence ao;
            Boolean update;
            if (authorOccurrences.containsKey(author.getFirst().getSecond())) {
                ao = authorOccurrences.get(author.getFirst().getSecond());
                update = true;
            } else {
                ao = new AuthorOccurrence();
                update = false;
            }
            ao.setAuthor(a);
            ao.setOccurrence(o);
            return new Pair<AuthorOccurrence, Boolean>(ao, update);
        }
        
    }//prepareAuthorOccurrence
    
    public void storeRecord() {
        //FIXME:
        try {     
            Pair<AuthorOccurrence,Boolean> rec;
            for (int i = 0; i < authorList.size(); i++) {
                Pair<Pair<String,Integer>,String> author = authorList.get(i);
                if (editMode)
                    rec = prepareAuthorOccurrence(false,author);
                else
                    rec = prepareAuthorOccurrence(true,author);
                rec.getFirst().setRole(author.getSecond());
                if (rec.getSecond()) {
                    logger.info("Updating AuthorOccurrence record id="+rec.getFirst().getId());
                    database.executeUpdate(rec.getFirst());
                } else {
                    logger.info("Creating a new AuthorOccurrence record for author "+author.getFirst().getFirst()+" role "+author.getSecond());
                    database.executeInsert(rec.getFirst());
                }
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }        
    }//createRecord()
    
    
    private ArrayList<Pair<Pair<String,Integer>,String>> getAuthorsOf(Occurrence o) {
        ArrayList<Pair<Pair<String,Integer>,String>> authorResults = new ArrayList<Pair<Pair<String,Integer>,String>>();
        authorOccurrences = new HashMap<Integer,AuthorOccurrence>();
        //FIXME:
        try {
            //Pair<Pair<String,Integer>,Pair<String,Integer>> p;
            SelectQuery sq = database.createQuery(AuthorOccurrence.class);        
            sq.addRestriction(PlantloreConstants.RESTR_EQ,AuthorOccurrence.OCCURRENCE,null,o,null);
            int resultid = database.executeQuery(sq);
            int resultCount = database.getNumRows(resultid);
            Object[] results = database.more(resultid, 1, resultCount);
            Object[] tmp;
            AuthorOccurrence ao;
            Author a;
            for (int i = 0; i < resultCount; i++) {
                tmp = (Object[]) results[i];
                ao = (AuthorOccurrence)tmp[0];
                a = ao.getAuthor();
                authorResults.add(new Pair<Pair<String,Integer>,String>(
                        new Pair<String,Integer>(a.getWholeName(),a.getId()),ao.getRole() ) );
                authorOccurrences.put(a.getId(),ao);
            }
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return authorResults;
    }
    
    
    public void addAuthorRow() {
        authorList.add(new Pair<Pair<String,Integer>,String>(new Pair<String,Integer>("",0),""));
        logger.info("Adding a new author row");
        setChanged();
        notifyObservers(new Pair<String,Integer>("addAuthorRow",-1));
    }
    
    public void removeAuthorRow(int i) {
        authorList.remove(i);
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
}


