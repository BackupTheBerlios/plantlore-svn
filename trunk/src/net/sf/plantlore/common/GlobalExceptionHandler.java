package net.sf.plantlore.common;

import java.awt.event.ActionListener;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import net.sf.plantlore.l10n.L10n;


/**
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 24.8.2006
 *
 */
public class GlobalExceptionHandler implements UncaughtExceptionHandler {
	
	private static boolean installed = false;
	private Logger logger = Logger.getLogger(net.sf.plantlore.client.Plantlore.class.getPackage().getName());
	
	private final ActionListener silentFinalAction;
	
	private GlobalExceptionHandler(ActionListener silentFinalAction) {
		this.silentFinalAction = silentFinalAction;
	}
	
	public void uncaughtException(Thread t, Throwable e) {
		logger.fatal(
				"Thread " + t.getName() + " (" + t.getClass().getName() + ") " +
				"spawned an unhandled exception " + e +".\n" +
				"The application will be terminated."
		);
		
		JOptionPane.showMessageDialog( 
				null,
				L10n.getString("Error.UnhandledExceptionalState") +
				"\n" +
				e.getMessage(), 
				L10n.getString("Error.Fatal"), 
				JOptionPane.ERROR_MESSAGE );
		
		if(silentFinalAction != null)
			silentFinalAction.actionPerformed(null);
	}

	
	public static synchronized void install(ActionListener silentFinalAction) {
		if( !installed ) {
			Thread.setDefaultUncaughtExceptionHandler( new GlobalExceptionHandler(silentFinalAction) );
			installed = true;
		} else
			throw new IllegalArgumentException(L10n.getString("Error.ImproperUse"));
	}


}
