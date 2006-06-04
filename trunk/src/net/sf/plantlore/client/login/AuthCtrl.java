package net.sf.plantlore.client.login;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sf.plantlore.l10n.L10n;


public class AuthCtrl {
	
	private AuthView view;
	private Login model;
	
	public AuthCtrl(Login login, AuthView authview) {
		this.model = login; this.view = authview;
		view.next.setAction(new NextAction());
		view.discard.setAction(new CancelAction());
	}

	
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
