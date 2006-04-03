package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class LoginCtrl {
	
	private Login model;
	private LoginView view;
	
	
	public LoginCtrl(Login login, LoginView loginview) {
		this.view = loginview; this.model = login;
		
		view.listAddListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				model.setSelected(e.getFirstIndex());
			}
		});
		
		view.addAddActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				ItemView i = new ItemView(model);
				i.setTitle("Add");
				ItemCtrl c = new ItemCtrl(model, i);
				c.setMode(ItemCtrl.Mode.ADD);
				i.setVisible(true);
				System.out.println("Adding dialog opened.");
			}
		});
		
		view.editAddActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				if(model.getSelected() == null) return;
				ItemView i = new ItemView(model);
				i.setTitle("Edit");
				i.fillWithSelected();
				ItemCtrl c = new ItemCtrl(model, i);
				c.setMode(ItemCtrl.Mode.EDIT);
				i.setVisible(true);
			}
		});
				
		view.removeAddActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				model.deleteSelected();				
			}
		});
		
		view.nextAddActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				if(model.getSelected() == null) return; // Must select somethin'								
				AuthView a = new AuthView(model);
				AuthCtrl c = new AuthCtrl(model, a);
				a.setVisible(true);
			}
		});
		
	}

}
