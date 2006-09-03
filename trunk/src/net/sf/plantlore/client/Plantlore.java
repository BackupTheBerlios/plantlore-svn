/*
 * Plantlore.java
 *
 * Created on 14. leden 2006, 17:46
 *
 */

package net.sf.plantlore.client;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import net.sf.plantlore.client.AppCore;
import net.sf.plantlore.client.AppCoreCtrl;
import net.sf.plantlore.client.AppCoreView;
import net.sf.plantlore.common.Dispatcher;
import net.sf.plantlore.common.GlobalExceptionHandler;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.common.debug.ConnectionMonitor;
import net.sf.plantlore.common.debug.MemoryMonitor;
import net.sf.plantlore.common.exception.PlantloreException;

import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.DocumentException;

/** The main class of Plantlore. This is where all begins.
 *
 * @author Jakub
 */
public class Plantlore {
    AppCore model;
    AppCoreView view;
    AppCoreCtrl ctrl;
    MainConfig mainConfig = null;
    Logger logger;
    private static SplashScreen splashScreen;   
    private static final String LOGGER_PROPS = "net/sf/plantlore/config/log4j.properties";
    public static final String PLANTLORE="plantlore";
    public static final String VERSION="1.0";
    private static final String MAIN_CONFIG_NAME=PLANTLORE+".xml";
    
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
        try { 
            UIManager.setLookAndFeel(new PlasticXPLookAndFeel()); 
        } catch (Exception e) { e.printStackTrace(); JFrame.setDefaultLookAndFeelDecorated(true); }
                
        //BasicConfigurator.configure();
        Plantlore plantlore = new Plantlore();
        try {
            L10n.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Initialize Help
        try {
            PlantloreHelp.initialize();
        } catch (PlantloreException e) {
            // TODO: Display error message
        }
        
        plantlore.run();
               
        new MemoryMonitor();        
      
    }
    
    
    private void loadConfiguration() throws IOException, DocumentException {
        String userHome = System.getProperty("user.home");
        String osName = System.getProperty("os.name");
        String plantloreDirName;
        if (osName.equals("Linux")) {
            plantloreDirName = "."+PLANTLORE;
        } else {
            plantloreDirName = PLANTLORE;
        }
        String plantloreConfDir = userHome+File.separator+plantloreDirName;
        File plantloreConfDirFile = new File(plantloreConfDir);
        if (!plantloreConfDirFile.exists()) {
            logger.info("Creating user configuration directory "+plantloreConfDir);
            plantloreConfDirFile.mkdir();
        }
        
        String mainConfig = plantloreConfDir + File.separator + MAIN_CONFIG_NAME;
        File mainConfigFile = new File(mainConfig);
        if (!mainConfigFile.exists()) {
            logger.info("Creating main configuration file "+mainConfig);
            MainConfig.createEmptyConfig(mainConfig);
        }
        
        this.mainConfig = new MainConfig(mainConfig);
        this.mainConfig.load();
    }
    
    /** Constructs the main MVC
     *
     */
    private void run() {
        try {
            try {
                loadConfiguration();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,"Couldn't load configuration file: "+e.getMessage());
            } catch (DocumentException e) {               
                JOptionPane.showMessageDialog(null,"Problem while loading configuration file: "+e.getMessage());
            }
            
            logger.info("Constructing AppCore MVC");
            splashScreen = new SplashScreen("resources/splashscreen.gif");
            splashScreen.splash();
            model = new AppCore(mainConfig);
            view = new AppCoreView(model);
            ctrl = new AppCoreCtrl(model, view);
            Dispatcher.initialize(view.progressBar);
            //view.init();
            view.setVisible(true);
            EventQueue.invokeLater( new SplashScreenCloser() );
            logger.info("AppCore MVC constructed. Plantlore client should be visible now.");
            
            logger.debug("Installing global exception handler.");
            GlobalExceptionHandler.install( ctrl.silentFinalAction );
            
        } catch(RuntimeException e) {
            //new ExceptionDialog(view,"Some exception was thrown: "+e);
            e.printStackTrace();
        }
        new ConnectionMonitor(model);        
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
