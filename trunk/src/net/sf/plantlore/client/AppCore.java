/*
 * AppCore.java
 *
 * Created on 14. leden 2006, 17:56
 *
 */

package net.sf.plantlore.client;

import java.util.Observable;
import java.util.prefs.Preferences;

// Imports for temporary db access
import net.sf.plantlore.client.dblayer.DBLayer;
import net.sf.plantlore.client.dblayer.FirebirdDBLayer;
import net.sf.plantlore.client.dblayer.DBLayerException;

/** Application core model
 *
 * @author Jakub
 */
public class AppCore extends Observable
{
    private Preferences prefs;
    private int recordsPerPage;
    private DBLayer database;    

    /** Creates a new instance of AppCore */
    public AppCore()
    {
        prefs = Preferences.userNodeForPackage(this.getClass());
        
        database = new FirebirdDBLayer("localhost", "3050", "c:/Kovo/DatabaseTest/database/plantlore.fdb", "SYSDBA", "masterkey");
        try {
            database.initialize();
        } catch (DBLayerException e) {
            System.out.println("Error initializing database: "+e.toString());
        }                                                                                           
        
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
}
