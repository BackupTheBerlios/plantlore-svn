package net.sf.plantlore.common;

import java.awt.Component;
import javax.swing.JOptionPane;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.client.AppCoreCtrl;


public class DefaultReconnectDialog {
	
	private static String[] options = new String[] { L10n.getString("Overview.MenuFileReconnect"), L10n.getString("Common.Ok") };
	private static AppCoreCtrl.ReconnectAction defaultReconnectAction;

	
	public static void setDefaultReconnectAction(AppCoreCtrl.ReconnectAction reconnect) {
		defaultReconnectAction = reconnect;
	}
	
	
	public static void show(Component parent, Exception e, AppCoreCtrl.ReconnectAction reconnect) {
		
		//reconnect.setParent(parent);
		
		int choice = JOptionPane.showOptionDialog(
				parent, 
				e.getMessage(), 
				L10n.getString("Error.ConnectionLost"),
				0,
				JOptionPane.ERROR_MESSAGE,
				null, 
				options,
				options[0]
		);
		
		if(choice == 0) 
			reconnect.actionPerformed(null);
	}
	
	
	public static void show(Component parent, Exception e) {
		
		defaultReconnectAction.setParent(parent);
		
		int choice = JOptionPane.showOptionDialog(
				parent, 
				e.getMessage(), 
				L10n.getString("Error.ConnectionLost"),
				0,
				JOptionPane.ERROR_MESSAGE,
				null, 
				options,
				options[0]
		);
		
		if(choice == 0 && defaultReconnectAction != null) 
			defaultReconnectAction.actionPerformed(null);
	}

}
