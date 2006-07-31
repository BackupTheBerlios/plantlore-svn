package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;


import net.sf.plantlore.l10n.L10n;

@Deprecated
public class ItemCtrl {
	
	private Login model;
	private ItemView view;
	
	public enum Mode { ADD, EDIT };
	
	private Mode mode = Mode.ADD;
	
	
	// Switch between the ADD/EDIT behaviour
	public void setMode(Mode m) {
		this.mode = m;
		switch(m) {
		case ADD:
			view.alias.setText(""); 
			view.host.setText("");
			view.port.setText("");
			((javax.swing.JTextField)view.database.getEditor().getEditorComponent()).setText("");
			view.databaseIdentifier.setText("");
			view.databasePort.setText("");
			view.databaseParameter.setText("");
			view.databaseMasterUser.setText("");
			view.databaseMasterPassword.setText("");
			
			view.setTitle(L10n.getString("Login.AddTitle"));
			view.next.setText(L10n.getString("Login.Add"));
			view.next.setToolTipText(L10n.getString("Login.AddTT"));
//			view.next.setEnabled(false);
			break;
		case EDIT:
			view.setTitle(L10n.getString("Login.EditTitle"));
			view.next.setText(L10n.getString("Login.Change"));
			view.next.setToolTipText(L10n.getString("Login.ChangeTT"));
			view.next.setEnabled(true);
			break;
		}
	}
	
	
	public ItemCtrl(Login login, ItemView itemview) {
		this.model = login; this.view = itemview;
		view.next.setAction(new NextAction());
		view.discard.setAction(new DiscardAction());
		
//		Action validator = new ObligatoryItemsListener();
//		view.alias.addActionListener(validator);
//		((javax.swing.JTextField)view.database.getEditor().getEditorComponent()).addActionListener(validator);
//		view.databasePort.addActionListener(validator);
//		view.databaseIdentifier.addActionListener(validator);
	}
	
	
	
	class ObligatoryItemsListener extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			boolean newState = 
				view.alias.getText().length() > 0
				&&
				((javax.swing.JTextField)view.database.getEditor().getEditorComponent()).getText().length() > 0
				&&
				view.databaseIdentifier.getText().length() > 0;
				
			if(newState)
				try {
					if( Integer.parseInt(view.databasePort.getText()) <= 0 )
						newState = false;
				} catch(Exception e) { 
					newState = false; 
				}
			
			if( view.next.isEnabled() != newState )
				view.next.setEnabled(newState);
		}
	}
	
	
	class NextAction extends AbstractAction {
		public NextAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.ChangeTT"));
			putValue(NAME, L10n.getString("Login.Change"));
		}
		public void actionPerformed(ActionEvent arg0) {
			int port = 1099, databasePort = 0;
			try {
				port = Integer.parseInt(view.port.getText());
			} catch(NumberFormatException e) {
				// Never mind, use the default port.
			}
			try {
				databasePort = Integer.parseInt(view.databasePort.getText());
			} catch(NumberFormatException e) {
				// Nothing we can do.
			}
			
			switch(mode) {
			case ADD:
				model.createRecord(
						view.alias.getText(),
						view.host.getText(),
						port,
						((javax.swing.JTextField)view.database.getEditor().getEditorComponent()).getText(),
						databasePort,
						view.databaseIdentifier.getText(),
						view.databaseParameter.getText(),
						view.databaseMasterUser.getText(),
						new String(view.databaseMasterPassword.getPassword())
				);
				break;
			case EDIT:
				model.updateSelectedRecord(
						view.alias.getText(),
						view.host.getText(),
						port,
						((javax.swing.JTextField)view.database.getEditor().getEditorComponent()).getText(),
						databasePort,
						view.databaseIdentifier.getText(),
						view.databaseParameter.getText(),
						view.databaseMasterUser.getText(),
						new String(view.databaseMasterPassword.getPassword())
				);
				break;
			}
			
			view.setVisible(false);
			
			setMode(Mode.EDIT);
		}
	}
	
	class DiscardAction extends AbstractAction {
		public DiscardAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.DiscardTT"));
			putValue(NAME, L10n.getString("Login.Discard"));
		}
		public void actionPerformed(ActionEvent arg0) {
			view.setVisible(false);
		}
	}

}
