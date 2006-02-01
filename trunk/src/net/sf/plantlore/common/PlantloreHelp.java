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
    
    /** Creates a new instance of PlantloreHelp */
    public PlantloreHelp() {
        
    }
  
    /**
     * Method for opening Help Viewer at a specified page.
     *
     * @param section Section of help which will be shown. Sections are defined in 
     *                <code>jHelpMap.jhm</code> and <code>jHelpToc.xml</code>
     * @throws ???
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
            // TODO: Throw new exception, we should define new exception
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
