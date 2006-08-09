package net.sf.plantlore.server.manager;

import java.awt.event.ActionEvent;

import net.sf.plantlore.common.DefaultProgressBar;
import net.sf.plantlore.common.StandardAction;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.server.DatabaseSettings;
import net.sf.plantlore.server.RMIServer;
import net.sf.plantlore.server.ServerSettings;

public class ServerCreateCtrl {

	private ServerMngView mngView;
	
	public ServerCreateCtrl(final ServerMng model, final ServerCreateView view) {
		
		mngView = new ServerMngView(model);
		new ServerMngCtrl(model, mngView);
		
		view.next.setAction( new StandardAction("Server.Create") {
			public void actionPerformed(ActionEvent arg0) {
				// Take the stored information.
				ServerSettings settings = model.getSettings(false);
				
				// Gather the information from the dialog. 
				
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
				try {
					databasePort = Integer.parseInt(view.databasePort.getText());
				} catch(Exception e) { databasePort = 5432 ; }
				
				settings = new ServerSettings(
						serverPort, 
						settings.getTimeout(), // use the stored value
						settings.getConnectionsTotal(),  // use the stored value
						settings.getConnectionsPerIP(), // use the stored value
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
				Task t = model.createNewServerTask( serverPassword );
				new DefaultProgressBar(t, view, true);
				t.start();
			}
		} );
	}

}
