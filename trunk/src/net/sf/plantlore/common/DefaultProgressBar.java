package net.sf.plantlore.common;

import javax.swing.JDialog;
import javax.swing.JFrame;

import net.sf.plantlore.l10n.L10n;

/**
 * A simple and convenient
 * implementation of the abstract ProgressBar with the default exception handler.
 * <br/>
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-07-26
 *
 */
public class DefaultProgressBar extends ProgressBar {
	
	private boolean doNotOfferReconnect = false;
	
	/**
	 * Create a new Default ProgressBar that will monitor the state 
	 * of the supplied task.
	 * 
	 * @param task	The task this ProgressBar should monitor.
	 * @param parent	The parent window in order to maintain the Swing window hierarchy.
	 * @param modal	True if the ProgressBar should be modal.
	 */
	public DefaultProgressBar(Task task, JFrame parent, boolean modal) {
		super(task, parent, modal);
	}
	
	/**
	 * Create a new Default ProgressBar that will monitor the state 
	 * of the supplied task.
	 * 
	 * @param task	The task this ProgressBar should monitor.
	 * @param parent	The parent window in order to maintain the Swing window hierarchy.
	 * @param modal	True if the ProgressBar should be modal.
	 * @param doNotOfferReconnect True if the Default Exception Handler should not offer
	 * the Reconnect under any circumstances (convenient for the Server or Login).
	 */
	public DefaultProgressBar(Task task, JFrame parent, boolean modal, boolean doNotOfferReconnet) {
		super(task, parent, modal);
		this.doNotOfferReconnect = doNotOfferReconnet;
	}
	
	/**
	 * Create a new Default ProgressBar that will monitor the state 
	 * of the supplied task.
	 * 
	 * @param task	The task this ProgressBar should monitor.
	 * @param parent	The parent window in order to maintain the Swing window hierarchy.
	 * @param modal	True if the ProgressBar should be modal.
	 */
	public DefaultProgressBar(Task task, JDialog parent, boolean modal) {
		super(task, parent, modal);
	}
	
	/**
	 * Create a new Default ProgressBar that will monitor the state 
	 * of the supplied task.
	 * 
	 * @param task	The task this ProgressBar should monitor.
	 * @param parent	The parent window in order to maintain the Swing window hierarchy.
	 * @param modal	True if the ProgressBar should be modal.
	 * @param doNotOfferReconnect True if the Default Exception Handler should not offer
	 * the Reconnect under any circumstances (convenient for the Server or Login).
	 */
	public DefaultProgressBar(Task task, JDialog parent, boolean modal, boolean doNotOfferReconnet) {
		super(task, parent, modal);
		this.doNotOfferReconnect = doNotOfferReconnet;
	}
	
	/**
	 * Stop the task and handle the exception with the DefaultExceptionHandler.
	 * 
	 * @see DefaultExceptionHandler
	 */
	@Override
	public void exceptionHandler(Exception ex) {
		getTask().stop();
		DefaultExceptionHandler.handle(parent, ex, L10n.getString("Error.General"), doNotOfferReconnect);
	}
	
}
