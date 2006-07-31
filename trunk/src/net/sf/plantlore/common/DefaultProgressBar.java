package net.sf.plantlore.common;

import java.awt.Component;

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
public class DefaultProgressBar extends ProgressBar {
	
	private Component[] components;
	
	
	public DefaultProgressBar(Task task, JFrame parent, boolean modal) {
		super(task, parent, modal);
	}
	
	public DefaultProgressBar(Task task, JDialog parent, boolean modal) {
		super(task, parent, modal);
	}
	
	
	public DefaultProgressBar unlockComponents(Component...c) {
		this.components = c;
		return this;
	}
	
	
	@Override
	public void afterStopped(Object value) {
		super.afterStopped(value);
		if(components != null)
			for(Component c : components)
				c.setEnabled(true);
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
