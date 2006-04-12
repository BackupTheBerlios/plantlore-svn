package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class LoginCtrl {
	
	private Login model;
	private LoginView view;
	
	private ItemView itemView;
	private ItemCtrl itemCtrl;
	private AuthView authView;
	
	
	
	public LoginCtrl(Login login, LoginView loginview) {
		this.view = loginview; this.model = login;
		// Create Item Add/Edit dialog.
		itemView = new ItemView(model);
		itemCtrl = new ItemCtrl(model, itemView);
		// Create Authorization dialog.
		authView = new AuthView(model);
		new AuthCtrl(model, authView);
		
		
		view.choice.addListSelectionListener(new ChoiceChanged());
		view.add.addActionListener(new AddRecord());
		view.edit.addActionListener(new EditRecord());
		view.remove.addActionListener(new RemoveRecord());
		view.next.addActionListener(new Next());
	}
	
	class ChoiceChanged implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			/*----------------------------------------------------------
			 *  Well here is some nasty behaviour of the Swing
			 *  framework: for some unknown reason 
			 *  the ListSelectionEvent is sent twice every time 
			 *  you select something in the list. Why?
			 *----------------------------------------------------------*/
			model.setSelected( view.choice.getSelectedIndex() );
		}	
	}
	
	class AddRecord extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			itemCtrl.setMode(ItemCtrl.Mode.ADD);
			itemView.setVisible(true);
		}
	}
	
	class  RemoveRecord extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			model.deleteSelectedRecord();
		}
	}
	
	class EditRecord extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			if(model.getSelected() == null) return;
			itemCtrl.setMode(ItemCtrl.Mode.EDIT);
			itemView.setVisible(true);
		}
	}
	
	class Next extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			if(model.getSelected() != null)
				authView.setVisible(true);
		}
	}

}
