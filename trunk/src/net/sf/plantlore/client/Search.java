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
import javax.swing.text.PlainDocument;
import net.sf.plantlore.common.DBLayerUtils;
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
import net.sf.plantlore.common.record.Record;
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
    
    private ArrayList<Column> columns;
    private int newResultId = -1;
    private SelectQuery exportQuery = null;
    
    
    /** Creates a new instance of AddEdit */
    public Search(DBLayer database) {
        this.database = database;
        this.editMode = editMode;
        logger = Logger.getLogger(this.getClass().getPackage().getName());         
        clear();
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
        
        if (taxonList != null)
            for (int i = 0; i < taxonList.size(); i++) {
                if (isNotEmpty(taxonList.get(i)))
                    allNull = false;
            }
                
        if (isNotEmpty(localityDescription))
            allNull = false;
        
        
        if (isNotEmpty(habitatNote))
            allNull = false;
        
        if (isNotEmpty(occurrenceNote))
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
                
                //zero fields not set by the user so that the comparison below is correct
                from.set(Calendar.HOUR,0);
                from.set(Calendar.MINUTE, 0);
                from.set(Calendar.SECOND, 0);
                from.set(Calendar.MILLISECOND, 0);
                
                to.set(Calendar.HOUR,0);
                to.set(Calendar.MINUTE, 0);
                to.set(Calendar.SECOND, 0);
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
        
       /* if (allNull)
            return new Pair<Boolean,String>(false,"You have to fill in at least one field.");
        else */
            return new Pair<Boolean,String>(true,"");
    }
    
    public Pair<SelectQuery,SelectQuery> constructQuery() {
        DBLayerUtils dlu = new DBLayerUtils(database);
        SelectQuery sq = null, exportQuery = null;
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
                
                /*---------------------------------------------------------------------------------------
                 *  Prepare the ExportQuery and all its aliases.
                 *  Note that ExportQuery is built over the [Occurrence] table.
                 *---------------------------------------------------------------------------------------*/
                String habitatAlias = Record.alias(Habitat.class) +".";
                exportQuery = database.createQuery(Occurrence.class);
                exportQuery.createAlias(Occurrence.HABITAT, Record.alias(Habitat.class));
                exportQuery.createAlias(Occurrence.PLANT, Record.alias(Plant.class));
                exportQuery.createAlias(Occurrence.METADATA, Record.alias(Metadata.class));
                exportQuery.createAlias(Occurrence.PUBLICATION, Record.alias(Publication.class));
                exportQuery.createAlias(habitatAlias+Habitat.TERRITORY, Record.alias(Territory.class));
                exportQuery.createAlias(habitatAlias+Habitat.NEARESTVILLAGE, Record.alias(Village.class));
                exportQuery.createAlias(habitatAlias+Habitat.PHYTOCHORION, Record.alias(Phytochorion.class));
                exportQuery.addOrder(PlantloreConstants.DIRECT_ASC, Occurrence.YEARCOLLECTED); //setridit podle roku
                //---------------------------------------------------------------------------------------
                // This restriction is in question; 
                // sometimes you want to export deleted data from the source database
                // so that they are deleted in the target database, too.
                // The restriction doesn't apply here. Dead records may be exported, it is the Filter describing
                // the file format that decides whether dead record shall be exported or not.
                
                //exportQuery.addRestriction(PlantloreConstants.RESTR_NE, Occurrence.DELETED, null, 1, null);
                
                //---------------------------------------------------------------------------------------
                for (Column column : columns) {
                    switch (column.type) {
                        case AUTHOR:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"author."+Author.WHOLENAME);
                            break;
                        case HABITAT_ALTITUDE:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.ALTITUDE);
                            break;
                        case HABITAT_COUNTRY:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.COUNTRY);
                            break;
                        case HABITAT_DESCRIPTION:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.DESCRIPTION);                    
                            break;
                        case HABITAT_LATITUDE:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.LATITUDE);
                            break;
                        case HABITAT_LONGITUDE:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.LONGITUDE);
                            break;
                        case HABITAT_NEAREST_VILLAGE_NAME:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"vill."+Village.NAME);
                            break;
                        case HABITAT_NOTE:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.NOTE);
                            break;
                        case HABITAT_QUADRANT:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"habitat."+Habitat.QUADRANT);
                            break;
                        case METADATA_DATASETTITLE:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"metadata."+Metadata.DATASETTITLE);
                            break;
                        case NUMBER:
                            break;
                        case OCCURRENCE_DATASOURCE:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"occ."+Occurrence.DATASOURCE);
                            break;
                        case OCCURRENCE_DAYCOLLECTED:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"occ."+Occurrence.DAYCOLLECTED);
                            break;
                        case OCCURRENCE_HERBARIUM:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"occ."+Occurrence.HERBARIUM);
                            break;
                        case OCCURRENCE_ID:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"occ."+Occurrence.ID);                            
                            break;
                        case OCCURRENCE_MONTHCOLLECTED:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"occ."+Occurrence.MONTHCOLLECTED);
                            break;
                        case OCCURRENCE_NOTE:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"occ."+Occurrence.NOTE);
                            break;
                        case OCCURRENCE_TIMECOLLECTED:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"occ."+Occurrence.TIMECOLLECTED);
                            break;
                        case OCCURRENCE_YEARCOLLECTED:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"occ."+Occurrence.YEARCOLLECTED);
                            break;
                        case PHYTOCHORION_CODE:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"phyt."+Phytochorion.CODE);                    
                            break;
                        case PHYTOCHORION_NAME:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"phyt."+Phytochorion.NAME);                    
                            break;
                        case PLANT_TAXON:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"plant."+Plant.TAXON);
                            break;
                        case PUBLICATION_COLLECTIONNAME:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"publication."+Publication.REFERENCECITATION);
                            break;
                        case SELECTION:
                            break;
                        case TERRITORY_NAME:
                            sq.addProjection(PlantloreConstants.PROJ_PROPERTY,"territory."+Territory.NAME);
                            break;
                        default:                                                
                    }
                }
                
                if (isNotEmpty(village)) {
                    sq.addRestriction(PlantloreConstants.RESTR_EQ,"habitat."+Habitat.NEARESTVILLAGE,null,dlu.getObjectFor(village.getSecond(),Village.class),null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_EQ,habitatAlias+Habitat.NEARESTVILLAGE,null,dlu.getObjectFor(village.getSecond(),Village.class),null);
                }
                
                int notEmpty = 0;
                for (int i = 0; i < authorList.size(); i++) {
                    Pair<Pair<String,Integer>,String> authorPair = authorList.get(i);
                    if (isNotEmpty(authorPair.getFirst()))
                        notEmpty++;
                    if (isNotEmpty(authorPair.getSecond()))
                        notEmpty++;
                }
                
                if (notEmpty > 0) {
                    Object[] args = new Object[notEmpty*4];
                    int conditionNumber = 0;
                    for (int i = 0; i < authorList.size(); i++) {
                        Pair<Pair<String,Integer>,String> authorPair = authorList.get(i);
                        if (isNotEmpty(authorPair.getFirst())) {
                            args[4*conditionNumber] = PlantloreConstants.RESTR_EQ;
                            args[4*conditionNumber+1] = AuthorOccurrence.AUTHOR;
                            args[4*conditionNumber+2] = null;
                            args[4*conditionNumber+3] = dlu.getObjectFor(authorPair.getFirst().getSecond(),Author.class);
                            conditionNumber++;
                        }
                        if (isNotEmpty(authorPair.getSecond())) {
                            args[4*conditionNumber] = PlantloreConstants.RESTR_EQ;
                            args[4*conditionNumber+1] = AuthorOccurrence.ROLE;
                            args[4*conditionNumber+2] = null;
                            args[4*conditionNumber+3] = authorPair.getSecond();
                            conditionNumber++;
                        }
                    }
                    sq.addOrRestriction(args);
                    //ExportQuery is over the Occurrence table!   exportQuery.addOrRestriction(args); 
                }

                notEmpty = 0;
                if (taxonList != null)
                    for (int i = 0; i < taxonList.size(); i++) {
                        if (isNotEmpty(taxonList.get(i)))
                            notEmpty++;
                    }
                
                if (notEmpty > 0) {
                    Object[] args = new Object[notEmpty*4], exportQueryArgs = new Object[notEmpty*4];
                    for (int i = 0; i < taxonList.size(); i++) {
                        String taxon = taxonList.get(i);
                        if (isNotEmpty(taxon)) {
                            args[4*i] = exportQueryArgs[4*i] = PlantloreConstants.RESTR_EQ;
                            args[4*i+1] = "occ."+Occurrence.PLANT; exportQueryArgs[4*i+1] = Occurrence.PLANT; 
                            args[4*i+2] = exportQueryArgs[4*i+2] = null;
                            args[4*i+3] = exportQueryArgs[4*i+3] = dlu.getObjectFor(lookupPlant(taxon),Plant.class);
                        }
                    }
                    sq.addOrRestriction(args);
                    exportQuery.addOrRestriction(exportQueryArgs);
                }
                if (isNotEmpty(localityDescription)) {
                    sq.addRestriction(PlantloreConstants.RESTR_LIKE,"habitat."+Habitat.DESCRIPTION,null,"%"+localityDescription+"%",null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_LIKE,habitatAlias+Habitat.DESCRIPTION,null,"%"+localityDescription+"%",null);
                }
                
                if (isNotEmpty(occurrenceNote)) {
                    sq.addRestriction(PlantloreConstants.RESTR_LIKE,"occ."+Occurrence.NOTE,null,"%"+occurrenceNote+"%",null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_LIKE,Occurrence.NOTE,null,"%"+occurrenceNote+"%",null);
                }
                
                if (isNotEmpty(habitatNote)) {
                    sq.addRestriction(PlantloreConstants.RESTR_LIKE,"habitat."+Habitat.NOTE,null,"%"+habitatNote+"%",null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_LIKE,habitatAlias+Habitat.NOTE,null,"%"+habitatNote+"%",null);
                }
                
                if (isNotEmpty(territoryName)) {
                    sq.addRestriction(PlantloreConstants.RESTR_EQ,"habitat."+Habitat.TERRITORY,null,dlu.getObjectFor(territoryName.getSecond(),Territory.class),null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_EQ,habitatAlias+Habitat.TERRITORY,null,dlu.getObjectFor(territoryName.getSecond(),Territory.class),null);
                }
                
                if (isNotEmpty(phytName)) {
                    sq.addRestriction(PlantloreConstants.RESTR_EQ,"habitat."+Habitat.PHYTOCHORION,null,dlu.getObjectFor(phytName.getSecond(),Phytochorion.class),null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_EQ,habitatAlias+Habitat.PHYTOCHORION,null,dlu.getObjectFor(phytName.getSecond(),Phytochorion.class),null);
                }
                
                if (isNotEmpty(phytCountry)) {
                    sq.addRestriction(PlantloreConstants.RESTR_EQ,"habitat."+Habitat.COUNTRY,null,phytCountry,null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_EQ,habitatAlias+Habitat.COUNTRY,null,phytCountry,null);
                }
                
                if (isNotEmpty(quadrant)) {
                    sq.addRestriction(PlantloreConstants.RESTR_EQ,"habitat."+Habitat.QUADRANT,null,quadrant,null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_EQ,habitatAlias+Habitat.QUADRANT,null,quadrant,null);
                }
                
                if (isNotEmpty(altitude)) {
                    sq.addRestriction(PlantloreConstants.RESTR_EQ,"habitat."+Habitat.ALTITUDE,null,altitude,null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_EQ,habitatAlias+Habitat.ALTITUDE,null,altitude,null);
                }
                
                if (isNotEmpty(longitude)) {
                    sq.addRestriction(PlantloreConstants.RESTR_EQ,"habitat."+Habitat.LONGITUDE,null,longitude,null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_EQ,habitatAlias+Habitat.LONGITUDE,null,longitude,null);
                }

                if (isNotEmpty(latitude)) {
                    sq.addRestriction(PlantloreConstants.RESTR_EQ,"habitat."+Habitat.LATITUDE,null,latitude,null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_EQ,habitatAlias+Habitat.LATITUDE,null,latitude,null);
                }

                if (isNotEmpty(source)) {
                    sq.addRestriction(PlantloreConstants.RESTR_EQ,"occ."+Occurrence.DATASOURCE,null,source,null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_EQ,Occurrence.DATASOURCE,null,source,null);
                }
                
                if (isNotEmpty(publication)) {
                    //FIXME: mozna pridat addOrRestriction na vsechny relevantni sloupky Publication
                    sq.addRestriction(PlantloreConstants.RESTR_EQ,"occ."+Occurrence.PUBLICATION,null,dlu.getObjectFor(publication.getSecond(),Publication.class),null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_EQ,Occurrence.PUBLICATION,null,dlu.getObjectFor(publication.getSecond(),Publication.class),null);
                }
                
                if (isNotEmpty(herbarium)) {
                    sq.addRestriction(PlantloreConstants.RESTR_EQ,"occ."+Occurrence.HERBARIUM,null,herbarium,null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_EQ,Occurrence.HERBARIUM,null,herbarium,null);
                }
                
                if (isNotEmpty(project)) {
                    sq.addRestriction(PlantloreConstants.RESTR_EQ,"occ."+Occurrence.METADATA,null,dlu.getObjectFor(project.getSecond(),Metadata.class),null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_EQ,Occurrence.METADATA,null,dlu.getObjectFor(project.getSecond(),Metadata.class),null);
                }
                
                if (timeChoice == INTERVAL && isNotEmpty(fromDate)) {
                    Calendar from = Calendar.getInstance(), to = Calendar.getInstance();
                    from.setTime(fromDate); to.setTime(toDate);
                    
                    //set the begining of the day
                    from.set(Calendar.HOUR_OF_DAY,0);
                    from.set(Calendar.MINUTE, 0);
                    from.set(Calendar.SECOND, 0);
                    from.set(Calendar.MILLISECOND, 0);

                    //set the end of the day
                    to.set(Calendar.HOUR_OF_DAY,23);
                    to.set(Calendar.MINUTE, 59);
                    to.set(Calendar.SECOND, 59);
                    to.set(Calendar.MILLISECOND, 999);
                    
                    ArrayList a = new ArrayList();
                    a.add(from.getTime());
                    a.add(to.getTime());
                    System.out.println("Searching between "+from.getTime()+" and "+to.getTime());
                    sq.addRestriction(PlantloreConstants.RESTR_BETWEEN,"occ."+Occurrence.ISODATETIMEBEGIN,null,null,a);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_BETWEEN,Occurrence.ISODATETIMEBEGIN,null,null,a);
                }
                
                if (timeChoice == MONTH && isNotEmpty(month)) {
                    sq.addRestriction(PlantloreConstants.RESTR_EQ,"occ."+Occurrence.MONTHCOLLECTED,null,month,null);
                    exportQuery.addRestriction(PlantloreConstants.RESTR_EQ,Occurrence.MONTHCOLLECTED,null,month,null);
                }
                
                this.exportQuery = exportQuery;
                int resultId = database.executeQuery(sq);
                this.newResultId = resultId;
                logger.debug("Created new query. Number of results: "+database.getNumRows(resultId));
                //let the SearchBridge in AppCoreCtrl know that new result is there
                setChanged(); 
                notifyObservers(resultId);
            } catch (RemoteException ex) {                
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }
        return new Pair<SelectQuery,SelectQuery>(sq,exportQuery);
    }
    
    public void clear() {
        clearAuthors();
    
        village = null;
        taxonList = null;
        localityDescription = null;
        year = null;
        habitatNote = null;
        occurrenceNote = null;
        territoryName = null;
        phytName = null;
        phytCode = null;
        phytCountry = null;
        quadrant = null;
        altitude = null;
        longitude = null;
        latitude = null;
        source = null;
        publication = null;
        herbarium = null;
        project = null;
        month  = null;
        fromDate = null;
        toDate = null;
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

    public int getNewResultId() {
        return newResultId;
    }

    public void setColumns(ArrayList<Column> columns) {
        logger.debug("Setting columns.");
        this.columns = columns;
    }

    public Pair<String, Integer>[] getPlants() {
        return plants;
    }

    public void setPlants(Pair<String, Integer>[] plants) {
        this.plants = plants;
    }

    public Pair<String, Integer>[] getAuthors() {
        return authors;
    }

    public void setAuthors(Pair<String, Integer>[] authors) {
        this.authors = authors;
    }

    public String[] getAuthorRoles() {
        return authorRoles;
    }

    public void setAuthorRoles(String[] authorRoles) {
        this.authorRoles = authorRoles;
    }

    public Pair<String, Integer>[] getVillages() {
        return villages;
    }

    public void setVillages(Pair<String, Integer>[] villages) {
        this.villages = villages;
    }

    public Pair<String, Integer>[] getTerritories() {
        return territories;
    }

    public void setTerritories(Pair<String, Integer>[] territories) {
        this.territories = territories;
    }

    public Pair<String, Integer>[] getPhytNames() {
        return phytNames;
    }

    public void setPhytNames(Pair<String, Integer>[] phytNames) {
        this.phytNames = phytNames;
    }

    public Pair<String, Integer>[] getPhytCodes() {
        return phytCodes;
    }

    public void setPhytCodes(Pair<String, Integer>[] phytCodes) {
        this.phytCodes = phytCodes;
    }

    public String[] getCountries() {
        return countries;
    }

    public void setCountries(String[] countries) {
        this.countries = countries;
    }

    public String[] getSources() {
        return sources;
    }

    public void setSources(String[] sources) {
        this.sources = sources;
    }

    public Pair<String, Integer>[] getPublications() {
        return publications;
    }

    public void setPublications(Pair<String, Integer>[] publications) {
        this.publications = publications;
    }

    public Pair<String, Integer>[] getProjects() {
        return projects;
    }

    public void setProjects(Pair<String, Integer>[] projects) {
        this.projects = projects;
    }
    
    public SelectQuery getExportQuery() {
        return exportQuery;
    }
}

