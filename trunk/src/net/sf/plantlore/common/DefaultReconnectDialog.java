package net.sf.plantlore.common;

import java.awt.Component;
import javax.swing.JOptionPane;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.client.AppCoreCtrl;

/**
 * The dialog that offers the User the possibility to perform automatic reconnection
 * if the connection with the database is lost.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @version 1.0
 */
public class DefaultReconnectDialog {
	
	private static String[] options = new String[] { 
		L10n.getString("Overview.MenuFileReconnect"), 
		L10n.getString("Common.Ok") };
	
	private static AppCoreCtrl.ReconnectAction defaultReconnectAction;

	/**
	 * Set the default reconnect action.
	 * 
	 * @param reconnect	The action that will be called if the User decides the reconnect is needed. 
	 */
	public static void setDefaultReconnectAction(AppCoreCtrl.ReconnectAction reconnect) {
		defaultReconnectAction = reconnect;
	}
	
	/**
	 * Display the reconnect dialog. If the reconnection is selected by the User, the supplied
	 * `reconnect` action will be used instead of the default one.
	 * 
	 * @param parent	The parent window in order to maintain the Swing window hierarchy.
	 * @param e	The exception that must be announced to the User.
	 * @param reconnect	The reconnect action that will be used instead of the default one.
	 */
	public static void show(Component parent, Exception e, AppCoreCtrl.ReconnectAction reconnect) {
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
	
	/**
	 * Display the reconnect dialog. If the reconnection is selected by the User, the default
	 * reconnect action will be used.
	 * 
	 * @param parent	The parent window in order to maintain the Swing window hierarchy.
	 * @param e	The exception that must be announced to the User.
	 */
	public static void show(Component parent, Exception e) {
		show(parent, e.getMessage());
	}
	
	/**
	 * Display the reconnect dialog. If the reconnection is selected by the User, the default
	 * reconnect action will be used. Instead of automatic exception handling, this time you
	 * can specify your own problem description. 
	 * 
	 * @param parent	The parent window in order to maintain the Swing window hierarchy.
	 * @param problemDescription	The string that will be displayed to the User.
	 */
	public static void show(Component parent, String problemDescription) {
		defaultReconnectAction.setParent(parent);
		int choice = JOptionPane.showOptionDialog(
				parent, 
				problemDescription, 
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
