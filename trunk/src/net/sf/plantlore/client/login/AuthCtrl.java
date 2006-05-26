package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.sf.plantlore.l10n.L10n;


public class AuthCtrl {
	
	private AuthView view;
	private Login model;
	
	public AuthCtrl(Login login, AuthView authview) {
		this.model = login; this.view = authview;
		view.next.setAction(new Next());
	}

	
	class Next extends AbstractAction {
		public Next() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.AuthorizeTT"));
			putValue(NAME, L10n.getString("Login.Authorize"));
		}
		public void actionPerformed(ActionEvent arg0) {
			String user = (view.user.getSelectedItem() != null) ? view.user.getSelectedItem().toString() : null;
			if(user != null)
				model.connectToSelected(user, new String(view.password.getPassword()));
			else 
				JOptionPane.showMessageDialog(view,
						L10n.getString("Error.MissingUsername"),
						L10n.getString("Error.Missing"),
						JOptionPane.WARNING_MESSAGE);
			
			view.password.setText("");
		}
	}

}
