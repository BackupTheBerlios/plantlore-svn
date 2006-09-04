package net.sf.plantlore.server.manager;

import java.awt.event.ActionEvent;

import net.sf.plantlore.common.Dispatcher;
import net.sf.plantlore.common.StandardAction;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.server.ConnectionInfo;

/**
 * Perform the requested operation from the view.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-21
 */
public class ServerMngCtrl {
	
	
	public ServerMngCtrl(final ServerMng model, final ServerMngView view) {
		
		view.kick.setAction(new StandardAction("Server.KickUser") {
			public void actionPerformed(ActionEvent arg0) {
				Task t = model.createKickTask( (ConnectionInfo)view.users.getSelectedValue() );
				Dispatcher.getDispatcher().dispatch(t, view, true);			
			}
		});
		
		view.terminate.setAction(new StandardAction("Server.Terminate") {
			public void actionPerformed(ActionEvent arg0) {
				Task t = model.createTerminateServerTask();
				Dispatcher.getDispatcher().dispatch(t, view, true);
			}
		});
		
		view.refresh.setAction(new StandardAction("Server.Refresh") {
			public void actionPerformed(ActionEvent arg0) {
				Task t = model.createUpdateConnectedUsersTask();
				Dispatcher.getDispatcher().dispatch(t, view, true);
			}
		});
		
		view.hide.setAction(new StandardAction("Server.Hide") {
			public void actionPerformed(ActionEvent ae) {
				model.deleteObserver( view );
				view.setVisible(false);
				view.dispose();
				if( !model.isAlive() ) {
					System.exit(0);
				}
			}
		});
	}

	

}
