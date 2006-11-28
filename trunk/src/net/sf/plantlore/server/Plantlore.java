package net.sf.plantlore.server;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import net.sf.plantlore.common.DefaultExceptionHandler;
import net.sf.plantlore.common.PlainProgressMonitor;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.common.SuperDispatcher;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.PlantloreException;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.server.manager.*;
import net.sf.plantlore.server.tools.RMI;


/**
 * The entry point when starting the Plantlore Server. 
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-03-30
 * @version 1.0
 */
public class Plantlore {
	
	private static final String LOGGER_PROPS = "net/sf/plantlore/config/log4j.properties";
	
	
	public static boolean hasOption(String option, String[] args) {
		for(String arg : args)
			if(arg.equalsIgnoreCase(option) || arg.equalsIgnoreCase("-"+option) || arg.equalsIgnoreCase("--"+option))
				return true;
		return false;
	}
	
	
	public static String parameterValue(String parameterName, String args[]) {
		for(int i = 0; i < args.length; i++) {
			String arg = args[i];
			if(arg.equalsIgnoreCase(parameterName) || arg.equalsIgnoreCase("-"+parameterName) || arg.equalsIgnoreCase("--"+parameterName)) {
				if( i + 1 < args.length )
					return args[i + 1]; 
			}
		}
		return null;
	}

	
	public static String replaceIfValid(String replacement, String defaultValue) {
		return (replacement != null && replacement.length() > 0) ? replacement : defaultValue;
	}
	
	
	public static int replaceIfValid(String replacement, int defaultValue) {
		if(replacement != null) {
			try {
				int value = Integer.parseInt(replacement);
				return value;
			} catch(NumberFormatException e) { /* Never mind, return the default value. */}
		}
		return defaultValue;
	}
	

	public static void main(String[] args) {
		/*=====================================================================
		 * 
		 * Process all parameters passed to the program.
		 * 
		 *=====================================================================*/
        boolean
        	stopServer = Plantlore.hasOption("terminate", args),
        	whoIsThere = Plantlore.hasOption("who", args),
        	noGUI = Plantlore.hasOption("nogui", args) || stopServer || whoIsThere;
        
        ServerSettings settings;
		try {
			settings = ServerMng.loadSettingsFromFile(ServerMng.settingsFileName);
		} catch( Exception e ) {
			settings = ServerMng.generateDefaultServerSettings();
		}
        int 
        	port = replaceIfValid(parameterValue("port", args), settings.getPort()), 
        	timeout = replaceIfValid(parameterValue("timeout", args), settings.getTimeout()), 
        	connectionsTotal = replaceIfValid(parameterValue("connectionstotal", args), settings.getConnectionsTotal()),
        	connectionsPerIP = replaceIfValid(parameterValue("connectionsperip", args), settings.getConnectionsPerIP()),
        	databasePort = replaceIfValid(parameterValue("dbport", args), settings.getDatabaseSettings().getPort());
        String
        	databaseEngine = replaceIfValid(parameterValue("dbengine", args), settings.getDatabaseSettings().getDatabase()),
        	databaseParameter =  replaceIfValid(parameterValue("dbparameter", args), settings.getDatabaseSettings().getConnectionStringSuffix()),
        	password = replaceIfValid(parameterValue("password", args), ""),
        	host = replaceIfValid(parameterValue("host", args), "localhost");
        
        // Prepare new Server Settings.
        settings = new ServerSettings(port, timeout, connectionsTotal, connectionsPerIP,
        		new DatabaseSettings(databaseEngine, databasePort, databaseParameter));
        

        /*=====================================================================
         * 
         * Perform the initialization ritual.
         * 
         *=====================================================================*/
        
		// Load log4j settings
		try {
			ClassLoader cl = Plantlore.class.getClassLoader();
			InputStream is = cl.getResourceAsStream(LOGGER_PROPS);
			Properties props = new Properties();
			props.load(is);
			PropertyConfigurator.configure(props);
			L10n.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// Get a logger so as to be able to report problems.
		Logger logger = Logger.getLogger(Plantlore.class.getPackage().getName());
		

        /*=====================================================================
         * 
         * Take the requested action!
         * 
         *=====================================================================*/
        
		final ServerMng serverManager = new ServerMng(settings);
		
		// TERMINATE A RUNNING SERVER
        if( stopServer ) {
        	Task 
        		connectToServerTask = serverManager.createConnectToRunningServerTask(host, port, password),
        		terminateTheServerTask = serverManager.createTerminateServerTask();
        	new PlainProgressMonitor(logger).registerTask(connectToServerTask, terminateTheServerTask);
        	
        	SuperDispatcher.enqueueMultitask(connectToServerTask, terminateTheServerTask);
        	return;
        }

        
        // Set the hostname to make remote connections possible
        RMI.setHostName();
        
        
        // CREATE A NEW SERVER
        if(noGUI) {
        	Task startNewServerTask = serverManager.createNewServerTask(password);
        	new PlainProgressMonitor(logger).registerTask(startNewServerTask);
        	
        	SuperDispatcher.enqueue(startNewServerTask);
        	return;
        }
        else {
    		// Set beautiful system look & feel.
            try { 
                UIManager.setLookAndFeel(new PlasticXPLookAndFeel()); 
            } catch (Exception e) { e.printStackTrace(); JFrame.setDefaultLookAndFeelDecorated(true); }
            
   		 	// Initialize Help
            try {
                PlantloreHelp.initialize();
            } catch (PlantloreException e) {}
            
            // Disable reconnect
            DefaultExceptionHandler.disableReconnect();
            
            // Open the Dialog
        	java.awt.EventQueue.invokeLater(new Runnable(){
        		public void run() {
        			ServerCreateView view = new ServerCreateView(serverManager);
        			new ServerCreateCtrl(serverManager, view);
        			view.setVisible(true);
        		}
        	});
        	return;
        }

	}

}
