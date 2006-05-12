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
    private int recordsPerPage = 30;
    private int currentPage = 1;
    private DBLayer database;  
    private Right accessRights;
    private OverviewTableModel tableModel;
    private Logger logger;

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
    
    public void setResultId(int resultId) {
        tableModel.setResultId(resultId);
    }
    
}
