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
import java.util.Date;
import java.util.Observable;
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
    private AuthorOccurrence ao;
    private Pair<String,Integer> author;
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
    
    /** Creates a new instance of AddEdit */
    public AddEdit(DBLayer database) {
        this.database = database;
        logger = Logger.getLogger(this.getClass().getPackage().getName());                
    }
 
    /** Makes the model load data from the parameter ao.
     *
     * @param ao Assumes it is from database and therefore assumes WGS84 coordinate system.
     */
    public void setRecord(AuthorOccurrence ao) {
        this.ao = ao;
        coordinateSystem = WGS84;
        author = new Pair(ao.getAuthor().getWholeName(),ao.getAuthor().getId());
        village = new Pair(ao.getOccurrence().getHabitat().getNearestVillage().getName(), ao.getOccurrence().getHabitat().getNearestVillage().getId());
        taxon = ao.getOccurrence().getPlant().getTaxon();
        localityDescription = ao.getOccurrence().getHabitat().getDescription();
        year = ao.getOccurrence().getYearCollected();
        
        occurrenceNote = ao.getOccurrence().getNote();
        habitatNote = ao.getOccurrence().getHabitat().getNote();
        territoryName = new Pair(ao.getOccurrence().getHabitat().getTerritory().getName(),ao.getOccurrence().getHabitat().getTerritory().getId());
        phytName = new Pair(ao.getOccurrence().getHabitat().getPhytochorion().getName(), ao.getOccurrence().getHabitat().getPhytochorion().getId());
        phytCode = new Pair(ao.getOccurrence().getHabitat().getPhytochorion().getCode(), ao.getOccurrence().getHabitat().getPhytochorion().getId());
        phytCountry = ao.getOccurrence().getHabitat().getCountry();
        quadrant = ao.getOccurrence().getHabitat().getQuadrant();
        altitude = ao.getOccurrence().getHabitat().getAltitude();
        longitude = ao.getOccurrence().getHabitat().getLongitude();
        latitude = ao.getOccurrence().getHabitat().getLatitude();
        source = ao.getOccurrence().getDataSource();
        publication = new Pair(ao.getOccurrence().getPublication().getReferenceCitation(), ao.getOccurrence().getPublication().getId());
        herbarium = ao.getOccurrence().getHerbarium();
        month = ao.getOccurrence().getMonthCollected();
        day = ao.getOccurrence().getDayCollected();
        time = ao.getOccurrence().getTimeCollected();
    }

    public Pair<String, Integer> getAuthor() {
        return author;
    }

    public void setAuthor(Pair<String, Integer> author) {
        this.author = author;
        logger.debug("Author set to "+author);
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
        notifyObservers("updateCode");
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
        notifyObservers("updateName");
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
                    System.out.println("Sources: \""+o.getDataSource()+"\"");
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
    
    private AuthorOccurrence prepareAuthorOccurrence(boolean newRecord) {
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
            o = ao.getOccurrence();
        
        assert author != null;
        if (newRecord)
            a = new Author();
        else
            a = ao.getAuthor();
        a.setId(author.getSecond());
        
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
        //o.setIsoDateTimeBegin(); ???
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
            return newAO;
        } else {
            ao.setAuthor(a);
            ao.setOccurrence(o);
            return ao;
        }
        
    }//prepareAuthorOccurrence
    
    public void createRecord() {
        logger.debug("About to insert new occurrence record");
        //FIXME:
        try {     
            database.executeInsert(prepareAuthorOccurrence(true));
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }        
    }//createRecord()
    
    public void updateRecord() {        
        logger.debug("About to update existing occurrence record id="+ao.getId());
        //FIXME:
        try {     
            database.executeUpdate(prepareAuthorOccurrence(false));
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
    }//updateRecord()
}


