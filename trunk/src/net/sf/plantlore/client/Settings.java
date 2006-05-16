/*
 * Settings.java
 *
 * Created on 17. leden 2006, 17:27
 *
 */

package net.sf.plantlore.client;

import java.util.ArrayList;
import java.util.Observable;
import java.util.prefs.Preferences;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/** Model for the Settings MVC
 *
 * Processes all Preferences set in Plantlore.
 * Loads them and stores them as well.
 *
 * @author Jakub
 */
public class Settings extends Observable
{
    public static final int DEFAULT_LANGUAGE = 0;
    public static final int CZECH = 1;
    public static final int ENGLISH = 2;
    private Preferences prefs;
    private String language;
    private Logger logger;
    
    private ArrayList<Column> selectedColumns;
    private ArrayList<Column> unselectedColumns;
    
    /** Creates a new instance of Settings */
    public Settings()
    {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        prefs = Preferences.userNodeForPackage(L10n.class);
        language = prefs.get("locale","xxx");
        if (language.equals("xxx"))
            language = L10n.DEFAULT_LANGUAGE;
    }
    
    /** Returns currently set language in this model.
     *
     * If setLanguage wasn't called yet returns the user preferred language.
     * Otherwise returns the language set by setLanguage().
     *
     */
    public int getLanguage() {
        if (language.equals(L10n.DEFAULT_LANGUAGE))
            return DEFAULT_LANGUAGE;
        if (language.equals(L10n.CZECH))
            return CZECH;
        if (language.equals(L10n.ENGLISH))
            return ENGLISH;
        logger.error("Language setting was neither of: unset, czech, english.");
        return DEFAULT_LANGUAGE;
    }
    
    /** Sets the language to this model. Doesn't store it yet.
     *
     * The argument should be one of L10n.DEFAULT_LANGUAGE, L10n.CZECH, L10n.ENGLISH
     *
     */
    public void setLanguage(String language) {
        this.language = language;
        logger.debug("Language set to "+language);
    }

    /** Stores all settings using the Preferences class.
     *
     */
    public void store()
    {
        /**********************************************
         * Store Language settings
         *********************************************/
        prefs = Preferences.userNodeForPackage(L10n.class);
        if (language.equals(L10n.DEFAULT_LANGUAGE))
            prefs.remove("locale");
        else
            prefs.put("locale",language);
        setChanged();
        notifyObservers("COLUMNS");        
    }//store()
    
    public void setSelectedColumns(ArrayList<Column> columns) {
        this.selectedColumns = columns;
        this.unselectedColumns = new ArrayList<Column>();
        for (Column.Type type : Column.Type.values()) {
            Column column = new Column(type);
            
            if (type.equals(Column.Type.OCCURRENCE_ID))
                continue;
            
            if (!selectedColumns.contains(column))
                unselectedColumns.add(column);
        }
    }
    
    public ArrayList<Column> getSelectedColumns() {
        return selectedColumns;
    }
    
    public ArrayList<Column> getUnselectedColumns() {
        return unselectedColumns;
    }
    
    
    
}
