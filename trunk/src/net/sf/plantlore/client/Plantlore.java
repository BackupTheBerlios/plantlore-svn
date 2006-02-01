/*
 * Plantlore.java
 *
 * Created on 14. leden 2006, 17:46
 *
 */

package net.sf.plantlore.client;

import java.io.IOException;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 *
 * @author Jakub
 */
public class Plantlore {
    AppCore model;
    AppCoreView view;
    AppCoreCtrl ctrl;
    Logger logger;
    
    /**
     * Creates a new instance of Plantlore
     */
    public Plantlore() {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
    }
    
    /** The main() method for Plantlore client
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().info("Plantlore client started");
        Plantlore plantlore = new Plantlore();
        try
        {
            L10n.load();
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
        plantlore.run();
    }

    /** Constructs the main MVC
     *
     */
    private void run()
    {
        logger.info("Constructing AppCore MVC");
        model = new AppCore();
        view = new AppCoreView(model);
        ctrl = new AppCoreCtrl(model, view);
        view.init();
        view.setVisible(true);
        logger.info("AppCore MVC constructed. Plantlore client should be visible now.");
    }
    
}
