package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class AuthCtrl {
	
	private AuthView view;
	private Login model;
	
	public AuthCtrl(Login login, AuthView authview) {
		this.model = login; this.view = authview;
		
		view.nextAddActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("hiding");
				try {
					model.connectToSelected(view.getUserName(), view.getPassword());
				} catch(Exception e) {
					System.err.println("The connection to the database couldn't be established!\n" + e);
				}
				
				view.setVisible(false);
			}
		});
	}

}
