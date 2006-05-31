/*
 * AppCore.java
 *
 * Created on 14. leden 2006, 17:56
 *
 */

package net.sf.plantlore.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Observable;
import java.util.prefs.Preferences;
import javax.swing.table.TableModel;
import net.sf.plantlore.client.login.DBInfo;
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
import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.Village;
import net.sf.plantlore.l10n.L10n;

// Imports for temporary db access
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.server.HibernateDBLayer;
import org.apache.log4j.Logger;

/** Application core model
 *
 * @author Jakub
 */
public class AppCore extends Observable
{
    private Preferences prefs;
    private MainConfig mainConfig;
    
    private int recordsPerPage = 30;
    private int currentPage = 1;
    private DBLayer database;  
    private Right accessRights;
    private OverviewTableModel tableModel;
    private TableSorter tableSorter;
    private Logger logger;
    private ArrayList<Column> columns;
    
    private SelectQuery exportQuery = null;
    private boolean usingProjections = false;
    private Class rootTable = null;
    
    private int selectedRow = 0;
    
    /** data for dialogs */
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
    
    
    /** Creates a new instance of AppCore */
    public AppCore(MainConfig mainConfig)
    {
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        prefs = Preferences.userNodeForPackage(this.getClass());
        
        this.mainConfig = mainConfig;
        ArrayList<Column> columns = mainConfig.getColumns();
        if (columns.size() < 1) {
            columns = new ArrayList<Column>(10);
            columns.add(new Column(Column.Type.OCCURRENCE_ID));
            columns.add(new Column(Column.Type.SELECTION));
            columns.add(new Column(Column.Type.NUMBER));
            columns.add(new Column(Column.Type.PLANT_TAXON));        
            columns.add(new Column(Column.Type.AUTHOR));
            columns.add(new Column(Column.Type.HABITAT_NEAREST_VILLAGE_NAME));
            columns.add(new Column(Column.Type.OCCURRENCE_YEARCOLLECTED));
            columns.add(new Column(Column.Type.PHYTOCHORION_NAME));
            columns.add(new Column(Column.Type.HABITAT_DESCRIPTION));
            columns.add(new Column(Column.Type.TERRITORY_NAME));            
        }
        this.columns = columns;
        
        tableModel =  new OverviewTableModel(prefs.getInt("recordsPerPage", 30), columns);
        tableSorter = new TableSorter(tableModel);
        
        // This is here in order to skip login procedure and connect to the database automatically
        // For developement purposes only - so that we don't have to go through login each time we run Plantlore 
/*        
        this.database = new HibernateDBLayer();
        try {        
            database.initialize("jdbc:firebirdsql:localhost/3050:c:/Temp/Plantlore/plantloreHIBdata.fdb","sysdba","masterkey");
        } catch (Exception e) {
            
        }
        // --- End of temporary code
*/        
        logger.debug("AppCore observers notified");
    }
    
    /*********************************************************
                Temporary solution for DB access             

        // Use your own settings...
        database = new FirebirdDBLayer("localhost", "3050", "c:/Kovo/DatabaseTest/database/plantlore.fdb", "SYSDBA", "masterkey");
        try {
            database.initialize();
        } catch (DBLayerException e) {
            System.out.println("Error initializing database: "+e.toString());
        }                                                                                           
     **********************************************************/
    
    public DBLayer getDatabase() {
        return this.database;
    }    
    
    protected void setDatabase(DBLayer dblayer) throws RemoteException, DBLayerException {
    	this.database = dblayer;
        loadDialogData();
    }
    
    public void loadDialogData() throws RemoteException, DBLayerException {
        logger.info("Loading dialog data ...");
        loadPlants();
        loadAuthors();
        loadAuthorRoles();
        loadVillages();
        loadPhytNames(); //loads phytCodes too
        loadTerritories();
        loadCountries();
        loadSources();
        loadPublications();
        loadProjects();
        logger.debug("Dialog data loaded.");
    }
    
