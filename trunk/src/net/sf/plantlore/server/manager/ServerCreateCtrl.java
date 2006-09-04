package net.sf.plantlore.server.manager;

import java.awt.event.ActionEvent;

import net.sf.plantlore.common.Dispatcher;
import net.sf.plantlore.common.StandardAction;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.server.DatabaseSettings;
import net.sf.plantlore.server.RMIServer;
import net.sf.plantlore.server.ServerSettings;

/**
 * Gather the information from the dialog and
 * perform the requested action - either conect to an already running
 * Plantlore Server or create a new Plantlore Server.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-05-30
  */
public class ServerCreateCtrl {

	private ServerMngView mngView;
	
	public ServerCreateCtrl(final ServerMng model, final ServerCreateView view) {
            
		Dispatcher.initialize( view.progress );
		
		mngView = new ServerMngView(model);
		new ServerMngCtrl(model, mngView);
		
        view.cancel.setAction( new StandardAction("Common.Cancel") {
			public void actionPerformed(ActionEvent arg0) {
				view.setVisible(false);
				System.exit(0);
			}
        });
		
		view.next.setAction( new StandardAction("Server.Continue") {
			public void actionPerformed(ActionEvent arg0) {
				
				switch( view.choicePane.getSelectedIndex() ) {
				/* CREATE A NEW SERVER AND CONNECT TO IT */
				case 0:
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
									suffix)
					);
					
					// Save those settings.
					model.setSettings( settings );
					
					// Create and run a new server.
					Task createServer = model.createNewServerTask( serverPassword );
					Dispatcher.getDispatcher().dispatch(createServer, view, false);
					break;
				
					
				/* CONNECT TO A RUNNING SERVER */
				case 1:
					
					//	Gather the information from the dialog.
					String password = new String( view.remoteServerPassword.getPassword() );
					int port;
					try {
						port = Integer.parseInt( view.remoteServerPort.getText() );
					} catch(Exception e) { port = RMIServer.DEFAULT_PORT; }
					
					Task connectToServer = model.createConnectToRunningServerTask(
							view.remoteHost.getText(), port, password );
					Dispatcher.getDispatcher().dispatch(connectToServer, view, false);
					
					break;
				}
			}
		} );
	}

}
