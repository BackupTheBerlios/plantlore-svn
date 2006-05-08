package net.sf.plantlore.client.login;

import java.awt.event.ActionEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.common.exception.DBLayerException;

public class AuthCtrl {
	
	private AuthView view;
	private Login model;
	
	private Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
	
	public AuthCtrl(Login login, AuthView authview) {
		this.model = login; this.view = authview;
		view.next.addActionListener(new Next());
	}

	
	class Next extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			try {
				String user = (view.user.getSelectedItem() != null) ? view.user.getSelectedItem().toString() : null;
				if(user != null) {
					model.connectToSelected(user, new String(view.password.getPassword()));
					view.setVisible(false);
				}
				else 
					JOptionPane.showMessageDialog(view,
						L10n.getString("warningNoUsername"),
					    L10n.getString("warningNoUsernameTitle"),
					    JOptionPane.WARNING_MESSAGE);
			} 
			catch(NotBoundException e) {
				logger.warn("The server is either not running or it is unreachable (java security policy). Details: " + e);
				JOptionPane.showMessageDialog(view,
						L10n.getString("errorNotBoundException"),
					    L10n.getString("errorNotBoundExceptionTitle"),
					    JOptionPane.WARNING_MESSAGE);
			}
			catch(RemoteException e) {
				logger.warn("Either the network connection has failed or the server has crashed or gone off. Details: " + e);
				e.printStackTrace();
				JOptionPane.showMessageDialog(view,
						L10n.getString("errorRemoteException"),
					    L10n.getString("errorRemoteExceptionTitle"),
					    JOptionPane.WARNING_MESSAGE);
			}
			catch(DBLayerException e) {
				logger.warn("Unable to initialize the database layer. Details: " + e);
				JOptionPane.showMessageDialog(view,
						L10n.getString("errorDBLayerException") + "\n" + e,
					    L10n.getString("errorDBLayerExceptionTitle"),
					    JOptionPane.WARNING_MESSAGE);
			}
			finally { view.password.setText(""); }
		}
	}

}