    public Right getAccessRights() {
    	return this.accessRights;
    }
    
    protected void setAccessRights(Right rights) {
    	this.accessRights = rights;
    }
    
    /** Returns table model for the main Overview.
     *
     * Should be called only after the user logs in to a database.
     *
     * @return null if the database connection wasn't created yet or an exception was thrown while working with it...
     * @return OverviewTableModel otherwise - creates a new one if it hasn't been created yet
     */
    public OverviewTableModel getTableModel() {
        return tableModel;
    }
    
    public TableSorter getTableSorter() {
        return tableSorter;
    }
        
    public void selectAll() {
        if (tableSorter != null)
            tableSorter.selectAll();
        //setChanged();
        //notifyObservers();
    }

    public void selectNone() {
        if (tableSorter != null)
            tableSorter.selectNone();
        //setChanged();
        //notifyObservers();
    }
    public void invertSelected() {
        if (tableSorter != null)
            tableSorter.invertSelected();
        //setChanged();
        //notifyObservers();
    }

    public int getRecordsPerPage() {
        if (tableSorter != null)
            return tableSorter.getPageSize();
        else 
            return 0;
    }

    public void setRecordsPerPage(int recordsPerPage) {        
        if (tableSorter != null)
        {
            logger.info("Setting records per page to "+recordsPerPage);
            tableSorter.setPageSize(recordsPerPage);
            setChanged();
            notifyObservers("RECORDS_PER_PAGE");        
        }
    }

    public void nextPage() {
        if (tableSorter != null)
        {
            //FIXME:
            try {
                tableSorter.nextPage();
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }
            setChanged();
            notifyObservers("PAGE_CHANGED");        
        }
    }
    
    public void prevPage() {
        if (tableSorter != null)
        {
            //FIXME:
            try {
                tableSorter.prevPage();
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }
            setChanged();
            notifyObservers("PAGE_CHANGED");
        }
    }

    public int getCurrentPage() {
        if (tableSorter != null)
            return tableSorter.getCurrentPage();
        else
            return 0;
    }

    public void setCurrentPage(int currentPage) {
        if (tableSorter != null)
        {
            tableSorter.setCurrentPage(currentPage);
            setChanged();
            notifyObservers();
        }
    }
    
    public int getResultsCount() {
        if (tableSorter != null)        
            return tableSorter.getResultsCount();
        else
            return 0;
    }
    
    public int getPagesCount() {
        if (tableSorter != null)
            return tableSorter.getPagesCount();
        else
            return 0;
    }
    
    public void invertSelectedOnCurrentRow() {
        tableSorter.invertSelected(selectedRow);
    }
    
    public void setSelectedRow(int i) 
    {
        selectedRow = i;
        logger.debug("Selected row #"+i);
    }
    
    public int getSelectedRowNumber()
    {
        return selectedRow;
    }
    
    public Object[] getSelectedRow()
    {
        return tableSorter.getRow(selectedRow);
    }
    
    public Integer getSelectedOccurrence() {
        //Object[] row = tableSorter.getRow(selectedRow);
        //return (Integer)row[row.length-1];
        return tableSorter.getOccurrenceId(selectedRow);
    }
    
    public void savePreferences() throws IOException {
        logger.info("Saving main window preferences.");
        prefs.putInt("recordsPerPage", recordsPerPage);
        mainConfig.save();
    }
    
    public void setResultId(int resultId, SelectQuery sq) throws RemoteException, DBLayerException {
        setChanged();
        notifyObservers("LOADING_NEW_DATA");
        tableSorter.setResultId(resultId, sq);
        setChanged();
        notifyObservers("NEW_QUERY");
    }

    // The export query should not be stored - a new export query must be constructed each time!!
//    public void setExportQuery(SelectQuery query, boolean useProjections, Class rootTable) {
//        this.exportQuery = query;
//        this.usingProjections = useProjections;
//        this.rootTable = rootTable;
//    }
    
