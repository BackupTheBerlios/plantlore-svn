﻿package net.sf.plantlore.common;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * A simple and convenient
 * implementation of the abstract ProgressBarEx with the default exception handler.
 * <br/>
 * 
 * @author kaimu
 * @since 2006-07-26
 *
 */
@Deprecated
public class DefaultProgressBarEx extends ProgressBarEx {
	
	/**
	 * Create a new Default ProgressBar that will monitor the state 
	 * of the supplied task.
	 * 
	 * @param task	The task this ProgressBar should monitor.
	 * @param parent	The parent window in order to maintain the Swing window hierarchy.
	 * @param modal	True if the ProgressBar should be modal.
	 */
	public DefaultProgressBarEx(Task task, JFrame parent, boolean modal) {
		super(task, parent, modal);
	}
	
	/**
	 * Create a new Default ProgressBar that will monitor the state 
	 * of the supplied task.
	 * 
	 * @param task	The task this ProgressBar should monitor.
	 * @param parent	The parent window in order to maintain the Swing window hierarchy.
	 * @param modal	True if the ProgressBar should be modal.
	 */
	public DefaultProgressBarEx(Task task, JDialog parent, boolean modal) {
		super(task, parent, modal);
	}
	
	/**
	 * Stop the task and handle the exception with the DefaultExceptionHandler.
	 * 
	 * @see DefaultExceptionHandler
	 */
	@Override
	public void exceptionHandler(Exception ex) {
		getTask().stop();
		DefaultExceptionHandler.handle(parent, ex);
	}
	
}
