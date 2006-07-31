package net.sf.plantlore.client.login;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.common.DefaultProgressBar;
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

	@Deprecated
	class NextAction extends AbstractAction {
		public NextAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.AuthorizeTT"));
			putValue(NAME, L10n.getString("Login.Authorize"));
		}
		public void actionPerformed(ActionEvent arg0) {
			view.next.setEnabled(false);
			view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			view.discard.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			String user = (view.user.getSelectedItem() != null) ? view.user.getSelectedItem().toString() : null;

			model.connectToSelected(user, new String(view.password.getPassword()));
		}
	}
	
	
	class NextAction2 extends AbstractAction {
		public NextAction2() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.AuthorizeTT"));
			putValue(NAME, L10n.getString("Login.Authorize"));
		}
		public void actionPerformed(ActionEvent arg0) {
			//view.next.setEnabled(false);
			//view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			//view.discard.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			String user = ((javax.swing.JTextField)view.user.getEditor().getEditorComponent()).getText();

			Task connect = model.createConnectionTask(user, new String(view.password.getPassword()));
			/*DefaultProgressBar bar = */new DefaultProgressBarEx(connect, view, true);
			//bar.unlockComponents(view.next);
			connect.start();
		}
	}
	
	@Deprecated
	class CancelAction extends AbstractAction {
		public CancelAction() {
			//putValue(SHORT_DESCRIPTION, L10n.getString("Login.DiscardTT"));
			putValue(NAME, L10n.getString("Login.Discard"));
		}
		public void actionPerformed(ActionEvent arg0) {
			if( !view.next.isEnabled() ) {
				view.next.setEnabled(true);
				view.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				model.interrupt();
			}
			else
				view.setVisible(false);
		}
	}

}
