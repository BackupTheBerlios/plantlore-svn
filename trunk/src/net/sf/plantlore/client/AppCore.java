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

/** Application core model
 *
 * @author Jakub
 */
public class AppCore extends Observable
{
    private Preferences prefs;
    private int recordsPerPage;
    private DBLayer database;  
    private OverviewTableModel tableModel;

    /** Creates a new instance of AppCore */
    public AppCore()
    {
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
        
        tableModel = new OverviewTableModel(database);
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
    
    /** 
     * TODO: make methods of OverviewTableModel call setChanged() of this observable.
     * and then remove from appCore methods duplicating those from OverviewTableModel
     */
    public void setModelChanged() {
        setChanged();
    }
    
    public void selectAll() {
        tableModel.selectAll();
        setChanged();
        notifyObservers();
    }

    public void selectNone() {
        tableModel.selectNone();
        setChanged();
        notifyObservers();
    }
    public void invertSelected() {
        tableModel.invertSelected();
        setChanged();
        notifyObservers();
    }
}
