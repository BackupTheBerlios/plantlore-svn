/*
 * L10n.java
 *
 * Created on 14. leden 2006, 18:27
 *
 */

package net.sf.plantlore.l10n;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import org.apache.log4j.Logger;

/** Looks after the Plantlore localization.
 *
 * Gets proper text strings from the resources according to the default or set
 * locale. Tries to take the locale from the stored Preferences.
 *
 * @author Jakub
 */
public class L10n
{
    public static final String CZECH = "cs_CZ";
    public static final String ENGLISH = "en";
    public static final String DEFAULT_LANGUAGE = "Default system language.";
    private static final String RESOURCE_NAME="net.sf.plantlore.l10n.Plantlore";
    private static ResourceBundle resource;
    private static Preferences prefs;
    private static Logger logger = Logger.getLogger(L10n.class.getPackage().getName());
    private static Locale currentLocale;
    private static String languageLocale;
    
    /** Creates a new instance of L10n */
    public L10n()
    {
    }
    
    /** Loads Plantlore resources
     *
     * If user didn't set language preference the resources are loaded according to
     * default locale. Otherwise according to the user preference.
     *
     * @throws IOException if the resource is not found.
     */
    public static void load() throws IOException {
        prefs = Preferences.userNodeForPackage(L10n.class);
        languageLocale = prefs.get("locale","DUMMY_DEFAULT");
        load(languageLocale);
    }
    
    public static void load(String locale) throws IOException {        
        Locale loc;
        if (locale.equals("DUMMY_DEFAULT")) {
            loc = Locale.getDefault();
            logger.info("Using default locale "+loc.toString());
        } else { // parse the string of format language_country_variant
            String[] s = locale.split("_");
            switch (s.length) {
                case 1:
                    loc = new Locale(s[0]);
                    break;
                case 2:
                    loc = new Locale(s[0],s[1]);
                    break;
                case 3:
                    loc = new Locale(s[0],s[1],s[2]);
                    break;
                default: //this should never happen
                    loc = Locale.getDefault();
                    logger.error("Problem parsing stored language preference. Falling into default locale.");
            }
            if (s.length >= 1 && s.length <= 3)
                logger.info("Using user stored locale "+locale);
        }
        resource = ResourceBundle.getBundle(RESOURCE_NAME, loc);
        currentLocale = loc;
    }
    
    /** Gets string for the given key
     *
     * Removes the first ampersand sign (&) because it is assumed that it is an indiaction of a mnemonic.
     *
     * @param key Key of the required value
     * @throws NullPointerException in case that <code>load()</code> wasn't called first or it failed.
     * @throws MissingResourceException in case the <code>key</code> is not defined!
     */
    public static String getString(String key) {
    	try {
    		StringBuffer sb = new StringBuffer(resource.getString(key));
    		int i = sb.indexOf("&");
    		if (i>=0)
    			sb.deleteCharAt(i);
    		return sb.toString();
    	} catch( MissingResourceException e ) {
    		logger.warn("The key \"" + key + "\" is not defined in the property file!");
    		return key; // nothing else we can do...
    	}
    }
    
    /** Gets string for the given key and formats it using given array of arguments
     *
     *
     * @param key Key of the required value
     * @param arg The array of arguments to be used to format the string.
     * @throws NullPointerException in case that <code>load()</code> wasn't called first or it failed.
     */
    public static String getFormattedString(String key, Object... arg) {
        String value = "";
    	try {
                value = resource.getString(key);
                String formattedValue;
                MessageFormat mf = new MessageFormat(value);
                formattedValue = mf.format(arg);
    		return formattedValue;
    	} catch( MissingResourceException e ) {
    		logger.warn("The key \"" + key + "\" is not defined in the property file!");
    		return key; // nothing else we can do...
    	} catch (IllegalArgumentException e ) {
                logger.warn("Property "+key+" has probably wrong format.");
                return value; // nothing else we can do...
        }
    }

    /** Returns mnemonic for the given key.
     *
     * If the mnemonic wasn't set in the string for the key then returns -1.
     *
     * @param key Key of the required value
     * @trhows NullPointerException in case that <code>load()</code> wasn't called first or it failed.
     */
    public static int getMnemonic(String key) {
        try {
            StringBuffer sb = new StringBuffer(resource.getString(key));
            int i = sb.indexOf("&");
            if (i < 0)
                return -1;
            if (i+1 == sb.length())
                return -1;
            Character c = sb.charAt(i+1);
            if (i>=0)
                sb.deleteCharAt(i);

            return Character.toUpperCase(c);
        } catch (MissingResourceException e) {
            logger.warn("The key \"" + key + "\" is not defined in the property file! Couldn't return mnemonic.");
            return 0;
        }
    }
    
    public static Locale getCurrentLocale() {
        return (Locale) currentLocale.clone();
    }
    
    public static String getLanguageLocale() {
    	return languageLocale;
    }
}
