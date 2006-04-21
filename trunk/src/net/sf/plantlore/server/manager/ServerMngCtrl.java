package net.sf.plantlore.server.manager;

import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.server.ConnectionInfo;

/**
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-21
 */
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
		public KickUsers() {
            putValue(NAME, L10n.getString("Kick"));
            putValue(SHORT_DESCRIPTION, L10n.getString("KickTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Kick"));            
        }
		
		public void actionPerformed(ActionEvent ae) {
			try {
				Object[] bunch = view.users.getSelectedValues();
				if(bunch == null || bunch.length == 0) return;

				for(Object client : bunch)
					if(client instanceof ConnectionInfo) model.kick((ConnectionInfo)client);

			} catch(RemoteException re) { 
				JOptionPane.showMessageDialog(view,
						L10n.getString("errorRemoteException"),
					    L10n.getString("errorRemoteExceptionTitle"),
					    JOptionPane.WARNING_MESSAGE);
				return;
			}
		}
	}
	
	class StopServer extends AbstractAction {
		public void actionPerformed(ActionEvent ae) {
			try {
				model.stopServer();
			} catch(RemoteException re) { 
				JOptionPane.showMessageDialog(view,
						L10n.getString("errorRemoteException"),
					    L10n.getString("errorRemoteExceptionTitle"),
					    JOptionPane.WARNING_MESSAGE);
				return;
			}
		}
	}
	
	class TerminateServer extends AbstractAction {
		public TerminateServer() {
            putValue(NAME, L10n.getString("TerminateServer"));
            putValue(SHORT_DESCRIPTION, L10n.getString("TerminateServerTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("TerminateServer"));            
        }
		
		public void actionPerformed(ActionEvent ae) {
			try {
				model.terminateServer();
			} catch(RemoteException re) { 
				JOptionPane.showMessageDialog(view,
						L10n.getString("errorRemoteException"),
					    L10n.getString("errorRemoteExceptionTitle"),
					    JOptionPane.WARNING_MESSAGE);
				return;
			}
		}
	}
	
	class Refresh extends AbstractAction {
		public Refresh() {
            putValue(NAME, L10n.getString("Refresh"));
            putValue(SHORT_DESCRIPTION, L10n.getString("RefreshTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Refresh"));            
        }
		
		public void actionPerformed(ActionEvent ae) {
				model.getConnectedUsers(true);
		}
	}

}
