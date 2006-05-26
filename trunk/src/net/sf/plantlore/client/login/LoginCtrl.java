package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
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
		itemView = new ItemView(model);
		itemCtrl = new ItemCtrl(model, itemView);
		// Create Authorization dialog.
		authView = new AuthView(model);
		new AuthCtrl(model, authView);
		
		
		view.choice.addListSelectionListener(new ChoiceChanged());
		view.add.setAction(new AddRecord());
		view.edit.setAction(new EditRecord());
		view.remove.setAction(new RemoveRecord());
		view.next.setAction(new Next());
		
		// Select something.
		view.choice.setSelectedIndex(0);
	}
	
	
	public void setVisible(boolean visible) {
		JDialog dialog = view;
		if( view.remember.isSelected() && !view.choice.isSelectionEmpty() ) { 
			dialog = authView;
		}
		
		//UNCOMMENT THIS:      
		dialog.setVisible(visible);
		
//		// TEMPORARY CODE STARTS HERE
//			System.out.println("HYPERACTIVE-LOGIN");
//			authView.password.setText("masterkey");
//			authView.next.doClick(); 
//		// TEMPORARY CODE ENDS HERE
	}
	
	
	class ChoiceChanged implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			/*----------------------------------------------------------
			 *  Well here is some nasty behaviour of the Swing
			 *  framework: for some unknown reason 
			 *  the ListSelectionEvent is sent twice every time 
			 *  you select something in the list. Why?
			 *  
			 *  The reason will be simple: 
			 *  mousePressed & mouseReleased 
			 *  (instead of mouseClicked).  
			 *----------------------------------------------------------*/
			model.setSelected( view.choice.getSelectedIndex() );
		}	
	}
	
	class AddRecord extends AbstractAction {
		public AddRecord() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.AddRecordTT"));
			putValue(NAME, L10n.getString("Login.AddRecord"));
		}
		public void actionPerformed(ActionEvent arg0) {
			itemCtrl.setMode(ItemCtrl.Mode.ADD);
			itemView.setVisible(true);
		}
	}
	
	class  RemoveRecord extends AbstractAction {
		public RemoveRecord() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.RemoveRecordTT"));
			putValue(NAME, L10n.getString("Login.RemoveRecord"));
		}
		public void actionPerformed(ActionEvent arg0) {
			model.deleteSelectedRecord();
		}
	}
	
	class EditRecord extends AbstractAction {
		public EditRecord() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.EditRecordTT"));
			putValue(NAME, L10n.getString("Login.EditRecord"));
		}
		public void actionPerformed(ActionEvent arg0) {
			if(model.getSelected() == null)
				JOptionPane.showMessageDialog(view,
						L10n.getString("Error.NothingSelected"),
					    L10n.getString("Error.Missing"),
					    JOptionPane.WARNING_MESSAGE);
			else {
				itemCtrl.setMode(ItemCtrl.Mode.EDIT);
				itemView.setVisible(true);
			}
		}
	}
	
	class Next extends AbstractAction {
		public Next() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.NextTT"));
			putValue(NAME, L10n.getString("Login.Next"));
		}
		public void actionPerformed(ActionEvent arg0) {
			if(model.getSelected() == null)
				JOptionPane.showMessageDialog(view,
						L10n.getString("Error.NothingSelected"),
					    L10n.getString("Error.Missing"),
					    JOptionPane.WARNING_MESSAGE);
			else
				authView.setVisible(true);
		}
	}

}
