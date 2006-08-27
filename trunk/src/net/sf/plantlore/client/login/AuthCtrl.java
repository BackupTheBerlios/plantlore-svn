package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.common.DefaultProgressBarEx;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.l10n.L10n;


public class AuthCtrl {
	
	private AuthView view;
	private Login model;
	
	public AuthCtrl(Login login, AuthView authview) {
		this.model = login; this.view = authview;
		view.next.setAction(new NextAction2());
		view.discard.setAction( new DefaultCancelAction(view) );
	}

	
	class NextAction2 extends AbstractAction {
		public NextAction2() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.AuthorizeTT"));
			putValue(NAME, L10n.getString("Login.Authorize"));
		}
		public void actionPerformed(ActionEvent arg0) {
			String user = ((javax.swing.JTextField)view.user.getEditor().getEditorComponent()).getText();

			Task connect = model.createConnectionTask(user, new String(view.password.getPassword()));
			
			// My own ProgressBar - 
			// it is not clever to offer reconnection after the login failed...
			new DefaultProgressBarEx(connect, view, true) {
				@Override
				public void exceptionHandler(Exception ex) {
					getTask().stop();
					
//					if(ex instanceof DBLayerException)
//						System.out.println(((DBLayerException)e).getErrorCode());
					
					JOptionPane.showMessageDialog( 
							parent, 
							ex.getMessage(), 
							L10n.getString("Error.ConnectionFailed"), 
							JOptionPane.ERROR_MESSAGE );
				}
			};
			connect.start();
		}
	}
	
	
}
