package net.sf.plantlore.server.manager;

import java.awt.event.ActionEvent;

import net.sf.plantlore.common.DefaultProgressBar;
import net.sf.plantlore.common.StandardAction;
import net.sf.plantlore.common.Task;

/**
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-04-21
 * @version GR-8
 */
public class ServerLoginCtrl {
	
	private ServerMngView mngView;
	
	
	
	public ServerLoginCtrl(final ServerMng model, final ServerLoginView view) {
		
		mngView = new ServerMngView(model);
		new ServerMngCtrl(model, mngView);
		
		view.next.setAction(new StandardAction("Server.Next") {
			public void actionPerformed(ActionEvent e) {
				String hostAndPort = view.host.getText(), host;
				int delimiter = hostAndPort.indexOf(':'), 
				port = net.sf.plantlore.server.RMIServer.DEFAULT_PORT;
				if(delimiter < 0) 
					host = hostAndPort;
				else { 
					host = hostAndPort.substring(0, delimiter);
					try {
						port = Integer.parseInt(hostAndPort.substring(delimiter + 1));
					} catch(Exception ex) { /* Never mind, use the default port.*/ }
				}
					
				Task t = model.createConnectToRunningServerTask(host, port, new String(view.password.getPassword()) );
				new DefaultProgressBar(t, view, true);
				t.start();
			}
			
		});
	}

}
