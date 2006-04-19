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
				// Go very carefully here!
				String user = (view.user.getSelectedItem() != null) ? view.user.getSelectedItem().toString() : null;
				if(user != null) {
					model.connectToSelected(user, new String(view.password.getPassword()));
					view.setVisible(false);
				}
				else ; // TODO: fire something must be selected info!
			} 
			catch(Exception e) { 
				System.err.println("The connection to the database couldn't be established!\n" + e); 
			}
			finally { view.password.setText(""); }
		}
	}

}
