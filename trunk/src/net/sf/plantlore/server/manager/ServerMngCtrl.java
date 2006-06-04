package net.sf.plantlore.server.manager;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import net.sf.plantlore.client.resources.Resource;
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
	private boolean terminated = false;
	
	public ServerMngCtrl(ServerMng model, ServerMngView view) {
		this.model = model; this. view = view;
		
		view.kick.setAction(new KickUserAction());
		view.terminate.setAction(new TerminateServerAction());
		view.refresh.setAction(new RefreshAction());
		view.hide.setAction(new HideAction());
	}
	
	
	class KickUserAction extends AbstractAction {
		public KickUserAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Server.KickUserTT"));
			putValue(NAME, L10n.getString("Server.KickUser"));
            ImageIcon icon = Resource.createIcon("Server.KickUser.gif");
			if(icon != null) putValue(SMALL_ICON, icon);
		}
		public void actionPerformed(ActionEvent ae) {
			Object client = view.users.getSelectedValue();
			if(client == null) 
				return;
			else if(client instanceof ConnectionInfo) 
				model.kick( (ConnectionInfo)client );
		}
	}
	
	class TerminateServerAction extends AbstractAction {
		public TerminateServerAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Server.TerminateTT"));
			putValue(NAME, L10n.getString("Server.Terminate"));
            ImageIcon icon = Resource.createIcon("Server.Terminate.gif");
			if(icon != null) putValue(SMALL_ICON, icon);
		}
		public void actionPerformed(ActionEvent ae) {
			model.terminateServer();
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					// Disable Terminate, Refresh and Kick buttons.
					view.terminate.setEnabled(false);
					view.refresh.setEnabled(false);
					view.kick.setEnabled(false);
					terminated = true;
				}
			});
		}
	}
	
	class RefreshAction extends AbstractAction {
		public RefreshAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Server.RefreshTT"));
			putValue(NAME, L10n.getString("Server.Refresh"));
            ImageIcon icon = Resource.createIcon("Server.Refresh.gif");
			if(icon != null) putValue(SMALL_ICON, icon);
		}
		public void actionPerformed(ActionEvent ae) {
			model.getConnectedUsers(true);
		}
	}
	
	class HideAction extends AbstractAction {
		public HideAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Server.HideTT"));
			putValue(NAME, L10n.getString("Server.Hide"));
            ImageIcon icon = Resource.createIcon("Server.Hide.gif");
			if(icon != null) putValue(SMALL_ICON, icon);
		}
		public void actionPerformed(ActionEvent ae) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					view.setVisible(false);
					view.dispose();
					if(terminated)
						System.exit(0);
				}
			});
		}
	}

}
