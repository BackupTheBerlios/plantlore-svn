package net.sf.plantlore.common;

import java.rmi.*;
import java.rmi.server.ExportException;

import javax.swing.JOptionPane;

import org.hibernate.JDBCException;

import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.l10n.L10n;

/**
 * The Default Exception Handler offers a united exception handling and
 * error presentation. It can offer reconnect if it is reasonable.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-08-28
 * @version 1.0
 */
public class DefaultExceptionHandler {
	
	private DefaultExceptionHandler() {}


	/**
	 * Announce the exception to the User.
	 * 
	 * @param parent	The parent window (in order to maintain the proper window hierachy in Swing).
	 * @param e	The exception to be announced.
	 */
	public static void handle(java.awt.Component parent, Exception e) {
		handle(parent, e, L10n.getString("Error.General"), false);		
	}
	
	/**
	 * Announce the exception to the User.
	 * 
	 * @param parent	The parent window (in order to maintain the proper window hierachy in Swing).
	 * @param e	The exception to be announced.
	 * @param title The title the window should have instead of the default title.
	 */
	public static void handle(java.awt.Component parent, Exception e, String title) {
		handle(parent, e, title, false);
	}
		
	/**
	 * Announce the exception to the User.
	 * 
	 * @param parent	The parent window (in order to maintain the proper window hierachy in Swing).
	 * @param e	The exception to be announced.
	 * @param title The title the window should have instead of the default title.
	 * @param doNotOfferReconnect True if the reconnect procedure should not be offered
	 * 					even if it might seem reasonable.
	 */	
	public static void handle(
			java.awt.Component parent, 
			Exception e, 
			String title, 
			boolean doNotOfferReconnect) {
		
		boolean isReconnectReasonable = false;
		String problemDescription = e.getMessage();
		
		/*
		 * REMOTE EXCEPTION HANDLER
		 */
		if( e instanceof RemoteException ) {
			// The caller does not have permission to perform the action requested by the method call.
			if( e instanceof AccessException )
				problemDescription = L10n.getString("Error.Server.AccessDenied");
			
			// Connection is refused to the remote host for a remote method call.
			else if( e instanceof ConnectException ) {
				problemDescription = L10n.getString("Error.Server.ConnectionRefused");
				isReconnectReasonable = true;
			}
			
			// An IOException occured while making a connection to the remote host for a remote method call.
			else if( e instanceof ConnectIOException )
				problemDescription = L10n.getString("Error.Server.ConnectionRefused");
			
			// An attempt to export a remote object failed.
			else if( e instanceof ExportException )
				problemDescription = L10n.getString("Error.Server.ExportFailed");
			
			// Occurs while marshalling the remote call header, arguments or 
			// return value for a remote method call.
			// Or if the receiver does not support the protocol version of the sender.
			else if( e instanceof MarshalException )
				problemDescription = L10n.getString("Error.Server.MarshallingFailed");
			
			// An attempt was made to invoke a method on an object 
			// that no longer exists in the remote virtual machine.
			else if( e instanceof NoSuchObjectException )
				problemDescription = L10n.getString("Error.Server.ObjectDoesNotExistAnymore");
			
			// An Error was thrown while processing the invocation on the server, 
			// either while unmarshalling the arguments, executing the remote method itself, 
			// or marshalling the return value.
			else if( e instanceof ServerError ) {
				problemDescription = L10n.getString("Error.Server.InTrouble");
				isReconnectReasonable = true;
			}
			
			// A RemoteException was thrown while processing the invocation on the server, 
			// either while unmarshalling the arguments or executing the remote method itself.
			else if( e instanceof ServerException ) {
				problemDescription = L10n.getString("Error.Server.InTrouble");
				isReconnectReasonable = true;
			}
			
			// A valid stub class could not be found for a remote object when it is exported.
			else if( e instanceof StubNotFoundException )
				problemDescription = L10n.getString("Error.Server.StubNotFound");
			
			// If the client of a remote method call receives, as a result of the call, 
			// a checked exception that is not among the checked exception types 
			// declared in the throws clause of the method in the remote interface.
			else if( e instanceof UnexpectedException )
				problemDescription = L10n.getString("Error.Server.UnexpectedProblem");
			
			// If a java.net.UnknownHostException occurs while creating a connection 
			// to the remote host for a remote method call.
			else if( e instanceof UnknownHostException )
				problemDescription = L10n.getString("Error.Server.UnknownHost");
			
			// Could be thrown while unmarshalling the parameters or results of a remote method call.
			else if( e instanceof UnmarshalException )
				problemDescription = L10n.getString("Error.Server.UnmarshallingFailed");

			
		}
		
		/*
		 * DBLAYER EXCEPTION HANDLER
		 */
		else if( e instanceof DBLayerException ) {
			DBLayerException d = (DBLayerException)e;
			if(d.getCause() instanceof JDBCException)
				problemDescription = problemDescription + "\n"  + d.getErrorInfo();
			isReconnectReasonable = d.isReconnectNecessary();
		}
		
		// Announce the error and offer the reconnection.
		if( isReconnectReasonable && ! doNotOfferReconnect )
			DefaultReconnectDialog.show(parent, problemDescription);
		// Just announce the problem.
		else
			JOptionPane.showMessageDialog( 
					parent, 
					problemDescription, 
					title, 
					JOptionPane.ERROR_MESSAGE );
		
	}
	
	
}
