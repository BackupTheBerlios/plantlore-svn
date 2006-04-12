package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class AuthCtrl {
	
	private AuthView view;
	private Login model;
	
	public AuthCtrl(Login login, AuthView authview) {
		this.model = login; this.view = authview;
		view.next.addActionListener(new Next());
	}
	
	class Next extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			try {
				model.connectToSelected(view.user.getSelectedItem().toString(), new String(view.password.getPassword()));
			} catch(Exception e) { System.err.println("The connection to the database couldn't be established!\n" + e); }
			finally { view.password.setText(""); }
			view.setVisible(false);
		}
	}

}
