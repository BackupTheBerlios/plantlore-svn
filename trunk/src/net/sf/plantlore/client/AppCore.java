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
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.Plant;
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
    private Hashtable<String, Integer> plants = null;
    private Object[] authors = null;
    private Object[] villages = null;
    private Object[] territories = null;

    private int selectedRow;

    /** Creates a new instance of AppCore */
    public AppCore()
    {
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        prefs = Preferences.userNodeForPackage(this.getClass());
        
//        database = new FirebirdDBLayer("localhost", "3050", "/mnt/data/temp/plantloreHIB.fdb", "sysdba", "masterkey");
        database = new HibernateDBLayer();
      
        try {
            database.initialize(null, null, null); // FIXME sem prijdou samosebou rozumne hodnoty
        } catch (DBLayerException e) {
            System.out.println("Error initializing database: "+e.toString());
        } catch(RemoteException e) {
        	System.err.println("Kdykoliv se pracuje s DBLayer nebo SelectQuery, musite hendlovat RemoteException");
        }
        
        
        
        //FIXME:
        try {
            
            tableModel = new OverviewTableModel(database, prefs.getInt("recordsPerPage", 30));
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (DBLayerException ex) {
            ex.printStackTrace();
        }
        logger.debug("tableModel created");
        setChanged();
        notifyObservers();
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
    
    public OverviewTableModel getTableModel() {
        return this.tableModel;
    }
        
    public void selectAll() {
        tableModel.selectAll();
        //setChanged();
        //notifyObservers();
    }

    public void selectNone() {
        tableModel.selectNone();
        //setChanged();
        //notifyObservers();
    }
    public void invertSelected() {
        tableModel.invertSelected();
        //setChanged();
        //notifyObservers();
    }

    public int getRecordsPerPage() {
        return tableModel.getPageSize();
    }

    public void setRecordsPerPage(int recordsPerPage) {
        tableModel.setPageSize(recordsPerPage);
        setChanged();
        notifyObservers();        
    }

    public void nextPage() {
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
    
    public void prevPage() {
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

    public int getCurrentPage() {
        return tableModel.getCurrentPage();
    }

    public void setCurrentPage(int currentPage) {
        tableModel.setCurrentPage(currentPage);
        setChanged();
        notifyObservers();
    }
    
    public int getResultsCount() {
        return tableModel.getResultsCount();
    }
    
    public int getPagesCount() {
        return tableModel.getPagesCount();
    }
    
    public void setSelectedRow(int i) 
    {
        selectedRow = i;
        logger.debug("Selected row #"+i);
    }
    
    public void savePreferences() {
        logger.info("Saving main window preferences.");
        prefs.putInt("recordsPerPage", recordsPerPage);
    }
    
    public Hashtable getPlants() {
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
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 1, resultsCount);
                plants = new Hashtable<String, Integer>(resultsCount+1, 1);
                for (int i = 1; i <= resultsCount; i++)
                {
                    p = (Plant)((Object[])records[i-1])[0];
                    plants.put(p.getTaxon(), p.getId());
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
    public Object[] getAuthors() {
        if (authors == null)
        {
            SelectQuery sq;
            int resultid;
            int resultsCount;
            Object[] records;
            //FIXME:
            try {
                sq = database.createQuery(Author.class);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 1, resultsCount);
                authors = new Object[resultsCount];
                for (int i = 1; i <= resultsCount; i++)
                {
                    authors[i-1] = ((Author)((Object[])records[i-1])[0]).getWholeName();
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
    
    public Object[] getVillages() {
        if (villages == null)
        {
            SelectQuery sq;
            int resultid;
            int resultsCount;
            Object[] records;
            //FIXME:
            try {
                sq = database.createQuery(Village.class);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 1, resultsCount);
                villages = new Object[resultsCount];
                for (int i = 1; i <= resultsCount; i++)
                {
                    villages[i-1] = ((Village)((Object[])records[i-1])[0]).getName();
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

    public Object[] getTerritories() {
        if (territories == null)
        {
            SelectQuery sq;
            int resultid;
            int resultsCount;
            Object[] records;
            //FIXME:
            try {
                sq = database.createQuery(Territory.class);
                resultid = database.executeQuery(sq);
                resultsCount = database.getNumRows(resultid);
                records = database.more(resultid, 1, resultsCount);
                territories = new Object[resultsCount];
                for (int i = 1; i <= resultsCount; i++)
                {
                    territories[i-1] = ((Territory)((Object[])records[i-1])[0]).getName();
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
}
