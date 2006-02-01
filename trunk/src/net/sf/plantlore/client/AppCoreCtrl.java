/*
 * AppCoreCtrl.java
 *
 * Created on 14. leden 2006, 18:31
 *
 */

package net.sf.plantlore.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;
import net.sf.plantlore.client.dblayer.DBLayer;
import net.sf.plantlore.client.dblayer.DBLayerException;
import net.sf.plantlore.client.dblayer.FirebirdDBLayer;
import net.sf.plantlore.client.dblayer.PlantRecord;
import net.sf.plantlore.client.dblayer.QueryResult;
import net.sf.plantlore.client.dblayer.SelectQuery;
import org.apache.log4j.Logger;

/**
 *
 * @author Jakub
 */
public class AppCoreCtrl
{
    Logger logger;
    AppCore model;
    AppCoreView view;
    Settings settingsModel;
    SettingsView settingsView;
    SettingsCtrl settingsCtrl;
    Preferences prefs;
    
    /** Creates a new instance of AppCoreCtrl */
    public AppCoreCtrl(AppCore model, AppCoreView view)
    {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        this.model = model;
        this.view = view;
        prefs = Preferences.userNodeForPackage(AppCoreView.class);
        view.addSettingsListener(new SettingsListener());
        view.addPrintListener(new PrintListener());
        view.addExitListener(new ExitListener());
        view.addHelpContentsListener(new HelpContentsListener());
        view.addHelpAboutListener(new HelpAboutListener());
    }
    
    /** Handles click to menu item Settings.
     *
     */
    class SettingsListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent)
        {
            logger.info("Settings selected");
            //If the dialog is already constructed then use it. Otherwise construct it first.
            //if (settingsModel == null) {
                settingsModel = new Settings();
                settingsView = new SettingsView(settingsModel, view.getSBM());
                settingsCtrl = new SettingsCtrl(settingsModel, settingsView);
                settingsView.setVisible(true);
            /*} else {
                settingsView.setVisible(true);
            }*/
        }
    }
    
    class PrintListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent)
        {
            System.out.println("Print selected");
            DBLayer dbl = new FirebirdDBLayer("localhost","3050","/mnt/data/temp/plantloreHIB.fdb","sysdba","augmentin");
            try
            {
                dbl.initialize();
            } catch (DBLayerException ex)
            {
                System.out.println("Exception while initializing DBLayer: "+ex.getMessage());
                ex.printStackTrace();
            }
            SelectQuery sq = new SelectQuery();
            try
            {
                sq.setType("PLANT");
                QueryResult qr = dbl.executeQuery(sq);
                System.out.println("There are "+qr.getNumRows()+" plants in the db.");
                PlantRecord p = (PlantRecord) dbl.next(qr);
                do {
                    System.out.println("-----------------");
                System.out.println("Plant is "+p.getPublishableName());
                System.out.println("Czech name "+p.getCzechName());
                System.out.println("Abbrev. "+p.getAbbreviation());
                System.out.println("Adopted name "+p.getAdoptedName());
                System.out.println("Note "+p.getNote());
                p = (PlantRecord) dbl.next(qr);
                } while (p!=null);
            } catch (DBLayerException ex)
            {
                System.out.println("Msg: "+ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    
    /** Handles the exit command.
     *
     * Maybe settings should be stored first?
     */
    class ExitListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent)
        {
            System.exit(0);
        }
    }
    
    class HelpContentsListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent)
        {
            System.out.println("Help contents selected");
        }
    }
    
    class HelpAboutListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent)
        {
            System.out.println("Help about selected");
        }
    }
    
    class AppWindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e)
        {
            logger.info("Saving main window preferences.");
            
        }
    }
    
}
