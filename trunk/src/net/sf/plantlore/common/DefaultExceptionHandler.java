package net.sf.plantlore.common;

import java.rmi.*;
import java.rmi.server.ExportException;

import javax.swing.JOptionPane;

import org.hibernate.JDBCException;

import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.l10n.L10n;

/**
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 28.8.2006
 *
 */
public class DefaultExceptionHandler {

	private DefaultExceptionHandler() {}
	
	
	public static void handle(java.awt.Component parent, Exception e) {
		handle(parent, e, L10n.getString("Error.General"));		
	}
	
	
	public static void handle(java.awt.Component parent, Exception e, String title) {
		/*
		 * REMOTE EXCEPTION HANDLER
		 */
		if( e instanceof RemoteException ) {
			String problemDescription = e.getMessage();
			
			if( e instanceof AccessException )
				problemDescription = L10n.getString("Error.Server.AccessDenied");
			else if( e instanceof ConnectException )
				problemDescription = L10n.getString("Error.Server.ConnectionRefused");
			else if( e instanceof ConnectIOException )
				problemDescription = L10n.getString("Error.Server.ConnectionRefused");
			else if( e instanceof ExportException )
				problemDescription = L10n.getString("Error.Server.ExportFailed");
			else if( e instanceof MarshalException )
				problemDescription = L10n.getString("Error.Server.MarshallingFailed");
			else if( e instanceof NoSuchObjectException )
				problemDescription = L10n.getString("Error.Server.ObjectDoesNotExistAnymore");
			else if( e instanceof ServerError )
				problemDescription = L10n.getString("Error.Server.InTrouble");
			else if( e instanceof ServerException )
				problemDescription = L10n.getString("Error.Server.InTrouble");
			else if( e instanceof StubNotFoundException )
				problemDescription = L10n.getString("Error.Server.StubNotFound");
			else if( e instanceof UnexpectedException )
				problemDescription = L10n.getString("Error.Server.UnexpectedProblem");
			else if( e instanceof UnknownHostException )
				problemDescription = L10n.getString("Error.Server.UnknownHost");
			else if( e instanceof UnmarshalException )
				problemDescription = L10n.getString("Error.Server.UnmarshallingFailed");

			DefaultReconnectDialog.show(parent, problemDescription);
		}
		
		/*
		 * DBLAYER EXCEPTION HANDLER
		 */
		else if( e instanceof DBLayerException && ((DBLayerException)e).isReconnectNecessary() ) {
			DBLayerException d = (DBLayerException)e;
			String problemDescription = d.getMessage();
			if(d.getCause() instanceof JDBCException)
				problemDescription = d.getMessage() + "\n"  + d.getErrorInfo();
			DefaultReconnectDialog.show(parent, problemDescription);
		}
		
		/*
		 * OTHER EXCEPTIONS HANDLER
		 */
		else
			JOptionPane.showMessageDialog( 
					parent, 
					e.getMessage(), 
					title, 
					JOptionPane.ERROR_MESSAGE );
	}
	
}
