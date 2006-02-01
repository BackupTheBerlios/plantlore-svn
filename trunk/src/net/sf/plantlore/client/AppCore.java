/*
 * AppCore.java
 *
 * Created on 14. leden 2006, 17:56
 *
 */

package net.sf.plantlore.client;

import java.util.Observable;
import java.util.prefs.Preferences;

/** Application core model
 *
 * @author Jakub
 */
public class AppCore extends Observable
{
    private Preferences prefs;
    private int recordsPerPage;
    
    /** Creates a new instance of AppCore */
    public AppCore()
    {
        prefs = Preferences.userNodeForPackage(this.getClass());
    }
    
}
