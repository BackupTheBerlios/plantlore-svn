package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.common.DefaultExceptionHandler;
import net.sf.plantlore.common.DefaultProgressBarEx;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.l10n.L10n;

/**
 * The mapping of buttons in the view to actions.
 * The User can click to start the Connection to the database.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 */
public class AuthCtrl {
	
	private AuthView view;
	private Login model;
	
	public AuthCtrl(Login login, AuthView authview) {
		this.model = login; this.view = authview;
		view.next.setAction(new NextAction2());
		view.discard.setAction( new DefaultCancelAction(view) );
	}

	/**
	 *	Gather the information from the dialog and 
	 * execute the connection task.
	 */
	class NextAction2 extends AbstractAction {
		public NextAction2() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.AuthorizeTT"));
			putValue(NAME, L10n.getString("Login.Authorize"));
		}
		public void actionPerformed(ActionEvent arg0) {
			String user = ((javax.swing.JTextField)view.user.getEditor().getEditorComponent()).getText();
			Task connect = model.createConnectionTask(user, new String(view.password.getPassword()));
			
			// My own ProgressBar - 
			// it would not be wise to offer reconnection right after the login failed...
			new DefaultProgressBarEx(connect, view, true) {
				@Override
				public void exceptionHandler(Exception ex) {
					getTask().stop();
					DefaultExceptionHandler.handle(parent, ex, L10n.getString("Login.ConnectionFailed"), true);
				}
			};
			connect.start();
			
			// Discard the password!
			view.password.setText("");
		}
	}
	
	
}
