package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ItemCtrl {
	
	private Login model;
	private ItemView view;
	
	public enum Mode { ADD, EDIT };
	
	private Mode mode = Mode.ADD;
	
	
	public void setMode(Mode m) { this.mode = m; }
	
	
	public ItemCtrl(Login login, ItemView itemview) {
		this.model = login; this.view = itemview;
		
		view.nextAddActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				int t = view.getHost().indexOf(':');
				if(mode == Mode.ADD)
					if(t < 0) model.createRecord(view.getAlias(), view.getHost(), 1099, view.getDB());
					else model.createRecord(view.getAlias(), view.getHost().substring(0, t - 1), Integer.parseInt(view.getHost().substring(t)), view.getDB());
				else if(mode == Mode.EDIT)
					if(t < 0) model.updateSelectedRecord(view.getAlias(), view.getHost(), 1099, view.getDB());
					else model.updateSelectedRecord(view.getAlias(), view.getHost().substring(0, t - 1), Integer.parseInt(view.getHost().substring(t)), view.getDB());
				view.setVisible(false);
			}
		});
		
	}

}
