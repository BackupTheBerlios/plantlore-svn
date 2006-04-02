/*
 * AppCore.java
 *
 * Created on 14. leden 2006, 17:56
 *
 */

package net.sf.plantlore.client;

import java.rmi.RemoteException;
import java.util.Observable;
import java.util.prefs.Preferences;

// Imports for temporary db access
import net.sf.plantlore.client.dblayer.FirebirdDBLayer;
import net.sf.plantlore.middleware.DBLayer;
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
    
    public void savePreferences() {
        logger.info("Saving main window preferences.");
        prefs.putInt("recordsPerPage", recordsPerPage);
    }
}
