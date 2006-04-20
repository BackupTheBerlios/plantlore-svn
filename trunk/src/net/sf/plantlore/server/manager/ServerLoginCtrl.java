package net.sf.plantlore.server.manager;

import java.awt.event.ActionEvent;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;


public class ServerLoginCtrl {
	
	private ServerMng model;
	private ServerLoginView view;
	
	private ServerMngView serverView;
	private ServerMngCtrl serverCtrl;
	
	
	
	
	public ServerLoginCtrl(ServerMng model, ServerLoginView view) {
		this.view = view;
		this.model = model;
		
		view.next.addActionListener(new ConnectCreate());
		view.connectExisting.addActionListener(new SelectExisting());
		view.createNew.addActionListener(new SelectNew());
		
		serverView = new ServerMngView(model);
		serverCtrl = new ServerMngCtrl(model, serverView);
	}
	
	
	class ConnectCreate extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			String hp = view.host.getText(), host;
			int t = hp.indexOf(':'), port = 1099;
			if(t < 0) host = hp;
			else { host = hp.substring(0, t); port = Integer.parseInt(hp.substring(t + 1)); }
			try {
				model.actAsInstructed(host, port, new String(view.password.getPassword()));
			} catch(RemoteException re) { 
				JOptionPane.showMessageDialog(view,
					    "Unable to connect to the server.\n The server is either not running,\n or there is a network problem.",
					    "Remoting problem...",
					    JOptionPane.WARNING_MESSAGE);
				return;
			} catch(AlreadyBoundException abe) {
				JOptionPane.showMessageDialog(view,
					    "Another server is already running on the selected port (" + abe + "). Didn't you want to connect to it?",
					    "Server collision problem...",
					    JOptionPane.WARNING_MESSAGE);
				return;
			} catch(NotBoundException nbe) {
				JOptionPane.showMessageDialog(view,
					    "There is no server running on the specified host and port (" + nbe + ").",
					    "Server connection problem...",
					    JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			view.setVisible(false);
			serverView.setVisible(true);
		}
	}
	
	class SelectNew extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			model.setMode(ServerMng.Mode.CREATE_NEW);
		}
	}
	
	class SelectExisting extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			model.setMode(ServerMng.Mode.CONNECT_EXISTING);
		}
	}

}
