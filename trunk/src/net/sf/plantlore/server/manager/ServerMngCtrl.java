package net.sf.plantlore.server.manager;

import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

import javax.swing.AbstractAction;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.plantlore.server.ConnectionInfo;

public class ServerMngCtrl {
	
	private ServerMngView view;
	private ServerMng model;
	
	public ServerMngCtrl(ServerMng model, ServerMngView view) {
		this.model = model; this. view = view;
		
		view.users.addListSelectionListener(new SelectionChange());
		view.kick.addActionListener(new KickUsers());
		view.startstop.addActionListener(new StopServer());
		view.terminate.addActionListener(new TerminateServer());
	}
	
	class SelectionChange implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent lse) {
			ConnectionInfo[] selected = (ConnectionInfo[])view.users.getSelectedValues();
			model.setSelectedClients(selected);			
		}
	}
	
	class KickUsers extends AbstractAction {
		public void actionPerformed(ActionEvent ae) {
			try {
				model.kickSelectedClients();
			} catch(RemoteException re) { }
		}
	}
	
	class StopServer extends AbstractAction {
		public void actionPerformed(ActionEvent ae) {
			try {
				model.stopServer();
			} catch(RemoteException re) { }
		}
	}
	
	class TerminateServer extends AbstractAction {
		public void actionPerformed(ActionEvent ae) {
			try {
				model.terminateServer();
			} catch(RemoteException re) { }
		}
	}
	
	class Refresh extends AbstractAction {
		public void actionPerformed(ActionEvent ae) {
				model.getConnectedUsers();
		}
	}

}
