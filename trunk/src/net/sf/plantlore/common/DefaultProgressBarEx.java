package net.sf.plantlore.common;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.sf.plantlore.l10n.L10n;

/**
 * A simple and convenient
 * implementation of the abstract ProgressBar.
 * <br/>
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-07-26
 *
 */
public class DefaultProgressBarEx extends ProgressBarEx {
	
	
	public DefaultProgressBarEx(Task task, JFrame parent, boolean modal) {
		super(task, parent, modal);
	}
	
	public DefaultProgressBarEx(Task task, JDialog parent, boolean modal) {
		super(task, parent, modal);
	}
	
	
	
	@Override
	public void exceptionHandler(Exception ex) {
		JOptionPane.showMessageDialog( 
				parent, 
				ex.getMessage(), 
				L10n.getString("Error.General"), 
				JOptionPane.ERROR_MESSAGE );
		getTask().stop();
		getTask().fireStopped(null); // So that the afterStopped() method is called! (BUG OR FEATURE in Task?)
	}
	
}
