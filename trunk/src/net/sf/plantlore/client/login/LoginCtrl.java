package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.l10n.L10n;

/**
 * The mapping of buttons in the view to actions.
 * The User can move to the Authorization dialog,
 * or to the Add/Edit Item dialog, or Delete one item 
 * from the list.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 */
public class LoginCtrl {
	
	private Login model;
	private LoginView view;
	
	private ItemView2 itemView;
	private ItemCtrl2 itemCtrl;
	private AuthView authView;
	
	
	/**
	 * Create a new Login Controller. Requires the Model and the View.
	 * 
	 * @param login The model.
	 * @param loginview	The view.
	 */
	public LoginCtrl(Login login, LoginView loginview) {
		this.view = loginview; this.model = login;
		
		// Create Item Add/Edit dialog.
		itemView = new ItemView2(view, model);
		itemCtrl = new ItemCtrl2(model, itemView);
		// Create Authorization dialog.
		authView = new AuthView(view, model);
		new AuthCtrl(model, authView);
		
		view.choice.addListSelectionListener(new ChoiceChanged());
		view.add.setAction(new AddRecordAction());
		view.edit.setAction(new EditRecordAction());
		view.remove.setAction(new RemoveRecordAction());
		view.next.setAction(new NextAction());
		view.discard.setAction(new DefaultCancelAction(view));
		
		// Select something.
		view.choice.setSelectedIndex(0);
	}
	
	/**
	 * Set either this dialog or the Authorization dialog visible.
	 * The authorization dialog will be opened instead of this one 
	 * if the selection is non-empty and the View.Remember checkbox is selected.
	 *  
	 * @param visible	True if the View should become visible.
	 */
	public void setVisible(boolean visible) {
		JDialog dialog = view.remember.isSelected() && !view.choice.isSelectionEmpty() ? authView : view;
		dialog.setVisible(visible);
	}
	
	/**
	 *	Set the currently selected record in the Model accordingly. 
	 */
	class ChoiceChanged implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			int index = view.choice.getSelectedIndex();
			view.next.setEnabled( index >= 0 ); // Evil code.
			model.setSelected( index );
		}	
	}
	
	/**
	 * Open the `Add New Record` dialog. 
	 */
	class AddRecordAction extends AbstractAction {
		public AddRecordAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.AddRecordTT"));
			putValue(NAME, L10n.getString("Login.AddRecord"));
		}
		public void actionPerformed(ActionEvent arg0) {
			itemCtrl.setMode(ItemCtrl2.Mode.ADD);
			itemView.setVisible(true);
		}
	}
	
	/**
	 *	Remove the selected record.
	 */
	class  RemoveRecordAction extends AbstractAction {
		public RemoveRecordAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.RemoveRecordTT"));
			putValue(NAME, L10n.getString("Login.RemoveRecord"));
		}
		public void actionPerformed(ActionEvent arg0) {
			model.deleteSelectedRecord();
		}
	}
	
	/**
	 * Open the `Edit the Selected Record` dialog.
	 */
	class EditRecordAction extends AbstractAction {
		public EditRecordAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.EditRecordTT"));
			putValue(NAME, L10n.getString("Login.EditRecord"));
		}
		public void actionPerformed(ActionEvent arg0) {
			if(model.getSelected() != null) {
				itemCtrl.setMode(ItemCtrl2.Mode.EDIT);
				itemView.setVisible(true);
			}
		}
	}
	
	/**
	 * Proceed to the Authorization - open the `Authentication` dialog.
	 */
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
