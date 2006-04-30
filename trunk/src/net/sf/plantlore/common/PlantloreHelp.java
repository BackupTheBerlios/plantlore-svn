/*
 * PlantloreHelp.java
 *
 * Created on 15. leden 2006, 3:31
 *
 */

package net.sf.plantlore.common;

import java.net.URL;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.JHelp;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Class for invoking Help viewer.
 *
 * @author Tomas Kovarik
 * @version 0.1, Jan 15, 2006
 */
public class PlantloreHelp {
    
    // File containing Java Help settings
    private static final String HELPFILE = "jhelpset.hs";
    
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
    public static final String METDATA_MANAGER          = "metdata";    
    // Add/edit metadata dialog
    public static final String METDATA_ADD              = "metdata.add";        
    // Description of history manager
    public static final String HISTORY_MANAGER          = "history";
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
            
    /** Creates a new instance of PlantloreHelp */
    public PlantloreHelp() {
        
    }
  
    /**
     * Method for opening Help Viewer at a specified page.
     *
     * @param section Section of help which will be shown. Sections are defined in 
     *                <code>jHelpMap.jhm</code> and <code>jHelpToc.xml</code>
     */
    public static void showHelp(String section) {
        JHelp helpViewer;
        try {
            // Get the classloader of this class.
            ClassLoader cl = PlantloreHelp.class.getClassLoader();
            URL url = HelpSet.findHelpSet(cl, HELPFILE);
            // Create a new JHelp object with a new HelpSet.        
            HelpSet hs = new HelpSet(cl, url);
            helpViewer = new JHelp(hs);
            // Set the initial entry point in the table of contents.
            helpViewer.setCurrentID(section);
        } catch (HelpSetException e) {
            System.out.println("EXCEPTION: "+e.getMessage());
            e.printStackTrace();
            return;
        }
        // Create a new frame.
        JFrame frame = new JFrame();
        frame.setSize(500,500);
        frame.getContentPane().add(helpViewer);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Make the frame visible.
        frame.setVisible(true);        
    }    
}
