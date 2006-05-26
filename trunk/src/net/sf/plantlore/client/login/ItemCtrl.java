package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

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
			view.setTitle(L10n.getString("Login.AddTitle"));
			view.alias.setText(""); 
			view.host.setText("");
			view.db.setText("");
			view.next.setText(L10n.getString("Login.Add"));
			view.next.setToolTipText(L10n.getString("Login.AddTT"));
			break;
		case EDIT:
			view.setTitle(L10n.getString("Login.EditTitle"));
			view.next.setText(L10n.getString("Login.Change"));
			view.next.setToolTipText(L10n.getString("Login.ChangeTT"));
			break;
		}
	}
	
	
	public ItemCtrl(Login login, ItemView itemview) {
		this.model = login; this.view = itemview;
		view.next.setAction(new Next());
	}
	
	class Next extends AbstractAction {
		public Next() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Login.ChangeTT"));
			putValue(NAME, L10n.getString("Login.Change"));
		}
		public void actionPerformed(ActionEvent arg0) {
			int t = view.host.getText().indexOf(':');
			int port = (t < 0) ? 1099 : Integer.parseInt(view.host.getText().substring(t));
			String host = (t < 0) ? view.host.getText() : view.host.getText().substring(0, t - 1);
			String alias = view.alias.getText(), db = view.db.getText();
			
			switch(mode) {
			case ADD:
				model.createRecord(alias, host, port, db);
				break;
			case EDIT:
				model.updateSelectedRecord(alias, host, port, db);
				break;
			}
			view.setVisible(false);
		}
	}

}
