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
import java.util.Observable;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.exception.DBLayerException;
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
import org.apache.log4j.Logger;

/**
 *
 * @author fraktalek
 */
public class Search extends Observable {
    public static final int INTERVAL = 1;
    public static final int MONTH = 2;
    
    private Logger logger;
    private DBLayer database;      
    
    private int coordinateSystem;
    private Occurrence o; //original occurrence
    
    //list of authors user selects
    private ArrayList<Pair<Pair<String,Integer>,String>> authorList;
    private ArrayList<String> resultRevision;
    
    private Pair<String,Integer> village = new Pair<String,Integer>("",-1);
    
    private ArrayList<String> taxonList = new ArrayList<String>();
    private String taxonOriginal;
    
    private String localityDescription;
    private Integer year;
    private String habitatNote;
    private String occurrenceNote;
    private Pair<String, Integer> territoryName = new Pair<String,Integer>("",-1);
    private Pair<String, Integer> phytName = new Pair<String,Integer>("",-1);
    private Pair<String, Integer> phytCode = new Pair<String,Integer>("",-1);
    private String phytCountry;
    private String quadrant;
    private Double altitude;
    private Double longitude;
    private Double latitude;
    private String source;
    private Pair<String,Integer> publication = new Pair<String,Integer>("",-1);
    private String herbarium;
    private Pair<String,Integer> project = new Pair<String,Integer>("",-1);
    private Integer month;
    private Date fromDate, toDate;
    
    private boolean yearChanged = false;
    private boolean dayChanged = false;
    private boolean monthChanged = false;
    private boolean timeChanged = false;
    
    
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

