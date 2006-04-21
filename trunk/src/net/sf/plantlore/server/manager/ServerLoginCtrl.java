package net.sf.plantlore.server.manager;

import java.awt.event.ActionEvent;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;


import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.server.CertificationException;

/**
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-21
 *
 */
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
		public ConnectCreate() {
            putValue(NAME, L10n.getString("Continue"));
            putValue(SHORT_DESCRIPTION, L10n.getString("ContinueTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Continue"));            
        }
		
		public void actionPerformed(ActionEvent e) {
			String hp = view.host.getText(), host;
			int t = hp.indexOf(':'), port = net.sf.plantlore.server.RMIServer.DEFAULT_PORT;
			if(t < 0) host = hp;
			else { host = hp.substring(0, t); port = Integer.parseInt(hp.substring(t + 1)); }
			try {
				
				model.actAsInstructed(host, port, new String(view.password.getPassword()));
				
			} catch(RemoteException re) { 
				JOptionPane.showMessageDialog(view,
						L10n.getString("errorRemoteException"),
					    L10n.getString("errorRemoteExceptionTitle"),
					    JOptionPane.WARNING_MESSAGE);
				return;
			} catch(AlreadyBoundException abe) {
				int response =
				JOptionPane.showOptionDialog(view,
					    L10n.getString("questionAlreadyBoundException"),
					    L10n.getString("questionAlreadyBoundExceptionTitle"),
					    JOptionPane.OK_CANCEL_OPTION,
					    JOptionPane.WARNING_MESSAGE,
					    null,
					    null,
					    null);
				if(response == JOptionPane.OK_OPTION) {
					view.connectExisting.doClick();
					view.next.doClick();
				}
				return;
			} catch(NotBoundException nbe) {
				JOptionPane.showMessageDialog(view,
					    L10n.getString("errorNotBoundException"),
					    L10n.getString("errorNotBoundExceptionTitle"),
					    JOptionPane.WARNING_MESSAGE);
				return;
			}
			catch(CertificationException ce) {
				JOptionPane.showMessageDialog(view,
					    L10n.getString("errorCertificationException"),
					    L10n.getString("errorCertificationExceptionTitle"),
					    JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			// Everything's fine in here.
			view.setVisible(false);
			serverView.setVisible(true);
		}
	}
	
	class SelectNew extends AbstractAction {
		public SelectNew() {
            putValue(NAME, L10n.getString("SelectNew"));
            putValue(SHORT_DESCRIPTION, L10n.getString("SelectNewTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("SelectNew"));            
        }
		public void actionPerformed(ActionEvent e) {
			model.setMode(ServerMng.Mode.CREATE_NEW);
		}
	}
	
	class SelectExisting extends AbstractAction {
		public SelectExisting() {
            putValue(NAME, L10n.getString("SelectExisting"));
            putValue(SHORT_DESCRIPTION, L10n.getString("SelectExistingTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("SelectExisting"));            
        }
		public void actionPerformed(ActionEvent e) {
			model.setMode(ServerMng.Mode.CONNECT_EXISTING);
		}
	}

}
