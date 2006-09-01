package net.sf.plantlore.common;

import java.awt.event.ActionListener;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import net.sf.plantlore.l10n.L10n;


/**
 * In case there are some unchecked exceptions or something goes really really
 * wrong (and is not covered by other catch blocks) the Global Exception handler
 * will catch that stranded exception, display a message to the User, and try
 * to shut down the application in a civilized manner.
 * <br/>
 * This is the last resort and the less you see this message the better job we have done.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-08-24
 * @version 1.0
 */
public class GlobalExceptionHandler implements UncaughtExceptionHandler {
	
	/**
	 * Make sure the DefaultException handler is not installed twice.
	 */
	private static boolean installed = false;
	private Logger logger = Logger.getLogger(net.sf.plantlore.client.Plantlore.class.getPackage().getName());
	
	private final ActionListener silentFinalAction;
	
	/**
	 * Create a new Global Exception handler.
	 * 
	 * @param silentFinalAction The action that will perform silent (i.e. without exceptions) cleanup.
	 */
	private GlobalExceptionHandler(ActionListener silentFinalAction) {
		this.silentFinalAction = silentFinalAction;
	}
	
	/**
	 * Write a message into the log describing the problem,
	 * display a message to the User, and call the silentFinalAction().
	 */
	public void uncaughtException(Thread t, Throwable e) {
		logger.fatal(
				"Thread " + t.getName() + " (" + t.getClass().getName() + ") " +
				"spawned an unhandled exception " + e +".\n" +
				"The application will be terminated."
		);
		
		//FIXME (maybe)
		e.printStackTrace();
		
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

	/**
	 * Install the Global exception handler. Thread safe. At most once semantics.
	 * 
	 * @param silentFinalAction	The action that will be called to perform the final cleanup.
	 * This action should be very sturdy and should throw no exceptions. At the end of the
	 * action there should be the call <code>System.exit(1);</code> that will terminate
	 * the application - there is no point in continuing because no error recovery is possible.
	 */
	public static synchronized void install(ActionListener silentFinalAction) {
		if( !installed ) {
			Thread.setDefaultUncaughtExceptionHandler( new GlobalExceptionHandler(silentFinalAction) );
			installed = true;
		} else
			throw new IllegalArgumentException(L10n.getString("Error.ImproperUse"));
	}


}
