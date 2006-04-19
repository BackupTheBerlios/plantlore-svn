/*
 * AppCore.java
 *
 * Created on 14. leden 2006, 17:56
 *
 */

package net.sf.plantlore.client;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Observable;
import java.util.prefs.Preferences;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.Habitat;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.common.record.Phytochorion;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.common.record.Territory;
import net.sf.plantlore.common.record.Village;

// Imports for temporary db access
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.server.DBLayerException;
import net.sf.plantlore.server.HibernateDBLayer;
import org.apache.log4j.Logger;

/** Application core model
 *
 * @author Jakub
 */
public class AppCore extends Observable
{
    private Preferences prefs;
    private int recordsPerPage = 30;
    private int currentPage = 1;
    private DBLayer database;  
    private OverviewTableModel tableModel;
    private Logger logger;
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

    private int selectedRow = 0;

    /** Creates a new instance of AppCore */
    public AppCore()
    {
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        prefs = Preferences.userNodeForPackage(this.getClass());
        
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
    }
    
    /** Returns table model for the main Overview.
     *
     * Should be called only after the user logs in to a database.
     *
     * @return null if the database connection wasn't created yet or an exception was thrown while working with it...
     * @return OverviewTableModel otherwise - creates a new one if it hasn't been created yet
     */
    public OverviewTableModel getTableModel() {
        if (database != null)
            if (tableModel == null) 
            {
            //FIXME:
                try {
                    tableModel = new OverviewTableModel(database, prefs.getInt("recordsPerPage", 30));
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                } catch (DBLayerException ex) {
                    ex.printStackTrace();
                }
                logger.debug("tableModel created");
                //FIXME: we should first return the model and *then* notifyObservers... :-/
                setChanged();
                notifyObservers();
                return tableModel;
            } else 
                return tableModel;
        else 
            return null;
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
            tableModel.setPageSize(recordsPerPage);
            setChanged();
            notifyObservers();        
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
            notifyObservers();        
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
            notifyObservers();
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
    
    public void savePreferences() {
        logger.info("Saving main window preferences.");
        prefs.putInt("recordsPerPage", recordsPerPage);
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
                sq.addProjection(PlantloreConstants.PROJ_DISTINCT,Publication.REFERENCECITATION);
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
}
