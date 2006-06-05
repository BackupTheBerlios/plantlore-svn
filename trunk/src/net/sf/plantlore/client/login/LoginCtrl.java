package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.plantlore.l10n.L10n;


public class LoginCtrl {
	
	private Login model;
	private LoginView view;
	
	private ItemView itemView;
	private ItemCtrl itemCtrl;
	private AuthView authView;
	
	
	
	public LoginCtrl(Login login, LoginView loginview) {
		this.view = loginview; this.model = login;
		
		// Create Item Add/Edit dialog.
		itemView = new ItemView(view, model);
		itemCtrl = new ItemCtrl(model, itemView);
		// Create Authorization dialog.
		authView = new AuthView(view, model);
		new AuthCtrl(model, authView);
		
		view.choice.addListSelectionListener(new ChoiceChanged());
		view.add.setAction(new AddRecordAction());
		view.edit.setAction(new EditRecordAction());
		view.remove.setAction(new RemoveRecordAction());
		view.next.setAction(new NextAction());
		
		// Select something.
		view.choice.setSelectedIndex(0);
	}
	
	
	public void setVisible(boolean visible) {
		JDialog dialog = view.remember.isSelected() && !view.choice.isSelectionEmpty() ? authView : view;
		dialog.setVisible(visible);
	}
	
	
	class ChoiceChanged implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			int index = view.choice.getSelectedIndex();
			view.next.setEnabled( index >= 0 ); // Evil code.
			model.setSelected( index );
		}	
	}
	
	class AddRecordAction extends AbstractAction {
		public AddRecordAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.AddRecordTT"));
			putValue(NAME, L10n.getString("Login.AddRecord"));
		}
		public void actionPerformed(ActionEvent arg0) {
			itemCtrl.setMode(ItemCtrl.Mode.ADD);
			itemView.setVisible(true);
		}
	}
	
	class  RemoveRecordAction extends AbstractAction {
		public RemoveRecordAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.RemoveRecordTT"));
			putValue(NAME, L10n.getString("Login.RemoveRecord"));
		}
		public void actionPerformed(ActionEvent arg0) {
			model.deleteSelectedRecord();
		}
	}
	
	class EditRecordAction extends AbstractAction {
		public EditRecordAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.EditRecordTT"));
			putValue(NAME, L10n.getString("Login.EditRecord"));
		}
		public void actionPerformed(ActionEvent arg0) {
			if(model.getSelected() != null) {
				itemCtrl.setMode(ItemCtrl.Mode.EDIT);
				itemView.setVisible(true);
			}
		}
	}
	
	class NextAction extends AbstractAction {
		public NextAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.NextTT"));
			putValue(NAME, L10n.getString("Login.Next"));
		}
		public void actionPerformed(ActionEvent arg0) {
			if(model.getSelected() != null)
				authView.setVisible(true);
		}
	}

}
