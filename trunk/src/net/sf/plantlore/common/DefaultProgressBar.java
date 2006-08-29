package net.sf.plantlore.common;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * A simple and convenient
 * implementation of the abstract ProgressBar.
 * <br/>
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-07-26
 *
 */
public class DefaultProgressBar extends ProgressBar {
	
	public DefaultProgressBar(Task task, JFrame parent, boolean modal) {
		super(task, parent, modal);
	}
	
	public DefaultProgressBar(Task task, JDialog parent, boolean modal) {
		super(task, parent, modal);
	}
	
	
	@Override
	public void exceptionHandler(Exception ex) {
		
		getTask().stop();
		
		DefaultExceptionHandler.handle(parent, ex);
		
	}
	
}
