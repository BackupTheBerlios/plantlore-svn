package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.event.ChangeEvent;

import net.sf.plantlore.l10n.L10n;

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
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
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
					view.next.setEnabled(false);
				}
			});
			break;
		case EDIT:
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					view.setTitle(L10n.getString("Login.EditTitle"));
					view.next.setText(L10n.getString("Login.Change"));
					view.next.setToolTipText(L10n.getString("Login.ChangeTT"));
					view.next.setEnabled(true);
				}
			});
			break;
		}
	}
	
	
	public ItemCtrl(Login login, ItemView itemview) {
		this.model = login; this.view = itemview;
		view.next.setAction(new NextAction());
		view.discard.setAction(new DiscardAction());
		//view.next.addChangeListener();
	}
	
	
	
	class ObligatoryItemsListener implements javax.swing.event.ChangeListener {
		public void stateChanged(ChangeEvent event) {
			boolean newState = 
				view.alias.getText().length() > 0
				&&
				((javax.swing.JTextField)view.database.getEditor().getEditorComponent()).getText().length() > 0
				&&
				view.databaseIdentifier.getText().length() > 0;
			if(newState)
				try {
					if( Integer.parseInt(view.databasePort.getText()) < 1024 )
						newState = false;
				} catch(Exception e) { 
					newState = false; 
				}
			
			boolean state = view.next.isEnabled();
			
			if( state != newState )
				view.next.setEnabled(newState);
		}
	}
	
	
	class NextAction extends AbstractAction {
		public NextAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.ChangeTT"));
			putValue(NAME, L10n.getString("Login.Change"));
		}
		public void actionPerformed(ActionEvent arg0) {
			int port = 1099, databasePort = -1;
			try {
				port = Integer.parseInt(view.port.getText());
			} catch(NumberFormatException e) {}
			try {
				databasePort = Integer.parseInt(view.databasePort.getText());
			} catch(NumberFormatException e) {}
			
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
			
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					view.setVisible(false);
				}
			});
			
			setMode(Mode.EDIT);
		}
	}
	
	class DiscardAction extends AbstractAction {
		public DiscardAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.DiscardTT"));
			putValue(NAME, L10n.getString("Login.Discard"));
		}
		public void actionPerformed(ActionEvent arg0) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					view.setVisible(false);
				}
			});
		}
	}

}
