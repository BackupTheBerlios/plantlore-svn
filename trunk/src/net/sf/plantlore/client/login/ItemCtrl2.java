package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.l10n.L10n;

public class ItemCtrl2 {
	
	private Login model;
	private ItemView2 view;
	
	public enum Mode { ADD, EDIT };
	
	private Mode mode = Mode.ADD;
	
	
	// Switch between the ADD/EDIT behaviour
	public void setMode(Mode m) {
		this.mode = m;
		switch(m) {
		case ADD:
			view.aliasLocal.setText("");
			view.aliasRemote.setText("");
			view.host.setText("");
			view.port.setText("");
			((javax.swing.JTextField)view.databaseEngine.getEditor().getEditorComponent()).setText("");
			view.databaseIdentifier.setText("");
			view.databasePort.setText("");
			view.databaseParameter.setText("");
			view.databaseMasterUser.setText("");
			view.databaseMasterPassword.setText("");
			
			view.remoteDatabaseIdentifier.setText("");
			
			view.setTitle(L10n.getString("Login.AddTitle"));
			view.next.setText(L10n.getString("Login.Add"));
			view.next.setToolTipText(L10n.getString("Login.AddTT"));
			break;
		case EDIT:
			view.setTitle(L10n.getString("Login.EditTitle"));
			view.next.setText(L10n.getString("Login.Change"));
			view.next.setToolTipText(L10n.getString("Login.ChangeTT"));
			view.next.setEnabled(true);
			break;
		}
	}
	
	
	public ItemCtrl2(Login login, ItemView2 itemview) {
		this.model = login; this.view = itemview;
		view.next.setAction(new NextAction());
		view.discard.setAction(
				new DefaultCancelAction(
						itemview, 
						L10n.getString("Login.Discard"), 
						L10n.getString("Login.DiscardTT")));
	}
	
	
	

	
	
	class NextAction extends AbstractAction {
		
		public NextAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.ChangeTT"));
			putValue(NAME, L10n.getString("Login.Change"));
		}
		
		public void actionPerformed(ActionEvent arg0) {
			
			switch( view.jTabbedPane1.getSelectedIndex() ) {
			//-------------------------------------------------
			// Direct Connection (local database only)
			//-------------------------------------------------
			case 0:
				// Validity check...
				String dbEngine = ((javax.swing.JTextField)view.databaseEngine.getEditor().getEditorComponent()).getText(),
				masterPassword = new String(view.databaseMasterPassword.getPassword());
				int dbPort = -1;
				try {
					dbPort = Integer.parseInt(view.databasePort.getText());
				} catch(NumberFormatException e) {
					// Nothing we can do.
				}
				
				if( dbEngine.length() == 0 || 
						view.databaseIdentifier.getText().length() == 0 ||
						dbPort < 0 ||
						view.databaseMasterUser.getText().length() == 0 ||
						masterPassword.length() == 0 ||
						view.aliasLocal.getText().length() == 0 ) {
					// Announce the problem.
					JOptionPane.showMessageDialog(
    						view, 
    						L10n.getString("Error.MissingSeveralCompulsoryFields"), 
    						L10n.getString("Error.MissingCompulsoryFieldTitle"), 
    						JOptionPane.ERROR_MESSAGE);
					return;
				}
					
				// The values appear to be sufficient.
				switch(mode) {
				case ADD:
					model.createRecord(
							view.aliasLocal.getText(),
							null,
							0,
							dbEngine,
							dbPort,
							view.databaseIdentifier.getText(),
							view.databaseParameter.getText(),
							view.databaseMasterUser.getText(),
							masterPassword
					);
					break;
				case EDIT:
					model.updateSelectedRecord(
							view.aliasLocal.getText(),
							null,
							0,
							dbEngine,
							dbPort,
							view.databaseIdentifier.getText(),
							view.databaseParameter.getText(),
							view.databaseMasterUser.getText(),
							masterPassword
					);
					break;
				}
				
				
				break;
				
			//-------------------------------------------------
			// Connection to a remote server
			//-------------------------------------------------
			case 1:
				int port = 1099;
				try {
					port = Integer.parseInt(view.port.getText());
				} catch(NumberFormatException e) {
					// Never mind, use the default port.
				}
				
				// Validity check...
				if( port < 0 || view.aliasRemote.getText().length() == 0 || 
						view.host.getText().length() == 0 || 
						view.remoteDatabaseIdentifier.getText().length() == 0 ) {
					// Announce the problem.
					JOptionPane.showMessageDialog(
    						view, 
    						L10n.getString("Error.MissingSeveralCompulsoryFields"), 
    						L10n.getString("Error.MissingCompulsoryFieldTitle"), 
    						JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// The values appear to be sufficient.
				switch(mode) {
				case ADD:
					model.createRecord(
							view.aliasRemote.getText(),
							view.host.getText(),
							port,
							null, 0, view.remoteDatabaseIdentifier.getText(), null, null, null
					);
					break;
				case EDIT:
					model.updateSelectedRecord(
							view.aliasRemote.getText(),
							view.host.getText(),
							port,
							null, 0, view.remoteDatabaseIdentifier.getText(), null, null, null
					);
					break;
				}
				
				break;
			}
			
			view.setVisible(false);
			setMode(Mode.EDIT);
		}
	}
	

}
