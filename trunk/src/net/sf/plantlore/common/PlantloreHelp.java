
package net.sf.plantlore.common;

import java.awt.Component;
import java.awt.Dimension;
import java.net.URL;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.JHelp;
import javax.swing.JDialog;
import javax.swing.JFrame;
import net.sf.plantlore.common.exception.PlantloreException;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 *  Class for invoking Help viewer. This class should never be instatiated and initialize() method 
 *  should be called to initialize help when application is started. This class also contains 
 *  constants with help mapping, therefore if you need to modify help structure, modify these constants.
 *
 *  For details about JavaHelp system, refer to the following pages:
 *  http://java.sun.com/products/javahelp/ - JavaHelp home a java.sun.com
 *  http://docs.sun.com/source/819-0913/dev/csh.html - implementation of context-sensitive help
 *
 *  @author Tomas Kovarik
 *  @version 1.0, June 4, 2006
 */
public class PlantloreHelp {    
    // File containing Java Help settings
    private static final String HELPFILE = "net/sf/plantlore/help/jhelpset.hs";    
    // Basic information about Plantlore
    public static final String PLANTLORE_OVERVIEW       = "plantlore.overview";
    // Resources available for Plantlore users
    public static final String PLANTLORE_RESOURCES      = "plantlore.resources";
    // Text of licence used
    public static final String PLANTLORE_LICENCE        = "plantlore.licence";
    // Login description
    public static final String LOGIN                    = "login";
    // Description of selecting the database
    public static final String SELECT_DB                = "login.selectdb";    
    // Authentication description
    public static final String AUTHENTICATION           = "login.authentication";    
    // Description of settings dialog
    public static final String SETTINGS                 = "settings";
    // Main Plantlore window with occurrence overview    
    public static final String PLANT_OVERVIEW           = "overview";    
    // Adding and editing occurrense records
    public static final String ADD_OCCURRENCE           = "overview.addoccurrence";
    // Description of "basic data" section
    public static final String ADD_BASIC                = "overview.addoccurrence.basic";    
    // Description of "extended data" section
    public static final String ADD_EXTENDED             = "overview.addoccurrence.extended";
    // Description of checklist
    public static final String ADD_CHECKLIST            = "overview.addoccurrence.checklist";        
    // Searching the occurrence records
    public static final String SEARCH_OCCURRENCE        = "overview.search";
    // History for the selected occurrence record
    public static final String OCCURRENCE_HISTORY       = "overview.history";    
    // Description of author manager
    public static final String AUTHOR_MANAGER           = "author";
    // Add /edit author dialog
    public static final String ADD_AUTHOR               = "author.add";        
    // Description of publication manager
    public static final String PUBLICATION_MANAGER      = "publication";    
    // Add/edit publication dialog
    public static final String PUBLICATION_ADD          = "publication.add";        
    // Description of metadata manager
    public static final String METDATA_MANAGER          = "metadata";    
    // Add/edit metadata dialog
    public static final String METDATA_ADD              = "metadata.add";        
    // Description of history manager
    public static final String HISTORY                   = "history";    
    // Description of whole history manager
    public static final String HISTORY_DATA              = "history.data";    
    // Desctripton of user manager
    public static final String USER_MANAGER             = "user";    
    // Add/edit user dialog
    public static final String USER_ADD                 = "user.add";    
    // Modification of rights description
    public static final String USER_RIGHTS              = "user.rights";    
    // Import feature description
    public static final String IMPORT                   = "import";
    // Export feature description
    public static final String EXPORT                   = "export"; 
    // Desctripton of server
    public static final String SERVER                   = "server"; 
    //  Desctripton of create server, connect to server and running Server
    public static final String SERVER_CONNECT           = "server.connect"; 
    //  Desctripton of server manager
    public static final String SERVER_MANAGER           = "server.manager"; 
    //  Desctripton of server info
    public static final String SERVER_INFO              = "server.info"; 
    // Description for creating new database
    public static final String CREATE_DB                = "createdb";    

    /* Instance of a logger */
    private static Logger logger = Logger.getLogger(PlantloreHelp.class.getPackage().getName());
    /* Instance of HelpSet and HelpBroker. These variables are initiliazed by initialize() method */
    private static HelpBroker hb;
    private static HelpSet hs;

    
    /** Creates a new instance of PlantloreHelp */
    public PlantloreHelp() {
    }
    
    /**
     *  Initialize Help (using JavaHelp 2.0). This method loads help configuration (jhelpset.hs)
     *  and creates HelpBroker.
     *
     *  @throws PlantloreException in case HelpSet could not be found and/or loaded properly
     */
    public static void initialize() throws PlantloreException {
        try {
            // Get the classloader of this class.
            ClassLoader cl = PlantloreHelp.class.getClassLoader();
            URL url = HelpSet.findHelpSet(cl, HELPFILE, L10n.getCurrentLocale());
            // Create a new JHelp object with a new HelpSet.        
            hs = new HelpSet(cl, url);
            hs.setTitle("Plantlore Help");            
            // Create the HelpBroker            
            hb = hs.createHelpBroker();
            hb.setSize(new Dimension(800,600));
        } catch (HelpSetException e) {
            logger.error("Unable to initialize help: "+e.getMessage());
            throw new PlantloreException("Unable to initialize help: "+e.getMessage());
        }        
    }
    
    /**
     *  Associate help with the given button (any descendant of javax.swing.AbstractButton or 
     *  java.awt.Button). Assigns given help section to the button and creates ActionListener
     *  for the button. In case the second argument is not a descendant of the mentioned classes,
     *  IllegalArgumentException is thrown.
     *
     *  @param section  section of help we want to display. Use constants defined in this class
     *  @param button   instance of javax.swing.AbstractButton or java.awt.Button or any of their
     *                  descendants that we want to associate with the given help section.
     */
    public static void addButtonHelp(String section, Component button) {
        hb.enableHelpOnButton(button, section, hs);        
    }
    
    /**
     *  Generic method for enabling help for any descendant of Component class.
     *
     *  @param section  section of help we want to display. Use constants defined in this class.
     *  @param comp     component for which we want to enable help. This can be any descendant of
     *                  Component class
     */
    public static void addComponentHelp(String section, Component comp) {
        hb.enableHelp(comp, section, hs);
    }
    
    /**
     *  Add context-sensitive help to the given component. The component will display appropriate
     *  section of help when Help Key is pressed (on Windows platform this should be F1 key). The 
     *  component must have focus for this feature to work. If you want to display help for the whole
     *  dialog window (no matter which of the components has focus, use dialog's RootPane as an
     *  argument for this method)
     *
     *  @param section  section of help we want to display. Use constants defined in this class.
     *  @param comp     component for which we want to register context-sensitive help
     */
    public static void addKeyHelp(String section, Component comp) {
        hb.enableHelpKey(comp, section, hs);
    }   
}