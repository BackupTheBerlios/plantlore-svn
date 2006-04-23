/*
 * Plantlore.java
 *
 * Created on 14. leden 2006, 17:46
 *
 */

package net.sf.plantlore.client;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.UIManager;

import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/** The main class of Plantlore. This is where all begins.
 *
 * @author Jakub
 */
public class Plantlore {
    AppCore model;
    AppCoreView view;
    AppCoreCtrl ctrl;
    Logger logger;
    private static SplashScreen splashScreen;   
    private static final String LOGGER_PROPS = "net/sf/plantlore/config/log4j.properties";
    
    /**
     * Creates a new instance of Plantlore
     */
    public Plantlore() {
        //Load log4j settings
        ClassLoader cl = this.getClass().getClassLoader();
        InputStream is = cl.getResourceAsStream(LOGGER_PROPS);
        Properties props = new Properties();
        //FIXME:
        try {
            props.load(is);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //maybe CHANGE to configureAndWatch()
        PropertyConfigurator.configure(props);

        logger = Logger.getLogger(this.getClass().getPackage().getName());
        logger.info("Plantlore client is starting up...");
    }
    
    /** The main() method for Plantlore client
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Set beautiful system look & feel.
        //try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { JFrame.setDefaultLookAndFeelDecorated(true); }
        
        
        //BasicConfigurator.configure();
        Plantlore plantlore = new Plantlore();
        try {
            L10n.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        plantlore.run();
    }
    
    /** Constructs the main MVC
     *
     */
    private void run() {
        try {
        logger.info("Constructing AppCore MVC");
        splashScreen = new SplashScreen("resources/splashscreen.gif");
        splashScreen.splash();
        model = new AppCore();
        view = new AppCoreView(model);
        ctrl = new AppCoreCtrl(model, view);
        view.init();
        view.setVisible(true);
        EventQueue.invokeLater( new SplashScreenCloser() );
        logger.info("AppCore MVC constructed. Plantlore client should be visible now.");
        } catch(RuntimeException e) {
            //new ExceptionDialog(view,"Some exception was thrown: "+e);
            e.printStackTrace();
        }
    }
    
    /**
     * Removes the splash screen.
     *
     * Invoke this <code>Runnable</code> using
     * <code>EventQueue.invokeLater</code>, in order to remove the splash screen
     * in a thread-safe manner.
     */
    private static final class SplashScreenCloser implements Runnable {
        public void run(){
            splashScreen.dispose();
        }
    }
}
