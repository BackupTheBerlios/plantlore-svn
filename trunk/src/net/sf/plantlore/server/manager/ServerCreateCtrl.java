package net.sf.plantlore.server.manager;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import net.sf.plantlore.client.resources.Resource;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.server.DatabaseSettings;
import net.sf.plantlore.server.RMIServer;
import net.sf.plantlore.server.ServerSettings;

public class ServerCreateCtrl {
	
	private ServerMng model;
	private ServerCreateView view;
	private ServerMngView mngView;
	
	public ServerCreateCtrl(ServerMng model, ServerCreateView view) {
		this.model = model;
		this.view = view;
		
		view.next.setAction( new CreateServerAction() );
	}
	
	private class CreateServerAction extends AbstractAction {
		public CreateServerAction() {
			putValue(SHORT_DESCRIPTION, L10n.getString("Server.CreateTT"));
			putValue(NAME, L10n.getString("Server.Create"));
            ImageIcon icon = Resource.createIcon("Server.Create.gif");
			if(icon != null) putValue(SMALL_ICON, icon);
		}
		public void actionPerformed(ActionEvent arg0) {
			// Take the stored information.
			ServerSettings settings = model.getSettings(false);
			
			// Gather the information from the dialog. 
			try {
				// About the server.
				String serverPassword = new String( view.serverPassword.getPassword() );
				int serverPort;
				try {
					serverPort = Integer.parseInt(view.serverPort.getText());
				} catch(Exception e) { serverPort = RMIServer.DEFAULT_PORT; }
				
				// About the database.
				String database = ((javax.swing.JTextField)view.databaseType.getEditor().getEditorComponent()).getText(),
				databasePassword = new String( view.databasePassword.getPassword() ),
				databaseUser = view.databaseUser.getText(),
				suffix = view.databaseParameter.getText();
				
				int databasePort; 
				databasePort = Integer.parseInt(view.databasePort.getText());
				
				settings = new ServerSettings(
						serverPort, 
						settings.getTimeout(), // use the stored values
						settings.getConnectionsTotal(),  // use the stored values
						settings.getConnectionsPerIP(), // use the stored values
						new DatabaseSettings(
								database, 
								databasePort, 
								suffix, 
								databaseUser, 
								databasePassword)
						);
				
				// Save those settings.
				model.setSettings( settings );
				
				// Create and run a new server.
				if( model.startNewServer(serverPassword) ){
					// Hide this window, create and display the ServerMngView instead.
					view.setVisible(false);
//					view.dispose();
					
					mngView = new ServerMngView(model);
					mngView.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
					/*mngCtrl = */ new ServerMngCtrl(model, mngView);
					mngView.setVisible(true);
				}

			} catch(final Exception e) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(null,
								e.getMessage(),
							    L10n.getString("Error.MissingInformation"),
							    JOptionPane.ERROR_MESSAGE);						
					}
				});
			}
		}
	}

}
