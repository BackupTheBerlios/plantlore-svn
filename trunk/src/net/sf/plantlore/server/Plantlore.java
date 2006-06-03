package net.sf.plantlore.server;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.log4j.PropertyConfigurator;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.server.manager.*;


/**
 * Temporary solution.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 30.3.2006
 * @version beta not tested
 */
public class Plantlore {
	
	private static final String LOGGER_PROPS = "net/sf/plantlore/config/log4j.properties";
	
	
	public void run(final boolean connectToExisting) {
		// Load log4j settings
		try {
			ClassLoader cl = this.getClass().getClassLoader();
			InputStream is = cl.getResourceAsStream(LOGGER_PROPS);
			Properties props = new Properties();
			props.load(is);
			PropertyConfigurator.configure(props);
			L10n.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

        java.awt.EventQueue.invokeLater(new Runnable(){
        	public void run() {
        		ServerMng model = new ServerMng();
        		if(connectToExisting) {
        			ServerLoginView view = new ServerLoginView(model);
        			/*ServerMngChooseCtrl ctrl = */new ServerLoginCtrl(model, view);
        			view.setVisible(true);
        		}
        		else {
        			ServerCreateView view = new ServerCreateView(model);
        			/*ServerMngChooseCtrl ctrl = */new ServerCreateCtrl(model, view);
        			view.setVisible(true);
        		}
        	}
        });
       	
        
	}
	

	/**
	 * 
	 * @param args Controlling the server from the command line:
	 * 		start|stop|who|kick=id [host] [port]
	 */
	public static void main(String[] args) {
		
		// Set beautiful system look & feel.
        try { 
            UIManager.setLookAndFeel(new PlasticXPLookAndFeel()); 
        } catch (Exception e) { e.printStackTrace(); JFrame.setDefaultLookAndFeelDecorated(true); }
		
        if(args.length > 0 && args[0].equalsIgnoreCase("connect"))
        	new Plantlore().run(true);
        else
        	new Plantlore().run(false);
	}

}
