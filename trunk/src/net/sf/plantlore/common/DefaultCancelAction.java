package net.sf.plantlore.common;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;

import net.sf.plantlore.l10n.L10n;


/**
 * Most dialogs have a Cancel or a Discard button which always does the same action:
 * closes the dialog without doing anything else. So as not to have to write this action
 * over and over again, this DefaultCancelAction came into existence.
 * <br/>
 * The use is very simple (in the controller):
 * <br/>
 * <code>myView.myCancelButton.setAction( new DefaultCancelAction( myView ) ); </code>
 * <br/>
 * Both JFrames and JDialogs may be used with the DefaultCancelAction.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-07-26
 */
public class DefaultCancelAction extends AbstractAction {
	
	protected JDialog dialog;
	protected JFrame frame;
	
	
	/**
	 * Create a new Cancel action that hides the given frame.
	 * 
	 * @param frame	The frame to be hidden.
	 * @param name		The caption of the button.
	 * @param tooltip	The tooltip displayed over the button.
	 */
	public DefaultCancelAction(JFrame frame, String name, String tooltip) {
		putValue(SHORT_DESCRIPTION, tooltip);
		putValue(NAME, name);
		this.frame = frame;
	}
	
	/**
	 * Create a new Cancel action that hides the given dialog.
	 * 
	 * @param dialog	The dialog to be hidden.
	 * @param name		The caption of the button.
	 * @param tooltip	The tooltip displayed over the button.
	 */
	public DefaultCancelAction(JDialog dialog, String name, String tooltip) {
		putValue(SHORT_DESCRIPTION, tooltip);
		putValue(NAME, name);
		this.dialog = dialog;
	}
	
	/**
	 * Create a new Cancel action that hides the given frame.
	 * The caption of the button is set to a default value.
	 * 
	 * @param frame	The frame to be hidden.
	 */
	public DefaultCancelAction(JFrame frame) {
		putValue(NAME, L10n.getString("Common.Cancel"));
		this.frame = frame;
	}
	
	/**
	 * Create a new Cancel action that hides the given dialog.
	 * The caption of the button is set to a default value.
	 * 
	 * @param dialog	The dialog to be hidden.
	 */
	public DefaultCancelAction(JDialog dialog) {
		putValue(NAME, L10n.getString("Common.Cancel"));
		this.dialog = dialog;
	}

	
	/**
	 * Hide the frame (or dialog).
	 */
	public void actionPerformed(ActionEvent e) {
		if(frame != null)
			frame.setVisible(false);
		else if(dialog != null)
			dialog.setVisible(false);
	}
	

}