    private int timeChoice = INTERVAL;
    private Boolean editMode = false;
    
    
    /** Creates a new instance of AddEdit */
    public Search(DBLayer database) {
        this.database = database;
        this.editMode = editMode;
        logger = Logger.getLogger(this.getClass().getPackage().getName());                
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
        if (!year.equals(1))
            yearChanged = true;
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
        if (!month.equals(1))
            monthChanged = true;
        logger.debug("Month set to "+month);
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
                records = database.more(resultid, 0, resultsCount-1);
                plants = new Pair[resultsCount];
                for (int i = 0; i < resultsCount; i++)
                {
                    p = (Plant)((Object[])records[i])[0];
                    plants[i] = new Pair(p.getTaxon(), p.getId());
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
                records = database.more(resultid, 0, resultsCount-1);
                authors = new Pair[resultsCount];
                for (int i = 0; i < resultsCount; i++)
                {
                    a = (Author)((Object[])records[i])[0];
                    authors[i] = new Pair<String, Integer>(a.getWholeName(), a.getId());
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
                records = database.more(resultid, 0, resultsCount-1);
                authorRoles = new String[resultsCount];
                String r;
                for (int i = 0; i < resultsCount; i++)
                {
                    r = (String)((Object[])records[i])[0];
                    authorRoles[i] = r;
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
                records = database.more(resultid, 0, resultsCount-1);
                villages = new Pair[resultsCount];
                for (int i = 0; i < resultsCount; i++)
                {
                    v = (Village)((Object[])records[i])[0];
                    villages[i] = new Pair<String, Integer>(v.getName(), v.getId());
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
                records = database.more(resultid, 0, resultsCount-1);
                territories = new Pair[resultsCount];
                for (int i = 0; i < resultsCount; i++)
                {
                    t = (Territory)((Object[])records[i])[0];
                    territories[i] = new Pair<String,Integer>(t.getName(), t.getId());
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
                records = database.more(resultid, 0, resultsCount-1);
                phytNames = new Pair[resultsCount];
                for (int i = 0; i < resultsCount; i++)
                {
                    p = (Phytochorion)((Object[])records[i])[0];
                    phytNames[i] = new Pair<String,Integer>(p.getName(), p.getId());
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
                records = database.more(resultid, 0, resultsCount-1);
                phytCodes = new Pair[resultsCount];
                for (int i = 0; i < resultsCount; i++)
                {
                    p = (Phytochorion)((Object[])records[i])[0];
                    phytCodes[i] = new Pair<String,Integer>(p.getCode(), p.getId());
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
                records = database.more(resultid, 0, resultsCount-1);
                countriesTemp = new String[resultsCount];
                for (int i = 0; i < resultsCount; i++)
                {
                    h = (Habitat)((Object[])records[i])[0];
                    if (h.getCountry() ==  null)
                        System.out.println("\twas null");
                    if (i == 0) {
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
                records = database.more(resultid, 0, resultsCount-1);
                sources = new String[resultsCount];
                for (int i = 0; i < resultsCount; i++)
                {
                    o = (Occurrence)((Object[])records[i])[0];
                    sources[i] = o.getDataSource();
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
                records = database.more(resultid, 0, resultsCount-1);
                publications = new Pair[resultsCount];
                for (int i = 0; i < resultsCount; i++)
                {
                    p = (Publication)((Object[])records[i])[0];
                    publications[i] = new Pair(p.getReferenceCitation(), p.getId());
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
                records = database.more(resultid, 0, resultsCount-1);
                projects = new Pair[resultsCount];
                for (int i = 0; i < resultsCount; i++)
                {
                    m = (Metadata)((Object[])records[i])[0];
                    projects[i] = new Pair(m.getDataSetTitle(), m.getId());
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

    public void setTimeChoice(int choice) {
        this.timeChoice = choice;
        switch (choice) {
            case INTERVAL:
                logger.debug("Time choice set to INTERVAL");
                break;
            case MONTH:
                logger.debug("Time choice set to MONTH");
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
        boolean allNull = true;
        for (int i = 0; i < authorList.size(); i++) {
            Pair<Pair<String,Integer>,String> p = authorList.get(i);
            if (isNotEmpty(p.getSecond()))
                allNull = false;
            if (isNotEmpty(p.getFirst()))
                allNull = false;
        }
        
        for (int i = 0; i < resultRevision.size(); i++) {
            if (isNotEmpty(resultRevision.get(i)))
                allNull = false;
        }
        
        if (isNotEmpty(village))
            allNull = false;
        
        for (int i = 0; i < taxonList.size(); i++) {
            if (isNotEmpty(taxonList.get(i)))
                allNull = false;
        }
            
        if (isNotEmpty(taxonOriginal))
            allNull = false;
    
        if (isNotEmpty(localityDescription))
            allNull = false;
        
        
        if (isNotEmpty(habitatNote))
            allNull = false;
        
        if (isNotEmpty(territoryName))
            allNull = false;
        
        if (isNotEmpty(phytName))
            allNull = false;
        
        if (isNotEmpty(phytCode))
            allNull = false;
        
        if (isNotEmpty(phytCountry))
            allNull = false;
        
        if (isNotEmpty(quadrant))
            allNull = false;
               
        if (isNotEmpty(altitude))
            allNull = false;
        
        if (isNotEmpty(longitude))
            allNull = false;
        
        if (isNotEmpty(latitude))
            allNull = false;
        
        if (isNotEmpty(source))
            allNull = false;

        if (isNotEmpty(publication))
            allNull = false;
        
        if (isNotEmpty(herbarium))
            allNull = false;
        
        if (isNotEmpty(project))
            allNull = false;

        if (timeChoice == INTERVAL) {
            if (isNotEmpty(fromDate) && isNotEmpty(toDate)) {
                Calendar from = Calendar.getInstance(), to = Calendar.getInstance();
                from.setTime(fromDate); to.setTime(toDate);
                from.set(Calendar.HOUR,0);
                from.set(Calendar.MINUTE, 0);
                from.set(Calendar.MILLISECOND, 0);
                
                to.set(Calendar.HOUR,0);
                to.set(Calendar.MINUTE, 0);
                to.set(Calendar.MILLISECOND, 0);
                
                if (to.compareTo(from) < 0)
                    return new Pair<Boolean,String>(false,"From-date must be before or equal to to-date");
                allNull = false;
            }
            
            if ((isNotEmpty(fromDate) && !isNotEmpty(toDate)) || 
                (!isNotEmpty(fromDate) && isNotEmpty(toDate)) 
                )
                return new Pair<Boolean,String>(false,"You have to specify either both from and to date or none of them.");
        }
        
        if (timeChoice == MONTH)
            if (isNotEmpty(month) && !month.equals(12)) //12 is the index of the empty String in the MonthChooser's ComboBox, the empty String is added in Post-init code in SearchView
                allNull = false;
        
        if (allNull)
            return new Pair<Boolean,String>(false,"You have to fill in at least one field.");
        else
            return new Pair<Boolean,String>(true,"");
    }
    
    public SelectQuery constructQuery() {
        SelectQuery sq = null;
            //FIXME:
            try {
                sq = database.createQuery(AuthorOccurrence.class);
                sq.createAlias(AuthorOccurrence.AUTHOR,"author");
                sq.createAlias(AuthorOccurrence.OCCURRENCE,"occ");
                sq.createAlias("occ."+Occurrence.HABITAT,"habitat");
                sq.createAlias("occ."+Occurrence.PLANT,"plant");
                sq.createAlias("occ."+Occurrence.PUBLICATION,"publication");
                sq.createAlias("occ."+Occurrence.METADATA,"metadata");
                sq.createAlias("habitat."+Habitat.PHYTOCHORION,"phyt");
                sq.createAlias("habitat."+Habitat.NEARESTVILLAGE,"vill");
                sq.createAlias("habitat."+Habitat.TERRITORY,"territory");
                sq.addOrder(PlantloreConstants.DIRECT_ASC, "occ."+Occurrence.YEARCOLLECTED); //setridit podle roku
                sq.addRestriction(PlantloreConstants.RESTR_NE, "occ."+Occurrence.DELETED, null, 1, null);
                if (timeChoice == INTERVAL && isNotEmpty(fromDate)) {
                    ArrayList a = new ArrayList();
                    a.add(fromDate);
                    a.add(toDate);
                    sq.addRestriction(PlantloreConstants.RESTR_BETWEEN,"occ."+Occurrence.ISODATETIMEBEGIN,null,null,a);
                }
                int resultId = database.executeQuery(sq);
                System.out.println("Number of results: "+database.getNumRows(resultId));
            } catch (RemoteException ex) {                
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }
        return sq;
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
    
    public void setToDate(Date d) {
        this.toDate = d;
        logger.debug("ToDate set to "+d);
    }
    
    public void setFromDate(Date d) {
        this.fromDate = d;
        logger.debug("FromDate set to "+d);
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
}