    // The export query should not be returned - a new export query must be constructed each time!!
//    public SelectQuery getExportQuery() {
//        return exportQuery;
//    }
    
    public Class getRootTable() {
    	return rootTable;
    }
    
    public boolean areProjectionsEnabled() {
    	return usingProjections;
    }
    
    public Pair<String, Integer>[] loadPlants() throws RemoteException, DBLayerException {
        SelectQuery sq;
        int resultid;
        int resultsCount;
        Object[] records;
        Object[] row;
        
        setChanged();
        notifyObservers("LOADING_PLANTS");
                
        sq = database.createQuery(Plant.class);
        sq.addOrder(PlantloreConstants.DIRECT_ASC, Plant.TAXON);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY, Plant.TAXON);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY, Plant.ID);
        resultid = database.executeQuery(sq);
        resultsCount = database.getNumRows(resultid);
        records = database.more(resultid, 0, resultsCount-1);
        plants = new Pair[resultsCount];
        for (int i = 0; i < resultsCount; i++)
        {
            row = (Object[])records[i];
            plants[i] = new Pair((String)row[0], (Integer)row[1]);
        }
        setChanged();
        notifyObservers("LOADED");
        logger.info("Loaded: "+resultsCount+" plants.");
        return plants;
    }
    
    public Pair<String, Integer>[] loadAuthors() throws DBLayerException, RemoteException {
        SelectQuery sq;
        int resultid;
        int resultsCount;
        Object[] records;
        Object[] row;

        setChanged(); notifyObservers("LOADING_AUTHORS");
        
        sq = database.createQuery(Author.class);
        sq.addOrder(PlantloreConstants.DIRECT_ASC, Author.WHOLENAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY, Author.WHOLENAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY, Author.ID);

        resultid = database.executeQuery(sq);
        resultsCount = database.getNumRows(resultid);
        records = database.more(resultid, 0, resultsCount-1);
        authors = new Pair[resultsCount];
        for (int i = 0; i < resultsCount; i++)
        {
            row = (Object[])records[i];
            authors[i] = new Pair<String, Integer>((String)row[0], (Integer)row[1]);
        }
        logger.info("Loaded: "+resultsCount+" authors.");
        setChanged(); notifyObservers("LOADED");
        return authors;
    }
    
    public String[] loadAuthorRoles() throws DBLayerException, RemoteException {
        SelectQuery sq;
        int resultid;
        int resultsCount;
        Object[] records;

        setChanged(); notifyObservers("LOADING_AUTHOR_ROLES");
        
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
        logger.info("Loaded: "+resultsCount+" author roles.");
        setChanged(); notifyObservers("LOADED");
        return authorRoles;
    }

    public Pair<String, Integer>[] loadVillages() throws DBLayerException, RemoteException {
        SelectQuery sq;
        int resultid;
        int resultsCount;
        Object[] records;
        Object[] row;

        setChanged(); notifyObservers("LOADING_VILLAGES");
        sq = database.createQuery(Village.class);
        sq.addOrder(PlantloreConstants.DIRECT_ASC, Village.NAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY, Village.NAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY, Village.ID);
        resultid = database.executeQuery(sq);
        resultsCount = database.getNumRows(resultid);
        records = database.more(resultid, 0, resultsCount-1);
        villages = new Pair[resultsCount];
        for (int i = 0; i < resultsCount; i++)
        {
            row = (Object[])records[i];
            villages[i] = new Pair<String, Integer>((String)row[0],(Integer)row[1]);
        }
        logger.info("Loaded: "+resultsCount+" villages.");
        setChanged(); notifyObservers("LOADED");
        return villages;
    }

    public Pair<String, Integer>[] loadTerritories() throws DBLayerException, RemoteException {
        SelectQuery sq;
        int resultid;
        int resultsCount;
        Object[] records;
        Object[] row;

        setChanged(); notifyObservers("LOADING_TERRITORIES");
        sq = database.createQuery(Territory.class);
        sq.addOrder(PlantloreConstants.DIRECT_ASC, Territory.NAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY, Territory.NAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY, Territory.ID);
        resultid = database.executeQuery(sq);
        resultsCount = database.getNumRows(resultid);
        records = database.more(resultid, 0, resultsCount-1);
        territories = new Pair[resultsCount];
        for (int i = 0; i < resultsCount; i++)
        {
            row = (Object[])records[i];
            territories[i] = new Pair<String,Integer>((String)row[0],(Integer)row[1]);
        }
        logger.info("Loaded: "+resultsCount+" territories.");
        setChanged(); notifyObservers("LOADED");
        return territories;
    }
    
    public Pair<String, Integer>[] loadPhytNames() throws DBLayerException, RemoteException {
        SelectQuery sq;
        int resultid;
        int resultsCount;
        Object[] records, row;

        setChanged(); notifyObservers("LOADING_PHYTOCHORIA");
        sq = database.createQuery(Phytochorion.class);
        sq.addOrder(PlantloreConstants.DIRECT_ASC, Phytochorion.NAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY, Phytochorion.NAME);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY, Phytochorion.CODE);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY, Phytochorion.ID);
        resultid = database.executeQuery(sq);
        resultsCount = database.getNumRows(resultid);
        records = database.more(resultid, 0, resultsCount-1);
        phytNames = new Pair[resultsCount];
        phytCodes = new Pair[resultsCount];
        for (int i = 0; i < resultsCount; i++)
        {
            row = (Object[])records[i];
            phytNames[i] = new Pair<String,Integer>((String)row[0], (Integer)row[2]);
            phytCodes[i] = new Pair<String,Integer>((String)row[1], (Integer)row[2]);
        }
        logger.info("Loaded: "+resultsCount+" phytochoria.");
        setChanged(); notifyObservers("LOADED");
        return phytNames;
    }
    
    public Pair<String, Integer>[] loadPhytCodes() throws DBLayerException, RemoteException {
        loadPhytNames();
        return phytCodes;
    }
    
    public String[] loadCountries() throws DBLayerException, RemoteException {
        SelectQuery sq;
        int resultid;
        int resultsCount;
        int uniqueCount = 0;
        Object[] records;
        String country;

        setChanged(); notifyObservers("LOADING_COUNTRIES");
        sq = database.createQuery(Habitat.class);
        sq.addOrder(PlantloreConstants.DIRECT_ASC, Habitat.COUNTRY);
        sq.addProjection(PlantloreConstants.PROJ_DISTINCT, Habitat.COUNTRY);
        resultid = database.executeQuery(sq); // the values can be doubled, we need to filter them 
        resultsCount = database.getNumRows(resultid);
        records = database.more(resultid, 0, resultsCount-1);
        countries = new String[resultsCount];
        for (int i = 0; i < resultsCount; i++)
        {
            country = (String)((Object[])records[i])[0];
            countries[i] = country;
        }

        logger.info("Loaded: "+resultsCount+" countries.");
        setChanged(); notifyObservers("LOADED");
        return countries;
    }
    
    public String[] loadSources() throws DBLayerException, RemoteException {
        SelectQuery sq;
        int resultid;
        int resultsCount;
        Object[] records;

        setChanged(); notifyObservers("LOADING_SOURCES");
        sq = database.createQuery(Occurrence.class);
        sq.addOrder(PlantloreConstants.DIRECT_ASC, Occurrence.DATASOURCE);
        sq.addProjection(PlantloreConstants.PROJ_DISTINCT,Occurrence.DATASOURCE);
        resultid = database.executeQuery(sq);
        resultsCount = database.getNumRows(resultid);
        records = database.more(resultid, 0, resultsCount-1);
        sources = new String[resultsCount];
        for (int i = 0; i < resultsCount; i++)
            sources[i] = (String)((Object[])records[i])[0];

        logger.info("Loaded: "+resultsCount+" sources.");
        setChanged(); notifyObservers("LOADED");
        return sources;
    }
    
    public Pair<String, Integer>[] loadPublications() throws DBLayerException, RemoteException {
        SelectQuery sq;
        int resultid;
        int resultsCount;
        Object[] records;
        Object[] row;

        setChanged(); notifyObservers("LOADING_PUBLICATIONS");
        sq = database.createQuery(Publication.class);
        sq.addOrder(PlantloreConstants.DIRECT_ASC, Publication.REFERENCECITATION);
        sq.addProjection(PlantloreConstants.PROJ_DISTINCT,Publication.REFERENCECITATION);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Publication.ID);
        resultid = database.executeQuery(sq);
        resultsCount = database.getNumRows(resultid);
        records = database.more(resultid, 0, resultsCount-1);
        publications = new Pair[resultsCount+1];
        publications[0] = new Pair<String,Integer>("",-1); //allow the user to enter a null value
        for (int i = 0; i < resultsCount; i++)
        {
            row = (Object[])records[i];
            publications[i] = new Pair(row[0], row[1]);
        }

        logger.info("Loaded: "+resultsCount+" publications.");
        setChanged(); notifyObservers("LOADED");
        return publications;
    }

    public Pair<String, Integer>[] loadProjects() throws DBLayerException, RemoteException {
        SelectQuery sq;
        int resultid;
        int resultsCount;
        Object[] records,row;

        setChanged(); notifyObservers("LOADING_PROJECTS");
        sq = database.createQuery(Metadata.class);
        sq.addOrder(PlantloreConstants.DIRECT_ASC, Metadata.DATASETTITLE);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Metadata.DATASETTITLE);
        sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Metadata.ID);
        resultid = database.executeQuery(sq);
        resultsCount = database.getNumRows(resultid);
        records = database.more(resultid, 0, resultsCount-1);
        projects = new Pair[resultsCount];
        for (int i = 0; i < resultsCount; i++)
        {
            row = (Object[])records[i];
            projects[i] = new Pair(row[0], row[1]);
        }

        logger.info("Loaded: "+resultsCount+" projects.");
        setChanged(); notifyObservers("LOADED");
        return projects;
    }
    
    public MainConfig getMainConfig() {
        return mainConfig;
    }
    
    public void login() {
        assert database != null;
        
        tableSorter.setDatabase(database);
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
 
    public Task deleteSelected() {
        final Task task = new Task() {
            int deleted = 0;
            
            public Object task() throws DBLayerException, RemoteException {
                DBLayerUtils dlu = new DBLayerUtils(database);

                Collection<Integer> toBeDeleted = getTableModel().getSelection().values();
                setLength(toBeDeleted.size()*2); //inform about approx. length of this task
                setStatusMessage(L10n.getFormattedString("Delete.Message.ProgressInfo",deleted,toBeDeleted.size()));
                for (Integer i : toBeDeleted) {
                    Occurrence occ = (Occurrence) dlu.getObjectFor(i.intValue(), Occurrence.class);
                    AuthorOccurrence[] aos = dlu.getAuthorsOf(occ);

                    logger.debug("Deleting occurrence id "+i);
                    occ.setDeleted(1);
                    database.executeUpdate(occ);
                    dlu.deleteHabitat(occ.getHabitat());
                    logger.debug("Occurrence id "+occ.getId()+" "+occ.getPlant().getTaxon()+" deleted.");
                    setPosition(getPosition()+1);
                    
                    for (AuthorOccurrence authorOcc : aos) {
                        authorOcc.setDeleted(2);
                        database.executeUpdateHistory(authorOcc);
                        logger.debug("AuthorOccurrence id "+authorOcc.getId()+" "+authorOcc.getAuthor().getWholeName()+" deleted.");
                    }
                    deleted++;
                    setPosition(getPosition()+1);
                    setStatusMessage(L10n.getFormattedString("Delete.Message.ProgressInfo",deleted,toBeDeleted.size()));
                }

                getTableModel().clearSelection();
                fireStopped(null);
                return null;
            }
        };//task
        
        return task;
    }
}


