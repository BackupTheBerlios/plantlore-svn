package net.sf.plantlore.common;

import java.rmi.RemoteException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.sf.plantlore.common.exception.DBLayerException;
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
	
	public DefaultProgressBar(Task task, JFrame parent, boolean modal) {
		super(task, parent, modal);
	}
	
	public DefaultProgressBar(Task task, JDialog parent, boolean modal) {
		super(task, parent, modal);
	}
	
	
	@Override
	public void exceptionHandler(Exception ex) {
		
		if( ex instanceof RemoteException || ex instanceof DBLayerException )
			DefaultReconnectDialog.show(parent, ex);
		
		else
			JOptionPane.showMessageDialog( 
					parent, 
					ex.getMessage(), 
					L10n.getString("Error.General"), 
					JOptionPane.ERROR_MESSAGE );
		
		getTask().stop();
		
	}
	
}
