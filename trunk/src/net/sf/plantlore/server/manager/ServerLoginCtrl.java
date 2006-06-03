package net.sf.plantlore.server.manager;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import net.sf.plantlore.client.resources.Resource;
import net.sf.plantlore.l10n.L10n;

/**
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-21
 * @version GR-8
 */
public class ServerLoginCtrl {
	
	private ServerMng model;
	private ServerLoginView view;
	
	private ServerMngView serverMngView;
//	private ServerMngCtrl serverMngCtrl;
	
	
	
	public ServerLoginCtrl(ServerMng model, ServerLoginView view) {
		this.view = view;
		this.model = model;
		
		view.next.setAction(new Connect());
	}
	
	
	class Connect extends AbstractAction {
		public Connect() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Server.NextTT"));
			putValue(NAME, L10n.getString("Server.Next"));
            ImageIcon icon = Resource.createIcon("Server.Connect.gif");
			if(icon != null) putValue(SMALL_ICON, icon);
		}
		
		public void actionPerformed(ActionEvent e) {
			String hp = view.host.getText(), host;
			int t = hp.indexOf(':'), 
			port = net.sf.plantlore.server.RMIServer.DEFAULT_PORT;
			if(t < 0) 
				host = hp;
			else { 
				host = hp.substring(0, t); 
				port = Integer.parseInt(hp.substring(t + 1)); 
			}
				
			if( model.connectToRunningServer(host, port, new String(view.password.getPassword())) ) {
				// Everything's fine.
				view.setVisible(false);
				
				serverMngView = new ServerMngView(model);
				serverMngView.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
				/*serverMngCtrl = */new ServerMngCtrl(model, serverMngView);
				serverMngView.setVisible(true);
			}
		}
	}

}
