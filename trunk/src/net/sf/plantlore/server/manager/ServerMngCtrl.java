package net.sf.plantlore.server.manager;

import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

import javax.swing.AbstractAction;


import net.sf.plantlore.server.ConnectionInfo;

public class ServerMngCtrl {
	
	private ServerMngView view;
	private ServerMng model;
	
	public ServerMngCtrl(ServerMng model, ServerMngView view) {
		this.model = model; this. view = view;
		
		view.kick.addActionListener(new KickUsers());
		view.startstop.addActionListener(new StopServer());
		view.terminate.addActionListener(new TerminateServer());
		view.refresh.addActionListener(new Refresh());
	}
	
	
	class KickUsers extends AbstractAction {
		public void actionPerformed(ActionEvent ae) {
			try {
				Object[] bunch = view.users.getSelectedValues();
				if(bunch == null || bunch.length == 0) return;

				for(Object client : bunch)
					if(client instanceof ConnectionInfo) model.kick((ConnectionInfo)client);

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
				model.getConnectedUsers(true);
		}
	}

}
