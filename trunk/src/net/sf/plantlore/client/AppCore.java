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
import java.util.Hashtable;
import java.util.Observable;
import java.util.prefs.Preferences;
import javax.swing.table.TableModel;
import net.sf.plantlore.client.login.DBInfo;
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
import net.sf.plantlore.common.record.Right;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.Village;

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
    
    protected void setDatabase(DBLayer dblayer) {
    	this.database = dblayer;
        loadDialogData();
    }
    
    public void loadDialogData() {
        logger.debug("Loading dialog data ...");
        plants = null; getPlants();
        authors = null; getAuthors();
        authorRoles = null; getAuthorRoles();
        villages = null; getVillages();
        phytNames = null; getPhytNames(); //loads phytCodes too
        territories = null; getTerritories();
        countries = null; getCountries();
        sources = null; getSources();
        publications = null; getPublications();
        projects = null; getProjects();
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
        
    public void selectAll() {
        if (tableModel != null)
            tableModel.selectAll();
        //setChanged();
        //notifyObservers();
    }

    public void selectNone() {
        if (tableModel != null)
            tableModel.selectNone();
        //setChanged();
        //notifyObservers();
    }
    public void invertSelected() {
        if (tableModel != null)
            tableModel.invertSelected();
        //setChanged();
        //notifyObservers();
    }

    public int getRecordsPerPage() {
        if (tableModel != null)
            return tableModel.getPageSize();
        else 
            return 0;
    }

    public void setRecordsPerPage(int recordsPerPage) {        
        if (tableModel != null)
        {
            logger.info("Setting records per page to "+recordsPerPage);
            tableModel.setPageSize(recordsPerPage);
            setChanged();
            notifyObservers("RECORDS_PER_PAGE");        
        }
    }

    public void nextPage() {
        if (tableModel != null)
        {
            //FIXME:
            try {
                tableModel.nextPage();
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
        if (tableModel != null)
        {
            //FIXME:
            try {
                tableModel.prevPage();
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
        if (tableModel != null)
            return tableModel.getCurrentPage();
        else
            return 0;
    }

    public void setCurrentPage(int currentPage) {
        if (tableModel != null)
        {
            tableModel.setCurrentPage(currentPage);
            setChanged();
            notifyObservers();
        }
    }
    
    public int getResultsCount() {
        if (tableModel != null)        
            return tableModel.getResultsCount();
        else
            return 0;
    }
    
    public int getPagesCount() {
        if (tableModel != null)
            return tableModel.getPagesCount();
        else
            return 0;
    }
    
    public void invertSelectedOnCurrentRow() {
        tableModel.invertSelected(selectedRow);
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
        return tableModel.getRow(selectedRow);
    }
    
    public Integer getSelectedOccurrence() {
        //Object[] row = tableModel.getRow(selectedRow);
        //return (Integer)row[row.length-1];
        return tableModel.getOccurrenceId(selectedRow);
    }
    
    public void savePreferences() throws IOException {
        logger.info("Saving main window preferences.");
        prefs.putInt("recordsPerPage", recordsPerPage);
        mainConfig.save();
    }
    
    public void setResultId(int resultId) {
        tableModel.setResultId(resultId);
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
    
    public Pair<String, Integer>[] getPlants() {
        if (plants == null)
        {
            SelectQuery sq;
            int resultid;
            int resultsCount;
            Object[] records;
            Object[] row;
            
            //FIXME:
            try {
                sq = database.createQuery(Plant.class);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, Plant.TAXON);
                sq.addProjection(PlantloreConstants.PROJ_PROPERTY, Plant.TAXON);
                sq.addProjection(PlantloreConstants.PROJ_PROPERTY, Plant.ID);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                System.out.println("getPlants(): we got "+resultsCount+" results.");
                records = database.more(resultid, 0, resultsCount-1);
                plants = new Pair[resultsCount];
                for (int i = 0; i < resultsCount; i++)
                {
                    row = (Object[])records[i];
                    plants[i] = new Pair((String)row[0], (Integer)row[1]);
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
            Object[] row;

            //FIXME:
            try {
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
            Object[] row;
            
            //FIXME:
            try {
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
            Object[] row;
            
            //FIXME:
            try {
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
            Object[] records, row;
            
            //FIXME:
            try {
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
            getPhytNames();
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
            Object[] records;
            String country;
            
            //FIXME:
            try {
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
            
            //FIXME:
            try {
                sq = database.createQuery(Occurrence.class);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, Occurrence.DATASOURCE);
                sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Occurrence.DATASOURCE);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 0, resultsCount-1);
                sources = new String[resultsCount];
                for (int i = 0; i < resultsCount; i++)
                {
                    sources[i] = (String)((Object[])records[i])[0];
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
            Object[] row;
            
            //FIXME:
            try {
                sq = database.createQuery(Publication.class);
                sq.addOrder(PlantloreConstants.DIRECT_ASC, Publication.REFERENCECITATION);
                sq.addProjection(PlantloreConstants.PROJ_DISTINCT,Publication.REFERENCECITATION);
                sq.addProjection(PlantloreConstants.PROJ_PROPERTY,Publication.ID);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 0, resultsCount-1);
                publications = new Pair[resultsCount];
                for (int i = 0; i < resultsCount; i++)
                {
                    row = (Object[])records[i];
                    publications[i] = new Pair(row[0], row[1]);
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
            Object[] records,row;
            
            //FIXME:
            try {
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
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (DBLayerException ex) {
                ex.printStackTrace();
            }            
            return projects;
        } else
            return projects;
    }
    
    public MainConfig getMainConfig() {
        return mainConfig;
    }
    
    public void login() {
        assert database != null;
        
        tableModel.setDatabase(database);
        Search search = new Search(getDatabase());
        search.setColumns(columns);
        search.constructQuery();
        setResultId(search.getNewResultId());
        //setExportQuery(search.getExportQuery(), false, null);
    }
    
}
